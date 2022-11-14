package com.flippbidd.activity.Property;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Fragments.PropertyList;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.Response.TypeList.CommonTypeResponse;
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
import com.flippbidd.interfaces.Consts;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.core.text.HtmlCompat;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class QuickAddProperty extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(QuickAddProperty.class);
    @BindView(R.id.image_toolbar)
    ImageView image_toolbar;
    @BindView(R.id.imageHeadreIcon)
    ImageView imageHeadreIcon;
    @BindView(R.id.txt_title_toolbar)
    CustomTextView txt_title_toolbar;
    @BindView(R.id.editTextViewAddQuickHouse)
    CustomEditText editTextViewAddQuickHouse;
    @BindView(R.id.editTextViewAddQuickAddress)
    CustomEditText editTextViewAddQuickAddress;
    @BindView(R.id.editTextViewAddQuickPrice)
    CustomEditText editTextViewAddQuickPrice;
    @BindView(R.id.checkBoxTermsAndCondistion)
    CheckBox checkBoxTermsAndCondistion;
    @BindView(R.id.btnQuickSaveProperty)
    CustomAppCompatButton btnQuickSaveProperty;
    @BindView(R.id.btnQuickAddOtherProperty)
    CustomAppCompatButton btnQuickAddOtherProperty;
    @BindView(R.id.textViewSelectStateTitle)
    CustomTextView textViewSelectStateTitle;
    @BindView(R.id.spinnerSelectState)
    Spinner spinnerSelectState;
    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.spinnerSelectCity)
    Spinner spinnerSelectCity;

    @BindView(R.id.relativeQuickAddSelectCity)
    RelativeLayout relativeQuickAddSelectCity;
    @BindView(R.id.relativeQuickAddSelectState)
    RelativeLayout relativeQuickAddSelectState;
    @BindView(R.id.textViewTermsAndCondistion)
    CustomTextView textViewTermsAndCondistion;
    @BindView(R.id.editTextViewNRDS)
    CustomEditText editTextViewNRDS;
    @BindView(R.id.linearLayoutOfNRDS)
    LinearLayout linearLayoutOfNRDS;

    @BindView(R.id.groupRadioListed)
    RadioGroup groupRadioListed;

    Disposable disposable;
    private String NDRS_value, state_id, city_id, mPrice, maddress, mhouse_no, mTremsAndCondistionIsSelected, btnClickView, mis_listed, mLocationLat, mLocationLang;
    Validator mValidator;

    private RadioButton radioListedButton;

    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();
    private static final int PLACE_PICKER_REQUEST = 1000;
    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";

    public static Intent getIntentActivity(Context mContext) {
        Intent mIntent = new Intent(mContext, QuickAddProperty.class);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getApplicationContext(), API_KEY);

        txt_title_toolbar.setVisibility(View.VISIBLE);
        txt_title_toolbar.setText("Quick Add Property");
        //id of image drawer in toolbar
        image_toolbar.setVisibility(View.VISIBLE);
        image_toolbar.setImageResource(R.drawable.back_icon_new);

        textViewTermsAndCondistion.setText(HtmlCompat.fromHtml(getResources().getString(R.string.string_trms), HtmlCompat.FROM_HTML_MODE_LEGACY));
        callStateApi(true);

        //radio listed selection action
        groupRadioListed.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioListedYes) {
                    //yes
                    linearLayoutOfNRDS.setVisibility(View.VISIBLE);
                } else {
                    //no
                    linearLayoutOfNRDS.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_quick_add_property;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnClick({R.id.tvSearchGoogle, R.id.imageViewSelectAddress})
    void viewAddressClick() {
        hideKeyboard();

        List<Place.Field> fields = Arrays.asList(Place.Field.ID
                ,Place.Field.ADDRESS,Place.Field.LAT_LNG,Place.Field.LAT_LNG);


        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

       /* PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityIfNeeded(builder.build(mBaseAppCompatActivity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
    }


    @OnTouch({R.id.relativeQuickAddSelectCity, R.id.relativeQuickAddSelectState, R.id.textViewTermsAndCondistion})
    public boolean onTouchSpinner() {
        hideKeyboard();
        return false;
    }


    @OnClick({R.id.relativeQuickAddSelectCity, R.id.relativeQuickAddSelectState, R.id.image_toolbar, R.id.btnQuickAddOtherProperty, R.id.btnQuickSaveProperty
            , R.id.textViewTermsAndCondistion})
    void onClickView(View view) {

        switch (view.getId()) {
            case R.id.image_toolbar:
                finish(true);
                break;
            case R.id.btnQuickAddOtherProperty:
                hideKeyboard();
                btnClickView = "addMore";
                callData();
                break;
            case R.id.btnQuickSaveProperty:
                hideKeyboard();
                btnClickView = "save";
                callData();
                break;
            case R.id.relativeQuickAddSelectState:
                if (mStateList != null && mStateList.size() != 0)
                    showDialogStateView();
                break;
            case R.id.relativeQuickAddSelectCity:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.textViewTermsAndCondistion:
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.CONDITION_, Constants.SELECTION_HEADER_TITLE.TERMS_AND_CONDITION), true);
                break;
        }

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
                //Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
                state_id = searchableAdapter.getItem(position).getCommonId();
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
                //Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectCityTitle.setText(searchableAdapter.getItem(position).getCommonName());
                city_id = searchableAdapter.getItem(position).getCommonId();
                dialog.dismiss();
            }
        });

        //in your Activity or Fragment where of Adapter is instantiated :

        mCustomEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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

    private void callData() {
        maddress = editTextViewAddQuickAddress.getText().toString();
        mhouse_no = editTextViewAddQuickHouse.getText().toString();
        mPrice = editTextViewAddQuickPrice.getText().toString();

        if (checkBoxTermsAndCondistion.isChecked()) {
            mTremsAndCondistionIsSelected = "isChecked";
        } else {
            mTremsAndCondistionIsSelected = "";
        }

        //listed
        int isSelectedListed = groupRadioListed.getCheckedRadioButtonId();
        radioListedButton = groupRadioListed.findViewById(isSelectedListed);
        if (radioListedButton != null) {
            if (radioListedButton.getText().toString().equalsIgnoreCase("Yes")) {
                mis_listed = "1";
            } else {
                mis_listed = "0";
            }
        }
        NDRS_value = editTextViewNRDS.getText().toString();
        mValidator = Validator.getInstance();

       /* QBChatService chatService = QBChatService.getInstance();
        boolean isLoggedIn = chatService.isLoggedIn();
        if (isLoggedIn) {
            ChatHelper.getInstance().logout(mBaseAppCompatActivity);
        }
*/
        if (!validation()) {

            if (mis_listed.equalsIgnoreCase("1")) {
                if (NDRS_value.equalsIgnoreCase("")) {
                    seekBarShow("please enter NDRS number", Color.RED);
                    return;
                } else if (NDRS_value.length() != 9) {
                    seekBarShow("please valid NDRS number", Color.RED);
                    return;
                }
            } else {
                NDRS_value = "";
            }

            if (mLocationLat != null && !mLocationLat.equalsIgnoreCase("") ||
                    mLocationLang != null && !mLocationLang.equalsIgnoreCase("")) {
                callAddQuickPostApi();
            } else {
                seekBarShow("please select address from google map", Color.RED);
                return;
            }
        }

    }

    private boolean validation() {

        if (mValidator.isEmpty(maddress)) {
            seekBarShow("Enter Address.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mPrice)) {
            seekBarShow("Enter House Price.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mTremsAndCondistionIsSelected)) {
            seekBarShow(Constants.val_tremsandcondistion, Color.RED);
            return true;
        }

        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeCall();
        finish();
    }


    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);

    }

    public void callAddQuickPostApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        addPostRequest.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), mPrice));
        addPostRequest.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), maddress));
        addPostRequest.put("house_no", RequestBody.create(MediaType.parse("multipart/form-data"), mhouse_no));
        addPostRequest.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), state_id));
        addPostRequest.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), city_id));
        addPostRequest.put("is_listed", RequestBody.create(MediaType.parse("multipart/form-data"), mis_listed));
        addPostRequest.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLat));
        addPostRequest.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLang));
        if (NDRS_value != null && !NDRS_value.equalsIgnoreCase("")) {
            addPostRequest.put("ndrs_number", RequestBody.create(MediaType.parse("multipart/form-data"), NDRS_value));
        } else {
            addPostRequest.put("ndrs_number", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable = userService.addQuickProperty(addPostRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    if (btnClickView.equalsIgnoreCase("addMore")) {
                        PropertyList.isClickBack = false;
                        clearData();
                        openSuccessDialog("addMore");
                    } else {
                        PropertyList.isClickBack = false;
                        openSuccessDialog("save");
                    }
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

/*
    //chat login with Qb
    private void singToQB(final int id) {
        QBUser qbUser = new QBUser(getAppendString(String.valueOf(id)), Consts.userPassword);
        qbUser.setFullName(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName());
        qbUser.setCustomData(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getProfilePic());

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                //SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
                if (qbUser.getId() != null && !qbUser.getId().equals("")) {
                    LogUtils.LOGD(TAG, "onSuccess() called with: response QbID= [" + qbUser.getId() + "]");
                    UserPreference.getInstance(mBaseAppCompatActivity).setUserQbId(qbUser.getId());
                    callUpdateQBID(qbUser.getId(), id);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                LogUtils.LOGD(TAG, "onError() called with: response QbID= [" + e.getErrors() + "]");
            }
        });

    }

    private String getAppendString(String Id) {

        String mString = Id + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_property";
        return mString;
    }


    private void callUpdateQBID(Integer qbId, int commonId) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        LinkedHashMap<String, RequestBody> addQBID = new LinkedHashMap<String, RequestBody>();
        addQBID.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        addQBID.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(commonId)));
        addQBID.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));
        addQBID.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(qbId)));

        //token,common_id,type,sender_qb_id
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateSenderQbId(addQBID);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    if (btnClickView.equalsIgnoreCase("addMore")) {
                        PropertyList.isClickBack = false;
                        clearData();
                        openSuccessDialog("addMore");
                    } else {
                        PropertyList.isClickBack = false;
                        openSuccessDialog("save");
                    }
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
*/

    //Success Dialog
    private void openSuccessDialog(final String mAction) {

        PromptDialog mPromptDialog = new PromptDialog(this);
        mPromptDialog.setSingleButton(false);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));

        if (mAction.equalsIgnoreCase("save")) {
            mPromptDialog.setContentText(getString(R.string.string_success_property));
        } else {
            mPromptDialog.setContentText(getString(R.string.string_quick_success_property));
        }

        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                if (mAction.equalsIgnoreCase("save")) {
                    //save
                    finish(true);
                }
            }
        });
        mPromptDialog.setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                finish(true);
            }
        });
        mPromptDialog.show();

    }


    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    private void clearData() {
        editTextViewAddQuickAddress.setText("");
        editTextViewAddQuickHouse.setText("");
        editTextViewAddQuickPrice.setText("");
        editTextViewNRDS.setText("");
        checkBoxTermsAndCondistion.setChecked(false);
    }

    //Get State
    private void callStateApi(boolean isProgress) {
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
                        textViewSelectStateTitle.setText(response.getData().get(0).getCommonName());
                        state_id = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectStateTitle.setText("");
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
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_CITY));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), state_id));

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
                    textViewSelectCityTitle.setText(response.getData().get(0).getCommonName());
                    city_id = response.getData().get(0).getCommonId();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        //get lat and long
                        mLocationLat = String.valueOf(place.getLatLng().latitude);
                        mLocationLang = String.valueOf(place.getLatLng().longitude);
                        //set data
                        editTextViewAddQuickAddress.setText("");
                        editTextViewAddQuickAddress.setText(place.getAddress());
                        editTextViewAddQuickAddress.setEnabled(true);

                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        // TODO: Handle the error.
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i(TAG, status.getStatusMessage());
                    } else if (resultCode == RESULT_CANCELED) {
                        // The user canceled the operation.
                    }
                } else {
                    showToast("something want to wrong,please re-select address!");
                }
                break;
        }
    }

}

