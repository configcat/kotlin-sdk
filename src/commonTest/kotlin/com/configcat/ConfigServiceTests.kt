package com.configcat

import com.soywiz.klock.DateTime
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigServiceTests {
    @AfterTest
    fun tearDown() {
        Services.reset()
    }

    @Test
    fun testAutoPollGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 2.seconds })

        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        TestUtils.awaitUntil {
            val result2 = service.getSettings()
            result2.settings["fakeKey"]?.value == "test2"
        }

        assertTrue(mockEngine.requestHistory.size in 2..3)
    }

    @Test
    fun testAutoPollGetFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 2.seconds })

        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        TestUtils.awaitUntil {
            val result2 = service.getSettings()
            result2.settings["fakeKey"]?.value == "test1" && mockEngine.requestHistory.size in 2..3
        }
    }

    @Test
    fun testAutoOnConfigChanged() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, autoPoll {
            pollingInterval = 2.seconds
        })

        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)
        
        assertTrue(mockEngine.requestHistory.size in 1..2)
    }

    @Test
    fun testLazyGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 2.seconds })

        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        TestUtils.awaitUntil {
            val result2 = service.getSettings()
            result2.settings["fakeKey"]?.value == "test2"
        }

        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testLazyGetFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 2.seconds })

        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        TestUtils.awaitUntil {
            val result2 = service.getSettings()
            result2.settings["fakeKey"]?.value == "test1" && mockEngine.requestHistory.size == 2
        }
    }

    @Test
    fun testManualPollGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, manualPoll())

        service.refresh()
        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        service.refresh()
        val result2 = service.getSettings()
        assertEquals("test2", result2.settings["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testManualPollFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Services.createConfigService(mockEngine, manualPoll())

        service.refresh()
        val result = service.getSettings()
        assertEquals("test1", result.settings["fakeKey"]?.value)

        service.refresh()
        val result2 = service.getSettings()
        assertEquals("test1", result2.settings["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)
    }

    @Test
    fun testAutoPollInitWaitTimeTimeout() = runTest {
        val mockEngine = MockEngine {
            delay(5000)
            respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
        }
        val service = Services.createConfigService(
            mockEngine,
            autoPoll {
                pollingInterval = 60.seconds
                maxInitWaitTime = 1.seconds
            }
        )

        val start = DateTime.now()
        val result = service.getSettings()
        val elapsed = DateTime.now() - start
        assertNull(result.settings["fakeKey"]?.value)
        assertTrue(elapsed.seconds in 1.0..2.0)
    }

    @Test
    fun testAutoPollInitWaitTimeTimeoutReturnsWithCached() = runTest {
        val mockEngine = MockEngine {
            delay(5000)
            respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
        }
        val cache = SingleValueCache(Data.formatCacheEntryWithDate("test", Constants.distantPast))
        val service = Services.createConfigService(
            mockEngine,
            autoPoll {
                pollingInterval = 60.seconds
                maxInitWaitTime = 1.seconds
            },
            cache
        )
        val start = DateTime.now()
        val result = service.getSettings()
        val elapsed = DateTime.now() - start
        assertEquals("test", result.settings["fakeKey"]?.value)
        println(elapsed)
        assertTrue(elapsed.seconds in 1.0..2.0)
    }

    @Test
    fun testAutoPollCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
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
    fun testPollIntervalRespectsCacheExpiration() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
        } as MockEngine

        val cache = SingleValueCache(Data.formatCacheEntry("test"))
        val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, cache)

        val setting = service.getSettings().settings["fakeKey"]
        assertEquals("test", setting?.value)

        assertEquals(0, mockEngine.requestHistory.size)

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size == 1
        }
    }

    @Test
    fun testAutoPollOnlineOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
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
    fun testAutoPollInitOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
        } as MockEngine

        val service = Services.createConfigService(mockEngine, autoPoll { pollingInterval = 1.seconds }, offline = true)

        TestUtils.wait(2.seconds)

        assertEquals(0, mockEngine.requestHistory.size)
        service.online()

        TestUtils.awaitUntil {
            mockEngine.requestHistory.size == 2
        }
    }

    @Test
    fun testInitWaitTimeIgnoredWhenCacheIsNotExpired() = runTest {
        val mockEngine = MockEngine {
            delay(5000)
            respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
        }
        val start = DateTime.now()
        val cache = SingleValueCache(Data.formatCacheEntry("test"))
        val service = Services.createConfigService(
            mockEngine,
            autoPoll {
                pollingInterval = 60.seconds
                maxInitWaitTime = 1.seconds
            },
            cache
        )
        val result = service.getSettings()
        val elapsed = DateTime.now() - start
        assertEquals("test", result.settings["fakeKey"]?.value)
        assertTrue(elapsed.seconds < 1)
    }

    @Test
    fun testLazyCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
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
    fun testCacheExpirationRespectedInTTLCalc() = runTest {
        val mockEngine = MockEngine {
            respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
        }
        val cache = SingleValueCache(Data.formatCacheEntry("test"))
        val service = Services.createConfigService(
            mockEngine,
            lazyLoad {
                cacheRefreshInterval = 1.seconds
            },
            cache
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
    fun testCacheExpirationRespectedInTTLCalc304() = runTest {
        val mockEngine = MockEngine {
            respond(content = "", status = HttpStatusCode.NotModified)
        }
        val cache = SingleValueCache(Data.formatCacheEntry("test"))
        val service = Services.createConfigService(
            mockEngine,
            lazyLoad {
                cacheRefreshInterval = 1.seconds
            },
            cache
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
    fun testLazyOnlineOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
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
    fun testLazyInitOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
            }
        } as MockEngine

        val service = Services.createConfigService(mockEngine, lazyLoad { cacheRefreshInterval = 1.seconds }, offline = true)

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
    fun testManualCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Data.formatJsonBody("test2"), status = HttpStatusCode.OK)
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
    fun testManualOnlineOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
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
    fun testManualInitOffline() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Data.formatJsonBody("test"), status = HttpStatusCode.OK)
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
    fun testEnsureManualNotInitiatesHTTP() = runTest {
        val mockEngine = MockEngine {
            respond(content = Data.formatJsonBody("test1"), status = HttpStatusCode.OK)
        }
        val service = Services.createConfigService(mockEngine, manualPoll())

        val result = service.getSettings()
        assertNull(result.settings["fakeKey"]?.value)

        assertEquals(0, mockEngine.requestHistory.size)
    }
}
