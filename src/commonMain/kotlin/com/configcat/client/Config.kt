package com.configcat.client

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
internal data class Config(
    @SerialName("p")
    val preferences: Preferences? = null,
    @SerialName("f")
    val settings: Map<String, Setting>,
) {
    internal fun isEmpty(): Boolean = this == empty

    internal companion object {
        val empty: Config = Config(null, mapOf())
    }
}

@Serializable
internal data class Preferences(
    @SerialName("u")
    val baseUrl: String,
    @SerialName("r")
    val redirect: Int,
)

@Serializable
public data class Setting(
    @Contextual
    @SerialName("v")
    val value: Any,
    @SerialName("t")
    val type: Int = 0,
    @SerialName("p")
    val percentageItems: List<RolloutPercentageItem> = listOf(),
    @SerialName("r")
    val rolloutRules: List<RolloutRule> = listOf(),
    @SerialName("i")
    val variationId: String? = null,
)

@Serializable
public data class RolloutPercentageItem(
    @Contextual
    @SerialName("v")
    val value: Any,
    @SerialName("p")
    val percentage: Double,
    @SerialName("i")
    val variationId: String? = null,
)

@Serializable
public data class RolloutRule(
    @Contextual
    @SerialName("v")
    val value: Any,
    @SerialName("a")
    val comparisonAttribute: String,
    @SerialName("t")
    val comparator: Int,
    @SerialName("c")
    val comparisonValue: String,
    @SerialName("i")
    val variationId: String? = null,
)

internal object FlagValueSerializer : KSerializer<Any> {
    override fun deserialize(decoder: Decoder): Any {
        val json = decoder as? JsonDecoder
            ?: throw IllegalStateException("Only JsonDecoder is supported.")
        val element = json.decodeJsonElement()
        val primitive = element as? JsonPrimitive ?: throw IllegalStateException("Unable to decode $element")
        return when (primitive.content) {
            "true", "false" -> primitive.content == "true"
            else -> primitive.content.toIntOrNull() ?: primitive.content.toDoubleOrNull() ?: primitive.content
        }
    }

    override fun serialize(encoder: Encoder, value: Any) {
        val json = encoder as? JsonEncoder
            ?: throw IllegalStateException("Only JsonEncoder is supported.")
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
