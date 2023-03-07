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

import io.nyris.sdk.internal.network.find.FindResponse
import io.nyris.sdk.internal.network.find.LinksDto
import io.nyris.sdk.internal.network.find.OfferDto
import kotlin.test.Test
import kotlin.test.assertEquals

class MatchResponseMapperTest {
    @Test
    fun `toMatchResponse should map findResponse to MatchResponse`() {
        val matchResponse = FIND_RESPONSE.toMatchResponse()
        assertEquals(EXPECTED_MATCH_RESPONSE, matchResponse.toString())
    }
}

private val FIND_RESPONSE = FindResponse(
    requestId = "requestId",
    sessionId = "requestId",
    offers = listOf(
        OfferDto(
            id = "id",
            title = "title",
            description = "description",
            descriptionLong = "descriptionLong",
            language = "language",
            brand = "brand",
            catalogNumbers = emptyList(),
            customIds = emptyMap(),
            keywords = emptyList(),
            categories = emptyList(),
            availability = "availability",
            feedId = "feedId",
            groupId = "groupId",
            priceStr = "priceStr",
            salePrice = "salePrice",
            links = LinksDto(
                mobile = "mobile",
                main = "main"
            ),
            images = emptyList(),
            metadata = "metadata",
            sku = "sku",
            score = 0.1F
        )
    )
)

private const val EXPECTED_MATCH_RESPONSE = "MatchResponse(" +
    "requestId=requestId, " +
    "sessionId=requestId, " +
    "offers=[" +
    "Offer(" +
    "id=id, " +
    "title=title, " +
    "description=description, " +
    "descriptionLong=descriptionLong, " +
    "language=language, brand=brand, " +
    "catalogNumbers=[], " +
    "customIds={}, " +
    "keywords=[], " +
    "categories=[], " +
    "availability=availability, " +
    "feedId=feedId, " +
    "groupId=groupId, " +
    "priceStr=priceStr, " +
    "salePrice=salePrice, " +
    "links=Links(main=main, mobile=mobile), " +
    "images=[], " +
    "metadata=metadata, " +
    "sku=sku, " +
    "score=0.1" +
    ")])"
