package com.flippbidd.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Others.Constants;
import com.flippbidd.R;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class PropertyBiddListAdapter extends RecyclerView.Adapter<PropertyBiddListAdapter.MyViewHolder> {

    Context moContext;
    List<CommonData.Bidds> jsonArrays;
    boolean isDeleteShow;
    ImageLoader mImageLoader;
    onItemClickLisnear mOnItemClickLisnear;
    private String ownerPropertyID;

    public PropertyBiddListAdapter(Context mContext, List<CommonData.Bidds> moListData, boolean isDeleted, String propertyID) {
        this.moContext = mContext;
        this.isDeleteShow = isDeleted;
        this.ownerPropertyID = propertyID;
        jsonArrays = new ArrayList<>();
        jsonArrays.addAll(moListData);
        mImageLoader = ImageLoader.getInstance();
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(moContext).inflate(R.layout.item_bidd_list, parent, false);
        //View view = LayoutInflater.from(moContext).inflate(R.layout.test_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            CommonData.Bidds mJsonObject = jsonArrays.get(position);
            holder.bideData(position, mJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return jsonArrays.size();
    }

    public void addData(List<CommonData.Bidds> moJsonArrayList) {
        jsonArrays.clear();
        jsonArrays.addAll(moJsonArrayList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView txtBiddByName, txtBidPrice;
        CircleImageView imgBiddUser;
        ImageView moIvDocumentView;
        //AppCompatImageView imageActionAccept, imageActionCountered, imageActionReject;
        CustomTextView tvBiddMaterialStatus;
        LinearLayoutCompat linearcall;
        AppCompatImageView imageaudiocall, imagevideocall;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //tvBiddMaterialStatus = itemView.findViewById(R.id.tvBiddMaterialStatus);
            //imageActionAccept = itemView.findViewById(R.id.imageActionAccept);
            //imageActionCountered = itemView.findViewById(R.id.imageActionCountered);
            //imageActionReject = itemView.findViewById(R.id.imageActionReject);

            imgBiddUser = itemView.findViewById(R.id.imageBiddUser);
            txtBiddByName = itemView.findViewById(R.id.txtBiddByName);
            txtBidPrice = itemView.findViewById(R.id.txtBidPrice);
            linearcall = itemView.findViewById(R.id.linearcall);
            imageaudiocall = itemView.findViewById(R.id.imageaudiocall);
            imagevideocall = itemView.findViewById(R.id.imagevideocall);

        }

        void bideData(int position, CommonData.Bidds mJsonObject) {
            if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mJsonObject.getLoginId())) {
                txtBiddByName.setText(mJsonObject.getUserFullName() + " (You)");
            } else {
                txtBiddByName.setText(mJsonObject.getUserFullName());
            }

            txtBidPrice.setText("$ " + currencyFormat(mJsonObject.getPrice()));

            if (mJsonObject.getUserImage() != null && !mJsonObject.getUserImage().equalsIgnoreCase("")) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.ARGB_4444)
                        .build();
                mImageLoader.displayImage(mJsonObject.getUserImage(), imgBiddUser, options);
            } else {
                imgBiddUser.setImageResource(R.mipmap.user);
            }

            if (ownerPropertyID.equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                //add call button here
                imageaudiocall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewCallService.dial(moContext, mJsonObject.getUserFullName(), mJsonObject.getUser_email(), false);
                        PreferenceUtils.setCalleeId(mJsonObject.getLoginId());
                    }
                });

                imagevideocall.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        NewCallService.dial(moContext, mJsonObject.getUserFullName(), mJsonObject.getUser_email(), true);
                        PreferenceUtils.setCalleeId(mJsonObject.getLoginId());
                    }
                });
                /*if (mJsonObject.getStatus().equalsIgnoreCase("1")) {
                    //accept
                    updateUi(false, Constants.BIDD_ACCEPT, moContext.getResources().getColor(R.color.quantum_googgreen));
                }
                if (mJsonObject.getStatus().equalsIgnoreCase("2")) {
                    //reject
                    updateUi(false, Constants.BIDD_REJECT, moContext.getResources().getColor(R.color.red));

                }
                if (mJsonObject.getStatus().equalsIgnoreCase("3")) {
                    //counter
                    updateUi(false, Constants.BIDD_COUNTERED, moContext.getResources().getColor(R.color.text_color_black));
                }*/
            } else {
                //other user(remove call button here)
                linearcall.setVisibility(View.GONE);
                //updateUi(false, "", moContext.getResources().getColor(R.color.transparent));
            }


            imgBiddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mJsonObject.getLoginId() != null && !mJsonObject.getLoginId().equalsIgnoreCase(""))
                        (moContext).startActivity(new Intent(moContext, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, mJsonObject.getLoginId()));
                }
            });

            txtBiddByName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mJsonObject.getLoginId() != null && !mJsonObject.getLoginId().equalsIgnoreCase(""))
                        (moContext).startActivity(new Intent(moContext, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, mJsonObject.getLoginId()));
                }
            });

            /*imageActionAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.ACCEPT, mJsonObject, moContext.getResources().getString(R.string.accept_alert_bidd_message));
                }
            });
            imageActionCountered.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.COUNTERED, mJsonObject, moContext.getResources().getString(R.string.counter_alert_bidd_message));
                }
            });

            imageActionReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.REJECT, mJsonObject, moContext.getResources().getString(R.string.reject_alert_bidd_message));
                }
            });*/

        }

        /*void updateUi(boolean isShowed, String biddStatus, int colorCode) {
            if (isShowed) {
                imageActionAccept.setVisibility(View.VISIBLE);
                imageActionCountered.setVisibility(View.VISIBLE);
                imageActionReject.setVisibility(View.VISIBLE);
                tvBiddMaterialStatus.setVisibility(View.GONE);

            } else {
                imageActionAccept.setVisibility(View.GONE);
                imageActionCountered.setVisibility(View.GONE);
                imageActionReject.setVisibility(View.GONE);
                tvBiddMaterialStatus.setVisibility(View.VISIBLE);

                tvBiddMaterialStatus.setTextColor(colorCode);
                tvBiddMaterialStatus.setText(biddStatus);
            }
        }*/

        void openDialogView(String statusCode, CommonData.Bidds mJsonObject, String message) {

            Dialog mDialog = new Dialog(moContext);
            mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            Window window = mDialog.getWindow();
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            mDialog.setContentView(R.layout.restriction_dialog_view);

            CustomTextView txt_003, txt_002, txt_001;
            CustomEditText editPriceBox;

            editPriceBox = mDialog.findViewById(R.id.editPriceBox);
            txt_001 = mDialog.findViewById(R.id.txt_01);
            txt_002 = mDialog.findViewById(R.id.txt_02);
            txt_003 = mDialog.findViewById(R.id.txt_03);

            txt_002.setText("Cancel");
            txt_001.setText(statusCode);
            txt_003.setText(message);

            if (statusCode.equalsIgnoreCase(Constants.COUNTERED)) {
                editPriceBox.setVisibility(View.VISIBLE);
                txt_001.setText("Submit");
            }


            mDialog.findViewById(R.id.txt_01).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String priceValue = "";
                    if (statusCode.equalsIgnoreCase(Constants.COUNTERED)) {
                        if (((CustomEditText) mDialog.findViewById(R.id.editPriceBox)).getText().toString().isEmpty()) {
                            ToastUtils.shortToast("Please add price");
                            return;
                        }
                        priceValue = ((CustomEditText) mDialog.findViewById(R.id.editPriceBox)).getText().toString();
                    }
                    switch (statusCode) {
                        case Constants
                                .ACCEPT:
                            //updateUi(false, Constants.BIDD_ACCEPT, moContext.getResources().getColor(R.color.quantum_googgreen));
                            //openDialogView(mActionType, mJsonObject, getResources().getString(R.string.accept_alert_bidd_message));
                            break;
                        case Constants
                                .REJECT:
                            //updateUi(false, Constants.BIDD_REJECT, moContext.getResources().getColor(R.color.red));
                            break;
                        case Constants
                                .COUNTERED:
                            //updateUi(false, Constants.BIDD_COUNTERED, moContext.getResources().getColor(R.color.text_color_black));
                            break;
                    }

                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(getAdapterPosition(), mJsonObject, statusCode, priceValue);
                    }
                    //call api
                    mDialog.dismiss();
                }
            });
            mDialog.findViewById(R.id.txt_02).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDialog.dismiss();
                }
            });
            mDialog.setCancelable(true);
            mDialog.setCanceledOnTouchOutside(true);
            mDialog.show();
        }
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, CommonData.Bidds mJsonObject, String mActionType, String priceValue);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


}