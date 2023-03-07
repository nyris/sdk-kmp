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

import io.ktor.http.HttpHeaders
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertEquals

class ApiHeadersTest {
    private val userAgent = mockk<UserAgent>().apply {
        every { this@apply.toString() } returns USER_AGENT
    }

    private val classToTest: ApiHeaders by lazy {
        ApiHeaders(API_KEY, userAgent)
    }

    @Test
    fun `default should contains the correct headers`() {
        with(classToTest) {
            assertEquals(EXPECTED_HEADERS_SIZE, default.size)
            assertEquals(API_KEY, default["X-Api-Key"])
            assertEquals(USER_AGENT, default[HttpHeaders.UserAgent])
        }

        verify { userAgent.toString() }
        confirmVerified(userAgent)
    }
}

private const val API_KEY = "API_KEY"
private const val USER_AGENT = "USER_AGENT"

private const val EXPECTED_HEADERS_SIZE = 2
