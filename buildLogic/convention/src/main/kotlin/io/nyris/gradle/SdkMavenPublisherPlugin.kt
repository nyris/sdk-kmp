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

import io.nyris.gradle.utils.Configuration
import io.nyris.gradle.utils.Vars
import java.time.LocalDate
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.SigningExtension
import org.gradle.plugins.signing.SigningPlugin

class SdkMavenPublisherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(MavenPublishPlugin::class.java)
            pluginManager.apply(SigningPlugin::class.java)
            group = "io.nyris.sdk"
            version = rootProject.extra["LIB_VERSION_NAME"]
                .toString()
                .cleanVersionName()

            afterEvaluate {
                configurePublishing()
            }
        }
    }

    private fun Project.configurePublishing() {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "Github"
                    url = uri("https://maven.pkg.github.com/nyris/sdk-kmp")
                    credentials {
                        username = rootProject.extra["NYRIS_BOT_USER"].toString()
                        password = rootProject.extra["NYRIS_BOT_TAP"].toString()
                    }
                }
                maven {
                    name = "LocalMaven"
                    url = uri("file://$rootDir/build/maven")
                }
            }

            publications {
                // KMM project has already a defined project Published, in this case we need to make sure to
                // use the publication and attach to it the pom configuration
                if (pluginManager.hasPlugin(Configuration.kotlinMultiplatform)) {
                    withType<MavenPublication> {
                        configurePom(this@configurePublishing)
                        configureSigning(this)
                    }
                } else {
                    create<MavenPublication>("release") {
                        from(components.findByName("release"))
                        configurePom(this@configurePublishing)
                        if (Vars.IS_CI) {
                            configureSigning(this)
                        }
                    }
                }
            }
        }
    }

    private fun Project.configureSigning(mavenPublication: MavenPublication) {
        configure<SigningExtension> {
            useInMemoryPgpKeys(
                rootProject.extra["MAVEN_GPG_KEY_ID"].toString(),
                file("$rootDir/configs/signing/release-maven.txt").readText(),
                rootProject.extra["MAVEN_GPG_PASSWORD"].toString()
            )
            sign(mavenPublication)
        }
    }

    private fun MavenPublication.configurePom(target: Project) {
        pom {
            name.set("Nyris SDK - ${target.name}")
            description.set("Nyris SDK - ${target.name}")
            inceptionYear.set(LocalDate.now().year.toString())
            url.set("https://github.com/nyris/sdk-kmp")

            organization {
                name.set("nyris GmbH")
                url.set("https://www.nyris.io/")
            }

            licenses {
                license {
                    name.set("APACHE LICENSE, VERSION 2.0")
                    url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                }
            }

            developers {
                developer {
                    id.set("github/nyris")
                    name.set("nyris GmbH")
                    email.set("hi@nyris.io")
                }
                // Add all other devs here...
            }

            issueManagement {
                system.set("Github")
                url.set("https://github.com/nyris/sdk-kmp/issues")
            }

            scm {
                connection.set("https://github.com/nyris/sdk-kmp.git")
                url.set("https://github.com/nyris/sdk-kmp")
            }
        }
    }
}

private fun String.cleanVersionName(): String = if (this.contains("-")) {
    split("-").run {
        this[size - 1]
    }
} else {
    this
}
