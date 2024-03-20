package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Setting Value contains the proper value based on type.
 */
@Serializable
public data class SettingValue(
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

        other as SettingValue

        if (booleanValue != null && booleanValue == other.booleanValue) return true
        if (stringValue != null && stringValue == other.stringValue) return true
        if (integerValue != null && integerValue == other.integerValue) return true
        if (doubleValue != null && doubleValue == other.doubleValue) return true

        return false
    }

    override fun hashCode(): Int {
        var result = booleanValue?.hashCode() ?: 0
        result = 31 * result + (stringValue?.hashCode() ?: 0)
        result = 31 * result + (integerValue ?: 0)
        result = 31 * result + (doubleValue?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return if (booleanValue != null) {
            booleanValue.toString()
        } else if (integerValue != null) {
            integerValue.toString()
        } else if (doubleValue != null) {
            doubleValue.toString()
        } else {
            stringValue.toString()
        }
    }
}
