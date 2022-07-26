package com.configcat.client

/**
 * A cache API used to make custom cache implementations.
 */
public interface ConfigCache {
    /**
     * Gets the actual value from the cache identified by the given [key].
     */
    public suspend fun read(key: String): String

    /**
     * Writes the given [value] to the cache by the given [key].
     */
    public suspend fun write(key: String, value: String)
}

internal class EmptyConfigCache : ConfigCache {
    override suspend fun read(key: String): String = ""
    override suspend fun write(key: String, value: String) {}
}
