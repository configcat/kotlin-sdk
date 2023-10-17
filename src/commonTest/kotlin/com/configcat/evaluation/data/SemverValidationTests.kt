package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object SemverValidationTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/BAr3KgLTP0ObzKnBTo5nhA"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"L97Em9TI9z1D1A2CNVPkt7Ryjo40HT7R6sh/SMB+vQU="
           },
           "f":{
              "isOneOf":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "1.0.0",
                                   "2"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (1.0.0, 2)"
                          },
                          "i":"1e934047"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "1.0.0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (1.0.0)"
                          },
                          "i":"44342254"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "",
                                   "2.0.1",
                                   "2.0.2"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (   , 2.0.1, 2.0.2,    )"
                          },
                          "i":"90e3ef46"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3......"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3......)"
                          },
                          "i":"59523971"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3...."
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3...)"
                          },
                          "i":"2de217a1"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3..0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3..0)"
                          },
                          "i":"bf943c79"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3.0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3.0)"
                          },
                          "i":"3a6a8077"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3.0."
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3.0.)"
                          },
                          "i":"44f25fed"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "3.0.0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is one of (3.0.0)"
                          },
                          "i":"e77f5306"
                       }
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"c4ec4d53"
              },
              "isOneOfWithPercentage":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":4,
                                "l":[
                                   "1.0.0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"is one of (1.0.0)"
                          },
                          "i":"0ac4afc1"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":20,
                       "v":{
                          "s":"20%"
                       },
                       "i":"e25dba31"
                    },
                    {
                       "p":80,
                       "v":{
                          "s":"80%"
                       },
                       "i":"8c70c181"
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"a94ff896"
              },
              "isNotOneOf":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":5,
                                "l":[
                                   "1.0.0",
                                   "1.0.1",
                                   "2.0.0",
                                   "2.0.1",
                                   "2.0.2",
                                   ""
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )"
                          },
                          "i":"a8d5f278"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":5,
                                "l":[
                                   "1.0.0",
                                   "3.0.1"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is not one of (1.0.0, 3.0.1)"
                          },
                          "i":"54ac757f"
                       }
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"f79b763d"
              },
              "isNotOneOfWithPercentage":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":5,
                                "l":[
                                   "1.0.0",
                                   "1.0.1",
                                   "2.0.0",
                                   "2.0.1",
                                   "2.0.2",
                                   ""
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )"
                          },
                          "i":"9bf9e66f"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":5,
                                "l":[
                                   "1.0.0",
                                   "3.0.1"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Is not one of (1.0.0, 3.0.1)"
                          },
                          "i":"bfc1a544"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":20,
                       "v":{
                          "s":"20%"
                       },
                       "i":"68f652f0"
                    },
                    {
                       "p":80,
                       "v":{
                          "s":"80%"
                       },
                       "i":"b8d926e0"
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"b9614bad"
              },
              "lessThanWithPercentage":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":6,
                                "s":"1.0.0"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"< 1.0.0"
                          },
                          "i":"0c27d053"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":20,
                       "v":{
                          "s":"20%"
                       },
                       "i":"3b1fde2a"
                    },
                    {
                       "p":80,
                       "v":{
                          "s":"80%"
                       },
                       "i":"42e92759"
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"0081c525"
              },
              "relations":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":6,
                                "s":"1.0.0,"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<1.0.0,"
                          },
                          "i":"21b31b61"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":6,
                                "s":"1.0.0"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"< 1.0.0"
                          },
                          "i":"db3ddb7d"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":7,
                                "s":"1.0.0"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<=1.0.0"
                          },
                          "i":"aa2c7493"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":8,
                                "s":"2.0.0"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":">2.0.0"
                          },
                          "i":"5e47a1ea"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":9,
                                "s":"2.0.0"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":">=2.0.0"
                          },
                          "i":"99482756"
                       }
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"c6155773"
              }
           }
        }
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "isNotOneOf",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "wrong_semver")),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 IS NOT ONE OF ['1.0.0', '1.0.1', '2.0.0', '2.0.1', '2.0.2', '']) for setting 'isNotOneOf' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 IS NOT ONE OF ['1.0.0', '3.0.1']) for setting 'isNotOneOf' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'isNotOneOf' for User '{"Identifier":"12345","Custom1":"wrong_semver"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 IS NOT ONE OF ['1.0.0', '1.0.1', '2.0.0', '2.0.1', '2.0.2', ''] THEN 'Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 IS NOT ONE OF ['1.0.0', '3.0.1'] THEN 'Is not one of (1.0.0, 3.0.1)' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'."""
        ),
        TestCase(
            key = "relations",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "wrong_semver")),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 < '1.0.0,') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 < '1.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 <= '1.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 > '2.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
WARNING [3004] Cannot evaluate condition (User.Custom1 >= '2.0.0') for setting 'relations' ('wrong_semver' is not a valid semantic version). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'relations' for User '{"Identifier":"12345","Custom1":"wrong_semver"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 < '1.0.0,' THEN '<1.0.0,' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 < '1.0.0' THEN '< 1.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 <= '1.0.0' THEN '<=1.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 > '2.0.0' THEN '>2.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF User.Custom1 >= '2.0.0' THEN '>=2.0.0' => cannot evaluate, the User.Custom1 attribute is invalid ('wrong_semver' is not a valid semantic version)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'."""
        )
    )
}
