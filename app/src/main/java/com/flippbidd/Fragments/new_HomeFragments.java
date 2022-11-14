package com.flippbidd.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.BuildConfig;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonList.CommonListResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.PropertyStateWise.PropertyStateWiseResponse;
import com.flippbidd.Model.Response.PropertyStateWise.StatePropertyList;
import com.flippbidd.Model.Response.Service.ServiceListData;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.FusedLocationHelper;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.flippbidd.activity.Cluster.CustomeClusterRenderer;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseAppCompatMapFragments;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.bottomsheet.SelectionBottomSheetDialogFragment;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.algo.Algorithm;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;
import static com.flippbidd.Others.Constants.PRO_VERSION;

public class new_HomeFragments extends BaseAppCompatMapFragments implements RefreshListener,
        LocationListener, FusedLocationHelper.LocationUpdateListener,
        GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {

    private static String TAG = new_HomeFragments.class.getSimpleName();
    public static final int REQUEST_UPLOAD_FUND = 1009;

    GoogleMap mMap;

    //GPS TRACKING
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 5000; // 10 meters
    //    GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    public Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private FusedLocationHelper mFusedLocationHelper;
    private Location mCusrrentLocation;

    private long UPDATE_INTERVAL = 3 * 20000;  /* 10 secs */
    boolean isClick = false;
    private int isTypeId = 0;


    //new design code
    @BindView(R.id.relativeItemMainLayout)
    RelativeLayout relativeItemMainLayout;

    //service code
    @BindView(R.id.textViewServicePrice)
    CustomTextView textViewServicePrice;
    @BindView(R.id.textViewServiceTitle)
    CustomTextView textViewServiceTitle;
    @BindView(R.id.textViewService)
    CustomTextView textViewService;
    @BindView(R.id.textViewUsername)
    CustomTextView textViewUsername;
    @BindView(R.id.imgEdit)
    ImageView imgEdit;
    @BindView(R.id.imgDelete)
    ImageView imgDelete;
    @BindView(R.id.relativeLayoutActionbox)
    RelativeLayout relativeLayoutActionbox;
    @BindView(R.id.relativeLayoutActionboxsecond)
    RelativeLayout relativeLayoutActionboxsecond;
    @BindView(R.id.imgHeart)
    ImageView imgHeart;
    @BindView(R.id.imgMessage)
    ImageView imgMessage;

    //new design code
    @BindView(R.id.mConstraintLayoutServiceLayout)
    RelativeLayout mConstraintLayoutServiceLayout;
    @BindView(R.id.lottieServiceAnimationProgress)
    LottieAnimationView lottieServiceAnimationProgress;
    @BindView(R.id.service_image)
    ImageView service_image;
    @BindView(R.id.btnProperty)
    CustomAppCompatButton btnProperty;
    @BindView(R.id.btnRental)
    CustomAppCompatButton btnRental;
    @BindView(R.id.btnServices)
    CustomAppCompatButton btnServices;
    @BindView(R.id.txtDistanceMeter)
    CustomTextView txtDistanceMeter;
    @BindView(R.id.txtServiceDistanceMeter)
    CustomTextView txtServiceDistanceMeter;

    /*New design property Base*/
    @BindView(R.id.mConstraintLayoutPropertyUi)
    ConstraintLayout mConstraintLayoutPropertyUi;


    private String isScreenType = "", viewType = "", rangeby = "70000";
    private String mTextSortVaule = "newToold";//newToold
    Disposable disposable;
    private List<StatePropertyList> mListDataResponse = new ArrayList<>();
    private ImageLoader imageLoader;
    private String mScreenName = "property";
    int isSelectedPosition;
    View loView;
    List<MyItem> myItemList = new ArrayList<>();
    Algorithm<MyItem> myItems;
    CommonData mPropertyPinSelectedData;
    MediaPlayer mp;
    float mapView = 5f;

    /**
     * The amount by which to scroll the camera. Note that this amount is in raw pixels, not dp
     * (density-independent pixels).
     */
    private static final int SCROLL_BY_PX = 100;
    OnFragmentInteractionListener mListener;

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentCreated();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
            mListener.onFragmentCreated();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        mFusedLocationHelper = new FusedLocationHelper(getActivity())
                .requestSingleLocation()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setLocationUpdateListener(this);

    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loView = view;
        imageLoader = ImageLoader.getInstance();
        if (!hasLocationPermission()) {
            requestLocationPermission();
        } else {
            onPermissionGranted();
        }

        //set default layout
        colorChanges(btnProperty, btnRental, btnServices);
        viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_BUYER, Constants.SELECTION_HEADER_TITLE.SELECTE_SELLER, 0);
    }

    @OnClick({R.id.relativeItemMainLayout, R.id.relativeViewPagerBox})
    void detailsViewClickEvent() {
        if (mPropertyPinSelectedData != null && !mPropertyPinSelectedData.equals("")) {
            if (mPropertyPinSelectedData.getIsAvailable().equalsIgnoreCase("1")) {

                ((BaseAppCompatActivity) getActivity()).startActivityIfNeeded(PropertyDetails.getIntentActivity(((BaseAppCompatActivity) getActivity()), mPropertyPinSelectedData.getPropertyId(), mPropertyPinSelectedData.getData().getLoginId(), viewType, mPropertyPinSelectedData.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
            } else {
                if (mPropertyPinSelectedData.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                    ((BaseAppCompatActivity) getActivity()).startActivityIfNeeded(PropertyDetails.getIntentActivity(getActivity(), mPropertyPinSelectedData.getPropertyId(), mPropertyPinSelectedData.getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mPropertyPinSelectedData.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                } else {
                    showNotAvailable();
                }
            }
        } else {
            showNotAvailable();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_UNAVILABLE) {
                if (mListDataResponse != null && !mListDataResponse.equals("")) {
                    mPropertyPinSelectedData.setIsAvailable("0");
                }
                //update api call
                getFirstLatestProperty(false);
            }
            if (requestCode == REQUEST_UPLOAD_FUND) {
                //update api call
                getFirstLatestProperty(false);
                Log.e("TAG", "onActivityResult()");
            }
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(getActivity());
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    void viewLeftButtonSelected(String str1, String str2, int typeId) {
        //set default values
        isTypeId = typeId;
        //set default selected
        viewLeftButton();
    }


    void viewLeftButton() {

        if (isTypeId == 0) {
            //show hide view
            isScreenType = Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER;
            viewType = Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY;
            if (mCusrrentLocation != null) {
                callPropetyListApi(true);
            }
        } else if (isTypeId == 1) {
            isScreenType = Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL;
            viewType = Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL;
            if (mCusrrentLocation != null) {
//                callRentalListApi(false);
            }
        } else if (isTypeId == 2) {
            isScreenType = Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER;
            viewType = Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE;
            if (mCusrrentLocation != null) {
//                callServiceListApi(false);
            }
        }

    }


    //Permission
    @Override
    public void requestLocationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
        } else {
            initLocationService();
        }
    }


    private void initLocationService() {

        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        try {
            this.locationManager = (LocationManager) getActivity().getSystemService(getActivity().LOCATION_SERVICE);

            // Get GPS and network status
            this.isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            this.isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isNetworkEnabled && !isGPSEnabled) {
                // cannot get location
                this.locationServiceAvailable = false;
            }

            this.locationServiceAvailable = true;

            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        UPDATE_INTERVAL,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (mCusrrentLocation != null) {
                        //call update api
                    }
                }
            } else if (isGPSEnabled) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        UPDATE_INTERVAL,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (mCusrrentLocation != null) {
                        //call update api
                    }
                }
            }
        } catch (Exception ex) {
            Log.e("", ex.getMessage());

        }
    }


    @Override
    protected void onPermissionGranted() {
        if (!mFusedLocationHelper.isLocationEnable(getActivity())) {
            openLocationRequestDialog(mFusedLocationHelper);
        }
        if (mMap != null) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    protected void onLocationEnable() {
//        if (mFusedLocationHelper != null) {
//            mFusedLocationHelper.requestSingleLocation();
//        }
        Log.e("TAG", "onLocationEnable()");
    }

    @Override
    protected int getMapViewId() {
        return R.id.mapView;
    }

    @Override
    protected int getBtnFirstViewId() {
        return R.id.btnLeftSelected;
    }

    @Override
    protected int getBtnSecondViewId() {
        return R.id.btnRightseleced;
    }

    @Override
    public void setUpGoogleMap(final MapView mapView) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
//                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setZoomGesturesEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);


                if (mMap != null) {
                    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }

                mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng arg0) {
                        hideView();
                    }
                });


                //my location icon
                if (mapView != null &&
                        mapView.findViewById(Integer.parseInt("1")) != null) {
                    // Get the button view
                    View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
                    // and next place it, on bottom right (as Google Maps app)
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                            locationButton.getLayoutParams();
                    // position on right bottom
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
                    layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
                    layoutParams.setMargins(0, 0, 30, 30);
                }
            }

        });

    }

    public String is_similar_deal = "0";

    public void showDialogForBeforeUploadProperty(MyItem marker, String dealID) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA.LOCATION, marker.getTitle());
        bundle.putString(Constants.EXTRA.LAT, marker.getDealData().getLat());
        bundle.putString(Constants.EXTRA.LANG, marker.getDealData().getLang());
        bundle.putString(Constants.EXTRA.DEAL_ID, marker.getDealData().getDealId());
        FMDBottomsheetFragment fmdBottomsheetFragment = new FMDBottomsheetFragment();
        fmdBottomsheetFragment.setArguments(bundle);
        fmdBottomsheetFragment.showNow(getParentFragmentManager(), FMDBottomsheetFragment.class.getSimpleName());
        hideProgressDialog();


        /*Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.deal_confirm_dialog_ui);

        CustomTextView tvHeaderMessage, tvCancel, tvUpload;
        MaterialCheckBox materialCheckBox, similarCheckedView;

        similarCheckedView = ((MaterialCheckBox) mDialog.findViewById(R.id.similarCheckedView));
        materialCheckBox = ((MaterialCheckBox) mDialog.findViewById(R.id.checkView));
        tvHeaderMessage = ((CustomTextView) mDialog.findViewById(R.id.txt_message_header));
        tvHeaderMessage.setText(marker.getTitle());
        tvCancel = ((CustomTextView) mDialog.findViewById(R.id.txt_cancle));
        tvUpload = ((CustomTextView) mDialog.findViewById(R.id.txt_upload));

        materialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_similar_deal = "0";
                    similarCheckedView.setChecked(false);
                    tvUpload.setEnabled(true);
                    tvUpload.setClickable(true);
                    tvUpload.setTextColor(getActivity().getResources().getColor(R.color.text_color));
                } else {
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(getActivity().getResources().getColor(R.color.text_color_dark_grey));
                }

            }
        });
        similarCheckedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_similar_deal = "1";
                    materialCheckBox.setChecked(false);
                    tvUpload.setEnabled(true);
                    tvUpload.setClickable(true);
                    tvUpload.setTextColor(getActivity().getResources().getColor(R.color.text_color));
                } else {
                    is_similar_deal = "0";
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(getActivity().getResources().getColor(R.color.text_color_dark_grey));
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_similar_deal.equalsIgnoreCase("1")) {
                    getActivity().startActivityIfNeeded(new Intent(getActivity(), PostNewProperty.class)
                            .putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.ADD)
                            .putExtra("is_similar_deal", is_similar_deal)
                            .putExtra("deal_id", "0")
                            .putExtra("deal_address", marker.getTitle())
                            .putExtra("deal_lat_lang", marker.getPosition()), REQUEST_UPLOAD_FUND);
                } else {
                    getActivity().startActivityIfNeeded(new Intent(getActivity(), PostNewProperty.class)
                            .putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.ADD)
                            .putExtra("is_similar_deal", is_similar_deal)
                            .putExtra("deal_id", dealID), REQUEST_UPLOAD_FUND);
                }


                mDialog.dismiss();
            }
        });
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();*/

    }


    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }


    @Override
    public void onProviderDisabled(String s) {

    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onLocationUpdate(Location location) {
        this.location = location;
        if (location == null) {
            return;
        }
        Log.d(TAG, "Update Location" + location.getLatitude() + "-" + location.getLatitude());
        mCusrrentLocation = location;
        getDeviceLocation();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
//        if (mGoogleApiClient.isConnected()) {
//
////            startLocationUpdates();
//            Log.d(TAG, "Location update resumed .....................");
//        } else {
//            stopLocationUpdates();
//        }
//        if (mGoogleApiClient != null) {
        initLocationService();
//        }

    }


    @Override
    public void onStart() {
        super.onStart();
//        if (mGoogleApiClient.isConnected()) {
//            startLocationUpdates();
        if (mFusedLocationHelper.getmGoogleApiClient().isConnected()) {
            onLocationChanged(mCusrrentLocation);
        }
//        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored()");
    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        stopLocationUpdates();
//        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }


    @Override
    public void onDetach() {
        super.onDetach();
        if (mFusedLocationHelper != null) {
            mFusedLocationHelper.stop();
        }
        stopLocationUpdates();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mFusedLocationHelper != null) {
            mFusedLocationHelper.stop();
        }
        stopLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
        if (location == null) {
            return;
        }
        mCusrrentLocation = location;
        //call for update location
        if (UserPreference.getInstance(BaseApplication.getInstance()).isUserLogin()) {
//            if(!BuildConfig.DEBUG){
            callUpdateLocation();
//            }
        }
    }

    private void callUpdateLocation() {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        linkedHashMap.put("long", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));
        linkedHashMap.put("timezone", RequestBody.create(MediaType.parse("multipart/form-data"), getTimeZone()));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.updateLocation(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {

                } else {
//                    showToast(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private String getTimeZone() {
        TimeZone defaultTz = TimeZone.getDefault();
        Date now = new Date();
        int offsetFromUtc = defaultTz.getOffset(now.getTime()) / 1000;
        Log.e("TAG", "Date Time Zone - " + offsetFromUtc / 60);
        int offSetUtc = (offsetFromUtc / 60);
        return "" + offSetUtc;
    }

    /*Update Device Token*/
    private void callUpdateDeviceToken() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("device_token", RequestBody.create(MediaType.parse("multipart/form-data"), PreferenceUtils.getPushToken()));
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.deviceTokenUpdate(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCusrrentLocation.getLatitude(), mCusrrentLocation.getLongitude()), 10.0f));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(15.259211, -94.484638), 3.0F));
            //get state wise property
//            if(!BuildConfig.DEBUG){
            //call api only for release apk
            callPropetyListApi(true);
//            }
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getFirstLatestProperty(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showToast(getResources().getString(R.string.no_internet));
            return;
        }

        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(getActivity()).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(getActivity()).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "1"));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));
        hashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        hashMap.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));
        hashMap.put("txt_sort", RequestBody.create(MediaType.parse("multipart/form-data"), mTextSortVaule));

        hashMap.put("range_by", RequestBody.create(MediaType.parse("multipart/form-data"), rangeby));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<CommonListResponse> observable = userService.commonListApi(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonListResponse>() {
            @Override
            public void onSuccess(CommonListResponse response) {
                //Service view hide
                mConstraintLayoutServiceLayout.setVisibility(View.GONE);
                hideProgressDialog();
                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        //new code today
                        if (response.getData().size() != 0) {
                            setDataOfFirstItemView(response.getData().get(0), 0);
                            isSelectedPosition = 0;
                        }
                    } else {
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (response.getData() != null && !response.getData().equals("")) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        if (response.getData().size() != 0) {
                            setDataOfFirstItemView(response.getData().get(0), 0);
                            isSelectedPosition = 0;
                        }
                        isSelectedPosition = 0;

                    } else {
//                        showToast(response.getText());
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }
                }
                //call device token update
                callUpdateDeviceToken();
            }

            @Override
            public void onFailed(Throwable throwable) {
                try {
                    if (relativeItemMainLayout != null) {
                        hideView();
                    }
                    hideProgressDialog();
                } catch (NullPointerException e) {
                    hideProgressDialog();
                }
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFusedLocationHelper != null) {
            mFusedLocationHelper.stop();
        }
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {

        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        mGoogleApiClient.disconnect();*/
        Log.d("", "Location update stopped .......................");
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.new_home_map_fragment;
    }


    @Override
    public void refresh(boolean b) {
        Log.e("TAG", "" + b);
        if (!b) {
            if (mCusrrentLocation != null) {
                getFirstLatestProperty(false);
            }
        }
    }

    // Declare a variable for the cluster manager.
    private ClusterManager<MyItem> clusterManager;
    private CustomMapClusterRenderer customMapClusterRenderer;

    private void steOnMapPins() {
        // Add a marker in Sydney and move the camera
        if (mMap != null)
            mMap.clear();

        clusterManager = new ClusterManager<MyItem>(getActivity(), mMap);
        customMapClusterRenderer = new CustomMapClusterRenderer<MyItem>(getActivity(), mMap, clusterManager);
        customMapClusterRenderer.setMinClusterSize(4);
//        clusterManager.setAnimation(true);
        mMap.setOnCameraIdleListener(clusterManager);
        mMap.setOnMarkerClickListener(clusterManager);

        //cluster item click marker click
        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem item) {
                //check view type
                int pos = Integer.parseInt(item.getTagView());
                if (item.getViewType().equalsIgnoreCase("property_pin")) {
                    //property
                    isSelectedPosition = pos;
                    setDataOfFirstItemView(item.getPropertyData(), pos);

                } else {
                    //find my deal
                    showDialogForBeforeUploadProperty(item, item.getViewId());
                }

                return false;
            }
        });

        //cluster round click
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<MyItem>() {
            @Override
            public boolean onClusterClick(Cluster<MyItem> cluster) {
                // Create the builder to collect all essential cluster items for the bounds.
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (ClusterItem item : cluster.getItems()) {
                    builder.include(item.getPosition());
                }
                // Get the LatLngBounds
                final LatLngBounds bounds = builder.build();
                // Animate camera to the bounds
                try {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                    clusterManager.setAnimation(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        //pin set
        if (mListDataResponse != null && mListDataResponse.size() != 0) {

            for (int i = 0; i < mListDataResponse.size(); i++) {
                //loop of property list array
                for (int j = 0; j < mListDataResponse.get(i).getPropertyList().size(); j++) {
                    if (mListDataResponse.get(i).getPropertyList().get(j).getLat() != null && !mListDataResponse.get(i).getPropertyList().get(j).getLat().equalsIgnoreCase("")) {
                        if (mListDataResponse.get(i).getPropertyList().get(j).getLang() != null && !mListDataResponse.get(i).getPropertyList().get(j).getLang().equalsIgnoreCase("")) {
                            LatLng sydney = new LatLng(Double.parseDouble(mListDataResponse.get(i).getPropertyList().get(j).getLat()), Double.parseDouble(mListDataResponse.get(i).getPropertyList().get(j).getLang()));
                            //property
                            BitmapDescriptor bitmapDescriptorFactory;
                            if (mListDataResponse.get(i).getPropertyList().get(j).getPropertyListed() != null && !mListDataResponse.get(i).getPropertyList().get(j).getPropertyListed().equalsIgnoreCase("")) {
                                if (mListDataResponse.get(i).getPropertyList().get(j).getPropertyListed().equalsIgnoreCase("1")) {
                                    bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell_o);
                                } else {
                                    bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell);
                                }
                            } else {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell);
                            }
                            MyItem myItem = new MyItem(sydney, mListDataResponse.get(i).getPropertyList().get(j).getHouse(), "", String.valueOf(i), "property_pin", mListDataResponse.get(i).getPropertyList().get(j).getPropertyId(), bitmapDescriptorFactory, mListDataResponse.get(i).getPropertyList().get(j), null);
                            myItemList.add(myItem);
//                            myItems.addItem(myItem);
                        }
                    }
                }
                //loop of find my deal list
                for (int dd = 0; dd < mListDataResponse.get(i).getDealList().size(); dd++) {
                    if (mListDataResponse.get(i).getDealList().get(dd).getLat() != null && !mListDataResponse.get(i).getDealList().get(dd).getLat().equalsIgnoreCase("")) {
                        if (mListDataResponse.get(i).getDealList().get(dd).getLang() != null && !mListDataResponse.get(i).getDealList().get(dd).getLang().equalsIgnoreCase("")) {
                            LatLng sydney = new LatLng(Double.parseDouble(mListDataResponse.get(i).getDealList().get(dd).getLat()), Double.parseDouble(mListDataResponse.get(i).getDealList().get(dd).getLang()));
                            BitmapDescriptor bitmapDescriptorFactory;
                            bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.my_deal);
                            MyItem myItem = new MyItem(sydney, mListDataResponse.get(i).getDealList().get(dd).getAddress(), "", String.valueOf(i), "find_deal", mListDataResponse.get(i).getDealList().get(dd).getDealId(), bitmapDescriptorFactory, null, mListDataResponse.get(i).getDealList().get(dd));
                            myItemList.add(myItem);
//                            myItems.addItem(myItem);
                        }
                    }
                }

                clusterManager.addItems(myItemList);
                clusterManager.setRenderer(customMapClusterRenderer);
            }
            clusterManager.cluster();
            //get my deal
//            getMyDealList();
        }

    }

    //Property Api Calling
    private void callPropetyListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showToast(getResources().getString(R.string.no_internet));
            return;
        }

        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(getActivity()).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(getActivity()).getUserDetail().getLoginId()));
        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<PropertyStateWiseResponse> observable = userService.stateWiseProperty(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<PropertyStateWiseResponse>() {
            @Override
            public void onSuccess(PropertyStateWiseResponse response) {
                //Service view hide
                mConstraintLayoutServiceLayout.setVisibility(View.GONE);
                hideProgressDialog();
                if (mListDataResponse != null)
                    mListDataResponse.clear();
                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mListDataResponse.addAll(response.getData());
                        if (mListDataResponse.size() != 0) {
                            if (mListDataResponse.get(0).getPropertyList().size() != 0) {
                                relativeItemMainLayout.setVisibility(View.VISIBLE);
//                                setDataOfFirstItemView(mListDataResponse.get(0).getPropertyList().get(0), 0);
                                isSelectedPosition = 0;
                            }
                        }
                    } else {
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (response.getData() != null && !response.getData().equals("")) {
                        mListDataResponse.addAll(response.getData());
                        if (mListDataResponse.size() != 0) {
                            if (mListDataResponse.get(0).getPropertyList().size() != 0) {
                                relativeItemMainLayout.setVisibility(View.VISIBLE);
//                                setDataOfFirstItemView(mListDataResponse.get(0).getPropertyList().get(0), 0);
                                isSelectedPosition = 0;
                            }
                        }

                    } else {
//                        showToast(response.getText());
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }
                }
                //set pin on map view
                if (mMap != null) {
                    steOnMapPins();
                }
                //latest property get
                getFirstLatestProperty(false);
            }

            @Override
            public void onFailed(Throwable throwable) {
                try {
                    if (relativeItemMainLayout != null) {
                        hideView();
                    }
                    hideProgressDialog();
                } catch (NullPointerException e) {
                    hideProgressDialog();
                }
            }
        });
    }

    //bottom sheet view
    public void showBottomView() {
        final SelectionBottomSheetDialogFragment selectionBottomSheetDialogFragment = SelectionBottomSheetDialogFragment.newInstance();
        selectionBottomSheetDialogFragment.addListener(new SelectionBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onPropertyClick() {

                selectionBottomSheetDialogFragment.dismiss();
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_BUYER, Constants.SELECTION_HEADER_TITLE.SELECTE_SELLER, 0);
            }

            @Override
            public void onRentalsClick() {
                selectionBottomSheetDialogFragment.dismiss();
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_LANDLOARD, Constants.SELECTION_HEADER_TITLE.SELECTE_TENANT, 1);
            }

            @Override
            public void onServicesClick() {
                relativeItemMainLayout.setVisibility(View.GONE);
                selectionBottomSheetDialogFragment.dismiss();
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_HOMEOWNER, Constants.SELECTION_HEADER_TITLE.SELECTE_SERVICE, 2);
            }

            @Override
            public void onCancel() {
                selectionBottomSheetDialogFragment.dismiss();
            }
        });
        selectionBottomSheetDialogFragment.show(getChildFragmentManager(), SelectionBottomSheetDialogFragment.class.getSimpleName());

    }


    //rental and property view set
    private void setDataOfFirstItemView(CommonData mHomeData, int index) {
        relativeItemMainLayout.setVisibility(View.VISIBLE);
        mConstraintLayoutPropertyUi.setVisibility(View.VISIBLE);

        mPropertyPinSelectedData = mHomeData;
        if (mPropertyPinSelectedData.getPropertyId() == null) {
            mPropertyPinSelectedData.setPropertyId(mHomeData.getCommonId());
        }
        final CommonData loMessage = mHomeData;

        CustomTextView txtPropertyName, txtPropertyLocation1, txtPropertyLocation2, txtPropertyPrice, txtUnlistedListedTag, textViewPropertyDetailsBedsNo,
                textViewPropertyDetailsBathNo, textViewAreaValues, textViewofChatTime;

        ImageView imagePropertyLikeStatus, imageTempView, imageViewUnavailableIconView, img_pin, img_eye;
        RelativeLayout relativeUnlistedListedTag, viewTimeCountDown;

        imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
        imageTempView = loView.findViewById(R.id.imageTempView);
        imageTempView.setVisibility(View.GONE);
        textViewPropertyDetailsBedsNo = loView.findViewById(R.id.textViewPropertyDetailsBedsNo);
        textViewPropertyDetailsBathNo = loView.findViewById(R.id.textViewPropertyDetailsBathNo);
        textViewAreaValues = loView.findViewById(R.id.textViewAreaValues);

        imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
        txtPropertyName = loView.findViewById(R.id.txtPropertyName);
        txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
        txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
        img_pin = loView.findViewById(R.id.img_pin);
        img_eye = loView.findViewById(R.id.img_eye);
        txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
        txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
        relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
        viewTimeCountDown = loView.findViewById(R.id.viewTimeCountDown);
        textViewofChatTime = loView.findViewById(R.id.textViewofChatTime);

        viewTimeCountDown.setVisibility(View.VISIBLE);


        txtPropertyName.setText(loMessage.getHouse());

        if (PreferenceUtils.getIsPremiumUser() == 1 ) {// is premium user is true
            img_eye.setVisibility(View.GONE);
            txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
            txtPropertyLocation1.setText(loMessage.getAddress1());
            txtPropertyLocation2.setText(loMessage.getAddress2());
        } else {
            if (!BaseApplication.getInstance().getLoginID().equals(loMessage.getData().getLoginId())) {
                img_eye.setVisibility(View.VISIBLE);
                txtPropertyLocation1.setText("****");
                txtPropertyLocation1.setTextSize(15);
                txtPropertyLocation2.setText(loMessage.getAddress2());
            } else {
                img_eye.setVisibility(View.GONE);
                txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                txtPropertyLocation1.setText(loMessage.getAddress1());
                txtPropertyLocation2.setText(loMessage.getAddress2());
            }

        }
        if (PreferenceUtils.getPlanVersionStatus()){
            img_eye.setVisibility(View.GONE);
            txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
            txtPropertyLocation1.setText(loMessage.getAddress1());
            txtPropertyLocation2.setText(loMessage.getAddress2());
        }
        if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
            //gone
            txtPropertyPrice.setVisibility(View.INVISIBLE);
        } else {
            if (loMessage.getRentAmount() != null && !loMessage.getRentAmount().equalsIgnoreCase("")) {
                txtPropertyPrice.setVisibility(View.VISIBLE);
                int dollr = Integer.parseInt(loMessage.getRentAmount());
                if (dollr <= 1000000) {
//                            int million = (dollr / 1000);
                    txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                } else if (dollr <= 100000000) {
//                            int billion = (dollr / 10000);
                    txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                } else {
                    txtPropertyPrice.setText("$" + currencyFormat(String.valueOf(dollr)));
                }
            } else {
                txtPropertyPrice.setVisibility(View.INVISIBLE);
            }
        }

        textViewPropertyDetailsBedsNo.setText(loMessage.getBedNos() + " bd");
        textViewPropertyDetailsBathNo.setText(loMessage.getBathNos() + " ba");
        textViewAreaValues.setText(loMessage.getArea() + " sqft");

        if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
            viewTimeCountDown.setBackgroundResource(R.drawable.blue);
            relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
            txtUnlistedListedTag.setText("Pre-Foreclosure");
        } else {
            if (loMessage.getPropertyListed().equalsIgnoreCase("0")) {
                viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                txtUnlistedListedTag.setText("Off-Market");

            } else {
                viewTimeCountDown.setBackgroundResource(R.drawable.orange);
                relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_gradian);
                txtUnlistedListedTag.setText("Listed");
            }
        }


        String days = Utils.getDays(loMessage.getCreated_date(), "");
        if (days.equalsIgnoreCase("1") || days.equalsIgnoreCase("0")) {
            textViewofChatTime.setText("New on Flippbidd");
        } else {
            textViewofChatTime.setText(days + " days on Flippbidd");
        }
        //trending
        if (loMessage.getViewCounts() > Constants.TRENDING_COUNTS) {
            viewTimeCountDown.setBackgroundResource(R.drawable.ic_green);
            textViewofChatTime.setText("Trending");
        }

        if (loMessage.isLike()) {
            imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
        } else {
            imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
        }
        if (loMessage.getIsAvailable().equalsIgnoreCase("0")) {
            //0 unavailable
            imageViewUnavailableIconView.setVisibility(View.VISIBLE);
        } else {
            //1 available
            imageViewUnavailableIconView.setVisibility(View.GONE);
        }

        if (loMessage.getImages() != null) {
            if (loMessage.getImages().size() != 0) {
                ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
                propertyNoImage.setVisibility(View.GONE);

                ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
                pager.setVisibility(View.VISIBLE);
                PhotosAdapter adapter = new PhotosAdapter(((BaseAppCompatActivity) getActivity()), loMessage.getImages(), loMessage.getPropertyId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
                pager.setAdapter(adapter);
                CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
                tabLayout.setViewPager(pager);
            } else {
                //show no image view
                ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
                propertyNoImage.setVisibility(View.VISIBLE);
                ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
                pager.setVisibility(View.INVISIBLE);
                CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
                tabLayout.setVisibility(View.INVISIBLE);

            }
        } else {
            //show no image view
            ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
            propertyNoImage.setVisibility(View.VISIBLE);
            ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
            pager.setVisibility(View.INVISIBLE);
            CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
            tabLayout.setVisibility(View.INVISIBLE);

        }

        if (loMessage.getLat() != null && !loMessage.getLat().equalsIgnoreCase("")
                || loMessage.getLang() != null && !loMessage.getLat().equalsIgnoreCase("")) {

            txtDistanceMeter.setVisibility(View.VISIBLE);
            Location locationA = new Location("point A");

            locationA.setLatitude(mCusrrentLocation.getLatitude());
            locationA.setLongitude(mCusrrentLocation.getLongitude());

            Location locationB = new Location("point B");

            locationB.setLatitude(Double.parseDouble(loMessage.getLat()));
            locationB.setLongitude(Double.parseDouble(loMessage.getLang()));

            float distance = locationA.distanceTo(locationB);

            double miles = distance / 1609.344;
            txtDistanceMeter.setText(String.format("%.02f", miles) + " Mi");
        } else {
            //hide location text
            txtDistanceMeter.setVisibility(View.GONE);
        }
        imagePropertyLikeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPropertyPinSelectedData.isLike()) {
                    mPropertyPinSelectedData.setLike(false);
                    imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
                    callLikesUnLikesApi(mPropertyPinSelectedData, "0");
                } else {
                    mPropertyPinSelectedData.setLike(true);
                    imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                    callLikesUnLikesApi(mPropertyPinSelectedData, "1");
                    //click to like play tone
                    mp = MediaPlayer.create(getActivity(), R.raw.like_tone);
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        public void onCompletion(MediaPlayer mp) {
                            //code
                            mp.release();
                        }
                    });
                }
            }
        });

    }


    //property and rental click events
    /*chat */
    String channelStatus = "";
    String channelUrl = "";
    String channelMainId = "";
    String channelPropertyId = "";
    String propertyOwenrEmail = "";


    String chatName, chatAddress, chatCoverUrl, ownerID, propertyID;

    public String getAddress(String address, String ownerID, String addedby, String propertyID, String channelMainID) {
        /*address**ownerID**addedby**propertyID**channelMainID*/
        return address + "##" + ownerID + "##"
                + addedby + "##" + propertyID + "##" + channelMainId;
    }

    /*server base chat manage*/
    private void checkChannelStatus(boolean isProgress, List<String> selectedId) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showToast(getString(R.string.no_internet));
            return;
        }
        //owner_id, buyer_id, property_id, type
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), ownerID));
        linkedHashMap.put("buyer_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getQBLoginID()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), propertyID));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<ChannelCreatedResponse> observable;
        observable = userService.channelStatusChecked(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ChannelCreatedResponse>() {
            @Override
            public void onSuccess(ChannelCreatedResponse response) {
                showProgressDialog(false);
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

                if (response.getSuccess()) {
                    channelStatus = "1";
                    channelUrl = response.getData().getChannelId();
                    channelMainId = response.getData().getChannelMainId();
                    channelPropertyId = response.getData().getPropertyId();
                    propertyOwenrEmail = response.getData().getOwnerId();

                    createGroupChannel(selectedId);
                } else {
                    //create new channel with user id
                    channelStatus = "0";
                    channelUrl = "";

                    createGroupChannel(selectedId);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressDialog(false);
            }
        });
    }


    private void createChannel(boolean isProgress, String channelID) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showToast(getString(R.string.no_internet));
            return;
        }

        //buyer_id, channel_id, owner_id, property_id, type, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("channel_id", RequestBody.create(MediaType.parse("multipart/form-data"), channelID));
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), ownerID));
        linkedHashMap.put("buyer_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getQBLoginID()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), propertyID));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "2"));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<ChannelCreatedResponse> observable;
        observable = userService.createChannel(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ChannelCreatedResponse>() {
            @Override
            public void onSuccess(ChannelCreatedResponse response) {
                showProgressDialog(false);
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //get channel url
                    channelMainId = response.getData().getChannelMainId();
                    channelPropertyId = response.getData().getPropertyId();
                    propertyOwenrEmail = response.getData().getOwnerId();
                    enterGroupChannel(channelID, channelMainId, channelPropertyId, propertyOwenrEmail);
                } else {
                    //create new channel with user id
                    Log.e(TAG, "Created else part");
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressDialog(false);

            }
        });
    }

    /*chat create */

    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds The users to be members of the new channel.
     *                Whether the channel is unique for the selected members.
     *                If you attempt to create another Distinct channel with the same members,
     *                the existing channel instance will be returned.
     */
    private void createGroupChannel(List<String> userIds) {


        if (channelStatus.equalsIgnoreCase("0")) {

            /* address **ownerID **addedby **propertyID **channelMainID*/
            if (mPropertyPinSelectedData != null && !mPropertyPinSelectedData.equals("")) {
                chatAddress = getAddress(mPropertyPinSelectedData.getAddress(), mPropertyPinSelectedData.getData().getEmailAddress(), mPropertyPinSelectedData.getData().getFullName(), mPropertyPinSelectedData.getPropertyId(), channelMainId);
            }

            //admin list
            List<String> moAdmin = new ArrayList<>();
            moAdmin.add(userIds.get(1));

            GroupChannelParams params = new GroupChannelParams()
                    .setPublic(false)
                    .setEphemeral(false)
                    .setDistinct(false)
                    .addUserIds(userIds)
                    .setOperatorUserIds(moAdmin)  // Or .setOperators(List<User> operators)
                    .setName(chatName)
                    .setCoverUrl(chatCoverUrl)
                    .setData(chatAddress)//.setCoverImage(FILE)            // Or .setCoverUrl(COVER_URL)
                    .setCustomType(mScreenName);
            //setChannelUrl(chatURL)  // In a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.
            GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        if (e.getMessage().equalsIgnoreCase("\"channel_url\" violates unique constraint.")) {
//                                enterGroupChannel(chatURL);
                        }
                        return;
                    }

                    createChannel(true, groupChannel.getUrl());
                }
            });

        } else {
            //get url form iur server
            GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }
                    enterGroupChannel(groupChannel.getUrl(), channelMainId, channelPropertyId, propertyOwenrEmail);
                }
            });
        }

    }


    /**
     * Enters a Group Channel with a URL.
     *
     * @param extraChannelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String extraChannelUrl, String channelMainId, String channelPropertyId, String ownerEmail) {
        Intent mainIntent = new Intent(getActivity(), GroupChatActivity.class);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_MAIN_ID, channelMainId);
        mainIntent.putExtra(EXTRA_PROPERTY_ID, channelPropertyId);
        mainIntent.putExtra(EXTRA_OWNER_EMAIL, ownerEmail);
        startActivity(mainIntent);
    }

    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_MAIN_ID = "GROUP_CHANNEL_MAIN_ID";
    public static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    public static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";
    //end


    //property api call like
    private void callLikesUnLikesApi(CommonData mPropertyData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(getActivity())) {
            showToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getPropertyId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(false);
        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.propertyLikes(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                } else {
                    showToast(response.getText());
                }
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    //service view set
    private void setDataOfServiceFirstItemView(List<ServiceListData> mServiceList,
                                               int position) {
        mConstraintLayoutServiceLayout.setVisibility(View.VISIBLE);


        final ServiceListData mServiceData = mServiceList.get(position);

        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
            relativeLayoutActionbox.setVisibility(View.GONE);
            //old
            relativeLayoutActionboxsecond.setVisibility(View.VISIBLE);
            //new
//            relativeLayoutActionboxsecond.setVisibility(View.GONE);
        } else {
            //old
//            relativeLayoutActionbox.setVisibility(View.VISIBLE);
            //new
            relativeLayoutActionbox.setVisibility(View.GONE);
            relativeLayoutActionboxsecond.setVisibility(View.GONE);
        }

        if (mServiceData.getSenderQbId() != null && !mServiceData.getSenderQbId().equalsIgnoreCase("")) {
            if (!UserPreference.getInstance(getActivity()).getUserDetail().getLoginId().equalsIgnoreCase(mServiceData.getLoginId())) {
                imgMessage.setVisibility(View.VISIBLE);
            } else {
                imgMessage.setVisibility(View.GONE);
            }
        } else {
            imgMessage.setVisibility(View.GONE);
        }


        if (mServiceData.getServicePrice() != null && !mServiceData.getServicePrice().equalsIgnoreCase("")) {

            textViewServicePrice.setText("$ " + mServiceData.getServicePrice());
        }
        if (mServiceData.getTitle() != null && !mServiceData.getTitle().equalsIgnoreCase("")) {

            textViewServiceTitle.setText(mServiceData.getTitle());
        }

        if (mServiceData.getServiceType() != null && !mServiceData.getServiceType().equalsIgnoreCase("")) {

            textViewService.setText(mServiceData.getDescription());
        }


        if (mServiceData.isLike()) {
            imgHeart.setImageResource(R.drawable.fav);
        } else {
            imgHeart.setImageResource(R.drawable.unfav);
        }
        //new design code
        if (mServiceData.getImages() != null && mServiceData.getImages().size() != 0) {
            int lastIndex = mServiceData.getImages().size() - 1;
            //reversed order
            if (mServiceData.getImages().get(lastIndex).getImageUrl() != null && !mServiceData.getImages().get(lastIndex).getImageUrl().equalsIgnoreCase("")) {
                lottieServiceAnimationProgress.setVisibility(View.VISIBLE);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheOnDisc()
                        .cacheInMemory()
                        .showImageOnFail(R.drawable.no_image_icon)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                imageLoader.displayImage(mServiceData.getImages().get(lastIndex).getImageUrl(), service_image, options);

            } else {
                if (lottieServiceAnimationProgress.isAnimating()) {
                    lottieServiceAnimationProgress.pauseAnimation();
                }
                lottieServiceAnimationProgress.setVisibility(View.GONE);
                service_image.setImageResource(R.drawable.no_image_icon);
            }

        } else {
            if (lottieServiceAnimationProgress.isAnimating()) {
                lottieServiceAnimationProgress.pauseAnimation();
            }
            lottieServiceAnimationProgress.setVisibility(View.GONE);
            service_image.setImageResource(R.drawable.no_image_icon);
        }


        //get distance
        if (mServiceData.getLat() != null && !mServiceData.getLat().equalsIgnoreCase("") ||
                mServiceData.getLang() != null && !mServiceData.getLang().equalsIgnoreCase("")) {
            txtServiceDistanceMeter.setVisibility(View.VISIBLE);
            Location locationA = new Location("point A");

            locationA.setLatitude(mCusrrentLocation.getLatitude());
            locationA.setLongitude(mCusrrentLocation.getLongitude());

            Location locationB = new Location("point B");

            locationB.setLatitude(Double.parseDouble(mServiceData.getLat()));
            locationB.setLongitude(Double.parseDouble(mServiceData.getLang()));

            float distance = locationA.distanceTo(locationB);
            double miles = distance / 1609.344;
            txtServiceDistanceMeter.setText("(" + String.format("%.02f", miles) + " Miles)");

        } else {
            txtServiceDistanceMeter.setVisibility(View.GONE);
        }

    }


    private void colorChanges(CustomAppCompatButton btnSelected, CustomAppCompatButton btnNoSelectFirst, CustomAppCompatButton btnNoSelectSecond) {

        btnSelected.setTextColor(getResources().getColor(R.color.dark_font_color));
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Poppins-Bold.ttf");
        btnSelected.setTypeface(face);

        btnNoSelectFirst.setTextColor(getResources().getColor(R.color.colorWhite));
        Typeface face1 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Poppins-Regular.ttf");
        btnNoSelectFirst.setTypeface(face1);

        btnNoSelectSecond.setTextColor(getResources().getColor(R.color.colorWhite));
        Typeface face2 = Typeface.createFromAsset(getActivity().getAssets(),
                "fonts/Poppins-Regular.ttf");
        btnNoSelectSecond.setTypeface(face2);

    }

    @Override
    public void onCameraMoveStarted(int i) {
        hideView();
    }

    private void hideView() {
        relativeItemMainLayout.setVisibility(View.GONE);
        mConstraintLayoutServiceLayout.setVisibility(View.GONE);
    }


    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


    //cluster data
    class CustomMapClusterRenderer<T extends ClusterItem> extends CustomeClusterRenderer<T> {

        IconGenerator mClusterIconGenerator;
        private ShapeDrawable mColoredCircleBackground;
        float mDensity;

        CustomMapClusterRenderer(Context context, GoogleMap map, ClusterManager<T> clusterManager) {
            super(context, map, clusterManager);
            mClusterIconGenerator = new IconGenerator(context);
            this.mDensity = context.getResources().getDisplayMetrics().density;
        }

        @Override
        protected void onBeforeClusterItemRendered(T item,
                                                   MarkerOptions markerOptions) {
            MyItem markerItem = (MyItem) item;
            markerOptions.icon(markerItem.getBitmapDescriptorFactory());
        }

        @Override
        protected void onBeforeClusterRendered(@NonNull Cluster<T> cluster, @NonNull MarkerOptions markerOptions) {
            super.onBeforeClusterRendered(cluster, markerOptions);
        }


        @Override
        public void onClustersChanged(Set<? extends Cluster<T>> clusters) {
            super.onClustersChanged(clusters);
        }

        @Override
        public void setMinClusterSize(int minClusterSize) {
            super.setMinClusterSize(minClusterSize);
        }

        @Override
        protected boolean shouldRenderAsCluster(@NonNull Cluster<T> cluster) {
            return cluster.getSize() > 13;
        }

        @Override
        public void setAnimation(boolean animate) {
            super.setAnimation(animate);
        }

        /*@Override
        protected boolean shouldRenderAsCluster(@NonNull Cluster<T> cluster) {
            return super.shouldRenderAsCluster(cluster);
        }*/

        @Override
        protected int getBucket(@NonNull Cluster<T> cluster) {
            return super.getBucket(cluster);
        }


        @Override
        protected int getColor(int clusterSize) {
            return Color.parseColor("#2B79A8");
        }

    }

    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(getActivity(), "Map is not ready,please wait few munites", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Go To Bondi button is clicked.
     */
    public void onGoToBondi(View view) {
        if (!checkReady()) {
            return;
        }

//        changeCamera(CameraUpdateFactory.newCameraPosition(BONDI));
    }

    /**
     * Called when the stop button is clicked.
     */
    public void onStopAnimation(View view) {
        if (!checkReady()) {
            return;
        }

        mMap.stopAnimation();
    }

    /**
     * Called when the zoom in button (the one with the +) is clicked.
     */
    public void onZoomIn(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomIn());
    }

    /**
     * Called when the zoom out button (the one with the -) is clicked.
     */
    public void onZoomOut(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.zoomOut());
    }

    /**
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    public void onScrollLeft(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(-SCROLL_BY_PX, 0));
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    public void onScrollRight(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(SCROLL_BY_PX, 0));
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    public void onScrollUp(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, -SCROLL_BY_PX));
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
     */
    public void onScrollDown(View view) {
        if (!checkReady()) {
            return;
        }

        changeCamera(CameraUpdateFactory.scrollBy(0, SCROLL_BY_PX));
    }

    private void changeCamera(CameraUpdate update) {
        changeCamera(update, null);
    }

    /**
     * Change the camera position by moving or animating the camera depending on the state of the
     * animate toggle button.
     */
    private void changeCamera(CameraUpdate update, GoogleMap.CancelableCallback callback) {
        mMap.animateCamera(update, callback);

    }
}