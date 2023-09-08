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
    "s": "8KM\u002B0ufTEkAeEZXy43ouhrfBTplpgPggG4UdowiBMZ0="
  },
  "f": {
    "allinone": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 20,
                "s": "1ed8023e01d8cd47851f86fed37b6a67cd5c7b73d1e54c6eabaac4a8944fcb8e"
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 21,
                "s": "1ed8023e01d8cd47851f86fed37b6a67cd5c7b73d1e54c6eabaac4a8944fcb8e"
              }
            }
          ],
          "s": {
            "v": {
              "s": "1"
            },
            "i": "ab0645f7"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 16,
                "l": [
                  "1ed8023e01d8cd47851f86fed37b6a67cd5c7b73d1e54c6eabaac4a8944fcb8e"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 17,
                "l": [
                  "1ed8023e01d8cd47851f86fed37b6a67cd5c7b73d1e54c6eabaac4a8944fcb8e"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "2"
            },
            "i": "dbe98f44"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "4_7e7f272219cfcd424b049db623d1747dce6011c21ac2c38eed13296f6292e905"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 23,
                "l": [
                  "4_7e7f272219cfcd424b049db623d1747dce6011c21ac2c38eed13296f6292e905"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "3"
            },
            "i": "e7121806"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "12_b444a797ae3b0c4c52f91953467048159c348ca5e61896fc431b69ed9c3d581d"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 25,
                "l": [
                  "12_b444a797ae3b0c4c52f91953467048159c348ca5e61896fc431b69ed9c3d581d"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "4"
            },
            "i": "579da034"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 2,
                "l": [
                  "e@e"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 3,
                "l": [
                  "e@e"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "5"
            },
            "i": "dd12c429"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Version",
                "c": 4,
                "l": [
                  "1.0.0"
                ]
              }
            },
            {
              "t": {
                "a": "Version",
                "c": 5,
                "l": [
                  "1.0.0"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "6"
            },
            "i": "dba5d266"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Version",
                "c": 6,
                "s": "1.0.1"
              }
            },
            {
              "t": {
                "a": "Version",
                "c": 9,
                "s": "1.0.1"
              }
            }
          ],
          "s": {
            "v": {
              "s": "7"
            },
            "i": "1637ffc5"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Version",
                "c": 8,
                "s": "0.9.9"
              }
            },
            {
              "t": {
                "a": "Version",
                "c": 7,
                "s": "0.9.9"
              }
            }
          ],
          "s": {
            "v": {
              "s": "8"
            },
            "i": "b084ddd6"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Number",
                "c": 10,
                "d": 1
              }
            },
            {
              "t": {
                "a": "Number",
                "c": 11,
                "d": 1
              }
            }
          ],
          "s": {
            "v": {
              "s": "9"
            },
            "i": "d1d537a6"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Number",
                "c": 12,
                "d": 1.1
              }
            },
            {
              "t": {
                "a": "Number",
                "c": 15,
                "d": 1.1
              }
            }
          ],
          "s": {
            "v": {
              "s": "10"
            },
            "i": "52c846d0"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Number",
                "c": 14,
                "d": 0.9
              }
            },
            {
              "t": {
                "a": "Number",
                "c": 13,
                "d": 0.9
              }
            }
          ],
          "s": {
            "v": {
              "s": "11"
            },
            "i": "c91ffb7c"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Date",
                "c": 18,
                "d": 1693497600
              }
            },
            {
              "t": {
                "a": "Date",
                "c": 19,
                "d": 1693497600
              }
            }
          ],
          "s": {
            "v": {
              "s": "12"
            },
            "i": "c12182ef"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Country",
                "c": 26,
                "l": [
                  "c8ac3838448639ee79c5f45a3a752f672161842d7ebc8e2999a12ec7d201aec3"
                ]
              }
            },
            {
              "t": {
                "a": "Country",
                "c": 27,
                "l": [
                  "c8ac3838448639ee79c5f45a3a752f672161842d7ebc8e2999a12ec7d201aec3"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "13"
            },
            "i": "37431937"
          }
        }
      ],
      "v": {
        "s": "default"
      },
      "i": "9ff25f81"
    },
    "arrayContainsCaseCheckDogDefaultCat": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Custom1",
                "c": 26,
                "l": [
                  "8211e4fa2fd20f43fcc98f600a42cb266cacd5dde1da5cd27d9cec44fb60ade7"
                ]
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
                "l": [
                  "dbbb639e2a4f655ddf8acccd89d15f1b371f9ed27b89ae24bd9b19401ab52b5e"
                ]
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
                "l": [
                  "ef89e18c29ec8aac2025968fbe6584441a1904ea8087edcd9542977baf3c3321"
                ]
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
                "l": [
                  "5bfedc599f556d29521ac022ee04b3e66a7972f6b09138fba36cacbdc19f83e9"
                ]
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
                  "14_b6e2dd0f98acd7f196a919898fd194704be70bd7dee979050afc556857a9d7d3"
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
                  "14_b6e2dd0f98acd7f196a919898fd194704be70bd7dee979050afc556857a9d7d3"
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
                "s": "662bc16ccd7780d0961f8006e930ec556073f16f8df545088bdd9fa6d82bc894"
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
                  "14_554603f28c0558ad8f8fee81b3ee9810e2993f879f14548f38dd739c78127f08"
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
                "s": "41bb1cbb0bc266eda7cb9b17edfb45039a756aaea00bd17a5188ccc8312cd05c"
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
                  "14_5be70635eda656fc7de22094f6960bca70b1792714490b6786e507b79161dde8"
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
                  "1_f9eed1ca617f5306990bba77dcb609c5c0e35e621d91a3b2fbf0a14e14866f6c"
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
                  "1_a103962b0c5a7b4f1b5bdc5be829e00422d56cdaa1c7b42a5f7f12c03dd3b97f"
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
