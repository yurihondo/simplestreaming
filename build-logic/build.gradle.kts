plugins {
    `kotlin-dsl`
}

group = "com.yurihondo.simplestreaming.buildlogic"

repositories {
    google()
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }

    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation(libs.bundles.plugins)
}

gradlePlugin {
    plugins {
        register("android.application") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.application"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidAppPlugin"
        }
        register("android.library") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.library"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidLibraryPlugin"
        }
        register("androidKotlin") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.kotlin"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidKotlinPlugin"
        }
        register("android.hilt") {
            id = "com.yurihondo.simplestreaming.buildlogic.android.hilt"
            implementationClass = "com.yurihondo.simplestreaming.buildlogic.AndroidHiltPlugin"
        }
    }
}