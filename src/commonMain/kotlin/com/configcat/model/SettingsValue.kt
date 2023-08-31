package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SettingsValue(
    @SerialName("b")
    var booleanValue: Boolean? = null,

    @SerialName("s")
    var stringValue: String? = null,

    @SerialName("i")
    var integerValue: Int? = null,

    @SerialName("d")
    var doubleValue: Double? = null
) {
    // No implementation
}
