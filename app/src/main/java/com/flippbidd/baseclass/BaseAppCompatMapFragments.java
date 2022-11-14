package com.flippbidd.baseclass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.Others.FusedLocationHelper;
import com.flippbidd.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.MapView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public abstract class BaseAppCompatMapFragments extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    protected static final float DEFAULT_ZOOM_LEVEL = 10;
    protected static final float DEFAULT_ZOOM_LEVEL_MIN = 6;
    protected static final float DEFAULT_ZOOM_LEVEL_MAX = 16;
    private static final int REQUEST_CODE_LOCATION_PERMISSION = 111;
    private static final int REQUEST_CODE_PERMISSION_SETTING = 222;
    private static final int REQUEST_CODE_LOCATION_SETTING = 333;
    private MapView mMapView;
    private CustomAppCompatButton mBtnLeft, mBtnRight;
    protected BaseAppCompatActivity mBaseAppCompatActivity;


    @BindView(R.id.mSimpleRelativeLayout)
    RelativeLayout mSimpleRelativeLayout;
    //
    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseAppCompatActivity = (BaseAppCompatActivity) context;
    }

    public void showProgressDialog(boolean isShow) {
        if (mSimpleRelativeLayout != null) {
            if (isShow)
                mSimpleRelativeLayout.setVisibility(View.VISIBLE);
            else
                mSimpleRelativeLayout.setVisibility(View.GONE);
        }
    }

    public void hideProgressDialog() {
        if (mSimpleRelativeLayout != null) {
            mSimpleRelativeLayout.setVisibility(View.GONE);
        }
    }

    public void showToast(String resource) {
        Toast.makeText(getActivity(), resource, Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        unbinder = ButterKnife.bind(this, view);
        mMapView = (MapView) view.findViewById(getMapViewId());
        mBtnLeft = (CustomAppCompatButton) view.findViewById(getBtnFirstViewId());
        mBtnRight = (CustomAppCompatButton) view.findViewById(getBtnSecondViewId());
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
            setUpGoogleMap(mMapView);
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
    }


    protected abstract int getLayoutResource();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    public boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean shouldShowLocationRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true;
        }
        return false;
    }

    public void requestLocationPermission() {
        if (shouldShowLocationRationale()) {
            showPermissionRationale();
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    if (shouldShowLocationRationale()) {
                        showPermissionRationale();
                    } else {
                        onPermissionDenied();
                    }
                }
                break;
        }
    }

    protected void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Settings")
                .setMessage("Open setting and enable permission.")
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + "cubtech.com.nufuel"));
                        getActivity().startActivityIfNeeded(intent, REQUEST_CODE_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    protected abstract void onPermissionGranted();

    /**
     * show rationale dialog if don't ask again checkbox not checked and denied permission
     */
    protected void showPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setTitle("Location Permission")
                .setMessage("Location permission is required to access your current location.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISSION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            if (hasLocationPermission()) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_SETTING) {
            if (isLocationEnabled()) {
                onLocationEnable();
            }
        }
    }


    protected abstract void onLocationEnable();

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getActivity().getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    /*  public void openLocationRequestDialog() {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                  .setTitle("Location Request")
                  .setMessage("Enable your location.")
                  .setCancelable(false)
                  .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                          startActivityIfNeeded(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_LOCATION_SETTING);
                      }
                  })
                  .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                      }
                  });
          builder.show();
      }*/
    public void openLocationRequestDialog(FusedLocationHelper fusedLocationHelper) {


        if (!fusedLocationHelper.isLocationEnable(mBaseAppCompatActivity)) {

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(10000 / 2);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(fusedLocationHelper.getmGoogleApiClient(), builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.i("TAG", "All location settings are satisfied.");
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            Log.i("TAG", "Location settings are not satisfied. Show the user a dialog to upgrade location settings ");

                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the result
                                // in onActivityResult().
                                status.startResolutionForResult(mBaseAppCompatActivity, REQUEST_CODE_LOCATION_SETTING);
                            } catch (IntentSender.SendIntentException e) {
                                Log.i("TAG", "PendingIntent unable to execute request.");
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.i("TAG", "Location settings are inadequate, and cannot be fixed here. Dialog not created.");
                            break;
                    }
                }
            });
        }

    }

    protected abstract int getMapViewId();

    protected abstract int getBtnFirstViewId();

    protected abstract int getBtnSecondViewId();

    public abstract void setUpGoogleMap(MapView mapView);

    public MapView getMapView() {
        return mMapView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mMapView != null) {
            //mMapView.onStart();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMapView != null) {
            //mMapView.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMapView != null) {
            mMapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }
}
