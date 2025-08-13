package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

class JsConfigFetcherTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testFetchParams() =
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
            fetcher.fetch("")

            assertEquals(1, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().url.parameters["sdk"],
            )
            assertEquals(null, mockEngine.requestHistory.last().url.parameters["ccetag"])

            fetcher.fetch(eTag)

            assertEquals(2, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().url.parameters["sdk"],
            )
            assertEquals(eTag, mockEngine.requestHistory.last().url.parameters["ccetag"])
        }

    @Test
    fun testFetchParamsWithHTTP2Headers() =
        runTest {
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
                mockEngine.requestHistory.last().url.parameters["sdk"],
            )
            assertEquals(null, mockEngine.requestHistory.last().url.parameters["ccetag"])

            fetcher.fetch(eTag)

            assertEquals(2, mockEngine.requestHistory.size)
            assertEquals(
                "ConfigCat-Kotlin/a-${Constants.VERSION}",
                mockEngine.requestHistory.last().url.parameters["sdk"],
            )
            assertEquals(eTag, mockEngine.requestHistory.last().url.parameters["ccetag"])
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
}
            """
    }
}
