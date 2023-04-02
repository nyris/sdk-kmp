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

import android.graphics.RectF
import io.nyris.sdk.camera.core.BarcodeFormat

internal class BarcodeRectFactory {
    fun create(
        @BarcodeFormat barcodeFormat: Int,
        width: Float,
        height: Float,
    ): KRectF = calculateRect(width, height, barcodeFormat.toAspectRation())

    private fun calculateRect(
        width: Float,
        height: Float,
        aspectRatio: Pair<Int, Int> = CODE_ALL_ASPECT_RATION,
    ): KRectF = with(aspectRatio) {
        val codeWidth = if (height > width) width else height
        val codeHeight = (codeWidth / first) * second

        val boxWidth = codeWidth * PERCENTAGE_FROM_SCREEN
        val boxHeight = codeHeight * PERCENTAGE_FROM_SCREEN
        val centerX = width / HALF
        val centerY = height / HALF
        KRectF(
            left = centerX - boxWidth / HALF,
            top = centerY - boxHeight / HALF,
            right = centerX + boxWidth / HALF,
            bottom = centerY + boxHeight / HALF,
        )
    }
}

data class KRectF(
    val left: Float,
    val top: Float,
    val right: Float,
    val bottom: Float,
) {
    fun toRectF(): RectF = RectF(left, top, right, bottom)
}

internal fun Int.toAspectRation(): Pair<Int, Int> = when (this) {
    BarcodeFormat.CODE_128 -> CODE_128_ASPECT_RATION
    BarcodeFormat.CODE_39 -> CODE_39_ASPECT_RATION
    BarcodeFormat.CODE_93 -> CODE_93_ASPECT_RATION
    BarcodeFormat.CODABAR -> CODABAR_ASPECT_RATION
    BarcodeFormat.EAN_8 -> EAN_8_ASPECT_RATION
    BarcodeFormat.EAN_13 -> EAN_13_ASPECT_RATION
    BarcodeFormat.ITF -> ITF_ASPECT_RATION
    BarcodeFormat.UPC_A -> UPC_A_ASPECT_RATION
    BarcodeFormat.UPC_E -> UPC_E_ASPECT_RATION
    BarcodeFormat.PDF417 -> PDF417_ASPECT_RATION
    BarcodeFormat.ALL,
    BarcodeFormat.QR_CODE,
    BarcodeFormat.AZTEC,
    BarcodeFormat.DATA_MATRIX,
    -> CODE_ALL_ASPECT_RATION
    else -> CODE_ALL_ASPECT_RATION
}

// To calculate aspect ratio use this https://andrew.hedges.name/experiments/aspect_ratio/
@Suppress("MagicNumber")
internal val CODE_ALL_ASPECT_RATION = Pair(1, 1)

@Suppress("MagicNumber")
internal val CODE_128_ASPECT_RATION = Pair(120, 47)

@Suppress("MagicNumber")
internal val CODE_39_ASPECT_RATION = Pair(131, 51)

@Suppress("MagicNumber")
internal val CODE_93_ASPECT_RATION = Pair(55, 37)

@Suppress("MagicNumber")
internal val CODABAR_ASPECT_RATION = Pair(1200, 689)

@Suppress("MagicNumber")
internal val EAN_8_ASPECT_RATION = Pair(512, 499)

@Suppress("MagicNumber")
internal val EAN_13_ASPECT_RATION = Pair(256, 55)

@Suppress("MagicNumber")
internal val ITF_ASPECT_RATION = Pair(384, 151)

@Suppress("MagicNumber")
internal val UPC_A_ASPECT_RATION = Pair(13, 7)

@Suppress("MagicNumber")
internal val UPC_E_ASPECT_RATION = Pair(41, 30)

@Suppress("MagicNumber")
internal val PDF417_ASPECT_RATION = Pair(27, 13)

private const val PERCENTAGE_FROM_SCREEN = 0.7F
private const val HALF = 2
