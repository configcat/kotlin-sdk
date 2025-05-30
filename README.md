# ConfigCat SDK for Kotlin Multiplatform
[![Kotlin CI](https://github.com/configcat/kotlin-sdk/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/configcat/kotlin-sdk/actions/workflows/ci.yml)
[![Maven Central](https://img.shields.io/maven-central/v/com.configcat/configcat-kotlin-client?label=maven%20central)](https://search.maven.org/artifact/com.configcat/configcat-kotlin-client/)
[![Quality Gate Status](https://img.shields.io/sonar/quality_gate/configcat_kotlin-sdk?logo=SonarCloud&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/project/overview?id=configcat_kotlin-sdk)
[![SonarCloud Coverage](https://img.shields.io/sonar/coverage/configcat_kotlin-sdk?logo=SonarCloud&server=https%3A%2F%2Fsonarcloud.io)](https://sonarcloud.io/project/overview?id=configcat_kotlin-sdk)
[![Kotlin](https://img.shields.io/badge/kotlin-2.1-blueviolet.svg?logo=kotlin)](http://kotlinlang.org)

ConfigCat SDK for Kotlin Multiplatform provides easy integration for your application to [ConfigCat](https://configcat.com).

## Getting started

### 1. Install the ConfigCat SDK
```kotlin
val configcatVersion: String by project

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("com.configcat:configcat-kotlin-client:$configcatVersion")
            }
        }
    }
}
```

### 2. Go to the <a href="https://app.configcat.com/sdkkey" target="_blank">ConfigCat Dashboard</a> to get your *SDK Key*:
![SDK-KEY](https://raw.githubusercontent.com/ConfigCat/java-sdk/master/media/readme02-3.png  "SDK-KEY")

### 3. Import *com.configcat.** in your application code
```kotlin
import com.configcat.*
```

### 4. Create a *ConfigCat* client instance
```kotlin
import com.configcat.*

suspend fun main() {
    val client = ConfigCatClient("#YOUR-SDK-KEY#")
}
```

### 5. Get your setting value
```kotlin
import com.configcat.*

suspend fun main() {
    val client = ConfigCatClient("#YOUR-SDK-KEY#")
    val isMyAwesomeFeatureEnabled = client.getValue("isMyAwesomeFeatureEnabled", false)
    if (isMyAwesomeFeatureEnabled) {
        doTheNewThing()
    } else {
        doTheOldThing()
    }
}
```

### 6. Close the client on application exit
```kotlin
client.close()
```

## Getting user-specific setting values with Targeting
Using this feature, you will be able to get different setting values for different users in your application by passing a `User Object` to the `getValue()` function.

Read more about Targeting [here](https://configcat.com/docs/advanced/targeting/).

## User Object
Percentage and targeted rollouts are calculated by the user object passed to the configuration requests.
The user object must be created with a **mandatory** identifier parameter which uniquely identifies each user:
```kotlin
import com.configcat.*

suspend fun main() {
    val client = ConfigCatClient("#YOUR-SDK-KEY#")
    
    val user = ConfigCatUser("#USER-IDENTIFIER#")
    val isMyAwesomeFeatureEnabled = client.getValue("isMyAwesomeFeatureEnabled", false, user)
    if (isMyAwesomeFeatureEnabled) {
        doTheNewThing()
    } else {
        doTheOldThing()
    }
}
```

## Sample / Demo app
* [Kotlin Multiplatform Mobile app](https://github.com/configcat/kotlin-sdk/tree/main/samples/kmm)
* [Android app](https://github.com/configcat/kotlin-sdk/tree/main/samples/android)
* [Kotlin app](https://github.com/configcat/kotlin-sdk/tree/main/samples/kotlin)
* [React app](https://github.com/configcat/kotlin-sdk/tree/main/samples/js)
* [Node.js app](https://github.com/configcat/kotlin-sdk/tree/main/samples/node-js)

## Polling Modes
The ConfigCat SDK supports three different polling mechanisms to acquire the setting values from ConfigCat. After the latest setting values are downloaded, they are stored in an internal cache . After that, all requests are served from the cache. Read more about Polling Modes and how to use them at [ConfigCat Kotlin Docs](https://configcat.com/docs/sdk-reference/kotlin/).

## Support
If you need help using this SDK, feel free to contact the ConfigCat Staff at [https://configcat.com](https://configcat.com). We're happy to help.

## Contributing
Contributions are welcome. For more info please read the [Contribution Guideline](CONTRIBUTING.md).

## About ConfigCat
ConfigCat is a feature flag and configuration management service that lets you separate releases from deployments. You can turn your features ON/OFF using <a href="https://app.configcat.com" target="_blank">ConfigCat Dashboard</a> even after they are deployed. ConfigCat lets you target specific groups of users based on region, email or any other custom user attribute.

ConfigCat is a <a href="https://configcat.com" target="_blank">hosted feature flag service</a>. Manage feature toggles across frontend, backend, mobile, desktop apps. <a href="https://configcat.com" target="_blank">Alternative to LaunchDarkly</a>. Management app + feature flag SDKs.

- [Official ConfigCat SDKs for other platforms](https://github.com/configcat)
- [Documentation](https://configcat.com/docs)
- [Blog](https://configcat.com/blog)