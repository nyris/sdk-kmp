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

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class FeedbackRequest(
    @SerialName("request_id")
    val requestId: String,
    @SerialName("session_id")
    val sessionId: String,
    @SerialName("timestamp")
    val timestamp: String,
    @SerialName("event")
    val eventType: String,
    @SerialName("data")
    val data: FeedbackDto,
)

@Serializable(FeedbackDtoSerializer::class)
internal sealed class FeedbackDto {
    internal object EMPTY : FeedbackDto()
}

@Serializable
internal data class ClickFeedbackDto(
    @SerialName("positions")
    val positions: List<Int>,
    @SerialName("product_ids")
    val productIds: List<String>,
) : FeedbackDto()

@Serializable
internal data class ConversationFeedbackDto(
    @SerialName("positions")
    val positions: List<Int>,
    @SerialName("product_ids")
    val productIds: List<String>,
) : FeedbackDto()

@Serializable
internal data class CommentFeedbackDto(
    @SerialName("success")
    val success: Boolean,
    @SerialName("comment")
    val comment: String?,
) : FeedbackDto()

@Serializable
internal data class RegionFeedbackDto(
    @SerialName("rect")
    val rect: RectDto,
) : FeedbackDto()

@Serializable
internal data class RectDto(
    @SerialName("x")
    val x: Float,
    @SerialName("y")
    val y: Float,
    @SerialName("w")
    val w: Float,
    @SerialName("h")
    val h: Float,
)
