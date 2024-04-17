import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.net.URL

buildscript {
    val kotlinVersion by extra("1.9.23")
    val atomicfuVersion: String by project
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicfuVersion")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:$kotlinVersion")
    }
}

apply(plugin = "kotlinx-atomicfu")

plugins {
    kotlin("multiplatform") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("com.android.library")
    id("maven-publish")
    id("signing")
    id("org.jetbrains.dokka") version "1.9.20"
    id("org.sonarqube") version "3.5.0.2730"
    id("org.jetbrains.kotlinx.kover") version "0.7.6"
    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.0"
}

val atomicfuVersion: String by project
val ktorVersion: String by project
val kotlinxSerializationVersion: String by project
val kotlinxCoroutinesVersion: String by project
val klockVersion: String by project
val kryptoVersion: String by project
val semverVersion: String by project

val buildNumber: String get() = System.getenv("BUILD_NUMBER") ?: ""
val isSnapshot: Boolean get() = System.getProperty("snapshot") != null

version = "$version${if (isSnapshot) "-SNAPSHOT" else ""}"

kotlin {
    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    androidTarget {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        publishLibraryVariants("release")
    }

    js {
        browser {
            testTask {
                useKarma {
                    useChromeHeadless()
                }
            }
            commonWebpackConfig {
                cssSupport {
                    enabled.set(true)
                }
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "20000"
                }
            }
        }
    }

    macosX64()
    macosArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    mingwX64()

    linuxX64()
    linuxArm64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:$ktorVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            // TODO replace with kotlinx-datetime
            //   implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.5.0")
            implementation("com.soywiz.korlibs.klock:klock:$klockVersion")
            implementation("com.soywiz.korlibs.krypto:krypto:$kryptoVersion")
            implementation("io.github.z4kn4fein:semver:$semverVersion")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("io.ktor:ktor-client-mock:$ktorVersion")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
        }

        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:$ktorVersion")
            implementation("io.ktor:ktor-client-core-js:$ktorVersion")
        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:$ktorVersion")
            implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfuVersion")
        }

        appleMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }

        appleTest.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
        consumerProguardFiles("configcat-proguard-rules.pro")
    }
}

tasks.getByName<DokkaTask>("dokkaHtml") {
    outputDirectory.set(file(buildDir.resolve("dokka")))

    dokkaSourceSets {
        named("appleMain") {
            platform.set(org.jetbrains.dokka.Platform.native)
            sourceLink {
                localDirectory.set(file("src/appleMain/kotlin"))
                remoteUrl.set(URL("https://github.com/configcat/kotlin-sdk/blob/main/src/appleMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

        named("commonMain") {
            sourceLink {
                localDirectory.set(file("src/commonMain/kotlin"))
                remoteUrl.set(URL("https://github.com/configcat/kotlin-sdk/blob/main/src/commonMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

        named("jsMain") {
            sourceLink {
                localDirectory.set(file("src/jsMain/kotlin"))
                remoteUrl.set(URL("https://github.com/configcat/kotlin-sdk/blob/main/src/jsMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }

        named("androidMain") {
            sourceLink {
                localDirectory.set(file("src/androidMain/kotlin"))
                remoteUrl.set(URL("https://github.com/configcat/kotlin-sdk/blob/main/src/androidMain/kotlin"))
                remoteLineSuffix.set("#L")
            }
        }
    }
}

val javadocJar =
    tasks.register<Jar>("javadocJar") {
        archiveClassifier.set("javadoc")
        dependsOn("dokkaHtml")
        from(buildDir.resolve("dokka"))
    }

detekt {
    config = files("$rootDir/detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
    isIgnoreFailures = true
}

ktlint {
    filter {
        exclude { element -> element.file.path.contains("build.gradle.kts") }
    }
}

tasks.withType<Detekt>().configureEach {
    setSource(project.files(project.projectDir.resolve("src/commonMain")))
    include("**/*.kt")
    reports {
        html.required.set(true)
        xml.required.set(true)
        txt.required.set(true)
        sarif.required.set(true)
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "configcat_kotlin-sdk")
        property("sonar.projectName", "kotlin-sdk")
        property("sonar.projectVersion", "$version-$buildNumber")
        property("sonar.organization", "configcat")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "src/commonMain/kotlin/com/configcat")
        property("sonar.tests", "src/commonTest/kotlin/com/configcat")
        property("sonar.kotlin.detekt.reportPaths", buildDir.resolve("reports/detekt/detekt.xml"))
        property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/xml/report.xml"))
    }
}

publishing {
    repositories {
        maven {
            val releasesRepoUrl = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://oss.sonatype.org/content/repositories/snapshots")
            url = if (isSnapshot) snapshotsRepoUrl else releasesRepoUrl
            credentials {
                username = System.getenv("SONATYPE_USERNAME")
                password = System.getenv("SONATYPE_PASSWORD")
            }
        }
    }

    publications.withType<MavenPublication> {
        artifact(javadocJar.get())

        pom {
            name.set("ConfigCat Kotlin SDK")
            description.set(
                "Kotlin Multiplatform SDK for ConfigCat, a feature flag, feature toggle, and configuration management service. That lets you launch new features and change your software configuration remotely without actually (re)deploying code. ConfigCat even helps you do controlled roll-outs like canary releases and blue-green deployments.",
            )
            url.set("https://github.com/configcat/kotlin-sdk")
            issueManagement {
                system.set("GitHub Issues")
                url.set("https://github.com/configcat/kotlin-sdk/issues")
            }
            licenses {
                license {
                    name.set("MIT License")
                    url.set("https://raw.githubusercontent.com/configcat/kotlin-sdk/main/LICENSE")
                }
            }
            developers {
                developer {
                    id.set("configcat")
                    name.set("ConfigCat")
                    email.set("developer@configcat.com")
                }
            }
            scm {
                url.set("https://github.com/configcat/kotlin-sdk")
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY") ?: ""
    val signingPassphrase = System.getenv("SIGNING_PASSPHRASE") ?: ""
    if (signingKey.isNotEmpty() && signingPassphrase.isNotEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassphrase)
        sign(publishing.publications)
    }
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    dependsOn(tasks.withType<Sign>())
}