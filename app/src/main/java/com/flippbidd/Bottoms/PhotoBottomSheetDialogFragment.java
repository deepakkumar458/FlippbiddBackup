package com.flippbidd.Bottoms;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;


import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;
import butterknife.OnClick;

public class PhotoBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;

    public static PhotoBottomSheetDialogFragment newInstance() {
        return new PhotoBottomSheetDialogFragment();
    }

    @OnClick(R.id.tv_photo_bottom_sheet_camera)
    void onCameraClick() {
        if (dailogListener != null) {
            dailogListener.onCameraClick();
        }
    }

    @OnClick(R.id.tv_photo_bottom_sheet_gallery)
    void onGalleryClick() {
        if (dailogListener != null) {
            dailogListener.onGalleryClick();
        }
    }


    @OnClick(R.id.textCancel)
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
        return R.layout.layout_bottom_sheet_photo;
    }

    public void addListener(DailogListener dailog) {
        dailogListener = dailog;
    }

    public interface DailogListener {
        void onCameraClick();

        void onGalleryClick();
        void  onCancelClick();

    }

}