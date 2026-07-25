import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.ksp)
}


val localProps = Properties().apply {
    load(rootProject.file("local.properties").inputStream())
}


android {
    namespace = "cm.horion.homegaz"
    compileSdk = 36

    defaultConfig {
        applicationId = "cm.horion.homegaz"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        defaultConfig {
            buildConfigField("String", "MAPKIT_API_KEY",
                "\"${localProps["MAPKIT_API_KEY"]}\"")
        }
        manifestPlaceholders["MAPKIT_API_KEY"] = localProps["MAPKIT_API_KEY"] ?: ""
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // Compose BOM
    implementation(platform(libs.androidx.compose.bom))

    // Compose UI
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)

    // Fonts
    implementation(libs.androidx.ui.text.google.fonts)

    // Navigation
    implementation(libs.androidx.navigation.compose)

    // Pager / Foundation
    implementation(libs.androidx.foundation)

    // Coil
    implementation(libs.coil.compose)

    // Permissions
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.navigation.animation)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.work:work-runtime-ktx:2.10.0")
    implementation("io.insert-koin:koin-androidx-workmanager:3.5.6")
    //Qrose
    implementation(libs.qrose)

    // Koin
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    // Google Maps (si encore utilisé)
    implementation(libs.maps.compose)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.location)

    // Yandex MapKit SDK
    implementation(libs.maps.mobile)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")

    // Client Http
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.logging)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)
    implementation("androidx.core:core-splashscreen:1.1.0-rc01")

    implementation("androidx.browser:browser:1.8.0")

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}