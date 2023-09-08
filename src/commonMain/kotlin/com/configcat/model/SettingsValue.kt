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
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SettingsValue

        if (booleanValue != other.booleanValue) return false
        if (stringValue != other.stringValue) return false
        if (integerValue != other.integerValue) return false
        if (doubleValue != other.doubleValue) return false

        return true
    }

    override fun hashCode(): Int {
        var result = booleanValue?.hashCode() ?: 0
        result = 31 * result + (stringValue?.hashCode() ?: 0)
        result = 31 * result + (integerValue ?: 0)
        result = 31 * result + (doubleValue?.hashCode() ?: 0)
        return result
    }
}
