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
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.feature.image.DEFAULT_QUALITY
import io.nyris.sdk.camera.feature.image.MAX_QUALITY
import io.nyris.sdk.camera.feature.image.MIN_QUALITY
import io.nyris.sdk.camera.internal.view.CameraViewDelegate
import io.nyris.sdk.camera.internal.view.CaptureBlock
import io.nyris.sdk.camera.internal.view.ErrorBlock
import io.nyris.sdk.camera.internal.view.TorchStateBlock
import kotlin.reflect.KClass

class CameraView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    @FocusMode
    private var focusMode: Int = 0

    @FeatureMode
    private var featureModes: Int = FeatureMode.CAPTURE

    @CaptureMode
    private var captureMode: Int = 0

    @CompressionFormat
    private var compressionFormat: Int = 0

    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    private var quality: Int = DEFAULT_QUALITY

    @BarcodeFormat
    private var barcodeFormat: Int = 0
    private var isBarcodeGuideEnabled: Boolean = false

    private val delegate: CameraViewDelegate by lazy {
        CameraViewDelegate(
            cameraView = this,
            attrs = null,
            focusMode = focusMode,
            featureModes = featureModes,
            captureMode = captureMode,
            compressionFormat = compressionFormat,
            quality = quality,
            barcodeFormat = barcodeFormat,
            isBarcodeGuideEnabled = isBarcodeGuideEnabled
        )
    }

    @Suppress("LongParameterList")
    constructor(
        context: Context,
        @FocusMode
        focusMode: Int = 0,
        @FeatureMode
        featureModes: Int,
        @CaptureMode
        captureMode: Int = 0,
        @CompressionFormat
        compressionFormat: Int = 0,
        @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
        quality: Int = DEFAULT_QUALITY,
        @BarcodeFormat
        barcodeFormat: Int = 0,
        isBarcodeGuideEnabled: Boolean = false,
    ) : this(context) {
        this.focusMode = focusMode
        this.featureModes = featureModes
        this.captureMode = captureMode
        this.compressionFormat = compressionFormat
        this.quality = quality
        this.barcodeFormat = barcodeFormat
        this.isBarcodeGuideEnabled = isBarcodeGuideEnabled
    }

    fun <R : Result> capture(
        @FeatureMode
        featureMode: Int,
        kClass: KClass<R>,
        block: CaptureBlock<R>?,
    ) {
        delegate.capture(featureMode, kClass, block)
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
