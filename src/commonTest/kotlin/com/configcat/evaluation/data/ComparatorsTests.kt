package com.configcat.evaluation.data

import com.configcat.ConfigCatUser

object ComparatorsTests : TestSet {
    override val sdkKey = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/OfQqcTjfFUGBwMKqtyEOrQ"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"UgZzJyRflMz2zF9hVQLdZz7POomplspehEOlXJs+rEI="
           },
           "f":{
              "allinone":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":20,
                                "s":"9e8f65438ee7112f89e37769abd01fd0631926d9c188182e53e2f2e3872bef74"
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":21,
                                "s":"9e8f65438ee7112f89e37769abd01fd0631926d9c188182e53e2f2e3872bef74"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"1"
                          },
                          "i":"ab0645f7"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":16,
                                "l":[
                                   "9e8f65438ee7112f89e37769abd01fd0631926d9c188182e53e2f2e3872bef74"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":17,
                                "l":[
                                   "9e8f65438ee7112f89e37769abd01fd0631926d9c188182e53e2f2e3872bef74"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"2"
                          },
                          "i":"dbe98f44"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "4_3fd9e0f8253bbf39b056038e5c0598b7fa656dce5389fc912e3f162c8725f726"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":23,
                                "l":[
                                   "4_3fd9e0f8253bbf39b056038e5c0598b7fa656dce5389fc912e3f162c8725f726"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"3"
                          },
                          "i":"e7121806"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "12_3fffad52e20f233dd93e0ccbe9aaffeb860c42acb0493252b83ca6d376b7475e"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":25,
                                "l":[
                                   "12_3fffad52e20f233dd93e0ccbe9aaffeb860c42acb0493252b83ca6d376b7475e"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"4"
                          },
                          "i":"579da034"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":2,
                                "l":[
                                   "e@e"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Email",
                                "c":3,
                                "l":[
                                   "e@e"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"5"
                          },
                          "i":"dd12c429"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Version",
                                "c":4,
                                "l":[
                                   "1.0.0"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Version",
                                "c":5,
                                "l":[
                                   "1.0.0"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"6"
                          },
                          "i":"dba5d266"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Version",
                                "c":6,
                                "s":"1.0.1"
                             }
                          },
                          {
                             "t":{
                                "a":"Version",
                                "c":9,
                                "s":"1.0.1"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"7"
                          },
                          "i":"1637ffc5"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Version",
                                "c":8,
                                "s":"0.9.9"
                             }
                          },
                          {
                             "t":{
                                "a":"Version",
                                "c":7,
                                "s":"0.9.9"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"8"
                          },
                          "i":"b084ddd6"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Number",
                                "c":10,
                                "d":1
                             }
                          },
                          {
                             "t":{
                                "a":"Number",
                                "c":11,
                                "d":1
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"9"
                          },
                          "i":"d1d537a6"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Number",
                                "c":12,
                                "d":1.1
                             }
                          },
                          {
                             "t":{
                                "a":"Number",
                                "c":15,
                                "d":1.1
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"10"
                          },
                          "i":"52c846d0"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Number",
                                "c":14,
                                "d":0.9
                             }
                          },
                          {
                             "t":{
                                "a":"Number",
                                "c":13,
                                "d":0.9
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"11"
                          },
                          "i":"c91ffb7c"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Date",
                                "c":18,
                                "d":1693497600
                             }
                          },
                          {
                             "t":{
                                "a":"Date",
                                "c":19,
                                "d":1693497600
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"12"
                          },
                          "i":"c12182ef"
                       }
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Country",
                                "c":26,
                                "l":[
                                   "fcc5ea9c77f7a3f981d595af43b396adb8e09e058d4f60df0e73ecb68df42bad"
                                ]
                             }
                          },
                          {
                             "t":{
                                "a":"Country",
                                "c":27,
                                "l":[
                                   "fcc5ea9c77f7a3f981d595af43b396adb8e09e058d4f60df0e73ecb68df42bad"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"13"
                          },
                          "i":"37431937"
                       }
                    }
                 ],
                 "v":{
                    "s":"default"
                 },
                 "i":"9ff25f81"
              },
              "arrayContainsCaseCheckDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":26,
                                "l":[
                                   "e45c8824edfc992eadc67d82fbf933eb53a6052265db02179d4be40945e70887"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"5d80eff1"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"ce055a38"
              },
              "arrayContainsDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":26,
                                "l":[
                                   "4ef41bf8b329cced8e0b587c923ca7f650f3e1185b346fb2bd7f629f4d263c6d"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"147fdd01"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"5f573f9c"
              },
              "arrayDoesNotContainCaseCheckDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":27,
                                "l":[
                                   "e3bde2f1f172413ebe11285515e8a2a01ee8691b3a062175f1545fbfc34aa24d"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"d4ad5730"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"df4915fd"
              },
              "arrayDoesNotContainDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":27,
                                "l":[
                                   "bb3e68b15d06b075a7e7e8ae790d4254cdb87295a6741c2d83bd92658ae69d55"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"c2161ac9"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"41910880"
              },
              "boolTrueIn202304":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Custom1",
                                "c":19,
                                "d":1680307200
                             }
                          },
                          {
                             "t":{
                                "a":"Custom1",
                                "c":18,
                                "d":1682899200
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "b":true
                          },
                          "i":"6948d7cd"
                       }
                    }
                 ],
                 "v":{
                    "b":false
                 },
                 "i":"ae2a09bd"
              },
              "countryPercentageAttribute":{
                 "t":1,
                 "a":"Country",
                 "p":[
                    {
                       "p":50,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"2b05fd81"
                    },
                    {
                       "p":50,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"e28b6a82"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"29bb6bbb"
              },
              "customPercentageAttribute":{
                 "t":1,
                 "a":"Custom1",
                 "p":[
                    {
                       "p":50,
                       "v":{
                          "s":"Falcon"
                       },
                       "i":"3715712d"
                    },
                    {
                       "p":50,
                       "v":{
                          "s":"Horse"
                       },
                       "i":"7b3542d5"
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"50466fb6"
              },
              "missingPercentageAttribute":{
                 "t":1,
                 "a":"NotFound",
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "14_2f4e1ada7a8354c2e40558d694d64ab812db73b4dcf4bcea6bc29cb8f9c33d97"
                                ]
                             }
                          }
                       ],
                       "p":[
                          {
                             "p":50,
                             "v":{
                                "s":"Falcon"
                             },
                             "i":"4b7d88ba"
                          },
                          {
                             "p":50,
                             "v":{
                                "s":"Horse"
                             },
                             "i":"a1c2c9a9"
                          }
                       ]
                    },
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "14_2f4e1ada7a8354c2e40558d694d64ab812db73b4dcf4bcea6bc29cb8f9c33d97"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"NotFound"
                          },
                          "i":"8aa042fe"
                       }
                    }
                 ],
                 "v":{
                    "s":"Chicken"
                 },
                 "i":"e5107172"
              },
              "stringDoseNotEqualDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":21,
                                "s":"53cdd8c4be263822fb7ecd309a20b68f3df839a776a601d13a84054efff0a982"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"8e423808"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"1835a09a"
              },
              "stringEndsWithDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":24,
                                "l":[
                                   "14_9976015305689cd92ac1c178ff9a852ad58132aa817e8d6aadfd97b393acc2c8"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"d7a00741"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"45b7d922"
              },
              "stringEqualsDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":20,
                                "s":"b5d595db02f89a71b54379e54d756685163c8c08193f390c4d72f3baa0dc2876"
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"703c31ed"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"adc0b01c"
              },
              "stringNotEndsWithDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":25,
                                "l":[
                                   "14_49b42d9d75638a99f6bea910fae5004a98b07f7b9a250d456fab17fcd64b7555"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"d37b6f18"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"91ba1bcb"
              },
              "stringNotStartsWithDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":23,
                                "l":[
                                   "1_6dd5a8f4c2b9891bc90f633ecf187c692a12bfe2d234f93fc18c8c241522f315"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"72c4e1ac"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"2b16da78"
              },
              "stringStartsWithDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":22,
                                "l":[
                                   "1_7f42af44f507c0c4b2dc862e28faff00e02101cca9f632a76303efac25b5deb4"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"Dog"
                          },
                          "i":"3b409872"
                       }
                    }
                 ],
                 "v":{
                    "s":"Cat"
                 },
                 "i":"3659b0fe"
              }
           }
        }
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "allinone",
            defaultValue = "",
            returnValue = "default",
            user = ConfigCatUser(
                "12345",
                "joe@example.com",
                "USA",
                custom = mapOf("Version" to "1.0.0", "Number" to "1.0", "Date" to "1693497500")
            ),
            expectedLog = """INFO [5000] Evaluating 'allinone' for User '{"Identifier":"12345","Email":"joe@example.com","Country":"USA","Version":"1.0.0","Number":"1.0","Date":"1693497500"}'
  Evaluating targeting rules and applying the first match if any:
  - IF User.Email EQUALS '<hashed value>' => true
    AND User.Email NOT EQUALS '<hashed value>' => false, skipping the remaining AND conditions
    THEN '1' => no match
  - IF User.Email IS ONE OF [<1 hashed value>] => true
    AND User.Email IS NOT ONE OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '2' => no match
  - IF User.Email STARTS WITH ANY OF [<1 hashed value>] => true
    AND User.Email NOT STARTS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '3' => no match
  - IF User.Email ENDS WITH ANY OF [<1 hashed value>] => true
    AND User.Email NOT ENDS WITH ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '4' => no match
  - IF User.Email CONTAINS ANY OF ['e@e'] => true
    AND User.Email NOT CONTAINS ANY OF ['e@e'] => false, skipping the remaining AND conditions
    THEN '5' => no match
  - IF User.Version IS ONE OF ['1.0.0'] => true
    AND User.Version IS NOT ONE OF ['1.0.0'] => false, skipping the remaining AND conditions
    THEN '6' => no match
  - IF User.Version < '1.0.1' => true
    AND User.Version >= '1.0.1' => false, skipping the remaining AND conditions
    THEN '7' => no match
  - IF User.Version > '0.9.9' => true
    AND User.Version <= '0.9.9' => false, skipping the remaining AND conditions
    THEN '8' => no match
  - IF User.Number = '1' => true
    AND User.Number != '1' => false, skipping the remaining AND conditions
    THEN '9' => no match
  - IF User.Number < '1.1' => true
    AND User.Number >= '1.1' => false, skipping the remaining AND conditions
    THEN '10' => no match
  - IF User.Number > '0.9' => true
    AND User.Number <= '0.9' => false, skipping the remaining AND conditions
    THEN '11' => no match
  - IF User.Date BEFORE '1693497600' (2023-08-31T16:00:00.000Z UTC) => true
    AND User.Date AFTER '1693497600' (2023-08-31T16:00:00.000Z UTC) => false, skipping the remaining AND conditions
    THEN '12' => no match
  - IF User.Country ARRAY CONTAINS ANY OF [<1 hashed value>] => true
    AND User.Country ARRAY NOT CONTAINS ANY OF [<1 hashed value>] => false, skipping the remaining AND conditions
    THEN '13' => no match
  Returning 'default'.
    """.trimIndent()
        )
    )
}
