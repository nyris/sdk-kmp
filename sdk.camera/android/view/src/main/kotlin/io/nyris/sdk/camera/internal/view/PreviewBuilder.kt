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
package io.nyris.sdk.camera.internal.view

import android.view.Surface
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.impl.ImageOutputConfig.RotationValue

internal class PreviewBuilder private constructor() {
    companion object {
        fun createInstance(): PreviewBuilder = PreviewBuilder()
    }

    @RotationValue
    private var rotation: Int = Surface.ROTATION_0
    private var surfaceProvider: Preview.SurfaceProvider? = null

    fun setTargetRotation(@RotationValue rotation: Int) = apply {
        this.rotation = rotation
    }

    fun setSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) = apply {
        this.surfaceProvider = surfaceProvider
    }

    fun build(): UseCase = Preview.Builder()
        .setTargetRotation(rotation)
        .build().apply {
            setSurfaceProvider(surfaceProvider)
        }
}
