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
package io.nyris.demo

import io.nyris.sdk.Nyris
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.ResponseException
import kotlinx.coroutines.runBlocking

object MainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val nyris = Nyris.createInstance(
            apiKey = System.getenv("PUBLIC_API_KEY"),
            config = NyrisConfig(isDebug = true)
        )

        runBlocking {
            nyris.imageMatching()
                .match(loadImage())
                .onSuccess {
                    println("woop woop!")
                }
                .onFailure {
                    if (it is ResponseException) {
                        println(it.toString())
                    } else {
                        println(it.message)
                    }
                }
        }
    }

    private fun loadImage(): ByteArray =
        this::class.java.classLoader?.getResourceAsStream("test_image.jpg")?.readAllBytes()
            ?: throw IllegalArgumentException("Image not available")
}
