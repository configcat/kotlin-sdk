package com.configcat.integration.matrix

object NumberMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/uGyK3q9_ckmdxRyI7vjwCw"
    override val data = """Identifier;Email;Country;Custom1;numberWithPercentage;number
##null##;;;;Default;Default
id1;;;0;<2.1;<>5
id1;;;0.0;<2.1;<>5
id1;;;0,0;<2.1;<>5
id1;;;0.2;<2.1;<>5
id2;;;0,2;<2.1;<>5
id3;;;1;<2.1;<>5
id4;;;1.0;<2.1;<>5
id5;;;1,0;<2.1;<>5
id6;;;1.5;<2.1;<>5
id7;;;1,5;<2.1;<>5
id8;;;2.1;<=2,1;<>5
id9;;;2,1;<=2,1;<>5
id10;;;3.50;=3.5;<>5
id11;;;3,50;=3.5;<>5
id12;;;5;>=5;Default
id13;;;5.0;>=5;Default
id14;;;5,0;>=5;Default
id13;;;5.76;>5;<>5
id14;;;5,76;>5;<>5
id15;;;4;<>4.2;<>5
id16;;;4.0;<>4.2;<>5
id17;;;4,0;<>4.2;<>5
id18;;;4.2;80%;<>5
id19;;;4,2;20%;<>5"""
    override val remoteJson =
        """{
            "p": {
                "u":"https://cdn-global.configcat.com",
                "r":0,
                "s": "/Y4mJ/uSa1GBTn2Wt5y33RohDIPavEWxe0TAqr5Lwp4"
            },
            "f": {
                "numberWithPercentage": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 12,
                            "d": 2.1
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C2.1"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 13,
                            "d": 2.1
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C=2,1"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 10,
                            "d": 3.5
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "=3.5"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 14,
                            "d": 5
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003E5"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 15,
                            "d": 5
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003E=5"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 11,
                            "d": 4.2
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C\u003E4.2"
                        }
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 80,
                      "v": {
                        "s": "80%"
                      }
                    },
                    {
                      "p": 20,
                      "v": {
                        "s": "20%"
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "number": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 11,
                            "d": 5
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C\u003E5"
                        }
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                }
              }
            }
            """
}
