package com.configcat

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigFetcherTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testFetchSuccess() = runTest {
        val mockEngine = MockEngine {
            respond(content = testBody, status = HttpStatusCode.OK)
        }
        val fetcher = Services.createFetcher(mockEngine)
        val result = fetcher.fetch("")

        assertTrue(result.isFetched)
        assertEquals("fakeValue", result.entry.config.settings?.get("fakeKey")?.settingValue?.stringValue)
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testFetchNotModified() = runTest {
        val mockEngine = MockEngine {
            respond(content = "", status = HttpStatusCode.NotModified)
        }
        val fetcher = Services.createFetcher(mockEngine)
        val result = fetcher.fetch("")

        assertTrue(result.isNotModified)
        assertTrue(result.entry.isEmpty())
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testFetchFailed() = runTest {
        val mockEngine = MockEngine {
            respond(content = "", status = HttpStatusCode.BadGateway)
        }
        val fetcher = Services.createFetcher(mockEngine)
        val result = fetcher.fetch("")

        assertTrue(result.isFailed)
        assertTrue(result.entry.isEmpty())
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testFetchNotModifiedETag() = runTest {
        val eTag = "test"
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = testBody, status = HttpStatusCode.OK, headersOf(Pair("ETag", listOf(eTag))))
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotModified)
            }
        } as MockEngine
        val fetcher = Services.createFetcher(mockEngine)
        val result = fetcher.fetch("")

        assertTrue(result.isFetched)
        assertFalse(result.entry.isEmpty())
        assertEquals(eTag, result.entry.eTag)
        assertEquals(1, mockEngine.requestHistory.size)

        val resultNotModified = fetcher.fetch(eTag)

        assertTrue(resultNotModified.isNotModified)
        assertTrue(resultNotModified.entry.isEmpty())
        assertEquals(2, mockEngine.requestHistory.size)
        // For Js we run a separate test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            assertEquals(eTag, mockEngine.requestHistory.last().url.parameters["ccetag"])
        } else {
            assertEquals(eTag, mockEngine.requestHistory.last().headers["If-None-Match"])
        }
    }

    @Test
    fun testFetchParams() = runTest {
        // For Js we run a separate test
        if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
            return@runTest
        }
        val eTag = "test"
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = testBody, status = HttpStatusCode.OK, headersOf(Pair("ETag", listOf(eTag))))
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotModified)
            }
        } as MockEngine
        val fetcher = Services.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(1, mockEngine.requestHistory.size)
        assertEquals(
            "ConfigCat-Kotlin/a-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
        assertEquals(null, mockEngine.requestHistory.last().headers["If-None-Match"])

        fetcher.fetch(eTag)

        assertEquals(2, mockEngine.requestHistory.size)
        assertEquals(
            "ConfigCat-Kotlin/a-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
        assertEquals(eTag, mockEngine.requestHistory.last().headers["If-None-Match"])
    }

    companion object {
        const val testBody =
            """{ "p": { "u": "https://cdn-global.configcat.com", "s": "test-slat" }, "f": { "fakeKey": { "t": 1, "v": {"s": "fakeValue" }, "p": [], "r": [], "a":""} }, "s": [] }"""
    }
}
