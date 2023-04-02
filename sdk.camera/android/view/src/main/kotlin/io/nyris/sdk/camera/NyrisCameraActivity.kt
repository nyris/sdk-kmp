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

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.databinding.CameraActivityBinding

class NyrisCameraActivity : AppCompatActivity() {
    private var cameraView: CameraView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = CameraActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // cameraView = binding.cameraView
        cameraView = CameraViewBuilder(binding.root)
            .captureMode(CaptureMode.BARCODE)
            .focusMode(FocusMode.AUTOMATIC)
            .barcodeFormat(BarcodeFormat.ALL)
            .compressionFormat(CompressionFormat.WEBP)
            .build()

        binding.torch.apply torch@{
            cameraView?.torchState { state ->
                this@torch.isEnabled = state != null
                this@torch.setOnCheckedChangeListener { _, isChecked ->
                    if (isChecked) {
                        cameraView?.enableTorch()
                    } else {
                        cameraView?.disableTorch()
                    }
                }
            }
        }

        binding.capture.setOnClickListener {
            cameraView?.capture(ImageResult::class) { result ->
                Log.i("result", "resut")
            }
        }

        binding.stop.setOnClickListener {
            cameraView?.stop()
        }

        cameraView?.showDebug(true)

        cameraView?.error { error ->
            Toast.makeText(
                this,
                error.message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onResume() {
        super.onResume()
        cameraView?.start()
    }

    override fun onStop() {
        super.onStop()
        cameraView?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView?.release()
    }
}
