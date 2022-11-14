package com.flippbidd.activity.Details;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.flippbidd.Adapter.PropertyBiddListAdapter;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.BidListResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.widget.WaitingDialog;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PropertyBiddListActivity extends AppCompatActivity implements PropertyBiddListAdapter.onItemClickLisnear {


    String mPostUserId, mCommonId, mScreenName;
    int userBiddStatus = 1;
    Disposable disposable;
    PropertyBiddListAdapter propertyBiddListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bidd_list_layout);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPostUserId = bundle.getString("user_id");
            mCommonId = bundle.getString("p_id");
            mScreenName = bundle.getString("p_type");
            userBiddStatus = bundle.getInt("isBidd", 0);
        }

        findViewById(R.id.ivBackBiddList).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_OK);
                finish();
            }
        });

        if (mPostUserId.equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
            findViewById(R.id.relativePlaceABidd).setVisibility(View.GONE);
        } else {
            if (userBiddStatus == 0) {
                findViewById(R.id.relativePlaceABidd).setVisibility(View.VISIBLE);
                findViewById(R.id.textViewOfPlaceYourBidd).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //open place bidd dialog
                        openDilaogView();
                    }
                });
            } else {
                findViewById(R.id.relativePlaceABidd).setVisibility(View.GONE);
            }
        }

        requestForBiddLikApiCall();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_OK);
        finish();
    }

    String isNotify = "0";

    private void openDilaogView() {

        Dialog mDialog = new Dialog(PropertyBiddListActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dialog_place_bidd);

        ((CheckBox) mDialog.findViewById(R.id.checkboxNotifyDrop)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotify = "1";
                } else {
                    isNotify = "0";
                }
            }
        });

        mDialog.findViewById(R.id.txt_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CustomEditText) mDialog.findViewById(R.id.txt_message)).getText().toString().isEmpty()) {
                    showSnkbar(Constants.ENTER_PRICE);
                    return;
                }
                findViewById(R.id.relativePlaceABidd).setVisibility(View.GONE);
                String priceValue = ((CustomEditText) mDialog.findViewById(R.id.txt_message)).getText().toString();
                addBidd(priceValue, isNotify);

                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private void addBidd(String priceValue, String isNotify) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressBar(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyBiddListActivity.this).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyBiddListActivity.this).getUserDetail().getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), priceValue));
        linkedHashMap.put("is_notify", RequestBody.create(MediaType.parse("multipart/form-data"), isNotify));

        UserServices userService = ApiFactory.getInstance(PropertyBiddListActivity.this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.addBidd(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
                    requestForBiddLikApiCall();
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

    // Shows or hides the ProgressBar
    protected void showProgressBar(boolean show) {
        if (show) {
            WaitingDialog.show(this);
        } else {
            WaitingDialog.dismiss();
        }
    }

    private void requestForBiddLikApiCall() {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }

        showProgressBar(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyBiddListActivity.this).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyBiddListActivity.this).getUserDetail().getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "500"));
        linkedHashMap.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));

        UserServices userService = ApiFactory.getInstance(PropertyBiddListActivity.this).provideService(UserServices.class);
        Observable<BidListResponse> observable;
        observable = userService.biddList(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<BidListResponse>() {
            @Override
            public void onSuccess(BidListResponse response) {
                // LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
                    setViewBidd(response.getData());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void setViewBidd(List<CommonData.Bidds> mCommonDetailsData) {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewBiddList);
        if (mCommonDetailsData != null && mCommonDetailsData.size() != 0) {
            propertyBiddListAdapter = new PropertyBiddListAdapter(PropertyBiddListActivity.this, mCommonDetailsData, false, mPostUserId);
            recyclerView.setLayoutManager(new LinearLayoutManager(PropertyBiddListActivity.this));
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(propertyBiddListAdapter);
            propertyBiddListAdapter.setItemOnClickEvent(this::onClickEvent);
        }
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }

    @Override
    public void onClickEvent(int position, CommonData.Bidds mJsonObject, String mActionType, String priceValue) {
        switch (mActionType) {
            case Constants
                    .ACCEPT:
                callUpdateStatus("1", mJsonObject.getBiddId(), priceValue);
                break;
            case Constants
                    .REJECT:
                callUpdateStatus("2", mJsonObject.getBiddId(), priceValue);
                break;
            case Constants
                    .COUNTERED:
                callUpdateStatus("3", mJsonObject.getBiddId(), priceValue);
                break;
        }
    }

    private void callUpdateStatus(String status, String biddID, String price) {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("bidd_id", RequestBody.create(MediaType.parse("multipart/form-data"), biddID));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), status));
        if (status.equalsIgnoreCase(Constants.COUNTERED)) {
            linkedHashMap.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), price));
        }

        UserServices userService = ApiFactory.getInstance(PropertyBiddListActivity.this).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.updateBiddStatus(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {

                } else {
                    showSnkbar(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }
}