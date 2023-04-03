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

import android.content.res.AssetManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.nyris.sdk.Nyris
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.ResponseException
import io.nyris.sdk.demo.databinding.ActivitySdkBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SdkActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivitySdkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val nyris = Nyris.createInstance(
            apiKey = BuildConfig.API_KEY,
            config = NyrisConfig(isDebug = true)
        )

        CoroutineScope(Dispatchers.Main + Job()).launch {
            nyris.imageMatching()
                .match(assets.loadImage("test_image.jpg"))
                .onSuccess {
                    binding.tv.text = it.toString()
                    Toast.makeText(applicationContext, "Woop Woop!", Toast.LENGTH_LONG).show()
                }
                .onFailure {
                    if (it is ResponseException) {
                        Toast.makeText(applicationContext, it.toString(), Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(applicationContext, it.message, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}

private fun AssetManager.loadImage(path: String): ByteArray = open(path).run {
    ByteArray(available()).apply {
        read(this)
        close()
    }
}
