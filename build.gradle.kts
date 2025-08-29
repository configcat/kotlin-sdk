import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import io.gitlab.arturbosch.detekt.Detekt
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
    alias(libs.plugins.mavenPublish)
}

val buildNumber: String get() = System.getenv("BUILD_NUMBER") ?: ""
val isSnapshot: Boolean get() = System.getProperty("snapshot") != null

version = "$version${if (isSnapshot) "-SNAPSHOT" else ""}"

kotlin {
    explicitApi()

    jvm {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
    }

    androidTarget {
        compilerOptions.jvmTarget = JvmTarget.JVM_1_8
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

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
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
        nodejs()
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
            implementation(libs.sha1)
            implementation(libs.sha2)
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

        wasmJsMain.dependencies {
            implementation(libs.browser)
        }

        androidMain.dependencies {
            implementation(libs.ktor.android)
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

        all {
            languageSettings.optIn("kotlin.time.ExperimentalTime")
            languageSettings.optIn("kotlin.concurrent.atomics.ExperimentalAtomicApi")
        }
    }
}

android {
    namespace = "com.configcat"
    compileSdk = 36
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        version = project.version as String
        consumerProguardFiles("configcat-proguard-rules.pro")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        targetSdk = 31
    }

    lint {
        targetSdk = 31
    }
}

dokka {
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka"))
    }

    dokkaSourceSets.commonMain {
        sourceLink {
            localDirectory.set(file("src/commonMain/kotlin"))
            remoteUrl("https://github.com/configcat/kotlin-sdk/blob/main/src/commonMain/kotlin")
            remoteLineSuffix.set("#L")
        }
    }

    dokkaSourceSets.named("appleMain") {
        sourceLink {
            localDirectory.set(file("src/appleMain/kotlin"))
            remoteUrl("https://github.com/configcat/kotlin-sdk/blob/main/src/appleMain/kotlin")
            remoteLineSuffix.set("#L")
        }
    }

    dokkaSourceSets.named("jsMain") {
        sourceLink {
            localDirectory.set(file("src/jsMain/kotlin"))
            remoteUrl("https://github.com/configcat/kotlin-sdk/blob/main/src/jsMain/kotlin")
            remoteLineSuffix.set("#L")
        }
    }

    dokkaSourceSets.named("androidMain") {
        sourceLink {
            localDirectory.set(file("src/androidMain/kotlin"))
            remoteUrl("https://github.com/configcat/kotlin-sdk/blob/main/src/androidMain/kotlin")
            remoteLineSuffix.set("#L")
        }
    }
}

detekt {
    config.setFrom("$rootDir/detekt.yml")
    buildUponDefaultConfig = true
    parallel = true
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

sonarqube {
    properties {
        property("sonar.projectKey", "configcat_kotlin-sdk")
        property("sonar.projectName", "kotlin-sdk")
        property("sonar.projectVersion", "$version-$buildNumber")
        property("sonar.organization", "configcat")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.sources", "src/commonMain/kotlin/com/configcat")
        property("sonar.tests", "src/commonTest/kotlin/com/configcat")
        property("sonar.kotlin.detekt.reportPaths", "detekt.xml")
        property("sonar.coverage.jacoco.xmlReportPaths", "report.xml")
    }
}

mavenPublishing {
    publishToMavenCentral()
    if (providers.environmentVariable("ORG_GRADLE_PROJECT_signingInMemoryKey").isPresent &&
        providers.environmentVariable("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword").isPresent
    ) {
        signAllPublications()
    }

    configure(
        KotlinMultiplatform(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true
        )
    )

    coordinates(project.group as String?, project.name, project.version as String?)

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
