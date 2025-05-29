package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshErrorCode
import com.configcat.fetch.RefreshResult
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Entry
import com.configcat.model.Setting
import korlibs.crypto.sha1
import korlibs.time.DateTime
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeoutOrNull

internal data class SettingResult(val settings: Map<String, Setting>, val fetchTime: DateTime) {
    fun isEmpty(): Boolean = this === empty

    companion object {
        val empty: SettingResult = SettingResult(emptyMap(), Constants.distantPast)
    }
}

internal data class EntryResult(
    val entry: Entry,
    val errorMessage: String?,
    val errorCode: RefreshErrorCode,
    val exception: Exception?,
) {
    fun withEntry(entry: Entry) = EntryResult(entry, this.errorMessage, this.errorCode, this.exception)

    companion object {
        fun success(entry: Entry) = EntryResult(entry, null, RefreshErrorCode.NONE, null)
    }
}

internal class ConfigService(
    private val options: ConfigCatOptions,
    private val configFetcher: ConfigFetcher,
    private val logger: InternalLogger,
    private val hooks: Hooks,
) : Closeable {
    internal val cacheKey: String = getCacheKey()
    private val mutex = Mutex()
    private val syncLock = reentrantLock()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val closed = atomic(false)
    private val initialized = atomic(false)
    private val cacheStateReported = atomic(false)
    private val offline = atomic(options.offline)
    private val mode = options.pollingMode
    private val fetchJob: AtomicRef<Deferred<EntryResult>?> = atomic(null)
    private val cachedEntry: AtomicRef<Entry> = atomic(Entry.empty)
    private var pollingJob: Job? = null
    private var fetching = false

    val isOffline: Boolean get() = offline.value

    init {
        if (mode is AutoPollMode) {
            startPoll(mode)
        } else {
            setInitializedState()
        }
    }

    private fun setInitializedState() {
        initialized.value = true
        coroutineScope.launch {
            mutex.withLock {
                val fromCache = readCache()
                if (!fromCache.isEmpty() && fromCache.eTag != cachedEntry.value.eTag) {
                    hooks.invokeOnConfigChanged(fromCache.config.settings)
                    cachedEntry.value = fromCache
                }
                initializeAndReportCacheState(cachedEntry.value)
            }
        }
    }

    fun getInMemorySettings(): SettingResult {
        val entry = cachedEntry.value
        return SettingResult(entry.config.settings ?: mapOf(), entry.fetchTime)
    }

    suspend fun getSettings(): SettingResult {
        val result =
            when (mode) {
                is LazyLoadMode -> {
                    fetchIfOlder(
                        DateTime.now()
                            .add(0, -mode.configuration.cacheRefreshInterval.inWholeMilliseconds.toDouble()),
                    )
                }
                else -> {
                    // If we are initialized, we prefer the cached results
                    val threshold =
                        if (!initialized.value && mode is AutoPollMode) {
                            DateTime.now()
                                .add(0, -mode.configuration.pollingInterval.inWholeMilliseconds.toDouble())
                        } else {
                            Constants.distantPast
                        }
                    fetchIfOlder(threshold, preferCached = initialized.value)
                }
            }
        return if (result.entry.isEmpty()) {
            SettingResult.empty
        } else {
            SettingResult(result.entry.config.settings ?: emptyMap(), result.entry.fetchTime)
        }
    }

    fun offline() {
        syncLock.withLock {
            if (!offline.compareAndSet(expect = false, update = true)) return
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("OFFLINE"))
        }
    }

    fun online() {
        syncLock.withLock {
            if (!offline.compareAndSet(expect = true, update = false)) return
            if (mode is AutoPollMode) {
                pollingJob?.cancel()
                startPoll(mode)
            }
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("ONLINE"))
        }
    }

    suspend fun refresh(): RefreshResult {
        if (offline.value && (options.configCache == null || options.configCache is EmptyConfigCache)) {
            val offlineMessage = ConfigCatLogMessages.CONFIG_SERVICE_CANNOT_INITIATE_HTTP_CALLS_WARN
            logger.warning(3200, offlineMessage)
            return RefreshResult(false, offlineMessage, RefreshErrorCode.OFFLINE_CLIENT, null)
        }
        val result = fetchIfOlder(Constants.distantFuture)
        return RefreshResult(result.errorMessage == null, result.errorMessage, result.errorCode, result.exception)
    }

    @Suppress("ComplexMethod")
    private suspend fun fetchIfOlder(
        threshold: DateTime,
        preferCached: Boolean = false,
    ): EntryResult {
        mutex.withLock {
            // Sync up with the cache and use it when it's not expired.
            val fromCache = readCache()
            if (!fromCache.isEmpty() && fromCache.eTag != cachedEntry.value.eTag) {
                hooks.invokeOnConfigChanged(fromCache.config.settings)
                cachedEntry.value = fromCache
            }
            // Cache isn't expired
            if (!cachedEntry.value.isExpired(threshold)) {
                initializeAndReportCacheState(cachedEntry.value)
                return EntryResult(cachedEntry.value, null, RefreshErrorCode.NONE, null)
            }
            // If we are in offline mode or the caller prefers cached values, do not initiate fetch.
            if (offline.value || preferCached) {
                initializeAndReportCacheState(cachedEntry.value)
                return EntryResult(cachedEntry.value, null, RefreshErrorCode.NONE, null)
            }

            if (fetchJob.value == null || !fetching) {
                // No fetch is running, initiate a new one.
                fetching = true
                val eTag = cachedEntry.value.eTag
                fetchJob.value =
                    coroutineScope.async {
                        if (mode is AutoPollMode && !initialized.value) {
                            // Waiting for the client initialization.
                            // After the maxInitWaitTimeInSeconds timeout the client will be initialized and while
                            // the config is not ready the default value will be returned.
                            val result =
                                withTimeoutOrNull(mode.configuration.maxInitWaitTime) {
                                    fetchConfig(eTag)
                                }
                            if (result != null) {
                                return@async result
                            }
                            // We got a timeout
                            val message =
                                ConfigCatLogMessages.getAutoPollMaxInitWaitTimeReached(
                                    mode.configuration.maxInitWaitTime.inWholeMilliseconds,
                                )
                            logger.warning(4200, message)
                            initializeAndReportCacheState(cachedEntry.value)

                            return@async EntryResult(Entry.empty, message, RefreshErrorCode.CLIENT_INIT_TIMED_OUT, null)
                        } else {
                            // The service is initialized, start fetch without timeout.
                            return@async fetchConfig(eTag)
                        }
                    }
            }
        }
        // Await the fetch routine.
        val result = fetchJob.value?.await()

        mutex.withLock {
            return result?.let { value ->
                when {
                    value.entry.isEmpty() -> value.withEntry(cachedEntry.value)
                    else -> value
                }
            } ?: EntryResult.success(cachedEntry.value)
        }
    }

    private suspend fun fetchConfig(eTag: String): EntryResult {
        val response = configFetcher.fetch(eTag)
        mutex.withLock {
            fetching = false
            if (response.isFetched) {
                cachedEntry.value = response.entry
                writeCache(response.entry)
                hooks.invokeOnConfigChanged(response.entry.config.settings)
                initializeAndReportCacheState(response.entry)
                return EntryResult.success(response.entry)
            } else if ((response.isNotModified || !response.isTransientError) && !cachedEntry.value.isEmpty()) {
                cachedEntry.value = cachedEntry.value.copy(fetchTime = DateTime.now())
                writeCache(cachedEntry.value)
            }
            initializeAndReportCacheState(cachedEntry.value)
            return EntryResult(cachedEntry.value, response.error, response.errorCode, response.errorException)
        }
    }

    private fun startPoll(mode: AutoPollMode) {
        logger.debug("Start polling with ${mode.configuration.pollingInterval.inWholeMilliseconds}ms interval.")
        pollingJob =
            coroutineScope.launch {
                while (isActive) {
                    fetchIfOlder(
                        DateTime.now()
                            .add(0, -(mode.configuration.pollingInterval.inWholeMilliseconds.toDouble() - 500)),
                    )
                    delay(mode.configuration.pollingInterval)
                }
            }
    }

    private fun initializeAndReportCacheState(entry: Entry) {
        initialized.value = true
        if (cacheStateReported.compareAndSet(expect = false, update = true)) {
            hooks.invokeOnClientReady(determineCacheState(entry))
        }
    }

    private suspend fun readCache(): Entry {
        return try {
            val cached = options.configCache?.read(cacheKey) ?: ""
            if (cached.isEmpty() || cached == cachedEntry.value.cacheString) return Entry.empty
            Entry.fromString(cached)
        } catch (e: Exception) {
            logger.error(2200, ConfigCatLogMessages.CONFIG_SERVICE_CACHE_READ_ERROR, e)
            Entry.empty
        }
    }

    private suspend fun writeCache(entry: Entry) {
        options.configCache?.let { cache ->
            try {
                cache.write(cacheKey, entry.cacheString)
            } catch (e: Exception) {
                logger.error(2201, ConfigCatLogMessages.CONFIG_SERVICE_CACHE_WRITE_ERROR, e)
            }
        }
    }

    private fun getCacheKey() =
        "${options.sdkKey}_${Constants.CONFIG_FILE_NAME}_${Constants.SERIALIZATION_FORMAT_VERSION}".encodeToByteArray()
            .sha1().hex

    override fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        configFetcher.close()
        coroutineScope.cancel()
    }

    private fun determineCacheState(cachedEntry: Entry): ClientCacheState {
        if (cachedEntry.isEmpty()) {
            return ClientCacheState.NO_FLAG_DATA
        }
        when (mode) {
            is ManualPollMode -> {
                return ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY
            }
            is LazyLoadMode -> {
                if (cachedEntry.isExpired(
                        DateTime.now()
                            .add(0, -mode.configuration.cacheRefreshInterval.inWholeMilliseconds.toDouble()),
                    )
                ) {
                    return ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY
                }
            }
            is AutoPollMode -> {
                if (cachedEntry.isExpired(
                        DateTime.now()
                            .add(0, -mode.configuration.pollingInterval.inWholeMilliseconds.toDouble()),
                    )
                ) {
                    return ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY
                }
            }
        }
        return ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA
    }
}
