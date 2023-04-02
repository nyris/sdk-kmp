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
package io.nyris.sdk.camera.internal

import android.content.Context
import androidx.annotation.IntRange
import androidx.camera.core.CameraState
import androidx.camera.core.impl.ImageOutputConfig.RotationValue
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CaptureModeEnum.Barcode
import io.nyris.sdk.camera.core.CaptureModeEnum.Lens
import io.nyris.sdk.camera.core.CaptureModeEnum.Screenshot
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.feature.barcode.BarcodeImageFeature
import io.nyris.sdk.camera.feature.image.LensCaptureImageFeature
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import io.nyris.sdk.camera.feature.image.ScreenshotCaptureImageFeature

internal interface CameraManager {
    companion object {
        @Suppress("LongParameterList")
        fun createInstance(
            context: Context,
            lifecycleOwner: LifecycleOwner,
            previewView: PreviewView,
            captureMode: CaptureModeEnum,
            focusMode: FocusModeEnum,
            compressionFormat: CompressionFormatEnum,
            @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
            quality: Int,
            @BarcodeFormat
            barcodeFormat: Int,
        ): CameraManager {
            val rotation = previewView.display.rotation
            val imageFeature =
                createImageFeature(captureMode, previewView, compressionFormat, quality, rotation, barcodeFormat)
            return CameraManagerImpl(
                context = context,
                focusMode = focusMode,
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                imageFeature = imageFeature,
            )
        }

        @Suppress("LongParameterList")
        internal fun createImageFeature(
            captureMode: CaptureModeEnum,
            previewView: PreviewView,
            compressionFormat: CompressionFormatEnum,
            @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
            quality: Int,
            @RotationValue
            rotation: Int,
            @BarcodeFormat
            barcodeFormat: Int,
        ): ImageFeature<ResultInternal> = when (captureMode) {
            Screenshot -> ScreenshotCaptureImageFeature.createInstance(previewView, compressionFormat, quality)
            Lens -> LensCaptureImageFeature.createInstance(rotation, compressionFormat, quality)
            Barcode -> BarcodeImageFeature.createInstance(rotation, barcodeFormat)
        }
    }

    fun bind()

    fun unbind()

    fun state(block: (CameraState) -> Unit)

    fun <R : ResultInternal> capture(block: (R?) -> Unit)

    fun torchState(block: (Boolean?) -> Unit)

    fun enableTorch()

    fun disableTorch()

    fun release()

    fun setManualFocus(
        x: Float,
        y: Float,
    )

    fun error(block: (CameraError) -> Unit)
}
