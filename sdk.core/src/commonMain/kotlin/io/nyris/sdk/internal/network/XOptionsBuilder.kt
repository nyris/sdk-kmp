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

internal class XOptionsBuilder {
    private var limit: Int? = null
    private var threshold: Float? = null

    fun limit(limit: Int?) = apply {
        this.limit = limit
    }

    fun threshold(threshold: Float?) = apply {
        this.threshold = threshold
    }

    fun build(): String? = StringBuilder().apply {
        if (limit != null) {
            append("limit=$limit ")
        }
        if (threshold != null) {
            append("threshold=$threshold ")
        }
    }.toString().takeIf { xOptions ->
        xOptions.isNotEmpty()
    }.also {
        reset()
    }

    private fun reset() {
        limit = null
        threshold = null
    }
}
