package com.configcat

import com.configcat.ConfigCache

class InMemoryCache : ConfigCache {
    internal val store: MutableMap<String, String> = mutableMapOf()

    override suspend fun read(key: String): String {
        return store[key] ?: ""
    }

    override suspend fun write(key: String, value: String) {
        store[key] = value
    }
}
