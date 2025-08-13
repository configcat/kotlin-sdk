package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.model.Config
import com.configcat.model.SettingType
import com.configcat.model.SettingValue
import kotlinx.serialization.json.Json

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val VERSION: String = "5.0.1"
    const val CONFIG_FILE_NAME: String = "config_v6.json"
    const val SERIALIZATION_FORMAT_VERSION: String = "v2"
    const val GLOBAL_CDN_URL = "https://cdn-global.configcat.com"
    const val EU_CDN_URL = "https://cdn-eu.configcat.com"
    const val SDK_KEY_PROXY_PREFIX = "configcat-proxy/"
    const val SDK_KEY_PREFIX = "configcat-sdk-1"
    const val SDK_KEY_SECTION_LENGTH = 22

    val json =
        Json {
            ignoreUnknownKeys = true
        }
}
