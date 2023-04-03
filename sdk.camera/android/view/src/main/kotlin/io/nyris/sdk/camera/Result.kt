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
package io.nyris.sdk.camera

import io.nyris.sdk.camera.core.BarcodeFormat

interface Result

data class BarcodeResult(val barcodes: List<Barcode>) : Result
data class Barcode(
    val code: String?,
    @BarcodeFormat
    val format: Int,
)

class ImageResult(
    val originalImage: ByteArray,
    val optimizedImage: ByteArray,
) : Result
