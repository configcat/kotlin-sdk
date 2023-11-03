package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object PrerequisiteFlagTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/ByMO9yZNn02kXcm72lnY1A"
    override val baseUrl = null
    override val jsonOverride = """
        {
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":0,
      "s":"XYA\u002BaDHULdih3lcV/DfSC4I3HgX3hkoYC5Te638DGSU="
   },
   "s":[
      {
         "n":"Beta Users",
         "r":[
            {
               "a":"Email",
               "c":16,
               "l":[
                  "5a1c36ec9cb651709b85f7295405880dc4728d3c5b27b4de09476bba2c10553b",
                  "83eea4b4f01b5471a1bf2505a1d141485fc29e576e741889a30bf6555ad02b01"
               ]
            }
         ]
      },
      {
         "n":"Developers",
         "r":[
            {
               "a":"Email",
               "c":16,
               "l":[
                  "3b578b8bd4998b1b3c042b3a28746c4ff1d41990a535e941a826564e1d45b523",
                  "16cb6133c8bdc3e6d427f96f818a2b1c3a274e62c5a37ec42c4a47184ee7c54b"
               ]
            }
         ]
      }
   ],
   "f":{
      "dependentFeature":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "p":{
                        "f":"mainFeature",
                        "c":0,
                        "v":{
                           "s":"target"
                        }
                     }
                  }
               ],
               "p":[
                  {
                     "p":25,
                     "v":{
                        "s":"Cat"
                     },
                     "i":"993d7ee0"
                  },
                  {
                     "p":25,
                     "v":{
                        "s":"Dog"
                     },
                     "i":"08b8348e"
                  },
                  {
                     "p":25,
                     "v":{
                        "s":"Falcon"
                     },
                     "i":"a6fb7a01"
                  },
                  {
                     "p":25,
                     "v":{
                        "s":"Horse"
                     },
                     "i":"699fb4bf"
                  }
               ]
            }
         ],
         "v":{
            "s":"Chicken"
         },
         "i":"e6198f92"
      },
      "dependentFeatureWithUserCondition":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":16,
                        "l":[
                           "c904e7bad986f101a63a5da52cdf4fbb9660bea2ad79dccbd6121075ab610b1f",
                           "84da58e3ca0ff12167c80d39731b391f9ecd8003af693b01240897b8453f0f43"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Dog"
                  },
                  "i":"ef802e43"
               }
            },
            {
               "c":[
                  {
                     "p":{
                        "f":"mainFeatureWithoutUserCondition",
                        "c":0,
                        "v":{
                           "b":true
                        }
                     }
                  }
               ],
               "p":[
                  {
                     "p":34,
                     "v":{
                        "s":"Cat"
                     },
                     "i":"4a65d6ef"
                  },
                  {
                     "p":33,
                     "v":{
                        "s":"Horse"
                     },
                     "i":"fc3bb22b"
                  },
                  {
                     "p":33,
                     "v":{
                        "s":"Falcon"
                     },
                     "i":"32e0e525"
                  }
               ]
            }
         ],
         "v":{
            "s":"Chicken"
         },
         "i":"472e10f4"
      },
      "dependentFeatureWithUserCondition2":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":16,
                        "l":[
                           "38b591c3f8a1434d092ad362b8a3f76625938d7937cdc5bb67bda2ccc474df94",
                           "2f985eef0a5f9b5193c88006e7ec1bb70cd4ea375273415254e538e33e033026"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Dog"
                  },
                  "i":"78eceed0"
               }
            },
            {
               "c":[
                  {
                     "p":{
                        "f":"mainFeature",
                        "c":0,
                        "v":{
                           "s":"public"
                        }
                     }
                  }
               ],
               "p":[
                  {
                     "p":34,
                     "v":{
                        "s":"Cat"
                     },
                     "i":"72b97d0e"
                  },
                  {
                     "p":33,
                     "v":{
                        "s":"Horse"
                     },
                     "i":"81846c69"
                  },
                  {
                     "p":33,
                     "v":{
                        "s":"Falcon"
                     },
                     "i":"e2f3b509"
                  }
               ]
            },
            {
               "c":[
                  {
                     "p":{
                        "f":"mainFeature",
                        "c":0,
                        "v":{
                           "s":"public"
                        }
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Frog"
                  },
                  "i":"cfd56c79"
               }
            }
         ],
         "v":{
            "s":"Chicken"
         },
         "i":"9e8d62c6"
      },
      "emailAnd":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":22,
                        "l":[
                           "4_76c49f275a4016de6c7a2464942df853891f0e7c39f176136abdf9edf6f3ffd4"
                        ]
                     }
                  },
                  {
                     "u":{
                        "a":"Email",
                        "c":2,
                        "l":[
                           "@"
                        ]
                     }
                  },
                  {
                     "u":{
                        "a":"Email",
                        "c":24,
                        "l":[
                           "20_54899f4b5464250fe02af0fc09f5bf863865bb543c9f981133151b7fcc133bb7"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Dog"
                  },
                  "i":"a1393561"
               }
            }
         ],
         "v":{
            "s":"Cat"
         },
         "i":"bdabd589"
      },
      "emailOr":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":22,
                        "l":[
                           "5_a366d82b39567d7d60654732b75973217f90cd8b3e8cbd89f8e9247257f8f421"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Jane"
                  },
                  "i":"01383bbf"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":22,
                        "l":[
                           "5_b587748cc92c5f3f3f5792e82b4cb93efaab7b2211aa7d1102e78a22589715e1"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"John"
                  },
                  "i":"a069dc24"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":22,
                        "l":[
                           "5_670b198d20a45c499f5a32b1a46ee1e4f13d1c42594b3096e47daf340d8fb8e0"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"Mark"
                  },
                  "i":"d7b02cc0"
               }
            }
         ],
         "v":{
            "s":"Cat"
         },
         "i":"ab0b46ad"
      },
      "mainFeature":{
         "t":1,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":24,
                        "l":[
                           "21_56a25e5b7dc4bff5f0634b52ec41b2e1d0ed838c7297a83967cd7aa1e14bc36a"
                        ]
                     }
                  },
                  {
                     "u":{
                        "a":"Country",
                        "c":16,
                        "l":[
                           "1fab893a891c70917518e97fb4b0be62aab1888d305a9c8f393a201a83900bc0",
                           "0d8f106bc09eef0f6aa4a36f861b4b30c17186623543ac69bd654c9a931cd42f"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"private"
                  },
                  "i":"64f8e1a6"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"Country",
                        "c":16,
                        "l":[
                           "d3be141e87c1ed4c35330fc7d7c37f617e98d4c17ee7e8be739c87ca07aa048c"
                        ]
                     }
                  },
                  {
                     "s":{
                        "s":0,
                        "c":1
                     }
                  },
                  {
                     "s":{
                        "s":1,
                        "c":1
                     }
                  }
               ],
               "s":{
                  "v":{
                     "s":"target"
                  },
                  "i":"f570ef26"
               }
            }
         ],
         "v":{
            "s":"public"
         },
         "i":"f16ac582"
      },
      "mainFeatureWithoutUserCondition":{
         "t":0,
         "v":{
            "b":true
         },
         "i":"1c6ca36e"
      }
   }
}
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "dependentFeatureWithUserCondition",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeatureWithUserCondition'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeatureWithoutUserCondition' EQUALS 'true'
    (
      Evaluating prerequisite flag 'mainFeatureWithoutUserCondition':
      Prerequisite flag evaluation result: 'true'.
      Condition (Flag 'mainFeatureWithoutUserCondition' EQUALS 'true') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Skipping % options because the User Object is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Chicken'."""
        ),
        TestCase(
            key = "dependentFeature",
            defaultValue = "default",
            returnValue = "Chicken",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeature'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'mainFeature' EQUALS 'target'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'target') evaluates to false.
    )
    THEN % options => no match
  Returning 'Chicken'."""
        ),
        TestCase(
            key = "dependentFeatureWithUserCondition2",
            defaultValue = "default",
            returnValue = "Frog",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition2' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'dependentFeatureWithUserCondition2'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email IS ONE OF [<2 hashed values>] THEN 'Dog' => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeature' EQUALS 'public'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'public') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Skipping % options because the User Object is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  - IF Flag 'mainFeature' EQUALS 'public'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      - IF User.Country IS ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'target' => cannot evaluate, User Object is missing
        The current targeting rule is ignored and the evaluation continues with the next rule.
      Prerequisite flag evaluation result: 'public'.
      Condition (Flag 'mainFeature' EQUALS 'public') evaluates to true.
    )
    THEN 'Frog' => MATCH, applying rule
  Returning 'Frog'."""
        ),
        TestCase(
            key = "dependentFeature",
            defaultValue = "default",
            returnValue = "Horse",
            user = ConfigCatUser("12345", "kate@configcat.com", "USA"),
            expectedLog = """INFO [5000] Evaluating 'dependentFeature' for User '{"Identifier":"12345","Email":"kate@configcat.com","Country":"USA"}'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'mainFeature' EQUALS 'target'
    (
      Evaluating prerequisite flag 'mainFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
        THEN 'private' => no match
      - IF User.Country IS ONE OF [<1 hashed value>] => true
        AND User IS NOT IN SEGMENT 'Beta Users'
        (
          Evaluating segment 'Beta Users':
          - IF User.Email IS ONE OF [<2 hashed values>] => false, skipping the remaining AND conditions
          Segment evaluation result: User IS NOT IN SEGMENT.
          Condition (User IS NOT IN SEGMENT 'Beta Users') evaluates to true.
        ) => true
        AND User IS NOT IN SEGMENT 'Developers'
        (
          Evaluating segment 'Developers':
          - IF User.Email IS ONE OF [<2 hashed values>] => false, skipping the remaining AND conditions
          Segment evaluation result: User IS NOT IN SEGMENT.
          Condition (User IS NOT IN SEGMENT 'Developers') evaluates to true.
        ) => true
        THEN 'target' => MATCH, applying rule
      Prerequisite flag evaluation result: 'target'.
      Condition (Flag 'mainFeature' EQUALS 'target') evaluates to true.
    )
    THEN % options => MATCH, applying rule
    Evaluating % options based on the User.Identifier attribute:
    - Computing hash in the [0..99] range from User.Identifier => 78 (this value is sticky and consistent across all SDKs)
    - Hash value 78 selects % option 4 (25%), 'Horse'.
  Returning 'Horse'."""
        )
    )
}
