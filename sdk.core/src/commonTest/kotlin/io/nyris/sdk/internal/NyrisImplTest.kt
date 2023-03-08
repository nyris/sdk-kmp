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

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.internal.di.ServiceLocator
import kotlin.test.BeforeTest
import kotlin.test.Test

class NyrisImplTest {
    private val config = mockk<NyrisConfig>(relaxed = true)
    private val requestBuilders = mockk<RequestBuilders>(relaxed = true)

    private val classToTest: NyrisImpl by lazy {
        NyrisImpl.createInstance(API_KEY, config)
    }

    @BeforeTest
    fun setup() {
        mockkObject(ServiceLocator)
        justRun { ServiceLocator.init(API_KEY, config) }
        every { ServiceLocator.objectMap[RequestBuilders::class] } returns lazy { requestBuilders }
    }

    @Test
    fun `imageMatching should call the imageMatching`() {
        classToTest.imageMatching()

        verify { requestBuilders.imageMatching() }
        confirmVerified(requestBuilders, config)
    }

    @Test
    fun `objectDetecting should call the objectDetecting`() {
        classToTest.objectDetecting()

        verify { requestBuilders.objectDetecting() }
        confirmVerified(requestBuilders, config)
    }
}

private const val API_KEY = "API_KEY"
