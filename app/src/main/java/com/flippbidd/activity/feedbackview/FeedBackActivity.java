package com.flippbidd.activity.feedbackview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.Feedback.PendingData;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Contract.RequestContractActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.ToastUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FeedBackActivity extends BaseActivity {

    PendingData pendingFeedback;

    private Context moContext;
    private LinearLayoutCompat linearViewMain,linearViewFirst, linearViewSecond, linearViewThird;
    private RadioGroup rAbleToAccess, rMyExperience, rRequestContract, rPropertyCondition;
    private AppCompatImageView imageCloseView;
    private CustomAppCompatButton imageActionSubmit;
    private AppCompatCheckBox feedbackTermsChecked;
    private CustomTextView textViewAbleAccess;
    private Disposable disposable;


    private String is_connect = "2", experience = "", property_condition = "", is_contract = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_view_layout);
        this.moContext = FeedBackActivity.this;

        //get intent data
        getData();
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    private void getData() {
        if (getIntent().hasExtra("data")) {
            pendingFeedback = (PendingData) getIntent().getParcelableExtra("data");
        }
        //set data
        ((CustomTextView) findViewById(R.id.textViewPropertyAddress)).setText(pendingFeedback.getAddress());
        textViewAbleAccess = findViewById(R.id.textViewAbleAccess);
        feedbackTermsChecked = findViewById(R.id.feedbackTermsChecked);
        linearViewMain = findViewById(R.id.linearViewMain);
        linearViewFirst = findViewById(R.id.linearViewFirst);
        linearViewSecond = findViewById(R.id.linearViewSecond);
        linearViewThird = findViewById(R.id.linearViewThird);

        rAbleToAccess = findViewById(R.id.rAbleToAccess);
        rMyExperience = findViewById(R.id.rMyExperience);
        rPropertyCondition = findViewById(R.id.rPropertyCondition);
        rRequestContract = findViewById(R.id.rRequestContract);
        //default value first set say Yes
        RadioButton rb = (RadioButton) findViewById(R.id.rYes);
        visiblityView(rb.getText().toString());
        rAbleToAccess.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                visiblityView(rb.getText().toString());
                is_connect = rb.getTag().toString();
            }
        });

        rMyExperience.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                experience = rb.getTag().toString();
            }
        });
        rPropertyCondition.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                property_condition = rb.getTag().toString();

            }
        });

        rRequestContract.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) findViewById(checkedId);
                is_contract = rb.getTag().toString();
            }
        });


        imageCloseView = findViewById(R.id.imageCloseView);
        imageCloseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.stay, R.anim.slide_down);
            }
        });
        imageActionSubmit = findViewById(R.id.imageActionSubmit);
        imageActionSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!feedbackTermsChecked.isChecked()) {
                    ToastUtils.longToast(Constants.val_tremsandcondistion);
                    return;
                }

                if (pendingFeedback.getFeedbackCount().equalsIgnoreCase("0")) {
                    //first
                    if (is_connect.equalsIgnoreCase("No")) {
                        experience = "";
                        is_contract = "";
                    }

                    addFeedback();

                } else {
                    //second
                    is_connect = "";
                    experience = "";
                    property_condition = "";

                    if (is_contract.equalsIgnoreCase("")) {
                        ToastUtils.longToast(Constants.CHOSE_CONTRACT_VIA_OPTION);
                        return;
                    }
                    addFeedback();
                }
            }
        });

    }

    private void visiblityView(String actionView) {
        //check feedback count
        if (pendingFeedback.getFeedbackCount().equalsIgnoreCase("0")) {
            //first time
            if (pendingFeedback.getType().equalsIgnoreCase("Video Call") || pendingFeedback.getType().equalsIgnoreCase("Audio Call")) {
                //video //auido call
                if (actionView.equalsIgnoreCase("Yes")) {
                    linearViewFirst.setVisibility(View.VISIBLE);
                    linearViewThird.setVisibility(View.VISIBLE);
                } else {
                    linearViewFirst.setVisibility(View.GONE);
                    linearViewThird.setVisibility(View.GONE);
                }
                textViewAbleAccess.setText(getResources().getString(R.string.get_access_property));

            } else {
                if (actionView.equalsIgnoreCase("Yes")) {
                    linearViewFirst.setVisibility(View.VISIBLE);
                    linearViewSecond.setVisibility(View.VISIBLE);
                    linearViewThird.setVisibility(View.VISIBLE);
                } else {
                    linearViewFirst.setVisibility(View.GONE);
                    linearViewSecond.setVisibility(View.GONE);
                    linearViewThird.setVisibility(View.GONE);
                }
                textViewAbleAccess.setText(getResources().getString(R.string.connect_property_admin));
            }
        } else if (pendingFeedback.getFeedbackCount().equalsIgnoreCase("1")) {
            //second time
            linearViewMain.setVisibility(View.GONE);
            linearViewFirst.setVisibility(View.GONE);
            linearViewSecond.setVisibility(View.GONE);
            linearViewThird.setVisibility(View.VISIBLE);

            ((RadioButton)findViewById(R.id.radioMessage)).setText("I am not interested.");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void addFeedback() {
        if (!NetworkUtils.isInternetAvailable(FeedBackActivity.this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        showProgressBar(true);

        //token, meeting_id, login_id, is_connect, experience, property_condition, is_contract
        LinkedHashMap<String, RequestBody> feedbackRequest = new LinkedHashMap<String, RequestBody>();
        feedbackRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        feedbackRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        feedbackRequest.put("meeting_id", RequestBody.create(MediaType.parse("multipart/form-data"), pendingFeedback.getMeetingId()));
        feedbackRequest.put("is_connect", RequestBody.create(MediaType.parse("multipart/form-data"), is_connect));
        feedbackRequest.put("experience", RequestBody.create(MediaType.parse("multipart/form-data"), experience));
        feedbackRequest.put("property_condition", RequestBody.create(MediaType.parse("multipart/form-data"), property_condition));
        feedbackRequest.put("is_contract", RequestBody.create(MediaType.parse("multipart/form-data"), is_contract));

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.feedbackAdd(feedbackRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                showProgressBar(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    //success message
                    if (mJsonObject.has("text")) {
                        openAlertDialog(mJsonObject.get("text").getAsString());
                    }
                } else {
                    //error messsage
                    openAlertDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void openAlertDialog(String s) {
        CommonDialogView.getInstance().showCommonDialog(moContext, "",
                s
                , "Ok"
                , "Ok"
                , false, true, true, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        if (is_contract.equalsIgnoreCase("1")) {
                            startActivityIfNeeded(new Intent(moContext, RequestContractActivity.class)
                                    .putExtra("p_id", pendingFeedback.getPropertyId()).putExtra("p_type", Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY), 764);
                            finish();
                            return;
                        }
                        onBackPressed();
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }
}
