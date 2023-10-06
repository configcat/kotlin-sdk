package com.configcat.integration.matrix

object SensitiveMatrix : DataMatrix {
    override val sdkKeyV5: String? = "PKDVCLf-Hq-h-kCzMp-L7Q/qX3TP2dTj06ZpCCT1h_SPA"
    override val sdkKeyV6: String? = "configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/-0YmVOUNgEGKkgRF-rU65g"
    override val data = """Identifier;Email;Country;Custom1;isOneOfSensitive;isNotOneOfSensitive
##null##;;;;ToAll;ToAll
id1;macska@example.com;;;Macska;Kigyo
Kutya;;;;Allat;ToAll
Sas;;;;ToAll;Kigyo
Kutya;macska@example.com;;;Macska;ToAll
id1;;Scotland;;Britt;Kigyo
Macska;;USA;;ToAll;Ireland"""
    override val remoteJson =
        """{
            "p": {
                "u":"https://cdn-global.configcat.com",
                "r":0,
                "s": "PTTl5hs8rhXMOBZju\u002B30y8SsG0F4GSqhrMS\u002Bd1HGRW0="
            },
            "f": {
                "isNotOneOfSensitive": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Identifier",
                            "c": 17,
                            "l": [
                              "61338bc24f4393fb5266167100d4ab5f56f5f146fa0c1c44d0ae9dee2d2ff0e6",
                              "ea4669a7df3b1c9989ce11e6fe1def6b92a07412c1ed5583aed6b16cca7de03c"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Kigyo"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 17,
                            "l": [
                              "f7995450d2d32812f13d40d8c24764d01c39685fcd9bd7cc9cb66c3288564e7a",
                              "a16c6e1a1e1bfc8f455b1f8c8756731cf5c6f456cc3e6c5c5a4226f427459d38"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Angolna"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Country",
                            "c": 17,
                            "l": [
                              "aedda83026d352c585ea7923307fb5c77859e0a68949fcb7c6c76baea517d6c1",
                              "53652982b82dc7b32a3681ce4f0d4a6e3643333d48e5678a31d9dd46a7bc3418",
                              "a69168fa5b2793618e0c62770c256ac568fc8322541634ed1d5bde7dcaf763fc"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Ireland"
                        }
                      }
                    }
                  ],
                  "v": {
                    "s": "ToAll"
                  }
                },
                "isOneOfSensitive": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 16,
                            "l": [
                              "980203a2d47f455ea84562067049bfbabe43032d750eac8471f7003e2ffcf26a"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Macska"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Identifier",
                            "c": 16,
                            "l": [
                              "8213c46251fb349f7c332e53a22238815cfba02bed3124b51cd3011be0dbb388",
                              "4e8611c778dfd8516d43a3b9d12544674aeef2726e333dcafd158b8dce029343"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Allat"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Country",
                            "c": 16,
                            "l": [
                              "ec9d3a16c19d872cd835f8fcf7d366fb960653d41719048db325e8a0343155d3",
                              "a3c1959a63910936a728f72bc133e1cc42120d2458d95eb041c34213567d7dc9",
                              "111d1e465f7a84483de93bffbc344e98150e8a89c6a5830a7e17fa2b1bf45546"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Britt"
                        }
                      }
                    }
                  ],
                  "v": {
                    "s": "ToAll"
                  }
                }
              }
            }
            """.trimIndent()
}
