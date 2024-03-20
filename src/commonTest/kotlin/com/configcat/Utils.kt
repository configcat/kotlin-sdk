package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.log.InternalLogger
import com.configcat.model.*
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
    const val SDK_KEY = "configcat-sdk-1/TEST_KEY-0123456789012/1234567890123456789012"

    const val MULTIPLE_BODY =
        """{ "p": {"u": "https://cdn-global.configcat.com", "s": "test-salt" }, "f": { "key1": { "t": 0, "v": { "b": true}, "i": "fakeId1", "p": [], "r": [], "a" : ""}, "key2": { "t": 0, "v": {"b": false}, "i": "fakeId2", "p": [], "r": [], "a":"" }}, "s": [] }"""

    fun formatJsonBodyWithString(value: String): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, "f": { "fakeKey": { "t": 1, "v": { "s": "$value" }, "p": [], "r": [], "a":"" }}, "s": [] }"""
    }

    fun formatJsonBodyWithBoolean(value: Boolean): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, "f": { "fakeKey": { "t": 0, "v": { "b": $value }, "p": [], "r": [], "a":"" }}, "s": [] }"""
    }

    fun formatJsonBodyWithInt(value: Int): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, "f": { "fakeKey": { "t": 2, "v": { "i": $value }, "p": [], "r": [], "a":"" }}, "s": [] }"""
    }

    fun formatJsonBodyWithDouble(value: Double): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, "f": { "fakeKey": { "t": 3, "v": { "d": $value }, "p": [], "r": [], "a":"" }}, "s": [] }"""
    }

    fun formatConfigWithRules(): String {
        val config = Config(
            Preferences("https://cdn-global.configcat.com", 0, "test-salt"),
            mapOf(
                "key" to Setting(
                    type = 1,
                    percentageAttribute = "",
                    percentageOptions = null,
                    targetingRules = arrayOf<TargetingRule>(
                        TargetingRule(
                            conditions = arrayOf<Condition>(
                                Condition(
                                    UserCondition(
                                        comparator = 2,
                                        comparisonAttribute = "Identifier",
                                        stringValue = null,
                                        doubleValue = null,
                                        stringArrayValue = arrayOf("@test1.com")
                                    ),
                                    null,
                                    null
                                )
                            ),
                            emptyArray(),
                            servedValue = ServedValue(
                                value = SettingValue(stringValue = "fake1"),
                                variationId = "fakeId1"
                            )
                        ),
                        TargetingRule(
                            conditions = arrayOf<Condition>(
                                Condition(
                                    UserCondition(
                                        comparator = 2,
                                        comparisonAttribute = "Identifier",
                                        stringValue = null,
                                        doubleValue = null,
                                        stringArrayValue = arrayOf("@test2.com")
                                    ),
                                    null,
                                    null
                                )
                            ),
                            emptyArray(),
                            servedValue = ServedValue(
                                value = SettingValue(stringValue = "fake2"),
                                variationId = "fakeId2"
                            )
                        )
                    ),
                    settingValue = SettingValue(stringValue = "default"),
                    variationId = "defaultId"
                )
            ),
            arrayOf()
        )
        return Constants.json.encodeToString(config)
    }

    fun formatCacheEntry(value: Any): String {
        val fetchTimeUnixSeconds = DateTime.now().unixMillis.toLong()
        return "${fetchTimeUnixSeconds}\n$value\n" + """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"}"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}"""
    }

    fun formatCacheEntryWithETag(value: Any, eTag: String): String {
        val fetchTimeUnixSeconds = DateTime.now().unixMillis.toLong()
        return "${fetchTimeUnixSeconds}\n$eTag\n" + """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"}"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}"""
    }

    fun formatCacheEntryWithDate(value: Any, time: DateTime): String {
        val fetchTimeUnixSeconds = time.unixMillis.toLong()
        return "${fetchTimeUnixSeconds}\n$value\n" + """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"}"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}"""
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
        offline: Boolean = false,
        options: ConfigCatOptions = ConfigCatOptions()
    ): ConfigService {
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
