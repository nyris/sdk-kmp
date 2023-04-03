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
import androidx.appcompat.app.AppCompatActivity
import io.nyris.sdk.demo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        with(ActivityMainBinding.inflate(layoutInflater)) {
            setContentView(root)

            val onClickListener = View.OnClickListener { view ->
                val kclass = when (view.id) {
                    R.id.cameraBtn -> CameraActivity::class
                    R.id.sdkBtn -> SdkActivity::class
                    R.id.searcherBtn -> SearcherActivity::class
                    else -> throw IllegalArgumentException("Button click not handled!")
                }
                startActivity(Intent(this@MainActivity, kclass.java))
            }

            cameraBtn.setOnClickListener(onClickListener)
            sdkBtn.setOnClickListener(onClickListener)
            searcherBtn.setOnClickListener(onClickListener)
        }
    }
}
