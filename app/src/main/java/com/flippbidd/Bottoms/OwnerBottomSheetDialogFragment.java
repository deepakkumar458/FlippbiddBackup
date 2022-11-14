package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class OwnerBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;
    boolean isValue = false;

    public static OwnerBottomSheetDialogFragment newInstance() {
        return new OwnerBottomSheetDialogFragment();
    }


    @OnClick({R.id.relative_menu_bottom_sheet_request_edit_request, R.id.tv_menu_bottom_sheet_request_edit_request})
    void viewRequestContract() {
        if (dailogListener != null) {
            dailogListener.onRequestEditClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_cancel_request, R.id.tv_menu_bottom_sheet_cancel_request})
    void viewRequestDataFolder() {
        if (dailogListener != null) {
            dailogListener.onRequestCancelClick();
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
    }

    @Override
    public int getLayoutResource() {
        return R.layout.owner_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog,boolean isAvailable) {
        dailogListener = dailog;
        isValue = isAvailable;
    }

    public interface DailogListener {
        void onRequestEditClick();
        void onRequestCancelClick();

        void onCancelClick();

    }

}