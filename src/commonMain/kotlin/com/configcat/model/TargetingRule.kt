package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Targeting rule.
 */
@Serializable
public data class TargetingRule(
    /**
     * The list of conditions (where there is a logical AND relation between the items).
     */
    @SerialName(value = "c")
    val conditions: Array<Condition>? = null,
    /**
     * The list of percentage options associated with the targeting rule or {@code null} if the targeting rule
     * has a simple value THEN part.
     */
    @SerialName(value = "p")
    val percentageOptions: Array<PercentageOption>? = null,
    /**
     * The value associated with the targeting rule or {@code null} if the targeting rule has percentage options
     * THEN part.
     */
    @SerialName(value = "s")
    val servedValue: ServedValue? = null,
) {
    internal val conditionAccessors: List<ConditionAccessor> =
        conditions?.let { condition -> condition.map { it } } ?: listOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is TargetingRule) return false

        if (!conditions.contentEquals(other.conditions)) return false
        if (!percentageOptions.contentEquals(other.percentageOptions)) return false
        if (servedValue != other.servedValue) return false
        if (conditionAccessors != other.conditionAccessors) return false

        return true
    }

    override fun hashCode(): Int {
        var result = conditions?.contentHashCode() ?: 0
        result = 31 * result + (percentageOptions?.contentHashCode() ?: 0)
        result = 31 * result + (servedValue?.hashCode() ?: 0)
        result = 31 * result + conditionAccessors.hashCode()
        return result
    }


}

@Serializable
public data class ServedValue(
    @SerialName(value = "v")
    val value: SettingValue,
    @SerialName(value = "i")
    val variationId: String? = null,
) {
    // No implementation
}
