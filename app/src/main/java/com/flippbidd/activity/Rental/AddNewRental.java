package com.flippbidd.activity.Rental;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.Adapter.Spinner.CommonTypeListAdapter;
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.Bottoms.PhotoBottomSheetDialogFragment;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Fragments.PropertyList;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.Response.TypeList.CommonTypeResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PdfViewer;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.core.text.HtmlCompat;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.refactor.lib.colordialog.PromptDialog;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;

public class AddNewRental extends BaseAppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.OnSingleImageSelectedListener {

    private static final String TAG = LogUtils.makeLogTag(AddNewRental.class);
    @BindView(R.id.image_toolbar)
    ImageView image_toolbar;
    @BindView(R.id.imageHeadreIcon)
    ImageView imageHeadreIcon;
    @BindView(R.id.txt_title_toolbar)
    CustomTextView txt_title_toolbar;
    @BindView(R.id.editTextViewHouse)
    CustomEditText editTextViewHouse;
    @BindView(R.id.editTextViewAddress)
    CustomEditText editTextViewAddress;
    @BindView(R.id.textViewSelectStateTitle)
    CustomTextView textViewSelectStateTitle;
    @BindView(R.id.imageSelectState)
    ImageView imageSelectState;

    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.imageSelectCity)
    ImageView imageSelectCity;
    @BindView(R.id.textViewSelectAvailabilityDateTitle)
    CustomTextView textViewSelectAvailabilityDateTitle;
    @BindView(R.id.imageSelectAvailabilityDate)
    ImageView imageSelectAvailabilityDate;
    @BindView(R.id.spinnerSelectAvailabilityDate)
    Spinner spinnerSelectAvailabilityDate;
    @BindView(R.id.editTextViewRentalBedsNo)
    CustomTextView editTextViewRentalBedsNo;
    @BindView(R.id.imageSelectRentalBedsNo)
    ImageView imageSelectRentalBedsNo;
    @BindView(R.id.spinnerSelectRentalBedsNo)
    Spinner spinnerSelectRentalBedsNo;
    @BindView(R.id.editTextViewRentalBathNo)
    CustomTextView editTextViewRentalBathNo;
    @BindView(R.id.imageSelectRentalBathNo)
    ImageView imageSelectRentalBathNo;
    @BindView(R.id.spinnerSelectRentalBathNo)
    Spinner spinnerSelectRentalBathNo;
    @BindView(R.id.editTextViewRentalArea)
    CustomEditText editTextViewRentalArea;
    @BindView(R.id.editTextViewRentalAmounts)
    CustomEditText editTextViewRentalAmounts;
    @BindView(R.id.editTextViewSecurityAmounts)
    CustomEditText editTextViewSecurityAmounts;
    @BindView(R.id.editTextViewOtherFees)
    CustomEditText editTextViewOtherFees;
    @BindView(R.id.textViewSelectLeaseTermsTitle)
    CustomTextView textViewSelectLeaseTermsTitle;
    @BindView(R.id.imageSelectLeaseTerms)
    ImageView imageSelectLeaseTerms;
    @BindView(R.id.spinnerSelectLeaseTerms)
    Spinner spinnerSelectLeaseTerms;
    @BindView(R.id.radioCreditCheckGroup)
    RadioGroup radioCreditCheckGroup;
    @BindView(R.id.editTextViewRentalDescription)
    CustomEditText editTextViewRentalDescription;
    @BindView(R.id.btnUploadImage)
    CustomTextView btnUploadImage;
    @BindView(R.id.btnUploadDocuments)
    CustomTextView btnUploadDocuments;
    @BindView(R.id.imageUploadDoc)
    ImageView imageUploadDoc;
    @BindView(R.id.imagesSelected)
    ImageView imagesSelected;
    @BindView(R.id.imagesDocSelected)
    ImageView imagesDocSelected;
    @BindView(R.id.checkBoxTermsAndCondistion)
    CheckBox checkBoxTermsAndCondistion;
    @BindView(R.id.btnUploadPost)
    CustomAppCompatButton btnUploadPost;

    @BindView(R.id.textViewSelectBuildingTypeTitle)
    CustomTextView textViewSelectBuildingTypeTitle;
    @BindView(R.id.imageSelectBuildingType)
    ImageView imageSelectBuildingType;
    @BindView(R.id.spinnerSelectBuildingType)
    Spinner spinnerSelectBuildingType;

    @BindView(R.id.checkBoxW2)
    CheckBox checkBoxW2;
    @BindView(R.id.checkBoxDriverLicense)
    CheckBox checkBoxDriverLicense;
    @BindView(R.id.checkBoxPaystubs)
    CheckBox checkBoxPaystubs;
    @BindView(R.id.checkBoxBankStatements)
    CheckBox checkBoxBankStatements;
    @BindView(R.id.checkBoxTaxReturn)
    CheckBox checkBoxTaxReturn;
    @BindView(R.id.checkBoxOther)
    CheckBox checkBoxOther;


    @BindView(R.id.relativeRentalSelectBath)
    RelativeLayout relativeRentalSelectBath;
    @BindView(R.id.relativeRentalSelectBeds)
    RelativeLayout relativeRentalSelectBeds;
    @BindView(R.id.relativeRentalSelectCity)
    RelativeLayout relativeRentalSelectCity;
    @BindView(R.id.relativeRentalSelectState)
    RelativeLayout relativeRentalSelectState;
    @BindView(R.id.relativeRentalSelectBuilding)
    RelativeLayout relativeRentalSelectBuilding;
    @BindView(R.id.relativeRentalSelectLeaseTerms)
    RelativeLayout relativeRentalSelectLeaseTerms;

    @BindView(R.id.relativeLayoutUploadImages)
    RelativeLayout relativeLayoutUploadImages;
    @BindView(R.id.relativeLayoutDocUpload)
    RelativeLayout relativeLayoutDocUpload;
    @BindView(R.id.textViewTermsAndCondistion)
    CustomTextView textViewTermsAndCondistion;
    @BindView(R.id.progressRental)
    ProgressBar loProgressRental;


    String mTremsAndCondistionIsSelected;
    private RadioButton radioCreditCheckButton;
    //adapter custom
    CommonTypeListAdapter mSpinnerPropertyTypeSelection;
    CommonTypeListAdapter mSpinnerLeaseTermsTypeSelection;
    CommonTypeListAdapter mSpinnerBuildingTypeSelection;


    CommonData mEditRentalData;
    Disposable disposable;
    Calendar mCalendarStart;
    private int Hour, Min;
    //array list
    List<CommonListData> mPropertyTypeLists = new ArrayList<>();
    List<CommonListData> mLeaseTrmsResponsesLists = new ArrayList<>();
    List<CommonListData> mBuildingData = new ArrayList<>();
    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();
    List<String> mDocNeededList = new ArrayList<>();

    private ArrayAdapter<String> dataAdapter, bedAdapter;


    private String house, address, property_type, state_id, city_id, building_type, available_date, bed_nos,
            bath_nos, area, rent_amount, sequrity_amount, other_fees, lease_term, credit_check, description, doc_need, mLocationLat, mLocationLang;
    String mScreenType;
    String itemSpinner;
    Validator mValidator;
    private static final int PLACE_PICKER_REQUEST = 1000;

    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";

    public static Intent getIntentActivity(Context mContext, CommonData mRentalData, String mScreenType) {
        Intent mIntent = new Intent(mContext, AddNewRental.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mRentalData);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mScreenType);
        return mIntent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Places.initialize(getApplicationContext(), API_KEY);

        txt_title_toolbar.setVisibility(View.VISIBLE);
        //id of image drawer in toolbar
        image_toolbar.setVisibility(View.VISIBLE);
        image_toolbar.setImageResource(R.drawable.back_icon_new);
        mCalendarStart = Calendar.getInstance();
        Calendar mcurrentTime = Calendar.getInstance();
        Hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        Min = mcurrentTime.get(Calendar.MINUTE);

        inits();


        callStateApi(false);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mScreenType = mBundle.getString(Constants.EXTRA.SCREEN_TYPE);
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                //post
                txt_title_toolbar.setText("Post Rental");
                callGetBuildingType(true);
            } else {
                //edit
                txt_title_toolbar.setText("Edit Rental");
                mEditRentalData = (CommonData) mBundle.getSerializable(Constants.EXTRA.DATA);
                callGetBuildingType(true);
            }
        }


        spinnerSelectLeaseTerms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
                textViewSelectLeaseTermsTitle.setText(item);

                if (mLeaseTrmsResponsesLists != null && mLeaseTrmsResponsesLists.size() != 0) {
                    lease_term = mLeaseTrmsResponsesLists.get(pos).getCommonId();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        spinnerSelectRentalBedsNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = spinnerSelectRentalBedsNo.getSelectedItem().toString();
                editTextViewRentalBedsNo.setText(item);
                //  LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSelectRentalBathNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = spinnerSelectRentalBathNo.getSelectedItem().toString();
                editTextViewRentalBathNo.setText(item);
                // LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSelectBuildingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
                textViewSelectBuildingTypeTitle.setText(item);

                if (mBuildingData != null && mBuildingData.size() != 0) {
                    building_type = mBuildingData.get(i).getCommonId();
                }

                //    LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        //check box init
        checkBoxDriverLicense.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxDriverLicense.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxDriverLicense.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxDriverLicense.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });

        checkBoxW2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxW2.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxW2.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxW2.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });

        checkBoxPaystubs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxPaystubs.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxPaystubs.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxPaystubs.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });

        checkBoxBankStatements.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxBankStatements.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxBankStatements.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxBankStatements.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });

        checkBoxTaxReturn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxTaxReturn.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxTaxReturn.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxTaxReturn.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });

        checkBoxOther.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    mDocNeededList.add(checkBoxOther.getText().toString());
                } else {
                    if (mDocNeededList != null && mDocNeededList.size() != 0) {
                        if (mDocNeededList.contains(checkBoxOther.getText().toString())) {
                            int pos = mDocNeededList.indexOf(checkBoxOther.getText().toString());
                            mDocNeededList.remove(pos);
                        }
                    }
                }
            }
        });


    }

    private void inits() {
        textViewTermsAndCondistion.setText(HtmlCompat.fromHtml(getResources().getString(R.string.string_trms), HtmlCompat.FROM_HTML_MODE_LEGACY));

//        mSpinnerPropertyTypeSelection = new CommonTypeListAdapter(mBaseAppCompatActivity, R.layout.adapter_spinner_layout, mPropertyTypeLists);
//        spinnerSelectPropertyType.setAdapter(mSpinnerPropertyTypeSelection);

        mSpinnerBuildingTypeSelection = new CommonTypeListAdapter(mBaseAppCompatActivity,
                R.layout.adapter_spinner_layout, mBuildingData);
        spinnerSelectBuildingType.setAdapter(mSpinnerBuildingTypeSelection);

        mSpinnerLeaseTermsTypeSelection = new CommonTypeListAdapter(mBaseAppCompatActivity,
                R.layout.adapter_spinner_layout, mLeaseTrmsResponsesLists);
        spinnerSelectLeaseTerms.setAdapter(mSpinnerLeaseTermsTypeSelection);

        // Creating adapter for spinner
        dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.bedAndBathCountArray);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerSelectRentalBathNo.setAdapter(dataAdapter);

        // Creating adapter for spinner
        bedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.bedAndBathCountArray);
        // Drop down layout style - list view with radio button
        bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerSelectRentalBedsNo.setAdapter(bedAdapter);


    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_new_rental_layout;
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
                , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);


        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this);
        startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

      /*  PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityIfNeeded(builder.build(mBaseAppCompatActivity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
    }


    @OnClick({R.id.btnUploadPost, R.id.relativeLayoutDocUpload, R.id.relativeLayoutUploadImages, R.id.imageSelectAvailabilityDate
            , R.id.relativeRentalSelectBath, R.id.relativeRentalSelectBeds, R.id.relativeRentalSelectBuilding, R.id.relativeRentalSelectLeaseTerms, R.id.relativeRentalSelectCity
            , R.id.relativeRentalSelectState, R.id.textViewTermsAndCondistion})
    void viewclickEvent(View view) {

        switch (view.getId()) {

            case R.id.relativeRentalSelectState:
                if (mStateList != null && mStateList.size() != 0) {
                    showDialogStateView();
                }
                break;
            case R.id.relativeRentalSelectCity:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.relativeRentalSelectLeaseTerms:
                spinnerSelectLeaseTerms.performClick();
                break;
            case R.id.relativeRentalSelectBuilding:
                spinnerSelectBuildingType.performClick();
                break;
            case R.id.imageSelectAvailabilityDate:
                openStartCalender();
                break;
            case R.id.relativeLayoutUploadImages:
                hideKeyboard();
                openBottomSheetView();
                break;
            case R.id.relativeLayoutDocUpload:
                hideKeyboard();
                openDocumentsFile();
                break;
            case R.id.relativeRentalSelectBeds:
                spinnerSelectRentalBedsNo.performClick();
                break;
            case R.id.relativeRentalSelectBath:
                spinnerSelectRentalBathNo.performClick();
                break;
            case R.id.btnUploadPost:
                hideKeyboard();
                house = editTextViewHouse.getText().toString().trim();
                address = editTextViewAddress.getText().toString().trim();
                available_date = textViewSelectAvailabilityDateTitle.getText().toString().trim();
                bed_nos = editTextViewRentalBedsNo.getText().toString().trim();
                bath_nos = editTextViewRentalBathNo.getText().toString().trim();
                area = editTextViewRentalArea.getText().toString().trim();
                rent_amount = editTextViewRentalAmounts.getText().toString().trim();
                sequrity_amount = editTextViewSecurityAmounts.getText().toString().trim();
                other_fees = editTextViewOtherFees.getText().toString().trim();
                description = editTextViewRentalDescription.getText().toString().trim();

                doc_need = android.text.TextUtils.join(",", mDocNeededList);

                //Credit Check
                int isSelectedVacated = radioCreditCheckGroup.getCheckedRadioButtonId();
                radioCreditCheckButton = radioCreditCheckGroup.findViewById(isSelectedVacated);
                if (radioCreditCheckButton != null) {
                    if (radioCreditCheckButton.getText().toString().equalsIgnoreCase("Yes")) {
                        credit_check = "1";
                    } else {
                        credit_check = "0";
                    }
                } else {
                    credit_check = "";
                }
//                if (spinnerSelectRentalBedsNo.getSelectedItemPosition() > 0) {
//                    bed_nos = spinnerSelectRentalBedsNo.getSelectedItem().toString();
//                }
//
//                if (spinnerSelectRentalBathNo.getSelectedItemPosition() > 0) {
//                    bath_nos = spinnerSelectRentalBathNo.getSelectedItem().toString();
//                }

                if (checkBoxTermsAndCondistion.isChecked()) {
                    mTremsAndCondistionIsSelected = "isChecked";
                } else {
                    mTremsAndCondistionIsSelected = "";
                }

                mValidator = Validator.getInstance();
                if (!validation()) {

                    if (mLocationLat != null && !mLocationLat.equalsIgnoreCase("") ||
                            mLocationLang != null && !mLocationLang.equalsIgnoreCase("")) {
                        callAddPostApi();
                    } else {
                        seekBarShow("please select address from google map", Color.RED);
                        return;
                    }
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
                //  Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
                state_id = searchableAdapter.getItem(position).getCommonId();
                callCityApi();
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
                // Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectCityTitle.setText(searchableAdapter.getItem(position).getCommonName());
                city_id = searchableAdapter.getItem(position).getCommonId();
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


    private boolean validation() {

        if (mValidator.isEmpty(address)) {
            seekBarShow("Enter Address.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(available_date)) {
            seekBarShow("Please select Date.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(area)) {
            seekBarShow("Enter area.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(rent_amount)) {
            seekBarShow("Enter Rent Amounts.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(sequrity_amount)) {
            seekBarShow("Enter Security Amounts.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(other_fees)) {
            seekBarShow("Enter Other Fees.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(credit_check)) {
            seekBarShow("Please select credit check.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(doc_need)) {
            seekBarShow("Must required one documents.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mTremsAndCondistionIsSelected)) {
            seekBarShow(Constants.val_tremsandcondistion, Color.RED);
            return true;
        }


        return false;
    }

    @OnTouch({R.id.relativeRentalSelectBath, R.id.relativeRentalSelectBeds, R.id.relativeRentalSelectBuilding, R.id.relativeRentalSelectLeaseTerms, R.id.relativeRentalSelectCity
            , R.id.relativeRentalSelectState, R.id.textViewTermsAndCondistion})
    public boolean onTouchSpinner() {
        hideKeyboard();
        return false;
    }

    @OnClick(R.id.image_toolbar)
    void clickBack() {
        finish(true);
    }

    private void openStartCalender() {
        DatePickerDialog StartTime = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {

                mCalendarStart.set(selectedYear, selectedMonth, selectedDay);
                String month = (selectedMonth + 1) > 9 ? String.valueOf(selectedMonth + 1) : String.valueOf("0" + (selectedMonth + 1));
                String day = (selectedDay) > 9 ? String.valueOf(selectedDay) : String.valueOf("0" + selectedDay);
                textViewSelectAvailabilityDateTitle.setText(month + "/" + day + "/" + selectedYear);
            }

        }, mCalendarStart.get(Calendar.YEAR), mCalendarStart.get(Calendar.MONTH), mCalendarStart.get(Calendar.DAY_OF_MONTH));
        StartTime.setTitle("Select Start Date");
        StartTime.getDatePicker().setMinDate(System.currentTimeMillis());
        StartTime.show();
    }

//    //get Property Type List
//    private void callGetPropertyType(final boolean isProgress) {
//        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
//            showToast(getString(R.string.no_internet));
//            return;
//        }
//        showProgressDialog(isProgress);
//
//        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
//        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
//        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
//        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));
//
//
//        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
//        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
//            @Override
//            public void onSuccess(CommonTypeResponse response) {
//                hideProgressDialog();
//                if (mPropertyTypeLists != null)
//                    mPropertyTypeLists.clear();
//
//                if (response.getSuccess()) {
//                    mPropertyTypeLists.addAll(response.getData());
//
//                    if (mSpinnerPropertyTypeSelection != null)
//                        mSpinnerPropertyTypeSelection.notifyDataSetChanged();
//
//                }
//                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
//                //call state api
//                callGetBuildingType(isProgress);
//
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//                hideProgressDialog();
//            }
//        });
//    }


    //get Lease Terms List
    private void callGetBuildingType(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_BUILDING));


        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                hideProgressDialog();
                if (mBuildingData != null)
                    mBuildingData.clear();

                //Lease Terms adapter
                if (response.getSuccess()) {

                    if (response.getData() != null && response.getData().size() != 0) {
                        mBuildingData.addAll(response.getData());
                        mSpinnerBuildingTypeSelection.notifyDataSetChanged();
                    }
                }

                callGetLeaseTermsType(isProgress);
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //get Lease Terms List
    private void callGetLeaseTermsType(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_LEASE));


        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                hideProgressDialog();
                if (mLeaseTrmsResponsesLists != null)
                    mLeaseTrmsResponsesLists.clear();
                //Lease Terms adapter
                if (response.getSuccess()) {

                    if (response.getData() != null && response.getData().size() != 0) {
                        mLeaseTrmsResponsesLists.addAll(response.getData());

                        if (mSpinnerLeaseTermsTypeSelection != null)
                            mSpinnerLeaseTermsTypeSelection.notifyDataSetChanged();
                    }
                }

                //check edit or add
                if (!mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setRentalItemData();
                        }
                    }, 4000);
                }


            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //Get State
    private void callStateApi(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_STATE));


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
                            callCityApi();
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
    private void callCityApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_CITY));
        hashMap.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), state_id));

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mCityList != null)
                    mCityList.clear();
                hideProgressDialog();

                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());
                    textViewSelectCityTitle.setText(response.getData().get(0).getCommonName());
                    city_id = response.getData().get(0).getCommonId();
                } else {
                    textViewSelectCityTitle.setText("");
                    openErorrDialog(getResources().getString(R.string.no_city_availabilityy));
                }


                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void openDocumentsFile() {
//        Intent intent = FileUtils.createGetContentIntent();
//        startActivityIfNeeded(Intent.createChooser(intent, "Choose File to Upload.."), PICK_FILE_REQUEST);

        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "pdf"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (PICK_FILE_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    String path = "";
                    for (NormalFile file : list) {
                        path = file.getPath();
                    }

                    mDocumentFile = FileUtils.getFile(mBaseAppCompatActivity, path);
                    if (mDocumentFile != null) {
                        imagesDocSelected.setVisibility(View.VISIBLE);
                        btnUploadDocuments.setVisibility(View.GONE);
                        imageUploadDoc.setVisibility(View.GONE);
                        if (Fileaccept(mDocumentFile)) {
                            btnUploadDocuments.setText(list.get(0).getName());
                        } else {
                            openErorrDialog(getResources().getString(R.string.string_re_select_file_error_message));
                        }
                    } else {
                        imagesDocSelected.setVisibility(View.GONE);
                        btnUploadDocuments.setVisibility(View.VISIBLE);
                        imageUploadDoc.setVisibility(View.VISIBLE);
                        openErorrDialog(getResources().getString(R.string.string_re_select_file_error_message));
                    }
                }
            }
            break;
            case PLACE_PICKER_REQUEST:

                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        //get lat and long
                        mLocationLat = String.valueOf(place.getLatLng().latitude);
                        mLocationLang = String.valueOf(place.getLatLng().longitude);
                        //set data
                        editTextViewAddress.setText("");
                        editTextViewAddress.setText(place.getAddress());
                        editTextViewAddress.setEnabled(true);

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
        // reset
        mResultCode = 0;
        mResultCode = Activity.RESULT_CANCELED;
        mData = null;
    }

    private void openBottomSheetView() {
        final PhotoBottomSheetDialogFragment commonListBottomSheet = PhotoBottomSheetDialogFragment.newInstance();
        commonListBottomSheet.addListener(new PhotoBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onCameraClick() {
                commonListBottomSheet.dismiss();
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }

            @Override
            public void onGalleryClick() {
                commonListBottomSheet.dismiss();
                BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                        .isMultiSelect() //Set this if you want to use multi selection mode.
                        .setMinimumMultiSelectCount(2) //Default: 1.
                        .setMaximumMultiSelectCount(6) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                        .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                        .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                        .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                        .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                        .disableOverSelectionMessage() //You can also decide not to show this over select message.
                        .build();
                multiSelectionPicker.show(getSupportFragmentManager(), "picker");
            }

            @Override
            public void onCancelClick() {
                commonListBottomSheet.dismiss();
            }
        });
        commonListBottomSheet.showNow(getSupportFragmentManager(), AddNewRental.class.getSimpleName());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PICK_FILE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openDocumentsFile();
                } else {
                    Toast.makeText(AddNewRental.this, "Approve permissions to open Documents File Picker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    // Image pick and select Code
    private static final int FROM_CAMERA_AND_GALLARY = 888;

    private static final int PICK_FILE_REQUEST = 3;

    private File mImageThumbFile = null, mImageFile = null, mDocumentFile = null;
    private List<File> mFileUriList = new ArrayList<>();
    private List<MultipartBody.Part> fileThumb = new ArrayList<>();
    private int mRequestCode;
    private int mResultCode;
    private Intent mData;


    public static boolean Fileaccept(File file) {
        for (String extension : Constants.cvFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    //end image
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    public void callAddPostApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        MultipartBody.Part itemDocumentFile = null;
        if (mFileUriList != null) {
            //add and Edit
            for (int file = 0; file < mFileUriList.size(); file++) {
                fileThumb.add(prepareFilePart("rental_img[]", mFileUriList.get(file)));
            }
        } else {
            //edit or Null
            fileThumb.add(prepareFilePart("rental_img[]", null));
        }


        if (mDocumentFile != null) {
            //add
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mDocumentFile);
            itemDocumentFile = MultipartBody.Part.createFormData("upload_doc", mDocumentFile.getName(), requestFile);
        } else {
            //edit
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                itemDocumentFile = MultipartBody.Part.createFormData("upload_doc", "", requestFile);
            } else {
                if (mEditRentalData.getUploadDoc() != null && !mEditRentalData.getUploadDoc().equalsIgnoreCase("")) {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mEditRentalData.getUploadDoc());
                    itemDocumentFile = MultipartBody.Part.createFormData("upload_doc", "", requestFile);
                } else {
                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                    itemDocumentFile = MultipartBody.Part.createFormData("upload_doc", "", requestFile);
                }
            }
        }
        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
            addPostRequest.put("rental_id", RequestBody.create(MediaType.parse("multipart/form-data"), mEditRentalData.getCommonId()));
        }

        addPostRequest.put("house", RequestBody.create(MediaType.parse("multipart/form-data"), house));
        addPostRequest.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), state_id));
        addPostRequest.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), city_id));
        addPostRequest.put("description", RequestBody.create(MediaType.parse("multipart/form-data"), description));
        addPostRequest.put("available_date", RequestBody.create(MediaType.parse("multipart/form-data"), available_date));
        addPostRequest.put("bed_nos", RequestBody.create(MediaType.parse("multipart/form-data"), bed_nos));
        addPostRequest.put("bath_nos", RequestBody.create(MediaType.parse("multipart/form-data"), bath_nos));
        addPostRequest.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), address));
        addPostRequest.put("area", RequestBody.create(MediaType.parse("multipart/form-data"), area));
        addPostRequest.put("rent_amount", RequestBody.create(MediaType.parse("multipart/form-data"), rent_amount));
        addPostRequest.put("sequrity_amount", RequestBody.create(MediaType.parse("multipart/form-data"), sequrity_amount));
        addPostRequest.put("property_type", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("building_type", RequestBody.create(MediaType.parse("multipart/form-data"), building_type));

        addPostRequest.put("other_fees", RequestBody.create(MediaType.parse("multipart/form-data"), other_fees));
        addPostRequest.put("lease_term", RequestBody.create(MediaType.parse("multipart/form-data"), lease_term));
        addPostRequest.put("credit_check", RequestBody.create(MediaType.parse("multipart/form-data"), credit_check));
        addPostRequest.put("doc_need", RequestBody.create(MediaType.parse("multipart/form-data"), doc_need));
        addPostRequest.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLat));
        addPostRequest.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLang));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable;

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
            observable = userService.addNewRental(fileThumb, itemDocumentFile, addPostRequest);
        } else {
            observable = userService.editNewRental(fileThumb, itemDocumentFile, addPostRequest);
        }


        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();
                // LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    PropertyList.isClickBack = false;
                    if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
//                        singToQB(response.getId());
                        openSuccessDialog();

                    } else {
                        openEditSuccessDialog();
                    }

                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }


    //    //chat login with Qb
/*
    private void singToQB(final int id) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        QBUser qbUser = new QBUser(getAppendString(String.valueOf(id)), Consts.userPassword);
        qbUser.setFullName(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName());
        qbUser.setCustomData(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getProfilePic());

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
                // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
                if (qbUser.getId() != null && !qbUser.getId().equals("")) {
                    //LogUtils.LOGD(TAG, "onSuccess() called with: response QbID= [" + qbUser.getId() + "]");
                    callUpdateQBID(qbUser.getId(), id);
                }
            }

            @Override
            public void onError(QBResponseException e) {
                //LogUtils.LOGD(TAG, "onError() called with: response QbID= [" + e.getErrors() + "]");
            }
        });

    }

    private String getAppendString(String Id) {

        String mString = Id + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_rental";
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
        addQBID.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "rental"));
        addQBID.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(qbId)));

        //token,common_id,type,sender_qb_id
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateSenderQbId(addQBID);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    openSuccessDialog();
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
    private void openSuccessDialog() {
        PromptDialog mPromptDialog = new PromptDialog(this);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));
        mPromptDialog.setContentText(getString(R.string.string_success_rental));
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                finish(true);
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


    private void openEditSuccessDialog() {
        new PromptDialog(this)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.string_success))
                .setContentText(getString(R.string.string_edit_rental))
                .setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        finish(true);
                    }
                })
                .setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        finish(true);
                    }
                }).show();
    }


    //set Edit Data
    private void setRentalItemData() {
        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {

            checkBoxTermsAndCondistion.setChecked(true);
            //edit
            editTextViewHouse.setText(mEditRentalData.getHouse());
            if (mEditRentalData.getAddress() != null && !mEditRentalData.getAddress().equalsIgnoreCase("")) {
                editTextViewAddress.setText(mEditRentalData.getAddress());
            }

            if (mEditRentalData.getState() != null && !mEditRentalData.getState().equalsIgnoreCase("")) {
                textViewSelectStateTitle.setText(mEditRentalData.getState());
            }

            if (mEditRentalData.getCity() != null && !mEditRentalData.getCity().equalsIgnoreCase("")) {
                textViewSelectCityTitle.setText(mEditRentalData.getCity());
            }

//            if (mEditRentalData.getPropertyType() != null && !mEditRentalData.getPropertyType().equalsIgnoreCase("")) {
//                textViewSelectPropertyTypeTitle.setText(mEditRentalData.getPropertyType());

//                if (mPropertyTypeLists != null && mPropertyTypeLists.size() != 0) {
//                    if (mPropertyTypeLists.contains(mEditRentalData.getPropertyType())) {
//                        String pTYpe = mEditRentalData.getPropertyType();
//                        int pos = mPropertyTypeLists.indexOf(pTYpe);
//                        property_type = mPropertyTypeLists.get(pos).getPropertyTypeId();
//                    }
//                }
//            }

            if (mEditRentalData.getBuildingType() != null && !mEditRentalData.getBuildingType().equalsIgnoreCase("")) {
                textViewSelectBuildingTypeTitle.setText(mEditRentalData.getBuildingType());
//                if (mBuildingData != null && mBuildingData.size() != 0) {
//                    int pos = mBuildingData.indexOf(mEditRentalData.getBuildingType());
//                    building_type = mBuildingData.get(pos).getBuildingTypeId();
//                }

            }

            if (mEditRentalData.getAvailableDate() != null && !mEditRentalData.getAvailableDate().equalsIgnoreCase("")) {
                textViewSelectAvailabilityDateTitle.setText(mEditRentalData.getAvailableDate());
            }


            if (mEditRentalData.getBedNos() != null && !mEditRentalData.getBedNos().equalsIgnoreCase("")) {
                editTextViewRentalBedsNo.setText(mEditRentalData.getBedNos());
                bed_nos = mEditRentalData.getBedNos();
            }

            if (mEditRentalData.getBathNos() != null && !mEditRentalData.getBathNos().equalsIgnoreCase("")) {
                editTextViewRentalBathNo.setText(mEditRentalData.getBathNos());
                bath_nos = mEditRentalData.getBathNos();
            }

            if (mEditRentalData.getArea() != null && !mEditRentalData.getArea().equalsIgnoreCase("")) {
                editTextViewRentalArea.setText(mEditRentalData.getArea());
            }


            if (mEditRentalData.getRentAmount() != null && !mEditRentalData.getRentAmount().equalsIgnoreCase("")) {
                editTextViewRentalAmounts.setText(mEditRentalData.getRentAmount());
            }

            if (mEditRentalData.getSequrityAmount() != null && !mEditRentalData.getSequrityAmount().equalsIgnoreCase("")) {
                editTextViewSecurityAmounts.setText(mEditRentalData.getSequrityAmount());
            }

            if (mEditRentalData.getOtherFees() != null && !mEditRentalData.getOtherFees().equalsIgnoreCase("")) {
                editTextViewOtherFees.setText(mEditRentalData.getOtherFees());
            }


            if (mEditRentalData.getLeaseTerm() != null && !mEditRentalData.getLeaseTerm().equalsIgnoreCase("")) {
                textViewSelectLeaseTermsTitle.setText(mEditRentalData.getLeaseTerm());

//                if (mLeaseTrmsResponsesLists != null && mLeaseTrmsResponsesLists.size() != 0) {
//                    int pos = mLeaseTrmsResponsesLists.indexOf(mEditRentalData.getLeaseTerm());
//                    lease_term = mLeaseTrmsResponsesLists.get(pos).getLeaseTermId();
//                }

            }

            if (mEditRentalData.getCreditCheck().equalsIgnoreCase("0")) {
                radioCreditCheckButton = radioCreditCheckGroup.findViewById(R.id.radioCreditCheckNo);
                radioCreditCheckButton.setChecked(true);
            } else {
                radioCreditCheckButton = radioCreditCheckGroup.findViewById(R.id.radioCreditCheckYes);
                radioCreditCheckButton.setChecked(true);
            }
            if (mEditRentalData.getDescription() != null && !mEditRentalData.getDescription().equalsIgnoreCase("")) {
                editTextViewRentalDescription.setText(mEditRentalData.getDescription());
            }

            if (mEditRentalData.getDocNeed() != null && !mEditRentalData.getDocNeed().equalsIgnoreCase("")) {
                mDocNeededList = new ArrayList<String>(Arrays.asList(mEditRentalData.getDocNeed().split(",")));
                // LogUtils.LOGD(TAG, "Documents Array -->" + mDocNeededList.size());

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_w2))) {
                    checkBoxW2.setChecked(true);
                }

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_driver_license))) {
                    checkBoxDriverLicense.setChecked(true);
                }

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_paystubs))) {
                    checkBoxPaystubs.setChecked(true);
                }

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_bank_statements))) {
                    checkBoxBankStatements.setChecked(true);
                }

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_other))) {
                    checkBoxOther.setChecked(true);
                }

                if (mDocNeededList.contains(getResources().getString(R.string.string_doc_tax_return))) {
                    checkBoxTaxReturn.setChecked(true);
                }
            }

            //get id of selected state and city
            if (mStateList != null && mStateList.size() != 0) {
                for (int st = 0; st < mStateList.size(); st++) {
                    String sName = mStateList.get(st).getCommonName();
                    if (sName.equalsIgnoreCase(mEditRentalData.getState())) {
                        state_id = mStateList.get(st).getCommonId();
                        callEditCityApi(false);
                    }
                }
            }


            if (mEditRentalData.getState() != null && !mEditRentalData.getState().equalsIgnoreCase("")) {
                textViewSelectStateTitle.setText(mEditRentalData.getState());
            }

            if (mEditRentalData.getCity() != null && !mEditRentalData.getCity().equalsIgnoreCase("")) {
                textViewSelectCityTitle.setText(mEditRentalData.getCity());
            }

            mLocationLat = mEditRentalData.getLat();
            mLocationLang = mEditRentalData.getLang();
        }
    }

    private MultipartBody.Part prepareFilePart(String partName, File fileUri) {

        // create RequestBody instance from file

        File mFile;
        RequestBody requestFile;
        if (fileUri != null) {
            requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), fileUri);
        } else {
            requestFile =
                    RequestBody.create(MediaType.parse("multipart/form-data"), "");
        }

        // MultipartBody.Part is used to send also the actual file name
        return MultipartBody.Part.createFormData(partName, fileUri.getName(), requestFile);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }

    @Override
    public void onMultiImageSelected(List<Uri> uriList) {
        if (mFileUriList != null && mFileUriList.size() != 0) {
            mFileUriList.clear();
        }
        Uri uri = null;
        if (uriList != null) {
            for (int select = 0; select < uriList.size(); select++) {
                File mFile = null;
                File mCompressor = null;
                mFile = FileUtils.getFile(mBaseAppCompatActivity, uriList.get(select));
                try {
//                    mCompressor = new Compressor(mBaseAppCompatActivity).compressToFile(mFile);
                    mCompressor = new Compressor(mBaseAppCompatActivity).setMaxHeight(512).setMaxWidth(412).setQuality(70).compressToFile(mFile);
                    mFileUriList.add(mCompressor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } else {
            uri = uriList.get(0);
            mFileUriList.add(FileUtils.getFile(mBaseAppCompatActivity, uri));
        }
        mImageThumbFile = mFileUriList.get(0);
        //get name file first index

        Bitmap bitmap = BitmapFactory.decodeFile(mImageThumbFile.getPath());
        imagesSelected.setImageBitmap(bitmap);
    }

    @Override
    public void onSingleImageSelected(Uri uri) {

        if (mFileUriList != null && mFileUriList.size() != 0) {
            mFileUriList.clear();
        }
        Uri uris = null;
        if (uri != null) {
            File mFile = null;
            File mCompressor = null;
            mFile = FileUtils.getFile(mBaseAppCompatActivity, uri);
            try {
                mCompressor = new Compressor(mBaseAppCompatActivity).compressToFile(mFile);
                mFileUriList.add(mCompressor);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mFileUriList.add(FileUtils.getFile(mBaseAppCompatActivity, uri));
        }
        mImageThumbFile = mFileUriList.get(0);
        //get name file first index
        Bitmap bitmap = BitmapFactory.decodeFile(mImageThumbFile.getPath());
        imagesSelected.setImageBitmap(bitmap);
    }

    private void callEditCityApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
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
                if (response.getSuccess()) {
                    mCityList.addAll(response.getData());

                    if (mCityList != null && mCityList.size() != 0) {
                        for (int st = 0; st < mCityList.size(); st++) {
                            String cName = mCityList.get(st).getCommonName();
                            if (cName.equalsIgnoreCase(mEditRentalData.getCity())) {
                                city_id = mCityList.get(st).getCommonId();
                            }
                        }
                    }
                } else {
                    textViewSelectCityTitle.setText("");
                    mBaseAppCompatActivity.seekBarShow("City Not Found!", Color.RED);
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }
}

