package com.configcat.fetch

import com.configcat.*
import com.configcat.AutoPollMode
import com.configcat.LazyLoadMode
import com.configcat.Constants
import com.configcat.log.InternalLogger
import com.soywiz.klock.DateTime
import com.soywiz.krypto.sha1
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

internal data class SettingResult(val settings: Map<String, Setting>, val fetchTime: DateTime)

internal class ConfigService constructor(
    private val options: ClientOptions,
    private val configFetcher: ConfigFetcher,
    private val logger: InternalLogger,
    private val hooks: Hooks,
) : Closeable {
    private val cacheKey: String = "kotlin_${options.sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
    private val mutex = Mutex()
    private val syncLock = reentrantLock()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val closed = atomic(false)
    private val initialized = atomic(false)
    private val mode = options.pollingMode
    private var pollingJob: Job? = null
    private var cachedEntry = Entry.empty
    private var cachedJsonString = ""
    private var fetchJob: Deferred<Pair<Entry, String?>>? = null
    private var fetching = false
    private var offline = options.offline

    val isOffline: Boolean get() = offline

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
                SettingResult(result.first.config.settings, result.first.fetchTime)
            }
            else -> {
                val result = fetchIfOlder(Constants.distantPast, preferCached = true)
                SettingResult(result.first.config.settings, result.first.fetchTime)
            }
        }
    }

    fun offline() {
        syncLock.withLock {
            if (offline) return
            offline = true
            pollingJob?.cancel()
            logger.debug("Switched to OFFLINE mode.")
        }
    }

    fun online() {
        syncLock.withLock {
            if (!offline) return
            offline = false
            if (mode is AutoPollMode) {
                startPoll(mode)
            }
            logger.debug("Switched to ONLINE mode.")
        }
    }

    suspend fun refresh(): RefreshResult {
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
            if (offline) {
                return Pair(cachedEntry, "The SDK is in offline mode, it can't initiate HTTP calls.")
            }

            val runningJob = fetchJob
            if (runningJob == null || !fetching) {
                // No fetch is running, initiate a new one.
                fetching = true
                val eTag = cachedEntry.eTag
                fetchJob = coroutineScope.async {
                    if (mode is AutoPollMode) {
                        if (initialized.value) { // The service is initialized, start fetch without timeout.
                            fetchConfig(eTag)
                        } else {
                            // Waiting for the client initialization.
                            // After the maxInitWaitTimeInSeconds timeout the client will be initialized and while
                            // the config is not ready the default value will be returned.
                            val result = withTimeoutOrNull(mode.configuration.maxInitWaitTime) {
                                fetchConfig(eTag)
                            }
                            if (result == null) { // We got a timeout
                                logger.warning("Max init wait time for the very first fetch reached " +
                                        "(${mode.configuration.maxInitWaitTime.inWholeMilliseconds}ms). " +
                                        "Returning cached config.")
                                setInitialized()
                            } // We got a timeout
                            result ?: Pair(Entry.empty, null)
                        }
                    } else {
                        fetchConfig(eTag)
                    }
                }
            }
        }
        val result = fetchJob?.await()

        mutex.withLock {
            return result ?: Pair(cachedEntry, null)
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
            } else if (response.isNotModified) {
                cachedEntry = cachedEntry.copy(fetchTime = DateTime.now())
                writeCache(cachedEntry)
            }
            return Pair(cachedEntry, response.error)
        }
    }

    private suspend fun readCache(): Entry {
        return try {
            val cached = options.configCache.read(cacheKey) ?: return Entry.empty
            if (cached.isEmpty() || cached == cachedJsonString) return Entry.empty
            cachedJsonString = cached
            Constants.json.decodeFromString(cached)
        } catch (e: Exception) {
            logger.error("An error occurred during the cache read. ${e.message}")
            Entry.empty
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

    private suspend fun writeCache(entry: Entry) {
        try {
            val json = Constants.json.encodeToString(entry)
            cachedJsonString = json
            options.configCache.write(cacheKey, json)
        } catch (e: Exception) {
            logger.error("An error occurred during the cache write. ${e.message}")
        }
    }

    override fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        configFetcher.close()
        coroutineScope.cancel()
    }
}
