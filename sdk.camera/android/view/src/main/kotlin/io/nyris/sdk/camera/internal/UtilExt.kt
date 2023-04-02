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
package io.nyris.sdk.camera.internal

import io.nyris.sdk.camera.core.BarcodeFormat
import io.nyris.sdk.camera.core.CaptureMode
import io.nyris.sdk.camera.core.CaptureModeEnum
import io.nyris.sdk.camera.core.CompressionFormat
import io.nyris.sdk.camera.core.CompressionFormatEnum
import io.nyris.sdk.camera.core.FocusMode
import io.nyris.sdk.camera.core.FocusModeEnum
import kotlin.math.roundToInt

internal inline fun <reified T> T?.required(block: () -> String? = { null }): T =
    this ?: throw IllegalArgumentException(block() ?: "${T::class.simpleName} is required!")

internal fun Float.round(): Float = (this * ROUND_TO_2_DIGITS).roundToInt() / ROUND_TO_2_DIGITS

internal fun Int.byteToKb(): Float = (this / KILO).round()
internal fun Int.byteToMb() = (byteToKb() / KILO).round()

internal fun Long.millisToSeconds() = (this / KILO).round()

internal fun @receiver:CaptureMode Int.toCaptureMode(): CaptureModeEnum = when {
    this == 0 -> CaptureModeEnum.Screenshot
    this == 1 -> CaptureModeEnum.Lens
    this == 2 -> CaptureModeEnum.Barcode
    else -> CaptureModeEnum.Screenshot
}

internal fun @receiver:FocusMode Int.toFocusMode(): FocusModeEnum = when {
    this == 0 -> FocusModeEnum.Automatic
    this == 1 -> FocusModeEnum.Manual
    else -> FocusModeEnum.Automatic
}

internal fun @receiver:CompressionFormat Int.toCompressionFormat(): CompressionFormatEnum = when {
    this == 0 -> CompressionFormatEnum.WebP
    this == 1 -> CompressionFormatEnum.Jpeg
    else -> CompressionFormatEnum.WebP
}

@BarcodeFormat
internal fun Int.toBarcodeFormat(): Int = this

private const val ROUND_TO_2_DIGITS = 100.0F
private const val KILO = 1000.0F
