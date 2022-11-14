package com.flippbidd.Bottoms;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.flippbidd.R;
import com.flippbidd.baseclass.BaseBottomSheetDialogFragment;

import butterknife.OnClick;

public class CallDialogFragment extends BaseBottomSheetDialogFragment {

    CallDialogFragment.DailogListener dailogListener;

    public static CallDialogFragment newInstance() {
        return new CallDialogFragment();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    @OnClick({R.id.relative_menu_bottom_sheet_call_now, R.id.tv_call})
    void callClickListener() {
        if (dailogListener != null) {
            dailogListener.onCallClick();
        }
    }

    @OnClick({R.id.relative_menu_bottom_sheet_schedule_call, R.id.tv_schedule})
    void scheduleCallClickListener() {
        if (dailogListener != null) {
            dailogListener.onScheduleCallClick();
        }
    }

    @OnClick({R.id.tv_menu_bottom_sheet_cancel})
    void cancelClickListener() {
        if (dailogListener != null) {
            dailogListener.onCancelClick();
        }
    }

    public void addListener(CallDialogFragment.DailogListener dailog) {
        dailogListener = dailog;
    }

    @Override
    public int getLayoutResource() {
        return R.layout.call_dialog_ui;
    }

    public interface DailogListener {
        void onCallClick();

        void onScheduleCallClick();


        void onCancelClick();

    }
}
