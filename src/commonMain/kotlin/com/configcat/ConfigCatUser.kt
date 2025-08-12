package com.configcat

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

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
 *  * accept [kotlin.time.Instant] values, which are automatically converted to a second-based Unix timestamp
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

    /**
     * Retrieves the attribute value associated with the given key.
     *
     * @param key The key of the attribute to retrieve. Should not be empty.
     * @return The value of the attribute associated with the given key, or null if the key is empty or does not exist.
     */
    public fun attributeFor(key: String): Any? {
        if (key.isEmpty()) {
            return null
        }
        return attributes[key]
    }

    override fun toString(): String {
        return Constants.json.encodeToString(toJsonElement(attributes))
    }

    private fun toJsonElement(value: Any): JsonElement =
        when (value) {
            is JsonElement -> value
            is Number -> JsonPrimitive(value)
            is String -> JsonPrimitive(value)
            is Boolean -> JsonPrimitive(value)
            is Enum<*> -> JsonPrimitive(value.toString())
            is Array<*> -> JsonArray(value.map { toJsonElement(it ?: "") })
            is Iterable<*> -> JsonArray(value.map { toJsonElement(it ?: "") })
            is Map<*, *> ->
                JsonObject(
                    value.map {
                            (key, value) ->
                        key as String to toJsonElement(value ?: "")
                    }.toMap(),
                )
            else -> JsonPrimitive(value.toString())
        }
}
