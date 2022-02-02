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

package com.google.android.gms.location.sample.activityrecognition.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for managing [ActivityTransitionRecord]s in the database.
 */
@Dao
interface ActivityTransitionDao {

    @Insert
    suspend fun insert(records: List<ActivityTransitionRecord>)

    @Query("DELETE FROM ActivityTransitionRecord")
    suspend fun deleteAll()

    @Query("SELECT * FROM ActivityTransitionRecord ORDER BY timestamp DESC LIMIT 20")
    fun getMostRecent(): Flow<List<ActivityTransitionRecord>>
}
