package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class SnapshotTests {
    @Test
    fun testWaitForReadyMultiple() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            client.waitForReady()
            assertNotNull(client.snapshot())
        }

    @Test
    fun testGetValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val value = snapshot.getValue("key1", false)
            assertTrue(value)

            val anyVal = snapshot.getAnyValue("key1", false, null)
            assertEquals(true, anyVal)
        }

    @Test
    fun testGetValueDetails() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val details = snapshot.getValueDetails("key1", false)
            assertTrue(details.value)
            assertEquals("key1", details.key)
            assertEquals("fakeId1", details.variationId)
            assertFalse(details.isDefaultValue)

            val anyDetails = snapshot.getAnyValueDetails("key1", false, null)
            assertEquals(true, anyDetails.value)
            assertEquals("key1", anyDetails.key)
            assertEquals("fakeId1", anyDetails.variationId)
            assertFalse(anyDetails.isDefaultValue)
        }

    @Test
    fun testGetValueTypeMismatch() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val value = snapshot.getValue("key1", "")
            assertEquals("", value)

            val details = snapshot.getValueDetails("key1", "")
            assertEquals("", details.value)
            assertEquals("key1", details.key)
            assertEquals("", details.variationId)
            assertTrue(details.isDefaultValue)
        }

    @Test
    fun testGetAllKeys() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val keys = snapshot.getAllKeys()
            assertEquals(linkedSetOf("key1", "key2"), keys)
        }

    @Test
    fun testGetAllKeysEmpty() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val keys = snapshot.getAllKeys()
            assertEquals(emptySet(), keys)
        }

    @Test
    fun testIllegalArguments() {
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            client.waitForReady()
            val snapshot = client.snapshot()

            val exception1 =
                assertFailsWith(IllegalArgumentException::class, block = {
                    snapshot.getValue("", false)
                })
            assertEquals("'key' cannot be empty.", exception1.message)

            val exception2 =
                assertFailsWith(IllegalArgumentException::class, block = {
                    snapshot.getValueDetails("", false)
                })
            assertEquals("'key' cannot be empty.", exception2.message)
        }
    }
}