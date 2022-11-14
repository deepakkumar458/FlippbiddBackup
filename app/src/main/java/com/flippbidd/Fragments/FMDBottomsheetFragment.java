package com.flippbidd.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.DealDetailsResponse;
import com.flippbidd.Model.Response.OtherUserList;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.UserListActivity;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FMDBottomsheetFragment extends BaseBottomSheetDialogFragment {
    @BindView(R.id.tv_address)
    TextView tv_address;

    @BindView(R.id.ll_users)
    LinearLayoutCompat ll_users;

    @BindView(R.id.tv_upload_property)
    TextView tv_upload_property;

    @BindView(R.id.tv_add_me)
    TextView tv_add_me;

    @BindView(R.id.tv_cancel)
    TextView tv_cancel;

    View view;
    String location, lat, lng, deal_id;
    String passed_address= "";
    Disposable disposable;
    ArrayList<OtherUserList> otherUserLists = new ArrayList<>();

    public static FMDBottomsheetFragment newInstance(String param1, String param2) {
        return new FMDBottomsheetFragment();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;

        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        location = getArguments().getString(Constants.EXTRA.LOCATION);
        lat = getArguments().getString(Constants.EXTRA.LAT);
        lng = getArguments().getString(Constants.EXTRA.LANG);
        deal_id = getArguments().getString(Constants.EXTRA.DEAL_ID);
        init();
        tv_address.setText(location);

        callDealDetailsApi();

    }

    private void callDealDetailsApi() {
        if (!NetworkUtils.isInternetAvailable(getContext())) {
            return;
        }

        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("deal_id", RequestBody.create(MediaType.parse("multipart/form-data"), deal_id));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(view.getContext()).provideService(UserServices.class);
        Observable<DealDetailsResponse> observable;
        observable = userService.dealDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<DealDetailsResponse>() {
            @Override
            public void onSuccess(DealDetailsResponse response) {
                //ArrayList<OtherUserDetails> usersList;
                if (response.getSuccess()) {
                    for (int i = 0; i < response.getData().getOtherUserList().size(); i++) {
                        otherUserLists.add(response.getData().getOtherUserList().get(i));
                           passed_address =response.getData().getAddress();
                    }
                    usersList();
                } else {
                    showToast(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }

    private void init() {
        tv_address = view.findViewById(R.id.tv_address);
        ll_users = view.findViewById(R.id.ll_users);
        tv_upload_property = view.findViewById(R.id.tv_upload_property);
        tv_add_me = view.findViewById(R.id.tv_add_me);
        tv_cancel = view.findViewById(R.id.tv_cancel);
    }

    private void usersList() {
        LinearLayoutCompat ll_users = view.findViewById(R.id.ll_users);
        if (otherUserLists != null && otherUserLists.size() != 0) {
            ll_users.removeAllViews();
            LayoutInflater loLayoutInflator = (LayoutInflater) view.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int fe = 0; fe < 5; fe++) {
                View loView = loLayoutInflator.inflate(R.layout.user_images_list, null);
                ll_users.addView(loView);

                CircleImageView img_user_profile = loView.findViewById(R.id.img_user_profile);
                Glide.with(view.getContext())
                        .load(otherUserLists.get(fe).getProfilePic())
                        .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                        .into(img_user_profile);
                ll_users.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDialog().dismiss();
                        System.out.println("passed -> address"+passed_address);
                        startActivity(new Intent(requireActivity(), UserListActivity.class).putExtra("OtherUserDetails", otherUserLists).putExtra("isFromFMDPopUp", true).putExtra("pass_address",passed_address));
                    }
                });
            }

            ll_users.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("passed -> address"+passed_address);
                    getDialog().dismiss();
                    startActivity(new Intent(requireActivity(), UserListActivity.class).putExtra("OtherUserDetails", otherUserLists).putExtra("isFromFMDPopUp", true).putExtra("pass_address",passed_address));
                }
            });
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.fragment_f_m_d_bottomsheet;
    }

    @OnClick({R.id.ll_users, R.id.tv_upload_property, R.id.tv_add_me, R.id.tv_cancel})
    void viewOnClickEvent(View view) {
        switch (view.getId()) {
            case R.id.ll_users:
                break;
            case R.id.tv_upload_property:
                getDialog().dismiss();

                view.getContext().startActivity(new Intent(view.getContext(), PostNewProperty.class)
                        .putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.ADD)
                        // .putExtra("is_similar_deal", "")
                        //.putExtra("deal_id", "0")
                        .putExtra("lat", lat)
                        .putExtra("lng", lng)
                        .putExtra("deal_address", location));
                //.putExtra("deal_lat_lang", 0))
                break;
            case R.id.tv_add_me:
                getDialog().dismiss();
                openAlertDialog();
                break;
            case R.id.tv_cancel:
                getDialog().dismiss();
                break;
        }
    }

    private void openAlertDialog() {

        Dialog mDialog = new Dialog(view.getContext());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.common_dialog_ui);

        ((CustomTextView) mDialog.findViewById(R.id.txt_message_header)).setText(view.getContext().getResources().getString(R.string.app_name));
        ((CustomTextView) mDialog.findViewById(R.id.txt_message)).setText(view.getContext().getResources().getString(R.string.message_of_find_my_deal_upload));

        mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                callMyDeal(location, "1");
            }
        });
        mDialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
            }
        });
        mDialog.findViewById(R.id.txt_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                callMyDeal(location, "0");
            }
        });

        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

    }

    private void callMyDeal(String tvAddress, String isNotify) {

        //showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), tvAddress));
        linkedHashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), lat));
        linkedHashMap.put("long", RequestBody.create(MediaType.parse("multipart/form-data"), lng));
        linkedHashMap.put("is_notify", RequestBody.create(MediaType.parse("multipart/form-data"), isNotify));

        UserServices userService = ApiFactory.getInstance(view.getContext()).provideService(UserServices.class);
        Observable<JsonElement> observable;
        observable = userService.findMyDeal(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                //hideProgressDialog();
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (((JsonObject) response).get("success").getAsBoolean()) {
                    //get channel url
                    // Re-query message list
                    shoSuccessDialog(((JsonObject) response).get("count").getAsInt());
//                    showToast(((JsonObject) response).get("text").getAsString());
                } else {
                    if (((JsonObject) response).get("text").getAsString().equalsIgnoreCase("You have hit the maximum amount of Find My Deal Requests. Please update to the Pro Version")) {
                        //open
                        CommonDialogView.getInstance().showCommonDialog(requireActivity(), "",
                                ((JsonObject) response).get("text").getAsString()
                                , "Cancel"
                                , "Upgrade"
                                , false, true, false, false, false, new CommonDialogCallBack() {
                                    @Override
                                    public void onDialogYes(View view) {
                                        //open upgrade plan activity
                                        Intent mIntent = new Intent(requireActivity(), InAppPurchaseActivity.class);
                                        startActivity(mIntent);
                                    }

                                    @Override
                                    public void onDialogCancel(View view) {
                                    }
                                });
                    } else {
                        //showToast(((JsonObject) response).get("text").getAsString());
                        Toast.makeText(view.getContext(), ((JsonObject) response).get("text").getAsString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

                //hideProgressDialog();
            }
        });
    }

    private void shoSuccessDialog(int count) {
        //String message = getActivity().getResources().getString(R.string.message_of_find_my_deal_upload_sucessfull, count);
        String message = view.getContext().getResources().getString(R.string.message_of_find_my_deal_upload_sucessfull, count);
        //String message = "Your search request was submitted %d members of the Flippbidd Network.";
        PromptDialog mPromptDialog = new PromptDialog(view.getContext());
        mPromptDialog.setSingleButton(true);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(view.getContext().getResources().getString(R.string.string_success));
        mPromptDialog.setContentText(message);
        mPromptDialog.setPositiveListener(view.getContext().getResources().getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();

                /*Fragment fragment = getFragmentManager().findFragmentByTag("0");
                if (fragment instanceof NewHomeFragments) {
                    ((NewHomeFragments) fragment).refresh(false);
                }
                //((BaseAppCompatActivity) mBaseAppCompatActivity).onBackPressed();
                dialog.dismiss();*/
            }
        });

        mPromptDialog.show();
        //getMyDealList();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

}