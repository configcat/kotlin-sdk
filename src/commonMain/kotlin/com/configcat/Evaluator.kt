package com.configcat

import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.log.LogHelper
import com.configcat.model.*
import com.soywiz.krypto.sha1
import com.soywiz.krypto.sha256
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion

internal data class EvaluationResult(
    val value: SettingsValue,
    val variationId: String?,
    val targetingRule: TargetingRule? = null,
    val percentageRule: PercentageOption? = null
)

internal data class EvaluationContext(
    val key: String,
    val user: ConfigCatUser?,
    val visitedKeys: ArrayList<String>?,
    val settings: Map<String, Setting>?,
    var isUserMissing: Boolean = false,
    var isUserAttributeMissing: Boolean = false
)

internal object ComparatorHelp {
    fun Int.toComparatorOrNull(): Evaluator.Comparator? = Evaluator.Comparator.values().firstOrNull { it.id == this }
    fun Int.toPrerequisiteComparatorOrNull(): Evaluator.PrerequisiteComparator? =
        Evaluator.PrerequisiteComparator.values().firstOrNull { it.id == this }

    fun Int.toSegmentComparatorOrNull(): Evaluator.SegmentComparator? =
        Evaluator.SegmentComparator.values().firstOrNull { it.id == this }
}

@Suppress("LargeClass")
internal class Evaluator(private val logger: InternalLogger) {

    fun evaluate(
        setting: Setting,
        key: String,
        user: ConfigCatUser?,
        settings: Map<String, Setting>?,
        evaluateLogger: EvaluateLogger?
    ): EvaluationResult {
        try {
            evaluateLogger?.logEvaluation(key)
            if (user != null) {
                evaluateLogger?.logUserObject(user)
            }
            evaluateLogger?.increaseIndentLevel()

            val context = EvaluationContext(key, user, null, settings)

            val evaluationResult = evaluateSetting(setting, evaluateLogger, context)

            evaluateLogger?.logReturnValue(evaluationResult.value)
            evaluateLogger?.decreaseIndentLevel()
            return evaluationResult
        } finally {
            if (evaluateLogger != null) {
                logger.info(5000, evaluateLogger.print())
            }
        }
    }

    private fun evaluateSetting(
        setting: Setting,
        evaluateLogger: EvaluateLogger?,
        context: EvaluationContext
    ): EvaluationResult {
        var evaluationResult: EvaluationResult? = null
        if (setting.targetingRules != null) {
            evaluationResult = evaluateTargetingRules(setting, context, evaluateLogger)
        }
        if (evaluationResult == null && !setting.percentageOptions.isNullOrEmpty()) {
            evaluationResult = evaluatePercentageOptions(
                setting.percentageOptions,
                setting.percentageAttribute,
                context,
                null,
                evaluateLogger
            )
        }
        if (evaluationResult == null) {
            evaluationResult = EvaluationResult(setting.settingsValue, setting.variationId)
        }
        return evaluationResult
    }

    @Suppress("LoopWithTooManyJumpStatements")
    private fun evaluateTargetingRules(
        setting: Setting,
        context: EvaluationContext,
        evaluateLogger: EvaluateLogger?
    ): EvaluationResult? {
        if (setting.targetingRules.isNullOrEmpty()) {
            return null
        }
        evaluateLogger?.logTargetingRules()
        for (rule: TargetingRule in setting.targetingRules) {
            var servedValue: SettingsValue? = null
            if (rule.servedValue != null) {
                servedValue = rule.servedValue.value
            }
            val evaluateConditionsResult: Boolean = evaluateConditions(
                (rule.conditions ?: arrayOf()) as Array<Any>,
                rule,
                setting.configSalt,
                context.key,
                context,
                setting.segments,
                evaluateLogger
            )
            if (!evaluateConditionsResult) {
                continue
            }

            if (servedValue != null) {
                return rule.servedValue?.let { EvaluationResult(it.value, rule.servedValue.variationId, rule, null) }
            }
            if (rule.percentageOptions.isNullOrEmpty()) {
                continue
            }
            evaluateLogger?.increaseIndentLevel()
            val evaluatePercentageOptions = evaluatePercentageOptions(
                rule.percentageOptions,
                setting.percentageAttribute,
                context,
                rule,
                evaluateLogger
            )
            evaluateLogger?.decreaseIndentLevel()

            if (evaluatePercentageOptions == null) {
                evaluateLogger?.logTargetingRuleIgnored()
                continue
            }
            return evaluatePercentageOptions
        }
        return null
    }

    @Suppress("NestedBlockDepth", "CyclomaticComplexMethod", "LongMethod", "LongParameterList")
    private fun evaluateConditions(
        conditions: Array<Any>,
        targetingRule: TargetingRule?,
        configSalt: String,
        contextSalt: String,
        context: EvaluationContext,
        segments: Array<Segment>,
        evaluateLogger: EvaluateLogger?
    ): Boolean {
        // Conditions are ANDs so if One is not matching return false, if all matching return true
        var firstConditionFlag = true
        var conditionsEvaluationResult = false
        var error: String? = null
        var newLine = false
        for (i in conditions.indices) {
            val rawCondition = conditions.get(i)
            if (firstConditionFlag) {
                firstConditionFlag = false
                evaluateLogger?.newLine()
                evaluateLogger?.append("- IF ")
                evaluateLogger?.increaseIndentLevel()
            } else {
                evaluateLogger?.increaseIndentLevel()
                evaluateLogger?.newLine()
                evaluateLogger?.append("AND ")
            }

            if (targetingRule == null) {
                try {
                    conditionsEvaluationResult = evaluateUserCondition(
                        rawCondition as UserCondition,
                        configSalt,
                        context,
                        contextSalt,
                        evaluateLogger
                    )
                } catch (evaluatorException: RolloutEvaluatorException) {
                    error = evaluatorException.message
                    conditionsEvaluationResult = false
                }
                newLine = conditions.size > 1
            } else {
                val condition = rawCondition as Condition
                if (condition.userCondition != null) {
                    try {
                        conditionsEvaluationResult = evaluateUserCondition(
                            condition.userCondition,
                            configSalt,
                            context,
                            context.key,
                            evaluateLogger
                        )
                    } catch (evaluatorException: RolloutEvaluatorException) {
                        error = evaluatorException.message
                        conditionsEvaluationResult = false
                    }
                    newLine = conditions.size > 1
                } else if (condition.segmentCondition != null) {
                    try {
                        conditionsEvaluationResult = evaluateSegmentCondition(
                            condition.segmentCondition,
                            context,
                            configSalt,
                            segments,
                            evaluateLogger
                        )
                    } catch (evaluatorException: RolloutEvaluatorException) {
                        error = evaluatorException.message
                        conditionsEvaluationResult = false
                    }
                    newLine = error == null || conditions.size > 1
                } else if (condition.prerequisiteFlagCondition != null) {
                    try {
                        conditionsEvaluationResult = evaluatePrerequisiteFlagCondition(
                            condition.prerequisiteFlagCondition,
                            context,
                            evaluateLogger
                        )
                    } catch (evaluatorException: RolloutEvaluatorException) {
                        error = evaluatorException.message
                        conditionsEvaluationResult = false
                    }
                    newLine = error == null || conditions.size > 1
                }
            }
            if (targetingRule == null || conditions.size > 1) {
                evaluateLogger?.logConditionConsequence(conditionsEvaluationResult)
            }
            evaluateLogger?.decreaseIndentLevel()
            if (!conditionsEvaluationResult) {
                break
            }
        }
        if (targetingRule != null) {
            evaluateLogger?.logTargetingRuleConsequence(targetingRule, error, conditionsEvaluationResult, newLine)
        }
        if (error != null) {
            evaluateLogger?.logTargetingRuleIgnored()
        }
        return conditionsEvaluationResult
    }

    private fun evaluateSegmentCondition(
        segmentCondition: SegmentCondition,
        context: EvaluationContext,
        configSalt: String,
        segments: Array<Segment>,
        evaluateLogger: EvaluateLogger?
    ): Boolean {
        val segmentIndex: Int = segmentCondition.segmentIndex
        var segment: Segment? = null
        if (segmentIndex < segments.size) {
            segment = segments[segmentIndex]
        }
        evaluateLogger?.append(LogHelper.formatSegmentFlagCondition(segmentCondition, segment))

        if (context.user == null) {
            if (!context.isUserMissing) {
                context.isUserMissing = true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            throw RolloutEvaluatorException("cannot evaluate, User Object is missing")
        }

        require(segment != null) { "Segment reference is invalid." }

        val segmentName: String? = segment.name
        require(!segmentName.isNullOrEmpty()) { "Segment name is missing." }

        evaluateLogger?.logSegmentEvaluationStart(segmentName)
        @Suppress("SwallowedException")
        val segmentRulesResult = try {
            evaluateConditions(
                segment.segmentRules as Array<Any>,
                null,
                configSalt,
                segmentName,
                context,
                segments,
                evaluateLogger
            )
        } catch (evaluatorException: RolloutEvaluatorException) {
            false
        }

        val segmentComparator = segmentCondition.segmentComparator.toSegmentComparatorOrNull()
            ?: throw IllegalArgumentException("Segment comparison operator is invalid.")
        var result = segmentRulesResult
        if (SegmentComparator.IS_NOT_IN_SEGMENT == segmentComparator) {
            result = !result
        }
        evaluateLogger?.logSegmentEvaluationResult(segmentCondition, segment, result, segmentRulesResult)

        return result
    }

    private fun evaluatePrerequisiteFlagCondition(
        prerequisiteFlagCondition: PrerequisiteFlagCondition,
        context: EvaluationContext,
        evaluateLogger: EvaluateLogger?
    ): Boolean {
        evaluateLogger?.append(LogHelper.formatPrerequisiteFlagCondition(prerequisiteFlagCondition))

        val prerequisiteFlagKey: String? = prerequisiteFlagCondition.prerequisiteFlagKey
        val prerequisiteFlagSetting = context.settings?.get(prerequisiteFlagKey)
        require(!prerequisiteFlagKey.isNullOrEmpty() && prerequisiteFlagSetting != null) {
            "Prerequisite flag key is missing or invalid."
        }
        var visitedKeys: ArrayList<String>? = context.visitedKeys
        if (visitedKeys == null) {
            visitedKeys = ArrayList()
        }
        visitedKeys.add(context.key)
        if (visitedKeys.contains(prerequisiteFlagKey)) {
            val dependencyCycle: String =
                LogHelper.formatCircularDependencyList(visitedKeys, prerequisiteFlagKey)
            logger.warning(
                3005,
                ConfigCatLogMessages.getCircularDependencyDetected(
                    context.key,
                    prerequisiteFlagCondition,
                    dependencyCycle
                )
            )
            throw RolloutEvaluatorException("cannot evaluate, circular dependency detected")
        }
        evaluateLogger?.logPrerequisiteFlagEvaluationStart(prerequisiteFlagKey)

        val prerequisiteFlagContext = EvaluationContext(
            prerequisiteFlagKey,
            context.user,
            visitedKeys,
            context.settings
        )

        val evaluateResult = evaluateSetting(
            prerequisiteFlagSetting,
            evaluateLogger,
            prerequisiteFlagContext
        )

        val prerequisiteComparator = prerequisiteFlagCondition.prerequisiteComparator.toPrerequisiteComparatorOrNull()
            ?: throw IllegalArgumentException("Prerequisite Flag comparison operator is invalid.")
        val conditionValue: SettingsValue? = prerequisiteFlagCondition.value
        var result = conditionValue == evaluateResult.value

        if (PrerequisiteComparator.NOT_EQUALS == prerequisiteComparator) {
            result = !result
        }
        evaluateLogger?.logPrerequisiteFlagEvaluationResult(prerequisiteFlagCondition, evaluateResult.value, result)

        return result
    }

    @Suppress("ThrowsCount", "ReturnCount", "CyclomaticComplexMethod", "LongMethod")
    private fun evaluateUserCondition(
        condition: UserCondition,
        configSalt: String,
        context: EvaluationContext,
        contextSalt: String,
        evaluateLogger: EvaluateLogger?
    ): Boolean {
        evaluateLogger?.append(LogHelper.formatUserCondition(condition))
        if (context.user == null) {
            if (!context.isUserMissing) {
                context.isUserMissing = true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            throw RolloutEvaluatorException("cannot evaluate, User Object is missing")
        }
        val comparisonAttribute = condition.comparisonAttribute
        val userValue = context.user.attributeFor(comparisonAttribute)
        val comparator = condition.comparator.toComparatorOrNull()
            ?: throw IllegalArgumentException("Comparison operator is invalid.")

        if (userValue.isNullOrEmpty()) {
            logger.warning(
                3003,
                ConfigCatLogMessages.getUserAttributeMissing(context.key, condition, comparisonAttribute)
            )
            throw RolloutEvaluatorException("cannot evaluate, the User.$comparisonAttribute attribute is missing")
        }

        when (comparator) {
            Comparator.CONTAINS_ANY_OF,
            Comparator.NOT_CONTAINS_ANY_OF -> {
                return processContains(condition, userValue, comparator)
            }

            Comparator.ONE_OF_SEMVER,
            Comparator.NOT_ONE_OF_SEMVER -> {
                @Suppress("SwallowedException")
                return try {
                    val userVersion = userValue.toVersion()
                    processSemVerOneOf(condition, userVersion, comparator)
                } catch (e: VersionFormatException) {
                    val reason = "'$userValue' is not a valid semantic version"
                    logger.warning(
                        3004,
                        ConfigCatLogMessages.getUserAttributeInvalid(
                            context.key,
                            condition,
                            reason,
                            comparisonAttribute
                        )
                    )
                    throw RolloutEvaluatorException(
                        "cannot evaluate, the User.$comparisonAttribute attribute is " +
                            "invalid ($reason)"
                    )
                }
            }

            Comparator.LT_SEMVER,
            Comparator.LTE_SEMVER,
            Comparator.GT_SEMVER,
            Comparator.GTE_SEMVER -> {
                @Suppress("SwallowedException")
                return try {
                    val userVersion = userValue.toVersion()
                    processSemVerCompare(condition, userVersion, comparator)
                } catch (e: VersionFormatException) {
                    val reason = "'$userValue' is not a valid semantic version"
                    logger.warning(
                        3004,
                        ConfigCatLogMessages.getUserAttributeInvalid(
                            context.key,
                            condition,
                            reason,
                            comparisonAttribute
                        )
                    )
                    throw RolloutEvaluatorException(
                        "cannot evaluate, the User.$comparisonAttribute attribute is " +
                            "invalid ($reason)"
                    )
                }
            }

            Comparator.EQ_NUM,
            Comparator.NOT_EQ_NUM,
            Comparator.LT_NUM,
            Comparator.LTE_NUM,
            Comparator.GT_NUM,
            Comparator.GTE_NUM -> {
                return try {
                    val userNumber = userValue.trim().replace(",", ".").toDouble()
                    processNumber(condition, userNumber, comparator)
                } catch (e: NumberFormatException) {
                    val reason = "'$userValue' is not a valid decimal number"
                    logger.warning(
                        3004,
                        ConfigCatLogMessages.getUserAttributeInvalid(
                            context.key,
                            condition,
                            reason,
                            comparisonAttribute
                        )
                    )
                    throw RolloutEvaluatorException(
                        "cannot evaluate, the User.$comparisonAttribute attribute is " +
                            "invalid ($reason)"
                    )
                }
            }

            Comparator.ONE_OF_SENS,
            Comparator.NOT_ONE_OF_SENS -> {
                return processSensitiveOneOf(condition, userValue, configSalt, contextSalt, comparator)
            }

            Comparator.DATE_BEFORE,
            Comparator.DATE_AFTER -> {
                return try {
                    val userDateDouble = userValue.trim().replace(",", ".").toDouble()
                    processDateCompare(condition, userDateDouble, comparator)
                } catch (e: NumberFormatException) {
                    val reason =
                        "'$userValue' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)"
                    logger.warning(
                        3004,
                        ConfigCatLogMessages.getUserAttributeInvalid(
                            context.key,
                            condition,
                            reason,
                            comparisonAttribute
                        )
                    )
                    throw RolloutEvaluatorException(
                        "cannot evaluate, the User.$comparisonAttribute attribute is " +
                            "invalid ($reason)"
                    )
                }
            }

            Comparator.HASHED_EQUALS,
            Comparator.HASHED_NOT_EQUALS -> {
                return processHashedEqualsCompare(condition, userValue, configSalt, contextSalt, comparator)
            }

            Comparator.HASHED_STARTS_WITH,
            Comparator.HASHED_NOT_STARTS_WITH,
            Comparator.HASHED_ENDS_WITH,
            Comparator.HASHED_NOT_ENDS_WITH -> {
                return processHashedStartEndsWithCompare(
                    condition,
                    userValue,
                    configSalt,
                    contextSalt,
                    comparator
                )
            }

            Comparator.HASHED_ARRAY_CONTAINS,
            Comparator.HASHED_ARRAY_NOT_CONTAINS -> {
                return processHashedArrayContainsCompare(
                    condition,
                    userValue,
                    configSalt,
                    contextSalt,
                    comparator
                )
            }
        }
    }

    private fun processContains(
        condition: UserCondition,
        userValue: String,
        comparator: Comparator
    ): Boolean {
        val values = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var matched = false
        for (value in values) {
            matched = userValue.contains(value)
            if (matched) {
                break
            }
        }
        if ((matched && comparator == Comparator.CONTAINS_ANY_OF) ||
            (!matched && comparator == Comparator.NOT_CONTAINS_ANY_OF)
        ) {
            return true
        }
        return false
    }

    private fun processSemVerOneOf(
        condition: UserCondition,
        userVersion: Version,
        comparator: Comparator
    ): Boolean {
        @Suppress("SwallowedException")
        try {
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
            // NOTE: Previous versions of the evaluation algorithm ignored invalid comparison values.
            // We keep this behavior for backward compatibility.
            return false
        }
        return false
    }

    private fun processSemVerCompare(
        condition: UserCondition,
        userVersion: Version,
        comparator: Comparator
    ): Boolean {
        @Suppress("SwallowedException")
        return try {
            val comparisonVersion = let { condition.stringValue ?: "" }.trim().toVersion()
            when (comparator) {
                Comparator.LT_SEMVER -> userVersion < comparisonVersion
                Comparator.LTE_SEMVER -> userVersion <= comparisonVersion
                Comparator.GT_SEMVER -> userVersion > comparisonVersion
                Comparator.GTE_SEMVER -> userVersion >= comparisonVersion
                else -> false
            }
        } catch (e: VersionFormatException) {
            // NOTE: Previous versions of the evaluation algorithm ignored invalid comparison values.
            // We keep this behavior for backward compatibility.
            false
        }
    }

    private fun processNumber(
        condition: UserCondition,
        userNumber: Double,
        comparator: Comparator
    ): Boolean {
        val comparisonNumber = condition.doubleValue
            ?: throw IllegalArgumentException("Comparison value is missing or invalid.")

        return when (comparator) {
            Comparator.EQ_NUM -> userNumber == comparisonNumber
            Comparator.NOT_EQ_NUM -> userNumber != comparisonNumber
            Comparator.LT_NUM -> userNumber < comparisonNumber
            Comparator.LTE_NUM -> userNumber <= comparisonNumber
            Comparator.GT_NUM -> userNumber > comparisonNumber
            Comparator.GTE_NUM -> userNumber >= comparisonNumber
            else -> false
        }
    }

    private fun processSensitiveOneOf(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        comparator: Comparator
    ): Boolean {
        val split = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val userValueHash = getSaltedUserValue(userValue, configSalt, contextSalt)
        return when (comparator) {
            Comparator.ONE_OF_SENS -> split.contains(userValueHash)
            Comparator.NOT_ONE_OF_SENS -> !split.contains(userValueHash)
            else -> false
        }
    }

    private fun processDateCompare(
        condition: UserCondition,
        userDateDouble: Double,
        comparator: Comparator
    ): Boolean {
        val comparisonDateDouble =
            condition.doubleValue ?: throw IllegalArgumentException("Comparison value is missing or invalid.")
        return when (comparator) {
            Comparator.DATE_BEFORE -> userDateDouble < comparisonDateDouble
            Comparator.DATE_AFTER -> userDateDouble > comparisonDateDouble
            else -> false
        }
    }

    private fun processHashedEqualsCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        comparator: Comparator
    ): Boolean {
        val userValueHash = getSaltedUserValue(userValue, configSalt, contextSalt)
        val comparisonValue = condition.stringValue
        return when (comparator) {
            Comparator.HASHED_EQUALS -> userValueHash == comparisonValue
            Comparator.HASHED_NOT_EQUALS -> userValueHash != comparisonValue
            else -> false
        }
    }

    private fun processHashedStartEndsWithCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        comparator: Comparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var matchCondition = false
        for (comparisonValueHashedStartsEnds in withValuesSplit) {
            try {
                val comparedTextLength = comparisonValueHashedStartsEnds.substringBeforeLast("_")
                require(comparedTextLength != comparisonValueHashedStartsEnds) {
                    "Comparison value is missing or invalid."
                }
                val comparedTextLengthInt: Int = comparedTextLength.toInt()
                if (userValue.length < comparedTextLengthInt) {
                    continue
                }
                val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfterLast("_")
                require(comparisonHashValue.isNotEmpty()) { "Comparison value is missing or invalid." }
                val userValueHashed =
                    if (comparator == Comparator.HASHED_STARTS_WITH ||
                        comparator == Comparator.HASHED_NOT_STARTS_WITH
                    ) {
                        getSaltedUserValue(userValue.substring(0, comparedTextLengthInt), configSalt, contextSalt)
                    } else {
                        // Comparator.HASHED_ENDS_WITH, Comparator.HASHED_NOT_ENDS_WITH
                        getSaltedUserValue(
                            userValue.substring(userValue.length - comparedTextLengthInt),
                            configSalt,
                            contextSalt
                        )
                    }
                if (userValueHashed == comparisonHashValue) {
                    matchCondition = true
                }
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Comparison value is missing or invalid.")
            }
        }
        if (comparator == Comparator.HASHED_NOT_STARTS_WITH || comparator == Comparator.HASHED_NOT_ENDS_WITH) {
            // negate the match in case of NOT ANY OF
            matchCondition = !matchCondition
        }
        return matchCondition
    }

    private fun processHashedArrayContainsCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        comparator: Comparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val userCSVNotContainsHashSplit = userValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (userCSVNotContainsHashSplit.isEmpty()) {
            return false
        }
        var contains = false
        userCSVNotContainsHashSplit.forEach {
            val hashedUserValue = getSaltedUserValue(it, configSalt, contextSalt)
            if (withValuesSplit.contains(hashedUserValue)) {
                contains = true
            }
        }
        return when (comparator) {
            Comparator.HASHED_ARRAY_CONTAINS -> contains
            Comparator.HASHED_ARRAY_NOT_CONTAINS -> !contains
            else -> false
        }
    }

    private fun getSaltedUserValue(userValue: String, configSalt: String, contextSalt: String): String {
        val value = userValue + configSalt + contextSalt
        return value.encodeToByteArray().sha256().hex
    }

    private fun evaluatePercentageOptions(
        percentageOptions: Array<PercentageOption>?,
        percentageOptionAttribute: String?,
        context: EvaluationContext,
        parentTargetingRule: TargetingRule?,
        evaluateLogger: EvaluateLogger?
    ): EvaluationResult? {
        if (context.user == null) {
            evaluateLogger?.logPercentageOptionUserMissing()
            if (!context.isUserMissing) {
                context.isUserMissing = true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            return null
        }

        val percentageOptionAttributeValue: String?
        var percentageOptionAttributeName = percentageOptionAttribute
        if (percentageOptionAttributeName.isNullOrEmpty()) {
            percentageOptionAttributeName = "Identifier"
            percentageOptionAttributeValue = context.user.identifier
        } else {
            percentageOptionAttributeValue = context.user.attributeFor(percentageOptionAttributeName)
            if (percentageOptionAttributeValue == null) {
                evaluateLogger?.logPercentageOptionUserAttributeMissing(percentageOptionAttributeName)
                if (!context.isUserAttributeMissing) {
                    context.isUserAttributeMissing = true
                    this.logger.warning(
                        3003,
                        ConfigCatLogMessages.getUserAttributeMissing(context.key, percentageOptionAttributeName)
                    )
                }
                return null
            }
        }
        evaluateLogger?.logPercentageOptionEvaluation(percentageOptionAttributeName)

        val hashCandidate = "${context.key}$percentageOptionAttributeValue"
        val hash = hashCandidate.encodeToByteArray().sha1().hex.substring(0, 7)
        val numberRepresentation = hash.toInt(radix = 16)
        val scale = numberRepresentation % 100
        evaluateLogger?.logPercentageOptionEvaluationHash(percentageOptionAttributeName, scale)

        if (percentageOptions.isNullOrEmpty()) {
            return null
        }

        var bucket = 0.0
        for (i in percentageOptions.indices) {
            val rule = percentageOptions[i]
            bucket += rule.percentage
            if (scale < bucket) {
                evaluateLogger?.logPercentageEvaluationReturnValue(scale, i, rule.percentage, rule.value)
                return EvaluationResult(rule.value, rule.variationId, parentTargetingRule, rule)
            }
        }

        return null
    }

    /**
     * Describes the Rollout Evaluator User Condition Comparators.
     */
    enum class Comparator(val id: Int, val value: String) {
        CONTAINS_ANY_OF(2, "CONTAINS ANY OF"),
        NOT_CONTAINS_ANY_OF(3, "NOT CONTAINS ANY OF"),
        ONE_OF_SEMVER(4, "IS ONE OF"),
        NOT_ONE_OF_SEMVER(5, "IS NOT ONE OF"),
        LT_SEMVER(6, "<"),
        LTE_SEMVER(7, "<="),
        GT_SEMVER(8, ">"),
        GTE_SEMVER(9, ">="),
        EQ_NUM(10, "="),
        NOT_EQ_NUM(11, "!="),
        LT_NUM(12, "<"),
        LTE_NUM(13, "<="),
        GT_NUM(14, ">"),
        GTE_NUM(15, ">="),
        ONE_OF_SENS(16, "IS ONE OF"),
        NOT_ONE_OF_SENS(17, "IS NOT ONE OF"),
        DATE_BEFORE(18, "BEFORE"),
        DATE_AFTER(19, "AFTER"),
        HASHED_EQUALS(20, "EQUALS"),
        HASHED_NOT_EQUALS(21, "NOT EQUALS"),
        HASHED_STARTS_WITH(22, "STARTS WITH ANY OF"),
        HASHED_NOT_STARTS_WITH(23, "NOT STARTS WITH ANY OF"),
        HASHED_ENDS_WITH(24, "ENDS WITH ANY OF"),
        HASHED_NOT_ENDS_WITH(25, "NOT ENDS WITH ANY OF"),
        HASHED_ARRAY_CONTAINS(26, "ARRAY CONTAINS ANY OF"),
        HASHED_ARRAY_NOT_CONTAINS(27, "ARRAY NOT CONTAINS ANY OF")
    }

    /**
     * Describes the Prerequisite Comparators.
     */
    enum class PrerequisiteComparator(val id: Int, val value: String) {
        EQUALS(0, "EQUALS"),
        NOT_EQUALS(1, "NOT EQUALS")
    }

    /**
     * Describes the Segment Comparators.
     */
    enum class SegmentComparator(val id: Int, val value: String) {
        IS_IN_SEGMENT(0, "IS IN SEGMENT"),
        IS_NOT_IN_SEGMENT(1, "IS NOT IN SEGMENT")
    }
}

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
        entries.append("\n")
        for (i in 0 until indentLevel) {
            entries.append("  ")
        }
    }

    fun print(): String {
        return entries.toString()
    }

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

    fun logPercentageOptionEvaluationHash(percentageOptionsAttributeName: String, hashValue: Int) {
        newLine()
        append(
            "- Computing hash in the [0..99] range from User.$percentageOptionsAttributeName => " +
                "$hashValue (this value is sticky and consistent across all SDKs)"
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

    fun logTargetingRuleConsequence(targetingRule: TargetingRule?, error: String?, isMatch: Boolean, newLine: Boolean) {
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

    fun logPercentageEvaluationReturnValue(hashValue: Int, i: Int, percentage: Int, settingsValue: SettingsValue?) {
        val percentageOptionValue = settingsValue?.toString() ?: LogHelper.INVALID_VALUE
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
        segmentResult: Boolean
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
            "Condition (${LogHelper.formatSegmentFlagCondition(segmentCondition, segment)}) evaluates to $result."
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
        prerequisiteFlagValue: SettingsValue?,
        result: Boolean
    ) {
        newLine()
        val prerequisiteFlagValueFormat = prerequisiteFlagValue?.toString() ?: LogHelper.INVALID_VALUE
        append("Prerequisite flag evaluation result: '$prerequisiteFlagValueFormat'.")
        newLine()
        append(
            "Condition (${LogHelper.formatPrerequisiteFlagCondition(prerequisiteFlagCondition!!)}) " +
                "evaluates to $result."
        )
        decreaseIndentLevel()
        newLine()
        append(")")
    }
}

internal class RolloutEvaluatorException(message: String?) : Exception(message)
