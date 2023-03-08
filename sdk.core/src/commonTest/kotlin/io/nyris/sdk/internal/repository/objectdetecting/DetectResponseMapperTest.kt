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
package io.nyris.sdk.internal.repository.objectdetecting

import io.nyris.sdk.internal.network.regions.PositionDto
import io.nyris.sdk.internal.network.regions.RegionDto
import io.nyris.sdk.internal.network.regions.RegionsResponse
import kotlin.test.assertEquals
import org.junit.Test

class DetectResponseMapperTest {
    @Test
    fun `toDetectResponse should map regionsResponse to DetectResponse`() {
        val detectResponse = REGION_RESPONSE.toDetectResponse()
        assertEquals(EXPECTED_DETECT_RESPONSE, detectResponse.toString())
    }
}

private val REGION_RESPONSE = RegionsResponse(
    regionsDto = listOf(
        RegionDto(
            confidence = 1.0F,
            positionDto = PositionDto(
                left = 1.0F,
                top = 1.0F,
                right = 1.0F,
                bottom = 1.0F,
            )
        )
    )
)

private const val EXPECTED_DETECT_RESPONSE =
    "DetectResponse(" +
        "regions=[" +
        "Region(" +
        "confidence=1.0, " +
        "position=" +
        "Position(" +
        "left=1.0, " +
        "top=1.0, " +
        "right=1.0, " +
        "bottom=1.0" +
        ")" +
        ")" +
        "]" +
        ")"
