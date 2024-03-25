package com.configcat

import com.configcat.model.PercentageOption
import com.configcat.model.TargetingRule

/**
 * Additional information about flag evaluation.
 */
public open class EvaluationDetailsBase internal constructor(
    public val key: String,
    public val variationId: String?,
    public val user: ConfigCatUser?,
    public val isDefaultValue: Boolean,
    public val error: String?,
    public val fetchTimeUnixMilliseconds: Long,
    public val matchedTargetingRule: TargetingRule?,
    public val matchedPercentageOption: PercentageOption?
)

/**
 * Additional information about flag evaluation.
 */
public class TypedEvaluationDetails<T> public constructor(
    key: String,
    variationId: String?,
    user: ConfigCatUser?,
    isDefaultValue: Boolean,
    error: String?,
    public val value: T,
    fetchTimeUnixMilliseconds: Long,
    matchedTargetingRule: TargetingRule?,
    matchedPercentageOption: PercentageOption?
) : EvaluationDetailsBase(
    key,
    variationId,
    user,
    isDefaultValue,
    error,
    fetchTimeUnixMilliseconds,
    matchedTargetingRule,
    matchedPercentageOption
)

/**
 * Additional information about flag evaluation.
 */
public class EvaluationDetails internal constructor(
    key: String,
    variationId: String?,
    user: ConfigCatUser?,
    isDefaultValue: Boolean,
    error: String?,
    public val value: Any?,
    fetchTimeUnixMilliseconds: Long,
    matchedTargetingRule: TargetingRule?,
    matchedPercentageOption: PercentageOption?
) : EvaluationDetailsBase(
    key,
    variationId,
    user,
    isDefaultValue,
    error,
    fetchTimeUnixMilliseconds,
    matchedTargetingRule,
    matchedPercentageOption
) {
    internal companion object {
        internal fun makeError(key: String, defaultValue: Any?, error: String, user: ConfigCatUser?):
            EvaluationDetails = EvaluationDetails(
            key, "", user, true, error,
            defaultValue, Constants.distantPast.unixMillisLong, null, null
        )
    }
}
