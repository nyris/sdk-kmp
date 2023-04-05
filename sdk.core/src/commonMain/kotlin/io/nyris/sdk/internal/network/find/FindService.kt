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

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.internal.repository.imagematching.GeolocationParam
import io.nyris.sdk.internal.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface FindService {
    suspend fun find(
        image: ByteArray,
        params: FindServiceParams,
    ): Result<FindResponse>
}

internal class FindServiceImpl(
    private val logger: Logger,
    private val xOptionsBuilder: XOptionsBuilder,
    private val endpoints: Endpoints,
    private val httpClient: NyrisHttpClient,
    private val coroutineContext: CoroutineContext,
) : FindService {
    override suspend fun find(
        image: ByteArray,
        params: FindServiceParams,
    ): Result<FindResponse> = withContext(coroutineContext) {
        logger.log("[FindServiceImpl] find")
        logger.log("[FindServiceImpl] params[$params]")

        return@withContext try {
            with(params) {
                logger.log("[FindServiceImpl] find post ${endpoints.find(geolocation)}")
                Result.success(
                    httpClient.post(
                        endpoints.find(geolocation)
                    ) {
                        header(NyrisHttpHeaders.AcceptLanguage, language)
                        header(NyrisHttpHeaders.XSession, session)
                        header(NyrisHttpHeaders.XOptions, xOptionsBuilder.limit(limit).threshold(threshold).build())

                        setBody(buildMultiParamForm(image, params))
                    }.body<FindResponse>()
                ).also {
                    logger.log("[FindServiceImpl] result is success")
                }
            }
        } catch (e: Throwable) {
            Result.failure<FindResponse>(e).also {
                logger.log("[FindServiceImpl] result is failure")
            }
        }
    }
}

internal class FindServiceParams(
    val language: String?,
    val limit: Int?,
    val threshold: Float?,
    val geolocation: GeolocationParam?,
    val filters: Map<String, List<String>>,
    val session: String?,
)

internal fun buildMultiParamForm(
    image: ByteArray,
    params: FindServiceParams,
): MultiPartFormDataContent = MultiPartFormDataContent(
    formData {
        appendImage(image)
        appendFilters(params.filters)
    }
)

internal fun FormBuilder.appendImage(image: ByteArray) {
    appendInput(
        key = "image",
        headers = Headers.build {
            this.append(NyrisHttpHeaders.ContentDisposition, "filename=image.jpg")
            this.append(NyrisHttpHeaders.ContentType, ContentType.Image.JPEG.toString())
            this.append(NyrisHttpHeaders.ContentLength, image.size.toString())
        },
        size = image.size.toLong()
    ) {
        buildPacket { writeFully(image) }
    }
}

internal fun FormBuilder.appendFilters(map: Map<String, List<String>>) {
    map.keys.forEachIndexed { i, filterType ->
        append("filters[$i].filterType", filterType)
        map[filterType]?.forEachIndexed { j, filterTypeValue ->
            append("filters[$i].filterValues[$j]", filterTypeValue)
        }
    }
}


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