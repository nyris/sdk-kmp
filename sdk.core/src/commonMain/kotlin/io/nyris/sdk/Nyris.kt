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
package io.nyris.sdk

import io.ktor.client.engine.HttpClientEngineConfig
import io.ktor.client.engine.HttpClientEngineFactory
import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder

expect interface Nyris {
    fun imageMatching(): ImageMatchingRequestBuilder

    fun objectDetecting(): ObjectDetectingRequestBuilder

    fun feedback(): FeedbackRequestBuilder

    fun skuMatching(): SkuMatchingRequestBuilder

    companion object {
        fun createInstance(
            apiKey: String,
            config: NyrisConfig = NyrisConfig(),
        ): Nyris
    }
}

typealias HttpEngine = HttpClientEngineFactory<HttpClientEngineConfig>
