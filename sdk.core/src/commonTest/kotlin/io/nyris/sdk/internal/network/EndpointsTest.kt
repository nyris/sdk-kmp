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
import kotlin.test.Test
import kotlin.test.assertEquals

class EndpointsTest {
    private val endpoints: Endpoints by lazy {
        Endpoints(ANY_BASE_URL)
    }

    @Test
    fun `find should build the correct find endpoint when geolocation is null`() {
        val findEndpoint = endpoints.find()

        assertEquals(EXPECTED_FIND_ENDPOINT, findEndpoint)
    }

    @Test
    fun `find should build the correct find endpoint when geolocation is not null`() {
        val findEndpoint = endpoints.find(
            GeolocationParam(lat = 1.1F, lon = 1.2F, dist = 1000)
        )

        assertEquals(EXPECTED_FIND_ENDPOINT_WITH_GEOLOCATION, findEndpoint)
    }

    @Test
    fun `regions should return the correct endpoint`() {
        assertEquals(EXPECTED_REGIONS, endpoints.regions)
    }

    @Test
    fun `feedback should return the correct endpoint`() {
        assertEquals(EXPECTED_FEEDBACK, endpoints.feedback)
    }
}

private const val ANY_BASE_URL = "ANY_BASE_URL/"
private const val EXPECTED_FIND_ENDPOINT = "${ANY_BASE_URL}find/v1.1"
private const val EXPECTED_FIND_ENDPOINT_WITH_GEOLOCATION = "${ANY_BASE_URL}find/v1.1?lat=1.1?lon=1.2?dist=1000"
private const val EXPECTED_REGIONS = "${ANY_BASE_URL}find/v2/regions"
private const val EXPECTED_FEEDBACK = "${ANY_BASE_URL}feedback/v1"
