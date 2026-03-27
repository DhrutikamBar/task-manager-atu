import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinxSerialization)
}

kotlin {
    @Suppress("DEPRECATION")
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    js {
        browser()
        binaries.executable()
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
        binaries.executable()
    }
    
    sourceSets {
        val commonMain by getting
        val commonTest by getting
        val androidMain by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val jsMain by getting
        val wasmJsMain by getting

        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.cio)
            implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(projects.shared)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation("io.ktor:ktor-client-logging:3.0.3")
            implementation(libs.navigation.compose)
            implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        
        val webMain by creating {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.kotlinx.browser)
                // Explicitly add for Web to ensure URL syncing works
                implementation(libs.navigation.compose)
                implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")
            }
        }
        
        iosArm64Main.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        iosSimulatorArm64Main.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        
        jsMain.apply {
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlinx.browser)
                implementation(libs.ktor.client.js)
                implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")
            }
        }
        
        wasmJsMain.apply {
            dependsOn(webMain)
            dependencies {
                implementation(libs.kotlinx.browser)
                implementation(libs.ktor.client.js)
                implementation("com.mohamedrejeb.richeditor:richeditor-compose:1.0.0-rc13")

            }
        }
    }
}

android {
    namespace = "com.atu.jira"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.atu.jira"
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
    debugImplementation(libs.compose.uiTooling)
}
