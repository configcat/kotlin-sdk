package com.configcat

import com.soywiz.klock.DateTime
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

internal data class Entry(
    val config: Config,
    val eTag: String,
    val configJson: String,
    val fetchTime: DateTime
) {
    fun isEmpty(): Boolean = this === empty

    companion object {
        val empty: Entry = Entry(Config.empty, "", "", Constants.distantPast)

        fun fromString(cacheValue: String?): Entry {
            if (cacheValue.isNullOrEmpty()) {
                return empty
            }
            val fetchTimeIndex = cacheValue.indexOf("\n")
            val eTagIndex = cacheValue.indexOf("\n", fetchTimeIndex + 1)
            require(fetchTimeIndex > 0 && eTagIndex > 0) { "Number of values is fewer than expected." }
            val fetchTimeRaw = cacheValue.substring(0, fetchTimeIndex)
            require(DateTimeUtils.isValidDate(fetchTimeRaw)) { "Invalid fetch time: $fetchTimeRaw" }
            val fetchTimeUnixMillis = fetchTimeRaw.toLong()
            val eTag = cacheValue.substring(fetchTimeIndex + 1, eTagIndex)
            require(eTag.isNotEmpty()) { "Empty eTag value." }
            val configJson = cacheValue.substring(eTagIndex + 1)
            require(configJson.isNotEmpty()) { "Empty config jsom value." }
            return try {
                val config: Config = Constants.json.decodeFromString(configJson)
                Entry(config, eTag, configJson, DateTime(fetchTimeUnixMillis))
            } catch (e: Exception) {
                throw IllegalArgumentException("Invalid config JSON content: $configJson", e)
            }
        }
    }

    fun serialize(): String {
        return "${fetchTime.unixMillis.toLong()}\n${eTag}\n$configJson"
    }
}

@Serializable
internal data class Config(
    @SerialName("p")
    val preferences: Preferences? = null,
    @SerialName("f")
    val settings: Map<String, Setting>
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
    val redirect: Int
)

/** Describes a feature flag / setting. */
@Serializable
public data class Setting(
    /** Value of the feature flag / setting. */
    @Contextual
    @SerialName("v")
    val value: Any,

    /**
     * Type of the feature flag / setting.
     *
     * 0 -> [Boolean],
     * 1 -> [String],
     * 2 -> [Int],
     * 3 -> [Double],
     */
    @SerialName("t")
    val type: Int = 0,

    /** Collection of percentage rules that belongs to the feature flag / setting. */
    @SerialName("p")
    val percentageItems: List<PercentageRule> = listOf(),

    /** Collection of targeting rules that belongs to the feature flag / setting. */
    @SerialName("r")
    val rolloutRules: List<RolloutRule> = listOf(),

    /** Variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null
)

/** Describes a percentage rule. */
@Serializable
public data class PercentageRule(
    /** Value served when the rule is selected during evaluation. */
    @Contextual
    @SerialName("v")
    val value: Any,

    /** The rule's percentage value. */
    @SerialName("p")
    val percentage: Double,

    /** The rule's variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null
)

/** Describes a targeting rule. */
@Serializable
public data class RolloutRule(
    /** Value served when the rule is selected during evaluation. */
    @Contextual
    @SerialName("v")
    val value: Any,

    /** The user attribute used in the comparison during evaluation. */
    @SerialName("a")
    val comparisonAttribute: String,

    /**
     * The operator used in the comparison.
     *
     * 2  -> 'CONTAINS',
     * 3  -> 'DOES NOT CONTAIN',
     * 4  -> 'IS ONE OF (SemVer)',
     * 5  -> 'IS NOT ONE OF (SemVer)',
     * 6  -> '< (SemVer)',
     * 7  -> '<= (SemVer)',
     * 8  -> '> (SemVer)',
     * 9  -> '>= (SemVer)',
     * 10 -> '= (Number)',
     * 11 -> '<> (Number)',
     * 12 -> '< (Number)',
     * 13 -> '<= (Number)',
     * 14 -> '> (Number)',
     * 15 -> '>= (Number)',
     * 16 -> 'IS ONE OF (Sensitive)',
     * 17 -> 'IS NOT ONE OF (Sensitive)',
     * 18 -> 'BEFORE (UTC DateTime)',
     * 19 -> 'AFTER (UTC DateTime)',
     * 20 -> 'EQUALS (hashed)',
     * 21 -> 'NOT EQUALS (hashed)',
     * 22 -> 'STARTS WITH ANY OF (hashed)',
     * 23 -> 'ENDS WITH ANY OF (hashed)',
     * 24 -> 'ARRAY CONTAINS (hashed)',
     * 25 -> 'ARRAY NOT CONTAINS (hashed)'
     */
    @SerialName("t")
    val comparator: Int,

    /** The comparison value compared to the given user attribute. */
    @SerialName("c")
    val comparisonValue: String,

    /** The rule's variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null
)

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
