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
    val percentageItems: List<RolloutPercentageItem> = listOf(),

    /** Collection of targeting rules that belongs to the feature flag / setting. */
    @SerialName("r")
    val rolloutRules: List<RolloutRule> = listOf(),

    /** Variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null,
)

/** Describes a percentage rule. */
@Serializable
public data class RolloutPercentageItem(
    /** Value served when the rule is selected during evaluation. */
    @Contextual
    @SerialName("v")
    val value: Any,

    /** The rule's percentage value. */
    @SerialName("p")
    val percentage: Double,

    /** The rule's variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null,
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
     * 0  -> 'IS ONE OF',
     * 1  -> 'IS NOT ONE OF',
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
     * 17 -> 'IS NOT ONE OF (Sensitive)'
     */
    @SerialName("t")
    val comparator: Int,

    /** The comparison value compared to the given user attribute. */
    @SerialName("c")
    val comparisonValue: String,

    /** The rule's variation ID (for analytical purposes). */
    @SerialName("i")
    val variationId: String? = null,
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
