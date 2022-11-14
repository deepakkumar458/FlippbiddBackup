package com.flippbidd.linkcreated;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Login;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import androidx.annotation.NonNull;

public class ReceiveDynamicLinkActivity extends Activity {

    Uri deepLink = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_dynamic_link);

        if (!UserPreference.getInstance(ReceiveDynamicLinkActivity.this).isUserLogin()) {
            startActivity(new Intent(ReceiveDynamicLinkActivity.this, Login.class));
            finish();
            return;
        }


        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        //Get deep link from result (may be null if no link is found)
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                        }

                        if (deepLink != null && !deepLink.equals("")) {
                            handleDeeplink("" + deepLink);
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("ERROR", "getDynamicLink:onFailure", e);
                    }
                });
    }

    public void handleDeeplink(String deepLink) {
        // Log.e("OPEN", "getDynamicLink:onFailure   " + deepLink);
        Uri deepUri = Uri.parse(deepLink);

        String path = deepUri.getPath();
        String currentString = path.substring(path.lastIndexOf('/') + 1);

        String[] separated = currentString.split("_");

        Log.e("TAG","Id "+separated[0]);
        Log.e("TAG","Login id "+separated[1]);
        Log.e("TAG","Type "+separated[2]);

        Intent mIntent = new Intent(this, PropertyDetails.class);
        mIntent.putExtra(Constants.EXTRA.DATA, separated[0]);
        mIntent.putExtra(Constants.EXTRA.LOGINID, separated[1]);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, separated[2]);
        mIntent.putExtra(Constants.EXTRA.EXPIRED_STATUS, false);
        mIntent.putExtra(Constants.EXTRA.FROM_TO, "");
        startActivity(mIntent);
        finish();

    }


}
