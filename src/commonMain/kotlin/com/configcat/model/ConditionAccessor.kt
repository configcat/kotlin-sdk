package com.configcat.model

internal interface ConditionAccessor {
    val userCondition: UserCondition?
    val segmentCondition: SegmentCondition?
    val prerequisiteFlagCondition: PrerequisiteFlagCondition?
}
