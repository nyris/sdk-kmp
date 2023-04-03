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
package io.nyris.sdk.camera.feature.barcode

import io.nyris.sdk.camera.core.BarcodeFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BarcodeRectFactoryTest {
    private val classToTest = BarcodeRectFactory()

    @ParameterizedTest(name = "create should create the correct rectF | barcodeFormat[{0}]")
    @MethodSource("getTestData")
    fun `create should create the correct rectF`(
        barcodeFormat: Int,
        expected: KRectF,
    ) {
        val rectF = classToTest.create(
            barcodeFormat = barcodeFormat,
            width = SCREEN_WIDTH,
            height = SCREEN_HEIGHT
        )
        assertEquals(expected, rectF)
    }

    @ParameterizedTest(name = "toAspectRation should map the codeFormat to the correct aspect ration| barcodeFormat[{0}]")
    @MethodSource("getTestData2")
    fun `toAspectRation should map the codeFormat to the correct aspect ration`(
        barcodeFormat: Int,
        expected: Pair<Int, Int>,
    ) {
        val aspectRatio = barcodeFormat.toAspectRation()
        assertEquals(expected, aspectRatio)
    }

    companion object {
        @JvmStatic
        fun getTestData() = listOf(
            Arguments.of(BarcodeFormat.ALL, KRectF(288.0F, 108.0F, 792.0F, 612.0F)),
            Arguments.of(BarcodeFormat.CODE_128, KRectF(288.0F, 261.3F, 792.0F, 458.7F)),
            Arguments.of(BarcodeFormat.CODE_39, KRectF(288.0F, 261.89313F, 792.0F, 458.10687F)),
            Arguments.of(BarcodeFormat.CODE_93, KRectF(288.0F, 190.47273F, 792.0F, 529.5273F)),
            Arguments.of(BarcodeFormat.EAN_8, KRectF(288.0F, 114.39844F, 792.0F, 605.60156F)),
            Arguments.of(BarcodeFormat.EAN_13, KRectF(288.0F, 305.85938F, 792.0F, 414.14062F)),
            Arguments.of(BarcodeFormat.ITF, KRectF(288.0F, 260.90625F, 792.0F, 459.09375F)),
            Arguments.of(BarcodeFormat.UPC_A, KRectF(288.0F, 224.3077F, 792.0F, 495.69232F)),
            Arguments.of(BarcodeFormat.UPC_E, KRectF(288.0F, 175.60976F, 792.0F, 544.39026F)),
            Arguments.of(BarcodeFormat.QR_CODE, KRectF(288.0F, 108.0F, 792.0F, 612.0F)),
            Arguments.of(BarcodeFormat.PDF417, KRectF(288.0F, 238.66667F, 792.0F, 481.3333F)),
            Arguments.of(BarcodeFormat.AZTEC, KRectF(288.0F, 108.0F, 792.0F, 612.0F)),
            Arguments.of(BarcodeFormat.DATA_MATRIX, KRectF(288.0F, 108.0F, 792.0F, 612.0F)),
        )

        @JvmStatic
        fun getTestData2() = listOf(
            Arguments.of(BarcodeFormat.ALL, CODE_ALL_ASPECT_RATION),
            Arguments.of(BarcodeFormat.CODE_128, CODE_128_ASPECT_RATION),
            Arguments.of(BarcodeFormat.CODE_39, CODE_39_ASPECT_RATION),
            Arguments.of(BarcodeFormat.CODE_93, CODE_93_ASPECT_RATION),
            Arguments.of(BarcodeFormat.CODABAR, CODABAR_ASPECT_RATION),
            Arguments.of(BarcodeFormat.EAN_8, EAN_8_ASPECT_RATION),
            Arguments.of(BarcodeFormat.EAN_13, EAN_13_ASPECT_RATION),
            Arguments.of(BarcodeFormat.ITF, ITF_ASPECT_RATION),
            Arguments.of(BarcodeFormat.UPC_A, UPC_A_ASPECT_RATION),
            Arguments.of(BarcodeFormat.UPC_E, UPC_E_ASPECT_RATION),
            Arguments.of(BarcodeFormat.QR_CODE, CODE_ALL_ASPECT_RATION),
            Arguments.of(BarcodeFormat.PDF417, PDF417_ASPECT_RATION),
            Arguments.of(BarcodeFormat.AZTEC, CODE_ALL_ASPECT_RATION),
            Arguments.of(BarcodeFormat.DATA_MATRIX, CODE_ALL_ASPECT_RATION),
        )
    }
}

private const val SCREEN_WIDTH = 1080F
private const val SCREEN_HEIGHT = 720F
