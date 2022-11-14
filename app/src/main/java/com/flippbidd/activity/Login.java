package com.flippbidd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.IPortal.Response;
import com.flippbidd.Model.Response.LoginResponse;
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
import com.flippbidd.gcm.RegistrationIntentService;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;
import com.flippbidd.sendbirdSdk.widget.AuthenticationUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.syncmanager.handler.CompletionHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


public class Login extends BaseAppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = LogUtils.makeLogTag(Login.class);
    @BindView(R.id.textViewLoginEmail)
    CustomEditText textViewLoginEmail;
    @BindView(R.id.textViewLoginPassword)
    CustomEditText textViewLoginPassword;
    @BindView(R.id.textViewForgotPassword)
    CustomTextView textViewForgotPassword;
    @BindView(R.id.checkBoxKeepSingin)
    CheckBox checkBoxKeepSingin;
    @BindView(R.id.btnLogin)
    CustomAppCompatButton btnLogin;
    @BindView(R.id.textViewSingUp)
    CustomAppCompatButton textViewSingUp;
    @BindView(R.id.gSingInBtn)
    SignInButton gSingInBtn;
    @BindView(R.id.btnGoogleSingIn)
    CustomAppCompatButton btnGoogleSingIn;

    //facebook
    @BindView(R.id.fbButton)
    LoginButton fbButton;
    @BindView(R.id.btnFaceBook)
    CustomAppCompatButton btnFaceBook;
    //google sing in
    private CallbackManager callbackManager;
    private GoogleSignInOptions gso;
    private GoogleApiClient mGoogleApiClient;
    private int SIGN_IN = 30;


    private Validator mValidator;
    private String mEmail, mPassword;
    Disposable disposable;
    private String mToken, mFullName, mUserNam, mStateId = "", mCityId = "", is_Social, is_social_type;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 4000;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_login;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            final Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        //facebook
        fbButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }


    @OnClick({R.id.btnLogin, R.id.textViewSingUp, R.id.textViewForgotPassword, R.id.btnGoogleSingIn, R.id.btnFaceBook})
    void viewClickEvent(View view) {

        switch (view.getId()) {
            case R.id.btnLogin:
                hideKeyboard();
                mEmail = textViewLoginEmail.getText().toString();
                mPassword = textViewLoginPassword.getText().toString();
                mValidator = Validator.getInstance();
                if (!Validation()) {
                    //call api
                    callLoginApi();
                }
                break;
            case R.id.textViewSingUp:
                openSingUpActivity();
                break;
            case R.id.textViewForgotPassword:
                //call forgot password activity
                startActivity(new Intent(Login.this, ForgotePassword.class), true);
                break;
            case R.id.btnGoogleSingIn:
                if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
                    openErorrDialog(getString(R.string.no_internet));
                    return;
                }
                is_Social = "1";
                is_social_type = "google";
                singInCall();
                break;

            case R.id.btnFaceBook:
                if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
                    openErorrDialog(getString(R.string.no_internet));
                    return;
                }
                is_Social = "0";
                is_social_type = "facebook";
                facebookSingInCall();
                break;

        }
    }

    private void facebookSingInCall() {
        fbButton.setReadPermissions(Arrays.asList("email", "public_profile"));
        fbButton.performClick();
        callbackManager = CallbackManager.Factory.create();

        fbButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                boolean loggedIn = AccessToken.getCurrentAccessToken() == null;
                getUserProfile(AccessToken.getCurrentAccessToken());

            }

            @Override
            public void onCancel() {
                // App code
                Log.d("onCancel()", "FaceBook onCancel");
                faceBookLogout();


            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("onError()", "FaceBook Error");
                faceBookLogout();

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    private void singInCall() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityIfNeeded(signInIntent, SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //If signin
        if (requestCode == SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            //Calling a new function to handle signin
            handleSignInResult(result);
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        //If the login succeed
        if (result.isSuccess()) {
            //Getting google account
            final GoogleSignInAccount acct = result.getSignInAccount();

            //Displaying name and email
            mFullName = acct.getDisplayName();
            mEmail = acct.getEmail();
            mUserNam = acct.getDisplayName();

            googleLogout();
            callSingApi("google");
        } else {
            //If login fails
            Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
            Log.d("GOOGLE LOGIN ->", "STATUS" + result.getStatus());
        }
    }

    //facebook user details
    private void getUserProfile(AccessToken currentAccessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("TAG", object.toString());
                        try {
                            String first_name = object.getString("first_name");
                            String last_name = object.getString("last_name");
                            mFullName = first_name + " " + last_name;
                            mEmail = object.getString("email");
                            mUserNam = mFullName;
//                            String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";

                            callSingApi("facebook");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(parameters);
        request.executeAsync();

    }

    private void callSingApi(String isSocialType) {
        mToken = UserPreference.getInstance(this).getDeviceToken();
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("full_name", RequestBody.create(MediaType.parse("multipart/form-data"), mFullName));
        hashMap.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), mUserNam));
        hashMap.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), mEmail));
        hashMap.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("country_code", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("is_social_signup", RequestBody.create(MediaType.parse("multipart/form-data"), is_Social));
        hashMap.put("social_type", RequestBody.create(MediaType.parse("multipart/form-data"), isSocialType));
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
                  ///////////////// need to remove /
                    if (response.getData().getPlan().equalsIgnoreCase(Constants.PRO_VERSION)) {
                        ///   updated the updagraded to pro text at the drawer for all users(pro) //
                        PreferenceUtils.setPlanVersionStatus(true);
                        System.out.println("Pro user is set");
                    }
                    else if(response.getData().getPlan().equalsIgnoreCase("PRO USER")){
                          PreferenceUtils.setUserPro("PRO USER");
                    }else {
                        PreferenceUtils.setPlanVersionStatus(false);
                    }
                    String profileUrl = "";
                    if (response.getData().getProfilePic() != null && !response.getData().getProfilePic().equalsIgnoreCase("")) {
                        PreferenceUtils.setProfilePic(response.getData().getProfilePic());
                        profileUrl = response.getData().getProfilePic();
                    } else {
                        PreferenceUtils.setProfilePic("");
                        profileUrl = "";
                    }

                    connect(response.getData().getEmailAddress(), response.getData().getFullName(), profileUrl);
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


    private void openSingUpActivity() {
        startActivity(new Intent(Login.this, SingUp.class), true);
        finish(true);
    }

    private void callLoginApi() {
        mToken = UserPreference.getInstance(this).getDeviceToken();
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        LinkedHashMap<String, RequestBody> loginRequest = new LinkedHashMap<String, RequestBody>();
        loginRequest.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), mEmail));
        loginRequest.put("password", RequestBody.create(MediaType.parse("multipart/form-data"), mPassword));
        loginRequest.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        loginRequest.put("android_token", RequestBody.create(MediaType.parse("multipart/form-data"), mToken));
        loginRequest.put("ios_token", RequestBody.create(MediaType.parse("multipart/form-data"), ""));

        //device_type,android_token,ios_token
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<LoginResponse> observable = userService.userLogin(loginRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                hideProgressDialog();
                //  LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    UserPreference.getInstance(mBaseAppCompatActivity).setUserDetail(response.getData());
                    UserPreference.getInstance(mBaseAppCompatActivity).setMobileNumber(response.getData().getMobileNumber());
                    PreferenceUtils.setUserId(response.getData().getEmailAddress());
                    PreferenceUtils.setNickname(response.getData().getFullName());
                    PreferenceUtils.setNickname(response.getData().getFullName());
                    PreferenceUtils.setProfilePic(response.getData().getProfilePic());
                    //
                    PreferenceUtils.setPlanVersionStatus(true);
                    //PreferenceUtils.setIsPremiumUser(response.getData().getIs_premium_user());
                    callPremiumUserStatus();


                    connect(response.getData().getEmailAddress(), response.getData().getFullName(), response.getData().getProfilePic());
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
                    //// bug
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                }else {
                    // bug
                    PreferenceUtils.setIsPremiumUser(Integer.valueOf(response.getData().getIsApprove()));
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                System.out.println("throwable = " + throwable.getMessage());
            }
        });
    }

    private void connect(String userId, String userNickname, String url) {
        ConnectionManager.connect(Login.this, userId, userNickname, url, new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (e == null) {
                    SyncManagerUtils.setup(Login.this, PreferenceUtils.getUserId(), new CompletionHandler() {
                        @Override
                        public void onCompleted(SendBirdException e) {
                            if (e != null) {
                                e.printStackTrace();
                                return;
                            }
                            PreferenceUtils.setConnected(true);
                            //init call sdk
                            callINTSdk(userId, userNickname);
                        }
                    });
                } else {
                    //seekBarShow(getString(R.string.login_failed), Color.RED);
                    PreferenceUtils.setConnected(false);
                    if (UserPreference.getInstance(Login.this).getMobileNumber() != null && !UserPreference.getInstance(Login.this).getMobileNumber().equalsIgnoreCase("")) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        //open update mobile number activity
                        Intent mNewIntent = new Intent(Login.this, UpdateMobileNumber.class);
                        startActivity(mNewIntent);
                        finish();
                    }
                }
            }
        });
    }

    private void callINTSdk(String userId, String userNickName) {
        if (!TextUtils.isEmpty(BaseApplication.APP_CALL) && !TextUtils.isEmpty(userId)
                && ((BaseApplication) getApplication()).initSendBirdCall(BaseApplication.APP_CALL)) {
            AuthenticationUtils.authenticate(mBaseAppCompatActivity, userId, userNickName, "", isSuccess -> {
                if (isSuccess) {
                    if (!UserPreference.getInstance(Login.this).getMobileNumber().equalsIgnoreCase("null") && !UserPreference.getInstance(Login.this).getMobileNumber().equalsIgnoreCase("")) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finishAffinity();
                    } else {
                        //open update mobile number activity
                        Intent mNewIntent = new Intent(Login.this, UpdateMobileNumber.class);
                        startActivity(mNewIntent);
                        finish();
                    }
                } else {
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finishAffinity();
                }
            });
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
        } else if (mValidator.isEmpty(mPassword)) {
            seekBarShow(Constants.vl_password, Color.RED);
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

    private void afterLogin() {
        Intent intent = new Intent(mBaseAppCompatActivity, MainActivity.class);
        startActivity(intent, true);
        finish();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private void googleLogout() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                    }
                });
    }

    private void faceBookLogout() {
        boolean loggedOut = AccessToken.getCurrentAccessToken() == null;
        if (!loggedOut) {
            //Using Graph API
            LoginManager.getInstance().logOut();
        }
    }

    private boolean checkPlayServices() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        if (mGoogleApiClient != null) {
//
//            mGoogleApiClient.stopAutoManage(this);
//            mGoogleApiClient.disconnect();
//        }
//    }

}

