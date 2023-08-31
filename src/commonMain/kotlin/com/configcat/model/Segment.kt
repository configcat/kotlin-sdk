package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class Segment(

    @SerialName("n")
    val name: String? = null,

    @SerialName("r")
    val segmentRules: Array<ComparisonCondition>
) {
    // No implementation
}
