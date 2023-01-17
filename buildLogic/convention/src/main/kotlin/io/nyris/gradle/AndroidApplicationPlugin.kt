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

import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
import io.nyris.gradle.utils.Configuration
import io.nyris.gradle.utils.applyKotlinJvmToolChain
import io.nyris.gradle.utils.configureDefaultApplicationPlugins
import io.nyris.gradle.utils.configureDefaultDependencies
import io.nyris.gradle.utils.kotlinOptions
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra

private val proguardFileList = listOf("proguard-rules.pro")

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            configureDefaultApplicationPlugins()
            configureApplication()
            configureDefaultDependencies(false)
        }
    }
}

private fun Project.configureApplication() {
    val project = this
    applyKotlinJvmToolChain()
    extensions.configure<BaseAppModuleExtension> {
        defaultConfig(project)

        defaultCompileOptions()

        defaultBundleOptions()

        defaultLintOptions()

        defaultSigningOptions(project)

        defaultBuildTypes(project)
    }
}

private fun BaseAppModuleExtension.defaultConfig(target: Project) {
    defaultConfig.targetSdk = Configuration.targetSdkVersion
    compileSdk = Configuration.compileSdkVersion

    defaultConfig {
        if (minSdk == null) {
            minSdk = Configuration.minSdkVersion
        }
        versionCode = calculateVersionCode(target)
    }
}

private fun calculateVersionCode(target: Project): Int {
    return "git rev-list --all --count".execute(target)?.toIntOrNull() ?: 1
}

private fun BaseAppModuleExtension.defaultCompileOptions() {
    compileOptions {
        sourceCompatibility = Configuration.javaVersionEnum
        targetCompatibility = Configuration.javaVersionEnum
    }

    kotlinOptions {
        jvmTarget = Configuration.javaVersionEnum.toString()
        freeCompilerArgs = listOf("-Xjvm-default=all")
    }
}

private fun BaseAppModuleExtension.defaultBundleOptions() {
    bundle {
        language.enableSplit = true
        density.enableSplit = true
        abi.enableSplit
    }
}

private fun BaseAppModuleExtension.defaultLintOptions() {
    lint {
        abortOnError = true
        checkReleaseBuilds = true
    }
}

private fun BaseAppModuleExtension.defaultSigningOptions(project: Project) {
    with(project) {
        signingConfigs {
            getByName("debug") {
                storeFile = file("$rootDir/configs/signing/debug.keystore")
                storePassword = "android"
                keyAlias = "androiddebugkey"
                keyPassword = "android"
            }

            create("release") {
                storeFile = project.file("$rootDir/configs/signing/release.keystore")
                storePassword = rootProject.extra["RELEASE_KEYSTORE_PASSWORD"].toString()
                keyAlias = rootProject.extra["RELEASE_KEY_ALIAS"].toString()
                keyPassword = rootProject.extra["RELEASE_KEY_PASSWORD"].toString()
            }
        }
    }
}

private fun BaseAppModuleExtension.defaultBuildTypes(project: Project) {
    val proguardFilez = proguardFileList.map { fileName ->
        project.file(fileName)
    } + getDefaultProguardFile("proguard-android.txt")

    buildTypes {
        debug {
            isDebuggable = true
            // Since we don't have firebase configuration for .debug app, we want to add the suffix
            // only when the environment is not CI, which will allow the app to compile on
            // debug build type.
            if (!System.getenv().containsKey("CI")) {
                applicationIdSuffix = ".debug"
            }

            isShrinkResources = false
            isMinifyEnabled = false

            signingConfig = signingConfigs.getByName("debug")

            enableUnitTestCoverage = false

            if (System.getenv().containsKey("CI")) {
                extra.set("enableCrashlytics", false)
                configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
            }
        }

        release {
            isDebuggable = false

            proguardFiles.addAll(proguardFilez)

            isShrinkResources = true
            isMinifyEnabled = true

            signingConfig = signingConfigs.getByName("release")

            if (System.getenv().containsKey("CI")) {
                extra.set("enableCrashlytics", false)
                configure<CrashlyticsExtension> { mappingFileUploadEnabled = false }
            }
        }
    }
}
