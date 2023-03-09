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

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.http.HttpHeaders
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.util.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class FeedbackServiceTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val endpoints = mockk<Endpoints> endpoint@{
        every { this@endpoint.feedback } returns ANY_ENDPOINT
    }
    private val httpClient = mockk<NyrisHttpClient>(relaxed = true)
    private val coroutineContext = UnconfinedTestDispatcher()

    private val classToTest: FeedbackServiceImpl by lazy {
        FeedbackServiceImpl(
            logger,
            endpoints,
            httpClient,
            coroutineContext
        )
    }

    @Test
    fun `send should post feedback request successfully`() = runTest {
        val feedbackRequest = mockk<FeedbackRequest>()
        val builderSlot = slot<HttpRequestBuilder.() -> Unit>()
        coJustRun { httpClient.post(ANY_ENDPOINT, capture(builderSlot)) }

        val result = classToTest.send(feedbackRequest)

        builderSlot.assertHeaders()
        assertTrue(result.isSuccess)
        verify { endpoints.feedback }
        coVerify { httpClient.post(ANY_ENDPOINT, builderSlot.captured) }
        confirmVerified(endpoints, httpClient)
    }

    @Test
    fun `send should fail to post when when http throw exception`() = runTest {
        val feedbackRequest = mockk<FeedbackRequest>()
        val expectedException = Throwable("The exception!")
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws expectedException

        val result = classToTest.send(feedbackRequest)

        verify { endpoints.feedback }
        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}

private fun CapturingSlot<HttpRequestBuilder.() -> Unit>.assertHeaders() {
    val headers = HttpRequestBuilder().apply(captured).headers.build()
    assertEquals("application/json", headers[HttpHeaders.ContentType])
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
