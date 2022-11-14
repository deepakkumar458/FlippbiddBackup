package com.flippbidd.Adapter.callrequest;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Others.Constants;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.utils.DateUtils;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class CallRequestListAdapter extends RecyclerView.Adapter<CallRequestListAdapter.MyViewHolder> {

    Context moContext;
    List<JsonElement> jsonArrays;
    boolean isDeleteShow;
    ImageLoader mImageLoader;
    onItemClickLisnear mOnItemClickLisnear;
    private String ownerPropertyID;
    private String actionType = "";

    public CallRequestListAdapter(Context mContext, List<JsonElement> moListData, boolean isDeleted, String propertyID) {
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
        View view = LayoutInflater.from(moContext).inflate(R.layout.item_request_call_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            JsonElement mJsonObject = jsonArrays.get(position);
            holder.bideData(position, mJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArrays.size();
    }

    public void addData(List<JsonElement> moJsonArrayList) {
        jsonArrays.clear();
        jsonArrays.addAll(moJsonArrayList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imgRequestUser, ivActionMoreView;
        CustomAppCompatButton imageActionAccept, imageActionNewTime, imageActionReject;
        MaterialTextView tvRequestCallStatus, txtAddressDetails,txtSpecialInstruction;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tvRequestCallStatus = itemView.findViewById(R.id.tvRequestCallStatus);
            imageActionAccept = itemView.findViewById(R.id.imageActionAccept);
            imageActionNewTime = itemView.findViewById(R.id.imageActionNewTime);
            imageActionReject = itemView.findViewById(R.id.imageActionReject);

            ivActionMoreView = itemView.findViewById(R.id.ivActionMoreView);
            imgRequestUser = itemView.findViewById(R.id.imageRequestUser);
            txtAddressDetails = itemView.findViewById(R.id.txtAddressDetails);
            txtSpecialInstruction = itemView.findViewById(R.id.txtSpecialInstruction);

        }

        void bideData(int position, JsonElement mJsonObject) {
            JsonObject jsonObject = mJsonObject.getAsJsonObject();
            //header message set
            String userName = "";
            String show_date;

            //2013-02-27 21:06:30";
            if (!jsonObject.get("owner_id").getAsString().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                //Requested User Name
                String address = jsonObject.get("address").getAsString();
                //get start time
                show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                userName = moContext.getString(R.string.login_user_name_with_message, jsonObject.get("type").getAsString(), address
                        , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));

            } else {
                //other user id
                actionType = Constants.ACTION_BOTTOM_SHEET_USER;
                String address = jsonObject.get("address").getAsString();
                //get start time
                show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                userName = moContext.getString(R.string.user_name_with_message, jsonObject.get("UserDetails").getAsJsonObject().get("full_name").getAsString(), jsonObject.get("type").getAsString(), address
                        , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));
            }

            if (!jsonObject.get("owner_id").getAsString().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                //Is this not a propertyu owner
                if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("0")){
                    //pending waiting for user approval
                    updateUi(false, Constants.WATING_ADMIN_REQUEST, moContext.getResources().getColor(R.color.colorPrimary), jsonObject.get("created_type").getAsString());

                    //Requested User Name
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.login_user_name_request_new_time_with_message, jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));

                } else if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("1")){

                    //Requested User Name
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.login_user_name_request_new_time_with_message, jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));
                } else if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("2")){

                    //Requested User Name
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.login_user_name_request_new_time_with_message, jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));
                } else if (jsonObject.get("created_type").getAsString().equalsIgnoreCase("user") && jsonObject.get("status").getAsString().equalsIgnoreCase("0")) {
                    //pending waiting for admin approval
                    updateUi(false, Constants.WATING_USER_REQUEST, moContext.getResources().getColor(R.color.colorPrimary), jsonObject.get("created_type").getAsString());
                }
            } else {
                //Is this a propertyu owner
                if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("0")){
                    //pending waiting for user approval
                    updateUi(true, "", moContext.getResources().getColor(R.color.transparent), jsonObject.get("created_type").getAsString());
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.user_name_request_new_time_with_message, jsonObject.get("UserDetails").getAsJsonObject().get("full_name").getAsString(), jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));

                } else if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("1")){
                    //Requested User Name
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.user_name_request_new_time_with_message, jsonObject.get("UserDetails").getAsJsonObject().get("full_name").getAsString(), jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));
                } else if(jsonObject.get("created_type").getAsString().equalsIgnoreCase("admin") && jsonObject.get("status").getAsString().equalsIgnoreCase("2")){
                    //Requested User Name
                    String address = jsonObject.get("address").getAsString();
                    //get start time
                    show_date = DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString());
                    userName = moContext.getString(R.string.user_name_request_new_time_with_message, jsonObject.get("UserDetails").getAsJsonObject().get("full_name").getAsString(), jsonObject.get("type").getAsString(), address
                            , show_date, uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString()), uTCToLocal("yyyy-MM-dd HH:mm:ss", "hh:mm a", jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString()));
                } else if (jsonObject.get("created_type").getAsString().equalsIgnoreCase("user") && jsonObject.get("status").getAsString().equalsIgnoreCase("0")) {
                    //pending waiting for admin approval
                    updateUi(true, "", moContext.getResources().getColor(R.color.colorPrimary), jsonObject.get("created_type").getAsString());
                }
            }

            //set message full
            txtAddressDetails.setText(userName);

            if(jsonObject.get("instruction").getAsString()!=null && !jsonObject.get("instruction").getAsString().equalsIgnoreCase("")){
                txtSpecialInstruction.setVisibility(View.VISIBLE);
                //open popup
                txtSpecialInstruction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CommonDialogView.getInstance().showCommonDialog(moContext, "",
                                jsonObject.get("instruction").getAsString()
                                , ""
                                , "Ok"
                                , false, true, true, false, false, new CommonDialogCallBack() {
                                    @Override
                                    public void onDialogYes(View view) {

                                    }

                                    @Override
                                    public void onDialogCancel(View view) {
                                    }
                                });
                    }
                });

            }else {
                txtSpecialInstruction.setVisibility(View.GONE);
            }

            if (jsonObject.get("status").getAsString().equalsIgnoreCase("1")) {
                //accept
                updateUi(false, Constants.REQUEST_ACCEPT, moContext.getResources().getColor(R.color.quantum_googgreen), jsonObject.get("created_type").getAsString());
            }
            if (jsonObject.get("status").getAsString().equalsIgnoreCase("2")) {
                //reject
                updateUi(false, Constants.REQUEST_REJECT, moContext.getResources().getColor(R.color.red), jsonObject.get("created_type").getAsString());

            }
            if (jsonObject.get("status").getAsString().equalsIgnoreCase("3")) {
                //new request time
                updateUi(false, Constants.NEW_REQUEST, moContext.getResources().getColor(R.color.text_color_black), jsonObject.get("created_type").getAsString());
            }
            String profilePic = "";


            //other user id
            try {
                if (!jsonObject.get("UserDetails").getAsJsonObject().get("profile_pic").isJsonNull())
                    profilePic = jsonObject.get("UserDetails").getAsJsonObject().get("profile_pic").getAsString();
                if (profilePic != null && !profilePic.equalsIgnoreCase("")) {
                    Glide.with(moContext).load(profilePic).apply(new RequestOptions().circleCrop()).into(imgRequestUser);
                } else {
                    imgRequestUser.setImageResource(R.mipmap.user);
                }
            } catch (JsonIOException e) {
                imgRequestUser.setImageResource(R.mipmap.user);
            }


            imageActionAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.ACCEPT, mJsonObject, moContext.getResources().getString(R.string.accept_alert_request_message));
                }
            });
            imageActionNewTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.NEWTIME, mJsonObject, moContext.getResources().getString(R.string.new_alert_rrequest_message));
                }
            });
            imageActionReject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openDialogView(Constants.REJECT, mJsonObject, moContext.getResources().getString(R.string.reject_alert_request_message));
                }
            });

            ivActionMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickLisnear != null) {

                        if (jsonObject.get("status").getAsString().equalsIgnoreCase("0")) {
                            //edit availabel
                            actionType = Constants.ACTION_BOTTOM_SHEET_OWNER;
                        } else {
                            //add calender
                            actionType = Constants.ACTION_BOTTOM_SHEET_USER;
                        }
                        mOnItemClickLisnear.onClickEvent(getAdapterPosition(), mJsonObject, actionType, "");
                    }
                }
            });

        }

        void updateUi(boolean isShowed, String requestStatus, int colorCode, String createdType) {
            if (isShowed) {
                imageActionAccept.setVisibility(View.VISIBLE);
                imageActionReject.setVisibility(View.VISIBLE);
                if (!createdType.equalsIgnoreCase("admin")) {
                    imageActionNewTime.setVisibility(View.VISIBLE);
                } else {
                    imageActionNewTime.setVisibility(View.GONE);
                }
                tvRequestCallStatus.setVisibility(View.GONE);
                ivActionMoreView.setVisibility(View.GONE);


            } else {
                imageActionAccept.setVisibility(View.GONE);
                imageActionNewTime.setVisibility(View.GONE);
                imageActionReject.setVisibility(View.GONE);
                tvRequestCallStatus.setVisibility(View.VISIBLE);
                ivActionMoreView.setVisibility(View.VISIBLE);

                tvRequestCallStatus.setTextColor(colorCode);
                tvRequestCallStatus.setText(requestStatus);
            }
        }

        void openDialogView(String statusCode, JsonElement mJsonObject, String message) {

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

            if (statusCode.equalsIgnoreCase(Constants.NEWTIME)) {
                txt_001.setText("Submit");
            }
            JsonObject jsonObject = mJsonObject.getAsJsonObject();

            mDialog.findViewById(R.id.txt_01).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (statusCode) {
                        case Constants
                                .ACCEPT:
                            updateUi(false, Constants.REQUEST_ACCEPT, moContext.getResources().getColor(R.color.quantum_googgreen), jsonObject.get("created_type").getAsString());
                            //openDialogView(mActionType, mJsonObject, getResources().getString(R.string.accept_alert_bidd_message));
                            break;
                        case Constants
                                .REJECT:
                            updateUi(false, Constants.REQUEST_REJECT, moContext.getResources().getColor(R.color.red), jsonObject.get("created_type").getAsString());
                            break;
                        case Constants
                                .NEWTIME:
                            updateUi(false, Constants.NEW_REQUEST, moContext.getResources().getColor(R.color.text_color_black), jsonObject.get("created_type").getAsString());
                            break;
                    }

                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(getAdapterPosition(), mJsonObject, statusCode, "");
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
        void onClickEvent(int position, JsonElement mJsonObject, String mActionType, String priceValue);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }

    public static String uTCToLocal(String dateFormatInPut, String dateFomratOutPut, String datesToConvert) {


        String dateToReturn = datesToConvert;

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormatInPut);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date gmt = null;

        SimpleDateFormat sdfOutPutToSend = new SimpleDateFormat(dateFomratOutPut);
        sdfOutPutToSend.setTimeZone(TimeZone.getDefault());

        try {

            gmt = sdf.parse(datesToConvert);
            dateToReturn = sdfOutPutToSend.format(gmt);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateToReturn;
    }

}