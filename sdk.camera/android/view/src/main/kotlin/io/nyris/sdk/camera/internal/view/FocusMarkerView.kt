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
package io.nyris.sdk.camera.internal.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import io.nyris.sdk.camera.databinding.NyrisCameraFocusViewBinding

internal class FocusMarkerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: NyrisCameraFocusViewBinding

    init {
        binding = NyrisCameraFocusViewBinding.inflate(LayoutInflater.from(context), this)
        binding.focus.alpha = ALPHA_0
    }

    fun render(
        xp: Float,
        yp: Float,
    ) {
        with(binding) {
            val x = (xp - focus.width / HALF).toInt()
            val y = (yp - focus.width / HALF).toInt()

            focus.translationX = x.toFloat()
            focus.translationY = y.toFloat()
            focus.animate().setListener(null).cancel()
            focus.scaleX = SCALE
            focus.scaleY = SCALE
            focus.alpha = ALPHA_1

            imFill.animate().setListener(null).cancel()
            imFill.scaleX = SCALE_0
            imFill.scaleY = SCALE_0
            imFill.alpha = ALPHA_1

            focus.animate().scaleX(SCALE_1)
                .scaleY(SCALE_1)
                .setStartDelay(DURATION_0)
                .setDuration(DURATION_30)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        focus.animate().alpha(ALPHA_0)
                            .setStartDelay(DURATION_150)
                            .setDuration(DURATION_200)
                            .setListener(null)
                            .start()
                    }
                }).start()

            imFill.animate()
                .scaleX(SCALE_1)
                .scaleY(SCALE_1)
                .setDuration(DURATION_30)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        imFill.animate()
                            .alpha(ALPHA_0)
                            .setDuration(DURATION_200)
                            .setListener(null)
                            .start()
                    }
                }).start()
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }
}

private const val HALF = 2
private const val SCALE = 1.30F
private const val SCALE_1 = 1F
private const val SCALE_0 = 0F

private const val ALPHA_0 = 0F
private const val ALPHA_1 = 1F

private const val DURATION_0 = 0L
private const val DURATION_30 = 30L
private const val DURATION_150 = 150L
private const val DURATION_200 = 200L
