package com.configcat.integration.matrix

object SegmentMatrix : DataMatrix {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/LP0_4hhbQkmVVJcsbO_2Lw"
    override val data =
        """Identifier;Email;Country;Custom1;developerAndBetaUserSegment
##null##;;;;False
;;;;False
john@example.com;john@example.com;##null##;##null##;False
jane@example.com;jane@example.com;##null##;##null##;False
kate@example.com;kate@example.com;##null##;##null##;True
""".trimIndent()
    override val remoteJson =
        """{
  "p": {
    "u": "https://test-cdn-eu.configcat.com",
    "r": 0,
    "s": "8MlFGoGg49HzYiO/Lf3Ue1AZ\u002Bm1YJ9uS38q9MnnifNM="
  },
  "s": [
    {
      "n": "Beta Users",
      "r": [
        {
          "a": "Email",
          "c": 16,
          "l": [
            "355ce67eb642fe01cb3c9972f0c910496bc5d9ec8cec9ef0bb54a81acc412c24",
            "49b5287a4269be5280e44a0f35ca632d80bbb1957bc5e14412857889fbb91406"
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
            "b5a47cdd19b4ae8c58e034d17f3d47c1711a32614be5b9896feb7381fb50052e",
            "732a6f070a1f78f73e599b6e3f116cdde13ee67f7c61c9b07cba3dee5e454c8c"
          ]
        }
      ]
    },
    {
      "n": "Not Beta Users",
      "r": [
        {
          "a": "Email",
          "c": 17,
          "l": [
            "1f3725b1b96012852d9ee416f114a59982c333a33c419d0add976c743fa1b8fa",
            "d9cc2899b7b3f8c94935f44ac5008c42fa4aa68fe7755495f8ff2eab0f50328c"
          ]
        }
      ]
    },
    {
      "n": "Not Developers",
      "r": [
        {
          "a": "Email",
          "c": 17,
          "l": [
            "79b386065c5d5f60a91e3c16457f3a3d7557922d1f5a82e4731aa92445de4436",
            "66f71cff40dc83805a3aa28be752e60a77f9c74ce87f29adf71508b079e8cd3c"
          ]
        }
      ]
    },
    {
      "n": "United",
      "r": [
        {
          "a": "Country",
          "c": 2,
          "l": [
            "United"
          ]
        }
      ]
    },
    {
      "n": "Not States",
      "r": [
        {
          "a": "Country",
          "c": 3,
          "l": [
            "States"
          ]
        }
      ]
    }
  ],
  "f": {
    "countrySegment": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "s": {
                "s": 4,
                "c": 0
              }
            },
            {
              "s": {
                "s": 5,
                "c": 0
              }
            }
          ],
          "s": {
            "v": {
              "s": "A"
            },
            "i": "9b7e6414"
          }
        }
      ],
      "v": {
        "s": "Z"
      },
      "i": "f71b6d96"
    },
    "developerAndBetaUserSegment": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "s": {
                "s": 1,
                "c": 0
              }
            },
            {
              "s": {
                "s": 0,
                "c": 1
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "ddc50638"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "6427f4b8"
    },
    "notDeveloperAndNotBetaUserSegment": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "s": {
                "s": 2,
                "c": 0
              }
            },
            {
              "s": {
                "s": 3,
                "c": 1
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "77081d42"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "a14eaf13"
    }
  }
}

"""
}
