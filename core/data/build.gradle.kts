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
    implementation("com.google.api-client:google-api-client-gson:2.2.0")
    implementation("com.google.apis:google-api-services-youtube:v3-rev20230807-2.0.0") {
        exclude(group = "org.apache.httpcomponents")
    }

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0")

    implementation("com.github.pedroSG94.rtmp-rtsp-stream-client-java:rtplibrary:2.2.6")

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidxJunit)
    androidTestImplementation(libs.espressoCore)
}
