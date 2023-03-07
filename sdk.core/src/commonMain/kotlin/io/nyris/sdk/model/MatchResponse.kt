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
package io.nyris.sdk.model

data class MatchResponse(
    val requestId: String? = null,

    val sessionId: String? = null,

    val offers: List<Offer> = emptyList(),
)

data class Offer(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val descriptionLong: String? = null,
    val language: String? = null,
    val brand: String? = null,
    val catalogNumbers: List<String> = emptyList(),
    val customIds: Map<String, String> = emptyMap(),
    val keywords: List<String>? = null,
    val categories: List<String> = emptyList(),
    val availability: String? = null,
    val feedId: String? = null,
    val groupId: String? = null,
    val priceStr: String? = null,
    val salePrice: String? = null,
    val links: Links? = null,
    val images: List<String> = emptyList(),
    val metadata: String? = null,
    val sku: String? = null,
    val score: Float? = null,
)

data class Links(
    val main: String? = null,
    val mobile: String? = null,
)
