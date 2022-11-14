package com.flippbidd.Fragments.TabFilter;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.flippbidd.Adapter.Spinner.CommonTypeListAdapter;
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.Model.Response.Filter.FileterData;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.Response.TypeList.CommonTypeResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseFragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class TabFilterRental extends BaseFragment implements RefreshListener {

    private static final String TAG = LogUtils.makeLogTag(TabFilterRental.class);
    @BindView(R.id.relativeRentalFilerSelectState)
    RelativeLayout relativeRentalFilerSelectState;
    @BindView(R.id.relativeRentalSelectCity)
    RelativeLayout relativeRentalSelectCity;
    @BindView(R.id.relativeRentalFilerSelectRentalType)
    RelativeLayout relativeRentalFilerSelectRentalType;

    @BindView(R.id.textViewSelectRentalFilerStateTitle)
    CustomTextView textViewSelectRentalFilerStateTitle;
    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.textViewSelectRentalFilerRentalTypeTitle)
    CustomTextView textViewSelectRentalFilerRentalTypeTitle;

    @BindView(R.id.editTextViewRentalFilerPriceMin)
    CustomEditText editTextViewRentalFilerPriceMin;
    @BindView(R.id.editTextViewRentalFilerPriceMax)
    CustomEditText editTextViewRentalFilerPriceMax;
    @BindView(R.id.editTextViewRentalFilerArea)
    CustomEditText editTextViewRentalFilerArea;
    @BindView(R.id.btnCreayeRentalFilterApply)
    CustomAppCompatButton btnCreayeRentalFilterApply;


    @BindView(R.id.spinnerSelectRentalFilerRentalType)
    Spinner spinnerSelectRentalFilerRentalType;

    @BindView(R.id.checkBoxallstatecityselection)
    CheckBox checkBoxallstatecityselection;
    @BindView(R.id.checkBoxallpropertyselection)
    CheckBox checkBoxallpropertyselection;
    @BindView(R.id.checkBoxallstateselection)
    CheckBox checkBoxallstateselection;

    @BindView(R.id.linearLayoutOfState)
    LinearLayout linearLayoutOfState;
    @BindView(R.id.linearLayoutOfCity)
    LinearLayout linearLayoutOfCity;


    Disposable disposable;
    CommonTypeListAdapter mSpinnerRentalBuildingTypeSelection;


    private String isAllstate = "0", isAllcity = "0", isAllproperty = "0", mStateId, mCityId, mBuildingTypeName, mBuildingId, mAreaSqt, mPriceMax, mPriceMin;

    List<CommonListData> mRentalBuildingTypeLists = new ArrayList<>();
    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();

    @Override
    protected int getLayoutResource() {
        return R.layout.tab_created_filter_rental_new_design_layout;
    }

    @Override
    public void refresh(boolean b) {
        openComingSonDialog();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCreayeRentalFilterApply.setVisibility(View.GONE);
       /* callStateApi(true);
        init();

        checkBoxallstatecityselection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAllcity = "1";
                } else {
                    isAllcity = "0";
                }
            }
        });

        checkBoxallstateselection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    1 for true,0 for false
                    isAllstate = "1";
                } else {
                    isAllstate = "0";
                }
            }
        });
        checkBoxallpropertyselection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    1 for true,0 for false
                    isAllproperty = "1";
                } else {
                    isAllproperty = "0";
                }
            }
        });*/


    }

    private void openComingSonDialog() {
        //coming soon
        PromptDialog mPromptDialog = new PromptDialog(getActivity());
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText("Flippbidd");
        mPromptDialog.setContentText("Coming soon");
        mPromptDialog.setPositiveListener(mBaseAppCompatActivity.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(mBaseAppCompatActivity.getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }

    private void init() {

        spinnerSelectRentalFilerRentalType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mBuildingTypeName = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();

                textViewSelectRentalFilerRentalTypeTitle.setText(mBuildingTypeName);
                if (mRentalBuildingTypeLists != null && mRentalBuildingTypeLists.size() != 0) {
                    mBuildingId = mRentalBuildingTypeLists.get(i).getCommonId();
                }

                LogUtils.LOGD(TAG, " response = [" + mBuildingTypeName.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


    }


    @OnClick({R.id.btnCreayeRentalFilterApply, R.id.relativeRentalFilerSelectState, R.id.relativeRentalSelectCity, R.id.relativeRentalFilerSelectRentalType})
    void viewClickEvent(View view) {
        switch (view.getId()) {
            case R.id.relativeRentalFilerSelectState:
                hideKeyboard();
                if (mStateList != null && mStateList.size() != 0) {
                    showDialogStateView();
                }
                break;
            case R.id.relativeRentalSelectCity:
                hideKeyboard();
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.relativeRentalFilerSelectRentalType:
                hideKeyboard();
                spinnerSelectRentalFilerRentalType.performClick();
                break;
            case R.id.btnCreayeRentalFilterApply:
                hideKeyboard();
                //listed
                mPriceMax = editTextViewRentalFilerPriceMax.getText().toString();
                mPriceMin = editTextViewRentalFilerPriceMin.getText().toString();
//                mAreaSqt = editTextViewRentalFilerArea.getText().toString();

                conformNotificationDialog();
                break;
        }
    }

    //conform notification filter dialog
    private void conformNotificationDialog() {

        new PromptDialog(mBaseAppCompatActivity)
                .setDialogType(PromptDialog.DIALOG_TYPE_INFO)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.string_notification_filter))
                .setContentText(getString(R.string.string_notification_message))
                .setPositiveListener(getString(R.string.string_conform), new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        callAddPropertyFilter(true);

                    }
                })
                .setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    private void showDialogStateView() {

        final Dialog dialog = new Dialog(mBaseAppCompatActivity);
        dialog.setTitle("Select State");
        dialog.setContentView(R.layout.searchable_list_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);

        CustomEditText mCustomEditText = dialog.findViewById(R.id.search);
        ListView mListView = dialog.findViewById(R.id.listItems);


        final SearchableAdapter searchableAdapter = new SearchableAdapter(mBaseAppCompatActivity, mStateList);
        mListView.setAdapter(searchableAdapter);
        dialog.show();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectRentalFilerStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
                mStateId = searchableAdapter.getItem(position).getCommonId();
                callCityApi(false);
                dialog.dismiss();
            }
        });

        //in your Activity or Fragment where of Adapter is instantiated :

        mCustomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                searchableAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }

    private void showDialogCityView() {

        final Dialog dialog = new Dialog(mBaseAppCompatActivity);
        dialog.setTitle("Select City");
        dialog.setContentView(R.layout.searchable_list_dialog);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams
                .SOFT_INPUT_STATE_HIDDEN);

        CustomEditText mCustomEditText = dialog.findViewById(R.id.search);
        ListView mListView = dialog.findViewById(R.id.listItems);


        final SearchableAdapter searchableAdapter = new SearchableAdapter(mBaseAppCompatActivity, mCityList);
        mListView.setAdapter(searchableAdapter);
        dialog.show();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectCityTitle.setText(searchableAdapter.getItem(position).getCommonName());
                mCityId = searchableAdapter.getItem(position).getCommonId();
                dialog.dismiss();
            }
        });

        //in your Activity or Fragment where of Adapter is instantiated :

        mCustomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("Text [" + s + "]");

                searchableAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


    }


    private void callAddPropertyFilter(boolean isProgress) {

        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //token,login_id,type,state_id,city_id,listed,min_price,max_price,sqft,building_type,service_type,licensed
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "rental"));

//        if (isAll.equalsIgnoreCase("1")) {
//            hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
//            hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
//        } else {
//            hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
//            hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
//        }

        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
        hashMap.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
        hashMap.put("all_state", RequestBody.create(MediaType.parse("multipart/form-data"), isAllstate));
        hashMap.put("all_city", RequestBody.create(MediaType.parse("multipart/form-data"), isAllcity));
        hashMap.put("all_property", RequestBody.create(MediaType.parse("multipart/form-data"), isAllproperty));
        hashMap.put("property_type", RequestBody.create(MediaType.parse("multipart/form-data"), ""));

        hashMap.put("listed", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("min_price", RequestBody.create(MediaType.parse("multipart/form-data"), mPriceMin));
        hashMap.put("max_price", RequestBody.create(MediaType.parse("multipart/form-data"), mPriceMax));
//        hashMap.put("sqft", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("building_type", RequestBody.create(MediaType.parse("multipart/form-data"), mBuildingId));
        hashMap.put("service_type", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        hashMap.put("licensed", RequestBody.create(MediaType.parse("multipart/form-data"), ""));

        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<FileterData> observable;

        observable = userService.addFilter(hashMap);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<FileterData>() {
            @Override
            public void onSuccess(FileterData response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    openSuccessDialog(response.getText());
                } else {
                    openErorrDialog(response.getText());
                }
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }
    //Success Dialog
    private void openSuccessDialog(String successMessages) {

        new PromptDialog(mBaseAppCompatActivity)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.string_success))
                .setContentText(successMessages)
                .setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, MainActivity.class), false);
                        mBaseAppCompatActivity.finishAffinity();
                    }
                })
                .setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        mBaseAppCompatActivity.startActivity(new Intent(mBaseAppCompatActivity, MainActivity.class), false);
                        mBaseAppCompatActivity.finishAffinity();
                    }
                }).show();

    }



    //Get State
    private void callStateApi(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
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
                        textViewSelectRentalFilerStateTitle.setText(response.getData().get(0).getCommonName());
                        mStateId = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectRentalFilerStateTitle.setText("");
                    openErorrDialog("Something want to wrong load state.");
                }
                callGetRentalType(isProgress);
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
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
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
                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());
                    textViewSelectCityTitle.setText(response.getData().get(0).getCommonName());
                    mCityId = response.getData().get(0).getCommonId();
                } else {
                    textViewSelectCityTitle.setText("");
                    openErorrDialog(getResources().getString(R.string.no_city_availabilityy));
                }


            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //get Building Type List
    private void callGetRentalType(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_BUILDING));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                hideProgressDialog();
                if (mRentalBuildingTypeLists != null)
                    mRentalBuildingTypeLists.clear();

                if (response.getSuccess()) {
                    mRentalBuildingTypeLists.addAll(response.getData());
                }
                mSpinnerRentalBuildingTypeSelection = new CommonTypeListAdapter(mBaseAppCompatActivity, R.layout.adapter_spinner_layout, response.getData());
                spinnerSelectRentalFilerRentalType.setAdapter(mSpinnerRentalBuildingTypeSelection);

                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

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
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
