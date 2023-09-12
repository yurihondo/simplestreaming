plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.library")
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin")
    id("com.yurihondo.simplestreaming.buildlogic.android.hilt")
}

android {
    namespace = "com.yurihondo.simplestreaming.datastore"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    // Modules
    implementation(projects.core.model)

    // AppAuth
    implementation(libs.appAuth)

    // Tink
    implementation(libs.tink)

    //DataStore
    implementation(libs.dataStore)
}
