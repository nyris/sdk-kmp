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

import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder
import io.nyris.sdk.internal.NyrisImpl

actual interface Nyris {
    actual fun imageMatching(): ImageMatchingRequestBuilder
    actual fun objectDetecting(): ObjectDetectingRequestBuilder
    actual fun feedback(): FeedbackRequestBuilder
    actual fun skuMatching(): SkuMatchingRequestBuilder

    actual companion object {
        internal actual fun createInstance(
            apiKey: String,
            config: NyrisConfig,
        ): Nyris = NyrisImpl.createInstance(
            apiKey = apiKey,
            config = config.copy(
                platform = NyrisPlatform.IOS
            )
        )
    }
}

class NyrisService(
    apiKey: String,
    isDebug: Boolean,
) : Nyris {
    private val instance = NyrisImpl.createInstance(
        apiKey = apiKey,
        config = NyrisConfig(isDebug = true, platform = NyrisPlatform.IOS)
    )

    override fun imageMatching(): ImageMatchingRequestBuilder {
        return instance.imageMatching()
    }

    override fun objectDetecting(): ObjectDetectingRequestBuilder {
        return instance.objectDetecting()
    }

    override fun feedback(): FeedbackRequestBuilder {
        return instance.feedback()
    }

    override fun skuMatching(): SkuMatchingRequestBuilder {
        return instance.skuMatching()
    }
}
