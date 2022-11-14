package com.flippbidd.utils;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;


import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;

import androidx.appcompat.app.AlertDialog;

public class DialogUtils {

    public static void showConnectionRetryDialog(final Context context) {

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectionManager.connect(context, PreferenceUtils.getUserId(), PreferenceUtils.getNickname(), PreferenceUtils.getProfilePic(),null);
            }
        }, 5000);
     /*   String[] options = new String[]{context.getString(R.string.option_retry), context.getString(R.string.option_retry_in_5second), context.getString(R.string.option_offline)};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.connect_failed));
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        ConnectionManager.connect(context, PreferenceUtils.getUserId(), PreferenceUtils.getNickname(),PreferenceUtils.getProfilePic(), null);
                        break;
                    case 1:
                        dialog.dismiss();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ConnectionManager.connect(context, PreferenceUtils.getUserId(), PreferenceUtils.getNickname(), PreferenceUtils.getProfilePic(),null);
                            }
                        }, 5000);
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                    default:
                        break;
                }
            }
        }).create().show();*/
    }

    public static void showConnectionRequireDialog(final Activity activity) {
       /* AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(activity.getString(R.string.connection_error));
        builder.setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.finish();
            }
        }).create().show();*/
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ConnectionManager.connect(activity, PreferenceUtils.getUserId(), PreferenceUtils.getNickname(), PreferenceUtils.getProfilePic(),null);
            }
        }, 5000);
    }
}
