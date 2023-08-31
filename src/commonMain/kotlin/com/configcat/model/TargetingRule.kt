package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TargetingRule(
    @SerialName(value = "c")
    val conditions: Array<Condition>? = null,
    @SerialName(value = "p")
    val percentageOptions: Array<PercentageOption>? = null,
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
