package com.configcat

import android.content.Context
import android.content.SharedPreferences

internal actual fun defaultCache(): ConfigCache {
    return EmptyConfigCache()
}

/**
 * [ConfigCache] implementation that uses [SharedPreferences] as persistent storage.
 */
public class SharedPreferencesCache(context: Context) : ConfigCache {
    private val sharedPreferences: SharedPreferences =
        context.applicationContext.getSharedPreferences("configcat_preferences", Context.MODE_PRIVATE)

    override suspend fun read(key: String): String? = sharedPreferences.getString(key, null)

    override suspend fun write(
        key: String,
        value: String,
    ) {
        sharedPreferences.edit().putString(key, value).apply()
    }
}
