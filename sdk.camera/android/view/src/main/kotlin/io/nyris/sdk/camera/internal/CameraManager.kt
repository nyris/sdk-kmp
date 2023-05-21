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
import androidx.camera.core.CameraState
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CaptureModeEnum.Lens
import io.nyris.sdk.camera.core.CaptureModeEnum.Screenshot
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.feature.barcode.BarcodeImageFeature
import io.nyris.sdk.camera.feature.image.LensCaptureImageFeature
import io.nyris.sdk.camera.feature.image.ScreenshotCaptureImageFeature

internal interface CameraManager {
    companion object {
        @Suppress("LongParameterList")
        fun createInstance(
            context: Context,
            lifecycleOwner: LifecycleOwner,
            previewView: PreviewView,
            focusMode: FocusModeEnum,
            captureConfig: CaptureConfig,
            barcodeConfig: BarcodeConfig,
        ): CameraManager {
            val features = mapOf(
                FeatureMode.CAPTURE to createImageFeature(captureConfig, previewView),
                FeatureMode.BARCODE to createBarcodeFeature(barcodeConfig)
            )
            return CameraManagerImpl(
                context = context,
                focusMode = focusMode,
                previewView = previewView,
                lifecycleOwner = lifecycleOwner,
                featuresMap = features,
            )
        }

        internal fun createImageFeature(
            captureConfig: CaptureConfig,
            previewView: PreviewView,
        ): ImageFeature<ResultInternal>? = with(captureConfig) {
            when (captureConfig.captureMode) {
                Screenshot -> ScreenshotCaptureImageFeature.createInstance(
                    previewView,
                    compressionFormat,
                    quality
                )
                Lens -> LensCaptureImageFeature.createInstance(
                    rotation,
                    compressionFormat,
                    quality
                )
                CaptureModeEnum.Barcode -> null
            }.takeIf { isEnabled }
        }

        internal fun createBarcodeFeature(
            barcodeConfig: BarcodeConfig,
        ): ImageFeature<ResultInternal>? = with(barcodeConfig) {
            BarcodeImageFeature.createInstance(rotation, barcodeFormat).takeIf { isEnabled }
        }
    }

    fun bind()

    fun unbind()

    fun state(block: (CameraState) -> Unit)

    @Deprecated(message = "Will be removed with the release of 1.2, Start using capture(feature: FeatureEnum)")
    fun <R : ResultInternal> capture(block: (R?) -> Unit)

    fun <R : ResultInternal> capture(
        @FeatureMode
        feature: Int,
        block: (R?) -> Unit,
    )

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
