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
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import io.nyris.sdk.internal.network.recommend.RecommendResponse
import io.nyris.sdk.internal.network.recommend.RecommendService
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.SkuResponse
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SkuMatchingRepositoryImplTest {
    private val logger = mockk<Logger>(relaxed = true)
    private val recommendService = mockk<RecommendService>()

    private val classToTest: SkuMatchingRepositoryImpl by lazy {
        SkuMatchingRepositoryImpl(logger, recommendService)
    }

    @BeforeTest
    fun setup() {
        mockkStatic("io.nyris.sdk.internal.repository.skumatching.SkuResponseMapperKt")
    }

    @AfterTest
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `match should cal match recommend service and map result`() = runTest {
        val recommendResponse = mockk<RecommendResponse>()
        val skuResponse = mockk<SkuResponse>()
        val result = Result.success(recommendResponse)
        coEvery { recommendService.match(SKU) } returns result
        every { recommendResponse.toSkuResponse() } returns skuResponse

        val response = classToTest.match(SKU)

        assertEquals(skuResponse, response.getOrNull())
        coVerify { recommendService.match(SKU) }
        verify { recommendResponse.toSkuResponse() }
        confirmVerified(recommendService)
    }
}

private const val SKU = "SKU"
