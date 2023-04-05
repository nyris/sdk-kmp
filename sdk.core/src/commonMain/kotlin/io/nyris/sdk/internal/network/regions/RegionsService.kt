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
package io.nyris.sdk.internal.network.regions

import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.internal.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface RegionsService {
    suspend fun detect(
        image: ByteArray,
        params: RegionsServiceParams,
    ): Result<RegionsResponse>
}

internal class RegionsServiceImpl(
    private val logger: Logger,
    private val endpoints: Endpoints,
    private val httpClient: NyrisHttpClient,
    private val coroutineContext: CoroutineContext,
) : RegionsService {

    override suspend fun detect(
        image: ByteArray,
        params: RegionsServiceParams,
    ): Result<RegionsResponse> = withContext(coroutineContext) {
        logger.log("[RegionsServiceImpl] detect")
        return@withContext try {
            with(params) {
                Result.success(
                    httpClient.post(endpoints.regions) {
                        contentType(ContentType.Image.JPEG)
                        header(NyrisHttpHeaders.XSession, session)
                        header(NyrisHttpHeaders.ContentLength, image.size)

                        setBody(image)
                    }.body<RegionsResponse>()
                ).also {
                    logger.log("[RegionsServiceImpl] result is success")
                }
            }
        } catch (e: Throwable) {
            Result.failure<RegionsResponse>(e).also {
                logger.log("[RegionsServiceImpl] result is failure")
            }
        }
    }
}

internal class RegionsServiceParams(
    val session: String?,
)


@Serializable
internal class RegionsResponse(
    @SerialName("regions")
    val regionsDto: List<RegionDto> = emptyList(),
)

@Serializable
internal class RegionDto(
    @SerialName("confidence")
    val confidence: Float = 0.0F,

    @SerialName("region")
    val positionDto: PositionDto? = null,
)

@Serializable
internal class PositionDto(
    @SerialName("left")
    val left: Float = 0F,

    @SerialName("top")
    val top: Float = 0F,

    @SerialName("right")
    val right: Float = 0F,

    @SerialName("bottom")
    val bottom: Float = 0F,
)

