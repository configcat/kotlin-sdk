package com.configcat

import com.configcat.fetch.RefreshErrorCode
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.util.PlatformUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.seconds

class ConfigFetcherTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testFetchSuccess() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = TEST_BODY, status = HttpStatusCode.OK)
                }
            val fetcher = Services.createFetcher(mockEngine)
            val result = fetcher.fetch("")

            assertTrue(result.isFetched)
            assertEquals("fakeValue", result.entry.config.settings?.get("fakeKey")?.settingValue?.stringValue)
            assertEquals(1, mockEngine.requestHistory.size)
            assertEquals(RefreshErrorCode.NONE, result.errorCode)
            assertNull(result.exception)
        }

    @Test
    fun testFetchNotModified() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.NotModified)
                }
            val fetcher = Services.createFetcher(mockEngine)
            val result = fetcher.fetch("")

            assertTrue(result.isNotModified)
            assertTrue(result.entry.isEmpty())
            assertEquals(RefreshErrorCode.NONE, result.errorCode)
            assertNull(result.exception)
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testFetchFailed() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.BadGateway)
                }
            val fetcher = Services.createFetcher(mockEngine)
            val result = fetcher.fetch("")

            assertTrue(result.isFailed)
            assertTrue(result.entry.isEmpty())
            assertEquals(RefreshErrorCode.UNEXPECTED_HTTP_RESPONSE, result.errorCode)
            assertNull(result.exception)
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testFetchBadJson() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "{", status = HttpStatusCode.OK)
                }
            val fetcher = Services.createFetcher(mockEngine)
            val result = fetcher.fetch("")

            assertTrue(result.isFailed)
            assertTrue(result.entry.isEmpty())
            assertEquals(RefreshErrorCode.INVALID_HTTP_RESPONSE_CONTENT, result.errorCode)
            assertNotNull(result.exception)
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testFetchNotFound() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.NotFound)
                }
            val fetcher = Services.createFetcher(mockEngine)
            val result = fetcher.fetch("")

            assertTrue(result.isFailed)
            assertTrue(result.entry.isEmpty())
            assertEquals(RefreshErrorCode.INVALID_SDK_KEY, result.errorCode)
            assertNull(result.exception)
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testFetchTimeout() =
        runTest {
            val mockEngine =
                MockEngine {
                    delay(3000)
                    respond(content = TEST_BODY, status = HttpStatusCode.OK)
                }
            val opts = ConfigCatOptions()
            opts.requestTimeout = 1.seconds
            val fetcher = Services.createFetcher(mockEngine, options = opts)
            val result = fetcher.fetch("")

            assertTrue(result.isFailed)
            assertTrue(result.entry.isEmpty())
            assertEquals(RefreshErrorCode.HTTP_REQUEST_TIMEOUT, result.errorCode)
            assertNotNull(result.exception)
        }

    @Test
    fun testFetchNotModifiedETag() =
        runTest {
            val eTag = "test"
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = TEST_BODY, status = HttpStatusCode.OK, headersOf(Pair("ETag", listOf(eTag))))
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
    fun testFetchParams() =
        runTest {
            // For Js we run a separate test
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                return@runTest
            }
            val eTag = "test"
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = TEST_BODY, status = HttpStatusCode.OK, headersOf(Pair("ETag", listOf(eTag))))
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.NotModified)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
            )
            assertEquals(null, mockEngine.requestHistory.last().headers["If-None-Match"])

            fetcher.fetch(eTag)

            assertEquals(2, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
            )
            assertEquals(eTag, mockEngine.requestHistory.last().headers["If-None-Match"])
        }

    @Test
    fun testFetchParamsWithHTTP2Headers() =
        runTest {
            // For Js we run a separate test
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                return@runTest
            }
            val eTag = "test"
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = TEST_BODY, status = HttpStatusCode.OK, headersOf(Pair("etag", listOf(eTag))))
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.NotModified)
                    }
                } as MockEngine
            val fetcher = Services.createFetcher(mockEngine)
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
            )
            assertEquals(null, mockEngine.requestHistory.last().headers["If-None-Match"])

            fetcher.fetch(eTag)

            assertEquals(2, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
            )
            assertEquals(eTag, mockEngine.requestHistory.last().headers["If-None-Match"])
        }

    companion object {
        const val TEST_BODY =
            """{
   "p":{
      "u":"https://cdn-global.configcat.com",
      "s":"test-slat"
   },
   "f":{
      "fakeKey":{
         "t":1,
         "v":{
            "s":"fakeValue"
         },
         "p":[
            
         ],
         "r":[
            
         ],
         "a":""
      }
   },
   "s":[
      
   ]
}"""
    }
}
