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
package io.nyris.sdk.internal.network.find

import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.network.ApiHeaders
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.appendHeaders
import io.nyris.sdk.internal.network.buildMultiParamForm
import io.nyris.sdk.util.Logger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class FindServiceImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val apiHeaders = mockk<ApiHeaders>(relaxed = true)
    private val endpoints = mockk<Endpoints>().apply {
        every { this@apply.find(any()) } returns ANY_ENDPOINT
    }
    private val httpClient = mockk<NyrisHttpClient>(relaxed = true)
    private val coroutineContext = UnconfinedTestDispatcher()

    private val classToTest: FindServiceImpl by lazy {
        FindServiceImpl(
            logger,
            apiHeaders,
            endpoints,
            httpClient,
            coroutineContext
        )
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.network.NyrisHttpClientKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `find should get a success result when http post is successful`() = runTest {
        val image = ByteArray(1)
        val params = mockk<FindServiceParams>(relaxed = true)
        val response = mockk<FindResponse>()
        val httpResponse = mockk<HttpResponse>().apply {
            coEvery { body<FindResponse>() } returns response
        }
        val builderSlot = slot<HttpRequestBuilder.() -> Unit>()
        coEvery { httpClient.post(ANY_ENDPOINT, capture(builderSlot)) } returns httpResponse

        val result = classToTest.find(image, params)

        builderSlot.verifyHttpRequestBuilder(apiHeaders, params)
        verify { buildMultiParamForm(image, params) }
        verify { endpoints.find(params.geolocation) }
        coVerify { httpClient.post(ANY_ENDPOINT, builderSlot.captured) }
        assertTrue(result.isSuccess)
        assertEquals(response, result.getOrNull())
    }

    @Test
    fun `find should get a failure result when http throw exception`() = runTest {
        val image = ByteArray(1)
        val params = mockk<FindServiceParams>(relaxed = true)
        val expectedException = Throwable("Yay an exception!")
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws expectedException

        val result = classToTest.find(image, params)

        verify { endpoints.find(params.geolocation) }
        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        assertTrue(result.isFailure)
        assertEquals(expectedException, result.exceptionOrNull())
    }
}

private fun CapturingSlot<HttpRequestBuilder.() -> Unit>.verifyHttpRequestBuilder(
    apiHeaders: ApiHeaders,
    params: FindServiceParams,
) {
    HttpRequestBuilder().apply(captured).run {
        verify { appendHeaders(apiHeaders, params) }
    }
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
