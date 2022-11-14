package com.flippbidd.Bottoms;

import android.os.Bundle;
import androidx.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;

import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import butterknife.OnClick;

public class MoreBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;

    public static MoreBottomSheetDialogFragment newInstance() {
        return new MoreBottomSheetDialogFragment();
    }

    @OnClick(R.id.tv_menu_bottom_sheet_browser_files)
    void onBlockClick() {
        if (dailogListener != null) {
            dailogListener.onBrowserFileClick();
        }
    }

    @OnClick(R.id.tv_menu_bottom_sheet_upload_link)
    void onClearChatClick() {
        if (dailogListener != null) {
            dailogListener.onUploadLinkClick();
        }
    }


    @OnClick(R.id.tv_menu_bottom_sheet_photos)
    void onDeleteChatClick() {
        if (dailogListener != null) {
            dailogListener.onPhotosClick();
        }
    }

    @OnClick(R.id.tv_menu_bottom_sheet_cancel)
    void onCancelChatClick() {
        if (dailogListener != null) {
            dailogListener.onCancelChatClick();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @Override
    public int getLayoutResource() {
        return R.layout.more_menu_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog) {
        dailogListener = dailog;
    }

    public interface DailogListener {
        void onBrowserFileClick();
        void onUploadLinkClick();
        void onPhotosClick();
        void  onCancelChatClick();

    }

}