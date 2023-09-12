package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Condition(
    @SerialName(value = "t")
    val userCondition: UserCondition? = null,
    @SerialName(value = "s")
    val segmentCondition: SegmentCondition? = null,
    @SerialName(value = "d")
    val prerequisiteFlagCondition: PrerequisiteFlagCondition? = null
) {
    // No implementation
}
