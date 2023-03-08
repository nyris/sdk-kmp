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
package io.nyris.sdk.internal

import io.mockk.mockk
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import kotlin.test.Test
import kotlin.test.assertEquals

class RequestBuildersImplTest {
    private val imageMatching = mockk<ImageMatchingRequestBuilder>(relaxed = true)
    private val objectDetecting = mockk<ObjectDetectingRequestBuilder>(relaxed = true)

    private val classToTest: RequestBuildersImpl by lazy { RequestBuildersImpl(imageMatching, objectDetecting) }

    @Test
    fun `imageMatching should provide the mock imageMatching`() {
        assertEquals(classToTest.imageMatching(), imageMatching)
    }

    @Test
    fun `objectDetecting should provide the mock objectDetecting`() {
        assertEquals(classToTest.objectDetecting(), objectDetecting)
    }
}
