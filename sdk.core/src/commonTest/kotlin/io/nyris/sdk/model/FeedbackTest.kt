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
package io.nyris.sdk.model

import kotlin.test.Test
import kotlin.test.assertFailsWith

class FeedbackTest {
    @Test
    fun `feedback region should throw exception when left is not in range 0 to 1`() {
        assertFailsWith<IllegalArgumentException>("left[2] should be in range of 0.0 to 1.0") {
            Feedback.Region(
                requestId = "",
                sessionId = "",
                left = 2.0F,
                top = 0.0F,
                width = 0.0F,
                height = 0.0F
            )
        }
    }

    @Test
    fun `feedback region should throw exception when top is not in range 0 to 1`() {
        assertFailsWith<IllegalArgumentException>("top[2] should be in range of 0.0 to 1.0") {
            Feedback.Region(
                requestId = "",
                sessionId = "",
                left = 0.0F,
                top = 2.0F,
                width = 0.0F,
                height = 0.0F
            )
        }
    }

    @Test
    fun `feedback region should throw exception when width is not in range 0 to 1`() {
        assertFailsWith<IllegalArgumentException>("width[2] should be in range of 0.0 to 1.0") {
            Feedback.Region(
                requestId = "",
                sessionId = "",
                left = 0.0F,
                top = 0.0F,
                width = 2.0F,
                height = 0.0F
            )
        }
    }

    @Test
    fun `feedback region should throw exception when height is not in range 0 to 1`() {
        assertFailsWith<IllegalArgumentException>("height[2] should be in range of 0.0 to 1.0") {
            Feedback.Region(
                requestId = "",
                sessionId = "",
                left = 0.0F,
                top = 0.0F,
                width = 0.0F,
                height = 2.0F
            )
        }
    }
}
