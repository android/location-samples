/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.example.sleepsamplekotlin.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

const val SLEEP_PREFERENCES_NAME = "sleep_preferences"

/**
 * Saves the sleep data subscription status into a [DataStore].
 * Used to check if the app is still listening to changes in sleep data when the app is brought
 * back into the foreground.
 */
class SleepSubscriptionStatus(private val dataStore: DataStore<Preferences>) {

    private object PreferencesKeys {
        val SUBSCRIBED_TO_SLEEP_DATA = booleanPreferencesKey("subscribed_to_sleep_data")
    }
    // Observed Flow will notify the observer when the the sleep subscription status has changed.
    val subscribedToSleepDataFlow: Flow<Boolean> = dataStore.data.map { preferences ->
        // Get the subscription value, defaults to false if not set:
        preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] ?: false
    }

    // Updates subscription status.
    suspend fun updateSubscribedToSleepData(subscribedToSleepData: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SUBSCRIBED_TO_SLEEP_DATA] = subscribedToSleepData
        }
    }
}
