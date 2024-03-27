package com.configcat.fetch

import com.configcat.*
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Config
import com.configcat.model.Entry
import com.soywiz.klock.DateTime
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update

internal class ConfigFetcher constructor(
    private val options: ConfigCatOptions,
    private val logger: InternalLogger
) : Closeable {
    private val httpClient = createClient()
    private val closed = atomic(false)
    private val isUrlCustom = options.isBaseURLCustom()
    private val baseUrl = atomic(
        options.baseUrl?.let { it.ifEmpty { null } }
            ?: if (options.dataGovernance == DataGovernance.GLOBAL) {
                Constants.globalCdnUrl
            } else {
                Constants.euCdnUrl
            }
    )

    suspend fun fetch(eTag: String): FetchResponse {
        val currentUrl = baseUrl.value
        val response = fetchHTTPWithPreferenceHandling(currentUrl, eTag)
        val newUrl = response.entry.config.preferences?.baseUrl ?: currentUrl
        baseUrl.update { newUrl }
        return response
    }

    override fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        httpClient.close()
    }

    private suspend fun fetchHTTPWithPreferenceHandling(baseUrl: String, eTag: String): FetchResponse {
        var currentBaseUrl = baseUrl
        repeat(3) {
            val response = fetchHTTP(currentBaseUrl, eTag)
            val preferences = response.entry.config.preferences
            if (!response.isFetched ||
                response.entry.isEmpty() ||
                preferences == null ||
                preferences.baseUrl == currentBaseUrl
            ) {
                return response
            }
            if (isUrlCustom && preferences.redirect != RedirectMode.FORCE_REDIRECT.ordinal) return response
            currentBaseUrl = preferences.baseUrl
            if (preferences.redirect == RedirectMode.NO_REDIRECT.ordinal) {
                return response
            } else if (preferences.redirect == RedirectMode.SHOULD_REDIRECT.ordinal) {
                logger.warning(3002, ConfigCatLogMessages.DATA_GOVERNANCE_IS_OUT_OF_SYNC_WARN)
            }
        }
        val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_REDIRECT_LOOP_ERROR
        logger.error(1104, message)
        return FetchResponse.failure(message, true)
    }

    private suspend fun fetchHTTP(baseUrl: String, eTag: String): FetchResponse {
        val url = "$baseUrl/configuration-files/${options.sdkKey}/${Constants.configFileName}"
        try {
            val httpRequestBuilder =
                httpRequestBuilder("ConfigCat-Kotlin/${options.pollingMode.identifier}-${Constants.version}", eTag)
            val response = httpClient.get(url) {
                httpRequestBuilder.headers.entries().forEach {
                    headers.appendAll(it.key, it.value)
                }
                url {
                    httpRequestBuilder.url.parameters.entries().forEach {
                        parameters.appendAll(it.key, it.value)
                    }
                }
            }
            if (response.status.value in 200..299) {
                logger.debug("Fetch was successful: new config fetched.")
                val body = response.bodyAsText()
                val newETag = response.etag()
                val (config, err) = deserializeConfig(body)
                if (err != null) {
                    return FetchResponse.failure(err, true)
                }
                val entry = Entry(config, newETag ?: "", body, DateTime.now())
                return FetchResponse.success(entry)
            } else if (response.status == HttpStatusCode.NotModified) {
                logger.debug("Fetch was successful: config not modified.")
                return FetchResponse.notModified()
            } else if (response.status == HttpStatusCode.NotFound || response.status == HttpStatusCode.Forbidden) {
                val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_INVALID_SDK_KEY_ERROR +
                    " Received response: ${response.status}"
                logger.error(1100, message)
                return FetchResponse.failure(message, false)
            } else {
                val message = ConfigCatLogMessages.getFetchFailedDueToUnexpectedHttpResponse(
                    response.status.value,
                    response.bodyAsText()
                )
                logger.error(1101, message)
                return FetchResponse.failure(message, true)
            }
        } catch (_: HttpRequestTimeoutException) {
            val message = ConfigCatLogMessages.getFetchFailedDueToRequestTimeout(
                options.requestTimeout.inWholeMilliseconds,
                options.requestTimeout.inWholeMilliseconds,
                options.requestTimeout.inWholeMilliseconds
            )
            logger.error(1102, message)
            return FetchResponse.failure(message, true)
        } catch (e: Exception) {
            val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_UNEXPECTED_ERROR
            logger.error(1103, message, e)
            return FetchResponse.failure(message, true)
        }
    }

    private fun createClient(): HttpClient =
        options.httpEngine?.let { HttpClient(it) { configureClient(this) } }
            ?: HttpClient { configureClient(this) }

    private fun configureClient(block: HttpClientConfig<*>) {
        block.install(HttpTimeout) {
            requestTimeoutMillis = options.requestTimeout.inWholeMilliseconds
        }
        block.engine {
            proxy = options.httpProxy
        }
    }

    private fun deserializeConfig(jsonString: String): Pair<Config, String?> {
        return try {
            Pair(Helpers.parseConfigJson(jsonString), null)
        } catch (e: Exception) {
            logger.error(1105, ConfigCatLogMessages.FETCH_RECEIVED_200_WITH_INVALID_BODY_ERROR, e)
            Pair(Config.empty, e.message)
        }
    }
}

internal expect fun httpRequestBuilder(configCatUserAgent: String, eTag: String): HttpRequestBuilder

internal fun commonHttpRequestBuilder(configCatUserAgent: String, eTag: String): HttpRequestBuilder {
    val httpRequestBuilder = HttpRequestBuilder()
    httpRequestBuilder.headers {
        append(
            "X-ConfigCat-UserAgent",
            configCatUserAgent
        )
        if (eTag.isNotEmpty()) append(HttpHeaders.IfNoneMatch, eTag)
    }
    return httpRequestBuilder
}
