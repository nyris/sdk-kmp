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
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.camera.core.CameraState
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FeatureModeEnum
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.databinding.NyrisCameraDebugViewBinding
import io.nyris.sdk.camera.feature.barcode.BarcodeInternal
import io.nyris.sdk.camera.feature.image.toBitmap
import io.nyris.sdk.camera.internal.byteToKb
import io.nyris.sdk.camera.internal.byteToMb
import io.nyris.sdk.camera.internal.millisToSeconds

@SuppressLint("SetTextI18n")
internal class CameraDebugView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : LinearLayout(context, attrs, defStyleAttr) {
    private val binding: NyrisCameraDebugViewBinding

    init {
        binding = NyrisCameraDebugViewBinding.inflate(LayoutInflater.from(context), this)
    }

    fun setFeatureMode(featureModes: List<FeatureModeEnum>) {
        binding.dFeatureMode.text = "Feature Modes: ${featureModes.joinToString(separator = "|") { it.name }}"
    }

    fun setCaptureModeDebugInfo(captureMode: CaptureModeEnum) {
        binding.dCaptureMode.text = "Capture Mode: ${captureMode.name}"
    }

    fun setFocusModeDebugInfo(focusMode: FocusModeEnum) {
        binding.dFocusMode.text = "Focus Mode: ${focusMode.name}"
    }

    fun setPreviewSizeDebugInfo(
        width: Int,
        height: Int,
    ) {
        binding.dPreviewSize.text = "Preview Size: ${width}x$height"
    }

    fun setBarcodesDebugInfo(barcodes: List<BarcodeInternal>) {
        val barcode = barcodes.first()
        binding.dBarcode.visibility = VISIBLE
        binding.dBarcode.text = "Barcode: ${barcode.code}\n" +
            "Barcode Format: ${barcode.format.toBarcodeFormatStr()}"
    }

    fun setImageDebugInfo(
        elapsed: Long,
        originalImage: ByteArray,
        optimizedImage: ByteArray,
    ) {
        with(binding) {
            dCaptureTime.text = "Capture time: ${elapsed.millisToSeconds()}s"

            val originalBitmap = originalImage.toBitmap()
            val optimizedBitmap = optimizedImage.toBitmap()
            post {
                dCapturedOriginalImageSize.text =
                    "${originalBitmap.width}x${originalBitmap.height} \n" +
                        "(${originalImage.size.byteToMb()}Mb) \n" +
                        "Original"
                dCapturedOriginalImage.setImageBitmap(originalBitmap)

                dCapturedOptimizedImageSize.text =
                    "${optimizedBitmap.width}x${optimizedBitmap.height} \n" +
                        "(${optimizedImage.size.byteToKb()}Kb) \n" +
                        "Optimized"
                dCapturedOptimizedImage.setImageBitmap(optimizedBitmap)

                dImageContainer.visibility = VISIBLE
            }
        }
    }

    fun setCameraStateDebugInfo(type: CameraState.Type) {
        with(binding) {
            val baseMessage = "Camera state:"
            when (type) {
                CameraState.Type.PENDING_OPEN -> dCameraState.text = "$baseMessage Pending Open"
                CameraState.Type.OPENING -> dCameraState.text = "$baseMessage Opening"
                CameraState.Type.OPEN -> dCameraState.text = "$baseMessage Open"
                CameraState.Type.CLOSING -> dCameraState.text = "$baseMessage Closing"
                CameraState.Type.CLOSED -> dCameraState.text = "$baseMessage Closed"
            }
        }
    }

    fun setTorchDebugInfo(isEnabled: Boolean?) {
        binding.dTorch.text = when {
            isEnabled == null -> "Torch not available"
            isEnabled -> "Torch enabled"
            else -> "Torch disabled"
        }
    }

    fun setCompressionFormatDebugInfo(compressionFormat: CompressionFormatEnum) {
        binding.dCompressionFormat.text = "Compression format: ${compressionFormat.name}"
    }

    fun setCompressionQuality(quality: Int) {
        binding.dCompressionQuality.text = "Compression quality: $quality"
    }

    private fun Int.toBarcodeFormatStr(): String = when (this) {
        BarcodeFormat.CODE_128 -> "CODE_128"
        BarcodeFormat.CODE_39 -> "CODE_39"
        BarcodeFormat.CODE_93 -> "CODE_93"
        BarcodeFormat.CODABAR -> "CODABAR"
        BarcodeFormat.EAN_8 -> "EAN_8"
        BarcodeFormat.EAN_13 -> "EAN_13"
        BarcodeFormat.ITF -> "ITF"
        BarcodeFormat.UPC_A -> "UPC_A"
        BarcodeFormat.UPC_E -> "UPC_E"
        BarcodeFormat.QR_CODE -> "QR_CODE"
        BarcodeFormat.PDF417 -> "PDF417"
        BarcodeFormat.AZTEC -> "AZTEC"
        BarcodeFormat.DATA_MATRIX -> "DATA_MATRIX"
        else -> "UNKNOWN"
    }
}
