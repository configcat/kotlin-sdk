package com.configcat.integration.matrix

object UnicodeMatrix : DataMatrix {
    override val sdkKey: String = "configcat-sdk-1/JcPbCGl_1E-K9M-fJOyKyQ/Da6w8dBbmUeMUBhh0iEeQQ"
    override val data =
        """Identifier;Email;Country;🆃🅴🆇🆃;boolTextEqualsHashed;boolTextEqualsCleartext;boolTextNotEqualsHashed;boolTextNotEqualsCleartext;boolIsOneOfHashed;boolIsOneOfCleartext;boolIsNotOneOfHashed;boolIsNotOneOfCleartext;boolStartsWithHashed;boolStartsWithCleartext;boolNotStartsWithHashed;boolNotStartsWithCleartext;boolEndsWithHashed;boolEndsWithCleartext;boolNotEndsWithHashed;boolNotEndsWithCleartext;boolContainsCleartext;boolNotContainsCleartext;boolArrayContainsHashed;boolArrayContainsCleartext;boolArrayNotContainsHashed;boolArrayNotContainsCleartext
1;;;ʄǟռƈʏ ȶɛӼȶ;True;True;False;False;False;False;True;True;False;False;True;True;False;False;True;True;False;True;False;False;False;False
1;;;ʄaռƈʏ ȶɛӼȶ;False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;False;True;False;False;False;False
1;;;ÁRVÍZTŰRŐ tükörfúrógép;False;False;True;True;True;True;False;False;True;True;False;False;True;True;False;False;True;False;False;False;False;False
1;;;árvíztűrő tükörfúrógép;False;False;True;True;False;False;True;True;False;False;True;True;True;True;False;False;True;False;False;False;False;False
1;;;ÁRVÍZTŰRŐ TÜKÖRFÚRÓGÉP;False;False;True;True;False;False;True;True;True;True;False;False;False;False;True;True;True;False;False;False;False;False
1;;;árvíztűrő TÜKÖRFÚRÓGÉP;False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;False;True;False;False;False;False
1;;;u𝖓𝖎𝖈𝖔𝖉e;False;False;True;True;True;True;False;False;True;True;False;False;True;True;False;False;True;False;False;False;False;False
;;;𝖚𝖓𝖎𝖈𝖔𝖉e;False;False;True;True;False;False;True;True;False;False;True;True;True;True;False;False;True;False;False;False;False;False
;;;u𝖓𝖎𝖈𝖔𝖉𝖊;False;False;True;True;False;False;True;True;True;True;False;False;False;False;True;True;True;False;False;False;False;False
;;;𝖚𝖓𝖎𝖈𝖔𝖉𝖊;False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;False;True;False;False;False;False
1;;;["ÁRVÍZTŰRŐ tükörfúrógép", "unicode"];False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;True;False;True;True;False;False
1;;;["ÁRVÍZTŰRŐ", "tükörfúrógép", "u𝖓𝖎𝖈𝖔𝖉e"];False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;True;False;True;True;False;False
1;;;["ÁRVÍZTŰRŐ", "tükörfúrógép", "unicode"];False;False;True;True;False;False;True;True;False;False;True;True;False;False;True;True;True;False;False;False;True;True"""
    override val remoteJson =
        """{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":0,
      "s":"V25d\u002Bbk2NfkY\u002Bq0Gle0pM361WEHJzwyWQ\u002BcSfdVs9hQ="
   },
   "f":{
      "boolArrayContainsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":34,
                        "l":[
                           "ÁRVÍZTŰRŐ tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"d7581d1d"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"ab278c56"
      },
      "boolArrayContainsHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":26,
                        "l":[
                           "c562d4492396a997352aeae7187b9c9f5e73798a8d3681da68a27b6997cb3763",
                           "4641c3569ae7d23b5907409ce15d906b871aa343e40a2cc2e58c82b4c786f295"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"3dbafdf8"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"351da900"
      },
      "boolArrayNotContainsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":35,
                        "l":[
                           "ÁRVÍZTŰRŐ tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"4e2a9e02"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"e60bde32"
      },
      "boolArrayNotContainsHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":27,
                        "l":[
                           "09372ae681c13ba4ae65d8b22461173e0f26ccf808709cedb5bcee0db2ca2beb",
                           "4e5d15f7644d53aa4febc85c682026fcbbb10e972f0eb5991a4a57c4ad19dfc3"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"c434fcb9"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"fec0125d"
      },
      "boolContainsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":2,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"1b0c7055"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"9b855029"
      },
      "boolEndsWithCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":32,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"968cc630"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"32e831fc"
      },
      "boolEndsWithHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":24,
                        "l":[
                           "13_950274ee38ff82f00ad5b9e3c5b7ef67d6999efc9062de88cc8d4a288c74ab00",
                           "17_4f87fcc086e15c043caadc8531864615426b5b4fe105626f0da2659f4bcc7683",
                           "9_6645286b3254cb3e067cd6b6e20f099e010d2e0b4c1f1830b8aee4ea24e77efc",
                           "13_0e82c292cf1bc116506703deadefd93f7436792e5c459f032c8030fc840ea66c"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"d0f8c4cc"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"582d003c"
      },
      "boolIsNotOneOfCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":1,
                        "l":[
                           "ÁRVÍZTŰRŐ tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"d507e0d2"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"b240d07d"
      },
      "boolIsNotOneOfHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":17,
                        "l":[
                           "f3802db2e5f5f3416f57106d9bfe4a1748f5fe53f6dce33c0182cc753ff90cf6",
                           "0e783d5dd44d6d234f47438b69641e7393fc90cd7e10faa38453f95b1d6547b9"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"4c287ac1"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"511ebd2c"
      },
      "boolIsOneOfCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":0,
                        "l":[
                           "ÁRVÍZTŰRŐ tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"7267e267"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"a784f049"
      },
      "boolIsOneOfHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":16,
                        "l":[
                           "eccc74e9fb25cec6db6883fa28ddbb470d3b088fc2667798cb80e42945f3af64",
                           "585d3de5e9ddd535cf1b3b69642a45ea96677eea3b01dc8b51a68df1dc8fcbd6"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"e1869f25"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"2ca5d4f6"
      },
      "boolNotContainsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":3,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"10540ea2"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"a0746844"
      },
      "boolNotEndsWithCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":33,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"13185229"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"ea6e6660"
      },
      "boolNotEndsWithHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":25,
                        "l":[
                           "13_8728453fa52044c6b94bf26bb5f867a35dc2f0f903cad3dbddf856b84d6d4f46",
                           "17_5e72011508c9ee9bb7e07b2f014f5d521e26f86e82a620802e78328ba7ee6bee",
                           "9_100aacb55aefcd99af3926a48255e61982ca252f747f18a133c7ced0b3b6351e",
                           "13_d5dbcf0d6c2771a5059c89da62f6828a497ab0d3e010a0e52dd7cf339d3a9a50"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"268dd59d"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"0264ab4c"
      },
      "boolNotStartsWithCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":31,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"cefae65b"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"c66b8227"
      },
      "boolNotStartsWithHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":23,
                        "l":[
                           "13_e684a44ab005250167a45d4b6e43c8131c02af075bd6d1acd67a9dba488cf1a9",
                           "17_e12051ab65d6ce71c027116cf7d8c47bdbcbddccc0960b83774265871f77885e",
                           "9_d10a8565bb05a756315f429def3f73de18e44c09ae3eeadaecffb9ff369bd0f1",
                           "13_da24e3e3d5784aadd3d260173c1a218ebf9d3d00c83a301f131d1bf0128ea1ab"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"cddfa2af"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"6ed7179f"
      },
      "boolStartsWithCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":30,
                        "l":[
                           "ÁRVÍZTŰRŐ",
                           "tükörfúrógép",
                           "u\uD835\uDD93\uD835\uDD8E",
                           "\uD835\uDD88\uD835\uDD94\uD835\uDD89e"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"a92d0998"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"fb61ec46"
      },
      "boolStartsWithHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":22,
                        "l":[
                           "13_18c344560e31a88013112f486a0a7ff3449af2385dbb62d7f63f16dfe585f35a",
                           "17_50879525d9d9ad528d904640340d8e59be77d01a16a6a88a04fb69df26164a67",
                           "9_5562cd5ac3d55ae55721086916db054c5dcfd1029685861f3cfb7a7ccd601729",
                           "13_400a0593a70d31f129473b0a9fdab090677d61cd1993ca736bf749135a080423"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"694c5376"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"8ece2cfc"
      },
      "boolTextEqualsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":28,
                        "s":"ʄǟռƈʏ ȶɛӼȶ"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"fc2c6a61"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"a86779a6"
      },
      "boolTextEqualsHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":20,
                        "s":"8d08b67cbb7dd65798ca58873eaf25f74a4aba7d6878e83b9d4c3d54afb311e7"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"e7c0d232"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"4079953c"
      },
      "boolTextNotEqualsCleartext":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":29,
                        "s":"ʄǟռƈʏ ȶɛӼȶ"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"2fc8ce05"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"d259a169"
      },
      "boolTextNotEqualsHashed":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"\uD83C\uDD83\uD83C\uDD74\uD83C\uDD87\uD83C\uDD83",
                        "c":21,
                        "s":"6df3551476d9f5dc9a82e9eb861db949555a1e195180a44560e7c3ff70e19320"
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"fe477fc6"
               }
            }
         ],
         "v":{
            "b":false
         },
         "i":"5252b4c2"
      }
   }
}"""
}
