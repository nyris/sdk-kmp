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

import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.XOptionsBuilder
import io.nyris.sdk.internal.network.feedback.FeedbackService
import io.nyris.sdk.internal.network.feedback.FeedbackServiceImpl
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.find.FindServiceImpl
import io.nyris.sdk.internal.network.regions.RegionsService
import io.nyris.sdk.internal.network.regions.RegionsServiceImpl
import io.nyris.sdk.internal.util.Logger
import kotlinx.coroutines.Dispatchers

internal object ServiceModule {
    fun init() {
        putFindService()
        putRegionsService()
        putFeedback()
    }

    private fun putFindService() {
        ServiceLocator.put(FindService::class) {
            FindServiceImpl(
                logger = ServiceLocator.get<Logger>().value,
                xOptionsBuilder = ServiceLocator.get<XOptionsBuilder>().value,
                httpClient = ServiceLocator.get<NyrisHttpClient>().value,
                endpoints = ServiceLocator.get<Endpoints>().value,
                coroutineContext = Dispatchers.IO
            )
        }
    }

    private fun putRegionsService() {
        ServiceLocator.put(RegionsService::class) {
            RegionsServiceImpl(
                logger = ServiceLocator.get<Logger>().value,
                httpClient = ServiceLocator.get<NyrisHttpClient>().value,
                endpoints = ServiceLocator.get<Endpoints>().value,
                coroutineContext = Dispatchers.IO
            )
        }
    }

    private fun putFeedback() {
        ServiceLocator.put(FeedbackService::class) {
            FeedbackServiceImpl(
                logger = ServiceLocator.get<Logger>().value,
                httpClient = ServiceLocator.get<NyrisHttpClient>().value,
                endpoints = ServiceLocator.get<Endpoints>().value,
                coroutineContext = Dispatchers.IO
            )
        }
    }
}
