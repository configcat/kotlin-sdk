package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Describes a feature flag / setting. */
@Serializable
public data class Setting(
    /**
     * Type of the feature flag / setting.
     *
     * 0 -> [Boolean],
     * 1 -> [String],
     * 2 -> [Int],
     * 3 -> [Double],
     */
    @SerialName(value = "t")
    var type: Int = -1,
    /**
     * The User Object attribute which serves as the basis of percentage options evaluation.
     */
    @SerialName(value = "a")
    val percentageAttribute: String? = null,
    /**
     * The list of percentage options.
     */
    @SerialName(value = "p")
    val percentageOptions: Array<PercentageOption>? = null,
    /**
     * The list of targeting rules (where there is a logical OR relation between the items).
     */
    @SerialName(value = "r")
    val targetingRules: Array<TargetingRule>? = null,
    /**
     * The value of the setting.
     */
    @SerialName(value = "v")
    val settingValue: SettingValue,
    /**
     * The variation ID of the setting.
     */
    @SerialName(value = "i")
    val variationId: String? = null,
) {
    var configSalt: String? = null
    internal var segments: Array<Segment> = arrayOf()

    public constructor() : this(0, "", null, null, SettingValue(), "")

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Setting) return false

        if (type != other.type) return false
        if (percentageAttribute != other.percentageAttribute) return false
        if (!percentageOptions.contentEquals(other.percentageOptions)) return false
        if (!targetingRules.contentEquals(other.targetingRules)) return false
        if (settingValue != other.settingValue) return false
        if (variationId != other.variationId) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type
        result = 31 * result + (percentageAttribute?.hashCode() ?: 0)
        result = 31 * result + percentageOptions.contentHashCode()
        result = 31 * result + targetingRules.contentHashCode()
        result = 31 * result + settingValue.hashCode()
        result = 31 * result + (variationId?.hashCode() ?: 0)
        return result
    }
}
