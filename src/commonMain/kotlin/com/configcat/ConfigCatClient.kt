package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshResult
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.DefaultLogger
import com.configcat.log.InternalLogger
import com.configcat.log.LogLevel
import com.configcat.log.Logger
import com.configcat.model.Setting
import com.configcat.model.SettingType
import com.configcat.override.FlagOverrides
import com.configcat.override.OverrideBehavior
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyConfig
import korlibs.time.DateTime
import kotlinx.atomicfu.AtomicRef
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CompletableDeferred
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

    internal fun isBaseURLCustom(): Boolean {
        return !baseUrl.isNullOrEmpty()
    }
}

/**
 * ConfigCat SDK client.
 */
public interface ConfigCatClient {
    /**
     * Gets the value of a feature flag or setting as [Any] identified by the given [key].
     *
     * @param key          the identifier of the feature flag or setting.
     * @param defaultValue in case of any failure, this value will be returned.
     * @param user         the user object.
     */
    public suspend fun getAnyValue(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): Any?

    /**
     * Gets the value and evaluation details of a feature flag or setting identified by the given [key].
     *
     * @param key          the identifier of the feature flag or setting.
     * @param defaultValue in case of any failure, this value will be returned.
     * @param user         the user object.
     */
    public suspend fun getAnyValueDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): EvaluationDetails

    /**
     * Gets the values along with evaluation details of all feature flags and settings.
     *
     * @param user the user object.
     */
    public suspend fun getAllValueDetails(user: ConfigCatUser? = null): Collection<EvaluationDetails>

    /**
     * Gets the key of a setting and its value identified by the given [variationId] (analytics).
     *
     * @param variationId the Variation ID.
     */
    public suspend fun getKeyAndValue(variationId: String): Pair<String, Any>?

    /**
     * Gets a collection of all setting keys.
     */
    public suspend fun getAllKeys(): Collection<String>

    /**
     * Gets the values of all feature flags or settings. The [user] param identifies the caller.
     *
     * @param user the user object.
     */
    public suspend fun getAllValues(user: ConfigCatUser? = null): Map<String, Any?>

    /**
     * Initiates a force refresh on the cached configuration.
     */
    public suspend fun forceRefresh(): RefreshResult

    /**
     * Configures the SDK to not initiate HTTP requests and work only from its cache.
     */
    public fun setOnline()

    /**
     * Set the client to offline mode. HTTP calls are not allowed.
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
     * If no user specified in the following calls [getValue], [getAnyValue], [getAllValues], [getValueDetails],
     * [getAnyValueDetails], [getAllValueDetails] the default user value will be used.
     *
     * @param user The new default user.
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
     * Awaits for SDK initialization.
     *
     * @return the future which executes the wait for ready and return with the client state.
     */
    public suspend fun waitForReady(): CompletableDeferred<ClientCacheState>

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
    block: ConfigCatOptions.() -> Unit = {},
): ConfigCatClient = Client.get(sdkKey, block)

/**
 * Gets the value of a feature flag or setting as [T] identified by the given [key].
 *
 * @param key          the identifier of the feature flag or setting.
 * @param defaultValue in case of any failure, this value will be returned.
 * @param user         the user object.
 * @param T            the type of the desired feature flag or setting. Only the following types are allowed: [String],
 * [Boolean], [Int] and [Double] (both nullable and non-nullable).
 */
public suspend inline fun <reified T> ConfigCatClient.getValue(
    key: String,
    defaultValue: T,
    user: ConfigCatUser? = null,
): T {
    require(
        T::class == Boolean::class ||
            T::class == String::class ||
            T::class == Int::class ||
            T::class == Double::class,
    ) {
        "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable)."
    }

    return getValueInternal(this, key, defaultValue, user) as T
}

@PublishedApi
internal suspend fun getValueInternal(
    configCatClient: ConfigCatClient,
    key: String,
    defaultValue: Any?,
    user: ConfigCatUser?,
): Any? {
    val client = configCatClient as? Client
    return client?.getValueImpl(key, defaultValue, user, allowAnyReturnType = false)
        ?: configCatClient.getAnyValue(key, defaultValue, user)
}

/**
 * Gets the value and evaluation details of a feature flag or setting identified by the given [key].
 *
 * @param key          the identifier of the feature flag or setting.
 * @param defaultValue in case of any failure, this value will be returned.
 * @param user         the user object.
 * @param T            the type of the desired feature flag or setting. Only the following types are allowed: [String],
 * [Boolean], [Int] and [Double] (both nullable and non-nullable).
 */
public suspend inline fun <reified T> ConfigCatClient.getValueDetails(
    key: String,
    defaultValue: T,
    user: ConfigCatUser? = null,
): TypedEvaluationDetails<T> {
    require(
        T::class == Boolean::class ||
            T::class == String::class ||
            T::class == Int::class ||
            T::class == Double::class,
    ) {
        "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable)."
    }

    val details = getValueDetailsInternal(this, key, defaultValue, user)
    return TypedEvaluationDetails(
        details.key,
        details.variationId,
        user,
        details.isDefaultValue,
        details.error,
        details.value as T,
        details.fetchTimeUnixMilliseconds,
        details.matchedTargetingRule,
        details.matchedPercentageOption,
    )
}

@PublishedApi
internal suspend fun getValueDetailsInternal(
    configCatClient: ConfigCatClient,
    key: String,
    defaultValue: Any?,
    user: ConfigCatUser?,
): EvaluationDetails {
    val client = configCatClient as? Client
    return client?.getValueDetailsImpl(key, defaultValue, user, allowAnyReturnType = false)
        ?: configCatClient.getAnyValueDetails(key, defaultValue, user)
}

internal class Client private constructor(
    private val sdkKey: String,
    options: ConfigCatOptions,
) : ConfigCatClient, Closeable {
    private val service: ConfigService?
    private val flagOverrides: FlagOverrides?
    private val evaluator: Evaluator
    private val logLevel: LogLevel
    private val logger: InternalLogger
    private val defaultUser: AtomicRef<ConfigCatUser?> = atomic(null)
    private val isClosed = atomic(false)

    override val hooks: Hooks

    init {
        options.sdkKey = sdkKey
        logger = InternalLogger(options.logger, options.logLevel, options.hooks)
        logLevel = options.logLevel
        hooks = options.hooks
        defaultUser.value = options.defaultUser
        flagOverrides = options.flagOverrides?.let { FlagOverrides().apply(it) }
        service =
            if (flagOverrides != null && flagOverrides.behavior == OverrideBehavior.LOCAL_ONLY) {
                hooks.invokeOnClientReady(ClientCacheState.HAS_LOCAL_OVERRIDE_FLAG_DATA_ONLY)
                null
            } else {
                ConfigService(options, ConfigFetcher(options, logger), logger, options.hooks)
            }
        evaluator = Evaluator(logger)
    }

    internal suspend fun getValueImpl(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): Any? {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val settingResult = getSettings()
        val evalUser = user ?: defaultUser.value
        val checkSettingAvailable = checkSettingAvailable(settingResult, key, defaultValue)
        val setting = checkSettingAvailable.second
        if (setting == null) {
            val details = EvaluationDetails.makeError(key, defaultValue, checkSettingAvailable.first, evalUser)
            hooks.invokeOnFlagEvaluated(details)
            return defaultValue
        }
        return try {
            if (!allowAnyReturnType) {
                validateValueType(setting.type, defaultValue)
            }
            evaluate(setting, key, evalUser, settingResult.fetchTime, settingResult.settings).value
        } catch (exception: Exception) {
            val errorMessage =
                ConfigCatLogMessages.getSettingEvaluationErrorWithDefaultValue(
                    "getAnyValue",
                    key,
                    "defaultValue",
                    defaultValue ?: "null",
                )
            logger.error(1002, errorMessage, exception)
            hooks.invokeOnFlagEvaluated(EvaluationDetails.makeError(key, defaultValue, errorMessage, evalUser))
            defaultValue
        }
    }

    override suspend fun getAnyValue(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): Any? {
        return getValueImpl(key, defaultValue, user, allowAnyReturnType = true)
    }

    internal suspend fun getValueDetailsImpl(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): EvaluationDetails {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val settingResult = getSettings()
        val evalUser = user ?: defaultUser.value

        val checkSettingAvailable = checkSettingAvailable(settingResult, key, defaultValue)
        val setting = checkSettingAvailable.second
        if (setting == null) {
            val details = EvaluationDetails.makeError(key, defaultValue, checkSettingAvailable.first, evalUser)
            hooks.invokeOnFlagEvaluated(details)
            return details
        }
        return try {
            if (!allowAnyReturnType) {
                validateValueType(setting.type, defaultValue)
            }
            evaluate(setting, key, evalUser, settingResult.fetchTime, settingResult.settings)
        } catch (exception: Exception) {
            val errorMessage =
                ConfigCatLogMessages.getSettingEvaluationErrorWithDefaultValue(
                    "getAnyValueDetails",
                    key,
                    "defaultValue",
                    defaultValue ?: "null",
                )
            logger.error(1002, errorMessage, exception)
            val errorDetails = EvaluationDetails.makeError(key, defaultValue, exception.message ?: "", evalUser)
            hooks.invokeOnFlagEvaluated(errorDetails)
            errorDetails
        }
    }

    override suspend fun getAnyValueDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): EvaluationDetails {
        return getValueDetailsImpl(key, defaultValue, user, allowAnyReturnType = true)
    }

    override suspend fun getAllValueDetails(user: ConfigCatUser?): Collection<EvaluationDetails> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty list")) {
            return emptyList()
        }
        return try {
            settingResult.settings.map {
                evaluate(it.value, it.key, user ?: defaultUser.value, settingResult.fetchTime, settingResult.settings)
            }
        } catch (exception: Exception) {
            val errorMessage =
                ConfigCatLogMessages.getSettingEvaluationErrorWithEmptyValue("getAllValueDetails", "empty list")
            logger.error(1002, errorMessage, exception)
            emptyList()
        }
    }

    override suspend fun getKeyAndValue(variationId: String): Pair<String, Any>? {
        require(variationId.isNotEmpty()) { "'variationId' cannot be empty." }

        try {
            val settingResult = getSettings()
            if (!checkSettingsAvailable(settingResult, "null")) {
                return null
            }
            val settings = settingResult.settings
            for (setting in settings) {
                if (setting.value.variationId == variationId) {
                    return Pair(
                        setting.key,
                        Helpers.validateSettingValueType(setting.value.settingValue, setting.value.type),
                    )
                }
                setting.value.targetingRules?.forEach { targetingRule ->
                    if (targetingRule.servedValue != null) {
                        if (targetingRule.servedValue.variationId == variationId) {
                            return Pair(
                                setting.key,
                                Helpers.validateSettingValueType(targetingRule.servedValue.value, setting.value.type),
                            )
                        }
                    } else if (!targetingRule.percentageOptions.isNullOrEmpty()) {
                        targetingRule.percentageOptions.forEach { percentageOption ->
                            if (percentageOption.variationId == variationId) {
                                return Pair(
                                    setting.key,
                                    Helpers.validateSettingValueType(percentageOption.value, setting.value.type),
                                )
                            }
                        }
                    } else {
                        error("Targeting rule THEN part is missing or invalid.")
                    }
                }
                setting.value.percentageOptions?.forEach { percentageOption ->
                    if (percentageOption.variationId == variationId) {
                        return Pair(
                            setting.key,
                            Helpers.validateSettingValueType(percentageOption.value, setting.value.type),
                        )
                    }
                }
            }
            this.logger.error(2011, ConfigCatLogMessages.getSettingForVariationIdIsNotPresent(variationId))
            return null
        } catch (exception: Exception) {
            val errorMessage = ConfigCatLogMessages.getSettingEvaluationErrorWithEmptyValue("getKeyAndValue", "null")
            logger.error(1002, errorMessage, exception)
            return null
        }
    }

    override suspend fun getAllKeys(): Collection<String> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty array")) {
            return emptyList()
        }
        return settingResult.settings.keys
    }

    override suspend fun getAllValues(user: ConfigCatUser?): Map<String, Any?> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty map")) {
            return emptyMap()
        }
        return try {
            return settingResult.settings.map {
                val evaluated =
                    evaluate(
                        it.value,
                        it.key,
                        user ?: defaultUser.value,
                        settingResult.fetchTime,
                        settingResult.settings,
                    )
                it.key to evaluated.value
            }.toMap()
        } catch (exception: Exception) {
            val errorMessage = ConfigCatLogMessages.getSettingEvaluationErrorWithEmptyValue("getAllValues", "empty map")
            logger.error(1002, errorMessage, exception)
            emptyMap()
        }
    }

    override suspend fun forceRefresh(): RefreshResult =
        service?.refresh() ?: RefreshResult(
            false,
            "The ConfigCat SDK is in local-only mode. Calling .forceRefresh() has no effect.",
        )

    override fun setOffline() {
        if (service == null || isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setOffline"),
            )
            return
        }
        service.offline()
    }

    override fun setOnline() {
        if (service == null || isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setOnline"),
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
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("setDefaultUser"),
            )
            return
        }
        defaultUser.value = user
    }

    override fun clearDefaultUser() {
        if (isClosed()) {
            this.logger.warning(
                3201,
                ConfigCatLogMessages.getConfigServiceMethodHasNoEffectDueToClosedClient("clearDefaultUser"),
            )
            return
        }
        defaultUser.value = null
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

    override suspend fun waitForReady(): CompletableDeferred<ClientCacheState> {
        val completableDeferred = CompletableDeferred<ClientCacheState>()
        hooks.addOnClientReady { clientCacheState -> completableDeferred.complete(clientCacheState) }
        return completableDeferred
    }

    private fun closeResources() {
        service?.close()
        hooks.clear()
    }

    private fun evaluate(
        setting: Setting,
        key: String,
        user: ConfigCatUser?,
        fetchTime: DateTime,
        settings: Map<String, Setting>,
    ): EvaluationDetails {
        var evaluateLogger: EvaluateLogger? = null
        if (logLevel == LogLevel.INFO) {
            evaluateLogger = EvaluateLogger()
        }
        val (value, variationId, targetingRule, percentageRule) =
            evaluator.evaluate(
                setting,
                key,
                user,
                settings,
                evaluateLogger,
            )
        val details =
            EvaluationDetails(
                key, variationId, user, false, null, Helpers.validateSettingValueType(value, setting.type),
                fetchTime.unixMillisLong, targetingRule, percentageRule,
            )
        hooks.invokeOnFlagEvaluated(details)
        return details
    }

    private suspend fun getSettings(): SettingResult {
        if (flagOverrides != null) {
            return when (flagOverrides.behavior) {
                OverrideBehavior.LOCAL_ONLY ->
                    SettingResult(
                        flagOverrides.dataSource.getOverrides(),
                        Constants.distantPast,
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

    private fun validateValueType(
        settingTypeInt: Int,
        defaultValue: Any?,
    ) {
        val settingType =
            settingTypeInt.toSettingTypeOrNull()
                ?: throw IllegalArgumentException(
                    "The setting type is not valid. Only String, Int, Double or Boolean types are supported.",
                )
        if (defaultValue == null) {
            return
        }
        if (!(
                (defaultValue is String && settingType == SettingType.STRING) ||
                    (defaultValue is Boolean && settingType == SettingType.BOOLEAN) ||
                    (
                        defaultValue is Int && (settingType == SettingType.INT || settingType == SettingType.JS_NUMBER)
                    ) ||
                    (
                        defaultValue is Double && (
                            settingType == SettingType.DOUBLE || settingType == SettingType.JS_NUMBER
                        )
                    )
            )
        ) {
            throw IllegalArgumentException(
                "The type of a setting must match the type of the specified default value. " +
                    "Setting's type was {" + settingType + "} but the default value's type was {" +
                    defaultValue::class.toString() + "}. Please use a default value which corresponds to the setting " +
                    "type {" + settingType + "}. Learn more: " +
                    "https://configcat.com/docs/sdk-reference/kotlin/#setting-type-mapping",
            )
        }
    }

    private fun checkSettingsAvailable(
        settingResult: SettingResult,
        emptyResult: String,
    ): Boolean {
        if (settingResult.isEmpty()) {
            this.logger.error(1000, ConfigCatLogMessages.getConfigJsonIsNotPresentedWithEmptyResult(emptyResult))
            return false
        }
        return true
    }

    private fun <T> checkSettingAvailable(
        settingResult: SettingResult,
        key: String,
        defaultValue: T,
    ): Pair<String, Setting?> {
        if (settingResult.isEmpty()) {
            val errorMessage =
                ConfigCatLogMessages.getConfigJsonIsNotPresentedWithDefaultValue(key, "defaultValue", defaultValue)
            logger.error(1000, errorMessage)
            return Pair(errorMessage, null)
        }
        val setting = settingResult.settings[key]
        if (setting == null) {
            val errorMessage =
                ConfigCatLogMessages.getSettingEvaluationFailedDueToMissingKey(
                    key,
                    "defaultValue",
                    defaultValue,
                    settingResult.settings.keys,
                )
            logger.error(1001, errorMessage)
            return Pair(errorMessage, null)
        }
        return Pair("", setting)
    }

    companion object {
        private val instances = mutableMapOf<String, Client>()
        private val lock = reentrantLock()

        fun get(
            sdkKey: String,
            block: ConfigCatOptions.() -> Unit = {},
        ): Client {
            val options = ConfigCatOptions().apply(block)
            val flagOverrides = options.flagOverrides?.let { FlagOverrides().apply(it) }
            if (sdkKey.isEmpty()) {
                options.hooks.invokeOnClientReady(ClientCacheState.NO_FLAG_DATA)
                throw IllegalArgumentException("SDK Key cannot be empty.")
            }

            if (OverrideBehavior.LOCAL_ONLY != flagOverrides?.behavior &&
                !isValidKey(sdkKey, options.isBaseURLCustom())
            ) {
                options.hooks.invokeOnClientReady(ClientCacheState.NO_FLAG_DATA)
                throw IllegalArgumentException("SDK Key '$sdkKey' is invalid.")
            }

            lock.withLock {
                val instance = instances[sdkKey]
                if (instance != null) {
                    instance.logger.warning(3000, ConfigCatLogMessages.getClientIsAlreadyCreated(sdkKey))
                    return instance
                }
                val client = Client(sdkKey, options)
                instances[sdkKey] = client
                return client
            }
        }

        private fun isValidKey(
            sdkKey: String,
            isCustomBaseURL: Boolean,
        ): Boolean {
            // configcat-proxy/ rules
            if (isCustomBaseURL && sdkKey.length > Constants.SDK_KEY_PROXY_PREFIX.length &&
                sdkKey.startsWith(Constants.SDK_KEY_PROXY_PREFIX)
            ) {
                return true
            }
            val splitSDKKey = sdkKey.split("/").toTypedArray()
            // 22/22 rules
            return if (splitSDKKey.size == 2 && splitSDKKey[0].length == Constants.SDK_KEY_SECTION_LENGTH &&
                splitSDKKey[1].length == Constants.SDK_KEY_SECTION_LENGTH
            ) {
                true
                // configcat-sdk-1/22/22 rules
            } else {
                splitSDKKey.size == 3 && splitSDKKey[0] == Constants.SDK_KEY_PREFIX &&
                    splitSDKKey[1].length == Constants.SDK_KEY_SECTION_LENGTH &&
                    splitSDKKey[2].length == Constants.SDK_KEY_SECTION_LENGTH
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

    internal object SettingTypeHelper {
        fun Int.toSettingTypeOrNull(): SettingType? = SettingType.entries.firstOrNull { it.id == this }
    }
}
