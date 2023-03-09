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
package io.nyris.sdk.internal.repository.objectdetecting

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.network.regions.RegionsResponse
import io.nyris.sdk.internal.network.regions.RegionsService
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.DetectResponse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectDetectingRepositoryTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val regionsService = mockk<RegionsService>()

    private val classToTest: ObjectDetectingRepository by lazy {
        ObjectDetectingRepositoryImpl(logger, regionsService)
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.repository.objectdetecting.DetectResponseMapperKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `match should call find service and map the response`(): Unit = runTest {
        val image = ByteArray(1)
        val params = ObjectDetectingParams(null)
        val regionsResponse = mockk<RegionsResponse>()
        val result = Result.success(regionsResponse)
        val detectResponse = mockk<DetectResponse>()
        coEvery { regionsService.detect(image, any()) } returns result
        every { regionsResponse.toDetectResponse() } returns detectResponse

        val response = classToTest.detect(image, params)

        assertEquals(response.getOrNull(), detectResponse)
        coVerify { regionsService.detect(image, any()) }
        verify { regionsResponse.toDetectResponse() }
        confirmVerified(regionsService)
    }

    @Test
    fun `toParams should map ObjectDetectingParams to RegionsServiceParams`() {
        with(
            ObjectDetectingParams(
                session = "session"
            ).toParams()
        ) {
            assertEquals("session", session)
        }
    }
}
