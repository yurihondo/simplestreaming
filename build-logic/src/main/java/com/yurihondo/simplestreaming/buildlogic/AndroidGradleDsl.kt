package com.yurihondo.simplestreaming.buildlogic

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.androidApplication(action: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.androidLibrary(action: LibraryExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.android(action: TestedExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.configureAndroid() {
    android {
        namespace?.let {
            this.namespace = it
        }
        compileSdkVersion(33)

        defaultConfig {
            minSdk = 33
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            isCoreLibraryDesugaringEnabled = true
        }
        dependencies {
            add("coreLibraryDesugaring", libs.findLibrary("androidDesugarJdkLibs").get())
        }
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        defaultConfig.targetSdk = 33
    }
}
