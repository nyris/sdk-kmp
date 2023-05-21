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

import android.view.Surface
import androidx.camera.view.PreviewView
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.feature.barcode.BarcodeImageFeature
import io.nyris.sdk.camera.feature.image.LensCaptureImageFeature
import io.nyris.sdk.camera.feature.image.ScreenshotCaptureImageFeature
import io.nyris.sdk.camera.internal.BarcodeConfig
import io.nyris.sdk.camera.internal.CameraManager
import io.nyris.sdk.camera.internal.CaptureConfig
import kotlin.reflect.KClass
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class CameraManagerTest {
    private val previewView = mockk<PreviewView>()
    private val compressionFormat = CompressionFormatEnum.WebP
    private val quality = 90
    private val rotation = Surface.ROTATION_0
    private val barcodeFormat = BarcodeFormat.ALL

    @BeforeEach
    fun setup() {
        mockkObject(ScreenshotCaptureImageFeature)
        every { ScreenshotCaptureImageFeature.createInstance(any(), any(), any()) } returns mockk()

        mockkObject(LensCaptureImageFeature)
        every { LensCaptureImageFeature.createInstance(any(), any(), any()) } returns mockk()

        mockkObject(BarcodeImageFeature)
        every { BarcodeImageFeature.createInstance(any(), any()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @ParameterizedTest(name = "createImageFeature should create the correct image feature [{0}]")
    @MethodSource("getTestData")
    fun `createImageFeature should create the correct image feature`(
        captureMode: CaptureModeEnum,
        expected: KClass<*>?,
    ) {
        val captureConfig = CaptureConfig(
            isEnabled = true,
            captureMode = captureMode,
            compressionFormat = compressionFormat,
            quality = quality,
            rotation = rotation
        )
        val imageFeature = CameraManager.createImageFeature(captureConfig, previewView)

        assertEquals(expected, imageFeature!!::class)
    }

    companion object {
        @JvmStatic
        fun getTestData() = listOf(
            Arguments.of(CaptureModeEnum.Screenshot, ScreenshotCaptureImageFeature::class),
            Arguments.of(CaptureModeEnum.Lens, LensCaptureImageFeature::class),
        )
    }

    @Test
    fun `createBarcodeFeature should create the correct barcode feature`() {
        val barcodeConfig = BarcodeConfig(
            isEnabled = true,
            barcodeFormat = barcodeFormat,
            rotation = rotation
        )
        val barcodeFeature = CameraManager.createBarcodeFeature(barcodeConfig)

        assertEquals(BarcodeImageFeature::class, barcodeFeature!!::class)
    }
}
