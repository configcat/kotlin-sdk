package com.configcat.client

import com.configcat.client.logging.InternalLogger
import com.soywiz.krypto.sha1
import io.github.z4kn4fein.semver.VersionFormatException
import io.github.z4kn4fein.semver.toVersion

internal class Evaluator constructor(private val logger: InternalLogger) {
    fun evaluate(setting: Setting, key: String, user: ConfigCatUser?): Pair<Any, String?> {
        val infoLogBuilder = StringBuilder()
        try {
            if (user == null) {
                if (setting.rolloutRules.isNotEmpty() || setting.percentageItems.isNotEmpty()) {
                    logger.warning("UserObject missing! You should pass a UserObject to getValue() in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object.")
                }
                infoLogBuilder.appendLine("Returning ${setting.value}")
                return Pair(setting.value, setting.variationId)
            }
            infoLogBuilder.appendLine("User object: $user")
            if (setting.rolloutRules.isNotEmpty()) {
                for (rule in setting.rolloutRules) {
                    val userValue = user.attributeFor(rule.comparisonAttribute)
                    val comparisonValue = rule.comparisonValue
                    val attribute = rule.comparisonAttribute
                    val comparator = rule.comparator.toComparatorOrNull()
                    val returnValue = rule.value
                    val variationId = rule.variationId

                    if (comparator == null) {
                        infoLogBuilder.appendLine(
                            logComparatorError(
                                attribute,
                                userValue ?: "",
                                rule.comparator,
                                comparisonValue
                            )
                        )
                        continue
                    }
                    if (userValue == null || userValue.isEmpty() || comparisonValue.isEmpty()) {
                        infoLogBuilder.appendLine(logNoMatch(attribute, userValue ?: "", comparator, comparisonValue))
                        continue
                    }

                    when (comparator) {
                        Comparator.ONE_OF -> {
                            val split = comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            if (split.contains(userValue)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }
                        Comparator.NOT_ONE_OF -> {
                            val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            if (!split.contains(userValue)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }
                        Comparator.CONTAINS -> {
                            if (userValue.contains(comparisonValue)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }
                        Comparator.NOT_CONTAINS -> {
                            if (!userValue.contains(comparisonValue)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }

                        Comparator.ONE_OF_SEMVER,
                        Comparator.NOT_ONE_OF_SEMVER -> {
                            try {
                                val userVersion = userValue.toVersion()
                                val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                                var matched = false
                                for (value in split) {
                                    matched = value.toVersion() == userVersion || matched
                                }
                                if ((matched && comparator == Comparator.ONE_OF_SEMVER) || (!matched && comparator == Comparator.NOT_ONE_OF_SEMVER)) {
                                    infoLogBuilder.appendLine(
                                        logMatch(
                                            attribute,
                                            userValue,
                                            comparator,
                                            comparisonValue,
                                            returnValue
                                        )
                                    )
                                    return Pair(returnValue, variationId)
                                }
                            } catch (e: VersionFormatException) {
                                infoLogBuilder.appendLine(
                                    logFormatError(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        e
                                    )
                                )
                            }
                        }

                        Comparator.LT_SEMVER,
                        Comparator.LTE_SEMVER,
                        Comparator.GT_SEMVER,
                        Comparator.GTE_SEMVER -> {
                            try {
                                val userVersion = userValue.toVersion()
                                val comparisonVersion = comparisonValue.trim().toVersion()

                                if ((comparator == Comparator.LT_SEMVER && userVersion < comparisonVersion) ||
                                    (comparator == Comparator.LTE_SEMVER && userVersion <= comparisonVersion) ||
                                    (comparator == Comparator.GT_SEMVER && userVersion > comparisonVersion) ||
                                    (comparator == Comparator.GTE_SEMVER && userVersion >= comparisonVersion)
                                ) {
                                    infoLogBuilder.appendLine(
                                        logMatch(
                                            attribute,
                                            userValue,
                                            comparator,
                                            comparisonValue,
                                            returnValue
                                        )
                                    )
                                    return Pair(returnValue, variationId)
                                }
                            } catch (e: VersionFormatException) {
                                infoLogBuilder.appendLine(
                                    logFormatError(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        e
                                    )
                                )
                            }
                        }

                        Comparator.EQ_NUM,
                        Comparator.NOT_EQ_NUM,
                        Comparator.LT_NUM,
                        Comparator.LTE_NUM,
                        Comparator.GT_NUM,
                        Comparator.GTE_NUM -> {
                            try {
                                val userNumber = userValue.replace(",", ".").toDouble()
                                val comparisonNumber = comparisonValue.trim().replace(",", ".").toDouble()

                                if ((comparator == Comparator.EQ_NUM && userNumber == comparisonNumber) ||
                                    (comparator == Comparator.NOT_EQ_NUM && userNumber != comparisonNumber) ||
                                    (comparator == Comparator.LT_NUM && userNumber < comparisonNumber) ||
                                    (comparator == Comparator.LTE_NUM && userNumber <= comparisonNumber) ||
                                    (comparator == Comparator.GT_NUM && userNumber > comparisonNumber) ||
                                    (comparator == Comparator.GTE_NUM && userNumber >= comparisonNumber)
                                ) {
                                    infoLogBuilder.appendLine(
                                        logMatch(
                                            attribute,
                                            userValue,
                                            comparator,
                                            comparisonValue,
                                            returnValue
                                        )
                                    )
                                    return Pair(returnValue, variationId)
                                }
                            } catch (e: NumberFormatException) {
                                infoLogBuilder.appendLine(
                                    logFormatError(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        e
                                    )
                                )
                            }
                        }

                        Comparator.ONE_OF_SENS -> {
                            val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val userValueHash = userValue.encodeToByteArray().sha1().hex
                            if (split.contains(userValueHash)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }
                        Comparator.NOT_ONE_OF_SENS -> {
                            val split = rule.comparisonValue.split(",").map { it.trim() }.filter { it.isNotEmpty() }
                            val userValueHash = userValue.encodeToByteArray().sha1().hex
                            if (!split.contains(userValueHash)) {
                                infoLogBuilder.appendLine(
                                    logMatch(
                                        attribute,
                                        userValue,
                                        comparator,
                                        comparisonValue,
                                        returnValue
                                    )
                                )
                                return Pair(returnValue, variationId)
                            }
                        }
                    }
                }
            }

            if (setting.percentageItems.isNotEmpty()) {
                val hashCandidate = "$key${user.identifier}"
                val hash = hashCandidate.encodeToByteArray().sha1().hex.substring(0, 7)
                val numberRepresentation = hash.toInt(radix = 16)
                val scale = numberRepresentation % 100

                var bucket = 0.0
                for (rule in setting.percentageItems) {
                    bucket += rule.percentage
                    if (scale < bucket) {
                        infoLogBuilder.appendLine("Evaluating % options. Returning ${rule.value}.")
                        return Pair(rule.value, rule.variationId)
                    }
                }
            }

            infoLogBuilder.appendLine("Returning ${setting.value}.")
            return Pair(setting.value, setting.variationId)
        } finally {
            logger.info(infoLogBuilder.toString())
        }
    }

    private fun logMatch(
        attribute: String,
        userValue: String,
        comparator: Comparator,
        comparisonValue: String,
        value: Any?
    ): String {
        return "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => match, returning: $value"
    }

    private fun logNoMatch(
        attribute: String,
        userValue: String,
        comparator: Comparator,
        comparisonValue: String
    ): String {
        return "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => no match"
    }

    private fun logFormatError(
        attribute: String,
        userValue: String,
        comparator: Comparator,
        comparisonValue: String,
        error: Throwable
    ): String {
        val message =
            "Evaluating rule: [$attribute:$userValue] [${comparator.value}] [$comparisonValue] => SKIP rule. Validation error: ${error.message}"
        logger.warning(message)
        return message
    }

    private fun logComparatorError(
        attribute: String,
        userValue: String,
        comparator: Int,
        comparisonValue: String
    ): String {
        val message =
            "Evaluating rule: [$attribute:$userValue] [$comparisonValue] => SKIP rule. Invalid comparator: $comparator"
        logger.warning(message)
        return message
    }

    private enum class Comparator(val value: String) {
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
        NOT_ONE_OF_SENS("IS NOT ONE OF (Sensitive)"),
    }

    private fun Int.toComparatorOrNull(): Comparator? = Comparator.values().firstOrNull { it.ordinal == this }
}
