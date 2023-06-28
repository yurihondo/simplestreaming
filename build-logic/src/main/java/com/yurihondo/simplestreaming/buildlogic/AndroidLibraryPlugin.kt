package com.yurihondo.simplestreaming.buildlogic

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryPlugin: Plugin<Project> {
    override fun apply(target: Project) {

        with(target) {
            with(pluginManager) {
                apply("com.android.library")
            }

            androidLibrary {
                configureAndroid()
            }
        }
    }
}