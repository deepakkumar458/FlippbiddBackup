package com.flippbidd.activity.inapppurchase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Model.Response.planlist.PlanDetails;
import com.flippbidd.Model.Response.planlist.PlanListResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PdfViewer;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.views.ShowMoreTextView;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.LinearLayoutCompat;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class InAppPurchaseActivity extends BaseActivity implements PurchasesUpdatedListener {

    private AppCompatActivity activity;

    private CustomTextView textUserNameWithTagMessage, textPriceWithPlanName, textTermsOfService, textTermsOfPolicy;
    private ShowMoreTextView textDescriptionMessage;
    private ImageView imageInAppBack;
    private AppCompatButton btnRestore;
    Disposable disposable;
    UserDetails mUserResponse;
    private BillingClient billingClient;
    private List<SkuDetails> productList = new ArrayList<>();
    private List<PlanDetails> planBenifits = new ArrayList<>();
    private boolean isApiCall = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_app_purchase_layout);
        this.activity = InAppPurchaseActivity.this;

        //init instance
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this::onPurchasesUpdated)
                .enablePendingPurchases()
                .build();
        //bind id
        initId();

    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    private void initId() {
        textTermsOfPolicy = findViewById(R.id.textTermsOfPolicy);
        textTermsOfService = findViewById(R.id.textTermsOfService);
        textUserNameWithTagMessage = findViewById(R.id.textUserNameWithTagMessage);
        textPriceWithPlanName = findViewById(R.id.textPriceWithPlanName);
        textDescriptionMessage = findViewById(R.id.textDescriptionMessage);
        imageInAppBack = findViewById(R.id.imageInAppBack);
        btnRestore = findViewById(R.id.btnRestore);

        imageInAppBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //set user with name
        setUserName();
    }

    private void setUserName() {
        mUserResponse = UserPreference.getInstance(activity).getUserDetail();
        //set name
        textUserNameWithTagMessage.setText(activity.getString(R.string.welcome_user_pro_purchase_header, mUserResponse.getFullName()));

        //call api for plan details
        getPlanDetailsList();

        listener();
    }

    private void listener() {
        textTermsOfPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PdfViewer.getIntentActivity(activity, Constants.POLICY_, ""));
            }
        });
        textTermsOfService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(PdfViewer.getIntentActivity(activity, Constants.CONDITION_, ""));
            }
        });
        btnRestore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initBillingClient();
            }
        });

    }

    private void getPlanDetailsList() {
        if (!NetworkUtils.isInternetAvailable(activity)) {
            openAlertDialog(activity.getResources().getString(R.string.no_internet));
            return;
        }
        showProgressBar(true);
        LinkedHashMap<String, RequestBody> feedbackRequest = new LinkedHashMap<String, RequestBody>();
        feedbackRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));

        UserServices userService = ApiFactory.getInstance(activity).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.inappPlanList(feedbackRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                showProgressBar(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                LogUtils.LOGD("TAG", "Response = [" + mJsonObject.toString() + "]");
                if (mJsonObject.get("success").getAsBoolean()) {
                    Gson gson = new Gson();
                    PlanListResponse mPlanListResponse = gson.fromJson(mJsonObject.toString(), PlanListResponse.class);
                    if (mPlanListResponse.getData().size() != 0) {
                        featuresList(mPlanListResponse.getData());
                    }
                } else {
                    //error message
                    openAlertDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void featuresList(List<PlanDetails> features) {
        if (planBenifits != null) {
            planBenifits.clear();
        }
        planBenifits.addAll(features);
        LinearLayoutCompat llFeaturesView = findViewById(R.id.llFeaturesView);
        llFeaturesView.removeAllViews();
        LayoutInflater loLayoutInflator = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        for (int fe = 0; fe < features.get(0).getPoints().size(); fe++) {
            View loView = loLayoutInflator.inflate(R.layout.item_feature_ui, null);
            llFeaturesView.addView(loView);

            ((CustomTextView) loView.findViewById(R.id.txt_features_value)).setText(features.get(0).getPoints().get(fe));
        }

        textPriceWithPlanName.setText(activity.getResources().getString(R.string.plan_title, features.get(0).getTitle(), "$", features.get(0).getAmount(), features.get(0).getPlanType()));
        textDescriptionMessage.addShowMoreText("Show more+");
        textDescriptionMessage.addShowLessText("Less");
        textDescriptionMessage.setShowingLine(2);
        textDescriptionMessage.setShowMoreColor(getColor(R.color.text_color_dark_grey_));
        textDescriptionMessage.setShowLessTextColor(getColor(R.color.text_color_dark_grey_));
        textDescriptionMessage.setText(features.get(0).getDescriptions());

        //purchase init
        initBillingClient();
    }

    private void openAlertDialog(String s) {
        CommonDialogView.getInstance().showCommonDialog(activity, "",
                s
                , "Ok"
                , "Ok"
                , false, true, true, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        finish();
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }

    private void openSuccessAfterPurchase(String s) {
        CommonDialogView.getInstance().showCommonDialog(activity, "",
                s
                , "Ok"
                , "Ok"
                , false, true, true, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        startActivity(new Intent(InAppPurchaseActivity.this, MainActivity.class));
                        finishAffinity();
                    }

                    @Override
                    public void onDialogCancel(View view) {
                        startActivity(new Intent(InAppPurchaseActivity.this, MainActivity.class));
                        finishAffinity();
                    }
                });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initBillingClient() {
        //connection start
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    loadALLSKU();
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                initBillingClient();
            }
        });
    }

    @Override
    public void onPurchasesUpdated(@NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                && purchases != null) {
            for (Purchase purchase : purchases) {
                handlePurchase(purchase);
            }
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            if (billingResult.getDebugMessage() != null && !billingResult.getDebugMessage().equalsIgnoreCase("")) {
                showErrorDialog(billingResult.getDebugMessage());
            }
        } else {
            // Handle any other error codes.
        }
    }


    void handlePurchase(Purchase purchase) {
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            PreferenceUtils.setPlanVersionStatus(true);
            // Purchase retrieved from BillingClient#queryPurchasesAsync or your PurchasesUpdatedListener.
            Purchase _purchase = purchase;

            // Verify the purchase.
            // Ensure entitlement was not already granted for this purchaseToken.
            // Grant entitlement to the user.
            ConsumeParams consumeParams =
                    ConsumeParams.newBuilder()
                            .setPurchaseToken(purchase.getPurchaseToken())
                            .build();

            ConsumeResponseListener listener = new ConsumeResponseListener() {
                @Override
                public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
//                        callAddPurchase(purchase.getOrderId(), _purchase.getOriginalJson());
                    }
                }
            };
            billingClient.consumeAsync(consumeParams, listener);
            if (!isApiCall) {
                isApiCall = true;
                callAddPurchase(purchase.getOrderId(), purchase.getOriginalJson());
                Log.e("TAG", "Purchase Response" + _purchase.getOriginalJson());
            }
        }
    }

    private void callAddPurchase(String orderId, String originalJson) {
        if (!NetworkUtils.isInternetAvailable(activity)) {
            openAlertDialog(activity.getResources().getString(R.string.no_internet));
            return;
        }
//        showProgressBar(true);
        LinkedHashMap<String, RequestBody> feedbackRequest = new LinkedHashMap<String, RequestBody>();
        feedbackRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        feedbackRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        feedbackRequest.put("inapp_plan_id", RequestBody.create(MediaType.parse("multipart/form-data"), planBenifits.get(0).getInappPlanId()));
        feedbackRequest.put("txn_id", RequestBody.create(MediaType.parse("multipart/form-data"), orderId));
        feedbackRequest.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        feedbackRequest.put("tra_response", RequestBody.create(MediaType.parse("multipart/form-data"), originalJson));

        //token, login_id, inapp_plan_id, txn_id, device_type, tra_response
        UserServices userService = ApiFactory.getInstance(activity).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.inappStatusUpdate(feedbackRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
//                showProgressBar(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                LogUtils.LOGD("TAG", "Response = [" + mJsonObject.toString() + "]");
                if (mJsonObject.get("success").getAsBoolean()) {
                    isApiCall = true;
                    //success payment
                    openSuccessAfterPurchase(mJsonObject.get("text").getAsString());
                } else {
                    isApiCall = false;
                    //error message
                    openAlertDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                isApiCall = false;
                showProgressBar(false);
            }
        });
    }

    private void showErrorDialog(String errorMessage) {
        new AlertDialog.Builder(activity).setCancelable(false).setTitle("Error").setMessage(errorMessage).setPositiveButton("OKay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                /*//finish activity
                Intent mIntent = new Intent(InAppPurchaseActivity.this, MainActivity.class);
                startActivity(mIntent, ActivityOptions.makeSceneTransitionAnimation(InAppPurchaseActivity.this).toBundle());
                finish();*/
            }
        }).show();
    }

    //subscription
    void loadALLSKU() {
        /**
         * To purchase an Subscription
         */
        final List<String> skuList = new ArrayList<>();
        skuList.add("flippbidd_pro");//

        SkuDetailsParams params = SkuDetailsParams.newBuilder()
                .setSkusList(skuList)
                .setType(BillingClient.SkuType.SUBS)
                .build();
        billingClient.querySkuDetailsAsync(params,
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (skuDetailsList.size() != 0) {
                                if (productList != null) {
                                    productList.clear();
                                }
                                productList.addAll(skuDetailsList);
                                //open purchase view direct
                                billingClient.launchBillingFlow(activity, BillingFlowParams.newBuilder().setSkuDetails(skuDetailsList.get(0)).build());
                            }
                        }
                    }
                });
    }
}
