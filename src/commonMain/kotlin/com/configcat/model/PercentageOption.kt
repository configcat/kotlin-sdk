package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PercentageOption(
    @SerialName(value = "p")
    val percentage: Int = 0,
    @SerialName(value = "v")
    val value: SettingsValue,
    @SerialName(value = "i")
    val variationId: String? = null
) {
    // No implementation
}
