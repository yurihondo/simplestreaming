plugins {
    `kotlin-dsl`
}

group = "com.yurihondo.simplestreaming.buildlogic"

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("android.library") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.library"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidLibraryPlugin"
        }
        register("androidKotlin") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.kotlin"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidKotlinPlugin"
        }
    }
}