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

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.annotation.IntRange
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.feature.image.DEFAULT_QUALITY
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import io.nyris.sdk.camera.internal.view.CameraViewDelegate
import io.nyris.sdk.camera.internal.view.CaptureBlock
import io.nyris.sdk.camera.internal.view.ErrorBlock
import io.nyris.sdk.camera.internal.view.TorchStateBlock
import kotlin.reflect.KClass

@Suppress("LongParameterList")
class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    @FocusMode
    focusMode: Int = 0,
    @CaptureMode
    captureMode: Int = 0,
    @CompressionFormat
    compressionFormat: Int = 0,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    quality: Int = DEFAULT_QUALITY,
    @BarcodeFormat
    barcodeFormat: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {

    private val delegate = CameraViewDelegate(
        cameraView = this,
        attrs = attrs,
        focusMode = focusMode,
        captureMode = captureMode,
        compressionFormat = compressionFormat,
        quality = quality,
        barcodeFormat = barcodeFormat
    )

    fun <R : Result> capture(
        kClass: KClass<R>,
        block: CaptureBlock<R>?,
    ) {
        delegate.capture(kClass, block)
    }

    fun enableTorch() {
        delegate.enableTorch()
    }

    fun disableTorch() {
        delegate.disableTorch()
    }

    fun torchState(block: TorchStateBlock?) {
        delegate.torchState(block)
    }

    fun error(block: ErrorBlock?) {
        delegate.error(block)
    }

    fun start() {
        delegate.start()
    }

    fun stop() {
        delegate.stop()
    }

    fun release() {
        delegate.release()
    }

    fun showDebug(isEnabled: Boolean) {
        delegate.showDebug(isEnabled)
    }
}