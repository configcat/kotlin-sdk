import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue
import com.configcat.log.LogLevel

suspend fun main(args: Array<String>) {
    val client = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/HhOWfwVtZ0mb30i9wi17GQ") {
        // Info level logging helps to inspect the feature flag evaluation process.
        // Use the default Warning level to avoid too detailed logging in your application.
        logLevel = LogLevel.INFO
    }

    // Get individual config values identified by a key for a user.
    println("isAwesomeFeatureEnabled: ${client.getValue("isAwesomeFeatureEnabled", defaultValue = false)}")

    // Create a user object to identify the caller.
    val user = ConfigCatUser(identifier = "configcat@example.com", email = "configcat@example.com")
    val isPOCFeatureEnabled = client.getValue(key = "isPOCFeatureEnabled", defaultValue = false, user = user)
    println("isPOCFeatureEnabled: $isPOCFeatureEnabled")

    client.close()
}