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
import io.nyris.sdk.model.Feedback
import kotlinx.coroutines.runBlocking

object MainApp {
    @JvmStatic
    fun main(args: Array<String>) {
        val nyris = Nyris.createInstance(
            apiKey = System.getenv("PUBLIC_API_KEY"),
            config = NyrisConfig(isDebug = true)
        )

        runBlocking {
            with(nyris) {
                imageMatchingDemo()
                objectDetectingDemo()
                feedbackDemo()
                skuMatchingDemo()
            }
        }
    }

    private suspend fun Nyris.imageMatchingDemo() {
        imageMatching()
            .match(loadImage("test_image.jpg"))
            .onSuccess {
                println("Successful image matching!")
            }
            .onFailure {
                if (it is ResponseException) {
                    println(it.toString())
                } else {
                    println(it.message)
                }
            }
    }

    private suspend fun Nyris.objectDetectingDemo() {
        objectDetecting()
            .detect(loadImage("test_image2.jpg"))
            .onSuccess {
                println("Successful object detecting!")
            }
            .onFailure {
                if (it is ResponseException) {
                    println(it.toString())
                } else {
                    println(it.message)
                }
            }
    }

    private suspend fun Nyris.feedbackDemo() {
        feedback()
            .send(
                Feedback.Region(
                    requestId = "9a2e5c0d56ff5f52ae270de10ba97f45",
                    sessionId = "9a2e5c0d56ff5f52ae270de10ba97f45",
                    left = 0.1F,
                    top = 0.1F,
                    height = 0.1F,
                    width = 0.1F,
                )
            )
            .onSuccess {
                println("Successful feedback sending!")
            }
            .onFailure {
                if (it is ResponseException) {
                    println(it.toString())
                } else {
                    println(it.message)
                }
            }
    }

    private suspend fun Nyris.skuMatchingDemo() {
        skuMatching()
            .match("10371203")
            .onSuccess {
                println("Successful sku matching!")
            }
            .onFailure {
                if (it is ResponseException) {
                    println(it.toString())
                } else {
                    println(it.message)
                }
            }
    }

    private fun loadImage(path: String): ByteArray =
        this::class.java.classLoader?.getResourceAsStream(path)?.readAllBytes()
            ?: throw IllegalArgumentException("Image not available")
}
