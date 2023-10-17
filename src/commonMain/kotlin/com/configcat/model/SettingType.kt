package com.configcat.model

/**
 * Describes the type of ConfigCat Feature Flag / Setting.
 */
public enum class SettingType(public val id: Int, public val value: String) {
    BOOLEAN(0, "Boolean"),
    STRING(1, "String"),
    INT(2, "Int"),
    DOUBLE(3, "Double")
}
