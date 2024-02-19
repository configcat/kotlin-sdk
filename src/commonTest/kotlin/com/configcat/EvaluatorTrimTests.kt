package com.configcat

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class EvaluatorTrimTests {

    private val testIdentifier = "12345"
    private val testVersion = "1.0.0"
    private val testNumber = "3"
    private val testCountry = "[\"USA\"]"
    private val testCountryWithWhiteSpaces = "[\" USA \"]"

    //1705253400 - 2014.01.14 17:30:00 +1 - test check between 17:00 and 18:00
    private val testDate = "1705253400"

    @Test
    fun runComparatorValueTrimsTest() = runTest {
        runComparatorValueTrimsTest("isoneof", "no trim")
        runComparatorValueTrimsTest("isnotoneof", "no trim")
        runComparatorValueTrimsTest("containsanyof", "no trim")
        runComparatorValueTrimsTest("notcontainsanyof", "no trim")
        runComparatorValueTrimsTest("isoneofhashed", "no trim")
        runComparatorValueTrimsTest("isnotoneofhashed", "no trim")
        runComparatorValueTrimsTest("equalshashed", "no trim")
        runComparatorValueTrimsTest("notequalshashed", "no trim")
        runComparatorValueTrimsTest("arraycontainsanyofhashed", "no trim")
        runComparatorValueTrimsTest("arraynotcontainsanyofhashed", "no trim")
        runComparatorValueTrimsTest("equals", "no trim")
        runComparatorValueTrimsTest("notequals", "no trim")
        runComparatorValueTrimsTest("startwithanyof", "no trim")
        runComparatorValueTrimsTest("notstartwithanyof", "no trim")
        runComparatorValueTrimsTest("endswithanyof", "no trim")
        runComparatorValueTrimsTest("notendswithanyof", "no trim")
        runComparatorValueTrimsTest("arraycontainsanyof", "no trim")
        runComparatorValueTrimsTest("arraynotcontainsanyof", "no trim")
        // the not trimmed comparator value case an exception in case of these comparator, default value expected
        // the not trimmed comparator value case an exception in case of these comparator, default value expected
        runComparatorValueTrimsTest("startwithanyofhashed", "default")
        runComparatorValueTrimsTest("notstartwithanyofhashed", "default")
        runComparatorValueTrimsTest("endswithanyofhashed", "default")
        runComparatorValueTrimsTest("notendswithanyofhashed", "default")
        //semver comparator values trimmed because of backward compatibility
        //semver comparator values trimmed because of backward compatibility
        runComparatorValueTrimsTest("semverisoneof", "4 trim")
        runComparatorValueTrimsTest("semverisnotoneof", "5 trim")
        runComparatorValueTrimsTest("semverless", "6 trim")
        runComparatorValueTrimsTest("semverlessequals", "7 trim")
        runComparatorValueTrimsTest("semvergreater", "8 trim")
        runComparatorValueTrimsTest("semvergreaterequals", "9 trim")
    }

    private suspend fun runComparatorValueTrimsTest(
        key: String,
        expectedValue: String
    ) {
        val mockEngine = MockEngine {
            respond(
                content = comparatorTestContent,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }
        val user: ConfigCatUser = createTestUser(testIdentifier, testCountry, testVersion, testNumber, testDate)

        val result: String = client.getValue(key, "default", user)

        assertEquals(expectedValue, result)

        ConfigCatClient.closeAll()
    }

    @Test
    fun runUserValueTrimsTest() = runTest {
        runUserValueTrimsTest("isoneof", "no trim")
        runUserValueTrimsTest("isnotoneof", "no trim")
        runUserValueTrimsTest("isoneofhashed", "no trim")
        runUserValueTrimsTest("isnotoneofhashed", "no trim")
        runUserValueTrimsTest("equalshashed", "no trim")
        runUserValueTrimsTest("notequalshashed", "no trim")
        runUserValueTrimsTest("arraycontainsanyofhashed", "no trim")
        runUserValueTrimsTest("arraynotcontainsanyofhashed", "no trim")
        runUserValueTrimsTest("equals", "no trim")
        runUserValueTrimsTest("notequals", "no trim")
        runUserValueTrimsTest("startwithanyof", "no trim")
        runUserValueTrimsTest("notstartwithanyof", "no trim")
        runUserValueTrimsTest("endswithanyof", "no trim")
        runUserValueTrimsTest("notendswithanyof", "no trim")
        runUserValueTrimsTest("arraycontainsanyof", "no trim")
        runUserValueTrimsTest("arraynotcontainsanyof", "no trim")
        runUserValueTrimsTest("startwithanyofhashed", "no trim")
        runUserValueTrimsTest("notstartwithanyofhashed", "no trim")
        runUserValueTrimsTest("endswithanyofhashed", "no trim")
        runUserValueTrimsTest("notendswithanyofhashed", "no trim")
        //semver comparators user values trimmed because of backward compatibility
        //semver comparators user values trimmed because of backward compatibility
        runUserValueTrimsTest("semverisoneof", "4 trim")
        runUserValueTrimsTest("semverisnotoneof", "5 trim")
        runUserValueTrimsTest("semverless", "6 trim")
        runUserValueTrimsTest("semverlessequals", "7 trim")
        runUserValueTrimsTest("semvergreater", "8 trim")
        runUserValueTrimsTest("semvergreaterequals", "9 trim")
        //number and date comparators user values trimmed because of backward compatibility
        //number and date comparators user values trimmed because of backward compatibility
        runUserValueTrimsTest("numberequals", "10 trim")
        runUserValueTrimsTest("numbernotequals", "11 trim")
        runUserValueTrimsTest("numberless", "12 trim")
        runUserValueTrimsTest("numberlessequals", "13 trim")
        runUserValueTrimsTest("numbergreater", "14 trim")
        runUserValueTrimsTest("numbergreaterequals", "15 trim")
        runUserValueTrimsTest("datebefore", "18 trim")
        runUserValueTrimsTest("dateafter", "19 trim")
        //"contains any of" and "not contains any of" is a special case, the not trimmed user attribute checked against not trimmed comparator values.
        //"contains any of" and "not contains any of" is a special case, the not trimmed user attribute checked against not trimmed comparator values.
        runUserValueTrimsTest("containsanyof", "no trim")
        runUserValueTrimsTest("notcontainsanyof", "no trim")
    }

    private suspend fun runUserValueTrimsTest(
        key: String,
        expectedValue: String
    ) {
        val mockEngine = MockEngine {
            respond(
                content = userTestContent,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }
        val user: ConfigCatUser = createTestUser(
            addWhiteSpaces(testIdentifier),
            testCountryWithWhiteSpaces,
            addWhiteSpaces(testVersion),
            addWhiteSpaces(testNumber),
            addWhiteSpaces(testDate)
        )

        val result: String = client.getValue(key, "default", user)

        assertEquals(expectedValue, result)

        ConfigCatClient.closeAll()
    }

    private fun createTestUser(
        id: String,
        country: String,
        version: String,
        number: String,
        date: String
    ): ConfigCatUser {
        val customMap = mutableMapOf<String, Any>()
        customMap["Version"] = version
        customMap["Number"] = number
        customMap["Date"] = date
        return ConfigCatUser(identifier = id, country = country, custom = customMap)
    }

    private fun addWhiteSpaces(raw: String): String {
        return " $raw "
    }

    /**
     * The comparatorTestContent contains settings with invalid comparator values. The server default handles the
     * trimming. To test the client the comparator values contains pre and post whitespaces.
     */
    /**
     * The comparatorTestContent contains settings with invalid comparator values. The server default handles the
     * trimming. To test the client the comparator values contains pre and post whitespaces.
     */
    private val comparatorTestContent = """
        {
          "p": {
            "u": "https://test-cdn-eu.configcat.com",
            "r": 0,
            "s": "zsVN1DQ9Oa2FjFc96MvPfMM5Vs+KKV00NyybJZipyf4="
          },
          "f": {
            "arraycontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 34,
                        "l": [
                          " USA "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "34 trim"
                    },
                    "i": "99c90883"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "9c66d87c"
            },
            "arraycontainsanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 26,
                        "l": [
                          " 028fdb841bf3b2cc27fce407da08f87acd3a58a08c67d819cdb9351857b14237 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "26 trim"
                    },
                    "i": "706c94b6"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "3b342be3"
            },
            "arraynotcontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 35,
                        "l": [
                          " USA "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "4eeb2176"
                  }
                }
              ],
              "v": {
                "s": "35 trim"
              },
              "i": "98bc8ebb"
            },
            "arraynotcontainsanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 27,
                        "l": [
                          " 60b747c290642863f9a6c68773ed309a9fb02c6c1ae65c77037046918f4c1d3c "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "8f248790"
                  }
                }
              ],
              "v": {
                "s": "27 trim"
              },
              "i": "278ddbe9"
            },
            "containsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 2,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "2 trim"
                    },
                    "i": "f750380a"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "c3ab37cf"
            },
            "endswithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 32,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "32 trim"
                    },
                    "i": "0ac9e321"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "777456df"
            },
            "endswithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 24,
                        "l": [
                          " 5_a6ce5e2838d4e0c27cd705c90f39e60d79056062983c39951668cf947ec406c2 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "24 trim"
                    },
                    "i": "0364bf98"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "2f6fc77b"
            },
            "equals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 28,
                        "s": " 12345 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "28 trim"
                    },
                    "i": "f2a682ca"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "0f806923"
            },
            "equalshashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 20,
                        "s": " a2868640b1fe24c98e50b168756d83fd03779dd4349d6ddab5d7d6ef8dad13bd "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "20 trim"
                    },
                    "i": "6f1798e9"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "771ecd4d"
            },
            "isnotoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 1,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "79d49e05"
                  }
                }
              ],
              "v": {
                "s": "1 trim"
              },
              "i": "61d13448"
            },
            "isnotoneofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 1,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "1c2df623"
                  }
                }
              ],
              "v": {
                "s": "17 trim"
              },
              "i": "0bc3daa1"
            },
            "isoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 0,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "0 trim"
                    },
                    "i": "308f0749"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "90984858"
            },
            "isoneofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 16,
                        "l": [
                          " 55ce90920d20fc0bf8078471062a85f82cc5ea2226012a901a5045775bace0f4 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "16 trim"
                    },
                    "i": "cd78a85d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "30b9483f"
            },
            "notcontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 3,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "4b8760c4"
                  }
                }
              ],
              "v": {
                "s": "3 trim"
              },
              "i": "f91ecf16"
            },
            "notendswithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 33,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "b0d7203e"
                  }
                }
              ],
              "v": {
                "s": "33 trim"
              },
              "i": "89740c7e"
            },
            "notendswithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 25,
                        "l": [
                          " 5_c517fc957907e30b6a790540a20172a3a5d3a7458a85e340a7b1a1ac982be278 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "059f59e3"
                  }
                }
              ],
              "v": {
                "s": "25 trim"
              },
              "i": "c1e95c48"
            },
            "notequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 29,
                        "s": " 12345 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "af1f1e95"
                  }
                }
              ],
              "v": {
                "s": "29 trim"
              },
              "i": "219e6bac"
            },
            "notequalshashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 21,
                        "s": " 31ceae14b865b0842e93fdc3a42a7e45780ccc41772ca9355db50e09d81e13ef "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "9fe2b26b"
                  }
                }
              ],
              "v": {
                "s": "21 trim"
              },
              "i": "9211e9f1"
            },
            "notstartwithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 31,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "ebe3ed2d"
                  }
                }
              ],
              "v": {
                "s": "31 trim"
              },
              "i": "7deb7219"
            },
            "notstartwithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 23,
                        "l": [
                          " 5_3643bbdd1bce4021fe4dbd55e6cc2f4902e4f50e592597d1a2d0e944fb7dfb42 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "7b606e54"
                  }
                }
              ],
              "v": {
                "s": "23 trim"
              },
              "i": "edec740e"
            },
            "semvergreater": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 8,
                        "s": " 0.1.1 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "8 trim"
                    },
                    "i": "25edfdc1"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "cb0224fd"
            },
            "semvergreaterequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 9,
                        "s": " 0.1.1 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "9 trim"
                    },
                    "i": "d8960b43"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "530ea45c"
            },
            "semverisnotoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 5,
                        "l": [
                          " 1.0.1 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "5 trim"
                    },
                    "i": "cb1bad57"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "4a7025a4"
            },
            "semverisoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 4,
                        "l": [
                          " 1.0.0 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "4 trim"
                    },
                    "i": "6cc37494"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "842a56b5"
            },
            "semverless": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 6,
                        "s": " 1.0.1 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "6 trim"
                    },
                    "i": "64c04b67"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "ae58de40"
            },
            "semverlessequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 7,
                        "s": " 1.0.1 "
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "7 trim"
                    },
                    "i": "7c62748d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "631a1888"
            },
            "startwithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 30,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "30 trim"
                    },
                    "i": "475a9c4f"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "5a73105a"
            },
            "startwithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 22,
                        "l": [
                          " 5_3e052709552ca9d5bd6c459cb7ab0389f3210f6aafc3d006a2481635e9614a7c "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "22 trim"
                    },
                    "i": "7650175d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "a38edbee"
            }
          }
        }
    """

    /**
     * trim_user_values.json contains valid settings. Expect "containsanyof" and "notcontainsanyof" flags where the
     * comparator values contains pre and post white spaces, the untrimmed user value can be properly compared
     * against the invalid data.
     */
    /**
     * trim_user_values.json contains valid settings. Expect "containsanyof" and "notcontainsanyof" flags where the
     * comparator values contains pre and post white spaces, the untrimmed user value can be properly compared
     * against the invalid data.
     */
    private val userTestContent = """
        {
          "p": {
            "u": "https://test-cdn-eu.configcat.com",
            "r": 0,
            "s": "VjBfGYcmyHzLBv5EINgSBbX6/rYevYGWQhF3Zk5t8i4="
          },
          "f": {
            "arraycontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 34,
                        "l": [
                          "USA"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "34 trim"
                    },
                    "i": "99c90883"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "9c66d87c"
            },
            "arraycontainsanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 26,
                        "l": [
                          "09d5761537a8136eb7fc45a53917b51cb9dcd2bb9b62ffa24ace0e8a7600a3c7"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "26 trim"
                    },
                    "i": "706c94b6"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "3b342be3"
            },
            "arraynotcontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 35,
                        "l": [
                          "USA"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "4eeb2176"
                  }
                }
              ],
              "v": {
                "s": "35 trim"
              },
              "i": "98bc8ebb"
            },
            "arraynotcontainsanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Country",
                        "c": 27,
                        "l": [
                          "99d06b6b3669b906803c285267f76fe4e2ccc194b00801ab07f2fd49939b6960"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "8f248790"
                  }
                }
              ],
              "v": {
                "s": "27 trim"
              },
              "i": "278ddbe9"
            },
            "endswithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 32,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "32 trim"
                    },
                    "i": "0ac9e321"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "777456df"
            },
            "endswithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 24,
                        "l": [
                          "5_7eb158c29b48b62cec860dffc459171edbfeef458bcc8e8bb62956d823eef3df"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "24 trim"
                    },
                    "i": "0364bf98"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "2f6fc77b"
            },
            "equals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 28,
                        "s": "12345"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "28 trim"
                    },
                    "i": "f2a682ca"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "0f806923"
            },
            "equalshashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 20,
                        "s": "ea0d05859bb737105eea40bc605f6afd542c8f50f8497cd21ace38e731d7eef0"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "20 trim"
                    },
                    "i": "6f1798e9"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "771ecd4d"
            },
            "isnotoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 1,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "79d49e05"
                  }
                }
              ],
              "v": {
                "s": "1 trim"
              },
              "i": "61d13448"
            },
            "isnotoneofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 1,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "1c2df623"
                  }
                }
              ],
              "v": {
                "s": "17 trim"
              },
              "i": "0bc3daa1"
            },
            "isoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 0,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "0 trim"
                    },
                    "i": "308f0749"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "90984858"
            },
            "isoneofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 16,
                        "l": [
                          "1765b470044971bbc19e7bed10112199c5da9c626455f86be109fef96e747911"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "16 trim"
                    },
                    "i": "cd78a85d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "30b9483f"
            },
            "notendswithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 33,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "b0d7203e"
                  }
                }
              ],
              "v": {
                "s": "33 trim"
              },
              "i": "89740c7e"
            },
            "notendswithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 25,
                        "l": [
                          "5_2a338d3beb8ebe2e711d198420d04e2627e39501c2fcc7d5b3b8d93540691097"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "059f59e3"
                  }
                }
              ],
              "v": {
                "s": "25 trim"
              },
              "i": "c1e95c48"
            },
            "notequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 29,
                        "s": "12345"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "af1f1e95"
                  }
                }
              ],
              "v": {
                "s": "29 trim"
              },
              "i": "219e6bac"
            },
            "notequalshashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 21,
                        "s": "650fe0e8e86030b5f73ccd77e6532f307adf82506048a22f02d95386206ecea1"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "9fe2b26b"
                  }
                }
              ],
              "v": {
                "s": "21 trim"
              },
              "i": "9211e9f1"
            },
            "notstartwithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 31,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "ebe3ed2d"
                  }
                }
              ],
              "v": {
                "s": "31 trim"
              },
              "i": "7deb7219"
            },
            "notstartwithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 23,
                        "l": [
                          "5_586ab2ec61946cb1457d4af170d88e7f14e655d9debf352b4ab6bf5bf77df3f7"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "7b606e54"
                  }
                }
              ],
              "v": {
                "s": "23 trim"
              },
              "i": "edec740e"
            },
            "semvergreater": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 8,
                        "s": "0.1.1"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "8 trim"
                    },
                    "i": "25edfdc1"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "cb0224fd"
            },
            "semvergreaterequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 9,
                        "s": "0.1.1"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "9 trim"
                    },
                    "i": "d8960b43"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "530ea45c"
            },
            "semverisnotoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 5,
                        "l": [
                          "1.0.1"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "5 trim"
                    },
                    "i": "cb1bad57"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "4a7025a4"
            },
            "semverisoneof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 4,
                        "l": [
                          "1.0.0"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "4 trim"
                    },
                    "i": "6cc37494"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "842a56b5"
            },
            "semverless": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 6,
                        "s": "1.0.1"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "6 trim"
                    },
                    "i": "64c04b67"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "ae58de40"
            },
            "semverlessequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Version",
                        "c": 7,
                        "s": "1.0.1"
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "7 trim"
                    },
                    "i": "7c62748d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "631a1888"
            },
            "startwithanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 30,
                        "l": [
                          "12345"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "30 trim"
                    },
                    "i": "475a9c4f"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "5a73105a"
            },
            "startwithanyofhashed": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 22,
                        "l": [
                          "5_67a323069ee45fef4ccd8365007d4713f7a3bc87764943b1139e8e50d1aee8fd"
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "22 trim"
                    },
                    "i": "7650175d"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "a38edbee"
            },
            "dateafter": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Date",
                        "c": 19,
                        "d": 1705251600
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "19 trim"
                    },
                    "i": "83e580ce"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "1c12e0cc"
            },
            "datebefore": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Date",
                        "c": 18,
                        "d": 1705255200
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "18 trim"
                    },
                    "i": "34614b07"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "26d4f328"
            },
            "numberequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 10,
                        "d": 3
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "10 trim"
                    },
                    "i": "6a8c0a08"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "7b8e49b9"
            },
            "numbergreater": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 14,
                        "d": 2
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "14 trim"
                    },
                    "i": "2037a7a4"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "902f9bd9"
            },
            "numbergreaterequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 15,
                        "d": 2
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "15 trim"
                    },
                    "i": "527c49d2"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "2280c961"
            },
            "numberless": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 12,
                        "d": 4
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "12 trim"
                    },
                    "i": "c454f775"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "ec935943"
            },
            "numberlessequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 13,
                        "d": 4
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "13 trim"
                    },
                    "i": "1e31aed8"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "1d53c679"
            },
            "numbernotequals": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Number",
                        "c": 11,
                        "d": 6
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "11 trim"
                    },
                    "i": "e8d7cf05"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "21c749a7"
            },
            "containsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 2,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "no trim"
                    },
                    "i": "f750380a"
                  }
                }
              ],
              "v": {
                "s": "2 trim"
              },
              "i": "c3ab37cf"
            },
            "notcontainsanyof": {
              "t": 1,
              "r": [
                {
                  "c": [
                    {
                      "u": {
                        "a": "Identifier",
                        "c": 3,
                        "l": [
                          " 12345 "
                        ]
                      }
                    }
                  ],
                  "s": {
                    "v": {
                      "s": "3 trim"
                    },
                    "i": "4b8760c4"
                  }
                }
              ],
              "v": {
                "s": "no trim"
              },
              "i": "f91ecf16"
            }
          }
        }
    """
}