plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.library")
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin")
    id("com.yurihondo.simplestreaming.buildlogic.android.hilt")
}

android {
    namespace = "com.yurihondo.simplestreaming.data"

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
    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}
