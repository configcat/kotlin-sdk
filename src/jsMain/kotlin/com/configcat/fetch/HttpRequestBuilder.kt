package com.configcat.fetch

import io.ktor.client.request.HttpRequestBuilder

internal actual fun httpRequestBuilder(
    configCatUserAgent: String,
    eTag: String,
): HttpRequestBuilder {
    val httpRequestBuilder = HttpRequestBuilder()
    httpRequestBuilder.url.parameters.append("sdk", configCatUserAgent)
    if (eTag.isNotEmpty()) httpRequestBuilder.url.parameters.append("ccetag", eTag)

    return httpRequestBuilder
}
