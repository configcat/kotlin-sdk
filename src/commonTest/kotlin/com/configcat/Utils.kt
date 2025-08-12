package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.log.InternalLogger
import com.configcat.model.Condition
import com.configcat.model.Config
import com.configcat.model.Preferences
import com.configcat.model.ServedValue
import com.configcat.model.Setting
import com.configcat.model.SettingValue
import com.configcat.model.TargetingRule
import com.configcat.model.UserCondition
import io.ktor.client.engine.mock.MockEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlin.time.TimeSource

internal object TestUtils {
    private val allowedSdkKeyChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')

    suspend fun awaitUntil(
        timeout: Duration = 5.seconds,
        condTarget: suspend () -> Boolean,
    ): Long {
        val ts = TimeSource.Monotonic
        val start = ts.markNow()
        withContext(Dispatchers.Default) {
            while (!condTarget()) {
                delay(200)
                val elapsed = ts.markNow() - start
                if (elapsed.inWholeMilliseconds > timeout.inWholeMilliseconds) {
                    throw Exception("Test await timed out.")
                }
            }
        }
        return (ts.markNow() - start).inWholeMilliseconds
    }

    suspend fun wait(timeout: Duration) {
        withContext(Dispatchers.Default) {
            delay(timeout)
        }
    }

    fun randomSdkKey(): String = "${randomString(22)}/${randomString(22)}"

    private fun randomString(length: Int): String =
        (1..length)
            .map { allowedSdkKeyChars.random() }
            .joinToString("")
}

internal object Data {
    const val MULTIPLE_BODY =
        """{ "p": {"u": "https://cdn-global.configcat.com", "s": "test-salt" }, 
            "f": { "key1": { "t": 0, "v": { "b": true}, "i": "fakeId1", "p": [], "r": [], "a" : ""}, 
            "key2": { "t": 0, "v": {"b": false}, "i": "fakeId2", "p": [], "r": [], "a":"" }}, "s": [] }"""

    fun formatJsonBodyWithString(value: String): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, 
            "f": { "fakeKey": { "t": 1, "v": { "s": "$value" }, "p": [], "r": [], "a":"" }}, "s": [] }
            """.trimMargin()
    }

    fun formatJsonBodyWithBoolean(value: Boolean): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, 
            "f": { "fakeKey": { "t": 0, "v": { "b": $value }, "p": [], "r": [], "a":"" }}, "s": [] }
            """.trimMargin()
    }

    fun formatJsonBodyWithInt(value: Int): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, 
            "f": { "fakeKey": { "t": 2, "v": { "i": $value }, "p": [], "r": [], "a":"" }}, "s": [] }
            """.trimMargin()
    }

    fun formatJsonBodyWithDouble(value: Double): String {
        return """{  "p": { "u": "https://cdn-global.configcat.com", "s": "test-salt" }, 
            "f": { "fakeKey": { "t": 3, "v": { "d": $value }, "p": [], "r": [], "a":"" }}, "s": [] }
            """.trimMargin()
    }

    fun formatConfigWithRules(): String {
        val config =
            Config(
                Preferences("https://cdn-global.configcat.com", 0, "test-salt"),
                mapOf(
                    "key" to
                        Setting(
                            type = 1,
                            percentageAttribute = "",
                            percentageOptions = null,
                            targetingRules =
                                arrayOf<TargetingRule>(
                                    TargetingRule(
                                        conditions =
                                            arrayOf<Condition>(
                                                Condition(
                                                    UserCondition(
                                                        comparator = 2,
                                                        comparisonAttribute = "Identifier",
                                                        stringValue = null,
                                                        doubleValue = null,
                                                        stringArrayValue = arrayOf("@test1.com"),
                                                    ),
                                                    null,
                                                    null,
                                                ),
                                            ),
                                        emptyArray(),
                                        servedValue =
                                            ServedValue(
                                                value = SettingValue(stringValue = "fake1"),
                                                variationId = "fakeId1",
                                            ),
                                    ),
                                    TargetingRule(
                                        conditions =
                                            arrayOf<Condition>(
                                                Condition(
                                                    UserCondition(
                                                        comparator = 2,
                                                        comparisonAttribute = "Identifier",
                                                        stringValue = null,
                                                        doubleValue = null,
                                                        stringArrayValue = arrayOf("@test2.com"),
                                                    ),
                                                    null,
                                                    null,
                                                ),
                                            ),
                                        emptyArray(),
                                        servedValue =
                                            ServedValue(
                                                value = SettingValue(stringValue = "fake2"),
                                                variationId = "fakeId2",
                                            ),
                                    ),
                                ),
                            settingValue = SettingValue(stringValue = "default"),
                            variationId = "defaultId",
                        ),
                ),
                arrayOf(),
            )
        return Constants.json.encodeToString(config)
    }

    fun formatCacheEntry(value: Any): String {
        val fetchTimeUnixSeconds = Clock.System.now().toEpochMilliseconds()
        return "${fetchTimeUnixSeconds}\n$value\n" +
            """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"},
            |"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}
            """.trimMargin()
    }

    fun formatCacheEntryWithETag(
        value: Any,
        eTag: String,
    ): String {
        val fetchTimeUnixSeconds = Clock.System.now().toEpochMilliseconds()
        return "${fetchTimeUnixSeconds}\n$eTag\n" +
            """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"},
            |"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}
            """.trimMargin()
    }

    fun formatCacheEntryWithDate(
        value: Any,
        time: Instant,
    ): String {
        val fetchTimeUnixSeconds = time.toEpochMilliseconds()
        return "${fetchTimeUnixSeconds}\n$value\n" +
            """{"p":{"u":"https://cdn-global.configcat.com","r":"0","s": "test-slat"},
            |"f":{"fakeKey":{"v":{"s":"$value"},"t":1,"p":[],"r":[], "a":""}}, "s":[]}
            """.trimMargin()
    }
}

internal object Services {
    private val closeables: MutableList<Closeable> = mutableListOf()

    fun createFetcher(
        engine: MockEngine,
        customUrl: String? = null,
        options: ConfigCatOptions? = null,
    ): ConfigFetcher {
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
        options: ConfigCatOptions = ConfigCatOptions(),
    ): ConfigService {
        options.pollingMode = mode
        options.configCache = cache
        options.hooks = hooks
        options.offline = offline
        val logger = InternalLogger(options.logger, options.logLevel, hooks)
        val service =
            ConfigService(
                options,
                SnapshotBuilder(FlagEvaluator(
                    logger, Evaluator(logger),
                    hooks = hooks
                ), null, logger, null),
                createFetcher(engine, options = options),
        logger,
                hooks,
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
