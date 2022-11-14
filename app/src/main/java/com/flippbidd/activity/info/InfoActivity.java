package com.flippbidd.activity.info;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.IPortal.Data;
import com.flippbidd.Model.IPortal.Response;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class InfoActivity extends BaseActivity implements View.OnClickListener {

    private static final int PLACE_PICKER_REQUEST = 1000;
    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";
    Boolean isfromAddress1, isfromAddress2, isfromAddress3, isfromAddress4, isfromAddress5;
    Validator mValidator;
    String states;
    ArrayList<String> assetType = new ArrayList<>();
    Disposable disposable;
    String is_nationWide;
    List<String> stateList = new ArrayList<String>();
    String selectedState;
    String selectedAssetType;
    List<String> assetTypeList = new ArrayList<String>();

    Data data;

    String address1_type, address2_type, address3_type, address4_type, address5_type;
    String selectedROI;
    int selectedRoi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Places.initialize(getApplicationContext(), API_KEY);

        ((TextView) findViewById(R.id.tv_address1)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_address2)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_address3)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_address4)).setOnClickListener(this);
        ((TextView) findViewById(R.id.tv_address5)).setOnClickListener(this);
        ((Button) findViewById(R.id.btnSubmit)).setOnClickListener(this);
        ((ImageView) findViewById(R.id.image_toolbar)).setOnClickListener(this);

        states = ((EditText) findViewById(R.id.ed_states)).getText().toString().trim();
        if (states.contains(",")) {

        }


        if (((CheckBox) findViewById(R.id.chk_nationwide)).isChecked()) {
            is_nationWide = "1";
        } else {
            is_nationWide = "0";
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_toolbar:
                onBackPressed();
                break;

            case R.id.tv_address1:
                isfromAddress1 = true;
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(this);
                    startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_address2:
                isfromAddress2 = true;
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(this);
                    startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_address3:
                isfromAddress3 = true;
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(this);
                    startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_address4:
                isfromAddress4 = true;
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(this);
                    startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.tv_address5:
                isfromAddress5 = true;
                try {
                    List<Place.Field> fields = Arrays.asList(Place.Field.ID
                            , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY, fields)
                            .build(this);
                    startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case R.id.btnSubmit:
                mValidator = Validator.getInstance();
                selectedRoi = ((RadioGroup) findViewById(R.id.rg_roi)).getCheckedRadioButtonId();
                if (!validation()) {
                    //api call here
                    showProgressBar(true);
                    callAddIPortalApi();
                }
                break;
        }
    }

    private void callAddIPortalApi() {
        if (((EditText) findViewById(R.id.ed_states)).getText().toString().trim().contains(",")) {
            stateList.add(((EditText) findViewById(R.id.ed_states)).getText().toString().trim());
            selectedState = String.join(",", stateList);
        } else {
            stateList.add(((EditText) findViewById(R.id.ed_states)).getText().toString().trim());
            selectedState = stateList.toString();
        }


        if (((CheckBox) findViewById(R.id.chk_single_family)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_single_family)).getText().toString().trim());
        }
        if (((CheckBox) findViewById(R.id.chk_commercial)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_commercial)).getText().toString().trim());
        }
        if (((CheckBox) findViewById(R.id.chk_land)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_land)).getText().toString().trim());
        }
        if (((CheckBox) findViewById(R.id.chk_multi_family)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_multi_family)).getText().toString().trim());
        }
        if (((CheckBox) findViewById(R.id.chk_development)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_development)).getText().toString().trim());
        }
        if (((CheckBox) findViewById(R.id.chk_other)).isChecked()) {
            assetTypeList.add(((CheckBox) findViewById(R.id.chk_other)).getText().toString().trim());
        }

        selectedAssetType = String.join(",", assetTypeList);

        int selectedAddress1Id = ((RadioGroup) findViewById(R.id.rg_address1)).getCheckedRadioButtonId();
        address1_type = ((RadioButton) findViewById(selectedAddress1Id)).getText().toString().trim();

        int selectedAddress2Id = ((RadioGroup) findViewById(R.id.rg_address2)).getCheckedRadioButtonId();
        address2_type = ((RadioButton) findViewById(selectedAddress2Id)).getText().toString().trim();

        int selectedAddress3Id = ((RadioGroup) findViewById(R.id.rg_address3)).getCheckedRadioButtonId();
        address3_type = ((RadioButton) findViewById(selectedAddress3Id)).getText().toString().trim();

        int selectedAddress4Id = ((RadioGroup) findViewById(R.id.rg_address4)).getCheckedRadioButtonId();
        address4_type = ((RadioButton) findViewById(selectedAddress4Id)).getText().toString().trim();

        int selectedAddress5Id = ((RadioGroup) findViewById(R.id.rg_address5)).getCheckedRadioButtonId();
        address5_type = ((RadioButton) findViewById(selectedAddress5Id)).getText().toString().trim();


        String roi = ((RadioButton) findViewById(selectedRoi)).getText().toString().trim();

        selectedROI = String.join(",", roi);


        LinkedHashMap<String, RequestBody> addOPortalRequest = new LinkedHashMap<String, RequestBody>();
        addOPortalRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getToken()));
        addOPortalRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getLoginId()));
        addOPortalRequest.put("referredBy", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.editTextRefferedby)).getText().toString().trim()));
        addOPortalRequest.put("investorName", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.edinvestorname)).getText().toString().trim()));
        addOPortalRequest.put("companyName", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.ed_companyName)).getText().toString().trim()));
        addOPortalRequest.put("capitalOnHand", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.edcapital)).getText().toString().trim()));
        addOPortalRequest.put("attorneyName", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.ed_attorneyname)).getText().toString().trim()));
        addOPortalRequest.put("areaofIntrest", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.ed_interestarea)).getText().toString().trim()));
        addOPortalRequest.put("is_nationwide", RequestBody.create(MediaType.parse("multipart/form-data"), is_nationWide));
        addOPortalRequest.put("address1", RequestBody.create(MediaType.parse("multipart/form-data"), ((TextView) findViewById(R.id.tv_address1)).getText().toString().trim()));
        addOPortalRequest.put("address1_type", RequestBody.create(MediaType.parse("multipart/form-data"), address1_type));
        addOPortalRequest.put("address2", RequestBody.create(MediaType.parse("multipart/form-data"), ((TextView) findViewById(R.id.tv_address2)).getText().toString().trim()));
        addOPortalRequest.put("address2_type", RequestBody.create(MediaType.parse("multipart/form-data"), address2_type));
        addOPortalRequest.put("address3", RequestBody.create(MediaType.parse("multipart/form-data"), ((TextView) findViewById(R.id.tv_address3)).getText().toString().trim()));
        addOPortalRequest.put("address3_type", RequestBody.create(MediaType.parse("multipart/form-data"), address3_type));
        addOPortalRequest.put("address4", RequestBody.create(MediaType.parse("multipart/form-data"), ((TextView) findViewById(R.id.tv_address4)).getText().toString().trim()));
        addOPortalRequest.put("address4_type", RequestBody.create(MediaType.parse("multipart/form-data"), address4_type));
        addOPortalRequest.put("address5", RequestBody.create(MediaType.parse("multipart/form-data"), ((TextView) findViewById(R.id.tv_address5)).getText().toString().trim()));
        addOPortalRequest.put("address5_type", RequestBody.create(MediaType.parse("multipart/form-data"), address5_type));
        addOPortalRequest.put("notes", RequestBody.create(MediaType.parse("multipart/form-data"), ((EditText) findViewById(R.id.ed_notes)).getText().toString().trim()));
        addOPortalRequest.put("states[]", RequestBody.create(MediaType.parse("multipart/form-data"), selectedState));
        addOPortalRequest.put("assetsType[]", RequestBody.create(MediaType.parse("multipart/form-data"), selectedAssetType));
        //addOPortalRequest.put("states[]", RequestBody.create(MediaType.parse("multipart/form-data"), "Gujarat"));
        //addOPortalRequest.put("assetsType[]", RequestBody.create(MediaType.parse("multipart/form-data"), "Single Family"));
        addOPortalRequest.put("roi[]", RequestBody.create(MediaType.parse("multipart/form-data"), selectedROI));
        System.out.println("!!!addOPortalRequest = " + addOPortalRequest);


        UserServices userService = ApiFactory.getInstance(this).provideService(UserServices.class);
        Observable<Response> observable;
        observable = userService.addIPortal(addOPortalRequest);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<Response>() {
            @Override
            public void onSuccess(Response response) {
                showProgressBar(false);
                if (response.getSuccess()) {
                    data = response.getData();
                    System.out.println("!!!response.getData() = " + response.getData());

                    PromptDialog mPromptDialog = new PromptDialog(InfoActivity.this);
                    mPromptDialog.setCanceledOnTouchOutside(false);
                    mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
                    mPromptDialog.setAnimationEnable(true);
                    mPromptDialog.setTitleText("Success");
                    mPromptDialog.setContentText("User I-Portal details added Sucessfully.");
                    mPromptDialog.setPositiveListener("Ok", new PromptDialog.OnPositiveListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                            onBackPressed();
                        }
                    });
                    mPromptDialog.setNegativeListener("Cancel", new PromptDialog.OnNegativeListener() {
                        @Override
                        public void onClick(PromptDialog dialog) {
                            dialog.dismiss();
                        }
                    });
                    mPromptDialog.show();

                } else {
                    CommonDialogView.getInstance().showCommonDialog(InfoActivity.this, "",
                            response.getText()
                            , ""
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
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
                System.out.println("!!!throwable = " + throwable.getMessage());
            }
        });
    }


    private boolean validation() {
        if (mValidator.isEmpty(((EditText) findViewById(R.id.editTextRefferedby)).getText().toString().trim())) {
            seekBarShow(Constants.ENTER_REFERRED_BY, Color.RED);
            return true;
        } else if (mValidator.isEmpty(((EditText) findViewById(R.id.edinvestorname)).getText().toString().trim())) {
            seekBarShow(Constants.ENTER_INVESTOR_NAME, Color.RED);
            return true;
        } else if (mValidator.isEmpty(((EditText) findViewById(R.id.edcapital)).getText().toString().trim())) {
            seekBarShow(Constants.ENTER_CAPITAL_ON_HAND, Color.RED);
            return true;
        } else if (mValidator.isEmpty(((EditText) findViewById(R.id.ed_attorneyname)).getText().toString().trim())) {
            seekBarShow(Constants.ENTER_ATTORNEY_NAME, Color.RED);
            return true;
        } else if (selectedRoi == -1) {
            seekBarShow(Constants.ENTER_ROI, Color.RED);
            return true;
        }
        return false;
    }

    private void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PLACE_PICKER_REQUEST:
                if (data != null) {

                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        String lat = String.valueOf(place.getLatLng().latitude);
                        String lang = String.valueOf(place.getLatLng().longitude);
                        if (isfromAddress1) {
                            ((TextView) findViewById(R.id.tv_address1)).setText(place.getAddress());
                            isfromAddress1 = false;
                        } else if (isfromAddress2) {
                            ((TextView) findViewById(R.id.tv_address2)).setText(place.getAddress());
                            isfromAddress2 = false;
                        } else if (isfromAddress3) {
                            ((TextView) findViewById(R.id.tv_address3)).setText(place.getAddress());
                            isfromAddress3 = false;
                        } else if (isfromAddress4) {
                            ((TextView) findViewById(R.id.tv_address4)).setText(place.getAddress());
                            isfromAddress4 = false;
                        } else if (isfromAddress5) {
                            isfromAddress5 = false;
                            ((TextView) findViewById(R.id.tv_address5)).setText(place.getAddress());
                        }


                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        Status status = Autocomplete.getStatusFromIntent(data);
                    } else if (resultCode == RESULT_CANCELED) {
                    }
                } else {
                    Toast.makeText(this, "something want to wrong,please re-select address!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}