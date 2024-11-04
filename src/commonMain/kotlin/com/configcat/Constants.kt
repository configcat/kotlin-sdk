package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.model.Config
import com.configcat.model.SettingType
import com.configcat.model.SettingValue
import korlibs.time.DateTime
import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.modules.SerializersModule

internal interface Closeable {
    fun close()
}

internal object Constants {
    const val VERSION: String = "4.0.0"
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
            serializersModule =
                SerializersModule {
                    contextual(Any::class, FlagValueSerializer)
                }
        }

    internal object FlagValueSerializer : KSerializer<Any> {
        override fun deserialize(decoder: Decoder): Any {
            val json =
                decoder as? JsonDecoder
                    ?: error("Only JsonDecoder is supported.")
            val element = json.decodeJsonElement()
            val primitive = element as? JsonPrimitive ?: error("Unable to decode $element")
            return when (primitive.content) {
                "true", "false" -> primitive.content == "true"
                else -> primitive.content.toIntOrNull() ?: primitive.content.toDoubleOrNull() ?: primitive.content
            }
        }

        override fun serialize(
            encoder: Encoder,
            value: Any,
        ) {
            val json =
                encoder as? JsonEncoder
                    ?: error("Only JsonEncoder is supported.")
            val element: JsonElement =
                when (value) {
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
                    throw IllegalArgumentException(
                        "Setting is of an unsupported type ($settingTypeEnum).",
                    )
                }
            }
        require(result != null) {
            "Setting value is not of the expected type ${settingTypeEnum.value}."
        }
        return result
    }
}
