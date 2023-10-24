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
    val servedValue: ServedValue? = null
) {
    // No implementation
}

@Serializable
public data class ServedValue(
    @SerialName(value = "v")
    val value: SettingsValue,
    @SerialName(value = "i")
    val variationId: String = ""
) {
    // No implementation
}
