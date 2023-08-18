package com.configcat

import com.configcat.DateTimeUtils.toDateTimeUTCString
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.soywiz.krypto.sha1
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion

internal data class EvaluationResult(
    val value: Any,
    val variationId: String?,
    val targetingRule: RolloutRule? = null,
    val percentageRule: PercentageRule? = null
)

internal class Evaluator(private val logger: InternalLogger) {

    // evaluatorLogger: EvaluatorLogger;

    fun evaluate(setting: Setting, key: String, user: ConfigCatUser?): EvaluationResult {
        val evaluatorLogger = EvaluatorLogger(key)
        try {
            if (user == null) {
                if (setting.rolloutRules.isNotEmpty() || setting.percentageItems.isNotEmpty()) {
                    logger.warning(3001, ConfigCatLogMessages.getTargetingIsNotPossible(key))
                }
                evaluatorLogger.logReturnValue(setting.value)
                return EvaluationResult(setting.value, setting.variationId)
            }
            evaluatorLogger.logUserObject(user)
            val valueFromTargetingRules = processTargetingRules(setting, user, evaluatorLogger)
            if (valueFromTargetingRules != null) return valueFromTargetingRules

            val valueFromPercentageRules = processPercentageRules(setting, user, key, evaluatorLogger)
            if (valueFromPercentageRules != null) return valueFromPercentageRules

            evaluatorLogger.logReturnValue(setting.value)
            return EvaluationResult(setting.value, setting.variationId)
        } finally {
            logger.info(5000, evaluatorLogger.print())
        }
    }

    @Suppress("ComplexMethod", "LoopWithTooManyJumpStatements")
    private fun processTargetingRules(
        setting: Setting,
        user: ConfigCatUser,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        if (setting.rolloutRules.isEmpty()) {
            return null
        }
        for (rule in setting.rolloutRules) {
            val userValue = user.attributeFor(rule.comparisonAttribute)
            val comparator = rule.comparator.toComparatorOrNull()

            if (comparator == null) {
                evaluatorLogger.logComparatorError(
                    rule.comparisonAttribute,
                    userValue ?: "",
                    rule.comparator,
                    rule.comparisonValue
                )
                continue
            }
            if (userValue.isNullOrEmpty() || rule.comparisonValue.isEmpty()) {
                evaluatorLogger.logNoMatch(
                    rule.comparisonAttribute,
                    userValue ?: "",
                    comparator,
                    rule.comparisonValue
                )
                continue
            }

            when (comparator) {
                Comparator.CONTAINS_ANY_OF,
                Comparator.NOT_CONTAINS_ANY_OF -> {
                    val value = processContains(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.ONE_OF_SEMVER,
                Comparator.NOT_ONE_OF_SEMVER -> {
                    val value = processSemverOneOf(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.LT_SEMVER,
                Comparator.LTE_SEMVER,
                Comparator.GT_SEMVER,
                Comparator.GTE_SEMVER -> {
                    val value = processSemverCompare(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.EQ_NUM,
                Comparator.NOT_EQ_NUM,
                Comparator.LT_NUM,
                Comparator.LTE_NUM,
                Comparator.GT_NUM,
                Comparator.GTE_NUM -> {
                    val value = processNumber(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.ONE_OF_SENS,
                Comparator.NOT_ONE_OF_SENS -> {
                    val value = processSensitiveOneOf(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.DATE_BEFORE,
                Comparator.DATE_AFTER -> {
                    val value = processDateCompare(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.HASHED_EQUALS,
                Comparator.HASHED_NOT_EQUALS -> {
                    val value = processHashedEqualsCompare(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.HASHED_STARTS_WITH,
                Comparator.HASHED_NOT_STARTS_WITH,
                Comparator.HASHED_ENDS_WITH,
                Comparator.HASHED_NOT_ENDS_WITH -> {
                    val value = processHashedStartEndsWithCompare(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.HASHED_ARRAY_CONTAINS,
                Comparator.HASHED_ARRAY_NOT_CONTAINS -> {
                    val value = processHashedArrayContainsCompare(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }
            }
        }
        return null
    }


    private fun processContains(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val matchCondition = when (comparator) {
            Comparator.CONTAINS_ANY_OF -> split.contains(userValue)
            Comparator.NOT_CONTAINS_ANY_OF -> !split.contains(userValue)
            else -> false
        }
        if (matchCondition) {
            evaluatorLogger.logMatch(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                rule.value
            )
            return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
        }
        return null
    }

    private fun processSemverOneOf(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        try {
            val userVersion = userValue.toVersion()
            val split = rule.comparisonValue.split(",")
                .map { it.trim() }.filter { it.isNotEmpty() }
            var matched = false
            for (value in split) {
                matched = value.toVersion() == userVersion || matched
            }
            if ((matched && comparator == Comparator.ONE_OF_SEMVER) ||
                (!matched && comparator == Comparator.NOT_ONE_OF_SEMVER)
            ) {
                evaluatorLogger.logMatch(
                    rule.comparisonAttribute,
                    userValue,
                    comparator,
                    rule.comparisonValue,
                    rule.value
                )
                return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
            }
        } catch (e: VersionFormatException) {
            evaluatorLogger.logFormatError(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                e
            )
        }
        return null
    }

    private fun processSemverCompare(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        try {
            val userVersion = userValue.trim().toVersion()
            val comparisonVersion = rule.comparisonValue.trim().toVersion()
            val matchCondition = when (comparator) {
                Comparator.LT_SEMVER -> userVersion < comparisonVersion
                Comparator.LTE_SEMVER -> userVersion <= comparisonVersion
                Comparator.GT_SEMVER -> userVersion > comparisonVersion
                Comparator.GTE_SEMVER -> userVersion >= comparisonVersion
                else -> false
            }
            if (matchCondition) {
                evaluatorLogger.logMatch(
                    rule.comparisonAttribute,
                    userValue,
                    comparator,
                    rule.comparisonValue,
                    rule.value
                )
                return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
            }
        } catch (e: VersionFormatException) {
            evaluatorLogger.logFormatError(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                e
            )
        }
        return null
    }

    private fun processNumber(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        try {
            val userNumber = userValue.trim().replace(",", ".").toDouble()
            val comparisonNumber = rule.comparisonValue.trim().replace(",", ".").toDouble()
            val matchCondition = when (comparator) {
                Comparator.EQ_NUM -> userNumber == comparisonNumber
                Comparator.NOT_EQ_NUM -> userNumber != comparisonNumber
                Comparator.LT_NUM -> userNumber < comparisonNumber
                Comparator.LTE_NUM -> userNumber <= comparisonNumber
                Comparator.GT_NUM -> userNumber > comparisonNumber
                Comparator.GTE_NUM -> userNumber >= comparisonNumber
                else -> false
            }
            if (matchCondition) {
                evaluatorLogger.logMatch(
                    rule.comparisonAttribute,
                    userValue,
                    comparator,
                    rule.comparisonValue,
                    rule.value
                )
                return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
            }
        } catch (e: NumberFormatException) {
            evaluatorLogger.logFormatError(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                e
            )
        }
        return null
    }

    private fun processSensitiveOneOf(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        //TODO add salt and salt error handle
        val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val userValueHash = userValue.encodeToByteArray().sha1().hex
        val matchCondition = when (comparator) {
            Comparator.ONE_OF_SENS -> split.contains(userValueHash)
            Comparator.NOT_ONE_OF_SENS -> !split.contains(userValueHash)
            else -> false
        }
        if (matchCondition) {
            evaluatorLogger.logMatch(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                rule.value
            )
            return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
        }
        return null
    }

    private fun processDateCompare(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        try {
            val userDateDouble = userValue.trim().replace(",", ".").toDouble()
            val comparisonDateDouble = rule.comparisonValue.trim().replace(",", ".").toDouble()
            val matchCondition = when (comparator) {
                Comparator.DATE_BEFORE -> userDateDouble < comparisonDateDouble
                Comparator.DATE_AFTER -> userDateDouble > comparisonDateDouble
                else -> false
            }
            if (matchCondition) {
                evaluatorLogger.logMatch(
                    rule.comparisonAttribute,
                    userDateDouble,
                    comparator,
                    comparisonDateDouble,
                    rule.value
                )
                return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
            }
        } catch (e: NumberFormatException) {
            //TODO add date specific error '{userAttributeValue}' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)
            evaluatorLogger.logFormatError(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                e
            )
        }
        return null
    }

    private fun processHashedEqualsCompare(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        //TODO add salt and salt error handle
        val userValueHash = userValue.encodeToByteArray().sha1().hex
        val matchCondition = when (comparator) {
            Comparator.HASHED_EQUALS -> userValueHash == rule.comparisonValue
            Comparator.HASHED_NOT_EQUALS -> userValueHash != rule.comparisonValue
            else -> false
        }
        if (matchCondition) {
            evaluatorLogger.logMatch(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                rule.value
            )
            return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
        }
        return null
    }

    private fun processHashedStartEndsWithCompare(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        //TODO add salt and salt error handle
        //TODO handle NOT as well

        val withValuesSplit = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        var matchCondition = false
        for (comparisonValueHashedStartsEnds in withValuesSplit) {
            try {
                val comparedTextLength = comparisonValueHashedStartsEnds.substringBeforeLast("_")
                if (comparedTextLength == comparisonValueHashedStartsEnds)
                    return null
                val comparedTextLengthInt: Int = comparedTextLength.toInt()
                val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfterLast("_")
                if (comparisonHashValue.isEmpty())
                    return null

                val userValueHashed = if( comparator == Comparator.HASHED_STARTS_WITH || comparator == Comparator.HASHED_NOT_STARTS_WITH )
                    userValue.substring(0, comparedTextLengthInt).encodeToByteArray().sha1().hex
                else
                // Comparator.HASHED_ENDS_WITH, Comparator.HASHED_NOT_ENDS_WITH
                    userValue.substring(userValue.length - comparedTextLengthInt).encodeToByteArray().sha1().hex

                if(userValueHashed == comparisonHashValue)
                    matchCondition = true

            } catch (e: NumberFormatException) {
                evaluatorLogger.logFormatError(
                    rule.comparisonAttribute,
                    userValue,
                    comparator,
                    rule.comparisonValue,
                    e
                )
            }
        }

        if (comparator == Comparator.HASHED_NOT_STARTS_WITH || comparator == Comparator.HASHED_NOT_ENDS_WITH )
            //negate the match in case of NOT ANY OF
            matchCondition = !matchCondition

        if (matchCondition) {
            evaluatorLogger.logMatch(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                rule.value
            )
            return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
        }
        return null
    }

    private fun processHashedArrayContainsCompare(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        //TODO add salt and salt error handle
        val userCSVNotContainsHashSplit = userValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (userCSVNotContainsHashSplit.isEmpty()) {
            return null
        }
        var contains = false
        userCSVNotContainsHashSplit.forEach {
            val hashedUserValue = it.encodeToByteArray().sha1().hex
            contains = hashedUserValue == rule.comparisonValue
        }
        val matchCondition = when (comparator) {
            Comparator.HASHED_EQUALS -> contains
            Comparator.HASHED_NOT_EQUALS -> !contains
            else -> false
        }
        if (matchCondition) {
            evaluatorLogger.logMatch(
                rule.comparisonAttribute,
                userValue,
                comparator,
                rule.comparisonValue,
                rule.value
            )
            return EvaluationResult(rule.value, rule.variationId, targetingRule = rule)
        }
        return null
    }

    private fun processPercentageRules(
        setting: Setting,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        //TODO add salt and salt error handle
        if (setting.percentageItems.isEmpty()) {
            return null
        }

        val hashCandidate = "$key${user.identifier}"
        val hash = hashCandidate.encodeToByteArray().sha1().hex.substring(0, 7)
        val numberRepresentation = hash.toInt(radix = 16)
        val scale = numberRepresentation % 100

        var bucket = 0.0
        for (rule in setting.percentageItems) {
            bucket += rule.percentage
            if (scale < bucket) {
                evaluatorLogger.logPercentageEvaluationReturnValue(rule.value)
                return EvaluationResult(rule.value, rule.variationId, percentageRule = rule)
            }
        }
        return null
    }

    enum class Comparator(val id: Int, val value: String) {
        CONTAINS_ANY_OF(2, "CONTAINS ANY OF"),
        NOT_CONTAINS_ANY_OF(3, "NOT CONTAINS ANY OF"),
        ONE_OF_SEMVER(4, "IS ONE OF (semver)"),
        NOT_ONE_OF_SEMVER(5, "IS NOT ONE OF (semver)"),
        LT_SEMVER(6, "< (semver)"),
        LTE_SEMVER(7, "<= (semver)"),
        GT_SEMVER(8, "> (semver)"),
        GTE_SEMVER(9, ">= (semver)"),
        EQ_NUM(10, "= (number)"),
        NOT_EQ_NUM(11, "<> (number)"),
        LT_NUM(12, "< (number)"),
        LTE_NUM(13, "<= (number)"),
        GT_NUM(14, "> (number)"),
        GTE_NUM(15, ">= (number)"),
        ONE_OF_SENS(16, "IS ONE OF (hashed)"),
        NOT_ONE_OF_SENS(17, "IS NOT ONE OF (hashed)"),
        DATE_BEFORE(18, "BEFORE (UTC DateTime)"),
        DATE_AFTER(19, "AFTER (UTC DateTime)"),
        HASHED_EQUALS(20, "EQUALS (hashed)"),
        HASHED_NOT_EQUALS(21, "NOT EQUALS (hashed)"),
        HASHED_STARTS_WITH(22, "STARTS WITH ANY OF (hashed)"),
        HASHED_NOT_STARTS_WITH(23, "NOT STARTS WITH ANY OF (hashed)"),
        HASHED_ENDS_WITH(24, "ENDS WITH ANY OF (hashed)"),
        HASHED_NOT_ENDS_WITH(25, "NOT ENDS WITH ANY OF (hashed)"),
        HASHED_ARRAY_CONTAINS(26, "ARRAY CONTAINS (hashed)"),
        HASHED_ARRAY_NOT_CONTAINS(27, "ARRAY NOT CONTAINS (hashed)")
    }

    private fun Int.toComparatorOrNull(): Comparator? = Comparator.values().firstOrNull { it.id == this }
}

internal class EvaluatorLogger constructor(
    key: String
) {
    private val entries = StringBuilder()

    init {
        entries.appendLine("Evaluating '$key'")
    }

    fun logReturnValue(value: Any) {
        entries.appendLine("Returning $value")
    }

    fun logPercentageEvaluationReturnValue(value: Any) {
        entries.appendLine("Evaluating % options. Returning $value.")
    }

    fun logUserObject(user: ConfigCatUser) {
        entries.appendLine("User object: $user")
    }

    fun logMatch(
        attribute: String,
        userValue: String,
        comparator: Evaluator.Comparator,
        comparisonValue: String,
        value: Any?
    ) {
        entries.appendLine(
            "Evaluating rule: [$attribute:$userValue] " +
                    "[${comparator.value}] [$comparisonValue] => match, returning: $value"
        )
    }

    fun logMatch(
        attribute: String,
        userValue: Double,
        comparator: Evaluator.Comparator,
        comparisonValue: Double,
        value: Any?
    ) {
        entries.appendLine(
            "Evaluating rule: [$attribute:$userValue (${userValue.toDateTimeUTCString()})] " +
                    "[${comparator.value}] [$comparisonValue (${comparisonValue.toDateTimeUTCString()})] => match, returning: $value"
        )
    }

    fun logNoMatch(
        attribute: String,
        userValue: String,
        comparator: Evaluator.Comparator,
        comparisonValue: String
    ) {
        entries.appendLine(
            "Evaluating rule: " +
                    "[$attribute:$userValue] [${comparator.value}] [$comparisonValue] => no match"
        )
    }

    fun logFormatError(
        attribute: String,
        userValue: String,
        comparator: Evaluator.Comparator,
        comparisonValue: String,
        error: Throwable
    ) {
        entries.appendLine(
            "Evaluating rule: [$attribute:$userValue] [${comparator.value}] " +
                    "[$comparisonValue] => SKIP rule. Validation error: ${error.message}"
        )
    }

    fun logComparatorError(
        attribute: String,
        userValue: String,
        comparator: Int,
        comparisonValue: String
    ) {
        entries.appendLine(
            "Evaluating rule: [$attribute:$userValue] [$comparisonValue] => SKIP rule. Invalid comparator: $comparator"
        )
    }

    fun print(): String {
        return entries.toString()
    }


}
