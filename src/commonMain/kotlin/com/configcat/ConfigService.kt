package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshErrorCode
import com.configcat.fetch.RefreshResult
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Entry
import com.configcat.model.Setting
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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Instant

internal data class SettingResult(
    val settings: Map<String, Setting>,
    val fetchTime: Instant,
) {
    fun isEmpty(): Boolean = this === empty

    companion object {
        val empty: SettingResult = SettingResult(emptyMap(), Instant.DISTANT_PAST)
    }
}

internal data class InMemoryResult(
    val settingResult: SettingResult,
    val cacheState: ClientCacheState,
)

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
    private val snapshotBuilder: SnapshotBuilder,
    private val configFetcher: ConfigFetcher,
    private val logger: InternalLogger,
    private val hooks: Hooks,
) : Closeable {
    internal val cacheKey: String = getCacheKey()
    private val asyncLock = Mutex()
    private val syncLock = reentrantLock()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val closed = AtomicBoolean(false)
    private val initialized = AtomicBoolean(false)
    private val cacheStateReported = AtomicBoolean(false)
    private val userIndicatedOffline = AtomicBoolean(options.offline)
    private val inForegroundAndHasNetwork = AtomicBoolean(options.stateMonitor?.isAllowedToUseHTTP() ?: true)
    private val offline = AtomicBoolean(isOffline)
    private val mode = options.pollingMode
    private var fetchJob: Deferred<EntryResult>? = null
    private val cachedEntry = AtomicReference(Entry.empty)
    private var pollingJob: Job? = null

    val isOffline: Boolean get() = userIndicatedOffline.load() || !inForegroundAndHasNetwork.load()

    init {
        options.stateMonitor?.subscribeToStateChanges { isAllowedToUseHTTP ->
            logger.debug("Application state change notification received. isAllowedToUseHTTP: $isAllowedToUseHTTP")
            this.inForegroundAndHasNetwork.store(isAllowedToUseHTTP)
            switchStateIfNeeded()
        }

        if (mode is AutoPollMode) {
            startPoll(mode)
        } else {
            setInitializedState()
        }
    }

    private fun setInitializedState() {
        initialized.store(true)
        coroutineScope.launch {
            val inMemory = cachedEntry.load()
            if (!inMemory.isEmpty()) {
                return@launch
            }
            fetchIfOlder(Instant.DISTANT_FUTURE, preferCached = true)
        }
    }

    fun getInMemoryState(): InMemoryResult {
        val entry = cachedEntry.load()
        return InMemoryResult(
            SettingResult(entry.config.settings ?: mapOf(), entry.fetchTime),
            determineCacheState(entry),
        )
    }

    suspend fun getSettings(): SettingResult {
        val result =
            when (mode) {
                is LazyLoadMode -> {
                    fetchIfOlder(
                        Clock.System.now().minus(mode.configuration.cacheRefreshInterval),
                    )
                }
                else -> {
                    // If we are initialized, we prefer the cached results
                    val threshold =
                        if (!initialized.load() && mode is AutoPollMode) {
                            Clock.System.now().minus(mode.configuration.pollingInterval)
                        } else {
                            Instant.DISTANT_PAST
                        }
                    fetchIfOlder(threshold, preferCached = initialized.load())
                }
            }
        return if (result.entry.isEmpty()) {
            SettingResult.empty
        } else {
            SettingResult(result.entry.config.settings ?: emptyMap(), result.entry.fetchTime)
        }
    }

    fun offline() {
        if (!userIndicatedOffline.compareAndSet(expectedValue = false, newValue = true)) return
        switchStateIfNeeded()
    }

    fun online() {
        if (!userIndicatedOffline.compareAndSet(expectedValue = true, newValue = false)) return
        switchStateIfNeeded()
    }

    fun switchStateIfNeeded() {
        val currentOfflineState = isOffline
        if (currentOfflineState && offline.compareAndSet(expectedValue = false, newValue = true)) {
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("OFFLINE"))
        }
        if (!currentOfflineState && offline.compareAndSet(expectedValue = true, newValue = false)) {
            if (mode is AutoPollMode) {
                startPoll(mode)
            }
            logger.info(5200, ConfigCatLogMessages.getConfigServiceStatusChanged("ONLINE"))
        }
    }

    suspend fun refresh(): RefreshResult {
        if (isOffline && (options.configCache == null || options.configCache is EmptyConfigCache)) {
            val offlineMessage = ConfigCatLogMessages.CONFIG_SERVICE_CANNOT_INITIATE_HTTP_CALLS_WARN
            logger.warning(3200, offlineMessage)
            return RefreshResult(false, offlineMessage, RefreshErrorCode.OFFLINE_CLIENT, null)
        }
        val result = fetchIfOlder(Instant.DISTANT_FUTURE)
        return RefreshResult(result.errorMessage == null, result.errorMessage, result.errorCode, result.exception)
    }

    @Suppress("ComplexMethod")
    private suspend fun fetchIfOlder(
        threshold: Instant,
        preferCached: Boolean = false,
    ): EntryResult {
        asyncLock.withLock {
            // Sync up with the cache and use it when it's not expired.
            val fromCache = readCache()
            if (!fromCache.isEmpty() && fromCache.eTag != cachedEntry.load().eTag) {
                cachedEntry.store(fromCache)
                reportConfigChanged()
            }
            // Cache isn't expired
            if (!cachedEntry.load().isExpired(threshold)) {
                initializeAndReportCacheState()
                return EntryResult(cachedEntry.load(), null, RefreshErrorCode.NONE, null)
            }
            // If we are in offline mode, or prohibited from using HTTP,
            // or the caller prefers cached values, do not initiate fetch.
            if (isOffline || preferCached) {
                initializeAndReportCacheState()
                return EntryResult(cachedEntry.load(), null, RefreshErrorCode.NONE, null)
            }

            if (fetchJob == null) {
                // No fetch is running, initiate a new one.
                val eTag = cachedEntry.load().eTag
                fetchJob =
                    coroutineScope.async {
                        if (mode is AutoPollMode && !initialized.load()) {
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
                            initializeAndReportCacheState()

                            return@async EntryResult(Entry.empty, message, RefreshErrorCode.CLIENT_INIT_TIMED_OUT, null)
                        } else {
                            // The service is initialized, start fetch without timeout.
                            return@async fetchConfig(eTag)
                        }
                    }
            }
        }
        // Await the fetch routine.
        val result = fetchJob?.await()
        return result?.let { value ->
            when {
                value.entry.isEmpty() -> value.withEntry(cachedEntry.load())
                else -> value
            }
        } ?: EntryResult.success(cachedEntry.load())
    }

    private suspend fun fetchConfig(eTag: String): EntryResult {
        val response = configFetcher.fetch(eTag)
        asyncLock.withLock {
            fetchJob = null
            if (response.isFetched) {
                cachedEntry.store(response.entry)
                writeCache(response.entry)
                reportConfigChanged()
                initializeAndReportCacheState()
                return EntryResult.success(response.entry)
            } else if ((response.isNotModified || !response.isTransientError) && !cachedEntry.load().isEmpty()) {
                cachedEntry.store(cachedEntry.load().copy(fetchTime = Clock.System.now()))
                writeCache(cachedEntry.load())
            }
            initializeAndReportCacheState()
            return EntryResult(cachedEntry.load(), response.error, response.errorCode, response.errorException)
        }
    }

    private fun startPoll(mode: AutoPollMode) {
        logger.debug("Start polling with ${mode.configuration.pollingInterval.inWholeMilliseconds}ms interval.")
        syncLock.withLock {
            pollingJob?.cancel()
            pollingJob =
                coroutineScope.launch {
                    while (isActive) {
                        fetchIfOlder(
                            Clock.System.now().minus(mode.configuration.pollingInterval - 500.milliseconds),
                        )
                        delay(mode.configuration.pollingInterval)
                    }
                }
        }
    }

    private fun initializeAndReportCacheState() {
        initialized.store(true)
        if (cacheStateReported.compareAndSet(expectedValue = false, newValue = true)) {
            coroutineScope.launch {
                hooks.invokeOnClientReady(snapshotBuilder, getInMemoryState())
            }
        }
    }

    private fun reportConfigChanged() {
        coroutineScope.launch {
            hooks.invokeOnConfigChanged(snapshotBuilder, getInMemoryState())
        }
    }

    private suspend fun readCache(): Entry {
        return try {
            val cached = options.configCache?.read(cacheKey) ?: ""
            if (cached.isEmpty() || cached == cachedEntry.load().cacheString) return Entry.empty
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
        "${options.sdkKey}_${Constants.CONFIG_FILE_NAME}_${Constants.SERIALIZATION_FORMAT_VERSION}"
            .encodeToByteArray()
            .sha1Hex()

    override fun close() {
        if (!closed.compareAndSet(expectedValue = false, newValue = true)) return
        configFetcher.close()
        options.stateMonitor?.close()
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
                        Clock.System.now().minus(mode.configuration.cacheRefreshInterval),
                    )
                ) {
                    return ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY
                }
            }
            is AutoPollMode -> {
                if (cachedEntry.isExpired(
                        Clock.System.now().minus(mode.configuration.pollingInterval),
                    )
                ) {
                    return ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY
                }
            }
        }
        return ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA
    }
}
