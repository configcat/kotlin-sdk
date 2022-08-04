import com.configcat.ConfigCatClient
import com.configcat.ConfigCatUser
import com.configcat.getValue
import com.configcat.log.LogLevel
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul
import react.useEffectOnce
import react.useState

private val scope = MainScope()

val FlagSample = FC<Props> {
    val client = ConfigCatClient("PKDVCLf-Hq-h-kCzMp-L7Q/HhOWfwVtZ0mb30i9wi17GQ") {
        // Info level logging helps to inspect the feature flag evaluation process.
        // Use the default Warning level to avoid too detailed logging in your application.
        logLevel = LogLevel.INFO
    }
    var isAwesomeFeatureEnabled by useState(false)
    var isPOCFeatureEnabled by useState(false)

    useEffectOnce {
        scope.launch {
            // Get individual config values identified by a key for a user.
            isAwesomeFeatureEnabled = client.getValue("isAwesomeFeatureEnabled", defaultValue = false)

            // Create a user object to identify the caller.
            val user = ConfigCatUser(identifier = "configcat@example.com", email = "configcat@example.com")
            isPOCFeatureEnabled = client.getValue(key = "isPOCFeatureEnabled", defaultValue = false, user = user)
        }
    }

    h1 {
        +"ConfigCat Kotlin Multiplatform React Sample"
    }
    ul {
        li {
            +"isAwesomeFeatureEnabled: $isAwesomeFeatureEnabled"
        }
        li {
            +"isPOCFeatureEnabled: $isPOCFeatureEnabled"
        }
    }
}