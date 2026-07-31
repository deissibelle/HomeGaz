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

    // Pager / Foundation
    implementation(libs.androidx.foundation)

    // Coil
    implementation(libs.coil.compose)

    // Permissions
    implementation(libs.accompanist.permissions)
    implementation(libs.accompanist.navigation.animation)

    // DataStore
    implementation(libs.datastore.preferences)
    implementation(libs.androidx.security.crypto)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.koin.androidx.workmanager)
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
    implementation(libs.kotlinx.coroutines.play.services)

    // Client Http
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.json)
    implementation(libs.ktor.client.logging)

    //splashscreen
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.core.splashscreen.v110rc01)

    implementation(libs.androidx.browser)

    // Navigation 3 (remplace androidx-navigation-compose / Nav 2)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3) // koinViewModel() scopé par NavEntry
    implementation(libs.kotlinx.serialization.core)               // @Serializable sur les NavKey

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(
        platform(libs.androidx.compose.bom)
    )

    androidTestImplementation(libs.androidx.ui.test.junit4)

    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}