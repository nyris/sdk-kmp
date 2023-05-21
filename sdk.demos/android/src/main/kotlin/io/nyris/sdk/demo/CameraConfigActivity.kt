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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import io.nyris.sdk.camera.core.FeatureMode
import io.nyris.sdk.demo.databinding.ActivityCameraConfigBinding

class CameraConfigActivity : AppCompatActivity() {
    private val binding: ActivityCameraConfigBinding by lazy {
        ActivityCameraConfigBinding.inflate(layoutInflater).apply { barcodeFormatSp.isEnabled = false }
    }
    private var captureMode = 0
    private var focusMode = 0
    private var barcodeFormat = 0
    private var isBarcodeGuideEnabled = false
    private var compressionFormat = 0
    private var compressionQuality = QUALITY
    private var isDebugInfo = true
    private val onItemSelectedListener = object : OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>,
            view: View,
            position: Int,
            id: Long,
        ) {
            when (parent.id) {
                R.id.captureModeSp -> {
                    captureMode = position
                }
                R.id.barcodeFormatSp -> barcodeFormat = position
                R.id.focusModeSp -> focusMode = position
                R.id.compressionFormatSp -> compressionFormat = position
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // NO-OP
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(binding) {
            setContentView(root)
            barcodeFeature.setOnCheckedChangeListener { _, isChecked ->
                binding.barcodeFormatSp.isEnabled = isChecked
                binding.barcodeGuideSwitch.isEnabled = isChecked
            }
            captureModeSp.onItemSelectedListener = onItemSelectedListener
            focusModeSp.onItemSelectedListener = onItemSelectedListener
            barcodeFormatSp.onItemSelectedListener = onItemSelectedListener
            compressionFormatSp.onItemSelectedListener = onItemSelectedListener

            debugInfoSwitch.setOnCheckedChangeListener { _, isChecked -> isDebugInfo = isChecked }
            barcodeGuideSwitch.setOnCheckedChangeListener { _, isChecked -> isBarcodeGuideEnabled = isChecked }

            startCameraBtn.setOnClickListener {
                compressionQuality = qualityEt.text.toString().toIntOrNull() ?: compressionQuality

                val featureModes =
                    if (barcodeFeature.isChecked) FeatureMode.CAPTURE or FeatureMode.BARCODE else FeatureMode.CAPTURE
                startActivity(
                    Intent(this@CameraConfigActivity, CameraActivity::class.java).apply {
                        putExtras(
                            bundleOf(
                                FEATURE_MODE_KEY to featureModes,
                                CAPTURE_MODE_KEY to captureMode,
                                FOCUS_MODE_KEY to focusMode,
                                BARCODE_FORMAT_KEY to barcodeFormat,
                                BARCODE_GUIDE_KEY to isBarcodeGuideEnabled,
                                COMPRESSION_FORMAT_KEY to compressionFormat,
                                COMPRESSION_QUALITY_KEY to compressionQuality,
                                IS_DEBUG_KEY to isDebugInfo
                            )
                        )
                    }
                )
            }
        }
    }

    companion object {
        const val FEATURE_MODE_KEY = "FEATURE_MODE_KEY"
        const val CAPTURE_MODE_KEY = "CAPTURE_MODE_KEY"
        const val FOCUS_MODE_KEY = "FOCUS_MODE_KEY"
        const val BARCODE_FORMAT_KEY = "BARCODE_FORMAT_KEY"
        const val BARCODE_GUIDE_KEY = "BARCODE_GUIDE_KEY"
        const val COMPRESSION_FORMAT_KEY = "COMPRESSION_FORMAT_KEY"
        const val COMPRESSION_QUALITY_KEY = "COMPRESSION_QUALITY_KEY"
        const val IS_DEBUG_KEY = "IS_DEBUG_KEY"
    }
}

private const val QUALITY = 90
