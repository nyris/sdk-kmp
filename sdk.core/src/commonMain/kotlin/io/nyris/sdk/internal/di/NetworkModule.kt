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
package io.nyris.sdk.internal.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import io.nyris.sdk.NyrisPlatform
import io.nyris.sdk.internal.ConfigInternal
import io.nyris.sdk.internal.network.CommonHeaders
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.HttpClientWrapper
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.UserAgent
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.util.Logger
import kotlinx.serialization.json.Json
import io.ktor.client.plugins.logging.Logger as KLogger

internal object NetworkModule {
    fun init(
        platform: NyrisPlatform,
        baseUrl: String,
    ) {
        putUserAgent(platform)
        putCommonHeaders()
        putXOptionsBuilder()
        putEndpoints(baseUrl)
        puJson()
        putHttpClient()
    }

    private fun putUserAgent(platform: NyrisPlatform) {
        ServiceLocator.put(UserAgent::class) {
            UserAgent(
                sdkVersion = "1.0.0",
                platform = platform,
                osVersion = "12.0"
            )
        }
    }

    private fun putCommonHeaders() {
        ServiceLocator.put(CommonHeaders::class) {
            CommonHeaders(
                ServiceLocator.get<ConfigInternal>().value.apiKey,
                ServiceLocator.get<UserAgent>().value
            )
        }
    }

    private fun putXOptionsBuilder() {
        ServiceLocator.put(XOptionsBuilder::class) {
            XOptionsBuilder()
        }
    }

    private fun putEndpoints(baseUrl: String) {
        ServiceLocator.put(Endpoints::class) {
            Endpoints(baseUrl)
        }
    }

    private fun puJson() {
        ServiceLocator.put(Json::class) {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
    }

    private fun putHttpClient() {
        val config = ServiceLocator.get<ConfigInternal>().value
        val engine = config.httpEngine

        val httpConfig: HttpClientConfig<*>.() -> Unit = {
            install(ContentNegotiation) {
                json(ServiceLocator.get<Json>().value)
            }
            if (config.isDebug) {
                install(Logging) {
                    logger = KLogger.SIMPLE
                    level = LogLevel.ALL
                }
            }
        }

        val httpClient = if (engine != null) {
            HttpClient(engine, httpConfig)
        } else {
            HttpClient(httpConfig)
        }

        ServiceLocator.put(NyrisHttpClient::class) {
            NyrisHttpClient(
                ServiceLocator.get<Logger>().value,
                ServiceLocator.get<CommonHeaders>().value,
                HttpClientWrapper(httpClient)
            )
        }
    }
}
