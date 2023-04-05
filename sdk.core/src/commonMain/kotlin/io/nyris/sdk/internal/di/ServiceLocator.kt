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
@file:Suppress("UNCHECKED_CAST")

package io.nyris.sdk.internal.di

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import io.nyris.sdk.NyrisConfig
import io.nyris.sdk.NyrisPlatform
import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder
import io.nyris.sdk.internal.ConfigInternal
import io.nyris.sdk.internal.RequestBuilders
import io.nyris.sdk.internal.RequestBuildersImpl
import io.nyris.sdk.internal.network.CommonHeaders
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.HttpClientWrapper
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.UserAgent
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.internal.network.feedback.FeedbackService
import io.nyris.sdk.internal.network.feedback.FeedbackServiceImpl
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.find.FindServiceImpl
import io.nyris.sdk.internal.network.recommend.RecommendService
import io.nyris.sdk.internal.network.recommend.RecommendServiceImpl
import io.nyris.sdk.internal.network.regions.RegionsService
import io.nyris.sdk.internal.network.regions.RegionsServiceImpl
import io.nyris.sdk.internal.repository.feedback.FeedbackRepository
import io.nyris.sdk.internal.repository.feedback.FeedbackRepositoryImpl
import io.nyris.sdk.internal.repository.feedback.FeedbackRequestBuilderImpl
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRepository
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRepositoryImpl
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRequestBuilderImpl
import io.nyris.sdk.internal.repository.objectdetecting.ObjectDetectingRepository
import io.nyris.sdk.internal.repository.objectdetecting.ObjectDetectingRepositoryImpl
import io.nyris.sdk.internal.repository.objectdetecting.ObjectDetectingRequestBuilderImpl
import io.nyris.sdk.internal.repository.skumatching.SkuMatchingRepository
import io.nyris.sdk.internal.repository.skumatching.SkuMatchingRepositoryImpl
import io.nyris.sdk.internal.repository.skumatching.SkuMatchingRequestBuilderImpl
import io.nyris.sdk.internal.util.DEFAULT
import io.nyris.sdk.internal.util.Logger
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json

internal object ServiceLocator {
    internal val objectMap: MutableMap<KClass<*>, Lazy<*>> = mutableMapOf()

    inline fun <reified T> get(): Lazy<T> =
        objectMap[T::class] as? Lazy<T>
            ?: throw IllegalArgumentException("Unable to get dependency[${T::class.simpleName}]")

    fun <T : Any> put(
        kClass: KClass<T>,
        initializer: () -> T,
    ): ServiceLocator {
        objectMap[kClass] = lazy { initializer.invoke() }
        return this
    }

    // Please do not change the order!!
    fun init(
        apiKey: String,
        config: NyrisConfig,
    ) {
        // Init your private methods here
        putLogger(
            if (config.isDebug) {
                Logger.DEFAULT
            } else {
                Logger.EMPTY
            }
        )

        putConfig(
            ConfigInternal(
                apiKey = apiKey,
                isDebug = config.isDebug,
                timeout = config.timeout
            )
        )

        // Init your modules here
        initNetworkModule(config.platform, config.baseUrl)

        initServiceModule()
        initRepositories()
        initRequestModule()
    }

    private fun putLogger(logger: Logger) {
        put(Logger::class) { logger }
    }

    private fun putConfig(config: ConfigInternal) {
        put(ConfigInternal::class) { config }
    }

    //region Network Module
    private fun initNetworkModule(
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
        put(UserAgent::class) {
            UserAgent(
                sdkVersion = "1.0.0",
                platform = platform,
                osVersion = "12.0"
            )
        }
    }

    private fun putCommonHeaders() {
        put(CommonHeaders::class) {
            CommonHeaders(
                get<ConfigInternal>().value.apiKey,
                get<UserAgent>().value
            )
        }
    }

    private fun putXOptionsBuilder() {
        put(XOptionsBuilder::class) {
            XOptionsBuilder()
        }
    }

    private fun putEndpoints(baseUrl: String) {
        put(Endpoints::class) {
            Endpoints(baseUrl)
        }
    }

    private fun puJson() {
        put(Json::class) {
            Json {
                isLenient = true
                ignoreUnknownKeys = true
            }
        }
    }

    private fun putHttpClient() {
        val config = get<ConfigInternal>().value

        val httpConfig: HttpClientConfig<*>.() -> Unit = {
            install(ContentNegotiation) {
                json(get<Json>().value)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = config.timeout
            }
            if (config.isDebug) {
                install(Logging) {
                    logger = io.ktor.client.plugins.logging.Logger.SIMPLE
                    level = LogLevel.ALL
                }
            }
        }

        val httpClient = HttpClient(httpConfig)

        put(NyrisHttpClient::class) {
            NyrisHttpClient(
                get<Logger>().value,
                get<CommonHeaders>().value,
                HttpClientWrapper(httpClient)
            )
        }
    }
    //endregion

    //region Service Module
    private fun initServiceModule() {
        putFindService()
        putRegionsService()
        putFeedbackService()
        putRecommendService()
    }

    private fun putFindService() {
        put(FindService::class) {
            FindServiceImpl(
                logger = get<Logger>().value,
                xOptionsBuilder = get<XOptionsBuilder>().value,
                httpClient = get<NyrisHttpClient>().value,
                endpoints = get<Endpoints>().value,
                coroutineContext = Dispatchers.Default
            )
        }
    }

    private fun putRegionsService() {
        put(RegionsService::class) {
            RegionsServiceImpl(
                logger = get<Logger>().value,
                httpClient = get<NyrisHttpClient>().value,
                endpoints = get<Endpoints>().value,
                coroutineContext = Dispatchers.Default
            )
        }
    }

    private fun putFeedbackService() {
        put(FeedbackService::class) {
            FeedbackServiceImpl(
                logger = get<Logger>().value,
                httpClient = get<NyrisHttpClient>().value,
                endpoints = get<Endpoints>().value,
                coroutineContext = Dispatchers.Default
            )
        }
    }

    private fun putRecommendService() {
        put(RecommendService::class) {
            RecommendServiceImpl(
                logger = get<Logger>().value,
                httpClient = get<NyrisHttpClient>().value,
                endpoints = get<Endpoints>().value,
                coroutineContext = Dispatchers.Default
            )
        }
    }
    //endregion

    //region Repositories module
    private fun initRepositories() {
        putImageMatchingRepository()
        putObjectDetectingRepository()
        putFeedbackRepository()
        putSkuMatchingRepository()
    }

    private fun putImageMatchingRepository() {
        put(ImageMatchingRepository::class) {
            ImageMatchingRepositoryImpl(
                logger = get<Logger>().value,
                findService = get<FindService>().value,
            )
        }
    }

    private fun putObjectDetectingRepository() {
        put(ObjectDetectingRepository::class) {
            ObjectDetectingRepositoryImpl(
                logger = get<Logger>().value,
                regionsService = get<RegionsService>().value,
            )
        }
    }

    private fun putFeedbackRepository() {
        put(FeedbackRepository::class) {
            FeedbackRepositoryImpl(
                logger = get<Logger>().value,
                feedbackService = get<FeedbackService>().value,
            )
        }
    }

    private fun putSkuMatchingRepository() {
        put(SkuMatchingRepository::class) {
            SkuMatchingRepositoryImpl(
                logger = get<Logger>().value,
                recommendService = get<RecommendService>().value,
            )
        }
    }
    //endregion

    //region Request Builder Module
    private fun initRequestModule() {
        putImageMatchingRequestBuilder()
        putObjectDetectingRequestBuilder()
        putFeedbackRequestBuilder()
        putSkuMatchingBuilder()

        putRequestBuilders()
    }

    private fun putImageMatchingRequestBuilder() {
        put(ImageMatchingRequestBuilder::class) {
            ImageMatchingRequestBuilderImpl(
                logger = get<Logger>().value,
                imageMatchingRepository = get<ImageMatchingRepository>().value
            )
        }
    }

    private fun putObjectDetectingRequestBuilder() {
        put(ObjectDetectingRequestBuilder::class) {
            ObjectDetectingRequestBuilderImpl(
                logger = get<Logger>().value,
                objectDetectingRepository = get<ObjectDetectingRepository>().value
            )
        }
    }

    private fun putFeedbackRequestBuilder() {
        put(FeedbackRequestBuilder::class) {
            FeedbackRequestBuilderImpl(
                logger = get<Logger>().value,
                feedbackRepository = get<FeedbackRepository>().value
            )
        }
    }

    private fun putSkuMatchingBuilder() {
        put(SkuMatchingRequestBuilder::class) {
            SkuMatchingRequestBuilderImpl(
                logger = get<Logger>().value,
                skuMatchingRepository = get<SkuMatchingRepository>().value
            )
        }
    }

    private fun putRequestBuilders() {
        put(RequestBuilders::class) {
            RequestBuildersImpl(
                imageMatching = get(),
                objectDetecting = get(),
                feedback = get(),
                skuMatching = get(),
            )
        }
    }
    //endregion
}
