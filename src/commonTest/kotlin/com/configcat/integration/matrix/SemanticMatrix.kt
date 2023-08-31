package com.configcat.integration.matrix

object SemanticMatrix : DataMatrix {
    override val sdkKey = "PKDVCLf-Hq-h-kCzMp-L7Q/BAr3KgLTP0ObzKnBTo5nhA"
    override val data =
        """Identifier;Email;Country;Custom1;isOneOf;isOneOfWithPercentage;isNotOneOf;isNotOneOfWithPercentage;lessThanWithPercentage;relations
##null##;;;;Default;Default;Default;Default;Default;Default
id1;;;0.0.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;0.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;0.2.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );< 1.0.0;< 1.0.0
id1;;;1;Default;80%;Default;80%;20%;Default
id2;;;1.0;Default;80%;Default;80%;80%;Default
id3;;;1.0.0;Is one of (1.0.0);is one of (1.0.0);Default;80%;80%;<=1.0.0
id4;;;1.0.0.0;Default;80%;Default;20%;20%;Default
id5;;;1.0.0.0.0;Default;80%;Default;80%;80%;Default
id6;;;1.0.1;Default;80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;Default
id7;;;1.0.11;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id8;;;1.0.111;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id9;;;1.0.2;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id10;;;1.0.3;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id11;;;1.0.4;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id12;;;1.0.5;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id13;;;1.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id14;;;1.1.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id15;;;1.1.2;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id16;;;1.1.3;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id17;;;1.1.4;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id18;;;1.1.5;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id19;;;1.9.0;Default;20%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;Default
id20;;;1.9.99;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;Default
id21;;;2.0.0;Default;80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);20%;>=2.0.0
id22;;;2.0.1;Is one of (   , 2.0.1, 2.0.2,    );80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;>2.0.0
id23;;;2.0.11;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0
id24;;;2.0.2;Is one of (   , 2.0.1, 2.0.2,    );80%;Is not one of (1.0.0, 3.0.1);Is not one of (1.0.0, 3.0.1);80%;>2.0.0
id25;;;2.0.3;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id26;;;3.0.0;Is one of (3.0.0);80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id27;;;3.0.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0
id28;;;3.1.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id28;;;3.1.1;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id29;;;5.0.0;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );80%;>2.0.0
id30;;;5.99.999;Default;80%;Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    );20%;>2.0.0"""
    override val remoteJson =
        """{
            "p": {
                "u":"https://cdn-global.configcat.com",
                "r":0,
                "s": "13VDn230ZoiZ0UlrxgR9P5v\u002Bvhu8/7itFsVNqtb3Mn8="
            },
            "f": {
                "isOneOf": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "1.0.0",
                              "2"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (1.0.0, 2)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "1.0.0"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (1.0.0)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "",
                              "2.0.1",
                              "2.0.2"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (   , 2.0.1, 2.0.2,    )"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3......"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3......)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3...."
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3...)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3..0"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3..0)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3.0"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3.0)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3.0."
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3.0.)"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "3.0.0"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is one of (3.0.0)"
                        }
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "isOneOfWithPercentage": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 4,
                            "l": [
                              "1.0.0"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "is one of (1.0.0)"
                        }
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 20,
                      "v": {
                        "s": "20%"
                      }
                    },
                    {
                      "p": 80,
                      "v": {
                        "s": "80%"
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "isNotOneOf": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 5,
                            "l": [
                              "1.0.0",
                              "1.0.1",
                              "2.0.0",
                              "2.0.1",
                              "2.0.2",
                              ""
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 5,
                            "l": [
                              "1.0.0",
                              "3.0.1"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is not one of (1.0.0, 3.0.1)"
                        }
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "isNotOneOfWithPercentage": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 5,
                            "l": [
                              "1.0.0",
                              "1.0.1",
                              "2.0.0",
                              "2.0.1",
                              "2.0.2",
                              ""
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is not one of (1.0.0, 1.0.1, 2.0.0   , 2.0.1, 2.0.2,    )"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 5,
                            "l": [
                              "1.0.0",
                              "3.0.1"
                            ]
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "Is not one of (1.0.0, 3.0.1)"
                        }
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 20,
                      "v": {
                        "s": "20%"
                      }
                    },
                    {
                      "p": 80,
                      "v": {
                        "s": "80%"
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "lessThanWithPercentage": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 6,
                            "s": " 1.0.0 "
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C 1.0.0"
                        }
                      }
                    }
                  ],
                  "p": [
                    {
                      "p": 20,
                      "v": {
                        "s": "20%"
                      }
                    },
                    {
                      "p": 80,
                      "v": {
                        "s": "80%"
                      }
                    }
                  ],
                  "v": {
                    "s": "Default"
                  }
                },
                "relations": {
                  "t": 1,
                  "r": [
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 6,
                            "s": "1.0.0,"
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C1.0.0,"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 6,
                            "s": "1.0.0"
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C 1.0.0"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 7,
                            "s": "1.0.0"
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003C=1.0.0"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 8,
                            "s": "2.0.0"
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003E2.0.0"
                        }
                      }
                    },
                    {
                      "c": [
                        {
                          "t": {
                            "a": "Custom1",
                            "c": 9,
                            "s": "2.0.0"
                          }
                        }
                      ],
                      "s": {
                        "v": {
                          "s": "\u003E=2.0.0"
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
            """.trimIndent()
}
