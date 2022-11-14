package com.flippbidd.activity.AppUpdate;

import static com.vincent.filepicker.Constant.MY_REQUEST_CODE;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.ActivityResult;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.vincent.filepicker.Constant;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

public class AppUpdateActivity extends AppCompatActivity {

    private String updateVersion = "";

    MaterialTextView tvVersionUpdateMessage, tvRemindLater;
    CustomAppCompatButton btnUpdateNow;
    AppUpdateManager appUpdateManager;
    CoordinatorLayout coordinator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (Build.VERSION.SDK_INT >= 23) {

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
//        }
        setContentView(R.layout.activity_app_update);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        updateVersion = getIntent().getExtras().getString("latest_version");
        appUpdateManager = AppUpdateManagerFactory.create(AppUpdateActivity.this);
        initView();
    }

    private void initView() {
        tvVersionUpdateMessage = findViewById(R.id.tvVersionUpdateMessage);
        tvRemindLater = findViewById(R.id.tvRemindLater);
        btnUpdateNow = findViewById(R.id.btnUpdateNow);
        coordinator = findViewById(R.id.coordinator);

        String versionMessage = getString(R.string.app_update_message, updateVersion);
        tvVersionUpdateMessage.setText(versionMessage);

        btnUpdateNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                finish();*/


                Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
                appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
                    @Override
                    public void onSuccess(AppUpdateInfo appUpdateInfo) {
                        if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                            //request for update here
                            try {
                                appUpdateManager.startUpdateFlowForResult(appUpdateInfo, AppUpdateType.FLEXIBLE, AppUpdateActivity.this, MY_REQUEST_CODE);
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }

                            InstallStateUpdatedListener listener = state -> {
                                // (Optional) Provide a download progress bar.
                                if (state.installStatus() == InstallStatus.DOWNLOADING) {
                                    long bytesDownloaded = state.bytesDownloaded();
                                    long totalBytesToDownload = state.totalBytesToDownload();
                                    Snackbar snackbar =
                                            Snackbar.make(
                                                    findViewById(R.id.coordinator),
                                                    "An update has just been downloaded.",
                                                    Snackbar.LENGTH_INDEFINITE);
                                    snackbar.setAction("RESTART", view -> appUpdateManager.completeUpdate());
                                    snackbar.setActionTextColor(
                                            getResources().getColor(R.color.black));
                                    snackbar.show();

                                }
                                // Log state or install the update.
                            };

                            // Before starting an update, register a listener for updates.
                            appUpdateManager.registerListener(listener);

                            // Start an update.

                            // When status updates are no longer needed, unregister the listener.
                            appUpdateManager.unregisterListener(listener);

                            appUpdateManager.completeUpdate();

                        } else {
                            //no updates available.
                            Toast.makeText(AppUpdateActivity.this, "No update Available.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        tvRemindLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
            ) {
                // If an in-app update is already running, resume the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            AppUpdateType.FLEXIBLE,
                            this,
                            MY_REQUEST_CODE
                    );
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE) {
            switch (resultCode) {
                case Activity.RESULT_OK://handle user's approval
                case Activity.RESULT_CANCELED://Result cancel
                case ActivityResult.RESULT_IN_APP_UPDATE_FAILED://handle update failure.
            }

        }
    }
}