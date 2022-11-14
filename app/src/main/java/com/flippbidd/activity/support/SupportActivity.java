package com.flippbidd.activity.support;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class SupportActivity extends BaseActivity implements BSImagePicker.OnMultiImageSelectedListener {


    private CustomEditText txtNoteFeedback;
    private List<File> mFileUriList = new ArrayList<>();
    Context moContext;
    Disposable disposable;
    File mSelectedFil1 = null, mSelectedFil2 = null, mSelectedFil3 = null;
    int maxSize = 3;
    int minSize = 1;

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
        this.moContext = SupportActivity.this;
        setContentView(R.layout.activity_suppot_layout);

        txtNoteFeedback = findViewById(R.id.txtNoteFeedback);
// using method from above
        System.out.println(getDeviceName());
        eventClick();
    }

    private void eventClick() {

        findViewById(R.id.imageSupportBackView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.imageAttached).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPhotoSelection();
            }
        });
        txtNoteFeedback.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = s.toString();
                if (str.length() > 0) {
                    ((CustomTextView) findViewById(R.id.txtSendFeed)).setEnabled(true);
                    ((CustomTextView) findViewById(R.id.txtSendFeed)).setTextColor(getResources().getColor(R.color.text_color_black));
                } else {
                    ((CustomTextView) findViewById(R.id.txtSendFeed)).setEnabled(false);
                    ((CustomTextView) findViewById(R.id.txtSendFeed)).setTextColor(getResources().getColor(R.color.color_grey));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        findViewById(R.id.txtSendFeed).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messageNote = txtNoteFeedback.getText().toString();

                if (messageNote.isEmpty()) {
                    showSnkbar("Please add note.");
                    return;
                }
                callUploadDocumentApi(messageNote);
            }
        });
    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.supportContent), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }

    private void openPhotoSelection() {
        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                .isMultiSelect() //Set this if you want to use multi selection mode.
                .setMinimumMultiSelectCount(minSize) //Default: 1.
                .setMaximumMultiSelectCount(maxSize) //Default: Integer.MAX_VALUE (i.e. User can select as many images as he/she wants)
                .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                .setMultiSelectTextColor(R.color.text_color_white) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                .disableOverSelectionMessage() //You can also decide not to show this over select message.\
                .build();
        multiSelectionPicker.show(getSupportFragmentManager(), "picker");
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
                mFile = FileUtils.getFile(this, uriList.get(select));
                try {
                    mCompressor = new Compressor(this).setMaxHeight(512).setMaxWidth(412).setQuality(70).compressToFile(mFile);
                    mFileUriList.add(mCompressor);
                    if (select == 0) {
                        mSelectedFil1 = mFileUriList.get(select);
                    }
                    if (select == 1) {
                        mSelectedFil2 = mFileUriList.get(select);
                    }
                    if (select == 2) {
                        mSelectedFil3 = mFileUriList.get(select);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            setViewImage(uriList);
        } else {
            uri = uriList.get(0);
            mFileUriList.add(FileUtils.getFile(this, uri));
            mSelectedFil1 = mFileUriList.get(0);
        }
        //mImageThumbFile = mFileUriList.get(0);
        //get name file first index

    }

    private void setViewImage(List<Uri> uriList) {

        if (uriList != null && uriList.size() != 0) {
            LinearLayout linearViewAllBidd = findViewById(R.id.linearViewAllList);
            linearViewAllBidd.setVisibility(View.VISIBLE);
            linearViewAllBidd.removeAllViews();

            LayoutInflater loLayoutInflator = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            for (int fe = 0; fe < uriList.size(); fe++) {
                if (fe == 0 | fe == 1 | fe == 2) {
                    View loView = loLayoutInflator.inflate(R.layout.item_selected_image_ui, null);
                    linearViewAllBidd.addView(loView);

                    ImageView moImageView;
                    moImageView = loView.findViewById(R.id.imageDelete);
                    Uri bidds = uriList.get(fe);
                    File mFile = FileUtils.getFile(this, bidds);
                    ((CustomTextView) loView.findViewById(R.id.txtImageName)).setText(mFile.getName());

                    int finalFe = fe;
                    moImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            uriList.remove(finalFe);
                            mFileUriList.remove(finalFe);
                            setViewImage(uriList);
                        }
                    });
                }
            }
        } else {
            LinearLayout linearViewAllBidd = findViewById(R.id.linearViewAllList);
            linearViewAllBidd.removeAllViews();
        }

    }

    private void callUploadDocumentApi(String mMessage) {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            showSnkbar(getResources().getString(R.string.connection_error));
            return;
        }
        showProgressBar(true);
        MultipartBody.Part itemSelectedFile1 = null;
        MultipartBody.Part itemSelectedFile2 = null;
        MultipartBody.Part itemSelectedFile3 = null;

        //token, login_id, msg, device_model, device_os, version_name, attachment1, attachment2, attachment3

        LinkedHashMap<String, RequestBody> uploadRequest = new LinkedHashMap<String, RequestBody>();
        uploadRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        uploadRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        uploadRequest.put("msg", RequestBody.create(MediaType.parse("multipart/form-data"), mMessage));
        uploadRequest.put("device_model", RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceName()));
        uploadRequest.put("device_os", RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceOS()));
        uploadRequest.put("version_name", RequestBody.create(MediaType.parse("multipart/form-data"), getDeviceOSVersion()));

        //check type
        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse_> observable;


        if (mSelectedFil1 != null) {
            RequestBody requestFile1 = RequestBody.create(MediaType.parse("multipart/form-data"), mSelectedFil1);
            itemSelectedFile1 = MultipartBody.Part.createFormData("attachment1", mSelectedFil1.getName(), requestFile1);
        }

        if (mSelectedFil2 != null) {
            RequestBody requestFile2 = RequestBody.create(MediaType.parse("multipart/form-data"), mSelectedFil2);
            itemSelectedFile2 = MultipartBody.Part.createFormData("attachment2", mSelectedFil2.getName(), requestFile2);
        }

        if (mSelectedFil3 != null) {
            RequestBody requestFile3 = RequestBody.create(MediaType.parse("multipart/form-data"), mSelectedFil3);
            itemSelectedFile3 = MultipartBody.Part.createFormData("attachment3", mSelectedFil3.getName(), requestFile3);
        }

        observable = userService.helMail(itemSelectedFile1, itemSelectedFile2, itemSelectedFile3, uploadRequest);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                showProgressBar(false);
                if (response.getSuccess()) {
                    CommonDialogView.getInstance().showCommonDialog(moContext, "",
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
                    CommonDialogView.getInstance().showCommonDialog(moContext, "",
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
                showProgressBar(false);
            }
        });
    }

    private void clearData() {
        txtNoteFeedback.setText("");
        mSelectedFil1 = null;
        mSelectedFil2 = null;
        mSelectedFil3 = null;
        if (mFileUriList != null) {
            mFileUriList.clear();
        }
    }

    public String getDeviceOS() {
        int version = Build.VERSION.SDK_INT;
        return "OS_" + version;
    }

    public String getDeviceOSVersion() {
        String versionRelease = Build.VERSION.RELEASE;
        return "OS_" + versionRelease;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }


    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
