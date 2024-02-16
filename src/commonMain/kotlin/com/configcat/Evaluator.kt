package com.configcat

import com.configcat.Client.SettingTypeHelp.toSettingTypeOrNull
import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.log.LogHelper
import com.configcat.model.*
import com.soywiz.klock.DateTime
import com.soywiz.krypto.sha1
import com.soywiz.krypto.sha256
import io.github.z4kn4fein.semver.Version
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion
import io.ktor.http.*
import io.ktor.utils.io.charsets.*
import kotlinx.serialization.decodeFromString

internal data class EvaluationResult(
    val value: SettingsValue,
    val variationId: String?,
    val matchedTargetingRule: TargetingRule? = null,
    val matchedPercentageOption: PercentageOption? = null
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
    fun Int.toComparatorOrNull(): Evaluator.UserComparator? = Evaluator.UserComparator.values().firstOrNull { it.id == this }
    fun Int.toPrerequisiteComparatorOrNull(): Evaluator.PrerequisiteComparator? =
        Evaluator.PrerequisiteComparator.values().firstOrNull { it.id == this }

    fun Int.toSegmentComparatorOrNull(): Evaluator.SegmentComparator? =
        Evaluator.SegmentComparator.values().firstOrNull { it.id == this }
}

private const val USER_OBJECT_IS_MISSING = "cannot evaluate, User Object is missing"

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

    @Suppress("LoopWithTooManyJumpStatements", "CyclomaticComplexMethod")
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
            var evaluateConditionsResult: Boolean
            var error: String? = null
            try {
                evaluateConditionsResult = evaluateConditions(
                    rule.conditionAccessors,
                    rule,
                    setting.configSalt,
                    context.key,
                    context,
                    setting.segments,
                    evaluateLogger
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
                return rule.servedValue.let { EvaluationResult(it.value, rule.servedValue.variationId, rule, null) }
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
        conditions: List<ConditionAccessor>,
        targetingRule: TargetingRule?,
        configSalt: String?,
        contextSalt: String,
        context: EvaluationContext,
        segments: Array<Segment>,
        evaluateLogger: EvaluateLogger?
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
                    conditionsEvaluationResult = evaluateUserCondition(
                        userCondition,
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
            }

            condition.segmentCondition?.let { segmentCondition ->
                try {
                    conditionsEvaluationResult = evaluateSegmentCondition(
                        segmentCondition,
                        context,
                        configSalt,
                        segments,
                        evaluateLogger
                    )
                } catch (evaluatorException: RolloutEvaluatorException) {
                    error = evaluatorException.message
                    conditionsEvaluationResult = false
                }
                newLine = error == null || USER_OBJECT_IS_MISSING != error || conditions.size > 1
            }

            condition.prerequisiteFlagCondition?.let { prerequisiteCondition ->
                try {
                    conditionsEvaluationResult = evaluatePrerequisiteFlagCondition(
                        prerequisiteCondition,
                        context,
                        evaluateLogger
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
            throw RolloutEvaluatorException(USER_OBJECT_IS_MISSING)
        }

        require(segment != null) { "Segment reference is invalid." }

        val segmentName: String? = segment.name
        require(!segmentName.isNullOrEmpty()) { "Segment name is missing." }

        evaluateLogger?.logSegmentEvaluationStart(segmentName)
        var result: Boolean
        @Suppress("SwallowedException")
        try {
            val segmentRulesResult = evaluateConditions(
                segment.conditionAccessors,
                null,
                configSalt,
                segmentName,
                context,
                segments,
                evaluateLogger
            )

            val segmentComparator = segmentCondition.segmentComparator.toSegmentComparatorOrNull()
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
        evaluateLogger: EvaluateLogger?
    ): Boolean {
        evaluateLogger?.append(LogHelper.formatPrerequisiteFlagCondition(prerequisiteFlagCondition))
        val prerequisiteFlagKey: String? = prerequisiteFlagCondition.prerequisiteFlagKey
        val prerequisiteFlagSetting = context.settings?.get(prerequisiteFlagKey)
        require(!prerequisiteFlagKey.isNullOrEmpty() && prerequisiteFlagSetting != null) {
            "Prerequisite flag key is missing or invalid."
        }

        val settingType = prerequisiteFlagSetting.type.toSettingTypeOrNull()
        require(
            settingType == SettingType.JS_NUMBER && (prerequisiteFlagCondition.value?.doubleValue != null || prerequisiteFlagCondition.value?.integerValue != null) ||
                settingType == SettingType.BOOLEAN && prerequisiteFlagCondition.value?.booleanValue != null ||
                settingType == SettingType.STRING && prerequisiteFlagCondition.value?.stringValue != null ||
                settingType == SettingType.INT && prerequisiteFlagCondition.value?.integerValue != null ||
                settingType == SettingType.DOUBLE && prerequisiteFlagCondition.value?.doubleValue != null
        ) {
            "Type mismatch between comparison value '${prerequisiteFlagCondition.value}' and prerequisite flag '$prerequisiteFlagKey'."
        }

        val visitedKeys: ArrayList<String> = context.visitedKeys ?: ArrayList()
        visitedKeys.add(context.key)
        if (visitedKeys.contains(prerequisiteFlagKey)) {
            val dependencyCycle: String =
                LogHelper.formatCircularDependencyList(visitedKeys, prerequisiteFlagKey)
            throw IllegalArgumentException("Circular dependency detected between the following depending flags: $dependencyCycle.")
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

        visitedKeys.remove(context.key)

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
        configSalt: String?,
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
            throw RolloutEvaluatorException(USER_OBJECT_IS_MISSING)
        }
        val comparisonAttribute = condition.comparisonAttribute
        val userValue = context.user.attributeFor(comparisonAttribute)
        val comparator = condition.comparator.toComparatorOrNull()
            ?: throw IllegalArgumentException("Comparison operator is invalid.")

        if (userValue == null) {
            logger.warning(
                3003,
                ConfigCatLogMessages.getUserAttributeMissing(context.key, condition, comparisonAttribute)
            )
            throw RolloutEvaluatorException("cannot evaluate, the User.$comparisonAttribute attribute is missing")
        }

        when (comparator) {
            UserComparator.CONTAINS_ANY_OF,
            UserComparator.NOT_CONTAINS_ANY_OF -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processContains(condition, userAttributeAsString, comparator)
            }

            UserComparator.ONE_OF_SEMVER,
            UserComparator.NOT_ONE_OF_SEMVER -> {
                val userAttributeAsVersion =
                    getUserAttributeAsVersion(context.key, condition, comparisonAttribute, userValue)
                return processSemVerOneOf(condition, userAttributeAsVersion, comparator)
            }

            UserComparator.LT_SEMVER,
            UserComparator.LTE_SEMVER,
            UserComparator.GT_SEMVER,
            UserComparator.GTE_SEMVER -> {
                val userAttributeAsVersion =
                    getUserAttributeAsVersion(context.key, condition, comparisonAttribute, userValue)
                return processSemVerCompare(condition, userAttributeAsVersion, comparator)
            }

            UserComparator.EQ_NUM,
            UserComparator.NOT_EQ_NUM,
            UserComparator.LT_NUM,
            UserComparator.LTE_NUM,
            UserComparator.GT_NUM,
            UserComparator.GTE_NUM -> {
                val userAttributeAsDouble =
                    getUserAttributeAsDouble(context.key, condition, comparisonAttribute, userValue)
                return processNumber(condition, userAttributeAsDouble, comparator)
            }

            UserComparator.IS_ONE_OF,
            UserComparator.IS_NOT_ONE_OF,
            UserComparator.ONE_OF_SENS,
            UserComparator.NOT_ONE_OF_SENS -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processSensitiveOneOf(condition, userAttributeAsString, configSalt, contextSalt, comparator)
            }

            UserComparator.DATE_BEFORE,
            UserComparator.DATE_AFTER -> {
                val userAttributeForDate = getUserAttributeForDate(condition, context, comparisonAttribute, userValue)
                return processDateCompare(condition, userAttributeForDate, comparator)
            }

            UserComparator.TEXT_EQUALS,
            UserComparator.TEXT_NOT_EQUALS,
            UserComparator.HASHED_EQUALS,
            UserComparator.HASHED_NOT_EQUALS -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processHashedEqualsCompare(condition, userAttributeAsString, configSalt, contextSalt, comparator)
            }

            UserComparator.HASHED_STARTS_WITH,
            UserComparator.HASHED_NOT_STARTS_WITH,
            UserComparator.HASHED_ENDS_WITH,
            UserComparator.HASHED_NOT_ENDS_WITH -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processHashedStartEndsWithCompare(
                    condition,
                    userAttributeAsString,
                    ensureConfigSalt(configSalt),
                    contextSalt,
                    comparator
                )
            }

            UserComparator.TEXT_STARTS_WITH,
            UserComparator.TEXT_NOT_STARTS_WITH -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processTextStartWithCompare(condition, userAttributeAsString, comparator)
            }

            UserComparator.TEXT_ENDS_WITH,
            UserComparator.TEXT_NOT_ENDS_WITH -> {
                val userAttributeAsString =
                    getUserAttributeAsString(context.key, condition, comparisonAttribute, userValue)
                return processTextEndWithCompare(condition, userAttributeAsString, comparator)
            }

            UserComparator.TEXT_ARRAY_CONTAINS,
            UserComparator.TEXT_ARRAY_NOT_CONTAINS,
            UserComparator.HASHED_ARRAY_CONTAINS,
            UserComparator.HASHED_ARRAY_NOT_CONTAINS -> {
                val userArrayValue = getUserAttributeAsStringArray(condition, context, comparisonAttribute, userValue)
                return processHashedArrayContainsCompare(
                    condition,
                    userArrayValue,
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
        userComparator: UserComparator
    ): Boolean {
        val values = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var matched = false
        for (value in values) {
            matched = userValue.contains(value)
            if (matched) {
                break
            }
        }
        if ((matched && userComparator == UserComparator.CONTAINS_ANY_OF) ||
            (!matched && userComparator == UserComparator.NOT_CONTAINS_ANY_OF)
        ) {
            return true
        }
        return false
    }

    private fun processSemVerOneOf(
        condition: UserCondition,
        userVersion: Version,
        userComparator: UserComparator
    ): Boolean {
        @Suppress("SwallowedException")
        try {
            val values = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
            var matched = false
            for (value in values) {
                matched = value.toVersion() == userVersion || matched
            }
            if ((matched && userComparator == UserComparator.ONE_OF_SEMVER) ||
                (!matched && userComparator == UserComparator.NOT_ONE_OF_SEMVER)
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
        userComparator: UserComparator
    ): Boolean {
        @Suppress("SwallowedException")
        return try {
            val comparisonVersion = let { condition.stringValue ?: "" }.trim().toVersion()
            when (userComparator) {
                UserComparator.LT_SEMVER -> userVersion < comparisonVersion
                UserComparator.LTE_SEMVER -> userVersion <= comparisonVersion
                UserComparator.GT_SEMVER -> userVersion > comparisonVersion
                UserComparator.GTE_SEMVER -> userVersion >= comparisonVersion
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
        userComparator: UserComparator
    ): Boolean {
        val comparisonNumber = condition.doubleValue
            ?: throw IllegalArgumentException("Comparison value is missing or invalid.")

        return when (userComparator) {
            UserComparator.EQ_NUM -> userNumber == comparisonNumber
            UserComparator.NOT_EQ_NUM -> userNumber != comparisonNumber
            UserComparator.LT_NUM -> userNumber < comparisonNumber
            UserComparator.LTE_NUM -> userNumber <= comparisonNumber
            UserComparator.GT_NUM -> userNumber > comparisonNumber
            UserComparator.GTE_NUM -> userNumber >= comparisonNumber
            else -> false
        }
    }

    private fun processSensitiveOneOf(
        condition: UserCondition,
        userValue: String,
        configSalt: String?,
        contextSalt: String,
        userComparator: UserComparator
    ): Boolean {
        val split = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val formattedUserValue = if (userComparator == UserComparator.ONE_OF_SENS || userComparator == UserComparator.NOT_ONE_OF_SENS) {
            getSaltedUserValue(userValue, ensureConfigSalt(configSalt), contextSalt)
        } else {
            userValue
        }
        return when (userComparator) {
            UserComparator.ONE_OF_SENS,
            UserComparator.IS_ONE_OF -> split.contains(formattedUserValue)

            UserComparator.NOT_ONE_OF_SENS,
            UserComparator.IS_NOT_ONE_OF -> !split.contains(formattedUserValue)

            else -> false
        }
    }

    private fun processDateCompare(
        condition: UserCondition,
        userDateDouble: Double,
        userComparator: UserComparator
    ): Boolean {
        val comparisonDateDouble =
            condition.doubleValue ?: throw IllegalArgumentException("Comparison value is missing or invalid.")
        return when (userComparator) {
            UserComparator.DATE_BEFORE -> userDateDouble < comparisonDateDouble
            UserComparator.DATE_AFTER -> userDateDouble > comparisonDateDouble
            else -> false
        }
    }

    private fun processHashedEqualsCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String?,
        contextSalt: String,
        userComparator: UserComparator
    ): Boolean {
        val formattedUserValue =
            if (userComparator == UserComparator.HASHED_EQUALS || userComparator == UserComparator.HASHED_NOT_EQUALS) {
                getSaltedUserValue(userValue, ensureConfigSalt(configSalt), contextSalt)
            } else {
                userValue
            }
        val comparisonValue = condition.stringValue
        return when (userComparator) {
            UserComparator.HASHED_EQUALS, UserComparator.TEXT_EQUALS -> formattedUserValue == comparisonValue
            UserComparator.HASHED_NOT_EQUALS, UserComparator.TEXT_NOT_EQUALS -> formattedUserValue != comparisonValue
            else -> false
        }
    }

    private fun processHashedStartEndsWithCompare(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        userComparator: UserComparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val userValueUTF8 = userValue.encodeToByteArray()
        var matchCondition = false
        for (comparisonValueHashedStartsEnds in withValuesSplit) {

                val comparedTextLength = comparisonValueHashedStartsEnds.substringBeforeLast("_")
                require(comparedTextLength != comparisonValueHashedStartsEnds) {
                    "Comparison value is missing or invalid."
                }
                val comparedTextLengthInt: Int
                try {
                     comparedTextLengthInt = comparedTextLength.toInt()
                } catch (e: NumberFormatException) {
                    throw IllegalArgumentException("Comparison value is missing or invalid.")
                }
                if (userValueUTF8.size < comparedTextLengthInt) {
                    continue
                }
                val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfterLast("_")
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
                }

        }
        if (userComparator == UserComparator.HASHED_NOT_STARTS_WITH || userComparator == UserComparator.HASHED_NOT_ENDS_WITH) {
            // negate the match in case of NOT ANY OF
            matchCondition = !matchCondition
        }
        return matchCondition
    }

    private fun processTextStartWithCompare(
        condition: UserCondition,
        userValue: String,
        userComparator: UserComparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var startWith = false
        for (textValue in withValuesSplit) {
            if (userValue.startsWith(textValue)) {
                startWith = true
                break
            }
        }
        if (userComparator == UserComparator.TEXT_NOT_STARTS_WITH) {
            startWith = !startWith
        }
        return startWith
    }

    private fun processTextEndWithCompare(
        condition: UserCondition,
        userValue: String,
        userComparator: UserComparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        var endWith = false
        for (textValue in withValuesSplit) {
            if (userValue.endsWith(textValue)) {
                endWith = true
                break
            }
        }
        if (userComparator == UserComparator.TEXT_NOT_ENDS_WITH) {
            endWith = !endWith
        }
        return endWith
    }

    private fun processHashedArrayContainsCompare(
        condition: UserCondition,
        userContainsArray: Array<String>,
        configSalt: String?,
        contextSalt: String,
        userComparator: UserComparator
    ): Boolean {
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        if (userContainsArray.isEmpty()) {
            return false
        }
        var contains = false
        val hashedRequired =
            userComparator == UserComparator.HASHED_ARRAY_CONTAINS || userComparator == UserComparator.HASHED_ARRAY_NOT_CONTAINS
        userContainsArray.forEach {
            val correctUserValue = if (hashedRequired) {
                getSaltedUserValue(it, ensureConfigSalt(configSalt), contextSalt)
            } else {
                it
            }
            if (withValuesSplit.contains(correctUserValue)) {
                contains = true
            }
        }
        return when (userComparator) {
            UserComparator.HASHED_ARRAY_CONTAINS,
            UserComparator.TEXT_ARRAY_CONTAINS -> contains

            UserComparator.HASHED_ARRAY_NOT_CONTAINS,
            UserComparator.TEXT_ARRAY_NOT_CONTAINS -> !contains

            else -> false
        }
    }

    private fun getSaltedUserValue(userValue: String, configSalt: String, contextSalt: String): String {
        val value = userValue + configSalt + contextSalt
        return value.encodeToByteArray().sha256().hex
    }

    private fun getSaltedUserValueSlice(userValue: ByteArray, configSalt: String, contextSalt: String): String {
        val configSaltByteArray = configSalt.encodeToByteArray()
        val contextSaltByteArray = contextSalt.encodeToByteArray()
        val concatByteArray = userValue + configSaltByteArray + contextSaltByteArray
        return concatByteArray.sha256().hex
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
            percentageOptionAttributeValue =
                userAttributeToString(context.user.attributeFor(percentageOptionAttributeName))
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

        throw IllegalArgumentException("Sum of percentage option percentages are less than 100.")
    }

    private fun getUserAttributeAsStringArray(
        userCondition: UserCondition,
        context: EvaluationContext,
        comparisonAttribute: String,
        userAttribute: Any
    ): Array<String> {
        try {
            if (userAttribute is Array<*> && userAttribute.all { it is String }) {
                return userAttribute as Array<String>
            }

            if ((userAttribute is List<*>) && userAttribute.all { it is String }) {
                var stringList: List<String> = userAttribute as List<String>
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
                comparisonAttribute
            )
        )
        throw RolloutEvaluatorException(
            "cannot evaluate, the User.$comparisonAttribute attribute is " +
                "invalid ($reason)"
        )
    }

    private fun getUserAttributeAsVersion(
        key: String,
        userCondition: UserCondition,
        comparisonAttribute: String,
        userValue: Any
    ): Version {
        @Suppress("SwallowedException")
        try {
            if (userValue is String) {
                return userValue.toVersion()
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
                comparisonAttribute
            )
        )
        throw RolloutEvaluatorException(
            "cannot evaluate, the User.$comparisonAttribute attribute is " +
                "invalid ($reason)"
        )
    }

    private fun getUserAttributeAsDouble(
        key: String,
        userCondition: UserCondition,
        comparisonAttribute: String,
        userValue: Any
    ): Double {
        var converted: Double
        try {
            if (userValue is Double) {
                converted = userValue
            } else {
                converted = userAttributeToDouble(userValue)
            }
            if (converted.isNaN()) {
                throw NumberFormatException()
            }
        } catch (e: NumberFormatException) {
            val reason = "'$userValue' is not a valid decimal number"
            logger.warning(
                3004,
                ConfigCatLogMessages.getUserAttributeInvalid(
                    key,
                    userCondition,
                    reason,
                    comparisonAttribute
                )
            )
            throw RolloutEvaluatorException(
                "cannot evaluate, the User.$comparisonAttribute attribute is " +
                    "invalid ($reason)"
            )
        }
        return converted
    }

    private fun getUserAttributeForDate(
        userCondition: UserCondition,
        context: EvaluationContext,
        comparisonAttribute: String,
        userValue: Any
    ): Double {
        try {
            if (userValue is DateTime) {
                return userValue.unixMillisDouble / 1000
            }
            val userAttributeToDouble = userAttributeToDouble(userValue)
            if (userAttributeToDouble.isNaN()) {
                throw NumberFormatException()
            }
            return userAttributeToDouble
        } catch (e: NumberFormatException) {
            val reason =
                "'$userValue' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)"
            logger.warning(
                3004,
                ConfigCatLogMessages.getUserAttributeInvalid(
                    context.key,
                    userCondition,
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

    private fun getUserAttributeAsString(
        key: String,
        userCondition: UserCondition,
        userAttributeName: String,
        userValue: Any
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
                userAttributeToString
            )
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
        if (userValue is DateTime) {
            return (userValue.milliseconds / 1000).toString()
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
        if (userValue is String) {
            return userValue.trim().replace(",", ".").toDouble()
        }

        throw NumberFormatException()
    }

    private fun ensureConfigSalt(configSalt: String?): String{
        if(configSalt != null){
            return configSalt
        }
        throw IllegalArgumentException("Config JSON salt is missing.")
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
        TEXT_ARRAY_NOT_CONTAINS(35, "ARRAY NOT CONTAINS ANY OF");
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

    fun logSegmentEvaluationError(segmentCondition: SegmentCondition?, segment: Segment?, error: String?) {
        newLine()
        append("Segment evaluation result: $error.")
        newLine()
        append("Condition (${LogHelper.formatSegmentFlagCondition(segmentCondition, segment)}) failed to evaluate.")
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
