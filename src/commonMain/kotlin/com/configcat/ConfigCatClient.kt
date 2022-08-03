package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.ConfigService
import com.configcat.log.DefaultLogger
import com.configcat.log.InternalLogger
import com.configcat.log.LogLevel
import com.configcat.log.Logger
import com.configcat.override.FlagOverrides
import com.configcat.override.OverrideBehavior
import io.ktor.client.engine.*
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

/**
 * Configuration options for [ConfigCatClient].
 */
public class ClientOptions {
    /**
     * Default: 30s. The maximum wait time for an HTTP response.
     */
    public var requestTimeoutMs: Long = 30_000

    /**
     * The base ConfigCat CDN url.
     */
    public var baseUrl: String? = null

    /**
     * The internal logger implementation. The default logger writes the messages with [println].
     */
    public var logger: Logger = DefaultLogger()

    /**
     * Default: [LogLevel.WARNING]. The internal log level.
     */
    public var logLevel: LogLevel = LogLevel.WARNING

    /**
     * The cache implementation used to cache the downloaded configurations.
     */
    public var configCache: ConfigCache = EmptyConfigCache()

    /**
     * Default: [DataGovernance.GLOBAL]. Set this parameter to be in sync with the
     * Data Governance preference on the [Dashboard](https://app.configcat.com/organization/data-governance).
     * (Only Organization Admins have access)
     */
    public var dataGovernance: DataGovernance = DataGovernance.GLOBAL

    /**
     * The polling mode.
     */
    public var pollingMode: PollingMode = autoPoll()

    /**
     * Feature flag and setting overrides.
     */
    public var flagOverrides: (FlagOverrides.() -> Unit)? = null

    /**
     * The underlying Ktor HTTP engine used to fetch the latest configuration.
     */
    public var httpEngine: HttpClientEngine? = null

    internal var sdkKey: String? = null
}

/**
 * ConfigCat SDK client.
 */
public interface ConfigCatClient {
    /**
     * Gets the value of a feature flag or setting as [Any] identified by the given [key].
     * In case of any failure, `null` will be returned. The [user] param identifies the caller.
     */
    public suspend fun getAnyValueOrNull(key: String, user: ConfigCatUser? = null): Any?

    /**
     * Gets the Variation ID (analytics) of a feature flag or setting based on its [key].
     * In case of any failure, [defaultVariationId] will be returned. The [user] param identifies the caller.
     */
    public suspend fun getVariationId(key: String, defaultVariationId: String?, user: ConfigCatUser? = null): String?

    /**
     * Gets the key of a setting and its value identified by the given [variationId] (analytics).
     */
    public suspend fun getKeyAndValue(variationId: String): Pair<String, Any>?

    /**
     * Gets a collection of all setting keys.
     */
    public suspend fun getAllKeys(): Collection<String>

    /**
     * Gets the values of all feature flags or settings. The [user] param identifies the caller.
     */
    public suspend fun getAllValues(user: ConfigCatUser? = null): Map<String, Any>

    /**
     * Gets the Variation IDs (analytics) of all feature flags or settings.
     * The [user] param identifies the caller.
     */
    public suspend fun getAllVariationIds(user: ConfigCatUser? = null): Collection<String>

    /**
     * Downloads the latest feature flag and configuration values.
     */
    public suspend fun refresh()

    /**
     * Companion object of [ConfigCatClient].
     */
    public companion object {
        /**
         * Closes an individual or all [ConfigCatClient] instances.
         *
         * If [client] is not set, all underlying [ConfigCatClient]
         * instances will be closed, otherwise only the given [client] will be closed.
         */
        public fun close(client: ConfigCatClient? = null): Unit = Client.close(client)
    }
}

/**
 * Creates a new or gets an already existing [ConfigCatClient] for the given [sdkKey].
 */
public fun ConfigCatClient(
    sdkKey: String,
    block: ClientOptions.() -> Unit = {}
): ConfigCatClient = Client.get(sdkKey, block)

/**
 * Gets the value of a feature flag or setting as [T] identified by the given [key].
 * In case of any failure, [defaultValue] will be returned. The [user] param identifies the caller.
 */
public suspend inline fun <reified T> ConfigCatClient.getValue(
    key: String,
    defaultValue: T,
    user: ConfigCatUser? = null
): T {
    return this.getAnyValueOrNull(key, user) as? T ?: defaultValue
}

internal class Client private constructor(
    private val sdkKey: String,
    options: ClientOptions
) : ConfigCatClient, Closeable {

    private val service: ConfigService?
    private val flagOverrides: FlagOverrides?
    private val evaluator: Evaluator

    init {
        options.sdkKey = sdkKey
        val logger = InternalLogger(options.logger, options.logLevel)
        flagOverrides = options.flagOverrides?.let { FlagOverrides().apply(it) }
        service = if (flagOverrides != null && flagOverrides.behavior == OverrideBehavior.LOCAL_ONLY) {
            null
        } else {
            ConfigService(options, ConfigFetcher(options, logger), logger)
        }
        evaluator = Evaluator(logger)
    }

    override suspend fun getAnyValueOrNull(key: String, user: ConfigCatUser?): Any? {
        val setting = getSettings()[key] ?: return null
        return evaluator.evaluate(setting, key, user).first
    }

    override suspend fun getVariationId(key: String, defaultVariationId: String?, user: ConfigCatUser?): String? {
        val setting = getSettings()[key] ?: return defaultVariationId
        return evaluator.evaluate(setting, key, user).second ?: defaultVariationId
    }

    override suspend fun getKeyAndValue(variationId: String): Pair<String, Any>? {
        val settings = getSettings()
        if (settings.isEmpty()) {
            return null
        }
        for (setting in settings) {
            if (setting.value.variationId == variationId) {
                return Pair(setting.key, setting.value.value)
            }
            for (rolloutRule in setting.value.rolloutRules) {
                if (rolloutRule.variationId == variationId) {
                    return Pair(setting.key, rolloutRule.value)
                }
            }
            for (percentageRule in setting.value.percentageItems) {
                if (percentageRule.variationId == variationId) {
                    return Pair(setting.key, percentageRule.value)
                }
            }
        }
        return null
    }

    override suspend fun getAllKeys(): Collection<String> {
        return getSettings().keys
    }

    override suspend fun getAllValues(user: ConfigCatUser?): Map<String, Any> {
        return getSettings().map {
            val evaluated = evaluator.evaluate(it.value, it.key, user)
            it.key to evaluated.first
        }.toMap()
    }

    override suspend fun getAllVariationIds(user: ConfigCatUser?): Collection<String> {
        return getSettings().map {
            val evaluated = evaluator.evaluate(it.value, it.key, user)
            evaluated.second
        }.filterNotNull()
    }

    override suspend fun refresh() {
        service?.refresh()
    }

    override fun close() {
        service?.close()
    }

    private suspend fun getSettings(): Map<String, Setting> {
        if (flagOverrides != null) {
            return when (flagOverrides.behavior) {
                OverrideBehavior.LOCAL_ONLY -> flagOverrides.dataSource.getOverrides()
                OverrideBehavior.LOCAL_OVER_REMOTE -> {
                    val remote = service?.getSettings() ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    remote + local
                }

                OverrideBehavior.REMOTE_OVER_LOCAL -> {
                    val remote = service?.getSettings() ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    local + remote
                }
            }
        }

        return service?.getSettings() ?: mapOf()
    }

    companion object {
        private val instances = mutableMapOf<String, Client>()
        private val lock = reentrantLock()

        fun get(sdkKey: String, block: ClientOptions.() -> Unit = {}): Client {
            if (sdkKey.isEmpty()) {
                throw IllegalArgumentException("'sdkKey' cannot be null or empty.")
            }
            lock.withLock {
                val instance = instances[sdkKey]
                if (instance != null)
                    return instance
                val client = Client(sdkKey, ClientOptions().apply(block))
                instances[sdkKey] = client
                return client
            }
        }

        fun close(client: ConfigCatClient? = null) {
            lock.withLock {
                if (client != null && client is Client) {
                    instances.remove(client.sdkKey)
                    client.close()
                    return
                }
                for (instance in instances) {
                    instance.value.close()
                }
                instances.clear()
            }
        }
    }
}
