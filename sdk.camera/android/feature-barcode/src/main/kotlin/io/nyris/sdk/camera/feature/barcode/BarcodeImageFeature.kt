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

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.UseCase
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CAMERA_BARCODE_ERROR
import io.nyris.sdk.camera.core.CameraError
import io.nyris.sdk.camera.core.ImageFeature
import io.nyris.sdk.camera.core.ResultInternal
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeResultInternal(val barcodes: List<Barcode>) : ResultInternal

@Suppress("UnsafeOptInUsageError")
class BarcodeImageFeature internal constructor(
    private val imageAnalysisExecutor: ExecutorService,
    private val scanner: BarcodeScanner,
    private val imageAnalysisWrapper: ImageAnalysisWrapper,
) : ImageFeature<BarcodeResultInternal> {
    init {
        imageAnalysisWrapper.apply {
            setAnalyzer(imageAnalysisExecutor) { imageProxy ->
                processImageProxy(imageProxy)
            }
        }
    }

    @get:Synchronized
    internal var resultCallback: ((BarcodeResultInternal) -> Unit)? = null

    internal var errorCallback: ((CameraError) -> Unit)? = null

    override fun useCase(): UseCase = imageAnalysisWrapper.imageAnalysis

    override fun shutdown() {
        imageAnalysisExecutor.shutdown()
    }

    override fun process(
        resultCallback: (BarcodeResultInternal) -> Unit,
        errorCallback: (CameraError) -> Unit,
    ) {
        this.resultCallback = resultCallback
        this.errorCallback = errorCallback
    }

    internal fun processImageProxy(imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage = InputImage.fromMediaImage(image, imageProxy.imageInfo.rotationDegrees)
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isEmpty()) return@addOnSuccessListener
                    resultCallback?.invoke(BarcodeResultInternal(barcodes))
                }
                .addOnFailureListener { e ->
                    errorCallback?.invoke(
                        CameraError(CAMERA_BARCODE_ERROR, "Can't read barcode! ${e.message}")
                    )
                }.addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }

    companion object {
        @Suppress("SpreadOperator") // Small Array
        fun createInstance(
            rotation: Int,
            @BarcodeFormat
            barcodeFormat: Int,
        ): BarcodeImageFeature {
            val scanner = BarcodeScanning.getClient(
                with(barcodeFormat.toLibraryBarcodeFormat()) {
                    BarcodeScannerOptions.Builder().apply {
                        if (second != null) {
                            setBarcodeFormats(first, *second!!)
                        } else {
                            setBarcodeFormats(first)
                        }
                    }.build()
                }
            )

            val imageAnalysisUseCase = ImageAnalysis.Builder()
                .setTargetRotation(rotation)
                .build()
            return BarcodeImageFeature(
                imageAnalysisExecutor = Executors.newSingleThreadExecutor(),
                scanner = scanner,
                imageAnalysisWrapper = ImageAnalysisWrapper(imageAnalysisUseCase)
            )
        }
    }
}

internal class ImageAnalysisWrapper(val imageAnalysis: ImageAnalysis) {
    fun setAnalyzer(
        executor: Executor,
        analyzer: ImageAnalysis.Analyzer,
    ) {
        imageAnalysis.setAnalyzer(executor, analyzer)
    }
}
