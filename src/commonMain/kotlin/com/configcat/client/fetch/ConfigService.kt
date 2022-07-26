package com.configcat.client.fetch

import com.configcat.client.*
import com.configcat.client.Constants
import com.configcat.client.logging.InternalLogger
import com.soywiz.klock.DateTime
import com.soywiz.krypto.sha1
import kotlinx.atomicfu.atomic
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class ConfigService constructor(
    private val options: ClientOptions,
    private val configFetcher: ConfigFetcher,
    private val logger: InternalLogger,
) {
    private val cacheKey: String = "kotlin_${options.sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
    private val mutex = Mutex()
    private val coroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val closed = atomic(false)
    private val initialized = atomic(false)
    private val mode = options.pollingMode
    private var cachedEntry = Entry.empty
    private var fetchJob: Deferred<Config>? = null

    init {
        if (mode is AutoPollMode) {
            coroutineScope.launch {
                while (isActive) {
                    refresh()
                    delay(mode.configuration.pollingIntervalSeconds.toLongMillis())
                }
            }
        } else {
            initialized.value = true
        }
    }

    suspend fun getSettings(): Map<String, Setting> {
        return when (mode) {
            is LazyLoadMode -> fetchIfOlder(
                DateTime.now()
                    .add(0, -mode.configuration.cacheRefreshIntervalSeconds.toDoubleMillis())
            ).settings
            else -> return fetchIfOlder(Constants.minDate, preferCached = true).settings
        }
    }

    suspend fun refresh() {
        fetchIfOlder(DateTime.now().add(1, 0.0))
    }

    private suspend fun fetchIfOlder(time: DateTime, preferCached: Boolean = false): Config {
        mutex.lock()
        if (cachedEntry.isEmpty() || cachedEntry.fetchTime > time) {
            val json = readCache()
            if (json.isNotEmpty() && json != cachedEntry.json) {
                val (config, error) = json.parseConfigJson()
                when (error) {
                    null -> cachedEntry = Entry(config, json, "", Constants.minDate)
                    else -> logger.error("JSON parsing failed. ${error.message}")
                }
            }
            if (cachedEntry.fetchTime > time) {
                mutex.unlock()
                return cachedEntry.config
            }
        }
        if (preferCached && initialized.value) {
            mutex.unlock()
            return cachedEntry.config
        }

        val runningJob = fetchJob
        if (runningJob != null) {
            mutex.unlock()
            return runningJob.await()
        }

        val eTag = cachedEntry.eTag
        val fetch = coroutineScope.async {
            if (mode is AutoPollMode) {
                if (initialized.value) {
                    fetchConfig(eTag)
                } else {
                    val result = withTimeoutOrNull(mode.configuration.maxInitWaitTimeSeconds.toLongMillis()) {
                        fetchConfig(eTag)
                    }
                    result ?: Config.empty
                }
            } else {
                fetchConfig(eTag)
            }
        }
        fetchJob = fetch
        mutex.unlock()

        val result = fetch.await()
        mutex.withLock {
            fetchJob = null
            return if (!result.isEmpty()) result else cachedEntry.config
        }
    }

    private suspend fun fetchConfig(eTag: String): Config {
        val response = configFetcher.fetch(eTag)
        mutex.withLock {
            initialized.value = true
            if (response.isFetched && response.entry != cachedEntry) {
                cachedEntry = response.entry
                writeCache(response.entry.json)
                return response.entry.config
            } else if (response.isNotModified) {
                cachedEntry = cachedEntry.copy(fetchTime = DateTime.now())
            }
            return cachedEntry.config
        }
    }

    private suspend fun readCache(): String {
        return try {
            options.configCache.read(cacheKey)
        } catch (e: Exception) {
            logger.error("An error occurred during the cache read. ${e.message}")
            ""
        }
    }

    private suspend fun writeCache(json: String) {
        try {
            options.configCache.write(cacheKey, json)
        } catch (e: Exception) {
            logger.error("An error occurred during the cache write. ${e.message}")
        }
    }

    fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        configFetcher.close()
        coroutineScope.cancel()
    }
}
