package com.configcat

import com.configcat.model.Config
import com.soywiz.klock.DateTime
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val version: String = "2.1.0"
    const val configFileName: String = "config_v6.json"
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

    internal object FlagValueSerializer : KSerializer<Any> {
        override fun deserialize(decoder: Decoder): Any {
            val json = decoder as? JsonDecoder
                ?: error("Only JsonDecoder is supported.")
            val element = json.decodeJsonElement()
            val primitive = element as? JsonPrimitive ?: error("Unable to decode $element")
            return when (primitive.content) {
                "true", "false" -> primitive.content == "true"
                else -> primitive.content.toIntOrNull() ?: primitive.content.toDoubleOrNull() ?: primitive.content
            }
        }

        override fun serialize(encoder: Encoder, value: Any) {
            val json = encoder as? JsonEncoder
                ?: error("Only JsonEncoder is supported.")
            val element: JsonElement = when (value) {
                is String -> JsonPrimitive(value)
                is Number -> JsonPrimitive(value)
                is Boolean -> JsonPrimitive(value)
                is JsonElement -> value
                else -> throw IllegalArgumentException("Unable to encode $value")
            }
            json.encodeJsonElement(element)
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor =
            ContextualSerializer(Any::class, null, emptyArray()).descriptor
    }
}

public fun parseConfigJson(jsonString: String): Pair<Config, String?> {
    val config: Config = Constants.json.decodeFromString(jsonString)
    addConfigSaltAndSegmentsToSettings(config)
    return Pair(config, null)
}

public fun addConfigSaltAndSegmentsToSettings(config: Config) {
    val configSalt = config.preferences?.salt
    config.settings?.values?.forEach {
        it.configSalt = configSalt
        it.segments = config.segments ?: arrayOf()
    }
}
