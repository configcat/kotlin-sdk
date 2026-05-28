package com.configcat

import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.TimeSource

class ConfigServiceTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testAutoPollGet() =
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
            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 2.seconds })

            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            TestUtils.awaitUntil {
                val result2 = service.getSettings()
                result2.settings["fakeKey"]?.settingValue?.stringValue == "test2"
            }

            assertTrue(mockEngine.requestHistory.size in 2..3)
        }

    @Test
    fun testAutoPollGetFailed() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.BadGateway)
                    }
                } as MockEngine
            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 2.seconds })

            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            TestUtils.awaitUntil {
                val result2 = service.getSettings()
                result2.settings["fakeKey"]?.settingValue?.stringValue == "test1" && mockEngine.requestHistory.size in 2..3
            }
        }

    @Test
    fun testAutoOnConfigChanged() =
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
            val service =
                Services.createConfigService(
                    mockEngine,
                    autoPoll {
                        pollingInterval = 2.seconds
                    },
                )

            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            assertTrue(mockEngine.requestHistory.size in 1..2)
        }

    @Test
    fun testLazyGet() =
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
            val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 2.seconds })

            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            TestUtils.awaitUntil {
                val result2 = service.getSettings()
                result2.settings["fakeKey"]?.settingValue?.stringValue == "test2"
            }

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testLazyGetFailed() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.BadGateway)
                    }
                } as MockEngine
            val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 2.seconds })

            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            TestUtils.awaitUntil {
                val result2 = service.getSettings()
                result2.settings["fakeKey"]?.settingValue?.stringValue == "test1" && mockEngine.requestHistory.size == 2
            }
        }

    @Test
    fun testManualPollGet() =
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
            val service = Services.createConfigService(mockEngine, manualPoll())

            service.refresh()
            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            service.refresh()
            val result2 = service.getSettings()
            assertEquals("test2", result2.settings["fakeKey"]?.settingValue?.stringValue)

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testManualPollFailed() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                    this.addHandler {
                        respond(content = "", status = HttpStatusCode.BadGateway)
                    }
                } as MockEngine
            val service = Services.createConfigService(mockEngine, manualPoll())

            service.refresh()
            val result = service.getSettings()
            assertEquals("test1", result.settings["fakeKey"]?.settingValue?.stringValue)

            service.refresh()
            val result2 = service.getSettings()
            assertEquals("test1", result2.settings["fakeKey"]?.settingValue?.stringValue)

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testAutoPollInitWaitTimeTimeout() =
        runTest {
            val mockEngine =
                MockEngine {
                    delay(5000)
                    respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                }

            val ts = TimeSource.Monotonic
            val start = ts.markNow()
            val service =
                Services.createConfigService(
                    mockEngine,
                    autoPoll {
                        pollingInterval = 60.seconds
                        maxInitWaitTime = 1.seconds
                    },
                )

            val result = service.getSettings()
            val elapsed = ts.markNow() - start
            assertNull(result.settings["fakeKey"]?.settingValue?.stringValue)
            assertTrue(elapsed.inWholeSeconds in 1..2)
        }

    @Test
    fun testNullCache() =
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
            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, null)

            TestUtils.awaitUntil {
                service.getSettings().settings.values.first().settingValue.stringValue == "test2"
            }

            assertTrue(mockEngine.requestHistory.size in 2..3)
        }

    @Test
    fun testAutoPollInitWaitTimeTimeoutReturnsWithCached() =
        runTest {
            val mockEngine =
                MockEngine {
                    delay(5000)
                    respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                }
            val cache = SingleValueCache(Data.formatCacheEntryWithDate("test", Instant.DISTANT_PAST))
            val ts = TimeSource.Monotonic
            val start = ts.markNow()
            val service =
                Services.createConfigService(
                    mockEngine,
                    autoPoll {
                        pollingInterval = 60.seconds
                        maxInitWaitTime = 1.seconds
                    },
                    cache,
                )
            val result = service.getSettings()
            val elapsed = ts.markNow() - start
            assertEquals("test", result.settings["fakeKey"]?.settingValue?.stringValue)
            println(elapsed)
            assertTrue(elapsed.inWholeSeconds in 1..2)
        }

    @Test
    fun testAutoPollCacheWrite() =
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
            val cache = InMemoryCache()
            Services.createConfigService(mockEngine, autoPoll { pollingInterval = 2.seconds }, cache)

            TestUtils.awaitUntil {
                !cache.store.values.isEmpty() && cache.store.values.first().contains("test1")
            }

            TestUtils.awaitUntil {
                !cache.store.values.isEmpty() && cache.store.values.first().contains("test2")
            }

            assertTrue(mockEngine.requestHistory.size in 2..3)
        }

    @Test
    fun testPollIntervalRespectsCacheExpiration() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val cache = SingleValueCache(Data.formatCacheEntry("test"))
            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, cache)

            val setting = service.getSettings().settings["fakeKey"]
            assertEquals("test", setting?.settingValue?.stringValue)

            assertEquals(0, mockEngine.requestHistory.size)

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 1
            }
        }

    @Test
    fun testPollsWhenCacheExpired() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val cache = SingleValueCache(Data.formatCacheEntryWithDate("test", Clock.System.now().minus(5.seconds)))
            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, cache)

            val setting = service.getSettings().settings["fakeKey"]
            assertEquals("test1", setting?.settingValue?.stringValue)

            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testAutoPollOnlineOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds })

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 1
            }

            service.offline()
            TestUtils.wait(2.seconds)

            assertEquals(1, mockEngine.requestHistory.size)
            service.online()

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 2
            }
        }

    @Test
    fun testAutoPollInitOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service =
                Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, offline = true)

            TestUtils.wait(2.seconds)

            assertEquals(0, mockEngine.requestHistory.size)
            service.online()

            TestUtils.awaitUntil {
                mockEngine.requestHistory.size == 2
            }
        }

    @Test
    fun testInitWaitTimeIgnoredWhenCacheIsNotExpired() =
        runTest {
            val mockEngine =
                MockEngine {
                    delay(5000)
                    respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                }
            val ts = TimeSource.Monotonic
            val start = ts.markNow()
            val cache = SingleValueCache(Data.formatCacheEntry("test"))
            val service =
                Services.createConfigService(
                    mockEngine,
                    autoPoll {
                        pollingInterval = 60.seconds
                        maxInitWaitTime = 1.seconds
                    },
                    cache,
                )
            val result = service.getSettings()
            val elapsed = ts.markNow() - start
            assertEquals("test", result.settings["fakeKey"]?.settingValue?.stringValue)
            assertTrue(elapsed.inWholeSeconds < 1)
        }

    @Test
    fun testLazyCacheWrite() =
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
            val cache = InMemoryCache()
            val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 2.seconds }, cache)

            service.getSettings()
            assertTrue(cache.store.values.first().contains("test1"))

            TestUtils.awaitUntil {
                service.getSettings()
                cache.store.values.first().contains("test2")
            }

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testCacheExpirationRespectedInTTLCalc() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                }
            val cache = SingleValueCache(Data.formatCacheEntry("test"))
            val service =
                Services.createConfigService(
                    mockEngine,
                    lazyLoad {
                        cacheRefreshInterval = 1.seconds
                    },
                    cache,
                )
            service.getSettings()
            service.getSettings()

            assertEquals(0, mockEngine.requestHistory.size)
            TestUtils.wait(1.seconds)

            service.getSettings()
            service.getSettings()

            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testCacheExpirationRespectedInTTLCalc304() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = "", status = HttpStatusCode.NotModified)
                }
            val cache = SingleValueCache(Data.formatCacheEntry("test"))
            val service =
                Services.createConfigService(
                    mockEngine,
                    lazyLoad {
                        cacheRefreshInterval = 1.seconds
                    },
                    cache,
                )
            service.getSettings()
            service.getSettings()

            assertEquals(0, mockEngine.requestHistory.size)
            TestUtils.wait(1.seconds)

            service.getSettings()
            service.getSettings()

            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testCacheTTLRespectsExternalCache() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithString("test_remote"), status = HttpStatusCode.OK)
                }
            val cache = SingleValueCache(Data.formatCacheEntryWithETag("test_local", "etag"))
            val service =
                Services.createConfigService(
                    mockEngine,
                    lazyLoad {
                        cacheRefreshInterval = 1.seconds
                    },
                    cache,
                )
            assertEquals("test_local", service.getSettings().settings["fakeKey"]?.settingValue?.stringValue)
            assertEquals(0, mockEngine.requestHistory.size)
            TestUtils.wait(1.seconds)

            cache.write("", Data.formatCacheEntryWithETag("test_local2", "etag2"))
            assertEquals("test_local2", service.getSettings().settings["fakeKey"]?.settingValue?.stringValue)
            assertEquals(0, mockEngine.requestHistory.size)
        }

    @Test
    fun testLazyOnlineOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 1.seconds })

            service.getSettings()

            assertEquals(1, mockEngine.requestHistory.size)
            service.offline()

            TestUtils.wait(1.5.seconds)
            service.getSettings()

            assertEquals(1, mockEngine.requestHistory.size)
            service.online()
            service.getSettings()

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testLazyInitOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service =
                Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 1.seconds }, offline = true)

            service.getSettings()

            assertEquals(0, mockEngine.requestHistory.size)

            TestUtils.wait(1.5.seconds)
            service.getSettings()

            assertEquals(0, mockEngine.requestHistory.size)
            service.online()
            service.getSettings()

            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testManualCacheWrite() =
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
            val cache = InMemoryCache()
            val service = Services.createConfigService(mockEngine, manualPoll(), cache)

            service.refresh()
            assertTrue(cache.store.values.first().contains("test1"))

            service.refresh()
            assertTrue(cache.store.values.first().contains("test2"))

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testManualOnlineOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service = Services.createConfigService(mockEngine, manualPoll())

            service.refresh()

            assertEquals(1, mockEngine.requestHistory.size)

            service.offline()
            service.refresh()

            assertEquals(1, mockEngine.requestHistory.size)

            service.online()
            service.refresh()

            assertEquals(2, mockEngine.requestHistory.size)
        }

    @Test
    fun testManualInitOffline() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val service = Services.createConfigService(mockEngine, manualPoll(), offline = true)

            service.refresh()

            assertEquals(0, mockEngine.requestHistory.size)

            service.refresh()

            assertEquals(0, mockEngine.requestHistory.size)

            service.online()
            service.refresh()

            assertEquals(1, mockEngine.requestHistory.size)
        }

    @Test
    fun testEnsureManualNotInitiatesHTTP() =
        runTest {
            val mockEngine =
                MockEngine {
                    respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
                }
            val service = Services.createConfigService(mockEngine, manualPoll())

            val result = service.getSettings()
            assertNull(result.settings["fakeKey"]?.settingValue?.stringValue)

            assertEquals(0, mockEngine.requestHistory.size)
        }

    @Test
    fun testCacheKey() {
        val mockEngine =
            MockEngine {
                respond(content = Data.formatJsonBodyWithString("test1"), status = HttpStatusCode.OK)
            }
        val configCatOptions = ConfigCatOptions()
        // Test Data: SDKKey "configcat-sdk-1/TEST_KEY-0123456789012/1234567890123456789012", HASH "f83ba5d45bceb4bb704410f51b704fb6dfa19942"
        configCatOptions.sdkKey = "configcat-sdk-1/TEST_KEY-0123456789012/1234567890123456789012"
        val service = Services.createConfigService(mockEngine, options = configCatOptions)
        assertEquals("f83ba5d45bceb4bb704410f51b704fb6dfa19942", service.cacheKey)

        // Test Data: SDKKey "configcat-sdk-1/TEST_KEY2-123456789012/1234567890123456789012", HASH "da7bfd8662209c8ed3f9db96daed4f8d91ba5876"
        configCatOptions.sdkKey = "configcat-sdk-1/TEST_KEY2-123456789012/1234567890123456789012"
        val service2 = Services.createConfigService(mockEngine, options = configCatOptions)
        assertEquals("da7bfd8662209c8ed3f9db96daed4f8d91ba5876", service2.cacheKey)
    }

    @Test
    fun testStateMonitor() =
        runTest {
            val mockEngine =
                MockEngine.create {
                    this.addHandler {
                        respond(content = Data.formatJsonBodyWithString("test"), status = HttpStatusCode.OK)
                    }
                } as MockEngine

            val opts = ConfigCatOptions()
            val monitor = TestStateMonitor()
            opts.stateMonitor = monitor
            val service = Services.createConfigService(mockEngine, options = opts)

            assertFalse(service.isOffline)

            monitor.isInValidState.store(false)
            monitor.notifyListeners()

            assertTrue(service.isOffline)

            monitor.isInValidState.store(true)
            monitor.notifyListeners()

            assertFalse(service.isOffline)

            service.offline()
            assertTrue(service.isOffline)

            monitor.isInValidState.store(false)
            monitor.notifyListeners()

            assertTrue(service.isOffline)

            monitor.isInValidState.store(true)
            monitor.notifyListeners()

            assertTrue(service.isOffline)

            service.online()

            assertFalse(service.isOffline)

            service.close()
        }
}
