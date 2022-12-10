pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.library" -> useModule("com.android.tools.build:gradle:7.2.2")
            }
        }
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "configcat-kotlin-client"
