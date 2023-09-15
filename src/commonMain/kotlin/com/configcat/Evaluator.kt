package com.configcat

import com.configcat.ComparatorHelp.toComparatorOrNull
import com.configcat.ComparatorHelp.toPrerequisiteComparatorOrNull
import com.configcat.ComparatorHelp.toSegmentComparatorOrNull
import com.configcat.DateTimeUtils.toDateTimeUTCString
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
    val visitedKeys: ArrayList<String>,
    val settings: Map<String, Setting>?,
    var isUserMissing: Boolean = false,
    var isUserAttributeMissing: Boolean = false
)

internal object ComparatorHelp{
    fun Int.toComparatorOrNull(): Evaluator.Comparator? = Evaluator.Comparator.values().firstOrNull { it.id == this }
    fun Int.toPrerequisiteComparatorOrNull(): Evaluator.PrerequisiteComparator? = Evaluator.PrerequisiteComparator.values().firstOrNull { it.id == this }
    fun Int.toSegmentComparatorOrNull(): Evaluator.SegmentComparator? = Evaluator.SegmentComparator.values().firstOrNull { it.id == this }
}

internal class Evaluator(private val logger: InternalLogger) {


    fun evaluate(setting: Setting, key: String, user: ConfigCatUser?, visitedKeys: ArrayList<String>?,  settings: Map<String, Setting>?, evaluatorLogger: EvaluatorLogger): EvaluationResult {

        try {
            if (user != null) {
                evaluatorLogger.logUserObject(user)
            }

            val tmpVisitedKeys = arrayListOf(key)
            if (visitedKeys != null) tmpVisitedKeys.addAll(visitedKeys)

            val context = EvaluationContext(key, user, tmpVisitedKeys, settings)

            val valueFromTargetingRules = evaluateTargetingRules(setting, context, evaluatorLogger)
            if (valueFromTargetingRules != null) return valueFromTargetingRules

            val valueFromPercentageRules = evaluatePercentageOptions(setting.percentageOptions, setting.percentageAttribute, context, null, evaluatorLogger)
            if (valueFromPercentageRules != null) return valueFromPercentageRules

            evaluatorLogger.logReturnValue(setting.settingsValue)
            return EvaluationResult(setting.settingsValue, setting.variationId)
        } finally {
            logger.info(5000, evaluatorLogger.print())
        }
    }


    private fun evaluateTargetingRules(
        setting: Setting,
        context: EvaluationContext,
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        if (setting.targetingRules.isNullOrEmpty()) {
            return null
        }
        for (rule in setting.targetingRules) {
            // TODO fix this. remove unnecessary null checks
            if (!evaluateConditions(rule.conditions ?: arrayOf(), setting.configSalt, context, setting.segments, evaluatorLogger)) {
                continue
            }
            if (rule.servedValue != null) {
                return EvaluationResult(rule.servedValue.value, rule.servedValue.variationId, rule, null)
            }
            if (rule.percentageOptions.isNullOrEmpty()) {
                continue
            }
            return evaluatePercentageOptions(
                rule.percentageOptions,
                setting.percentageAttribute,
                context,
                rule,
                evaluatorLogger
            ) ?: continue
        }
        return null
    }

    private fun evaluateConditions(
        conditions: Array<Condition>,
        configSalt: String,
        context: EvaluationContext,
        segments: Array<Segment>?,
        evaluatorLogger: EvaluatorLogger
    ): Boolean {
        // TODO rework logging based on changes possibly

        // Conditions are ANDs so if One is not matching return false, if all matching return true
        // TODO rework logging based on changes possibly
        var conditionsEvaluationResult = false
        for (condition in conditions) {
            // TODO log IF, AND based on order

            // TODO Condition, what if condition invalid? more then one condition added or none. rework basic if
            if (condition.userCondition != null) {
                conditionsEvaluationResult = evaluateUserCondition(
                    condition.userCondition,
                    configSalt,
                    context,
                    context.key,
                    evaluatorLogger
                )
            } else if (condition.segmentCondition != null) {
                conditionsEvaluationResult = evaluateSegmentCondition(condition.segmentCondition, context, configSalt, segments, evaluatorLogger)
            } else if (condition.prerequisiteFlagCondition != null) {
                conditionsEvaluationResult = evaluatePrerequisiteFlagCondition(condition.prerequisiteFlagCondition, context, evaluatorLogger)
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
        segmentCondition: SegmentCondition,
        context: EvaluationContext,
        configSalt: String,
        segments: Array<Segment>?,
        evaluateLogger: EvaluatorLogger
    ): Boolean {
        if (context.user == null) {
            // evaluateLogger "Skipping % options because the User Object is missing."
            //TODO isUserMissing in context? check pyhton
            return false
        }
        val segmentIndex: Int = segmentCondition.segmentIndex
        if (segmentIndex >= (segments?.size ?: 0)) {
            //TODO log invalid segment
            return false
        }
        val segment = segments?.get(segmentIndex)
        val segmentName: String? = segment?.name
        if (segmentName.isNullOrEmpty()) {
            //TODO log segment name is missing
            return false
        }
        //TODO add logging
        var segmentRulesResult = false
        for (comparisonCondition in segment.segmentRules) {
            segmentRulesResult =
                evaluateUserCondition(comparisonCondition, configSalt,context, segmentName, evaluateLogger)
            //this is an AND if one false we can start the evaluation on the segmentComparator
            if (!segmentRulesResult) {
                break
            }
        }

        val segmentComparator = segmentCondition.segmentComparator.toSegmentComparatorOrNull()

        return if (SegmentComparator.IS_IN_SEGMENT == segmentComparator) {
            segmentRulesResult
        } else {
            !segmentRulesResult
        }
    }

    private fun evaluatePrerequisiteFlagCondition(
        prerequisiteFlagCondition: PrerequisiteFlagCondition,
        context: EvaluationContext,
        evaluatorLogger: EvaluatorLogger
    ): Boolean {
        //TODO add logger evaluateLogger
        val prerequisiteFlagKey: String? = prerequisiteFlagCondition.prerequisiteFlagKey
        val prerequisiteFlagSetting = context.settings?.get(prerequisiteFlagKey)
        if (prerequisiteFlagKey.isNullOrEmpty() || prerequisiteFlagSetting == null) {
            // TODO Log error
            return false
        }
        if (context.visitedKeys.contains(prerequisiteFlagKey)) {
            //TODO log eval , return error message?
            val dependencyCycle: String =
                LogHelper.formatCircularDependencyList(context.visitedKeys, prerequisiteFlagKey)
            logger.warning(
                3004,
                ConfigCatLogMessages.getCircularDependencyDetected(
                    context.key,
                    prerequisiteFlagCondition,
                    dependencyCycle
                )
            )
            return false
        }

        val (value) = evaluate(prerequisiteFlagSetting, prerequisiteFlagKey, context.user, context.visitedKeys, context.settings, evaluatorLogger)
        val prerequisiteComparator = prerequisiteFlagCondition.prerequisiteComparator.toPrerequisiteComparatorOrNull()
        val conditionValue: SettingsValue? = prerequisiteFlagCondition.value
        return if (PrerequisiteComparator.EQUALS == prerequisiteComparator) {
            conditionValue == value
        } else {
            conditionValue != value
        }
    }

    private fun evaluateUserCondition(
        condition: UserCondition,
        configSalt: String,
        context: EvaluationContext,
        contextSalt: String,
        evaluatorLogger: EvaluatorLogger
    ): Boolean {

        //TODO evalLogger CC eval is happening
        if (context.user == null) {
            //TODO eval logger error must be logged as well
            if(!context.isUserMissing){
                context.isUserMissing= true
                this.logger.warning(3001, ConfigCatLogMessages.getUserObjectMissing(context.key))
            }
            return false
        }
        val comparisonAttribute = condition.comparisonAttribute
        val userValue = context.user.attributeFor(comparisonAttribute)
        val comparator = condition.comparator.toComparatorOrNull()
            ?: // TODO add log
            return false

        if (userValue.isNullOrEmpty()) {
            logger.warning(3003, ConfigCatLogMessages.getUserAttributeMissing(context.key, condition, comparisonAttribute))
            //TODO eval logger needed
            return false
        }

        when (comparator) {
            Comparator.CONTAINS_ANY_OF,
            Comparator.NOT_CONTAINS_ANY_OF -> {
                return processContains(condition, userValue, evaluatorLogger, comparator)
            }

            Comparator.ONE_OF_SEMVER,
            Comparator.NOT_ONE_OF_SEMVER -> {
                return try {
                    val userVersion = userValue.toVersion()
                    processSemVerOneOf(condition, userVersion, evaluatorLogger, comparator)
                } catch (e: VersionFormatException){
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
                    false
                }

            }

            Comparator.LT_SEMVER,
            Comparator.LTE_SEMVER,
            Comparator.GT_SEMVER,
            Comparator.GTE_SEMVER -> {
                return try {
                    val userVersion = userValue.toVersion()
                    processSemVerCompare(condition, userVersion, evaluatorLogger, comparator)
                } catch (e: VersionFormatException){
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
                    false
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
                    processNumber(condition, userNumber, evaluatorLogger, comparator)
                } catch (e: NumberFormatException){
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
                    false
                }
            }

            Comparator.ONE_OF_SENS,
            Comparator.NOT_ONE_OF_SENS -> {
                return processSensitiveOneOf(condition, userValue, configSalt, contextSalt, evaluatorLogger, comparator)
            }

            Comparator.DATE_BEFORE,
            Comparator.DATE_AFTER -> {
                return try {
                    val userDateDouble = userValue.trim().replace(",", ".").toDouble()
                    processDateCompare(condition, userDateDouble, evaluatorLogger, comparator)
                } catch (e: NumberFormatException){
                    val reason = "'$userValue' is not a valid Unix timestamp (number of seconds elapsed since Unix epoch)"
                    logger.warning(
                        3004,
                        ConfigCatLogMessages.getUserAttributeInvalid(
                            context.key,
                            condition,
                            reason,
                            comparisonAttribute
                        )
                    )
                    false
                }
            }

            Comparator.HASHED_EQUALS,
            Comparator.HASHED_NOT_EQUALS -> {
                return processHashedEqualsCompare(condition, userValue, configSalt, contextSalt, evaluatorLogger, comparator)
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
                    contextSalt,
                    evaluatorLogger,
                    comparator
                )
            }
        }
    }

    private fun processContains(
        condition: UserCondition,
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

    private fun processSemVerOneOf(
        condition: UserCondition,
        userVersion: Version,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
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
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
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
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {

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
            return false
        }
    }

    private fun processSensitiveOneOf(
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
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
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        try {
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
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val userValueHash = getSaltedUserValue(userValue, configSalt, contextSalt )
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
                if(userValue.length < comparedTextLengthInt){
                    continue
                }
                val comparisonHashValue = comparisonValueHashedStartsEnds.substringAfterLast("_")
                if (comparisonHashValue.isEmpty()) {
                    return false
                }
                val userValueHashed =
                    if (comparator == Comparator.HASHED_STARTS_WITH || comparator == Comparator.HASHED_NOT_STARTS_WITH) {
                        getSaltedUserValue(userValue.substring(0, comparedTextLengthInt), configSalt, contextSalt)
                    } else {
                        // Comparator.HASHED_ENDS_WITH, Comparator.HASHED_NOT_ENDS_WITH
                        //TODO check value has to be bigger than 0
                        getSaltedUserValue(
                            userValue.substring( userValue.length - comparedTextLengthInt),
                            configSalt,
                            contextSalt
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
        condition: UserCondition,
        userValue: String,
        configSalt: String,
        contextSalt: String,
        evaluatorLogger: EvaluatorLogger,
        comparator: Comparator
    ): Boolean {
        // TODO salt error handle
        val withValuesSplit = condition.stringArrayValue?.map { it.trim() }?.filter { it.isNotEmpty() }.orEmpty()
        val userCSVNotContainsHashSplit = userValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
        if (userCSVNotContainsHashSplit.isEmpty()) {
            return false
        }
        var contains = false
        userCSVNotContainsHashSplit.forEach {
            val hashedUserValue = getSaltedUserValue(it, configSalt, contextSalt)
            if(withValuesSplit.contains(hashedUserValue)){
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
        evaluatorLogger: EvaluatorLogger
    ): EvaluationResult? {
        if (context.user == null) {
            // evaluateLogger "Skipping % options because the User Object is missing."
            //TODO isUserMissing in context? check pyhton
            return null
        }

        //TODO if user missing? based on .net skipp should be logged here
        val percentageOptionAttributeValue: String?
        var percentageOptionAttributeName = percentageOptionAttribute
        if (percentageOptionAttributeName.isNullOrEmpty()) {
            percentageOptionAttributeName = "Identifier"
            percentageOptionAttributeValue = context.user.identifier
        } else {
            percentageOptionAttributeValue = context.user.attributeFor(percentageOptionAttributeName)
            if (percentageOptionAttributeValue.isNullOrEmpty()) {
                //TODO log skip because attribute value missing
                return null
            }
        }

        if(percentageOptions.isNullOrEmpty()){
            return null
        }

        val hashCandidate = "${context.key}${percentageOptionAttributeValue}"
        val hash = hashCandidate.encodeToByteArray().sha1().hex.substring(0, 7)
        val numberRepresentation = hash.toInt(radix = 16)
        val scale = numberRepresentation % 100

        var bucket = 0.0
        for (rule in percentageOptions) {
            bucket += rule.percentage
            if (scale < bucket) {
                evaluatorLogger.logPercentageEvaluationReturnValue(rule.value)
                return EvaluationResult(rule.value, rule.variationId, parentTargetingRule, rule)
            }
        }
        return null
    }

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
        NOT_EQ_NUM(11, "<>"),
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
        HASHED_ARRAY_CONTAINS(26, "ARRAY CONTAINS"),
        HASHED_ARRAY_NOT_CONTAINS(27, "ARRAY NOT CONTAINS")
    }

    enum class PrerequisiteComparator(val id: Int, val value: String) {
        EQUALS(0,"EQUALS"),
        NOT_EQUALS(1,"NOT EQUALS")
    }

    enum class SegmentComparator(val id: Int, val value: String) {
        IS_IN_SEGMENT(0,"IS IN SEGMENT"),
        IS_NOT_IN_SEGMENT(1,"IS NOT IN SEGMENT")
    }

}

internal class EvaluatorLogger constructor(
    key: String
) {
    private val entries = StringBuilder()
    private var indentLevel: Int = 0

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
        entries.appendLine(" for User '$user'")
    }

    fun append(line: String) {
        entries.appendLine(line)
    }

    fun increaseIndentLevel() {
        indentLevel++
    }

    fun decreaseIndentLevel() {
        //TODO validate it cannot be less then 0?
        indentLevel--
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
