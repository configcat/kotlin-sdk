package com.configcat.client

import com.soywiz.klock.DateTime
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal object Constants {
    const val version: String = "0.1.0"
    const val configFileName: String = "config_v5"
    const val globalCdnUrl = "https://cdn-global.configcat.com"
    const val euCdnUrl = "https://cdn-eu.configcat.com"
    val minDate = DateTime.fromUnix(0)
}

internal fun String.parseConfigJson(): Pair<Config, Throwable?> {
    return try {
        val json = Json {
            ignoreUnknownKeys = true
            serializersModule = SerializersModule {
                contextual(Any::class, FlagValueSerializer)
            }
        }
        Pair(json.decodeFromString(this), null)
    } catch (e: Exception) {
        Pair(Config.empty, e)
    }
}

internal fun Int.toLongMillis(): Long = this.toLong() * 1000
internal fun Int.toDoubleMillis(): Double = this.toDouble() * 1000
