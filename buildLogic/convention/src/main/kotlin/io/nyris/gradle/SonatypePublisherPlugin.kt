package io.nyris.gradle

import io.github.gradlenexus.publishplugin.NexusPublishExtension
import io.github.gradlenexus.publishplugin.NexusPublishPlugin
import java.net.URI
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra

class SonatypePublisherPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply(NexusPublishPlugin::class.java)

            configure<NexusPublishExtension> {
                repositories {
                    sonatype {
                        nexusUrl.set(URI.create("https://s01.oss.sonatype.org/service/local/"))
                        username.set(rootProject.extra["SONATYPE_OSSRH_USERNAME"].toString())
                        password.set(rootProject.extra["SONATYPE_OSSRH_PASSWORD"].toString())
                        stagingProfileId.set(rootProject.extra["SONATYPE_STAGING_PROFILE_ID"].toString())
                    }
                }
            }
        }
    }
}
