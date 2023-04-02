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
package io.nyris.sdk.camera.feature.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.os.Build
import androidx.annotation.IntRange
import androidx.camera.core.ImageProxy
import io.nyris.sdk.camera.core.CompressionFormatEnum
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import kotlin.math.roundToInt

fun ImageProxy.toCorrectBitmap(): Bitmap {
    val imageBytes = this.toByteArray()
    val imageBtm = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val matrix = Matrix().apply { preRotate(imageInfo.rotationDegrees.toFloat()) }
    return Bitmap.createBitmap(
        imageBtm,
        cropRect.left,
        cropRect.top,
        cropRect.width(),
        cropRect.height(),
        matrix,
        true
    )
}

fun ByteArray.optimize(
    compressionFormat: CompressionFormatEnum,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    quality: Int,
): ByteArray {
    val bitmap = toBitmap()
    val megaByte = count().byteToMb()
    return if (megaByte >= 1) {
        val width = (bitmap.width / megaByte).toInt()
        val height = (bitmap.height / megaByte).toInt()
        Bitmap.createScaledBitmap(bitmap, width, height, true)
    } else {
        bitmap
    }.toByteArray(compressionFormat = compressionFormat, quality = quality)
}

fun ImageProxy.toByteArray(): ByteArray = with(this.planes.first().buffer) {
    rewind()
    ByteArray(capacity()).apply {
        get(this)
    }.also {
        close()
    }
}

fun Bitmap.toByteArray(
    compressionFormat: CompressionFormatEnum,
    @IntRange(from = MIN_QUALITY, to = MAX_QUALITY)
    quality: Int = 100,
): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(compressionFormat.toBitmapCompressionFormat(), quality, stream)
    return stream.toByteArray()
}

fun ByteArray.toBitmap(): Bitmap = BitmapFactory.decodeByteArray(this, 0, size)

fun Image.fromYuvToBitmap(rotation: Int): Bitmap {
    val yBuffer: ByteBuffer = planes[0].buffer
    val uBuffer: ByteBuffer = planes[1].buffer
    val vBuffer: ByteBuffer = planes[2].buffer
    val ySize: Int = yBuffer.remaining()
    val uSize: Int = uBuffer.remaining()
    val vSize: Int = vBuffer.remaining()
    val nv21 = ByteArray(ySize + uSize + vSize)
    // U and V are swapped
    yBuffer.get(nv21, OFFSET_0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(LEFT_0, TOP_0, yuvImage.width, yuvImage.height), YUV_QUALITY, out)
    val imageBytes = out.toByteArray()
    val imageBtm = BitmapFactory.decodeByteArray(imageBytes, OFFSET_0, imageBytes.size)

    val matrix = Matrix().apply { preRotate(rotation.toFloat()) }
    return Bitmap.createBitmap(
        imageBtm,
        X_0,
        Y_0,
        imageBtm.width,
        imageBtm.height,
        matrix,
        true
    )
}

@Suppress("DEPRECATION")
internal fun CompressionFormatEnum.toBitmapCompressionFormat(): Bitmap.CompressFormat = when (this) {
    CompressionFormatEnum.WebP -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Bitmap.CompressFormat.WEBP_LOSSY
        } else {
            Bitmap.CompressFormat.WEBP
        }
    }
    CompressionFormatEnum.Jpeg -> {
        Bitmap.CompressFormat.JPEG
    }
}

internal fun Float.round(): Float = (this * ROUND_TO_2_DIGITS).roundToInt() / ROUND_TO_2_DIGITS
internal fun Int.byteToKb(): Float = (this / KILO).round()
internal fun Int.byteToMb() = (byteToKb() / KILO).round()

internal const val KILO = 1000.0F
internal const val ROUND_TO_2_DIGITS = 1000.0F
internal const val OFFSET_0 = 0
internal const val LEFT_0 = 0
internal const val TOP_0 = 0
internal const val X_0 = 0
internal const val Y_0 = 0
internal const val YUV_QUALITY = 75
