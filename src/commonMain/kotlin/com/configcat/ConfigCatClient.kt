package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshResult
import com.configcat.log.*
import com.configcat.override.FlagOverrides
import com.configcat.override.OverrideBehavior
import com.soywiz.klock.DateTime
import io.ktor.client.engine.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Configuration options for [ConfigCatClient].
 */
public class ConfigCatOptions {
    /**
     * Default: 30s. The maximum wait time for an HTTP response.
     */
    public var requestTimeout: Duration = 30.seconds

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
    public var configCache: ConfigCache? = defaultCache()

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

    /**
     * Proxy configuration for the HTTP engine.
     */
    public var httpProxy: ProxyConfig? = null

    /**
     * The default user, used as fallback when there's no user
     * parameter is passed to the [ConfigCatClient.getValue] method.
     */
    public var defaultUser: ConfigCatUser? = null

    /**
     * Indicates whether the SDK should be initialized in offline mode or not.
     */
    public var offline: Boolean = false

    /**
     * Hooks for events fired by [ConfigCatClient].
     */
    public var hooks: Hooks = Hooks()

    internal var sdkKey: String? = null
}

/**
 * ConfigCat SDK client.
 */
public interface ConfigCatClient {
    /**
     * Gets the value of a feature flag or setting as [Any] identified by the given [key].
     * In case of any failure, [defaultValue] will be returned. The [user] param identifies the caller.
     */
    public suspend fun getAnyValue(key: String, defaultValue: Any, user: ConfigCatUser?): Any

    /**
     * Gets the value and evaluation details of a feature flag or setting identified by the given [key].
     * The [user] param identifies the caller.
     */
    public suspend fun getAnyValueDetails(key: String, defaultValue: Any, user: ConfigCatUser?): EvaluationDetails

    /**
     * Gets the values along with evaluation details of all feature flags and settings.
     * The [user] param identifies the caller.
     */
    public suspend fun getAllValueDetails(user: ConfigCatUser? = null): Collection<EvaluationDetails>

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
     * Downloads the latest feature flag and configuration values.
     */
    public suspend fun forceRefresh(): RefreshResult

    /**
     * Configures the SDK to allow HTTP requests.
     */
    public fun setOnline()

    /**
     * Configures the SDK to not initiate HTTP requests.
     */
    public fun setOffline()

    /**
     * True when the SDK is configured not to initiate HTTP requests, otherwise false.
     */
    public val isOffline: Boolean

    /**
     * Gets the [Hooks] object for subscribing events.
     */
    public val hooks: Hooks

    /**
     * Sets the default user.
     */
    public fun setDefaultUser(user: ConfigCatUser)

    /**
     * Sets the default user to null.
     */
    public fun clearDefaultUser()

    /**
     * Closes the client.
     */
    public fun close()

    /**
     * Get the client closed status.
     */
    public fun isClosed(): Boolean

    /**
     * Companion object of [ConfigCatClient].
     */
    public companion object {
        /**
         * Closes all [ConfigCatClient] instances.
         */
        public fun closeAll(): Unit = Client.closeAll()
    }
}

/**
 * Creates a new or gets an already existing [ConfigCatClient] for the given [sdkKey].
 */
public fun ConfigCatClient(
    sdkKey: String,
    block: ConfigCatOptions.() -> Unit = {}
): ConfigCatClient = Client.get(sdkKey, block)

/**
 * Gets the value of a feature flag or setting as [T] identified by the given [key].
 * In case of any failure, [defaultValue] will be returned. The [user] param identifies the caller.
 */
public suspend inline fun <reified T : Any> ConfigCatClient.getValue(
    key: String,
    defaultValue: T,
    user: ConfigCatUser? = null
): T = this.getAnyValue(key, defaultValue, user) as? T ?: defaultValue

/**
 * Gets the value and evaluation details of a feature flag or setting identified by the given [key].
 * The [user] param identifies the caller.
 */
public suspend inline fun <reified T : Any> ConfigCatClient.getValueDetails(
    key: String,
    defaultValue: T,
    user: ConfigCatUser? = null
): TypedEvaluationDetails<T> {
    val details = this.getAnyValueDetails(key, defaultValue, user)
    val value = details.value as? T
    return TypedEvaluationDetails(
        details.key,
        details.variationId,
        user,
        details.isDefaultValue || value == null,
        details.error,
        value ?: defaultValue,
        details.fetchTimeUnixMilliseconds,
        details.matchedEvaluationRule,
        details.matchedEvaluationPercentageRule
    )
}

internal class Client private constructor(
    private val sdkKey: String,
    options: ConfigCatOptions
) : ConfigCatClient, Closeable {

    private val service: ConfigService?
    private val flagOverrides: FlagOverrides?
    private val evaluator: Evaluator
    private val logger: InternalLogger
    private var defaultUser: ConfigCatUser?
    private val isClosed = atomic(false)

    override val hooks: Hooks

    init {
        options.sdkKey = sdkKey
        logger = InternalLogger(options.logger, options.logLevel, options.hooks)
        hooks = options.hooks
        defaultUser = options.defaultUser
        flagOverrides = options.flagOverrides?.let { FlagOverrides().apply(it) }
        service = if (flagOverrides != null && flagOverrides.behavior == OverrideBehavior.LOCAL_ONLY) {
            null
        } else {
            ConfigService(options, ConfigFetcher(options, logger), logger, options.hooks)
        }
        evaluator = Evaluator(logger)
    }

    override suspend fun getAnyValue(key: String, defaultValue: Any, user: ConfigCatUser?): Any {
        val settingResult = getSettings()
        val evalUser = user ?: defaultUser
        val checkSettingAvailableMessage = checkSettingAvailable(settingResult, key, defaultValue)
        if (checkSettingAvailableMessage != null) {
            val details = EvaluationDetails.makeError(key, defaultValue, checkSettingAvailableMessage, evalUser)
            hooks.invokeOnFlagEvaluated(details)
            return defaultValue
        }
        val setting = settingResult.settings[key]

        return evaluate(setting!!, key, evalUser, settingResult.fetchTime).value
    }

    override suspend fun getAnyValueDetails(key: String, defaultValue: Any, user: ConfigCatUser?): EvaluationDetails {
        val settingResult = getSettings()
        val evalUser = user ?: defaultUser
        val checkSettingAvailableMessage = checkSettingAvailable(settingResult, key, defaultValue)
        if (checkSettingAvailableMessage != null) {
            val details = EvaluationDetails.makeError(key, defaultValue, checkSettingAvailableMessage, evalUser)
            hooks.invokeOnFlagEvaluated(details)
            return details
        }
        val setting = settingResult.settings[key]

        return evaluate(setting!!, key, evalUser, settingResult.fetchTime)
    }

    override suspend fun getAllValueDetails(user: ConfigCatUser?): Collection<EvaluationDetails> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty list")) {
            return emptyList()
        }
        return settingResult.settings.map {
            evaluate(it.value, it.key, user ?: defaultUser, settingResult.fetchTime)
        }
    }

    override suspend fun getKeyAndValue(variationId: String): Pair<String, Any>? {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "null")) {
            return null
        }
        val settings = settingResult.settings
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
        this.logger.error(2011, ConfigCatLogMessages.getSettingForVariationIdIsNotPresent(variationId))
        return null
    }

    override suspend fun getAllKeys(): Collection<String> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty array")) {
            return emptyList()
        }
        return settingResult.settings.keys
    }

    override suspend fun getAllValues(user: ConfigCatUser?): Map<String, Any> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty map")) {
            return emptyMap()
        }
        return settingResult.settings.map {
            val evaluated = evaluate(it.value, it.key, user ?: defaultUser, settingResult.fetchTime)
            it.key to evaluated.value
        }.toMap()
    }

    override suspend fun forceRefresh(): RefreshResult = service?.refresh() ?: RefreshResult(
        false,
        "The ConfigCat SDK is in local-only mode. Calling .forceRefresh() has no effect."
    )

    override fun setOffline() {
        if (service == null || isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setOffline")
            )
            return
        }
        service.offline()
    }

    override fun setOnline() {
        if (service == null || isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setOnline")
            )
            return
        }
        service.online()
    }

    override val isOffline: Boolean
        get() = service?.isOffline ?: true

    override fun setDefaultUser(user: ConfigCatUser) {
        if (isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setDefaultUser")
            )
            return
        }
        defaultUser = user
    }

    override fun clearDefaultUser() {
        if (isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("clearDefaultUser")
            )
            return
        }
        defaultUser = null
    }

    override fun close() {
        if (!this.isClosed.compareAndSet(false, update = true)) {
            return
        }
        closeResources()
        removeFromInstances(this)
    }

    override fun isClosed(): Boolean {
        return isClosed.value
    }

    private fun closeResources() {
        service?.close()
        hooks.clear()
    }

    private fun evaluate(setting: Setting, key: String, user: ConfigCatUser?, fetchTime: DateTime): EvaluationDetails {
        val (value, variationId, targetingRule, percentageRule) = evaluator.evaluate(setting, key, user)
        val details = EvaluationDetails(
            key, variationId, user, false, null, value,
            fetchTime.unixMillisLong, targetingRule, percentageRule
        )
        hooks.invokeOnFlagEvaluated(details)
        return details
    }

    private suspend fun getSettings(): SettingResult {
        if (flagOverrides != null) {
            return when (flagOverrides.behavior) {
                OverrideBehavior.LOCAL_ONLY -> SettingResult(
                    flagOverrides.dataSource.getOverrides(),
                    Constants.distantPast
                )

                OverrideBehavior.LOCAL_OVER_REMOTE -> {
                    val result = service?.getSettings()
                    val remote = result?.settings ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    SettingResult(remote + local, result?.fetchTime ?: Constants.distantPast)
                }

                OverrideBehavior.REMOTE_OVER_LOCAL -> {
                    val result = service?.getSettings()
                    val remote = result?.settings ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    SettingResult(local + remote, result?.fetchTime ?: Constants.distantPast)
                }
            }
        }

        return service?.getSettings() ?: SettingResult(mapOf(), Constants.distantPast)
    }

    private fun checkSettingsAvailable(settingResult: SettingResult, emptyResult: String): Boolean {
        if (settingResult.isEmpty()) {
            this.logger.error(1000, ConfigCatLogMessages.getConfigJsonIsNotPresentedWithEmptyResult(emptyResult))
            return false
        }
        return true
    }

    private fun <T> checkSettingAvailable(
        settingResult: SettingResult,
        key: String,
        defaultValue: T
    ): String? {
        if (settingResult.isEmpty()) {
            val errorMessage =
                ConfigCatLogMessages.getConfigJsonIsNotPresentedWithDefaultValue(key, "defaultValue", defaultValue)
            logger.error(1000, errorMessage)
            return errorMessage
        }
        val setting = settingResult.settings[key]
        if (setting == null) {
            val errorMessage = ConfigCatLogMessages.getSettingEvaluationFailedDueToMissingKey(
                key,
                "defaultValue",
                defaultValue,
                settingResult.settings.keys
            )
            logger.error(1001, errorMessage)
            return errorMessage
        }
        return null
    }

    companion object {
        private val instances = mutableMapOf<String, Client>()
        private val lock = reentrantLock()

        fun get(sdkKey: String, block: ConfigCatOptions.() -> Unit = {}): Client {
            require(sdkKey.isNotEmpty()) { "'sdkKey' cannot be empty." }
            lock.withLock {
                val instance = instances[sdkKey]
                if (instance != null) {
                    instance.logger.warning(3000, ConfigCatLogMessages.getClientIsAlreadyCreated(sdkKey))
                    return instance
                }
                val client = Client(sdkKey, ConfigCatOptions().apply(block))
                instances[sdkKey] = client
                return client
            }
        }

        fun removeFromInstances(client: Client) {
            lock.withLock {
                if (instances[client.sdkKey] == client) {
                    instances.remove(client.sdkKey)
                }
            }
        }

        fun closeAll() {
            lock.withLock {
                for (instance in instances) {
                    instance.value.closeResources()
                }
                instances.clear()
            }
        }
    }
}
