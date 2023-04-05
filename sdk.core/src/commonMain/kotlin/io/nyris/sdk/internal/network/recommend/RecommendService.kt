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
package io.nyris.sdk.internal.network.recommend

import io.ktor.client.call.body
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

internal interface RecommendService {
    suspend fun match(sku: String): Result<RecommendResponse>
}

internal class RecommendServiceImpl(
    private val logger: Logger,
    private val endpoints: Endpoints,
    private val httpClient: NyrisHttpClient,
    private val coroutineContext: CoroutineContext,
) : RecommendService {
    override suspend fun match(sku: String): Result<RecommendResponse> = withContext(coroutineContext) {
        logger.log("[RecommendServiceImpl] match")
        try {
            Result.success(
                httpClient.get(endpoints.recommend(sku)).body<RecommendResponse>()
            ).also {
                logger.log("[RecommendServiceImpl] result is success")
            }
        } catch (e: Throwable) {
            Result.failure<RecommendResponse>(e).also {
                logger.log("[RecommendServiceImpl] result is failure")
            }
        }
    }
}

@Serializable
internal class RecommendResponse(
    @SerialName("id")
    val requestId: String? = null,
    @SerialName("session")
    val sessionId: String? = null,
    @SerialName("results")
    val result: List<SkuOfferDto> = emptyList(),
)

@Serializable
internal class SkuOfferDto(
    @SerialName("sku")
    val sku: String? = null,
    @SerialName("score")
    val score: Float? = null,
)
