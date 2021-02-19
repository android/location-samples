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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

private const val DATABASE_NAME = "sleep_segments_database"

/**
 * Stores all sleep segment data.
 */
@Database(
    entities = [SleepSegmentEventEntity::class, SleepClassifyEventEntity::class],
    version = 3,
    exportSchema = false
)
abstract class SleepDatabase : RoomDatabase() {

    abstract fun sleepSegmentEventDao(): SleepSegmentEventDao
    abstract fun sleepClassifyEventDao(): SleepClassifyEventDao

    companion object {
        // For Singleton instantiation
        @Volatile
        private var INSTANCE: SleepDatabase? = null

        fun getDatabase(context: Context): SleepDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context,
                    SleepDatabase::class.java,
                    DATABASE_NAME
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    // Migration is not part of this sample.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
