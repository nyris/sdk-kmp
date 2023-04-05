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
package io.nyris.sdk.internal.network

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.nyris.sdk.ClientException
import io.nyris.sdk.ResponseException
import io.nyris.sdk.ServerException
import io.nyris.sdk.internal.repository.imagematching.GeolocationParam
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ApiError(
    @SerialName("title")
    val title: String? = null,

    @SerialName("status")
    val status: Int? = null,

    @SerialName("detail")
    val detail: String? = null,

    @SerialName("traceId")
    val traceId: String? = null,

    @SerialName("itemKey")
    val itemKey: String? = null,
)

internal class CommonHeaders(
    apiKey: String,
    userAgent: UserAgent,
) {
    val default: Map<String, String> = mapOf(
        NyrisHttpHeaders.XApiKey to apiKey,
        NyrisHttpHeaders.UserAgent to userAgent.toString(),
    )
}

internal class Endpoints(baseUrl: String) {
    private val find: String = "${baseUrl}find/v1.1"

    private val recommend: String = "${baseUrl}recommend/v1"

    val regions: String = "${baseUrl}find/v2/regions"

    val feedback: String = "${baseUrl}feedback/v1"

    fun find(geolocation: GeolocationParam? = null): String = geolocation?.let {
        with(it) { "$find?lat=$lat?lon=$lon?dist=$dist" }
    } ?: find

    fun recommend(sku: String) = "$recommend/$sku"
}

internal fun ApiError.toNyrisException(): ResponseException = ResponseException(
    title = title,
    status = status,
    detail = detail,
    traceId = traceId,
    itemKey = itemKey
)

internal fun ClientRequestException.toNyrisException(): ClientException = ClientException(message)

internal fun ServerResponseException.toNyrisException(): ServerException = ServerException(message)