rootProject.name = "sdk-kmp"

pluginManagement {
    includeBuild("buildLogic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

include(":sdk.core")
project(":sdk.core").name = "core"

include(":sdk.camera:android")
project(":sdk.camera:android").name = "camera"

include(":sdk.ui:android")
project(":sdk.ui:android").name = "ui"

include(":sdk.demos:android")
include(":sdk.demos:console")

