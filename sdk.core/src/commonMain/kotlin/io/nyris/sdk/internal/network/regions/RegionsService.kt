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
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.NyrisHttpHeaders
import io.nyris.sdk.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

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
                        header(NyrisHttpHeaders.XSession, session)
                        header(NyrisHttpHeaders.ContentType, "image/jpg")
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

internal data class RegionsServiceParams(
    val session: String?,
)
