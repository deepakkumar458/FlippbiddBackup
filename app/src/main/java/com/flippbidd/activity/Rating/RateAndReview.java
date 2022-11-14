package com.flippbidd.activity.Rating;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
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
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.willy.ratingbar.BaseRatingBar;

import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RateAndReview extends BaseAppCompatActivity {

    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;
    @BindView(R.id.rotationratingbar_main)
    BaseRatingBar rotationratingbar_main;
    @BindView(R.id.rotationratingbar_main2)
    BaseRatingBar rotationratingbar_main2;
    @BindView(R.id.rotationratingbar_main3)
    BaseRatingBar rotationratingbar_main3;
    @BindView(R.id.rotationratingbar_main4)
    BaseRatingBar rotationratingbar_main4;
    @BindView(R.id.rotationratingbar_main5)
    BaseRatingBar rotationratingbar_main5;
    @BindView(R.id.btnSubmitFeedback)
    CustomAppCompatButton btnSubmitFeedback;
    @BindView(R.id.editTextViewCommentsValues)
    CustomEditText editTextViewCommentsValues;

    Disposable disposable;
    private String comments = "", userId;
    private String rate1 = "0", rate2 = "0", rate3 = "0", rate4 = "0", rate5 = "0";


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_rate_and_review_layout;
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
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            userId = mBundle.getString("user_id");
//            LogUtils.LOGD("RateandReview", "User Id-->" + userId);
        }


        rotationratingbar_main.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rate1 = String.valueOf(rating);
            }
        });

        rotationratingbar_main2.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rate2 = String.valueOf(rating);
            }
        });

        rotationratingbar_main3.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rate3 = String.valueOf(rating);
            }
        });

        rotationratingbar_main4.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rate4 = String.valueOf(rating);
            }
        });

        rotationratingbar_main5.setOnRatingChangeListener(new BaseRatingBar.OnRatingChangeListener() {
            @Override
            public void onRatingChange(BaseRatingBar ratingBar, float rating) {
                rate5 = String.valueOf(rating);
            }
        });


        editTextViewCommentsValues.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (editTextViewCommentsValues.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }


    @OnClick(R.id.btnSubmitFeedback)
    void viewClick() {
        comments = editTextViewCommentsValues.getText().toString();
        if (comments.length() <= 0) {
            editTextViewCommentsValues.setText("");
            seekBarShow(Constants.ENTER_COMMENT, Color.RED);
            return;
        }

        giveFeedBackUser();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    @OnClick(R.id.imageViewBack)
    void viewBackClick() {
        onBackPressed();
    }

    private void giveFeedBackUser() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
            return;
        }

        //login_id,token,user_id,rate1,rate2,rate3,rate4,rate5,comments
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), userId));
        linkedHashMap.put("rate1", RequestBody.create(MediaType.parse("multipart/form-data"), rate1));
        linkedHashMap.put("rate2", RequestBody.create(MediaType.parse("multipart/form-data"), rate2));
        linkedHashMap.put("rate3", RequestBody.create(MediaType.parse("multipart/form-data"), rate3));
        linkedHashMap.put("rate4", RequestBody.create(MediaType.parse("multipart/form-data"), rate4));
        linkedHashMap.put("rate5", RequestBody.create(MediaType.parse("multipart/form-data"), rate5));
        linkedHashMap.put("comment", RequestBody.create(MediaType.parse("multipart/form-data"), comments));

        showProgressDialog(true);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.giveFeedbackUser(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    setResult(RESULT_OK);
                    finish(true);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();

            }
        });
    }

}
