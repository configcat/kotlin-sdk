package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * ConfigCat segment.
 */
@Serializable
public data class Segment(
    /**
     * The name of the segment.
     */
    @SerialName("n")
    val name: String? = null,
    /**
     * The list of segment rule conditions (where there is a logical AND relation between the items).
     */
    @SerialName("r")
    val segmentRules: Array<UserCondition>? = null,
) {
    internal val conditionAccessors: List<ConditionAccessor> =
        segmentRules?.let { condition -> condition.map { it } } ?: listOf()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Segment) return false

        if (name != other.name) return false
        if (!segmentRules.contentEquals(other.segmentRules)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + segmentRules.contentHashCode()
        return result
    }
}
