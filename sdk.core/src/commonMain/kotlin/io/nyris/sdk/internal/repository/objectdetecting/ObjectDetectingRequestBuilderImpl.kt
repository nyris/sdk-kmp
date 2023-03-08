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
package io.nyris.sdk.internal.repository.objectdetecting

import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.model.DetectResponse
import io.nyris.sdk.util.Logger

internal class ObjectDetectingRequestBuilderImpl(
    private val logger: Logger,
    private val objectDetectingRepository: ObjectDetectingRepository,
) : ObjectDetectingRequestBuilder {
    private var session: String? = null

    override fun session(session: String) = apply {
        logger.log("[ObjectDetectingRequestBuilderImpl] session=$session")
        this.session = session
    }

    override suspend fun detect(image: ByteArray): Result<DetectResponse> {
        logger.log("[ObjectDetectingRequestBuilderImpl] detect")
        return objectDetectingRepository.detect(image, createParams())
    }

    internal fun createParams(): ObjectDetectingParams = ObjectDetectingParams(
        session = session
    ).also {
        logger.log("[ObjectDetectingRequestBuilderImpl] createParams[params=$it]")
        reset()
    }

    internal fun reset() {
        logger.log("[ObjectDetectingRequestBuilderImpl] reset")
        session = null
    }
}
