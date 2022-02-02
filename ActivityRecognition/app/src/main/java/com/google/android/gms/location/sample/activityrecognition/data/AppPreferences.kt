/*
 * Copyright 2022 Google, Inc
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

package com.google.android.gms.location.sample.activityrecognition.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AppPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    val isActivityTransitionUpdatesTurnedOn = dataStore.data.map {
        it[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] ?: false
    }

    suspend fun setActivityTransitionUpdatesTurnedOn(isOn: Boolean) {
        dataStore.edit {
            it[ACTIVITY_TRANSITION_UPDATES_TURNED_ON] = isOn
        }
    }

    private companion object {
        val ACTIVITY_TRANSITION_UPDATES_TURNED_ON =
            booleanPreferencesKey("activity_transition_updates_on")
    }
}
