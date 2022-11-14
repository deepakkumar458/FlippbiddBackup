package com.flippbidd.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.flippbidd.Adapter.CommonListData.NewPropertyListDataAdapter;
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CommonClass.PaginationScrollListener;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonList.CommonListResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.Response.TypeList.CommonTypeResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PropertyList extends BaseFragment implements NewPropertyListDataAdapter.onItemClickLisnear, SwipeRefreshLayout.OnRefreshListener, RefreshListener {
    private static final String TAG = LogUtils.makeLogTag(PropertyList.class);

    Disposable disposable;

    @BindView(R.id.recyclerViewPropertyList)
    RecyclerView recyclerViewPropertyList;
    @BindView(R.id.swipeRefreshViewPropertyList)
    SwipeRefreshLayout swipeRefreshViewPropertyList;

    LinearLayoutManager mLinearLayoutManager;
    //Property Adapter
    NewPropertyListDataAdapter mPropertyListing;

    @BindView(R.id.textViewSelectFilterPropertyStateTitle)
    CustomTextView textViewSelectFilterPropertyStateTitle;
    @BindView(R.id.imageSelectFilterPropertyState)
    ImageView imageSelectFilterPropertyState;
    @BindView(R.id.textViewSelectFilterPropertyCityTitle)
    CustomTextView textViewSelectFilterPropertyCityTitle;
    @BindView(R.id.imageSelectFilterPropertyCity)
    ImageView imageSelectFilterPropertyCity;
    @BindView(R.id.textMinPrice)
    CustomEditText textMinPrice;
    @BindView(R.id.textMaxPrice)
    CustomEditText textMaxPrice;
    @BindView(R.id.mRelativeProgress)
    RelativeLayout mRelativeProgress;

    @BindView(R.id.btnGo)
    CustomAppCompatButton btnGo;
    @BindView(R.id.btnReset)
    CustomAppCompatButton btnReset;
    @BindView(R.id.relativeLayoutFilterPropertyCityBox)
    RelativeLayout relativeLayoutFilterPropertyCityBox;
    @BindView(R.id.relativeLayoutFilterPropertySateBox)
    RelativeLayout relativeLayoutFilterPropertySateBox;

    View viewNoData;
    public static CustomTextView textViewNoDataMessage;
    private String mStateId = "", mCityId = "", mTextValuesMinPrice = "", mTextValuesMaxPrice = "";
    public static String isScreenType;
    private String mTextSortVaule = "newToold", mTextFileter = "";//newToold
    public static RelativeLayout linearLayoutHeardViewFilterSort;
    public static boolean isClickBack = true;

    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();

    public static RadioButton radioSortButton;
    public static Dialog dialog;
    //chat
    private String mScreenName;
    private CommonData mSelectedRentalData;

    private boolean isFlter = false;
    private List<CommonData> temAraa = new ArrayList<>();

    // Index from which pagination should start (0 is 1st page in our case)
    private int listLimite = 10;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    //new code 14/11/2018
    private ShimmerFrameLayout mShimmerViewContainer;

    /*chat */
    String channelStatus = "";
    String channelUrl = "";
    String channelMainId = "";
    String channelPropertyId = "";
    String propertyOwenrEmail = "";


    String chatName, chatAddress, chatCoverUrl, ownerID, propertyID;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_property_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        linearLayoutHeardViewFilterSort = (RelativeLayout) view.findViewById(R.id.linearLayoutHeardViewFilterSort);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        viewNoData = view.findViewById(R.id.viewNoData);
        textViewNoDataMessage = view.findViewById(R.id.textViewNoDataMessage);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isScreenType = bundle.getString("screen");
        }

        //check screen
        switch (isScreenType) {
            case Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER:
                mPropertyListing = new NewPropertyListDataAdapter(mBaseAppCompatActivity, isScreenType);
                viewPropertyInitialization();
                callStateApi(false);
                break;
            case Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER:
                setNodataView("Property");
                mPropertyListing = new NewPropertyListDataAdapter(mBaseAppCompatActivity, isScreenType);
                viewPropertyInitialization();
                break;
        }


        swipeRefreshViewPropertyList.setOnRefreshListener(this);
        swipeRefreshViewPropertyList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        MainActivity.image5_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showHideViw(false);
                CreateAlertDialogWithRadioButtonGroup(mBaseAppCompatActivity);
            }
        });


        MainActivity.msearView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTextFileter = newText;
                if (!mTextFileter.equalsIgnoreCase("")) {
                    isFlter = true;
                    //property list
                    callPropetyListApi(true);
                }
                return false;
            }
        });

        MainActivity.msearView.setOnCloseListener(new androidx.appcompat.widget.SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MainActivity.txt_title_toolbar.setVisibility(View.VISIBLE);
                MainActivity.image6_toolbar.setVisibility(View.VISIBLE);
                MainActivity.image5_toolbar.setVisibility(View.VISIBLE);
                mTextFileter = "";
                isFlter = false;
                //property list
                callPropetyListApi(true);
                return false;
            }
        });

        recyclerViewPropertyList.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems(RecyclerView recyclerView, int dx, int dy) {

                if (!recyclerView.canScrollVertically(-1)) {
//                    LogUtils.LOGE(TAG, "onScrolledToTop()");
                    mRelativeProgress.setVisibility(View.GONE);
                } else if (!recyclerView.canScrollVertically(1)) {
                    //hide progress bottom view
                    if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
                        mRelativeProgress.setVisibility(View.GONE);
                        openErorrDialog(getString(R.string.no_internet));
                        return;
                    }

                    if (!isLastPage || isFlter) {
                        mRelativeProgress.setVisibility(View.VISIBLE);
                        isLoading = true;
                        // mocking network delay for API call
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                loadNextPage();
                            }
                        }, 1000);
                    }
                }

            }

            @Override
            public int getTotalPageCount() {
                return temAraa.size();
            }

            @Override
            public boolean isLastPage() {
                return false;
            }

            @Override
            public boolean isLoading() {
                return false;
            }
        });

    }


    @Override
    public void onPause() {
        mShimmerViewContainer.stopShimmer();
        super.onPause();
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void loadNextPage() {
        listLimite += 5;
        isLoading = false;
        callPropetyListApi(false);
    }

    public static void setNodataView(String textView) {

        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER) || isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
            //Rental list
            textViewNoDataMessage.setText("No " + textView + " available.");
        } else {
            //property list
            textViewNoDataMessage.setText(Constants.no_data_found + textView + Constants.no_data_found_append);
        }

    }


    public void viewPropertyInitialization() {
        //set layout manager
        serRecyclerViewlayout();
        //set data in adapter
        recyclerViewPropertyList.setAdapter(mPropertyListing);
        mPropertyListing.setItemOnClickEvent(this);

        callPropetyListApi(true);
    }

    public void serRecyclerViewlayout() {
        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewPropertyList.setLayoutManager(mLinearLayoutManager);
        recyclerViewPropertyList.setItemAnimator(new DefaultItemAnimator());
    }


    private void callPropetyListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(false);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));

        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));

        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER)) {
            if (isFlter) {
                if (mTextFileter != null && !mTextFileter.equalsIgnoreCase("")) {
                    hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                    hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                } else {
                    hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
                    hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
                }
                hashMap.put("min_price", RequestBody.create(MediaType.parse("multipart/form-data"), mTextValuesMinPrice));
                hashMap.put("max_price", RequestBody.create(MediaType.parse("multipart/form-data"), mTextValuesMaxPrice));
                hashMap.put("txt_filter", RequestBody.create(MediaType.parse("multipart/form-data"), mTextFileter));
            }
        }
        hashMap.put("txt_sort", RequestBody.create(MediaType.parse("multipart/form-data"), mTextSortVaule));

        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER)) {
            hashMap.put("flag", RequestBody.create(MediaType.parse("multipart/form-data"), "my"));
        }

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonListResponse> observable = userService.commonListApi(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonListResponse>() {
            @Override
            public void onSuccess(CommonListResponse response) {
                hideProgressDialog();
                hideSwipeRefreshView();
                mRelativeProgress.setVisibility(View.GONE);
                if (response.getSuccess()) {
                    recyclerViewPropertyList.setVisibility(View.VISIBLE);
                    viewNoData.setVisibility(View.GONE);
                    if (temAraa != null) {
                        if (temAraa.size() == response.getData().size()) {
                            isLastPage = true;
                        }
                        temAraa.clear();
                    }
                    temAraa.addAll(response.getData());
                    mPropertyListing.addAll(temAraa);


                } else {
                    isLastPage = true;
                    if (response.getData() != null && !response.getData().equals("")) {
                        recyclerViewPropertyList.setVisibility(View.VISIBLE);
                        viewNoData.setVisibility(View.GONE);
                        mPropertyListing.addAll(response.getData());
                    } else {

                        setNodataView("Property");
                        recyclerViewPropertyList.setVisibility(View.GONE);
                        viewNoData.setVisibility(View.VISIBLE);
                    }
                }
                // stop animating Shimmer and hide the layout
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
            }

            @Override
            public void onFailed(Throwable throwable) {

                try {
                    isLastPage = true;
                    hideProgressDialog();
                    hideSwipeRefreshView();
                    if (mRelativeProgress != null) {
                        mRelativeProgress.setVisibility(View.GONE);
                    }

                } catch (Exception e) {

                }

            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onClickEvent(int position, CommonData mPropertyData, String mActionType) {
        switch (mActionType) {
            case Constants.ACTION.PROFILE_VIEW_ACTION:
                if (mPropertyData.getData() != null && !mPropertyData.getData().equals("")) {
                    startActivity(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), PropertyOtherUserDetailsActivity.class)
                            .putExtra(PropertyOtherUserDetailsActivity.USER_ID, mPropertyData.getData().getLoginId()));
                    (getActivity()).overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
                break;
            case Constants.ACTION.DELETE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.DELETE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_delete_message), Constants.ACTION.DELETE_ACTION);
                break;
            case Constants.ACTION.VIEW_ACTION:
                if (mPropertyData != null && !mPropertyData.equals("")) {
                    if (mPropertyData.getIsAvailable().equalsIgnoreCase("1")) {
                        ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mPropertyData.getCommonId(), mPropertyData.getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mPropertyData.isExpiriedStatus()), 666);
                    } else {
                        if (mPropertyData.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                            ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mPropertyData.getCommonId(), mPropertyData.getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mPropertyData.isExpiriedStatus()), 666);
                        } else {
                            showNotAvailable();
                        }
                    }
                } else {
                    showNotAvailable();
                }
                break;
            case Constants.ACTION.EDIT_ACTION:
                mBaseAppCompatActivity.startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.EDIT), Constants.REQUEST_UPLOAD_FUND, true);
                break;
            case Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.UNAVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_unavailable_message), Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);
                break;
            case Constants.ACTION.PROPERTY_AVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.AVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_available_message), Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                break;
            case Constants.ACTION.PROPERTY_LIKES_ACTION:
                callLikesUnLikesApi(position, mPropertyData, "1");
                break;
            case Constants.ACTION.PROPERTY_UNLIKES_ACTION:
                callLikesUnLikesApi(position, mPropertyData, "0");
                break;
            case Constants.ACTION.MESSAGES_VIEW_ACTION:
                mSelectedRentalData = null;
                if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mPropertyData.getData().getLoginId())) {
                    if (!mPropertyData.isExpiriedStatus()) {
                        mSelectedRentalData = mPropertyData;
                        mScreenName = "property";

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

                    } else {
                        openMessageLimiOver();
                    }
                }
                break;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 666) {
            callPropetyListApi(false);
        }
    }


    private void callLikesUnLikesApi(final int position, CommonData mPropertyData, String isLikes) {
        //property
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
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
                    mPropertyListing.updateAdapter(position);
                } else {
                    openErorrDialog(response.getText());
                }
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    private void callUnAvailableApi(final int position, CommonData mPropertyData, String isAvailable) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getCommonId()));
        hashMap.put("is_available", RequestBody.create(MediaType.parse("multipart/form-data"), isAvailable));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.propertyUnavailable(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess())
                    mPropertyListing.updateAdapter(position);
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    private void callDeleteApi(final int position, final CommonData mPropertyData) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getCommonId()));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.propertyDelete(hashMap);


        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    if (mPropertyListing != null) {
                        mPropertyListing.deleteAdapter(position);
                    }
                    if (position == 0) {
                        callPropetyListApi(true);
                    } else {
                        callPropetyListApi(false);
                    }
                }
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    //Get State
    private void callStateApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
//            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_STATE));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mStateList != null)
                    mStateList.clear();

                hideProgressDialog();

                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mStateList.addAll(response.getData());
                        //textViewSelectFilterPropertyStateTitle.setText(response.getData().get(0).getCommonName());
                        //mStateId = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectFilterPropertyStateTitle.setText("");
                    openErorrDialog("Something want to wrong load state.");
                }
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //Get City
    private void callCityApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_CITY));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));

        showProgressDialog(isProgress);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mCityList != null)
                    mCityList.clear();

                hideProgressDialog();
                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());
                    textViewSelectFilterPropertyCityTitle.setText("");
                    mCityId = "";
                    /*textViewSelectFilterPropertyCityTitle.setText(response.getData().get(0).getCommonName());
                    mCityId = response.getData().get(0).getCommonId();*/
                } else {
                    textViewSelectFilterPropertyCityTitle.setText("");
//                    openErorrDialog(getResources().getString(R.string.no_city_availabilityy));
                }


            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onRefresh() {
//        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_LANDLOARD) || isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
//            //Rental list
//            listLimite = 20;
//            callRentalListApi(false);
//        } else {
        //Property list
        listLimite = 10;
        callPropetyListApi(false);
//        }

    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            isClickBack = true;
            callPropetyListApi(isClickBack);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mShimmerViewContainer != null) {
            mShimmerViewContainer.startShimmer();
        }
        refresh(isClickBack);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        isClickBack = true;
    }

    public void hideSwipeRefreshView() {
        if (swipeRefreshViewPropertyList != null) {
            if (swipeRefreshViewPropertyList.isRefreshing()) {
                swipeRefreshViewPropertyList.setRefreshing(false);
            }
        }
    }


    public void DeleteDilaog(String headerTitle, final String mView, final int position, final CommonData mRentalData, final CommonData mPropertyData, String messages, final String mActionView) {

        ColorDialog dialog = new ColorDialog(mBaseAppCompatActivity);
        dialog.setTitle(headerTitle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(messages);
        dialog.setPositiveListener(getString(R.string.string_yes), new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
                try {

                    if (mView.equalsIgnoreCase("Property")) {
                        // use the value.
                        if (mActionView.equalsIgnoreCase(Constants.ACTION.DELETE_ACTION)) {
                            //Delete
                            callDeleteApi(position, mPropertyData);
                        } else {
                            if (mActionView.equalsIgnoreCase(Constants.ACTION.PROPERTY_AVAILABLE_ACTION)) {
                                callUnAvailableApi(position, mPropertyData, "1");
                            } else {
                                callUnAvailableApi(position, mPropertyData, "0");
                            }
                        }
                    } else {
                        if (mActionView.equalsIgnoreCase(Constants.ACTION.DELETE_ACTION)) {
                            //Delete
                            //callRentalDeleteApi(position, mRentalData);
                        } else {
                            if (mActionView.equalsIgnoreCase(Constants.ACTION.PROPERTY_AVAILABLE_ACTION)) {
                                //callRentalUnAvailableApi(position, mRentalData, "1");
                            } else {
                                //callRentalUnAvailableApi(position, mRentalData, "0");
                            }

                        }
                    }


                } catch (Exception e) {

                }
            }
        }).setNegativeListener(getString(R.string.string_no), new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    public static void showHideViw(Boolean isView) {
        if (linearLayoutHeardViewFilterSort.getVisibility() == View.GONE) {
            if (isView) {
                linearLayoutHeardViewFilterSort.animate()
                        .translationY(0).alpha(1.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                super.onAnimationStart(animation);
                                linearLayoutHeardViewFilterSort.setVisibility(View.VISIBLE);
                                linearLayoutHeardViewFilterSort.setAlpha(0.0f);
                            }
                        });
            }
        } else {
            linearLayoutHeardViewFilterSort.animate()
                    .translationY(0).alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            linearLayoutHeardViewFilterSort.setVisibility(View.GONE);
                        }
                    });
        }

    }

    private void CreateAlertDialogWithRadioButtonGroup(final Context mContext) {
        // custom dialog
        dialog = new Dialog(mContext);
        dialog.setContentView(R.layout.sort_dilaog_layout);
        dialog.setCancelable(true);
        RadioGroup rg = dialog.findViewById(R.id.radioVacantGroup);

        if (!mTextSortVaule.equalsIgnoreCase("")) {
            if (mTextSortVaule.equalsIgnoreCase(Constants.SELECTION_REQUEST_TYPE.TYPE_LTOH)) {
                radioSortButton = rg.findViewById(R.id.radioLowToHigh);
                radioSortButton.setChecked(true);
            } else if (mTextSortVaule.equalsIgnoreCase(Constants.SELECTION_REQUEST_TYPE.TYPE_HTOL)) {
                radioSortButton = rg.findViewById(R.id.radioHighToLow);
                radioSortButton.setChecked(true);
            } else if (mTextSortVaule.equalsIgnoreCase(Constants.SELECTION_REQUEST_TYPE.TYPE_NTOO)) {
                radioSortButton = rg.findViewById(R.id.radioNewToOld);
                radioSortButton.setChecked(true);
            } else if (mTextSortVaule.equalsIgnoreCase(Constants.SELECTION_REQUEST_TYPE.TYPE_OTON)) {
                radioSortButton = rg.findViewById(R.id.radioOldToNew);
                radioSortButton.setChecked(true);
            } else {
                radioSortButton = rg.findViewById(R.id.radioNewToOld);
                radioSortButton.setChecked(true);
            }
        } else {
            radioSortButton = rg.findViewById(R.id.radioNewToOld);
            radioSortButton.setChecked(true);
        }


        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int isSelectedListed = group.getCheckedRadioButtonId();
                radioSortButton = group.findViewById(isSelectedListed);
                if (radioSortButton != null) {
                    mTextSortVaule = radioSortButton.getTag().toString();
                    isFlter = true;
                    mStateId = "";
                    mCityId = "";
                    callApiView();
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }


    public void callApiView() {
        callPropetyListApi(true);
    }

    //show Message Limit Over Dialog
    private void openMessageLimiOver() {

        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
        mPromptDialog.setSingleButton(true);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_INFO);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Flippbidd");
        mPromptDialog.setContentText(mBaseAppCompatActivity.getResources().getString(R.string.messages_limit_error));
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }

    @OnClick({R.id.btnGo, R.id.btnReset, R.id.relativeLayoutFilterPropertyCityBox, R.id.relativeLayoutFilterPropertySateBox})
    void filterPropetyClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnGo:
                mTextValuesMinPrice = textMinPrice.getText().toString();
                mTextValuesMaxPrice = textMaxPrice.getText().toString();

                if (mTextValuesMinPrice != null && !mTextValuesMinPrice.equalsIgnoreCase("")
                        && mTextValuesMaxPrice != null && !mTextValuesMaxPrice.equalsIgnoreCase("")) {
                    if (Integer.valueOf(mTextValuesMinPrice) > Integer.valueOf(mTextValuesMaxPrice)) {
                        showToast("Minimum price not grater then max price");
                        return;
                    }
                }
                isFlter = true;
                linearLayoutHeardViewFilterSort.setVisibility(View.GONE);
                callPropetyListApi(false);
                break;
            case R.id.btnReset:
                clearData();
                showHideViw(false);
                break;
            case R.id.relativeLayoutFilterPropertyCityBox:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.relativeLayoutFilterPropertySateBox:
                if (mStateList != null && mStateList.size() != 0) {
                    showDialogStateView();
                }

                break;
        }
    }

    private void showDialogStateView() {

        final Dialog dialog = new Dialog(mBaseAppCompatActivity);
        dialog.setTitle("Select State");
        dialog.setContentView(R.layout.searchable_list_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);

        CustomEditText mCustomEditText = dialog.findViewById(R.id.search);
        ListView mListView = dialog.findViewById(R.id.listItems);


        final SearchableAdapter searchableAdapter = new SearchableAdapter(mBaseAppCompatActivity, mStateList);
        mListView.setAdapter(searchableAdapter);
        dialog.show();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectFilterPropertyStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
                mStateId = searchableAdapter.getItem(position).getCommonId();
                callCityApi(false);
                dialog.dismiss();
            }
        });

        //in your Activity or Fragment where of Adapter is instantiated :

        mCustomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                searchableAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    private void showDialogCityView() {

        final Dialog dialog = new Dialog(mBaseAppCompatActivity);
        dialog.setTitle("Select City");
        dialog.setContentView(R.layout.searchable_list_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);

        CustomEditText mCustomEditText = dialog.findViewById(R.id.search);
        ListView mListView = dialog.findViewById(R.id.listItems);


        final SearchableAdapter searchableAdapter = new SearchableAdapter(mBaseAppCompatActivity, mCityList);
        mListView.setAdapter(searchableAdapter);
        dialog.show();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectFilterPropertyCityTitle.setText(searchableAdapter.getItem(position).getCommonName());
                mCityId = searchableAdapter.getItem(position).getCommonId();
                dialog.dismiss();
            }
        });

        //in your Activity or Fragment where of Adapter is instantiated :
        mCustomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                searchableAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    @OnTouch({R.id.btnGo, R.id.btnReset, R.id.relativeLayoutFilterPropertyCityBox, R.id.relativeLayoutFilterPropertySateBox})
    boolean HideKeyBoardView() {
        hideKeyboard();
        return false;
    }


    private void clearData() {
        isFlter = false;
        textMaxPrice.setText("");
        textMinPrice.setText("");

        //clear data
        mCityId = "";
        mStateId = "";
        mTextValuesMaxPrice = "";
        mTextValuesMinPrice = "";

//        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_LANDLOARD) || isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
//            //Rental list
//            callRentalListApi(false);
//        } else {
        //Property list
        callPropetyListApi(false);
//        }
    }

    /*server base chat manage*/
    private void checkChannelStatus(boolean isProgress, List<String> selectedId) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
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
            openErorrDialog(getString(R.string.no_internet));
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


    public String getAddress(String address, String ownerID, String addedby, String propertyID, String channelMainID) {
        /*address**ownerID**addedby**propertyID**channelMainID*/
        return address + "##" + ownerID + "##"
                + addedby + "##" + propertyID + "##" + channelMainId;
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


}
