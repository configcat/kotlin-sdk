package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object OptionsWithinTargetingRuleTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/P4e3fAz_1ky2-Zg2e4cbkw"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"5AY8okcLggegP3KSQ+lSLVF8DaGZVYa6SPbPoq5DOFw="
           },
           "f":{
              "dependentFlag":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "d":{
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
                             "d":{
                                "f":"key1",
                                "c":0,
                                "v":{
                                   "s":"value1"
                                }
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "12_f8294ec2522d5744890c15b405640f9728b7081cb1e4b0faffc56324999e8d6f"
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
                             "t":{
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
              }
           }
        }
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = null,
            expectedLog = """WARNING [3001] Cannot evaluate targeting rules and % options for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (User Object is missing). You should pass a User Object to the evaluation methods like `getValue()`/`getValueAsync()` in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => cannot evaluate, User Object is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
    """.trimIndent()
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345"),
            expectedLog = """WARNING [3003] Cannot evaluate condition (User.Email CONTAINS ANY OF ['@configcat.com']) for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (the User.Email attribute is missing). You should set the User.Email attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => cannot evaluate, the User.Email attribute is missing
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
    """.trimIndent()
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@example.com"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@example.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => no match
  Returning 'Cat'.
    """.trimIndent()
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@configcat.com"),
            expectedLog = """WARNING [3003] Cannot evaluate % options for setting 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' (the User.Country attribute is missing). You should set the User.Country attribute in order to make targeting work properly. Read more: https://configcat.com/docs/advanced/user-object/
INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => MATCH, applying rule
    Skipping % options because the User.Country attribute is missing.
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Cat'.
    """.trimIndent()
        ),
        TestCase(
            key = "stringContainsString75Cat0Dog25Falcon0HorseDefaultCat",
            defaultValue = "default",
            returnValue = "Cat",
            user = ConfigCatUser("12345", "joe@configcat.com", "US"),
            expectedLog = """INFO [5000] Evaluating 'stringContainsString75Cat0Dog25Falcon0HorseDefaultCat' for User '{"Identifier":"12345","Email":"joe@configcat.com","Country":"US"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email CONTAINS ANY OF ['@configcat.com'] THEN % options => MATCH, applying rule
    Evaluating % options based on the User.Country attribute:
    - Computing hash in the [0..99] range from User.Country => 63 (this value is sticky and consistent across all SDKs)
    - Hash value 63 selects % option 1 (75%), 'Cat'.
  Returning 'Cat'.
    """.trimIndent()
        )
    )
}
