package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;

import butterknife.OnClick;

public class AdminMenuBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;
    boolean isValue = false;
    CustomTextView txtAvailable;

    public static AdminMenuBottomSheetDialogFragment newInstance() {
        return new AdminMenuBottomSheetDialogFragment();
    }


    @OnClick({R.id.relative_menu_bottom_sheet_request_edit_property, R.id.tv_menu_bottom_sheet_request_edit_property})
    void viewRequestContract() {
        if (dailogListener != null) {
            dailogListener.onRequestEditPropertyClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_view_data_folder, R.id.tv_menu_bottom_sheet_view_data_folder})
    void viewRequestDataFolder() {
        if (dailogListener != null) {
            dailogListener.onRequestViewDataFolderClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_view_bidd, R.id.tv_menu_bottom_sheet_view_bidd})
    void viewRequestPlaceBidd() {
        if (dailogListener != null) {
            dailogListener.onRequestViewBiddClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_unavailable, R.id.tv_menu_bottom_sheet_unavailable})
    void viewRequestUnavilable() {
        String txtValue = txtAvailable.getText().toString();
        if (dailogListener != null) {
            dailogListener.onRequestPropertyUnavilable(txtValue);
        }
    }

    @OnClick(R.id.tv_menu_bottom_sheet_cancel)
    void onCancelClick() {
        if (dailogListener != null) {
            dailogListener.onCancelClick();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        txtAvailable = view.findViewById(R.id.tv_menu_bottom_sheet_unavailable);
        if (isValue) {
            //text value available
            txtAvailable.setText("Unavilable");
        } else {
            txtAvailable.setText("Available");
            //unavailable
        }
    }

    @Override
    public int getLayoutResource() {
        return R.layout.admin_more_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog, boolean isAvailable) {
        dailogListener = dailog;
        isValue = isAvailable;
    }

    public interface DailogListener {
        void onRequestEditPropertyClick();

        void onRequestViewDataFolderClick();

        void onRequestViewBiddClick();

        void onRequestPropertyUnavilable(String value);


        void onCancelClick();

    }

}