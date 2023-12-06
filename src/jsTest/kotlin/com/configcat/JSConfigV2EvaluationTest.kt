package com.configcat

import com.configcat.evaluation.EvaluationTestLogger
import com.configcat.evaluation.LogEvent
import com.configcat.log.LogLevel
import com.configcat.override.OverrideBehavior
import com.configcat.override.OverrideDataSource
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class JSConfigV2EvaluationTest {

    // This is the same test cases as the ConfigV2EvaluationTest with different expected results.
    // The JS format the double differently, then the other platforms
    @Test
    fun prerequisiteFlagTypeMismatchTest() = runTest {
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
        // in js the Double converted to Int and vice versa 2.0 == 2 and 2 results Dog
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnInt", "mainIntFlag", 2.0, "Dog")
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 0.1, "Dog")
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 0.11, "Cat")
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", "0.1", "")
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", true, "")
        // in js the Double converted to Int and vice versa 1 == 1.0 and 1.0 results Dog
        runPrerequisiteFlagTypeMismatchTest("stringDependsOnDouble", "mainDoubleFlag", 1, "Cat")
    }

    private suspend fun runPrerequisiteFlagTypeMismatchTest(
        key: String,
        prerequisiteFlagKey: String,
        prerequisiteFlagValue: Any,
        expectedValue: String?
    ) {
        val evaluationTestLogger = EvaluationTestLogger()

        val mockEngine = MockEngine {
            respond(content = prerequisiteFlagMismatchRemoteJson, status = HttpStatusCode.OK)
        }
        val flagOverrideMap = mutableMapOf<String, Any>()
        flagOverrideMap[prerequisiteFlagKey] = prerequisiteFlagValue

        val client = ConfigCatClient("configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/JoGwdqJZQ0K2xDy7LnbyOg") {
            pollingMode = manualPoll()
            configCache = SingleValueCache("")
            httpEngine = mockEngine
            logLevel = LogLevel.ERROR
            logger = evaluationTestLogger
            flagOverrides = {
                behavior = OverrideBehavior.LOCAL_OVER_REMOTE
                dataSource = OverrideDataSource.map(
                    flagOverrideMap
                )
            }
        }
        client.forceRefresh()

        val value = client.getValue(key, "", null)
        var errorLogs = mutableListOf<LogEvent>()
        assertEquals(expectedValue, value, "Flag key: $key PrerequisiteFlagKey: $prerequisiteFlagKey PrerequisiteFlagValue: $prerequisiteFlagValue")
        if (expectedValue.isNullOrEmpty()) {
            val logsList = evaluationTestLogger.getLogList()
            for (i in logsList.indices) {
                var log = logsList[i]
                if (log.logLevel == LogLevel.ERROR) {
                    errorLogs.add(log)
                }
            }
            assertEquals(1, errorLogs.size, "Error size not matching")
            val errorMessage: String = errorLogs[0].logMessage
            assertContains(errorMessage, "[1002]")

            if (prerequisiteFlagValue == null) {
                assertContains(errorMessage, "Setting value is null")
            } else {
                assertContains(errorMessage, "Type mismatch between comparison value")
            }

            evaluationTestLogger.resetLogList()
        }

        ConfigCatClient.closeAll()
    }

    private val prerequisiteFlagMismatchRemoteJson = """
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
}
