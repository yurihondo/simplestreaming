plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.application")
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin")
}

android {
    namespace = "com.yurihondo.simplestreaming"

    defaultConfig {
        applicationId ="com.yurihondo.simplestreaming"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    @Suppress("UnstableApiUsage")
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFile(getDefaultProguardFile("proguard-android-optimize.txt"))
            proguardFile(file("proguard-rules.pro"))
        }
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        jniLibs {
            excludes.addAll(
                listOf(
                    "META-INF/ASL2.0",
                    "META-INF/AL2.0",
                    "META-INF/*.kotlin_module",
                    "META-INF/LGPL2.1"
                )
            )
        }
    }
}

dependencies {
    // Modules
    implementation(projects.feature.text)
    implementation(projects.feature.setting)

    // Compose
    val composeBom = platform(libs.composeBom)
    implementation(composeBom)
    implementation(libs.bundles.compose)
    androidTestImplementation(composeBom)
    androidTestImplementation(libs.composeUiTestJunit4)
    debugImplementation(libs.bundles.composeDebug)

    // AndroidX
    implementation(libs.androidxCore)
    implementation(libs.androidxActivity)

    // Lifecycle
    implementation(libs.lifecycleRuntimeKtx)

    // Navigation
    implementation(libs.navigationCompose)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}