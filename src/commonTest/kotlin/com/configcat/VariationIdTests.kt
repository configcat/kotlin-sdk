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
            respond(
                content = variationIdBody,
                status = HttpStatusCode.OK
            )
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
            respond(
                content = variationIdBody,
                status = HttpStatusCode.OK
            )
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
            respond(
                content = variationIdBody,
                status = HttpStatusCode.OK
            )
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
            respond(
                content = "{}",
                status = HttpStatusCode.OK
            )
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
            respond(
                content = variationIdBody,
                status = HttpStatusCode.OK
            )
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
            respond(
                content = "{}",
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertNull(client.getKeyAndValue("fakeId1"))
    }

    companion object {
        const val variationIdBody = """
                   {"f":{
                       "key1":{
                           "v":true,
                           "i":"fakeId1",
                           "p":[
                               {
                                   "v":true,
                                   "p":50,
                                   "i":"percentageId1"
                               },
                               {
                                   "v":false,
                                   "p":50,
                                   "i":"percentageId2"
                               }
                           ],
                           "r":[
                               {
                                   "a":"Email",
                                   "t":2,
                                   "c":"@configcat.com",
                                   "v":true,
                                   "i":"rolloutId1"
                               },
                               {
                                   "a":"Email",
                                   "t":2,
                                   "c":"@test.com",
                                   "v":false,
                                   "i":"rolloutId2"
                               }
                           ]
                       },
                       "key2":{
                           "v":false,
                           "i":"fakeId2",
                           "p":[],
                           "r":[]
                       }
                   }}
                   """
    }
}
