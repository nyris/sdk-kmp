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
import io.ktor.http.Headers
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.internal.repository.imagematching.GeolocationParam
import io.nyris.sdk.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

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

internal data class FindServiceParams(
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
            this.append(NyrisHttpHeaders.ContentType, "image/jpg")
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
