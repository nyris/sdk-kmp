plugins {
    `kotlin-dsl`
}
group = "io.nyris.sdk.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    google()
    gradlePluginPortal()
}

dependencies {
    compileOnly(libs.android.plugin)
    compileOnly(libs.kotlin.plugin)
    compileOnly(libs.detekt.plugin)
    compileOnly(libs.testlogger.plugin)
    compileOnly(libs.firebase.appdist.plugin)
    compileOnly(libs.firebase.crashlytics.plugin)
}

gradlePlugin {
    plugins {
        register("detekt") {
            id = "io.nyris.gradle.detekt"
            implementationClass = "io.nyris.gradle.DetektConventionPlugin"
        }

        register("android-app-plugin") {
            id = "io.nyris.gradle.app"
            implementationClass = "io.nyris.gradle.AndroidApplicationPlugin"
        }

        register("android-library-plugin") {
            id = "io.nyris.gradle.library"
            implementationClass = "io.nyris.gradle.AndroidLibraryPlugin"
        }

        register("android-library-serialization-plugin") {
            id = "io.nyris.gradle.library.serialization"
            implementationClass = "io.nyris.gradle.AndroidLibrarySerializationPlugin"
        }

        register("root-build-gradle-config-plugin") {
            id = "io.nyris.gradle.configuration"
            implementationClass = "io.nyris.gradle.RootBuildGradleConfigPlugin"
        }

        register("android-multiplatform-library-plugin") {
            id = "io.nyris.gradle.multiplatform.library"
            implementationClass = "io.nyris.gradle.MultiplatformAndroidLibraryPlugin"
        }

        register("sdk-maven-publisher-plugin") {
            id = "io.nyris.gradle.publisher"
            implementationClass = "io.nyris.gradle.SdkMavenPublisherPlugin"
        }

        register("app-dist-plugin") {
            id = "io.nyris.gradle.appdist"
            implementationClass = "io.nyris.gradle.ApplicationDistributorConventionPlugin"
        }

        register("crash-reporter-plugin") {
            id = "io.nyris.gradle.crash-reporter"
            implementationClass = "io.nyris.gradle.ApplicationCrashReporterConventionPlugin"
        }
    }
}