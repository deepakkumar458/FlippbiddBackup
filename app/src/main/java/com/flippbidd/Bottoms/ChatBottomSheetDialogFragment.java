package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import androidx.annotation.Nullable;
import butterknife.OnClick;

public class ChatBottomSheetDialogFragment extends BaseBottomSheetDialogFragment {


    DailogListener dailogListener;
    private String userName="";

    public static ChatBottomSheetDialogFragment newInstance() {
        return new ChatBottomSheetDialogFragment();
    }

    @OnClick({R.id.tv_menu_bottom_sheet_voice_call,R.id.relative_menu_bottom_sheet_voice_call})
    void onBlockClick() {
        if (dailogListener != null) {
            dailogListener.onVoiceCallClick();
        }
    }

    @OnClick({R.id.tv_menu_bottom_sheet_video_call,R.id.relative_menu_bottom_sheet_video_call})
    void onClearChatClick() {
        if (dailogListener != null) {
            dailogListener.onUVideoCallClick();
        }
    }


    @OnClick({R.id.tv_menu_bottom_sheet_view_profile,R.id.relative_menu_bottom_sheet_view_profile})
    void onDeleteChatClick() {
        if (dailogListener != null) {
            dailogListener.onViewProfileClick();
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
        ((CustomTextView) view.findViewById(R.id.tv_menu_bottom_sheet_header_title)).setText(userName);
    }

    @Override
    public int getLayoutResource() {
        return R.layout.chat_bottom_sheet_layout;
    }

    public void addListener(DailogListener dailog,String fullname) {
        dailogListener = dailog;
        userName = fullname;
    }

    public interface DailogListener {
        void onVoiceCallClick();

        void onUVideoCallClick();

        void onViewProfileClick();

        void onCancelChatClick();

    }

}