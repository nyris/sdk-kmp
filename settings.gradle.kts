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

include(":sdk.camera:android:view")
project(":sdk.camera:android:view").name = "camera-view"

include(":sdk.camera:android:core")
project(":sdk.camera:android:core").name = "camera-core"

include(":sdk.camera:android:feature-image")
project(":sdk.camera:android:feature-image").name = "camera-feature-image"

include(":sdk.camera:android:feature-barcode")
project(":sdk.camera:android:feature-barcode").name = "camera-feature-barcode"

include(":sdk.ui:android")
project(":sdk.ui:android").name = "ui"

include(":sdk.demos:android")
include(":sdk.demos:console")

