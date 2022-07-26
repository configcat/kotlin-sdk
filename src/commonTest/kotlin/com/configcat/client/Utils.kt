package com.configcat.client

import com.configcat.client.fetch.ConfigFetcher
import com.configcat.client.fetch.ConfigService
import com.configcat.client.fetch.PollingMode
import com.configcat.client.fetch.autoPoll
import com.configcat.client.logging.InternalLogger
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal object Utils {
    fun createFetcher(engine: MockEngine, customUrl: String? = null, options: ClientOptions? = null): ConfigFetcher {
        val opts = options ?: ClientOptions()
        opts.httpEngine = engine
        opts.baseUrl = customUrl
        return ConfigFetcher(opts, InternalLogger(opts.logger, opts.logLevel))
    }

    fun createService(
        engine: MockEngine,
        mode: PollingMode = autoPoll(),
        cache: ConfigCache = EmptyConfigCache()
    ): ConfigService {
        val options = ClientOptions()
        options.pollingMode = mode
        options.configCache = cache
        return ConfigService(
            options,
            createFetcher(engine, options = options),
            InternalLogger(options.logger, options.logLevel)
        )
    }

    suspend fun delayWithBlock(ms: Long) {
        withContext(Dispatchers.Default) {
            delay(ms)
        }
    }

    fun formatJsonBody(value: Any): String {
        return """{ "f": { "fakeKey": { "v": $value, "p": [], "r": [] } } }"""
    }
}
