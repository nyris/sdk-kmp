@file:Suppress("DSL_SCOPE_VIOLATION")

//https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    id("io.nyris.gradle.configuration")
    id("io.nyris.gradle.detekt")
    id("io.nyris.gradle.sonatype-publisher")

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.testlogger) apply false
    alias(libs.plugins.firebase.appdist) apply false
    alias(libs.plugins.firebase.crashlytics) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.nexus.publish) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        // Keeping this here to allow Android Studio to do automatic upgrade
        classpath("com.android.tools.build:gradle:7.4.0")
    }
}