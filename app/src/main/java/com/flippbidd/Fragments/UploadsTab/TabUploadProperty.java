package com.flippbidd.Fragments.UploadsTab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.flippbidd.Adapter.CommonListData.NewPropertyListDataAdapter;
import com.flippbidd.Bottoms.LoginUserMenuBottomSheetDialogFragment;
import com.flippbidd.CommonClass.PaginationScrollListener;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Fragments.NewHomeFragments;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonList.CommonListResponse;
import com.flippbidd.Model.Response.CommonResponse;
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
import cn.refactor.lib.colordialog.ColorDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabUploadProperty extends BaseFragment implements RefreshListener, NewPropertyListDataAdapter.onItemClickLisnear, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = LogUtils.makeLogTag(TabUploadProperty.class);

    @BindView(R.id.recyclerViewPropertyList)
    RecyclerView recyclerViewPropertyList;
    @BindView(R.id.swipeRefreshViewPropertyList)
    SwipeRefreshLayout swipeRefreshViewPropertyList;
    @BindView(R.id.mRelativeProgress)
    RelativeLayout mRelativeProgress;

    LinearLayoutManager mLinearLayoutManager;
    //Property Adapter
//    CommonListDataAdapter mPropertyListing;
    NewPropertyListDataAdapter mPropertyListing;
    Disposable disposable;
    private String mTextSortVaule = "";

    private List<CommonData> temAraa = new ArrayList<>();

    // Index from which pagination should start (0 is 1st page in our case)
    private int listLimite = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private ShimmerFrameLayout mShimmerViewContainer;

    //chat code
    View viewNoData;
    public static CustomTextView textViewNoDataMessage;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_property_layout;
    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            if (mPropertyListing != null)
                mPropertyListing.notifyDataSetChanged();
        }
    }

    private void loadNextPage() {

        listLimite += 10;
        isLoading = false;
        callPropetyListApi();
    }

    public void serRecyclerViewlayout() {
        mPropertyListing = new NewPropertyListDataAdapter(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER);

        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewPropertyList.setLayoutManager(mLinearLayoutManager);
        recyclerViewPropertyList.setItemAnimator(new DefaultItemAnimator());
        //set data in adapter
        recyclerViewPropertyList.setAdapter(mPropertyListing);
        if (mPropertyListing != null)
            mPropertyListing.setItemOnClickEvent(this);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        serRecyclerViewlayout();

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        viewNoData = view.findViewById(R.id.viewNoData);
        textViewNoDataMessage = view.findViewById(R.id.textViewNoDataMessage);

        swipeRefreshViewPropertyList.setOnRefreshListener(this);
        swipeRefreshViewPropertyList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        //load more data
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

                    if (!isLastPage) {
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

    private void callPropetyListApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
//        showProgressDialog(isProgress);
        showProgressDialog(false);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(listLimite)));
        hashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));
        hashMap.put("flag", RequestBody.create(MediaType.parse("multipart/form-data"), "my"));


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
                    //Log.e("FlippBIdd", "Temp Array size-->" + temAraa.size());
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
//                    if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER)) {
                    if (response.getData() != null && !response.getData().equals("")) {
                        recyclerViewPropertyList.setVisibility(View.VISIBLE);
                        viewNoData.setVisibility(View.GONE);
                        mPropertyListing.addAll(response.getData());
                    } else {

                        setNodataView("Property");
                        recyclerViewPropertyList.setVisibility(View.GONE);
                        viewNoData.setVisibility(View.VISIBLE);
                    }
//                    }
                }
                // stop animating Shimmer and hide the layout
                mShimmerViewContainer.stopShimmer();
                mShimmerViewContainer.setVisibility(View.GONE);
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
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

//                    // stop animating Shimmer and hide the layout
//                    mShimmerViewContainer.stopShimmer();
//                    mShimmerViewContainer.setVisibility(View.GONE);
                } catch (Exception e) {

                }

            }
        });

    }

    public static void setNodataView(String textView) {
        //property list
        textViewNoDataMessage.setText(Constants.no_data_found + textView + Constants.no_data_found_append);


    }

    public void hideSwipeRefreshView() {
        if (swipeRefreshViewPropertyList != null) {
            if (swipeRefreshViewPropertyList.isRefreshing()) {
                swipeRefreshViewPropertyList.setRefreshing(false);
            }
        }
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
    public void onDestroyView() {
        super.onDestroyView();

    }

    @Override
    public void onRefresh() {
        //Property list
        listLimite = 20;
        callPropetyListApi();
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
                mBaseAppCompatActivity.startActivity(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.EDIT), true);
                break;
            case Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.UNAVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_unavailable_message), Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);
                break;
            case Constants.ACTION.PROPERTY_AVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.AVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_available_message), Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                break;
            case Constants.ACTION.PROPERTY_MY_ACTION:
                openLoginUserBottomSheet(position, mPropertyData);
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
            callPropetyListApi();
        }
    }

    //admin bottom view
    private void openLoginUserBottomSheet(int position, CommonData mPropertyData) {
        LoginUserMenuBottomSheetDialogFragment loginMenuBottomSheetDialogFragment = LoginUserMenuBottomSheetDialogFragment.newInstance();
        loginMenuBottomSheetDialogFragment.addListener(new LoginUserMenuBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onRequestEditPropertyClick() {
                loginMenuBottomSheetDialogFragment.dismiss();
                ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivity(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.EDIT), true);
            }

            @Override
            public void onRequestViewUnavailableClick() {
                loginMenuBottomSheetDialogFragment.dismiss();

                if (mPropertyData.getIsAvailable().equalsIgnoreCase("1")) {
                    mPropertyData.setIsAvailable("0");
                    DeleteDilaog(Constants.ACTION_HEADER_TYPE.UNAVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_unavailable_message), Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);

                } else {
                    mPropertyData.setIsAvailable("1");
                    DeleteDilaog(Constants.ACTION_HEADER_TYPE.AVAILABLE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_available_message), Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                }
            }

            @Override
            public void onRequestViewDeletePropertyClick() {
                loginMenuBottomSheetDialogFragment.dismiss();
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.DELETE, "Property", position, null, mPropertyData, getResources().getString(R.string.string_delete_message), Constants.ACTION.DELETE_ACTION);
            }

            @Override
            public void onCancelClick() {
                loginMenuBottomSheetDialogFragment.dismiss();
            }
        });
        loginMenuBottomSheetDialogFragment.show(getChildFragmentManager(), PropertyDetails.class.getSimpleName());
    }


    public void DeleteDilaog(String headerTitle, final String mView, final int position,
                             final CommonData mRentalData, final CommonData mPropertyData, String messages,
                             final String mActionView) {

        ColorDialog dialog = new ColorDialog(mBaseAppCompatActivity);
        dialog.setTitle(headerTitle);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(messages);
        dialog.setPositiveListener(getString(R.string.string_yes), new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
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
                dialog.dismiss();
            }
        }).setNegativeListener(getString(R.string.string_no), new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    private void callUnAvailableApi(final int position, CommonData mPropertyData, String
            isAvailable) {
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
                if (response.getSuccess()) {
                    mPropertyListing.updateAdapter(position);
                }
                Fragment fragment = getParentFragment().getFragmentManager().findFragmentByTag("0");
                if (fragment instanceof NewHomeFragments) {
                    ((NewHomeFragments) fragment).refresh(false);
                }
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
                    mPropertyListing.deleteAdapter(position);
                    if (position == 0) {
                        callPropetyListApi();
                    }
                    Fragment fragment = getParentFragment().getFragmentManager().findFragmentByTag("0");
                    if (fragment instanceof NewHomeFragments) {
                        ((NewHomeFragments) fragment).refresh(false);
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        callPropetyListApi();
    }
}
