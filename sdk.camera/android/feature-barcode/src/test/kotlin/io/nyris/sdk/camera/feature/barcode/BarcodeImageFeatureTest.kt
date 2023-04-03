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
package io.nyris.sdk.camera.feature.barcode

import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.camera.core.CameraError
import java.util.concurrent.ExecutorService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BarcodeImageFeatureTest {
    private val imageAnalysisExecutor = mockk<ExecutorService>(relaxed = true)
    private val scanner = mockk<BarcodeScanner>(relaxed = true)
    private val imageAnalysisWrapper = mockk<ImageAnalysisWrapper>(relaxed = true)

    private val classToTest: BarcodeImageFeature = BarcodeImageFeature(
        imageAnalysisExecutor = imageAnalysisExecutor,
        scanner = scanner,
        imageAnalysisWrapper = imageAnalysisWrapper
    )

    @BeforeEach
    fun setup() {
        mockkStatic(InputImage::class)
        every { InputImage.fromMediaImage(any(), any()) } returns mockk()
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `init should setAnalyzer`() {
        verify { imageAnalysisWrapper.setAnalyzer(imageAnalysisExecutor, any()) }
    }

    @Test
    fun `process should set the internal callbacks`() {
        val resultCallback: (BarcodeResultInternal) -> Unit = {}
        val errorCallback: (CameraError) -> Unit = {}

        classToTest.process(resultCallback, errorCallback)

        assertEquals(resultCallback, classToTest.resultCallback)
        assertEquals(errorCallback, classToTest.errorCallback)
    }

    @Test
    fun `processImageProxy should call resultCallback when its success`() {
        val resultCallback = mockk<ResultCallback>(relaxed = true)
        val imageProxy = mockk<ImageProxy>(relaxed = true)
        val successListenerSlot = slot<OnSuccessListener<List<Barcode>>>()
        val task = mockk<Task<List<Barcode>>>(relaxed = true).apply {
            every { addOnSuccessListener(capture(successListenerSlot)) } returns this
            every { addOnFailureListener(any()) } returns this
            every { addOnCompleteListener(any()) } returns this
        }
        every { scanner.process(any<InputImage>()) } returns task
        classToTest.resultCallback = resultCallback

        classToTest.processImageProxy(imageProxy)
        successListenerSlot.captured.onSuccess(listOf(mockk(relaxed = true)))

        verify { resultCallback.invoke(any()) }
    }

    @Test
    fun `processImageProxy should call errorCallback when its fail`() {
        val errorCallback = mockk<ErrorCallback>(relaxed = true)
        val imageProxy = mockk<ImageProxy>(relaxed = true)
        val failListenerSlot = slot<OnFailureListener>()
        val task = mockk<Task<List<Barcode>>>(relaxed = true).apply {
            every { addOnSuccessListener(any()) } returns this
            every { addOnFailureListener(capture(failListenerSlot)) } returns this
            every { addOnCompleteListener(any()) } returns this
        }
        every { scanner.process(any<InputImage>()) } returns task
        classToTest.errorCallback = errorCallback

        classToTest.processImageProxy(imageProxy)
        failListenerSlot.captured.onFailure(mockk(relaxed = true))

        verify { errorCallback.invoke(any()) }
    }

    @Test
    fun `processImageProxy should close image proxy on complete`() {
        val imageProxy = mockk<ImageProxy>(relaxed = true)
        val onCompleteListener = slot<OnCompleteListener<List<Barcode>>>()
        val task = mockk<Task<List<Barcode>>>(relaxed = true).apply {
            every { addOnSuccessListener(any()) } returns this
            every { addOnFailureListener(any()) } returns this
            every { addOnCompleteListener(capture(onCompleteListener)) } returns this
        }
        every { scanner.process(any<InputImage>()) } returns task

        classToTest.processImageProxy(imageProxy)
        onCompleteListener.captured.onComplete(mockk())

        verify { imageProxy.close() }
    }
}

private typealias ResultCallback = (BarcodeResultInternal) -> Unit
private typealias ErrorCallback = (CameraError) -> Unit
