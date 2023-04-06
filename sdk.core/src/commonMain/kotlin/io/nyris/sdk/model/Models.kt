package io.nyris.sdk.model

class DetectResponse(
    val regions: List<Region> = emptyList(),
)

class Region(
    val confidence: Float = 0.0F,
    val position: Position? = null,
)

class Position(
    val left: Float = 0F,
    val top: Float = 0F,
    val right: Float = 0F,
    val bottom: Float = 0F,
)

sealed class Feedback private constructor(
    internal val requestId: String,
    internal val sessionId: String,
) {
    class Click(
        requestId: String,
        sessionId: String,
        //TODO: Check if we can use @ShouldRefineInSwift
        // https://kotlinlang.org/docs/native-objc-interop.html#hiding-kotlin-declarations
        // @ShouldRefineInSwift
        // TODO: try to rename KotlinInt to just NSNumber to make it more iOS friendly.
        val positions: List<Int>,
        val productIds: List<String>,
    ) : Feedback(requestId, sessionId)

    class Conversion(
        requestId: String,
        sessionId: String,
        val positions: List<Int>,
        val productIds: List<String>,
    ) : Feedback(requestId, sessionId)

    class Comment(
        requestId: String,
        sessionId: String,
        val success: Boolean,
        val comment: String? = null,
    ) : Feedback(requestId, sessionId)

    class Region(
        requestId: String,
        sessionId: String,
        val left: Float,
        val top: Float,
        val width: Float,
        val height: Float,
    ) : Feedback(requestId, sessionId) {
        init {
            require(left in RANGE_MIN..RANGE_MAX) { "left[$left] should be in range of 0.0 to 1.0" }
            require(top in RANGE_MIN..RANGE_MAX) { "top[$top] should be in range of 0.0 to 1.0" }
            require(width in RANGE_MIN..RANGE_MAX) { "width[$width] should be in range of 0.0 to 1.0" }
            require(height in RANGE_MIN..RANGE_MAX) { "height[$height] should be in range of 0.0 to 1.0" }
        }
    }
}

private const val RANGE_MIN = 0.0
private const val RANGE_MAX = 1.0

class MatchResponse(
    val requestId: String? = null,

    val sessionId: String? = null,

    val offers: List<Offer> = emptyList(),
)

class Offer(
    val id: String? = null,
    val title: String? = null,
    val description: String? = null,
    val descriptionLong: String? = null,
    val language: String? = null,
    val brand: String? = null,
    val catalogNumbers: List<String> = emptyList(),
    val customIds: Map<String, String> = emptyMap(),
    val keywords: List<String>? = null,
    val categories: List<String> = emptyList(),
    val availability: String? = null,
    val feedId: String? = null,
    val groupId: String? = null,
    val priceStr: String? = null,
    val salePrice: String? = null,
    val links: Links? = null,
    val images: List<String> = emptyList(),
    val metadata: String? = null,
    val sku: String? = null,
    val score: Float? = null,
)

class Links(
    val main: String? = null,
    val mobile: String? = null,
)

class SkuResponse(
    val requestId: String?,
    val sessionId: String?,
    val offers: List<SkuOffer> = emptyList(),
)

class SkuOffer(
    val sku: String?,
    val score: Float?,
)
