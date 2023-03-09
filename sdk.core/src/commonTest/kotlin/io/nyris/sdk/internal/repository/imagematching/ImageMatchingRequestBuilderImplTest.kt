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
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.nyris.sdk.internal.util.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ImageMatchingRequestBuilderImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val imageMatchingRepository = mockk<ImageMatchingRepository>(relaxed = true)

    private val classToTest: ImageMatchingRequestBuilderImpl by lazy {
        ImageMatchingRequestBuilderImpl(logger, imageMatchingRepository)
    }

    @Test
    fun `createParams should create the expected parameters`() {
        val params = classToTest.createParams()

        assertEquals(EXPECTED_DEFAULT_PARAMS, params.toString())
        verify { classToTest.reset() }
    }

    @Test
    fun `reset should reset builder properties`() {
        classToTest.limit(1)
            .language("language")
            .threshold(0.5F)
            .geolocation(1F, 1F, 1000)
            .filters(mapOf("filter1" to listOf("value1", "value2")))
            .session("session")

        classToTest.reset()

        assertEquals(EXPECTED_DEFAULT_PARAMS, classToTest.createParams().toString())
    }

    @Test
    fun `limit should throw exception when value is not between 1 and 100`() {
        assertFailsWith<IllegalArgumentException>(message = "Limit[1000] should be in range of 1 to 100") {
            classToTest.limit(1000)
        }
    }

    @Test
    fun `threshold should throw exception when value is not between 0-01 and 1-0`() {
        assertFailsWith<IllegalArgumentException>(message = "Threshold[2] should be in range of 0.01 to 1.0") {
            classToTest.threshold(2.0F)
        }
    }

    @Test
    fun `builder parameters should be well created`() {
        classToTest.limit(1)
            .language("language")
            .threshold(0.5F)
            .geolocation(1F, 1F, 1000)
            .filters(mapOf("filter1" to listOf("value1", "value2")))
            .session("session")

        assertEquals(EXPECTED_BUILDER_PARAMS, classToTest.createParams().toString())
    }

    @Test
    fun `match should call repository match with correct params`() = runTest {
        val image = ByteArray(1)
        val paramsSlot = slot<ImageMatchingParams>()
        coEvery {
            imageMatchingRepository.match(image, capture(paramsSlot))
        } returns Result.success(mockk())

        val result = classToTest.limit(1)
            .language("language")
            .threshold(0.5F)
            .geolocation(1F, 1F, 1000)
            .filters(mapOf("filter1" to listOf("value1", "value2")))
            .session("session")
            .match(image)

        assertEquals(EXPECTED_PARAMS, paramsSlot.captured)
        assertTrue(result.isSuccess)
        coVerify { imageMatchingRepository.match(image, paramsSlot.captured) }
    }
}

private const val EXPECTED_DEFAULT_PARAMS = "ImageMatchingParams(" +
    "limit=null, " +
    "language=null, " +
    "threshold=null, " +
    "geolocation=null, " +
    "filters={}, " +
    "session=null" +
    ")"

private const val EXPECTED_BUILDER_PARAMS = "ImageMatchingParams(" +
    "limit=1, " +
    "language=language, " +
    "threshold=0.5, " +
    "geolocation=GeolocationParam(lat=1.0, lon=1.0, dist=1000), " +
    "filters={filter1=[value1, value2]}, " +
    "session=session" +
    ")"

private val EXPECTED_PARAMS = ImageMatchingParams(
    limit = 1,
    language = "language",
    threshold = 0.5F,
    geolocation = GeolocationParam(1F, 1F, 1000),
    filters = mapOf("filter1" to listOf("value1", "value2")),
    session = "session"
)
