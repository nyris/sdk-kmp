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

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.IntRange
import androidx.camera.core.CameraState
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import io.nyris.sdk.camera.CameraView
import io.nyris.sdk.camera.R
import io.nyris.sdk.camera.Result
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.camera.core.FeatureModeEnum
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.databinding.NyrisCameraViewBinding
import io.nyris.sdk.camera.feature.barcode.BarcodeInternal
import io.nyris.sdk.camera.feature.barcode.BarcodeOverlayDrawable
import io.nyris.sdk.camera.feature.image.DEFAULT_QUALITY
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import io.nyris.sdk.camera.internal.CameraViewContract
import io.nyris.sdk.camera.internal.CameraViewPresenter
import io.nyris.sdk.camera.internal.required
import io.nyris.sdk.camera.internal.toBarcodeFormat
import io.nyris.sdk.camera.internal.toCaptureMode
import io.nyris.sdk.camera.internal.toCompressionFormat
import io.nyris.sdk.camera.internal.toFeatureModeEnumList
import io.nyris.sdk.camera.internal.toFocusMode
import kotlin.reflect.KClass

@Suppress("LongParameterList")
internal class CameraViewDelegate(
    private val cameraView: CameraView,
    attrs: TypedArray,
    featureModes: Int = 0,
    focusMode: Int = 0,
    captureMode: Int = 0,
    compressionFormat: Int = 0,
    quality: Int = DEFAULT_QUALITY,
    barcodeFormat: Int = 0,
    isBarcodeGuideEnabled: Boolean = false,
) : CameraViewContract.View {
    private val lifecycleOwner = (cameraView.context as? LifecycleOwner).required {
        "Lifecycle Owner is required to use this camera view! Please make sure that your activity/fragment " +
            "is a lifecycle owner."
    }

    private val binding: NyrisCameraViewBinding = NyrisCameraViewBinding.inflate(
        LayoutInflater.from(cameraView.context), cameraView
    )
    private var presenter: CameraViewContract.Presenter? = null

    private var torchStateBlock: TorchStateBlock? = null
    private var errorBlock: ErrorBlock? = null
    private var captureBlockMap: MutableMap<Int, CaptureBlock<Result>?> = mutableMapOf()

    init {
        val featureModeEnumList =
            attrs.getInt(R.styleable.CameraView_feature_modes, featureModes).toFeatureModeEnumList()
        val focusModeEnum =
            attrs.getInt(R.styleable.CameraView_focus_mode, focusMode).toFocusMode()
        val captureModeEnum =
            attrs.getInt(R.styleable.CameraView_capture_mode, captureMode).toCaptureMode()
        val compressionModeEnum =
            attrs.getInt(R.styleable.CameraView_compression_format, compressionFormat).toCompressionFormat()
        val qualityValue =
            attrs.getInt(R.styleable.CameraView_quality, quality).takeIf {
                it in MIN_QUALITY..MAX_QUALITY
            }.required {
                "Quality should be in range $MIN_QUALITY and $MAX_QUALITY"
            }
        val barcodeFormatEnum =
            attrs.getInt(R.styleable.CameraView_barcode_format, barcodeFormat).toBarcodeFormat()
        attrs.recycle()

        cameraView.post {
            if (cameraView.isInEditMode) return@post

            presenter = CameraViewPresenter(
                featureModeEnumList,
                focusModeEnum,
                captureModeEnum,
                compressionModeEnum,
                qualityValue,
                barcodeFormatEnum,
            ).apply {
                attach(this@CameraViewDelegate)
            }

            setPreviewSizeDebugInfo()

            if (isBarcodeGuideEnabled && featureModeEnumList.contains(FeatureModeEnum.Barcode)) {
                binding.previewView.overlay.add(
                    BarcodeOverlayDrawable(
                        barcodeFormat = barcodeFormat,
                        parentWidth = cameraView.width.toFloat(),
                        parentHeight = cameraView.height.toFloat(),
                        context = context()
                    )
                )
            }
        }
    }

    override fun context(): Context = cameraView.context

    override fun previewView(): PreviewView = binding.previewView

    override fun lifecycleOwner(): LifecycleOwner = lifecycleOwner

    override fun setFeatureModeInfo(featureModes: List<FeatureModeEnum>) {
        binding.debugView.setFeatureMode(featureModes)
    }

    override fun setFocusModeDebugInfo(focusMode: FocusModeEnum) {
        binding.debugView.setFocusModeDebugInfo(focusMode)
    }

    override fun setCaptureModeDebugInfo(captureMode: CaptureModeEnum) {
        binding.debugView.setCaptureModeDebugInfo(captureMode)
    }

    override fun setCompressionFormatDebugInfo(compressionFormat: CompressionFormatEnum) {
        binding.debugView.setCompressionFormatDebugInfo(compressionFormat)
    }

    override fun setCompressionQualityDebugInfo(@IntRange(from = MIN_QUALITY, to = MAX_QUALITY) quality: Int) {
        binding.debugView.setCompressionQuality(quality)
    }

    override fun setCameraStateDebugInfo(type: CameraState.Type) {
        binding.debugView.setCameraStateDebugInfo(type)
    }

    override fun setTorchDebugDebugInfo(isEnabled: Boolean?) {
        binding.debugView.setTorchDebugInfo(isEnabled)
    }

    override fun setImageDebugInfo(
        elapsed: Long,
        originalImage: ByteArray,
        optimizedImage: ByteArray,
    ) {
        binding.debugView.post {
            binding.debugView.setImageDebugInfo(elapsed, originalImage, optimizedImage)
        }
    }

    override fun showDebugInfo() {
        binding.debugView.visibility = FrameLayout.VISIBLE
    }

    override fun hideDebugInfo() {
        binding.debugView.visibility = FrameLayout.GONE
    }

    override fun setBarcodesDebugInfo(barcodes: List<BarcodeInternal>) {
        binding.debugView.setBarcodesDebugInfo(barcodes)
    }

    override fun onTorchStateChanged(isEnabled: Boolean?) {
        torchStateBlock?.invoke(isEnabled)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun observeTouch() {
        val focusView = FocusMarkerView(cameraView.context)
        cameraView.addView(focusView)
        binding.previewView.setOnTouchListener { _, event ->
            return@setOnTouchListener when (event.action) {
                MotionEvent.ACTION_DOWN -> true
                MotionEvent.ACTION_UP -> {
                    focusView.render(event.x, event.y)
                    presenter?.setFocusPoint(event.x, event.y)
                    true
                }
                else -> false
            }
        }
    }

    override fun <R : Result> onResult(
        @FeatureMode featureMode: Int,
        result: R,
    ) {
        captureBlockMap[featureMode]?.invoke(result)
    }

    override fun onError(error: CameraError) {
        errorBlock?.invoke(error)
    }

    private fun setPreviewSizeDebugInfo() {
        with(binding.previewView) {
            binding.debugView.setPreviewSizeDebugInfo(width, height)
        }
    }

    @Suppress("UNCHECKED_CAST")
    internal fun <R : Result> capture(
        @FeatureMode
        featureMode: Int,
        kClass: KClass<R>,
        block: CaptureBlock<R>?,
    ) {
        captureBlockMap[featureMode] = block as? CaptureBlock<Result>
        cameraView.post {
            presenter?.capture(featureMode, kClass)
        }
    }

    internal fun enableTorch() {
        cameraView.post {
            presenter?.enableTorch()
        }
    }

    internal fun disableTorch() {
        cameraView.post {
            presenter?.disableTorch()
        }
    }

    internal fun torchState(block: TorchStateBlock?) {
        torchStateBlock = block
    }

    internal fun error(block: ErrorBlock?) {
        errorBlock = block
    }

    internal fun start() {
        cameraView.post {
            presenter?.start()
        }
    }

    internal fun stop() {
        cameraView.post {
            presenter?.stop()
        }
    }

    internal fun release() {
        errorBlock = null
        torchStateBlock = null
        presenter?.release()
    }

    internal fun showDebug(isEnabled: Boolean) {
        cameraView.post {
            presenter?.showDebug(isEnabled)
        }
    }
}

internal typealias CaptureBlock<R> = (R) -> Unit
internal typealias ErrorBlock = (CameraError) -> Unit
internal typealias TorchStateBlock = (Boolean?) -> Unit
