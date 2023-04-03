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

import com.google.mlkit.vision.barcode.common.Barcode
import io.nyris.sdk.camera.core.BarcodeFormat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class BarcodeFormatExtTest {
    @ParameterizedTest(name = "toLibraryBarcodeFormat should return the correct BarcodeIntArray | barcodeFormat[{0}]")
    @MethodSource("getTestData")
    fun `toLibraryBarcodeFormat should return the correct BarcodeIntArray`(
        barcodeFormat: Int,
        expected: BarcodeIntArray?,
    ) {
        if (barcodeFormat != UNKNOWN_BARCODE_FORMAT) {
            toLibraryBarcodeFormat(barcodeFormat, expected)
        } else {
            assertThrows(IllegalArgumentException::class.java) {
                toLibraryBarcodeFormat(barcodeFormat, expected)
            }
        }
    }

    private fun toLibraryBarcodeFormat(
        barcodeFormat: Int,
        expected: BarcodeIntArray?,
    ) {
        val result = barcodeFormat.toLibraryBarcodeFormat()
        assertEquals(expected, result)
    }

    companion object {
        @JvmStatic
        fun getTestData() = listOf(
            Arguments.of(BarcodeFormat.ALL, BarcodeIntArray(BARCODE_ALL_LIST.first(), BARCODE_ALL_LIST)),
            Arguments.of(BarcodeFormat.CODE_128, BarcodeIntArray(Barcode.FORMAT_CODE_128, null)),
            Arguments.of(BarcodeFormat.CODE_39, BarcodeIntArray(Barcode.FORMAT_CODE_39, null)),
            Arguments.of(BarcodeFormat.CODE_93, BarcodeIntArray(Barcode.FORMAT_CODE_93, null)),
            Arguments.of(BarcodeFormat.CODABAR, BarcodeIntArray(Barcode.FORMAT_CODABAR, null)),
            Arguments.of(BarcodeFormat.EAN_8, BarcodeIntArray(Barcode.FORMAT_EAN_8, null)),
            Arguments.of(BarcodeFormat.EAN_13, BarcodeIntArray(Barcode.FORMAT_EAN_13, null)),
            Arguments.of(BarcodeFormat.ITF, BarcodeIntArray(Barcode.FORMAT_ITF, null)),
            Arguments.of(BarcodeFormat.UPC_A, BarcodeIntArray(Barcode.FORMAT_UPC_A, null)),
            Arguments.of(BarcodeFormat.UPC_E, BarcodeIntArray(Barcode.FORMAT_UPC_E, null)),
            Arguments.of(BarcodeFormat.QR_CODE, BarcodeIntArray(Barcode.FORMAT_QR_CODE, null)),
            Arguments.of(BarcodeFormat.PDF417, BarcodeIntArray(Barcode.FORMAT_PDF417, null)),
            Arguments.of(BarcodeFormat.AZTEC, BarcodeIntArray(Barcode.FORMAT_AZTEC, null)),
            Arguments.of(BarcodeFormat.DATA_MATRIX, BarcodeIntArray(Barcode.FORMAT_DATA_MATRIX, null)),
            Arguments.of(UNKNOWN_BARCODE_FORMAT, null)
        )
    }
}

private const val UNKNOWN_BARCODE_FORMAT = -1
