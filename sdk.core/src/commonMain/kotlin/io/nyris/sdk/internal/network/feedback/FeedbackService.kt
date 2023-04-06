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
package io.nyris.sdk.internal.network.feedback

import io.nyris.sdk.builder.NyrisResult

internal interface FeedbackService {
    suspend fun send(feedbackRequest: FeedbackRequest): NyrisResult
}

internal class FeedbackServiceImpl : FeedbackService {
    override suspend fun send(feedbackRequest: FeedbackRequest): NyrisResult {
        TODO("Not yet implemented")
    }
}


internal class FeedbackRequest(
    val requestId: String,
    val sessionId: String,
    val timestamp: String,
    val eventType: String,
    val data: FeedbackDto,
)

internal sealed class FeedbackDto {
    internal object EMPTY : FeedbackDto()
}

internal class ClickFeedbackDto(
    val positions: List<Int>,
    val productIds: List<String>,
) : FeedbackDto()

internal class ConversationFeedbackDto(
    val positions: List<Int>,
    val productIds: List<String>,
) : FeedbackDto()

internal class CommentFeedbackDto(
    val success: Boolean,
    val comment: String?,
) : FeedbackDto()

internal class RegionFeedbackDto(
    val rect: RectDto,
) : FeedbackDto()

internal class RectDto(
    val x: Float,
    val y: Float,
    val w: Float,
    val h: Float,
)
