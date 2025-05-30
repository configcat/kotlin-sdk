package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.RefreshErrorCode
import com.configcat.fetch.RefreshResult
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.DefaultLogger
import com.configcat.log.InternalLogger
import com.configcat.log.LogLevel
import com.configcat.log.Logger
import com.configcat.model.SettingType
import com.configcat.override.FlagOverrides
import com.configcat.override.OverrideBehavior
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ProxyConfig
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
     * Updates the internally cached config by synchronizing with the external cache (if any),
     * then by fetching the latest version from the ConfigCat CDN (provided that the client is online).
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
     * Waits for the client to reach the ready state, i.e. to complete initialization.
     *
     * Ready state is reached as soon as the initial sync with the external cache (if any) completes.
     * If this does not produce up-to-date config data, and the client is online (i.e. HTTP requests are allowed),
     * the first config fetch operation is also awaited in Auto Polling mode before ready state is reported.
     *
     * That is, reaching the ready state usually means the client is ready to evaluate feature flags and settings.
     * However, please note that this is not guaranteed. In case of initialization failure or timeout,
     * the internal cache may be empty or expired even after the ready state is reported. You can verify this by
     * checking the return value.
     *
     * @return the state of the internal cache at the time the initialization was completed.
     */
    public suspend fun waitForReady(): ClientCacheState

    /**
     * Captures the SDK's internally cached config data.
     * It does not attempt to update it by synchronizing with the external cache or by fetching
     * the latest version from the ConfigCat CDN.
     *
     * Therefore, it is recommended to use snapshots in conjunction with the Auto Polling mode,
     * where the SDK automatically updates the internal cache in the background.
     *
     * For other polling modes, you will need to manually initiate a cache
     * update by invoking [ConfigCatClient.forceRefresh].
     *
     * @return the captured snapshot.
     */
    public fun snapshot(): ConfigCatClientSnapshot

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
 *
 * This method accepts an optional functional parameter to configure the constructed [ConfigCatClient].
 *
 * ```
 * val client = ConfigCatClient("YOUR-SDK-KEY") {
 *     pollingMode = autoPoll()
 * }
 * ```
 */
public fun ConfigCatClient(
    sdkKey: String,
    block: ConfigCatOptions.() -> Unit = {},
): ConfigCatClient = Client.get(sdkKey, ConfigCatOptions().apply(block))

/**
 * Creates a new or gets an already existing [ConfigCatClient] for the given [sdkKey].
 *
 * This method accepts an optional [ConfigCatOptions] parameter to configure the constructed [ConfigCatClient].
 *
 * ```
 * val options = ConfigCatOptions()
 * options.pollingMode = autoPoll()
 *
 * val client = ConfigCatClient("YOUR-SDK-KEY", options)
 * ```
 */
public fun ConfigCatClient(
    sdkKey: String,
    options: ConfigCatOptions,
): ConfigCatClient = Client.get(sdkKey, options)

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
    return client?.eval(key, defaultValue, user, allowAnyReturnType = false)
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
        details.errorCode,
        details.errorException,
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
    return client?.evalDetails(key, defaultValue, user, allowAnyReturnType = false)
        ?: configCatClient.getAnyValueDetails(key, defaultValue, user)
}

internal class Client private constructor(
    private val sdkKey: String,
    options: ConfigCatOptions,
) : ConfigCatClient, Closeable {
    private val service: ConfigService?
    private val flagOverrides: FlagOverrides?
    private val evaluator: Evaluator
    private val flagEvaluator: FlagEvaluator
    private val logger: InternalLogger
    private val defaultUser: AtomicRef<ConfigCatUser?> = atomic(null)
    private val isClosed = atomic(false)

    override val hooks: Hooks

    init {
        options.sdkKey = sdkKey
        logger = InternalLogger(options.logger, options.logLevel, options.hooks)
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
        flagEvaluator = FlagEvaluator(logger, evaluator, options.hooks)
    }

    internal suspend fun eval(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): Any? {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val settingResult = getSettings()
        val evalUser = user ?: defaultUser.value
        return flagEvaluator.findAndEvalFlag(
            settingResult,
            key,
            defaultValue,
            evalUser,
            "getValue",
            allowAnyReturnType,
        )
    }

    override suspend fun getAnyValue(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): Any? = eval(key, defaultValue, user, allowAnyReturnType = true)

    internal suspend fun evalDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): EvaluationDetails {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val settingResult = getSettings()
        val evalUser = user ?: defaultUser.value

        return flagEvaluator.findAndEvalFlagDetails(
            settingResult,
            key,
            defaultValue,
            evalUser,
            "getValueDetails",
            allowAnyReturnType,
        )
    }

    override suspend fun getAnyValueDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): EvaluationDetails = evalDetails(key, defaultValue, user, allowAnyReturnType = true)

    override suspend fun getAllValueDetails(user: ConfigCatUser?): Collection<EvaluationDetails> {
        val settingResult = getSettings()
        if (!checkSettingsAvailable(settingResult, "empty list")) {
            return emptyList()
        }
        return try {
            settingResult.settings.map {
                flagEvaluator.evalFlag(
                    it.value,
                    it.key,
                    user ?: defaultUser.value,
                    settingResult.fetchTime,
                    settingResult.settings,
                )
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
                    flagEvaluator.evalFlag(
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
            "Client is configured to use the LOCAL_ONLY override behavior, which prevents " +
                "synchronization with external cache and making HTTP requests.",
            RefreshErrorCode.LOCAL_ONLY_CLIENT,
            null,
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

    override suspend fun waitForReady(): ClientCacheState {
        val completableDeferred = CompletableDeferred<ClientCacheState>()
        hooks.addOnClientReady { clientCacheState -> completableDeferred.complete(clientCacheState) }
        return completableDeferred.await()
    }

    override fun snapshot(): ConfigCatClientSnapshot =
        Snapshot(
            flagEvaluator,
            getInMemorySettings(),
            defaultUser.value,
            logger,
        )

    private fun closeResources() {
        service?.close()
        hooks.clear()
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

    private fun getInMemorySettings(): SettingResult {
        if (flagOverrides != null) {
            return when (flagOverrides.behavior) {
                OverrideBehavior.LOCAL_ONLY ->
                    SettingResult(
                        flagOverrides.dataSource.getOverrides(),
                        Constants.distantPast,
                    )

                OverrideBehavior.LOCAL_OVER_REMOTE -> {
                    val result = service?.getInMemorySettings()
                    val remote = result?.settings ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    SettingResult(remote + local, result?.fetchTime ?: Constants.distantPast)
                }

                OverrideBehavior.REMOTE_OVER_LOCAL -> {
                    val result = service?.getInMemorySettings()
                    val remote = result?.settings ?: mapOf()
                    val local = flagOverrides.dataSource.getOverrides()
                    SettingResult(local + remote, result?.fetchTime ?: Constants.distantPast)
                }
            }
        }

        return service?.getInMemorySettings() ?: SettingResult(mapOf(), Constants.distantPast)
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

    companion object {
        private val instances = mutableMapOf<String, Client>()
        private val lock = reentrantLock()

        fun get(
            sdkKey: String,
            options: ConfigCatOptions,
        ): Client {
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
