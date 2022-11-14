package com.flippbidd.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.flippbidd.Adapter.OtherFMDAdapterList;
import com.flippbidd.Adapter.RecentlySearchAdapterList;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.DealData;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.UserListActivity;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.ToastUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FindMyDealFragment extends BaseFragment {

    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";
    private static final String TAG = LogUtils.makeLogTag(FindMyDealFragment.class);
    View moView;
    private static final int PLACE_PICKER_REQUEST = 1000;
    String mLocationLat, mLocationLang;
    RecyclerView rvRecentlySearchDeal, rvOtherDeal;
    RecentlySearchAdapterList loRecentlySearchAdapterList;
    List<DealData> dealDataList = new ArrayList<>();

    LinearLayout linearRecentlyFMD, linearOtherFMD;
    Disposable disposable;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_find_my_deal_layout;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Initialize Places.
        Places.initialize(context.getApplicationContext(), API_KEY);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moView = view;
        initView();
    }


    private void initView() {
        rvRecentlySearchDeal = moView.findViewById(R.id.rvRecentlySearchDeal);

        rvOtherDeal = moView.findViewById(R.id.rvOtherDeal);
        linearOtherFMD = moView.findViewById(R.id.linearOtherFMD);
        linearRecentlyFMD = moView.findViewById(R.id.linearRecentlyFMD);
        clickListner();
        getMyDealList();
    }

    private void clickListner() {

        moView.findViewById(R.id.tvFindMyDealAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
// Create a new Places client instance.
                    // Set the fields to specify which types of place data to return.
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    // Start the autocomplete intent.
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(getActivity());
                    getActivity().startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        moView.findViewById(R.id.btnFindView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tvAddress = ((CustomTextView) moView.findViewById(R.id.tvFindMyDealAddress)).getText().toString();

                if (tvAddress.isEmpty()) {
                    ToastUtils.shortToast("Please add address");
                    return;
                }
                openAlertDialog(tvAddress);

            }
        });
    }

    private void openAlertDialog(String tvAddress) {

        Dialog mDialog = new Dialog(getActivity());
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.common_dialog_ui);

        ((CustomTextView) mDialog.findViewById(R.id.txt_message_header)).setText(getActivity().getResources().getString(R.string.app_name));
        ((CustomTextView) mDialog.findViewById(R.id.txt_message)).setText(getActivity().getResources().getString(R.string.message_of_find_my_deal_upload));

        mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.dismiss();
                }
                callMyDeal(tvAddress, "1");
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
                callMyDeal(tvAddress, "0");
            }
        });

        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();

       /* CommonDialogView.getInstance().showCommonDialog(getActivity(), getActivity().getResources().getString(R.string.message_of_find_my_deal_upload),
                ""
                , getActivity().getResources().getString(R.string.string_no)
                , getActivity().getResources().getString(R.string.string_yes)
                , true, false, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        callMyDeal(tvAddress, "1");
                    }

                    @Override
                    public void onDialogCancel(View view) {
                        callMyDeal(tvAddress, "0");
                    }
                });*/
    }

    private void callMyDeal(String tvAddress, String isNotify) {

        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), tvAddress));
        linkedHashMap.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLat));
        linkedHashMap.put("long", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLang));
        linkedHashMap.put("is_notify", RequestBody.create(MediaType.parse("multipart/form-data"), isNotify));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<JsonElement> observable;
        observable = userService.findMyDeal(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                hideProgressDialog();
                ((CustomTextView) moView.findViewById(R.id.tvFindMyDealAddress)).setText("");
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
                        showToast(((JsonObject) response).get("text").getAsString());
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }


    private void shoSuccessDialog(int count) {
        String message = getActivity().getResources().getString(R.string.message_of_find_my_deal_upload_sucessfull, count);
        PromptDialog mPromptDialog = new PromptDialog(getActivity());
        mPromptDialog.setSingleButton(true);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));
        mPromptDialog.setContentText(message);
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                getMyDealList();
               /* Fragment fragment = getFragmentManager().findFragmentByTag("0");
                if (fragment instanceof NewHomeFragments) {
                    ((NewHomeFragments) fragment).refresh(false);
                }
                ((BaseAppCompatActivity) mBaseAppCompatActivity).onBackPressed();*/
            }
        });

        mPromptDialog.show();
        //getMyDealList();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (resultCode == Activity.RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                //get lat and long
                mLocationLat = String.valueOf(place.getLatLng().latitude);
                mLocationLang = String.valueOf(place.getLatLng().longitude);
                //set data
                ((CustomTextView) moView.findViewById(R.id.tvFindMyDealAddress)).setText("");
                ((CustomTextView) moView.findViewById(R.id.tvFindMyDealAddress)).setText(place.getAddress());

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i(TAG, status.getStatusMessage());
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else {
            showToast("something want to wrong,please re-select address!");
        }
    }

    private void getMyDealList() {

        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<RecentlySearchResponse> observable;
        observable = userService.getFindMyDeal(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<RecentlySearchResponse>() {
            @Override
            public void onSuccess(RecentlySearchResponse response) {
                hideProgressDialog();
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    setAdapter(response.getData());
                } else {
                    linearRecentlyFMD.setVisibility(View.GONE);
                    showToast(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });

        synchronized (requireActivity()) {
            getOtherFindDealList();
        }
    }

    private void getOtherFindDealList() {

        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(getActivity()).provideService(UserServices.class);
        Observable<RecentlySearchResponse> observable;
        observable = userService.getOtherFindMyDeal(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<RecentlySearchResponse>() {
            @Override
            public void onSuccess(RecentlySearchResponse response) {
                hideProgressDialog();
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    setOtherFMDAdapter(response.getData());
                } else {
                    linearOtherFMD.setVisibility(View.GONE);
                    showToast(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void setAdapter(List<DealData> data) {

        if (data != null && data.size() != 0) {
            linearRecentlyFMD.setVisibility(View.VISIBLE);
            RecentlySearchAdapterList loRecentlySearchAdapterList = new RecentlySearchAdapterList(getActivity(), data, false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            rvRecentlySearchDeal.setLayoutManager(linearLayoutManager);
            rvRecentlySearchDeal.setAdapter(loRecentlySearchAdapterList);
            loRecentlySearchAdapterList.setItemOnClickEvent(new RecentlySearchAdapterList.onItemClickLisnear() {
                @Override
                public void onClickEvent(int position, DealData mJsonObject, String mActionType) {
                    switch (mActionType) {
                        case "User List":
                            //open user list activity
                            startActivity(new Intent(requireActivity(), UserListActivity.class).putExtra("user_data", mJsonObject));
                            break;
                        default:
                            if (mJsonObject.getUploadedPropertyIsAvailable() == 1) {
                                ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, String.valueOf(mJsonObject.getUploadedPropertyId()), String.valueOf(mJsonObject.getUploadedLoginId()), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 666);
                            } else {
                                if (String.valueOf(mJsonObject.getUploadedLoginId()).equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                    ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, String.valueOf(mJsonObject.getUploadedPropertyId()), String.valueOf(mJsonObject.getUploadedLoginId()), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, true), 666);
                                } else {
                                    showNotAvailable();
                                }
                            }
                            break;
                    }
                }
            });
        } else {
            linearRecentlyFMD.setVisibility(View.GONE);
        }
    }

    private void setOtherFMDAdapter(List<DealData> data) {

        if (data != null && data.size() != 0) {
            linearOtherFMD.setVisibility(View.VISIBLE);

            OtherFMDAdapterList loOtherFMDAdapterList = new OtherFMDAdapterList(getActivity(), data);
            LinearLayoutManager linearManager = new LinearLayoutManager(getActivity());
            rvOtherDeal.setLayoutManager(linearManager);
            rvOtherDeal.setAdapter(loOtherFMDAdapterList);
            loOtherFMDAdapterList.setItemOnClickEvent(new OtherFMDAdapterList.onItemClickLisnear() {
                @Override
                public void onClickEvent(int position, DealData mJsonObject, String mActionType) {
                    switch (mActionType) {
                        case "Add Me":
                            mLocationLat = mJsonObject.getLat();
                            mLocationLang = mJsonObject.getLang();
                            openAlertDialog(mJsonObject.getAddress());
                            break;
                        case "User List":
                            //open user list activity
                            startActivity(new Intent(requireActivity(), UserListActivity.class).putExtra("user_data", mJsonObject));
                            break;
                        default:
                            if (mJsonObject.getUploadedPropertyIsAvailable() == 1) {
                                ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, String.valueOf(mJsonObject.getUploadedPropertyId()), String.valueOf(mJsonObject.getUploadedLoginId()), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 666);
                            } else {
                                if (String.valueOf(mJsonObject.getUploadedLoginId()).equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                    ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity, String.valueOf(mJsonObject.getUploadedPropertyId()), String.valueOf(mJsonObject.getUploadedLoginId()), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, true), 666);
                                } else {
                                    showNotAvailable();
                                }
                            }
                            break;
                    }
                }
            });
        } else {
            linearOtherFMD.setVisibility(View.GONE);
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(getActivity());
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

}
