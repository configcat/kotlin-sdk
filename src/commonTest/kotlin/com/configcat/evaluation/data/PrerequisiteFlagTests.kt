package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object PrerequisiteFlagTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/ByMO9yZNn02kXcm72lnY1A"
    override val baseUrl = null
    override val jsonOverride =
        """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"9QUCCinAuxoxzwqYysygixlMORhwuxu3wg00Cs0mw5I="
           },
           "s":[
              {
                 "n":"Beta Users",
                 "r":[
                    {
                       "a":"Email",
                       "c":16,
                       "l":[
                          "edbdeb7620729047fb22ffab8ab349a7eac8d0aa473d3d47630182f89821541b",
                          "1960614f092689190de846b8c28fd52dbd9d70fe75cbcd28fb8ebcb616d9c525"
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
                          "1fc2737a1be8ace9e5e6fa78b87b304374ce85b3a94202844834472cd779a73f",
                          "f729137a8dfed4b2f616c4213f9555150a0dba6f75736e77deb539f1806f5077"
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
              "dependentFeatureMultipleLevels":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "p":{
                                "f":"intermediateFeature",
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
                          "i":"cdbc4728"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"1c895d65"
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
                                   "c331a2f2bea132f0e39651cb8834b420a189bbf03a3985ec05daf78c7ca4baf7",
                                   "4916de795d199f76ad7d056d5d14311d71841cf6e6dd64b863be396ff590788c"
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
                                   "c9a31439dc0f6651b26e19af7618bde322667d740b7420f7a4205b538a642b8a",
                                   "10743927669a473a7b7871e4986e38f632721b5f590bf707a3824350392f9925"
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
                                   "4_5ef657b8b86d0915729e13ed68a58dfe62927698c33fc0fed7fd27c1ce07083b"
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
                                   "20_a64e9a2f841a01b6e97458b09e67931a0d749b4de1d7a287edafcc4ca67a574c"
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
                                   "5_831d3723899ca121aecd407492308e20293440167e867e209ffbed9394fedc7e"
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
                                   "5_1e30956b0edd4dd8796a9c67ba4cb9627407c2fffdd5a2a395c6e38765a23fd7"
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
                                   "5_f325235338d2a8ecef054f2ecec90aaf0379de685445d59aac603253dad60093"
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
              "intermediateFeature":{
                 "t":0,
                 "r":[
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
                          },
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
                       "s":{
                          "v":{
                             "b":true
                          },
                          "i":"570cf731"
                       }
                    }
                 ],
                 "v":{
                    "b":false
                 },
                 "i":"cdcafb72"
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
                                   "21_5067125a3533392865a118b2c79a4a52a00fee531910090a500832cfc6e8191f"
                                ]
                             }
                          },
                          {
                             "u":{
                                "a":"Country",
                                "c":16,
                                "l":[
                                   "27fc59bcf5b7bdfb1877ebc1e34b39ded9d557317ead60058e5a2be20949be56",
                                   "613ba3556acefc0bdb54d92ec910c579234ae6b552fe215c17cd565e37f61735"
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
                                   "daf7a57a85b7efa51d52f674d977987401ec6d9412d9e65586c2e2df9ea8b265"
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
    override val tests: Array<TestCase> =
        arrayOf(
            TestCase(
                key = "dependentFeatureWithUserCondition",
                defaultValue = "default",
                returnValue = "Chicken",
                user = null,
                expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
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
  Returning 'Chicken'.""",
            ),
            TestCase(
                key = "dependentFeature",
                defaultValue = "default",
                returnValue = "Chicken",
                user = null,
                expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
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
  Returning 'Chicken'.""",
            ),
            TestCase(
                key = "dependentFeatureWithUserCondition2",
                defaultValue = "default",
                returnValue = "Frog",
                user = null,
                expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'dependentFeatureWithUserCondition2' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
WARNING [3001] Cannot evaluate targeting rules and % options for setting 'mainFeature' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
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
  Returning 'Frog'.""",
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
  Returning 'Horse'.""",
            ),
            TestCase(
                key = "dependentFeatureMultipleLevels",
                defaultValue = "default",
                returnValue = "Dog",
                user = null,
                expectedLog = """INFO [5000] Evaluating 'dependentFeatureMultipleLevels'
  Evaluating targeting rules and applying the first match if any:
  - IF Flag 'intermediateFeature' EQUALS 'true'
    (
      Evaluating prerequisite flag 'intermediateFeature':
      Evaluating targeting rules and applying the first match if any:
      - IF Flag 'mainFeatureWithoutUserCondition' EQUALS 'true'
        (
          Evaluating prerequisite flag 'mainFeatureWithoutUserCondition':
          Prerequisite flag evaluation result: 'true'.
          Condition (Flag 'mainFeatureWithoutUserCondition' EQUALS 'true') evaluates to true.
        ) => true
        AND Flag 'mainFeatureWithoutUserCondition' EQUALS 'true'
        (
          Evaluating prerequisite flag 'mainFeatureWithoutUserCondition':
          Prerequisite flag evaluation result: 'true'.
          Condition (Flag 'mainFeatureWithoutUserCondition' EQUALS 'true') evaluates to true.
        ) => true
        THEN 'true' => MATCH, applying rule
      Prerequisite flag evaluation result: 'true'.
      Condition (Flag 'intermediateFeature' EQUALS 'true') evaluates to true.
    )
    THEN 'Dog' => MATCH, applying rule
  Returning 'Dog'.""",
            ),
        )
}
