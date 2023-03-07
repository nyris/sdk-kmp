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
package io.nyris.sdk

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.NyrisImpl
import kotlin.test.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test

class NyrisTest {
    @Before
    fun setup() {
        mockkObject(NyrisImpl)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `createInstance should create a proper instance with android as platform`() {
        val configSlot = slot<NyrisConfig>()
        every { NyrisImpl.createInstance(any(), capture(configSlot)) } returns mockk()

        Nyris.createInstance(API_KEY)

        assertEquals(NyrisPlatform.Android, configSlot.captured.platform)
        verify { NyrisImpl.createInstance(any(), configSlot.captured) }
    }
}

private const val API_KEY = "API_KEY"
