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
