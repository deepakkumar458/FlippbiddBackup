package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class UserBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;
    boolean isValue = false;

    public static UserBottomSheetDialogFragment newInstance() {
        return new UserBottomSheetDialogFragment();
    }


    @OnClick({R.id.relative_menu_bottom_sheet_request_add_to_calender, R.id.tv_menu_bottom_sheet_request_add_to_calender})
    void viewRequestContract() {
        if (dailogListener != null) {
            dailogListener.onRequestAddToCalender();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_cancel_request, R.id.tv_menu_bottom_sheet_cancel_request})
    void viewRequestCancel() {
        if (dailogListener != null) {
            dailogListener.onRequestCancel();
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
        return R.layout.user_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog,boolean isAvailable) {
        dailogListener = dailog;
        isValue = isAvailable;
    }

    public interface DailogListener {
        void onRequestAddToCalender();
        void onRequestCancel();
        void onCancelClick();

    }

}