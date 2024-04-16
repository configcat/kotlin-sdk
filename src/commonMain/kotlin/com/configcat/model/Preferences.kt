package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The config preferences.
 */
@Serializable
public data class Preferences(
    @SerialName(value = "u")
    val baseUrl: String,
    @SerialName(value = "r")
    val redirect: Int = 0,
    /**
     * The config salt which was used to hash sensitive data.
     */
    @SerialName(value = "s")
    val salt: String?,
)
