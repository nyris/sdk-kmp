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

import io.nyris.sdk.internal.network.feedback.FeedbackService
import io.nyris.sdk.internal.network.find.FindService
import io.nyris.sdk.internal.network.regions.RegionsService
import io.nyris.sdk.internal.repository.feedback.FeedbackRepository
import io.nyris.sdk.internal.repository.feedback.FeedbackRepositoryImpl
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRepository
import io.nyris.sdk.internal.repository.imagematching.ImageMatchingRepositoryImpl
import io.nyris.sdk.internal.repository.objectdetecting.ObjectDetectingRepository
import io.nyris.sdk.internal.repository.objectdetecting.ObjectDetectingRepositoryImpl
import io.nyris.sdk.internal.util.Logger

internal object RepositoryModule {
    fun init() {
        putImageMatchingRepository()
        putObjectDetectingRepository()
        putFeedbackRepository()
    }

    private fun putImageMatchingRepository() {
        ServiceLocator.put(ImageMatchingRepository::class) {
            ImageMatchingRepositoryImpl(
                logger = ServiceLocator.get<Logger>().value,
                findService = ServiceLocator.get<FindService>().value,
            )
        }
    }

    private fun putObjectDetectingRepository() {
        ServiceLocator.put(ObjectDetectingRepository::class) {
            ObjectDetectingRepositoryImpl(
                logger = ServiceLocator.get<Logger>().value,
                regionsService = ServiceLocator.get<RegionsService>().value,
            )
        }
    }

    private fun putFeedbackRepository() {
        ServiceLocator.put(FeedbackRepository::class) {
            FeedbackRepositoryImpl(
                logger = ServiceLocator.get<Logger>().value,
                feedbackService = ServiceLocator.get<FeedbackService>().value,
            )
        }
    }
}
