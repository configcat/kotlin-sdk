package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.log.InternalLogger
import com.soywiz.klock.DateTime
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

internal object TestUtils {
    suspend fun awaitUntil(timeout: Duration = 5.seconds, condTarget: suspend () -> Boolean): Long {
        val start = DateTime.now()
        withContext(Dispatchers.Default) {
            while (!condTarget()) {
                delay(200)
                val elapsed = DateTime.now() - start
                if (elapsed.milliseconds > timeout.inWholeMilliseconds) {
                    throw Exception("Test await timed out.")
                }
            }
        }
        return (DateTime.now() - start).milliseconds.toLong()
    }

    suspend fun wait(timeout: Duration) {
        withContext(Dispatchers.Default) {
            delay(timeout)
        }
    }
}

internal object Data {
    fun formatJsonBody(value: Any): String {
        return """{ "f": { "fakeKey": { "v": $value, "p": [], "r": [] } } }"""
    }

    fun formatConfigWithRules(): String {
        val config = Config(
            null,
            mapOf(
                "key" to Setting(
                    value = "default",
                    variationId = "defaultId",
                    rolloutRules = listOf(
                        RolloutRule(
                            comparator = 2,
                            comparisonAttribute = "Identifier",
                            comparisonValue = "@test1.com",
                            value = "fake1",
                            variationId = "fakeId1"
                        ),
                        RolloutRule(
                            comparator = 2,
                            comparisonAttribute = "Identifier",
                            comparisonValue = "@test2.com",
                            value = "fake2",
                            variationId = "fakeId2"
                        )
                    )
                )
            )
        )
        return Constants.json.encodeToString(config)
    }

    fun formatCacheEntry(value: Any): String {
        val fetchTimeUnixSeconds = DateTime.now().unixMillis / 1000
        return "${fetchTimeUnixSeconds}\n$value\n" + """{"f":{"fakeKey":{"v":$value}}}"""
    }

    fun formatCacheEntryWithDate(value: Any, time: DateTime): String {
        val fetchTimeUnixSeconds = time.unixMillis / 1000
        return "${fetchTimeUnixSeconds}\n$value\n" + """{"f":{"fakeKey":{"v":$value}}}"""
    }
}

internal object Services {
    private val closeables: MutableList<Closeable> = mutableListOf()

    fun createFetcher(engine: MockEngine, customUrl: String? = null, options: ConfigCatOptions? = null): ConfigFetcher {
        val opts = options ?: ConfigCatOptions()
        opts.httpEngine = engine
        opts.baseUrl = customUrl
        val fetcher = ConfigFetcher(opts, InternalLogger(opts.logger, opts.logLevel, opts.hooks))
        closeables.add(fetcher)
        return fetcher
    }

    fun createConfigService(
        engine: MockEngine,
        mode: PollingMode = autoPoll(),
        cache: ConfigCache? = EmptyConfigCache(),
        hooks: Hooks = Hooks(),
        offline: Boolean = false
    ): ConfigService {
        val options = ConfigCatOptions()
        options.pollingMode = mode
        options.configCache = cache
        options.hooks = hooks
        options.offline = offline
        val service = ConfigService(
            options,
            createFetcher(engine, options = options),
            InternalLogger(options.logger, options.logLevel, hooks),
            hooks
        )
        closeables.add(service)
        return service
    }

    fun reset() {
        for (closeable in closeables) {
            closeable.close()
        }
    }
}
