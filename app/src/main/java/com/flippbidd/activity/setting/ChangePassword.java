package com.flippbidd.activity.setting;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.activity.Login;
import com.flippbidd.baseclass.BaseAppCompatActivity;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ChangePassword extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(ChangePassword.class);

    @BindView(R.id.textViewOldPassword)
    CustomEditText textViewOldPassword;
    @BindView(R.id.textViewNewPassword)
    CustomEditText textViewNewPassword;
    @BindView(R.id.textViewConfirmPassword)
    CustomEditText textViewConfirmPassword;
    @BindView(R.id.btnChangePassword)
    CustomAppCompatButton btnChangePassword;
    @BindView(R.id.imageBackIcon)
    ImageView imageBackIcon;

    Disposable disposable;
    Validator mValidator;
    private String mOldPassword, mNewPassword, mConfirmPassword;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_change_password_layout;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnClick({R.id.imageBackIcon, R.id.btnChangePassword})
    void viewClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangePassword:
                try {
                    hideKeyboard();
                    mOldPassword = textViewOldPassword.getText().toString();
                    mNewPassword = textViewNewPassword.getText().toString();
                    mConfirmPassword = textViewConfirmPassword.getText().toString();

                    mValidator = Validator.getInstance();
                    if (!Validation()) {
                        //call api
                        callChangePassword();
                    }

                } catch (Exception e) {

                }
                break;
            case R.id.imageBackIcon:
                onBackPressed();
                break;


        }

    }

    private void callChangePassword() {


        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //login_id, old_password, new_password
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("old_password", RequestBody.create(MediaType.parse("multipart/form-data"), mOldPassword));
        hashMap.put("new_password", RequestBody.create(MediaType.parse("multipart/form-data"), mNewPassword));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.changePassword(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    openSuccessDialog(response.getText());
                } else {
                    openErorrDialog(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });


    }

    public boolean Validation() {
        if (mValidator.isEmpty(mOldPassword)) {
            seekBarShow(Constants.vl_old_password, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mNewPassword)) {
            seekBarShow(Constants.vl_new_password, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mConfirmPassword)) {
            seekBarShow(Constants.vl_confirm_password, Color.RED);
            return true;
        } else if (mValidator.checkEquals(mNewPassword, mConfirmPassword)) {
            seekBarShow(Constants.vl_passwordnomatch, Color.RED);
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    public void openSuccessDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(mBaseAppCompatActivity);
        mPromptDialog.setSingleButton(false);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(mBaseAppCompatActivity.getString(R.string.string_change_password_success));
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                //ChatHelper.getInstance().logout(mBaseAppCompatActivity);
                UserPreference.getInstance(mBaseAppCompatActivity).clear();
                startActivity(new Intent(mBaseAppCompatActivity, Login.class), false);
                finishAffinity();

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
}
