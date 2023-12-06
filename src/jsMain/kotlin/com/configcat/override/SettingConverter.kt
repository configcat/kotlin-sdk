package com.configcat.override

import com.configcat.model.Setting

internal actual fun convertToSetting(value: Any): Setting {
    val setting = Setting()
    when (value) {
        is Boolean -> {
            setting.settingsValue.booleanValue = value
            setting.type = 0
        }

        is Double -> {
            // is Double return true for Int as well in JS platform
            setting.settingsValue.doubleValue = value
            setting.settingsValue.integerValue = value.toInt()
            setting.type = -1
        }

        else -> {
            setting.settingsValue.stringValue = value.toString()
            setting.type = 1
        }
    }
    return setting
}
