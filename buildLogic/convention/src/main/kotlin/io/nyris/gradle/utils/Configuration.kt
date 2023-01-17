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

import org.gradle.api.JavaVersion

object Configuration {
    const val compileSdkVersion = 33
    const val minSdkVersion = 21
    const val targetSdkVersion = 33
    const val javaVersion = 11
    val javaVersionEnum = JavaVersion.VERSION_11

    const val kotlinMultiplatform = "org.jetbrains.kotlin.multiplatform"
    const val androidApplicationPlugin = "com.android.application"
    const val kotlinAndroidPlugin = "org.jetbrains.kotlin.android"
    const val androidLibraryPlugin = "com.android.library"
    const val kotlinKaptPlugin = "org.jetbrains.kotlin.kapt"
    const val kotlinSerialization = "org.jetbrains.kotlin.plugin.serialization"
    const val testLogger = "com.adarshr.test-logger"
    const val appDist = "io.nyris.gradle.appdist"
    const val crashReporter = "io.nyris.gradle.crash-reporter"
    const val googleServices = "com.google.gms.google-services"
}
