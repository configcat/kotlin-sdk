package com.configcat

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
                Comparator.ONE_OF,
                Comparator.NOT_ONE_OF -> {
                    val value = processOneOf(rule, userValue, evaluatorLogger, comparator)
                    if (value != null) return value
                }

                Comparator.CONTAINS,
                Comparator.NOT_CONTAINS -> {
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
            }
        }
        return null
    }

    private fun processOneOf(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        val matchCondition = when (comparator) {
            Comparator.ONE_OF -> split.contains(userValue)
            Comparator.NOT_ONE_OF -> !split.contains(userValue)
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

    private fun processContains(
        rule: RolloutRule,
        userValue: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): EvaluationResult? {
        val matchCondition = when (comparator) {
            Comparator.CONTAINS -> userValue.contains(rule.comparisonValue)
            Comparator.NOT_CONTAINS -> !userValue.contains(rule.comparisonValue)
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
            val userVersion = userValue.toVersion()
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
            val userNumber = userValue.replace(",", ".").toDouble()
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

    private fun processPercentageRules(
        setting: Setting,
        user: ConfigCatUser,
        key: String,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
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

    enum class Comparator(val value: String) {
        ONE_OF("IS ONE OF"),
        NOT_ONE_OF("IS NOT ONE OF"),
        CONTAINS("CONTAINS"),
        NOT_CONTAINS("DOES NOT CONTAIN"),
        ONE_OF_SEMVER("IS ONE OF (SemVer)"),
        NOT_ONE_OF_SEMVER("IS NOT ONE OF (SemVer)"),
        LT_SEMVER("< (SemVer)"),
        LTE_SEMVER("<= (SemVer)"),
        GT_SEMVER("> (SemVer)"),
        GTE_SEMVER(">= (SemVer)"),
        EQ_NUM("= (Number)"),
        NOT_EQ_NUM("<> (Number)"),
        LT_NUM("< (Number)"),
        LTE_NUM("<= (Number)"),
        GT_NUM("> (Number)"),
        GTE_NUM(">= (Number)"),
        ONE_OF_SENS("IS ONE OF (Sensitive)"),
        NOT_ONE_OF_SENS("IS NOT ONE OF (Sensitive)")
    }

    private fun Int.toComparatorOrNull(): Comparator? = Comparator.values().firstOrNull { it.ordinal == this }
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

    fun logNoMatch(
        attribute: String,
        userValue: String,
        comparator: Evaluator.Comparator,
        comparisonValue: String
    ) {
        entries.appendLine("Evaluating rule: " +
                "[$attribute:$userValue] [${comparator.value}] [$comparisonValue] => no match")
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
