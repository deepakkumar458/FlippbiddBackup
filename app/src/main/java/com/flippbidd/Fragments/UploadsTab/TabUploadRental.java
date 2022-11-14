package com.flippbidd.Fragments.UploadsTab;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.flippbidd.Adapter.Rental.AdapterRentalList;
import com.flippbidd.CustomClass.CustomTextView;
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
import com.flippbidd.activity.Rental.AddNewRental;
import com.flippbidd.baseclass.BaseFragment;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabUploadRental extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener, RefreshListener, AdapterRentalList.onItemClickLisnear {
    private static final String TAG = LogUtils.makeLogTag(TabUploadRental.class);
    Disposable disposable;

    @BindView(R.id.recyclerViewPropertyList)
    RecyclerView recyclerViewPropertyList;
    @BindView(R.id.swipeRefreshViewPropertyList)
    SwipeRefreshLayout swipeRefreshViewPropertyList;
    @BindView(R.id.mRelativeProgress)
    RelativeLayout mRelativeProgress;


    LinearLayoutManager mLinearLayoutManager;

    //Rental Adapter
    AdapterRentalList mAdapterRentalList;

    View viewNoData;
    public static CustomTextView textViewNoDataMessage;

    public static Dialog dialog;

    private CommonData mSelectedRentalData;


    private List<CommonData> temAraa = new ArrayList<>();

    // Index from which pagination should start (0 is 1st page in our case)
    private int listLimite = 20;
    private boolean isLoading = false;
    private boolean isLastPage = false;


    //new code 14/11/2018
    private ShimmerFrameLayout mShimmerViewContainer;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_property_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.setVisibility(View.GONE);
        viewNoData = view.findViewById(R.id.viewNoData);
        textViewNoDataMessage = view.findViewById(R.id.textViewNoDataMessage);

        setNodataView("Lending");//Rentals
       /* mAdapterRentalList = new AdapterRentalList(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_LANDLOARD);
        viewRentalInitialization();

        swipeRefreshViewPropertyList.setOnRefreshListener(this);
        swipeRefreshViewPropertyList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


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
        });*/



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

        listLimite += 10;
        isLoading = false;
        callRentalListApi(false);
    }

    public static void setNodataView(String textView) {
        //Rental list
        textViewNoDataMessage.setText(Constants.no_data_found + textView + Constants.no_data_found_append);
    }


    private void viewRentalInitialization() {
        //set layout manager
        serRecyclerViewlayout();
        //set data in adapter
        recyclerViewPropertyList.setAdapter(mAdapterRentalList);
        mAdapterRentalList.setItemOnClickEvent(this);
        callRentalListApi(true);

    }

    public void serRecyclerViewlayout() {
        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewPropertyList.setLayoutManager(mLinearLayoutManager);
        recyclerViewPropertyList.setItemAnimator(new DefaultItemAnimator());

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


   /* private void deleteUser(CommonData mPropertyData, String strType) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        String mString = mPropertyData.getCommonId() + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_" + strType;

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
                        //Rental list
                        callRentalListApi(false);
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        LogUtils.LOGE(TAG, "Delete User Error!");
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtils.LOGE(TAG, "Login User Error!");
                hideProgressDialog();
            }
        });
    }*/


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
        //Rental list
        listLimite = 20;
        callRentalListApi(false);


    }

    @Override
    public void refresh(boolean b) {
        if (!b) {
            //callRentalListApi(true);
            openComingSonDialog();
        }
    }

    private void openComingSonDialog() {
        //coming soon
        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
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

    @Override
    public void onResume() {
        super.onResume();
        mShimmerViewContainer.startShimmer();
        //refresh(false);
    }

    @Override
    public void onAttachFragment(Fragment childFragment) {
        super.onAttachFragment(childFragment);

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

                    if (mActionView.equalsIgnoreCase(Constants.ACTION.DELETE_ACTION)) {
                        //Delete
                        callRentalDeleteApi(position, mRentalData);
                    } else {
                        if (mActionView.equalsIgnoreCase(Constants.ACTION.PROPERTY_AVAILABLE_ACTION)) {
                            callRentalUnAvailableApi(position, mRentalData, "1");
                        } else {
                            callRentalUnAvailableApi(position, mRentalData, "0");
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

    //Rental api calling
    private void callRentalListApi(boolean isProgress) {
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
        //needed changes
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_RENTAL));

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
                    mAdapterRentalList.addAll(response.getData());
                } else {
//                    if (isScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                    if (response.getData() != null && !response.getData().equals("")) {
                        recyclerViewPropertyList.setVisibility(View.VISIBLE);
                        viewNoData.setVisibility(View.GONE);
                        mAdapterRentalList.addAll(response.getData());
                    } else {
                        setNodataView("Rental");
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
                mRelativeProgress.setVisibility(View.GONE);
                hideProgressDialog();
                hideSwipeRefreshView();

            }
        });

    }

    private void callRentalUnAvailableApi(final int position, CommonData mRentalData, String isAvailable) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("rental_id", RequestBody.create(MediaType.parse("multipart/form-data"), mRentalData.getCommonId()));
        hashMap.put("is_available", RequestBody.create(MediaType.parse("multipart/form-data"), isAvailable));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.unavailableRental(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mAdapterRentalList.updateAdapter(position);
                }
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    private void callRentalDeleteApi(final int position, final CommonData mRentalData) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("rental_id", RequestBody.create(MediaType.parse("multipart/form-data"), mRentalData.getCommonId()));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.deleteRental(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mAdapterRentalList.deleteAdapter(position);
//                    deleteUser(mRentalData, "rental");
                }
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

    }

    private void callRentalLikesUnLikesApi(final int position, CommonData mRentalData, String isLikes) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("rental_id", RequestBody.create(MediaType.parse("multipart/form-data"), mRentalData.getCommonId()));
        hashMap.put("is_like", RequestBody.create(MediaType.parse("multipart/form-data"), isLikes));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.likesRental(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    mAdapterRentalList.updateAdapter(position);
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

    @Override
    public void onRentalListClickEvents(int position, CommonData mRentalData, String mActionType) {
        switch (mActionType) {
            case Constants.ACTION.DELETE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.DELETE, "Rental", position, mRentalData, null, getResources().getString(R.string.string_delete_message), Constants.ACTION.DELETE_ACTION);
                break;
            case Constants.ACTION.VIEW_ACTION:
                mBaseAppCompatActivity.startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mRentalData.getCommonId(), mRentalData.getData().getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL, mRentalData.isExpiriedStatus()), true);
                break;
            case Constants.ACTION.EDIT_ACTION:
                mBaseAppCompatActivity.startActivity(AddNewRental.getIntentActivity(mBaseAppCompatActivity, mRentalData, Constants.SCREEN_ACTION_TYPE.EDIT), true);
                break;
            case Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.UNAVAILABLE, "Rental", position, mRentalData, null, getResources().getString(R.string.string_unavailable_message), Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);
                break;
            case Constants.ACTION.PROPERTY_AVAILABLE_ACTION:
                DeleteDilaog(Constants.ACTION_HEADER_TYPE.AVAILABLE, "Rental", position, mRentalData, null, getResources().getString(R.string.string_available_message), Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                break;
        }

    }

}
