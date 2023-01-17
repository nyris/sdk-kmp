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
package io.nyris.gradle

import com.adarshr.gradle.testlogger.TestLoggerExtension
import com.adarshr.gradle.testlogger.theme.ThemeType
import com.android.build.gradle.LibraryExtension
import io.nyris.gradle.utils.Configuration
import io.nyris.gradle.utils.applyKotlinJvmToolChain
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class MultiplatformAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(Configuration.kotlinMultiplatform)
            pluginManager.apply(Configuration.androidLibraryPlugin)
            pluginManager.apply(Configuration.testLogger)
            extensions.configure<TestLoggerExtension> {
                theme = ThemeType.MOCHA
            }
            configureMultiplatformLibrary()
        }
    }
}

internal fun Project.configureMultiplatformLibrary() {
    applyKotlinJvmToolChain()

    extensions.configure<LibraryExtension> {
        compileSdk = Configuration.compileSdkVersion

        defaultConfig {
            minSdk = Configuration.minSdkVersion
            @Suppress("deprecation")
            targetSdk = Configuration.targetSdkVersion
            consumerProguardFiles(file("consumer-rules.pro"))
        }

        compileOptions {
            sourceCompatibility = Configuration.javaVersionEnum
            targetCompatibility = Configuration.javaVersionEnum
        }
    }
}
