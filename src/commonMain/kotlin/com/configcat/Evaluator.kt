package com.configcat

import com.configcat.DateTimeUtils.toDateTimeUTCString
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.*
import com.soywiz.krypto.sha1
import com.soywiz.krypto.sha256
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion

internal data class EvaluationResult(
    val value: SettingsValue,
    val variationId: String?,
    val targetingRule: TargetingRule? = null,
    val percentageRule: PercentageOption? = null
)

internal class Evaluator(private val logger: InternalLogger) {

    // evaluatorLogger: EvaluatorLogger;

    fun evaluate(setting: Setting, key: String, user: ConfigCatUser?): EvaluationResult {
        val evaluatorLogger = EvaluatorLogger(key)
        // TODO update the logging
        try {
            if (user == null) {
                if (setting.targetingRules?.isNotEmpty() == true || setting.percentageOptions?.isNotEmpty() == true) {
                    logger.warning(3001, ConfigCatLogMessages.getTargetingIsNotPossible(key))
                }
                // TODO logging
                evaluatorLogger.logReturnValue(setting.settingsValue.toString())
                return EvaluationResult(setting.settingsValue, setting.variationId)
            }
            evaluatorLogger.logUserObject(user)
            val valueFromTargetingRules = evaluateTargetingRules(setting, user, key, evaluatorLogger)
            if (valueFromTargetingRules != null) return valueFromTargetingRules

            val valueFromPercentageRules = evaluatePercentageOptions(setting, user, key, evaluatorLogger)
            if (valueFromPercentageRules != null) return valueFromPercentageRules

            evaluatorLogger.logReturnValue(setting.settingsValue)
            return EvaluationResult(setting.settingsValue, setting.variationId)
        } finally {
            logger.info(5000, evaluatorLogger.print())
        }
    }

    @Suppress("ComplexMethod", "LoopWithTooManyJumpStatements")
    private fun evaluateTargetingRules(
        setting: Setting,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        if (setting.targetingRules.isNullOrEmpty()) {
            return null
        }
        for (rule in setting.targetingRules) {
            // TODO fix this. remove unnecessary null checks
            if (!evaluateConditions(rule.conditions ?: arrayOf(), setting.configSalt, user, key, evaluatorLogger)) {
                continue
            }
            if (rule.servedValue != null) {
                return EvaluationResult(rule.servedValue.value, rule.servedValue.variationId, rule, null)
            }
            if (rule.percentageOptions.isNullOrEmpty()) {
                continue
            }
            return evaluatePercentageOptions(setting, user, key, evaluatorLogger)
        }
        return null
    }

    private fun evaluateConditions(
        conditions: Array<Condition>,
        configSalt: String,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): Boolean {
        // TODO rework logging based on changes possibly

        // Conditions are ANDs so if One is not matching return false, if all matching return true
        // TODO rework logging based on changes possibly
        var conditionsEvaluationResult = false
        for (condition in conditions) {
            // TODO log IF, AND based on order

            // TODO Condition, what if condition invalid? more then one condition added or none. rework basic if
            if (condition.comparisonCondition != null) {
                conditionsEvaluationResult = evaluateComparisonCondition(
                    condition.comparisonCondition,
                    configSalt,
                    user,
                    key,
                    evaluatorLogger
                )
            } else if (condition.segmentCondition != null) {
                conditionsEvaluationResult = evaluateSegmentCondition(condition.segmentCondition)
            } else if (condition.prerequisiteFlagCondition != null) {
                conditionsEvaluationResult = evaluatePrerequisiteFlagCondition(condition.prerequisiteFlagCondition)
            }
            // else throw Some exception here?
            if (!conditionsEvaluationResult) {
                // TODO no match for the TR. LOG and go to the next one?
                // TODO this should be return from a condEvalMethod
                return false
            }
        }
        return conditionsEvaluationResult
    }

    private fun evaluateSegmentCondition(
        segmentCondition: SegmentCondition
    ): Boolean {
        // TODO implement
        return true
    }

    private fun evaluatePrerequisiteFlagCondition(
        prerequisiteFlagCondition: PrerequisiteFlagCondition
    ): Boolean {
        // TODO implement
        return true
    }

    private fun evaluateComparisonCondition(
        condition: ComparisonCondition,
        configSalt: String,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): Boolean {
        val comparisonAttribute = condition.comparisonAttribute
        val userValue = user.attributeFor(comparisonAttribute)
        val comparator = condition.comparator.toComparatorOrNull()

        if (comparator == null) {
            // TODO add log
            return false
        }
        if (userValue.isNullOrEmpty()) {
            // evaluatorLogger.logNoMatch(
            // TODO add log
            return false
        }

        when (comparator) {
            Comparator.CONTAINS_ANY_OF,
            Comparator.NOT_CONTAINS_ANY_OF -> {
                return processContains(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.ONE_OF_SEMVER,
            Comparator.NOT_ONE_OF_SEMVER -> {
                return processSemverOneOf(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.LT_SEMVER,
            Comparator.LTE_SEMVER,
            Comparator.GT_SEMVER,
            Comparator.GTE_SEMVER -> {
                return processSemverCompare(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.EQ_NUM,
            Comparator.NOT_EQ_NUM,
            Comparator.LT_NUM,
            Comparator.LTE_NUM,
            Comparator.GT_NUM,
            Comparator.GTE_NUM -> {
                return processNumber(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.ONE_OF_SENS,
            Comparator.NOT_ONE_OF_SENS -> {
                return processSensitiveOneOf(condition, userValue, configSalt, key, evaluatorLogger, comparator)
            }

            Comparator.DATE_BEFORE,
            Comparator.DATE_AFTER -> {
                return processDateCompare(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.HASHED_EQUALS,
            Comparator.HASHED_NOT_EQUALS -> {
                return processHashedEqualsCompare(condition, userValue, configSalt, key, evaluatorLogger, comparator)
            }

            Comparator.HASHED_STARTS_WITH,
            Comparator.HASHED_NOT_STARTS_WITH,
            Comparator.HASHED_ENDS_WITH,
            Comparator.HASHED_NOT_ENDS_WITH -> {
                return processHashedStartEndsWithCompare(
                    condition,
                    userValue,
                    configSalt,
                    key,
                    evaluatorLogger,
                    comparator
                )
            }

            Comparator.HASHED_ARRAY_CONTAINS,
            Comparator.HASHED_ARRAY_NOT_CONTAINS -> {
                return processHashedArrayContainsCompare(
                    condition,
                    userValue,
                    configSalt,
                    key,
                    evaluatorLogger,
                    comparator
                )
            }
        }
    }

    private fun processContains(
        condition: ComparisonCondition,
        userValue: String,
        // TODO remove logger?
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        val values = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var matched = false
        for (value in values) {
            matched = userValue.contains(value)
        }
        if ((matched && comparator == Comparator.CONTAINS_ANY_OF) ||
            (!matched && comparator == Comparator.NOT_CONTAINS_ANY_OF)
        ) {
            return true
        }
        return false
    }

    private fun processSemverOneOf(
        condition: ComparisonCondition,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {
            val userVersion = userValue.toVersion()
            val values = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
            var matched = false
            for (value in values) {
                matched = value.toVersion() == userVersion || matched
            }
            if ((matched && comparator == Comparator.ONE_OF_SEMVER) ||
                (!matched && comparator == Comparator.NOT_ONE_OF_SEMVER)
            ) {
                return true
            }
        } catch (e: VersionFormatException) {
            // TODO add log
        }
        return false
    }

    private fun processSemverCompare(
        condition: ComparisonCondition,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {
            val userVersion = userValue.trim().toVersion()
            val comparisonVersion = let { condition.stringValue ?: "" }.trim().toVersion()
            return when (comparator) {
                Comparator.LT_SEMVER -> userVersion < comparisonVersion
                Comparator.LTE_SEMVER -> userVersion <= comparisonVersion
                Comparator.GT_SEMVER -> userVersion > comparisonVersion
                Comparator.GTE_SEMVER -> userVersion >= comparisonVersion
                else -> false
            }
        } catch (e: VersionFormatException) {
            // TODO log error
        }
        return false
    }

    private fun processNumber(
        condition: ComparisonCondition,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {
            val userNumber = userValue.trim().replace(",", ".").toDouble()
            val comparisonNumber = condition.doubleValue
                ?: throw NumberFormatException()
            return when (comparator) {
                Comparator.EQ_NUM -> userNumber == comparisonNumber
                Comparator.NOT_EQ_NUM -> userNumber != comparisonNumber
                Comparator.LT_NUM -> userNumber < comparisonNumber
                Comparator.LTE_NUM -> userNumber <= comparisonNumber
                Comparator.GT_NUM -> userNumber > comparisonNumber
                Comparator.GTE_NUM -> userNumber >= comparisonNumber
                else -> false
            }
        } catch (e: NumberFormatException) {
            // TODO log error
            evaluatorLogger.logFormatError(
                condition.comparisonAttribute,
                userValue,
                comparator,
                condition.doubleValue.toString(),
                NumberFormatException()
            )
        }
        return false
    }

    private fun processSensitiveOneOf(
        condition: ComparisonCondition,
        userValue: String,
        configSalt: String,
        key: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val split = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val userValueHash = getSaltedUserValue(userValue, configSalt, key)
        return when (comparator) {
            Comparator.ONE_OF_SENS -> split.contains(userValueHash)
            Comparator.NOT_ONE_OF_SENS -> !split.contains(userValueHash)
            else -> false
        }
    }

    private fun processDateCompare(
        condition: ComparisonCondition,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {
            val userDateDouble = userValue.trim().replace(",", ".").toDouble()
            val comparisonDateDouble = condition.doubleValue ?: throw NumberFormatException()
            return when (comparator) {
                Comparator.DATE_BEFORE -> userDateDouble < comparisonDateDouble
                Comparator.DATE_AFTER -> userDateDouble > comparisonDateDouble
                else -> false
            }
        } catch (e: NumberFormatException) {
            // TODO add date specific error '{userAttributeValue}' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)
        }
        return false
    }

    private fun processHashedEqualsCompare(
        condition: ComparisonCondition,
        userValue: String,
        configSalt: String,
        key: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val userValueHash = getSaltedUserValue(userValue, configSalt, key)
        val comparisonValue = condition.stringValue
        return when (comparator) {
            Comparator.HASHED_EQUALS -> userValueHash == comparisonValue
            Comparator.HASHED_NOT_EQUALS -> userValueHash != comparisonValue
            else -> false
        }
    }

    private fun processHashedStartEndsWithCompare(
        condition: ComparisonCondition,
        userValue: String,
        configSalt: String,
        key: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var matchCondition = false
        for (comparisonValueHashedStartsEnds in withValuesSplit) {
            try {
                val comparedTextLength = comparisonValueHashedStartsEnds.substringBeforeLast("_")
                if (comparedTextLength == comparisonValueHashedStartsEnds) {
                    return false
                }
                val comparedTextLengthInt: Int = comparedTextLength.toInt()
                val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfterLast("_")
                if (comparisonHashValue.isEmpty()) {
                    return false
                }
                val userValueHashed =
                    if (comparator == Comparator.HASHED_STARTS_WITH || comparator == Comparator.HASHED_NOT_STARTS_WITH) {
                        getSaltedUserValue(userValue.substring(0, comparedTextLengthInt), configSalt, key)
                    } else {
                        // Comparator.HASHED_ENDS_WITH, Comparator.HASHED_NOT_ENDS_WITH
                        getSaltedUserValue(
                            userValue.substring(userValue.length - comparedTextLengthInt),
                            configSalt,
                            key
                        )
                    }
                if (userValueHashed == comparisonHashValue) {
                    matchCondition = true
                }
            } catch (e: NumberFormatException) {
                // TODO log error
                return false
            }
        }
        if (comparator == Comparator.HASHED_NOT_STARTS_WITH || comparator == Comparator.HASHED_NOT_ENDS_WITH) {
            // negate the match in case of NOT ANY OF
            matchCondition = !matchCondition
        }
        return matchCondition
    }

    private fun processHashedArrayContainsCompare(
        condition: ComparisonCondition,
        userValue: String,
        configSalt: String,
        key: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val userCSVNotContainsHashSplit = userValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (userCSVNotContainsHashSplit.isEmpty()) {
            return false
        }
        var contains = false
        userCSVNotContainsHashSplit.forEach {
            val hashedUserValue = getSaltedUserValue(it, configSalt, key)
            contains = hashedUserValue == condition.stringValue
        }
        return when (comparator) {
            Comparator.HASHED_EQUALS -> contains
            Comparator.HASHED_NOT_EQUALS -> !contains
            else -> false
        }
    }

    private fun getSaltedUserValue(userValue: String, configSalt: String, key: String): String {
        val value = userValue + configSalt + key
        return value.encodeToByteArray().sha256().hex
    }

    private fun evaluatePercentageOptions(
        setting: Setting,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        if (setting.percentageOptions.isNullOrEmpty()) {
            return null
        }

        val hashCandidate = "$key${user.identifier}"
        val hash = hashCandidate.encodeToByteArray().sha1().hex.substring(0, 7)
        val numberRepresentation = hash.toInt(radix = 16)
        val scale = numberRepresentation % 100

        var bucket = 0.0
        for (rule in setting.percentageOptions) {
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
            "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => match, returning: $value"
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
            "Evaluating rule: [$attribute:$userValue (${userValue.toDateTimeUTCString()})] [${comparator.value}] [$comparisonValue (${comparisonValue.toDateTimeUTCString()})] => match, returning: $value"
        )
    }

    fun logNoMatch(
        attribute: String,
        userValue: String,
        comparator: Evaluator.Comparator,
        comparisonValue: String
    ) {
        entries.appendLine(
            "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => no match"
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
            "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => SKIP rule. Validation error: ${error.message}"
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
