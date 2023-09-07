package com.configcat.integration.matrix

object ComparatorsV6Matrix : DataMatrix {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/Lv2mD9Tgx0Km27nuHjw_FA"
    override val data =
        """Identifier;Email;Country;Custom1;boolTrueIn202304;stringEqualsDogDefaultCat;stringDoseNotEqualDogDefaultCat;stringStartsWithDogDefaultCat;stringNotStartsWithDogDefaultCat;stringEndsWithDogDefaultCat;stringNotEndsWithDogDefaultCat;arrayContainsDogDefaultCat;arrayDoesNotContainDogDefaultCat;arrayContainsCaseCheckDogDefaultCat;arrayDoesNotContainCaseCheckDogDefaultCat;customPercentageAttribute;missingPercentageAttribute;countryPercentageAttribute
##null##;;;;False;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Chicken;Chicken;Chicken
;;;;False;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Cat;Chicken;Chicken;Chicken
a@configcat.com;a@configcat.com;##null##;##null##;False;Dog;Dog;Dog;Cat;Dog;Cat;Cat;Cat;Cat;Cat;Chicken;NotFound;Chicken
b@configcat.com;b@configcat.com;Hungary;0;False;Cat;Cat;Cat;Dog;Dog;Cat;Cat;Dog;Cat;Dog;Horse;NotFound;Falcon
c@configcat.com;c@configcat.com;United Kingdom;1680307199.9;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Dog;Cat;Dog;Falcon;NotFound;Falcon
anna@configcat.com;anna@configcat.com;Hungary;1681118000.56;True;Cat;Dog;Dog;Cat;Dog;Cat;Cat;Dog;Cat;Dog;Falcon;NotFound;Falcon
bogjobber@verizon.net;bogjobber@verizon.net;##null##;1682899200.1;False;Cat;Dog;Cat;Dog;Cat;Dog;Cat;Dog;Cat;Dog;Horse;Chicken;Chicken
cliffordj@aol.com;cliffordj@aol.com;Austria;1682999200;False;Cat;Dog;Cat;Dog;Cat;Dog;Cat;Dog;Cat;Dog;Falcon;Chicken;Falcon
reader@configcat.com;reader@configcat.com;Bahamas;read,execute;False;Cat;Dog;Cat;Dog;Dog;Cat;Dog;Dog;Cat;Dog;Falcon;NotFound;Falcon
writer@configcat.com;writer@configcat.com;Belgium;write, execute;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Cat;Cat;Dog;Horse;NotFound;Horse
reader@configcat.com;reader@configcat.com;Canada;execute, Read;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Dog;Dog;Dog;Horse;NotFound;Horse
writer@configcat.com;writer@configcat.com;China;Write;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Dog;Cat;Cat;Falcon;NotFound;Horse
admin@configcat.com;admin@configcat.com;France;read, write,execute;False;Cat;Dog;Dog;Cat;Dog;Cat;Dog;Cat;Cat;Dog;Falcon;NotFound;Horse
user@configcat.com;user@configcat.com;Greece;,execute;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Dog;Cat;Dog;Falcon;NotFound;Horse
user@configcat.com;user@configcat.com;Monaco;,null, ,,nil, None;False;Cat;Dog;Cat;Dog;Dog;Cat;Cat;Dog;Cat;Dog;Falcon;NotFound;Horse""".trimIndent()
    override val remoteJson =
        """{
  "p": {
    "u": "https://test-cdn-eu.configcat.com",
    "r": 0,
    "s": "fnsN/dCtZSYiyzV3Jwjps3ZiDBt311Mt8mF8RYQBKsE="
  },
  "f": {
    "arrayContainsCaseCheckDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 26,
                "s": "3e359449038d715931c5e91421a2bf1f27ce99a38234e8a415a7ead5509affcd"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "5d80eff1"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "ce055a38"
    },
    "arrayContainsDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 26,
                "s": "1c4f5d58afe08fdaa369f8ed8409c33b7548180585b1542c90c0d88751ebfced"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "147fdd01"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "5f573f9c"
    },
    "arrayDoesNotContainCaseCheckDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 27,
                "s": "de05a57254747b9bab36de7725edc1046fe5b0c94dd25cdf1b6189052edb4bce"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "d4ad5730"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "df4915fd"
    },
    "arrayDoesNotContainDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 27,
                "s": "19f9de551c58edf500e6f50e84a19c056856de2e79232bea54213ac8841c2f26"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "c2161ac9"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "41910880"
    },
    "boolTrueIn202304": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 19,
                "d": 1680307200
              }
            },
            {
              "t": {
                "a": "Custom1",
                "c": 18,
                "d": 1682899200
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "6948d7cd"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "ae2a09bd"
    },
    "countryPercentageAttribute": {
      "t": 1,
      "a": "Country",
      "p": [
        {
          "p": 50,
          "v": {
            "s": "Falcon"
          },
          "i": "2b05fd81"
        },
        {
          "p": 50,
          "v": {
            "s": "Horse"
          },
          "i": "e28b6a82"
        }
      ],
      "v": {
        "s": "Chicken"
      },
      "i": "29bb6bbb"
    },
    "customPercentageAttribute": {
      "t": 1,
      "a": "Custom1",
      "p": [
        {
          "p": 50,
          "v": {
            "s": "Falcon"
          },
          "i": "3715712d"
        },
        {
          "p": 50,
          "v": {
            "s": "Horse"
          },
          "i": "7b3542d5"
        }
      ],
      "v": {
        "s": "Chicken"
      },
      "i": "50466fb6"
    },
    "missingPercentageAttribute": {
      "t": 1,
      "a": "NotFound",
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "14_4f37ad4871d3190f63ebfdba79ed8367ae8aa3c4eaa8611bc5b14ec8ef2945da"
                ]
              }
            }
          ],
          "p": [
            {
              "p": 50,
              "v": {
                "s": "Falcon"
              },
              "i": "4b7d88ba"
            },
            {
              "p": 50,
              "v": {
                "s": "Horse"
              },
              "i": "a1c2c9a9"
            }
          ]
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "14_4f37ad4871d3190f63ebfdba79ed8367ae8aa3c4eaa8611bc5b14ec8ef2945da"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "NotFound"
            },
            "i": "8aa042fe"
          }
        }
      ],
      "v": {
        "s": "Chicken"
      },
      "i": "e5107172"
    },
    "stringDoseNotEqualDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 21,
                "s": "d876e020501be8c2e9ed0943adca9e26e995549c024ffe7c42f8c03d67346335"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "8e423808"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "1835a09a"
    },
    "stringEndsWithDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "14_9854d684e4793c1c646c78a1ddfd75d21939ef3f356fdce86b2596bb1467d10a"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "d7a00741"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "45b7d922"
    },
    "stringEqualsDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 20,
                "s": "fbd972b07a5c2c8e2b088643a4d9470b793439fdb5682356f1952dd973faee3c"
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "703c31ed"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "adc0b01c"
    },
    "stringNotEndsWithDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 25,
                "l": [
                  "14_f0bc12657df12b3b1e50df16f4e2249b01d543a57b25c052b32128806af788c6"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "d37b6f18"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "91ba1bcb"
    },
    "stringNotStartsWithDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 23,
                "l": [
                  "1_908943b62e1814fd3752d894444a817562a75549a525108c472c189bacd5c033"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "72c4e1ac"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "2b16da78"
    },
    "stringStartsWithDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "1_1e9fe2effb394cf60fea3ebb72bda5bc82c06a0dec14ad306322fd8df236e87c"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "3b409872"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "3659b0fe"
    }
  }
}
"""
}
