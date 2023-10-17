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
    /**
     * The User Object attribute which serves as the basis of percentage options evaluation.
     */
    @SerialName(value = "a")
    val percentageAttribute: String? = "",
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
    val settingsValue: SettingsValue,
    /**
     * The variation ID of the setting.
     */
    @SerialName(value = "i")
    val variationId: String = ""
) {

    var configSalt: String = ""
    internal var segments: Array<Segment> = arrayOf()

    public constructor() : this(0, "", null, null, SettingsValue(), "")
}
