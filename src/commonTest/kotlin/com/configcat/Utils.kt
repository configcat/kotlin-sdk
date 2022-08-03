package com.configcat

import com.configcat.fetch.ConfigFetcher
import com.configcat.fetch.ConfigService
import com.configcat.log.InternalLogger
import io.ktor.client.engine.mock.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext

internal object Utils {
    suspend fun delayWithBlock(ms: Long) {
        withContext(Dispatchers.Default) {
            delay(ms)
        }
    }

    fun formatJsonBody(value: Any): String {
        return """{ "f": { "fakeKey": { "v": $value, "p": [], "r": [] } } }"""
    }
}

internal object Services {
    private val closeables: MutableList<Closeable> = mutableListOf()

    fun createFetcher(engine: MockEngine, customUrl: String? = null, options: ClientOptions? = null): ConfigFetcher {
        val opts = options ?: ClientOptions()
        opts.httpEngine = engine
        opts.baseUrl = customUrl
        val fetcher = ConfigFetcher(opts, InternalLogger(opts.logger, opts.logLevel))
        closeables.add(fetcher)
        return fetcher
    }

    fun createConfigService(
        engine: MockEngine,
        mode: PollingMode = autoPoll(),
        cache: ConfigCache = EmptyConfigCache()
    ): ConfigService {
        val options = ClientOptions()
        options.pollingMode = mode
        options.configCache = cache
        val service = ConfigService(
            options,
            createFetcher(engine, options = options),
            InternalLogger(options.logger, options.logLevel)
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
