package com.flippbidd.activity.requestProperty;

import android.Manifest;
import android.app.Activity;
import android.app.LauncherActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.utils.ToastUtils;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;

public class PropertyRequestActivity extends AppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener, View.OnClickListener {


    private Context mContext;
    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";
    public static final int PERMISSION_REQUEST_CODE = 1010;

    private CustomEditText editTextViewContactName, editTextViewContactPhoneNumber, editTextViewPropertyAddress, editTextViewPrice, editTextViewArea, editTextViewAddNotes;
    private CustomTextView txtUploadDocuments, editTextViewUnits, editTextViewBedsNo, editTextViewBathNo;
    private Spinner spinnerSelectUnits, spinnerSelectBedsNo, spinnerSelectBathNo;
    private RadioGroup radioVacantGroup;
    private RadioButton radioVacatedButton;
    ProgressDialog progressDialog;

    // Image pick and select Code
    private static final int PICK_FILE_REQUEST = 3;
    private static final int PLACE_PICKER_REQUEST = 1000;

    private File mImageThumbFile = null, mDocumentFile = null;
    private List<File> mFileUriList = new ArrayList<>();
    private List<File> mDocFileUriList = new ArrayList<>();
    List<MultipartBody.Part> fileThumb = new ArrayList<>();
    List<MultipartBody.Part> fileDoc = new ArrayList<>();
    private String contact_name = "", phone_number = "", address = "", price = "", units = "", beds = "", baths = "", area = "", vacant = "", notes = "";
    Disposable disposable;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        this.mContext = PropertyRequestActivity.this;
        setContentView(R.layout.activity_request_property);

        //init google place
        Places.initialize(getApplicationContext(), API_KEY);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please wait sending mail to flippbidd..");
        progressDialog.setCanceledOnTouchOutside(false);

        initView();

    }

    private void clickVenst() {

        findViewById(R.id.image_toolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.editTextViewPropertyAddress).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        findViewById(R.id.relativeLayoutUploadImages).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                //open image selection
                if (!requestPemission()) {
                    openPhotoSelection();
                }
            }
        });
        findViewById(R.id.relativeLayoutDocUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SDK_INT >= Build.VERSION_CODES.R){
                    ToastUtils.shortToast(PropertyRequestActivity.this.getResources().getString(R.string.os_message));
                    return;
                }
                //open document selection
                openDocumentsFile();
//                if (!checkPermission_()) {
//                    openDocumentsFile();
//                } else {
//                    //for android 11
//                    requestPermission();
//                }
            }
        });

        findViewById(R.id.btnRequestPropertySubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spinnerSelectBedsNo.getSelectedItemPosition() > 0) {
                    beds = spinnerSelectBedsNo.getSelectedItem().toString();
                }

                if (spinnerSelectUnits.getSelectedItemPosition() > 0) {
                    units = spinnerSelectUnits.getSelectedItem().toString();
                }

                if (spinnerSelectBathNo.getSelectedItemPosition() > 0) {
                    baths = spinnerSelectBathNo.getSelectedItem().toString();
                }

                if (!isValidation()) {

                    contact_name = editTextViewContactName.getText().toString();
                    phone_number = editTextViewContactPhoneNumber.getText().toString();
                    address = editTextViewPropertyAddress.getText().toString();
                    notes = editTextViewAddNotes.getText().toString();
                    area = editTextViewArea.getText().toString();
                    price = editTextViewPrice.getText().toString();

                    int isSelectedVacant = radioVacantGroup.getCheckedRadioButtonId();
                    radioVacatedButton = radioVacantGroup.findViewById(isSelectedVacant);

                    if (radioVacatedButton != null) {
                        if (radioVacatedButton.getText().toString().equalsIgnoreCase("Yes")) {
                            vacant = "Yes";
                        } else {
                            vacant = "No";
                        }
                    }

                    callForRequstProperty();
                }
            }
        });

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

        spinnerSelectUnits.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = spinnerSelectUnits.getSelectedItem().toString();
                editTextViewUnits.setText(item);
                //LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        findViewById(R.id.relativePropertySelectBeds).setOnClickListener(this::onClick);
        findViewById(R.id.relativePropertySelectBath).setOnClickListener(this::onClick);
        findViewById(R.id.relativePropertySelectUnit).setOnClickListener(this::onClick);
        findViewById(R.id.tvSearchGoogle).setOnClickListener(this::onClick);
        findViewById(R.id.imageViewSelectAddress).setOnClickListener(this::onClick);

    }


    void viewAddressClick() {
        hideKeyboard();
        try {
// Create a new Places client instance.
            // Set the fields to specify which types of place data to return.
            List<Place.Field> fields = Arrays.asList(Place.Field.ID
                    , Place.Field.ADDRESS, Place.Field.LAT_LNG, Place.Field.LAT_LNG);

            // Start the autocomplete intent.
            Intent intent = new Autocomplete.IntentBuilder(
                    AutocompleteActivityMode.FULLSCREEN, fields)
                    .build(this);
            startActivityIfNeeded(intent, PLACE_PICKER_REQUEST);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void callForRequstProperty() {

        if (!NetworkUtils.isInternetAvailable(mContext)) {
            seekBarShow(getString(R.string.no_internet), Color.RED);
            return;
        }

        if (progressDialog != null) {
            progressDialog.show();
        }

        //contact_name, phone_number, address, price, units, beds, baths, area, vacant, notes, property_pic[], property_document[]

        //old code single image parse
        if (mFileUriList != null) {
            //add
            for (int file = 0; file < mFileUriList.size(); file++) {
                fileThumb.add(prepareFilePart("property_pic[]", mFileUriList.get(file)));
            }
        } else {
            //edit or Null
            fileThumb.add(prepareFilePart("property_pic[]", null));
        }
        if (mDocFileUriList != null) {
            //add
            for (int file = 0; file < mDocFileUriList.size(); file++) {
                fileDoc.add(prepareFilePart("property_document[]", mDocFileUriList.get(file)));
            }
        } else {
            //edit or Null
            fileDoc.add(prepareFilePart("property_document[]", null));
        }


        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mContext).getUserDetail().getLoginId()));
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mContext).getUserDetail().getToken()));

        addPostRequest.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), price));
        addPostRequest.put("notes", RequestBody.create(MediaType.parse("multipart/form-data"), notes));
        addPostRequest.put("vacant", RequestBody.create(MediaType.parse("multipart/form-data"), vacant));
        addPostRequest.put("units", RequestBody.create(MediaType.parse("multipart/form-data"), units));
        addPostRequest.put("area", RequestBody.create(MediaType.parse("multipart/form-data"), area));
        addPostRequest.put("baths", RequestBody.create(MediaType.parse("multipart/form-data"), baths));
        addPostRequest.put("beds", RequestBody.create(MediaType.parse("multipart/form-data"), beds));
        addPostRequest.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), address));
        addPostRequest.put("contact_name", RequestBody.create(MediaType.parse("multipart/form-data"), contact_name));
        addPostRequest.put("phone_number", RequestBody.create(MediaType.parse("multipart/form-data"), phone_number));

        UserServices userService = ApiFactory.getInstance(mContext).provideService(UserServices.class);
        Observable<CommonResponse> observable;

        observable = userService.requestSubmitProperty(fileThumb, fileDoc, addPostRequest);


        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                if (progressDialog != null) {
                    progressDialog.dismiss();
                }
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    seekBarShow(response.getText(), Color.GREEN);
                    finish();
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

    private void openPhotoSelection() {
        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                .isMultiSelect() //Set this if you want to use multi selection mode.
                .setMinimumMultiSelectCount(1) //Default: 1.
                .setMaximumMultiSelectCount(3) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                .setMultiSelectTextColor(R.color.text_color_white) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                .disableOverSelectionMessage() //You can also decide not to show this over select message.\
                .build();
        multiSelectionPicker.show(getSupportFragmentManager(), "picker");
    }

    private void openDocumentsFile() {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 3);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "docx", "pdf"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void initView() {


        txtUploadDocuments = findViewById(R.id.btnUploadDocuments);
        editTextViewContactName = findViewById(R.id.editTextViewContactName);
        editTextViewContactPhoneNumber = findViewById(R.id.editTextViewContactPhoneNumber);
        editTextViewPropertyAddress = findViewById(R.id.editTextViewPropertyAddress);
        editTextViewPrice = findViewById(R.id.editTextViewPrice);
        editTextViewArea = findViewById(R.id.editTextViewArea);
        editTextViewAddNotes = findViewById(R.id.editTextViewAddNotes);


        editTextViewUnits = findViewById(R.id.editTextViewUnits);
        editTextViewBedsNo = findViewById(R.id.editTextViewBedsNo);
        editTextViewBathNo = findViewById(R.id.editTextViewBathNo);

        spinnerSelectUnits = findViewById(R.id.spinnerSelectUnits);
        spinnerSelectBedsNo = findViewById(R.id.spinnerSelectBedsNo);
        spinnerSelectBathNo = findViewById(R.id.spinnerSelectBathNo);

        radioVacantGroup = findViewById(R.id.radioVacantGroup);


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

        // Creating adapter for spinner
        ArrayAdapter<String> unitAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Constants.bedAndBathCountArray);
        // Drop down layout style - list view with radio button
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        spinnerSelectUnits.setAdapter(unitAdapter);

        clickVenst();
    }

    private boolean isValidation() {

        if (editTextViewContactName.getText().toString().isEmpty()) {
            seekBarShow(Constants.ENTER_CONTACT_NAME, Color.RED);
            return true;
        }
        if (editTextViewContactPhoneNumber.getText().toString().isEmpty()) {
            seekBarShow(Constants.ENTER_CONTACT_NUMBER, Color.RED);
            return true;
        }

        if (editTextViewContactPhoneNumber.getText().toString().length() <= 0) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        }
        if (editTextViewContactPhoneNumber.getText().toString().length() <= 9) {
            seekBarShow(Constants.vl_mobile_number_length, Color.RED);
            return true;
        }
        if (editTextViewPropertyAddress.getText().toString().isEmpty()) {
            seekBarShow(Constants.ENTER_PROPERTY_ADDRESS, Color.RED);
            return true;
        }


        /*if (editTextViewPrice.getText().toString().isEmpty()) {
            seekBarShow("Required Price", Color.RED);
            return true;
        }
        if (editTextViewArea.getText().toString().isEmpty()) {
            seekBarShow("Required Carpet Area", Color.RED);
            return true;
        }
        if (editTextViewAddNotes.getText().toString().isEmpty()) {
            seekBarShow("Required Notes", Color.RED);
            return true;
        }

        if (units.isEmpty() || units.equalsIgnoreCase("N/A")) {
            seekBarShow("Select No Of Units.", Color.RED);
            return true;
        }
        if (beds.isEmpty() || beds.equalsIgnoreCase("N/A")) {
            seekBarShow("Select No Of Beds.", Color.RED);
            return true;
        }
        if (baths.isEmpty() || baths.equalsIgnoreCase("N/A")) {
            seekBarShow("Select No Of Baths.", Color.RED);
            return true;
        }*/

        return false;
    }

    public void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    protected void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_FILE_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    if (mDocFileUriList != null) {
                        mDocFileUriList.clear();
                    }
                    //file picker code
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    String path = "";
                    for (NormalFile file : list) {
                        path = file.getPath();
                        File mFile = FileUtils.getFile(mContext, path);
                        mDocFileUriList.add(mFile);
                    }

                    //selected document count
                    if (mDocFileUriList.size() > 1) {
                        txtUploadDocuments.setText("Number of documents " + mDocFileUriList.size());
                    } else {
                        txtUploadDocuments.setText("Number of document " + mDocFileUriList.size());
                    }

                }
            }
            break;
            case (PLACE_PICKER_REQUEST): {

                if (data != null) {
                    if (resultCode == RESULT_OK) {
                        Place place = Autocomplete.getPlaceFromIntent(data);
                        //set data
                        editTextViewPropertyAddress.setText("");
                        editTextViewPropertyAddress.setText(place.getAddress());

                    } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                        // TODO: Handle the error.
                        Status status = Autocomplete.getStatusFromIntent(data);
                        Log.i("TAG", status.getStatusMessage());
                    } else if (resultCode == RESULT_CANCELED) {
                        // The user canceled the operation.
                    }
                } else {
                    seekBarShow("something want to wrong,please re-select address!", Color.RED);
                }
            }
            break;
            case (2296): {
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // perform action when allow permission success
                        openDocumentsFile();
                    } else {
//                            Toast.makeText(this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 696:
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openPhotoSelection();
                } else {
                    requestPemission();
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean requestPemission() {

        // Checking if permission is not granted
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(PropertyRequestActivity.this, new String[]{READ_EXTERNAL_STORAGE
                        , WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA}, 696);

                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /*Document*/
//    private boolean checkPermission_() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(PropertyRequestActivity.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(PropertyRequestActivity.this, WRITE_EXTERNAL_STORAGE);
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
////            ActivityCompat.requestPermissions(PropertyRequestActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//            openDocumentsFile();
//        }
//
//    }
    /*end document*/

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
                mFile = FileUtils.getFile(mContext, uriList.get(select));
                try {

                    mCompressor = new Compressor(mContext).setMaxHeight(512).setMaxWidth(412).setQuality(70).compressToFile(mFile);
                    mFileUriList.add(mCompressor);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            uri = uriList.get(0);
            mFileUriList.add(FileUtils.getFile(mContext, uri));
        }
        mImageThumbFile = mFileUriList.get(0);
        //get name file first index

        Bitmap bitmap = BitmapFactory.decodeFile(mImageThumbFile.getPath());
        ((ImageView) findViewById(R.id.imagesSelected)).setImageBitmap(bitmap);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.relativePropertySelectBeds:
                spinnerSelectBedsNo.performClick();
                break;
            case R.id.relativePropertySelectBath:
                spinnerSelectBathNo.performClick();
                break;
            case R.id.relativePropertySelectUnit:
                spinnerSelectUnits.performClick();
                break;
            case R.id.tvSearchGoogle:
                viewAddressClick();
                break;
            case R.id.imageViewSelectAddress:
                viewAddressClick();
                break;
        }

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
}
