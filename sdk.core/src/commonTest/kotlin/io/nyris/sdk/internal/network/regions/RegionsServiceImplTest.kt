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
package io.nyris.sdk.internal.network.regions

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.util.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class RegionsServiceImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val endpoints = mockk<Endpoints>().apply {
        every { regions } returns ANY_ENDPOINT
    }
    private val httpClient = mockk<NyrisHttpClient>(relaxed = true)
    private val coroutineContext = UnconfinedTestDispatcher()

    private val classToTest: RegionsServiceImpl by lazy {
        RegionsServiceImpl(
            logger,
            endpoints,
            httpClient,
            coroutineContext
        )
    }

    @Test
    fun `detect should get a success result when http post is successful`() = runTest {
        val image = ByteArray(1)
        val params = mockk<RegionsServiceParams>(relaxed = true).apply {
            every { this@apply.session } returns SESSION
        }
        val response = mockk<RegionsResponse>()
        val httpResponse = mockk<HttpResponse>().apply { coEvery { body<RegionsResponse>() } returns response }
        val builderSlot = slot<HttpRequestBuilder.() -> Unit>()
        coEvery { httpClient.post(ANY_ENDPOINT, capture(builderSlot)) } returns httpResponse

        val result = classToTest.detect(image, params)

        builderSlot.assertHeaders()
        assertTrue(result.isSuccess)
        verify { endpoints.regions }
        coVerify { httpClient.post(ANY_ENDPOINT, builderSlot.captured) }
        assertEquals(response, result.getOrNull())
        confirmVerified(endpoints, httpClient)
    }

    @Test
    fun `detect should get a failure result when http throw exception`() = runTest {
        val image = ByteArray(1)
        val params = mockk<RegionsServiceParams>(relaxed = true)
        val expectedException = Throwable("The exception!")
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws expectedException

        val result = classToTest.detect(image, params)

        verify { endpoints.regions }
        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}

private fun CapturingSlot<HttpRequestBuilder.() -> Unit>.assertHeaders() {
    val headers = HttpRequestBuilder().apply(captured).headers.build()
    assertEquals(SESSION, headers[NyrisHttpHeaders.XSession])
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
private const val SESSION = "SESSION"
