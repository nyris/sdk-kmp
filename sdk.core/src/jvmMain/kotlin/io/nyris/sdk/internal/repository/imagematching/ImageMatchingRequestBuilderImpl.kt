package io.nyris.sdk.internal.repository.imagematching

import io.nyris.sdk.internal.util.Logger
import io.nyris.sdk.model.MatchResponse

internal actual class ImageMatchingRequestBuilderImpl actual constructor(
    logger: Logger,
    imageMatchingRepository: ImageMatchingRepository,
) : CommonImageMatchingRequestBuilderImpl(logger, imageMatchingRepository) {
    override suspend fun match(image: DataType): Result<MatchResponse> {
        logger.log("[ImageMatchingRequestBuilderImpl] match")

        return imageMatchingRepository.match(
            image = image,
            params = createParams()
        )
    }
}

internal typealias DataType = ByteArray