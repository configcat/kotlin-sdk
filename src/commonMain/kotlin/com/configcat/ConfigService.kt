package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshResult
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Entry
import com.configcat.model.Setting
import com.soywiz.klock.DateTime
import com.soywiz.krypto.sha1
import io.ktor.util.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal data class SettingResult(val settings: Map<String, Setting>, val fetchTime: DateTime) {
    fun isEmpty(): Boolean = this === empty

    companion object {
        val empty: SettingResult = SettingResult(emptyMap(), Constants.distantPast)
    }
}

internal class ConfigService(
    private val options: ConfigCatOptions,
    private val configFetcher: ConfigFetcher,
    private val logger: InternalLogger,
    private val hooks: Hooks
) : Closeable {
    internal val cacheKey: String = getCacheKey()
    private val mutex = Mutex()
    private val syncLock = reentrantLock()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val closed = atomic(false)
    private val initialized = atomic(false)
    private val offline = atomic(options.offline)
    private val mode = options.pollingMode
    private var pollingJob: Job? = null
    private var cachedEntry = Entry.empty
    private var cachedJsonString = ""
    private var fetchJob: Deferred<Pair<Entry, String?>>? = null
    private var fetching = false

    val isOffline: Boolean get() = offline.value

    init {
        if (mode is AutoPollMode && !options.offline) {
            startPoll(mode)
        } else {
            setInitialized()
        }
    }

    suspend fun getSettings(): SettingResult {
        return when (mode) {
            is LazyLoadMode -> {
                val result = fetchIfOlder(
                    DateTime.now()
                        .add(0, -mode.configuration.cacheRefreshInterval.inWholeMilliseconds.toDouble())
                )
                if (result.first.isEmpty()) {
                    SettingResult.empty
                } else {
                    SettingResult(result.first.config.settings ?: emptyMap(), result.first.fetchTime)
                }
            }

            else -> {
                val result = fetchIfOlder(Constants.distantPast, preferCached = true)
                if (result.first.isEmpty()) {
                    SettingResult.empty
                } else {
                    SettingResult(result.first.config.settings ?: emptyMap(), result.first.fetchTime)
                }
            }
        }
    }

    fun offline() {
        syncLock.withLock {
            if (!offline.compareAndSet(expect = false, update = true)) return
            pollingJob?.cancel()
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("OFFLINE"))
        }
    }

    fun online() {
        syncLock.withLock {
            if (!offline.compareAndSet(expect = true, update = false)) return
            if (mode is AutoPollMode) {
                startPoll(mode)
            }
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("ONLINE"))
        }
    }

    suspend fun refresh(): RefreshResult {
        if (offline.value) {
            val offlineMessage = ConfigCatLogMessages.CONFIG_SERVICE_CANNOT_INITIATE_HTTP_CALLS_WARN
            logger.warning(3200, offlineMessage)
            return RefreshResult(false, offlineMessage)
        }
        val result = fetchIfOlder(Constants.distantFuture)
        return RefreshResult(result.second == null, result.second)
    }

    @Suppress("ComplexMethod")
    private suspend fun fetchIfOlder(time: DateTime, preferCached: Boolean = false): Pair<Entry, String?> {
        mutex.withLock {
            // Sync up with the cache and use it when it's not expired.
            if (cachedEntry.isEmpty() || cachedEntry.fetchTime > time) {
                val entry = readCache()
                if (!entry.isEmpty() && entry.eTag != cachedEntry.eTag) {
                    cachedEntry = entry
                    hooks.invokeOnConfigChanged(entry.config.settings)
                }
                // Cache isn't expired
                if (cachedEntry.fetchTime > time) {
                    setInitialized()
                    return Pair(cachedEntry, null)
                }
            }
            // Use cache anyway (get calls on auto & manual poll must not initiate fetch).
            // The initialized check ensures that we subscribe for the ongoing fetch during the
            // max init wait time window in case of auto poll.
            if (preferCached && initialized.value) {
                return Pair(cachedEntry, null)
            }
            // If we are in offline mode we are not allowed to initiate fetch.
            if (offline.value) {
                return Pair(cachedEntry, null)
            }

            val runningJob = fetchJob
            if (runningJob == null || !fetching) {
                // No fetch is running, initiate a new one.
                fetching = true
                val eTag = cachedEntry.eTag
                fetchJob = coroutineScope.async {
                    if (mode is AutoPollMode && !initialized.value) {
                        // Waiting for the client initialization.
                        // After the maxInitWaitTimeInSeconds timeout the client will be initialized and while
                        // the config is not ready the default value will be returned.
                        val result = withTimeoutOrNull(mode.configuration.maxInitWaitTime) {
                            fetchConfig(eTag)
                        }
                        if (result != null) {
                            return@async result
                        }
                        // We got a timeout
                        val message = ConfigCatLogMessages.getAutoPollMaxInitWaitTimeReached(
                            mode.configuration.maxInitWaitTime.inWholeMilliseconds
                        )
                        logger.warning(4200, message)
                        setInitialized()
                        return@async Pair(Entry.empty, message)
                    } else {
                        // The service is initialized, start fetch without timeout.
                        return@async fetchConfig(eTag)
                    }
                }
            }
        }
        // Await the fetch routine.
        val result = fetchJob?.await()

        mutex.withLock {
            return result?.let { value ->
                when {
                    value.first.isEmpty() -> Pair(cachedEntry, value.second)
                    else -> value
                }
            } ?: Pair(cachedEntry, null)
        }
    }

    private suspend fun fetchConfig(eTag: String): Pair<Entry, String?> {
        val response = configFetcher.fetch(eTag)
        mutex.withLock {
            setInitialized()
            fetching = false
            if (response.isFetched) {
                cachedEntry = response.entry
                writeCache(response.entry)
                hooks.invokeOnConfigChanged(response.entry.config.settings)
                return Pair(response.entry, null)
            } else if ((response.isNotModified || !response.isTransientError) && !cachedEntry.isEmpty()) {
                cachedEntry = cachedEntry.copy(fetchTime = DateTime.now())
                writeCache(cachedEntry)
            }
            return Pair(cachedEntry, response.error)
        }
    }

    private fun startPoll(mode: AutoPollMode) {
        logger.debug("Start polling with ${mode.configuration.pollingInterval.inWholeMilliseconds}ms interval.")
        pollingJob = coroutineScope.launch {
            while (isActive) {
                fetchIfOlder(
                    DateTime.now()
                        .add(0, -mode.configuration.pollingInterval.inWholeMilliseconds.toDouble())
                )
                delay(mode.configuration.pollingInterval)
            }
        }
    }

    private fun setInitialized() {
        if (!initialized.compareAndSet(expect = false, update = true)) return
        hooks.invokeOnClientReady()
    }

    private suspend fun readCache(): Entry {
        return try {
            val cached = options.configCache?.read(cacheKey) ?: ""
            if (cached.isEmpty() || cached == cachedJsonString) return Entry.empty
            cachedJsonString = cached
            Entry.fromString(cached)
        } catch (e: Exception) {
            logger.error(2200, ConfigCatLogMessages.CONFIG_SERVICE_CACHE_READ_ERROR, e)
            Entry.empty
        }
    }

    private suspend fun writeCache(entry: Entry) {
        options.configCache?.let { cache ->
            try {
                val json = entry.serialize()
                cachedJsonString = json
                cache.write(cacheKey, json)
            } catch (e: Exception) {
                logger.error(2201, ConfigCatLogMessages.CONFIG_SERVICE_CACHE_WRITE_ERROR, e)
            }
        }
    }

    private fun getCacheKey() =
        "${options.sdkKey}_${Constants.configFileName}_${Constants.serializationFormatVersion}".encodeToByteArray()
            .sha1().hex

    override fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        configFetcher.close()
        coroutineScope.cancel()
    }
}
