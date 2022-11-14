package com.flippbidd.baseclass;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;

import java.io.File;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.refactor.lib.colordialog.PromptDialog;


public abstract class BaseFragment extends Fragment {

    private static final String TAG = LogUtils.makeLogTag(BaseFragment.class);
    protected BaseAppCompatActivity mBaseAppCompatActivity;

    @BindView(R.id.mRelativeLayout)
    RelativeLayout mRelativeLayout;
    @BindView(R.id.lt_loading_view)
    LottieAnimationView mLottieAnimationView;

    private Unbinder unbinder;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mBaseAppCompatActivity = (BaseAppCompatActivity) context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Error Dialog
    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(getActivity());
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
        mPromptDialog.setAnimationEnable(true);

        if (text.equalsIgnoreCase(getResources().getString(R.string.no_internet))) {
            mPromptDialog.setTitleText(getString(R.string.string_no_internet_header));
        } else {
            mPromptDialog.setTitleText(getString(R.string.string_error));
        }
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutResource(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    protected abstract int getLayoutResource();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            deleteCache(getActivity());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        unbinder.unbind();
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


    protected void hideKeyboard() {
        View view = mBaseAppCompatActivity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) mBaseAppCompatActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    protected void launchActivity(Intent intent) {
        mBaseAppCompatActivity.launchActivity(intent);
    }

    public void launchActivity(Intent intent, boolean finishCurrent) {
        launchActivity(intent, mBaseAppCompatActivity.DEFAULT_REQUEST_CODE, finishCurrent);
    }

    protected void launchActivity(Intent intent, int requestCode, boolean finishCurrent) {
        mBaseAppCompatActivity.launchActivity(intent, requestCode, finishCurrent);
//        startActivityIfNeeded(intent, requestCode);
    }

    public void showToast(String message) {
        if (getActivity() != null && !TextUtils.isEmpty(message)) {
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
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


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void shareApp() {

        String message = "";

        LogUtils.LOGD(TAG, "shareApp() called: " + message);
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

}
