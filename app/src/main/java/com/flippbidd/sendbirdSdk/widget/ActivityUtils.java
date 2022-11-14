package com.flippbidd.sendbirdSdk.widget;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.flippbidd.MainActivity;
import com.flippbidd.activity.Login;
import com.flippbidd.baseclass.BaseApplication;

import androidx.annotation.NonNull;

public class ActivityUtils {

    public static final int START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE = 1;

    public static void startAuthenticateActivityAndFinish(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startAuthenticateActivityAndFinish()");

        Intent intent = new Intent(activity, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startSignInManuallyActivityForResult(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startSignInManuallyActivityAndFinish()");

       /* Intent intent = new Intent(activity, SignInManuallyActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivityIfNeeded(intent, START_SIGN_IN_MANUALLY_ACTIVITY_REQUEST_CODE);*/
    }

    public static void startMainActivityAndFinish(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startMainActivityAndFinish()");

        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        activity.startActivity(intent);
        activity.finish();
    }

    public static void startApplicationInformationActivity(@NonNull Activity activity) {
        Log.i(BaseApplication.TAG, "[ActivityUtils] startApplicationInformationActivity()");

        /*Intent intent = new Intent(activity, ApplicationInformationActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);*/
    }
}