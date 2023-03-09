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

import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.find.FindServiceParams
import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.MatchResponse

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

internal data class ImageMatchingParams(
    val limit: Int?,
    val language: String?,
    val threshold: Float?,
    val geolocation: GeolocationParam?,
    val filters: Map<String, List<String>>,
    val session: String?,
)

internal data class GeolocationParam(
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
