package com.flippbidd.activity.DataRequest;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.widget.WaitingDialog;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;

//import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class RequestDataActivity extends AppCompatActivity {

    public static final String DATA_ID = "DATA_ID";
    private MaterialCheckBox tvOtherCheck, tvPicturesCheck, tvVideoCheck, tvSealsCompsCheck, tvMarketAnalysisCheck, tvDrawingsCheck, tvFinancialsCheck;
    private MaterialTextView tvCharCounts;
    private CustomEditText editOtherNotes;
    private CustomTextView tvRequestCancel;
    private CustomAppCompatButton btnRequestDataSubmit;
    private List<String> mStringList = new ArrayList<>();
    private String propertyID = "";
    private int counts = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_data);
        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        propertyID = getIntent().getExtras().getString(DATA_ID);

        clickInit();
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    private void clickInit() {
        tvRequestCancel = findViewById(R.id.tvRequestCancel);
        tvCharCounts = findViewById(R.id.tvCharCounts);
        editOtherNotes = findViewById(R.id.editOtherNotes);
        tvOtherCheck = findViewById(R.id.tvOtherCheck);
        tvPicturesCheck = findViewById(R.id.tvPicturesCheck);
        tvVideoCheck = findViewById(R.id.tvVideoCheck);
        tvSealsCompsCheck = findViewById(R.id.tvSealsCompsCheck);
        tvMarketAnalysisCheck = findViewById(R.id.tvMarketAnalysisCheck);
        tvDrawingsCheck = findViewById(R.id.tvDrawingsCheck);
        tvFinancialsCheck = findViewById(R.id.tvFinancialsCheck);
        btnRequestDataSubmit = findViewById(R.id.btnRequestDataSubmit);


        tvRequestCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvOtherCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editOtherNotes.setVisibility(View.VISIBLE);
                    tvCharCounts.setVisibility(View.VISIBLE);
                } else {
                    editOtherNotes.setText("");
                    editOtherNotes.setVisibility(View.GONE);
                    tvCharCounts.setVisibility(View.GONE);
                }
            }
        });
        editOtherNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String s1 = s.toString();
                counts = s1.length();
                tvCharCounts.setText(counts + "/20");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        tvVideoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvVideoCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvVideoCheck.getText().toString());
                    }
                }
            }
        });

        tvPicturesCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvPicturesCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvPicturesCheck.getText().toString());
                    }
                }
            }
        });
        tvSealsCompsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvSealsCompsCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvSealsCompsCheck.getText().toString());
                    }
                }
            }
        });

        tvMarketAnalysisCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvMarketAnalysisCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvMarketAnalysisCheck.getText().toString());
                    }
                }
            }
        });

        tvDrawingsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvDrawingsCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvDrawingsCheck.getText().toString());
                    }
                }
            }
        });

        tvFinancialsCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mStringList.add(tvFinancialsCheck.getText().toString());
                } else {
                    if (mStringList.size() != 0) {
                        mStringList.remove(tvFinancialsCheck.getText().toString());
                    }
                }
            }
        });

        btnRequestDataSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otherData = "";
                if (tvOtherCheck.isChecked()) {
                    if (editOtherNotes.getText().toString().trim().isEmpty()) {
                        ToastUtils.shortToast("Please enter document name!");
                        return;
                    }
                    otherData = editOtherNotes.getText().toString().trim();
                }

                if (mStringList.size() == 0 && !tvOtherCheck.isChecked()) {
                    ToastUtils.shortToast("Please select any one type of request data.");
                    return;
                }
                requestData(getList(), otherData);
            }
        });
    }

    private String getList() {
        if (mStringList != null && mStringList.size() != 0) {
//            return StringUtil.join(mStringList, ",");
            return TextUtils.join(",", mStringList);
        }
        return "";
    }

    private void requestData(String folderData, String otherData) {
        //token,login_id,property_id,folder_data,other
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), propertyID));
        linkedHashMap.put("folder_data", RequestBody.create(MediaType.parse("multipart/form-data"), folderData));
        linkedHashMap.put("other", RequestBody.create(MediaType.parse("multipart/form-data"), otherData));

        showProgress(true);

        UserServices userService = ApiFactory.getInstance(RequestDataActivity.this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.propertyRequestData(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgress(false);
                /*Your data request has been submitted.*/
                if (response.getSuccess()) {
                    CommonDialogView.getInstance().showCommonDialog(RequestDataActivity.this, "",
                            response.getText()
                            , ""
                            , "Ok"
                            , false, true, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    setResult(RESULT_OK);
                                    finish();
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });
                } else {
                    CommonDialogView.getInstance().showCommonDialog(RequestDataActivity.this, "",
                            response.getText()
                            , ""
                            , "Close"
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
                showProgress(false);
            }
        });
    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }

    private void showProgress(boolean show) {
        if (show) {
            WaitingDialog.show(this);
        } else {
            WaitingDialog.dismiss();
        }
    }


}