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
import io.nyris.gradle.utils.Modules

plugins {
    id("io.nyris.gradle.app")
}

android {
    namespace = "io.nyris.sdk.demo"
    defaultConfig {
        applicationId = "io.nyris.sdk.demo"
        versionName = rootProject.extra["LIB_VERSION_NAME"].toString()
    }

    buildFeatures {
        viewBinding = true
    }

    buildTypes.forEach {
        it.buildConfigField("String", "API_KEY", "\"${System.getenv("PUBLIC_API_KEY")}\"")
    }
}

dependencies {
    implementation(project(Modules.sdk_core))
    implementation(project(Modules.sdk_camera_android))
    implementation(project(Modules.sdk_camera_feature_barcode_android))
    implementation(project(Modules.sdk_ui_android))

    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.ktor.core)
    implementation(libs.ktor.android)

    releaseImplementation(libs.firebase.crashlytics)
}
