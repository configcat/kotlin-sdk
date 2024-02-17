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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithInt(10),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueTypeMismatch() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithString("fake"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            message = "The type of a setting must match the type of the setting's default value. Setting's type was {class java.lang.String} but the default value's type was {class java.lang.Integer}. Please use a default value which corresponds to the setting type {class java.lang.String}.Learn more: https://configcat.com/docs/sdk-reference/dotnet/#setting-type-mapping",
            block = { client.getValue("fakeKey", 0) }
        )
    }

    @Test
    fun testGetValueInvalidJson() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = "{",
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals(0, client.getValue("fakeKey", 0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetStringValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithString("test"),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals("", client.getValue("fakeKey", ""))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetDoubleValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithDouble(3.14),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals(0.0, client.getValue("fakeKey", 0.0))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetBoolValue() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(1, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetValueInvalidType() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertFailsWith(
            exceptionClass = IllegalArgumentException::class,
            message = "The type of a setting must match the type of the setting's default value. Setting's type was {class java.lang.Boolean} but the default value's type was {class java.lang.Float}. Please use a default value which corresponds to the setting type {class java.lang.Boolean}.Learn more: https://configcat.com/docs/sdk-reference/dotnet/#setting-type-mapping",
            block = { client.getValue("fakeKey", "55".toFloat()) }
        )
    }

    @Test
    fun testRequestTimeout() = runTest {
        val mockEngine = MockEngine {
            delay(3000)
            respond(content = Data.formatJsonBodyWithBoolean(true), status = HttpStatusCode.OK)
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val sdkKey = Data.SDK_KEY
        val cacheKey: String =
            "${sdkKey}_${Constants.configFileName}_${Constants.serializationFormatVersion}".encodeToByteArray()
                .sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Data.formatCacheEntry("test"))
        val client = ConfigCatClient(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
            pollingMode = autoPoll {
                pollingInterval = 100.milliseconds
            }
        }

        assertEquals("test", client.getValue("fakeKey", ""))

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
        val sdkKey = Data.SDK_KEY
        val cacheKey: String =
            "${sdkKey}_${Constants.configFileName}_${Constants.serializationFormatVersion}".encodeToByteArray()
                .sha1().hex
        val cache = InMemoryCache()
        cache.write(cacheKey, Data.formatCacheEntry("test"))
        val client = ConfigCatClient(sdkKey) {
            httpEngine = mockEngine
            configCache = cache
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found",
            result.error
        )
        assertEquals("test", client.getValue("fakeKey", ""))
        assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
    }

    @Test
    fun testGetLatestOnFail() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.Forbidden)
            }
        } as MockEngine
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        assertEquals("test1", client.getValue("fakeKey", ""))
        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 403 Forbidden",
            result.error
        )
        assertEquals("test1", client.getValue("fakeKey", ""))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testForceRefreshLazy() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found",
            result.error
        )
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
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = lazyLoad()
        }

        val result = client.forceRefresh()
        assertFalse(result.isSuccess)
        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found",
            result.error
        )
        assertEquals(false, client.getValue("fakeKey", false))
        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testGetAllKeys() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.MULTIPLE_BODY,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val keys = client.getAllKeys()
        assertEquals(2, keys.size)
    }

    @Test
    fun testGetAllValues() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.MULTIPLE_BODY,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.MULTIPLE_BODY,
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
        }

        val details = client.getValueDetails("fakeKey", "")

        assertEquals("", details.value)
        assertTrue(details.isDefaultValue)
        assertEquals(
            "Config JSON is not present when evaluating setting 'fakeKey'. Returning the `defaultValue` parameter that you specified in your application: ''.",
            details.error
        )
    }

    @Test
    fun testOnlineOffline() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
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
                content = Data.formatJsonBodyWithBoolean(true),
                status = HttpStatusCode.OK
            )
        }
        var ready = false
        ConfigCatClient(Data.SDK_KEY) {
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
        val client = ConfigCatClient(Data.SDK_KEY) {
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
    fun testDefaultUserVariationId() = runTest {
        val mockEngine = MockEngine {
            respond(
                content = Data.formatConfigWithRules(),
                status = HttpStatusCode.OK
            )
        }
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }
        client.forceRefresh()

        val user1 = ConfigCatUser("test@test1.com")
        val user2 = ConfigCatUser("test@test2.com")

        client.setDefaultUser(user1)

        assertEquals("fakeId1", client.getValueDetails("key", "").variationId)
        assertEquals("fakeId2", client.getValueDetails("key", "", user2).variationId)

        client.clearDefaultUser()

        assertEquals("defaultId", client.getValueDetails("key", "").variationId)
    }

    @Test
    fun testHooks() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotFound)
            }
        }
        var error = ""
        var changed = false
        var ready = false

        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnConfigChanged { changed = true }
            hooks.addOnClientReady { ready = true }
            hooks.addOnError { err -> error = err }
        }

        client.forceRefresh()
        client.forceRefresh()

        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found",
            error
        )
        assertTrue(changed)
        assertTrue(ready)
    }

    @Test
    fun testHooksSub() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.NotFound)
            }
        }
        var error = ""
        var changed = false

        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = manualPoll()
        }

        client.hooks.addOnConfigChanged { changed = true }
        client.hooks.addOnError { err -> error = err }

        client.forceRefresh()
        client.forceRefresh()

        assertEquals(
            "Your SDK Key seems to be wrong. You can find the valid SDK Key at https://app.configcat.com/sdkkey. Received response: 404 Not Found",
            error
        )
        assertTrue(changed)
    }

    @Test
    fun testFail400() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadRequest)
            }
        }

        val client = ConfigCatClient(Data.SDK_KEY) {
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

        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnFlagEvaluated { details ->
                called = true
                assertTrue(details.isDefaultValue)
                assertEquals("", details.value)
                assertEquals("ID", details.user?.identifier)
                assertEquals(
                    "Config JSON is not present when evaluating setting 'fakeKey'. Returning the `defaultValue` parameter that you specified in your application: ''.",
                    details.error
                )
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
        val client = ConfigCatClient(Data.SDK_KEY) {
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
        assertEquals("test@test1.com", details.user?.identifier)
        assertEquals(1, details.matchedTargetingRule?.conditions?.size)
        val condition = details.matchedTargetingRule?.conditions?.get(0)

        assertEquals("Identifier", condition?.userCondition?.comparisonAttribute)
        assertEquals(2, condition?.userCondition?.comparator)
        assertEquals("@test1.com", condition?.userCondition?.stringArrayValue?.get(0))
        assertNull(details.matchedPercentageOption)
    }

    @Test
    fun testEvalDetailsHook() = runTest {
        val mockEngine = MockEngine {
            respond(content = Data.formatConfigWithRules(), status = HttpStatusCode.OK)
        }
        var called = false
        val client = ConfigCatClient(Data.SDK_KEY) {
            httpEngine = mockEngine
            pollingMode = manualPoll()
            hooks.addOnFlagEvaluated { details ->
                assertFalse(details.isDefaultValue)
                assertEquals("fake1", details.value)
                assertEquals("key", details.key)
                assertEquals("fakeId1", details.variationId)
                assertNull(details.error)
                assertEquals("test@test1.com", details.user?.identifier)
                assertEquals(1, details.matchedTargetingRule?.conditions?.size)
                val condition = details.matchedTargetingRule?.conditions?.get(0)

                assertEquals("Identifier", condition?.userCondition?.comparisonAttribute)
                assertEquals(2, condition?.userCondition?.comparator)
                assertEquals("@test1.com", condition?.userCondition?.stringArrayValue?.get(0))
                assertNull(details.matchedPercentageOption)

                called = true
            }
        }

        client.forceRefresh()
        client.getValue("key", "", ConfigCatUser("test@test1.com"))
        assertTrue(called)
    }

    @Test
    fun testSingleton() {
        var client1 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        val client2 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client1, client2)

        ConfigCatClient.closeAll()

        client1 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)
    }

    @Test
    fun testRemoveTheClosingInstanceOnly() {
        val client1 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        client1.close()

        val client2 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)

        client1.close()

        val client3 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client2, client3)
    }

    @Test
    fun testClose() {
        val client1 = ConfigCatClient(Data.SDK_KEY) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        assertFalse(client1.isClosed())
        client1.close()
        assertTrue(client1.isClosed())
    }

    @Test
    fun testSDKKeyIsNotEmpty() {
        assertFailsWith(IllegalArgumentException::class, "SDK Key cannot be empty.", block = {
            ConfigCatClient("")
        })
    }

    @Test
    fun testSDKKeyIsValid() {
        // TEST VALID KEYS
        var client = ConfigCatClient("sdk-key-90123456789012/1234567890123456789012")
        assertNotNull(client)
        client = ConfigCatClient("configcat-sdk-1/sdk-key-90123456789012/1234567890123456789012")
        assertNotNull(client)
        client = ConfigCatClient("configcat-proxy/sdk-key-90123456789012") {
            baseUrl = "https://my-configcat-proxy"
        }
        assertNotNull(client)

        ConfigCatClient.closeAll()

        // TEST INVALID KEYS
        val wrongSDKKeys: List<String> = listOf(
            "sdk-key-90123456789012",
            "sdk-key-9012345678901/1234567890123456789012",
            "sdk-key-90123456789012/123456789012345678901",
            "sdk-key-90123456789012/12345678901234567890123",
            "sdk-key-901234567890123/1234567890123456789012",
            "configcat-sdk-1/sdk-key-90123456789012",
            "configcat-sdk-1/sdk-key-9012345678901/1234567890123456789012",
            "configcat-sdk-1/sdk-key-90123456789012/123456789012345678901",
            "configcat-sdk-1/sdk-key-90123456789012/12345678901234567890123",
            "configcat-sdk-1/sdk-key-901234567890123/1234567890123456789012",
            "configcat-sdk-2/sdk-key-90123456789012/1234567890123456789012",
            "configcat-proxy/",
            "configcat-proxy/sdk-key-90123456789012"
        )
        wrongSDKKeys.forEach {
            assertFailsWith(IllegalArgumentException::class, "SDK Key '$it' is invalid.", block = {
                ConfigCatClient(it)
            })
        }

        assertFailsWith(IllegalArgumentException::class, "SDK Key 'configcat-proxy/' is invalid.", block = {
            ConfigCatClient("configcat-proxy/") { baseUrl = "https://my-configcat-proxy" }
        })

        //TEST OverrideBehaviour.localOnly skip sdkKey validation
        client = ConfigCatClient("sdk-key-90123456789012"){flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotNull(client)

        ConfigCatClient.closeAll()
    }
}
