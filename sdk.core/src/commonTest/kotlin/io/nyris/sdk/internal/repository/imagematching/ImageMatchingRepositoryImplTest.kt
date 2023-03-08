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
package io.nyris.sdk.internal.repository.imagematching

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.network.find.FindResponse
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.model.MatchResponse
import io.nyris.sdk.util.Logger
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ImageMatchingRepositoryImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val findService = mockk<FindService>()

    private val classToTest: ImageMatchingRepositoryImpl by lazy {
        ImageMatchingRepositoryImpl(logger, findService)
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.repository.imagematching.MatchResponseMapperKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `match should call find service and map the response`(): Unit = runTest {
        val image = ByteArray(1)
        val params = ImageMatchingParams(
            null, null, null, null, emptyMap(), null
        )
        val findResponse = mockk<FindResponse>()
        val result = Result.success(findResponse)
        val matchResponse = mockk<MatchResponse>()
        coEvery { findService.find(image, any()) } returns result
        every { findResponse.toMatchResponse() } returns matchResponse

        val response = classToTest.match(image, params)

        assertEquals(response.getOrNull(), matchResponse)
        coVerify { findService.find(image, any()) }
        verify { findResponse.toMatchResponse() }
        confirmVerified(findService)
    }

    @Test
    fun `toParams should map ImageMatchingParams to FindServiceParams`() {
        with(
            ImageMatchingParams(
                limit = 10,
                language = "*",
                threshold = 0.1F,
                geolocation = null,
                filters = emptyMap(),
                session = "session"
            ).toParams()
        ) {
            assertEquals(10, limit)
            assertEquals("*", language)
            assertEquals(0.1F, threshold)
            assertEquals(null, geolocation)
            assertEquals(emptyMap(), filters)
            assertEquals("session", session)
        }
    }
}
