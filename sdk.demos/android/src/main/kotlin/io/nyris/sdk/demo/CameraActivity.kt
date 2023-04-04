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
package io.nyris.sdk.demo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.nyris.sdk.camera.BarcodeResult
import io.nyris.sdk.camera.CameraView
import io.nyris.sdk.camera.CameraViewBuilder
import io.nyris.sdk.camera.ImageResult
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.demo.CameraConfigActivity.Companion.BARCODE_FORMAT_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.BARCODE_GUIDE_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.CAPTURE_MODE_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.COMPRESSION_FORMAT_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.COMPRESSION_QUALITY_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.FOCUS_MODE_KEY
import io.nyris.sdk.demo.CameraConfigActivity.Companion.IS_DEBUG_KEY
import io.nyris.sdk.demo.databinding.ActivityCameraBinding

class CameraActivity : AppCompatActivity() {
    private var cameraView: CameraView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = intent?.extras ?: return
        val binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val captureMode = bundle.getInt(CAPTURE_MODE_KEY)
        with(bundle) {
            cameraView = CameraViewBuilder(binding.cameraContainer)
                .captureMode(captureMode)
                .focusMode(getInt(FOCUS_MODE_KEY))
                .barcodeFormat(getInt(BARCODE_FORMAT_KEY))
                .barcodeGuide(getBoolean(BARCODE_GUIDE_KEY))
                .compressionFormat(getInt(COMPRESSION_FORMAT_KEY))
                .quality(getInt(COMPRESSION_QUALITY_KEY))
                .build().apply {
                    showDebug(getBoolean(IS_DEBUG_KEY))
                }
        }

        with(binding) {
            torch.apply torch@{
                cameraView?.torchState { state ->
                    if (state == null) {
                        torch.isEnabled = false
                        return@torchState
                    }
                    torch.isEnabled = true
                    torch.isChecked = state
                    torch.setOnCheckedChangeListener { _, isChecked ->
                        if (isChecked) {
                            cameraView?.enableTorch()
                        } else {
                            cameraView?.disableTorch()
                        }
                    }
                }
            }

            if (captureMode == CaptureMode.BARCODE) {
                capture.visibility = View.GONE
                cameraView?.capture(BarcodeResult::class) {
                    // Handle you result!
                }
            } else {
                capture.setOnClickListener {
                    cameraView?.capture(ImageResult::class) {
                        // Handle you result!
                    }
                }
            }

            stop.setOnClickListener {
                cameraView?.stop()
            }

            cameraView?.error { error ->
                Toast.makeText(
                    this@CameraActivity,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        } else {
            cameraView?.start()
        }
    }

    override fun onStop() {
        super.onStop()
        cameraView?.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraView?.release()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != REQUEST_CODE_PERMISSIONS) return
        if (allPermissionsGranted()) {
            cameraView?.start()
        } else {
            Toast.makeText(this, "Camera Permission declined !", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}

private val REQUIRED_PERMISSIONS = listOf(Manifest.permission.CAMERA).toTypedArray()
private const val REQUEST_CODE_PERMISSIONS = 12323
