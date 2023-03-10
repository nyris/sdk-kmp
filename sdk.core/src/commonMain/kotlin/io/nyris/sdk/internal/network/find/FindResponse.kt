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
package io.nyris.sdk.internal.network.find

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal class FindResponse(
    @SerialName("id")
    val requestId: String? = null,

    @SerialName("session")
    val sessionId: String? = null,

    @SerialName("results")
    val offers: List<OfferDto> = emptyList(),
)

@Serializable
@Suppress("LongParameterList")
internal class OfferDto(
    @SerialName("oid")
    val id: String? = null,

    @SerialName("title")
    val title: String? = null,

    @SerialName("descriptionShort")
    val description: String? = null,

    @SerialName("descriptionLong")
    val descriptionLong: String? = null,

    @SerialName("language")
    val language: String? = null,

    @SerialName("brand")
    val brand: String? = null,

    @SerialName("catalogNumbers")
    val catalogNumbers: List<String> = emptyList(),

    @SerialName("customIds")
    val customIds: Map<String, String> = emptyMap(),

    @SerialName("keywords")
    val keywords: List<String> = emptyList(),

    @SerialName("categories")
    val categories: List<String> = emptyList(),

    @SerialName("availability")
    val availability: String? = null,

    @SerialName("feedId")
    val feedId: String? = null,

    @SerialName("groupId")
    val groupId: String? = null,

    @SerialName("price")
    val priceStr: String? = null,

    @SerialName("salePrice")
    val salePrice: String? = null,

    @SerialName("links")
    val links: LinksDto? = null,

    @SerialName("images")
    val images: List<String> = emptyList(),

    @SerialName("metadata")
    val metadata: String? = null,

    @SerialName("sku")
    val sku: String? = null,

    @SerialName("score")
    val score: Float? = null,
)

@Serializable
internal class LinksDto(
    @SerialName("main")
    val main: String? = null,

    @SerialName("mobile")
    val mobile: String? = null,
)
