package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OneTargetingRuleTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"pkw2BWOIXiTrXO53/OPECHP9OeJzmW8y/yV47\u002BQ8HLM="
           },
           "f":{
              "bool30TrueAdvancedRules":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":0,
                                "l":[
                                   "a@configcat.com",
                                   "b@configcat.com"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":false
                          },
                          "i":"385d9803"
                       }
                    },
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Country",
                                "c":2,
                                "l":[
                                   "United"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":false
                          },
                          "i":"385d9803"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":30,
                       "v":{
                          "b":true
                       },
                       "i":"607147d5"
                    },
                    {
                       "p":70,
                       "v":{
                          "b":false
                       },
                       "i":"385d9803"
                    }
                 ],
                 "v":{
                    "b":true
                 },
                 "i":"607147d5"
              },
              "boolDefaultFalse":{
                 "t":0,
                 "v":{
                    "b":false
                 },
                 "i":"489a16d2"
              },
              "boolDefaultTrue":{
                 "t":0,
                 "v":{
                    "b":true
                 },
                 "i":"09513143"
              },
              "double25Pi25E25Gr25Zero":{
                 "t":3,
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
                       "s":{
                          "v":{
                             "d":5.561
                          },
                          "i":"3f7826de"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":25,
                       "v":{
                          "d":3.1415
                       },
                       "i":"6d75b4d3"
                    },
                    {
                       "p":25,
                       "v":{
                          "d":2.7182
                       },
                       "i":"183ee713"
                    },
                    {
                       "p":25,
                       "v":{
                          "d":1.61803
                       },
                       "i":"01eb6326"
                    },
                    {
                       "p":25,
                       "v":{
                          "d":0
                       },
                       "i":"64c434ff"
                    }
                 ],
                 "v":{
                    "d":-1
                 },
                 "i":"9503a1de"
              },
              "doubleDefaultPi":{
                 "t":3,
                 "v":{
                    "d":3.1415
                 },
                 "i":"5af8acc7"
              },
              "integer25One25Two25Three25FourAdvancedRules":{
                 "t":2,
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
                       "s":{
                          "v":{
                             "i":5
                          },
                          "i":"58136ba2"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":25,
                       "v":{
                          "i":1
                       },
                       "i":"11634414"
                    },
                    {
                       "p":25,
                       "v":{
                          "i":2
                       },
                       "i":"5530655d"
                    },
                    {
                       "p":25,
                       "v":{
                          "i":3
                       },
                       "i":"2ad19a52"
                    },
                    {
                       "p":25,
                       "v":{
                          "i":4
                       },
                       "i":"41b30851"
                    }
                 ],
                 "v":{
                    "i":-1
                 },
                 "i":"ce3c4f5a"
              },
              "integerDefaultOne":{
                 "t":2,
                 "v":{
                    "i":1
                 },
                 "i":"faadbf54"
              },
              "keySampleText":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Country",
                                "c":0,
                                "l":[
                                   "Hungary",
                                   "Bahamas"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"9fa0e57e"
                       }
                    },
                    {
                       "c":[
                          {
                             "u":{
                                "a":"SubscriptionType",
                                "c":0,
                                "l":[
                                   "unlimited"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Lion"
                          },
                          "i":"2be6b03f"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":50,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"baff2362"
                    },
                    {
                       "p":50,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"dab78ba5"
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"69ef126c"
              },
              "string25Cat25Dog25Falcon25Horse":{
                 "t":1,
                 "p":[
                    {
                       "p":25,
                       "v":{
                          "s":"Cat"
                       },
                       "i":"d227b334"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Dog"
                       },
                       "i":"622f5d07"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"0ff32bab"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"6c597441"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"2588a3e6"
              },
              "string25Cat25Dog25Falcon25HorseAdvancedRules":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Country",
                                "c":0,
                                "l":[
                                   "Hungary",
                                   "United Kingdom"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dolphin"
                          },
                          "i":"3accb1d0"
                       }
                    },
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Custom1",
                                "c":2,
                                "l":[
                                   "admi"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Lion"
                          },
                          "i":"e95ebf10"
                       }
                    },
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
                       "s":{
                          "v":{
                             "s":"Kitten"
                          },
                          "i":"88243650"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":25,
                       "v":{
                          "s":"Cat"
                       },
                       "i":"83461b47"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Dog"
                       },
                       "i":"4f026fbc"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"392a4d59"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"bb66b1f3"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"8250ef5a"
              },
              "string75Cat0Dog25Falcon0Horse":{
                 "t":1,
                 "p":[
                    {
                       "p":75,
                       "v":{
                          "s":"Cat"
                       },
                       "i":"93f5a1c0"
                    },
                    {
                       "p":0,
                       "v":{
                          "s":"Dog"
                       },
                       "i":"b8f49554"
                    },
                    {
                       "p":25,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"7beaf504"
                    },
                    {
                       "p":0,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"30ee31af"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"aa65b5ce"
              },
              "stringContainsDogDefaultCat":{
                 "t":1,
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
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"d0cd8f06"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"ce564c3a"
              },
              "stringDefaultCat":{
                 "t":1,
                 "v":{
                    "s":"Cat"
                 },
                 "i":"7a0be518"
              },
              "stringIsInDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":0,
                                "l":[
                                   "a@configcat.com",
                                   "b@configcat.com"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"5b64d9b4"
                       }
                    },
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Custom1",
                                "c":0,
                                "l":[
                                   "admin"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"5b64d9b4"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"83372510"
              },
              "stringIsNotInDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":1,
                                "l":[
                                   "a@configcat.com",
                                   "b@configcat.com"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"6ada5ff2"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"2459598d"
              },
              "stringNotContainsDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Email",
                                "c":3,
                                "l":[
                                   "@configcat.com"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"f7f8f43d"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"44ab483a"
              }
           }
        }
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'stringContainsDogDefaultCat' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsDogDefaultCat'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.""",
            user = null
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email CONTAINS ANY OF ['@configcat.com']) for setting 'stringContainsDogDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'."""
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@example.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345","Email":"joe@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => no match
  Returning 'Cat'."""
        ),
        TestCase(
            key = "stringContainsDogDefaultCat",
            defaultValue = "default",
            returnValue = "Dog",
            user = ConfigCatUser("12345", "joe@configcat.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsDogDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN 'Dog' => MATCH, applying rule
  Returning 'Dog'."""
        )
    )
}
