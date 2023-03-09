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
package io.nyris.sdk.internal.network.feedback

import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.util.Logger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.withContext

internal interface FeedbackService {
    suspend fun send(feedbackRequest: FeedbackRequest): Result<Unit>
}

internal class FeedbackServiceImpl(
    private val logger: Logger,
    private val endpoints: Endpoints,
    private val httpClient: NyrisHttpClient,
    private val coroutineContext: CoroutineContext,
) : FeedbackService {
    override suspend fun send(
        feedbackRequest: FeedbackRequest,
    ): Result<Unit> = withContext(coroutineContext) {
        logger.log("[FeedbackServiceImpl] send")
        return@withContext try {
            httpClient.post(endpoints.feedback) {
                contentType(ContentType.Application.Json)

                setBody(feedbackRequest)
            }
            Result.success(Unit)
        } catch (e: Throwable) {
            Result.failure<Unit>(e).also {
                logger.log("[RegionsServiceImpl] result is failure")
            }
        }
    }
}
