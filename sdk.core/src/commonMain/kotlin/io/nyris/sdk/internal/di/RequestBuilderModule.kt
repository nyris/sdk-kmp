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

import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.internal.RequestBuilders
import io.nyris.sdk.internal.RequestBuildersImpl
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRepository
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRequestBuilderImpl
import io.nyris.sdk.util.Logger

internal object RequestBuilderModule {
    fun init() {
        putImageMatchingRequestBuilder()

        putRequestBuilders()
    }

    private fun putImageMatchingRequestBuilder() {
        ServiceLocator.put(ImageMatchingRequestBuilder::class) {
            ImageMatchingRequestBuilderImpl(
                logger = ServiceLocator.get<Logger>().value,
                imageMatchingRepository = ServiceLocator.get<ImageMatchingRepository>().value
            )
        }
    }

    private fun putRequestBuilders() {
        ServiceLocator.put(RequestBuilders::class) {
            RequestBuildersImpl(
                imageMatching = ServiceLocator.get<ImageMatchingRequestBuilder>().value
            )
        }
    }
}
