package com.flippbidd;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.IPortal.Response;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.activity.Login;
import com.flippbidd.activity.UpdateMobileNumber;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.gcm.RegistrationIntentService;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;
import com.flippbidd.sendbirdSdk.widget.ActivityUtils;
import com.flippbidd.sendbirdSdk.widget.AuthenticationUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.sendbird.android.SendBirdException;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.LinkedHashMap;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SplashActivity extends AppCompatActivity {
    private static final String TAG = LogUtils.makeLogTag(SplashActivity.class);
    private static final int SPLASH_TIME_MS = 1000;
    Context context;

    private Timer mTimer;
    private Boolean mAutoAuthenticateResult;
    private String mEncodedAuthInfo;
    Disposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(R.layout.activity_splash_layout);
        context = SplashActivity.this;
        requestPemission();

    }

    private void requestPemission() {
        // Checking if permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    // checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(SplashActivity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR}, 899);
            } else {
                startSplash();
            }
        } else {
            startSplash();
        }
    }

    private void startSplash() {

        if (checkPlayServices()) {
            //Start IntentService to register this application with GCM.
            final Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        if (getIntent() != null && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) == Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) {
            getIntent().removeExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL);
        }

        VideoView vvSlashScreen = findViewById(R.id.vvSlashScreen);
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_new_vv);
        vvSlashScreen.setVideoURI(video);
        vvSlashScreen.setFitsSystemWindows(true);
        vvSlashScreen.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {

                if (UserPreference.getInstance(context).isUserLogin()) {
                    //open main activity
                    if (ConnectionManager.isLogin() && PreferenceUtils.getUserId() != null) {
                        setUpSyncManager();
                    } else {
                        //open Login activity
                        openLoginActivity();
                    }
                } else {
                    //open Login activity
                    openLoginActivity();
                }
            }
        });
        vvSlashScreen.requestFocus();
        vvSlashScreen.start();

    }

    private void openLoginActivity() {
        startActivity(new Intent(SplashActivity.this, Login.class));
        finish();
    }


    private boolean checkPlayServices() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                //apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void setUpSyncManager() {
        if (PreferenceUtils.getUserId() != null) {
            SyncManagerUtils.setup(SplashActivity.this, PreferenceUtils.getUserId(), new CompletionHandler() {
                @Override
                public void onCompleted(SendBirdException e) {
                    if (e != null) {
                        //Toast.makeText(SplashActivity.this, "Cannot Setup SyncManager", Toast.LENGTH_SHORT).show();
                        SendBirdSyncManager.getInstance().clearCache();
                        Intent intent = new Intent(SplashActivity.this, Login.class);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    //check user mobile number
                    if (!UserPreference.getInstance(context).getMobileNumber().equalsIgnoreCase("null") && !UserPreference.getInstance(context).getMobileNumber().equalsIgnoreCase("")) {
                        setTimer();
                        if (!hasDeepLink()) {
                            callPremiumUserStatus();
                            autoAuthenticate();
                        }

                    } else {
                        //open update mobile number activity
                        Intent mNewIntent = new Intent(SplashActivity.this, UpdateMobileNumber.class);
                        startActivity(mNewIntent);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 899) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                startSplash();
            } else {
                requestPemission();
            }
        }
    }


    private boolean hasDeepLink() {
        boolean result = false;

        Intent intent = getIntent();
        if (intent != null) {
            Uri data = intent.getData();
            if (data != null) {
                String scheme = data.getScheme();
                if (scheme != null && scheme.equals("sendbird")) {
                    Log.i(BaseApplication.TAG, "[SplashActivity] deep link: " + data.toString());
                    mEncodedAuthInfo = data.getHost();
                    if (!TextUtils.isEmpty(mEncodedAuthInfo)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    private void setTimer() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(() -> {
                    mTimer = null;

                    if (!TextUtils.isEmpty(mEncodedAuthInfo)) {
                        AuthenticationUtils.authenticateWithEncodedAuthInfo(SplashActivity.this, mEncodedAuthInfo, (isSuccess, hasInvalidValue) -> {
                            if (isSuccess) {
//                                ActivityUtils.startMainActivityAndFinish(SplashActivity.this);
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                if (getIntent().hasExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL)) {
                                    String pushedChannelUrl = getIntent().getStringExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL);
                                    intent.putExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL, pushedChannelUrl);
                                }
                                startActivity(intent);
                                finish();

                            } else {
                                if (hasInvalidValue) {
                                    ToastUtils.longToast(getString(R.string.calls_invalid_deep_link));
                                } else {
                                    ToastUtils.longToast(getString(R.string.calls_deep_linking_to_authenticate_failed));
                                }
                                finish();
                            }
                        });
                        return;
                    }
                    callPremiumUserStatus();

                    if (mAutoAuthenticateResult != null) {
                        if (mAutoAuthenticateResult) {
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            if (getIntent().hasExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL)) {
                                String pushedChannelUrl = getIntent().getStringExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL);
                                intent.putExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL, pushedChannelUrl);
                            }
                            startActivity(intent);
                            finish();
                        } else {


                            autoAuthenticate();
//                            ActivityUtils.startAuthenticateActivityAndFinish(SplashActivity.this);
                        }
                    }
                });
            }
        }, SPLASH_TIME_MS);
    }

    private void callPremiumUserStatus() {
        LinkedHashMap<String, RequestBody> premiumUserRequest = new LinkedHashMap<String, RequestBody>();
        premiumUserRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getToken()));
        premiumUserRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getLoginId()));
        System.out.println("!!!loginIdInLogin = " + UserPreference.getInstance(this).getUserDetail().getLoginId());
        System.out.println("!!!token = " + UserPreference.getInstance(this).getUserDetail().getToken());
        UserServices userService = ApiFactory.getInstance(this).provideService(UserServices.class);
        Observable<Response> observable;
        observable = userService.checkPremiumUserStstus(premiumUserRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<Response>() {

            @Override
            public void onSuccess(Response response) {
                if (response.getSuccess()) {
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                } else {
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                System.out.println("throwable = " + throwable.getMessage());
            }
        });
    }

    private void autoAuthenticate() {
        AuthenticationUtils.autoAuthenticate(SplashActivity.this, userId -> {
            if (mTimer != null) {
                mAutoAuthenticateResult = !TextUtils.isEmpty(userId);
            } else {
                if (userId != null) {
                    ActivityUtils.startMainActivityAndFinish(SplashActivity.this);
                } else {
                    ActivityUtils.startAuthenticateActivityAndFinish(SplashActivity.this);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        super.onBackPressed();
    }
}
