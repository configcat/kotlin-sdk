package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object NumberValidationTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/uGyK3q9_ckmdxRyI7vjwCw"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"S5u1jpG+AdniHyRu/R7n22yT3zZwseApLiXlJkaHWFg="
           },
           "f":{
              "numberWithPercentage":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":12,
                                "d":2.1
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<2.1"
                          },
                          "i":"a900bc23"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":13,
                                "d":2.1
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<=2,1"
                          },
                          "i":"2c85f73d"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":10,
                                "d":3.5
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"=3.5"
                          },
                          "i":"ae86baf5"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":14,
                                "d":5
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":">5"
                          },
                          "i":"c6924001"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":15,
                                "d":5
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":">=5"
                          },
                          "i":"8090543a"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":11,
                                "d":4.2
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<>4.2"
                          },
                          "i":"2691fade"
                       }
                    }
                 ],
                 "p":[
                    {
                       "p":80,
                       "v":{
                          "s":"80%"
                       },
                       "i":"ad5f05a7"
                    },
                    {
                       "p":20,
                       "v":{
                          "s":"20%"
                       },
                       "i":"786b696f"
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"642bbb26"
              },
              "number":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":11,
                                "d":5
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"<>5"
                          },
                          "i":"a41938c5"
                       }
                    }
                 ],
                 "v":{
                    "s":"Default"
                 },
                 "i":"5ced27a9"
              }
           }
        }"""
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "number",
            defaultValue = "default",
            returnValue = "Default",
            user = ConfigCatUser("12345", custom = mapOf("Custom1" to "not_a_number")),
            expectedLog = """WARNING [3004] Cannot evaluate condition (User.Custom1 != '5') for setting 'number' ('not_a_number' is not a valid decimal number). Please check the User.Custom1 attribute and make sure that its value corresponds to the comparison operator.
INFO [5000] Evaluating 'number' for User '{"Identifier":"12345","Custom1":"not_a_number"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Custom1 != '5' THEN '<>5' => cannot evaluate, the User.Custom1 attribute is invalid ('not_a_number' is not a valid decimal number)
    The current targeting rule is ignored and the evaluation continues with the next rule.
  Returning 'Default'."""
        )
    )
}
