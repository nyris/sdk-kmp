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
package io.nyris.sdk

sealed class NyrisException(message: String?) : Throwable(message)

data class ResponseException(
    val title: String?,
    val status: Int?,
    val detail: String?,
    val traceId: String?,
    val itemKey: String?,
) : NyrisException(detail)

data class ClientException(
    override val message: String?,
) : NyrisException(message)

data class ServerException(
    override val message: String?,
) : NyrisException(message)
