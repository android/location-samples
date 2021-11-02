/*
 * Copyright (C) 2021 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.locationaddress.data

import android.location.Geocoder
import android.location.Location
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

data class FormattedAddress(val display: String)

/** Provides an API to search for addresses from a [Location]. */
class GeocodingApi @Inject constructor(
    private val geocoder: Geocoder
) {
    // Geocoder specifically says that this call can use network and that it must not be called
    // from the main thread, so move it to the IO dispatcher.
    suspend fun getFromLocation(
        location: Location,
        maxResults: Int = 1
    ): List<FormattedAddress> = withContext(Dispatchers.IO) {
        try {
            val addresses = geocoder.getFromLocation(
                location.latitude,
                location.longitude,
                maxResults
            ) ?: emptyList()
            addresses.map { address ->
                FormattedAddress(
                    (0..address.maxAddressLineIndex)
                        .joinToString("\n") { address.getAddressLine(it) }
                )
            }
        } catch (e: IOException) {
            Log.w("GeocodingApi", "Error trying to get address from location.", e)
            emptyList()
        }
    }
}
