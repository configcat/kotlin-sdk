package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Segment Condition.
 */
@Serializable
public data class SegmentCondition(
    /**
     * The index of the segment that the condition is based on.
     */
    @SerialName(value = "s")
    val segmentIndex: Int,
    /**
     * The operator which defines the expected result of the evaluation of the segment.
     */
    @SerialName(value = "c")
    val segmentComparator: Int = 0
) {
    // No implementation
}
