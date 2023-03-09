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

import io.nyris.sdk.internal.network.feedback.ClickFeedbackDto
import io.nyris.sdk.internal.network.feedback.CommentFeedbackDto
import io.nyris.sdk.internal.network.feedback.ConversationFeedbackDto
import io.nyris.sdk.internal.network.feedback.FeedbackDto
import io.nyris.sdk.internal.network.feedback.FeedbackRequest
import io.nyris.sdk.internal.network.feedback.RectDto
import io.nyris.sdk.internal.network.feedback.RegionFeedbackDto
import io.nyris.sdk.model.Feedback
import kotlinx.datetime.Clock

internal fun Feedback.toFeedbackRequest(): FeedbackRequest = with(this) {
    FeedbackRequest(
        requestId = this.requestId,
        sessionId = this.sessionId,
        timestamp = TimeProvider.currentTime(),
        eventType = this.toEventType(),
        data = this.toData()
    )
}

internal fun Feedback.toEventType(): String = when (this) {
    is Feedback.Click -> "click"
    is Feedback.Conversion -> "conversion"
    is Feedback.Comment -> "feedback"
    is Feedback.Region -> "region"
}

internal fun Feedback.toData(): FeedbackDto = when (this) {
    is Feedback.Click -> {
        ClickFeedbackDto(
            productIds = this.productIds,
            positions = this.positions
        )
    }
    is Feedback.Conversion -> {
        ConversationFeedbackDto(
            productIds = this.productIds,
            positions = this.positions
        )
    }
    is Feedback.Comment -> {
        CommentFeedbackDto(
            success = this.success,
            comment = this.comment
        )
    }
    is Feedback.Region -> {
        RegionFeedbackDto(
            RectDto(
                x = this.left,
                y = this.top,
                w = this.width,
                h = this.height
            )
        )
    }
}

internal object TimeProvider {
    fun currentTime(): String = Clock.System.now().toString()
}
