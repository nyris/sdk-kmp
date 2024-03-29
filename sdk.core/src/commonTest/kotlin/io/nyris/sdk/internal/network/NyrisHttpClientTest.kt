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
package io.nyris.sdk.internal.network

import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.mockk.CapturingSlot
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.nyris.sdk.ClientException
import io.nyris.sdk.ResponseException
import io.nyris.sdk.ServerException
import io.nyris.sdk.internal.util.Logger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class NyrisHttpClientTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val commonHeaders: CommonHeaders by lazy {
        val userAgent = mockk<UserAgent>().apply { every { this@apply.toString() } returns USER_AGENT }
        CommonHeaders(API_KEY, userAgent)
    }
    private val httpClient = mockk<HttpClientWrapper>(relaxed = true)

    private val classToTest: NyrisHttpClient by lazy {
        NyrisHttpClient(logger, commonHeaders, httpClient)
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.network.ExceptionMapperKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `post should return result success when http status code is OK`(): Unit = runTest {
        val expectedBody = mockk<Any>()
        val response = mockk<HttpResponse>(relaxed = true).apply {
            every { status } returns HttpStatusCode.OK
            coEvery { body<Any>() } returns expectedBody
        }
        val builderSlot = slot<HttpRequestBuilder.() -> Unit>()
        coEvery { httpClient.post(ANY_ENDPOINT, capture(builderSlot)) } returns response

        val body = classToTest.post(ANY_ENDPOINT).body<Any>()

        builderSlot.assertHeaders()
        assertEquals(body, expectedBody)
        coVerify { httpClient.post(ANY_ENDPOINT, builderSlot.captured) }
        confirmVerified(httpClient)
    }

    @Test
    fun `post should return result failure when http status code is not OK`() = runTest {
        val exception = mockk<ResponseException>(relaxed = true)
        val response = mockk<HttpResponse>(relaxed = true).apply {
            val body = mockk<ApiError>(relaxed = true)
            every { status } returns HttpStatusCode.Unauthorized
            coEvery { body<ApiError>() } returns body
            every { body.toNyrisException() } returns exception
        }
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } returns response

        assertFailsWith<ResponseException> {
            classToTest.post(ANY_ENDPOINT)
        }

        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        confirmVerified(httpClient)
    }

    @Test
    fun `post should return result failure when client throw ClientException`() = runTest {
        val exception = mockk<ClientRequestException>(relaxed = true)
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws exception
        every { exception.toNyrisException() } returns mockk()

        assertFailsWith<ClientException> {
            classToTest.post(ANY_ENDPOINT)
        }

        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        confirmVerified(httpClient)
    }

    @Test
    fun `post should return result failure when client throw ServerException`() = runTest {
        val exception = mockk<ServerResponseException>(relaxed = true)
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws exception
        every { exception.toNyrisException() } returns mockk()

        assertFailsWith<ServerException> {
            classToTest.post(ANY_ENDPOINT)
        }

        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        confirmVerified(httpClient)
    }

    @Test
    fun `post should return result failure when client throw any exception`() = runTest {
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } throws Throwable("A throwable")

        assertFailsWith<Throwable> {
            classToTest.post(ANY_ENDPOINT)
        }

        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        confirmVerified(httpClient)
    }
}

private fun CapturingSlot<HttpRequestBuilder.() -> Unit>.assertHeaders() {
    val headers = HttpRequestBuilder().apply(captured).headers.build()
    assertEquals(API_KEY, headers[NyrisHttpHeaders.XApiKey])
    assertEquals(USER_AGENT, headers[NyrisHttpHeaders.UserAgent])
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
private const val API_KEY = "API_KEY"
private const val USER_AGENT = "USER_AGENT"
