plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "com.uysal.minikakademi.core.navigation"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

