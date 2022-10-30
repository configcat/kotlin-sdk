package com.configcat

import com.soywiz.klock.DateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val version: String = "1.0.2"
    const val configFileName: String = "config_v5"
    const val globalCdnUrl = "https://cdn-global.configcat.com"
    const val euCdnUrl = "https://cdn-eu.configcat.com"
    val distantPast = DateTime.fromUnix(0)
    val distantFuture = DateTime.now().add(10_000, 0.0)
    val json = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(Any::class, FlagValueSerializer)
        }
    }
}
