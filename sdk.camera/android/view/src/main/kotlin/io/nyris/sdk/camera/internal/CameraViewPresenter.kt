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

import androidx.annotation.IntRange
import io.nyris.sdk.camera.BarcodeResult
import io.nyris.sdk.camera.ImageResult
import io.nyris.sdk.camera.Result
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.feature.barcode.BarcodeResultInternal
import io.nyris.sdk.camera.feature.image.ImageResultInternal
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import kotlin.reflect.KClass

internal class CameraViewPresenter(
    private val focusMode: FocusModeEnum,
    private val captureMode: CaptureModeEnum,
    private val compressionFormat: CompressionFormatEnum,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    private val quality: Int,
    @BarcodeFormat
    private val barcodeFormat: Int,
) : CameraViewContract.Presenter {
    private var isDebug: Boolean = false
    internal var cameraManager: CameraManager? = null
    internal var view: CameraViewContract.View? = null

    override fun attach(
        view: CameraViewContract.View,
    ) {
        this.view = view
        setDebugInfo(focusMode, captureMode, compressionFormat, quality)
        cameraManager = CameraManager.createInstance(
            context = view.context(),
            previewView = view.previewView(),
            captureMode = captureMode,
            focusMode = focusMode,
            lifecycleOwner = view.lifecycleOwner(),
            compressionFormat = compressionFormat,
            quality = quality,
            barcodeFormat = barcodeFormat
        )
        observeErrors()
        observeCameraState()
        observeTorchState()
        observeTouch(focusMode)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R : Result> capture(
        kClass: KClass<R>,
    ) {
        cameraManager?.capture<ResultInternal> captureInternal@{
            val captureResult = it ?: return@captureInternal
            val result = when (captureResult) {
                is ImageResultInternal -> {
                    if (isDebug) {
                        with(captureResult) {
                            view?.setImageDebugInfo(elapsed, originalImage, optimizedImage)
                        }
                    }
                    captureResult.toImageResult()
                }
                is BarcodeResultInternal -> {
                    if (isDebug) {
                        view?.setBarcodesDebugInfo(captureResult.barcodes)
                    }
                    captureResult.toBarcodeResult()
                }
                else -> throw IllegalArgumentException("Result type not handled here !")
            }

            if (result::class == kClass) {
                view?.onResult(result as R)
            }
        }
    }

    override fun enableTorch() {
        cameraManager?.enableTorch()
    }

    override fun disableTorch() {
        cameraManager?.disableTorch()
    }

    override fun showDebug(isEnabled: Boolean) {
        isDebug = isEnabled
        if (isEnabled) {
            view?.showDebugInfo()
        } else {
            view?.hideDebugInfo()
        }
    }

    override fun start() {
        cameraManager?.bind()
    }

    override fun stop() {
        cameraManager?.unbind()
    }

    override fun release() {
        view = null
        cameraManager?.release()
    }

    override fun setFocusPoint(
        x: Float,
        y: Float,
    ) {
        cameraManager?.setManualFocus(x, y)
    }

    private fun setDebugInfo(
        focusMode: FocusModeEnum,
        captureMode: CaptureModeEnum,
        compressionFormat: CompressionFormatEnum,
        @IntRange(from = MIN_QUALITY, to = MAX_QUALITY) quality: Int,
    ) {
        view?.setFocusModeDebugInfo(focusMode)
        view?.setCaptureModeDebugInfo(captureMode)
        view?.setCompressionFormatDebugInfo(compressionFormat)
        view?.setCompressionQualityDebugInfo(quality)
    }

    private fun observeErrors() {
        cameraManager?.error { error ->
            view?.onError(error)
        }
    }

    private fun observeCameraState() {
        cameraManager?.state { state ->
            view?.setCameraStateDebugInfo(state.type)
        }
    }

    private fun observeTorchState() {
        cameraManager?.torchState { isEnabled ->
            view?.onTorchStateChanged(isEnabled)
            view?.setTorchDebugDebugInfo(isEnabled)
        }
    }

    private fun observeTouch(focusMode: FocusModeEnum) {
        if (focusMode == FocusModeEnum.Automatic) return
        view?.observeTouch()
    }
}

internal fun BarcodeResultInternal.toBarcodeResult(): BarcodeResult = BarcodeResult(
    barcodes = this.barcodes,
)

internal fun ImageResultInternal.toImageResult(): ImageResult = ImageResult(
    originalImage = this.originalImage,
    optimizedImage = this.optimizedImage,
)