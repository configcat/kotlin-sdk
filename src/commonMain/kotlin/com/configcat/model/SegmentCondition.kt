package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SegmentCondition(
    @SerialName(value = "s")
    val segmentIndex: Double? = null,
    @SerialName(value = "c")
    val segmentComparator: Int = 0
) {
    // No implementation
}
