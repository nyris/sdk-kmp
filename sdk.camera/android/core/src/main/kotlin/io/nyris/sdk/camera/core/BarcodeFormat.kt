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
package io.nyris.sdk.camera.core

import androidx.annotation.IntDef
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

@IntDef(
    ALL, CODE_128, CODE_39, CODE_93, CODABAR, EAN_8, EAN_13, ITF, UPC_A, UPC_E, QR_CODE, PDF417, AZTEC, DATA_MATRIX
)
@Retention(AnnotationRetention.SOURCE)
annotation class BarcodeFormat {
    companion object {
        const val ALL = 0
        const val CODE_128 = 1
        const val CODE_39 = 2
        const val CODE_93 = 3
        const val CODABAR = 4
        const val EAN_8 = 5
        const val EAN_13 = 6
        const val ITF = 7
        const val UPC_A = 8
        const val UPC_E = 9
        const val QR_CODE = 10
        const val PDF417 = 11
        const val AZTEC = 12
        const val DATA_MATRIX = 13
    }
}
