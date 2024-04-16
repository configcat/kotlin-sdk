package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Container class for different condition types.
 */
@Serializable
public data class Condition(
    @SerialName(value = "u")
    override val userCondition: UserCondition? = null,
    @SerialName(value = "s")
    override val segmentCondition: SegmentCondition? = null,
    @SerialName(value = "p")
    override val prerequisiteFlagCondition: PrerequisiteFlagCondition? = null,
) : ConditionAccessor
