package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Container class for different condition types.
 */
@Serializable
public data class Condition(
    @SerialName(value = "u")
    val userCondition: UserCondition? = null,
    @SerialName(value = "s")
    val segmentCondition: SegmentCondition? = null,
    @SerialName(value = "p")
    val prerequisiteFlagCondition: PrerequisiteFlagCondition? = null
) {
    // No implementation
}
