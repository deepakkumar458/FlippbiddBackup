package com.flippbidd.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flippbidd.Adapter.CommonListData.OtherUserPropertyListDataAdapter;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Others.Constants;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.view.BaseActivity;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.refactor.lib.colordialog.ColorDialog;

public class OtherUserPropertyList extends BaseActivity implements OtherUserPropertyListDataAdapter.onItemClickLisnear {

    RecyclerView moRecyclerViewOtherUserPropertiesList;
    OtherUserPropertyListDataAdapter mOtherUserPropertyListDataAdapter;
    OtherUserDetails loOtherUserDetails;
    int lastSelectedPosition = -1;

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
        setContentView(R.layout.activity_other_user_property_list_activity);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            loOtherUserDetails = (OtherUserDetails) bundle.getSerializable("p_list");
        }

        initView();

        findViewById(R.id.imageOtherUPropertiesBackView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initView() {

        moRecyclerViewOtherUserPropertiesList = findViewById(R.id.recyclerViewOtherUserPropertiesList);

        mOtherUserPropertyListDataAdapter = new OtherUserPropertyListDataAdapter(this, "");

        moRecyclerViewOtherUserPropertiesList.setLayoutManager(new LinearLayoutManager(OtherUserPropertyList.this));
        moRecyclerViewOtherUserPropertiesList.setHasFixedSize(true);
        moRecyclerViewOtherUserPropertiesList.setAdapter(mOtherUserPropertyListDataAdapter);
        //mOtherUserPropertyListDataAdapter.addAll(loOtherUserDetails.getPropertyList());
        mOtherUserPropertyListDataAdapter.addAll(loOtherUserDetails);
        mOtherUserPropertyListDataAdapter.setItemOnClickEvent(this::onClickEvent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onClickEvent(int position, CommonData mPropertyData, String mActionType) {

        switch (mActionType) {
            case Constants.ACTION.VIEW_ACTION:
                lastSelectedPosition = position;
                if (mPropertyData.getLoginId() != null && !mPropertyData.getLoginId().equals("")) {
                    if (mPropertyData.getIsAvailable().equalsIgnoreCase("1")) {
                        startActivityIfNeeded(PropertyDetails.getIntentActivity(OtherUserPropertyList.this,
                                mPropertyData.getPropertyId(), mPropertyData.getLoginId(),
                                "PROPERTY", mPropertyData.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                    } else {
                        if (mPropertyData.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                            startActivityIfNeeded(PropertyDetails.getIntentActivity(OtherUserPropertyList.this,
                                    mPropertyData.getPropertyId(), mPropertyData.getLoginId(),
                                    "PROPERTY", mPropertyData.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                        } else {
                            showNotAvailable();
                        }
                    }
                } else {
                    showNotAvailable();
                }
                break;
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(OtherUserPropertyList.this);
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == Constants.REQUEST_UNAVILABLE) {
                Log.e("TAG", "Update Other User Property List");
                if (mOtherUserPropertyListDataAdapter != null) {
                    mOtherUserPropertyListDataAdapter.updatePositionData(lastSelectedPosition);
                }
            }
        }
    }

}
