/*
 * Copyright (C) 2021 Google Inc.
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

package com.google.android.gms.location.sample.foregroundlocation.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Data store which holds preferences for the app.
 */
class LocationPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val isLocationTurnedOn = dataStore.data.map {
        it[locationOnKey] ?: false
    }

    suspend fun setLocationTurnedOn(isStarted: Boolean) = withContext(Dispatchers.IO) {
        dataStore.edit {
            it[locationOnKey] = isStarted
        }
    }

    private companion object {
        val locationOnKey = booleanPreferencesKey("is_location_on")
    }
}
