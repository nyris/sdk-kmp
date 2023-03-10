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
package io.nyris.sdk.internal.repository.skumatching

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.nyris.sdk.internal.util.Logger
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SkuMatchingRequestBuilderImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val skuMatchingRepository = mockk<SkuMatchingRepository>(relaxed = true)

    private val classToTest: SkuMatchingRequestBuilderImpl by lazy {
        SkuMatchingRequestBuilderImpl(logger, skuMatchingRepository)
    }

    @Test
    fun `match should call match feedback repository`() = runTest {
        val sku = "sku"
        coEvery { skuMatchingRepository.match(sku) } returns Result.success(mockk())

        val result = classToTest.match(sku)

        assertTrue(result.isSuccess)
        coVerify { skuMatchingRepository.match(sku) }
    }
}
