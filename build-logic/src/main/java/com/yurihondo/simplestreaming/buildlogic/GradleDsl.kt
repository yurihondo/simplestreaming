package com.yurihondo.simplestreaming.buildlogic

import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import java.util.Optional

fun DependencyHandlerScope.implementation(
    artifact: Optional<Provider<MinimalExternalModuleDependency>>
) {
    add("implementation", artifact.get())
}

fun DependencyHandlerScope.debugImplementation(
    artifact: Optional<Provider<MinimalExternalModuleDependency>>
) {
    add("debugImplementation", artifact.get())
}

fun DependencyHandlerScope.androidTestImplementation(
    artifact: Optional<Provider<MinimalExternalModuleDependency>>
) {
    add("androidTestImplementation", artifact.get())
}

fun DependencyHandlerScope.testImplementation(
    artifact: Optional<Provider<MinimalExternalModuleDependency>>
) {
    add("testImplementation", artifact.get())
}

private fun DependencyHandlerScope.api(
    artifact: Optional<Provider<MinimalExternalModuleDependency>>
) {
    add("api", artifact.get())
}

fun Project.java(action: JavaPluginExtension.() -> Unit) {
    extensions.configure(action)
}

val Project.libs get() = extensions.getByType<VersionCatalogsExtension>().named("libs")