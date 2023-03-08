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

import io.mockk.spyk
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class XOptionsBuilderTest {
    private val classToTest = spyk(XOptionsBuilder())

    @Test
    fun `build without passing parameters should build null object`() {
        val xOptions = classToTest.build()

        assertNull(xOptions)
    }

    @Test
    fun `build with passing parameters should build the correct xoptions`() {
        val xOptions = classToTest
            .limit(10)
            .threshold(1.0F)
            .build()

        assertEquals(EXPECTED_XOPTIONS, xOptions)
    }
}

private const val EXPECTED_XOPTIONS = "limit=10 threshold=1.0 "
