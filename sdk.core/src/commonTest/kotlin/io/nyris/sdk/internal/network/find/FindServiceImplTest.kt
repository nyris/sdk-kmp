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
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.mockk.CapturingSlot
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
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.internal.util.Logger
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
    private val endpoints = mockk<Endpoints>().apply {
        every { this@apply.find(any()) } returns ANY_ENDPOINT
    }
    private val xOptionsBuilder = mockk<XOptionsBuilder>(relaxed = true).apply {
        every { limit(any()) } returns this
        every { threshold(any()) } returns this
        every { build() } returns X_OPTIONS
    }
    private val httpClient = mockk<NyrisHttpClient>(relaxed = true)
    private val coroutineContext = UnconfinedTestDispatcher()

    private val classToTest: FindServiceImpl by lazy {
        FindServiceImpl(
            logger,
            xOptionsBuilder,
            endpoints,
            httpClient,
            coroutineContext
        )
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.network.find.FindServiceKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `find should get a success result when http post is successful`() = runTest {
        val image = ByteArray(1)
        val params = mockk<FindServiceParams>(relaxed = true).apply {
            every { this@apply.language } returns LANGUAGE
            every { this@apply.session } returns SESSION
        }
        val response = mockk<FindResponse>()
        val httpResponse = mockk<HttpResponse>().apply { coEvery { body<FindResponse>() } returns response }
        val builderSlot = slot<HttpRequestBuilder.() -> Unit>()
        coEvery { httpClient.post(ANY_ENDPOINT, capture(builderSlot)) } returns httpResponse

        val result = classToTest.find(image, params)

        builderSlot.assertHeaders()
        verifyAll {
            xOptionsBuilder.limit(any())
            xOptionsBuilder.threshold(any())
            xOptionsBuilder.build()
        }
        assertTrue(result.isSuccess)
        verify { endpoints.find(params.geolocation) }
        coVerify { httpClient.post(ANY_ENDPOINT, builderSlot.captured) }
        verify { buildMultiParamForm(image, params) }
        assertEquals(response, result.getOrNull())
        confirmVerified(endpoints, httpClient, xOptionsBuilder)
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

        headersSlot.captured.assertMultiFormHeaders()
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
}

private fun CapturingSlot<HttpRequestBuilder.() -> Unit>.assertHeaders() {
    val headers = HttpRequestBuilder().apply(captured).headers.build()
    assertEquals(LANGUAGE, headers[NyrisHttpHeaders.AcceptLanguage])
    assertEquals(SESSION, headers[NyrisHttpHeaders.XSession])
    assertEquals(X_OPTIONS, headers[NyrisHttpHeaders.XOptions])
}

private fun Headers.assertMultiFormHeaders() {
    assertEquals("filename=image.jpg", this[NyrisHttpHeaders.ContentDisposition])
    assertEquals(ContentType.Image.JPEG.toString(), this[NyrisHttpHeaders.ContentType])
    assertEquals("1", this[NyrisHttpHeaders.ContentLength])
}

private const val ANY_ENDPOINT = "ANY_ENDPOINT"
private const val LANGUAGE = "LANGUAGE"
private const val SESSION = "SESSION"
private const val X_OPTIONS = "X_OPTIONS"
