package com.flippbidd.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.R;
import com.flippbidd.interfaces.CommonDialogCallBack;

public class CommonDialogView {

    private Dialog mDialog;
    private static CommonDialogView commonDialogView = null;

    private CommonDialogCallBack commonDialogCallBack;

    public static CommonDialogView getInstance() {
        if (commonDialogView == null) commonDialogView = new CommonDialogView();
        return commonDialogView;
    }

    public void showCommonDialog(final Context context, String title, String desc, String txtCancel, String txtYes, boolean hideDes
            , boolean hideHeader, boolean onlyOneBtn, boolean isCancelable, boolean isCloseView, CommonDialogCallBack actionButtonListener) {
        if (context != null) {
            mDialog = new Dialog(context);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = mDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mDialog.setContentView(R.layout.dilaog_logout_ui);

            if (hideHeader) {
                ((CustomTextView) mDialog.findViewById(R.id.txt_message_header)).setVisibility(View.GONE);
            } else {
                ((CustomTextView) mDialog.findViewById(R.id.txt_message_header)).setText(title);
            }

            if (hideDes) {
                ((CustomTextView) mDialog.findViewById(R.id.txt_message)).setVisibility(View.GONE);
            } else {
                ((CustomTextView) mDialog.findViewById(R.id.txt_message)).setText(desc);
            }

            if (onlyOneBtn) {
                ((CustomTextView) mDialog.findViewById(R.id.txt_cancel)).setVisibility(View.GONE);
                ((View) mDialog.findViewById(R.id.viewLineHorizantal)).setVisibility(View.GONE);
            } else {
                ((CustomTextView) mDialog.findViewById(R.id.txt_cancel)).setText(txtCancel);
            }
            ((CustomTextView) mDialog.findViewById(R.id.txt_okay)).setText(txtYes);

            mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (commonDialogCallBack != null) {
                        commonDialogCallBack.onDialogYes(view);
                    }
                    mDialog = null;
                }
            });
            mDialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mDialog != null) {
                        mDialog.dismiss();
                    }
                    if (commonDialogCallBack != null) {
                        commonDialogCallBack.onDialogCancel(view);
                    }
                    mDialog = null;
                }
            });

            setActionButtonListener(actionButtonListener);

            mDialog.setCancelable(isCancelable);
            mDialog.setCanceledOnTouchOutside(isCancelable);
            mDialog.show();
        }

    }

    public void setActionButtonListener(CommonDialogCallBack actionButtonListener) {
        commonDialogCallBack = actionButtonListener;
    }

    public void dismissAcknowledgeDailog() {
        mDialog.dismiss();
    }


}
