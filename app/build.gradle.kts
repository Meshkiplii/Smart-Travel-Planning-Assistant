import java.util.Properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties") // Correct path to root project's file
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "1.9.23"
}


android {
    namespace = "com.meshkipli.smarttravel"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.meshkipli.smarttravel"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        val geminiApiKey = localProperties.getProperty("gemini.api.key") ?: "" // Provide a default or handle missing key
        buildConfigField("String", "GEMINI_API_KEY", "\"$geminiApiKey\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.core.splashscreen)
    implementation("com.halilibo.compose-richtext:richtext-ui-material3:0.20.0")
    implementation("com.halilibo.compose-richtext:richtext-commonmark:0.20.0")
  // Ktor Client Core
    implementation("io.ktor:ktor-client-core:3.2,2") // Check for the latest 
//    implementation("io.ktor:ktor-client-cio:2.3.12")
    // Or, for Android specific engine (might have better integration with Android features):
    implementation("io.ktor:ktor-client-android:3.2.2")
    // Or, if you prefer OkHttp:
    // implementation("io.ktor:ktor-client-okhttp:2.3.12")

    // Ktor Content Negotiation & Kotlinx Serialization
    implementation("io.ktor:ktor-client-content-negotiation:3.2.2")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.2.2")


    // Kotlinx Serialization JSON (if not already present)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3") // Check latest

    // Room
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
//    annotationProcessor(libs.room.compiler)
    ksp(libs.room.compiler)
    // Gemini
    implementation(libs.gemini.generativeai)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
