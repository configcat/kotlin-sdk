package com.configcat

import com.configcat.model.PercentageOption
import com.configcat.model.TargetingRule
import kotlin.time.Instant

/**
 * Specifies the possible evaluation error codes.
 */
public enum class EvaluationErrorCode(
    public val code: Int,
) {
    /** An unexpected error occurred during the evaluation. */
    UNEXPECTED_ERROR(-1),

    /** No error occurred (the evaluation was successful). */
    NONE(0),

    /**
     * The evaluation failed because of an error in the config model.
     * (Most likely, invalid data was passed to the SDK via flag overrides.)
     */
    INVALID_CONFIG_MODEL(1),

    /**
     * The evaluation failed because of a type mismatch between the evaluated
     * setting value and the specified default value.
     */
    SETTING_VALUE_TYPE_MISMATCH(2),

    /** The evaluation failed because the config JSON was not available locally. */
    CONFIG_JSON_NOT_AVAILABLE(1000),

    /**
     * The evaluation failed because the key of the evaluated setting was not found in
     * the config JSON.
     */
    SETTING_KEY_MISSING(1001),
}

/**
 * Additional information about flag evaluation.
 */
public open class EvaluationDetailsBase internal constructor(
    public val key: String,
    public val variationId: String?,
    public val user: ConfigCatUser?,
    public val isDefaultValue: Boolean,
    public val error: String?,
    public val errorCode: EvaluationErrorCode,
    public val errorException: Exception?,
    public val fetchTimeUnixMilliseconds: Long,
    public val matchedTargetingRule: TargetingRule?,
    public val matchedPercentageOption: PercentageOption?,
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
    errorCode: EvaluationErrorCode,
    exception: Exception?,
    public val value: T,
    fetchTimeUnixMilliseconds: Long,
    matchedTargetingRule: TargetingRule?,
    matchedPercentageOption: PercentageOption?,
) : EvaluationDetailsBase(
        key,
        variationId,
        user,
        isDefaultValue,
        error,
        errorCode,
        exception,
        fetchTimeUnixMilliseconds,
        matchedTargetingRule,
        matchedPercentageOption,
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
    errorCode: EvaluationErrorCode,
    exception: Exception?,
    public val value: Any?,
    fetchTimeUnixMilliseconds: Long,
    matchedTargetingRule: TargetingRule?,
    matchedPercentageOption: PercentageOption?,
) : EvaluationDetailsBase(
        key,
        variationId,
        user,
        isDefaultValue,
        error,
        errorCode,
        exception,
        fetchTimeUnixMilliseconds,
        matchedTargetingRule,
        matchedPercentageOption,
    ) {
    internal companion object {
        internal fun makeError(
            key: String,
            defaultValue: Any?,
            error: String,
            errorCode: EvaluationErrorCode,
            exception: Exception?,
            user: ConfigCatUser?,
        ): EvaluationDetails =
            EvaluationDetails(
                key,
                "",
                user,
                true,
                error,
                errorCode,
                exception,
                defaultValue,
                Instant.DISTANT_PAST.toEpochMilliseconds(),
                null,
                null,
            )
    }
}
