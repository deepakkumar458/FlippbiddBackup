package com.flippbidd.activity.Property;

import android.Manifest;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.Adapter.Spinner.CommonTypeListAdapter;
import com.flippbidd.Adapter.Spinner.SearchableAdapter;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Fragments.PropertyList;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse;
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
import com.flippbidd.databinding.ActivityPostPropertyLayoutBinding;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.hbb20.CountryCodePicker;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.ActivityCompat;

import butterknife.BindView;
import butterknife.BindViews;
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

public class PostNewProperty extends BaseAppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.OnSingleImageSelectedListener {

    private static final String TAG = LogUtils.makeLogTag(PostNewProperty.class);

    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";
    @BindView(R.id.image_toolbar)
    ImageView image_toolbar;
    @BindView(R.id.txt_title_toolbar)
    CustomTextView txt_title_toolbar;
    @BindView(R.id.textViewSelectPropertyTypeTitle)
    CustomTextView textViewSelectPropertyTypeTitle;
    @BindView(R.id.spinnerSelectPropertyType)
    Spinner spinnerSelectPropertyType;
    @BindView(R.id.progressEdit)
    ProgressBar loProgressEdit;
    @BindView(R.id.groupRadioListed)
    RadioGroup groupRadioListed;
    @BindView(R.id.editTextViewPrice)
    CustomEditText editTextViewPrice;
    @BindView(R.id.editTextViewHouse)
    CustomEditText editTextViewHouse;
    @BindView(R.id.editTextViewAddress)
    CustomEditText editTextViewAddress;
    @BindView(R.id.textViewSelectStateTitle)
    CustomTextView textViewSelectStateTitle;
    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.editTextViewBedsNo)
    CustomTextView editTextViewBedsNo;
    @BindView(R.id.editTextViewBathNo)
    CustomTextView editTextViewBathNo;
    @BindView(R.id.editTextViewArea)
    CustomEditText editTextViewArea;
    @BindView(R.id.editTextViewAreaCode)
    CustomTextView editTextViewAreaCode;
    @BindView(R.id.spinnerSelectAreaCode)
    Spinner spinnerSelectAreaCode;
    @BindView(R.id.radioVacantGroup)
    RadioGroup radioVacantGroup;
    @BindViews({R.id.radioVacantYes, R.id.radioVacantNo})
    List<RadioButton> radioButtons;
    @BindView(R.id.radioPreForeclosureGroup)
    RadioGroup radioPreForeclosureGroup;
    @BindView(R.id.radioSurrenderAgreementGroup)
    RadioGroup radioSurrenderAgreementGroup;
    @BindView(R.id.editTextViewAddDescription)
    CustomEditText editTextViewAddDescription;
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

    @BindView(R.id.spinnerSelectBedsNo)
    Spinner spinnerSelectBedsNo;
    @BindView(R.id.spinnerSelectBathNo)
    Spinner spinnerSelectBathNo;

    @BindView(R.id.etAddPropertyPhone)
    CustomEditText loEtAddPropertyPhone;
    @BindView(R.id.addPropertyCode)
    CountryCodePicker mAddPropertyCode;

    @BindView(R.id.relativePropertySelectBath)
    RelativeLayout relativePropertySelectBath;
    @BindView(R.id.relativePropertySelectBeds)
    RelativeLayout relativePropertySelectBeds;
    @BindView(R.id.relativePropertySelectPropertyType)
    RelativeLayout relativePropertySelectPropertyType;
    @BindView(R.id.relativePropertySelectCity)
    RelativeLayout relativePropertySelectCity;
    @BindView(R.id.relativePropertySelectState)
    RelativeLayout relativePropertySelectState;

    @BindView(R.id.llPriceBox)
    LinearLayout lollPriceBox;
    @BindView(R.id.llListedBox)
    LinearLayout lollListedBox;


    @BindView(R.id.linearContactHolder)
    LinearLayout linearContactHolder;
    @BindView(R.id.relativeLayoutUploadImages)
    RelativeLayout relativeLayoutUploadImages;


    ///new implements of NDRS
    @BindView(R.id.linearLayoutOfNRDS)
    LinearLayout linearLayoutOfNRDS;
    @BindView(R.id.editTextViewNRDS)
    CustomEditText editTextViewNRDS;
    @BindView(R.id.textViewTermsAndCondistion)
    CustomTextView textViewTermsAndCondistion;
    @BindView(R.id.radioContractHolderGroup)
    RadioGroup radioContractHolderGroup;

    //new feature add
    @BindView(R.id.linearPreProCloserManage)
    LinearLayout linearPreProCloserManage;
    @BindView(R.id.editTextViewPayOffAmount)
    CustomEditText editTextViewPayOffAmount;
    @BindView(R.id.editTextViewNoteBalance)
    CustomEditText editTextViewNoteBalance;
    @BindView(R.id.editTextViewSurrenderAgreement)
    CustomEditText editTextViewSurrenderAgreement;
    @BindView(R.id.editTextViewFlippBiddServices)
    CustomEditText editTextViewFlippBiddServices;
    @BindView(R.id.textViewMustCloseDate)
    CustomTextView textViewMustCloseDate;
    @BindView(R.id.llSurrenderAgreementEdittext)
    LinearLayout llSurrenderAgreementEdittext;
    //end new feature


    private RadioButton radioListedButton, radioVacatedButton, radioPreForeclosureButton, radioSurrenderAgreementButton, radioContractHolderButton;
    String mScreenType, mTremsAndCondistionIsSelected;
    CommonData mEditPropertyData;
    Validator mValidator;

    Disposable disposable;
    //Spinner Adapter
    CommonTypeListAdapter mSpinnerSellerTypeSelection;
    CommonTypeListAdapter mSpinnerPropertyTypeSelection;
    //list data from api
    List<CommonListData> mSellerTypList = new ArrayList<>();
    List<CommonListData> mPropertyTypeLists = new ArrayList<>();
    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();

    //api parameter data pass
    private String NDRS_value = "", mhouse_title, mPrice = "", mdescription, mis_perforeclosure, mis_vacant,
            marea_unit, mno_of_bath, mno_of_bed, marea, maddress, mis_listed = "2",
            mproperty_type, mCityId, mStateId, mLocationLat, mLocationLang;
    private String is_similar_deal = "0", deal_id = "0", is_Surrender = "";
    private String rec_username = "", rec_country_code = "", rec_mobile_number = "", rec_email_address = "", view_365 = "";
    private String itemSpinner;

    //new feature
    private String PayOffAmount = "", NoteBalance = "", SurrenderAgreement = "", FlippBiddServices = "", mustCloseDate = "";
    //end new feature
    private static final int PLACE_PICKER_REQUEST = 1000;
//    private GoogleApiClient mClient;

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_post_property_layout;
    }


    @Override
    protected void onStart() {
        super.onStart();
//        mClient.connect();
    }

    @Override
    protected void onStop() {
//        mClient.disconnect();
        super.onStop();
    }

    public static Intent getIntentActivity(Context mContext, CommonData mPropertyData, String mScreenType) {
        Intent mIntent = new Intent(mContext, PostNewProperty.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mPropertyData);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mScreenType);
        return mIntent;
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        // Initialize Places.
        Places.initialize(getApplicationContext(), API_KEY);


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.bedAndBathCountArray);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerSelectBathNo.setAdapter(dataAdapter);

        // Creating adapter for spinner
        ArrayAdapter<String> bedAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.bedAndBathCountArray);
        // Drop down layout style - list view with radio button
        bedAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerSelectBedsNo.setAdapter(bedAdapter);

        mAddPropertyCode.setCcpDialogShowNameCode(false);
        mAddPropertyCode.showArrow(false);
        mAddPropertyCode.showNameCode(false);
        mAddPropertyCode.setContentColor(R.color.colorPrimaryDark);
        mAddPropertyCode.setCountryForNameCode("US");
//        mAddPropertyCode.setCountryForPhoneCode(189);

        callStateApi(true);
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mScreenType = mBundle.getString(Constants.EXTRA.SCREEN_TYPE);
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {//post
                txt_title_toolbar.setText("Sell My Deal");
                callGetPropertyType(true);

                if (getIntent().hasExtra("deal_address")) {
                    maddress = mBundle.getString("deal_address", "");
                    editTextViewAddress.setText(maddress);
                }
                if (getIntent().hasExtra("lat")) {
                    mLocationLat = mBundle.getString("lat", "");
                }
                if (getIntent().hasExtra("lng")) {
                    mLocationLang = mBundle.getString("lng", "");
                }
                if (getIntent().hasExtra("deal_lat_lang")) {
                    LatLng mLatLang = mBundle.getParcelable("deal_lat_lang");
                    mLocationLat = String.valueOf(mLatLang.latitude);
                    mLocationLang = String.valueOf(mLatLang.longitude);
                }

                if (getIntent().hasExtra("is_similar_deal")) {
                    is_similar_deal = mBundle.getString("is_similar_deal");
                }
                if (getIntent().hasExtra("deal_id")) {
                    deal_id = mBundle.getString("deal_id");
                }

            } else {
                //edit
                txt_title_toolbar.setText("Edit Property");
                mEditPropertyData = (CommonData) mBundle.getSerializable(Constants.EXTRA.DATA);
                callGetPropertyType(true);

            }
        }
        spinnerSelectPropertyType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                itemSpinner = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();

                textViewSelectPropertyTypeTitle.setText(itemSpinner);
                if (mPropertyTypeLists != null && mPropertyTypeLists.size() != 0) {
                    mproperty_type = mPropertyTypeLists.get(i).getCommonId();
                }

                //LogUtils.LOGD(TAG, " response = [" + itemSpinner.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
      /*  spinnerSelectSellerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
                textViewSelectSellerTypeTitle.setText(item);
                if (mSellerTypList != null && mSellerTypList.size() != 0) {
                    mseller_type = mSellerTypList.get(i).getCommonId();
                }

                //LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });*/


        spinnerSelectBedsNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = spinnerSelectBedsNo.getSelectedItem().toString();
                editTextViewBedsNo.setText(item);
                // LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSelectBathNo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = spinnerSelectBathNo.getSelectedItem().toString();
                editTextViewBathNo.setText(item);
                //LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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

        //radio pre
        radioPreForeclosureGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioPreForeclosurYes) {
                    //hide
                    lollPriceBox.setVisibility(View.GONE);
                    lollListedBox.setVisibility(View.GONE);
//                    linearLayoutOfNRDS.setVisibility(View.GONE);
                    linearPreProCloserManage.setVisibility(View.VISIBLE);
                } else {
                    //show
                    lollPriceBox.setVisibility(View.VISIBLE);
                    lollListedBox.setVisibility(View.VISIBLE);
//                    linearLayoutOfNRDS.setVisibility(View.GONE);
                    linearPreProCloserManage.setVisibility(View.GONE);
                }
            }
        });

        radioSurrenderAgreementGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioSurrenderAgreementYes) {
                    llSurrenderAgreementEdittext.setVisibility(View.VISIBLE);
                } else {
                    llSurrenderAgreementEdittext.setVisibility(View.GONE);
                }
            }
        });


        //new feature
        textViewMustCloseDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDateTimeField();
            }
        });
        //end new feature

        radioContractHolderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioContractHolderYes) {
                    //show
                    linearContactHolder.setVisibility(View.GONE);
                } else {
                    //Hide
                    linearContactHolder.setVisibility(View.VISIBLE);
                }
            }
        });

        //NDRS edit textchanged
        editTextViewNRDS.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (!str.startsWith("0")) {
                } else {
                    seekBarShow("Does't start with 0 please enter valid number!", Color.RED);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        customTextView(textViewTermsAndCondistion);
    }

    /*new feature*/
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("MM-dd-yyyy");
    private SimpleDateFormat dateFormatter1 = new SimpleDateFormat("yyyy-MM-dd");

    private void setDateTimeField() {
        Calendar calendarSelected = Calendar.getInstance();
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(PostNewProperty.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarSelected.set(year, month, dayOfMonth);
                textViewMustCloseDate.setText(dateFormatter.format(calendarSelected.getTime()));
                mustCloseDate = dateFormatter1.format(calendarSelected.getTime());
                Log.e("TAG", "Date is==>" + mustCloseDate);
            }
        }, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
        datePickerDialog.show();
    }
    //end feature

    private void customTextView(CustomTextView view) {
        //By signing up, you agree to Flippbidd's Terms of use and Privacy Policy as well End User License Agreement.
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(
                "By checking the Box you are agreeing to Flippbidd's ");//52
        spanTxt.append("Terms & Conditions");//63//70
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.CONDITION_, ""), true);
            }
        }, spanTxt.length() - "Terms & Conditions".length(), spanTxt.length(), 0);
        spanTxt.append(" and ");//75
        spanTxt.setSpan(new ForegroundColorSpan(Color.parseColor("#a6a6a6")), 71, spanTxt.length(), 0);
        spanTxt.append("Privacy Policy");//70
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.POLICY_, ""), true);
            }
        }, spanTxt.length() - "Privacy Policy".length(), spanTxt.length(), 0);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
    }

    //, R.id.relativePropertySelectSellerType
    @OnTouch({R.id.relativePropertySelectBath, R.id.relativePropertySelectBeds, R.id.relativePropertySelectPropertyType
            , R.id.relativePropertySelectCity, R.id.relativePropertySelectState})
    public boolean onTouchSpinner() {
        hideKeyboard();
        return false;
    }

    public void setEditData() {

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
            checkBoxTermsAndCondistion.setChecked(true);
            editTextViewHouse.setText(mEditPropertyData.getHouse());
            if (mEditPropertyData.getAreaMeasure() != null && !mEditPropertyData.getAreaMeasure().equalsIgnoreCase("")) {
                editTextViewAreaCode.setText(mEditPropertyData.getAreaMeasure());
            }

            if (mEditPropertyData.getPropertyType() != null && !mEditPropertyData.getPropertyType().equalsIgnoreCase("")) {
                textViewSelectPropertyTypeTitle.setText(mEditPropertyData.getPropertyType());
                itemSpinner = mEditPropertyData.getPropertyType();
            }

            /*if (mEditPropertyData.getSallerType() != null && !mEditPropertyData.getSallerType().equalsIgnoreCase("")) {
                textViewSelectSellerTypeTitle.setText(mEditPropertyData.getSallerType());
            }*/

            if (mEditPropertyData.getRentAmount() != null && !mEditPropertyData.getRentAmount().equalsIgnoreCase("")) {
                editTextViewPrice.setText(mEditPropertyData.getRentAmount());
            }


            if (mEditPropertyData.getBedNos() != null && !mEditPropertyData.getBedNos().equalsIgnoreCase("")) {
                editTextViewBedsNo.setText(mEditPropertyData.getBedNos());
            }

            if (mEditPropertyData.getBathNos() != null && !mEditPropertyData.getBathNos().equalsIgnoreCase("")) {
                editTextViewBathNo.setText(mEditPropertyData.getBathNos());
            }

            if (mEditPropertyData.getAddress() != null && !mEditPropertyData.getAddress().equalsIgnoreCase("")) {
                editTextViewAddress.setText(mEditPropertyData.getAddress());
            }


            if (mEditPropertyData.getDescription() != null && !mEditPropertyData.getDescription().equalsIgnoreCase("")) {
                editTextViewAddDescription.setText(mEditPropertyData.getDescription());
            }

            if (mEditPropertyData.getArea() != null && !mEditPropertyData.getArea().equalsIgnoreCase("")) {
                editTextViewArea.setText(mEditPropertyData.getArea());
            }

            if (mEditPropertyData.getNdrs_number() != null && !mEditPropertyData.getNdrs_number().equalsIgnoreCase("")) {
                editTextViewNRDS.setText(mEditPropertyData.getNdrs_number());
            }

            if (mEditPropertyData.getPropertyListed().equalsIgnoreCase("0")) {
                radioListedButton = groupRadioListed.findViewById(R.id.radioListedNo);
                radioListedButton.setChecked(true);
            } else {
                radioListedButton = groupRadioListed.findViewById(R.id.radioListedYes);
                radioListedButton.setChecked(true);
            }


            if (mEditPropertyData.getPreForeclosure().equalsIgnoreCase("0")) {
                radioPreForeclosureButton = radioPreForeclosureGroup.findViewById(R.id.radioPreForeclosurNo);
                radioPreForeclosureButton.setChecked(true);
                //new feature
                linearPreProCloserManage.setVisibility(View.GONE);
            } else {
                radioPreForeclosureButton = radioPreForeclosureGroup.findViewById(R.id.radioPreForeclosurYes);
                radioPreForeclosureButton.setChecked(true);
                linearPreProCloserManage.setVisibility(View.VISIBLE);

                if (!mEditPropertyData.getPayoffAmt().isEmpty()) {
                    editTextViewPayOffAmount.setText(mEditPropertyData.getPayoffAmt());
                }
                if (!mEditPropertyData.getNoteBalance().isEmpty()) {
                    editTextViewNoteBalance.setText(mEditPropertyData.getNoteBalance());
                }

                if (mEditPropertyData.getIs_surrender() == 0) {
                    radioSurrenderAgreementButton = radioSurrenderAgreementGroup.findViewById(R.id.radioSurrenderAgreementNo);
                    radioSurrenderAgreementButton.setChecked(true);

                } else {
                    radioSurrenderAgreementButton = radioSurrenderAgreementGroup.findViewById(R.id.radioSurrenderAgreementYes);
                    radioSurrenderAgreementButton.setChecked(true);
                    if (!mEditPropertyData.getSurrenderAgreement().isEmpty()) {
                        editTextViewSurrenderAgreement.setText(mEditPropertyData.getSurrenderAgreement());
                    }
                }

                if (!mEditPropertyData.getFlippBiddServices().isEmpty()) {
                    editTextViewFlippBiddServices.setText(mEditPropertyData.getFlippBiddServices());
                }
                if (!mEditPropertyData.getMustCloseByDate().isEmpty()) {
                    textViewMustCloseDate.setText(mEditPropertyData.getMustCloseByDate());
                }
            }

            if (mEditPropertyData.getPropertyVacant().equalsIgnoreCase("0")) {
                radioVacatedButton = radioVacantGroup.findViewById(R.id.radioVacantNo);
                radioVacatedButton.setChecked(true);
            } else {
                radioVacatedButton = radioVacantGroup.findViewById(R.id.radioVacantYes);
                radioVacatedButton.setChecked(true);
            }

            if (mEditPropertyData.getRecUsername() != null) {
                if (!mEditPropertyData.getRecUsername().isEmpty()) {
                    ((CustomEditText) findViewById(R.id.editTextViewReceivedByName)).setText(mEditPropertyData.getRecUsername());
                    if (mEditPropertyData.getRecMobileMumber() != null) {
                        if (!mEditPropertyData.getRecMobileMumber().isEmpty()) {
                            loEtAddPropertyPhone.setText(mEditPropertyData.getRecMobileMumber());
                            mAddPropertyCode.setCountryForPhoneCode(Integer.parseInt(mEditPropertyData.getRecCountryCode()));
                            Log.e("TAG", "Code " + mEditPropertyData.getRecCountryCode());
                        }
                    }
                    if (!mEditPropertyData.getRecEmailAddress().isEmpty()) {
                        ((CustomEditText) findViewById(R.id.etEmailReceivedby)).setText(mEditPropertyData.getRecEmailAddress());
                    }
                    radioContractHolderButton = radioContractHolderGroup.findViewById(R.id.radioContractHolderNo);
                    radioContractHolderButton.setChecked(true);
                } else {
                    radioContractHolderButton = radioContractHolderGroup.findViewById(R.id.radioContractHolderYes);
                    radioContractHolderButton.setChecked(true);
                }
            } else {
                radioContractHolderButton = radioContractHolderGroup.findViewById(R.id.radioContractHolderYes);
                radioContractHolderButton.setChecked(true);
            }

//            if(mEditPropertyData.getView365()!=null && !mEditPropertyData.getView365().equalsIgnoreCase("")){
//                ((CustomEditText) findViewById(R.id.etUrlView)).setText(mEditPropertyData.getView365());
//            }


            mLocationLat = mEditPropertyData.getLat();
            mLocationLang = mEditPropertyData.getLang();

            //get id of selected state and city
            if (mStateList != null && mStateList.size() != 0) {
                for (int st = 0; st < mStateList.size(); st++) {
                    String sName = mStateList.get(st).getCommonName();
                    if (sName.equalsIgnoreCase(mEditPropertyData.getState())) {
                        mStateId = mStateList.get(st).getCommonId();
                        callEditCityApi(false);
                    }
                }
            }

            if (mEditPropertyData.getState() != null && !mEditPropertyData.getState().equalsIgnoreCase("")) {
                textViewSelectStateTitle.setText(mEditPropertyData.getState());
            }

            if (mEditPropertyData.getCity() != null && !mEditPropertyData.getCity().equalsIgnoreCase("")) {
                textViewSelectCityTitle.setText(mEditPropertyData.getCity());
            }
            hideProgressDialog();
            showProgressBar(false);
//            loProgressEdit.setVisibility(View.GONE);

            propertyImagesList(mEditPropertyData.getImages());
        }
    }


    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }


    @OnClick(R.id.image_toolbar)
    void clickBack() {
        finish(true);
    }


    //R.id.relativeLayoutDocUpload,
    @OnClick({R.id.btnUploadPost, R.id.relativePropertySelectBath
            , R.id.relativePropertySelectBeds, R.id.relativePropertySelectPropertyType
            , R.id.relativePropertySelectCity, R.id.relativePropertySelectState, R.id.relativeLayoutUploadImages
            , R.id.relativeLayoutDocUpload})
    void viewClickEvents(View view) {
        switch (view.getId()) {
            case R.id.relativePropertySelectSellerType:
//                spinnerSelectSellerType.performClick();
                break;
            case R.id.relativePropertySelectPropertyType:
                spinnerSelectPropertyType.performClick();
                break;
            case R.id.relativePropertySelectState:
                if (mStateList != null && mStateList.size() != 0) {
                    showDialogStateView();
                }
                break;
            case R.id.relativePropertySelectCity:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.relativeLayoutUploadImages:
                hideKeyboard();
                requestPemission();
                break;
            case R.id.relativeLayoutDocUpload:
                //new feature
                hideKeyboard();
                openDocumentsFile();
                break;
            case R.id.btnUploadPost:
                hideKeyboard();
                //vacated
                int isSelectedVacated = radioVacantGroup.getCheckedRadioButtonId();
                radioVacatedButton = radioVacantGroup.findViewById(isSelectedVacated);

                if (radioVacatedButton != null) {
                    if (radioVacatedButton.getText().toString().equalsIgnoreCase("Yes")) {
                        mis_vacant = "1";
                    } else {
                        mis_vacant = "0";
                    }
                }

                if (spinnerSelectBedsNo.getSelectedItemPosition() > 0) {
                    mno_of_bed = spinnerSelectBedsNo.getSelectedItem().toString();
                }

                if (spinnerSelectBathNo.getSelectedItemPosition() > 0) {
                    mno_of_bath = spinnerSelectBathNo.getSelectedItem().toString();
                }


                mhouse_title = editTextViewHouse.getText().toString().trim();
                NDRS_value = editTextViewNRDS.getText().toString().trim();
                maddress = editTextViewAddress.getText().toString().trim();
                mno_of_bed = editTextViewBedsNo.getText().toString().trim();
                mno_of_bath = editTextViewBathNo.getText().toString().trim();
                marea = editTextViewArea.getText().toString().trim();
                marea_unit = "Sq. Feet";
                mdescription = editTextViewAddDescription.getText().toString().trim();


                if (checkBoxTermsAndCondistion.isChecked()) {
                    mTremsAndCondistionIsSelected = "isChecked";
                } else {
                    mTremsAndCondistionIsSelected = "";
                }

                mValidator = Validator.getInstance();
                if (!validation()) {

                    //new condition
                    int isSelectedPerForeclosure = radioPreForeclosureGroup.getCheckedRadioButtonId();
                    radioPreForeclosureButton = radioPreForeclosureGroup.findViewById(isSelectedPerForeclosure);

                    if (radioPreForeclosureButton != null) {
                        if (radioPreForeclosureButton.getText().toString().equalsIgnoreCase("Yes")) {

                            //new feature
                            if (editTextViewPayOffAmount.getText().toString().isEmpty()) {
                                seekBarShow(Constants.ENTER_PAY_OFF_AMOUNT, Color.RED);
                                return;
                            }

                            if (editTextViewNoteBalance.getText().toString().isEmpty()) {
                                seekBarShow(Constants.ENTER_NOTE_BALANCE, Color.RED);
                                return;
                            }


                            if (editTextViewFlippBiddServices.getText().toString().isEmpty()) {
                                seekBarShow(Constants.ENTER_FLIPPBIDD_SERVICE, Color.RED);
                                return;
                            }
                            if (textViewMustCloseDate.getText().toString().isEmpty()) {
                                seekBarShow(Constants.ENTER_MUST_CLOSE_DATE, Color.RED);
                                return;
                            }
                            //end new feature

                            mis_perforeclosure = "1";
                            mPrice = "";
                            NDRS_value = "";
                            mis_listed = "";
                            //new feature
                            PayOffAmount = editTextViewPayOffAmount.getText().toString().trim();
                            NoteBalance = editTextViewNoteBalance.getText().toString().trim();
                            SurrenderAgreement = editTextViewSurrenderAgreement.getText().toString().trim();
                            FlippBiddServices = editTextViewFlippBiddServices.getText().toString().trim();
                            mustCloseDate = textViewMustCloseDate.getText().toString();
                            //end new feature

                            int isSelectedSurrenderAgreement = radioSurrenderAgreementGroup.getCheckedRadioButtonId();
                            radioSurrenderAgreementButton = radioSurrenderAgreementGroup.findViewById(isSelectedSurrenderAgreement);
                            if (radioPreForeclosureButton != null) {
                                if (radioSurrenderAgreementButton.getText().toString().equalsIgnoreCase("Yes")) {
                                    if (editTextViewSurrenderAgreement.getText().toString().isEmpty()) {
                                        seekBarShow(Constants.ENTER_SURRENDER_AGREEMENT, Color.RED);
                                        return;
                                    } else {
                                        is_Surrender = "1";
                                    }
                                } else {
                                    is_Surrender = "0";
                                }
                            }


                        } else {
                            mis_perforeclosure = "0";
                            PayOffAmount = "";
                            NoteBalance = "";
                            SurrenderAgreement = "";
                            FlippBiddServices = "";
                            mustCloseDate = "";
                            //get price
                            mPrice = editTextViewPrice.getText().toString().trim();

                            //listed
                            int isSelectedListed = groupRadioListed.getCheckedRadioButtonId();
                            radioListedButton = groupRadioListed.findViewById(isSelectedListed);
                            if (radioListedButton != null) {
                                if (radioListedButton.getText().toString().equalsIgnoreCase("Yes")) {
                                    mis_listed = "1";

                                    if (NDRS_value.equalsIgnoreCase("")) {
                                        seekBarShow("please enter MLS Agent ID", Color.RED);
                                        return;
                                    } else if (NDRS_value.length() != 9) {
                                        seekBarShow("please valid MLS Agent ID", Color.RED);
                                        return;
                                    }

                                } else {
                                    mis_listed = "0";
                                    NDRS_value = "";
                                }
                            }
                        }
                    }
                    //get code
                    int isSelectedContractView = radioContractHolderGroup.getCheckedRadioButtonId();
                    radioContractHolderButton = radioContractHolderGroup.findViewById(isSelectedContractView);

                    if (radioContractHolderButton != null) {
                        if (radioContractHolderButton.getText().toString().equalsIgnoreCase("Yes")) {
                            rec_username = "";
                            rec_country_code = "";
                            rec_mobile_number = "";
                            rec_email_address = "";
                        } else {
                            if (!((CustomEditText) findViewById(R.id.editTextViewReceivedByName)).getText().toString().isEmpty()) {
                                rec_username = ((CustomEditText) findViewById(R.id.editTextViewReceivedByName)).getText().toString().trim();
                            }
                            if (!((CustomEditText) findViewById(R.id.etEmailReceivedby)).getText().toString().isEmpty()) {
                                rec_email_address = ((CustomEditText) findViewById(R.id.etEmailReceivedby)).getText().toString().trim();
                                if (mValidator.checkEmail(rec_email_address)) {
                                    seekBarShow("please enter valid email address.", Color.RED);
                                    return;
                                }
                            }

                            if (!loEtAddPropertyPhone.getText().toString().isEmpty()) {
                                rec_country_code = mAddPropertyCode.getSelectedCountryCode();
                                rec_mobile_number = loEtAddPropertyPhone.getText().toString().trim();
                            }
                        }

                    } else {
                        rec_username = "";
                        rec_country_code = "";
                        rec_mobile_number = "";
                        rec_email_address = "";
                    }

//                    if(!((CustomEditText) findViewById(R.id.etUrlView)).getText().toString().isEmpty()){
//                        view_365 = ((CustomEditText) findViewById(R.id.etUrlView)).getText().toString();
//                    }

                    if (mLocationLat != null && !mLocationLat.equalsIgnoreCase("") ||
                            mLocationLang != null && !mLocationLang.equalsIgnoreCase("") || !maddress.equals("")) {
                        callAddPostApi();
                    } else {
                        seekBarShow("please select address from google map", Color.RED);
                        return;
                    }
                }
                break;
            case R.id.relativePropertySelectBeds:
                spinnerSelectBedsNo.performClick();
                break;
            case R.id.relativePropertySelectBath:
                spinnerSelectBathNo.performClick();
                break;
        }
    }

    private void openDocumentsFile() {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 3);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "docx", "pdf"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }

    @OnClick({R.id.tvSearchGoogle, R.id.imageViewSelectAddress})
    void viewAddressClick() {
        hideKeyboard();
//        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

        try {
//            startActivityIfNeeded(builder.build(mBaseAppCompatActivity), PLACE_PICKER_REQUEST);

// Create a new Places client instance.
            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID
                    , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.OVERLAY, fields)
                    .build(this);
            startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
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
                Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
                textViewSelectStateTitle.setText(searchableAdapter.getItem(position).getCommonName());
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
                //Log.e(TAG, "selected values--" + searchableAdapter.getItem(position));
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

    private void showGallery() {

        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                .isMultiSelect() //Set this if you want to use multi selection mode.
                .setMinimumMultiSelectCount(1) //Default: 1.
                .setMaximumMultiSelectCount(5) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                .disableOverSelectionMessage() //You can also decide not to show this over select message.\
                .build();
        multiSelectionPicker.show(getSupportFragmentManager(), "picker");
    }

    private void requestPemission() {
        // Checking if permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PostNewProperty.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 696);
            } else {
                showGallery();
            }
        } else {
            showGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_FILE_REQUEST):
                if (resultCode == Activity.RESULT_OK) {

                    //file picker code
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
                            seekBarShow("File type not Accepted please Re-Select file.", Color.RED);
                        }
                    } else {
                        imagesDocSelected.setVisibility(View.GONE);
                        btnUploadDocuments.setVisibility(View.VISIBLE);
                        imageUploadDoc.setVisibility(View.VISIBLE);
                        seekBarShow("File type not Accepted please Re-Select file.", Color.RED);

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


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 696:
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showGallery();
                } else {
                    requestPemission();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    //get Property Type List
    private void callGetPropertyType(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_PROPERTY));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                hideProgressDialog();
                if (mPropertyTypeLists != null)
                    mPropertyTypeLists.clear();

                if (response.getSuccess()) {
                    mPropertyTypeLists.addAll(response.getData());

                }
                mSpinnerPropertyTypeSelection = new CommonTypeListAdapter(mBaseAppCompatActivity, R.layout.adapter_spinner_layout, response.getData());
                spinnerSelectPropertyType.setAdapter(mSpinnerPropertyTypeSelection);

                if (!mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                    showProgressBar(true);
//                    loProgressEdit.setVisibility(View.VISIBLE);
                    //edit data set
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setEditData();
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
    private void callStateApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
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

                if (response.getSuccess()) {
                    if (response.getData() != null && response.getData().size() != 0) {
                        mStateList.addAll(response.getData());
                        textViewSelectStateTitle.setText(response.getData().get(0).getCommonName());
                        mStateId = response.getData().get(0).getCommonId();
                        if (mCityList.size() == 0) {
                            callCityApi(false);
                        }
                    }
                } else {
                    textViewSelectStateTitle.setText("");
                    mBaseAppCompatActivity.seekBarShow("Something want to wrong load state.", Color.RED);
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
            showToast(getString(R.string.no_internet));
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
                    mBaseAppCompatActivity.seekBarShow("City Not Found!", Color.RED);
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void callEditCityApi(boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
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

                    if (mCityList != null && mCityList.size() != 0) {
                        for (int st = 0; st < mCityList.size(); st++) {
                            String cName = mCityList.get(st).getCommonName();
                            if (cName.equalsIgnoreCase(mEditPropertyData.getCity())) {
                                mCityId = mCityList.get(st).getCommonId();
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

    //Success Dialog
    private void openSuccessDialog() {
        PromptDialog mPromptDialog = new PromptDialog(this);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));
        mPromptDialog.setContentText(getString(R.string.success_property));
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                afterUploadProperty();
            }
        });
        mPromptDialog.setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
                setResult(RESULT_OK);
                finish(true);
            }
        });
        mPromptDialog.show();

    }

    private void afterUploadProperty() {
        setResult(RESULT_OK);
        finish(true);
    }


    // Image pick and select Code
    private static final int PICK_FILE_REQUEST = 3;

    private File mImageThumbFile = null, mImageFile = null, mDocumentFile = null;
    private List<File> mFileUriList = new ArrayList<>();
    List<MultipartBody.Part> fileThumb = new ArrayList<>();

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


    public void callAddPostApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showToast(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        MultipartBody.Part itemDocumentFile = null;
        //old code single image parse
        if (mFileUriList != null) {
            //add
            for (int file = 0; file < mFileUriList.size(); file++) {
                if (!mFileUriList.get(file).getPath().contains("https")) {
                    fileThumb.add(prepareFilePart("property_pic[]", mFileUriList.get(file)));
                }
            }
        } else {
            //edit or Null
            fileThumb.add(prepareFilePart("property_pic[]", null));
        }


        //document file
        //new feature add
        if (mDocumentFile != null) {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mDocumentFile);
            itemDocumentFile = MultipartBody.Part.createFormData("property_document", mDocumentFile.getName(), requestFile);
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
            itemDocumentFile = MultipartBody.Part.createFormData("property_document", "", requestFile);
        }
        //end new feature


        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
            addPostRequest.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mEditPropertyData.getCommonId()));
        }

        addPostRequest.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), mPrice));
        addPostRequest.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
        addPostRequest.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
        addPostRequest.put("description", RequestBody.create(MediaType.parse("multipart/form-data"), mdescription));
        addPostRequest.put("is_vacant", RequestBody.create(MediaType.parse("multipart/form-data"), mis_vacant));
        addPostRequest.put("area_unit", RequestBody.create(MediaType.parse("multipart/form-data"), marea_unit));
        addPostRequest.put("area", RequestBody.create(MediaType.parse("multipart/form-data"), marea));
        addPostRequest.put("no_of_bath", RequestBody.create(MediaType.parse("multipart/form-data"), mno_of_bath));
        addPostRequest.put("no_of_bed", RequestBody.create(MediaType.parse("multipart/form-data"), mno_of_bed));
        addPostRequest.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), maddress));
        addPostRequest.put("house_no", RequestBody.create(MediaType.parse("multipart/form-data"), mhouse_title));
        addPostRequest.put("is_listed", RequestBody.create(MediaType.parse("multipart/form-data"), mis_listed));
        addPostRequest.put("property_type", RequestBody.create(MediaType.parse("multipart/form-data"), mproperty_type));
        addPostRequest.put("seller_type", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("pre_foreclosure", RequestBody.create(MediaType.parse("multipart/form-data"), mis_perforeclosure));
        addPostRequest.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLat));
        addPostRequest.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLang));

        addPostRequest.put("rec_username", RequestBody.create(MediaType.parse("multipart/form-data"), rec_username));
        addPostRequest.put("rec_country_code", RequestBody.create(MediaType.parse("multipart/form-data"), rec_country_code));
        addPostRequest.put("rec_mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), rec_mobile_number));
        addPostRequest.put("rec_email_address", RequestBody.create(MediaType.parse("multipart/form-data"), rec_email_address));
        addPostRequest.put("view_365", RequestBody.create(MediaType.parse("multipart/form-data"), view_365));
        addPostRequest.put("is_similar_deal", RequestBody.create(MediaType.parse("multipart/form-data"), is_similar_deal));
        addPostRequest.put("deal_id", RequestBody.create(MediaType.parse("multipart/form-data"), deal_id));
        if (!NDRS_value.equalsIgnoreCase("")) {
            addPostRequest.put("ndrs_number", RequestBody.create(MediaType.parse("multipart/form-data"), NDRS_value));
        } else {
            addPostRequest.put("ndrs_number", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }

        //new feature
        if (!PayOffAmount.equalsIgnoreCase("")) {
            addPostRequest.put("payoff_amt", RequestBody.create(MediaType.parse("multipart/form-data"), PayOffAmount));
        } else {
            addPostRequest.put("payoff_amt", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        if (!NoteBalance.equalsIgnoreCase("")) {
            addPostRequest.put("note_balance", RequestBody.create(MediaType.parse("multipart/form-data"), NoteBalance));
        } else {
            addPostRequest.put("note_balance", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        if (!SurrenderAgreement.equalsIgnoreCase("")) {
            addPostRequest.put("surrender_agreement", RequestBody.create(MediaType.parse("multipart/form-data"), SurrenderAgreement));
        } else {
            addPostRequest.put("surrender_agreement", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        if (!FlippBiddServices.equalsIgnoreCase("")) {
            addPostRequest.put("flippBidd_services", RequestBody.create(MediaType.parse("multipart/form-data"), FlippBiddServices));
        } else {
            addPostRequest.put("flippBidd_services", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        if (!mustCloseDate.equalsIgnoreCase("")) {
            addPostRequest.put("must_close_by_date", RequestBody.create(MediaType.parse("multipart/form-data"), mustCloseDate));
        } else {
            addPostRequest.put("must_close_by_date", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }
        addPostRequest.put("is_surrender", RequestBody.create(MediaType.parse("multipart/form-data"), is_Surrender));
        //end

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable;

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
            observable = userService.addNewProperty(fileThumb, itemDocumentFile, addPostRequest);
        } else {
            observable = userService.editProperty(fileThumb, itemDocumentFile, addPostRequest);
        }

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();

                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    PropertyList.isClickBack = false;
                    if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                        openSuccessDialog();
                    } else {
                        afterUploadProperty();
                    }

                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private boolean validation() {

        /*if (mValidator.isEmpty(mseller_type)) {
            seekBarShow("please select Seller Type.", Color.RED);
            return true;
        }*/

        if (mValidator.isEmpty(mproperty_type)) {
            seekBarShow(Constants.SELECT_PROPERTY_TYPE, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mhouse_title)) {
            seekBarShow(Constants.ENTER_TITLE, Color.RED);
            return true;
        } else if (mValidator.isEmpty(maddress)) {
            seekBarShow(Constants.ENTER_ADDRESS, Color.RED);
            return true;
        } else if (mValidator.isEmpty(marea)) {
            seekBarShow(Constants.ENTER_AREA, Color.RED);
            return true;
        } else if (mValidator.isEmpty(marea_unit)) {
            seekBarShow(Constants.ENTER_CODE, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mdescription)) {
            seekBarShow(Constants.ENTER_DESCRIPCTION, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mTremsAndCondistionIsSelected)) {
            seekBarShow(Constants.val_tremsandcondistion, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mno_of_bed)) {
            seekBarShow(Constants.SELECT_NUB_OF_BAD, Color.RED);
            return true;
        } else if (mValidator.isEmpty(mno_of_bath)) {
            seekBarShow(Constants.SELECT_NUB_OF_BATH, Color.RED);
            return true;
        }


//        else if (mValidator.isEmpty(mis_listed)) {
//            seekBarShow("please select Listed.", Color.RED);
//            return true;
//        }
//        else if (mValidator.isEmpty(mPrice)) {
//            seekBarShow("Enter House Price.", Color.RED);
//            return true;
//        }
//        else if (mValidator.isEmpty(mno_of_bed) || mno_of_bed.equalsIgnoreCase("N/A")) {
//            seekBarShow("Enter No Of Beds.", Color.RED);
//            return true;
//        } else if (mValidator.isEmpty(mno_of_bath) || mno_of_bath.equalsIgnoreCase("N/A")) {
//            seekBarShow("Enter No Of Baths.", Color.RED);
//            return true;
//        }

//        else if (mImageThumbFile == null) {
//            seekBarShow("Please select Image Your House.", Color.RED);
//            return true;
//        }

        return false;
    }

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
        finish(true);
        super.onBackPressed();
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


    @Override
    protected void onResume() {
        super.onResume();
    }

    /*image show*/
    private void propertyImagesList(List<CommonData.Image> imagesList) {
        LinearLayoutCompat llPropertyImages = findViewById(R.id.llPropertyImages);
        if (imagesList != null && imagesList.size() != 0) {
            llPropertyImages.setVisibility(View.VISIBLE);
            llPropertyImages.removeAllViews();
            LayoutInflater loLayoutInflator = (LayoutInflater) mBaseAppCompatActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int fe = 0; fe < imagesList.size(); fe++) {
                View loView = loLayoutInflator.inflate(R.layout.property_images_view, null);
                llPropertyImages.addView(loView);

                CommonData.Image moImage = imagesList.get(fe);
                AppCompatImageView ivProductImage = loView.findViewById(R.id.ivProductImage);
                ImageView ivClearImage = loView.findViewById(R.id.imageClear);

                Glide.with(mBaseAppCompatActivity)
                        .load(moImage.getImageUrl())
                        .apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image_icon).error(R.drawable.no_image_icon))
                        .into(ivProductImage);
                int position = fe;
                ivClearImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (moImage.getImageUrl() != null && !moImage.getImageUrl().equalsIgnoreCase("")) {
                            //call api for delete image
                            CommonDialogView.getInstance().showCommonDialog(PostNewProperty.this, "Delete",
                                    "Are you sure you want delete this image?"
                                    , "No"
                                    , "Yes"
                                    , false, false, false, false, false, new CommonDialogCallBack() {
                                        @Override
                                        public void onDialogYes(View view) {
                                            llPropertyImages.removeViewAt(position);
                                            callDeleteApi(moImage);
                                        }

                                        @Override
                                        public void onDialogCancel(View view) {
                                        }
                                    });
                        }
                    }
                });
            }
        } else {
            llPropertyImages.setVisibility(View.GONE);
        }

    }

    /*delete image*/
    private void callDeleteApi(CommonData.Image images) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("image_id", RequestBody.create(MediaType.parse("multipart/form-data"), images.getImageId()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));
        //token, image_id, type

        showProgressDialog(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.imageDelete(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {

                } else {
                    openErorrDialog(response.getText());
                }
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }
}
