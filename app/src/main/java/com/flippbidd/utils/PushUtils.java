package com.flippbidd.utils;

import android.content.Context;
import android.util.Log;

import com.flippbidd.Others.UserPreference;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.gcm.MyFirebaseMessagingService;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sendbird.android.SendBird;
import com.sendbird.calls.SendBirdCall;
import com.sendbird.calls.SendBirdException;

import org.apache.commons.lang3.StringUtils;

public class PushUtils {

    public static void registerPushTokenForCurrentUser() {
        registerPushTokenForCurrentUser(null);
    }

    public static void registerPushTokenForCurrentUser(SendBird.RegisterPushTokenWithStatusHandler handler) {
        MyFirebaseMessagingService.getPushToken(pushToken -> {
            Log.d("Token", "++ pushToken : "+ pushToken);
            UserPreference.getInstance(BaseApplication.getInstance()).setDeviceToken(pushToken);
            SendBird.registerPushTokenForCurrentUser(pushToken, handler);
            registerPushToken(pushToken, new PushTokenHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    Log.i(BaseApplication.TAG, "[PushUtils] getPushToken() => pushToken: " + pushToken);
                }
            });
        });
    }

    public static void unregisterPushTokenForCurrentUser(SendBird.UnregisterPushTokenHandler handler) {
        MyFirebaseMessagingService.getPushToken(pushToken -> SendBird.unregisterPushTokenForCurrentUser(pushToken, handler));
    }

    public static void unregisterPushTokenAllForCurrentUser(SendBird.UnregisterPushTokenHandler handler) {
        SendBird.unregisterPushTokenAllForCurrentUser(handler);
    }
    public interface GetPushTokenHandler {
        void onResult(String token, SendBirdException e);
    }

    public static void getPushToken(Context context, final GetPushTokenHandler handler) {
        Log.i(BaseApplication.TAG, "[PushUtils] getPushToken()");

        String savedToken = PreferenceUtils.getPushToken();
        if (StringUtils.isEmpty(savedToken)) {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
                if (!task.isSuccessful()) {
                    Log.i(BaseApplication.TAG, "[PushUtils] getPushToken() => getInstanceId failed", task.getException());
                    if (handler != null) {
                        handler.onResult(null, new SendBirdException((task.getException() != null ? task.getException().getMessage() : "")));
                    }
                    return;
                }

                String pushToken = (task.getResult() != null ? task.getResult().getToken() : "");
                Log.i(BaseApplication.TAG, "[PushUtils] getPushToken() => pushToken: " + pushToken);
                if (handler != null) {
                    handler.onResult(pushToken, null);
                }
            });
        } else {
            Log.i(BaseApplication.TAG, "[PushUtils] savedToken: " + savedToken);
            if (handler != null) {
                handler.onResult(savedToken, null);
            }
        }
    }

    public interface PushTokenHandler {
        void onResult(SendBirdException e);
    }

    public static void registerPushToken(String pushToken, PushTokenHandler handler) {
        Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken(pushToken: " + pushToken + ")");

        SendBirdCall.registerPushToken(pushToken, false, e -> {
            if (e != null) {
                Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken() => e: " + e.getMessage());
                PreferenceUtils.setPushToken(pushToken);

                if (handler != null) {
                    handler.onResult(e);
                }
                return;
            }

            Log.i(BaseApplication.TAG, "[PushUtils] registerPushToken() => OK");
            PreferenceUtils.setPushToken(pushToken);

            if (handler != null) {
                handler.onResult(null);
            }
        });
    }

}
