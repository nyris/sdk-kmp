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
    id("io.nyris.gradle.library")
    id("io.nyris.gradle.publisher")
}

android {
    namespace = "io.nyris.sdk.camera"
    buildFeatures {
        viewBinding = true
    }
    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
        }
    }
}

dependencies {
    implementation(libs.androidx.core)
    implementation(libs.androidx.camera.lifecycle)
    implementation(libs.androidx.camera.view)
    implementation(libs.androidx.camera.camera2)

    api(project(Modules.sdk_camera_core_android))
    implementation(project(Modules.sdk_camera_feature_image_android))
    compileOnly(project(Modules.sdk_camera_feature_barcode_android))


    testImplementation(libs.test.junit.params)
    implementation(project(Modules.sdk_camera_feature_barcode_android))
}

tasks.create("publishCameraModulesToGithub") {
    dependsOn(":sdk.camera:android:camera-core:publishAllPublicationsToGithubRepository")
    dependsOn(":sdk.camera:android:camera-feature-image:publishAllPublicationsToGithubRepository")
    dependsOn(":sdk.camera:android:camera-feature-barcode:publishAllPublicationsToGithubRepository")
    dependsOn(":sdk.camera:android:camera-view:publishAllPublicationsToGithubRepository")
}

tasks.create("publishCameraModulesToLocal") {
    dependsOn(":sdk.camera:android:camera-core:publishAllPublicationsToLocalMavenRepository")
    dependsOn(":sdk.camera:android:camera-feature-image:publishAllPublicationsToLocalMavenRepository")
    dependsOn(":sdk.camera:android:camera-feature-barcode:publishAllPublicationsToLocalMavenRepository")
    dependsOn(":sdk.camera:android:camera-view:publishAllPublicationsToLocalMavenRepository")
}
