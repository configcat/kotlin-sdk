package com.configcat.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

@Serializable
internal data class Config(
    @SerialName("p")
    val preferences: Preferences?,
    @SerialName("f")
    var settings: Map<String, Setting>? = null,
    @SerialName("s")
    var segments: Array<Segment>? = null
) {
    internal fun isEmpty(): Boolean = this == empty

    internal companion object {
        val empty: Config = Config(null, mapOf(), arrayOf())
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
