package com.flippbidd.Fragments.UploadsTab;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;

import com.flippbidd.Adapter.Service.CommonServiceListAdapter;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Service.ServiceListData;
import com.flippbidd.Model.Response.Service.ServiceListResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Services.AddNewServicer;
import com.flippbidd.baseclass.BaseFragment;
import java.util.LinkedHashMap;

import butterknife.BindView;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabUploadService extends BaseFragment implements RefreshListener, CommonServiceListAdapter.onItemClickLisnear, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = LogUtils.makeLogTag(TabUploadService.class);


    @BindView(R.id.recycelerViweServiceList)
    RecyclerView recycelerViweServiceList;
    @BindView(R.id.swipeRefreshViewPropertyList)
    SwipeRefreshLayout swipeRefreshViewPropertyList;
    @BindView(R.id.mRelativeProgress)
    RelativeLayout mRelativeProgress;


    LinearLayoutManager mLinearLayoutManager;

    Disposable disposable;
    CommonServiceListAdapter mCommonServiceListAdapter;
    private int listLimite = 20;
    private boolean isLoading = false;

    private View viewNodata;
    private CustomTextView customTextView;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_servicer_homeowener_layout;
    }

    @Override
    public void refresh(boolean b) {

        if (!b) {
            /*if (mCommonServiceListAdapter != null)
                mCommonServiceListAdapter.notifyDataSetChanged();*/
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        viewNodata = view.findViewById(R.id.viewNodata);
        customTextView = view.findViewById(R.id.textViewNoDataMessage);
        /*mCommonServiceListAdapter = new CommonServiceListAdapter(mBaseAppCompatActivity, Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE);
        viewPropertyInitialization();

        swipeRefreshViewPropertyList.setOnRefreshListener(this);
        swipeRefreshViewPropertyList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);


        if (mCommonServiceListAdapter != null)
            mCommonServiceListAdapter.setItemOnClickEvent(this);*/

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

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ServiceListResponse> observable;
        observable = userService.myServiceList(hashMap);


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
        customTextView.setText(Constants.no_data_found + textView + Constants.no_data_found_append);
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
    public void onRefresh() {
        callServiceListApi(false);
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

    /*private void deleteUser(String mServiceId) {
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
}
