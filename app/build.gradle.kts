plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.uysal.minikakademi.app"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.uysal.minikakademi"
        minSdk = 26
        targetSdk = 37
        versionCode = 1
        versionName = "1.0.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    sourceSets["main"].assets.srcDir(rootProject.file("content_v4/runtime"))
    sourceSets["main"].assets.srcDir(rootProject.file("audio_v4/runtime"))
}


dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:navigation"))
    implementation(project(":core:design-system"))
    implementation(project(":core:datastore"))

    implementation(project(":feature:splash"))
    implementation(project(":feature:onboarding"))
    implementation(project(":feature:child-profile"))
    implementation(project(":feature:avatar-selection"))
    implementation(project(":feature:child-home"))
    implementation(project(":feature:learning-path"))
    implementation(project(":feature:tracing"))
    implementation(project(":feature:literacy"))
    implementation(project(":feature:mathematics"))
    implementation(project(":feature:mini-games"))
    implementation(project(":feature:parent-gate"))
    implementation(project(":feature:parent-dashboard"))
    implementation(project(":feature:settings"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material3)
    implementation(libs.compose.ui.tooling.preview)
    debugImplementation(libs.compose.ui.tooling)

    androidTestImplementation("androidx.test.ext:junit:1.3.0")
    androidTestImplementation("androidx.test:runner:1.7.0")
    androidTestImplementation("androidx.test:rules:1.7.0")
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}


val prepareV4CAudio by tasks.registering(Exec::class) {
    group = "build setup"
    description = "Prepare the approved offline V4 C speech assets."
    workingDir(rootProject.projectDir)
    commandLine("python", rootProject.file("tools/audio/prepare_v4_runtime_audio.py").absolutePath)
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(prepareV4CAudio)
}


val prepareV4Content by tasks.registering(Exec::class) {
    group = "build setup"
    description = "Prepare the owner-approved corrected runtime content."
    workingDir(rootProject.projectDir)
    commandLine("python", rootProject.file("tools/content-validator/prepare_v4_runtime_content.py").absolutePath)
}

tasks.matching { it.name == "preBuild" }.configureEach {
    dependsOn(prepareV4Content)
}
