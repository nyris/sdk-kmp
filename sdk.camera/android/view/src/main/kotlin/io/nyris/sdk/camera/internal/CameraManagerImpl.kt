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
package io.nyris.sdk.camera.internal

import android.content.Context
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraState
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.TorchState
import androidx.camera.core.UseCaseGroup
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import io.nyris.sdk.camera.core.CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE
import io.nyris.sdk.camera.core.CAMERA_ERROR_BIND
import io.nyris.sdk.camera.core.CAMERA_ERROR_MANUAL_FOCUS
import io.nyris.sdk.camera.core.CAMERA_ERROR_STATE
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.camera.core.FocusModeEnum
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import io.nyris.sdk.camera.internal.view.PreviewBuilder
import java.util.concurrent.TimeUnit

internal class CameraManagerImpl(
    context: Context,
    private val previewView: PreviewView,
    private val lifecycleOwner: LifecycleOwner,
    private val focusMode: FocusModeEnum,
    private val featuresMap: Map<Int, ImageFeature<ResultInternal>?>,
) : CameraManager {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private val cameraProvider: ProcessCameraProvider by lazy { cameraProviderFuture.get() }
    private val cameraPreview = PreviewBuilder.createInstance()
        .setTargetRotation(previewView.display.rotation)
        .setSurfaceProvider(previewView.surfaceProvider)
        .build()

    private var onCameraStateChanged: ((CameraState) -> Unit)? = null
    private var onTorchStateChanged: ((Boolean?) -> Unit)? = null
    private var onError: ((CameraError) -> Unit)? = null
    internal var cameraError: CameraError? = null
        set(value) {
            field = value
            value?.let { e ->
                Log.e(TAG, "Error code: ${e.code}, message: ${e.message}")
                onError?.invoke(e)
            }
        }
    internal var camera: Camera? = null

    init {
        cameraProviderFuture.addListener({ bind() }, ContextCompat.getMainExecutor(context))
    }

    override fun bind() {
        if (!cameraProviderFuture.isDone) return
        if (!cameraProvider.requireBackCamera()) {
            cameraError = CameraError(
                CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE,
                "Camera can't find back camera. Camera component can't start!"
            )
            return
        }
        try {
            val useCaseGroup = UseCaseGroup.Builder().apply {
                setViewPort(previewView.viewPort!!)
                addUseCase(cameraPreview)
                featuresMap.values.forEach { feature ->
                    feature?.useCase()?.let { useCase ->
                        addUseCase(useCase)
                    }
                }
            }.build()

            camera = cameraProvider.bindToLifecycle(
                lifecycleOwner,
                CameraSelector.DEFAULT_BACK_CAMERA,
                useCaseGroup
            )
            if (focusMode == FocusModeEnum.Automatic) {
                setAutoFocus()
            }
            observeCameraState()
            observeTorchState()
        } catch (e: Exception) {
            cameraError = CameraError(
                CAMERA_ERROR_BIND, "Failed to bind camera! ${e.message}"
            )
        }
    }

    override fun unbind() {
        camera = null
        onTorchStateChanged?.invoke(null)
        cameraProvider.unbindAll()
    }

    override fun state(block: (CameraState) -> Unit) {
        onCameraStateChanged = block
    }

    @Suppress("UNCHECKED_CAST")
    override fun <R : ResultInternal> capture(
        @FeatureMode
        feature: Int,
        block: (R?) -> Unit,
    ) {
        featuresMap[feature]?.process({ result ->
            block(result as? R)
        }, { error ->
            cameraError = error
        })
    }

    override fun release() {
        featuresMap.values.forEach { feature -> feature?.shutdown() }
        onCameraStateChanged = null
        onTorchStateChanged = null
        camera?.cameraInfo?.torchState?.removeObservers(lifecycleOwner)
        camera?.cameraInfo?.cameraState?.removeObservers(lifecycleOwner)
        camera = null
    }

    override fun setManualFocus(
        x: Float,
        y: Float,
    ) {
        if (focusMode == FocusModeEnum.Automatic) {
            return
        }
        val autoFocusPoint = SurfaceOrientedMeteringPointFactory(
            previewView.width.toFloat(), previewView.height.toFloat()
        ).createPoint(x, y)
        val focusAction = FocusMeteringAction.Builder(
            autoFocusPoint, FocusMeteringAction.FLAG_AF
        ).apply {
            disableAutoCancel()
        }.build()

        try {
            camera?.cameraControl?.startFocusAndMetering(focusAction)
        } catch (e: Exception) {
            cameraError = CameraError(
                CAMERA_ERROR_MANUAL_FOCUS, "Can't manually focus! ${e.message}"
            )
        }
    }

    override fun error(block: (CameraError) -> Unit) {
        onError = block
    }

    override fun torchState(block: (Boolean?) -> Unit) {
        onTorchStateChanged = block
    }

    override fun enableTorch() {
        camera?.cameraControl?.enableTorch(true)
    }

    override fun disableTorch() {
        camera?.cameraControl?.enableTorch(false)
    }

    private fun hasTorch(): Boolean = camera?.cameraInfo?.hasFlashUnit() ?: false

    private fun observeCameraState() {
        camera?.cameraInfo?.cameraState?.observe(lifecycleOwner) { state ->
            if (state.error != null) {
                onError?.invoke(CameraError(CAMERA_ERROR_STATE, "Error on camera state!"))
            }
            onCameraStateChanged?.invoke(state)
        }
    }

    internal fun observeTorchState() {
        camera?.cameraInfo?.torchState?.observe(lifecycleOwner) { state ->
            onTorchStateChanged?.invoke(
                if (!hasTorch()) {
                    null
                } else {
                    state == TorchState.ON
                }
            )
        }
    }

    private fun setAutoFocus() {
        val action = FocusMeteringAction.Builder(
            SurfaceOrientedMeteringPointFactory(
                DEFAULT_FOCUS_SIZE,
                DEFAULT_FOCUS_SIZE
            ).createPoint(DEFAULT_FOCUS_LOCATION, DEFAULT_FOCUS_LOCATION),
            FocusMeteringAction.FLAG_AF
        ).apply {
            setAutoCancelDuration(AUTO_CANCEL_DURATION, TimeUnit.SECONDS)
        }.build()
        camera?.cameraControl?.startFocusAndMetering(action)
    }
}

private fun ProcessCameraProvider.requireBackCamera(): Boolean = hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)

private const val DEFAULT_FOCUS_SIZE = 1F
private const val DEFAULT_FOCUS_LOCATION = 0.5F
private const val AUTO_CANCEL_DURATION = 2L
