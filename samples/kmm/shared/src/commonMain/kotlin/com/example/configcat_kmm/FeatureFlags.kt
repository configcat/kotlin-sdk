package com.example.configcat_kmm

import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue
import com.configcat.log.LogLevel

class FeatureFlags {
    private val client: ConfigCatClient = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/HhOWfwVtZ0mb30i9wi17GQ") {
        // Info level logging helps to inspect the feature flag evaluation process.
        // Use the default Warning level to avoid too detailed logging in your application.
        logLevel = LogLevel.INFO
    }

    suspend fun isFeatureEnabled(key: String, email: String): Boolean {
        // Create a user object to identify the caller.
        val user = ConfigCatUser(identifier = email, email = email)
        return client.getValue(key, defaultValue = false, user = user)
    }

    fun close() {
        ConfigCatClient.close()
    }
}