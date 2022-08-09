package com.configcat.fetch

import com.configcat.*
import com.configcat.Closeable
import com.configcat.Constants
import com.configcat.log.InternalLogger
import com.soywiz.klock.DateTime
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.atomicfu.*

internal class ConfigFetcher constructor(
    private val options: ClientOptions,
    private val logger: InternalLogger,
) : Closeable {
    private val httpClient = createClient()
    private val closed = atomic(false)
    private val isUrlCustom = !options.baseUrl.isNullOrEmpty()
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
            ) return response
            if (isUrlCustom && preferences.redirect != RedirectMode.FORCE_REDIRECT.ordinal) return response
            currentBaseUrl = preferences.baseUrl
            if (preferences.redirect == RedirectMode.NO_REDIRECT.ordinal) return response
            else if (preferences.redirect == RedirectMode.SHOULD_REDIRECT.ordinal) {
                logger.warning(
                    "Your \'dataGovernance\' parameter at ConfigCatClient initialization is " +
                            "not in sync with your preferences on the ConfigCat Dashboard: " +
                            "https://app.configcat.com/organization/data-governance. " +
                            "Only Organization Admins can access this preference."
                )
            }
        }
        logger.error("Redirect loop during config.json fetch. Please contact support@configcat.com.")
        return FetchResponse.failure()
    }

    private suspend fun fetchHTTP(baseUrl: String, eTag: String): FetchResponse {
        val url = "$baseUrl/configuration-files/${options.sdkKey}/${Constants.configFileName}.json"
        try {
            val response = httpClient.get(url) {
                headers {
                    append(
                        "X-ConfigCat-UserAgent",
                        "ConfigCat-Kotlin/${options.pollingMode.identifier}-${Constants.version}"
                    )
                    if (eTag.isNotEmpty()) append(HttpHeaders.IfNoneMatch, eTag)
                }
            }

            if (response.status.value in 200..299) {
                logger.debug("Fetch was successful: new config fetched.")
                val body = response.bodyAsText()
                val newETag = response.etag()
                val (config, error) = body.parseConfigJson()
                if (config.isEmpty()) {
                    logger.error("JSON parsing failed. ${error?.message}")
                    return FetchResponse.failure()
                }
                val entry = Entry(config, body, newETag ?: "", DateTime.now())
                return FetchResponse.success(entry)
            } else if (response.status == HttpStatusCode.NotModified) {
                logger.debug("Fetch was successful: config not modified.")
                return FetchResponse.notModified()
            } else {
                logger.error(
                    "Double-check your API KEY at https://app.configcat.com/apikey. " +
                            "Received unexpected response: ${response.status}"
                )
                return FetchResponse.failure()
            }
        } catch (_: HttpRequestTimeoutException) {
            logger.error("Request timed out. Timeout value: ${options.requestTimeoutMs}ms")
            return FetchResponse.failure()
        } catch (e: Exception) {
            logger.error("Error during config JSON download. ${e.message}")
            return FetchResponse.failure()
        }
    }

    private fun createClient(): HttpClient =
        options.httpEngine?.let { HttpClient(it) { configureClient(this) } }
            ?: HttpClient { configureClient(this) }

    private fun configureClient(block: HttpClientConfig<*>) {
        block.install(HttpTimeout) {
            requestTimeoutMillis = options.requestTimeoutMs
        }
        block.engine {
            proxy = options.httpProxy
        }
    }
}
