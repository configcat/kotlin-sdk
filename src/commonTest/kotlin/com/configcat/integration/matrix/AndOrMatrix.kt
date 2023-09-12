package com.configcat.integration.matrix

object AndOrMatrix : DataMatrix {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/FfwncdJg1kq0lBqxhYC_7g"
    override val data =
        """Identifier;Email;Country;Custom1;mainFeature;dependentFeature;emailAnd;emailOr
##null##;;;;public;Chicken;Cat;Cat
;;;;public;Chicken;Cat;Cat
jane@example.com;jane@example.com;##null##;##null##;public;Chicken;Cat;Jane
john@example.com;john@example.com;##null##;##null##;public;Chicken;Cat;John
a@example.com;a@example.com;USA;##null##;target;Cat;Cat;Cat
mark@example.com;mark@example.com;USA;##null##;target;Dog;Cat;Mark
nora@example.com;nora@example.com;USA;##null##;target;Falcon;Cat;Cat
stern@msn.com;stern@msn.com;USA;##null##;target;Horse;Cat;Cat
jane@sensitivecompany.com;jane@sensitivecompany.com;England;##null##;private;Chicken;Dog;Jane
anna@sensitivecompany.com;anna@sensitivecompany.com;France;##null##;private;Chicken;Cat;Cat
jane@sensitivecompany.com;jane@sensitivecompany.com;england;##null##;public;Chicken;Dog;Jane
jane;jane;##null##;##null##;public;Chicken;Cat;Cat
@sensitivecompany.com;@sensitivecompany.com;##null##;##null##;public;Chicken;Cat;Cat
jane.sensitivecompany.com;jane.sensitivecompany.com;##null##;##null##;public;Chicken;Cat;Cat""".trimIndent()
    override val remoteJson =
        """{
  "p": {
    "u": "https://test-cdn-eu.configcat.com",
    "r": 0,
    "s": "W8tBvwwMoeP6Ht74jMCI7aPNTc\u002B1W6rtwob18ojXQ9U="
  },
  "s": [
    {
      "n": "Beta Users",
      "r": [
        {
          "a": "Email",
          "c": 16,
          "l": [
            "53b705ed36e670da5aef88e2f137ff20f12a54481ae594a3e76ec2ffbee0faae",
            "9a043335df07ce20b25a6f954745ba5f103cef7a612ef05b1b374940d686c9ce"
          ]
        }
      ]
    },
    {
      "n": "Developers",
      "r": [
        {
          "a": "Email",
          "c": 16,
          "l": [
            "242f9fc71048494f1b6cc133e21c56356b7c8dfdea9a666549508a6b450e47a6",
            "b2f917f06274f8f3aef56058d747507ffed572e4ef16f93df1d9220c7babe181"
          ]
        }
      ]
    }
  ],
  "f": {
    "dependentFeature": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainFeature",
                "c": 0,
                "v": {
                  "s": "target"
                }
              }
            }
          ],
          "p": [
            {
              "p": 25,
              "v": {
                "s": "Cat"
              },
              "i": "993d7ee0"
            },
            {
              "p": 25,
              "v": {
                "s": "Dog"
              },
              "i": "08b8348e"
            },
            {
              "p": 25,
              "v": {
                "s": "Falcon"
              },
              "i": "a6fb7a01"
            },
            {
              "p": 25,
              "v": {
                "s": "Horse"
              },
              "i": "699fb4bf"
            }
          ]
        }
      ],
      "v": {
        "s": "Chicken"
      },
      "i": "e6198f92"
    },
    "emailAnd": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "4_489600ff47625c552000830d4b6e37c5fc3318c7e0a41f5a863db09051db9efa"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 2,
                "l": [
                  "@"
                ]
              }
            },
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "20_be728e1753794d1f30b35c434a76fccc9b9570ceb40fea8b6af55ec9ade4e0bc"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "a1393561"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "bdabd589"
    },
    "emailOr": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "5_8e188f72736a1e6028a98d7d124281b5ab2a7011bd4e5bc1732a1d1cb440cd9c"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Jane"
            },
            "i": "01383bbf"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "5_965119e3781f6ca2f6b9c0a54992d66a458ac45249fc45369aed7d4cacc30a61"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "John"
            },
            "i": "a069dc24"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 22,
                "l": [
                  "5_312ad4bcbe280a5d5dea617727f0aac863eb394e2d0d8eff0e66e46a2dfc7d68"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "Mark"
            },
            "i": "d7b02cc0"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "ab0b46ad"
    },
    "mainFeature": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_57e6ffefe612f30121fa1b82dfb11f718a90cc5a2c39b8d9b6fccb7558dcb1d8"
                ]
              }
            },
            {
              "t": {
                "a": "Country",
                "c": 16,
                "l": [
                  "c64310b2d22611d80b9253f4a2261185456bb9f1a508b038857a3ea6cbf2f625",
                  "ec1fdd343dbeee5860be0a4744318ea98f6752346f2dd3f7013a4084d658a933"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "private"
            },
            "i": "64f8e1a6"
          }
        },
        {
          "c": [
            {
              "t": {
                "a": "Country",
                "c": 16,
                "l": [
                  "172faabf6aba529f302c5bb6d2aac5c8f3ffe6fa11dcee64cbfe1a57ad8f310c"
                ]
              }
            },
            {
              "s": {
                "s": 0,
                "c": 1
              }
            },
            {
              "s": {
                "s": 1,
                "c": 1
              }
            }
          ],
          "s": {
            "v": {
              "s": "target"
            },
            "i": "f570ef26"
          }
        }
      ],
      "v": {
        "s": "public"
      },
      "i": "f16ac582"
    }
  }
}

"""
}