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
package io.nyris.sdk.internal.network

import io.nyris.sdk.internal.repository.imagematching.GeolocationParam

internal class Endpoints(baseUrl: String) {
    private val find: String = "${baseUrl}find/v1.1"

    private val recommend: String = "${baseUrl}recommend/v1"

    val regions: String = "${baseUrl}find/v2/regions"

    val feedback: String = "${baseUrl}feedback/v1"

    fun find(geolocation: GeolocationParam? = null): String = geolocation?.let {
        with(it) { "$find?lat=$lat?lon=$lon?dist=$dist" }
    } ?: find

    fun recommend(sku: String) = "$recommend/$sku"
}
