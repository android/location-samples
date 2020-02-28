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

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.checkSelfPermission
import androidx.fragment.app.Fragment

import com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.databinding.FragmentPermissionRequestBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "PermissionRequestFrag"

/**
 * Displays information about why a user should enable either the FINE location permission or the
 * background location permission (depending on what is needed).
 *
 * Allows users to grant the permissions as well.
 */
class PermissionRequestFragment : Fragment() {

    // Set by Activity for which type of permission to request (Fine or background).
    private var permissionRequestType: PermissionRequestType? = null

    private lateinit var binding: FragmentPermissionRequestBinding

    private var activityListener: Callbacks? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequestType =
            arguments?.getSerializable(ARG_PERMISSION_REQUEST_TYPE) as PermissionRequestType
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentPermissionRequestBinding.inflate(inflater, container, false)

        when (permissionRequestType) {
            PermissionRequestType.FINE_LOCATION ->
                binding.explanationTextView.text =
                    getString(R.string.fine_location_access_rationale_text)

            PermissionRequestType.BACKGROUND_LOCATION ->
                binding.explanationTextView.text =
                    getString(R.string.background_location_access_rationale_text)
        }

        binding.permissionRequestButton.setOnClickListener {
            when (permissionRequestType) {
                PermissionRequestType.FINE_LOCATION ->
                    requestFineLocationPermission()

                PermissionRequestType.BACKGROUND_LOCATION ->
                    TODO("Add system background permission request.")
            }
        }

        return binding.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is Callbacks) {
            activityListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()

        activityListener = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive an empty array.
                    Log.d(TAG, "User interaction was cancelled.")

                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    activityListener?.displayLocationUI()

                else -> {

                    Snackbar.make(
                        binding.frameLayout,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
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
            }
        }
    }

    private fun fineLocationPermissionApproved(): Boolean {

        val context = context ?: return false

        return PackageManager.PERMISSION_GRANTED == checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private fun requestFineLocationPermission() {

        if (fineLocationPermissionApproved()) {
            activityListener?.displayLocationUI()

        } else {

            val provideRationale = shouldShowRequestPermissionRationale(
                Manifest.permission.ACCESS_FINE_LOCATION
            )

            // If the user denied a previous request, but didn't check "Don't ask again", provide
            // additional rationale.
            if (provideRationale) {
                Snackbar.make(
                    binding.frameLayout,
                    R.string.simple_permission_rationale,
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {
                        requestPermissions(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                        )
                    }
                    .show()
            } else {
                Log.d(TAG, "Request fine location permission")
                requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE
                )
            }
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface Callbacks {
        fun displayLocationUI()
    }

    companion object {
        private const val ARG_PERMISSION_REQUEST_TYPE =
            "com.google.android.gms.location.sample.locationupdatesbackgroundkotlin.PERMISSION_REQUEST_TYPE"

        private const val REQUEST_FINE_LOCATION_PERMISSIONS_REQUEST_CODE = 34

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param permissionRequestType Type of permission you would like to request.
         * @return A new instance of fragment PermissionRequestFragment.
         */
        @JvmStatic
        fun newInstance(permissionRequestType: PermissionRequestType) =
            PermissionRequestFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PERMISSION_REQUEST_TYPE, permissionRequestType)
                }
            }
    }
}

enum class PermissionRequestType {
    FINE_LOCATION, BACKGROUND_LOCATION
}
