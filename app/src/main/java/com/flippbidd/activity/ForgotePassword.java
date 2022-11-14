package com.flippbidd.activity;

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
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ForgotePassword extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(ForgotePassword.class);
    @BindView(R.id.imageBackIcon)
    ImageView imageBackIcon;
    @BindView(R.id.textViewForgotEmail)
    CustomEditText textViewForgotEmail;
    @BindView(R.id.btnForgotPassword)
    CustomAppCompatButton btnForgotPassword;

    Disposable disposable;
    private String mEmail;
    private Validator mValidator;


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
        return R.layout.activity_forgote_passoword_layout;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnClick({R.id.btnForgotPassword, R.id.imageBackIcon})
    void viewClickEvent(View view) {

        switch (view.getId()) {
            case R.id.imageBackIcon:
                onBackPressed();
                break;
            case R.id.btnForgotPassword:
                hideKeyboard();
                mEmail = textViewForgotEmail.getText().toString();
                mValidator = Validator.getInstance();
                if (!Validation()) {
                    //call api
                    callForgotApi();
                }
                break;
        }
    }

    //LOGIN FORM VALIDATION CHECK
    public boolean Validation() {
        if (mValidator.isEmpty(mEmail)) {
            seekBarShow(Constants.vl_email, Color.RED);
            return true;
        } else if (mValidator.checkEmail(mEmail)) {
            seekBarShow(Constants.vl_invalid_email, Color.RED);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    private void callForgotApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        LinkedHashMap<String, RequestBody> loginRequest = new LinkedHashMap<String, RequestBody>();
        loginRequest.put("email_address", RequestBody.create(MediaType.parse("multipart/form-data"), mEmail));

        //device_type,android_token,ios_token

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.userForgotPassword(loginRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                //{"status":true,"text":"We have made new temporary password for you. You can get password in your mail inbox"}
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

    //Success Dialog
    private void openSuccessDialog(String successMesg) {
        PromptDialog mPromptDialog = new PromptDialog(this);
        mPromptDialog.setSingleButton(true);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));
        mPromptDialog.setContentText(successMesg);
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                onBackPressed();
            }
        });
        mPromptDialog.show();

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
}
