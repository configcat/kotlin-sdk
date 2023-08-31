package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PrerequisiteFlagCondition(
    @SerialName(value = "f")
    val prerequisiteFlagKey: String? = null,

    @SerialName(value = "c")
    val prerequisiteComparator: Int = 0,

    @SerialName(value = "v")
    val value: SettingsValue? = null
) {
    // No implementation
}
