package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SnapshotTests {
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
}