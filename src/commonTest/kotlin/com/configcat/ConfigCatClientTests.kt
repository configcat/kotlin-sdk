package com.configcat

import com.configcat.override.OverrideBehavior
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
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigCatClientTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testGetValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
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
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(10),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(10, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetIntValueFailed() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueTypeMismatch() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody("fake"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueInvalidJson() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "{",
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetIntValueBadRequest() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetStringValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody("test"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals("test", client.getValue("fakeKey", ""))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetStringValueFailed() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals("", client.getValue("fakeKey", ""))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetDoubleValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(3.14),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(3.14, client.getValue("fakeKey", 0.0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetDoubleValueFailed() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(0.0, client.getValue("fakeKey", 0.0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetBoolValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(true, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetBoolValueFailed() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadGateway
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueInvalidType() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals("55".toFloat(), client.getValue("fakeKey", "55".toFloat()))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testRequestTimeout() = runTest {
        val mockEngine = MockEngine {
            delay(3000)
            respond(content = Data.formatJsonBody(true), status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            requestTimeout = 1.seconds
        }

        val start = DateTime.now()
        assertEquals(false, client.getValue("fakeKey", false))
        val elapsed = DateTime.now() - start
        assertTrue(elapsed.seconds > 1)
        assertTrue(elapsed.seconds < 2)
    }

    @Test
    fun testGetFromOnlyCache() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val sdkKey = "test"
        val cacheKey: String = "kotlin_${sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Data.formatCacheEntry(true))
        val client = ConfigCatClient(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
            pollingMode = autoPoll {
                pollingInterval = 100.milliseconds
            }
        }

        assertEquals(true, client.getValue("fakeKey", false))

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size == 1
        }
    }

    @Test
    fun testOnlyCacheRefresh() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val sdkKey = "test"
        val cacheKey: String = "kotlin_${sdkKey}_${Constants.configFileName}".encodeToByteArray().sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Data.formatCacheEntry(true))
        val client = ConfigCatClient(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found", result.error)
        assertEquals(true, client.getValue("fakeKey", false))
        assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
    }

    @Test
    fun testGetLatestOnFail() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.Forbidden)
            }
        } as MockEngine
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 403 Forbidden", result.error)
        assertEquals("test1", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testForceRefreshLazy() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        val result = client.forceRefresh()
        assertTrue(result.isSuccess)
        assertEquals("test2", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testForceRefreshAuto() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll()
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        val result = client.forceRefresh()
        assertTrue(result.isSuccess)
        assertEquals("test2", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollFailing() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollRefreshFailing() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found", result.error)
        assertEquals(false, client.getValue("fakeKey", false))
        assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
    }

    @Test
    fun testLazyFailing() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testLazyRefreshFailing() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.NotFound
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found", result.error)
        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetAllKeys() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = testMultipleBody,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        val keys = client.getAllKeys()
        assertEquals(2, keys.size)
    }

    @Test
    fun testGetAllValues() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = testMultipleBody,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        val values = client.getAllValues()
        assertEquals(2, values.size)
        assertEquals(true, values["key1"])
        assertEquals(false, values["key2"])
    }

    @Test
    fun testGetAllValueDetails() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = testMultipleBody,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        val details = client.getAllValueDetails()
        assertEquals(2, details.size)
        assertTrue { details.elementAt(0).value as? Boolean ?: false }
        assertFalse(details.elementAt(1).value as? Boolean ?: true)
    }

    @Test
    fun testAutoPollUserAgent() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
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
    fun testLazyUserAgent() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
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
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.forceRefresh()
        assertEquals(
            "ConfigCat-Kotlin/m-${Constants.version}",
            mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"]
        )
    }

    @Test
    fun testGetValueDetailsWithError() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "",
                status = HttpStatusCode.BadRequest
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
        }

        val details = client.getValueDetails("fakeKey", "")

        assertEquals("", details.value)
        assertTrue(details.isDefaultValue)
        assertEquals("Config JSON is not present when evaluating setting 'fakeKey'. Returning the `defaultValue` parameter that you specified in your application: ''.", details.error)
    }

    @Test
    fun testOnlineOffline() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll { pollingInterval = 2.seconds }
        }

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size == 1
        }

        client.setOffline()
        assertTrue(client.isOffline)

        val result = client.forceRefresh()

        assertFalse(result.isSuccess)
        assertEquals("Client is in offline mode, it cannot initiate HTTP calls.", result.error)
        assertEquals(1, mockEngine.requestHistory.size)

        client.setOnline()
        assertFalse(client.isOffline)

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size > 1
        }
    }

    @Test
    fun testInitOffline() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll { pollingInterval = 2.seconds }
            offline = true
        }

        assertTrue(client.isOffline)

        client.forceRefresh()

        assertEquals(0, mockEngine.requestHistory.size)

        client.setOnline()
        assertFalse(client.isOffline)

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size > 1
        }
    }

    @Test
    fun testInitOfflineCallsReady() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBody(true),
                status = HttpStatusCode.OK
            )
        }
        var ready = false
        ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = autoPoll { pollingInterval = 2.seconds }
            offline = true
            hooks.addOnClientReady { ready = true }
        }

        assertEquals(0, mockEngine.requestHistory.size)
        TestUtils.awaitUntil {
            ready
        }
    }

    @Test
    fun testDefaultUser() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatConfigWithRules(),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }
        client.forceRefresh()

        val user1 = ConfigCatUser("test@test1.com")
        val user2 = ConfigCatUser("test@test2.com")

        client.setDefaultUser(user1)

        var value = client.getValue("key", "")
        assertEquals("fake1", value)

        value = client.getValue("key", "", user2)
        assertEquals("fake2", value)

        client.clearDefaultUser()

        value = client.getValue("key", "")
        assertEquals("default", value)
    }

    @Test
    fun testHooks() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotFound)
            }
        }
        var error = ""
        var changed = false
        var ready = false

        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnConfigChanged { changed = true }
            hooks.addOnClientReady { ready = true }
            hooks.addOnError { err -> error = err }
        }

        client.forceRefresh()
        client.forceRefresh()

        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found", error)
        assertTrue(changed)
        assertTrue(ready)
    }

    @Test
    fun testHooksSub() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotFound)
            }
        }
        var error = ""
        var changed = false

        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.hooks.addOnConfigChanged { changed = true }
        client.hooks.addOnError { err -> error = err }

        client.forceRefresh()
        client.forceRefresh()

        assertEquals("Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found", error)
        assertTrue(changed)
    }

    @Test
    fun testFail400() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadRequest)
            }
        }

        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.forceRefresh()
        val error = client.forceRefresh()

        assertEquals("Unexpected HTTP response was received while trying to fetch config JSON: 400 ", error.error)
    }

    @Test
    fun testOnFlagEvalError() = runTest {
        val mockEngine = MockEngine {
            respond(content = "", status = HttpStatusCode.BadRequest)
        }
        var called = false

        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnFlagEvaluated { details ->
                called = true
                assertTrue(details.isDefaultValue)
                assertEquals("", details.value)
                assertEquals("ID", details.user?.identifier)
                assertEquals("Config JSON is not present when evaluating setting 'fakeKey'. Returning the `defaultValue` parameter that you specified in your application: ''.", details.error)
            }
        }

        client.forceRefresh()
        client.getValue("fakeKey", "", ConfigCatUser("ID"))

        assertTrue(called)
    }

    @Test
    fun testEvalDetails() = runTest {
        val mockEngine = MockEngine {
            respond(content = Data.formatConfigWithRules(), status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.forceRefresh()
        val details = client.getValueDetails("key", "", ConfigCatUser("test@test1.com"))

        assertFalse(details.isDefaultValue)
        assertEquals("fake1", details.value)
        assertEquals("key", details.key)
        assertEquals("fakeId1", details.variationId)
        assertNull(details.error)
        assertEquals("Identifier", details.matchedEvaluationRule?.comparisonAttribute)
        assertEquals(2, details.matchedEvaluationRule?.comparator)
        assertEquals("@test1.com", details.matchedEvaluationRule?.comparisonValue)
        assertNull(details.matchedEvaluationPercentageRule)
        assertEquals("test@test1.com", details.user?.identifier)
    }

    @Test
    fun testEvalDetailsHook() = runTest {
        val mockEngine = MockEngine {
            respond(content = Data.formatConfigWithRules(), status = HttpStatusCode.OK)
        }
        var called = false
        val client = ConfigCatClient("test") {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnFlagEvaluated { details ->
                assertFalse(details.isDefaultValue)
                assertEquals("fake1", details.value)
                assertEquals("key", details.key)
                assertEquals("fakeId1", details.variationId)
                assertNull(details.error)
                assertEquals("Identifier", details.matchedEvaluationRule?.comparisonAttribute)
                assertEquals(2, details.matchedEvaluationRule?.comparator)
                assertEquals("@test1.com", details.matchedEvaluationRule?.comparisonValue)
                assertNull(details.matchedEvaluationPercentageRule)
                assertEquals("test@test1.com", details.user?.identifier)
                called = true
            }
        }

        client.forceRefresh()
        client.getValue("key", "", ConfigCatUser("test@test1.com"))
        assertTrue(called)
    }

    @Test
    fun testSingleton() {
        var client1 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        val client2 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client1, client2)

        ConfigCatClient.closeAll()

        client1 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)
    }

    @Test
    fun testRemoveTheClosingInstanceOnly() {
        var client1 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        client1.close()

        val client2 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)

        client1.close()

        val client3 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client2, client3)
    }

    @Test
    fun testClose() {
        val client1 = ConfigCatClient("test") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        assertFalse(client1.isClosed())
        client1.close()
        assertTrue(client1.isClosed())
    }

    companion object {
        const val testMultipleBody =
            """{ "f": { "key1": { "v": true, "i": "fakeId1" }, "key2": { "v": false, "i": "fakeId2" } } }"""
    }
}
