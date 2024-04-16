package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Percentage option.
 */
@Serializable
public data class PercentageOption(
    /**
     * A number between 0 and 100 that represents a randomly allocated fraction of the users.
     */
    @SerialName(value = "p")
    val percentage: Int = 0,
    /**
     * The server value of the percentage option.
     */
    @SerialName(value = "v")
    val value: SettingValue,
    /**
     * The variation ID of the percentage option.
     */
    @SerialName(value = "i")
    val variationId: String? = null,
) {
    // No implementation
}
