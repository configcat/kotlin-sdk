package com.configcat.client

import com.configcat.client.fetch.autoPoll
import com.configcat.client.fetch.lazyLoad
import com.configcat.client.fetch.manualPoll
import com.soywiz.klock.DateTime
import com.soywiz.krypto.sha1
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigCatClientTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.close()
    }

    @Test
    fun testGetValue() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }
        val op1 = async { client.getValue("fakeKey", false) }
        val op2 = async { client.getValue("fakeKey", false) }
        val op3 = async { client.getValue("fakeKey", false) }
        val op4 = async { client.getValue("fakeKey", false) }
        val op5 = async { client.getValue("fakeKey", false) }
        val results = awaitAll(op1, op2, op3, op4, op5)

        assertTrue { results.all { it } }
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetIntValue() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(10),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(10, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetIntValueFailed() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueTypeMismatch() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody("fake"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueInvalidJson() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "{",
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetIntValueBadRequest() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetStringValue() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody("test"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals("test", client.getValue("fakeKey", ""))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetStringValueFailed() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals("", client.getValue("fakeKey", ""))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetDoubleValue() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(3.14),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(3.14, client.getValue("fakeKey", 0.0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetDoubleValueFailed() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(0.0, client.getValue("fakeKey", 0.0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetBoolValue() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(true, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetBoolValueFailed() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueInvalidType() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals("55".toFloat(), client.getValue("fakeKey", "55".toFloat()))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testRequestTimeout() = runTest {
        val mockEngine = MockEngine { _ ->
            delay(3000)
            respond(content = Utils.formatJsonBody(true), status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            requestTimeoutMs = 1000
        }

        val start = DateTime.now()
        assertEquals(false, client.getValue("fakeKey", false))
        val elapsed = DateTime.now() - start
        assertTrue(elapsed.seconds > 1)
        assertTrue(elapsed.seconds < 2)
    }

    @Test
    fun testGetFromOnlyCache() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val sdkKey = "test"
        val cacheKey: String = "kotlin_${sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Utils.formatJsonBody(true))
        val client = ConfigCatClient.get(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
        }

        assertEquals(true, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testOnlyCacheRefresh() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val sdkKey = "test"
        val cacheKey: String = "kotlin_${sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Utils.formatJsonBody(true))
        val client = ConfigCatClient.get(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
        }

        client.refresh()
        assertEquals(true, client.getValue("fakeKey", false))
        assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
    }

    @Test
    fun testGetLatestOnFail() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        client.refresh()
        assertEquals("test1", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testForceRefreshLazy() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        client.refresh()
        assertEquals("test2", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testForceRefreshAuto() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll()
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        client.refresh()
        assertEquals("test2", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollFailing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollRefreshFailing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        client.refresh()
        assertEquals(false, client.getValue("fakeKey", false))
        assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
    }

    @Test
    fun testLazyFailing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testLazyRefreshFailing() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        client.refresh()
        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollUserAgent() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll()
        }

        client.getValue("fakeKey", false)
        assertEquals(
            "ConfigCat-Kotlin/a-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
    }

    @Test
    fun testGetAllKeys() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = testMultipleBody,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        val keys = client.getAllKeys()
        assertEquals(2, keys.size)
    }

    @Test
    fun testGetAllValues() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = testMultipleBody,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
        }

        val values = client.getAllValues()
        assertEquals(2, values.size)
        assertEquals(true, values["key1"])
        assertEquals(false, values["key2"])
    }

    @Test
    fun testLazyUserAgent() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        client.getValue("fakeKey", false)
        assertEquals(
            "ConfigCat-Kotlin/l-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
    }

    @Test
    fun testManualPollUserAgent() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(
                content = Utils.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient.get("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.refresh()
        assertEquals(
            "ConfigCat-Kotlin/m-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
    }

    companion object {
        const val testMultipleBody =
            """{ "f": { "key1": { "v": true, "i": "fakeId1" }, "key2": { "v": false, "i": "fakeId2" } } }"""
    }
}
