package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.ConditionAccessor
import com.configcat.model.PercentageOption
import com.configcat.model.PrerequisiteFlagCondition
import com.configcat.model.Segment
import com.configcat.model.SegmentCondition
import com.configcat.model.Setting
import com.configcat.model.SettingType
import com.configcat.model.SettingValue
import com.configcat.model.TargetingRule
import com.configcat.model.UserCondition
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion
import io.github.z4kn4fein.semver.toVersionOrNull
import korlibs.crypto.sha1
import korlibs.crypto.sha256
import korlibs.time.DateTime
import korlibs.time.DateTimeTz
import kotlinx.serialization.encodeToString
import kotlin.math.absoluteValue

internal data class EvaluationResult(
    val value: SettingValue,
    val variationId: String?,
    val matchedTargetingRule: TargetingRule? = null,
    val matchedPercentageOption: PercentageOption? = null,
)

internal data class EvaluationContext(
    val key: String,
    val user: ConfigCatUser?,
    val visitedKeys: ArrayList<String>?,
    val settings: Map<String, Setting>?,
    var isUserMissing: Boolean = false,
    var isUserAttributeMissing: Boolean = false,
)

internal object ComparatorHelp {
    fun Int.toComparatorOrNull(): Evaluator.UserComparator? =
        Evaluator.UserComparator.entries.firstOrNull { it.id == this }

    fun Int.toPrerequisiteComparatorOrNull(): Evaluator.PrerequisiteComparator? =
        Evaluator.PrerequisiteComparator.entries.firstOrNull { it.id == this }

    fun Int.toSegmentComparatorOrNull(): Evaluator.SegmentComparator? =
        Evaluator.SegmentComparator.entries.firstOrNull {
            it.id == this
        }
}

private const val USER_OBJECT_IS_MISSING = "cannot evaluate, User Object is missing"

@Suppress("LargeClass", "TooManyFunctions")
internal class Evaluator(private val logger: InternalLogger) {
    fun evaluate(
        setting: Setting,
        key: String,
        user: ConfigCatUser?,
        settings: Map<String, Setting>?,
        evaluateLogger: EvaluateLogger?,
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
        context: EvaluationContext,
    ): EvaluationResult {
        var evaluationResult: EvaluationResult? = null
        if (!setting.targetingRules.isNullOrEmpty()) {
            evaluationResult = evaluateTargetingRules(setting.targetingRules, setting, context, evaluateLogger)
        }
        if (evaluationResult == null && !setting.percentageOptions.isNullOrEmpty()) {
            evaluationResult =
                evaluatePercentageOptions(
                    setting.percentageOptions,
                    setting.percentageAttribute,
                    context,
                    null,
                    evaluateLogger,
                )
        }
        if (evaluationResult == null) {
            evaluationResult = EvaluationResult(setting.settingValue, setting.variationId)
        }
        return evaluationResult
    }

    @Suppress("LoopWithTooManyJumpStatements", "CyclomaticComplexMethod")
    private fun evaluateTargetingRules(
        targetingRules: Array<TargetingRule>,
        setting: Setting,
        context: EvaluationContext,
        evaluateLogger: EvaluateLogger?,
    ): EvaluationResult? {
        evaluateLogger?.logTargetingRules()
        for (rule: TargetingRule in targetingRules) {
            var evaluateConditionsResult: Boolean
            var error: String? = null
            try {
                evaluateConditionsResult =
                    evaluateConditions(
                        rule.conditionAccessors,
                        rule,
                        setting.configSalt,
                        context.key,
                        context,
                        setting.segments,
                        evaluateLogger,
                    )
            } catch (rolloutEvaluatorException: RolloutEvaluatorException) {
                error = rolloutEvaluatorException.message
                evaluateConditionsResult = false
            }
            if (!evaluateConditionsResult) {
                if (error != null) {
                    evaluateLogger?.logTargetingRuleIgnored()
                }
                continue
            }

            if (rule.servedValue != null) {
                return EvaluationResult(rule.servedValue.value, rule.servedValue.variationId, rule, null)
            }
            if (rule.percentageOptions.isNullOrEmpty()) {
                error("Targeting rule THEN part is missing or invalid.")
            }
            evaluateLogger?.increaseIndentLevel()
            val evaluatePercentageOptions =
                evaluatePercentageOptions(
                    rule.percentageOptions,
                    setting.percentageAttribute,
                    context,
                    rule,
                    evaluateLogger,
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
        conditions: List<ConditionAccessor>,
        targetingRule: TargetingRule?,
        configSalt: String?,
        contextSalt: String,
        context: EvaluationContext,
        segments: Array<Segment>,
        evaluateLogger: EvaluateLogger?,
    ): Boolean {
        var conditionsEvaluationResult = true
        var error: String? = null
        var newLine = false
        for ((index, condition) in conditions.withIndex()) {
            when (index) {
                0 -> {
                    evaluateLogger?.newLine()
                    evaluateLogger?.append("- IF ")
                    evaluateLogger?.increaseIndentLevel()
                }

                else -> {
                    evaluateLogger?.increaseIndentLevel()
                    evaluateLogger?.newLine()
                    evaluateLogger?.append("AND ")
                }
            }

            condition.userCondition?.let { userCondition ->
                try {
                    conditionsEvaluationResult =
                        evaluateUserCondition(
                            userCondition,
                            configSalt,
                            context,
                            contextSalt,
                            evaluateLogger,
                        )
                } catch (evaluatorException: RolloutEvaluatorException) {
                    error = evaluatorException.message
                    conditionsEvaluationResult = false
                }
                newLine = conditions.size > 1
            }

            condition.segmentCondition?.let { segmentCondition ->
                try {
                    conditionsEvaluationResult =
                        evaluateSegmentCondition(
                            segmentCondition,
                            context,
                            configSalt,
                            segments,
                            evaluateLogger,
                        )
                } catch (evaluatorException: RolloutEvaluatorException) {
                    error = evaluatorException.message
                    conditionsEvaluationResult = false
                }
                newLine = error == null || USER_OBJECT_IS_MISSING != error || conditions.size > 1
            }

            condition.prerequisiteFlagCondition?.let { prerequisiteCondition ->
                try {
                    conditionsEvaluationResult =
                        evaluatePrerequisiteFlagCondition(
                            prerequisiteCondition,
                            context,
                            evaluateLogger,
                        )
                } catch (evaluatorException: RolloutEvaluatorException) {
                    error = evaluatorException.message
                    conditionsEvaluationResult = false
                }
                newLine = true
            }

            if (targetingRule == null || conditions.size > 1) {
                evaluateLogger?.logConditionConsequence(conditionsEvaluationResult)
            }
            evaluateLogger?.decreaseIndentLevel()
            if (!conditionsEvaluationResult) {
                break
            }
        }
        targetingRule?.let {
            evaluateLogger?.logTargetingRuleConsequence(targetingRule, error, conditionsEvaluationResult, newLine)
        }
        error?.let {
            throw RolloutEvaluatorException(error)
        }
        return conditionsEvaluationResult
    }

    @Suppress("ThrowsCount")
    private fun evaluateSegmentCondition(
        segmentCondition: SegmentCondition,
        context: EvaluationContext,
        configSalt: String?,
        segments: Array<Segment>,
        evaluateLogger: EvaluateLogger?,
    ): Boolean {
        val segmentIndex: Int = segmentCondition.segmentIndex
        var segment: Segment? = null
        if (0 <= segmentIndex && segmentIndex < segments.size) {
            segment = segments[segmentIndex]
        }
        evaluateLogger?.append(EvaluatorLogHelper.formatSegmentFlagCondition(segmentCondition, segment))

        if (context.user == null) {
            if (!context.isUserMissing) {
                context.isUserMissing = true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            throw RolloutEvaluatorException(USER_OBJECT_IS_MISSING)
        }

        require(segment != null) { "Segment reference is invalid." }

        val segmentName: String? = segment.name
        require(!segmentName.isNullOrEmpty()) { "Segment name is missing." }

        evaluateLogger?.logSegmentEvaluationStart(segmentName)
        var result: Boolean
        @Suppress("SwallowedException")
        try {
            val segmentRulesResult =
                evaluateConditions(
                    segment.conditionAccessors,
                    null,
                    configSalt,
                    segmentName,
                    context,
                    segments,
                    evaluateLogger,
                )

            val segmentComparator =
                segmentCondition.segmentComparator.toSegmentComparatorOrNull()
                    ?: throw IllegalArgumentException("Segment comparison operator is invalid.")
            result = segmentRulesResult
            if (SegmentComparator.IS_NOT_IN_SEGMENT == segmentComparator) {
                result = !result
            }
            evaluateLogger?.logSegmentEvaluationResult(segmentCondition, segment, result, segmentRulesResult)
        } catch (evaluatorException: RolloutEvaluatorException) {
            evaluateLogger?.logSegmentEvaluationError(segmentCondition, segment, evaluatorException.message)
            throw evaluatorException
        }

        return result
    }

    private fun evaluatePrerequisiteFlagCondition(
        prerequisiteFlagCondition: PrerequisiteFlagCondition,
        context: EvaluationContext,
        evaluateLogger: EvaluateLogger?,
    ): Boolean {
        evaluateLogger?.append(EvaluatorLogHelper.formatPrerequisiteFlagCondition(prerequisiteFlagCondition))
        val prerequisiteFlagKey: String? = prerequisiteFlagCondition.prerequisiteFlagKey
        val prerequisiteFlagSetting = context.settings?.get(prerequisiteFlagKey)
        require(!prerequisiteFlagKey.isNullOrEmpty() && prerequisiteFlagSetting != null) {
            "Prerequisite flag key is missing or invalid."
        }

        val settingType = prerequisiteFlagSetting.type.toSettingTypeOrNull()
        require(
            settingType == SettingType.JS_NUMBER &&
                (
                    prerequisiteFlagCondition.value?.doubleValue != null ||
                        prerequisiteFlagCondition.value?.integerValue != null
                ) ||
                settingType == SettingType.BOOLEAN && prerequisiteFlagCondition.value?.booleanValue != null ||
                settingType == SettingType.STRING && prerequisiteFlagCondition.value?.stringValue != null ||
                settingType == SettingType.INT && prerequisiteFlagCondition.value?.integerValue != null ||
                settingType == SettingType.DOUBLE && prerequisiteFlagCondition.value?.doubleValue != null,
        ) {
            "Type mismatch between comparison value '${prerequisiteFlagCondition.value}' and prerequisite flag " +
                "'$prerequisiteFlagKey'."
        }

        val visitedKeys: ArrayList<String> = context.visitedKeys ?: ArrayList()
        visitedKeys.add(context.key)
        if (visitedKeys.contains(prerequisiteFlagKey)) {
            val dependencyCycle: String =
                EvaluatorLogHelper.formatCircularDependencyList(visitedKeys, prerequisiteFlagKey)
            throw IllegalArgumentException(
                "Circular dependency detected between the following depending flags: $dependencyCycle.",
            )
        }
        evaluateLogger?.logPrerequisiteFlagEvaluationStart(prerequisiteFlagKey)

        val prerequisiteFlagContext =
            EvaluationContext(
                prerequisiteFlagKey,
                context.user,
                visitedKeys,
                context.settings,
            )

        val evaluateResult =
            evaluateSetting(
                prerequisiteFlagSetting,
                evaluateLogger,
                prerequisiteFlagContext,
            )
        visitedKeys.removeAt(visitedKeys.size - 1)

        Helpers.validateSettingValueType(evaluateResult.value, prerequisiteFlagSetting.type)

        val prerequisiteComparator =
            prerequisiteFlagCondition.prerequisiteComparator.toPrerequisiteComparatorOrNull()
                ?: throw IllegalArgumentException("Prerequisite Flag comparison operator is invalid.")

        val conditionValue: SettingValue? = prerequisiteFlagCondition.value
        var result = evaluateResult.value.equalsBasedOnSettingType(conditionValue, prerequisiteFlagSetting.type)

        if (PrerequisiteComparator.NOT_EQUALS == prerequisiteComparator) {
            result = !result
        }
        evaluateLogger?.logPrerequisiteFlagEvaluationResult(prerequisiteFlagCondition, evaluateResult.value, result)

        return result
    }

    @Suppress("ThrowsCount", "ReturnCount", "CyclomaticComplexMethod", "LongMethod")
    private fun evaluateUserCondition(
        condition: UserCondition,
        configSalt: String?,
        context: EvaluationContext,
        contextSalt: String,
        evaluateLogger: EvaluateLogger?,
    ): Boolean {
        evaluateLogger?.append(EvaluatorLogHelper.formatUserCondition(condition))
        if (context.user == null) {
            if (!context.isUserMissing) {
                context.isUserMissing = true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            throw RolloutEvaluatorException(USER_OBJECT_IS_MISSING)
        }
        val comparisonAttribute = condition.comparisonAttribute
        val userValue = context.user.attributeFor(comparisonAttribute)
        val comparator =
            condition.comparator.toComparatorOrNull()
                ?: throw IllegalArgumentException("Comparison operator is invalid.")

        if (userValue == null) {
            logger.warning(
                3003,
                ConfigCatLogMessages.getUserAttributeMissing(context.key, condition, comparisonAttribute),
            )
            throw RolloutEvaluatorException("cannot evaluate, the User.$comparisonAttribute attribute is missing")
        }

        when (comparator) {
            UserComparator.CONTAINS_ANY_OF,
            UserComparator.NOT_CONTAINS_ANY_OF,
            -> {
                val negateContainsAnyOf = UserComparator.NOT_CONTAINS_ANY_OF == comparator
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processContains(condition, userAttributeAsString, negateContainsAnyOf)
            }

            UserComparator.ONE_OF_SEMVER,
            UserComparator.NOT_ONE_OF_SEMVER,
            -> {
                val negateSemverIsOneOf: Boolean = UserComparator.NOT_ONE_OF_SEMVER == comparator
                val userAttributeAsVersion =
                    getUserAttributeAsVersion(context.key, condition, comparisonAttribute, userValue)
                return processSemVerOneOf(condition, userAttributeAsVersion, negateSemverIsOneOf)
            }

            UserComparator.LT_SEMVER,
            UserComparator.LTE_SEMVER,
            UserComparator.GT_SEMVER,
            UserComparator.GTE_SEMVER,
            -> {
                val userAttributeAsVersion =
                    getUserAttributeAsVersion(context.key, condition, comparisonAttribute, userValue)
                return processSemVerCompare(condition, userAttributeAsVersion, comparator)
            }

            UserComparator.EQ_NUM,
            UserComparator.NOT_EQ_NUM,
            UserComparator.LT_NUM,
            UserComparator.LTE_NUM,
            UserComparator.GT_NUM,
            UserComparator.GTE_NUM,
            -> {
                val userAttributeAsDouble =
                    getUserAttributeAsDouble(context.key, condition, comparisonAttribute, userValue)
                return processNumber(condition, userAttributeAsDouble, comparator)
            }

            UserComparator.IS_ONE_OF,
            UserComparator.IS_NOT_ONE_OF,
            UserComparator.ONE_OF_SENS,
            UserComparator.NOT_ONE_OF_SENS,
            -> {
                val negateIsOneOf =
                    UserComparator.NOT_ONE_OF_SENS == comparator || UserComparator.IS_NOT_ONE_OF == comparator
                val sensitiveIsOneOf =
                    UserComparator.ONE_OF_SENS == comparator || UserComparator.NOT_ONE_OF_SENS == comparator

                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processSensitiveOneOf(
                    condition,
                    userAttributeAsString,
                    configSalt,
                    contextSalt,
                    negateIsOneOf,
                    sensitiveIsOneOf,
                )
            }

            UserComparator.DATE_BEFORE,
            UserComparator.DATE_AFTER,
            -> {
                val userAttributeForDate = getUserAttributeForDate(condition, context, comparisonAttribute, userValue)
                return processDateCompare(condition, userAttributeForDate, comparator)
            }

            UserComparator.TEXT_EQUALS,
            UserComparator.TEXT_NOT_EQUALS,
            UserComparator.HASHED_EQUALS,
            UserComparator.HASHED_NOT_EQUALS,
            -> {
                val negateEquals =
                    UserComparator.HASHED_NOT_EQUALS == comparator || UserComparator.TEXT_NOT_EQUALS == comparator
                val hashedEquals =
                    UserComparator.HASHED_EQUALS == comparator || UserComparator.HASHED_NOT_EQUALS == comparator

                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processHashedEqualsCompare(
                    condition,
                    userAttributeAsString,
                    configSalt,
                    contextSalt,
                    negateEquals,
                    hashedEquals,
                )
            }

            UserComparator.HASHED_STARTS_WITH,
            UserComparator.HASHED_NOT_STARTS_WITH,
            UserComparator.HASHED_ENDS_WITH,
            UserComparator.HASHED_NOT_ENDS_WITH,
            -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processHashedStartEndsWithCompare(
                    condition,
                    userAttributeAsString,
                    ensureConfigSalt(configSalt),
                    contextSalt,
                    comparator,
                )
            }

            UserComparator.TEXT_STARTS_WITH,
            UserComparator.TEXT_NOT_STARTS_WITH,
            -> {
                val negateTextStartWith = UserComparator.TEXT_NOT_STARTS_WITH == comparator
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processTextStartWithCompare(condition, userAttributeAsString, negateTextStartWith)
            }

            UserComparator.TEXT_ENDS_WITH,
            UserComparator.TEXT_NOT_ENDS_WITH,
            -> {
                val negateTextEndsWith = UserComparator.TEXT_NOT_ENDS_WITH == comparator
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processTextEndWithCompare(condition, userAttributeAsString, negateTextEndsWith)
            }

            UserComparator.TEXT_ARRAY_CONTAINS,
            UserComparator.TEXT_ARRAY_NOT_CONTAINS,
            UserComparator.HASHED_ARRAY_CONTAINS,
            UserComparator.HASHED_ARRAY_NOT_CONTAINS,
            -> {
                val negateArrayContains =
                    UserComparator.HASHED_ARRAY_NOT_CONTAINS == comparator ||
                        UserComparator.TEXT_ARRAY_NOT_CONTAINS == comparator
                val hashedArrayContains =
                    UserComparator.HASHED_ARRAY_CONTAINS == comparator ||
                        UserComparator.HASHED_ARRAY_NOT_CONTAINS == comparator
                val userArrayValue = getUserAttributeAsStringArray(condition, context, comparisonAttribute, userValue)
                return processHashedArrayContainsCompare(
                    condition,
                    userArrayValue,
                    configSalt,
                    contextSalt,
                    negateArrayContains,
                    hashedArrayContains,
                )
            }
        }
    }

    private fun processContains(
        condition: UserCondition,
        userValue: String,
        negate: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        for (containsValue in comparisonValues) {
            if (userValue.contains(ensureComparisonValue(containsValue))) {
                return !negate
            }
        }
        return negate
    }

    private fun processSemVerOneOf(
        condition: UserCondition,
        userVersion: Version,
        negate: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        var matched = false
        for (semVer in comparisonValues) {
            // Previous versions of the evaluation algorithm ignore empty comparison values.
            // We keep this behavior for backward compatibility.
            if (ensureComparisonValue(semVer).isEmpty()) {
                continue
            }
            val comparisonSemVer = semVer.trim().toVersionOrNull()
            matched =
                if (comparisonSemVer == null) {
                    false
                } else {
                    comparisonSemVer == userVersion || matched
                }
        }

        return negate != matched
    }

    private fun processSemVerCompare(
        condition: UserCondition,
        userVersion: Version,
        userComparator: UserComparator,
    ): Boolean {
        val comparisonVersion = ensureComparisonValue(condition.stringValue).trim().toVersionOrNull() ?: return false
        return when (userComparator) {
            UserComparator.LT_SEMVER -> userVersion < comparisonVersion
            UserComparator.LTE_SEMVER -> userVersion <= comparisonVersion
            UserComparator.GT_SEMVER -> userVersion > comparisonVersion
            UserComparator.GTE_SEMVER -> userVersion >= comparisonVersion
            else -> error("Invalid comparator $userComparator.")
        }
    }

    private fun processNumber(
        condition: UserCondition,
        userNumber: Double,
        userComparator: UserComparator,
    ): Boolean {
        val comparisonNumber = ensureComparisonValue(condition.doubleValue)
        return when (userComparator) {
            UserComparator.EQ_NUM -> userNumber == comparisonNumber
            UserComparator.NOT_EQ_NUM -> userNumber != comparisonNumber
            UserComparator.LT_NUM -> userNumber < comparisonNumber
            UserComparator.LTE_NUM -> userNumber <= comparisonNumber
            UserComparator.GT_NUM -> userNumber > comparisonNumber
            UserComparator.GTE_NUM -> userNumber >= comparisonNumber
            else -> error("Invalid comparator $userComparator.")
        }
    }

    private fun processSensitiveOneOf(
        condition: UserCondition,
        userValue: String,
        configSalt: String?,
        contextSalt: String,
        negateIsOneOf: Boolean,
        sensitiveIsOneOf: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        val userIsOneOfValue: String =
            if (sensitiveIsOneOf) {
                getSaltedUserValue(userValue, ensureConfigSalt(configSalt), contextSalt)
            } else {
                userValue
            }

        for (inValuesElement in comparisonValues) {
            if (ensureComparisonValue(inValuesElement) == userIsOneOfValue) {
                return !negateIsOneOf
            }
        }
        return negateIsOneOf
    }

    private fun processDateCompare(
        condition: UserCondition,
        userDateDouble: Double,
        userComparator: UserComparator,
    ): Boolean {
        val comparisonDateDouble =
            ensureComparisonValue(condition.doubleValue)
        return when (userComparator) {
            UserComparator.DATE_BEFORE -> userDateDouble < comparisonDateDouble
            UserComparator.DATE_AFTER -> userDateDouble > comparisonDateDouble
            else -> error("Invalid comparator $userComparator.")
        }
    }

    private fun processHashedEqualsCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String?,
        contextSalt: String,
        negateEquals: Boolean,
        hashedEquals: Boolean,
    ): Boolean {
        val comparisonValue = ensureComparisonValue(condition.stringValue)
        val valueEquals =
            if (hashedEquals) {
                getSaltedUserValue(userValue, ensureConfigSalt(configSalt), contextSalt)
            } else {
                userValue
            }

        return negateEquals != (valueEquals == comparisonValue)
    }

    private fun processHashedStartEndsWithCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        userComparator: UserComparator,
    ): Boolean {
        val withValuesSplit = ensureComparisonValue(condition.stringArrayValue)
        val userValueUTF8 = userValue.encodeToByteArray()
        var matchCondition = false
        @Suppress("LoopWithTooManyJumpStatements")
        for (comparisonValueHashedStartsEnds in withValuesSplit) {
            val comparedTextLength = ensureComparisonValue(comparisonValueHashedStartsEnds).substringBefore("_")
            require(comparedTextLength != comparisonValueHashedStartsEnds) {
                "Comparison value is missing or invalid."
            }
            val comparedTextLengthInt: Int
            try {
                comparedTextLengthInt = comparedTextLength.trim().toInt()
            } catch (e: NumberFormatException) {
                throw IllegalArgumentException("Comparison value is missing or invalid.")
            }
            if (userValueUTF8.size < comparedTextLengthInt) {
                continue
            }
            val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfter("_")
            require(comparisonHashValue.isNotEmpty()) { "Comparison value is missing or invalid." }
            val userValueSlice =
                if (userComparator == UserComparator.HASHED_STARTS_WITH ||
                    userComparator == UserComparator.HASHED_NOT_STARTS_WITH
                ) {
                    userValueUTF8.copyOfRange(0, comparedTextLengthInt)
                } else {
                    // Comparator.HASHED_ENDS_WITH, Comparator.HASHED_NOT_ENDS_WITH
                    userValueUTF8.copyOfRange(userValueUTF8.size - comparedTextLengthInt, userValueUTF8.size)
                }
            val userValueHashed = getSaltedUserValueSlice(userValueSlice, configSalt, contextSalt)
            if (userValueHashed == comparisonHashValue) {
                matchCondition = true
                break
            }
        }
        if (userComparator == UserComparator.HASHED_NOT_STARTS_WITH ||
            userComparator == UserComparator.HASHED_NOT_ENDS_WITH
        ) {
            // negate the match in case of NOT ANY OF
            matchCondition = !matchCondition
        }
        return matchCondition
    }

    private fun processTextStartWithCompare(
        condition: UserCondition,
        userValue: String,
        negateTextStartWith: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        for (textValue in comparisonValues) {
            if (userValue.startsWith(ensureComparisonValue(textValue))) {
                return !negateTextStartWith
            }
        }
        return negateTextStartWith
    }

    private fun processTextEndWithCompare(
        condition: UserCondition,
        userValue: String,
        negateTextEndsWith: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        for (textValue in comparisonValues) {
            if (userValue.endsWith(ensureComparisonValue(textValue))) {
                return !negateTextEndsWith
            }
        }
        return negateTextEndsWith
    }

    private fun processHashedArrayContainsCompare(
        condition: UserCondition,
        userContainsArray: Array<String>,
        configSalt: String?,
        contextSalt: String,
        negateArrayContains: Boolean,
        hashedArrayContains: Boolean,
    ): Boolean {
        val comparisonValues = ensureComparisonValue(condition.stringArrayValue)
        if (userContainsArray.isEmpty()) {
            return negateArrayContains
        }
        for (userContainsValue in userContainsArray) {
            val userContainsValueConverted =
                if (hashedArrayContains) {
                    getSaltedUserValue(
                        userContainsValue,
                        ensureConfigSalt(configSalt),
                        contextSalt,
                    )
                } else {
                    userContainsValue
                }
            for (inValuesElement in comparisonValues) {
                if (ensureComparisonValue(inValuesElement) == userContainsValueConverted) {
                    return !negateArrayContains
                }
            }
        }
        return negateArrayContains
    }

    private fun getSaltedUserValue(
        userValue: String,
        configSalt: String,
        contextSalt: String,
    ): String {
        val value = userValue + configSalt + contextSalt
        return value.encodeToByteArray().sha256().hex
    }

    private fun getSaltedUserValueSlice(
        userValue: ByteArray,
        configSalt: String,
        contextSalt: String,
    ): String {
        val configSaltByteArray = configSalt.encodeToByteArray()
        val contextSaltByteArray = contextSalt.encodeToByteArray()
        val concatByteArray = userValue + configSaltByteArray + contextSaltByteArray
        return concatByteArray.sha256().hex
    }

    private fun evaluatePercentageOptions(
        percentageOptions: Array<PercentageOption>,
        percentageOptionAttribute: String?,
        context: EvaluationContext,
        parentTargetingRule: TargetingRule?,
        evaluateLogger: EvaluateLogger?,
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
        if (percentageOptionAttributeName == null) {
            percentageOptionAttributeName = "Identifier"
            percentageOptionAttributeValue = context.user.identifier
        } else {
            percentageOptionAttributeValue =
                userAttributeToString(context.user.attributeFor(percentageOptionAttributeName))
            if (percentageOptionAttributeValue == null) {
                evaluateLogger?.logPercentageOptionUserAttributeMissing(percentageOptionAttributeName)
                if (!context.isUserAttributeMissing) {
                    context.isUserAttributeMissing = true
                    this.logger.warning(
                        3003,
                        ConfigCatLogMessages.getUserAttributeMissing(context.key, percentageOptionAttributeName),
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

        var bucket = 0.0
        for (i in percentageOptions.indices) {
            val rule = percentageOptions[i]
            bucket += rule.percentage
            if (scale < bucket) {
                evaluateLogger?.logPercentageEvaluationReturnValue(scale, i, rule.percentage, rule.value)
                return EvaluationResult(rule.value, rule.variationId, parentTargetingRule, rule)
            }
        }

        throw IllegalArgumentException("Sum of percentage option percentages is less than 100.")
    }

    private fun getUserAttributeAsStringArray(
        userCondition: UserCondition,
        context: EvaluationContext,
        comparisonAttribute: String,
        userAttribute: Any,
    ): Array<String> {
        @Suppress("SwallowedException")
        try {
            if (userAttribute is Array<*> && userAttribute.all { it is String }) {
                return userAttribute as Array<String>
            }
            if ((userAttribute is List<*>) && userAttribute.all { it is String }) {
                val stringList: List<String> = userAttribute as List<String>
                return stringList.toTypedArray()
            }
            if (userAttribute is String) {
                return Constants.json.decodeFromString(userAttribute)
            }
        } catch (exception: Exception) {
            // if exception or no return yet, then throw RolloutEvaluatorException
        }
        val reason = "'$userAttribute' is not a valid JSON string array"
        logger.warning(
            3004,
            ConfigCatLogMessages.getUserAttributeInvalid(
                context.key,
                userCondition,
                reason,
                comparisonAttribute,
            ),
        )
        throw RolloutEvaluatorException(
            "cannot evaluate, the User.$comparisonAttribute attribute is " +
                "invalid ($reason)",
        )
    }

    private fun getUserAttributeAsVersion(
        key: String,
        userCondition: UserCondition,
        comparisonAttribute: String,
        userValue: Any,
    ): Version {
        @Suppress("SwallowedException")
        try {
            if (userValue is String) {
                return userValue.trim().toVersion()
            }
        } catch (e: VersionFormatException) {
            // Version parse failed continue with the RolloutEvaluatorException
        }
        val reason = "'$userValue' is not a valid semantic version"
        logger.warning(
            3004,
            ConfigCatLogMessages.getUserAttributeInvalid(
                key,
                userCondition,
                reason,
                comparisonAttribute,
            ),
        )
        throw RolloutEvaluatorException(
            "cannot evaluate, the User.$comparisonAttribute attribute is " +
                "invalid ($reason)",
        )
    }

    private fun getUserAttributeAsDouble(
        key: String,
        userCondition: UserCondition,
        comparisonAttribute: String,
        userValue: Any,
    ): Double {
        try {
            return if (userValue is Double) {
                userValue
            } else {
                userAttributeToDouble(userValue)
            }
        } catch (e: NumberFormatException) {
            val reason = "'$userValue' is not a valid decimal number"
            logger.warning(
                3004,
                ConfigCatLogMessages.getUserAttributeInvalid(
                    key,
                    userCondition,
                    reason,
                    comparisonAttribute,
                ),
            )
            throw RolloutEvaluatorException(
                "cannot evaluate, the User.$comparisonAttribute attribute is " +
                    "invalid ($reason)",
            )
        }
    }

    private fun getUserAttributeForDate(
        userCondition: UserCondition,
        context: EvaluationContext,
        comparisonAttribute: String,
        userValue: Any,
    ): Double {
        try {
            if (userValue is DateTime) {
                return userValue.unixMillisDouble / 1000
            }
            if (userValue is DateTimeTz) {
                return userValue.local.unixMillisDouble / 1000
            }
            return userAttributeToDouble(userValue)
        } catch (e: NumberFormatException) {
            val reason =
                "'$userValue' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)"
            logger.warning(
                3004,
                ConfigCatLogMessages.getUserAttributeInvalid(
                    context.key,
                    userCondition,
                    reason,
                    comparisonAttribute,
                ),
            )
            throw RolloutEvaluatorException(
                "cannot evaluate, the User.$comparisonAttribute attribute is " +
                    "invalid ($reason)",
            )
        }
    }

    private fun getUserAttributeAsString(
        key: String,
        userCondition: UserCondition,
        userAttributeName: String,
        userValue: Any,
    ): String {
        if (userValue is String) {
            return userValue
        }
        val userAttributeToString = userAttributeToString(userValue) ?: ""
        logger.warning(
            3005,
            ConfigCatLogMessages.getUserObjectAttributeIsAutoConverted(
                key,
                userCondition,
                userAttributeName,
                userAttributeToString,
            ),
        )

        return userAttributeToString
    }

    private fun userAttributeToString(userValue: Any?): String? {
        if (userValue == null) {
            return null
        }
        if (userValue is String) {
            return userValue
        }
        if (userValue is Array<*> && userValue.all { it is String }) {
            return Constants.json.encodeToString(userValue as Array<String>)
        }
        if (userValue is List<*> && userValue.all { it is String }) {
            return Constants.json.encodeToString(userValue as List<String>)
        }
        if (userValue is Float) {
            return doubleToString(userValue.toDouble())
        }
        if (userValue is Double) {
            return doubleToString(userValue)
        }

        if (userValue is DateTime) {
            return doubleToString((userValue.unixMillisDouble / 1000))
        }
        if (userValue is DateTimeTz) {
            return doubleToString((userValue.local.unixMillisDouble / 1000))
        }
        return userValue.toString()
    }

    private fun userAttributeToDouble(userValue: Any): Double {
        if (userValue is Double) {
            return userValue
        }
        if (userValue is Float) {
            return userValue.toDouble()
        }
        if (userValue is Int) {
            return userValue.toDouble()
        }
        if (userValue is Long) {
            return userValue.toDouble()
        }
        if (userValue is Byte) {
            return userValue.toDouble()
        }
        if (userValue is Short) {
            return userValue.toDouble()
        }
        if (userValue is String) {
            return userValue.trim().replace(",", ".").toDouble()
        }

        throw NumberFormatException()
    }

    private fun ensureConfigSalt(configSalt: String?): String {
        if (configSalt != null) {
            return configSalt
        }
        throw IllegalArgumentException("Config JSON salt is missing.")
    }

    private inline fun <reified T> ensureComparisonValue(value: T?): T {
        if (value != null) {
            return value
        }
        throw IllegalArgumentException("Comparison value is missing or invalid.")
    }

    /**
     * Describes the Rollout Evaluator User Condition Comparators.
     */
    enum class UserComparator(val id: Int, val value: String) {
        IS_ONE_OF(0, "IS ONE OF"),
        IS_NOT_ONE_OF(1, "IS NOT ONE OF"),
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
        HASHED_ARRAY_NOT_CONTAINS(27, "ARRAY NOT CONTAINS ANY OF"),
        TEXT_EQUALS(28, "EQUALS"),
        TEXT_NOT_EQUALS(29, "NOT EQUALS"),
        TEXT_STARTS_WITH(30, "STARTS WITH ANY OF"),
        TEXT_NOT_STARTS_WITH(31, "NOT STARTS WITH ANY OF"),
        TEXT_ENDS_WITH(32, "ENDS WITH ANY OF"),
        TEXT_NOT_ENDS_WITH(33, "NOT ENDS WITH ANY OF"),
        TEXT_ARRAY_CONTAINS(34, "ARRAY CONTAINS ANY OF"),
        TEXT_ARRAY_NOT_CONTAINS(35, "ARRAY NOT CONTAINS ANY OF"),
    }

    /**
     * Describes the Prerequisite Comparators.
     */
    enum class PrerequisiteComparator(val id: Int, val value: String) {
        EQUALS(0, "EQUALS"),
        NOT_EQUALS(1, "NOT EQUALS"),
    }

    /**
     * Describes the Segment Comparators.
     */
    enum class SegmentComparator(val id: Int, val value: String) {
        IS_IN_SEGMENT(0, "IS IN SEGMENT"),
        IS_NOT_IN_SEGMENT(1, "IS NOT IN SEGMENT"),
    }
}

/**
 * Convert [Double] to [String] based on the following format rules.
 *
 * To get similar result between different SDKs the Double value format is modified.
 * Between 1e-6 and 1e21 we don't use scientific-notation. Over these limits scientific-notation used but the
 * ExponentSeparator replaced with "e" and "e+". "." used as decimal separator in all cases.
 *
 * For [Double.NaN], [Double.POSITIVE_INFINITY] and [Double.NEGATIVE_INFINITY] simple String representation used.
 */
internal expect fun doubleToString(doubleToString: Double): String

/**
 * Format [Double] value for logging.
 *
 **/
internal expect fun formatDoubleForLog(doubleToFormat: Double): String

internal fun commonDoubleToString(doubleToString: Double): String {
    if (doubleToString.isNaN() || doubleToString.isInfinite()) {
        return doubleToString.toString()
    }
    // Scientific Notation use cannot be turned on or off in native and no formatter can be used properly.
    // As best effort we replace the "," and the "E" if presented.
    val stringFormatScientificNotation = doubleToString.toString().replace(",", ".")
    return if (doubleToString.absoluteValue > 1) {
        stringFormatScientificNotation.replace("E", "e+")
    } else {
        stringFormatScientificNotation.replace("E-", "e-")
    }
}

internal fun commonFormatDoubleForLog(doubleToFormat: Double): String {
    val comparisonValueString = doubleToFormat.toString().replace(',', '.')
    return if (comparisonValueString.contains('.') || comparisonValueString.contains(',')) {
        comparisonValueString.trimEnd { it == '0' }.trimEnd { it == '.' }
    } else {
        comparisonValueString
    }
}

internal class RolloutEvaluatorException(message: String?) : Exception(message)
