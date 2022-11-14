package com.flippbidd.Fragments;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.flippbidd.Adapter.Notification.ListOfNotification;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Notification.NotificationData;
import com.flippbidd.Model.Response.Notification.NotificationListRespons;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.DataRequest.RequestDocumentList;
import com.flippbidd.activity.Details.PropertyBiddListActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.ProfileEditActivity;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.activity.UploadFiles_Activity_List;
import com.flippbidd.activity.my_calender.RequestCallListActivity;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.ToastUtils;

import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class Notification extends BaseFragment implements RefreshListener, SwipeRefreshLayout.OnRefreshListener, ListOfNotification.onItemClickLisnear {
    private static final String TAG = LogUtils.makeLogTag(Notification.class);

    @BindView(R.id.recyclerViewNotificationList)
    RecyclerView recyclerViewNotificationList;
    @BindView(R.id.swipeRefreshViewNotificationList)
    SwipeRefreshLayout swipeRefreshViewNotificationList;

    //objects
    Disposable disposable;
    private View viewNodata;
    private CustomTextView customTextView;
    LinearLayoutManager mLinearLayoutManager;
    public static ListOfNotification mListOfNotification;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_notification_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewNodata = view.findViewById(R.id.viewNoDataNotification);
        customTextView = view.findViewById(R.id.textViewNoDataMessage);
        viewNodata.setVisibility(View.VISIBLE);

        mListOfNotification = new ListOfNotification(mBaseAppCompatActivity);
        mLinearLayoutManager = new LinearLayoutManager(mBaseAppCompatActivity);
        recyclerViewNotificationList.setLayoutManager(mLinearLayoutManager);
        recyclerViewNotificationList.setItemAnimator(new DefaultItemAnimator());
        recyclerViewNotificationList.setAdapter(mListOfNotification);
        mListOfNotification.setItemOnClickEvent(this);

        swipeRefreshViewNotificationList.setOnRefreshListener(this);
        swipeRefreshViewNotificationList.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        callNotificationListApi(true);


        //Delete all notification
        MainActivity.image_delete_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                allDeleteNotificationAlert(getString(R.string.alert_delete_all_notification_message), "", 0);
            }
        });
    }

    public void allDeleteNotificationAlert(String delete_message, final String deletType, final int postion) {

        Dialog mDialog = new Dialog(mBaseAppCompatActivity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dilaog_logout_ui);

        CustomTextView txt_message_header, txt_message, txt_okay, txt_cancel;

        txt_okay = mDialog.findViewById(R.id.txt_okay);
        txt_okay.setText("Cancel");
        txt_cancel = mDialog.findViewById(R.id.txt_cancel);
        txt_cancel.setText("Clear All");
        txt_cancel.setTextColor(getActivity().getResources().getColor(R.color.red));

        txt_message_header = mDialog.findViewById(R.id.txt_message_header);
        txt_message_header.setText("Clear all notifications");
        txt_message = mDialog.findViewById(R.id.txt_message);
        txt_message.setText("Do you want to clear all notifications?");


        txt_okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mDialog.dismiss();
                    // use the value.
                } catch (Exception e) {
                }
            }
        });

        txt_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mDialog.dismiss();
                    // use the value.
                    callDeleteAllNotification(deletType, postion);

                } catch (Exception e) {
                }
            }
        });

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private void callDeleteAllNotification(final String notiId, final int position) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        if (notiId.equalsIgnoreCase("")) {
            hashMap.put("noti_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        } else {
            hashMap.put("noti_id", RequestBody.create(MediaType.parse("multipart/form-data"), notiId));
        }
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.deleteNotification(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    if (notiId.equalsIgnoreCase("")) {
                        //all delete
                        mListOfNotification.deleteAdapter();
                    } else {
                        //delete by id
                        mListOfNotification.deleteItem(position);
                    }

                    callNotificationListApi(false);
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });


    }


    //Rental api calling
    private void callNotificationListApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<NotificationListRespons> observable;
        observable = userService.getNotificationList(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<NotificationListRespons>() {
            @Override
            public void onSuccess(NotificationListRespons response) {
                hideProgressDialog();
                hideSwipeRefreshView();
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {

                    if (response.getData() != null && response.getData().size() != 0) {
                        MainActivity.image_delete_notification.setVisibility(View.VISIBLE);
                        recyclerViewNotificationList.setVisibility(View.VISIBLE);
                        viewNodata.setVisibility(View.GONE);
                        mListOfNotification.addAll(response.getData());
                    } else {
                        MainActivity.image_delete_notification.setVisibility(View.GONE);
                    }
                } else {
                    MainActivity.image_delete_notification.setVisibility(View.GONE);
                    recyclerViewNotificationList.setVisibility(View.GONE);
                    viewNodata.setVisibility(View.VISIBLE);
                    setNodataView();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
                hideSwipeRefreshView();
            }
        });

    }

    public void setNodataView() {
        customTextView.setText(getResources().getString(R.string.no_notification_mesg));
    }

    public void hideSwipeRefreshView() {
        if (swipeRefreshViewNotificationList != null) {
            if (swipeRefreshViewNotificationList.isRefreshing()) {
                swipeRefreshViewNotificationList.setRefreshing(false);
            }
        }
    }

    @Override
    public void onRefresh() {
        callNotificationListApi(false);
    }

    @Override
    public void refresh(boolean b) {
    }

    @Override
    public void onClickEvent(int position, NotificationData mNotificationData, String mActionType) {
        if (mNotificationData.getPropertyDetails() != null && !mNotificationData.getPropertyDetails().equals("")) {
            startActivity(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, mNotificationData.getCommonId(), mNotificationData.getPropertyDetails().getLoginId(), mNotificationData.getType(), false));
        } else {
            CommonDialogView.getInstance().showCommonDialog(((BaseAppCompatActivity) mBaseAppCompatActivity), "",
                    "This Details are not longer available."
                    , ""
                    , "Ok"
                    , false, true, true, false, false, new CommonDialogCallBack() {
                        @Override
                        public void onDialogYes(View view) {

                        }

                        @Override
                        public void onDialogCancel(View view) {
                        }
                    });
        }
    }

    private void openMyCalendarScreen(NotificationData mNotificationData) {
        requireActivity().startActivity(new Intent(requireActivity(), RequestCallListActivity.class));
    }

    //1)Call Request 2)Request Cancelled 3)Call Request Accepted 4)Call Request Reject 5)Suggest New Time 6)flippbidd_call
    //7)Add New Bidd 8)Document Request 9)New Request Contract

}
