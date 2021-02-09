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
package com.android.example.wear.sleepsamplekotlin.data

import com.android.example.wear.sleepsamplekotlin.data.db.SleepClassifyEventDao
import com.android.example.wear.sleepsamplekotlin.data.db.SleepClassifyEventEntity
import com.android.example.wear.sleepsamplekotlin.data.db.SleepSegmentEventDao
import com.android.example.wear.sleepsamplekotlin.data.db.SleepSegmentEventEntity
import kotlinx.coroutines.flow.Flow

class SleepRepository(
    private val sleepSegmentEventDao: SleepSegmentEventDao,
    private val sleepClassifyEventDao: SleepClassifyEventDao
) {

    // Methods for SleepSegmentEventDao
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allSleepSegmentEvents: Flow<List<SleepSegmentEventEntity>> =
        sleepSegmentEventDao.getAll()

    // By default Room runs suspend queries off the main thread. Therefore, we don't need to
    // implement anything else to ensure we're not doing long-running database work off the
    // main thread.
    suspend fun insertSleepSegment(sleepSegmentEventEntity: SleepSegmentEventEntity) {
        sleepSegmentEventDao.insert(sleepSegmentEventEntity)
    }

    // By default Room runs suspend queries off the main thread. Therefore, we don't need to
    // implement anything else to ensure we're not doing long-running database work off the
    // main thread.
    suspend fun insertSleepSegments(sleepSegmentEventEntities: List<SleepSegmentEventEntity>) {
        sleepSegmentEventDao.insertAll(sleepSegmentEventEntities)
    }

    // Methods for SleepClassifyEventDao
    // Observed Flow will notify the observer when the data has changed.
    val allSleepClassifyEvents: Flow<List<SleepClassifyEventEntity>> =
        sleepClassifyEventDao.getAll()

    suspend fun insertSleepClassifyEvent(sleepClassifyEventEntity: SleepClassifyEventEntity) {
        sleepClassifyEventDao.insert(sleepClassifyEventEntity)
    }

    suspend fun insertSleepClassifyEvents(sleepClassifyEventEntities: List<SleepClassifyEventEntity>) {
        sleepClassifyEventDao.insertAll(sleepClassifyEventEntities)
    }
}
