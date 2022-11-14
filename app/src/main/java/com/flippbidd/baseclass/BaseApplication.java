package com.flippbidd.baseclass;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.decoder.SimpleProgressiveJpegConfig;
import com.flippbidd.BuildConfig;
import com.flippbidd.Model.OkClientFactory;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.sendbirdSdk.widget.BroadcastUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.github.tamir7.contacts.Contacts;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.sendbird.android.SendBird;
import com.sendbird.calls.DirectCall;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.handler.DirectCallListener;
import com.sendbird.calls.handler.SendBirdCallListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

import androidx.multidex.MultiDex;
//import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;


public class BaseApplication extends Application {

    //Chat settings
    public static final String APP_CALL = "0FF29AD7-4CAE-45A2-A55A-257E0A4AD337"; //Test for call
    public static final String APP_CHAT = "56BABDFB-D4CD-4340-BDB0-3215D2A1D169"; //live for Chat
    public static final String APP_VERSION = "v3/";

    private static BaseApplication instance;
    private static OkHttpClient mOkHttpClient;
    private static FirebaseAnalytics mFirebaseAnalytics;


    public static final String TAG = "SendBirdCalls";

    public static BaseApplication getInstance() {
        return instance;
    }

    public static FirebaseAnalytics getFirebaseAnalyticsInstance() {
        return mFirebaseAnalytics;
    }

    public static OkHttpClient getClient() {
        if (mOkHttpClient == null) {
            mOkHttpClient = OkClientFactory.provideOkHttpClient(instance, BuildConfig.DEBUG);
        }
        return mOkHttpClient;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Contacts.initialize(this);
        MultiDex.install(this);
        instance = this;

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setApplicationId("1:721269325855:android:8a12be9b6c9520de") // Required for Analytics.
                .setProjectId("flippbidd-214612") // Required for Firebase Installations.
                .setApiKey("AIzaSyDgay1kQoePJZ-Hh55ofjDY-jpeePetLs4") // Required for Auth.
                .build();
        FirebaseApp.initializeApp(this, options, "FlippBidd");

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        // the following line is important
        ImagePipelineConfig configs = ImagePipelineConfig.newBuilder(this)
                .setProgressiveJpegConfig(new SimpleProgressiveJpegConfig())
                .setResizeAndRotateEnabledForNetwork(true)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, configs);
        initializeOkHttpClient();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .build();
        ImageLoader.getInstance().init(config);

        printHashKey();
    }

    // Initialize initializeOkHttpClient
    private void initializeOkHttpClient() {
        mOkHttpClient = OkClientFactory.provideOkHttpClient(instance, BuildConfig.DEBUG);

        PreferenceUtils.init(getApplicationContext());

//        SendBird.init(APP_CALL, getApplicationContext());
        SendBird.init(APP_CHAT, getApplicationContext());
        initSendBirdCall(APP_CALL);
    }

    public void printHashKey() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.flippbidd",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }

        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    public String getToken() {
        return UserPreference.getInstance(instance).getUserDetail().getToken();
    }

    public String getLoginID() {
        return UserPreference.getInstance(instance).getUserDetail().getLoginId();
    }

    public String getQBLoginID() {
        return UserPreference.getInstance(instance).getUserDetail().getEmailAddress();
    }

    public String getUserFullName() {
        return UserPreference.getInstance(instance).getUserDetail().getFullName();
    }

    public String getUserMobileNum() {
        return UserPreference.getInstance(instance).getUserDetail().getMobileNumber();
    }

    public String getCurrentUserQBID() {
        return UserPreference.getInstance(instance).getUserDetail().getQbId();
    }

    public boolean initSendBirdCall(String appId) {
        Log.i(BaseApplication.TAG, "[BaseApplication] initSendBirdCall(appId: " + appId + ")");
        Context context = getApplicationContext();

        if (TextUtils.isEmpty(appId)) {
            appId = APP_CALL;
        }

        if (SendBirdCall.init(context, appId)) {
            SendBirdCall.removeAllListeners();
            SendBirdCall.addListener(UUID.randomUUID().toString(), new SendBirdCallListener() {
                @Override
                public void onRinging(DirectCall call) {
                    int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                    Log.i(BaseApplication.TAG, "[BaseApplication] onRinging() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                    if (ongoingCallCount >= 2) {
                        call.end();
                        return;
                    }

                    call.setListener(new DirectCallListener() {
                        @Override
                        public void onConnected(DirectCall call) {
                        }

                        @Override
                        public void onEnded(DirectCall call) {
                            int ongoingCallCount = SendBirdCall.getOngoingCallCount();
                            Log.i(BaseApplication.TAG, "[BaseApplication] onEnded() => callId: " + call.getCallId() + ", getOngoingCallCount(): " + ongoingCallCount);

                            BroadcastUtils.sendCallLogBroadcast(context, call.getCallLog());

                            if (ongoingCallCount == 0) {
                                NewCallService.stopService(context);
                            }
                        }
                    });

//                    NewCallService.onRinging(context, call);
                    NewCallService.onRinging(context, call);
                }
            });

            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.DIALING, R.raw.dialing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RINGING, R.raw.ringing);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTING, R.raw.reconnecting);
            SendBirdCall.Options.addDirectCallSound(SendBirdCall.SoundType.RECONNECTED, R.raw.reconnected);
            return true;
        }
        return false;
    }
}
