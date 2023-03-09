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
import io.mockk.mockk
import io.mockk.slot
import io.nyris.sdk.internal.util.Logger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ObjectDetectingRequestBuilderImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val objectDetectingRepository = mockk<ObjectDetectingRepository>(relaxed = true)

    private val classToTest: ObjectDetectingRequestBuilderImpl by lazy {
        ObjectDetectingRequestBuilderImpl(logger, objectDetectingRepository)
    }

    @Test
    fun `createParams should create the expected default parameters`() {
        val params = classToTest.createParams()

        assertEquals(EXPECTED_DEFAULT_PARAMS, params.toString())
    }

    @Test
    fun `reset should reset builder properties`() {
        classToTest.session("session")

        classToTest.reset()

        assertEquals(EXPECTED_DEFAULT_PARAMS, classToTest.createParams().toString())
    }

    @Test
    fun `builder parameters should be well created`() {
        classToTest.session("session")

        assertEquals(EXPECTED_BUILDER_PARAMS, classToTest.createParams().toString())
    }

    @Test
    fun `detect should call repository detect with correct params`() = runTest {
        val image = ByteArray(1)
        val paramsSlot = slot<ObjectDetectingParams>()
        coEvery {
            objectDetectingRepository.detect(image, capture(paramsSlot))
        } returns Result.success(mockk())

        val result = classToTest.session("session").detect(image)

        assertEquals(EXPECTED_PARAMS, paramsSlot.captured)
        assertTrue(result.isSuccess)
        coVerify { objectDetectingRepository.detect(image, paramsSlot.captured) }
    }
}

private const val EXPECTED_DEFAULT_PARAMS = "ObjectDetectingParams(session=null)"
private const val EXPECTED_BUILDER_PARAMS = "ObjectDetectingParams(session=session)"
private val EXPECTED_PARAMS = ObjectDetectingParams(
    session = "session"
)
