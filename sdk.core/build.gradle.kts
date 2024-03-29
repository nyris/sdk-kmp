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
plugins {
    id("io.nyris.gradle.multiplatform.library")
    id("io.nyris.gradle.publisher")
    id("kotlinx-serialization")
}

android {
    namespace = "io.nyris.sdk"
}

kotlin {
    android {
        publishLibraryVariants("release")
    }
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(libs.ktx.coroutines.core)
                implementation(libs.ktor.core)
                implementation(libs.ktor.json)
                implementation(libs.ktor.loggin)
                implementation(libs.ktor.contentnegotiation)
                implementation(libs.ktor.serialization)

                implementation(libs.ktx.json)

                implementation(libs.ktx.date)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
                implementation(libs.test.mockk.core)
                implementation(libs.test.coroutines)
            }
        }
        val androidMain by getting {
            dependencies {
                api(libs.ktor.android)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.test.mockk.core)
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.ktor.jvm)
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
                implementation(libs.test.mockk.core)
            }
        }
    }
}