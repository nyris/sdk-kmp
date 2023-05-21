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
package io.nyris.sdk.camera.core

import androidx.annotation.IntDef
import io.nyris.sdk.camera.core.FeatureMode.Companion.BARCODE
import io.nyris.sdk.camera.core.FeatureMode.Companion.CAPTURE

@IntDef(CAPTURE, BARCODE)
@Retention(AnnotationRetention.SOURCE)
annotation class FeatureMode {
    companion object {
        const val CAPTURE = 0
        const val BARCODE: Int = 1
    }
}
