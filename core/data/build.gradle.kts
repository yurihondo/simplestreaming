plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.library")
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin")
    id("com.yurihondo.simplestreaming.buildlogic.android.hilt")
}

android {
    namespace = "com.yurihondo.simplestreaming.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
}

dependencies {
    // Modules
    implementation(projects.core.datastore)
    implementation(projects.core.model)

    // AppAuth
    implementation(libs.appAuth)

    // Google API
    implementation(libs.gson)
    implementation(libs.youtubeDataApi) {
        exclude(group = "org.apache.httpcomponents")
    }

    // DataStore
    implementation(libs.dataStore)

    implementation("com.github.pedroSG94.RootEncoder:library:2.4.5")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}
