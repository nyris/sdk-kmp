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

import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_AZTEC
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODABAR
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_128
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_39
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_CODE_93
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_DATA_MATRIX
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_13
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_EAN_8
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_ITF
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_PDF417
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_QR_CODE
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_A
import com.google.mlkit.vision.barcode.common.Barcode.FORMAT_UPC_E
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.ALL
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.AZTEC
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.CODABAR
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.CODE_128
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.CODE_39
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.CODE_93
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.DATA_MATRIX
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.EAN_13
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.EAN_8
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.ITF
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.PDF417
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.QR_CODE
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.UPC_A
import io.nyris.sdk.camera.core.BarcodeFormat.Companion.UPC_E

internal val BARCODE_ALL_LIST = intArrayOf(
    FORMAT_CODE_128,
    FORMAT_CODE_39,
    FORMAT_CODE_93,
    FORMAT_CODABAR,
    FORMAT_EAN_8,
    FORMAT_EAN_13,
    FORMAT_ITF,
    FORMAT_UPC_A,
    FORMAT_UPC_E,
    FORMAT_QR_CODE,
    FORMAT_PDF417,
    FORMAT_AZTEC,
    FORMAT_DATA_MATRIX
)
internal const val BARCODE_ALL =
    FORMAT_CODE_128 + FORMAT_CODE_39 + FORMAT_CODE_93 + FORMAT_CODABAR + FORMAT_EAN_8 +
        FORMAT_EAN_13 + FORMAT_ITF + FORMAT_UPC_A + FORMAT_UPC_E + FORMAT_QR_CODE + FORMAT_PDF417 + FORMAT_AZTEC +
        FORMAT_DATA_MATRIX

internal fun Int.toLibraryBarcodeFormat(): BarcodeIntArray = when (this) {
    ALL -> {
        require(BARCODE_ALL == BARCODE_ALL_LIST.sum()) {
            "List of the barcode does not match the flag io.nyris.sdk.camera.feature.barcode.BARCODE_ALL"
        }
        BarcodeIntArray(BARCODE_ALL_LIST.first(), BARCODE_ALL_LIST)
    }
    CODE_128 -> BarcodeIntArray(FORMAT_CODE_128, null)
    CODE_39 -> BarcodeIntArray(FORMAT_CODE_39, null)
    CODE_93 -> BarcodeIntArray(FORMAT_CODE_93, null)
    CODABAR -> BarcodeIntArray(FORMAT_CODABAR, null)
    EAN_8 -> BarcodeIntArray(FORMAT_EAN_8, null)
    EAN_13 -> BarcodeIntArray(FORMAT_EAN_13, null)
    ITF -> BarcodeIntArray(FORMAT_ITF, null)
    UPC_A -> BarcodeIntArray(FORMAT_UPC_A, null)
    UPC_E -> BarcodeIntArray(FORMAT_UPC_E, null)
    QR_CODE -> BarcodeIntArray(FORMAT_QR_CODE, null)
    PDF417 -> BarcodeIntArray(FORMAT_PDF417, null)
    AZTEC -> BarcodeIntArray(FORMAT_AZTEC, null)
    DATA_MATRIX -> BarcodeIntArray(FORMAT_DATA_MATRIX, null)
    else -> throw IllegalArgumentException("Barcode format not recognized!")
}

internal typealias BarcodeIntArray = Pair<Int, IntArray?>
