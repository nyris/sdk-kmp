package io.nyris.sdk.demo

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nyris.sdk.camera.CameraView
import io.nyris.sdk.camera.CameraViewBuilder
import io.nyris.sdk.camera.ImageResult
import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.demo.databinding.ActivityCameraBinding

class CameraConfigActivity : AppCompatActivity() {
    private var cameraView: CameraView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cameraView = CameraViewBuilder(binding.root)
            .captureMode(CaptureMode.BARCODE)
            .focusMode(FocusMode.AUTOMATIC)
            .barcodeFormat(BarcodeFormat.ALL)
            .compressionFormat(CompressionFormat.WEBP)
            .build()

        with(binding) {
            torch.apply torch@{
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

            capture.setOnClickListener {
                cameraView?.capture(ImageResult::class) { result ->
                    Toast.makeText(
                        this@CameraConfigActivity,
                        "Image Captured! $result",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            stop.setOnClickListener {
                cameraView?.stop()
            }

            cameraView?.showDebug(true)

            cameraView?.error { error ->
                Toast.makeText(
                    this@CameraConfigActivity,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
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
