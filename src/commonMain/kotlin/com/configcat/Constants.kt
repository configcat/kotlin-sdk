package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.model.Config
import com.configcat.model.SettingType
import com.configcat.model.SettingValue
import korlibs.time.DateTime
import kotlinx.serialization.json.Json

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val VERSION: String = "4.2.0"
    const val CONFIG_FILE_NAME: String = "config_v6.json"
    const val SERIALIZATION_FORMAT_VERSION: String = "v2"
    const val GLOBAL_CDN_URL = "https://cdn-global.configcat.com"
    const val EU_CDN_URL = "https://cdn-eu.configcat.com"
    const val SDK_KEY_PROXY_PREFIX = "configcat-proxy/"
    const val SDK_KEY_PREFIX = "configcat-sdk-1"
    const val SDK_KEY_SECTION_LENGTH = 22

    val distantPast = DateTime.fromUnixMillis(0)
    val distantFuture = DateTime.now().add(10_000, 0.0)
    val json =
        Json {
            ignoreUnknownKeys = true
        }
}

internal object Helpers {
    fun parseConfigJson(jsonString: String): Config {
        val config: Config = Constants.json.decodeFromString(jsonString)
        addConfigSaltAndSegmentsToSettings(config)
        return config
    }

    fun addConfigSaltAndSegmentsToSettings(config: Config) {
        val configSalt = config.preferences?.salt
        config.settings?.values?.forEach {
            it.configSalt = configSalt
            it.segments = config.segments ?: arrayOf()
        }
    }

    fun validateSettingValueType(
        settingValue: SettingValue?,
        settingType: Int,
    ): Any {
        val settingTypeEnum = settingType.toSettingTypeOrNull()
        require(settingValue != null) { "Setting value is missing or invalid." }
        val result: Any?
        result =
            when (settingTypeEnum) {
                SettingType.BOOLEAN -> {
                    settingValue.booleanValue
                }

                SettingType.STRING -> {
                    settingValue.stringValue
                }

                SettingType.INT -> {
                    settingValue.integerValue
                }

                SettingType.DOUBLE -> {
                    settingValue.doubleValue
                }

                SettingType.JS_NUMBER -> {
                    settingValue.doubleValue
                }

                else -> {
                    throw InvalidConfigModelException(
                        "Setting is of an unsupported type ($settingTypeEnum).",
                    )
                }
            }
        if (result == null) {
            throw InvalidConfigModelException("Setting value is not of the expected type ${settingTypeEnum.value}.")
        }
        return result
    }
}
