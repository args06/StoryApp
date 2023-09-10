plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("kotlin-parcelize")
    id("androidx.navigation.safeargs")
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

android {
    namespace = "com.example.storyapp"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.storyapp"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
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
}

object DependencyVersion {
    // Core
    const val coreVersion = "1.10.1"
    const val appcompatVersion = "1.6.1"
    const val lifecycleVersion = "2.6.2"
    const val activityVersion = "1.7.2"
    const val fragmentVersion = "1.6.1"

    // UI
    const val materialVersion = "1.9.0"
    const val constraintlayoutVersion = "2.1.4"
    const val circleImageViewVersion = "3.1.0"

    // Navigation
    const val navigationVersion = "2.6.0"

    // Photo Online Loader
    const val glideVersion = "4.15.1"

    // API
    const val retrofit2Version = "2.9.0"
    const val okhttp3Version = "4.9.3"

    // Unit Testing
    const val junitVersion = "4.13.2"

    // Integration Testing
    const val espressoVersion = "3.5.1"
    const val extVersion = "1.1.5"

    // Hilt Injection Support
    const val hiltVersion = "2.44"

    // Preferences DataStore
    const val dataStoreVersion = "1.0.0"

    // Lottie
    const val lottieVersion = "4.2.2"

    // Splash Screen
    const val splashScreenVersion = "1.0.0"

    // Splash Screen
    const val cameraVersion = "1.2.3"
}

dependencies {

    // Core
    implementation("androidx.core:core-ktx:${DependencyVersion.coreVersion}")
    implementation("androidx.appcompat:appcompat:${DependencyVersion.appcompatVersion}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:${DependencyVersion.lifecycleVersion}")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:${DependencyVersion.lifecycleVersion}")
    implementation("androidx.activity:activity-ktx:${DependencyVersion.activityVersion}")
    implementation("androidx.fragment:fragment-ktx:${DependencyVersion.fragmentVersion}")

    // UI
    implementation("com.google.android.material:material:${DependencyVersion.materialVersion}")
    implementation("androidx.constraintlayout:constraintlayout:${DependencyVersion.constraintlayoutVersion}")

    // Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:${DependencyVersion.navigationVersion}")
    implementation("androidx.navigation:navigation-ui-ktx:${DependencyVersion.navigationVersion}")

    // Photo Online Loader
    implementation("com.github.bumptech.glide:glide:${DependencyVersion.glideVersion}")

    // API
    implementation("com.squareup.retrofit2:retrofit:${DependencyVersion.retrofit2Version}")
    implementation("com.squareup.retrofit2:converter-gson:${DependencyVersion.retrofit2Version}")
    implementation("com.squareup.okhttp3:logging-interceptor:${DependencyVersion.okhttp3Version}")

    // Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:${DependencyVersion.dataStoreVersion}")
    implementation("com.google.android.gms:play-services-maps:18.1.0")

    // Unit Testing
    testImplementation("junit:junit:${DependencyVersion.junitVersion}")

    // Integration Testing
    androidTestImplementation("androidx.test.espresso:espresso-core:${DependencyVersion.espressoVersion}")
    androidTestImplementation("androidx.test.ext:junit:${DependencyVersion.extVersion}")

    // Hilt Injection Support
    implementation("com.google.dagger:hilt-android:${DependencyVersion.hiltVersion}")
    kapt("com.google.dagger:hilt-android-compiler:${DependencyVersion.hiltVersion}")

    // Lottie
    implementation("com.airbnb.android:lottie:${DependencyVersion.lottieVersion}")

    // Splash Screen
    implementation("androidx.core:core-splashscreen:${DependencyVersion.splashScreenVersion}")

    // Camera Support
    implementation("androidx.camera:camera-camera2:${DependencyVersion.cameraVersion}")
    implementation("androidx.camera:camera-lifecycle:${DependencyVersion.cameraVersion}")
    implementation("androidx.camera:camera-view:${DependencyVersion.cameraVersion}")
}
