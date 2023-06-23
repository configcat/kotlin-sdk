plugins {
    kotlin("js") version "1.7.10"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url = uri("https://oss.sonatype.org/content/repositories/snapshots")
    }
}

dependencies {
    implementation("com.configcat:configcat-kotlin-client:2.0.0")
}

kotlin {
    js(IR) {
        binaries.executable()
        nodejs {

        }
    }
}