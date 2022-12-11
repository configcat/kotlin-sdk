package com.configcat

import platform.Foundation.NSUserDefaults

internal actual fun defaultCache(): ConfigCache {
    return UserDefaultsCache()
}

internal class UserDefaultsCache : ConfigCache {
    private val userDefaults = NSUserDefaults.standardUserDefaults

    override suspend fun read(key: String): String? = userDefaults.stringForKey("com.configcat-$key")

    override suspend fun write(key: String, value: String) {
        userDefaults.setObject(value, "com.configcat-$key")
    }
}