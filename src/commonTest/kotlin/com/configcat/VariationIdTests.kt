package com.configcat

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class VariationIdTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testGetVariationId() = runTest {
        val mockEngine = MockEngine {
            respond(content = variationIdBody, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val variationId = client.getValueDetails("key1", "defaultValue").variationId
        assertEquals("fakeId1", variationId)
    }

    @Test
    fun testGetVariationIdNotFound() = runTest {
        val mockEngine = MockEngine {
            respond(content = variationIdBody, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val variationId = client.getValueDetails("nonexisting", "defaultValue").variationId
        assertEquals("", variationId)
    }

    @Test
    fun testGetAllVariationIds() = runTest {
        val mockEngine = MockEngine {
            respond(content = variationIdBody, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val allValueDetails = client.getAllValueDetails()

        assertEquals(2, allValueDetails.size)
        assertEquals("fakeId1", allValueDetails.elementAt(0).variationId)
        assertEquals("fakeId2", allValueDetails.elementAt(1).variationId)
    }

    @Test
    fun testGetAllVariationIdsEmpty() = runTest {
        val mockEngine = MockEngine {
            respond(content = "{}", status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val allValueDetails = client.getAllValueDetails()
        assertEquals(0, allValueDetails.size)
    }

    @Test
    fun testGetKeyAndValue() = runTest {
        val mockEngine = MockEngine {
            respond(content = variationIdBody, status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
    }

    @Test
    fun testGetKeyAndValueNotFound() = runTest {
        val mockEngine = MockEngine {
            respond(content = "{}", status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertNull(client.getKeyAndValue("fakeId1"))
    }

    companion object {
        const val variationIdBody = """
                   {   "p": {"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"},
                       "f":{
                           "key1":{
                                "t": 0,
                                "v":{ "b": true},
                                "i":"fakeId1",
                                "p":[
                                    {
                                        "v":{ "b": true},
                                        "p":50,
                                        "i":"percentageId1"
                                    },
                                    {
                                        "v":{ "b": false},
                                        "p":50,
                                        "i":"percentageId2"
                                    }
                                ],
                                "r":[
                                    {
                                        "c": [{ 
                                                "t": {
                                                    "a":"Email",
                                                    "c":2,
                                                    "l":["@configcat.com"]
                                                 }
                                            }],
                                        "p": [
                                        ],
                                        "s": {
                                            "v": { "b": true}, 
                                            "i": "rolloutId1"
                                        }
                                    }, 
                                    {
                                        "c": [{ 
                                                "t": {
                                                    "a":"Email",
                                                    "c":2,
                                                    "l": ["@test.com"]
                                               }
                                        }],
                                        "p": [
                                        ],
                                        "s": {
                                            "v": { "b": false}, 
                                            "i": "rolloutId2"
                                        }
                                    }
                                ],
                                "a":""
                           },
                           "key2":{
                                "t": 0,
                                "v": { "b": false},
                                "i":"fakeId2",
                                "p":[],
                                "r":[],
                                "a":""
                           }
                       },
                       "s":[]
                   }
                   """
    }
}
