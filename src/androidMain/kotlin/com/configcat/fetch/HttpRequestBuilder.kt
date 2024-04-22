package com.configcat.fetch

import io.ktor.client.request.HttpRequestBuilder

internal actual fun httpRequestBuilder(
    configCatUserAgent: String,
    eTag: String,
): HttpRequestBuilder {
    return commonHttpRequestBuilder(configCatUserAgent, eTag)
}
