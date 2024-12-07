plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "edu.birzeit.jetset"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.birzeit.jetset"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        multiDexEnabled = true
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

//repositories {
//    google()
//    mavenCentral()
//}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(platform(libs.kotlin.bom))
    implementation(libs.lottie)
    implementation(libs.gson)
    implementation(libs.material.v120alpha03)
    implementation(libs.dotsindicator)
    implementation(libs.ticketview)
    implementation(libs.window)
    implementation(libs.constraintlayout)
    implementation(libs.multidex)
    implementation(libs.annotation)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}