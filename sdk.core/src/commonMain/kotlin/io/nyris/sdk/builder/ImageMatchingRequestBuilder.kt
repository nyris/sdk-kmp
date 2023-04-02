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
package io.nyris.sdk.builder

import io.nyris.sdk.model.MatchResponse

interface ImageMatchingRequestBuilder<D> {
    fun limit(limit: Int): ImageMatchingRequestBuilder<D>

    fun language(language: String): ImageMatchingRequestBuilder<D>

    fun threshold(threshold: Float): ImageMatchingRequestBuilder<D>

    fun geolocation(
        lat: Float,
        lon: Float,
        dist: Int,
    ): ImageMatchingRequestBuilder<D>

    fun filters(filters: Map<String, List<String>>): ImageMatchingRequestBuilder<D>

    fun session(session: String): ImageMatchingRequestBuilder<D>

    // Put the params before this call
    suspend fun match(image: D): Result<MatchResponse>
}
