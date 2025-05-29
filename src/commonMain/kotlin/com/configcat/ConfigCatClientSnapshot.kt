package com.configcat

import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger

/**
 * Represents the state of `ConfigCatClient` captured at a specific point in time.
 */
public interface ConfigCatClientSnapshot {
    /**
     * Gets the value of a feature flag or setting as [Any] identified by the given [key].
     *
     * @param key          the identifier of the feature flag or setting.
     * @param defaultValue in case of any failure, this value will be returned.
     * @param user         the user object.
     */
    public fun getAnyValue(
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
    public fun getAnyValueDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): EvaluationDetails

    /**
     * Gets a collection of all setting keys.
     */
    public fun getAllKeys(): Collection<String>
}

/**
 * Gets the value of a feature flag or setting as [T] identified by the given [key].
 *
 * @param key          the identifier of the feature flag or setting.
 * @param defaultValue in case of any failure, this value will be returned.
 * @param user         the user object.
 * @param T            the type of the desired feature flag or setting. Only the following types are allowed: [String],
 * [Boolean], [Int] and [Double] (both nullable and non-nullable).
 */
public inline fun <reified T> ConfigCatClientSnapshot.getValue(
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

    return getValueFromSnapshotInternal(this, key, defaultValue, user) as T
}

@PublishedApi
internal fun getValueFromSnapshotInternal(
    snapshot: ConfigCatClientSnapshot,
    key: String,
    defaultValue: Any?,
    user: ConfigCatUser?,
): Any? {
    val snap = snapshot as? Snapshot
    return snap?.eval(key, defaultValue, user, allowAnyReturnType = false)
        ?: snapshot.getAnyValue(key, defaultValue, user)
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
public inline fun <reified T> ConfigCatClientSnapshot.getValueDetails(
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

    val details = getValueDetailsFromSnapshotInternal(this, key, defaultValue, user)
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
internal fun getValueDetailsFromSnapshotInternal(
    snapshot: ConfigCatClientSnapshot,
    key: String,
    defaultValue: Any?,
    user: ConfigCatUser?,
): EvaluationDetails {
    val snap = snapshot as? Snapshot
    return snap?.evalDetails(key, defaultValue, user, allowAnyReturnType = false)
        ?: snapshot.getAnyValueDetails(key, defaultValue, user)
}

internal class Snapshot(
    private val flagEvaluator: FlagEvaluator,
    private val settingResult: SettingResult,
    private val defaultUser: ConfigCatUser?,
    private val logger: InternalLogger,
) : ConfigCatClientSnapshot {
    fun eval(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): Any? {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val evalUser = user ?: defaultUser
        return flagEvaluator.findAndEvalFlag(
            settingResult,
            key,
            defaultValue,
            evalUser,
            "Snapshot.getValue",
            allowAnyReturnType,
        )
    }

    override fun getAnyValue(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): Any? = eval(key, defaultValue, user, allowAnyReturnType = true)

    fun evalDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        allowAnyReturnType: Boolean,
    ): EvaluationDetails {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val evalUser = user ?: defaultUser
        return flagEvaluator.findAndEvalFlagDetails(
            settingResult,
            key,
            defaultValue,
            evalUser,
            "Snapshot.getValueDetails",
            allowAnyReturnType,
        )
    }

    override fun getAnyValueDetails(
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
    ): EvaluationDetails = evalDetails(key, defaultValue, user, allowAnyReturnType = true)

    override fun getAllKeys(): Collection<String> {
        if (settingResult.isEmpty()) {
            this.logger.error(1000, ConfigCatLogMessages.getConfigJsonIsNotPresentedWithEmptyResult("empty list"))
            return emptyList()
        }
        return settingResult.settings.keys
    }
}
