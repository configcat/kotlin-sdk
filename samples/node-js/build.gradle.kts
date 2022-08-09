plugins {
    kotlin("js") version "1.7.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation("com.configcat:configcat-kotlin-client:0.1.0")
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs {

        }
    }
}