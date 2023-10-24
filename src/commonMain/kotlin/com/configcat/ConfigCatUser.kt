package com.configcat

import kotlinx.serialization.encodeToString

/**
 * An object containing attributes to properly identify a given user for variation evaluation.
 * Its only mandatory attribute is the [identifier].
 */
public class ConfigCatUser(
    public val identifier: String,
    email: String? = null,
    country: String? = null,
    custom: Map<String, String>? = null
) {
    private val attributes: Map<String, String>

    init {
        val attr = mutableMapOf("Identifier" to identifier)
        if (!email.isNullOrEmpty()) {
            attr["Email"] = email
        }
        if (!country.isNullOrEmpty()) {
            attr["Country"] = country
        }
        if (custom != null) {
            for (item in custom) {
                attr[item.key] = item.value
            }
        }
        attributes = attr
    }

    internal fun attributeFor(key: String): String? {
        if (key.isEmpty()) {
            return null
        }
        return attributes[key]
    }

    override fun toString(): String {
        return "{${attributes.map { "\"${it.key}\":\"${it.value}\"" }.joinToString(",")}}"
    }

    public companion object {
        public fun attributeValueFrom(number: Double): String {
            return number.toString()
        }

        public fun attributeValueFrom(number: Int): String {
            return number.toString()
        }

        public fun attributeValueFrom(items: Array<String>): String {
            return Constants.json.encodeToString(items)
        }
    }
}
