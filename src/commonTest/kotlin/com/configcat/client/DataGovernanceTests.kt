package com.configcat.client

import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class DataGovernanceTests {
    @Test
    fun testShouldStayOnServer() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(content = formatBody("https://fakeUrl", 0), status = HttpStatusCode.OK)
        }
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(1, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldStayOnSameUrl() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(content = formatBody(Constants.globalCdnUrl, 1), status = HttpStatusCode.OK)
        }
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(1, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldStayOnSameUrlEvenWithForce() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(content = formatBody(Constants.globalCdnUrl, 2), status = HttpStatusCode.OK)
        }
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(1, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldRedirectToAnotherServer() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 1), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 0), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(2, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.globalCdnUrl))
        assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.euCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldRedirectToAnotherServerWhenForced() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 2), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 0), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(2, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.globalCdnUrl))
        assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.euCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldBreakRedirectLoop() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 1), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = formatBody(Constants.globalCdnUrl, 1), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(3, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.globalCdnUrl))
        assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.euCdnUrl))
        assertTrue(mockEngine.requestHistory[2].url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldBreakRedirectLoopWhenForced() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.euCdnUrl, 2), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = formatBody(Constants.globalCdnUrl, 2), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine)
        fetcher.fetch("")

        assertEquals(3, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.globalCdnUrl))
        assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.euCdnUrl))
        assertTrue(mockEngine.requestHistory[2].url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldRespectCustomUrlWhenNotForced() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.globalCdnUrl, 1), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine, customCdnUrl)
        fetcher.fetch("")

        assertEquals(1, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(customCdnUrl))

        fetcher.close()
    }

    @Test
    fun testShouldNotRespectCustomUrlWhenForced() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = formatBody(Constants.globalCdnUrl, 2), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = formatBody(Constants.globalCdnUrl, 0), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val fetcher = Utils.createFetcher(mockEngine, customCdnUrl)
        fetcher.fetch("")

        assertEquals(2, mockEngine.requestHistory.size)
        assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(customCdnUrl))
        assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.globalCdnUrl))

        fetcher.close()
    }

    companion object {
        fun formatBody(url: String, redirect: Int): String {
            return """{ "p": { "u": "$url", "r": $redirect }, "f": {} }"""
        }

        const val customCdnUrl = "https://custom-cdn.configcat.com"
    }
}
