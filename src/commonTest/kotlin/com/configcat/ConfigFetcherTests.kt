package com.configcat

import io.ktor.client.engine.mock.*
import io.ktor.http.*
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
        assertEquals("fakeValue", result.entry.config.settings["fakeKey"]?.value)
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
        assertEquals(eTag, mockEngine.requestHistory.last().headers["If-None-Match"])
    }

    companion object {
        const val testBody = """{ "f": { "fakeKey": { "v": "fakeValue", "p": [], "r": [] } } }"""
    }
}
