package com.flippbidd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
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
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.hbb20.CountryCodePicker;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class UpdateMobileNumber extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(UpdateMobileNumber.class);
    @BindView(R.id.imageBackIcon)
    ImageView imageBackIcon;
    @BindView(R.id.textViewMobileNumber)
    CustomEditText textViewMobileNumber;
    @BindView(R.id.tvSignUpNow)
    CustomTextView tvSignUpNow;
    @BindView(R.id.btnMobileNumberSubmit)
    CustomAppCompatButton btnMobileNumberSubmit;
    @BindView(R.id.updatePhoneCode)
    CountryCodePicker mUpdatePhoneCode;


    Disposable disposable;
    private String mMobileNumber, mCCode;
    private Validator mValidator;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_update_mobile_number_layout;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

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
//        mUpdatePhoneCode.setAutoDetectedCountry(true);
        mUpdatePhoneCode.setCcpDialogShowNameCode(false);
        mUpdatePhoneCode.showArrow(false);
        mUpdatePhoneCode.showNameCode(false);
        mUpdatePhoneCode.setContentColor(R.color.colorPrimaryDark);
        mUpdatePhoneCode.setCountryForNameCode("US");

//        mUpdatePhoneCode.setAutoDetectionFailureListener(new CountryCodePicker.FailureListener() {
//            @Override
//            public void onCountryAutoDetectionFailed() {
//                mUpdatePhoneCode.setCountryForPhoneCode(189);
//            }
//        });

        tvSignUpNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateMobileNumber.this, SingUp.class));
                finish(true);
            }
        });
    }

    @OnClick({R.id.btnMobileNumberSubmit})
    void viewClickEvent(View view) {

        switch (view.getId()) {
            case R.id.btnMobileNumberSubmit:
                hideKeyboard();
                mCCode = mUpdatePhoneCode.getSelectedCountryCode();
                mMobileNumber = textViewMobileNumber.getText().toString();
                mValidator = Validator.getInstance();
                if (!Validation()) {
                    //call api
                    callUpdateMobileNumberApi();
                }
                break;
        }
    }

    //LOGIN FORM VALIDATION CHECK
    public boolean Validation() {
        if (mValidator.isEmpty(mMobileNumber)) {
            seekBarShow(Constants.vl_mobile_number, Color.RED);
            return true;
        } else if (mMobileNumber.length() <= 0) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        } else if (mMobileNumber.length() <= 9) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    private void callUpdateMobileNumberApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token, login_id, country_code, mobile_number
        LinkedHashMap<String, RequestBody> loginRequest = new LinkedHashMap<String, RequestBody>();
        loginRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        loginRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        loginRequest.put("country_code", RequestBody.create(MediaType.parse("multipart/form-data"), "+" + mCCode));
        loginRequest.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), mMobileNumber));

        //device_type,android_token,ios_token

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateMobileNumber(loginRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {

                    UserPreference.getInstance(UpdateMobileNumber.this).setMobileNumber(mMobileNumber);
                    UserPreference.getInstance(UpdateMobileNumber.this).setCCode(mCCode);

                    Intent intent = new Intent(UpdateMobileNumber.this, MainActivity.class);
                    if (getIntent().hasExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL)) {
                        String pushedChannelUrl = getIntent().getStringExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL);
                        intent.putExtra(MainActivity.EXTRA_GROUP_CHANNEL_URL, pushedChannelUrl);
                    }
                    startActivity(intent);
                    finish();
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
