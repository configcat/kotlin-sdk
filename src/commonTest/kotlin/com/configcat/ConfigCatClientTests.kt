package com.configcat

import com.configcat.evaluation.EvaluationTestLogger
import com.configcat.evaluation.LogEvent
import com.configcat.fetch.RefreshErrorCode
import com.configcat.log.LogLevel
import com.configcat.override.OverrideBehavior
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.util.PlatformUtils
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

class ConfigCatClientTests {
    @AfterTest
    fun tearDown() {
        ConfigCatClient.closeAll()
    }

    @Test
    fun testGetValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }
            val defs = mutableListOf<Deferred<Boolean>>()
            val iter = 1000

            backgroundScope.launch {
                repeat(iter) {
                    defs.add(async { client.getValue("fakeKey", false) })
                }
            }

            TestUtils.awaitUntil(5.seconds) {
                defs.size == iter
            }

            val results = awaitAll(*defs.toTypedArray())

            assertTrue { results.all { it } }
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetValueMissing() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val details = client.getValueDetails("non-existing", 0)
            assertEquals(0, details.value)
            assertEquals(EvaluationErrorCode.SETTING_KEY_MISSING, details.errorCode)
            assertTrue(details.isDefaultValue)
        }

    @Test
    fun testGetIntValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithInt(10),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(10, client.getValue("fakeKey", 0))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetIntValueFailed() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadGateway,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(0, client.getValue("fakeKey", 0))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetValueTypeMismatch() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithString("fake"),
                        status = HttpStatusCode.OK,
                    )
                }

            val evaluationTestLogger = EvaluationTestLogger()

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    logLevel = LogLevel.ERROR
                    logger = evaluationTestLogger
                    configCache = SingleValueCache("")
                }

            val result = client.getValueDetails("fakeKey", 0)
            assertEquals(0, result.value)

            val errorLogs = mutableListOf<LogEvent>()

            val logsList = evaluationTestLogger.getLogList()
            for (i in logsList.indices) {
                val log = logsList[i]
                if (log.logLevel == LogLevel.ERROR) {
                    errorLogs.add(log)
                }
            }
            assertEquals(1, errorLogs.size, "Error size not matching")
            val errorMessage: String = errorLogs[0].logMessage
            assertContains(errorMessage, "[1002]")
            assertContains(
                errorMessage,
                "Error occurred in the `getValueDetails` method while evaluating setting 'fakeKey'. " +
                    "Returning the `defaultValue` parameter that you specified in your application: '0'.",
            )
            // we don't check the full exception message because the Integer class can be different in other platforms. We only check the first part of the message
            assertContains(
                errorMessage,
                "The type of a setting must match the type of the specified default value. Setting's " +
                    "type was {STRING} but the default value's type was ",
            )
            assertEquals(EvaluationErrorCode.SETTING_VALUE_TYPE_MISMATCH, result.errorCode)
            evaluationTestLogger.resetLogList()
        }

    @Test
    fun testGetValueInvalidJson() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "{",
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val details = client.getValueDetails("fakeKey", 0)
            assertEquals(0, details.value)
            assertEquals(EvaluationErrorCode.CONFIG_JSON_NOT_AVAILABLE, details.errorCode)
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetIntValueBadRequest() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val details = client.getValueDetails("fakeKey", 0)
            assertEquals(0, details.value)
            assertEquals(EvaluationErrorCode.CONFIG_JSON_NOT_AVAILABLE, details.errorCode)
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetStringValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithString("test"),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals("test", client.getValue("fakeKey", ""))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetStringValueFailed() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadGateway,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals("", client.getValue("fakeKey", ""))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetDoubleValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithDouble(3.14),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(3.14, client.getValue("fakeKey", 0.0))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetDoubleValueFailed() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadGateway,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(0.0, client.getValue("fakeKey", 0.0))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetBoolValue() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(true, client.getValue("fakeKey", false))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetBoolValueFailed() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadGateway,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(false, client.getValue("fakeKey", false))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testRequestTimeout() =
        runTest {
            val mockEngine =
                MockEngine {
                    delay(3000)
                    respond(content = Data.formatJsonBodyWithBoolean(true), status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    requestTimeout = 1.seconds
                }

            val ts = TimeSource.Monotonic
            val start = ts.markNow()
            assertEquals(false, client.getValue("fakeKey", false))
            val elapsed = ts.markNow() - start
            assertTrue(elapsed.inWholeSeconds in 1..2)
        }

    @Test
    fun testGetFromOnlyCache() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                    )
                }
            val sdkKey = TestUtils.randomSdkKey()
            val cacheKey: String =
                "${sdkKey}_${Constants.CONFIG_FILE_NAME}_${Constants.SERIALIZATION_FORMAT_VERSION}".encodeToByteArray()
                    .sha1Hex()
            val cache = InMemoryCache()
            cache.write(cacheKey, Data.formatCacheEntry("test"))
            val client =
                ConfigCatClient(sdkKey) {
                    httpEngine = mockEngine
                    configCache = cache
                    pollingMode =
                        autoPoll {
                            pollingInterval = 100.milliseconds
                        }
                }

            assertEquals("test", client.getValue("fakeKey", ""))

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 1
            }
        }

    @Test
    fun testOnlyCacheRefresh() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotFound,
                    )
                }
            val sdkKey = TestUtils.randomSdkKey()
            val cacheKey: String =
                "${sdkKey}_${Constants.CONFIG_FILE_NAME}_${Constants.SERIALIZATION_FORMAT_VERSION}".encodeToByteArray()
                    .sha1Hex()
            val cache = InMemoryCache()
            val opts = ConfigCatOptions()
            opts.configCache = cache
            opts.httpEngine = mockEngine
            cache.write(cacheKey, Data.formatCacheEntry("test"))
            val client = ConfigCatClient(sdkKey, opts)

            val result = client.forceRefresh()
            assertFalse(result.isSuccess)
            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 404 Not Found",
                result.error,
            )
            assertEquals("test", client.getValue("fakeKey", ""))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testGetLatestOnFail() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.Forbidden)
                    }
                } as MockEngine
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals("test1", client.getValue("fakeKey", ""))
            val result = client.forceRefresh()
            assertFalse(result.isSuccess)
            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 403 Forbidden",
                result.error,
            )
            assertEquals("test1", client.getValue("fakeKey", ""))
            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testForceRefreshLazy() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test2"), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
    fun testForceRefreshAuto() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test2"), status = HttpStatusCode.OK)
                    }
                } as MockEngine
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
    fun testAutoPollFailing() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            assertEquals(false, client.getValue("fakeKey", false))
            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testAutoPollRefreshFailing() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotFound,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val result = client.forceRefresh()
            assertFalse(result.isSuccess)
            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 404 Not Found",
                result.error,
            )
            assertEquals(false, client.getValue("fakeKey", false))
            assertTrue(mockEngine.requestHistory.size == 1 || mockEngine.requestHistory.size == 2)
        }

    @Test
    fun testLazyFailing() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = lazyLoad()
                }

            assertEquals(false, client.getValue("fakeKey", false))
            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testLazyRefreshFailing() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotFound,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = lazyLoad()
                }

            val result = client.forceRefresh()
            assertFalse(result.isSuccess)
            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 404 Not Found",
                result.error,
            )
            assertEquals(false, client.getValue("fakeKey", false))
            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testGetAllKeys() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.MULTIPLE_BODY,
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val keys = client.getAllKeys()
            assertEquals(2, keys.size)
        }

    @Test
    fun testGetAllValues() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.MULTIPLE_BODY,
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val values = client.getAllValues()
            assertEquals(2, values.size)
            assertEquals(true, values["key1"])
            assertEquals(false, values["key2"])
        }

    @Test
    fun testGetAllValueDetails() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.MULTIPLE_BODY,
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val details = client.getAllValueDetails()
            assertEquals(2, details.size)
            assertTrue { details.elementAt(0).value as? Boolean ?: false }
            assertFalse(details.elementAt(1).value as? Boolean ?: true)
        }

    @Test
    fun testAutoPollUserAgent() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = autoPoll()
                }

            client.getValue("fakeKey", false)
            // For Js we check the query params
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                assertEquals(
                    "ConfigCat-Kotlin/a-${Constants.VERSION}",
                    mockEngine.requestHistory.last().url.parameters["sdk"],
                )
            } else {
                assertEquals(
                    "ConfigCat-Kotlin/a-${Constants.VERSION}",
                    mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
                )
            }
        }

    @Test
    fun testLazyUserAgent() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = lazyLoad()
                }

            client.getValue("fakeKey", false)
            // For Js we check the query params
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                assertEquals(
                    "ConfigCat-Kotlin/l-${Constants.VERSION}",
                    mockEngine.requestHistory.last().url.parameters["sdk"],
                )
            } else {
                assertEquals(
                    "ConfigCat-Kotlin/l-${Constants.VERSION}",
                    mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
                )
            }
        }

    @Test
    fun testManualPollUserAgent() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = manualPoll()
                }

            client.forceRefresh()
            // For Js we check the query params
            if (PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE) {
                assertEquals(
                    "ConfigCat-Kotlin/m-${Constants.VERSION}",
                    mockEngine.requestHistory.last().url.parameters["sdk"],
                )
            } else {
                assertEquals(
                    "ConfigCat-Kotlin/m-${Constants.VERSION}",
                    mockEngine.requestHistory.last().headers["X-ConfigCat-UserAgent"],
                )
            }
        }

    @Test
    fun testGetValueDetailsWithError() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.BadRequest,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val details = client.getValueDetails("fakeKey", "")

            assertEquals("", details.value)
            assertTrue(details.isDefaultValue)
            assertEquals(
                "Config JSON is not present when evaluating setting 'fakeKey'. " +
                    "Returning the `defaultValue` parameter that you specified in your application: ''.",
                details.error,
            )
            assertEquals(EvaluationErrorCode.CONFIG_JSON_NOT_AVAILABLE, details.errorCode)
        }

    @Test
    fun testOnlineOffline() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = null
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
    fun testOnlineOfflineWithCache() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = InMemoryCache()
                    pollingMode = autoPoll { pollingInterval = 2.seconds }
                }

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 1
            }

            client.setOffline()
            assertTrue(client.isOffline)

            val result = client.forceRefresh()

            assertTrue(result.isSuccess)
            assertEquals(1, mockEngine.requestHistory.size)

            client.setOnline()
            assertFalse(client.isOffline)

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size > 1
            }
        }

    @Test
    fun testInitOffline() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
    fun testInitOfflineCallsReady() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatJsonBodyWithBoolean(true),
                        status = HttpStatusCode.OK,
                    )
                }
            var ready: ClientCacheState? = null
            ConfigCatClient(TestUtils.randomSdkKey()) {
                httpEngine = mockEngine
                pollingMode = autoPoll { pollingInterval = 2.seconds }
                offline = true
                hooks.addOnClientReady { clientCacheState: ClientCacheState -> ready = clientCacheState }
            }

            assertEquals(0, mockEngine.requestHistory.size)
            TestUtils.awaitUntil {
                ready != null
            }
        }

    @Test
    fun testOfflineRefreshFromCache() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotModified,
                    )
                }
            val cache = SingleValueCache(Data.formatCacheEntry("test1"))
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = cache
                    pollingMode = autoPoll { pollingInterval = 2.seconds }
                }

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 1
            }

            client.setOffline()
            assertTrue(client.isOffline)

            val value = client.getValue("fakeKey", "")
            assertEquals("test1", value)

            cache.write("", Data.formatCacheEntry("test2"))

            val result = client.forceRefresh()

            assertTrue(result.isSuccess)
            assertEquals(RefreshErrorCode.NONE, result.errorCode)
            assertNull(result.errorException)
            assertEquals(1, mockEngine.requestHistory.size)

            val value2 = client.getValue("fakeKey", "")
            assertEquals("test2", value2)

            client.setOnline()
            assertFalse(client.isOffline)

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size > 1
            }
        }

    @Test
    fun testOfflinePollRefreshesFromCache() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = "",
                        status = HttpStatusCode.NotModified,
                    )
                }
            val cache = SingleValueCache(Data.formatCacheEntry("test1"))
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    configCache = cache
                    pollingMode = autoPoll { pollingInterval = 1.seconds }
                    offline = true
                }

            client.waitForReady()

            TestUtils.awaitUntil {
                val snapshot = client.snapshot()
                val value = snapshot.getValue("fakeKey", "")
                value == "test1"
            }

            cache.write("", Data.formatCacheEntry("test2"))

            TestUtils.awaitUntil {
                val snapshot = client.snapshot()
                val value = snapshot.getValue("fakeKey", "")
                value == "test2"
            }

            assertEquals(0, mockEngine.requestHistory.size)
        }

    @Test
    fun testDefaultUser() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatConfigWithRules(),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
    fun testDefaultUserVariationId() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = Data.formatConfigWithRules(),
                        status = HttpStatusCode.OK,
                    )
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
    fun testHooks() =
        runTest {
            val mockEngine =
                MockEngine.create {
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
            var readyWithClientState: ClientCacheState? = null

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = manualPoll()
                    hooks.addOnConfigChanged { settings, snapshot -> changed = true }
                    hooks.addOnClientReady { clientCacheState: ClientCacheState -> readyWithClientState = clientCacheState }
                    hooks.addOnClientReadyWithSnapshot { snapshot: ConfigCatClientSnapshot -> ready = true}
                    hooks.addOnError { err -> error = err }
                }

            client.waitForReady()

            client.forceRefresh()
            client.forceRefresh()

            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 404 Not Found",
                error,
            )
            assertTrue(changed)

            assertTrue(ready)
            assertEquals(ClientCacheState.NO_FLAG_DATA, readyWithClientState)
        }

    @Test
    fun testHooksSub() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.NotFound)
                    }
                }
            var error = ""
            var changed = false

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = manualPoll()
                }

            client.hooks.addOnConfigChanged { settings, snapshot -> changed = true }
            client.hooks.addOnError { err -> error = err }

            client.forceRefresh()
            client.forceRefresh()

            assertEquals(
                "Your SDK Key seems to be wrong. You can find the valid SDK Key at " +
                    "https://app.configcat.com/sdkkey. Received response: 404 Not Found",
                error,
            )
            assertTrue(changed)
        }

    @Test
    fun testReadyHookManualPollWithCache() =
        runTest {
            val cache = SingleValueCache(Data.formatCacheEntry("test"))

            var ready: ClientCacheState? = null

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    pollingMode = manualPoll()
                    configCache = cache
                    hooks.addOnClientReady { clientReadyState: ClientCacheState -> ready = clientReadyState }
                }

            TestUtils.awaitUntil {
                ready != null
            }

            assertEquals(ClientCacheState.HAS_CACHED_FLAG_DATA_ONLY, ready)
        }

    @Test
    fun testReadyHookLocalOnly() =
        runTest {

            var ready: ClientCacheState? = null

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY }
                    pollingMode = manualPoll()
                    hooks.addOnClientReady { clientReadyState: ClientCacheState -> ready = clientReadyState }
                }

            assertEquals(ClientCacheState.HAS_LOCAL_OVERRIDE_FLAG_DATA_ONLY, ready)
        }

    @Test
    fun testHooksAutoPollSub() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.InternalServerError)
                    }
                }
            var error = ""
            var changed = false
            var ready: ClientCacheState? = null

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = autoPoll()
                    hooks.addOnConfigChanged { settings, snapshot -> changed = true }
                    hooks.addOnClientReady { clientReadyState: ClientCacheState -> ready = clientReadyState }
                    hooks.addOnError { err -> error = err }
                }

            client.forceRefresh()
            client.forceRefresh()

            assertEquals(
                "Unexpected HTTP response was received while trying to fetch config JSON: 500 ",
                error,
            )
            assertTrue(changed)
            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, ready)
        }

    @Test
    fun testFail400() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.BadRequest)
                    }
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = manualPoll()
                }

            client.forceRefresh()
            val error = client.forceRefresh()

            assertEquals("Unexpected HTTP response was received while trying to fetch config JSON: 400 ", error.error)
            assertEquals(RefreshErrorCode.UNEXPECTED_HTTP_RESPONSE, error.errorCode)
            assertNull(error.errorException)
        }

    @Test
    fun testOnFlagEvalError() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.BadRequest)
                }
            var called = false

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = manualPoll()
                    hooks.addOnFlagEvaluated { details ->
                        called = true
                        assertTrue(details.isDefaultValue)
                        assertEquals("", details.value)
                        assertEquals("ID", details.user?.identifier)
                        assertEquals(
                            "Config JSON is not present when evaluating setting 'fakeKey'. Returning the " +
                                "`defaultValue` parameter that you specified in your application: ''.",
                            details.error,
                        )
                        assertEquals(EvaluationErrorCode.CONFIG_JSON_NOT_AVAILABLE, details.errorCode)
                    }
                }

            client.forceRefresh()
            client.getValue("fakeKey", "", ConfigCatUser("ID"))

            assertTrue(called)
        }

    @Test
    fun testEvalDetails() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatConfigWithRules(), status = HttpStatusCode.OK)
                }
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
            assertEquals(EvaluationErrorCode.NONE, details.errorCode)
            assertNull(details.matchedPercentageOption)
        }

    @Test
    fun testEvalDetailsHook() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatConfigWithRules(), status = HttpStatusCode.OK)
                }
            var called = false
            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
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
                        assertEquals(EvaluationErrorCode.NONE, details.errorCode)
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
        var client1 = ConfigCatClient("testSingleton") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        val client2 = ConfigCatClient("testSingleton") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client1, client2)

        ConfigCatClient.closeAll()

        client1 = ConfigCatClient("testSingleton") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)
    }

    @Test
    fun testRemoveTheClosingInstanceOnly() {
        val client1 = ConfigCatClient("testRemoveTheClosingInstanceOnly") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        client1.close()

        val client2 = ConfigCatClient("testRemoveTheClosingInstanceOnly") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertNotSame(client1, client2)

        client1.close()

        val client3 = ConfigCatClient("testRemoveTheClosingInstanceOnly") { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }

        assertSame(client2, client3)
    }

    @Test
    fun testClose() {
        val client1 = ConfigCatClient(TestUtils.randomSdkKey()) { flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY } }
        assertFalse(client1.isClosed())
        client1.close()
        assertTrue(client1.isClosed())
    }

    @Test
    fun testSDKKeyIsNotEmpty() {
        val exception =
            assertFailsWith(IllegalArgumentException::class, block = {
                ConfigCatClient("")
            })
        assertEquals("SDK Key cannot be empty.", exception.message)
    }

    @Test
    fun testSDKKeyIsValid() {
        val mockEngine =
            MockEngine {
                respond(
                    content = Data.formatJsonBodyWithBoolean(true),
                    status = HttpStatusCode.OK,
                )
            }
        // TEST VALID KEYS
        var client =
            ConfigCatClient("sdk-key-90123456789012/1234567890123456789012") {
                httpEngine = mockEngine
            }
        assertNotNull(client)
        client =
            ConfigCatClient("configcat-sdk-1/sdk-key-90123456789012/1234567890123456789012") {
                httpEngine = mockEngine
            }
        assertNotNull(client)
        client =
            ConfigCatClient("configcat-proxy/sdk-key-90123456789012") {
                baseUrl = "https://my-configcat-proxy"
                httpEngine = mockEngine
            }
        assertNotNull(client)

        ConfigCatClient.closeAll()

        // TEST INVALID KEYS
        val wrongSDKKeys: List<String> =
            listOf(
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
                "configcat-proxy/sdk-key-90123456789012",
            )
        wrongSDKKeys.forEach {
            val exception =
                assertFailsWith(IllegalArgumentException::class, block = {
                    ConfigCatClient(it)
                })
            assertEquals("SDK Key '$it' is invalid.", exception.message)
        }

        val exception =
            assertFailsWith(IllegalArgumentException::class, block = {
                ConfigCatClient("configcat-proxy/") { baseUrl = "https://my-configcat-proxy" }
            })
        assertEquals("SDK Key 'configcat-proxy/' is invalid.", exception.message)

        // TEST OverrideBehaviour.localOnly skip sdkKey validation
        client =
            ConfigCatClient("sdk-key-90123456789012") {
                flagOverrides = { behavior = OverrideBehavior.LOCAL_ONLY }
            }

        assertNotNull(client)

        ConfigCatClient.closeAll()
    }

    @Test
    fun testSpecialCharactersWorks() =
        runTest {
            // override content with configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/u28_1qNyZ0Wz-ldYHIU7-g
            val mockEngine =
                MockEngine {
                    respond(
                        content = specialCharacterContent,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient("configcat-sdk-1/PKDVCLf-Hq-h-kCzMp-L7Q/u28_1qNyZ0Wz-ldYHIU7-g") {
                    httpEngine = mockEngine
                }
            // äöüÄÖÜçéèñışğâ¢™✓😀
            val specialCharacters = "äöüÄÖÜçéèñışğâ¢™✓\uD83D\uDE00"

            val user = ConfigCatUser(specialCharacters)

            assertEquals(specialCharacters, client.getValue("specialCharacters", "NOT_CAT", user))

            assertEquals(specialCharacters, client.getValue("specialCharactersHashed", "NOT_CAT", user))

            ConfigCatClient.closeAll()
        }

    @Test
    fun testGetValueValidTypes() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = testGetValueTypes,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }
            // String
            assertEquals("fakeValueString", client.getValue("fakeKeyString", "default", null))
            // Boolean
            assertEquals(true, client.getValue("fakeKeyBoolean", false, null))
            // Int
            assertEquals(1, client.getValue("fakeKeyInt", 0, null))
            // Double
            assertEquals(2.1, client.getValue("fakeKeyDouble", 1.1, null))

            // getValue allows null.
            val value1 = client.getValue<Boolean?>("wrongKey", null, null)
            assertNull(value1)

            // getAnyValue allows null.
            val value2 = client.getAnyValue("wrongKey", null, null)
            assertNull(value2)

            // getAnyValue allows any default value.
            val defaultValue = ConfigCatUser("testId")
            val value3 = client.getAnyValue("wrongKey", defaultValue, null)
            assertEquals(defaultValue, value3)
        }

    @Test
    fun testGetValueInvalidTypes() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = testGetValueTypes,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            // In case of JS the float is converted to an accepted type, in this case skip this test
            if (!(PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE)) {
                // Float
                val floatException =
                    assertFailsWith(
                        exceptionClass = IllegalArgumentException::class,
                        block = { client.getValue("fakeKeyString", 3.14f) },
                    )
                assertEquals(
                    "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable).",
                    floatException.message,
                )
            }

            // Object
            val exception =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { client.getValue("fakeKeyString", ConfigCatUser("testId")) },
                )
            assertEquals(
                "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable).",
                exception.message,
            )
        }

    @Test
    fun testGetValueDetailsValidTypes() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = testGetValueTypes,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }
            // String
            assertEquals("fakeValueString", client.getValueDetails("fakeKeyString", "default", null).value)
            // Boolean
            assertEquals(true, client.getValueDetails("fakeKeyBoolean", false, null).value)
            // Int
            assertEquals(1, client.getValueDetails("fakeKeyInt", 0, null).value)
            // Double
            assertEquals(2.1, client.getValueDetails("fakeKeyDouble", 1.1, null).value)

            // getValue allows null.
            val value1 = client.getValueDetails<Boolean?>("wrongKey", null, null).value
            assertNull(value1)

            // getAnyValue allows null.
            val value2 = client.getAnyValueDetails("wrongKey", null, null).value
            assertNull(value2)

            // getAnyValue allows any default value.
            val defaultValue = ConfigCatUser("testId")
            val value3 = client.getAnyValueDetails("wrongKey", defaultValue, null).value
            assertEquals(defaultValue, value3)
        }

    @Test
    fun testGetValueDetailsInvalidTypes() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = testGetValueTypes,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            // In case of JS the float is converted to an accepted type, in this case skip this test
            if (!(PlatformUtils.IS_BROWSER || PlatformUtils.IS_NODE)) {
                // Float
                val floatException =
                    assertFailsWith(
                        exceptionClass = IllegalArgumentException::class,
                        block = { client.getValueDetails("fakeKeyString", 3.14f) },
                    )
                assertEquals(
                    "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable).",
                    floatException.message,
                )
            }

            // Object
            val exception =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { client.getValueDetails("fakeKeyString", ConfigCatUser("testId")) },
                )
            assertEquals(
                "Only the following types are supported: String, Boolean, Int, Double (both nullable and non-nullable).",
                exception.message,
            )
        }

    @Test
    fun testFlagKeyAndVariationIdValidation() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(
                        content = testGetValueTypes,
                        status = HttpStatusCode.OK,
                    )
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                }

            val exceptionGetValue =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { client.getValue("", "default", null) },
                )
            assertEquals("'key' cannot be empty.", exceptionGetValue.message)

            val exceptionGetValueDetails =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { client.getValueDetails("", "default", null) },
                )
            assertEquals("'key' cannot be empty.", exceptionGetValueDetails.message)

            val exceptionGetKeyAndValue =
                assertFailsWith(
                    exceptionClass = IllegalArgumentException::class,
                    block = { client.getKeyAndValue("") },
                )
            assertEquals("'variationId' cannot be empty.", exceptionGetKeyAndValue.message)
        }

    @Test
    fun testWaitForReady() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                }

            val client =
                ConfigCatClient(TestUtils.randomSdkKey()) {
                    httpEngine = mockEngine
                    pollingMode = autoPoll()
                }

            val clientCacheState = client.waitForReady()

            assertEquals(ClientCacheState.HAS_UP_TO_DATE_FLAG_DATA, clientCacheState)
        }

    private val testGetValueTypes =
        """
        {
           "p":{
              "s":"test-salt",
              "u":"test"
           },
           "f":{
              "fakeKeyString":{
                 "t":1,
                 "v":{
                    "s":"fakeValueString"
                 },
                 "s":0,
                 "p":[
                    
                 ],
                 "r":[
                    
                 ]
              },
              "fakeKeyInt":{
                 "t":2,
                 "v":{
                    "i":1
                 },
                 "s":0,
                 "p":[
                    
                 ],
                 "r":[
                    
                 ]
              },
              "fakeKeyDouble":{
                 "t":3,
                 "v":{
                    "d":2.1
                 },
                 "s":0,
                 "p":[
                    
                 ],
                 "r":[
                    
                 ]
              },
              "fakeKeyBoolean":{
                 "t":0,
                 "v":{
                    "b":true
                 },
                 "s":0,
                 "p":[
                    
                 ],
                 "r":[
                    
                 ]
              }
           }
        }
        """.trimIndent()

    private val specialCharacterContent = """
        {
           "p":{
              "u":"https://cdn-global.configcat.com",
              "r":0,
              "s":"ABWpFwDcdChe8DCLRnfe1qcRzFaRWqFKifbGCBnkHTU="
           },
           "f":{
              "specialCharacters":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Identifier",
                                "c":30,
                                "l":[
                                   "äöüÄÖÜçéèñışğâ¢™✓😀"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"äöüÄÖÜçéèñışğâ¢™✓😀"
                          },
                          "i":"1238ed4f"
                       }
                    }
                 ],
                 "v":{
                    "s":"NOT_CAT"
                 },
                 "i":"6a20318f"
              },
              "specialCharactersHashed":{
                 "t":1,
                 "r":[
                    {
                       "c":[
                          {
                             "u":{
                                "a":"Identifier",
                                "c":22,
                                "l":[
                                   "40_4e37b40f1a89cf05c99a9451f6baa1d149a79c5f9a9a3793a6782c8eed9f605d"
                                ]
                             }
                          }
                       ],
                       "s":{
                          "v":{
                             "s":"äöüÄÖÜçéèñışğâ¢™✓😀"
                          },
                          "i":"bb95d969"
                       }
                    }
                 ],
                 "v":{
                    "s":"NOT_CAT"
                 },
                 "i":"33f810a1"
              }
           }
        }
    """
}
