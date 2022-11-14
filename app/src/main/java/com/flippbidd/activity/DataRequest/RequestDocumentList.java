package com.flippbidd.activity.DataRequest;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flippbidd.Adapter.RequestDocumentUserListAdapter;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.RequestData.RequestDataDocumentResponse;
import com.flippbidd.Model.Response.RequestData.RequestProfileData;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestDocumentList extends AppCompatActivity {

    public static final String DATA_ID = "DATA_ID";

    private Context moContext;
    Disposable disposable;
    ProgressBar contentProgress;
    ImageView ivReQuestDocumentListBack;
    private String propertyID = "";
    RequestDocumentUserListAdapter requestDocumentUserListAdapter;
    private RecyclerView recyclerviewDocumentList;
    private SwipeRefreshLayout swipeRefreshDocumentList;
    private RelativeLayout relativeNoDataView;
    LinearLayoutManager loLinearLayoutManager;
    List<RequestProfileData> profileDataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        this.moContext = RequestDocumentList.this;
        setContentView(R.layout.activity_request_document_list);

        propertyID = getIntent().getExtras().getString(DATA_ID);

        initView();
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    private void initView() {
        relativeNoDataView = findViewById(R.id.relativeNoDataView);
        swipeRefreshDocumentList = findViewById(R.id.swipeRefreshDocumentList);
        recyclerviewDocumentList = findViewById(R.id.recyclerviewDocumentList);
        contentProgress = findViewById(R.id.contentProgress);
        ivReQuestDocumentListBack = findViewById(R.id.ivReQuestDocumentListBack);

        requestDocumentUserListAdapter = new RequestDocumentUserListAdapter(moContext, profileDataList, false, propertyID);
        loLinearLayoutManager = new LinearLayoutManager(moContext);
        recyclerviewDocumentList.setLayoutManager(loLinearLayoutManager);
        recyclerviewDocumentList.setAdapter(requestDocumentUserListAdapter);


        ivReQuestDocumentListBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshDocumentList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestDataDocumentUserList();
            }
        });

        requestDataDocumentUserList();
    }

    private void requestDataDocumentUserList() {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token,login_id,property_id,limit,offset
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), propertyID));
        documentRequest.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        documentRequest.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "500"));

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.requestDataList(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                hideProgressDialog();
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    if (profileDataList != null) {
                        profileDataList.clear();
                    }
                    //show response of data list
                    Gson gson = new Gson();
                    RequestDataDocumentResponse moJsonArrayList = gson.fromJson(mJsonObject, RequestDataDocumentResponse.class);
                    //array add
                    requestDocumentUserListAdapter.addData(moJsonArrayList.getData());
                    relativeNoDataView.setVisibility(View.GONE);
                    recyclerviewDocumentList.setVisibility(View.VISIBLE);
                } else {
                    recyclerviewDocumentList.setVisibility(View.GONE);
                    relativeNoDataView.setVisibility(View.VISIBLE);
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
        if (contentProgress != null) {
            if (isShow) {
                contentProgress.setVisibility(View.VISIBLE);
            } else {
                contentProgress.setVisibility(View.GONE);
            }
        }
    }

    public void hideProgressDialog() {
        if (contentProgress != null) {
            contentProgress.setVisibility(View.GONE);
        }

        if (swipeRefreshDocumentList != null) {
            swipeRefreshDocumentList.setRefreshing(false);
        }
    }
}
