plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
//    kotlin("jvm") version "2.1.0" // or kotlin("multiplatform") or any other kotlin plugin
    kotlin("plugin.serialization") version "2.1.0"
}

android {
    namespace = "com.example.navbottomtest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.navbottomtest"
        minSdk = 33
        //noinspection EditedTargetSdkVersion
        targetSdk = 35
        versionCode = 1
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.palette.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
//    implementation()
    implementation(libs.material.v130alpha03)
//    implementation (libs.supabase.supabase.kt) // Use the latest version

    implementation(platform("io.github.jan-tennert.supabase:bom:3.0.0"))
    implementation(libs.postgrest.kt)
    implementation("io.github.jan-tennert.supabase:supabase-kt:3.0.3")
    implementation("io.github.jan-tennert.supabase:auth-kt:3.0.3")
    implementation("io.ktor:ktor-client-android:3.0.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation ("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.media3:media3-exoplayer:1.5.1")
    implementation("androidx.media3:media3-exoplayer-dash:1.5.1")
    implementation("androidx.media3:media3-ui:1.5.1")

    implementation ("androidx.core:core:1.6.0")
    implementation ("androidx.media:media:1.4.3")
    implementation(libs.androidx.swiperefreshlayout)

}