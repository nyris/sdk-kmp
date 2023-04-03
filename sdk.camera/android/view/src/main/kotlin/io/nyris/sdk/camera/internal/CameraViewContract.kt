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
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import io.nyris.sdk.camera.Result
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.feature.barcode.BarcodeInternal
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import kotlin.reflect.KClass

internal interface CameraViewContract {
    interface Presenter {
        fun attach(
            view: View,
        )

        fun <R : Result> capture(
            kClass: KClass<R>,
        )

        fun setFocusPoint(
            x: Float,
            y: Float,
        )

        fun enableTorch()

        fun disableTorch()

        fun start()

        fun stop()

        fun release()

        fun showDebug(isEnabled: Boolean)
    }

    @Suppress("TooManyFunctions")
    interface View {
        fun context(): Context
        fun previewView(): PreviewView
        fun lifecycleOwner(): LifecycleOwner

        fun setFocusModeDebugInfo(focusMode: FocusModeEnum)
        fun setCaptureModeDebugInfo(captureMode: CaptureModeEnum)
        fun setCompressionFormatDebugInfo(compressionFormat: CompressionFormatEnum)
        fun setCompressionQualityDebugInfo(@IntRange(from = MIN_QUALITY, to = MAX_QUALITY) quality: Int)
        fun setCameraStateDebugInfo(type: CameraState.Type)
        fun setTorchDebugDebugInfo(isEnabled: Boolean?)
        fun setImageDebugInfo(
            elapsed: Long,
            originalImage: ByteArray,
            optimizedImage: ByteArray,
        )

        fun showDebugInfo()
        fun hideDebugInfo()

        fun setBarcodesDebugInfo(barcodes: List<BarcodeInternal>)

        fun onTorchStateChanged(isEnabled: Boolean?)
        fun observeTouch()

        fun <R : Result> onResult(result: R)
        fun onError(error: CameraError)
    }
}
