package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Prerequisite Flag Condition.
 */
@Serializable
public data class PrerequisiteFlagCondition(
    /**
     * The key of the prerequisite flag that the condition is based on.
     */
    @SerialName(value = "f")
    val prerequisiteFlagKey: String? = null,
    /**
     * The operator which defines the relation between the evaluated value of the prerequisite flag and
     * the comparison value.
     */
    @SerialName(value = "c")
    val prerequisiteComparator: Int = -1,
    /**
     * The value that the evaluated value of the prerequisite flag is compared to.
     */
    @SerialName(value = "v")
    val value: SettingValue? = null,
) {
    // No implementation
}
