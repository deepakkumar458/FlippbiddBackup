package com.flippbidd.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.LoginResponse;
import com.flippbidd.Model.Response.UserDetails;
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
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;
import static com.vincent.filepicker.activity.BaseActivity.IS_NEED_FOLDER_LIST;

public class ProfileEditActivity extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener {

    Activity moActivity;
    CircleImageView moIvUserProfile;
    ImageView image_toolbar;
    RelativeLayout moRlChangeProfile, moRelativeUpdateProfile, relativeUpdatePOF;
    CustomAppCompatButton moBtnMyProfileUpdate;
    CustomEditText moTextViewEditFullName, moTextViewEditMobile;
    CountryCodePicker moEditPhoneCode;
    UserDetails mUserDetails;
    ImageLoader mImageLoader;
    Disposable disposable;

    Validator mValidator;
    private String mMobileNumber = "", mPhoneCode = "", mFullName = "";
    ProgressDialog mPromptDialog;

    private static final int PICK_FILE_REQUEST = 3;
    private File mImageThumbFile = null, mDocumentFile = null;


    public static final int PERMISSION_REQUEST_CODE = 1010;

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
        setContentView(R.layout.activity_profile_edit_layout);
        this.moActivity = ProfileEditActivity.this;
        mUserDetails = UserPreference.getInstance(ProfileEditActivity.this).getUserDetail();
        mImageLoader = ImageLoader.getInstance();

        //init view id
        moEditPhoneCode = findViewById(R.id.editPhoneCode);
        moIvUserProfile = findViewById(R.id.ivUserProfile);
        moRlChangeProfile = findViewById(R.id.rlChangeProfile);
        moRelativeUpdateProfile = findViewById(R.id.relativeUpdateProfile);
        relativeUpdatePOF = findViewById(R.id.relativeUpdatePOF);
        moTextViewEditMobile = findViewById(R.id.textViewEditMobile);
        moTextViewEditFullName = findViewById(R.id.textViewEditFullName);
        moBtnMyProfileUpdate = findViewById(R.id.btnMyProfileUpdate);
        image_toolbar = findViewById(R.id.image_toolbar);

        image_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //set data form store
        setUserData(mUserDetails);
//        ((CustomAppCompatButton) findViewById(R.id.btnPOFUpdate)).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                requestPemission();
//            }
//        });

        moRlChangeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }
        });

        //click event
        moBtnMyProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFullName = moTextViewEditFullName.getText().toString();
                mMobileNumber = moTextViewEditMobile.getText().toString();
                mPhoneCode = moEditPhoneCode.getSelectedCountryCode();

                UserPreference.getInstance(moActivity).setCCode(moEditPhoneCode.getSelectedCountryCode());

                mValidator = Validator.getInstance();
                if (mValidator.isEmpty(mFullName)) {
                    seekBarShow(Constants.vl_fullname, Color.RED);
                    return;
                } else if (mValidator.isEmpty(mMobileNumber)) {
                    seekBarShow(Constants.vl_mobile_number, Color.RED);
                    return;
                } else if (mMobileNumber.length() <= 0) {
                    seekBarShow(Constants.vl_mobile_number_length, Color.RED);
                    return;
                } else if (mMobileNumber.length() <= 9) {
                    seekBarShow(Constants.vl_mobile_number_length, Color.RED);
                    return;
                }

                callEditProfile();
            }
        });
    }

    private String getCCode(String ccCode) {
        return ccCode.substring(1);
    }

    private void requestPemission() {

        // Checking if permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ProfileEditActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA}, 696);
            } else {
                openDocumentsFile();
            }
        } else {
            openDocumentsFile();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 696:
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openDocumentsFile();
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

    /*Document*/
//    private boolean checkPermission_() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(ProfileEditActivity.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(ProfileEditActivity.this, WRITE_EXTERNAL_STORAGE);
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
//            openDocumentsFile();
//            //ActivityCompat.requestPermissions(ProfileEditActivity.this, new String[]{WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
//        }
//    }
//    /*end document*/

    private void openDocumentsFile() {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "pdf"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(moActivity.findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    private void setUserData(UserDetails details) {
        try {
            if (details != null) {

                if (details.getFullName() != null && !details.getFullName().equalsIgnoreCase("")) {
                    moTextViewEditFullName.setText(details.getFullName());
                }

                if (details.getMobileNumber() != null && !details.getMobileNumber().equalsIgnoreCase("")) {
                    moTextViewEditMobile.setText(details.getMobileNumber());
                } else {
                    if (UserPreference.getInstance(moActivity).getMobileNumber() != null &&
                            !UserPreference.getInstance(moActivity).getMobileNumber().equalsIgnoreCase("")) {
                        moTextViewEditMobile.setText(UserPreference.getInstance(moActivity).getMobileNumber());
                    }
                }

                if (details.getProfilePic() != null && !details.getProfilePic().equalsIgnoreCase("")) {
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                            .cacheInMemory(true)
                            .cacheOnDisk(true)
                            .bitmapConfig(Bitmap.Config.ARGB_4444)
                            .build();
                    mImageLoader.displayImage(details.getProfilePic(), moIvUserProfile, options);
                } else {
                    moIvUserProfile.setImageResource(R.mipmap.user);
                }
                String code = "";
                if (UserPreference.getInstance(moActivity).getCCode().equalsIgnoreCase("0")) {
                    if (mUserDetails.getCountryCode() != null && !mUserDetails.getCountryCode().equalsIgnoreCase("")) {
                        code = getCCode(mUserDetails.getCountryCode());
                    }
                } else {
                    code = UserPreference.getInstance(moActivity).getCCode();
                }
                moEditPhoneCode.setCountryForPhoneCode(Integer.parseInt(code));
                moEditPhoneCode.setCcpDialogShowFlag(false);
                moEditPhoneCode.setCcpDialogShowNameCode(false);
                moEditPhoneCode.setArrowColor(R.color.color_black);
                moEditPhoneCode.setContentColor(R.color.color_black);

                moEditPhoneCode.setTextSize(40);
            }
        } catch (Exception e) {

        }
    }

   /* @OnClick({R.id.rlChangeProfile, R.id.tvSelectProfile})
    void viewOnClickEvent(View view) {
        switch (view.getId()) {
            case R.id.rlChangeProfile:

            case R.id.tvSelectProfile:
                BSImagePicker pickerDialog1 = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                        .build();
                pickerDialog1.show(getSupportFragmentManager(), "picker");
                break;
        }
    }*/

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

    public void callEditProfile() {
        if (!NetworkUtils.isInternetAvailable(moActivity)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }

        mPromptDialog = new ProgressDialog(moActivity);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setMessage("Loading..");
        mPromptDialog.show();

        MultipartBody.Part itemImageFile, itemDocumentFile = null;
        if (mImageThumbFile != null) {
            //add
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mImageThumbFile);
            itemImageFile = MultipartBody.Part.createFormData("profile_pic", mImageThumbFile.getName(), requestFile);
        } else {
            if (mUserDetails.getProfilePic() != null && !mUserDetails.getProfilePic().equalsIgnoreCase("")) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mUserDetails.getProfilePic());
                itemImageFile = MultipartBody.Part.createFormData("profile_pic", "", requestFile);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                itemImageFile = MultipartBody.Part.createFormData("profile_pic", "", requestFile);
            }
        }

        if (mDocumentFile != null) {
            //add
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mDocumentFile);
            itemDocumentFile = MultipartBody.Part.createFormData("pof_doc", mDocumentFile.getName(), requestFile);
        } else {
            if (mUserDetails.getProfilePic() != null && !mUserDetails.getProfilePic().equalsIgnoreCase("")) {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mUserDetails.getPofDoc());
                itemDocumentFile = MultipartBody.Part.createFormData("pof_doc", "", requestFile);
            } else {
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                itemDocumentFile = MultipartBody.Part.createFormData("pof_doc", "", requestFile);
            }
        }
        LinkedHashMap<String, RequestBody> addPostRequest = new LinkedHashMap<String, RequestBody>();
        addPostRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moActivity).getUserDetail().getToken()));
        addPostRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(moActivity).getUserDetail().getLoginId()));

        if (mUserDetails.getEmailAddress() != null && !mUserDetails.getEmailAddress().equalsIgnoreCase("")) {
            addPostRequest.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), mUserDetails.getEmailAddress()));
        } else {
            addPostRequest.put("email", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        }

        addPostRequest.put("full_name", RequestBody.create(MediaType.parse("multipart/form-data"), mFullName));
        addPostRequest.put("username", RequestBody.create(MediaType.parse("multipart/form-data"), mUserDetails.getUsername()));
        addPostRequest.put("profession", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("state_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("city_id", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("address", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("about_me", RequestBody.create(MediaType.parse("multipart/form-data"), ""));
        addPostRequest.put("mobile_number", RequestBody.create(MediaType.parse("multipart/form-data"), mMobileNumber));
        addPostRequest.put("country_code", RequestBody.create(MediaType.parse("multipart/form-data"), "+" + mPhoneCode));

        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<LoginResponse> observable;
        observable = userService.editProfile(itemImageFile, itemDocumentFile, addPostRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse response) {
                if (mPromptDialog != null) {
                    mPromptDialog.dismiss();
                }
                LogUtils.LOGD("", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    UserPreference.getInstance(moActivity).setUserDetail(response.getData());
                    successProfile(response.getText());
                    setUserData(response.getData());

                } else {
                    openErorrDialog(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (mPromptDialog != null) {
                    mPromptDialog.dismiss();
                }                //hideProgressDialog();
            }
        });
    }

    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(moActivity);
        mPromptDialog.setSingleButton(true);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(getString(R.string.string_error));
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }

    private void successProfile(String text) {
        new PromptDialog(moActivity)
                .setSingleButton(true)
                .setDialogType(PromptDialog.DIALOG_TYPE_SUCCESS)
                .setAnimationEnable(true)
                .setTitleText(getString(R.string.string_success))
                .setContentText(text)
                .setPositiveListener(getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
                    @Override
                    public void onClick(PromptDialog dialog) {
                        dialog.dismiss();
                        setResult(RESULT_OK);
                        finish();
                    }
                }).show();
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        try {
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(uri).setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(4, 4)
                    .start(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = null;
                try {
                    String path = resultUri.toString();
                    file = new File(new URI(path));
                    mImageThumbFile = file;
                    String filePath = mImageThumbFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    moIvUserProfile.setImageBitmap(bitmap);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == PICK_FILE_REQUEST) {

            if (resultCode == Activity.RESULT_OK) {
                ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                String path = "";
                for (NormalFile file : list) {
                    path = file.getPath();
                }

                mDocumentFile = FileUtils.getFile(moActivity, path);
                if (mDocumentFile != null) {
                    if (Fileaccept(mDocumentFile)) {
                        //upload POF
                        ((CustomAppCompatButton) findViewById(R.id.btnPOFUpdate)).setText(mDocumentFile.getName());
                    } else {
                        seekBarShow("File type not Accepted please Re-Select file.", Color.RED);
                    }
                } else {
                    seekBarShow("File type not Accepted please Re-Select file.", Color.RED);

                }
            }
            //file picker code
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


}
