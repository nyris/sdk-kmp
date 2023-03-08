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
import io.ktor.client.request.post
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.nyris.sdk.internal.network.find.FindResponseError
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

object NyrisHttpHeaders {
    val UserAgent: String = HttpHeaders.UserAgent
    val AcceptLanguage: String = HttpHeaders.AcceptLanguage
    val ContentLength: String = HttpHeaders.ContentLength
    val ContentDisposition: String = HttpHeaders.ContentDisposition
    val ContentType: String = HttpHeaders.ContentType

    const val XApiKey: String = "X-Api-Key"
    const val XSession: String = "X-Session"
    const val XOptions: String = "X-Options"
}
