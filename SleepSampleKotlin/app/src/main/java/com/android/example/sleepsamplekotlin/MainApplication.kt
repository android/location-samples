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
package com.android.example.sleepsamplekotlin

import android.app.Application
import androidx.datastore.preferences.createDataStore
import com.android.example.sleepsamplekotlin.data.SleepRepository
import com.android.example.sleepsamplekotlin.data.datastore.SLEEP_PREFERENCES_NAME
import com.android.example.sleepsamplekotlin.data.datastore.SleepSubscriptionStatus
import com.android.example.sleepsamplekotlin.data.db.SleepDatabase

/**
 * Sets up repository for all sleep data.
 */
class MainApplication : Application() {
    // Both database and repository use lazy so they aren't created when the app starts, but only
    // when repository is first needed.
    private val database by lazy {
        SleepDatabase.getDatabase(applicationContext)
    }

    val repository by lazy {
        SleepRepository(
            sleepSubscriptionStatus = SleepSubscriptionStatus(
                applicationContext.createDataStore(name = SLEEP_PREFERENCES_NAME)),
            sleepSegmentEventDao = database.sleepSegmentEventDao(),
            sleepClassifyEventDao = database.sleepClassifyEventDao()
        )
    }
}
