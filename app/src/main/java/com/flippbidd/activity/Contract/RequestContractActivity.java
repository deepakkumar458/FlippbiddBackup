package com.flippbidd.activity.Contract;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.Bottoms.CommonListBottomSheet;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.requestProperty.PropertyRequestActivity;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.ToastUtils;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.ToastUtil;
import com.vincent.filepicker.Util;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;

public class RequestContractActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    Context moContext;
    String mCommonId = "", mScreenName = "Property";
    CommonData mCommonDetailsData;

    Disposable disposable;

    private CustomEditText edtRequestBuyerName, edtRequestOfferAmount, edtRequestDownPayment, edtRequestAttorneyName, edtRequestAttorneyFirmName, edtRequestAttorneyNumber, edtRequestFaxNumber, edtRequestEmail, edtRequestAddress, edtRequestContingencies, edtRequestFirstName,
            edtRequestLastName, edtRequestMobileNumber;
    private CustomTextView tvSelectedFileName;
    private CustomTextView tvRequestClosingTime, tvFundingType;
    private CheckBox checkRequestSelection, checkRequestSelection1;
    private Spinner spinnerFundingType, spinnerClosingFrim;
    private RadioGroup rPurchasing;


    private File mSelectedFile;
    List<String> mNormalList = new ArrayList<>();

    String closingDate = "";
    String FundingType = "";
    String selectedRbText = "";
    ProgressDialog progressDialog;

    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    // Image pick and select Code
    private static final int PICK_FILE_REQUEST = 3;
    private static final int TAKE_PICTURE = 1;
    private Uri mImageUri;
    private String mCurrentPhotoPath;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;

    public static final int PERMISSION_REQUEST_CODE = 1010;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        this.moContext = RequestContractActivity.this;
        setContentView(R.layout.activity_contract_request_layout);
        Bundle bundle = getIntent().getExtras();

        if (bundle != null) {
            mCommonId = bundle.getString("p_id");
            mScreenName = bundle.getString("p_type");

            getDetailsProperty();
        }

        initView();
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    private void initView() {
        progressDialog = new ProgressDialog(moContext);
        progressDialog.setMessage("Please wait..");
        progressDialog.setCanceledOnTouchOutside(false);

        tvSelectedFileName = findViewById(R.id.tvSelectedFileName);
        checkRequestSelection = findViewById(R.id.checkRequestSelection);
        checkRequestSelection1 = findViewById(R.id.checkRequestSelection1);
        rPurchasing = findViewById(R.id.rPurchasing);

        spinnerFundingType = findViewById(R.id.spinnerFundingType);
        tvFundingType = findViewById(R.id.tvFundingType);

        spinnerClosingFrim = findViewById(R.id.spinnerClosingTime);
        tvRequestClosingTime = findViewById(R.id.tvRequestClosingTime);


        edtRequestBuyerName = findViewById(R.id.edtRequestBuyerName);
        edtRequestOfferAmount = findViewById(R.id.edtRequestOfferAmount);
        edtRequestDownPayment = findViewById(R.id.edtRequestDownPayment);
        edtRequestAttorneyName = findViewById(R.id.edtRequestAttorneyName);
        edtRequestAttorneyFirmName = findViewById(R.id.edtRequestAttorneyFirmName);
        edtRequestAttorneyNumber = findViewById(R.id.edtRequestAttorneyNumber);
        edtRequestFaxNumber = findViewById(R.id.edtRequestFaxNumber);
        edtRequestEmail = findViewById(R.id.edtRequestEmail);
        edtRequestAddress = findViewById(R.id.edtRequestAddress);
        edtRequestContingencies = findViewById(R.id.edtRequestContingencies);
        edtRequestFirstName = findViewById(R.id.edtRequestFirstName);
        edtRequestLastName = findViewById(R.id.edtRequestLastName);
        edtRequestMobileNumber = findViewById(R.id.edtRequestMobileNumber);


        clickEvents();
    }

    private boolean validationCheck() {
        String regex = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        if (edtRequestBuyerName.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Buyer Name");
            return false;
        }

        if (edtRequestOfferAmount.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Offer amount");
            return false;
        }

        if (edtRequestDownPayment.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Down payment");
            return false;
        }

        if (spinnerFundingType.getSelectedItem().toString().equalsIgnoreCase("Select Type")) {
            ToastUtils.longToast("Please select anyone option for funding type");
            return false;
        }

        int selectedRadioButtonId = rPurchasing.getCheckedRadioButtonId();
        if (selectedRadioButtonId != -1) {
            RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
            selectedRbText = selectedRadioButton.getText().toString();
        } else {
            ToastUtils.shortToast("Please select for the AS IS");

            return false;
        }

        if (closingDate.isEmpty() || closingDate.equalsIgnoreCase("")) {
            ToastUtils.longToast("Please select anyone option for closing timeframe");
            return false;
        }
        if (FundingType.isEmpty() || FundingType.equalsIgnoreCase("")) {
            ToastUtils.longToast("Please select anyone option for funding type");
            return false;
        }

        if (edtRequestAttorneyName.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Attorney Name");
            return false;
        }
        if (edtRequestAttorneyFirmName.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Attorney Firm Name");
            return false;
        }
        if (edtRequestAttorneyNumber.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Attorney Number");
            return false;
        }
        if (edtRequestAttorneyNumber.getText().toString().length() < 9) {
            ToastUtils.longToast("Please enter Attorney Number Minimum 10-Digit");
            return false;
        }
        if (edtRequestFaxNumber.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Fax Number");
            return false;
        }
        if (edtRequestFaxNumber.getText().toString().length() < 9) {
            ToastUtils.longToast("Please enter Fax Number Minimum 10-Digit");
            /*Please enter valid 10-digit phone number*/
            return false;
        }
        if (edtRequestEmail.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Email Address");
            return false;
        } else if (!edtRequestEmail.getText().toString().matches(regex)) {
            ToastUtils.longToast("Please enter valid email address");
            return false;
        }

        if (edtRequestAddress.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Address");
            return false;
        }
        if (edtRequestContingencies.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Notes");
            return false;
        }

        if (edtRequestFirstName.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter First Name");
            return false;
        }

        if (edtRequestLastName.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Last Name");
            return false;
        }
        if (edtRequestMobileNumber.getText().toString().isEmpty()) {
            ToastUtils.longToast("Please enter Mobile Number");
            return false;
        }

        if (!checkRequestSelection.isChecked()) {
            ToastUtils.longToast(Constants.val_tremsandcondistion);
            return false;
        }
        if (!checkRequestSelection1.isChecked()) {
            ToastUtils.longToast(Constants.val_tremsandcondistion);
            return false;
        }


        return true;
    }


    private void clickEvents() {

        findViewById(R.id.btnRequestSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closingDate = tvRequestClosingTime.getText().toString();
                FundingType = tvFundingType.getText().toString();
                //check validation
                if (validationCheck()) {
                    requestForApiCall();
                }
            }
        });
        findViewById(R.id.tvFileActivityCancle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSelectedFileName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMedia();
            }
        });
        tvRequestClosingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerClosingFrim.performClick();
            }
        });

        tvFundingType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinnerFundingType.performClick();
            }
        });

        edtRequestContingencies.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                if (edtRequestContingencies.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        //set frame time
        closingTime();
        //set up
        setUpFundingType();
    }


    private void requestMedia() {
        if (ContextCompat.checkSelfPermission(moContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(moContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(RequestContractActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                            , Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.CAMERA}, PERMISSION_WRITE_EXTERNAL_STORAGE);
                }
            } else {
                showBottomView(getList());
            }
        } else {
            showBottomView(getList());
        }
    }

    private void showBottomView(List<String> list) {
        final CommonListBottomSheet commonListBottomSheet = CommonListBottomSheet.newInstance();
        commonListBottomSheet.addListener(RequestContractActivity.this, new CommonListBottomSheet.DailogListener() {
            @Override
            public void onSaveCollection(int mPosition, String actionType) {

                switch (actionType) {
                    case "Take Photo":
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
                        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                                + "/IMG_" + timeStamp + ".jpeg");
                        mCurrentPhotoPath = file.getAbsolutePath();

                        ContentValues contentValues = new ContentValues(1);
                        contentValues.put(MediaStore.Images.Media.DATA, mCurrentPhotoPath);
                        mImageUri = moContext.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                        if (Util.detectIntent(moContext, cameraIntent)) {
                            ((Activity) moContext).startActivityIfNeeded(cameraIntent, TAKE_PICTURE);
                        } else {
                            ToastUtil.getInstance(moContext).showToast(moContext.getString(com.vincent.filepicker.R.string.vw_no_photo_app));
                        }

                        break;
                    case "Document":
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            ToastUtils.shortToast(moContext.getResources().getString(R.string.os_message));
                            return;
                        }
//                        if (!checkPermission_()) {
//                            openDocumentsFile();
//                        } else {
//                            //for android 11
//                            requestPermission();
//                        }
                        openDocumentsFile();
                        break;
                    case "Choose from Gallery":
                        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                                .build();
                        multiSelectionPicker.show(getSupportFragmentManager(), "picker");

//                        Intent intent = new Intent();
//                        intent.setType("image/*");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
//                        // Always show the chooser (if there are multiple options available)
//                        startActivityIfNeeded(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);
//                        // Set this as false to maintain connection
//                        // even when an external Activity is started.
//                        SendBird.setAutoBackgroundDetection(false);
                        break;
                    case "Cancel":
                        break;
                }

                commonListBottomSheet.dismiss();
            }
        }, list);
        commonListBottomSheet.show(getSupportFragmentManager(), CommonListBottomSheet.class.getSimpleName());
    }

    /*Document*/
//    private boolean checkPermission_() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(RequestContractActivity.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(RequestContractActivity.this, WRITE_EXTERNAL_STORAGE);
//            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                startActivityIfNeeded(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityIfNeeded(intent, 2296);
//            }
//        } else {
//            //below android 11
////            ActivityCompat.requestPermissions(RequestContractActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//            openDocumentsFile();
//        }
//
//    }
    /*end document*/

    private void openDocumentsFile() {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "docx", "pdf", "xls", "xlsx"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }

    private List<String> getList() {
        if (mNormalList != null)
            mNormalList.clear();
        mNormalList.add("Choose from Gallery");
        mNormalList.add("Take Photo");
        mNormalList.add("Document");
        return mNormalList;
    }

    private void closingTime() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.closing_time, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerClosingFrim.setAdapter(adapter1);

        spinnerClosingFrim.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                tvRequestClosingTime.setText(spinnerClosingFrim.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setUpFundingType() {
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.funding_type, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinnerFundingType.setAdapter(adapter);

        spinnerFundingType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    return;
                }
                tvFundingType.setText(spinnerFundingType.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void requestForApiCall() {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }

        if (progressDialog != null) {
            progressDialog.show();
        }

        /*owner_id, property_id, login_id, full_address, buyer_name, offer_amount, down_payment, funding_type, timeframe,
        purchasing, attorney_name, attorney_firm_name, attorney_phone_number, attorney_fax_number, attorney_email_address, attorney_address,
         contingencies, first_name, last_name, mobile_number, attachment*/

        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moContext).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moContext).getUserDetail().getLoginId()));
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("full_address", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getAddress()));
        linkedHashMap.put("buyer_name", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestBuyerName.getText().toString()));
        linkedHashMap.put("offer_amount", RequestBody.create(MediaType.parse("multipart/form-data"), "$" + edtRequestOfferAmount.getText().toString()));
        linkedHashMap.put("down_payment", RequestBody.create(MediaType.parse("multipart/form-data"), "$" + edtRequestDownPayment.getText().toString()));
        linkedHashMap.put("funding_type", RequestBody.create(MediaType.parse("multipart/form-data"), spinnerFundingType.getSelectedItem().toString()));
        linkedHashMap.put("timeframe", RequestBody.create(MediaType.parse("multipart/form-data"), closingDate));
        linkedHashMap.put("purchasing", RequestBody.create(MediaType.parse("multipart/form-data"), selectedRbText));
        linkedHashMap.put("attorney_name", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestAttorneyName.getText().toString()));
        linkedHashMap.put("attorney_firm_name", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestAttorneyFirmName.getText().toString()));
        linkedHashMap.put("attorney_phone_number", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestAttorneyNumber.getText().toString()));
        linkedHashMap.put("attorney_fax_number", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestFaxNumber.getText().toString()));
        linkedHashMap.put("attorney_email_address", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestEmail.getText().toString()));
        linkedHashMap.put("attorney_address", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestAddress.getText().toString()));
        linkedHashMap.put("contingencies", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestContingencies.getText().toString()));
        linkedHashMap.put("first_name", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestFirstName.getText().toString()));
        linkedHashMap.put("last_name", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestLastName.getText().toString()));
        linkedHashMap.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), edtRequestMobileNumber.getText().toString()));
        MultipartBody.Part itemSelectedFile = null;
        if (mSelectedFile != null) {
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), mSelectedFile);
            itemSelectedFile = MultipartBody.Part.createFormData("attachment", mSelectedFile.getName(), requestFile2);
        }

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.OwnerMail(linkedHashMap, itemSelectedFile);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                if (response.getSuccess()) {
                    CommonDialogView.getInstance().showCommonDialog(RequestContractActivity.this, "",
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

                } else {
                    CommonDialogView.getInstance().showCommonDialog(RequestContractActivity.this, "",
                            response.getText()
                            , ""
                            , "Ok"
                            , false, true, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });

                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
            }
        });

    }

    private void getDetailsProperty() {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }

        //token,username,qb_id
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moContext).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moContext).getUserDetail().getLoginId()));
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));


        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<DetailsTypeRespons> observable;
        observable = userService.getDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<DetailsTypeRespons>() {
            @Override
            public void onSuccess(DetailsTypeRespons response) {

                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    mCommonDetailsData = response.getData();

                    setUpData();
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });

    }


    private void setUpData() {
        ((CustomTextView) findViewById(R.id.tvPropertyAddress)).setText(mCommonDetailsData.getAddress());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                return;
            }
            try {

                if (mSelectedFile != null)
                    tvSelectedFileName.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }

        } else if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                mSelectedFile = new File(mCurrentPhotoPath);
                if (mSelectedFile != null)
                    tvSelectedFileName.setText(mSelectedFile.getName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK) {
            //file picker code
            ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
            String path = "";
            for (NormalFile file : list) {
                path = file.getPath();
            }

            mSelectedFile = FileUtils.getFile(moContext, path);
            if (mSelectedFile != null) {
                if (Fileaccept(mSelectedFile)) {
                    //show dialog by rename file
                } else {
                    ToastUtils.shortToast("File type not Accepted please Re-Select file.");
                }
            } else {
                ToastUtils.shortToast("File type not Accepted please Re-Select file.");
            }
            if (mSelectedFile != null)
                tvSelectedFileName.setText(mSelectedFile.getName());
        } else if (requestCode == 2296 && resultCode == Activity.RESULT_OK) {
            if (SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                    openDocumentsFile();
                } else {
//                            Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public static boolean Fileaccept(File file) {
        for (String extension : Constants.cvFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_WRITE_EXTERNAL_STORAGE:
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    showBottomView(getList());
                } else {
                    requestMedia();
                }
                break;
//            case PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
//                        // perform action when allow permission success
//                        openDocumentsFile();
//                    } else {
////                        Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
//                    }
//                }
//                break;
            // other 'case' lines to check for other
            // permissions this app might request.
        }

    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        Uri uris = null;
        if (uri != null) {
            mSelectedFile = FileUtils.getFile(moContext, uri);
        } else {
            mSelectedFile = FileUtils.getFile(moContext, uri);
        }

        if (mSelectedFile != null)
            tvSelectedFileName.setText(mSelectedFile.getName());
    }
}
