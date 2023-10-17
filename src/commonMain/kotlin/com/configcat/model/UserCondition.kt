package com.configcat.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * User Condition.
 */
@Serializable
public data class UserCondition(
    /**
     * The User Object attribute that the condition is based on. Can be "User ID", "Email", "Country" or any custom attribute.
     */
    @SerialName("a")
    val comparisonAttribute: String,
    /**
     * The operator which defines the relation between the comparison attribute and the comparison value.
     */
    @SerialName("c")
    val comparator: Int,
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
    val stringArrayValue: Array<String>? = null
) {
    // No implementation
}
