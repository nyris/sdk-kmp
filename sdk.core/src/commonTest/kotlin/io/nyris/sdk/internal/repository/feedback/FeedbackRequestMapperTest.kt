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

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.nyris.sdk.internal.network.feedback.ClickFeedbackDto
import io.nyris.sdk.internal.network.feedback.CommentFeedbackDto
import io.nyris.sdk.internal.network.feedback.ConversationFeedbackDto
import io.nyris.sdk.internal.network.feedback.FeedbackRequest
import io.nyris.sdk.internal.network.feedback.RectDto
import io.nyris.sdk.internal.network.feedback.RegionFeedbackDto
import io.nyris.sdk.model.Feedback
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class FeedbackRequestMapperTest {
    @BeforeTest
    fun setup() {
        mockkObject(TimeProvider)
        every { TimeProvider.currentTime() } returns ANY_TIME
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `toEventType should map Feedback Types to EventType`() {
        val listFeedbackType = listOf(
            mockk<Feedback.Click>(),
            mockk<Feedback.Conversion>(),
            mockk<Feedback.Comment>(),
            mockk<Feedback.Region>()
        )

        val eventTypes = listFeedbackType.map { feedback -> feedback.toEventType() }

        assertContentEquals(EXPECTED_EVENT_TYPES, eventTypes)
    }

    @Test
    fun `toData should map Feedback to FeedbackDto`() {
        val listFeedbackType = listOf(
            mockk<Feedback.Click>(relaxed = true),
            mockk<Feedback.Conversion>(relaxed = true),
            mockk<Feedback.Comment>(relaxed = true),
            mockk<Feedback.Region>(relaxed = true)
        )

        val feedbackDtoList = listFeedbackType.map { feedback -> feedback.toData() }

        assertContentEquals(EXPECTED_DATA_DTO, feedbackDtoList)
    }

    @Test
    fun `toFeedbackRequest should map Feedback to FeedbackRequest`() {
        val feedbackRequest = mockk<Feedback.Comment>(relaxed = true).toFeedbackRequest()

        assertEquals(EXPECTED_FEEDBACK_REQUEST, feedbackRequest)
    }
}

private const val ANY_TIME = "ANY_TIME"
private val EXPECTED_EVENT_TYPES = listOf("click", "conversion", "feedback", "region")
private val EXPECTED_DATA_DTO = listOf(
    ClickFeedbackDto(emptyList(), emptyList()),
    ConversationFeedbackDto(emptyList(), emptyList()),
    CommentFeedbackDto(false, ""),
    RegionFeedbackDto(RectDto(0.0F, 0.0F, 0.0F, 0.0F))
)

private val EXPECTED_FEEDBACK_REQUEST = FeedbackRequest(
    requestId = "",
    sessionId = "",
    timestamp = ANY_TIME,
    eventType = "feedback",
    data = CommentFeedbackDto(false, "")
)
