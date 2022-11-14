package com.flippbidd.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.flippbidd.Adapter.Service.CommonServiceListAdapter;
import com.flippbidd.Adapter.Spinner.CommonTypeListAdapter;
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CommonClass.PaginationScrollListener;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;

import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Service.ServiceListData;
import com.flippbidd.Model.Response.Service.ServiceListResponse;
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
import com.flippbidd.activity.Details.CommonDetails.CommonDetailsActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.Services.AddNewServicer;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.interfaces.Consts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Services extends BaseFragment implements RefreshListener, SwipeRefreshLayout.OnRefreshListener, CommonServiceListAdapter.onItemClickLisnear {
    private static final String TAG = LogUtils.makeLogTag(PostNewProperty.class);
    String isScreenType;
    public static RelativeLayout linearLayoutHeardViewFilterSort;

    @BindView(R.id.recycelerViweServiceList)
    RecyclerView recycelerViweServiceList;
    @BindView(R.id.swipeRefreshViewPropertyList)
    SwipeRefreshLayout swipeRefreshViewPropertyList;

    @BindView(R.id.textViewSelectStateTitle)
    CustomTextView textViewSelectStateTitle;
    @BindView(R.id.imageSelectState)
    ImageView imageSelectState;
    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.imageSelectCity)
    ImageView imageSelectCity;

    @BindView(R.id.spinnerCategories)
    Spinner spinnerCategories;
    @BindView(R.id.textViewSelectCategories)
    CustomTextView textViewSelectCategories;
    @BindView(R.id.imageSelectCategories)
    ImageView imageSelectCategories;

    @BindView(R.id.btnServiceGo)
    CustomAppCompatButton btnServiceGo;
    @BindView(R.id.relativeLayoutCityBox)
    RelativeLayout relativeLayoutCityBox;
    @BindView(R.id.relativeLayoutSateBox)
    RelativeLayout relativeLayoutSateBox;
    @BindView(R.id.relativeLayoutCategoriesBox)
    RelativeLayout relativeLayoutCategoriesBox;
    @BindView(R.id.main_progress)
    ProgressBar main_progress;
    @BindView(R.id.mRelativeProgress)
    RelativeLayout mRelativeProgress;

    @BindView(R.id.radioServiiceGroup)
    RadioGroup radioServiiceGroup;
    private View viewNodata;
    private CustomTextView customTextView;

    CommonTypeListAdapter mSpinnerCategoriesAdapter;
    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();
    List<CommonListData> mCategoriesList = new ArrayList<>();
    private String mStateId = "", mCityId = "", mCategoriesId = "", isServiceListed = "";
    private String mTextSortVaule = "", mTextFileter = "";
    CommonServiceListAdapter mCommonServiceListAdapter;
    GridLayoutManager mLinearLayoutManager;
    private RadioButton radioListedButton;
    Disposable disposable;
    public static boolean isClickBack = true;

    //hightTolow, lowTohigh, newToold, oldTonew
    public static RadioButton radioSortButton;
    public static Dialog dialog;

    private String mStringScreen, mCommonId, mScreenName, mLoginId;
    private int myQbID, QbIdOfRecUser;
    private String mCommonDialogId;
    private ServiceListData mSeletcedServiceData;
    private boolean isFlter = false;

    private int listLimite = 20;
    private boolean isLoading = false;

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);
        isClickBack = true;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_servicer_homeowener_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewNodata = view.findViewById(R.id.viewNodata);
        customTextView = view.findViewById(R.id.textViewNoDataMessage);
        Bundle bundle = getArguments();
        if (bundle != null) {
            isScreenType = bundle.getString("screen");
        }
        linearLayoutHeardViewFilterSort = view.findViewById(R.id.linearLayoutHeardViewFilterSort);
        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
            setNodataView("Services");
            callCategoriesApi(true);
            isHomeOwener();
        }

        mCommonServiceListAdapter = new CommonServiceListAdapter(mBaseAppCompatActivity, isScreenType);
        viewPropertyInitialization();

        swipeRefreshViewPropertyList.setOnRefreshListener(this);
        swipeRefreshViewPropertyList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        if (mCommonServiceListAdapter != null)
            mCommonServiceListAdapter.setItemOnClickEvent(this);


        MainActivity.image5_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAlertDialogWithRadioButtonGroup(mBaseAppCompatActivity);
            }
        });


        //search view
        /*MainActivity.msearView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mTextFileter = newText;
                if (!mTextFileter.equalsIgnoreCase("")) {
                    isFlter = true;
                    callServiceListApi(true);
                }
                return false;
            }
        });


        MainActivity.msearView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                MainActivity.txt_title_toolbar.setVisibility(View.VISIBLE);
                MainActivity.image6_toolbar.setVisibility(View.VISIBLE);
                MainActivity.image5_toolbar.setVisibility(View.VISIBLE);
//                if (!mTextFileter.equalsIgnoreCase("")) {
                mTextFileter = "";
                isFlter = false;
                callServiceListApi(true);
//                }
                return false;
            }
        });*/

        recycelerViweServiceList.addOnScrollListener(new PaginationScrollListener(mLinearLayoutManager) {
            @Override
            protected void loadMoreItems(RecyclerView recyclerView, int dx, int dy) {

                if (!recyclerView.canScrollVertically(-1)) {
                    LogUtils.LOGE(TAG, "onScrolledToTop()");
                    mRelativeProgress.setVisibility(View.GONE);
                } else if (!recyclerView.canScrollVertically(1)) {
                    //hide progress bottom view
                    if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
                        mRelativeProgress.setVisibility(View.GONE);
                        openErorrDialog(getString(R.string.no_internet));
                        return;
                    }
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

            @Override
            public int getTotalPageCount() {
                return 0;
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

    private void loadNextPage() {
        listLimite += 10;
        isLoading = false;
        callServiceListApi(false);
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
                    mStateId = "";
                    mCityId = "";
                    isFlter = true;
                    callServiceListApi(true);
                }
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        //visisblity of view
        try {
            MainActivity.txt_title_toolbar.setVisibility(View.VISIBLE);
            MainActivity.image6_toolbar.setVisibility(View.VISIBLE);
            MainActivity.image5_toolbar.setVisibility(View.VISIBLE);
            MainActivity.msearView.onActionViewCollapsed();
            MainActivity.msearView.setQuery("", false);
            MainActivity.msearView.clearFocus();
        } catch (Exception e) {

        }

    }

    public void viewPropertyInitialization() {
        //set layout manager
        serRecyclerViewlayout();
        //set data in adapter
        recycelerViweServiceList.setAdapter(mCommonServiceListAdapter);

        callServiceListApi(true);

    }

    public void serRecyclerViewlayout() {
        mLinearLayoutManager = new GridLayoutManager(mBaseAppCompatActivity, 1);
        recycelerViweServiceList.setLayoutManager(mLinearLayoutManager);
        recycelerViweServiceList.setItemAnimator(new DefaultItemAnimator());
    }


    private void isHomeOwener() {
        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
                textViewSelectCategories.setText(item);

                if (mCategoriesList != null && mCategoriesList.size() != 0) {
                    mCategoriesId = mCategoriesList.get(pos).getCommonId();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public static void showHideViw(Boolean isUp) {

        if (linearLayoutHeardViewFilterSort.getVisibility() == View.GONE) {
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

    //Service api calling
    private void callServiceListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));

        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
            if (isFlter) {
                if (mTextFileter != null && !mTextFileter.equalsIgnoreCase("")) {
                    hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                    hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                } else {
                    hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
                    hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
                }

                hashMap.put("category_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCategoriesId));
                hashMap.put("is_listed", RequestBody.create(MediaType.parse("multipart/form-data"), isServiceListed));
                hashMap.put("txt_filter", RequestBody.create(MediaType.parse("multipart/form-data"), mTextFileter));
                hashMap.put("txt_sort", RequestBody.create(MediaType.parse("multipart/form-data"), mTextSortVaule));
            }
        }


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ServiceListResponse> observable;
        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
            //My Rental / Landlord
            observable = userService.serviceList(hashMap);
        } else {
            //Rental / Tenant
            observable = userService.myServiceList(hashMap);
        }

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ServiceListResponse>() {
            @Override
            public void onSuccess(ServiceListResponse response) {
                hideProgressDialog();
                hideSwipeRefreshView();
                mRelativeProgress.setVisibility(View.GONE);
                if (response.getSuccess()) {
                    recycelerViweServiceList.setVisibility(View.VISIBLE);
                    viewNodata.setVisibility(View.GONE);
                    mCommonServiceListAdapter.addAll(response.getData());
                } else {

//                        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
                    if (response.getData() != null && !response.getData().equals("")) {
                        recycelerViweServiceList.setVisibility(View.VISIBLE);
                        viewNodata.setVisibility(View.GONE);
                        mCommonServiceListAdapter.addAll(response.getData());
                    } else {
                        setNodataView("Service");
                        recycelerViweServiceList.setVisibility(View.GONE);
                        viewNodata.setVisibility(View.VISIBLE);
                    }
//                        }
                }
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                mRelativeProgress.setVisibility(View.GONE);
                hideProgressDialog();
                hideSwipeRefreshView();
            }
        });

    }

    public void setNodataView(String textView) {
        if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
            customTextView.setText("No " + textView + " available.");
        } else {
            customTextView.setText(Constants.no_data_found + textView + Constants.no_data_found_append);
        }

    }

    public void hideSwipeRefreshView() {
        if (swipeRefreshViewPropertyList != null) {
            if (swipeRefreshViewPropertyList.isRefreshing()) {
                swipeRefreshViewPropertyList.setRefreshing(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refresh(isClickBack);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //MainActivity.showIcon(3, "");
    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            isClickBack = true;
            callServiceListApi(b);
        }
    }


    //Get Categories
    private void callCategoriesApi(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
//            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_SERVICE));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mCategoriesList != null)
                    mCategoriesList.clear();

                hideProgressDialog();

                if (response.getSuccess()) {
                    mSpinnerCategoriesAdapter = new CommonTypeListAdapter(mBaseAppCompatActivity,
                            R.layout.adapter_spinner_layout, response.getData());
                    spinnerCategories.setAdapter(mSpinnerCategoriesAdapter);
                    if (response.getData() != null && response.getData().size() != 0)
                        mCategoriesList.addAll(response.getData());
                }
                callStateApi(isProgress);
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

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
                        textViewSelectStateTitle.setText(response.getData().get(0).getCommonName());
                        mStateId = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectStateTitle.setText("");
                    openErorrDialog("Something want to wrong load state.");
                }


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
                hideProgressDialog();

                if (mCityList != null) {
                    mCityList.clear();
                }
                hideProgressDialog();
                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());
                    textViewSelectCityTitle.setText(response.getData().get(0).getCommonName());
                    mCityId = response.getData().get(0).getCommonId();
                } else {
                    textViewSelectCityTitle.setText("");
                    openErorrDialog(getResources().getString(R.string.no_city_availabilityy));
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onRefresh() {
        clearData();
    }

    @Override
    public void onClickEvent(int position, ServiceListData mServiceListData, String mActionType) {

        switch (mActionType) {
            case Constants.ACTION.DELETE_ACTION:
                //delete
                DeleteDilaog(position, mServiceListData, getResources().getString(R.string.string_delete_message));
                break;
            case Constants.ACTION.EDIT_ACTION:
                //Edit
                mBaseAppCompatActivity.startActivity(AddNewServicer.getIntentActivity(mBaseAppCompatActivity, mServiceListData, Constants.SCREEN_ACTION_TYPE.EDIT), true);
                break;
            case Constants.ACTION.VIEW_ACTION:
                mBaseAppCompatActivity.startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mServiceListData.getServiceId(), mServiceListData.getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE, false), true);
                break;
            case Constants.ACTION.PROPERTY_LIKES_ACTION:
                callServiceLikesUnLikesApi(position, mServiceListData, "1");
                break;
            case Constants.ACTION.PROPERTY_UNLIKES_ACTION:
                callServiceLikesUnLikesApi(position, mServiceListData, "0");
                break;
            case Constants.ACTION.MESSAGES_VIEW_ACTION:
                mSeletcedServiceData = mServiceListData;
                if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mLoginId)) {
//                    singToQB();
                }
                break;
        }
    }

    public void DeleteDilaog(final int position, final ServiceListData mServiceListData, String messages) {

        ColorDialog dialog = new ColorDialog(mBaseAppCompatActivity);
        dialog.setTitle(Constants.ACTION_HEADER_TYPE.DELETE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(messages);
        dialog.setPositiveListener(getString(R.string.string_yes), new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                try {
                    dialog.dismiss();
                    //call delete srvice api
                    callDeleteServiceApi(position, mServiceListData.getServiceId());
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


    private void callServiceLikesUnLikesApi(final int position, ServiceListData mServiceData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
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
                    mCommonServiceListAdapter.updateAdapter(position);
                } else {
                    openErorrDialog(response.getText());
                }

                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }


    private void callDeleteServiceApi(final int position, final String serviceId) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), serviceId));

        showProgressDialog(true);
        UserServices deleteService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = deleteService.deleteService(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    mCommonServiceListAdapter.deleteAdapter(position);
//                    deleteUser(serviceId);
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

/*    private void deleteUser(String mServiceId) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        String mString = mServiceId + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_service";

        QBChatService chatService = QBChatService.getInstance();
        boolean isLoggedIn = chatService.isLoggedIn();
        if (isLoggedIn) {
            ChatHelper.getInstance().logout(mBaseAppCompatActivity);
        }
        QBUser qbUser = new QBUser(mString, Consts.userPassword);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();

                QBUsers.deleteUser(qbUser.getId()).performAsync(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        mBaseAppCompatActivity.seekBarShow("Delete User Successfully!", getResources().getColor(R.color.colorAccent));
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        //LogUtils.LOGE(TAG, "Delete User Error!");
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                //LogUtils.LOGE(TAG, "Login User Error!");
                hideProgressDialog();
            }
        });
    }*/

    @OnClick({R.id.btnServiceGo, R.id.relativeLayoutCityBox, R.id.relativeLayoutSateBox, R.id.relativeLayoutCategoriesBox})
    void serviceFiletClickEvent(View view) {
        switch (view.getId()) {
            case R.id.relativeLayoutCityBox:
//                spinnerSelectCity.performClick();
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }

                break;
            case R.id.relativeLayoutSateBox:
//                spinnerSelectState.performClick();
                if (mStateList != null && mStateList.size() != 0)
                    showDialogStateView();
                break;
            case R.id.relativeLayoutCategoriesBox:
                spinnerCategories.performClick();
                break;
            case R.id.btnServiceGo:
                showHideViw(false);
                isFlter = true;
                int isSelectedListed = radioServiiceGroup.getCheckedRadioButtonId();
                radioListedButton = radioServiiceGroup.findViewById(isSelectedListed);
                if (radioListedButton != null) {
                    if (radioListedButton.getText().toString().equalsIgnoreCase("Yes")) {
                        isServiceListed = "1";
                    } else {
                        isServiceListed = "0";
                    }
                }
                callServiceListApi(true);
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
                textViewSelectStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
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
                textViewSelectCityTitle.setText(searchableAdapter.getItem(position).getCommonName());
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


    @OnTouch({R.id.btnServiceGo, R.id.relativeLayoutCityBox, R.id.relativeLayoutSateBox, R.id.relativeLayoutCategoriesBox})
    boolean hideKeyBoardView() {
        hideKeyboard();
        return false;
    }


    //new code for chat
    private String getAppendString() {

        String mString = mSeletcedServiceData.getServiceId() + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_service";
        return mString;
    }

   /* private void loginToQB() {
        //current user
        ArrayList<QBUser> qbUsers = new ArrayList<>();
        QBUser qbUser = new QBUser();
        qbUser.setId(myQbID);
        qbUser.setPassword(Consts.userPassword);
        qbUser.setFullName(getAppendString());
        qbUser.setCustomData(mSeletcedServiceData.getData().getProfilePic());
        qbUsers.add(qbUser);

        // user
        QBUser qbUser1 = new QBUser();
        qbUser1.setId(QbIdOfRecUser);
        qbUser1.setPassword(Consts.userPassword);
        qbUser1.setCustomData(mSeletcedServiceData.getData().getProfilePic());
        qbUsers.add(qbUser1);


        NewChatActivity.startForResult(mBaseAppCompatActivity, qbUsers,mCommonDialogId, mSeletcedServiceData.getServiceId(), "service", mSeletcedServiceData.getLoginId(),mSeletcedServiceData.getData().getFullName(),165);

    }*/

/*
    //chat login with Qb
    private void singToQB() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        QBUser qbUser = new QBUser(getAppendString(), Consts.userPassword);
        qbUser.setFullName(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName());

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                if (qbUser.getId() != null && !qbUser.getId().equals("")) {

                    // LogUtils.LOGD(TAG, "onSuccess() called with: response QbID= [" + qbUser.getId() + "]");
                    callUpdateQBID(qbUser.getId());
                }
            }

            @Override
            public void onError(QBResponseException e) {
                hideProgressDialog();
                //get Qb_id
                getQbID();
            }
        });
        // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
    }

    private void callUpdateQBID(final Integer qbId) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateQBID(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()
                , UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()
                , qbId
                , UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getUsername());

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    myQbID = qbId;
                    loginToQBs();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }


    private void getQbID() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), getAppendString()));


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable = userService.getQBID(linkedHashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    myQbID = response.getId();
                    loginToQBs();
                } else {
                    openErorrDialog(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void loginToQBs() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        QBChatService chatService = QBChatService.getInstance();
        boolean isLoggedIn = chatService.isLoggedIn();
        if (isLoggedIn) {
            ChatHelper.getInstance().logout(mBaseAppCompatActivity);
        }
        QBUser qbUser = new QBUser(getAppendString(), Consts.userPassword);
        QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                ChatHelper.getInstance();
                createDialog();
            }

            @Override
            public void onError(QBResponseException e) {
                //seekBarShow("Login", Color.RED);
            }
        });

        // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
    }

    private void createDialog() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        if (mSeletcedServiceData.getSenderQbId() != null && !mSeletcedServiceData.getSenderQbId().equalsIgnoreCase("")) {
            QbIdOfRecUser = Integer.parseInt(mSeletcedServiceData.getSenderQbId());

            ArrayList<Integer> occupantIdsList = new ArrayList<Integer>();
            occupantIdsList.add(myQbID);//my id
            occupantIdsList.add(QbIdOfRecUser);
            QBChatDialog dialog = DialogUtils.buildDialog(mSeletcedServiceData.getData().getFullName(), QBDialogType.PRIVATE, occupantIdsList);

            QBRestChatService.createChatDialog(dialog).performAsync(new QBEntityCallback<QBChatDialog>() {
                @Override
                public void onSuccess(QBChatDialog result, Bundle params) {
                    mCommonDialogId = "";
                    mCommonDialogId = result.getDialogId();
                    createCallApi(result.getDialogId());
                }

                @Override
                public void onError(QBResponseException responseException) {
                    // seekBarShow("Created Dialog", Color.RED);
                }
            });
        } else {
            //seekBarShow("User does't register in Qb.", Color.RED);
            //QB Id Not Available
            new PromptDialog(mBaseAppCompatActivity)
                    .setDialogType(PromptDialog.DIALOG_TYPE_WRONG)
                    .setAnimationEnable(true)
                    .setTitleText(getString(R.string.string_error))
                    .setContentText(getString(R.string.string_something_wrong_for_created_chat))
                    .setPositiveListener(getString(R.string.string_retry), new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                            //retry call sing up in QB
                            singToQB();
                        }
                    })
                    .setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                            //singToQB();
                        }
                    }).show();
        }


    }

    private void createCallApi(String dialogId) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("sender_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        linkedHashMap.put("receiver_id", RequestBody.create(MediaType.parse("multipart/form-data"), mSeletcedServiceData.getLoginId()));
        linkedHashMap.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(myQbID)));
        linkedHashMap.put("receiver_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(QbIdOfRecUser)));
        linkedHashMap.put("dialog_id", RequestBody.create(MediaType.parse("multipart/form-data"), dialogId));
        linkedHashMap.put("dialog_type", RequestBody.create(MediaType.parse("multipart/form-data"), "GROUP"));
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mSeletcedServiceData.getServiceId()));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "service"));


        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);

        Observable<CommonResponse> observable;

        observable = userService.chat(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
//                if (response.getSuccess()) {
                loginToQB();
//                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }
*/

    private void clearData() {
        isFlter = false;
        //clear data
        mCityId = "";
        mStateId = "";
        isServiceListed = "";
        mTextSortVaule = "";
        mTextFileter = "";
        callServiceListApi(false);
    }
}
