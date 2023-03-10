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

import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder

/**
 * Since the Request builders class represents the components for each implemented api, we lazy load the builders to
 * avoid heavy waist of time on sdk instance creation.
 * Please make sure to respect that
 */
internal class RequestBuildersImpl(
    private val imageMatching: Lazy<ImageMatchingRequestBuilder>,
    private val objectDetecting: Lazy<ObjectDetectingRequestBuilder>,
    private val feedback: Lazy<FeedbackRequestBuilder>,
    private val skuMatching: Lazy<SkuMatchingRequestBuilder>,
) : RequestBuilders {
    override fun imageMatching(): ImageMatchingRequestBuilder = imageMatching.value

    override fun objectDetecting(): ObjectDetectingRequestBuilder = objectDetecting.value

    override fun feedback(): FeedbackRequestBuilder = feedback.value

    override fun skuMatching(): SkuMatchingRequestBuilder = skuMatching.value
}
