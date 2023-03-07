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

import io.nyris.sdk.internal.network.ApiHeaders
import io.nyris.sdk.internal.network.Endpoints
import io.nyris.sdk.internal.network.NyrisHttpClient
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.find.FindServiceImpl
import io.nyris.sdk.util.Logger
import kotlinx.coroutines.Dispatchers

internal object ServiceModule {
    fun init() {
        putFindService()
    }

    private fun putFindService() {
        ServiceLocator.put(FindService::class) {
            FindServiceImpl(
                logger = ServiceLocator.get<Logger>().value,
                apiHeaders = ServiceLocator.get<ApiHeaders>().value,
                httpClient = ServiceLocator.get<NyrisHttpClient>().value,
                endpoints = ServiceLocator.get<Endpoints>().value,
                coroutineContext = Dispatchers.IO
            )
        }
    }
}
