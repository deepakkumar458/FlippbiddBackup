package com.flippbidd.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.DeleteAccountResponse;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Login;
import com.flippbidd.activity.setting.ChangePassword;
import com.flippbidd.activity.setting.Help;
import com.flippbidd.activity.setting.NotificationSettings;
import com.flippbidd.activity.support.SupportActivity;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.Task;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.SendBirdSyncManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class settings extends BaseFragment {

    private Disposable disposable;

    private static final String TAG = LogUtils.makeLogTag(settings.class);
    @BindView(R.id.textViewChangePasswordTitle)
    CustomTextView textViewChangePasswordTitle;
    @BindView(R.id.view_change_password_line)
    View view_change_password_line;
    @BindView(R.id.textViewHelp)
    CustomTextView textViewHelp;

    UserDetails mUserDetails;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUserDetails = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
        try {
            if (mUserDetails != null) {
                if (mUserDetails.getLoginType().equalsIgnoreCase("google")
                        || mUserDetails.getLoginType().equalsIgnoreCase("facebook")) {
                    textViewChangePasswordTitle.setVisibility(View.GONE);
                    view_change_password_line.setVisibility(View.GONE);
                } else {
                    textViewChangePasswordTitle.setVisibility(View.VISIBLE);
                    view_change_password_line.setVisibility(View.VISIBLE);
                }
            }
        } catch (NullPointerException e) {
            textViewChangePasswordTitle.setVisibility(View.GONE);
            view_change_password_line.setVisibility(View.GONE);
        }
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_setting_layout;
    }

    @OnClick({R.id.textViewRestorePurchase, R.id.textViewChangePasswordTitle, R.id.textViewHelp, R.id.textViewNotificationSettings, R.id.textViewSupportedSettings, R.id.textViewRateThisAppSettings,R.id.delete_account_btn})
    void onViewClick(View view) {
        switch (view.getId()) {
            case R.id.textViewChangePasswordTitle:
                mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, ChangePassword.class), true);
                break;
            case R.id.textViewHelp:
                mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, Help.class), true);
                break;
            case R.id.textViewNotificationSettings:
                mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, NotificationSettings.class), true);
                break;
            case R.id.textViewSupportedSettings:
                mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, SupportActivity.class), true);
                break;
            case R.id.textViewRateThisAppSettings:

                ReviewManager manager = ReviewManagerFactory.create(getActivity());
                Task<ReviewInfo> request = manager.requestReviewFlow();
                request.addOnCompleteListener(new OnCompleteListener<ReviewInfo>() {
                    @Override
                    public void onComplete(@NonNull Task<ReviewInfo> task) {
                        if (task.isSuccessful()) {
                            ReviewInfo reviewinfo = task.getResult();
                            Task<Void> flow = manager.launchReviewFlow(getActivity(), reviewinfo);
                            flow.addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                   // Toast.makeText(getActivity(), "Task is successful handle your application flow here.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            //Toast.makeText(getActivity(), "Something wrong.Please try again. ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                /*String link = "market://details?id=";
                try {
                    // play market available
                    getActivity().getPackageManager()
                            .getPackageInfo("com.flippbidd", 0);
                    // not available
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    // should use browser
                    link = "https://play.google.com/store/apps/details?id=";
                }
                // starts external action
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(link + getActivity().getPackageName())));*/
                break;
            case R.id.textViewRestorePurchase:
                //open resubscription
                //https://play.google.com/store/account/subscriptions?sku=your-sub-product-id&package=your-app-package
                getActivity().startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/account/subscriptions?sku=flippbidd_pro&package=" + getActivity().getPackageName())));
                break;


            case R.id.delete_account_btn:

                System.out.println("delete account button clicked ->");
                show_dialog();


        }
    }

     void handleDeleteAccount(){
         if (!NetworkUtils.isInternetAvailable(requireActivity())) {
             ToastUtils.longToast(getString(R.string.no_internet));
             return;
         }
         showProgressDialog(true);
         LinkedHashMap<String, RequestBody>linkedHashMap = new LinkedHashMap<>();
         linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(requireActivity()).getUserDetail().getLoginId()));
         linkedHashMap.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));

         UserServices userServices = ApiFactory.getInstance(requireActivity()).provideService(UserServices.class);
         Observable<DeleteAccountResponse> observable;
         observable = userServices.allowUserToDeleteAccount(linkedHashMap);

         disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<DeleteAccountResponse>() {
             @Override
             public void onSuccess(DeleteAccountResponse deleteAccountResponse) {
                 System.out.println("Account Deleted Successfull");
                // here we are disconnect the send bird service
                 disconnect();
             }

             @Override
             public void onFailed(Throwable throwable) {
                 showToast("Error while Deleting the account"+throwable.getMessage().toString());
             }
         });

     }

    public void show_dialog() {
        Dialog mDialog = new Dialog(requireActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dilaog_ui_for_delete_account);
        mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mDialog.dismiss();

                    handleDeleteAccount();

                } catch (Exception e) {
                    //Disconnect Chat
                   // disconnect();
                }
            }
        });

        mDialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();

    }

    private void disconnect() {
        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    // Don't return because we still need to disconnect.
                } else {
//                    Toast.makeText(MainActivity.this, "All push tokens unregistered.", Toast.LENGTH_SHORT).show();
                }
                //remove connection
                ConnectionManager.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
                        String userId = PreferenceUtils.getUserId();
                        // if you want to clear cache of specific user when disconnect, you can do like this.
                        SendBirdSyncManager.getInstance().clearCache(userId);
                        PreferenceUtils.clearAll();
                        UserPreference.getInstance(requireActivity()).clear();
                        requireActivity().startActivity(new Intent(requireActivity(), Login.class));
                    }
                });
            }
        });
    }



}

