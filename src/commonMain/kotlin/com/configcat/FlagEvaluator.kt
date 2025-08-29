package com.configcat

import com.configcat.Client.SettingTypeHelper.toSettingTypeOrNull
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.log.LogLevel
import com.configcat.model.Setting
import com.configcat.model.SettingType
import kotlin.time.Instant

internal class FlagEvaluator(
    private val logger: InternalLogger,
    private val evaluator: Evaluator,
    private val hooks: Hooks,
) {
    fun findAndEvalFlag(
        result: SettingResult,
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        methodName: String,
        allowAnyReturnType: Boolean,
    ): Any? {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val checkSettingAvailable = checkSettingAvailable(result, key, defaultValue)
        val setting = checkSettingAvailable.third
        if (setting == null) {
            val details =
                EvaluationDetails.makeError(
                    key,
                    defaultValue,
                    checkSettingAvailable.first,
                    checkSettingAvailable.second,
                    null,
                    user,
                )
            hooks.invokeOnFlagEvaluated(details)
            return defaultValue
        }
        if (!allowAnyReturnType) {
            val validationResult = validateValueType(setting.type, defaultValue)
            if (validationResult != null) {
                return handleEvaluationError(
                    key,
                    defaultValue,
                    methodName,
                    EvaluationErrorCode.SETTING_VALUE_TYPE_MISMATCH,
                    user,
                    validationResult,
                    null,
                ).value
            }
        }
        return try {
            evalFlag(setting, key, user, result.fetchTime, result.settings).value
        } catch (exception: InvalidConfigModelException) {
            return handleEvaluationError(
                key,
                defaultValue,
                methodName,
                EvaluationErrorCode.INVALID_CONFIG_MODEL,
                user,
                exception.message,
                null,
            ).value
        } catch (exception: Exception) {
            return handleEvaluationError(
                key,
                defaultValue,
                methodName,
                EvaluationErrorCode.UNEXPECTED_ERROR,
                user,
                exception.message,
                exception,
            ).value
        }
    }

    fun findAndEvalFlagDetails(
        result: SettingResult,
        key: String,
        defaultValue: Any?,
        user: ConfigCatUser?,
        methodName: String,
        allowAnyReturnType: Boolean,
    ): EvaluationDetails {
        require(key.isNotEmpty()) { "'key' cannot be empty." }

        val checkSettingAvailable = checkSettingAvailable(result, key, defaultValue)
        val setting = checkSettingAvailable.third
        if (setting == null) {
            val details =
                EvaluationDetails.makeError(
                    key,
                    defaultValue,
                    checkSettingAvailable.first,
                    checkSettingAvailable.second,
                    null,
                    user,
                )
            hooks.invokeOnFlagEvaluated(details)
            return details
        }
        if (!allowAnyReturnType) {
            val validationResult = validateValueType(setting.type, defaultValue)
            if (validationResult != null) {
                return handleEvaluationError(
                    key,
                    defaultValue,
                    methodName,
                    EvaluationErrorCode.SETTING_VALUE_TYPE_MISMATCH,
                    user,
                    validationResult,
                    null,
                )
            }
        }
        return try {
            evalFlag(setting, key, user, result.fetchTime, result.settings)
        } catch (exception: InvalidConfigModelException) {
            return handleEvaluationError(
                key,
                defaultValue,
                methodName,
                EvaluationErrorCode.INVALID_CONFIG_MODEL,
                user,
                exception.message,
                null,
            )
        } catch (exception: Exception) {
            return handleEvaluationError(
                key,
                defaultValue,
                methodName,
                EvaluationErrorCode.UNEXPECTED_ERROR,
                user,
                exception.message,
                exception,
            )
        }
    }

    fun evalFlag(
        setting: Setting,
        key: String,
        user: ConfigCatUser?,
        fetchTime: Instant,
        settings: Map<String, Setting>,
    ): EvaluationDetails {
        var evaluateLogger: EvaluateLogger? = null
        if (logger.level == LogLevel.INFO) {
            evaluateLogger = EvaluateLogger()
        }
        val (value, variationId, targetingRule, percentageRule) =
            evaluator.evaluate(
                setting,
                key,
                user,
                settings,
                evaluateLogger,
            )
        val details =
            EvaluationDetails(
                key,
                variationId,
                user,
                false,
                null,
                EvaluationErrorCode.NONE,
                null,
                value.validateType(setting.type),
                fetchTime.toEpochMilliseconds(),
                targetingRule,
                percentageRule,
            )
        hooks.invokeOnFlagEvaluated(details)
        return details
    }

    private fun validateValueType(
        settingTypeInt: Int,
        defaultValue: Any?,
    ): String? {
        val settingType = settingTypeInt.toSettingTypeOrNull()
        if (settingType == null) {
            return "The setting type is not valid. Only String, Int, Double or Boolean types are supported."
        }
        if (defaultValue == null) {
            return null
        }
        val isString = defaultValue is String && settingType == SettingType.STRING
        val isBoolean = defaultValue is Boolean && settingType == SettingType.BOOLEAN
        val isInt = defaultValue is Int && (settingType == SettingType.INT || settingType == SettingType.JS_NUMBER)
        val isDouble =
            defaultValue is Double &&
                (settingType == SettingType.DOUBLE || settingType == SettingType.JS_NUMBER)
        if (!(isString || isBoolean || isInt || isDouble)) {
            return "The type of a setting must match the type of the specified default value. " +
                "Setting's type was {" + settingType + "} but the default value's type was {" +
                defaultValue::class.toString() + "}. Please use a default value which corresponds to the setting " +
                "type {" + settingType + "}. Learn more: " +
                "https://configcat.com/docs/sdk-reference/kotlin/#setting-type-mapping"
        }
        return null
    }

    private fun <T> checkSettingAvailable(
        settingResult: SettingResult,
        key: String,
        defaultValue: T,
    ): Triple<String, EvaluationErrorCode, Setting?> {
        if (settingResult.isEmpty()) {
            val errorMessage =
                ConfigCatLogMessages.getConfigJsonIsNotPresentedWithDefaultValue(key, "defaultValue", defaultValue)
            logger.error(1000, errorMessage)
            return Triple(errorMessage, EvaluationErrorCode.CONFIG_JSON_NOT_AVAILABLE, null)
        }
        val setting = settingResult.settings[key]
        if (setting == null) {
            val errorMessage =
                ConfigCatLogMessages.getSettingEvaluationFailedDueToMissingKey(
                    key,
                    "defaultValue",
                    defaultValue,
                    settingResult.settings.keys,
                )
            logger.error(1001, errorMessage)
            return Triple(errorMessage, EvaluationErrorCode.SETTING_KEY_MISSING, null)
        }
        return Triple("", EvaluationErrorCode.NONE, setting)
    }

    private fun handleEvaluationError(
        key: String,
        defaultValue: Any?,
        methodName: String,
        errorCode: EvaluationErrorCode,
        user: ConfigCatUser?,
        error: String?,
        exception: Exception?,
    ): EvaluationDetails {
        val errorMessage =
            "${
                ConfigCatLogMessages.getSettingEvaluationErrorWithDefaultValue(
                    methodName,
                    key,
                    "defaultValue",
                    defaultValue ?: "null",
                )
            }${error.let { " $it" }}"
        logger.error(1002, errorMessage)
        val errorDetails =
            EvaluationDetails.makeError(
                key,
                defaultValue,
                errorMessage,
                errorCode,
                exception,
                user,
            )
        hooks.invokeOnFlagEvaluated(errorDetails)
        return errorDetails
    }
}
