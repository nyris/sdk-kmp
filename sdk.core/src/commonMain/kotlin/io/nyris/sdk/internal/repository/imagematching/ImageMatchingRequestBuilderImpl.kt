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
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.MatchResponse

internal class ImageMatchingRequestBuilderImpl(
    private val logger: Logger,
    private val imageMatchingRepository: ImageMatchingRepository,
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
