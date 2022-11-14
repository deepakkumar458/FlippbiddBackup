package com.flippbidd.activity.reportcontent;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedHashMap;

import androidx.annotation.Nullable;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ReportContentActivity extends BaseActivity {


    private String mUserId = "", mCommonType = "", mCommonId = "", radioType = "";

    Disposable disposable;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_content_layout);

        //get Bundle
        Bundle mBundle = getIntent().getExtras();

        if (mBundle != null) {
            mCommonType = mBundle.getString("post_type");
            mCommonId = mBundle.getString("post_id");
            mUserId = mBundle.getString("user_id");
        }


        initView();

        eventClick();
    }

    private void eventClick() {

        findViewById(R.id.txtCancelReportView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        ((RadioGroup) findViewById(R.id.radioGroupReport)).setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioOffensive) {
                    radioType = "Offensive";
                }
                if (checkedId == R.id.radioInaccurate) {
                    radioType = "Inaccurate";
                }
                if (checkedId == R.id.radioViolate) {
                    radioType = "Violate State/Federal Laws";
                }
            }
        });

        findViewById(R.id.relativeReportProperty).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strMessage = ((CustomEditText) findViewById(R.id.editTextMessageFeedBack)).getText().toString();
                if (strMessage.isEmpty()) {
                    strMessage = "";
                }

                if (radioType.isEmpty()) {
                    showSnkbar(Constants.SELECT_REPORT_TYPE);
                    return;
                }

                addReport(radioType, strMessage);

            }
        });
    }

    private void initView() {
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    private void addReport(String mType, String mNotes) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }

        showProgressBar(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(ReportContentActivity.this).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(ReportContentActivity.this).getUserDetail().getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mType));
        linkedHashMap.put("notes", RequestBody.create(MediaType.parse("multipart/form-data"), mNotes));

        UserServices userService = ApiFactory.getInstance(ReportContentActivity.this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.reportSubmit(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
                    //showSnkbar(response.getText());
                    onBackPressed();
                } else {
                    showSnkbar(response.getText());
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }

}
