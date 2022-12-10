package com.configcat

import kotlinx.browser.localStorage
import org.w3c.dom.get
import org.w3c.dom.set

internal actual fun defaultCache(): ConfigCache = LocalStorageCache()

internal class LocalStorageCache: ConfigCache {
    override suspend fun read(key: String): String? = localStorage[key]

    override suspend fun write(key: String, value: String) {
        localStorage[key] = value;
    }

}