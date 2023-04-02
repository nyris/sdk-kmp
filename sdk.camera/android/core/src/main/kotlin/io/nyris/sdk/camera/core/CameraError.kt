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

class CameraError(
    @CameraErrorCode val code: Int,
    message: String?,
) : Throwable(message)

@IntDef(
    CAMERA_ERROR_UNKNOWN,
    CAMERA_ERROR_BIND,
    CAMERA_ERROR_CAPTURE_NOT_AVAILABLE,
    CAMERA_ERROR_CAPTURE_LENS,
    CAMERA_ERROR_CAPTURE_SCREENSHOT,
    CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE,
    CAMERA_ERROR_MANUAL_FOCUS,
    CAMERA_ERROR_STATE,
    CAMERA_BARCODE_ERROR
)
@Retention(AnnotationRetention.SOURCE)
annotation class CameraErrorCode

const val CAMERA_ERROR_UNKNOWN = 0
const val CAMERA_ERROR_BIND = 1
const val CAMERA_ERROR_CAPTURE_NOT_AVAILABLE = 2
const val CAMERA_ERROR_CAPTURE_LENS = 3
const val CAMERA_ERROR_CAPTURE_SCREENSHOT = 4
const val CAMERA_ERROR_BACK_CAMERA_NOT_AVAILABLE = 5
const val CAMERA_ERROR_MANUAL_FOCUS = 6
const val CAMERA_ERROR_STATE = 7
const val CAMERA_BARCODE_ERROR = 8
