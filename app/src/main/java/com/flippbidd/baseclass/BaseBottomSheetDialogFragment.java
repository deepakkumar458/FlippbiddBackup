package com.flippbidd.baseclass;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.flippbidd.R;


public abstract class BaseBottomSheetDialogFragment extends BaseDialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new BottomSheetDialog(getContext(), getTheme());
    }


    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        getDialog().setCanceledOnTouchOutside(false);

        getDialog().setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                BottomSheetDialog d = (BottomSheetDialog) dialog;
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(R.id.design_bottom_sheet);
                if (bottomSheet == null)
                    return;

                bottomSheet.setBackground(null);
                //add new for bottom screen open full
                BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setHideable(false);


              /*  BottomSheetDialog bottomSheetDialog = (BottomSheetDialog)dialog;
                FrameLayout frameLayout = bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
                if(bottomSheetDialog!=null){
                    return;
                }
                frameLayout.setBackground(null);*/
            }
        });
    }
}