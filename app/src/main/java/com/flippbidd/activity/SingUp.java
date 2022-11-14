package com.flippbidd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.IPortal.Response;
import com.flippbidd.Model.Response.LoginResponse;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PdfViewer;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;
import com.flippbidd.sendbirdSdk.widget.AuthenticationUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.hbb20.CountryCodePicker;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.Nullable;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SingUp extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(SingUp.class);

    public static final String EXTRA_NAME = "extra_name";
    public static final String EXTRA_EMAIL = "extra_email";
    public static final String EXTRA_USER_NAME = "extra_user_name";
    public static final String EXTRA_TYPE = "extra_type";

    @BindView(R.id.textViewSingUpFullName)
    CustomEditText textViewSingUpFullName;
    @BindView(R.id.textViewSingUpUserName)
    CustomEditText textViewSingUpUserName;
    @BindView(R.id.textViewSingUpEmail)
    CustomEditText textViewSingUpEmail;
    @BindView(R.id.textViewSingUpPassword)
    CustomEditText textViewSingUpPassword;
    @BindView(R.id.textViewSingUpConfirmPassword)
    CustomEditText textViewSingUpConfirmPassword;
    //    @BindView(R.id.textViewSelectStates)
//    CustomTextView textViewSelectStates;
//    @BindView(R.id.textViewSelectCity)
//    CustomTextView textViewSelectCity;
    @BindView(R.id.etRegisterPhone)
    CustomEditText loEtRegisterPhone;
    @BindView(R.id.checkBoxTermsAndCondistion)
    CheckBox checkBoxTermsAndCondistion;

//    @BindView(R.id.iconBackBtn)
//    ImageView iconBackBtn;

    //    @BindView(R.id.relativeSingUpSelectCity)
//    RelativeLayout relativeSingUpSelectCity;
//    @BindView(R.id.relativeSingUpSelectState)
    RelativeLayout relativeSingUpSelectState;
    @BindView(R.id.textViewTermsAndCondistion)
    CustomTextView textViewTermsAndCondistion;
    @BindView(R.id.registerPhoneCode)
    CountryCodePicker mRegisterPhoneCode;

    @BindView(R.id.btnSubmite)
    CustomAppCompatButton btnSubmite;
    @BindView(R.id.tvSingInOpen)
    CustomTextView tvSingInOpen;

    Validator mValidator;
    String mFullName, mUserNam, mEmail, phoneCode, mMobileNumaber, mPassword, mConfirmPassword, mState, mCity, mTremsAndCondistionIsSelected;
    //mStateId, mCityId;
    String is_Social, is_social_type;
    Disposable disposable;
    private String mToken;


    //Latest updated Project code
    @Override
    protected int getLayoutResource() {
        return R.layout.activity_sign_up;
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
        mToken = UserPreference.getInstance(this).getDeviceToken();

        Bundle moBundle = getIntent().getExtras();
        if (moBundle != null) {
            mFullName = moBundle.getString(EXTRA_NAME);
            mEmail = moBundle.getString(EXTRA_EMAIL);
            is_social_type = moBundle.getString(EXTRA_TYPE);
            mUserNam = moBundle.getString(EXTRA_USER_NAME);
            //set data form social
            textViewSingUpFullName.setText(mFullName);
            textViewSingUpEmail.setText(mEmail);
            textViewSingUpUserName.setText(mUserNam);

        }
        customTextView(textViewTermsAndCondistion);

        //validate
        textViewSingUpUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (containCheck(s)) {
                    CommonDialogView.getInstance().showCommonDialog(SingUp.this, SingUp.this.getResources().getString(R.string.app_name),
                            "You can not pass into email or mobile number!"
                            , ""
                            , SingUp.this.getResources().getString(R.string.string_ok)
                            , false, false, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    textViewSingUpUserName.getText().clear();
                                    textViewSingUpUserName.setText("");
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });

                } else {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean containCheck(CharSequence values) {
        Pattern digitPattern = Pattern.compile("\\d{7}");
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        Matcher m = digitPattern.matcher(values);
        if (m.find()) {
            return true;
        }

        if (values.toString().matches(emailPattern)) {
            return true;
        }

        return false;
    }

    private void customTextView(CustomTextView view) {
        //By signing up, you agree to Flippbidd's Terms of use and Privacy Policy as well End User License Agreement.
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By Signing up, you agree to Flippbidd's ");//40
        spanTxt.append("Terms & Conditions");//51 //58
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.CONDITION_, ""), true);
            }
        }, spanTxt.length() - "Terms & conditions".length(), spanTxt.length(), 0);
        spanTxt.append(" , ");//55//61
        spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#a6a6a6")), 59, spanTxt.length(), 0);
        spanTxt.append("Privacy Policy");//70//75
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.POLICY_, ""), true);
            }
        }, spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        spanTxt.append(" and ");//80
        spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#a6a6a6")), 76, spanTxt.length(), 0);
        spanTxt.append("End User License Agreement.");//106
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.AGGREMENTS_, ""), true);
            }
        }, spanTxt.length() - "End User License Agreement.".length(), spanTxt.length(), 0);


        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.NORMAL);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //update country code
        mRegisterPhoneCode.setCcpDialogShowNameCode(false);
        mRegisterPhoneCode.showArrow(false);
        mRegisterPhoneCode.showNameCode(false);
        mRegisterPhoneCode.setContentColor(R.color.colorPrimaryDark);
        mRegisterPhoneCode.setCountryForNameCode("US");
    }

    private void callSingApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("full_name", RequestBody.create(MediaType.parse("multipart/form-data"), mFullName));
        hashMap.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), mUserNam));
        hashMap.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), mEmail));
        hashMap.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), mMobileNumaber));
        hashMap.put("country_code", RequestBody.create(MediaType.parse("multipart/form-data"), "+" + phoneCode));
        hashMap.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), mPassword));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("is_social_signup", RequestBody.create(MediaType.parse("multipart/form-data"), is_Social));
        hashMap.put("social_type", RequestBody.create(MediaType.parse("multipart/form-data"), is_social_type));
        hashMap.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        hashMap.put("android_token", RequestBody.create(MediaType.parse("multipart/form-data"), mToken));
        hashMap.put("ios_token", RequestBody.create(MediaType.parse("multipart/form-data"), ""));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<LoginResponse> observable = userService.userSingUp(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                hideProgressDialog();
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    UserPreference.getInstance(mBaseAppCompatActivity).setUserDetail(response.getData());
                    UserPreference.getInstance(mBaseAppCompatActivity).setMobileNumber(response.getData().getMobileNumber());

                    PreferenceUtils.setUserId(response.getData().getEmailAddress());
                    PreferenceUtils.setNickname(response.getData().getFullName());
                    PreferenceUtils.setProfilePic("");

                    callPremiumUserStatus();
                    connect(response.getData().getEmailAddress(), response.getData().getFullName(), "");
//                    singToQB(response);
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

    private void callPremiumUserStatus() {
        LinkedHashMap<String, RequestBody> premiumUserRequest = new LinkedHashMap<String, RequestBody>();
        premiumUserRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getToken()));
        premiumUserRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getLoginId()));
        System.out.println("!!!loginIdInLogin = " + UserPreference.getInstance(this).getUserDetail().getLoginId());
        System.out.println("!!!token = " + UserPreference.getInstance(this).getUserDetail().getToken());
        UserServices userService = ApiFactory.getInstance(this).provideService(UserServices.class);
        Observable<Response> observable;
        observable = userService.checkPremiumUserStstus(premiumUserRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<Response>() {

            @Override
            public void onSuccess(Response response) {
                if (response.getSuccess()) {
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                } else {
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                System.out.println("throwable = " + throwable.getMessage());
            }
        });
    }

 /*   //Get State
    private void callStateApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_STATE));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mStateList != null)
                    mStateList.clear();

                hideProgressDialog();

                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mStateList.addAll(response.getData());
                        textViewSelectStates.setText(response.getData().get(0).getCommonName());
                        mStateId = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectStates.setText("");
                    openErorrDialog(getResources().getString(R.string.string__state_load_error_message));
                }


            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //Get City
    private void callCityApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_CITY));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));

        showProgressDialog(isProgress);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                hideProgressDialog();

                if (mCityList != null) {
                    mCityList.clear();
                }
                hideProgressDialog();
                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());
                    textViewSelectCity.setText(response.getData().get(0).getCommonName());
                    mCityId = response.getData().get(0).getCommonId();
                } else {
                    textViewSelectCity.setText("");
                    openErorrDialog(getResources().getString(R.string.string_re_select_file_error_message));
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    @OnTouch({R.id.relativeSingUpSelectCity, R.id.relativeSingUpSelectState})
    boolean onTouchEvent() {
        hideKeyboard();
        return false;
    }*/

    //R.id.relativeSingUpSelectCity, , R.id.relativeSingUpSelectState,R.id.iconBackBtn,
    @OnClick({R.id.textViewTermsAndCondistion, R.id.btnSubmite, R.id.tvSingInOpen})
    void viewClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnSubmite:

                mFullName = textViewSingUpFullName.getText().toString().trim();
                mUserNam = textViewSingUpUserName.getText().toString().trim();
                mEmail = textViewSingUpEmail.getText().toString().trim();
                mPassword = textViewSingUpPassword.getText().toString().trim();
                mConfirmPassword = textViewSingUpConfirmPassword.getText().toString().trim();
/*
                mState = textViewSelectStates.getText().toString().trim();
                mCity = textViewSelectCity.getText().toString().trim();
*/
                is_social_type = "";
                is_Social = "";

                if (checkBoxTermsAndCondistion.isChecked()) {
                    mTremsAndCondistionIsSelected = "isChecked";
                } else {
                    mTremsAndCondistionIsSelected = "";
                }
                //get code
                phoneCode = mRegisterPhoneCode.getSelectedCountryCode();
                mMobileNumaber = loEtRegisterPhone.getText().toString();

                //validation
                mValidator = Validator.getInstance();
                if (!Validation()) {
                    callSingApi();
                }
                break;
            case R.id.tvSingInOpen:
                startActivity(new Intent(SingUp.this, Login.class), true);
                finish(true);
                break;
/*
            case R.id.relativeSingUpSelectState:
                if (mStateList != null && mStateList.size() != 0)
                    showDialogStateView();
                break;
            case R.id.relativeSingUpSelectCity:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
*/
            /*case R.id.iconBackBtn:
                finish(true);
                break;*/
            case R.id.textViewTermsAndCondistion:
//                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.TERMS_AND_CONDITION, ""), true);
                break;

        }
    }

    public boolean Validation() {

        if (mValidator.isEmpty(mFullName)) {
            seekBarShow(Constants.vl_fullname, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mUserNam)) {
            seekBarShow(Constants.vl_username, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mEmail)) {
            seekBarShow(Constants.vl_email, Color.RED);
            return true;
        } else if (mValidator.checkEmail(mEmail)) {
            seekBarShow(Constants.vl_invalid_email, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mMobileNumaber)) {
            seekBarShow(Constants.vl_mobile_number, Color.RED);
            return true;
        } else if (mMobileNumaber.length() <= 0) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        } else if (mMobileNumaber.length() <= 9) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mPassword)) {
            seekBarShow(Constants.vl_password, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mConfirmPassword)) {
            seekBarShow(Constants.vl_confirmpassword, Color.RED);
            return true;
        } else if (mValidator.checkEquals(mPassword, mConfirmPassword)) {
            seekBarShow(Constants.vl_passwordnomatch, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mTremsAndCondistionIsSelected)) {
            seekBarShow(Constants.val_tremsandcondistion, Color.RED);
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


    private void connect(String userId, String userNickname, String url) {
        ConnectionManager.connect(SingUp.this, userId, userNickname, url, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e == null) {
                    SyncManagerUtils.setup(SingUp.this, PreferenceUtils.getUserId(), new CompletionHandler() {
                        @Override
                        public void onCompleted(SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                            PreferenceUtils.setConnected(true);
                            callINTSdk(userId, userNickname);

                        }
                    });
                } else {
                    //seekBarShow(getString(R.string.login_failed), Color.RED);
                    PreferenceUtils.setConnected(false);
                    Intent intent = new Intent(SingUp.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            }
        });

    }

    private void callINTSdk(String userId, String userNickName) {
        if (!TextUtils.isEmpty(BaseApplication.APP_CALL) && !TextUtils.isEmpty(userId)
                && ((BaseApplication) getApplication()).initSendBirdCall(BaseApplication.APP_CALL)) {
            AuthenticationUtils.authenticate(mBaseAppCompatActivity, userId, userNickName, "", isSuccess -> {
                //update user details
                SendBird.updateCurrentUserInfo(userNickName, "", new SendBird.UserInfoUpdateHandler() {
                    @Override
                    public void onUpdated(com.sendbird.android.SendBirdException e) {
                        if (e != null) {
                            //Toast.makeText(context, context.getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                        Intent intent = new Intent(SingUp.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    }
                });
            });
        }
    }

}
