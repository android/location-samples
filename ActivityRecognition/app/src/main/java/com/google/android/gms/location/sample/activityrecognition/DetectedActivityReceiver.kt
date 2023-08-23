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

package com.google.android.gms.location.sample.activityrecognition

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.sample.activityrecognition.data.db.ActivityTransitionDao
import com.google.android.gms.location.sample.activityrecognition.data.db.asRecord
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetectedActivityReceiver : BroadcastReceiver() {

    @Inject lateinit var dao: ActivityTransitionDao

    private val coroutineScope = MainScope()

    override fun onReceive(context: Context, intent: Intent) {
        val result = ActivityTransitionResult.extractResult(intent) ?: return

        if (result.transitionEvents.isNotEmpty()) {
            coroutineScope.launch {
                dao.insert(
                    result.transitionEvents.map { it.asRecord() }
                )
            }
        }
    }
}
