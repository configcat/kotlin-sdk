package com.configcat

/**
 * Additional information about flag evaluation.
 */
public abstract class EvaluationDetailsBase internal constructor(
    public val key: String,
    public val variationId: String?,
    public val user: ConfigCatUser?,
    public val isDefaultValue: Boolean,
    public val error: String?,
    public val fetchTimeUnixMilliseconds: Long,
    public val matchedEvaluationRule: RolloutRule?,
    public val matchedEvaluationPercentageRule: PercentageRule?,
)

public class TypedEvaluationDetails<T> internal constructor(
    key: String,
    variationId: String?,
    user: ConfigCatUser?,
    isDefaultValue: Boolean,
    error: String?,
    public val value: T,
    fetchTimeUnixMilliseconds: Long,
    matchedEvaluationRule: RolloutRule?,
    matchedEvaluationPercentageRule: PercentageRule?,
): EvaluationDetailsBase(key, variationId, user, isDefaultValue, error, fetchTimeUnixMilliseconds, matchedEvaluationRule, matchedEvaluationPercentageRule) {
    internal companion object {
        internal fun <T> makeError(key: String, defaultValue: T, error: String, user: ConfigCatUser?): TypedEvaluationDetails<T> =
            TypedEvaluationDetails(key, "", user, true, error, defaultValue, Constants.distantPast.unixMillisLong, null, null)
    }
}

public class EvaluationDetails internal constructor(
    key: String,
    variationId: String?,
    user: ConfigCatUser?,
    isDefaultValue: Boolean,
    error: String?,
    public val value: Any,
    fetchTimeUnixMilliseconds: Long,
    matchedEvaluationRule: RolloutRule?,
    matchedEvaluationPercentageRule: PercentageRule?,
): EvaluationDetailsBase(key, variationId, user, isDefaultValue, error, fetchTimeUnixMilliseconds, matchedEvaluationRule, matchedEvaluationPercentageRule) {
    internal companion object {
        internal fun makeError(key: String, defaultValue: Any, error: String, user: ConfigCatUser?): EvaluationDetails =
            EvaluationDetails(key, "", user, true, error, defaultValue, Constants.distantPast.unixMillisLong, null, null)
    }
}