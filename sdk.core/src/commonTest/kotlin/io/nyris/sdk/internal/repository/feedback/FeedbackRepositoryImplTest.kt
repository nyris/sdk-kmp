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

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.network.feedback.FeedbackRequest
import io.nyris.sdk.internal.network.feedback.FeedbackService
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.Feedback
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackRepositoryImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val feedbackService = mockk<FeedbackService>()

    private val classToTest: FeedbackRepositoryImpl by lazy {
        FeedbackRepositoryImpl(logger, feedbackService)
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.repository.feedback.FeedbackRequestMapperKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `send should can send feedback service`() = runTest {
        val feedback = mockk<Feedback>()
        val result = Result.success(Unit)
        val feedbackRequest = mockk<FeedbackRequest>()
        every { feedback.toFeedbackRequest() } returns feedbackRequest
        coEvery { feedbackService.send(feedbackRequest) } returns result

        val response = classToTest.send(feedback)

        assertTrue(response.isSuccess)
        coVerify { feedbackService.send(feedbackRequest) }
        verify { feedback.toFeedbackRequest() }
        confirmVerified(feedbackService, feedbackRequest)
    }
}
