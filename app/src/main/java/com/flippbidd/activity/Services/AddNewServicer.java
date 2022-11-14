package com.flippbidd.activity.Services;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.flippbidd.Fragments.Services;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Service.ServiceListData;
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
import com.flippbidd.interfaces.Consts;
import com.google.android.libraries.places.api.Places;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;
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

public class AddNewServicer extends BaseAppCompatActivity implements BSImagePicker.OnMultiImageSelectedListener, BSImagePicker.OnSingleImageSelectedListener {
    private static final String TAG = LogUtils.makeLogTag(AddNewServicer.class);

    private String API_KEY = "AIzaSyBc_J_YeSgQaawZ69wpGkvEy6L9vXNzaE8";

    @BindView(R.id.spinnerSelectServicePerHour)
    Spinner spinnerSelectServicePerHour;
    @BindView(R.id.spinnerSelectServiceType)
    Spinner spinnerSelectServiceType;
    @BindView(R.id.btnBackIcon)
    ImageView btnBackIcon;
    @BindView(R.id.textViewTitleValues)
    CustomEditText textViewTitleValues;
    @BindView(R.id.textViewSelectServiceTypeTitle)
    CustomTextView textViewSelectServiceTypeTitle;
    @BindView(R.id.textViewSelectStateTitle)
    CustomTextView textViewSelectStateTitle;
    @BindView(R.id.textViewSelectCityTitle)
    CustomTextView textViewSelectCityTitle;
    @BindView(R.id.editTextServicePrice)
    CustomEditText editTextServicePrice;
    @BindView(R.id.textViewBusinessYearValues)
    CustomEditText textViewBusinessYearValues;

    @BindView(R.id.tvSearchGoogle)
    CustomTextView tvSearchGoogle;

    @BindView(R.id.textViewSelectServicePerHoursTitle)
    CustomTextView textViewSelectServicePerHoursTitle;
    @BindView(R.id.groupServiceRadioListed)
    RadioGroup groupServiceRadioListed;
    @BindView(R.id.editTextViewAddServiceDescription)
    CustomEditText editTextViewAddServiceDescription;
    @BindView(R.id.checkBoxServiceTermsAndCondistion)
    CheckBox checkBoxServiceTermsAndCondistion;
    @BindView(R.id.btnServiceUploadPost)
    CustomAppCompatButton btnServiceUploadPost;

    @BindView(R.id.relativeServicerSelectServiceType)
    RelativeLayout relativeServicerSelectServiceType;
    @BindView(R.id.relativeServiceSelectCity)
    RelativeLayout relativeServiceSelectCity;
    @BindView(R.id.relativeServiceSelectState)
    RelativeLayout relativeServiceSelectState;
    @BindView(R.id.relativeServiceSelectPerHours)
    RelativeLayout relativeServiceSelectPerHours;
    @BindView(R.id.radioServiceListedYes)
    RadioButton radioServiceListedYes;
    @BindView(R.id.radioServiceListedNo)
    RadioButton radioServiceListedNo;
    @BindView(R.id.textViewHearTitle)
    CustomTextView textViewHearTitle;
    @BindView(R.id.editTextViewServiceAddress)
    CustomEditText editTextViewServiceAddress;


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
    @BindView(R.id.textViewServiceTermsAndCondistion)
    CustomTextView textViewServiceTermsAndCondistion;

    @BindView(R.id.relativeLayoutUploadImages)
    RelativeLayout relativeLayoutUploadImages;
    @BindView(R.id.relativeLayoutDocUpload)
    RelativeLayout relativeLayoutDocUpload;



    CommonTypeListAdapter mSpinnerServiceTypeAdapter;
    List<CommonListData> mServiceTypList = new ArrayList<>();
    private String business_year, mStateId, price_on, mCityId, mServiceId, mServiceTitle, mPrice, mDescripstion, isLiseted, mTremsAndCondistionIsSelected, mLocationLat, mLocationLang, service_address;
    private RadioButton radioListedButton;
    Disposable disposable;
    List<CommonListData> mStateList = new ArrayList<>();
    List<CommonListData> mCityList = new ArrayList<>();
    Validator mValidator;
    ServiceListData mEditServiceListData;
    String mScreenType;


    // Image pick and select Code
    private static final int PICK_FILE_REQUEST = 2;

    private File mImageThumbFile = null, mImageFile = null, mDocumentFile = null;
    private List<File> mFileUriList = new ArrayList<>();
    List<MultipartBody.Part> fileThumb = new ArrayList<>();

    private int mRequestCode;
    private int mResultCode;
    private Intent mData;

    private static final int PLACE_PICKER_REQUEST = 1000;

    public static boolean Fileaccept(File file) {
        for (String extension : Constants.cvFileExtensions) {
            if (file.getName().toLowerCase().endsWith(extension)) {
                return true;
            }
        }
        return false;
    }


    public static Intent getIntentActivity(Context mContext, ServiceListData mServiceListData, String mScreenType) {
        Intent mIntent = new Intent(mContext, AddNewServicer.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mServiceListData);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mScreenType);
        return mIntent;
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_add_new_servicer_layout;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Places.initialize(getApplicationContext(), API_KEY);
        callServiceType(true);

        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mScreenType = mBundle.getString(Constants.EXTRA.SCREEN_TYPE);
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
                //Edit
                textViewHearTitle.setText(getResources().getString(R.string.String_title_of_edit_Servicer));
                mEditServiceListData = (ServiceListData) mBundle.getSerializable(Constants.EXTRA.DATA);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        setEditData();
                    }
                }, 4000);
            } else {
                textViewHearTitle.setText(getResources().getString(R.string.String_title_of_post_Servicer));
            }
        }

        spinnerSelectServiceType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
                textViewSelectServiceTypeTitle.setText(item);
                if (mServiceTypList != null && mServiceTypList.size() != 0) {
                    mServiceId = mServiceTypList.get(i).getCommonId();
                }

                LogUtils.LOGD(TAG, " response = [" + item.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        spinnerSelectServicePerHour.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                price_on = spinnerSelectServicePerHour.getSelectedItem().toString();
                textViewSelectServicePerHoursTitle.setText(price_on);

                LogUtils.LOGD(TAG, " response = [" + price_on.toString() + "]");

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        textViewServiceTermsAndCondistion.setText(HtmlCompat.fromHtml(getResources().getString(R.string.string_trms), HtmlCompat.FROM_HTML_MODE_LEGACY));

    }

    private void setEditData() {
        if (mEditServiceListData != null) {
            textViewTitleValues.setText(mEditServiceListData.getTitle());
            textViewSelectServiceTypeTitle.setText(mEditServiceListData.getServiceType());
            textViewSelectStateTitle.setText(mEditServiceListData.getState());
            textViewSelectCityTitle.setText(mEditServiceListData.getCity());
            editTextServicePrice.setText(mEditServiceListData.getServicePrice());
            textViewSelectServicePerHoursTitle.setText(mEditServiceListData.getPriceOn());
            editTextViewAddServiceDescription.setText(mEditServiceListData.getDescription());
            textViewBusinessYearValues.setText(mEditServiceListData.getBusinessYear());
            editTextViewServiceAddress.setText(mEditServiceListData.getAddress());

            if (mEditServiceListData.getServiceListed().equalsIgnoreCase("0")) {
                radioServiceListedNo.setChecked(true);
            } else {
                radioServiceListedYes.setChecked(true);
            }
            mTremsAndCondistionIsSelected = "isChecked";
            checkBoxServiceTermsAndCondistion.setChecked(true);

            mLocationLat = mEditServiceListData.getLat();
            mLocationLang = mEditServiceListData.getLang();
        }
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnTouch({R.id.relativeServiceSelectPerHours, R.id.relativeServiceSelectState, R.id.relativeServiceSelectCity, R.id.relativeServicerSelectServiceType, R.id.btnServiceUploadPost
            , R.id.textViewServiceTermsAndCondistion})
    boolean onTouchView() {
        hideKeyboard();
        return false;
    }

    @OnClick({R.id.relativeLayoutUploadImages, R.id.relativeLayoutDocUpload, R.id.btnBackIcon, R.id.relativeServicerSelectServiceType, R.id.relativeServiceSelectState, R.id.relativeServiceSelectCity, R.id.btnServiceUploadPost, R.id.relativeServiceSelectPerHours
            , R.id.textViewServiceTermsAndCondistion})
    void viewClickEvent(View view) {
        switch (view.getId()) {
            case R.id.btnBackIcon:
                finish(false);
                break;
            case R.id.relativeServicerSelectServiceType:
                spinnerSelectServiceType.performClick();
                break;
            case R.id.relativeServiceSelectState:
                if (mStateList != null && mStateList.size() != 0) {
                    showDialogStateView();
                }
                break;
            case R.id.relativeServiceSelectCity:
                if (mCityList != null && mCityList.size() != 0) {
                    showDialogCityView();
                }
                break;
            case R.id.relativeLayoutUploadImages:
                hideKeyboard();
                openBottomSheetView();
                break;
            case R.id.relativeLayoutDocUpload:
                hideKeyboard();
                openDocumentsFile();
                break;
            case R.id.btnServiceUploadPost:
                hideKeyboard();
                //listed
                int isSelectedListed = groupServiceRadioListed.getCheckedRadioButtonId();
                radioListedButton = groupServiceRadioListed.findViewById(isSelectedListed);
                if (radioListedButton.getText().toString().equalsIgnoreCase("Yes")) {
                    isLiseted = "1";
                } else {
                    isLiseted = "0";
                }
                mServiceTitle = textViewTitleValues.getText().toString();
                mDescripstion = editTextViewAddServiceDescription.getText().toString();
                mPrice = editTextServicePrice.getText().toString();
                business_year = textViewBusinessYearValues.getText().toString();
                service_address = editTextViewServiceAddress.getText().toString();

                if (business_year.equalsIgnoreCase("")) {
                    business_year = "0";
                }

                if (checkBoxServiceTermsAndCondistion.isChecked()) {
                    mTremsAndCondistionIsSelected = "isChecked";

                } else {
                    mTremsAndCondistionIsSelected = "";
                }

                mValidator = Validator.getInstance();
                if (!validation()) {

                    if (mLocationLat != null && !mLocationLat.equalsIgnoreCase("") ||
                            mLocationLang != null && !mLocationLang.equalsIgnoreCase("")) {
                        callAddServicePostApi();
                    } else {
                        seekBarShow("please select address from google map", Color.RED);
                        return;
                    }

                }
                break;
            case R.id.relativeServiceSelectPerHours:
                spinnerSelectServicePerHour.performClick();
                break;
            case R.id.textViewServiceTermsAndCondistion:
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, Constants.CONDITION_, Constants.SELECTION_HEADER_TITLE.TERMS_AND_CONDITION), true);
                break;
        }
    }

    @OnClick({R.id.tvSearchGoogle,R.id.imageViewSelectAddress})
    void viewAddressClick() {
        hideKeyboard();

       /* PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityIfNeeded(builder.build(mBaseAppCompatActivity), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }*/
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
        commonListBottomSheet.showNow(getSupportFragmentManager(), AddNewServicer.class.getSimpleName());
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

    private boolean validation() {

        if (mValidator.isEmpty(mServiceId)) {
            seekBarShow("please select Service Type.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mStateId)) {
            seekBarShow("Please Select State.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mCityId)) {
            seekBarShow("Please Select City.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(isLiseted)) {
            seekBarShow("Please Select Listed.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mServiceTitle)) {
            seekBarShow("Enter Service Title.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mPrice)) {
            seekBarShow("Enter Service Price.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(service_address)) {
            seekBarShow("Please select/add address.", Color.RED);
            return true;
        } else if (mValidator.isEmpty(mTremsAndCondistionIsSelected)) {
            seekBarShow(Constants.val_tremsandcondistion, Color.RED);
            return true;
        }

        return false;
    }

    public void callAddServicePostApi() {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

//        token,login_id,title,service_type,state_id,city_id,price,price_on,description,is_listed,service_image

        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        addPostRequest.put("title", RequestBody.create(MediaType.parse("multipart/form-data"), mServiceTitle));
        addPostRequest.put("service_type", RequestBody.create(MediaType.parse("multipart/form-data"), mServiceId));
        addPostRequest.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), mStateId));
        addPostRequest.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCityId));
        addPostRequest.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), mPrice));
        addPostRequest.put("price_on", RequestBody.create(MediaType.parse("multipart/form-data"), price_on));
        addPostRequest.put("description", RequestBody.create(MediaType.parse("multipart/form-data"), mDescripstion));
        addPostRequest.put("is_listed", RequestBody.create(MediaType.parse("multipart/form-data"), isLiseted));
        addPostRequest.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("service_address", RequestBody.create(MediaType.parse("multipart/form-data"), service_address));
        addPostRequest.put("lat", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLat));
        addPostRequest.put("lang", RequestBody.create(MediaType.parse("multipart/form-data"), mLocationLang));

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
            addPostRequest.put("service_id", RequestBody.create(MediaType.parse("multipart/form-data"), mEditServiceListData.getServiceId()));
            addPostRequest.put("business_year", RequestBody.create(MediaType.parse("multipart/form-data"), business_year));
        } else {
            addPostRequest.put("business_year", RequestBody.create(MediaType.parse("multipart/form-data"), business_year));
        }

        //token,login_id,title,service_type,state_id,city_id,price,price_on,description,is_listed,service_image,service_document,qb_id

        //old code single image parse
        MultipartBody.Part itemDocumentFile = null;
        if (mFileUriList != null) {
            //add
            for (int file = 0; file < mFileUriList.size(); file++) {
                fileThumb.add(prepareFilePart("service_image[]", mFileUriList.get(file)));
            }
        } else {
            //edit or Null
            fileThumb.add(prepareFilePart("service_image[]", null));
        }

        if (mDocumentFile != null) {
            //add
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mDocumentFile);
            itemDocumentFile = MultipartBody.Part.createFormData("service_document", mDocumentFile.getName(), requestFile);
        } else {
            //edit
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.ADD)) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                itemDocumentFile = MultipartBody.Part.createFormData("service_document", "", requestFile);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                itemDocumentFile = MultipartBody.Part.createFormData("service_document", "", requestFile);

            }

        }


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<AddResponse> observable;

        if (mScreenType.equalsIgnoreCase(Constants.SCREEN_ACTION_TYPE.EDIT)) {
            observable = userService.editNewService(fileThumb, itemDocumentFile, addPostRequest);
        } else {
            observable = userService.addNewService(fileThumb, itemDocumentFile, addPostRequest);
        }


        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<AddResponse>() {
            @Override
            public void onSuccess(AddResponse response) {
                hideProgressDialog();
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    Services.isClickBack = false;
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

  /*  //    //chat login with Qb
    private void singToQB(final int id) {
        QBUser qbUser = new QBUser(getAppendString(String.valueOf(id)), Consts.userPassword);
        qbUser.setFullName(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName());

        QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser qbUser, Bundle bundle) {
                hideProgressDialog();
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

        String mString = Id + "_" + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId() + "_service";
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
        addQBID.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "service"));
        addQBID.put("sender_qb_id", RequestBody.create(MediaType.parse("multipart/form-data"), String.valueOf(qbId)));

        //token,common_id,type,sender_qb_id
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.updateSenderQbId(addQBID);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    // SubscribeService.subscribeToPushes(mBaseAppCompatActivity, true);
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
        mPromptDialog.setContentText(getString(R.string.string_succes_service));
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

        PromptDialog mPromptDialog = new PromptDialog(this);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_success));
        mPromptDialog.setContentText(getString(R.string.string_edit_service));
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


    private void openDocumentsFile() {
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

                   /* Place place = PlacePicker.getPlace(data, this);
                    if (place != null) {
                        StringBuilder stBuilder = new StringBuilder();
                        String placename = String.format("%s", place.getName());
                        mLocationLat = String.valueOf(place.getLatLng().latitude);
                        mLocationLang = String.valueOf(place.getLatLng().longitude);
                        String address = String.format("%s", place.getAddress());

                        if (address != null && !address.equalsIgnoreCase("")) {
                            stBuilder.append(address);
                        } else {
                            stBuilder.append(placename);
                        }

                        editTextViewServiceAddress.setText("");
                        editTextViewServiceAddress.setText(stBuilder.toString());
                        editTextViewServiceAddress.setEnabled(true);

                    }*/
                }else {
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
            case PICK_FILE_REQUEST: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openDocumentsFile();
                } else {
                    Toast.makeText(AddNewServicer.this, "Approve permissions to open Documents File Picker", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }


    //Get State
    private void callServiceType(final boolean isProgress) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), Constants.SELECTION_REQUEST_TYPE.TYPE_SERVICE));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonTypeResponse> observable = userService.getListdata(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonTypeResponse>() {
            @Override
            public void onSuccess(CommonTypeResponse response) {
                if (mServiceTypList != null)
                    mServiceTypList.clear();

                hideProgressDialog();

                if (response.getSuccess()) {
                    mSpinnerServiceTypeAdapter = new CommonTypeListAdapter(mBaseAppCompatActivity,
                            R.layout.adapter_spinner_layout, response.getData());
                    spinnerSelectServiceType.setAdapter(mSpinnerServiceTypeAdapter);
                    if (response.getData() != null && response.getData().size() != 0)
                        mServiceTypList.addAll(response.getData());
                }

                callStateApi(isProgress);
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

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

                hideProgressDialog();

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


                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
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
                    mCompressor = new Compressor(mBaseAppCompatActivity).compressToFile(mFile);
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
//                mCompressor = new Compressor(mBaseAppCompatActivity).compressToFile(mFile);
                mCompressor = new Compressor(mBaseAppCompatActivity).setMaxHeight(512).setMaxWidth(412).setQuality(70).compressToFile(mFile);
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
}


