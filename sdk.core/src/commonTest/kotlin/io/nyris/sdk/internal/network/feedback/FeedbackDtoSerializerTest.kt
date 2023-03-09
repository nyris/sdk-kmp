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

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.descriptors.elementNames
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@OptIn(ExperimentalSerializationApi::class)
class FeedbackDtoSerializerTest {
    private val classToTest = FeedbackDtoSerializer

    @Test
    fun `serializer should have the correct serial descriptor`() {
        with(classToTest.descriptor) {
            val names = elementNames.toList()
            assertEquals(5, names.size)
            assertEquals("positions", names[POSITIONS_INDEX])
            assertEquals("product_ids", names[PUBLIC_IDS_INDEX])
            assertEquals("success", names[SUCCESS_INDEX])
            assertEquals("comment", names[COMMENT_INDEX])
            assertEquals("rect", names[RECT_INDEX])

            val elements = elementDescriptors.toList()
            with(elements[POSITIONS_INDEX]) {
                assertEquals("kotlin.collections.ArrayList", serialName)
                assertEquals("kotlin.Int", elementDescriptors.toList().first().serialName)
            }
            with(elements[PUBLIC_IDS_INDEX]) {
                assertEquals("kotlin.collections.ArrayList", serialName)
                assertEquals("kotlin.String", elementDescriptors.toList().first().serialName)
            }
            assertEquals("kotlin.Boolean", elements[SUCCESS_INDEX].serialName)
            assertEquals("kotlin.String", elements[COMMENT_INDEX].serialName)
            assertEquals("io.nyris.sdk.internal.network.feedback.RectDto", elements[RECT_INDEX].serialName)
        }
    }

    @Test
    fun `deserialize should return empty feedback dto`() {
        val decoder = mockk<Decoder>()

        val dto = classToTest.deserialize(decoder)

        assertEquals(FeedbackDto.EMPTY, dto)
    }

    @Test
    fun `serialize should serialize CommentFeedbackDto`() {
        val dto = CommentFeedbackDto(true, "comment")
        val compositeEncoder = mockk<CompositeEncoder>(relaxed = true)
        val encoder = mockk<Encoder> encoder@{
            every { this@encoder.beginStructure(any()) } returns compositeEncoder
        }

        classToTest.serialize(encoder, dto)

        verify {
            compositeEncoder.encodeBooleanElement(any(), SUCCESS_INDEX, true)
            compositeEncoder.encodeStringElement(any(), COMMENT_INDEX, "comment")
        }
    }

    @Test
    fun `serialize should not serialize FeedbackDto-EMPTY`() {
        val dto = FeedbackDto.EMPTY
        val compositeEncoder = mockk<CompositeEncoder>(relaxed = true)
        val encoder = mockk<Encoder> encoder@{
            every { this@encoder.beginStructure(any()) } returns compositeEncoder
        }

        classToTest.serialize(encoder, dto)

        verify { compositeEncoder.endStructure(any()) }
        confirmVerified(compositeEncoder)
    }

    @Test
    fun `serialize should serialize ClickFeedbackDto`() {
        val dto = mockk<ClickFeedbackDto>(relaxed = true)
        val compositeEncoder = mockk<CompositeEncoder>(relaxed = true)
        val encoder = mockk<Encoder> encoder@{
            every { this@encoder.beginStructure(any()) } returns compositeEncoder
        }

        classToTest.serialize(encoder, dto)

        verify {
            compositeEncoder.endStructure(any())
            compositeEncoder.encodeSerializableElement(any(), POSITIONS_INDEX, any(), any<List<Int>>())
            compositeEncoder.encodeSerializableElement(any(), PUBLIC_IDS_INDEX, any(), any<List<Int>>())
        }
        confirmVerified(compositeEncoder)
    }

    @Test
    fun `serialize should serialize ConversationFeedbackDto`() {
        val dto = mockk<ConversationFeedbackDto>(relaxed = true)
        val compositeEncoder = mockk<CompositeEncoder>(relaxed = true)
        val encoder = mockk<Encoder> encoder@{
            every { this@encoder.beginStructure(any()) } returns compositeEncoder
        }

        classToTest.serialize(encoder, dto)

        verify {
            compositeEncoder.endStructure(any())
            compositeEncoder.encodeSerializableElement(any(), POSITIONS_INDEX, any(), any<List<Int>>())
            compositeEncoder.encodeSerializableElement(any(), PUBLIC_IDS_INDEX, any(), any<List<Int>>())
        }
        confirmVerified(compositeEncoder)
    }

    @Test
    fun `serialize should serialize RegionFeedbackDto`() {
        val dto = mockk<RegionFeedbackDto>(relaxed = true)
        val compositeEncoder = mockk<CompositeEncoder>(relaxed = true)
        val encoder = mockk<Encoder> encoder@{
            every { this@encoder.beginStructure(any()) } returns compositeEncoder
        }

        classToTest.serialize(encoder, dto)

        verify {
            compositeEncoder.endStructure(any())
            compositeEncoder.encodeSerializableElement(any(), RECT_INDEX, any(), any<RectDto>())
        }
        confirmVerified(compositeEncoder)
    }
}

private const val POSITIONS_INDEX = 0
private const val PUBLIC_IDS_INDEX = 1
private const val SUCCESS_INDEX = 2
private const val COMMENT_INDEX = 3
private const val RECT_INDEX = 4
