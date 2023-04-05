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

import io.nyris.gradle.utils.Vars
import java.io.ByteArrayOutputStream
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtraPropertiesExtension
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.repositories

class RootBuildGradleConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            extra.loadExtras(this)

            allprojects {
                repositories {
                    google()
                    mavenCentral()
                    maven {
                        url = uri("file://$rootDir/build/maven")
                    }
                }
            }

            applyWorkaroundIssue247906487()
        }
    }
}

private fun ExtraPropertiesExtension.loadExtras(target: Project) {
    loadDemoVersionCode(target)
    loadLibVersionName(target)
    loadGithubProperties(target)
    loadSigningProperties(target)
}

private fun ExtraPropertiesExtension.loadDemoVersionCode(target: Project) {
    set("DEMO_VERSION_CODE", generateVersionCode(target))
}

private fun ExtraPropertiesExtension.loadLibVersionName(target: Project) {
    set("LIB_VERSION_NAME", generateVersionName(target))
}

private fun ExtraPropertiesExtension.loadGithubProperties(target: Project) {
    val propertiesFile = target.rootDir.resolve("local.properties")
    if (propertiesFile.exists()) {
        val properties = java.util.Properties()
        properties.load(propertiesFile.inputStream())

        set("NYRIS_BOT_USER", properties["NYRIS_BOT_USER"])
        set("NYRIS_BOT_TAP", properties["NYRIS_BOT_TAP"])
    } else {
        set("NYRIS_BOT_USER", Vars.NYRIS_BOT_USER ?: "")
        set("NYRIS_BOT_TAP", Vars.NYRIS_BOT_TAP ?: "")
    }
}

private fun ExtraPropertiesExtension.loadSigningProperties(target: Project) {
    val propertiesFile = target.rootDir.resolve("configs/signing/signing.properties")
    if (propertiesFile.exists()) {
        val properties = java.util.Properties()
        properties.load(propertiesFile.inputStream())

        set("RELEASE_KEYSTORE_PASSWORD", properties["RELEASE_KEYSTORE_PASSWORD"])
        set("RELEASE_KEY_ALIAS", properties["RELEASE_KEY_ALIAS"])
        set("RELEASE_KEY_PASSWORD", properties["RELEASE_KEY_PASSWORD"])

        set("MAVEN_GPG_KEY_ID", properties["MAVEN_GPG_KEY_ID"])
        set("MAVEN_GPG_PASSWORD", properties["MAVEN_GPG_PASSWORD"])
        set("MAVEN_GPG_KEY", properties["MAVEN_GPG_KEY"])

        set("SONATYPE_OSSRH_USERNAME", properties["SONATYPE_OSSRH_USERNAME"])
        set("SONATYPE_OSSRH_PASSWORD", properties["SONATYPE_OSSRH_PASSWORD"])
        set("SONATYPE_STAGING_PROFILE_ID", properties["SONATYPE_STAGING_PROFILE_ID"])
    } else {
        set("RELEASE_KEYSTORE_PASSWORD", "")
        set("RELEASE_KEY_ALIAS", "")
        set("RELEASE_KEY_PASSWORD", "")

        set("MAVEN_GPG_KEY_ID", "")
        set("MAVEN_GPG_PASSWORD", "")
        set("MAVEN_GPG_KEY", "")

        set("SONATYPE_OSSRH_USERNAME", "")
        set("SONATYPE_OSSRH_PASSWORD", "")
        set("SONATYPE_STAGING_PROFILE_ID", "")
    }
}

private fun generateVersionCode(project: Project): Int {
    return "git rev-list --count main".execute(project)?.toIntOrNull() ?: 1
}

private fun generateVersionName(project: Project): String {
    val versionName = Vars.LIB_VERSION_NAME

    return if (Vars.IS_CI && versionName != null) {
        versionName
    } else {
        val hash = "git rev-parse --short HEAD".execute(project)
        "${versionName ?: "0.0.1"}-snapshot-$hash"
    }
}

internal fun String.execute(project: Project): String? = try {
    val currentWorkingDir = project.file("./")
    val byteOut = ByteArrayOutputStream()
    project.exec {
        workingDir = currentWorkingDir
        commandLine = this@execute.split("\\s".toRegex())
        standardOutput = byteOut
    }
    String(byteOut.toByteArray()).trim()
} catch (ignore: Exception) {
    null
}

// https://issuetracker.google.com/issues/247906487
private fun applyWorkaroundIssue247906487() {
    val loggerFactory = org.slf4j.LoggerFactory.getILoggerFactory()
    val addNoOpLogger =
        loggerFactory.javaClass.getDeclaredMethod("addNoOpLogger", String::class.java)
    addNoOpLogger.isAccessible = true
    addNoOpLogger.invoke(
        loggerFactory,
        "com.android.build.api.component.impl.MutableListBackedUpWithListProperty"
    )
    addNoOpLogger.invoke(
        loggerFactory,
        "com.android.build.api.component.impl.MutableMapBackedUpWithMapProperty"
    )
}
