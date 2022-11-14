package com.flippbidd.Fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
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

import com.airbnb.lottie.LottieAnimationView;
import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonList.CommonListResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.DealData;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.flippbidd.Model.Response.Service.ServiceListData;
import com.flippbidd.Model.Response.Service.ServiceListResponse;
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
import com.flippbidd.activity.Details.CommonDetails.CommonDetailsActivity;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.app.Activity.RESULT_OK;

public class NewHomeFragments extends BaseAppCompatMapFragments implements RefreshListener, LocationListener, FusedLocationHelper.LocationUpdateListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {

    private static String TAG = NewHomeFragments.class.getSimpleName();
    public static final int REQUEST_UPLOAD_FUND = 1009;

    GoogleMap mMap;
    //GPS TRACKING
    public LocationRequest mLocationRequest;
    public static final int REQUEST_LOCATION_PERMISSIONS_CODE = 0;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    //    GoogleApiClient mGoogleApiClient;
    private LocationManager locationManager;
    public Location location;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;
    boolean locationServiceAvailable;
    private FusedLocationHelper mFusedLocationHelper;
    private Location mCusrrentLocation;

    private long UPDATE_INTERVAL = 1 * 20000;  /* 10 secs */

    boolean isClick = false;
    private String lableStr1, lableStr2;

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
    private String mTextSortVaule = "";//newToold
    Disposable disposable;
    private int listLimite = 500;
    private List<CommonData> mListDataResponse = new ArrayList<>();
    private List<ServiceListData> mServiceDataList = new ArrayList<>();
    private List<DealData> mFindMyDealList = new ArrayList<>();

    private ImageLoader imageLoader;
    Marker _myLocation;

    private String mScreenName;
    private String isSelectedString = "property";
    int isSelectedPosition;
    private CommonData mSelectedRentalData;
    ServiceListData mSeletcedServiceData;
    View loView;

    MediaPlayer mp;

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        isClick = true;
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


        //icon click view
        MainActivity.imageRightOfJV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomView();
            }
        });

    }


    //open list view
    private void openListViewByslectedType() {
        Bundle bundle = null;
        switch (isScreenType) {
            case Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER:
                isClick = false;
                MainActivity.showIcon(1, Constants.SELECTION_HEADER_TITLE.TITLE_PROPERTIES);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER);
                PropertyList fragment = new PropertyList();
                fragment.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment, true);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_LANDLOARD:
                isClick = false;
                MainActivity.showIcon(4, Constants.SELECTION_HEADER_TITLE.TITLE_MY_RENTAL);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_LANDLOARD);
                PropertyList fragment1 = new PropertyList();
                fragment1.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment1, true);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER:
                isClick = false;
                MainActivity.showIcon(1, Constants.SELECTION_HEADER_TITLE.SELECTE_HOMEOWNER);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER);
                Services fragment2 = new Services();
                fragment2.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment2, true);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER:
                isClick = false;
                MainActivity.showIcon(2, Constants.SELECTION_HEADER_TITLE.TITLE_MY_PROPERTIRS);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER);
                PropertyList fragment3 = new PropertyList();
                fragment3.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment3, true);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL:
                isClick = false;
                MainActivity.showIcon(1, Constants.SELECTION_HEADER_TITLE.TITLE_RENTAL);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL);
                PropertyList fragment4 = new PropertyList();
                fragment4.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment4, true);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE:
                isClick = false;
                MainActivity.showIcon(4, Constants.SELECTION_HEADER_TITLE.SELECTE_SERVICE);
                bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE);
                Services fragment5 = new Services();
                fragment5.setArguments(bundle);
                mBaseAppCompatActivity.replaceFragment(fragment5, true);
                break;

        }

    }


    @OnClick({R.id.relativeItemMainLayout, R.id.relativeViewPagerBox})
    void detailsViewClickEvent() {
        if (mListDataResponse != null && !mListDataResponse.equals("")) {
            if (mListDataResponse.get(isSelectedPosition).getIsAvailable().equalsIgnoreCase("1")) {
                ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(((BaseAppCompatActivity) mBaseAppCompatActivity), mListDataResponse.get(isSelectedPosition).getCommonId(), mListDataResponse.get(isSelectedPosition).getData().getLoginId(), viewType, mListDataResponse.get(isSelectedPosition).isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
            } else {
                if (mListDataResponse.get(isSelectedPosition).getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                    ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mListDataResponse.get(isSelectedPosition).getCommonId(), mListDataResponse.get(isSelectedPosition).getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mListDataResponse.get(isSelectedPosition).isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
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
                    mListDataResponse.get(isSelectedPosition).setIsAvailable("0");
                }
                //update api call
                callPropetyListApi(false);
            }
            if (requestCode == REQUEST_UPLOAD_FUND) {
                //update api call
                callPropetyListApi(false);
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

    @OnClick(R.id.mConstraintLayoutServiceLayout)
    void serviceViewClickEvent() {
        mBaseAppCompatActivity.startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mServiceDataList.get(isSelectedPosition).getServiceId(), mListDataResponse.get(isSelectedPosition).getData().getLoginId(), viewType, mListDataResponse.get(isSelectedPosition).isExpiriedStatus()), true);
    }


    void viewLeftButtonSelected(String str1, String str2, int typeId) {

        //set default values
        isTypeId = typeId;
        lableStr1 = str1;
        lableStr2 = str2;
        //set default selected
        viewLeftButton();
    }


    void viewLeftButton() {

        if (isTypeId == 0) {
            //show hide view
            isScreenType = Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER;
            viewType = Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY;
            if (mCusrrentLocation != null) {
//                callPropetyListApi(true);
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
                getActivity().checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            this.requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSIONS_CODE);
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
        mFusedLocationHelper = new FusedLocationHelper(getActivity())
                .requestSingleLocation()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setLocationUpdateListener(this);

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
        if (mFusedLocationHelper != null) {
            mFusedLocationHelper.requestSingleLocation();
        }

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
                mMap.getUiSettings().setZoomControlsEnabled(false);
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


                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {

                        int pos = (int) marker.getTag();
                        if (pos > mListDataResponse.size()) {
                            //open alert dialog
                            //check marke data with selection data

                            for (int ff = 0; ff < mFindMyDealList.size(); ff++) {
                                String address = marker.getTitle();
                                if (address.equalsIgnoreCase(mFindMyDealList.get(ff).getAddress())) {
                                    Log.e("TAG", "Deal id-- " + mFindMyDealList.get(ff).getAddress());
                                    showDialogForBeforeUploadProperty(marker, mFindMyDealList.get(ff).getDealId());
                                    break;
                                }
                            }
                        } else {
                            //Property and Rental
                            isSelectedPosition = pos;
                            setDataOfFirstItemView(mListDataResponse, pos);
                        }
                        return false;
                    }
                });


                mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
                    @Override
                    public void onCameraMoveStarted(int i) {
                        Log.e("Move", "Postion" + i);

                        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                            @Override
                            public void onCameraChange(CameraPosition cameraPosition) {
                                //Log.d("Camera postion change" + "", cameraPosition + "");
                                LatLng mCenterLatLong = cameraPosition.target;
                                try {

                                    Location mLocation = new Location("");
                                    mLocation.setLatitude(mCenterLatLong.latitude);
                                    mLocation.setLongitude(mCenterLatLong.longitude);

                                    mCusrrentLocation = mLocation;
                                    //Log.e("Location Point", "Lat : " + mCenterLatLong.latitude + "," + "Long : " + mCenterLatLong.longitude);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
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

    public void showDialogForBeforeUploadProperty(Marker marker, String dealID) {

        Dialog mDialog = new Dialog(getActivity());
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
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color));
                } else {
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color_dark_grey));
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
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color));
                } else {
                    is_similar_deal = "0";
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color_dark_grey));
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
        mDialog.show();

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
        onLocationChanged(mCusrrentLocation);
//        }
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        Log.d(TAG, "onViewStateRestored()");
    }

    //location
    // Trigger new location updates at interval
//    protected void startLocationUpdates() {
//        // Create the location request to start receiving updates
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
//
//        // Create LocationSettingsRequest object using location request
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
//        builder.addLocationRequest(mLocationRequest);
//        LocationSettingsRequest locationSettingsRequest = builder.build();
//
//        // Check whether location settings are satisfied
//        // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
//        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
//        settingsClient.checkLocationSettings(locationSettingsRequest);
//
//        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
//        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//
//        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(mLocationRequest, new LocationCallback() {
//                    @Override
//                    public void onLocationResult(LocationResult locationResult) {
//                        // do work here
//                        onLocationChanged(locationResult.getLastLocation());
//                    }
//                },
//                Looper.myLooper());
//    }
    //end\


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
        Log.d(TAG, "location" + location.getLatitude() + "-" + location.getLatitude());
        mCusrrentLocation = location;
        //call for update loction
        if (UserPreference.getInstance(BaseApplication.getInstance()).isUserLogin()) {
//            callUpdateLocation();
        }
    }

    private void callUpdateLocation() {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        linkedHashMap.put("long", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));


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


    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mCusrrentLocation.getLatitude(), mCusrrentLocation.getLongitude()), 8.55f));
            callPropetyListApi(true);
        } catch (Exception e) {
            Log.e("Exception: %s", e.getMessage());
        }
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
            callPropetyListApi(false);
        }
    }

    private void steOnMapPins() {
        // Add a marker in Sydney and move the camera
        if (mMap != null)
            mMap.clear();

        if (mListDataResponse != null && mListDataResponse.size() != 0) {
            for (int i = 0; i < mListDataResponse.size(); i++) {

                if (mListDataResponse.get(i).getLat() != null && !mListDataResponse.get(i).getLat().equalsIgnoreCase("")) {
                    if (mListDataResponse.get(i).getLang() != null && !mListDataResponse.get(i).getLang().equalsIgnoreCase("")) {
                        LatLng sydney = new LatLng(Double.parseDouble(mListDataResponse.get(i).getLat()), Double.parseDouble(mListDataResponse.get(i).getLang()));

                        //checked type
                        if (isTypeId == 0) {
                            //property
                            //checl property type
                            BitmapDescriptor bitmapDescriptorFactory;

                            if (mListDataResponse.get(i).getPropertyListed() != null && !mListDataResponse.get(i).getPropertyListed().equalsIgnoreCase("")) {

                                if (mListDataResponse.get(i).getPropertyListed().equalsIgnoreCase("1")) {
                                    bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell_o);
                                } else {
                                    bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell);
                                }
                            } else {
                                bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.buysell);
                            }
                            _myLocation = mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .zIndex(i)
                                    .title(mListDataResponse.get(i).getHouse())
                                    .icon(bitmapDescriptorFactory));
                            _myLocation.setTag(i);

                        } else if (isTypeId == 1) {
                            //rental
                            if (_myLocation != null)
                                _myLocation = null;

                            _myLocation = mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .zIndex(i)
                                    .title(mListDataResponse.get(i).getHouse())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.rental)));
                            _myLocation.setTag(i);
                        } else {
                            //service
                            _myLocation = mMap.addMarker(new MarkerOptions()
                                    .position(sydney)
                                    .zIndex(i)
                                    .title(mListDataResponse.get(i).getHouse())
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.service)));
                            _myLocation.setTag(i);
                        }
                    }
                }
            }
            //get my deal
            getMyDealList();
        }


    }

    private void setServiceOnMapPins() {
        // Add a marker in Sydney and move the camera
        if (mMap != null)
            mMap.clear();


        if (mServiceDataList != null && mServiceDataList.size() != 0) {
            for (int i = 0; i < mServiceDataList.size(); i++) {
                if (mServiceDataList.get(i).getLat() != null && !mServiceDataList.get(i).getLat().equalsIgnoreCase("")) {
                    if (mServiceDataList.get(i).getLang() != null && !mServiceDataList.get(i).getLang().equalsIgnoreCase("")) {
                        LatLng sydney = new LatLng(Double.parseDouble(mServiceDataList.get(i).getLat()), Double.parseDouble(mServiceDataList.get(i).getLang()));
                        _myLocation = mMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .zIndex(i)
                                .title(mServiceDataList.get(i).getTitle())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.service)));
                        _myLocation.setTag(i);
                    }
                }
            }
        }
    }


    //Property Api Calling
    private void callPropetyListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            return;
        }

        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));
        hashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        hashMap.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));
        hashMap.put("txt_sort", RequestBody.create(MediaType.parse("multipart/form-data"), mTextSortVaule));

        hashMap.put("range_by", RequestBody.create(MediaType.parse("multipart/form-data"), rangeby));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonListResponse> observable = userService.commonListApi(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonListResponse>() {
            @Override
            public void onSuccess(CommonListResponse response) {
                //Service view hide
                mConstraintLayoutServiceLayout.setVisibility(View.GONE);

                hideProgressDialog();
                if (mListDataResponse != null)
                    mListDataResponse.clear();
                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        mListDataResponse.addAll(response.getData());
                        //new code today
                        setDataOfFirstItemView(mListDataResponse, 0);
                        isSelectedPosition = 0;
                    } else {
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (response.getData() != null && !response.getData().equals("")) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        mListDataResponse.addAll(response.getData());
                        setDataOfFirstItemView(mListDataResponse, 0);
                        isSelectedPosition = 0;

                    } else {
//                        showToast(response.getText());
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }
                }
                //set pin on map view
                if (mMap != null)
                    steOnMapPins();
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

    //Rental api calling
    private void callRentalListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            return;
        }

        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        //needed changes
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_RENTAL));

        hashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        hashMap.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));
        hashMap.put("range_by", RequestBody.create(MediaType.parse("multipart/form-data"), rangeby));


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonListResponse> observable = userService.commonListApi(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonListResponse>() {
            @Override
            public void onSuccess(CommonListResponse response) {
                //Service view hide
                mConstraintLayoutServiceLayout.setVisibility(View.GONE);

                hideProgressDialog();
                if (mListDataResponse != null)
                    mListDataResponse.clear();
                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        mListDataResponse.addAll(response.getData());
                        //new code today
                        setDataOfFirstItemView(mListDataResponse, 0);
                        isSelectedPosition = 0;
                    } else {
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (response.getData() != null && !response.getData().equals("")) {
                        relativeItemMainLayout.setVisibility(View.VISIBLE);
                        mListDataResponse.addAll(response.getData());

                        setDataOfFirstItemView(mListDataResponse, 0);
                        isSelectedPosition = 0;

                    } else {
                        showToast(response.getText());
                        relativeItemMainLayout.setVisibility(View.GONE);
                    }
                }


                if (mMap != null)
                    steOnMapPins();
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

    //Service api calling
    private void callServiceListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));

        //new changes
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_SERVICE));
        hashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLatitude())));
        hashMap.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(mCusrrentLocation.getLongitude())));
        hashMap.put("range_by", RequestBody.create(MediaType.parse("multipart/form-data"), rangeby));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ServiceListResponse> observable;
        observable = userService.serviceList(hashMap);


        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ServiceListResponse>() {
            @Override
            public void onSuccess(ServiceListResponse response) {
                //property and rental view hide
                relativeItemMainLayout.setVisibility(View.GONE);

                hideProgressDialog();
                if (mServiceDataList != null)
                    mServiceDataList.clear();

                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mConstraintLayoutServiceLayout.setVisibility(View.VISIBLE);
                        mServiceDataList.addAll(response.getData());

                        setDataOfServiceFirstItemView(mServiceDataList, 0);
                        isSelectedPosition = 0;
                    } else {
                        mConstraintLayoutServiceLayout.setVisibility(View.GONE);
                    }

                } else {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mConstraintLayoutServiceLayout.setVisibility(View.VISIBLE);

                        mServiceDataList.addAll(response.getData());
                        setDataOfServiceFirstItemView(mServiceDataList, 0);

                        isSelectedPosition = 0;
                    } else {
//                        showToast(response.getText());
                        mConstraintLayoutServiceLayout.setVisibility(View.GONE);
                    }
                }
                if (mMap != null)
                    setServiceOnMapPins();
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

    private void openComingSonDialog() {
        //coming soon
        PromptDialog mPromptDialog = new PromptDialog(getActivity());
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Flippbidd");
        mPromptDialog.setContentText("Coming soon");
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(mBaseAppCompatActivity.getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
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
    private void setDataOfFirstItemView(List<CommonData> mHomeData, int index) {
        relativeItemMainLayout.setVisibility(View.VISIBLE);
        mConstraintLayoutPropertyUi.setVisibility(View.VISIBLE);

        final CommonData loMessage = mHomeData.get(index);

        CustomTextView txtPropertyName, txtPropertyLocation, txtPropertyPrice, txtUnlistedListedTag, textViewPropertyDetailsBedsNo,
                textViewPropertyDetailsBathNo, textViewAreaValues, textViewofChatTime;

        ImageView imagePropertyLikeStatus, imageTempView, imageViewUnavailableIconView;
        RelativeLayout relativeUnlistedListedTag, viewTimeCountDown;

        imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
        imageTempView = loView.findViewById(R.id.imageTempView);
        imageTempView.setVisibility(View.GONE);
        textViewPropertyDetailsBedsNo = loView.findViewById(R.id.textViewPropertyDetailsBedsNo);
        textViewPropertyDetailsBathNo = loView.findViewById(R.id.textViewPropertyDetailsBathNo);
        textViewAreaValues = loView.findViewById(R.id.textViewAreaValues);

        imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
        txtPropertyName = loView.findViewById(R.id.txtPropertyName);
        txtPropertyLocation = loView.findViewById(R.id.txtPropertyLocation);
        txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
        txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
        relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
        viewTimeCountDown = loView.findViewById(R.id.viewTimeCountDown);
        textViewofChatTime = loView.findViewById(R.id.textViewofChatTime);

        viewTimeCountDown.setVisibility(View.VISIBLE);


        txtPropertyName.setText(loMessage.getHouse());
        txtPropertyLocation.setText(loMessage.getAddress());

        if (loMessage.getRentAmount() != null && !loMessage.getRentAmount().equalsIgnoreCase("")) {

            if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
                txtPropertyPrice.setVisibility(View.GONE);
            } else {
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
            }

        } else {
            txtPropertyPrice.setVisibility(View.INVISIBLE);
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
                PhotosAdapter adapter = new PhotosAdapter(((BaseAppCompatActivity) mBaseAppCompatActivity), loMessage.getImages(), loMessage.getCommonId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
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
//            double miles = haversine(locationA.getAltitude(),locationA.getLongitude(),locationB.getAltitude(),locationB.getLongitude());

            txtDistanceMeter.setText(String.format("%.02f", miles) + " miles");
//            txtDistanceMeter.setText(String.format("%.02f", miles) + " Miles");
        } else {
            //hide location text
            txtDistanceMeter.setVisibility(View.GONE);
        }
//        updateTimeRemaining(System.currentTimeMillis(), loMessage);

        imagePropertyLikeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListDataResponse.get(isSelectedPosition).isLike()) {
                    mListDataResponse.get(isSelectedPosition).setLike(false);
                    imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
                    callLikesUnLikesApi(isSelectedPosition, mListDataResponse.get(isSelectedPosition), "0");
                } else {
                    mListDataResponse.get(isSelectedPosition).setLike(true);
                    imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                    callLikesUnLikesApi(isSelectedPosition, mListDataResponse.get(isSelectedPosition), "1");
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

    //chat view click
    void viewChatClickEvent() {
        mSelectedRentalData = null;
        if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mListDataResponse.get(isSelectedPosition).getData().getLoginId())) {
            mSelectedRentalData = mListDataResponse.get(isSelectedPosition);
            if (isSelectedString.equalsIgnoreCase("property")) {
                mScreenName = isSelectedString;
            } else {
                mScreenName = isSelectedString;
            }

            if (mSelectedRentalData.getHouse() != null && !mSelectedRentalData.getHouse().equalsIgnoreCase("")) {
                chatName = mSelectedRentalData.getHouse();
            } else {
                chatName = mSelectedRentalData.getTitle();
            }

            if (mSelectedRentalData.getImages() != null && mSelectedRentalData.getImages().size() != 0) {
                chatCoverUrl = mSelectedRentalData.getImages().get(0).getImageUrl();
            } else {
                chatCoverUrl = "";
            }

            ownerID = mSelectedRentalData.getData().getEmailAddress();
            propertyID = mSelectedRentalData.getCommonId();

            List<String> mSelectedIds = new ArrayList<>();
            //add user
            mSelectedIds.add(PreferenceUtils.getUserId());//current user id
            mSelectedIds.add(mSelectedRentalData.getData().getEmailAddress()); //other user id

            //call api for chat
            checkChannelStatus(true, mSelectedIds);


        }
    }


    public String getAddress(String address, String ownerID, String addedby, String propertyID, String channelMainID) {
        /*address**ownerID**addedby**propertyID**channelMainID*/
        return address + "##" + ownerID + "##"
                + addedby + "##" + propertyID + "##" + channelMainId;
    }

    /*server base chat manage*/
    private void checkChannelStatus(boolean isProgress, List<String> selectedId) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
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

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
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
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
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

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
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
            if (mSelectedRentalData != null && !mSelectedRentalData.equals("")) {
                chatAddress = getAddress(mSelectedRentalData.getAddress(), mSelectedRentalData.getData().getEmailAddress(), mSelectedRentalData.getData().getFullName(), mSelectedRentalData.getCommonId(), channelMainId);

                Log.e("TAG", "Main Channel Id " + channelMainId);
            }

/*
            if(mSeletcedServiceData!=null && !mSeletcedServiceData.equals("")){
                chatAddress = getAddress(mSeletcedServiceData.getAddress(), mSeletcedServiceData.getData().getEmailAddress(), mSeletcedServiceData.getData().getFullName(), mSeletcedServiceData.getServiceId(), channelMainId);
            }
*/


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
        Intent mainIntent = new Intent(mBaseAppCompatActivity, GroupChatActivity.class);
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
    private void callLikesUnLikesApi(final int position, CommonData mPropertyData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getCommonId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(false);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
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
            if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mServiceData.getLoginId())) {
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


    @OnClick({R.id.btnProperty, R.id.btnRental, R.id.btnServices})
    void btnTopClickEvent(View mView) {

        switch (mView.getId()) {
            case R.id.btnProperty:
                isSelectedString = "property";
                colorChanges(btnProperty, btnRental, btnServices);
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_BUYER, Constants.SELECTION_HEADER_TITLE.SELECTE_SELLER, 0);
                break;
            case R.id.btnRental:
                openComingSonDialog();
/*
                isSelectedString = "rental";
                colorChanges(btnRental, btnProperty, btnServices);
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_TENANT, Constants.SELECTION_HEADER_TITLE.SELECTE_LANDLOARD, 1);
*/
                break;
            case R.id.btnServices:
                openComingSonDialog();
/*
                isSelectedString = "service";
                colorChanges(btnServices, btnRental, btnProperty);
                viewLeftButtonSelected(Constants.SELECTION_HEADER_TITLE.SELECTE_HOMEOWNER, Constants.SELECTION_HEADER_TITLE.SELECTE_SERVICE, 2);
*/
                break;

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
        Log.e("Moved", "Id-->" + i);
        hideView();
    }

    private void hideView() {
        relativeItemMainLayout.setVisibility(View.GONE);
        mConstraintLayoutServiceLayout.setVisibility(View.GONE);
    }


    //SERVICE VIWE click event

    @OnClick(R.id.imgHeart)
    void clickServieceLikes() {

        if (mServiceDataList.get(isSelectedPosition).isLike()) {
            mServiceDataList.get(isSelectedPosition).setLike(false);
            imgHeart.setImageResource(R.drawable.unfav);
            callServiceLikesUnLikesApi(isSelectedPosition, mServiceDataList.get(isSelectedPosition), "0");
        } else {
            mServiceDataList.get(isSelectedPosition).setLike(true);
            imgHeart.setImageResource(R.drawable.fav);
            callServiceLikesUnLikesApi(isSelectedPosition, mServiceDataList.get(isSelectedPosition), "1");
        }


    }


    private void callServiceLikesUnLikesApi(final int position, ServiceListData mServiceData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
            return;
        }
        //token,login_id,service_id,is_like
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), mServiceData.getServiceId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.isServiceLike(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {

                } else {
                    showToast(response.getText());
                }

                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


    //service chat create
    @OnClick(R.id.imgMessage)
    void viewChatservice() {
        mSeletcedServiceData = mServiceDataList.get(isSelectedPosition);
    }


    /*get my deal*/
    private void getMyDealList() {

        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<RecentlySearchResponse> observable;
        observable = userService.getFindDealList(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<RecentlySearchResponse>() {
            @Override
            public void onSuccess(RecentlySearchResponse response) {
                hideProgressDialog();
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //set pin
                    if (mFindMyDealList != null) {
                        mFindMyDealList.clear();
                    }
                    mFindMyDealList.addAll(response.getData());
                    addPinMyDeal(response.getData());
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

    private void addPinMyDeal(List<DealData> data) {

        if (data != null && data.size() != 0) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getLat() != null && !data.get(i).getLat().equalsIgnoreCase("")) {
                    if (data.get(i).getLang() != null && !data.get(i).getLang().equalsIgnoreCase("")) {
                        LatLng sydney = new LatLng(Double.parseDouble(data.get(i).getLat()), Double.parseDouble(data.get(i).getLang()));
                        //my deal
                        int index;
                        if (i == 0) {
                            index = mListDataResponse.size() + 1;
                        } else {
                            index = mListDataResponse.size() + i;
                        }

                        BitmapDescriptor bitmapDescriptorFactory;
                        bitmapDescriptorFactory = BitmapDescriptorFactory.fromResource(R.drawable.my_deal);
                        _myLocation = mMap.addMarker(new MarkerOptions()
                                .position(sydney)
                                .zIndex(index)
//                                .snippet(data.get(i).getDealId())
                                .title(data.get(i).getAddress())
                                .icon(bitmapDescriptorFactory));
                        _myLocation.setTag(index);
                        _myLocation.setVisible(true);
                        _myLocation.hideInfoWindow();
                    }
                }
            }
        }
    }
}