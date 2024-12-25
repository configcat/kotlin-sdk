import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.dokka.gradle.DokkaTask
import java.net.URL

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.serialization)
    alias(libs.plugins.atomicfu)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.dokka)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.kover)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    id("maven-publish")
    id("signing")
}

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
            implementation(libs.ktor)
            implementation(libs.serialization.core)
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
            implementation(libs.klock)
            implementation(libs.krypto)
            implementation(libs.semver)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.ktor.mock)
            implementation(libs.coroutines.test)
        }

        jvmMain.dependencies {
            implementation(libs.ktor.okhttp)
        }

        jsMain.dependencies {
            implementation(libs.ktor.js)
        }

        androidMain.dependencies {
            implementation(libs.ktor.android)
            implementation(libs.atomicfu)
        }

        appleMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        appleTest.dependencies {
            implementation(libs.ktor.darwin)
        }

        val nativeRestMain by creating {
            dependsOn(commonMain.get())
        }
        val nativeRestTest by creating {
            dependsOn(commonTest.get())
            dependencies {
                implementation(libs.ktor.cio)
            }
        }

        mingwMain.get().dependsOn(nativeRestMain)
        mingwTest.get().dependsOn(nativeRestTest)

        linuxMain.get().dependsOn(nativeRestMain)
        linuxTest.get().dependsOn(nativeRestTest)
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        //noinspection ExpiredTargetSdkVersion
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
    additionalEditorconfig.set(
        mapOf(
            "max_line_length" to "120"
        )
    )
    filter {
        exclude { element -> element.file.path.contains("build.gradle.kts") || element.file.path.contains("Test") }
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
        property("sonar.coverage.jacoco.xmlReportPaths", buildDir.resolve("reports/kover/report.xml"))
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