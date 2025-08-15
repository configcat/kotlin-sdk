package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

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

            val snapshot = client.snapshot()
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, snapshot.cacheState)

            client.close()
        }

    @Test
    fun testClientState() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                    }
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = autoPoll { pollingInterval = 1.seconds }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.NO_FLAG_DATA, state)
            assertEquals(ClientCacheState.NO_FLAG_DATA, client.snapshot().cacheState)

            TestUtils.awaitUntil {
                client.snapshot().cacheState == ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA
            }

            client.close()
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

            client.close()
        }

    @Test
    fun testHookSnapshot() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    hooks.addOnConfigChanged { settings, snapshot ->
                        assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, snapshot.cacheState)
                        val value = snapshot.getValue("key1", false)
                        assertTrue(value)

                        val anyVal = snapshot.getAnyValue("key1", false, null)
                        assertEquals(true, anyVal)
                        called = true
                    }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, state)

            TestUtils.awaitUntil { called }

            client.close()
        }

    @Test
    fun testReadyHookSnapshot() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.MULTIPLE_BODY, status = HttpStatusCode.OK)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    hooks.addOnClientReadyWithSnapshot { snapshot ->
                        assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, snapshot.cacheState)
                        val value = snapshot.getValue("key1", false)
                        assertTrue(value)

                        val anyVal = snapshot.getAnyValue("key1", false, null)
                        assertEquals(true, anyVal)
                        called = true
                    }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, state)

            TestUtils.awaitUntil { called }

            client.close()
        }

    @Test
    fun testHookSnapshotCache() =
        runTest {
            val cache = SingleValueCache(Data.formatCacheEntry("test"))

            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.InternalServerError)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = cache
                    hooks.addOnConfigChanged { settings, snapshot ->
                        assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, snapshot.cacheState)
                        val value = snapshot.getValue("fakeKey", "")
                        assertEquals("test", value)
                        called = true
                    }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, state)

            TestUtils.awaitUntil { called }

            client.close()
        }

    @Test
    fun testReadyHookSnapshotCache() =
        runTest {
            val cache = SingleValueCache(Data.formatCacheEntry("test"))

            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.InternalServerError)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = cache
                    hooks.addOnClientReadyWithSnapshot { snapshot ->
                        assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, snapshot.cacheState)
                        val value = snapshot.getValue("fakeKey", "")
                        assertEquals("test", value)
                        called = true
                    }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, state)

            TestUtils.awaitUntil { called }

            client.close()
        }

    @Test
    fun testHookSnapshotCacheExpired() =
        runTest {
            val cache = SingleValueCache(Data.formatCacheEntryWithDate("test", Instant.DISTANT_PAST))

            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.InternalServerError)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = cache
                    hooks.addOnConfigChanged { settings, snapshot ->
                        assertEquals(ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY, snapshot.cacheState)
                        val value = snapshot.getValue("fakeKey", "")
                        assertEquals("test", value)
                        called = true
                    }
                }

            val state = client.waitForReady()
            assertEquals(ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY, state)

            TestUtils.awaitUntil { called }

            client.close()
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

            client.close()
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

            client.close()
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

            client.close()
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

            client.close()
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

            client.close()
        }
    }
}