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
package io.nyris.sdk.internal.network.feedback

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure
import kotlinx.serialization.serializer

internal object FeedbackDtoSerializer : KSerializer<FeedbackDto> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FeedbackDto") {
        element<List<Int>>("positions")
        element<List<String>>("product_ids")
        element<Boolean>("success")
        element<String>("comment")
        element<RectDto>("rect")
    }

    // NO Deserialize, return EMPTY
    override fun deserialize(decoder: Decoder): FeedbackDto = FeedbackDto.EMPTY

    override fun serialize(
        encoder: Encoder,
        value: FeedbackDto,
    ) {
        encoder.encodeStructure(descriptor) {
            when (value) {
                is ClickFeedbackDto -> {
                    encodeSerializableElement(descriptor, POSITIONS_INDEX, serializer(), value.positions)
                    encodeSerializableElement(descriptor, PUBLIC_IDS_INDEX, serializer(), value.productIds)
                }
                is ConversationFeedbackDto -> {
                    encodeSerializableElement(descriptor, POSITIONS_INDEX, serializer(), value.positions)
                    encodeSerializableElement(descriptor, PUBLIC_IDS_INDEX, serializer(), value.productIds)
                }
                is CommentFeedbackDto -> {
                    encodeBooleanElement(descriptor, SUCCESS_INDEX, value.success)
                    value.comment?.let { encodeStringElement(descriptor, COMMENT_INDEX, value.comment) }
                }
                is RegionFeedbackDto -> {
                    encodeSerializableElement(descriptor, RECT_INDEX, serializer(), value.rect)
                }
                is FeedbackDto.EMPTY -> {
                    /*Ignore*/
                }
            }
        }
    }
}

private const val POSITIONS_INDEX = 0
private const val PUBLIC_IDS_INDEX = 1
private const val SUCCESS_INDEX = 2
private const val COMMENT_INDEX = 3
private const val RECT_INDEX = 4
