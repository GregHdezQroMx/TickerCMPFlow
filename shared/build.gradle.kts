import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    /**
     * Unified iOS Target Configuration.
     * Generates a static framework for Apple targets, allowing the shared logic
     * to be consumed by the iOS application.
     */
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        /**
         * COMMON MAIN SOURCE SET
         * Contains the pure Kotlin logic accessible by all platforms (Android, iOS, etc.).
         */
        val commonMain by getting {
            dependencies {
                // Dependency Injection: Koin Core is required for the shared module definitions.
                implementation(libs.koin.core)
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Networking: Ktor Core and WebSockets for real-time data communication.
                implementation(libs.ktor.client.core)
                implementation(libs.ktor.client.websockets)

                // Serialization: JSON parsing and WebSocket content negotiation.
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)

                // Napier Logging
                implementation(libs.napier)

                // Compose Multiplatform Resources
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
        
        val androidMain by getting {
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.koin.android)
            }
        }
        
        /**
         * IOS SPECIFIC IMPLEMENTATION
         * Uses Darwin (URLSession) as the networking engine for Apple targets.
         */
        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
        
        // Connect specific architecture source sets to the base iosMain
        val iosArm64Main by getting { dependsOn(iosMain) }
        val iosSimulatorArm64Main by getting { dependsOn(iosMain) }
    }
}

android {
    // Unique namespace for the shared library module.
    namespace = "com.jght.business.stockmarket.ticker_cmp_flow.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }
}

compose.resources {
    publicResClass = true
}