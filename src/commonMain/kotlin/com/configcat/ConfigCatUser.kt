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

}

public object ConfigCatUserHelper{
    //    public fun attributeValueFrom(date: java.util.Date?): String? {
//        if (date == null) {
//            throw java.lang.IllegalArgumentException("Invalid 'date' parameter.")
//        }
//        val unixSeconds: Double = DateTimeUtils.getUnixSeconds(date)
//        val decimalFormat: java.text.DecimalFormat = Utils.getDecimalFormat()
//        return decimalFormat.format(unixSeconds)
//    }

     public fun attributeValueFrom(number: Double): String {
        return number.toString()
    }

     public fun attributeValueFrom(number: Int): String {
        return number.toString()
    }

     public fun attributeValueFrom(items: Array<String>): String {
        if (items == null) {
            throw IllegalArgumentException("Invalid 'items' parameter.")
        }
        return Constants.json.encodeToString(items)
    }
}


