import org.jetbrains.kotlin.konan.properties.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)

    kotlin("kapt")

    id("com.google.gms.google-services")
}


android {
    namespace = "app.solution.swing_by"
    compileSdk = 34

    defaultConfig {
        applicationId = "app.solution.swing_by"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)

    /* Room */
//    implementation(libs.androidx.room.runtime)
//    annotationProcessor(libs.androidx.room.compiler)

    /* Firebase */
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.messaging.ktx)
    implementation(libs.firebase.database.ktx)

    /* Kakao Map API */
//    implementation("com.kakao.sdk:v2-all:2.20.6")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.kakao.maps.open:android:2.12.7")


    /* Retrofit */
    implementation(libs.retrofit)
    implementation(libs.gson)
    implementation(libs.retrofit2.converter.gson)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}