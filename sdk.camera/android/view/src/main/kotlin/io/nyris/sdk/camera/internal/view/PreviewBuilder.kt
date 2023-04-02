package io.nyris.sdk.camera.internal.view

import android.view.Surface
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.impl.ImageOutputConfig.RotationValue

internal class PreviewBuilder private constructor() {
    companion object {
        fun createInstance(): PreviewBuilder = PreviewBuilder()
    }

    @RotationValue
    private var rotation: Int = Surface.ROTATION_0
    private var surfaceProvider: Preview.SurfaceProvider? = null

    fun setTargetRotation(@RotationValue rotation: Int) = apply {
        this.rotation = rotation
    }

    fun setSurfaceProvider(surfaceProvider: Preview.SurfaceProvider) = apply {
        this.surfaceProvider = surfaceProvider
    }

    fun build(): UseCase = Preview.Builder()
        .setTargetRotation(rotation)
        .build().apply {
            setSurfaceProvider(surfaceProvider)
        }
}
