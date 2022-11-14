package com.flippbidd.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.asksira.bsimagepicker.BSImagePicker;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.flippbidd.Adapter.UploadList.UploadDocumentAdapterList;
import com.flippbidd.Bottoms.MoreBottomSheetDialogFragment;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.RealPathUtil;
import com.flippbidd.R;
import com.flippbidd.activity.DataRequest.RequestDataActivity;
import com.flippbidd.activity.DataRequest.RequestDocumentList;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.FileMessage;
import com.stfalcon.frescoimageviewer.ImageViewer;
import com.vincent.filepicker.Constant;
import com.vincent.filepicker.activity.NormalFilePickActivity;
import com.vincent.filepicker.filter.entity.NormalFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.refactor.lib.colordialog.ColorDialog;
import cn.refactor.lib.colordialog.PromptDialog;
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

public class UploadFiles_Activity_List extends AppCompatActivity implements BSImagePicker.OnSingleImageSelectedListener, UploadDocumentAdapterList.onItemClickLisnear {

    private static final String TAG = UploadFiles_Activity_List.class.getSimpleName();

    private static final int REQUEST_DATA_CODE = 1003;
    private CustomAppCompatButton btnRequestData;
    private RelativeLayout moRelativeNoDataView;
//    private CustomTextView moTvFileActivityCancle;
    private SwipeRefreshLayout swipe_layout_upload_files__list;
    private ImageView moIvFileActivityAdd,moIvFileActivityCancel;
    private Context moContext;
    private ProgressBar mProgressBar;//change progress
    private RecyclerView recycler_upload_files_list;
    RecyclerView.LayoutManager layoutManager;
    UploadDocumentAdapterList moUploadDocumentAdapterList;
    List<JsonElement> moJsonArrayList = new ArrayList<JsonElement>();
    boolean isDeleteShow = false;


    private String mUserId = "", mCommonType = "", mCommonId = "", mfileType = "";

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
        this.moContext = UploadFiles_Activity_List.this;
        setContentView(R.layout.activity_upload_files_layout);

        //get Bundle
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCommonType = mBundle.getString("post_type");
            mCommonId = mBundle.getString("post_id");
            mUserId = mBundle.getString("user_id");
        }

        //init variable
        btnRequestData = findViewById(R.id.btnRequestData);
        recycler_upload_files_list = findViewById(R.id.recycler_upload_files_list);
        moIvFileActivityCancel = findViewById(R.id.ivFileActivityCancel);
        moIvFileActivityAdd = findViewById(R.id.ivFileActivityAdd);
        mProgressBar = findViewById(R.id.progressBarView);
        moRelativeNoDataView = findViewById(R.id.relativeNoDataView);
        swipe_layout_upload_files__list = findViewById(R.id.swipe_layout_upload_files__list);

        //check user id and login id
        if (mUserId.equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
            isDeleteShow = true;
            moIvFileActivityAdd.setVisibility(View.VISIBLE);
            btnRequestData.setText(getResources().getString(R.string.view_request_data));
        } else {
            isDeleteShow = false;
            moIvFileActivityAdd.setVisibility(View.INVISIBLE);
            btnRequestData.setText(getResources().getString(R.string.request_data));
        }

        //init layout
        moUploadDocumentAdapterList = new UploadDocumentAdapterList(moContext, moJsonArrayList, isDeleteShow);
        layoutManager = new LinearLayoutManager(moContext);
        recycler_upload_files_list.setLayoutManager(layoutManager);
        recycler_upload_files_list.setAdapter(moUploadDocumentAdapterList);

        moUploadDocumentAdapterList.setItemOnClickEvent(this::onClickEvent);

        swipe_layout_upload_files__list.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe_layout_upload_files__list.setRefreshing(false);
                callGetAllDocumentList();
            }
        });
        //click event init
        iniClickView();
    }


    private void iniClickView() {

        moIvFileActivityCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        moIvFileActivityAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestPemission();
            }
        });

        btnRequestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnRequestData.getText().toString().equalsIgnoreCase(getResources().getString(R.string.view_request_data))) {
                    //view data
                    startActivityIfNeeded(new Intent(UploadFiles_Activity_List.this, RequestDocumentList.class)
                            .putExtra(RequestDocumentList.DATA_ID, mCommonId), REQUEST_DATA_CODE);

                } else {
                    startActivityIfNeeded(new Intent(UploadFiles_Activity_List.this, RequestDataActivity.class)
                            .putExtra(RequestDataActivity.DATA_ID, mCommonId), REQUEST_DATA_CODE);
                }
            }
        });
        callGetAllDocumentList();
    }

    private void requestPemission() {

        // Checking if permission is not granted
        //checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
        //, Manifest.permission.MANAGE_EXTERNAL_STORAGE
        if (SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(UploadFiles_Activity_List.this, new String[]{READ_EXTERNAL_STORAGE
                        , WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA}, 696);
            } else {
                openBottomSheetView();
            }
        } else {
            openBottomSheetView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
        finish();
    }

    private void openBottomSheetView() {
        final MoreBottomSheetDialogFragment commonListBottomSheet = MoreBottomSheetDialogFragment.newInstance();
        commonListBottomSheet.addListener(new MoreBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onBrowserFileClick() {
                commonListBottomSheet.dismiss();
                if (SDK_INT >= Build.VERSION_CODES.R){
                    ToastUtils.shortToast(moContext.getResources().getString(R.string.os_message));
                    return;
                }
                mfileType = "document";
                openDocumentsFile();
                //show file selection
//                if (checkPermission_()) {
//                    openDocumentsFile();
//                } else {
//                    //for android 11
//                    requestPermission();
//                }
            }

            @Override
            public void onUploadLinkClick() {
                mfileType = "link";
                commonListBottomSheet.dismiss();
                showAddLinkDialog();
            }

            @Override
            public void onPhotosClick() {
                mfileType = "image";
                commonListBottomSheet.dismiss();

                //open photo gallery
                BSImagePicker pickerDialog = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                        .build();
                pickerDialog.show(getSupportFragmentManager(), "picker");
            }

            @Override
            public void onCancelChatClick() {
                commonListBottomSheet.dismiss();
            }
        });
        commonListBottomSheet.showNow(getSupportFragmentManager(), UploadFiles_Activity_List.class.getSimpleName());
    }


    private void callGetAllDocumentList() {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token, login_id, common_type, common_id, limit, offset
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("common_type", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonType));
        documentRequest.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        documentRequest.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        documentRequest.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "500"));

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.getDocumentsList(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                hideProgressDialog();

                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    if (moJsonArrayList != null) {
                        moJsonArrayList.clear();
                    }
                    //show response of data list
                    JsonArray jsonArray = mJsonObject.get("data").getAsJsonArray();

                    for (int arr = 0; arr < jsonArray.size(); arr++) {
                        moJsonArrayList.add(jsonArray.get(arr));
                    }
                    //array add
                    moUploadDocumentAdapterList.addData(moJsonArrayList);

                    //show recyclerview
                    recycler_upload_files_list.setVisibility(View.VISIBLE);
                    //Hide no data view
                    moRelativeNoDataView.setVisibility(View.GONE);
                } else {
//                    openErorrDialog(mJsonObject.get("text").getAsString());

                    //hide recyclerview
                    recycler_upload_files_list.setVisibility(View.GONE);
                    //show no data view
                    moRelativeNoDataView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    public void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    //Error Dialog
    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(moContext);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(moContext.getString(R.string.string_error));
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(moContext.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
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

    public void showProgressDialog(boolean isShow) {
        if (mProgressBar != null) {
            if (isShow) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    public void hideProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    // Image pick and select Code
    private static final int PICK_FILE_REQUEST = 3;
    private static final int INTENT_REQUEST_CHOOSE_DOCUMENT = 4;

    private File mImageThumbFile = null, mDocumentFile = null;
    private List<File> mFileUriList = new ArrayList<>();


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

    /*Document*/
//    private boolean checkPermission_() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(UploadFiles_Activity_List.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(UploadFiles_Activity_List.this, WRITE_EXTERNAL_STORAGE);
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
//        }
//    }
    /*end document*/

//    private void browseDocuments() {
//
//        String[] mimeTypes =
//                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
//                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
//                        "text/plain",
//                        "application/pdf",
//                        "application/zip"};
//
//        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//
//        if (SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
//            if (mimeTypes.length > 0) {
//                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
//            }
//        } else {
//            String mimeTypesStr = "";
//            for (String mimeType : mimeTypes) {
//                mimeTypesStr += mimeType + "|";
//            }
//            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
//        }
//        startActivityIfNeeded(Intent.createChooser(intent, "ChooseFile"), INTENT_REQUEST_CHOOSE_DOCUMENT);
//
//    }

    private void openDocumentsFile() {
        Intent intent4 = new Intent(this, NormalFilePickActivity.class);
        intent4.putExtra(Constant.MAX_NUMBER, 1);
        intent4.putExtra(IS_NEED_FOLDER_LIST, false);
        intent4.putExtra(NormalFilePickActivity.SUFFIX,
                new String[]{"doc", "docx", "pdf", "xls", "xlsx"});
        startActivityIfNeeded(intent4, PICK_FILE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (PICK_FILE_REQUEST): {
                if (resultCode == Activity.RESULT_OK) {
                    //file picker code
                    ArrayList<NormalFile> list = data.getParcelableArrayListExtra(Constant.RESULT_PICK_FILE);
                    String path = "";
                    for (NormalFile file : list) {
                        path = file.getPath();
                    }

                    mDocumentFile = FileUtils.getFile(moContext, path);
                    if (mDocumentFile != null) {
                        if (Fileaccept(mDocumentFile)) {
                            //show dialog by rename file
                            showRenameDialog(list.get(0).getName(), mDocumentFile);
                        } else {
                            seekBarShow("File type not Accepted please Re-Select file.", Color.RED);
                        }
                    } else {
                        seekBarShow("File type not Accepted please Re-Select file.", Color.RED);

                    }
                }
            }
            // reset
            mResultCode = 0;
            mResultCode = Activity.RESULT_CANCELED;
            mData = null;
            break;
            case INTENT_REQUEST_CHOOSE_DOCUMENT:
                if (data == null) {
                    return;
                }
//                Uri contentUri = data.getData();
                fileGetDocument(data.getData());
                // reset
                mResultCode = 0;
                mResultCode = Activity.RESULT_CANCELED;
                mData = null;
                break;
        }
    }

    private void fileGetDocument(Uri contentUri) {

        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = com.flippbidd.utils.FileUtils.getFileInfo(UploadFiles_Activity_List.this, contentUri);

        final String name,path;
        final File file;
        if (info == null || info.isEmpty()) {
//            path = RealPathUtil.getRealPath(UploadFiles_Activity_List.this,contentUri);
            file = RealPathUtil.getFile(UploadFiles_Activity_List.this,contentUri);
            if (file.getName()!=null && !file.getName().equalsIgnoreCase("")) {
                name = file.getName();
            } else {
                name = "Sendbird File";
            }
        }else {

            if (info.containsKey("name")) {
                name = (String) info.get("name");
            } else {
                name = "Sendbird File";
            }
            path = (String) info.get("path");
            Log.e("TAG","File ==>"+path);
            file = new File(path);
            mDocumentFile = file;
            Log.e("TAG","File ==>"+mDocumentFile.getAbsolutePath());
        }

        if (mDocumentFile != null) {
            //show dialog by rename file
            showRenameDialog(name, mDocumentFile);
        } else {
            seekBarShow("File type not Accepted please Re-Select file.", Color.RED);
        }
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 696:
                // Checking whether user granted the permission or not.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    openBottomSheetView();
                } else {
                    requestPemission();
                }
                break;

            // other 'case' lines to check for other
            // permissions this app might request.
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void showRenameDialog(String documentName, File mSelectedFile) {
        // Creating alert Dialog with one Button
        Dialog alertDialog = new Dialog(UploadFiles_Activity_List.this);
        alertDialog.setContentView(R.layout.rename_dialog_ui);
        // Setting Dialog Title
        alertDialog.setCancelable(false);
        // Setting Dialog Message

        EditText editText = alertDialog.findViewById(R.id.tvContent);
        editText.setText(documentName);
        alertDialog.findViewById(R.id.btnPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String editValue = editText.getText().toString();
                if (editValue.length() != 0) {
                    alertDialog.dismiss();
                    //cal api for upload document
                    if(!mSelectedFile.equals("")){
                        callUploadDocumentApi(editValue, mSelectedFile, "");
                    } else {
                        seekBarShow("Something want to wrong for uploading file please try after sometime!", Color.RED);
                    }
                } else {
                    editText.setError("Please add name here");
                }
            }
        });

        alertDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageThumbFile = null;
                mDocumentFile = null;
                alertDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void showAddLinkDialog() {
        // Creating alert Dialog with one Button
        Dialog alertDialog = new Dialog(UploadFiles_Activity_List.this);
        alertDialog.setContentView(R.layout.upload_link_dialog_ui);
        // Setting Dialog Title
        alertDialog.setCancelable(false);
        // Setting Dialog Message

        EditText editText1 = alertDialog.findViewById(R.id.tvTypeName);
        EditText editText2 = alertDialog.findViewById(R.id.tvAddLink);

        alertDialog.findViewById(R.id.btnPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get type name
                String typeName = editText1.getText().toString();
                String addLinkName = editText2.getText().toString();

                if (typeName.isEmpty()) {
                    editText1.setError(Constants.ENTER_DOC_NAME);
                    editText1.requestFocus();
                    return;
                } else if (addLinkName.isEmpty()) {
                    editText2.setError(Constants.ENTER_LINK);
                    editText2.requestFocus();
                    return;
                } else if (!isValid(addLinkName)) {
                    editText2.setError(Constants.ENTER_VALID_LINK);
                    editText2.requestFocus();
                    return;
                } else {
                    alertDialog.dismiss();
                    //cal api for upload document
                    callUploadDocumentApi(typeName, null, addLinkName);
                }

            }
        });


        alertDialog.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private boolean isValid(String urlString) {
        try {
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (Exception e) {

        }
        return false;
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
            mFile = FileUtils.getFile(moContext, uri);
            try {
                mCompressor = new Compressor(moContext).compressToFile(mFile);
                mFileUriList.add(mCompressor);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            mFileUriList.add(FileUtils.getFile(moContext, uri));
        }
        mImageThumbFile = mFileUriList.get(0);
        //get name file first index
        showRenameDialog(mFileUriList.get(0).getName(), mImageThumbFile);
    }

    private void callUploadDocumentApi(String mTitleValue, File mSelectedFil, String addLinkVlaue) {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);
        MultipartBody.Part itemSelectedFile = null;

        LinkedHashMap<String, RequestBody> uploadRequest = new LinkedHashMap<String, RequestBody>();
        uploadRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        uploadRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        uploadRequest.put("common_type", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonType));
        uploadRequest.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        uploadRequest.put("file_type", RequestBody.create(MediaType.parse("multipart/form-data"), mfileType));
        uploadRequest.put("insert_type", RequestBody.create(MediaType.parse("multipart/form-data"), "file"));
        uploadRequest.put("title", RequestBody.create(MediaType.parse("multipart/form-data"), mTitleValue));

        //check type
        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        if (mfileType.equalsIgnoreCase("link")) {
            uploadRequest.put("upload_file", RequestBody.create(MediaType.parse("multipart/form-data"), addLinkVlaue));

            observable = userService.documentsUploadFile(uploadRequest);
        } else {
            RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), mSelectedFil);
            itemSelectedFile = MultipartBody.Part.createFormData("upload_file", mSelectedFil.getName(), requestFile);

            observable = userService.documentsUploadFile(uploadRequest, itemSelectedFile);
        }

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    //get list
                    callGetAllDocumentList();
                } else {
                    openErorrDialog(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
                if(!throwable.getMessage().isEmpty())
                    openErorrDialog(throwable.getMessage());
                else
                    openErorrDialog(throwable.getLocalizedMessage());
            }
        });
    }


    @Override
    public void onClickEvent(int position, JsonElement mJsonObject, String mActionType) {

        switch (mActionType) {
            case "View":

                String fileTyp = mJsonObject.getAsJsonObject().get("file_type").getAsString();

                switch (fileTyp) {
                    case "image":
                        String imaegUrl = mJsonObject.getAsJsonObject().get("upload_file").getAsString();
                        openMultipleImage(imaegUrl);
                        break;
                    case "document":
                        try {
                            String docUrl = mJsonObject.getAsJsonObject().get("upload_file").getAsString();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + docUrl), "text/html");
                            startActivity(intent);
                        } catch (Exception ex) {
                        }
                        break;
                    case "link":
                        try {
                            String url = mJsonObject.getAsJsonObject().get("upload_file").getAsString();
                            Intent i = new Intent(Intent.ACTION_VIEW);
                            i.setData(Uri.parse(url));
                            startActivity(i);
                        } catch (Exception e) {
                        }
                        break;
                }

                break;
            case "Delete":
                String deleteId = mJsonObject.getAsJsonObject().get("document_id").getAsString();
                deleteDialog(deleteId);
                break;
        }

    }

    private void deleteDialog(String docID) {

        ColorDialog dialog = new ColorDialog(moContext);
        dialog.setTitle("Delete");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText("Are you sure want to delete this?");
        dialog.setPositiveListener(getString(R.string.string_yes), new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
                try {
                    dialog.dismiss();
                    deleteDoc(docID);
                } catch (Exception e) {

                }
            }
        }).setNegativeListener(getString(R.string.string_no), new ColorDialog.OnNegativeListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();

    }

    private void openMultipleImage(String mImageUrl) {

        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setProgressBarImage(R.drawable.progressbardrawable);

        List<String> images = new ArrayList<>();
        images.add(mImageUrl);
        //items show reverse form
        new ImageViewer.Builder<>(this, images)
                .setFormatter(new ImageViewer.Formatter<String>() {
                    @Override
                    public String format(String customImage) {
                        return customImage;
                    }
                })
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder).show();

    }

    private void deleteDoc(String docId) {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token, login_id, country_code, mobile_number
        LinkedHashMap<String, RequestBody> loginRequest = new LinkedHashMap<String, RequestBody>();
        loginRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        loginRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        loginRequest.put("document_id", RequestBody.create(MediaType.parse("multipart/form-data"), docId));


        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse> observable = userService.documentsDelete(loginRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                hideProgressDialog();
                if (response.getSuccess()) {
                    callGetAllDocumentList();
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
}
