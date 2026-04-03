import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    // Serialization plugin is kept here to allow the UI/ViewModel
    // to handle specialized data parsing if required in the future.
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    /**
     * Unified iOS Target Configuration.
     * Simplifies the build process for both physical devices and simulators
     * by applying a consistent framework base name.
     */
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val commonMain by getting {
            dependencies {
                /**
                 * CORE MODULE LINK
                 * This provides access to the Domain and Data layers defined in :shared.
                 */
                implementation(project(":shared"))

                // JetBrains Compose Multiplatform Core Dependencies
                implementation(libs.compose.runtime)
                implementation(libs.compose.foundation)
                implementation(libs.compose.material3)
                implementation(libs.compose.ui)
                implementation(libs.compose.components.resources)
                implementation(libs.compose.uiToolingPreview)

                // Lifecycle & Architecture Components for Compose
                implementation(libs.androidx.lifecycle.viewmodelCompose)
                implementation(libs.androidx.lifecycle.runtimeCompose)

                /**
                 * DEPENDENCY INJECTION (KOIN)
                 * koin-compose provides the integration between Koin and the
                 * Composable lifecycle, allowing seamless ViewModel injection.
                 */
                implementation(libs.koin.compose)
                implementation(libs.koin.compose.viewmodel)

                // Note: Ktor and Serialization core dependencies are managed
                // within the :shared module to enforce Clean Architecture.
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(libs.compose.uiToolingPreview)
                implementation(libs.androidx.activity.compose)
                implementation(libs.koin.android)
                // Ktor OkHttp engine: Required for the networking layer on Android.
                implementation(libs.ktor.client.okhttp)
            }
        }

        val iosMain by creating {
            dependsOn(commonMain)
            dependencies {
                // Ktor Darwin engine: Required for the networking layer on iOS devices.
                implementation(libs.ktor.client.darwin)
            }
        }

        val iosArm64Main by getting {
            dependsOn(iosMain)
        }

        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain)
        }
    }
}

android {
    namespace = "com.jght.business.stockmarket.ticker_cmp_flow"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.jght.business.stockmarket.ticker_cmp_flow"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    /**
     * ANDROID UI DEBUGGING
     * Allows the use of Layout Inspector and Preview tools within Android Studio.
     */
    debugImplementation(libs.compose.uiTooling)
}