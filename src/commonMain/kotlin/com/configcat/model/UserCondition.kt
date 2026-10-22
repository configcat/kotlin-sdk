package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * User Condition.
 */
@Serializable
public data class UserCondition(
    /**
     * The User Object attribute that the condition is based on. Can be "User ID", "Email", "Country" or
     * any custom attribute.
     */
    @SerialName("a")
    val comparisonAttribute: String,
    /**
     * The operator which defines the relation between the comparison attribute and the comparison value.
     */
    @SerialName("c")
    val comparator: Int = -1,
    /**
     * The String value that the attribute is compared or {@code null} if the comparator use a different type.
     */
    @SerialName("s")
    val stringValue: String? = null,
    /**
     * The Double value that the attribute is compared or {@code null} if the comparator use a different type.
     */
    @SerialName("d")
    val doubleValue: Double? = null,
    /**
     * The String Array value that the attribute is compared or {@code null} if the comparator use a different type.
     */
    @SerialName("l")
    val stringArrayValue: Array<String>? = null,
) : ConditionAccessor {
    @Transient
    override val userCondition: UserCondition = this

    @Transient
    override val segmentCondition: SegmentCondition? = null

    @Transient
    override val prerequisiteFlagCondition: PrerequisiteFlagCondition? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is UserCondition) return false

        if (comparator != other.comparator) return false
        if (doubleValue != other.doubleValue) return false
        if (comparisonAttribute != other.comparisonAttribute) return false
        if (stringValue != other.stringValue) return false
        if (!stringArrayValue.contentEquals(other.stringArrayValue)) return false
        if (userCondition != other.userCondition) return false
        if (segmentCondition != other.segmentCondition) return false
        if (prerequisiteFlagCondition != other.prerequisiteFlagCondition) return false

        return true
    }

    override fun hashCode(): Int {
        var result = comparator
        result = 31 * result + (doubleValue?.hashCode() ?: 0)
        result = 31 * result + comparisonAttribute.hashCode()
        result = 31 * result + (stringValue?.hashCode() ?: 0)
        result = 31 * result + (stringArrayValue?.contentHashCode() ?: 0)
        result = 31 * result + userCondition.hashCode()
        result = 31 * result + (segmentCondition?.hashCode() ?: 0)
        result = 31 * result + (prerequisiteFlagCondition?.hashCode() ?: 0)
        return result
    }
}
