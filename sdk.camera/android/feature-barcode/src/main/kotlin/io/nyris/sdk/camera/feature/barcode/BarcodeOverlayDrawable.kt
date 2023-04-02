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
import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.graphics.drawable.Drawable
import io.nyris.sdk.camera.core.BarcodeFormat

class BarcodeOverlayDrawable(
    barcodeFormat: Int = BarcodeFormat.ALL,
    parentWidth: Float,
    parentHeight: Float,
    context: Context,
) : Drawable() {
    private val barcodeBoxPaint: Paint = Paint().apply {
        style = Paint.Style.STROKE
        color = context.getColorInt(R.color.nyris_barcode_box_stroke)
        strokeWidth = context.getDimensionPixelOffset(R.dimen.nyris_barcode_box_width)
    }
    private val overlayBackgroundPaint: Paint = Paint().apply {
        color = context.getColorInt(R.color.nyris_barcode_overlay_background)
    }
    private val eraserPaint: Paint = Paint().apply {
        style = Paint.Style.FILL
        strokeWidth = barcodeBoxPaint.strokeWidth
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }
    private val barcodeFormatPaint = Paint().apply {
        color = context.getColorInt(R.color.nyris_barcode_format_text)
        textSize = context.getDimensionPixelOffset(R.dimen.nyris_barcode_format)
    }

    private val barcodeCornerRadius: Float = context.getDimensionPixelOffset(R.dimen.nyris_barcode_box_radius)
    private val barcodeRectFactory: BarcodeRectFactory = BarcodeRectFactory()
    private var barcodeRect: RectF = barcodeRectFactory.create(barcodeFormat, parentWidth, parentHeight).toRectF()
    private val barcodeFormatStr: String =
        "${context.getString(R.string.nyris_barcode_format_label)}: ${barcodeFormat.toBarcodeFormatStr(context)}"

    override fun draw(canvas: Canvas) {
        with(canvas) {
            drawRect(0f, 0f, width.toFloat(), height.toFloat(), overlayBackgroundPaint)
            drawRoundRect(barcodeRect, barcodeCornerRadius, barcodeCornerRadius, eraserPaint)
            drawRoundRect(barcodeRect, barcodeCornerRadius, barcodeCornerRadius, barcodeBoxPaint)
            drawText(
                barcodeFormatStr,
                barcodeRect.left,
                barcodeRect.top - FORMAT_CODE_STRING_PADDING,
                barcodeFormatPaint
            )
        }
    }

    override fun setAlpha(alpha: Int) {
        // NO-OP
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        // NO-OP
    }

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}

private const val FORMAT_CODE_STRING_PADDING = 20
