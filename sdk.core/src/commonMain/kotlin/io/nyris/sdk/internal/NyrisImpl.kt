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
package io.nyris.sdk.internal

import io.nyris.sdk.Nyris
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.internal.di.ServiceLocator

internal class NyrisImpl private constructor(
    apiKey: String,
    config: NyrisConfig,
) : Nyris {
    private val requestBuilders: RequestBuilders

    init {
        ServiceLocator.init(apiKey, config)
        requestBuilders = ServiceLocator.get<RequestBuilders>().value
    }

    override fun imageMatching(): ImageMatchingRequestBuilder = requestBuilders.imageMatching()

    override fun objectDetecting(): ObjectDetectingRequestBuilder = requestBuilders.objectDetecting()

    override fun feedback(): FeedbackRequestBuilder = requestBuilders.feedback()

    companion object {
        fun createInstance(
            apiKey: String,
            config: NyrisConfig,
        ): NyrisImpl {
            return NyrisImpl(apiKey, config)
        }
    }
}
