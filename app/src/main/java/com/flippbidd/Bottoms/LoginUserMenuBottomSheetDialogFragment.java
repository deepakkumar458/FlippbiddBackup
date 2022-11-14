package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class LoginUserMenuBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;

    public static LoginUserMenuBottomSheetDialogFragment newInstance() {
        return new LoginUserMenuBottomSheetDialogFragment();
    }


    @OnClick({R.id.relative_menu_bottom_sheet_request_edit_property, R.id.tv_menu_bottom_sheet_request_edit_property})
    void viewRequestContract() {
        if (dailogListener != null) {
            dailogListener.onRequestEditPropertyClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_view_unavailable, R.id.tv_menu_bottom_sheet_view_unavailable})
    void viewRequestDataFolder() {
        if (dailogListener != null) {
            dailogListener.onRequestViewUnavailableClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_view_delete_property, R.id.tv_menu_bottom_sheet_view_delete_property})
    void viewRequestPlaceBidd() {
        if (dailogListener != null) {
            dailogListener.onRequestViewDeletePropertyClick();
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
        return R.layout.login_user_more_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog) {
        dailogListener = dailog;
    }

    public interface DailogListener {
        void onRequestEditPropertyClick();
        void onRequestViewUnavailableClick();
        void onRequestViewDeletePropertyClick();


        void onCancelClick();

    }

}