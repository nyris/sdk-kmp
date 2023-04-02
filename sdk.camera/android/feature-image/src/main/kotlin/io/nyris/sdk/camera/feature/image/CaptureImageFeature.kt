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
package io.nyris.sdk.camera.feature.image

import androidx.annotation.IntRange
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCase
import androidx.camera.core.impl.ImageOutputConfig.RotationValue
import androidx.camera.view.PreviewView
import io.nyris.sdk.camera.core.CAMERA_ERROR_CAPTURE_LENS
import io.nyris.sdk.camera.core.CAMERA_ERROR_CAPTURE_SCREENSHOT
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.core.Time
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageResultInternal(
    val elapsed: Long,
    val originalImage: ByteArray,
    val optimizedImage: ByteArray,
) : ResultInternal

sealed class CaptureImageFeature(protected val captureExecutor: ExecutorService) : ImageFeature<ImageResultInternal> {
    override fun shutdown() {
        captureExecutor.shutdown()
    }
}

class LensCaptureImageFeature internal constructor(
    captureExecutor: ExecutorService,
    private val compressionFormat: CompressionFormatEnum,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    private val quality: Int,
    private val imageCaptureWrapper: ImageCaptureWrapper,
) : CaptureImageFeature(captureExecutor) {
    override fun useCase(): UseCase = imageCaptureWrapper.imageCaptureUseCase

    override fun process(
        resultCallback: (ImageResultInternal) -> Unit,
        errorCallback: (CameraError) -> Unit,
    ) {
        val start = Time.currentTimeMillis()
        imageCaptureWrapper.takePicture(
            captureExecutor,
            object : OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    super.onCaptureSuccess(imageProxy)
                    val originalImage =
                        imageProxy.toCorrectBitmap().toByteArray(compressionFormat = CompressionFormatEnum.Jpeg)
                    val optimizedImage =
                        originalImage.optimize(compressionFormat = compressionFormat, quality = quality)
                    val elapsed = Time.currentTimeMillis() - start
                    resultCallback(ImageResultInternal(elapsed, originalImage, optimizedImage))
                }

                override fun onError(e: ImageCaptureException) {
                    super.onError(e)
                    errorCallback(
                        CameraError(
                            CAMERA_ERROR_CAPTURE_LENS, "Can't capture image using lens! ${e.message}"
                        )
                    )
                }
            }
        )
    }

    companion object {
        fun createInstance(
            @RotationValue
            rotation: Int,
            compressionFormat: CompressionFormatEnum,
            @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
            quality: Int,
        ): LensCaptureImageFeature {
            val imageCaptureUseCase = ImageCapture.Builder()
                .setTargetRotation(rotation)
                .build()
            val captureExecutor = Executors.newSingleThreadExecutor()
            return LensCaptureImageFeature(
                captureExecutor = captureExecutor,
                compressionFormat = compressionFormat,
                quality = quality,
                imageCaptureWrapper = ImageCaptureWrapper(imageCaptureUseCase)
            )
        }
    }
}

class ScreenshotCaptureImageFeature(
    captureExecutor: ExecutorService,
    private val previewView: PreviewView,
    private val compressionFormat: CompressionFormatEnum,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    private val quality: Int,
) : CaptureImageFeature(captureExecutor) {
    override fun useCase(): UseCase? = null

    override fun process(
        resultCallback: (ImageResultInternal) -> Unit,
        errorCallback: (CameraError) -> Unit,
    ) {
        try {
            val start = Time.currentTimeMillis()
            val bitmap = previewView.bitmap!!
            captureExecutor.execute {
                val originalImage = bitmap.toByteArray(compressionFormat = CompressionFormatEnum.Jpeg)
                val optimizedImage = originalImage.optimize(compressionFormat = compressionFormat, quality = quality)
                val elapsed = Time.currentTimeMillis() - start
                resultCallback(ImageResultInternal(elapsed, originalImage, optimizedImage))
            }
        } catch (e: Exception) {
            errorCallback(
                CameraError(
                    CAMERA_ERROR_CAPTURE_SCREENSHOT, "Can't capture image using screenshot! ${e.message}"
                )
            )
        }
    }

    companion object {
        fun createInstance(
            previewView: PreviewView,
            compressionFormat: CompressionFormatEnum,
            @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
            quality: Int,
        ): ScreenshotCaptureImageFeature {
            val captureExecutor = Executors.newSingleThreadExecutor()
            return ScreenshotCaptureImageFeature(
                previewView = previewView,
                captureExecutor = captureExecutor,
                compressionFormat = compressionFormat,
                quality = quality,
            )
        }
    }
}

internal class ImageCaptureWrapper(internal val imageCaptureUseCase: ImageCapture) {
    fun takePicture(
        executor: Executor,
        callback: OnImageCapturedCallback,
    ) {
        imageCaptureUseCase.takePicture(executor, callback)
    }
}

const val DEFAULT_QUALITY = 90
const val MIN_QUALITY = 10L
const val MAX_QUALITY = 100L
