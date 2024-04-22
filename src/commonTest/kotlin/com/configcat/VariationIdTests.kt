package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VariationIdTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testGetVariationId() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = VARIATION_ID_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val variationId = client.getValueDetails("key1", false).variationId
            assertEquals("fakeId1", variationId)
        }

    @Test
    fun testGetVariationIdNotFound() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = VARIATION_ID_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val variationId = client.getValueDetails("nonexisting", "defaultValue").variationId
            assertEquals("", variationId)
        }

    @Test
    fun testGetAllVariationIds() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = VARIATION_ID_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val allValueDetails = client.getAllValueDetails()

            assertEquals(3, allValueDetails.size)
            assertEquals("fakeId1", allValueDetails.elementAt(0).variationId)
            assertEquals("fakeId2", allValueDetails.elementAt(1).variationId)
            assertEquals("fakeId3", allValueDetails.elementAt(2).variationId)
        }

    @Test
    fun testGetAllVariationIdsEmpty() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "{}", status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val allValueDetails = client.getAllValueDetails()
            assertEquals(0, allValueDetails.size)
        }

    @Test
    fun testGetKeyAndValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = VARIATION_ID_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val kv1 = client.getKeyAndValue("fakeId2")
            assertEquals("key2", kv1?.first)
            assertFalse(kv1?.second as? Boolean ?: true)

            val kv2 = client.getKeyAndValue("percentageId2")
            assertEquals("key1", kv2?.first)
            assertFalse(kv2?.second as? Boolean ?: true)

            val kv3 = client.getKeyAndValue("rolloutId1")
            assertEquals("key1", kv3?.first)
            assertTrue(kv3?.second as? Boolean ?: false)

            val kv4 = client.getKeyAndValue("targetPercentageId2")
            assertEquals("key3", kv4?.first)
            assertFalse(kv4?.second as? Boolean ?: true)
        }

    @Test
    fun testGetKeyAndValueIncorrectTargetingRule() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = VARIATION_ID_INCORRECT_TARGETING_RULE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertNull(client.getKeyAndValue("targetPercentageId2"))
        }

    @Test
    fun testGetKeyAndValueNotFound() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "{}", status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertNull(client.getKeyAndValue("fakeId1"))
        }

    companion object {
        const val VARIATION_ID_INCORRECT_TARGETING_RULE_BODY = """
{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":"0",
      "s":"PJUt0np9JA4ukMciF3BVAVRJiwIjTOiX\u002BE8B1HQohck="
   },
   "f":{
      "incorrect":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":2,
                        "l":[
                           "@configcat.com"
                        ]
                     }
                  }
               ]
            }
         ],
         "v":{
            "b":false
         },
         "i":"incorrectId"
      }
   }
}
        """

        const val VARIATION_ID_BODY = """
{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "r":"0",
      "s":"PJUt0np9JA4ukMciF3BVAVRJiwIjTOiX\u002BE8B1HQohck="
   },
   "f":{
      "key1":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":2,
                        "l":[
                           "@configcat.com"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":true
                  },
                  "i":"rolloutId1"
               }
            },
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":2,
                        "l":[
                           "@test.com"
                        ]
                     }
                  }
               ],
               "s":{
                  "v":{
                     "b":false
                  },
                  "i":"rolloutId2"
               }
            }
         ],
         "p":[
            {
               "p":50,
               "v":{
                  "b":true
               },
               "i":"percentageId1"
            },
            {
               "p":50,
               "v":{
                  "b":false
               },
               "i":"percentageId2"
            }
         ],
         "v":{
            "b":true
         },
         "i":"fakeId1"
      },
      "key2":{
         "t":0,
         "v":{
            "b":false
         },
         "i":"fakeId2"
      },
      "key3":{
         "t":0,
         "r":[
            {
               "c":[
                  {
                     "u":{
                        "a":"Email",
                        "c":2,
                        "l":[
                           "@configcat.com"
                        ]
                     }
                  }
               ],
               "p":[
                  {
                     "p":50,
                     "v":{
                        "b":true
                     },
                     "i":"targetPercentageId1"
                  },
                  {
                     "p":50,
                     "v":{
                        "b":false
                     },
                     "i":"targetPercentageId2"
                  }
               ]
            }
         ],
         "v":{
            "b":false
         },
         "i":"fakeId3"
      }
   }
}
                   """
    }
}
