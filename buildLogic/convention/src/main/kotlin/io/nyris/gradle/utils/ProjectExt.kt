/*
 * Copyright 2023 nyris GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.nyris.gradle.utils

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun Project.configureDefaultApplicationPlugins() {
    with(pluginManager) {
        apply(Configuration.androidApplicationPlugin)
        if (System.getenv().containsKey("CI")) {
            apply(Configuration.appDist)
            apply(Configuration.crashReporter)
            apply(Configuration.googleServices)
        }
    }
    configureDefaultPlugins()
}

internal fun Project.configureDefaultPlugins() {
    with(pluginManager) {
        apply(Configuration.kotlinAndroidPlugin)
        apply(Configuration.kotlinKaptPlugin)
        apply(Configuration.testLogger)
    }

    extensions.configure<TestLoggerExtension> {
        theme = ThemeType.MOCHA
    }
}

internal fun Project.configureDefaultLibraryPlugins() {
    pluginManager.apply(Configuration.androidLibraryPlugin)
    configureDefaultPlugins()
}

internal fun Project.applyKotlinJvmToolChain() {
    extensions.configure<KotlinProjectExtension> {
        jvmToolchain(Configuration.javaVersion)
    }
}

internal fun Project.configureDefaultLibrarySerializationPlugins() {
    pluginManager.apply(Configuration.kotlinSerialization)
    configureDefaultLibraryPlugins()
}

internal fun CommonExtension<*, *, *, *>.kotlinOptions(block: KotlinJvmOptions.() -> Unit) {
    (this as ExtensionAware).extensions.configure("kotlinOptions", block)
}

internal fun Project.configureDefaultDependencies(includeLibs: Boolean = true) {
    dependencies {
        if (includeLibs) {
            add("implementation", fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        }
        add("testImplementation", libs().findLibrary("test_junit_engine").get())
        add("testImplementation", libs().findLibrary("test_mockk.core").get())
        add("testImplementation", libs().findLibrary("test_assertj_core").get())
    }
}

internal fun Project.libs(): VersionCatalog =
    extensions.getByType<VersionCatalogsExtension>().named("libs")
