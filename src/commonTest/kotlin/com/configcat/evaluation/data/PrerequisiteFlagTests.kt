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
              "s":"zlVnq+GjKscd7CCttof3QSgIrbf6guY+Wyvm43HiiwQ="
           },
           "s":[
              {
                 "n":"Beta Users",
                 "r":[
                    {
                       "a":"Email",
                       "c":16,
                       "l":[
                          "f0c2a1171c264e84c30d06e8f0181ab060307b8be699d20c77b3563e65fc51ae",
                          "f93f24deb25e5091d5fb51f1ae7d13cf9254607bc91c5e3cacced9b07b0f6a35"
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
                          "7c9aec489978fbc202dde6fb2d8fb8a35642b1bc513ada57a49451dc97de2a9c",
                          "eb575b649594f99effdf11953e893efc8e737bf95e837cb790a0270dbf3fae20"
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
                             "d":{
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
                             "t":{
                                "a":"Email",
                                "c":16,
                                "l":[
                                   "321beeabac75ac6dcb2f2148c800febd5ad3aaea3b7d530aa3a749a36a5a2029",
                                   "94ff8bcd6db5a6659131d7efd853566521a0da896cd9da181bebb61925c3cc08"
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
                             "d":{
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
                             "t":{
                                "a":"Email",
                                "c":16,
                                "l":[
                                   "4631ad7970aa30cc2e016d2e361f778767632e00b12ba9d6c93df83f2db73e6b",
                                   "36ebf231cd371d6fa31468f9ec28dd7490b602af4ae3cf2a631cd20dcce0cfb8"
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
                             "d":{
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
                             "d":{
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
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "4_41a64b5ebf052a120e8a9c09138148871eb8ba6933f793e475e77732dbf6b788"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":2,
                                "l":[
                                   "@"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "20_e001b20def19469d7c0a86c5e6f51e23da2f3eb9a047995ad8f31752bcab30f4"
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
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "5_ba2a1cffc0c80dea98087470686004e72cea59bd028c39eea911c6982d970eb0"
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
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "5_7a2707ce87e2649ae9e249b0f0d58d58543dbdcb4f27702c37dd0c5d4111682a"
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
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "5_ff5dcaab81623ad10a26a29778075c578b7d29c1f65e473b3463c839ca56f839"
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
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "21_2e6575d0357cd787b6e3a0c6e1fe2279e8dfdfb23071af45c57dd4fbea35d4d0"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Country",
                                "c":16,
                                "l":[
                                   "60a210babac9a4e2cfbe9dd5d80e140d3457e9e351391c40450d0e8db1355347",
                                   "300cf01202fdfdc31176ed5e868341b219cea9aedfaba50bff0cef3eaeab7584"
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
                             "t":{
                                "a":"Country",
                                "c":16,
                                "l":[
                                   "d34c7fab9b86c80cc0454e7ff422206ca803d7209702a30fd8be6c94b8d15185"
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
  Returning 'Chicken'.
    """.trimIndent()
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
  Returning 'Chicken'.
    """.trimIndent()
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
  Returning 'Frog'.
    """.trimIndent()
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
  Returning 'Horse'.
    """.trimIndent()
        )
    )
}
