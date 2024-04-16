import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import java.net.URL

buildscript {
    val kotlin_version by extra("1.9.23")
    val atomicfu_version: String by project
    dependencies {
        classpath("org.jetbrains.kotlinx:atomicfu-gradle-plugin:$atomicfu_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin.android:org.jetbrains.kotlin.android.gradle.plugin:$kotlin_version")
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

val atomicfu_version: String by project
val ktor_version: String by project
val kotlinx_serialization_version: String by project
val kotlinx_coroutines_version: String by project
val klock_version: String by project
val krypto_version: String by project
val semver_version: String by project

val build_number: String get() = System.getenv("BUILD_NUMBER") ?: ""
val is_snapshot: Boolean get() = System.getProperty("snapshot") != null
val nativeMainSets: MutableList<KotlinSourceSet> = mutableListOf()
val nativeTestSets: MutableList<KotlinSourceSet> = mutableListOf()
val host: Host = getHostType()

version = "$version${if (is_snapshot) "-SNAPSHOT" else ""}"

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

//    wasmJs {
//        browser {
//            testTask {
//                useKarma {
//                    useChromeHeadless()
//                }
//            }
//            commonWebpackConfig {
//                cssSupport {
//                    enabled.set(true)
//                }
//            }
//        }
//        nodejs {
//            testTask {
//                useMocha {
//                    timeout = "20000"
//                }
//            }
//        }
//    }
//
//    wasmWasi {
//        nodejs()
//    }

    macosX64()
    macosArm64()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    // ERROR whit the korlibs dependencies
//    watchosArm32()
    watchosArm64()
    watchosX64()
    watchosSimulatorArm64()

    tvosArm64()
    tvosX64()
    tvosSimulatorArm64()

    mingwX64()

    linuxX64()
    linuxArm64()
//    linuxArm32Hfp() // this is deprecated?

    // why? check androidTarget?
//    androidNativeArm32()
//    androidNativeArm64()
//    androidNativeX86()
//    androidNativeX64()

    applyDefaultHierarchyTemplate()

    sourceSets {
        commonMain.dependencies {
            implementation("io.ktor:ktor-client-core:$ktor_version")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinx_serialization_version")
            implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinx_serialization_version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinx_coroutines_version")
            implementation("com.soywiz.korlibs.klock:klock:$klock_version")
            implementation("com.soywiz.korlibs.krypto:krypto:$krypto_version")
            implementation("io.github.z4kn4fein:semver:$semver_version")
        }
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation("io.ktor:ktor-client-mock:$ktor_version")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinx_coroutines_version")
        }

        jvmMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktor_version")
        }

        jvmTest.dependencies {}

        jsMain.dependencies {
            implementation("io.ktor:ktor-client-js:$ktor_version")
            implementation("io.ktor:ktor-client-core-js:$ktor_version")
        }
        jsTest.dependencies {
        }

//        jsMain by getting {
//            dependsOn(commonMain)
//            dependencies {
//                implementation("io.ktor:ktor-client-js:$ktor_version")
//                implementation("io.ktor:ktor-client-core-js:$ktor_version")
//            }
//        }
//
//        val jsTest by getting {
//            dependsOn(commonTest)
//        }

        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:$ktor_version")
            implementation("org.jetbrains.kotlinx:atomicfu:$atomicfu_version")
        }

//        val darwinMain by creating {
//            dependsOn(commonMain)
//            dependencies {
//                implementation("io.ktor:ktor-client-darwin:$ktor_version")
//            }
//        }
//
//        val darwinTest by creating {
//            dependsOn(commonTest)
//            dependencies {
//                implementation("io.ktor:ktor-client-darwin:$ktor_version")
//            }
//        }

        nativeMain.dependencies { }

        nativeTest.dependencies { }
//        val nativeMain by getting {
//            dependsOn(commonMain)
//        }
//
//        val nativeTest by getting {
//            dependsOn(commonTest)
//        }

//        configure(nativeMainSets) {
//            if (this.name.isDarwin()) {
//                dependsOn(darwinMain)
//            } else {
//                dependsOn(nativeMain)
//            }
//        }
//
//        configure(nativeTestSets) {
//            if (this.name.isDarwin()) {
//                dependsOn(darwinTest)
//            } else {
//                dependsOn(nativeTest)
//            }
//        }
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
//        named("darwinMain") {
//            platform.set(org.jetbrains.dokka.Platform.native)
//            sourceLink {
//                localDirectory.set(file("src/darwinMain/kotlin"))
//                remoteUrl.set(URL("https://github.com/configcat/kotlin-sdk/blob/main/src/darwinMain/kotlin"))
//                remoteLineSuffix.set("#L")
//            }
//        }

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
        property("sonar.projectVersion", "$version-$build_number")
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
            url = if (is_snapshot) snapshotsRepoUrl else releasesRepoUrl
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

    tasks.withType(AbstractPublishToMaven::class).configureEach {
        onlyIf { isPublicationAllowed(publication.name) }
    }

    tasks.withType(GenerateModuleMetadata::class).configureEach {
        onlyIf { isPublicationAllowed(publication.get().name) }
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

fun isPublicationAllowed(name: String): Boolean =
    when {
        name.startsWith("mingw") -> host == Host.WINDOWS
        name.isDarwin() -> host == Host.MAC_OS
        else -> host == Host.LINUX
    }

fun getHostType(): Host {
    val hostOs = System.getProperty("os.name")
    return when {
        hostOs.startsWith("Windows") -> Host.WINDOWS
        hostOs.startsWith("Mac") -> Host.MAC_OS
        hostOs == "Linux" -> Host.LINUX
        else -> throw Error("Invalid host.")
    }
}

enum class Host { WINDOWS, MAC_OS, LINUX }

fun String.isDarwin(): Boolean =
    this.startsWith("macos") ||
        this.startsWith("ios") ||
        this.startsWith("watchos") ||
        this.startsWith("tvos")
