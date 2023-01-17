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

import io.gitlab.arturbosch.detekt.Detekt
import io.nyris.gradle.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.dependencies as dependenciesKt

class DetektConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply("io.gitlab.arturbosch.detekt")
            tasks.named<Detekt>("detekt") {
                description = "Run detekt on root project"
                config.setFrom(files("$projectDir/configs/code/detekt.yml"))
                setSource(files(projectDir))
                include("**/*.kt")
                exclude("**/*.kts")
                exclude("**/build/")
                exclude("**/bin/")
                exclude("**/spotless/")

                reports {
                    html {
                        required.set(true)
                        outputLocation.set(file("build/reports/detekt/detekt.html"))
                    }
                    sarif {
                        required.set(true)
                        outputLocation.set(file("build/reports/detekt/detekt.sarif"))
                    }
                }

                autoCorrect = true
                parallel = true

                dependenciesKt {
                    val detektFormatting = libs().findLibrary("detekt_formatting").get()
                    val detektCli = libs().findLibrary("detekt_cli").get()
                    val detektLibraries = libs().findLibrary("detekt_libraries").get()
                    add("detektPlugins", detektFormatting)
                    add("detektPlugins", detektLibraries)
                    add("detektPlugins", detektCli)
                }
            }
        }
    }
}
