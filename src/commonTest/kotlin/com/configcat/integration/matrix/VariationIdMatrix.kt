package com.configcat.integration.matrix

object VariationIdMatrix : DataMatrix {
    override val sdkKeyV5: String? = "PKDVCLf-Hq-h-kCzMp-L7Q/nQ5qkhRAUEa6beEyyrVLBA"
    override val sdkKeyV6: String? = "configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/spQnkRTIPEWVivZkWM84lQ"
    override val data = """Identifier;Email;Country;Custom1;boolean;decimal;text;whole
##null##;;;;a0e56eda;63612d39;3f05be89;cf2e9162;
a@configcat.com;a@configcat.com;Hungary;admin;67787ae4;8f9559cf;9bdc6a1f;ab30533b;
b@configcat.com;b@configcat.com;Hungary;admin;67787ae4;8f9559cf;9bdc6a1f;ab30533b;
a@test.com;a@test.com;Hungary;admin;67787ae4;d66c5781;65310deb;ec14f6a9;
b@test.com;b@test.com;Hungary;admin;a0e56eda;d66c5781;65310deb;ec14f6a9;
cliffordj@aol.com;cliffordj@aol.com;Hungary;admin;67787ae4;8155ad7b;cf19e913;ec14f6a9;
bryanw@verizon.net;bryanw@verizon.net;Hungary;;a0e56eda;d0dbc27f;30ba32b9;61a5a033;"""
    override val remoteJson =
        """{
            "p": {
                "u":"https://cdn-global.configcat.com",
                "r":0,
                "s": "XNvUomOaJnfFzAfmqPLzbRgtU\u002BK\u002BPtFywkA\u002Bf/NsOhc="
            },
             "f": {
                "boolean": {
                  "t": 0,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 2,
                            "l": [
                              "@configcat.com"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "b": true
                        },
                        "i": "67787ae4"
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 50,
                      "v": {
                        "b": true
                      },
                      "i": "67787ae4"
                    },
                    {
                      "p": 50,
                      "v": {
                        "b": false
                      },
                      "i": "a0e56eda"
                    }
                  ],
                  "v": {
                    "b": false
                  },
                  "i": "a0e56eda"
                },
                "text": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 2,
                            "l": [
                              "@configcat.com"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "true"
                        },
                        "i": "9bdc6a1f"
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 2,
                            "l": [
                              "@test.com"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "false"
                        },
                        "i": "65310deb"
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 50,
                      "v": {
                        "s": "a"
                      },
                      "i": "30ba32b9"
                    },
                    {
                      "p": 50,
                      "v": {
                        "s": "b"
                      },
                      "i": "cf19e913"
                    }
                  ],
                  "v": {
                    "s": "c"
                  },
                  "i": "3f05be89"
                },
                "whole": {
                  "t": 2,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 2,
                            "l": [
                              "@configcat.com"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "i": 1
                        },
                        "i": "ab30533b"
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 50,
                      "v": {
                        "i": 0
                      },
                      "i": "ec14f6a9"
                    },
                    {
                      "p": 50,
                      "v": {
                        "i": -1
                      },
                      "i": "61a5a033"
                    }
                  ],
                  "v": {
                    "i": 999999
                  },
                  "i": "cf2e9162"
                },
                "decimal": {
                  "t": 3,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 2,
                            "l": [
                              "@configcat.com"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "d": -2147483647.2147484
                        },
                        "i": "8f9559cf"
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 16,
                            "l": [
                              "16c5c406a4ab19fe4924f77e61d70ea58349db2c76311e757d0acac0d76f592f"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "d": 0.12345678912345678
                        },
                        "i": "d66c5781"
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Email",
                            "c": 16,
                            "l": [
                              "5bc0abba39810e3565c0d73ff143483a76c8aa620b9567f5edb312f3c5d17c81"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "d": 0.12345678912
                        },
                        "i": "d66c5781"
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 50,
                      "v": {
                        "d": 1
                      },
                      "i": "d0dbc27f"
                    },
                    {
                      "p": 50,
                      "v": {
                        "d": 2
                      },
                      "i": "8155ad7b"
                    }
                  ],
                  "v": {
                    "d": 0
                  },
                  "i": "63612d39"
                }
              }
            }
            """
}
