package com.configcat.client

import com.configcat.client.fetch.autoPoll
import com.configcat.client.fetch.lazyLoad
import com.configcat.client.fetch.manualPoll
import com.soywiz.klock.DateTime
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ConfigServiceTests {
    @Test
    fun testAutoPollGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, autoPoll { pollingIntervalSeconds = 2 })

        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        Utils.delayWithBlock(3_000)

        val settings2 = service.getSettings()
        assertEquals("test2", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testAutoPollGetFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, autoPoll { pollingIntervalSeconds = 2 })

        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        Utils.delayWithBlock(3_000)

        val settings2 = service.getSettings()
        assertEquals("test1", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testLazyGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, lazyLoad { cacheRefreshIntervalSeconds = 2 })

        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        Utils.delayWithBlock(3_000)

        val settings2 = service.getSettings()
        assertEquals("test2", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testLazyGetFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, lazyLoad { cacheRefreshIntervalSeconds = 2 })

        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        Utils.delayWithBlock(3_000)

        val settings2 = service.getSettings()
        assertEquals("test1", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testManualPollGet() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, manualPoll())

        service.refresh()
        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        service.refresh()
        val settings2 = service.getSettings()
        assertEquals("test2", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testManualPollFailed() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = "", status = HttpStatusCode.BadGateway)
            }
        } as MockEngine
        val service = Utils.createService(mockEngine, manualPoll())

        service.refresh()
        val settings1 = service.getSettings()
        assertEquals("test1", settings1["fakeKey"]?.value)

        service.refresh()
        val settings2 = service.getSettings()
        assertEquals("test1", settings2["fakeKey"]?.value)

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testAutoPollInitWaitTimeTimeout() = runTest {
        val mockEngine = MockEngine { _ ->
            delay(5000)
            respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
        }
        val service = Utils.createService(
            mockEngine,
            autoPoll {
                pollingIntervalSeconds = 60
                maxInitWaitTimeSeconds = 1
            }
        )

        val start = DateTime.now()
        val settings1 = service.getSettings()
        assertNull(settings1["fakeKey"]?.value)
        val elapsed = DateTime.now() - start
        assertTrue(elapsed.seconds > 1)
        assertTrue(elapsed.seconds < 2)

        service.close()
    }

    @Test
    fun testAutoPollCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val cache = InMemoryCache()
        val service = Utils.createService(mockEngine, autoPoll { pollingIntervalSeconds = 2 }, cache)

        Utils.delayWithBlock(1_000)
        assertEquals(Utils.formatJsonBody("test1"), cache.store.values.first())

        Utils.delayWithBlock(2_000)
        assertEquals(Utils.formatJsonBody("test2"), cache.store.values.first())

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testLazyCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val cache = InMemoryCache()
        val service = Utils.createService(mockEngine, lazyLoad { cacheRefreshIntervalSeconds = 2 }, cache)

        service.getSettings()
        assertEquals(Utils.formatJsonBody("test1"), cache.store.values.first())

        Utils.delayWithBlock(3_000)
        service.getSettings()
        assertEquals(Utils.formatJsonBody("test2"), cache.store.values.first())

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testManualCacheWrite() = runTest {
        val mockEngine = MockEngine.create {
            this.addHandler {
                respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
            }
            this.addHandler {
                respond(content = Utils.formatJsonBody("test2"), status = HttpStatusCode.OK)
            }
        } as MockEngine
        val cache = InMemoryCache()
        val service = Utils.createService(mockEngine, manualPoll(), cache)

        service.refresh()
        assertEquals(Utils.formatJsonBody("test1"), cache.store.values.first())

        service.refresh()
        assertEquals(Utils.formatJsonBody("test2"), cache.store.values.first())

        assertEquals(2, mockEngine.requestHistory.size)

        service.close()
    }

    @Test
    fun testEnsureManualNotInitiatesHTTP() = runTest {
        val mockEngine = MockEngine { _ ->
            respond(content = Utils.formatJsonBody("test1"), status = HttpStatusCode.OK)
        }
        val service = Utils.createService(mockEngine, manualPoll())

        val settings1 = service.getSettings()
        assertNull(settings1["fakeKey"]?.value)

        assertEquals(0, mockEngine.requestHistory.size)

        service.close()
    }
}
