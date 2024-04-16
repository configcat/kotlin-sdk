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
    val segmentIndex: Int = -1,
    /**
     * The operator which defines the expected result of the evaluation of the segment.
     */
    @SerialName(value = "c")
    val segmentComparator: Int = -1,
) {
    // No implementation
}
