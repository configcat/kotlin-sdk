package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class UserCondition(
    @SerialName("a")
    val comparisonAttribute: String,
    @SerialName("c")
    val comparator: Int,
    @SerialName("s")
    val stringValue: String? = null,
    @SerialName("d")
    val doubleValue: Double? = null,
    @SerialName("l")
    val stringArrayValue: Array<String>? = null
) {
    // No implementation
}
