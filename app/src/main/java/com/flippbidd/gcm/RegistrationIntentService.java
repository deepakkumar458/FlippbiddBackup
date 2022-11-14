package com.flippbidd.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.interfaces.Consts;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import androidx.annotation.NonNull;


/**
 * Purpose:-
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        try {
            FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                @Override
                public void onComplete(@NonNull Task<InstanceIdResult> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "getInstanceId failed", task.getException());
                        return;
                    }
                    // Get new Instance ID token
                    InstanceIdResult result = task.getResult();
                    if (result != null) {
                        String token = result.getToken();
                        UserPreference.getInstance(BaseApplication.getInstance()).setDeviceToken(token);
                        Log.d(TAG, "FCM token : " + token);

                    }
                }
            });

        } catch (Exception e) {
            Log.e("GCM TOKEN ERROR:-", e.getMessage());
        }
    }

    /**
     * Persist registration to third-party servers.
     * <p/>
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param registrationID The new token.
     */


}
