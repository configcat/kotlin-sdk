package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DataGovernanceTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testShouldStayOnServer() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = formatBody("https://fakeUrl", 0), status = HttpStatusCode.OK)
                }
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    @Test
    fun testShouldStayOnSameUrl() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = formatBody(Constants.GLOBAL_CDN_URL, 1), status = HttpStatusCode.OK)
                }
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    @Test
    fun testShouldStayOnSameUrlEvenWithForce() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = formatBody(Constants.GLOBAL_CDN_URL, 2), status = HttpStatusCode.OK)
                }
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory.last().url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    @Test
    fun testShouldRedirectToAnotherServer() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 1), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 0), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(2, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.EU_CDN_URL))
        }

    @Test
    fun testShouldRedirectToAnotherServerWhenForced() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 2), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 0), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(2, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.EU_CDN_URL))
        }

    @Test
    fun testShouldBreakRedirectLoop() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 1), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = formatBody(Constants.GLOBAL_CDN_URL, 1), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(3, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.EU_CDN_URL))
            assertTrue(mockEngine.requestHistory[2].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    @Test
    fun testShouldBreakRedirectLoopWhenForced() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.EU_CDN_URL, 2), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = formatBody(Constants.GLOBAL_CDN_URL, 2), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(3, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.EU_CDN_URL))
            assertTrue(mockEngine.requestHistory[2].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    @Test
    fun testShouldRespectCustomUrlWhenNotForced() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.GLOBAL_CDN_URL, 1), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine, CUSTOM_CDN_URL)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(CUSTOM_CDN_URL))

            fetcher.fetch("")
            assertEquals(2, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(CUSTOM_CDN_URL))
        }

    @Test
    fun testShouldNotRespectCustomUrlWhenForced() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = formatBody(Constants.GLOBAL_CDN_URL, 2), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = formatBody(Constants.GLOBAL_CDN_URL, 0), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine, CUSTOM_CDN_URL)
            fetcher.fetch("")

            assertEquals(2, mockEngine.requestHistory.size)
            assertTrue(mockEngine.requestHistory[0].url.toString().startsWith(CUSTOM_CDN_URL))
            assertTrue(mockEngine.requestHistory[1].url.toString().startsWith(Constants.GLOBAL_CDN_URL))
        }

    companion object {
        fun formatBody(
            url: String,
            redirect: Int,
        ): String {
            return """{ "p": { "u": "$url", "r": $redirect, "s": "test-salt" }, "f": {}, "s":[] }"""
        }

        const val CUSTOM_CDN_URL = "https://custom-cdn.configcat.com"
    }
}
