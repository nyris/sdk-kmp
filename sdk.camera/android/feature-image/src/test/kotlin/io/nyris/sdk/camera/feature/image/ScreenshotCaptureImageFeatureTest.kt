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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.camera.core.CAMERA_ERROR_CAPTURE_SCREENSHOT
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.Time
import java.util.concurrent.ExecutorService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ScreenshotCaptureImageFeatureTest {
    private val captureExecutor = mockk<ExecutorService>()
    private val previewView = mockk<PreviewView>().apply {
        every { bitmap } returns mockk()
    }
    private val compressionFormat = CompressionFormatEnum.WebP
    private val quality = 90

    private val classToTest: ScreenshotCaptureImageFeature by lazy {
        ScreenshotCaptureImageFeature(
            captureExecutor = captureExecutor,
            previewView = previewView,
            compressionFormat = compressionFormat,
            quality = quality
        )
    }

    @BeforeEach
    fun setup() {
        mockkStatic("io.nyris.sdk.camera.feature.image.ImageExtKt")
        mockkStatic(BitmapFactory::class)
        mockkObject(Time)
        every { Time.currentTimeMillis() } returns START andThen END
        every { any<CompressionFormatEnum>().toBitmapCompressionFormat() } returns Bitmap.CompressFormat.WEBP_LOSSY
        every { any<ImageProxy>().toCorrectBitmap() } returns mockk(relaxed = true)
        every { BitmapFactory.decodeByteArray(any(), any(), any()) } returns mockk(relaxed = true)
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `process should succeed to take a screen shot`() {
        val resultCallback = mockk<ResultCallback>()
        val imageResultInternalSlot = slot<ImageResultInternal>()
        justRun { resultCallback.invoke(capture(imageResultInternalSlot)) }
        val runnableSlot = slot<Runnable>()
        justRun { captureExecutor.execute(capture(runnableSlot)) }
        val originalImageMock = mockToByteArray()
        val optimizedImageMock = mockOptimize()

        classToTest.process(resultCallback, mockk())
        runnableSlot.captured.run()

        with(imageResultInternalSlot.captured) {
            verify { resultCallback.invoke(this@with) }
            assertEquals(EXPECTED_ELAPSED, this.elapsed)
            assertEquals(originalImageMock, originalImage)
            assertEquals(optimizedImageMock, optimizedImage)
        }
    }

    @Test
    fun `process should fail while taking a screenshot`() {
        val errorCallback = mockk<ErrorCallback>(relaxed = true)
        val cameraErrorSlot = slot<CameraError>()
        justRun { errorCallback(capture(cameraErrorSlot)) }
        every { captureExecutor.execute(any()) } throws Exception("")

        classToTest.process(mockk(), errorCallback)

        with(cameraErrorSlot.captured) {
            assertEquals(CAMERA_ERROR_CAPTURE_SCREENSHOT, code)
            assertEquals(EXPECTED_ERROR_MESSAGE, message)
        }
    }

    private fun mockToByteArray(): ByteArray {
        val originalImageMock = ByteArray(1)
        every {
            any<Bitmap>().toByteArray(compressionFormat = CompressionFormatEnum.Jpeg)
        } returns originalImageMock
        return originalImageMock
    }

    private fun mockOptimize(): ByteArray {
        val optimizedImageMock = ByteArray(2)
        every {
            any<ByteArray>().optimize(compressionFormat = CompressionFormatEnum.WebP, quality = quality)
        } returns optimizedImageMock
        return optimizedImageMock
    }
}

private const val START = 1000L
private const val END = 2000L
private const val EXPECTED_ELAPSED = END - START
private const val EXPECTED_ERROR_MESSAGE = "Can't capture image using screenshot! "
