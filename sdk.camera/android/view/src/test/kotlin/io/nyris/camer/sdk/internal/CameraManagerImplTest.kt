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

import android.content.Context
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector.DEFAULT_BACK_CAMERA
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.Preview
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.google.common.util.concurrent.ListenableFuture
import io.mockk.CapturingSlot
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.camera.core.CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.internal.CameraManagerImpl
import io.nyris.sdk.camera.internal.view.PreviewBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CameraManagerImplTest {
    private val context = mockk<Context>(relaxed = true)
    private val previewView = mockk<PreviewView>(relaxed = true)
    private val lifecycleOwner = mockk<LifecycleOwner>()
    private val focusMode = FocusModeEnum.Manual
    private val imageFeature = mockk<ImageFeature<ResultInternal>>(relaxed = true)
    private val cameraProvider = mockk<ProcessCameraProvider>(relaxed = true)
    private val cameraProviderFuture = mockk<ListenableFuture<ProcessCameraProvider>>().apply {
        every { get() } returns cameraProvider
        justRun { addListener(any(), any()) }
    }

    private lateinit var classToTest: CameraManagerImpl

    @BeforeEach
    fun setup() {
        mockkObject(PreviewBuilder)
        mockkStatic(Preview::class)
        val builder = mockk<PreviewBuilder>(relaxed = true)
        every { PreviewBuilder.createInstance() } returns builder
        every { builder.setTargetRotation(any()) } returns builder
        every { builder.setSurfaceProvider(any()) } returns builder
        every { builder.build() } returns mockk(relaxed = true)

        mockkStatic(ProcessCameraProvider::class)
        every { ProcessCameraProvider.getInstance(context) } returns cameraProviderFuture

        mockkStatic(ContextCompat::class)
        every { ContextCompat.getMainExecutor(context) } returns mockk()

        classToTest = CameraManagerImpl(
            context = context,
            previewView = previewView,
            lifecycleOwner = lifecycleOwner,
            focusMode = focusMode,
            imageFeature = imageFeature
        )
    }

    @AfterEach
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `cameraProviderFuture should add listener`() {
        verify { cameraProviderFuture.addListener(any(), any()) }
    }

    @Test
    fun `bind should bind use cases to camera`() {
        every { cameraProviderFuture.isDone } returns true
        every { cameraProvider.hasCamera(DEFAULT_BACK_CAMERA) } returns true
        val camera = mockk<Camera>(relaxed = true)
        every {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                DEFAULT_BACK_CAMERA,
                any<UseCaseGroup>()
            )
        } returns camera

        classToTest.bind()

        verify { previewView.viewPort }
        verify { imageFeature.useCase() }
        verify {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                DEFAULT_BACK_CAMERA,
                any<UseCaseGroup>()
            )
        }
        verify { camera.cameraInfo.cameraState }
        verify { camera.cameraInfo.torchState }
    }

    @Test
    fun `bind should not bind camera when cameraProviderFuture is not done`() {
        every { cameraProviderFuture.isDone } returns false

        classToTest.bind()

        confirmVerified(cameraProvider, imageFeature, lifecycleOwner)
    }

    @Test
    fun `bind should not bind camera when cameraProviderFuture has not DEFAULT_BACK_CAMERA`() {
        every { cameraProviderFuture.isDone } returns true
        every { cameraProvider.hasCamera(DEFAULT_BACK_CAMERA) } returns false

        classToTest.bind()

        verify { cameraProviderFuture.isDone }
        verify { cameraProvider.hasCamera(DEFAULT_BACK_CAMERA) }
        assertEquals(CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE, classToTest.cameraError?.code)
        assertEquals(EXPECTED_CAMERA_ERROR_MESSAGE, classToTest.cameraError?.message)
        confirmVerified(cameraProvider, imageFeature, lifecycleOwner)
    }

    @Test
    fun `unbind should unbind camera`() {
        classToTest.camera = mockk(relaxed = true)

        classToTest.unbind()

        assertEquals(null, classToTest.camera)
        verify { cameraProvider.unbindAll() }
        confirmVerified(cameraProvider)
    }

    @Test
    fun `release should release camera`() {
        val camera = mockk<Camera>(relaxed = true)
        classToTest.camera = camera

        classToTest.release()

        verify { imageFeature.shutdown() }
        assertEquals(null, classToTest.camera)
        verify { camera.cameraInfo.torchState.removeObservers(lifecycleOwner) }
        verify { camera.cameraInfo.cameraState.removeObservers(lifecycleOwner) }
    }

    @Test
    fun `setManualFocus should start focus and metering`() {
        every { previewView.width } returns 1
        every { previewView.height } returns 1
        val focusActionSlot = slot<FocusMeteringAction>()
        val camera = mockk<Camera>().apply { every { cameraControl.startFocusAndMetering(capture(focusActionSlot)) } }
        classToTest.camera = camera

        classToTest.setManualFocus(1F, 200F)

        verify { previewView.width }
        verify { previewView.height }
        verify { camera.cameraControl.startFocusAndMetering(focusActionSlot.captured) }
        with(focusActionSlot.captured) {
            assertFalse(isAutoCancelEnabled)
            assertTrue(meteringPointsAf.isNotEmpty())
        }
        confirmVerified(camera)
    }

    @Test
    fun `observeTorchState should call onTorchStateChanged with null when torch is not available`() {
        torchTest(null)
    }

    @Test
    fun `observeTorchState should call onTorchStateChanged with true when torch is available and on`() {
        torchTest(true)
    }

    private fun torchTest(isTorchOn: Boolean?) {
        val observerSlot = slot<Observer<Int>>()
        val camera = mockTorchCamera(observerSlot, isTorchOn != null)
        classToTest.camera = camera
        val torchStateCallback = mockk<TorchStateCallback>(relaxed = true)
        classToTest.torchState(torchStateCallback)

        classToTest.observeTorchState()
        observerSlot.captured.onChanged(1)

        verify { camera.cameraInfo.torchState.observe(any(), observerSlot.captured) }
        verify { camera.cameraInfo.hasFlashUnit() }
        verify { torchStateCallback.invoke(isTorchOn) }
        confirmVerified(camera)
    }

    private fun mockTorchCamera(
        observerSlot: CapturingSlot<Observer<Int>>,
        isTorchAvailable: Boolean = false,
    ): Camera {
        return mockk<Camera>(relaxed = true).apply {
            justRun {
                cameraInfo.torchState.observe(
                    lifecycleOwner,
                    capture(observerSlot)
                )
            }
            every { cameraInfo.hasFlashUnit() } returns isTorchAvailable
        }
    }
}

private const val EXPECTED_CAMERA_ERROR_MESSAGE = "Camera can't find back camera. Camera component can't start!"
private typealias TorchStateCallback = (Boolean?) -> Unit
