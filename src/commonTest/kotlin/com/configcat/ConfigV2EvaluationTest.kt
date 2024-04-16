package com.configcat

import com.configcat.evaluation.EvaluationTestLogger
import com.configcat.evaluation.LogEvent
import com.configcat.log.LogLevel
import com.configcat.override.OverrideBehavior
import com.configcat.override.OverrideDataSource
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.util.PlatformUtils
import korlibs.time.DateTime
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigV2EvaluationTest {
    @Test
    fun circularDependencyTest() =
        runTest {
            runCircularDependencyTest("key1", "'key1' -> 'key1'")
            runCircularDependencyTest("key2", "'key2' -> 'key3' -> 'key2'")
            runCircularDependencyTest("key4", "'key4' -> 'key3' -> 'key2' -> 'key3'")
        }

    @Test
    fun ruleAndPercentageOptionTest() =
        runTest {
            runRuleAndPercentageOptionTest(
                "12345",
                null,
                null,
                "Cat",
                expectedTargetingRule = false,
                expectedPercentageOption = false,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "a@example.com",
                null,
                "Dog",
                expectedTargetingRule = true,
                expectedPercentageOption = false,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "a@configcat.com",
                null,
                "Cat",
                expectedTargetingRule = false,
                expectedPercentageOption = false,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "a@configcat.com",
                "",
                "Frog",
                expectedTargetingRule = true,
                expectedPercentageOption = true,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "a@configcat.com",
                "US",
                "Fish",
                expectedTargetingRule = true,
                expectedPercentageOption = true,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "b@configcat.com",
                null,
                "Cat",
                expectedTargetingRule = false,
                expectedPercentageOption = false,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "b@configcat.com",
                "",
                "Falcon",
                expectedTargetingRule = false,
                expectedPercentageOption = true,
            )
            runRuleAndPercentageOptionTest(
                "12345",
                "b@configcat.com",
                "US",
                "Spider",
                expectedTargetingRule = false,
                expectedPercentageOption = true,
            )
        }

    @Test
    fun prerequisiteFlagTypeMismatchTest() =
        runTest {
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                return@runTest
            }
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnBool", "mainBoolFlag", true, "Dog")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnBool", "mainBoolFlag", false, "Cat")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnBool", "mainBoolFlag", "1", "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnBool", "mainBoolFlag", 1, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnBool", "mainBoolFlag", 1.0, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnString", "mainStringFlag", "private", "Dog")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnString", "mainStringFlag", "Private", "Cat")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnString", "mainStringFlag", true, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnString", "mainStringFlag", 1, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnString", "mainStringFlag", 1.0, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", 2, "Dog")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", 1, "Cat")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", "2", "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", true, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", 2.0, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 0.1, "Dog")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 0.11, "Cat")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", "0.1", "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", true, "")
            runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 1, "")
        }

    @Test
    fun prerequisiteFlagOverrideTest() =
        runTest {
            runPrerequisiteFlagOverrideTest("stringDependsOnString", "1", "john@sensitivecompany.com", null, "Dog")

            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.REMOTE_OVER_LOCAL,
                "Dog",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.LOCAL_OVER_REMOTE,
                "Dog",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.LOCAL_ONLY,
                "",
            )
            runPrerequisiteFlagOverrideTest("stringDependsOnString", "2", "john@notsensitivecompany.com", null, "Cat")
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "2",
                "john@notsensitivecompany.com",
                OverrideBehavior.REMOTE_OVER_LOCAL,
                "Cat",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "2",
                "john@notsensitivecompany.com",
                OverrideBehavior.LOCAL_OVER_REMOTE,
                "Dog",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnString",
                "2",
                "john@notsensitivecompany.com",
                OverrideBehavior.LOCAL_ONLY,
                "",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@sensitivecompany.com",
                null,
                "Dog",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.REMOTE_OVER_LOCAL,
                "Dog",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.LOCAL_OVER_REMOTE,
                "Falcon",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@sensitivecompany.com",
                OverrideBehavior.LOCAL_ONLY,
                "Falcon",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@notsensitivecompany.com",
                null,
                "Cat",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@notsensitivecompany.com",
                OverrideBehavior.REMOTE_OVER_LOCAL,
                "Cat",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@notsensitivecompany.com",
                OverrideBehavior.LOCAL_OVER_REMOTE,
                "Falcon",
            )
            runPrerequisiteFlagOverrideTest(
                "stringDependsOnInt",
                "1",
                "john@notsensitivecompany.com",
                OverrideBehavior.LOCAL_ONLY,
                "Falcon",
            )
        }

    @Test
    fun runComparisonAttributeConversionToCanonicalStringRepresentationTest() =
        runTest {
            runComparisonAttributeConversionToCanonicalStringRepresentationTest("numberToStringConversion", .12345, "1")
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionInt",
                125.toByte(),
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionInt",
                125.toShort(),
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest("numberToStringConversionInt", 125, "4")
            runComparisonAttributeConversionToCanonicalStringRepresentationTest("numberToStringConversionInt", 125L, "4")
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionPositiveExp",
                -1.23456789e96,
                "2",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNegativeExp",
                -12345.6789E-100,
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNaN",
                Double.NaN,
                "3",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionPositiveInf",
                Double.POSITIVE_INFINITY,
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNegativeInf",
                Double.NEGATIVE_INFINITY,
                "3",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionPositiveExp",
                -1.23456789e96,
                "2",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNegativeExp",
                -12345.6789E-100,
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNaN",
                Float.NaN,
                "3",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionPositiveInf",
                Float.POSITIVE_INFINITY,
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "numberToStringConversionNegativeInf",
                Float.NEGATIVE_INFINITY,
                "3",
            )
            if (!PlatformUtils.IS_NATIVE) {
                // Native number format converts the double value to scientific notation causes a fail in these test cases
                runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                    "dateToStringConversion",
                    "date:2023-03-31T23:59:59.999Z",
                    "3",
                )
                runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                    "dateToStringConversion",
                    1680307199.999,
                    "3",
                )
            }
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "dateToStringConversionNaN",
                Double.NaN,
                "3",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "dateToStringConversionPositiveInf",
                Double.POSITIVE_INFINITY,
                "1",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "dateToStringConversionNegativeInf",
                Double.NEGATIVE_INFINITY,
                "5",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "stringArrayToStringConversion",
                arrayOf("read", "Write", " eXecute "),
                "4",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "stringArrayToStringConversionEmpty",
                arrayOfNulls<String>(0),
                "5",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "stringArrayToStringConversionSpecialChars",
                arrayOf("+<>%\"'\\/\t\r\n"),
                "3",
            )
            runComparisonAttributeConversionToCanonicalStringRepresentationTest(
                "stringArrayToStringConversionUnicode",
                arrayOf("äöüÄÖÜçéèñışğâ¢™✓\uD83D\uDE00"),
                "2",
            )
        }

    private suspend fun runRuleAndPercentageOptionTest(
        userId: String,
        email: String?,
        percentageBaseCustom: String?,
        expectedValue: String?,
        expectedTargetingRule: Boolean?,
        expectedPercentageOption: Boolean?,
    ) {
        val mockEngine =
            MockEngine {
                respond(content = ruleOrOptionRemoteJson, status = HttpStatusCode.OK)
            }
        val client =
            ConfigCatClient("configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/P4e3fAz_1ky2-Zg2e4cbkw") {
                pollingMode = manualPoll()
                httpEngine = mockEngine
                logLevel = LogLevel.ERROR
            }
        client.forceRefresh()

        val customAttributes = mutableMapOf<String, Any>()
        if (percentageBaseCustom != null) {
            customAttributes["PercentageBase"] = percentageBaseCustom
        }
        val configCatUser = ConfigCatUser(userId, email, null, customAttributes)

        val result = client.getValueDetails("stringMatchedTargetingRuleAndOrPercentageOption", "", configCatUser)

        assertEquals(expectedValue, result.value)
        assertEquals(expectedTargetingRule, result.matchedTargetingRule != null)
        assertEquals(expectedPercentageOption, result.matchedPercentageOption != null)

        ConfigCatClient.closeAll()
    }

    private suspend fun runCircularDependencyTest(
        key: String,
        dependencyCycle: String,
    ) {
        val mockEngine =
            MockEngine {
                respond(content = circularDependencyTestRemoteJson, status = HttpStatusCode.OK)
            }
        val client =
            ConfigCatClient(Data.SDK_KEY) {
                pollingMode = manualPoll()
                httpEngine = mockEngine
                logLevel = LogLevel.ERROR
            }
        client.forceRefresh()

        val valueDetails = client.getValueDetails(key, "", null)
        assertEquals(
            "Circular dependency detected between the following depending flags: $dependencyCycle.",
            valueDetails.error,
        )

        ConfigCatClient.closeAll()
    }

    private suspend fun runPrerequisiteFlagTypeMismatchTest(
        key: String,
        prerequisiteFlagKey: String,
        prerequisiteFlagValue: Any,
        expectedValue: String?,
    ) {
        val evaluationTestLogger = EvaluationTestLogger()

        val mockEngine =
            MockEngine {
                respond(content = prerequisiteFlagMismatchRemoteJson, status = HttpStatusCode.OK)
            }
        val flagOverrideMap = mutableMapOf<String, Any>()
        flagOverrideMap[prerequisiteFlagKey] = prerequisiteFlagValue

        val client =
            ConfigCatClient("configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/JoGwdqJZQ0K2xDy7LnbyOg") {
                pollingMode = manualPoll()
                configCache = SingleValueCache("")
                httpEngine = mockEngine
                logLevel = LogLevel.ERROR
                logger = evaluationTestLogger
                flagOverrides = {
                    behavior = OverrideBehavior.LOCAL_OVER_REMOTE
                    dataSource =
                        OverrideDataSource.map(
                            flagOverrideMap,
                        )
                }
            }
        client.forceRefresh()

        val value = client.getValue(key, "", null)
        val errorLogs = mutableListOf<LogEvent>()
        assertEquals(
            expectedValue,
            value,
            "Flag key: $key PrerequisiteFlagKey: $prerequisiteFlagKey PrerequisiteFlagValue: $prerequisiteFlagValue",
        )
        if (expectedValue.isNullOrEmpty()) {
            val logsList = evaluationTestLogger.getLogList()
            for (i in logsList.indices) {
                val log = logsList[i]
                if (log.logLevel == LogLevel.ERROR) {
                    errorLogs.add(log)
                }
            }
            assertEquals(1, errorLogs.size, "Error size not matching")
            val errorMessage: String = errorLogs[0].logMessage
            assertContains(errorMessage, "[1002]")

            assertContains(errorMessage, "Type mismatch between comparison value")

            evaluationTestLogger.resetLogList()
        }

        ConfigCatClient.closeAll()
    }

    private suspend fun runPrerequisiteFlagOverrideTest(
        key: String,
        userId: String?,
        email: String?,
        overrideBehaviour: OverrideBehavior?,
        expectedValue: Any?,
    ) {
        var user: ConfigCatUser? = null
        if (userId != null) {
            user = ConfigCatUser(identifier = userId, email = email)
        }
        val overrideMap = mutableMapOf<String, Any>()
        overrideMap["mainStringFlag"] = "private"
        overrideMap["stringDependsOnInt"] = "Falcon"

        val mockEngine =
            MockEngine {
                respond(content = prerequisiteFlagMismatchRemoteJson, status = HttpStatusCode.OK)
            }

        val client =
            ConfigCatClient("configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/JoGwdqJZQ0K2xDy7LnbyOg") {
                pollingMode = manualPoll()
                httpEngine = mockEngine
                if (overrideBehaviour != null) {
                    flagOverrides = {
                        behavior = overrideBehaviour
                        dataSource =
                            OverrideDataSource.map(
                                overrideMap,
                            )
                    }
                }
            }
        client.forceRefresh()

        val value = client.getValue(key, "", user)

        assertEquals(expectedValue, value)

        ConfigCatClient.closeAll()
    }

    private suspend fun runComparisonAttributeConversionToCanonicalStringRepresentationTest(
        key: String,
        userAttribute: Any,
        expectedValue: String,
    ) {
        val mockEngine =
            MockEngine {
                respond(
                    content = comparisionAttributeConversionRemoteJson,
                    status = HttpStatusCode.OK,
                )
            }
        val client =
            ConfigCatClient(Data.SDK_KEY) {
                httpEngine = mockEngine
            }
        val userAttributeToMap: Any =
            if (userAttribute is String && userAttribute.startsWith("date:")) {
                DateTime.fromString(userAttribute.substring(5))
            } else {
                userAttribute
            }
        val customMap = mutableMapOf<String, Any>()
        customMap["Custom1"] = userAttributeToMap

        val user = ConfigCatUser(identifier = "12345", custom = customMap)

        val result: String = client.getValue(key, "default", user)

        assertEquals(expectedValue, result)

        ConfigCatClient.closeAll()
    }

    private val circularDependencyTestRemoteJson =
        """
        {
          "p": {
            "u": "https://cdn-global.configcat.com",
            "r": 0,
            "s": "test-salt"
          },
          "f": {
            "key1": {
              "t": 1,
              "v": { "s": "key1-value" },
              "r": [
                {
                  "c": [
                    {
                      "p": {
                        "f": "key1",
                        "c": 0,
                        "v": { "s": "key1-prereq" }
                      }
                    }
                  ],
                  "s": { "v": { "s": "key1-prereq" } }
                }
              ]
            },
            "key2": {
              "t": 1,
              "v": { "s": "key2-value" },
              "r": [
                {
                  "c": [
                    {
                      "p": {
                        "f": "key3",
                        "c": 0,
                        "v": { "s": "key3-prereq" }
                      }
                    }
                  ],
                  "s": { "v": { "s": "key2-prereq" } }
                }
              ]
            },
            "key3": {
              "t": 1,
              "v": { "s": "key3-value" },
              "r": [
                {
                  "c": [
                    {
                      "p": {
                        "f": "key2",
                        "c": 0,
                        "v": { "s": "key2-prereq" }
                      }
                    }
                  ],
                  "s": { "v": { "s": "key3-prereq" } }
                }
              ]
            },
            "key4": {
              "t": 1,
              "v": { "s": "key4-value" },
              "r": [
                {
                  "c": [
                    {
                      "p": {
                        "f": "key3",
                        "c": 0,
                        "v": { "s": "key3-prereq" }
                      }
                    }
                  ],
                  "s": { "v": { "s": "key4-prereq" } }
                }
              ]
            }
          }
        }
        """.trimIndent()

    private val ruleOrOptionRemoteJson =
        """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"tpaRmJHutF5/zEKQVFTXvZ\u002BvFTT5BO28cJh9vbb\u002BNOE="
           },
           "f":{
              "dependentFlag":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"key1",
                                "c":0,
                                "v":{
                                   "s":"value1"
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Chicken"
                          },
                          "i":"5916066a"
                       }
                    },
                    {
                       "c":[
                          {
                             "p":{
                                "f":"key1",
                                "c":0,
                                "v":{
                                   "s":"value1"
                                }
                             }
                          },
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "12_6f86a4abfc7c40270d03abde842b48c426c1b03f1e59824df508ea2d4bba8eb8"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Cat"
                          },
                          "i":"a4346a91"
                       }
                    }
                 ],
                 "v":{
                    "s":"dependentFlag"
                 },
                 "i":"e0afe4ca"
              },
              "key1":{
                 "t":1,
                 "v":{
                    "s":"value1"
                 },
                 "i":"1605ae93"
              },
              "string75Cat0Dog25Falcon0HorseCustomAttr":{
                 "t":1,
                 "a":"Country",
                 "p":[
                    {
                       "p":75,
                       "v":{
                          "s":"Cat"
                       },
                       "i":"8285ed60"
                    },
                    {
                       "p":0,
                       "v":{
                          "s":"Dog"
                       },
                       "i":"597e1dd1"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"8896564a"
                    },
                    {
                       "p":0,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"d1944e2c"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"13ad5bbc"
              },
              "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat":{
                 "t":1,
                 "a":"Country",
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":2,
                                "l":[
                                   "@configcat.com"
                                ]
                             }
                          }
                       ],
                       "p":[
                          {
                             "p":75,
                             "v":{
                                "s":"Cat"
                             },
                             "i":"05a1d8f3"
                          },
                          {
                             "p":0,
                             "v":{
                                "s":"Dog"
                             },
                             "i":"52a42c84"
                          },
                          {
                             "p":25,
                             "v":{
                                "s":"Falcon"
                             },
                             "i":"06c2db91"
                          },
                          {
                             "p":0,
                             "v":{
                                "s":"Horse"
                             },
                             "i":"fe226091"
                          }
                       ]
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"05a1d8f3"
              },
              "stringMatchedTargetingRuleAndOrPercentageOption":{
                 "t":1,
                 "a":"PercentageBase",
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":32,
                                "l":[
                                   "@example.com"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"7c01f064"
                       }
                    },
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":30,
                                "l":[
                                   "a@"
                                ]
                             }
                          }
                       ],
                       "p":[
                          {
                             "p":50,
                             "v":{
                                "s":"Frog"
                             },
                             "i":"8e2d8a91"
                          },
                          {
                             "p":50,
                             "v":{
                                "s":"Fish"
                             },
                             "i":"7c67b71b"
                          }
                       ]
                    }
                 ],
                 "p":[
                    {
                       "p":50,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"9e644055"
                    },
                    {
                       "p":0,
                       "v":{
                          "s":"Chicken"
                       },
                       "i":"ceeb332c"
                    },
                    {
                       "p":50,
                       "v":{
                          "s":"Spider"
                       },
                       "i":"fec43740"
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"7d4140ce"
              }
           }
        }
        """.trimIndent()

    // Used for prerequisiteFlag Override as well
    private val prerequisiteFlagMismatchRemoteJson =
        """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"PBMv8zBDvXO9ZObbLwsP5TQOsgn8aOv1K3\u002BxPFJCoAU="
           },
           "f":{
              "boolDependsOnBool":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlag",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":true
                          },
                          "i":"8dc94c1d"
                       }
                    }
                 ],
                 "v":{
                    "b":false
                 },
                 "i":"d6194760"
              },
              "boolDependsOnBoolDependsOnBool":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"boolDependsOnBool",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":false
                          },
                          "i":"d6870486"
                       }
                    }
                 ],
                 "v":{
                    "b":true
                 },
                 "i":"cd4c95e7"
              },
              "boolDependsOnBoolInverse":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlagInverse",
                                "c":1,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":true
                          },
                          "i":"3c09bff0"
                       }
                    }
                 ],
                 "v":{
                    "b":false
                 },
                 "i":"cecbc501"
              },
              "doubleDependsOnBool":{
                 "t":3,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlag",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "d":1.1
                          },
                          "i":"271fd003"
                       }
                    }
                 ],
                 "v":{
                    "d":3.14
                 },
                 "i":"718aae2b"
              },
              "intDependsOnBool":{
                 "t":2,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlag",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "i":1
                          },
                          "i":"d2dda649"
                       }
                    }
                 ],
                 "v":{
                    "i":42
                 },
                 "i":"43ec49a8"
              },
              "mainBoolFlag":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_32abe94b0866402b226383eb666a98312dc898119e2a9241ffbfcc114eb6a57b"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":false
                          },
                          "i":"e842ea6f"
                       }
                    }
                 ],
                 "v":{
                    "b":true
                 },
                 "i":"8a68b064"
              },
              "mainBoolFlagEmpty":{
                 "t":0,
                 "v":{
                    "b":true
                 },
                 "i":"f3295d43"
              },
              "mainBoolFlagInverse":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_69627ce988f31d14807ed75022d5325645914dadc3bfe7cdc1b6dbeca8763b67"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":true
                          },
                          "i":"28c65f1f"
                       }
                    }
                 ],
                 "v":{
                    "b":false
                 },
                 "i":"d70e47a7"
              },
              "mainDoubleFlag":{
                 "t":3,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_4cb521a31b1b604875ec3c7c90553a7cb692434f9aee8a318215f9bf1165f0e3"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "d":0.1
                          },
                          "i":"a67947ed"
                       }
                    }
                 ],
                 "v":{
                    "d":3.14
                 },
                 "i":"beb3acc7"
              },
              "mainIntFlag":{
                 "t":2,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_0ad4d095ab7ae197936c7dde2a53e55b2df616c0845c9b216ade6f14b2a4cf3d"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "i":2
                          },
                          "i":"67e14078"
                       }
                    }
                 ],
                 "v":{
                    "i":42
                 },
                 "i":"a7490aca"
              },
              "mainStringFlag":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_78d8c5a677414bd170650ec60b51e9325663ef8447b280862ec52be49cca7b0f"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"private"
                          },
                          "i":"51b57fb0"
                       }
                    }
                 ],
                 "v":{
                    "s":"public"
                 },
                 "i":"24c96275"
              },
              "stringDependsOnBool":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlag",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"fc8daf80"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"d53a2b42"
              },
              "stringDependsOnDouble":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainDoubleFlag",
                                "c":0,
                                "v":{
                                   "d":0.1
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"84fc7ed9"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"9cc8fd8f"
              },
              "stringDependsOnDoubleIntValue":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainDoubleFlag",
                                "c":0,
                                "v":{
                                   "d":0
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"842c1d75"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"db7f56c8"
              },
              "stringDependsOnEmptyBool":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlagEmpty",
                                "c":0,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"EmptyOn"
                          },
                          "i":"d5508c78"
                       }
                    }
                 ],
                 "v":{
                    "s":"EmptyOff"
                 },
                 "i":"8e0dbe88"
              },
              "stringDependsOnInt":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainIntFlag",
                                "c":0,
                                "v":{
                                   "i":2
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"12531eec"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"e227d926"
              },
              "stringDependsOnString":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainStringFlag",
                                "c":0,
                                "v":{
                                   "s":"private"
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"426b6d4d"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"d36000e1"
              },
              "stringDependsOnStringCaseCheck":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainStringFlag",
                                "c":0,
                                "v":{
                                   "s":"Private"
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"87d24aed"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"ad94f385"
              },
              "stringInverseDependsOnEmptyBool":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"mainBoolFlagEmpty",
                                "c":1,
                                "v":{
                                   "b":true
                                }
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"EmptyOff"
                          },
                          "i":"b7c3efae"
                       }
                    }
                 ],
                 "v":{
                    "s":"EmptyOn"
                 },
                 "i":"f6b4b8a2"
              }
           }
        }
        """.trimIndent()

    private val comparisionAttributeConversionRemoteJson = """
        {
  "p": {
    "u": "https://test-cdn-global.configcat.com",
    "r": 0,
    "s": "uM29sy1rjx71ze3ehr\u002BqCnoIpx8NZgL8V//MN7OL1aM="
  },
  "f": {
    "numberToStringConversion": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "0.12345"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionInt": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "125"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionPositiveExp": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "-1.23456789e+96"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionNegativeExp": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "-1.23456789e-96"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionNaN": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "NaN"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionPositiveInf": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "Infinity"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "numberToStringConversionNegativeInf": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "-Infinity"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "dateToStringConversion": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "1680307199.999"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "dateToStringConversionNaN": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "NaN"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "dateToStringConversionPositiveInf": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "Infinity"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "dateToStringConversionNegativeInf": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "-Infinity"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "stringArrayToStringConversion": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "[\"read\",\"Write\",\" eXecute \"]"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "stringArrayToStringConversionEmpty": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "[]"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "stringArrayToStringConversionSpecialChars": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "[\"+<>%\\\"'\\\\/\\t\\r\\n\"]"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    },
    "stringArrayToStringConversionUnicode": {
      "t": 1,
      "a": "Custom1",
      "r": [
        {
          "c": [
            {
              "u": {
                "a": "Custom1",
                "c": 28,
                "s": "[\"äöüÄÖÜçéèñışğâ¢™✓😀\"]"
              }
            }
          ],
          "p": [
            {
              "p": 20,
              "v": {
                "s": "1"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "2"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "3"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "4"
              }
            },
            {
              "p": 20,
              "v": {
                "s": "5"
              }
            }
          ]
        }
      ],
      "v": {
        "s": "0"
      },
      "i": "test-variation-id"
    }
  }
}
    """
}
