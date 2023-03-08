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

data class DetectResponse(
    val regions: List<Region> = emptyList(),
)

data class Region(
    val confidence: Float = 0.0F,
    val position: Position? = null,
)

data class Position(
    val left: Float = 0F,
    val top: Float = 0F,
    val right: Float = 0F,
    val bottom: Float = 0F,
)
