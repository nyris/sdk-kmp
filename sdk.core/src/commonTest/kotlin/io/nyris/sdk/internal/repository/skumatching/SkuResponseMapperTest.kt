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

import io.nyris.sdk.internal.network.recommend.RecommendResponse
import io.nyris.sdk.internal.network.recommend.SkuOfferDto
import io.nyris.sdk.model.SkuOffer
import io.nyris.sdk.model.SkuResponse
import kotlin.test.Test
import kotlin.test.assertEquals

class SkuResponseMapperTest {
    @Test
    fun `toSkuOffer should map dto to SkuOffer`() {
        val dto = SkuOfferDto(sku = SKU, score = SCORE)

        val skuOffer = dto.toSkuOffer()

        assertEquals(EXPECTED_SKU_OFFER, skuOffer)
    }

    @Test
    fun `toSkuOfferList should map dto list to SkuOffer List`() {
        val dtoList = listOf(SkuOfferDto(sku = SKU, score = SCORE))

        val skuOfferList = dtoList.toSkuOfferList()

        assertEquals(EXPECTED_SKU_OFFER_LIST, skuOfferList)
    }

    @Test
    fun `toSkuResponse should map response list to sku response`() {
        val recommendResponse = RecommendResponse(
            requestId = ANY_ID,
            sessionId = ANY_ID,
            result = listOf(SkuOfferDto(sku = SKU, score = SCORE))
        )

        val skuResponse = recommendResponse.toSkuResponse()

        assertEquals(EXPECTED_SKU_RESPONSE, skuResponse)
    }
}

private const val SKU = "SKU"
private const val SCORE = 1.0F
private const val ANY_ID = "ANY_ID"
private val EXPECTED_SKU_OFFER = SkuOffer(sku = SKU, score = SCORE)
private val EXPECTED_SKU_OFFER_LIST = listOf(EXPECTED_SKU_OFFER)
private val EXPECTED_SKU_RESPONSE = SkuResponse(
    requestId = ANY_ID,
    sessionId = ANY_ID,
    offers = EXPECTED_SKU_OFFER_LIST
)
