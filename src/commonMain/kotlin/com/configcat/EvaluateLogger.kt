package com.configcat

import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.model.PrerequisiteFlagCondition
import com.configcat.model.Segment
import com.configcat.model.SegmentCondition
import com.configcat.model.SettingValue
import com.configcat.model.TargetingRule
import com.configcat.model.UserCondition

@Suppress("TooManyFunctions")
internal class EvaluateLogger {
    private val entries = StringBuilder()
    private var indentLevel: Int = 0

    fun append(line: String) {
        entries.append(line)
    }

    fun increaseIndentLevel() {
        indentLevel++
    }

    fun decreaseIndentLevel() {
        if (indentLevel > 0) {
            indentLevel--
        }
    }

    fun newLine() {
        entries.appendLine()
        for (i in 0 until indentLevel) {
            entries.append("  ")
        }
    }

    fun print(): String = entries.toString()

    fun logEvaluation(key: String) {
        append("Evaluating '$key'")
    }

    fun logReturnValue(value: Any) {
        newLine()
        append("Returning '$value'.")
    }

    fun logUserObject(user: ConfigCatUser) {
        append(" for User '$user'")
    }

    fun logPercentageOptionUserMissing() {
        newLine()
        append("Skipping % options because the User Object is missing.")
    }

    fun logPercentageOptionUserAttributeMissing(percentageOptionsAttributeName: String) {
        newLine()
        append("Skipping % options because the User.$percentageOptionsAttributeName attribute is missing.")
    }

    fun logPercentageOptionEvaluation(percentageOptionsAttributeName: String) {
        newLine()
        append("Evaluating % options based on the User.$percentageOptionsAttributeName attribute:")
    }

    fun logPercentageOptionEvaluationHash(
        percentageOptionsAttributeName: String,
        hashValue: Int,
    ) {
        newLine()
        append(
            "- Computing hash in the [0..99] range from User.$percentageOptionsAttributeName => " +
                "$hashValue (this value is sticky and consistent across all SDKs)",
        )
    }

    fun logTargetingRules() {
        newLine()
        append("Evaluating targeting rules and applying the first match if any:")
    }

    fun logConditionConsequence(result: Boolean) {
        append(" => $result")
        if (!result) {
            append(", skipping the remaining AND conditions")
        }
    }

    fun logTargetingRuleIgnored() {
        increaseIndentLevel()
        newLine()
        append("The current targeting rule is ignored and the evaluation continues with the next rule.")
        decreaseIndentLevel()
    }

    fun logTargetingRuleConsequence(
        targetingRule: TargetingRule?,
        error: String?,
        isMatch: Boolean,
        newLine: Boolean,
    ) {
        increaseIndentLevel()
        var valueFormat = "% options"
        if (targetingRule?.servedValue?.value != null) {
            valueFormat = "'" + targetingRule.servedValue.value + "'"
        }
        if (newLine) {
            newLine()
        } else {
            append(" ")
        }
        append("THEN $valueFormat => ")
        if (!error.isNullOrEmpty()) {
            append(error)
        } else {
            if (isMatch) {
                append("MATCH, applying rule")
            } else {
                append("no match")
            }
        }
        decreaseIndentLevel()
    }

    fun logPercentageEvaluationReturnValue(
        hashValue: Int,
        i: Int,
        percentage: Int,
        settingValue: SettingValue?,
    ) {
        val percentageOptionValue = settingValue?.toString() ?: EvaluatorLogHelper.INVALID_VALUE
        newLine()
        append("- Hash value $hashValue selects % option ${(i + 1)} ($percentage%), '$percentageOptionValue'.")
    }

    fun logSegmentEvaluationStart(segmentName: String) {
        newLine()
        append("(")
        increaseIndentLevel()
        newLine()
        append("Evaluating segment '$segmentName':")
    }

    fun logSegmentEvaluationResult(
        segmentCondition: SegmentCondition?,
        segment: Segment?,
        result: Boolean,
        segmentResult: Boolean,
    ) {
        newLine()
        val segmentResultComparator: String =
            if (segmentResult) {
                Evaluator.SegmentComparator.IS_IN_SEGMENT.value
            } else {
                Evaluator.SegmentComparator.IS_NOT_IN_SEGMENT.value
            }
        append("Segment evaluation result: User $segmentResultComparator.")
        newLine()
        append(
            "Condition (${EvaluatorLogHelper.formatSegmentFlagCondition(segmentCondition, segment)}) evaluates" +
                " to $result.",
        )
        decreaseIndentLevel()
        newLine()
        append(")")
    }

    fun logSegmentEvaluationError(
        segmentCondition: SegmentCondition?,
        segment: Segment?,
        error: String?,
    ) {
        newLine()
        append("Segment evaluation result: $error.")
        newLine()
        append(
            "Condition (${EvaluatorLogHelper.formatSegmentFlagCondition(segmentCondition, segment)}) failed to " +
                "evaluate.",
        )
        decreaseIndentLevel()
        newLine()
        append(")")
    }

    fun logPrerequisiteFlagEvaluationStart(prerequisiteFlagKey: String) {
        newLine()
        append("(")
        increaseIndentLevel()
        newLine()
        append("Evaluating prerequisite flag '$prerequisiteFlagKey':")
    }

    fun logPrerequisiteFlagEvaluationResult(
        prerequisiteFlagCondition: PrerequisiteFlagCondition?,
        prerequisiteFlagValue: SettingValue?,
        result: Boolean,
    ) {
        newLine()
        val prerequisiteFlagValueFormat = prerequisiteFlagValue?.toString() ?: EvaluatorLogHelper.INVALID_VALUE
        append("Prerequisite flag evaluation result: '$prerequisiteFlagValueFormat'.")
        newLine()
        append(
            "Condition (${EvaluatorLogHelper.formatPrerequisiteFlagCondition(prerequisiteFlagCondition!!)}) " +
                "evaluates to $result.",
        )
        decreaseIndentLevel()
        newLine()
        append(")")
    }
}

internal object EvaluatorLogHelper {
    private const val HASHED_VALUE = "<hashed value>"
    const val INVALID_VALUE = "<invalid value>"
    private const val INVALID_NAME = "<invalid name>"
    private const val INVALID_REFERENCE = "<invalid reference>"
    private const val MAX_LIST_ELEMENT = 10

    private fun formatStringListComparisonValue(
        comparisonValue: Array<String>?,
        isSensitive: Boolean,
    ): String {
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

    private fun formatStringComparisonValue(
        comparisonValue: String?,
        isSensitive: Boolean,
    ): String =
        if (isSensitive) {
            "'$HASHED_VALUE'"
        } else {
            "'$comparisonValue'"
        }

    private fun formatDoubleComparisonValue(
        comparisonValue: Double?,
        isDate: Boolean,
    ): String {
        if (comparisonValue == null) {
            return INVALID_VALUE
        }
        val comparisonValueString = formatDoubleForLog(comparisonValue)
        return if (isDate) {
            "'$comparisonValueString' (${comparisonValue.toDateTimeUTCString()} UTC)"
        } else {
            "'$comparisonValueString'"
        }
    }

    fun formatUserCondition(userCondition: UserCondition): String {
        val userComparator = userCondition.comparator.toComparatorOrNull()
        val comparisonValue: String =
            when (userComparator) {
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
                Evaluator.UserComparator.TEXT_ARRAY_NOT_CONTAINS,
                ->
                    formatStringListComparisonValue(
                        userCondition.stringArrayValue,
                        false,
                    )
                Evaluator.UserComparator.LT_SEMVER,
                Evaluator.UserComparator.LTE_SEMVER,
                Evaluator.UserComparator.GT_SEMVER,
                Evaluator.UserComparator.GTE_SEMVER,
                Evaluator.UserComparator.TEXT_EQUALS,
                Evaluator.UserComparator.TEXT_NOT_EQUALS,
                ->
                    formatStringComparisonValue(
                        userCondition.stringValue,
                        false,
                    )
                Evaluator.UserComparator.EQ_NUM,
                Evaluator.UserComparator.NOT_EQ_NUM,
                Evaluator.UserComparator.LT_NUM,
                Evaluator.UserComparator.LTE_NUM,
                Evaluator.UserComparator.GT_NUM,
                Evaluator.UserComparator.GTE_NUM,
                ->
                    formatDoubleComparisonValue(
                        userCondition.doubleValue,
                        false,
                    )
                Evaluator.UserComparator.ONE_OF_SENS,
                Evaluator.UserComparator.NOT_ONE_OF_SENS,
                Evaluator.UserComparator.HASHED_STARTS_WITH,
                Evaluator.UserComparator.HASHED_NOT_STARTS_WITH,
                Evaluator.UserComparator.HASHED_ENDS_WITH,
                Evaluator.UserComparator.HASHED_NOT_ENDS_WITH,
                Evaluator.UserComparator.HASHED_ARRAY_CONTAINS,
                Evaluator.UserComparator.HASHED_ARRAY_NOT_CONTAINS,
                ->
                    formatStringListComparisonValue(
                        userCondition.stringArrayValue,
                        true,
                    )
                Evaluator.UserComparator.DATE_BEFORE, Evaluator.UserComparator.DATE_AFTER ->
                    formatDoubleComparisonValue(
                        userCondition.doubleValue,
                        true,
                    )
                Evaluator.UserComparator.HASHED_EQUALS, Evaluator.UserComparator.HASHED_NOT_EQUALS ->
                    formatStringComparisonValue(
                        userCondition.stringValue,
                        true,
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

    fun formatCircularDependencyList(
        visitedKeys: List<String?>,
        key: String?,
    ): String {
        val builder = StringBuilder()
        visitedKeys.forEach { visitedKey: String? ->
            builder.append("'").append(visitedKey).append("' -> ")
        }
        builder.append("'").append(key).append("'")
        return builder.toString()
    }

    fun formatSegmentFlagCondition(
        segmentCondition: SegmentCondition?,
        segment: Segment?,
    ): String {
        val segmentName: String =
            if (segment != null) {
                segment.name ?: INVALID_NAME
            } else {
                INVALID_REFERENCE
            }
        val prerequisiteComparator = segmentCondition?.segmentComparator?.toSegmentComparatorOrNull()
        return "User ${prerequisiteComparator?.value} '$segmentName'"
    }
}
