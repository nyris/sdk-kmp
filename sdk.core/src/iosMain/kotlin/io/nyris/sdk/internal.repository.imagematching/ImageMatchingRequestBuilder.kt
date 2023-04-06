package io.nyris.sdk.internal.repository.imagematching

import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.internal.util.toByteArray
import io.nyris.sdk.model.MatchResponse
import platform.Foundation.NSData

internal actual class ImageMatchingRequestBuilderImpl actual constructor(
    logger: Logger,
    imageMatchingRepository: ImageMatchingRepository,
) : CommonImageMatchingRequestBuilderImpl(logger, imageMatchingRepository) {
    override suspend fun match(image: DataType): Result<MatchResponse> {
        logger.log("[ImageMatchingRequestBuilderImpl] match")

        // Convert NSData to byte array
        return imageMatchingRepository.match(
            image = image.toByteArray(),
            params = createParams()
        )
    }
}

internal actual typealias DataType = NSData
