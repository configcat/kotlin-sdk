package com.configcat.fetch

import com.configcat.Closeable
import com.configcat.ConfigCatOptions
import com.configcat.Constants
import com.configcat.DataGovernance
import com.configcat.DateTimeUtils.defaultTimeZone
import com.configcat.Helpers
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Config
import com.configcat.model.Entry
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.etag
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.toLocalDateTime

internal class ConfigFetcher(
    private val options: ConfigCatOptions,
    private val logger: InternalLogger,
) : Closeable {
    private val httpClient = createClient()
    private val closed = atomic(false)
    private val isUrlCustom = options.isBaseURLCustom()
    private val baseUrl =
        atomic(
            options.baseUrl?.let { it.ifEmpty { null } }
                ?: if (options.dataGovernance == DataGovernance.GLOBAL) {
                    Constants.GLOBAL_CDN_URL
                } else {
                    Constants.EU_CDN_URL
                },
        )

    suspend fun fetch(eTag: String): FetchResponse {
        return fetchHTTPWithPreferenceHandling(eTag)
    }

    override fun close() {
        if (!closed.compareAndSet(expect = false, update = true)) return
        httpClient.close()
    }

    private suspend fun fetchHTTPWithPreferenceHandling(eTag: String): FetchResponse {
        repeat(3) {
            val response = fetchHTTP(baseUrl.value, eTag)
            val preferences = response.entry.config.preferences
            if (!response.isFetched ||
                response.entry.isEmpty() ||
                preferences == null ||
                preferences.baseUrl == baseUrl.value
            ) {
                return response
            }
            if (isUrlCustom && preferences.redirect != RedirectMode.FORCE_REDIRECT.ordinal) {
                return response
            }

            baseUrl.update { preferences.baseUrl }

            if (preferences.redirect == RedirectMode.NO_REDIRECT.ordinal) {
                return response
            } else if (preferences.redirect == RedirectMode.SHOULD_REDIRECT.ordinal) {
                logger.warning(3002, ConfigCatLogMessages.DATA_GOVERNANCE_IS_OUT_OF_SYNC_WARN)
            }
        }
        val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_REDIRECT_LOOP_ERROR
        logger.error(1104, message)
        return FetchResponse.failure(message, RefreshErrorCode.UNEXPECTED_ERROR, true)
    }

    private suspend fun fetchHTTP(
        baseUrl: String,
        eTag: String,
    ): FetchResponse {
        val url = "$baseUrl/configuration-files/${options.sdkKey}/${Constants.CONFIG_FILE_NAME}"
        try {
            val httpRequestBuilder =
                httpRequestBuilder("ConfigCat-Kotlin/${options.pollingMode.identifier}-${Constants.VERSION}", eTag)
            val response =
                httpClient.get(url) {
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
                    return FetchResponse.failure(
                        err.message ?: "",
                        RefreshErrorCode.INVALID_HTTP_RESPONSE_CONTENT,
                        true,
                        err,
                    )
                }
                val entry = Entry(config, newETag ?: "", body, Clock.System.now().toLocalDateTime(defaultTimeZone))
                return FetchResponse.success(entry)
            } else if (response.status == HttpStatusCode.NotModified) {
                logger.debug("Fetch was successful: config not modified.")
                return FetchResponse.notModified()
            } else if (response.status == HttpStatusCode.NotFound || response.status == HttpStatusCode.Forbidden) {
                val message =
                    ConfigCatLogMessages.FETCH_FAILED_DUE_TO_INVALID_SDK_KEY_ERROR +
                        " Received response: ${response.status}"
                logger.error(1100, message)
                return FetchResponse.failure(message, RefreshErrorCode.INVALID_SDK_KEY, false)
            } else {
                val message =
                    ConfigCatLogMessages.getFetchFailedDueToUnexpectedHttpResponse(
                        response.status.value,
                        response.bodyAsText(),
                    )
                logger.error(1101, message)
                return FetchResponse.failure(message, RefreshErrorCode.UNEXPECTED_HTTP_RESPONSE, true)
            }
        } catch (e: HttpRequestTimeoutException) {
            val message =
                ConfigCatLogMessages.getFetchFailedDueToRequestTimeout(
                    options.requestTimeout.inWholeMilliseconds,
                    options.requestTimeout.inWholeMilliseconds,
                    options.requestTimeout.inWholeMilliseconds,
                )
            logger.error(1102, message)
            return FetchResponse.failure(message, RefreshErrorCode.HTTP_REQUEST_TIMEOUT, true, e)
        } catch (e: Exception) {
            val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_UNEXPECTED_ERROR
            logger.error(1103, message, e)
            return FetchResponse.failure(message, RefreshErrorCode.HTTP_REQUEST_FAILURE, true, e)
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

    private fun deserializeConfig(jsonString: String): Pair<Config, Exception?> {
        return try {
            Pair(Helpers.parseConfigJson(jsonString), null)
        } catch (e: Exception) {
            logger.error(1105, ConfigCatLogMessages.FETCH_RECEIVED_200_WITH_INVALID_BODY_ERROR, e)
            Pair(Config.empty, e)
        }
    }
}

internal expect fun httpRequestBuilder(
    configCatUserAgent: String,
    eTag: String,
): HttpRequestBuilder

internal fun commonHttpRequestBuilder(
    configCatUserAgent: String,
    eTag: String,
): HttpRequestBuilder {
    val httpRequestBuilder = HttpRequestBuilder()
    httpRequestBuilder.headers {
        append(
            "X-ConfigCat-UserAgent",
            configCatUserAgent,
        )
        if (eTag.isNotEmpty()) append(HttpHeaders.IfNoneMatch, eTag)
    }
    return httpRequestBuilder
}
