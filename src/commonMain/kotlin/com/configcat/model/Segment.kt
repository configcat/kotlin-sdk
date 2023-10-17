package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ConfigCat segment.
 */
@Serializable
internal data class Segment(
    /**
     * The name of the segment.
     */
    @SerialName("n")
    val name: String? = null,
    /**
     * The list of segment rule conditions (where there is a logical AND relation between the items).
     */
    @SerialName("r")
    val segmentRules: Array<UserCondition>
) {
    // No implementation
}
