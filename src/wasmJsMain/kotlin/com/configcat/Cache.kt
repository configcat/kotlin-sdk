package com.configcat

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

internal fun isBrowser(): Boolean = js("typeof window !== \"undefined\" && typeof window.document !== \"undefined\"")

internal actual fun defaultCache(): ConfigCache =
    when {
        isBrowser() == true -> LocalStorageCache()
        else -> EmptyConfigCache()
    }

/**
 * [ConfigCache] implementation that uses [localStorage] as persistent storage.
 */
public class LocalStorageCache : ConfigCache {
    override suspend fun read(key: String): String? = localStorage[key]

    override suspend fun write(
        key: String,
        value: String,
    ) {
        localStorage[key] = value
    }
}
