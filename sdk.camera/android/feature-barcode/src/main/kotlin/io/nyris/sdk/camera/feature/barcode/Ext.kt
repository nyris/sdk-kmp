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

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import io.nyris.sdk.camera.core.BarcodeFormat

@ColorInt
internal fun Context.getColorInt(@ColorRes id: Int) =
    ContextCompat.getColor(this, id)

internal fun Context.getDimensionPixelOffset(@DimenRes id: Int) =
    resources.getDimensionPixelOffset(id).toFloat()

internal fun Int.toBarcodeFormatStr(context: Context): String = when (this) {
    BarcodeFormat.ALL -> context.getString(R.string.nyris_barcode_format_all)
    BarcodeFormat.CODE_128 -> context.getString(R.string.nyris_barcode_format_code_128)
    BarcodeFormat.CODE_39 -> context.getString(R.string.nyris_barcode_format_code_39)
    BarcodeFormat.CODE_93 -> context.getString(R.string.nyris_barcode_format_code_93)
    BarcodeFormat.CODABAR -> context.getString(R.string.nyris_barcode_format_codebar)
    BarcodeFormat.EAN_8 -> context.getString(R.string.nyris_barcode_format_ean_8)
    BarcodeFormat.EAN_13 -> context.getString(R.string.nyris_barcode_format_ean_13)
    BarcodeFormat.ITF -> context.getString(R.string.nyris_barcode_format_ean_itf)
    BarcodeFormat.UPC_A -> context.getString(R.string.nyris_barcode_format_upc_a)
    BarcodeFormat.UPC_E -> context.getString(R.string.nyris_barcode_format_upc_e)
    BarcodeFormat.PDF417 -> context.getString(R.string.nyris_barcode_format_pdf417)
    BarcodeFormat.QR_CODE -> context.getString(R.string.nyris_barcode_format_qr_code)
    BarcodeFormat.AZTEC -> context.getString(R.string.nyris_barcode_format_aztec)
    BarcodeFormat.DATA_MATRIX -> context.getString(R.string.nyris_barcode_format_data_matrix)
    else -> context.getString(R.string.nyris_barcode_format_unknown)
}
