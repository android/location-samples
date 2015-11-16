/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.location.sample.backgroundlocationupdates;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * The fragment responsible for starting and stopping location updates and displaying location data
 * on a Google map.
 */
public class MainActivityFragment extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        SharedPreferences.OnSharedPreferenceChangeListener, OnMapReadyCallback {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

    private static final String KEY_SERVICE_BOUND = "service-bound";
    private static final String KEY_LAST_KNOWN_LOCATION = "last-known-location";

    private static final float DEFAULT_MAP_ZOOM_LEVEL = 17;

    // UI Widgets.
    private Button mStartUpdatesButton;
    private Button mStopUpdatesButton;

    /**
     * The entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Location request used when requesting updates in batched mode.  This is done when the
     * there are clients bound to {@link LocationUpdatesService}. Updates are created as requested,
     * but their delivery is delayed and multiple locations are delivered in batch.
     */
    private LocationRequest mBatchedLocationRequest;

    /**
     * Location request used when requesting updates in non-batch mode, when the activity containing
     * this fragment is in the foreground.
     */
    private LocationRequest mNonBatchedLocationRequest;

    /**
     * Keeps track of the last location available, which may or may not be the device's current
     * location.
     */
    private Location mLastKnownLocation;

    /**
     * Service responsible for requesting, removing, and processing location updates.
     */
    LocationUpdatesService mLocationUpdatesService;

    /**
     * Tracks whether LocationUpdatesService is bound or not.
     */
    boolean mServiceBound = false;

    /**
     * Entry point to all methods related to the Google Maps Android API.
     */
    GoogleMap mGoogleMap;

    /**
     * Monitors state of LocationUpdatesService.
     */
    private ServiceConnection mServiceConnection;

    /**
     * The map component in this sample.
     */
    SupportMapFragment mSupportMapFragment;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Monitors state of LocationUpdatesService.
        mServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                LocationUpdatesService.MyBinder binder = (LocationUpdatesService.MyBinder) service;
                mLocationUpdatesService = binder.getService();
                mServiceBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mLocationUpdatesService = null;
                mServiceBound = false;
            }
        };
        mNonBatchedLocationRequest = Utils.createNonBatchedLocationRequest();
        mBatchedLocationRequest = Utils.createBatchedLocationRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Add the map fragment to the main fragment.
        mSupportMapFragment = new SupportMapFragment();
        getChildFragmentManager().beginTransaction().add(R.id.google_map_container,
                mSupportMapFragment).commit();
        mSupportMapFragment.getMapAsync(this);

        mStartUpdatesButton = (Button) view.findViewById(R.id.start_updates_button);
        mStartUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Utils.requestingLocationUpdates(getActivity())) {
                    checkSettingsAndRequestUpdates();
                }
            }
        });

        mStopUpdatesButton = (Button) view.findViewById(R.id.stop_updates_button);
        mStopUpdatesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.setRequestingLocationUpdates(getActivity(), false);
                setButtonsEnabledState();
                if (mServiceBound) {
                    mLocationUpdatesService.removeUpdates();
                } else {
                    Log.e(TAG, "Cannot remove updates because LocationUpdatesService is not bound");
                }
            }
        });

        setButtonsEnabledState();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Intent intent = new Intent(getActivity(), LocationUpdatesService.class);
        getActivity().bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
        mServiceBound = true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onStop() {
        if (mServiceBound) {
            getActivity().unbindService(mServiceConnection);
            mServiceBound = false;
        }
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mServiceBound = savedInstanceState.getBoolean(KEY_SERVICE_BOUND, false);
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LAST_KNOWN_LOCATION);
        }
    }

    /**
     * Uses the {@link com.google.android.gms.location.SettingsApi} to determine of device's current
     * settings are adequate for using the {@link LocationRequest}s specified in this sample.
     * Initiates location updates once settings are found to be adequate.
     */
    private void checkSettingsAndRequestUpdates() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mBatchedLocationRequest)
                .addLocationRequest(mNonBatchedLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.i(TAG, "Location settings are satisfied");
                        requestUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.i(TAG, "Location settings are not satisfied. Resolution required.");
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    getActivity(),
                                    Constants.REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.i(TAG, "Location settings are not satisfied. However, we have no way " +
                                "to fix the settings, and we won't show the dialog.");
                        break;
                }
            }
        });
    }

    /**
     * Clears previous location data and requests location updates.
     */
    protected void requestUpdates() {
        // Clear previously stored location data.
        getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                .edit()
                .putString(Constants.KEY_LOCATION_DATA, "")
                .apply();
        mGoogleMap.clear();

        Utils.setRequestingLocationUpdates(getActivity(), true);
        setButtonsEnabledState();

        // Start the bound service. That way, the service keeps running even if no client is bound
        // to it.
        Intent intent = new Intent(getActivity(), LocationUpdatesService.class);
        getActivity().startService(intent);

        if (mServiceBound) {
            mLocationUpdatesService.requestNonBatchedUpdates();
        } else {
            Log.e(TAG, "Cannot request updates because LocationUpdatesService is not bound");
        }
    }

    /**
     * Sets the enabled state of buttons used to start and stop location updates.
     */
    private void setButtonsEnabledState() {
        if (Utils.requestingLocationUpdates(getActivity())) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save state in the Bundle.
        outState.putBoolean(KEY_SERVICE_BOUND, mServiceBound);
        outState.putParcelable(KEY_LAST_KNOWN_LOCATION, mLastKnownLocation);

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient connected");
        if (mLastKnownLocation == null) {
            mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastKnownLocation != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(mLastKnownLocation.getLatitude(),
                                mLastKnownLocation.getLongitude()), DEFAULT_MAP_ZOOM_LEVEL));
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // For simplicity, we don't handle connection failure thoroughly in this sample. Refer to
        // the following Google Play services doc for more details:
        // http://developer.android.com/google/auth/api-client.html
        Log.i(TAG, "connection to GoogleApiClient failed");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(TAG, "GoogleApiClient connection suspended: "
                + connectionSuspendedCauseToString(cause));
    }

    private static String connectionSuspendedCauseToString(int cause) {
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                return "CAUSE_NETWORK_LOST";
            case CAUSE_SERVICE_DISCONNECTED:
                return "CAUSE_SERVICE_DISCONNECTED";
            default:
                return "CAUSE_UNKNOWN: " + cause;
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (TextUtils.equals(key, Constants.KEY_LOCATION_DATA)) {
            ArrayList<LatLng> latLngArrayList = Utils.getStoredLocationData(getActivity());
            if (!latLngArrayList.isEmpty()) {
                LatLng latLng = latLngArrayList.get(latLngArrayList.size() - 1);
                plotCircle(latLng);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mGoogleMap = map;
        Log.i(TAG, "map is ready");
        ArrayList<LatLng> latLngArrayList = Utils.getStoredLocationData(getActivity());
        for (LatLng latLng : latLngArrayList) {
            plotCircle(latLng);
        }
    }

    /**
     * Plots a small circle on the Google map for the given location.
     *
     * @param latLng The latitude and longitude coordinates.
     */
    private void plotCircle(LatLng latLng) {
        mGoogleMap.addCircle(new CircleOptions()
                .center(latLng)
                .radius(3)
                .fillColor(Color.RED)
                .strokeColor(Color.RED));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_MAP_ZOOM_LEVEL));
    }
}