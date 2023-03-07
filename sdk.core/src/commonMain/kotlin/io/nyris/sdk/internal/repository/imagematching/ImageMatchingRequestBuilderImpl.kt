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
import io.nyris.sdk.model.MatchResponse
import io.nyris.sdk.util.Logger

internal class ImageMatchingRequestBuilderImpl(
    private val logger: Logger,
    private val imageMatchingRepository: ImageMatchingRepository,
) : ImageMatchingRequestBuilder {
    private var limit: Int? = null
    private var language: String? = null
    private var threshold: Float? = null
    private var geolocation: GeolocationParam? = null
    private var filters: Map<String, List<String>> = emptyMap()
    override fun limit(limit: Int) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] limit=$limit")
        this.limit = limit
    }

    override fun language(language: String) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] language=$language")
        this.language = language
    }

    override fun threshold(threshold: Float) = apply {
        logger.log("[ImageMatchingRequestBuilderImpl] threshold=$threshold")
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

    override suspend fun match(image: ByteArray): Result<MatchResponse> {
        logger.log("[ImageMatchingRequestBuilderImpl] match")

        return imageMatchingRepository.match(
            image = image,
            params = createParams()
        )
    }

    internal fun createParams(): ImageMatchingParams = ImageMatchingParams(
        limit = limit,
        language = language,
        threshold = threshold,
        geolocation = geolocation,
        filters = filters
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
    }
}

internal data class ImageMatchingParams(
    val limit: Int?,
    val language: String?,
    val threshold: Float?,
    val geolocation: GeolocationParam?,
    val filters: Map<String, List<String>>,
)

internal data class GeolocationParam(
    val lat: Float,
    val lon: Float,
    val dist: Int,
)
