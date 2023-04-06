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

import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.internal.network.find.FindResponse
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.find.FindServiceParams
import io.nyris.sdk.internal.network.find.LinksDto
import io.nyris.sdk.internal.network.find.OfferDto
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.Links
import io.nyris.sdk.model.MatchResponse
import io.nyris.sdk.model.Offer

internal abstract class CommonImageMatchingRequestBuilderImpl(
    protected val logger: Logger,
    protected val imageMatchingRepository: ImageMatchingRepository,
) : ImageMatchingRequestBuilder {
    private var limit: Int? = null
    private var language: String? = null
    private var threshold: Float? = null
    private var geolocation: GeolocationParam? = null
    private var filters: Map<String, List<String>> = emptyMap()
    private var session: String? = null

    override fun limit(limit: Int) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] limit=$limit")
        require(limit in LIMIT_MIN..LIMIT_MAX) { "Limit[$limit] should be in range of 1 to 100" }
        this.limit = limit
    }

    override fun language(language: String) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] language=$language")
        this.language = language
    }

    override fun threshold(threshold: Float) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] threshold=$threshold")
        require(threshold in THRESHOLD_MIN..THRESHOLD_MAX) { "Threshold[$threshold] should be in range of 0.01 to 1.0" }
        this.threshold = threshold
    }

    override fun geolocation(
        lat: Float,
        lon: Float,
        dist: Int,
    ) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] geolocation[lat=$lat,lon=$lon,dist=$dist]")
        this.geolocation = GeolocationParam(
            lat = lat,
            lon = lon,
            dist = dist
        )
    }

    override fun filters(filters: Map<String, List<String>>) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] filters=$filters")
        this.filters = filters
    }

    override fun session(session: String): ImageMatchingRequestBuilder = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] session=$session")
        this.session = session
    }

    internal fun createParams(): ImageMatchingParams = ImageMatchingParams(
        limit = limit,
        language = language,
        threshold = threshold,
        geolocation = geolocation,
        filters = filters,
        session = session
    ).also {
        logger.log("[ImageMatchingRequestBuilderImpl] createParams[params=$it]")
        reset()
    }

    internal fun reset() {
        logger.log("[ImageMatchingRequestBuilderImpl] reset")
        limit = null
        language = null
        threshold = null
        geolocation = null
        filters = emptyMap()
        session = null
    }
}

private const val LIMIT_MIN = 1
private const val LIMIT_MAX = 100
private const val THRESHOLD_MIN = 0.01F
private const val THRESHOLD_MAX = 1.0F

internal expect class ImageMatchingRequestBuilderImpl(
    logger: Logger,
    imageMatchingRepository: ImageMatchingRepository,
) : CommonImageMatchingRequestBuilderImpl

expect class DataType


internal interface ImageMatchingRepository {
    suspend fun match(
        image: ByteArray,
        params: ImageMatchingParams,
    ): Result<MatchResponse>
}

internal class ImageMatchingRepositoryImpl(
    private val logger: Logger,
    private val findService: FindService,
) : ImageMatchingRepository {
    override suspend fun match(
        image: ByteArray,
        params: ImageMatchingParams,
    ): Result<MatchResponse> {
        logger.log("[ImageMatchingRepositoryImpl] match")
        return findService.find(
            image,
            params.toParams()
        ).map { findResponse ->
            logger.log("[ImageMatchingRepositoryImpl] mapping findResponse to match response")
            findResponse.toMatchResponse()
        }
    }
}

internal class ImageMatchingParams(
    val limit: Int?,
    val language: String?,
    val threshold: Float?,
    val geolocation: GeolocationParam?,
    val filters: Map<String, List<String>>,
    val session: String?,
)

internal class GeolocationParam(
    val lat: Float,
    val lon: Float,
    val dist: Int,
)

internal fun ImageMatchingParams.toParams(): FindServiceParams = FindServiceParams(
    language = language,
    limit = limit,
    threshold = threshold,
    geolocation = geolocation,
    filters = filters,
    session = session
)


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

