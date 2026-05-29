package com.configcat.fetch

import com.configcat.Closeable
import com.configcat.ConfigCatOptions
import com.configcat.Constants
import com.configcat.DataGovernance
import com.configcat.log.ConfigCatLogMessages
import com.configcat.log.InternalLogger
import com.configcat.model.Config
import com.configcat.model.Entry
import com.configcat.parseConfigJson
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
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicReference
import kotlin.time.Clock

internal class ConfigFetcher(
    private val options: ConfigCatOptions,
    private val logger: InternalLogger,
) : Closeable {
    private val httpClient = createClient()
    private val closed = AtomicBoolean(false)
    private val isUrlCustom = options.isBaseURLCustom()
    private val baseUrl =
        AtomicReference(
            options.baseUrl?.let { it.ifEmpty { null } }
                ?: if (options.dataGovernance == DataGovernance.GLOBAL) {
                    Constants.GLOBAL_CDN_URL
                } else {
                    Constants.EU_CDN_URL
                },
        )

    suspend fun fetch(eTag: String): FetchResponse = fetchHTTPWithPreferenceHandling(eTag)

    override fun close() {
        if (!closed.compareAndSet(expectedValue = false, newValue = true)) return
        httpClient.close()
    }

    private suspend fun fetchHTTPWithPreferenceHandling(eTag: String): FetchResponse {
        var cfRayId: String? = null
        repeat(3) {
            val response = fetchHTTP(baseUrl.load(), eTag)
            val preferences = response.entry.config.preferences
            if (!response.isFetched ||
                response.entry.isEmpty() ||
                preferences == null ||
                preferences.baseUrl == baseUrl.load()
            ) {
                return response
            }
            if (isUrlCustom && preferences.redirect != RedirectMode.FORCE_REDIRECT.ordinal) {
                return response
            }

            baseUrl.store(preferences.baseUrl)

            if (preferences.redirect == RedirectMode.NO_REDIRECT.ordinal) {
                return response
            } else if (preferences.redirect == RedirectMode.SHOULD_REDIRECT.ordinal) {
                logger.warning(3002, ConfigCatLogMessages.DATA_GOVERNANCE_IS_OUT_OF_SYNC_WARN)
            }
            cfRayId = response.cfRayId
        }
        val message = ConfigCatLogMessages.FETCH_FAILED_DUE_TO_REDIRECT_LOOP_ERROR
        logger.error(1104, message)
        return FetchResponse.failure(message, RefreshErrorCode.UNEXPECTED_ERROR, true, cfRayId = cfRayId)
    }

    private suspend fun fetchHTTP(
        baseUrl: String,
        eTag: String,
    ): FetchResponse {
        val url = "$baseUrl/configuration-files/${options.sdkKey}/${Constants.CONFIG_FILE_NAME}"
        var cfRayId: String? = null
        var fetchResponse: FetchResponse? = null
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

            val newETag = response.etag()
            val responseCode = response.status
            cfRayId = response.headers["CF-RAY"]

            if (responseCode == HttpStatusCode.OK) {
                val body = response.bodyAsText()

                logger.debug("Fetch was successful: new config fetched.")

                val (config, err) = deserializeConfig(body, cfRayId)
                if (err != null) {
                    fetchResponse =
                        FetchResponse.failure(
                            err.message ?: "",
                            RefreshErrorCode.INVALID_HTTP_RESPONSE_CONTENT,
                            true,
                            err,
                            cfRayId,
                        )
                } else {
                    val entry = Entry(config, newETag ?: "", body, Clock.System.now())
                    fetchResponse = FetchResponse.success(entry, cfRayId)
                }
            } else if (responseCode == HttpStatusCode.NotModified) {
                logger.debug("Fetch was successful: config not modified.")
                fetchResponse = FetchResponse.notModified(cfRayId)
            } else if (responseCode == HttpStatusCode.NotFound || responseCode == HttpStatusCode.Forbidden) {
                val message =
                    ConfigCatLogMessages.getFetchFailDueToInvalidSdkKeyError(cfRayId)
                logger.error(1100, message)
                fetchResponse =
                    FetchResponse.failure(
                        message,
                        RefreshErrorCode.INVALID_SDK_KEY,
                        false,
                        cfRayId = cfRayId,
                    )
            } else {
                val message =
                    ConfigCatLogMessages.getFetchFailedDueToUnexpectedHttpResponse(
                        responseCode.value,
                        response.bodyAsText(),
                        cfRayId,
                    )
                logger.error(1101, message)
                fetchResponse =
                    FetchResponse.failure(
                        message,
                        RefreshErrorCode.UNEXPECTED_HTTP_RESPONSE,
                        true,
                        cfRayId = cfRayId,
                    )
            }
        } catch (e: HttpRequestTimeoutException) {
            val message =
                ConfigCatLogMessages.getFetchFailedDueToRequestTimeout(options.requestTimeout, cfRayId)
            logger.error(1102, message)
            fetchResponse = FetchResponse.failure(message, RefreshErrorCode.HTTP_REQUEST_TIMEOUT, true, e, cfRayId)
        } catch (e: Exception) {
            val message = ConfigCatLogMessages.getFetchFailedDueToRequestTimeout(cfRayId)
            logger.error(1103, message, e)
            fetchResponse = FetchResponse.failure(message, RefreshErrorCode.HTTP_REQUEST_FAILURE, true, e, cfRayId)
        } finally {
            if (fetchResponse == null) {
                val message = ConfigCatLogMessages.getFetchFailedDueToRequestTimeout(cfRayId)
                fetchResponse =
                    FetchResponse.failure(
                        message,
                        RefreshErrorCode.HTTP_REQUEST_FAILURE,
                        true,
                        cfRayId = cfRayId,
                    )
            }
        }
        return fetchResponse
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

    private fun deserializeConfig(
        jsonString: String,
        cfRayId: String?,
    ): Pair<Config, Exception?> =
        try {
            Pair(jsonString.parseConfigJson(), null)
        } catch (e: Exception) {
            logger.error(1105, ConfigCatLogMessages.getFetchReceived200WithInvalidBodyError(cfRayId), e)
            Pair(Config.empty, e)
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
