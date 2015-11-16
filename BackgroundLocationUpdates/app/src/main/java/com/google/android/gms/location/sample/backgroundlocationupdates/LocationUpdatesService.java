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

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.util.LogPrinter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Interacts with FusedLocationProvider. Responsible for requesting updates, removing updates, and
 * processing location results. This is a bound and started service: when a client binds to the
 * service, it gets location updates in non-batched mode, and when the last client unbinds from the
 * service, it switches to getting updates in batched mode. For this example, only a single client,
 * {@link MainActivity} binds to this service. As a result, as long as {@code MainActivity} is in
 * the foreground, location updates are delivered in real time. When the activity is no longer in
 * the foreground, location updates are batched. When the activity returns to the foreground,
 * batched updates are flushed using
 * {@link com.google.android.gms.location.FusedLocationProviderApi#flushLocations(GoogleApiClient)}.
 *
 */

public class LocationUpdatesService extends Service implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    private static final String TAG = LocationUpdatesService.class.getSimpleName();

    /**
     * The amount of time to wait to confirm that this service is no longer bound to from a client.
     */
    public static final long ACTIVITY_GONE_CONFIRMATION_DURATION = 10 * 1000; // 10 seconds.

    protected static final String ACTION_INCOMING_LOCATION_UPDATE = BuildConfig.APPLICATION_ID +
            "action.INCOMING_LOCATION_UPDATE";

    // LocationRequest objects.
    private LocationRequest mBatchedLocationRequest;
    private LocationRequest mNonBatchedLocationRequest;

    public LocationUpdatesService() {
    }

    private IBinder mBinder = new MyBinder();

    /**
     * Handler for sending and processing Runnable objects.
     */
    private Handler mServiceHandler;

    /**
     * The entry point to Google Play services.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * Helper for tracking whether a calls to unbind are temporary (when the bound client changes
     * orientation, for example), or longer lasting.
     */
    private boolean mActivityReallyGone = false;

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "service bound");
        requestNonBatchedUpdates();
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
        Log.i(TAG, "service rebound");
        requestNonBatchedUpdates();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i(TAG, "Unbinding from service");
        mActivityReallyGone = true;
        // Wait to ensure that this method isn't immediately followed onRebind(). If the binding
        // client is really gone, switch to batched updates.
        mServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Check that the activity is still gone. If it is, switch to batched mode.
                if (mActivityReallyGone) {
                    if (Utils.requestingLocationUpdates(getApplicationContext())) {
                        googleApiClientBlockingConnect();
                        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                                getPendingIntent());
                        Log.i(TAG, "getting batched location updates");
                        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                                mBatchedLocationRequest, getPendingIntent());
                    }
                }
            }
        }, ACTIVITY_GONE_CONFIRMATION_DURATION);

        // Returning true allows for onRebind() to run when a client binds again.
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mGoogleApiClient.connect();

        mNonBatchedLocationRequest = Utils.createNonBatchedLocationRequest();
        mBatchedLocationRequest = Utils.createBatchedLocationRequest();

        HandlerThread thread = new HandlerThread(TAG);
        thread.start();
        mServiceHandler = new Handler(thread.getLooper());
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        final String action = intent.getAction();

        // Process incoming location updates.
        if (TextUtils.equals(ACTION_INCOMING_LOCATION_UPDATE, action)) {
            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    LocationResult result = LocationResult.extractResult(intent);
                    if (result != null) {
                        List<Location> locations = result.getLocations();
                        Gson gson = new Gson();
                        ArrayList<LatLng> allLatLngs = new ArrayList<>();
                        SharedPreferences sp = getSharedPreferences(BuildConfig.APPLICATION_ID,
                                Context.MODE_PRIVATE);
                        String previousLatLngString = sp.getString(Constants.KEY_LOCATION_DATA, "");
                        if (!TextUtils.isEmpty(previousLatLngString)) {
                            ArrayList<LatLng> previousLatLngArrayList = new Gson().fromJson(
                                    previousLatLngString,
                                    new TypeToken<ArrayList<LatLng>>() {
                                    }.getType());
                            allLatLngs.addAll(previousLatLngArrayList);
                        }
                        for (Location location : locations) {
                            location.dump(new LogPrinter(Log.DEBUG, TAG), TAG);
                            allLatLngs.add(new LatLng(location.getLatitude(),
                                    location.getLongitude()));
                        }
                        sp.edit().putString(Constants.KEY_LOCATION_DATA,
                                gson.toJson(allLatLngs)).apply();
                    }
                }
            });
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "destroying service");
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                googleApiClientBlockingConnect();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                        getPendingIntent());
                mGoogleApiClient.disconnect();
            }
        });
        super.onDestroy();
    }


    @Override
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "GoogleApiClient onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "GoogleApiClient connection suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "GoogleApiClient connection failed");
    }

    /**
     * Returns the PendingIntent associated with this service. Used when requesting or removing
     * location updates.
     */
    private PendingIntent getPendingIntent() {
        Intent intent = new Intent(this, this.getClass());
        intent.setAction(ACTION_INCOMING_LOCATION_UPDATE);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected void requestNonBatchedUpdates() {
        mActivityReallyGone = false;
        if (Utils.requestingLocationUpdates(getApplicationContext())) {
            mServiceHandler.post(new Runnable() {
                @Override
                public void run() {
                    googleApiClientBlockingConnect();
                    // We may have been getting batched updates before. We flush batched locations
                    // and switch to non-batch mode.
                    LocationServices.FusedLocationApi.flushLocations(mGoogleApiClient);
                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                            getPendingIntent());

                    Log.i(TAG, "getting non-batched location updates");
                    LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                            mNonBatchedLocationRequest, getPendingIntent());
                }
            });
        }
    }

    protected void removeUpdates() {
        mServiceHandler.post(new Runnable() {
            @Override
            public void run() {
                googleApiClientBlockingConnect();
                LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,
                        getPendingIntent());
                Log.i(TAG, "stopping service");
                stopSelf();
            }
        });
    }

    private void googleApiClientBlockingConnect() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.blockingConnect();
        }
    }

    public class MyBinder extends Binder {
        LocationUpdatesService getService() {
            return LocationUpdatesService.this;
        }
    }
}