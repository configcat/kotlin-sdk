package com.configcat

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual

/**
 * An object containing attributes to properly identify a given user for variation evaluation.
 * Its only mandatory attribute is the [identifier].
 *
 * Custom attributes of the user for advanced targeting rule definitions (e.g. user role, subscription type, etc.)
 *
 * The set of allowed attribute values depends on the comparison type of the condition which references the
 * User Object attribute.<br>
 * [String] values are supported by all comparison types (in some cases they need to be provided in a specific
 * format though).<br>
 * Some of the comparison types work with other types of values, as described below.
 *
 * Text-based comparisons (EQUALS, IS ONE OF, etc.)<br>
 *  * accept [String] values,
 *  * all other values are automatically converted to [String] (a warning will be logged but evaluation will continue
 *  as normal).
 *
 * SemVer-based comparisons (IS ONE OF, &lt;, &gt;=, etc.)<br>
 *  * accept [String] values containing a properly formatted, valid semver value,
 *  * all other values are considered invalid (a warning will be logged and the currently evaluated targeting rule
 *  will be skipped).
 *
 * Number-based comparisons (=, &lt;, &gt;=, etc.)<br>
 *  * accept [Double] values and all other numeric values which can safely be converted to [Double]
 *  * accept [String] values containing a properly formatted, valid [Double]  value
 *  * all other values are considered invalid (a warning will be logged and the currently evaluated targeting rule
 *  will be skipped).
 *
 * Date time-based comparisons (BEFORE / AFTER)<br>
 *  * accept [korlibs.time.DateTime] values, which are automatically converted to a second-based Unix timestamp
 *  * accept [Double] values representing a second-based Unix timestamp and all other numeric values which can safely
 *  be converted to {@link Double}
 *  * accept [String] values containing a properly formatted, valid [Double]  value
 *  * all other values are considered invalid (a warning will be logged and the currently evaluated targeting rule will
 *  be skipped).
 *
 * String array-based comparisons (ARRAY CONTAINS ANY OF / ARRAY NOT CONTAINS ANY OF)<br>
 *  * accept arrays of [String]
 *  * accept [List] of [String]
 *  * accept [String] values containing a valid JSON string which can be deserialized to an array of [String]
 *  * all other values are considered invalid (a warning will be logged and the currently evaluated targeting rule
 *  will be skipped).
 *
 * In case a non-string attribute value needs to be converted to [String] during evaluation, it will always be done
 * using the same format which is accepted by the comparisons.
 */
public class ConfigCatUser(
    public val identifier: String,
    email: String? = null,
    country: String? = null,
    custom: Map<String, Any>? = null,
) {
    private val attributes: Map<String, Any>

    init {
        val attr = mutableMapOf<String, Any>()
        attr["Identifier"] = identifier
        if (!email.isNullOrEmpty()) {
            attr["Email"] = email
        }
        if (!country.isNullOrEmpty()) {
            attr["Country"] = country
        }
        if (custom != null) {
            for (item in custom) {
                if (item.key != "Identifier" && item.key != "Email" && item.key != "Country") {
                    attr[item.key] = item.value
                }
            }
        }
        attributes = attr
    }

    internal fun attributeFor(key: String): Any? {
        if (key.isEmpty()) {
            return null
        }
        return attributes[key]
    }

    override fun toString(): String {
        return Constants.json.encodeToString(AnySerializer.module.serializer(), attributes)
    }

    private object AnySerializer : KSerializer<Any> {
        val module = SerializersModule {
            contextual(Any::class, AnySerializer)
        }

        override fun deserialize(decoder: Decoder): Any {
            if (decoder is JsonDecoder) {
                return toPrimitive(decoder.decodeJsonElement()) ?: ""
            } else {
                throw NotImplementedError("Only JsonDecoder is supported")
            }
        }

        override fun serialize(encoder: Encoder, value: Any) {
            if (encoder is JsonEncoder) {
                encoder.encodeJsonElement(toJsonElement(value))
            } else {
                throw NotImplementedError("Only JsonEncoder is supported")
            }
        }

        @OptIn(ExperimentalSerializationApi::class)
        override val descriptor: SerialDescriptor =
            ContextualSerializer(Any::class, null, emptyArray()).descriptor

        private fun toPrimitive(element: JsonElement): Any? = when (element) {
            is JsonNull -> null
            is JsonObject -> toPrimitiveMap(element)
            is JsonArray -> element.map { toPrimitive(it) }
            is JsonPrimitive -> {
                if (element.isString) {
                    element.contentOrNull
                } else {
                    element.booleanOrNull ?: element.longOrNull ?: element.doubleOrNull
                }
            }
            else -> null
        }

        private fun toPrimitiveMap(json: JsonObject): Map<String, Any> =
            json.map { (key, value) -> key to (toPrimitive(value) ?: "") }.toMap()

        private fun toJsonElement(value: Any): JsonElement = when (value) {
            is JsonElement -> value
            is Number -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Enum<*> -> JsonPrimitive(value.toString())
            is Array<*> -> JsonArray(value.map { toJsonElement(it ?: "") })
            is Iterable<*> -> JsonArray(value.map { toJsonElement(it ?: "") })
            is Map<*, *> -> JsonObject(value.map { (key, value) -> key as String to toJsonElement(value ?: "") }.toMap())
            else -> JsonPrimitive(value.toString())
        }
    }
}
