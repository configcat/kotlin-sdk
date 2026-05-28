package com.configcat

import kotlin.concurrent.atomics.AtomicBoolean

class InMemoryCache : ConfigCache {
    internal val store: MutableMap<String, String> = mutableMapOf()

    override suspend fun read(key: String): String? {
        return store[key]
    }

    override suspend fun write(
        key: String,
        value: String,
    ) {
        store[key] = value
    }
}

class SingleValueCache(private var value: String) : ConfigCache {
    override suspend fun read(key: String): String = value

    override suspend fun write(
        key: String,
        value: String,
    ) {
        this.value = value
    }
}

class TestStateMonitor : StateMonitor() {
    var isInValidState: AtomicBoolean = AtomicBoolean(true)

    override fun isAllowedToUseHTTP() = isInValidState.load()

    fun notifyListeners() = notifyStateChanged()
}
