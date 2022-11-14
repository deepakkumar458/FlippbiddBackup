package com.flippbidd.baseclass;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.facebook.FacebookSdk;
import com.flippbidd.BuildConfig;
import com.flippbidd.Model.ConnectionEvent;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.widget.WaitingDialog;
import com.flippbidd.utils.DialogUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.google.android.material.snackbar.Snackbar;
import com.sendbird.android.SendBird;
import com.sendbird.syncmanager.SendBirdSyncManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.refactor.lib.colordialog.PromptDialog;

/**
 * Created by Pranay on 14/4/17.
 */

public abstract class BaseAppCompatActivity extends AppCompatActivity {

    private String TAG = "BaseAppCompatActivity";

    protected BaseAppCompatActivity mBaseAppCompatActivity;
    public int DEFAULT_REQUEST_CODE = 100;
    private int FRAGMENT_TRANSACTION_ADD = 200;
    private int FRAGMENT_TRANSACTION_REPLACE = 300;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 111;
    private static final int REQUEST_CODE_PERMISSION_SETTING = 222;
    private static final int REQUEST_CODE_LOCATION_SETTING = 333;
    private static final int FROM_CAMERA_AND_GALLARY = 2;


    @BindView(R.id.mRelativeLayout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.lt_loading_view)
    LottieAnimationView mLottieAnimationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this);
        setContentView(getLayoutResource());
        ButterKnife.bind(this);

        mBaseAppCompatActivity = this;
    }

    protected abstract int getLayoutResource();

    public void launchActivity(Intent intent) {
        launchActivity(intent, false);
    }

    public void launchActivity(Intent intent, boolean finishCurrent) {
        launchActivity(intent, DEFAULT_REQUEST_CODE, finishCurrent);
    }

    public void launchActivity(Intent intent, int requestCode, boolean finishCurrent) {
        if (requestCode != DEFAULT_REQUEST_CODE) {
            startActivityIfNeeded(intent, requestCode);
        } else {
            if (finishCurrent) {
                finish();
            }
            startActivity(intent);
        }
    }

    public void addFragment(Fragment fragment) {
        addFragment(fragment, false);
    }

    public void addFragment(Fragment fragment, boolean addToBackStack) {
        loadFragment(fragment, FRAGMENT_TRANSACTION_ADD, addToBackStack);
    }

    public void replaceFragment(Fragment fragment) {
        replaceFragment(fragment, false);
    }

    public void replaceFragment(Fragment fragment, boolean addToBackStack) {
        loadFragment(fragment, FRAGMENT_TRANSACTION_REPLACE, addToBackStack);
    }

    private void loadFragment(Fragment fragment, int transactionType) {
        loadFragment(fragment, transactionType, false);
    }

    private void loadFragment(Fragment fragment, int transactionType, boolean addToBackStack) {
        loadFragment(fragment, R.id.content_frame, transactionType, addToBackStack);
        //container
    }

    void loadFragment(Fragment fragment, int containerId, int transactionType, boolean addToBackStack) {
        String fragmentName = fragment.getClass().getSimpleName();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragmentName);
        }
        if (transactionType == FRAGMENT_TRANSACTION_ADD) {
            fragmentTransaction.add(containerId, fragment, fragmentName);
        } else {
            fragmentTransaction.replace(containerId, fragment, fragmentName);
            //clare stack code
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction.commit();
    }

    public void showDialogFragment(AppCompatDialogFragment appCompatDialogFragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(appCompatDialogFragment, appCompatDialogFragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void showDialogFragment(Fragment targetFragment, AppCompatDialogFragment appCompatDialogFragment, int requestCode) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        appCompatDialogFragment.setTargetFragment(targetFragment, requestCode);
        fragmentTransaction.add(appCompatDialogFragment, appCompatDialogFragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    public void showDialogFragment(BaseDialogFragment targetFragment, AppCompatDialogFragment appCompatDialogFragment, int requestCode) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        appCompatDialogFragment.setTargetFragment(targetFragment, requestCode);
        fragmentTransaction.add(appCompatDialogFragment, appCompatDialogFragment.getClass().getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showToast(int resourceId) {
        Toast.makeText(this, resourceId, Toast.LENGTH_SHORT).show();
    }

    public void showToast(String resource) {
        Toast.makeText(this, resource, Toast.LENGTH_SHORT).show();
    }


    public void showToast(int resourceId, int toastLong) {
        Toast.makeText(this, resourceId, toastLong).show();
    }

    public void showToast(String resource, int toastLong) {
        Toast.makeText(this, resource, toastLong).show();
    }

    public void startApiService(Intent intent) {
        startService(intent);
    }

    public void finishWithResultOk() {
        finishWithResultOk(null);
    }

    protected void finishWithResultOk(Intent intent) {
        if (intent == null)
            setResult(RESULT_OK);
        else
            setResult(RESULT_OK, intent);
        finish();
    }

    protected void finishWithResultCancel() {
        finishWithResultCancel(null);
    }

    protected void finishWithResultCancel(Intent intent) {
        if (intent == null)
            setResult(RESULT_CANCELED);
        else
            setResult(RESULT_CANCELED, intent);
        finish();
    }

    protected Fragment findFragment(Class fragment) {
        Fragment fragment1 = getSupportFragmentManager().findFragmentByTag(fragment.getSimpleName());
        if (fragment1 != null && fragment1.isVisible())
            return fragment1;
        else
            return null;
    }

    public void showProgressDialog(boolean isShow) {
        if (mRelativeLayout != null) {
            if (isShow) {
                mRelativeLayout.setVisibility(View.VISIBLE);
                mLottieAnimationView.setVisibility(View.VISIBLE);
                mLottieAnimationView.setAnimation("progress.json");
                mLottieAnimationView.loop(true);
                mLottieAnimationView.playAnimation();
            } else {
                mLottieAnimationView.setVisibility(View.GONE);
                mRelativeLayout.setVisibility(View.GONE);
                mLottieAnimationView.pauseAnimation();
            }
        }
    }

    public void hideProgressDialog() {
        if (mRelativeLayout != null) {
            mRelativeLayout.setVisibility(View.GONE);
            mRelativeLayout.setVisibility(View.GONE);

            if (mLottieAnimationView.isAnimating())
                mLottieAnimationView.pauseAnimation();
        }
    }

    public void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    //Error Dialog
    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
        mPromptDialog.setAnimationEnable(true);
        if (text.equalsIgnoreCase(getResources().getString(R.string.no_internet))) {
            mPromptDialog.setTitleText(getString(R.string.string_no_internet_header));
        } else {
            mPromptDialog.setTitleText(getString(R.string.string_error));
        }
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }


    @Override
    public void onBackPressed() {
//        int count = mBaseAppCompatActivity.getFragmentManager().getBackStackEntryCount();

        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
//            getFragmentManager().popBackStack();
            getSupportFragmentManager().popBackStack();
        }
        hideKeyboard();
    }

    public boolean hasLocationPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean shouldShowLocationRationale() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
            return true;
        }
        return false;
    }

    public void requestLocationPermission() {
        if (shouldShowLocationRationale()) {
            showPermissionRationale();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_CODE_LOCATION_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    onPermissionGranted();
                } else {
                    if (shouldShowLocationRationale()) {
                        showPermissionRationale();
                    } else {
                        onPermissionDenied();
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try {
            deleteCache(this);
        } catch (Exception e) {
            //  TODO Auto-generated catch block
            e.printStackTrace();
        }
//        BaseApplication.getInstance().clearApplicationData();
    }

    protected void onPermissionDenied() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Settings")
                .setMessage("Open setting and enable permission.")
                .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                        startActivityIfNeeded(intent, REQUEST_CODE_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    protected abstract void onPermissionGranted();

    /**
     * show rationale dialog if don't ask again checkbox not checked and denied permission
     */
    protected void showPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("Location Permission")
                .setMessage("Location permission is required to access your current location.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(BaseAppCompatActivity.this,
                                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_CODE_LOCATION_PERMISSION);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PERMISSION_SETTING) {
            if (hasLocationPermission()) {
                onPermissionGranted();
            } else {
                onPermissionDenied();
            }
        } else if (requestCode == REQUEST_CODE_LOCATION_SETTING) {
            if (isLocationEnabled()) {
                onLocationEnable();
            }
        }else if (requestCode == 1000) {
            Fragment fragment = (Fragment) getSupportFragmentManager().findFragmentByTag("4");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    protected abstract void onLocationEnable();

    public boolean isLocationEnabled() {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            try {
                locationMode = Settings.Secure.getInt(getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }
            return locationMode != Settings.Secure.LOCATION_MODE_OFF;
        } else {
            locationProviders = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public void openLocationRequestDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Location Request")
                .setMessage("Enable your location.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startActivityIfNeeded(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), REQUEST_CODE_LOCATION_SETTING);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }


    // ------------------------------------------------------------// Activity Transition Support functions// ------------------------------------------------------------
    public void startActivity(Intent intent, boolean animate) {
        super.startActivity(intent);
        if (animate)
            overridePendingTransition(R.anim.animation_slide_from_right, R.anim.animation_slide_to_left);
    }

    public void startActivitybottomToTop(Intent intent, boolean animate) {
        super.startActivity(intent);
        if (animate)
            overridePendingTransition(R.anim.slide_up, R.anim.stay);
    }

    public void finishTop(boolean animate) {
        super.finish();
        if (animate)
            overridePendingTransition(R.anim.stay, R.anim.slide_down);
    }// reverse animation


    public void startActivityIfNeeded(Intent intent, int requestCode, boolean animate) {
        super.startActivityIfNeeded(intent, requestCode);
        if (animate)
            overridePendingTransition(R.anim.animation_slide_from_right, R.anim.animation_slide_to_left);
    }

    public void finish(boolean animate) {
        super.finish();
        if (animate)
            overridePendingTransition(R.anim.animation_slide_from_left, R.anim.animation_slide_to_right);
    }// reverse animation

    public void finish(boolean animate, boolean reverseAnimate) {
        super.finish();
        if ((animate == true) && (reverseAnimate == true))
            overridePendingTransition(R.anim.animation_slide_from_left, R.anim.animation_slide_to_right);
        else if ((animate == true) && (reverseAnimate == false))
            overridePendingTransition(R.anim.animation_slide_from_left, R.anim.animation_slide_to_right);
    }

    public void startActivity(Intent intent, boolean animate, boolean reverseAnimate) {
        super.startActivity(intent);
        if ((animate == true) && (reverseAnimate == true))
            overridePendingTransition(R.anim.animation_slide_from_left, R.anim.animation_slide_to_right);
        else if ((animate == true) && (reverseAnimate == false))
            overridePendingTransition(R.anim.animation_slide_from_right, R.anim.animation_slide_to_left);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    /*for chat base */
    @Override
    protected void onResume() {
        super.onResume();

        registerConnectionHandler();

        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    protected String getConnectionHandlerId() {
        return "CONNECTION_HANDLER_MAIN_ACTIVITY";
    }

    private void registerConnectionHandler() {
        SendBird.addConnectionHandler(getConnectionHandlerId(), new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
                SendBirdSyncManager.getInstance().pauseSync();
            }

            @Override
            public void onReconnectSucceeded() {
                SendBirdSyncManager.getInstance().resumeSync();
            }

            @Override
            public void onReconnectFailed() {
            }
        });
    }

    // Shows or hides the ProgressBar
    protected void showProgressBar(boolean show) {
        if (show) {
            WaitingDialog.show(this);
        } else {
            WaitingDialog.dismiss();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        SendBird.removeConnectionHandler(getConnectionHandlerId());
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ConnectionEvent event) {
        if (!event.isConnected() && PreferenceUtils.getConnected()) {
            DialogUtils.showConnectionRetryDialog(this);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

}
