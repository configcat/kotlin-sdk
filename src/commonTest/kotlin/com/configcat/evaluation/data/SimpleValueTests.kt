package com.configcat.evaluation.data

object SimpleValueTests : TestSet {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/psuH7BGHoUmdONrzzUOY7A"
    override val baseUrl = null
    override val jsonOverride = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"0s40c7ztfxPEuNdctDmr4fSmmJwhuKjC6UANhMuiX3E="
           },
           "f":{
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
                             "t":{
                                "a":"Email",
                                "c":16,
                                "l":[
                                   "b11f234e3ccb6f14e1fbc1252f3c43e9fc314f53c24beaac4648ef85860c3881",
                                   "3bf883983b87a1e09355b5841433d2f7d3ced38da674040db1d34b947a5241e8"
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
                             "t":{
                                "a":"Custom1",
                                "c":16,
                                "l":[
                                   "d42a610197739f57e7647f167ec1877d95b9b2eb1dabdb6946ca244bd60a8ca5"
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
                             "t":{
                                "a":"Email",
                                "c":17,
                                "l":[
                                   "f839eecad4affdf0fa4eb0f50b7d83ce40f8bf4b355c06ac386a013909c9e7bb",
                                   "7b41ae60c668be16448c596a1a42faeb4c1a3bb9835b8269389583791cf0186d"
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
              "stringContainsDogDefaultCat":{
                 "t":1,
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
              "stringNotContainsDogDefaultCat":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
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
              "string25Cat25Dog25Falcon25HorseAdvancedRules":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Country",
                                "c":16,
                                "l":[
                                   "f4f2b3548d31cf09b576f65042a312b13faf3456e120a06b6bdb65ef88a5d380",
                                   "15d5172958213506e0b58db96fb6105c60c8836473644a052d6a04eead629c0f"
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
                             "t":{
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
                             "t":{
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
              "boolDefaultTrue":{
                 "t":0,
                 "v":{
                    "b":true
                 },
                 "i":"09513143"
              },
              "boolDefaultFalse":{
                 "t":0,
                 "v":{
                    "b":false
                 },
                 "i":"489a16d2"
              },
              "bool30TrueAdvancedRules":{
                 "t":0,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Email",
                                "c":16,
                                "l":[
                                   "17ff44242a86bbe605297b8caea6c69a12ac4534ed70c22b965c5e0164810856",
                                   "9a4ad45ec26ef45f32e3e8929bf7de41bbf4b59812e0d15f563f4d72b230ac15"
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
                             "t":{
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
              "integer25One25Two25Three25FourAdvancedRules":{
                 "t":2,
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
              "doubleDefaultPi":{
                 "t":3,
                 "v":{
                    "d":3.1415
                 },
                 "i":"5af8acc7"
              },
              "double25Pi25E25Gr25Zero":{
                 "t":3,
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
              "keySampleText":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "t":{
                                "a":"Country",
                                "c":16,
                                "l":[
                                   "60fae38825d7cd79ff77fcbce82ef92c9c7d7707dff5103ca5cfad29f4d8d442",
                                   "33ee51c30326dff6d221d0faee2410977c5985584b3d25284fc48a0dedb4c107"
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
                             "t":{
                                "a":"SubscriptionType",
                                "c":16,
                                "l":[
                                   "8a6e5faf69242ea8ed8b070bc0512a8e29e7b5aea1b633f739c674fb93548fbf"
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
              }
           }
        }
    """.trimIndent()
    override val tests: Array<TestCase> = arrayOf(
        TestCase(
            key = "boolDefaultFalse",
            defaultValue = true,
            returnValue = false,
            expectedLog = """INFO [5000] Evaluating 'boolDefaultFalse'
  Returning 'false'.""",
            user = null
        ),
        TestCase(
            key = "boolDefaultTrue",
            defaultValue = false,
            returnValue = true,
            expectedLog = """INFO [5000] Evaluating 'boolDefaultTrue'
  Returning 'true'.""",
            user = null
        ),
        TestCase(
            key = "stringDefaultCat",
            defaultValue = "stringDefaultCat",
            returnValue = "Cat",
            expectedLog = """INFO [5000] Evaluating 'stringDefaultCat'
  Returning 'Cat'.""",
            user = null
        ),
        TestCase(
            key = "integerDefaultOne",
            defaultValue = 0,
            returnValue = 1,
            expectedLog = """INFO [5000] Evaluating 'integerDefaultOne'
  Returning '1'.""",
            user = null
        ),
        TestCase(
            key = "doubleDefaultPi",
            defaultValue = 0.0,
            returnValue = 3.1415,
            expectedLog = """INFO [5000] Evaluating 'doubleDefaultPi'
  Returning '3.1415'.""",
            user = null
        )
    )
}
