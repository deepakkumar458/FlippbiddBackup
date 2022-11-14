package com.flippbidd.bottomsheet;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;


import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import butterknife.OnClick;

public class SelectionBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;

    public static SelectionBottomSheetDialogFragment newInstance() {
        return new SelectionBottomSheetDialogFragment();
    }

    @OnClick(R.id.tv_Property_selected)
    void onPropertyClick() {
        if (dailogListener != null) {
            dailogListener.onPropertyClick();
        }
    }


//    @OnClick(R.id.tv_photo_bottom_sheet_camera)
//    void onCameraClick() {
//        if (dailogListener != null) {
//            dailogListener.onRentalsClick();
//        }
//    }
//
//
//
//    @OnClick(R.id.tv_photo_bottom_sheet_gallery)
//    void onGalleryClick() {
//        if (dailogListener != null) {
//            dailogListener.onServicesClick();
//        }
//    }
//

    @OnClick(R.id.textCancel)
    void onCancelClick() {
        if (dailogListener != null) {
            dailogListener.onCancel();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.layout_bottom_sheet_selection;
    }

    public void addListener(DailogListener dailog) {
        dailogListener = dailog;
    }

    public interface DailogListener {
        void onPropertyClick();
        void onRentalsClick();
        void onServicesClick();
        void onCancel();
    }

}