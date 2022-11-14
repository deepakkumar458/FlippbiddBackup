package com.flippbidd.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.RequestData.RequestProfileData;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.google.android.material.textview.MaterialTextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RequestDocumentUserListAdapter extends RecyclerView.Adapter<RequestDocumentUserListAdapter.MyViewHolder> {

    Context moContext;
    List<RequestProfileData> jsonArrays;
    boolean isDeleteShow;
    ImageLoader mImageLoader;
    onItemClickLisnear mOnItemClickLisnear;
    private String ownerPropertyID;

    public RequestDocumentUserListAdapter(Context mContext, List<RequestProfileData> moListData, boolean isDeleted, String propertyID) {
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
        View view = LayoutInflater.from(moContext).inflate(R.layout.item_request_doc_user_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            RequestProfileData mJsonObject = jsonArrays.get(position);
            holder.bideData(position, mJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArrays.size();
    }

    public void addData(List<RequestProfileData> moJsonArrayList) {
        jsonArrays.clear();
        jsonArrays.addAll(moJsonArrayList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView tvUserProfileName, tvRequestDocumentName, tvUserUpdateDate;
        CircleImageView ivUserProfileView;
        RelativeLayout relativeMainView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            relativeMainView = itemView.findViewById(R.id.relativeMainView);
            ivUserProfileView = itemView.findViewById(R.id.ivUserProfileView);
            tvUserProfileName = itemView.findViewById(R.id.tvUserProfileName);
            tvUserUpdateDate = itemView.findViewById(R.id.tvUserUpdateDate);
            tvRequestDocumentName = itemView.findViewById(R.id.tvRequestDocumentName);

        }

        void bideData(int position, RequestProfileData mJsonObject) {
            tvUserProfileName.setText(mJsonObject.getUserDetails().getFullName());
            String dataFolder = mJsonObject.getFolderData();
            if (!mJsonObject.getOther().equalsIgnoreCase("")) {
                if (dataFolder != null && !dataFolder.equalsIgnoreCase("")) {
                    dataFolder = mJsonObject.getFolderData() + "," + mJsonObject.getOther();
                } else {
                    dataFolder = mJsonObject.getOther();
                }
            }
            tvRequestDocumentName.setText(moContext.getResources().getString(R.string.view_request_message, dataFolder));
            //2021-01-06 03:30:53
            tvUserUpdateDate.setText(Utils.getConvertData1(mJsonObject.getCreatedAt(), "yyyy-MM-dd hh:mm:ss", "MM/dd/yyyy"));

            if (mJsonObject.getUserDetails().getProfilePic() != null && !mJsonObject.getUserDetails().getProfilePic().equalsIgnoreCase("")) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.ARGB_4444)
                        .build();
                mImageLoader.displayImage(mJsonObject.getUserDetails().getProfilePic(), ivUserProfileView, options);
            } else {
                ivUserProfileView.setImageResource(R.mipmap.user);
            }

            if (mJsonObject.getIsView() == 1) {
                updateUi(moContext.getResources().getColor(R.color.color_light_blue_qb));
            } else {
                updateUi(moContext.getResources().getColor(R.color.color_white));
            }


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


        }

        void updateUi(int colorCode) {
            relativeMainView.setBackgroundColor(colorCode);
        }
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, CommonData.Bidds mJsonObject, String mActionType, String priceValue);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }


}