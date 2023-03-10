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
package io.nyris.sdk.internal.network.recommend

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
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
class RecommendServiceTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val endpoints = mockk<Endpoints>(relaxed = true) endpoint@{
        every { this@endpoint.recommend(any()) } returns ANY_ENDPOINT
    }
    private val httpClient = mockk<NyrisHttpClient>(relaxed = true)
    private val coroutineContext = UnconfinedTestDispatcher()

    private val classToTest: RecommendService by lazy {
        RecommendServiceImpl(
            logger,
            endpoints,
            httpClient,
            coroutineContext
        )
    }

    @Test
    fun `match should get recommendation successfully`() = runTest {
        val response = mockk<RecommendResponse>()
        val httpResponse = mockk<HttpResponse>().apply { coEvery { body<RecommendResponse>() } returns response }
        coEvery { httpClient.get(ANY_ENDPOINT, any()) } returns httpResponse

        val result = classToTest.match(SKU)

        assertTrue(result.isSuccess)
        verify { endpoints.recommend(SKU) }
        coVerify { httpClient.get(ANY_ENDPOINT, any()) }
        assertEquals(response, result.getOrNull())
        confirmVerified(endpoints, httpClient)
    }

    @Test
    fun `match should fail to get recommendation when http throw exception`() = runTest {
        val expectedException = Throwable("The exception!")
        coEvery { httpClient.get(ANY_ENDPOINT, any()) } throws expectedException

        val result = classToTest.match(SKU)

        verify { endpoints.recommend(SKU) }
        coVerify { httpClient.get(ANY_ENDPOINT, any()) }
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
private const val SKU = "SKU"
