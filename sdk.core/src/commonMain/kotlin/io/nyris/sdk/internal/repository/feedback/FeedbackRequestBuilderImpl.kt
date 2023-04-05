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
package io.nyris.sdk.internal.repository.feedback

import io.nyris.sdk.NyrisResultCompletable
import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.Feedback

internal class FeedbackRequestBuilderImpl(
    private val logger: Logger,
    private val feedbackRepository: FeedbackRepository,
) : FeedbackRequestBuilder {
    override suspend fun send(feedback: Feedback): NyrisResultCompletable {
        logger.log("[FeedbackRequestBuilderImpl] send")
        return feedbackRepository.send(feedback)
    }
}
