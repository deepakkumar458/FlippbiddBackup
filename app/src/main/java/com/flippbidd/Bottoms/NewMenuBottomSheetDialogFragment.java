package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;

import butterknife.OnClick;

public class NewMenuBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;
    boolean _isPremiunUser;

    public static NewMenuBottomSheetDialogFragment newInstance() {
        return new NewMenuBottomSheetDialogFragment();
    }


    @OnClick({R.id.relative_menu_bottom_sheet_request_contract, R.id.tv_menu_bottom_sheet_request_contract})
    void viewRequestContract() {
        if (dailogListener != null) {
            dailogListener.onRequestContractClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_view_data_folder, R.id.tv_menu_bottom_sheet_view_data_folder})
    void viewRequestDataFolder() {
        if (dailogListener != null) {
            dailogListener.onRequestViewDataFolderClick();
        }
    }

    /*@OnClick({R.id.relative_menu_bottom_sheet_message_user, R.id.tv_menu_bottom_sheet_message_user})
    void viewRequestMessageUser() {
        if (dailogListener != null) {
            dailogListener.onRequestMessageUserClick();
        }
    }*/

    @OnClick({R.id.relative_menu_bottom_sheet_place_bidd, R.id.tv_menu_bottom_sheet_place_bidd})
    void viewRequestPlaceBidd() {
        if (dailogListener != null) {
            dailogListener.onRequestPlaceBiddClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_share_property, R.id.tv_menu_bottom_sheet_share_property})
    void viewRequestShareProperty() {
        if (dailogListener != null) {
            dailogListener.onRequestSharePropertyClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_report_content, R.id.tv_menu_bottom_sheet_report_content})
    void viewRequestReportContent() {
        if (dailogListener != null) {
            dailogListener.onRequestReportContentClick();
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

        if (_isPremiunUser) {
            ((LinearLayout) view.findViewById(R.id.relative_menu_bottom_sheet_request_contract)).setVisibility(View.VISIBLE);
            ((LinearLayout) view.findViewById(R.id.relative_menu_bottom_sheet_view_data_folder)).setVisibility(View.VISIBLE);
        } else {
            //hide here
            ((LinearLayout) view.findViewById(R.id.relative_menu_bottom_sheet_request_contract)).setVisibility(View.GONE);
            ((LinearLayout) view.findViewById(R.id.relative_menu_bottom_sheet_view_data_folder)).setVisibility(View.GONE);
        }

    }

    @Override
    public int getLayoutResource() {
        return R.layout.new_more_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog, boolean isPremiumUser) {
        dailogListener = dailog;
        _isPremiunUser = isPremiumUser;
    }

    public interface DailogListener {
        void onRequestContractClick();

        void onRequestViewDataFolderClick();

        void onRequestMessageUserClick();

        void onRequestPlaceBiddClick();

        void onRequestSharePropertyClick();

        void onRequestReportContentClick();

        void onCancelClick();

    }

}