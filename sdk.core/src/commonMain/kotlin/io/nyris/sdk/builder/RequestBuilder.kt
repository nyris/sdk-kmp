package io.nyris.sdk.builder

import io.nyris.sdk.internal.repository.imagematching.DataType
import io.nyris.sdk.model.DetectResponse
import io.nyris.sdk.model.Feedback
import io.nyris.sdk.model.MatchResponse
import io.nyris.sdk.model.SkuResponse

interface FeedbackRequestBuilder {
    suspend fun send(feedback: Feedback): NyrisResult
}

sealed class NyrisResult {
    object Successfull : NyrisResult()
    object Fail : NyrisResult()
}

interface ImageMatchingRequestBuilder {
    fun limit(limit: Int): ImageMatchingRequestBuilder

    fun language(language: String): ImageMatchingRequestBuilder

    fun threshold(threshold: Float): ImageMatchingRequestBuilder

    fun geolocation(
        lat: Float,
        lon: Float,
        dist: Int,
    ): ImageMatchingRequestBuilder

    fun filters(filters: Map<String, List<String>>): ImageMatchingRequestBuilder

    fun session(session: String): ImageMatchingRequestBuilder

    // Put the params before this call
    suspend fun match(image: DataType): Result<MatchResponse>
}

interface ObjectDetectingRequestBuilder {
    fun session(session: String): ObjectDetectingRequestBuilder

    suspend fun detect(image: ByteArray): Result<DetectResponse>
}

interface SkuMatchingRequestBuilder {
    suspend fun match(sku: String): Result<SkuResponse>
}

