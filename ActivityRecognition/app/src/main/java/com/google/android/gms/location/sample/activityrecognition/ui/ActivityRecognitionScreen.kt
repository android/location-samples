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

package com.google.android.gms.location.sample.activityrecognition.ui

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.sample.activityrecognition.R
import com.google.android.gms.location.sample.activityrecognition.data.db.ActivityTransitionRecord
import com.google.android.gms.location.sample.activityrecognition.data.db.DetectedActivityType.IN_VEHICLE
import com.google.android.gms.location.sample.activityrecognition.data.db.DetectedActivityType.STILL
import com.google.android.gms.location.sample.activityrecognition.data.db.DetectedActivityType.WALKING
import com.google.android.gms.location.sample.activityrecognition.data.db.DetectedTransitionType.ENTER
import com.google.android.gms.location.sample.activityrecognition.ui.theme.ActivityRecognitionTheme
import java.time.Duration
import java.time.Instant

/** The main UI of the app when Google Play Services is available. */
@Composable
fun ActivityRecognitionScreen(
    isActivityUpdatesTurnedOn: Boolean,
    showDegradedExperience: Boolean,
    needsPermissionRationale: Boolean,
    onButtonClick: () -> Unit,
    transitionEvents: List<ActivityTransitionRecord>
) {
    var showRationaleDialog by remember { mutableStateOf(false) }
    if (showRationaleDialog) {
        PermissionRationaleDialog(
            onConfirm = {
                showRationaleDialog = false
                onButtonClick()
            },
            onDismiss = { showRationaleDialog = false }
        )
    }

    fun onClick() {
        if (needsPermissionRationale) {
            showRationaleDialog = true
        } else {
            onButtonClick()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val buttonText = if (isActivityUpdatesTurnedOn) {
            stringResource(id = R.string.stop_activity_recognition)
        } else {
            stringResource(id = R.string.start_activity_recognition)
        }
        Button(onClick = { onClick() }) {
            Text(text = buttonText)
        }

        if (showDegradedExperience) {
            Text(
                text = stringResource(id = R.string.please_allow_permission),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        } else if (!isActivityUpdatesTurnedOn) {
            Text(
                text = stringResource(id = R.string.not_started),
                style = MaterialTheme.typography.h6,
                textAlign = TextAlign.Center
            )
        } else {
            RecentEventsList(transitionEvents)
        }
    }
}

@Composable
fun RecentEventsList(transitionEvents: List<ActivityTransitionRecord>) {
    if (transitionEvents.isEmpty()) {
        Text(
            text = stringResource(id = R.string.waiting_for_events),
            style = MaterialTheme.typography.h6,
            textAlign = TextAlign.Center
        )
    } else {
        val now = System.currentTimeMillis()
        LazyColumn(modifier = Modifier.widthIn(max = 360.dp)) {
            items(transitionEvents) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = it.activityType.name,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = DateUtils.getRelativeTimeSpanString(
                            it.timestamp.toEpochMilli(),
                            now,
                            DateUtils.SECOND_IN_MILLIS
                        ).toString()
                    )
                }
            }
        }
    }
}

@Composable
fun PermissionRationaleDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(id = R.string.permission_rationale_dialog_title))
        },
        text = {
            Text(text = stringResource(id = R.string.permission_rationale_dialog_message))
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun LocationUpdatesScreenPreview() {
    val now = Instant.now()
    val previewData = listOf(
        ActivityTransitionRecord(
            activityType = IN_VEHICLE,
            transitionType = ENTER,
            timestamp = now.minus(Duration.ofSeconds(10))
        ),
        ActivityTransitionRecord(
            activityType = WALKING,
            transitionType = ENTER,
            timestamp = now.minus(Duration.ofMinutes(7))
        ),
        ActivityTransitionRecord(
            activityType = STILL,
            transitionType = ENTER,
            timestamp = now.minus(Duration.ofHours(2))
        ),
    )
    ActivityRecognitionTheme {
        ActivityRecognitionScreen(
            isActivityUpdatesTurnedOn = true,
            showDegradedExperience = false,
            needsPermissionRationale = false,
            onButtonClick = {},
            transitionEvents = previewData
        )
    }
}
