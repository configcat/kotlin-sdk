package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object SegmenTests : TestSet {
    override val sdkKey = "configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/y_ZB7o-Xb0Swxth-ZlMSeA"
    override val baseUrl = null
    override val jsonOverride = """
{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":0,
      "s":"UZWYnRWPwF7hApMquVrUmyPRGziigICYz372JOYqXgw="
   },
   "s":[
      {
         "n":"Beta users",
         "r":[
            {
               "a":"Email",
               "c":16,
               "l":[
                  "89f6d080752f2969b6802c399e6141885c4ce40fb151f41b9ec955c1f4790490",
                  "2dde8bd2436cb07d45fb455847f8a09ea2427313c278b3352a39db31e6106c4c"
               ]
            }
         ]
      },
      {
         "n":"Beta users (cleartext)",
         "r":[
            {
               "a":"Email",
               "c":0,
               "l":[
                  "jane@example.com",
                  "john@example.com"
               ]
            }
         ]
      },
      {
         "n":"Not Beta users",
         "r":[
            {
               "a":"Email",
               "c":17,
               "l":[
                  "46e76bee50cb35e27095f4a624e8ba02a174f83cd062fb92975ea04fa0518a3f",
                  "274909972567e293a115dfdff5780c8aae7769a912ca596367e7d5523b8e8891"
               ]
            }
         ]
      },
      {
         "n":"Not Beta users (cleartext)",
         "r":[
            {
               "a":"Email",
               "c":1,
               "l":[
                  "jane@example.com",
                  "john@example.com"
               ]
            }
         ]
      }
   ],
   "f":{
      "featureWithNegatedSegmentTargeting":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":0,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"772939a0"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"3c0be020"
      },
      "featureWithNegatedSegmentTargetingCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":1,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"0fc9b378"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"6c5f81e3"
      },
      "featureWithNegatedSegmentTargetingInverse":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":2,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"145a1eb0"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"e9c52981"
      },
      "featureWithNegatedSegmentTargetingInverseCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":3,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"4898b966"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"fa8f80d5"
      },
      "featureWithSegmentTargeting":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":0,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"a49f6150"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"cfe41874"
      },
      "featureWithSegmentTargetingCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":1,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"d03ed88c"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"89fac05a"
      },
      "featureWithSegmentTargetingInverse":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":2,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"cf444ba3"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"2ddaee84"
      },
      "featureWithSegmentTargetingInverseCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":3,
                        "c":0
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"a78fc410"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"6a3224de"
      },
      "featureWithSegmentTargetingMultipleConditions":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "s":{
                        "s":1,
                        "c":0
                     }
                  },
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
                     "b":true
                  },
                  "i":"dffdf084"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"3f2ec515"
      }
   }
}
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "featureWithSegmentTargeting",
            defaultValue = false,
            returnValue = false,
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'featureWithSegmentTargeting' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'featureWithSegmentTargeting'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS IN SEGMENT 'Beta users' THEN 'true' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'false'."""
        ),
        TestCase(
            key = "featureWithSegmentTargeting",
            defaultValue = false,
            returnValue = true,
            user = ConfigCatUser("12345", "jane@example.com"),
            expectedLog = """INFO [5000] Evaluating 'featureWithSegmentTargeting' for User '{"Identifier":"12345","Email":"jane@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS IN SEGMENT 'Beta users'
    (
      Evaluating segment 'Beta users':
      - IF User.Email IS ONE OF [<2 hashed values>] => true
      Segment evaluation result: User IS IN SEGMENT.
      Condition (User IS IN SEGMENT 'Beta users') evaluates to true.
    )
    THEN 'true' => MATCH, applying rule
  Returning 'true'."""
        ),
        TestCase(
            key = "featureWithNegatedSegmentTargeting",
            defaultValue = false,
            returnValue = false,
            user = ConfigCatUser("12345", "jane@example.com"),
            expectedLog = """INFO [5000] Evaluating 'featureWithNegatedSegmentTargeting' for User '{"Identifier":"12345","Email":"jane@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS NOT IN SEGMENT 'Beta users'
    (
      Evaluating segment 'Beta users':
      - IF User.Email IS ONE OF [<2 hashed values>] => true
      Segment evaluation result: User IS IN SEGMENT.
      Condition (User IS NOT IN SEGMENT 'Beta users') evaluates to false.
    )
    THEN 'true' => no match
  Returning 'false'."""
        ),
        TestCase(
            key = "featureWithNegatedSegmentTargetingCleartext",
            defaultValue = false,
            returnValue = false,
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email IS ONE OF ['jane@example.com', 'john@example.com']) for setting 'featureWithNegatedSegmentTargetingCleartext' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'featureWithNegatedSegmentTargetingCleartext' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS NOT IN SEGMENT 'Beta users (cleartext)'
    (
      Evaluating segment 'Beta users (cleartext)':
      - IF User.Email IS ONE OF ['jane@example.com', 'john@example.com'] => false, skipping the remaining AND conditions
      Segment evaluation result: cannot evaluate, the User.Email attribute is missing.
      Condition (User IS NOT IN SEGMENT 'Beta users (cleartext)') failed to evaluate.
    )
    THEN 'true' => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'false'."""
        ),
        TestCase(
            key = "featureWithSegmentTargetingMultipleConditions",
            defaultValue = false,
            returnValue = false,
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'featureWithSegmentTargetingMultipleConditions' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'featureWithSegmentTargetingMultipleConditions'
  Evaluating targeting rules and applying the first match if any:
  - IF User IS IN SEGMENT 'Beta users (cleartext)' => false, skipping the remaining AND conditions
    THEN 'true' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'false'."""
        )
    )
}
