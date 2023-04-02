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
package io.nyris.sdk.camera

import android.view.ViewGroup
import androidx.annotation.IntRange
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.feature.image.DEFAULT_QUALITY
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY

class CameraViewBuilder(private val parent: ViewGroup) {
    @CaptureMode
    private var captureMode: Int = CaptureMode.SCREENSHOT

    @FocusMode
    private var focusMode: Int = FocusMode.AUTOMATIC

    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    private var quality: Int = DEFAULT_QUALITY

    @CompressionFormat
    private var compressionFormat: Int = CompressionFormat.WEBP

    @BarcodeFormat
    private var barcodeFormat: Int = BarcodeFormat.ALL

    fun captureMode(
        @CaptureMode captureMode: Int,
    ) = apply { this.captureMode = captureMode }

    fun focusMode(
        @FocusMode focusMode: Int,
    ) = apply { this.focusMode = focusMode }

    fun quality(
        @IntRange(from = MIN_QUALITY, to = MAX_QUALITY) quality: Int,
    ) = apply { this.quality = quality }

    fun compressionFormat(
        @CompressionFormat compressionFormat: Int,
    ) = apply { this.compressionFormat = compressionFormat }

    fun barcodeFormat(
        @BarcodeFormat barcodeFormat: Int,
    ) = apply { this.barcodeFormat = barcodeFormat }

    fun build(): CameraView = CameraView(
        context = parent.context,
        focusMode = focusMode,
        captureMode = captureMode,
        compressionFormat = compressionFormat,
        quality = quality,
        barcodeFormat = barcodeFormat
    ).apply {
        layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        this@CameraViewBuilder.parent.addView(this)
    }
}
