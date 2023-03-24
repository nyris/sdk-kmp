package io.nyris.sdk

import io.nyris.sdk.builder.FeedbackRequestBuilder
import io.nyris.sdk.builder.ImageMatchingRequestBuilder
import io.nyris.sdk.builder.ObjectDetectingRequestBuilder
import io.nyris.sdk.builder.SkuMatchingRequestBuilder
import io.nyris.sdk.internal.NyrisImpl

actual interface Nyris {
    actual fun imageMatching(): ImageMatchingRequestBuilder
    actual fun objectDetecting(): ObjectDetectingRequestBuilder
    actual fun feedback(): FeedbackRequestBuilder
    actual fun skuMatching(): SkuMatchingRequestBuilder

    actual companion object {
        actual fun createInstance(
            apiKey: String,
            config: NyrisConfig,
        ): Nyris = NyrisImpl.createInstance(
            apiKey = apiKey,
            config = config.copy(
                platform = NyrisPlatform.IOS
            )
        )
    }
}
