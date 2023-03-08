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
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.mockk.verifyAll
import io.mockk.verifyOrder
import io.nyris.sdk.ClientException
import io.nyris.sdk.ResponseException
import io.nyris.sdk.ServerException
import io.nyris.sdk.internal.network.find.FindResponseError
import io.nyris.sdk.internal.network.find.FindServiceParams
import io.nyris.sdk.util.Logger
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
    private val httpClient = mockk<HttpClientWrapper>(relaxed = true)

    private val classToTest: NyrisHttpClient by lazy {
        NyrisHttpClient(logger, httpClient)
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
        coEvery { httpClient.post(ANY_ENDPOINT, any()) } returns response

        val body = classToTest.post(ANY_ENDPOINT).body<Any>()

        assertEquals(body, expectedBody)
        coVerify { httpClient.post(ANY_ENDPOINT, any()) }
        confirmVerified(httpClient)
    }

    @Test
    fun `post should return result failure when http status code is not OK`() = runTest {
        val exception = mockk<ResponseException>(relaxed = true)
        val response = mockk<HttpResponse>(relaxed = true).apply {
            val body = mockk<FindResponseError>(relaxed = true)
            every { status } returns HttpStatusCode.Unauthorized
            coEvery { body<FindResponseError>() } returns body
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

    @Test
    fun `appendHeaders should append the correct headers`() {
        val userAgent = mockk<UserAgent>().apply {
            every { this@apply.toString() } returns USER_AGENT
        }
        val apiHeaders = ApiHeaders(API_KEY, userAgent)
        val params = mockk<FindServiceParams>().apply {
            every { this@apply.language } returns LANGUAGE
            every { this@apply.session } returns SESSION
        }
        val httpRequestBuilder = mockk<HttpRequestBuilder>(relaxed = true)

        httpRequestBuilder.appendHeaders(apiHeaders, params)

        verifyAll {
            httpRequestBuilder.headers.append("x-api-key", API_KEY)
            httpRequestBuilder.headers.append(HttpHeaders.UserAgent, USER_AGENT)
            httpRequestBuilder.headers.append(HttpHeaders.AcceptLanguage, LANGUAGE)
            httpRequestBuilder.headers.append("x-session", SESSION)
        }
        confirmVerified(httpRequestBuilder.headers)
    }

    @Test
    fun `appendImage should append image to for part`() {
        val image = ByteArray(1)
        val headersSlot = slot<Headers>()
        val formBuilder = mockk<FormBuilder>(relaxed = true)
        justRun {
            formBuilder.appendInput(
                key = "image",
                headers = capture(headersSlot),
                size = image.size.toLong(),
                any()
            )
        }

        formBuilder.appendImage(image)

        assertEquals(HEADERS, headersSlot.captured.toString())
        verify {
            formBuilder.appendInput(
                key = "image",
                headers = headersSlot.captured,
                size = image.size.toLong(),
                any()
            )
        }
        confirmVerified(formBuilder)
    }

    @Test
    fun `appendFilters should append filters to form part headers`() {
        val filters = mapOf("filterType0" to listOf("value0", "value1"))
        val formBuilder = mockk<FormBuilder>(relaxed = true)

        formBuilder.appendFilters(filters)

        verifyOrder {
            formBuilder.append("filters[0].filterType", "filterType0")
            formBuilder.append("filters[0].filterValues[0]", "value0")
            formBuilder.append("filters[0].filterValues[1]", "value1")
        }

        confirmVerified(formBuilder)
    }
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
private const val API_KEY = "API_KEY"
private const val USER_AGENT = "USER_AGENT"
private const val LANGUAGE = "LANGUAGE"
private const val SESSION = "SESSION"
private const val HEADERS = "Headers [" +
    "Content-Disposition=[filename=image.jpg], " +
    "Content-Type=[image/jpg], " +
    "Content-Length=[1]" +
    "]"
