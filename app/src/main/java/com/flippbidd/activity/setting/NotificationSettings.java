package com.flippbidd.activity.setting;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.PushUtils;
import com.flippbidd.utils.ToastUtils;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.util.LinkedHashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class NotificationSettings extends AppCompatActivity {

    private Activity mActivity;
    SwitchCompat mSwitchOn, mSwitchFindMyDeal, mViewNotification, mUploadPropertyNotification;
    AppCompatButton mBtnPushTest;
    Toolbar moToolbar;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_settings_layout);
        this.mActivity = NotificationSettings.this;
        moToolbar = findViewById(R.id.toolbarChatNotificationSettings);

        findViewById(R.id.imageBackIcon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Implemented by activity
            }
        });

        mBtnPushTest = findViewById(R.id.btnPushTest);
        mSwitchOn = findViewById(R.id.switchOn);
        mSwitchFindMyDeal = findViewById(R.id.switchFindMyDeal);
        mViewNotification = findViewById(R.id.ViewNotification);
        mUploadPropertyNotification = findViewById(R.id.uploadPropertyNotification);
        //define id
        if (PreferenceUtils.getNotifications()) {
            mSwitchOn.setChecked(true);
        } else {
            mSwitchOn.setChecked(false);
        }


        mSwitchOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                UserPreference.getInstance(mActivity).setPushSub(isChecked);
                if (isChecked) {
                    PushUtils.registerPushTokenForCurrentUser(new SendBird.RegisterPushTokenWithStatusHandler() {
                        @Override
                        public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                            if (e != null) {
                                mSwitchOn.setChecked(!isChecked);
                                return;
                            }

                            PreferenceUtils.setNotifications(isChecked);
                        }
                    });
                } else {
                    PushUtils.unregisterPushTokenForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
                        @Override
                        public void onUnregistered(SendBirdException e) {
                            if (e != null) {
                                mSwitchOn.setChecked(!isChecked);
                                return;
                            }

                            PreferenceUtils.setNotifications(isChecked);
                        }
                    });
                }
            }
        });

        if (PreferenceUtils.getMyDealNotifications()) {
            mSwitchFindMyDeal.setChecked(true);
        } else {
            mSwitchFindMyDeal.setChecked(false);
        }
        mSwitchFindMyDeal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                UserPreference.getInstance(mActivity).setPushSub(isChecked);
                if (isChecked) {
                    PreferenceUtils.setMyDealNotifications(isChecked);
                    updateNotificationStatus("1");
                } else {
                    PreferenceUtils.setMyDealNotifications(isChecked);
                    updateNotificationStatus("0");
                }
            }
        });

        if (PreferenceUtils.getViewNotifications()) {
            mViewNotification.setChecked(true);
        } else {
            mViewNotification.setChecked(false);
        }
        mViewNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    PreferenceUtils.setViewNotifications(true);
                    updateViewNotificationStatus("1");
                } else {
                    PreferenceUtils.setViewNotifications(false);
                    updateViewNotificationStatus("0");
                }

            }
        });

        if (PreferenceUtils.getUploadPropertyNotifications()) {
            mUploadPropertyNotification.setChecked(true);
        } else {
            mUploadPropertyNotification.setChecked(false);
        }
        mUploadPropertyNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                if (isChecked) {
                    PreferenceUtils.setUploadPropertyNotifications(true);
                    updatePropertyUploadNotificationStatus("1");
                } else {
                    PreferenceUtils.setUploadPropertyNotifications(false);
                    updatePropertyUploadNotificationStatus("0");
                }

            }
        });

        mBtnPushTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callPushTest();
            }
        });
    }

    private void callPushTest() {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(mActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.pustNotificationTest(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    private void updateNotificationStatus(String status) {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("find_my_deal", RequestBody.create(MediaType.parse("multipart/form-data"), status));

        UserServices userService = ApiFactory.getInstance(mActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.updateMyDealNotificationStatus(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                CommonDialogView.getInstance().showCommonDialog(mActivity, "",
                        response.getText()
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

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    private void updatePropertyUploadNotificationStatus(String status) {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), status));

        UserServices userService = ApiFactory.getInstance(mActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.updatePropertyUploadNotificationStatus(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                CommonDialogView.getInstance().showCommonDialog(mActivity, "",
                        response.getText()
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

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }


    private void updateViewNotificationStatus(String status) {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), status));

        UserServices userService = ApiFactory.getInstance(mActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.updateViewNotificationStatus(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                CommonDialogView.getInstance().showCommonDialog(mActivity, "",
                        response.getText()
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

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
