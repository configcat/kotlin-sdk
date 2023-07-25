package com.configcat

import com.soywiz.klock.DateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val version: String = "2.0.0"
    const val configFileName: String = "config_v5.json"
    const val serializationFormatVersion: String = "v2"
    const val globalCdnUrl = "https://cdn-global.configcat.com"
    const val euCdnUrl = "https://cdn-eu.configcat.com"
    const val SDK_KEY_PROXY_PREFIX = "configcat-proxy/"
    const val SDK_KEY_PREFIX = "configcat-sdk-1"
    const val SDK_KEY_SECTION_LENGTH = 22


    val distantPast = DateTime.fromUnix(0)
    val distantFuture = DateTime.now().add(10_000, 0.0)
    val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(Any::class, FlagValueSerializer)
        }
    }
}
