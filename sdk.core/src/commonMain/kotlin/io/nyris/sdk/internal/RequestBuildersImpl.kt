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

internal class RequestBuildersImpl(
    private val imageMatching: ImageMatchingRequestBuilder,
    private val objectDetecting: ObjectDetectingRequestBuilder,
    private val feedback: FeedbackRequestBuilder,
) : RequestBuilders {
    override fun imageMatching(): ImageMatchingRequestBuilder = imageMatching

    override fun objectDetecting(): ObjectDetectingRequestBuilder = objectDetecting

    override fun feedback(): FeedbackRequestBuilder = feedback
}
