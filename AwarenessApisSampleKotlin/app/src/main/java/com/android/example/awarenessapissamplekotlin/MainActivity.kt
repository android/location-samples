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
package com.android.example.awarenessapissamplekotlin

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.android.example.awarenessapissamplekotlin.databinding.ActivityMainBinding
import com.google.android.gms.awareness.Awareness
import com.google.android.gms.awareness.snapshot.DetectedActivityResponse
import com.google.android.gms.common.util.PlatformVersion
import com.google.android.gms.location.ActivityRecognitionResult
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

/**
 * Demonstrates requesting/displaying a Awareness Snapshot.
 * NOTE: The Awareness APIs (both Snapshot and Fend) require an API key in your manifest. Check
 * this link for more information:
 * https://developers.google.com/awareness/android-api/get-a-key
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    fun onClickSnapshot(view: View) {
        Log.d(TAG, "onClickSnapshot()")

        if (activityRecognitionPermissionApproved()) {
            requestSnapshot()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACTIVITY_RECOGNITION)
        }
    }

    // Permission is checked in onClickSnapshot before this method is called.
    @SuppressLint("MissingPermission")
    private fun requestSnapshot() {
        Log.d(TAG, "requestSnapshot()")

        val task = Awareness.getSnapshotClient(this).detectedActivity

        task.addOnCompleteListener { taskResponse ->
            if (taskResponse.isSuccessful) {
                val detectedActivityResponse = taskResponse.result
                val activityRecognitionResult = detectedActivityResponse.activityRecognitionResult
                Log.d(TAG, "Snapshot successfully retrieved: $activityRecognitionResult")
                printSnapshotResult(detectedActivityResponse.activityRecognitionResult)
            } else {
                Log.d(TAG, "Data was not able to be retrieved: ${taskResponse.exception}")
            }
        }
    }

    private fun activityRecognitionPermissionApproved(): Boolean {
        // Permission check for 29+.
        return if (PlatformVersion.isAtLeastQ()) {
            PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACTIVITY_RECOGNITION
            )
        } else {
            true
        }
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted && PlatformVersion.isAtLeastQ()) {
                // Permission denied on Android platform that supports runtime permissions.
                displayPermissionSettingsSnackBar()
            } else {
                // Permission was granted (either by approval or Android version below Q).
                binding.output.text = getString(R.string.permission_approved)
            }
        }

    private fun displayPermissionSettingsSnackBar() {
        Snackbar.make(
            binding.mainActivity,
            R.string.permission_rational,
            Snackbar.LENGTH_LONG
        )
            .setAction(R.string.action_settings) {
                // Build intent that displays the App settings screen.
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts(
                    "package",
                    BuildConfig.APPLICATION_ID,
                    null
                )
                intent.data = uri
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
            }
            .show()
    }

    private fun printSnapshotResult(activityRecognitionResult: ActivityRecognitionResult) {
        val timestamp = Calendar.getInstance().time.toString()
        val output = "Current Snapshot ($timestamp):\n\n$activityRecognitionResult"
        binding.output.text = output
    }

    companion object {
        const val TAG = "MainActivity"
    }
}
