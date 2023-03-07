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

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.utils.io.core.buildPacket
import io.ktor.utils.io.core.writeFully
import io.nyris.sdk.internal.network.find.FindResponseError
import io.nyris.sdk.internal.network.find.FindServiceParams
import io.nyris.sdk.util.Logger

internal class NyrisHttpClient(
    private val logger: Logger,
    private val httpClient: HttpClientWrapper,
) {
    suspend fun post(
        endpoint: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse = try {
        logger.log("[NyrisHttpClient] post $endpoint")
        val response = httpClient.post(endpoint, block)
        if (response.status == HttpStatusCode.OK) {
            logger.log("[NyrisHttpClient] post status ok")
            response
        } else {
            logger.log("[NyrisHttpClient] post status ${response.status}")
            throw response.body<FindResponseError>().toNyrisException()
        }
    } catch (ignore: ClientRequestException) {
        logger.log("[NyrisHttpClient] ClientRequestException is thrown")
        throw ignore.toNyrisException()
    } catch (ignore: ServerResponseException) {
        logger.log("[NyrisHttpClient] ServerResponseException is thrown")
        throw ignore.toNyrisException()
    } catch (e: Throwable) {
        logger.log("[NyrisHttpClient] General Exception is thrown")
        throw e
    }
}

internal class HttpClientWrapper(private val httpClient: HttpClient) {
    suspend fun post(
        urlString: String,
        block: HttpRequestBuilder.() -> Unit = {},
    ): HttpResponse = httpClient.post(urlString, block)
}

internal fun HttpRequestBuilder.appendHeaders(
    apiHeaders: ApiHeaders,
    params: FindServiceParams,
) {
    apiHeaders.default.forEach { entry -> header(entry.key, entry.value) }
    header(HttpHeaders.AcceptLanguage, params.language)
}

internal fun buildMultiParamForm(
    image: ByteArray,
    params: FindServiceParams,
): MultiPartFormDataContent = MultiPartFormDataContent(
    formData {
        appendImage(image)
        appendFilters(params.filters)
    }
)

fun FormBuilder.appendImage(image: ByteArray) {
    appendInput(
        key = "image",
        headers = Headers.build {
            this.append(HttpHeaders.ContentDisposition, "filename=image.jpg")
            this.append(HttpHeaders.ContentType, "image/jpg")
            this.append(HttpHeaders.ContentLength, image.size.toString())
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
