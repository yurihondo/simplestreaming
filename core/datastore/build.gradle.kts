plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.library")
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin")
    id("com.yurihondo.simplestreaming.buildlogic.android.hilt")
    kotlin("plugin.serialization")
}

android {
    namespace = "com.yurihondo.simplestreaming.datastore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
}

dependencies {
    implementation(projects.core.model)

    implementation("com.google.crypto.tink:tink-android:1.9.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")
}
