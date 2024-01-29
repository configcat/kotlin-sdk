package com.configcat.log

import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.DateTimeUtils.toDateTimeUTCString
import com.configcat.Evaluator
import com.configcat.model.PrerequisiteFlagCondition
import com.configcat.model.Segment
import com.configcat.model.SegmentCondition
import com.configcat.model.UserCondition

internal object LogHelper {
    private const val HASHED_VALUE = "<hashed value>"
    const val INVALID_VALUE = "<invalid value>"
    private const val INVALID_NAME = "<invalid name>"
    private const val INVALID_REFERENCE = "<invalid reference>"
    private const val MAX_LIST_ELEMENT = 10
    private fun formatStringListComparisonValue(comparisonValue: Array<String>?, isSensitive: Boolean): String {
        if (comparisonValue == null) {
            return INVALID_VALUE
        }
        val comparisonValues = comparisonValue.map { it }
        if (comparisonValues.isEmpty()) {
            return INVALID_VALUE
        }
        val formattedList: String
        if (isSensitive) {
            val sensitivePostFix = if (comparisonValues.size == 1) "value" else "values"
            formattedList = "<${comparisonValues.size} hashed $sensitivePostFix>"
        } else {
            var listPostFix = ""
            if (comparisonValues.size > MAX_LIST_ELEMENT) {
                val count = comparisonValues.size - MAX_LIST_ELEMENT
                val countPostFix = if (count == 1) "value" else "values"
                listPostFix = ", ... <$count more $countPostFix>"
            }
            val subList: List<String> = comparisonValues.subList(0, minOf(MAX_LIST_ELEMENT, comparisonValues.size))
            val formatListBuilder = StringBuilder()
            for (i in subList.indices) {
                formatListBuilder.append("'${subList[i]}'")
                if (i != subList.size - 1) {
                    formatListBuilder.append(", ")
                }
            }
            formatListBuilder.append(listPostFix)
            formattedList = formatListBuilder.toString()
        }
        return "[$formattedList]"
    }

    private fun formatStringComparisonValue(comparisonValue: String?, isSensitive: Boolean): String {
        return if (isSensitive) {
            "'$HASHED_VALUE'"
        } else {
            "'$comparisonValue'"
        }
    }

    private fun formatDoubleComparisonValue(comparisonValue: Double?, isDate: Boolean): String {
        if (comparisonValue == null) {
            return INVALID_VALUE
        }

        return if (isDate) {
            val comparisonValueString = comparisonValue.toDouble().toString()
            "'$comparisonValueString' (${comparisonValue.toDateTimeUTCString()} UTC)"
        } else {
            var comparisonValueString = comparisonValue.toString()
            if (comparisonValueString.contains('.') || comparisonValueString.contains(',')) {
                comparisonValueString =
                    comparisonValueString.trimEnd { it == '0' }.trimEnd { it == '.' }.trimEnd { it == ',' }
            }
            "'$comparisonValueString'"
        }
    }

    fun formatUserCondition(userCondition: UserCondition): String {
        val userComparator = userCondition.comparator.toComparatorOrNull()
        val comparisonValue: String = when (userComparator) {
            Evaluator.UserComparator.IS_ONE_OF,
            Evaluator.UserComparator.IS_NOT_ONE_OF,
            Evaluator.UserComparator.CONTAINS_ANY_OF,
            Evaluator.UserComparator.NOT_CONTAINS_ANY_OF,
            Evaluator.UserComparator.ONE_OF_SEMVER,
            Evaluator.UserComparator.NOT_ONE_OF_SEMVER,
            Evaluator.UserComparator.TEXT_STARTS_WITH,
            Evaluator.UserComparator.TEXT_NOT_STARTS_WITH,
            Evaluator.UserComparator.TEXT_ENDS_WITH,
            Evaluator.UserComparator.TEXT_NOT_ENDS_WITH,
            Evaluator.UserComparator.TEXT_ARRAY_CONTAINS,
            Evaluator.UserComparator.TEXT_ARRAY_NOT_CONTAINS -> formatStringListComparisonValue(
                userCondition.stringArrayValue,
                false
            )

            Evaluator.UserComparator.LT_SEMVER,
            Evaluator.UserComparator.LTE_SEMVER,
            Evaluator.UserComparator.GT_SEMVER,
            Evaluator.UserComparator.GTE_SEMVER,
            Evaluator.UserComparator.TEXT_EQUALS,
            Evaluator.UserComparator.TEXT_NOT_EQUALS -> formatStringComparisonValue(
                userCondition.stringValue,
                false
            )

            Evaluator.UserComparator.EQ_NUM,
            Evaluator.UserComparator.NOT_EQ_NUM,
            Evaluator.UserComparator.LT_NUM,
            Evaluator.UserComparator.LTE_NUM,
            Evaluator.UserComparator.GT_NUM,
            Evaluator.UserComparator.GTE_NUM -> formatDoubleComparisonValue(
                userCondition.doubleValue,
                false
            )

            Evaluator.UserComparator.ONE_OF_SENS,
            Evaluator.UserComparator.NOT_ONE_OF_SENS,
            Evaluator.UserComparator.HASHED_STARTS_WITH,
            Evaluator.UserComparator.HASHED_NOT_STARTS_WITH,
            Evaluator.UserComparator.HASHED_ENDS_WITH,
            Evaluator.UserComparator.HASHED_NOT_ENDS_WITH,
            Evaluator.UserComparator.HASHED_ARRAY_CONTAINS,
            Evaluator.UserComparator.HASHED_ARRAY_NOT_CONTAINS -> formatStringListComparisonValue(
                userCondition.stringArrayValue,
                true
            )

            Evaluator.UserComparator.DATE_BEFORE, Evaluator.UserComparator.DATE_AFTER -> formatDoubleComparisonValue(
                userCondition.doubleValue,
                true
            )

            Evaluator.UserComparator.HASHED_EQUALS, Evaluator.UserComparator.HASHED_NOT_EQUALS -> formatStringComparisonValue(
                userCondition.stringValue,
                true
            )

            else -> INVALID_VALUE
        }
        return "User.${userCondition.comparisonAttribute} ${userComparator?.value} $comparisonValue"
    }

    fun formatPrerequisiteFlagCondition(prerequisiteFlagCondition: PrerequisiteFlagCondition): String {
        val prerequisiteComparator = prerequisiteFlagCondition.prerequisiteComparator.toPrerequisiteComparatorOrNull()
        return "Flag '${prerequisiteFlagCondition.prerequisiteFlagKey}' ${prerequisiteComparator?.value} " +
            "'${prerequisiteFlagCondition.value ?: INVALID_VALUE}'"
    }

    fun formatCircularDependencyList(visitedKeys: List<String?>, key: String?): String {
        val builder = StringBuilder()
        visitedKeys.forEach { visitedKey: String? ->
            builder.append("'").append(visitedKey).append("' -> ")
        }
        builder.append("'").append(key).append("'")
        return builder.toString()
    }

    fun formatSegmentFlagCondition(segmentCondition: SegmentCondition?, segment: Segment?): String {
        val segmentName: String = if (segment != null) {
            segment.name ?: INVALID_NAME
        } else {
            INVALID_REFERENCE
        }
        val prerequisiteComparator = segmentCondition?.segmentComparator?.toSegmentComparatorOrNull()
        return "User ${prerequisiteComparator?.value} '$segmentName'"
    }
}
