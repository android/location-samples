/*
 * Copyright (C) 2020 Google Inc. All Rights Reserved.
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
package com.google.android.gms.location.sample.locationupdatesbackgroundkotlin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView



import com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.PermissionRequestFragment.Callbacks
import com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.databinding.ActivityMainBinding


/**
 * This app allows a user to track their location in the background.
 *
 * IMPORTANT NOTE: You should always prefer 'while-in-use' location tracking, i.e., track location
 * while the app is in use and create a foreground service (tied to a Notification) when the
 * user navigates away from the app.
 *
 * If you do have an approved use case for tracking location in the background, it will require an
 * additional permission.
 *
 * Note: Users have four options in Android 11+ regarding location:
 *
 *  * One time only
 *  * Allow while app is in use, i.e., while app is in foreground
 *  * Allow all the time
 *  * Not allow location at all
 *
 * Best practice requires you spread out your first fine/course request and your background request.
 */
class MainActivity : AppCompatActivity(), Callbacks {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)

        if (currentFragment == null) {

            val fragment = PermissionRequestFragment.newInstance(PermissionRequestType.FINE_LOCATION)

            supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun displayLocationUI() {
        TODO("Add redirect to main app fragment")
    }
}
