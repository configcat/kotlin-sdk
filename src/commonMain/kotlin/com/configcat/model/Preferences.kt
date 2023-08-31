package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Preferences(
    @SerialName(value = "u")
    val baseUrl: String,

    @SerialName(value = "r")
    val redirect: Int = 0,

    @SerialName(value = "s")
    val salt: String
)
