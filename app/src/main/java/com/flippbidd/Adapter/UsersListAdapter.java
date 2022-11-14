package com.flippbidd.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.OtherUserList;
import com.flippbidd.R;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.utils.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

public class UsersListAdapter extends RecyclerView.Adapter<UsersListAdapter.MyViewHolder> {

    Context moContext;
    List<OtherUserList> jsonArrays;
    onItemClickLisnear mOnItemClickLisnear;
    Boolean _isFromUser;


    public UsersListAdapter(Context mContext, List<OtherUserList> moListData, boolean isFromUser) {
        this.moContext = mContext;
        jsonArrays = new ArrayList<>();
        jsonArrays.addAll(moListData);
        _isFromUser = isFromUser;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(moContext).inflate(R.layout.item_bidd_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            OtherUserList mJsonObject = jsonArrays.get(position);
            holder.bideData(position, mJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArrays.size();
    }

    public void addData(List<OtherUserList> moJsonArrayList) {
        jsonArrays.clear();
        jsonArrays.addAll(moJsonArrayList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView txtBiddByName, txtBidPrice;
        CircleImageView imgBiddUser;
        ImageView moIvDocumentView;
        AppCompatImageView imageActionAccept, imageActionCountered, imageActionReject, imageaudiocall, imagevideocall;
        CustomTextView tvBiddMaterialStatus;
        LinearLayoutCompat linearStatusActionBox, linearcall;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //tvBiddMaterialStatus = itemView.findViewById(R.id.tvBiddMaterialStatus);
            //tvBiddMaterialStatus.setVisibility(View.GONE);
            //imageActionAccept = itemView.findViewById(R.id.imageActionAccept);
            //imageActionCountered = itemView.findViewById(R.id.imageActionCountered);
            //imageActionReject = itemView.findViewById(R.id.imageActionReject);

            imgBiddUser = itemView.findViewById(R.id.imageBiddUser);
            txtBiddByName = itemView.findViewById(R.id.txtBiddByName);
            txtBidPrice = itemView.findViewById(R.id.txtBidPrice);
            txtBidPrice.setVisibility(View.GONE);
            // linearStatusActionBox = itemView.findViewById(R.id.linearStatusActionBoxx);
            linearcall = itemView.findViewById(R.id.linearcall);
          //  linearcall.setVisibility(View.GONE);
            // linearStatusActionBox.setVisibility(View.GONE);
            imageaudiocall = itemView.findViewById(R.id.imageaudiocall);
            imagevideocall = itemView.findViewById(R.id.imagevideocall);

        }

        void bideData(int position, OtherUserList mJsonObject) {
            if (_isFromUser) {
                linearcall.setVisibility(View.VISIBLE);
            } else {
             //  linearcall.setVisibility(View.GONE);
            }
            if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mJsonObject.getLoginId())) {
                txtBiddByName.setText(mJsonObject.getFullName() + " (You)");
                linearcall.setVisibility(View.GONE);
            } else {
                txtBiddByName.setText(mJsonObject.getFullName());
                if (_isFromUser) {
                    linearcall.setVisibility(View.VISIBLE);
                } else {
                   // linearcall.setVisibility(View.GONE);
                }

            }

            if (mJsonObject.getProfilePic() != null && !mJsonObject.getProfilePic().equalsIgnoreCase("")) {
                Glide.with(moContext)
                        .load(mJsonObject.getProfilePic())
                        .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                        .into(imgBiddUser);
            } else {
                imgBiddUser.setImageResource(R.mipmap.user);
            }
            imageaudiocall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewCallService.dial(moContext, mJsonObject.getFullName(), mJsonObject.getEmail_address(), false);
                    PreferenceUtils.setCalleeId(mJsonObject.getLoginId());
                }
            });

            imagevideocall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NewCallService.dial(moContext, mJsonObject.getFullName(), mJsonObject.getEmail_address(), true);
                    PreferenceUtils.setCalleeId(mJsonObject.getLoginId());
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mJsonObject.getLoginId() != null && !mJsonObject.getLoginId().equalsIgnoreCase(""))
                        (moContext).startActivity(new Intent(moContext, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, mJsonObject.getLoginId()));
                }
            });
        }
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, CommonData.Bidds mJsonObject, String mActionType, String priceValue);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

}