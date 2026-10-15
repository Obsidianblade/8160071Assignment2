plugins {
    alias(libs.plugins.android.application)
    // Generates Parcelable code for @Parcelize classes (passing a Book between screens)
    alias(libs.plugins.kotlin.parcelize)
    // Kotlin Symbol Processing: runs the Hilt and Moshi code generators
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    // Generates type-safe Directions/Args classes from nav_graph.xml
    alias(libs.plugins.navigation.safeargs)
}

android {
    namespace = "com.example.booknest"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.booknest"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Exposed to Kotlin as BuildConfig.BASE_URL so the server address lives in one place
        buildConfigField("String", "BASE_URL", "\"https://nit3213apinew.onrender.com/\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // AndroidX and Material Design
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.recyclerview)
    implementation(libs.material)

    // ViewModel, viewModelScope and repeatOnLifecycle
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Navigation Component
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.androidx.navigation.ui)

    // Hilt dependency injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Networking: Retrofit + Moshi JSON converter + OkHttp request logging
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.moshi)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.moshi)
    ksp(libs.moshi.kotlin.codegen)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Local unit tests (run on the JVM, no device needed)
    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
}
