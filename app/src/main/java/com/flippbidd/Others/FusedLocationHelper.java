package com.flippbidd.Others;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApi;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;


public class FusedLocationHelper implements GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private static final String TAG = FusedLocationHelper.class.getSimpleName();
    private final Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationUpdateListener mLocationUpdateListener;
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //Log.d(TAG, "onLocationChanged: " + location.toString());
            if (mLocationUpdateListener != null) {
                mLocationUpdateListener.onLocationUpdate(location);
            }
        }
    };
    // location request parameter
    private long mInterval;                  // time in millisecond
    private long mFastestInterval;           // time in millisecond
    private float mSmallestDisplacement;     // meter
    private int mPriority = LocationRequest.PRIORITY_HIGH_ACCURACY;
    private final GoogleApiClient.ConnectionCallbacks continuousRequest = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "ContinuousRequest : onConnected");
            startLocationUpdates();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "ContinuousRequest : onConnectionSuspended = " + cause);
        }
    };

    private final GoogleApiClient.ConnectionCallbacks singleRequest = new GoogleApiClient.ConnectionCallbacks() {
        @Override
        public void onConnected(Bundle bundle) {
            Log.d(TAG, "SingleRequest : onConnected");
            getLastKnownLocation();
        }

        @Override
        public void onConnectionSuspended(int cause) {
            Log.d(TAG, "SingleRequest : onConnectionSuspended = " + cause);
        }
    };

    public FusedLocationHelper(Context context) {
        this.mContext = context;
    }

    public static boolean isLocationEnable(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed : " + connectionResult.getErrorMessage());
    }

    private void setupGoogleApiClient(GoogleApiClient.ConnectionCallbacks connectionCallbacks) {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(connectionCallbacks)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();
    }

    public GoogleApiClient getmGoogleApiClient() {
        return mGoogleApiClient;
    }

    public FusedLocationHelper setInterval(long interval) {
        this.mInterval = interval;
        return this;
    }

    public FusedLocationHelper setFastestInterval(long fastestInterval) {
        this.mFastestInterval = fastestInterval;
        return this;
    }

    public FusedLocationHelper setSmallestDisplacement(float smallestDisplacement) {
        this.mSmallestDisplacement = smallestDisplacement;
        return this;
    }

    public FusedLocationHelper setPriority(int priority) {
        this.mPriority = priority;
        return this;
    }

    public FusedLocationHelper setLocationUpdateListener(LocationUpdateListener locationUpdateListener) {
        this.mLocationUpdateListener = locationUpdateListener;
        return this;
    }

    public void stop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * create location request
     *
     * @return instance of LocationRequest
     */
    private LocationRequest createLocationRequest() {
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(mInterval);
        mLocationRequest.setFastestInterval(mFastestInterval);
        mLocationRequest.setSmallestDisplacement(mSmallestDisplacement);
        mLocationRequest.setPriority(mPriority);
        return mLocationRequest;
    }

    public FusedLocationHelper requestSingleLocation() {
        // set google api client
        setupGoogleApiClient(singleRequest);
        return this;
    }

    private void getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastKnownLocation != null) {
            Log.d(TAG, "getLastKnownLocation : " + mLastKnownLocation.toString());
            if (mLocationUpdateListener != null) {
                mLocationUpdateListener.onLocationUpdate(mLastKnownLocation);
            }
            mGoogleApiClient.disconnect();
        } else {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.d(TAG, "getLastKnownLocation: onLocationChanged: " + location.toString());
                    if (mLocationUpdateListener != null) {
                        mLocationUpdateListener.onLocationUpdate(location);
                    }
//                    LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
                    stopLocationUpdates();
                    mGoogleApiClient.disconnect();
                }
            });
        }
    }

    public FusedLocationHelper requestContinuousLocation() {
        // set google api client
        setupGoogleApiClient(continuousRequest);
        return this;
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, createLocationRequest(), locationListener);
            pendingResult.setResultCallback(this);
        }catch (Exception s){
            Log.d(TAG, "PendingResult : " + s.getMessage());
        }

    }

    private void stopLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, locationListener);
            pendingResult.setResultCallback(this);
        }catch (Exception s){
            Log.d(TAG, "PendingResult : " + s.getMessage());
        }

    }

    @Override
    public void onResult(@NonNull Status status) {
        //Log.d(TAG, "PendingResult : " + status.getStatusMessage());
    }

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
    }
}
