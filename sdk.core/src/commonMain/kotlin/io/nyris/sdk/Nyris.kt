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
package io.nyris.sdk

import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder

expect interface Nyris {
    fun imageMatching(): ImageMatchingRequestBuilder

    fun objectDetecting(): ObjectDetectingRequestBuilder

    fun feedback(): FeedbackRequestBuilder

    fun skuMatching(): SkuMatchingRequestBuilder
}

expect fun createInstance(
    apiKey: String,
    config: NyrisConfig = NyrisConfig(),
): Nyris

class NyrisConfig(
    val isDebug: Boolean = false,
    val baseUrl: String = BASE_URL,
    val timeout: Long = TIMEOUT,
    internal val platform: NyrisPlatform = NyrisPlatform.Generic,
)

const val BASE_URL = "https://api.nyris.io/"
const val TIMEOUT = 3000L

sealed class NyrisException(message: String?) : Throwable(message)

class ResponseException(
    val title: String?,
    val status: Int?,
    val detail: String?,
    val traceId: String?,
    val itemKey: String?,
) : NyrisException(detail)

class ClientException(
    override val message: String?,
) : NyrisException(message)

class ServerException(
    override val message: String?,
) : NyrisException(message)

enum class NyrisPlatform { Generic, Android, IOS, Jvm }