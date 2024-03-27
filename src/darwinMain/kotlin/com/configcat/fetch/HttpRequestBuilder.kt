package com.configcat.fetch

import io.ktor.client.request.*

internal actual fun httpRequestBuilder(
    configCatUserAgent: String,
    eTag: String
): HttpRequestBuilder {
    return commonHttpRequestBuilder(configCatUserAgent, eTag)
}
