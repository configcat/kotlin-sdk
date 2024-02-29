package com.configcat

import platform.Foundation.NSUserDefaults

internal actual fun defaultCache(): ConfigCache {
    return UserDefaultsCache()
}

/**
 * [ConfigCache] implementation that uses [NSUserDefaults] as persistent storage.
 */
public class UserDefaultsCache : ConfigCache {
    private val userDefaults = NSUserDefaults.standardUserDefaults
    private val prefix = "com.configcat"

    override suspend fun read(key: String): String? = userDefaults.stringForKey("$prefix-$key")

    override suspend fun write(key: String, value: String) {
        userDefaults.setObject(value, "$prefix-$key")
    }
}
