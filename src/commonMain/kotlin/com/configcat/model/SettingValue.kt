package com.configcat.model

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
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
    public fun equalsBasedOnSettingType(other: Any?, settingType: Int): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SettingValue
        val settingTypeEnum = settingType.toSettingTypeOrNull()
        return when (settingTypeEnum) {
            SettingType.BOOLEAN -> {
                booleanValue == other.booleanValue
            }

            SettingType.STRING -> {
                stringValue == other.stringValue
            }

            SettingType.INT -> {
                integerValue == other.integerValue
            }

            SettingType.DOUBLE -> {
                doubleValue == other.doubleValue
            }

            SettingType.JS_NUMBER -> {
                doubleValue == other.doubleValue || integerValue == other.integerValue
            }

            else -> {
                throw IllegalArgumentException(
                    "Setting is of an unsupported type ($settingTypeEnum)."
                )
            }
        }
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
