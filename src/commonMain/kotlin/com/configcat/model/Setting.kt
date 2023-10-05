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
    var type: Int = 0,

    @SerialName(value = "a")
    val percentageAttribute: String? = "",

    @SerialName(value = "p")
    val percentageOptions: Array<PercentageOption>? = null,

    @SerialName(value = "r")
    val targetingRules: Array<TargetingRule>? = null,

    @SerialName(value = "v")
    val settingsValue: SettingsValue,

    @SerialName(value = "i")
    val variationId: String = ""
) {

    var configSalt: String = ""
    internal var segments: Array<Segment> = arrayOf()

    public constructor() : this(0, "", null, null, SettingsValue(), "")
}
