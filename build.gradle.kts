// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.yurihondo.simplestreaming.buildlogic.android.application") version "unspecified" apply false
    id("com.yurihondo.simplestreaming.buildlogic.android.library") version "unspecified" apply false
    id("com.yurihondo.simplestreaming.buildlogic.android.kotlin") version "unspecified" apply false
    alias(libs.plugins.littleRobotsVersionCatalog)
    alias(libs.plugins.gradleVersions)
    kotlin("jvm") version "1.9.0"
    kotlin("plugin.serialization") version "1.9.0"
}

buildscript {
    repositories {
        google()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.2")
    }
}

versionCatalogUpdate {
    sortByKey.set(false)
}
