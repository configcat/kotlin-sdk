package com.configcat

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.util.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.*

@OptIn(ExperimentalCoroutinesApi::class)
class JsConfigFetcherTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testFetchParams() = runTest {
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
            mockEngine.requestHistory.last().url.parameters["sdk"]
        )
        assertEquals(null, mockEngine.requestHistory.last().url.parameters["ccetag"])

        fetcher.fetch(eTag)

        assertEquals(2, mockEngine.requestHistory.size)
        assertEquals(
            "ConfigCat-Kotlin/a-${Constants.version}",
            mockEngine.requestHistory.last().url.parameters["sdk"]
        )
        assertEquals(eTag, mockEngine.requestHistory.last().url.parameters["ccetag"])
    }

    companion object {
        const val testBody =
            """{ "p": { "u": "https://cdn-global.configcat.com", "s": "test-slat" }, "f": { "fakeKey": { "t": 1, "v": {"s": "fakeValue" }, "p": [], "r": [], "a":""} }, "s": [] }"""
    }
}
