package com.configcat.model

import kotlinx.serialization.Serializable

@Serializable
internal sealed interface ConditionAccessor {
    val userCondition: UserCondition?
    val segmentCondition: SegmentCondition?
    val prerequisiteFlagCondition: PrerequisiteFlagCondition?
}
