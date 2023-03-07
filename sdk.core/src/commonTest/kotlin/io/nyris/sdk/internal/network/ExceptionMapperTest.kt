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

import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.mockk.every
import io.mockk.mockk
import io.nyris.sdk.internal.network.find.FindResponseError
import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionMapperTest {
    private val findResponseError = FindResponseError(
        title = "title",
        status = 100,
        detail = "detail",
        traceId = "traceId"
    )

    @Test
    fun `toNyrisException should map FindResponseError to ResponseException`() {
        val responseException = findResponseError.toNyrisException()

        assertEquals(EXPECTED_EXCEPTION, responseException.toString())
    }

    @Test
    fun `toNyrisException should map ClientRequestException to ClientException`() {
        val clientRequestException = mockk<ClientRequestException>().apply {
            every { this@apply.message } returns "message"
        }

        val exception = clientRequestException.toNyrisException()

        assertEquals("message", exception.message)
    }

    @Test
    fun `toNyrisException should map ServerResponseException to ServerException`() {
        val serverException = mockk<ServerResponseException>().apply {
            every { this@apply.message } returns "message"
        }

        val exception = serverException.toNyrisException()

        assertEquals("message", exception.message)
    }
}

private const val EXPECTED_EXCEPTION = "ResponseException(" +
    "title=title, " +
    "status=100, " +
    "detail=detail, " +
    "traceId=traceId" +
    ")"
