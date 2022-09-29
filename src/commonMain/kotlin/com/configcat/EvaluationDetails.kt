package com.configcat

import com.soywiz.klock.DateTime

/**
 * Additional information about flag evaluation.
 */
public data class EvaluationDetails<T>(
    public val key: String,
    public val variationId: String,
    public val user: ConfigCatUser?,
    public val isDefaultValue: Boolean,
    public val error: String?,
    public val value: T,
    public val fetchTime: DateTime,
    public val matchedEvaluationRule: RolloutRule?,
    public val matchedEvaluationPercentageRule: PercentageRule?,
) {
    internal companion object {
        internal fun <T> makeError(key: String, defaultValue: T, error: String): EvaluationDetails<T> =
            EvaluationDetails(key, "", null, true, error, defaultValue, Constants.distantPast, null, null)
    }
}