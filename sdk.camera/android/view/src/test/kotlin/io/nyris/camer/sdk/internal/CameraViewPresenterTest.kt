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
package io.nyris.camer.sdk.internal

import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyAll
import io.nyris.sdk.camera.BarcodeResult
import io.nyris.sdk.camera.ImageResult
import io.nyris.sdk.camera.Result
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.feature.barcode.BarcodeResultInternal
import io.nyris.sdk.camera.feature.image.ImageResultInternal
import io.nyris.sdk.camera.internal.CameraManager
import io.nyris.sdk.camera.internal.CameraViewContract
import io.nyris.sdk.camera.internal.CameraViewPresenter
import io.nyris.sdk.camera.internal.toBarcodeResult
import io.nyris.sdk.camera.internal.toImageResult
import kotlin.reflect.KClass
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CameraViewPresenterTest {
    private val focusMode = FocusModeEnum.Manual
    private val captureMode = CaptureModeEnum.Lens
    private val compressionFormat = CompressionFormatEnum.WebP
    private val quality = 90
    private val barcodeFormat = BarcodeFormat.ALL
    private val view = mockk<CameraViewContract.View>(relaxed = true)
    private val cameraManager = mockk<CameraManager>(relaxed = true)
    private val imageResult = ImageResult(
        originalImage = ByteArray(1),
        optimizedImage = ByteArray(1),
    )
    private val barcodeResult = BarcodeResult(barcodes = listOf())

    private val classToTest: CameraViewPresenter by lazy {
        CameraViewPresenter(
            featureModes = listOf(FeatureMode.CAPTURE),
            focusMode = focusMode,
            captureMode = captureMode,
            compressionFormat = compressionFormat,
            quality = quality,
            barcodeFormat = barcodeFormat,
        )
    }

    @BeforeEach
    fun setup() {
        classToTest.view = view
        classToTest.cameraManager = cameraManager

        mockkStatic("io.nyris.sdk.camera.internal.CameraViewPresenterKt")
        every { any<BarcodeResultInternal>().toBarcodeResult() } returns barcodeResult
        every { any<ImageResultInternal>().toImageResult() } returns imageResult
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `attach should show debug, create cameraManager and observe states`() {
        mockkObject(CameraManager)
        every {
            CameraManager.createInstance(
                context = any(),
                previewView = any(),
                focusMode = focusMode,
                lifecycleOwner = any(),
                captureConfig = any(),
                barcodeConfig = any()
            )
        } returns cameraManager

        classToTest.attach(view)

        verifyAll {
            view.setFocusModeDebugInfo(focusMode)
            view.setCaptureModeDebugInfo(captureMode)
            view.setCompressionFormatDebugInfo(compressionFormat)
            view.setCompressionQualityDebugInfo(quality)
            view.context()
            view.previewView()
            view.lifecycleOwner()
            view.observeTouch()
        }
        verifyAll {
            cameraManager.error(any())
            cameraManager.state(any())
            cameraManager.torchState(any())
        }
    }

    @Test
    fun `capture should capture image result when result is ImageResultInternal`() {
        assertCaptureTest<ImageResult, ImageResultInternal>(FeatureMode.CAPTURE, ImageResult::class, imageResult)
    }

    @Test
    fun `capture should capture barcode result when result is BarcodeResultInternal`() {
        assertCaptureTest<BarcodeResult, BarcodeResultInternal>(
            FeatureMode.BARCODE,
            BarcodeResult::class,
            barcodeResult
        )
    }

    @Test
    fun `capture should throw exception when internal result is not recognized`() {
        assertThrows(
            IllegalArgumentException::class.java,
            {
                assertCaptureTest<RandomResult, RandomResultInternal>(
                    FeatureMode.CAPTURE,
                    RandomResult::class,
                    mockk()
                )
            },
            "Result type not handled here !"
        )
    }

    @Test
    fun `enableTorch should enable torch`() {
        classToTest.enableTorch()

        verify { cameraManager.enableTorch() }
    }

    @Test
    fun `disableTorch should enable torch`() {
        classToTest.disableTorch()

        verify { cameraManager.disableTorch() }
    }

    @Test
    fun `showDebug with true should show debug view`() {
        classToTest.showDebug(true)

        verify { view.showDebugInfo() }
    }

    @Test
    fun `showDebug with false should hide debug view`() {
        classToTest.showDebug(false)

        verify { view.hideDebugInfo() }
    }

    @Test
    fun `start should bind camera manager`() {
        classToTest.start()

        verify { cameraManager.bind() }
    }

    @Test
    fun `stop should unbind camera manager`() {
        classToTest.stop()

        verify { cameraManager.unbind() }
    }

    @Test
    fun `release should view and camera manager`() {
        classToTest.release()

        assertEquals(null, classToTest.view)
        verify { cameraManager.release() }
    }

    @Test
    fun `setFocusPoint should set camera manager focus`() {
        val x = 1F
        val y = 1F
        classToTest.setFocusPoint(x, y)

        verify { cameraManager.setManualFocus(x, y) }
    }

    private inline fun <R : Result, reified R2 : ResultInternal> assertCaptureTest(
        @FeatureMode
        featureMode: Int,
        kClass: KClass<R>,
        expected: R,
    ) {
        val captureBlockSlot = slot<CaptureCallback>()
        justRun { cameraManager.capture(featureMode, capture(captureBlockSlot)) }
        val resultInternal = mockk<R2>()

        classToTest.capture(featureMode, kClass)
        captureBlockSlot.captured.invoke(resultInternal)

        verify { view.onResult(featureMode, expected) }
    }
}

private interface RandomResultInternal : ResultInternal
private interface RandomResult : Result
private typealias CaptureCallback = (ResultInternal?) -> Unit
