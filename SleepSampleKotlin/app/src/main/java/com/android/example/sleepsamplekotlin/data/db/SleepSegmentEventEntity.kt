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
package com.android.example.sleepsamplekotlin.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.SleepSegmentEvent

/**
 * Data class for Sleep Segment Events which represents the time the user slept at night.
 */
@Entity(tableName = "sleep_segment_events_table")
data class SleepSegmentEventEntity(
    @PrimaryKey
    @ColumnInfo(name = "start_time_millis")
    val startTimeMillis: Long,

    @ColumnInfo(name = "end_time_millis")
    val endTimeMillis: Long,

    @ColumnInfo(name = "status")
    val status: Int
) {
    companion object {
        fun from(sleepSegmentEvent: SleepSegmentEvent): SleepSegmentEventEntity {
            return SleepSegmentEventEntity(
                startTimeMillis = sleepSegmentEvent.startTimeMillis,
                endTimeMillis = sleepSegmentEvent.endTimeMillis,
                status = sleepSegmentEvent.status
            )
        }
    }
}
