package com.flippbidd.activity.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class Help extends BaseAppCompatActivity{

    @BindView(R.id.btn_email)
    CustomTextView btn_email;
    @BindView(R.id.imageBackIcon)
    ImageView imageBackIcon;

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
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_help_layout;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnClick({R.id.btn_email,R.id.imageBackIcon})
    void viewClick(View view){
        switch (view.getId())
        {
            case R.id.btn_email:
                Utils.mailTo(mBaseAppCompatActivity,"FlippBidd","flippbidd@gmail.com","");
                break;
            case R.id.imageBackIcon:
                onBackPressed();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish(true);
    }
}
