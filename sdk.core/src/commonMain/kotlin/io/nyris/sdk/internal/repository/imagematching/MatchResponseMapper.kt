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
import io.nyris.sdk.model.Links
import io.nyris.sdk.model.MatchResponse
import io.nyris.sdk.model.Offer

internal fun FindResponse.toMatchResponse(): MatchResponse = with(this) {
    MatchResponse(
        requestId = requestId,
        sessionId = sessionId,
        offers = offers.toOfferList()
    )
}

internal fun List<OfferDto>.toOfferList(): List<Offer> = map { dto ->
    dto.toOffer()
}

internal fun OfferDto.toOffer(): Offer = with(this) {
    Offer(
        id = id,
        title = title,
        description = description,
        descriptionLong = descriptionLong,
        language = language,
        brand = brand,
        catalogNumbers = catalogNumbers,
        customIds = customIds,
        keywords = keywords,
        categories = categories,
        availability = availability,
        feedId = feedId,
        groupId = groupId,
        priceStr = priceStr,
        salePrice = salePrice,
        links = links.toLinks(),
        images = images,
        metadata = metadata,
        sku = sku,
        score = score,
    )
}

internal fun LinksDto?.toLinks(): Links = Links(
    main = this?.main,
    mobile = this?.mobile
)
