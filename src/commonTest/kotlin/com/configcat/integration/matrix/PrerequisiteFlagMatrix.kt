package com.configcat.integration.matrix

object PrerequisiteFlagMatrix : DataMatrix {
    override val sdkKey = "configcat-sdk-1/XUbbCFZX_0mOU_uQ_XYGMg/Lv2mD9Tgx0Km27nuHjw_FA"
    override val data =
        """Identifier;Email;Country;Custom1;mainBoolFlag;mainStringFlag;mainIntFlag;mainDoubleFlag;stringDependsOnBool;stringDependsOnString;stringDependsOnStringCaseCheck;stringDependsOnInt;stringDependsOnDouble;stringDependsOnDoubleIntValue;boolDependsOnBool;intDependsOnBool;doubleDependsOnBool;boolDependsOnBoolDependsOnBool;mainBoolFlagEmpty;stringDependsOnEmptyBool;stringInverseDependsOnEmptyBool;mainBoolFlagInverse;boolDependsOnBoolInverse
##null##;;;;True;public;42;3.14;Dog;Cat;Cat;Cat;Cat;Cat;True;1;1.1;False;True;EmptyOn;EmptyOn;False;True
;;;;True;public;42;3.14;Dog;Cat;Cat;Cat;Cat;Cat;True;1;1.1;False;True;EmptyOn;EmptyOn;False;True
john@sensitivecompany.com;john@sensitivecompany.com;##null##;##null##;False;private;2;0.1;Cat;Dog;Cat;Dog;Dog;Cat;False;42;3.14;True;True;EmptyOn;EmptyOn;True;False
jane@example.com;jane@example.com;##null##;##null##;True;public;42;3.14;Dog;Cat;Cat;Cat;Cat;Cat;True;1;1.1;False;True;EmptyOn;EmptyOn;False;True""".trimIndent()
    override val remoteJson =
        """{
  "p": {
    "u": "https://test-cdn-eu.configcat.com",
    "r": 0,
    "s": "p\u002BMaxP5JLS0HoMC\u002BoGGTnrAP5VL7czEx0F5SHsuOwzg="
  },
  "f": {
    "boolDependsOnBool": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlag",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "8dc94c1d"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "d6194760"
    },
    "boolDependsOnBoolDependsOnBool": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "boolDependsOnBool",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "b": false
            },
            "i": "d6870486"
          }
        }
      ],
      "v": {
        "b": true
      },
      "i": "cd4c95e7"
    },
    "boolDependsOnBoolInverse": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlagInverse",
                "c": 1,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "3c09bff0"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "cecbc501"
    },
    "doubleDependsOnBool": {
      "t": 3,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlag",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "d": 1.1
            },
            "i": "271fd003"
          }
        }
      ],
      "v": {
        "d": 3.14
      },
      "i": "718aae2b"
    },
    "intDependsOnBool": {
      "t": 2,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlag",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "i": 1
            },
            "i": "d2dda649"
          }
        }
      ],
      "v": {
        "i": 42
      },
      "i": "43ec49a8"
    },
    "mainBoolFlag": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_b3ee43186c09233376dd8d2394450c4f899817a335c4d9213e10292d0a9b7b05"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "b": false
            },
            "i": "e842ea6f"
          }
        }
      ],
      "v": {
        "b": true
      },
      "i": "8a68b064"
    },
    "mainBoolFlagEmpty": {
      "t": 0,
      "v": {
        "b": true
      },
      "i": "f3295d43"
    },
    "mainBoolFlagInverse": {
      "t": 0,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_40c8122bec31cb64a6d9179c9784d5cdc7fe451931452a110a9b2e0a3f962fbb"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "b": true
            },
            "i": "28c65f1f"
          }
        }
      ],
      "v": {
        "b": false
      },
      "i": "d70e47a7"
    },
    "mainDoubleFlag": {
      "t": 3,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_591f14e5eba4d699e95e15c8770fc3f981e4716a3ceca10270cde83096fe946e"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "d": 0.1
            },
            "i": "a67947ed"
          }
        }
      ],
      "v": {
        "d": 3.14
      },
      "i": "beb3acc7"
    },
    "mainIntFlag": {
      "t": 2,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_ff2aa9a8e2ed3b9c2b0b1e99accfd4e9e134f5ae016476e151f3d04d6d1cef97"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "i": 2
            },
            "i": "67e14078"
          }
        }
      ],
      "v": {
        "i": 42
      },
      "i": "a7490aca"
    },
    "mainStringFlag": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "t": {
                "a": "Email",
                "c": 24,
                "l": [
                  "21_efe9ef40a5a5ab6bbc685463594f6970e917f96948e9f7798b9be9daf2926c59"
                ]
              }
            }
          ],
          "s": {
            "v": {
              "s": "private"
            },
            "i": "51b57fb0"
          }
        }
      ],
      "v": {
        "s": "public"
      },
      "i": "24c96275"
    },
    "stringDependsOnBool": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlag",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "fc8daf80"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "d53a2b42"
    },
    "stringDependsOnDouble": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainDoubleFlag",
                "c": 0,
                "v": {
                  "d": 0.1
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "84fc7ed9"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "9cc8fd8f"
    },
    "stringDependsOnDoubleIntValue": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainDoubleFlag",
                "c": 0,
                "v": {
                  "d": 0
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "842c1d75"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "db7f56c8"
    },
    "stringDependsOnEmptyBool": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlagEmpty",
                "c": 0,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "EmptyOn"
            },
            "i": "d5508c78"
          }
        }
      ],
      "v": {
        "s": "EmptyOff"
      },
      "i": "8e0dbe88"
    },
    "stringDependsOnInt": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainIntFlag",
                "c": 0,
                "v": {
                  "i": 2
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "12531eec"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "e227d926"
    },
    "stringDependsOnString": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainStringFlag",
                "c": 0,
                "v": {
                  "s": "private"
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "426b6d4d"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "d36000e1"
    },
    "stringDependsOnStringCaseCheck": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainStringFlag",
                "c": 0,
                "v": {
                  "s": "Private"
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "Dog"
            },
            "i": "87d24aed"
          }
        }
      ],
      "v": {
        "s": "Cat"
      },
      "i": "ad94f385"
    },
    "stringInverseDependsOnEmptyBool": {
      "t": 1,
      "r": [
        {
          "c": [
            {
              "d": {
                "f": "mainBoolFlagEmpty",
                "c": 1,
                "v": {
                  "b": true
                }
              }
            }
          ],
          "s": {
            "v": {
              "s": "EmptyOff"
            },
            "i": "b7c3efae"
          }
        }
      ],
      "v": {
        "s": "EmptyOn"
      },
      "i": "f6b4b8a2"
    }
  }
}

"""
}
