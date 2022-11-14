package com.flippbidd.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.DealData;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class OtherFMDAdapterList extends RecyclerView.Adapter<OtherFMDAdapterList.MyViewHolder> {

    Context moContext;
    List<DealData> jsonArrays;
    onItemClickLisnear mOnItemClickLisnear;

    public OtherFMDAdapterList(Context mContext, List<DealData> moListData) {
        this.moContext = mContext;
        jsonArrays = new ArrayList<>();
        jsonArrays.addAll(moListData);
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(moContext).inflate(R.layout.item_others_serach_ui, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        try {
            DealData mJsonObject = jsonArrays.get(position);
            holder.bideData(position, mJsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return jsonArrays.size();
    }

    public void addData(List<DealData> moJsonArrayList) {
        jsonArrays.clear();
        jsonArrays.addAll(moJsonArrayList);
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        CustomTextView moTvSearchAddress, moTextViewofChatTime, mTextPeopleCounts;
        RelativeLayout viewTimeCountDown;
        ImageView moIvDocumentView;
        CustomTextView moBtnAddMe;
        RelativeLayout relativeUserListView, relativeMoreCounts;

        CircleImageView imageOne, imageTwo, imageThree, imageFour, imageFive;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            moTvSearchAddress = itemView.findViewById(R.id.tvSearchAddress);
            moTextViewofChatTime = itemView.findViewById(R.id.textViewofChatTime);
            viewTimeCountDown = itemView.findViewById(R.id.viewTimeCountDown);
            moBtnAddMe = itemView.findViewById(R.id.btnAddMe);

            mTextPeopleCounts = itemView.findViewById(R.id.txtPeopleCounts);
            relativeMoreCounts = itemView.findViewById(R.id.relativeMoreCounts);
            relativeUserListView = itemView.findViewById(R.id.relativeUserListView);
            imageOne = itemView.findViewById(R.id.imageOne);
            imageTwo = itemView.findViewById(R.id.imageTwo);
            imageThree = itemView.findViewById(R.id.imageThree);
            imageFour = itemView.findViewById(R.id.imageFour);
            imageFive = itemView.findViewById(R.id.imageFive);

        }

        void bideData(int position, DealData mJsonObject) {
            if (mJsonObject.getIsDelete().equalsIgnoreCase("0")) {
                moTvSearchAddress.setTextColor(moContext.getResources().getColor(R.color.quantum_orange200));
                //click view
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, mJsonObject, "view");
                        }
                    }
                });
            }
            moTvSearchAddress.setText(mJsonObject.getAddress());
            //other deal
            if (!mJsonObject.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                moBtnAddMe.setVisibility(View.VISIBLE);
                moBtnAddMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, mJsonObject, "Add Me");
                        }
                    }
                });
            } else {
                moBtnAddMe.setVisibility(View.GONE);
            }
            if (mJsonObject.getOtherUserList() != null && mJsonObject.getOtherUserList().size() != 0) {
                for (int i = 0; i < mJsonObject.getOtherUserList().size(); i++) {
                    switch (i) {
                        case 0:
                            imageOne.setVisibility(View.VISIBLE);
                            Glide.with(moContext)
                                    .load(mJsonObject.getOtherUserList().get(i).getProfilePic())
                                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                                    .into(imageOne);
                            imageOne.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                        case 1:
                            imageTwo.setVisibility(View.VISIBLE);
                            Glide.with(moContext)
                                    .load(mJsonObject.getOtherUserList().get(i).getProfilePic())
                                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                                    .into(imageTwo);
                            imageTwo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                        case 2:
                            imageThree.setVisibility(View.VISIBLE);
                            Glide.with(moContext)
                                    .load(mJsonObject.getOtherUserList().get(i).getProfilePic())
                                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                                    .into(imageThree);
                            imageThree.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                        case 3:
                            imageFour.setVisibility(View.VISIBLE);
                            Glide.with(moContext)
                                    .load(mJsonObject.getOtherUserList().get(i).getProfilePic())
                                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                                    .into(imageFour);
                            imageFour.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                        case 4:
                            imageFive.setVisibility(View.VISIBLE);
                            Glide.with(moContext)
                                    .load(mJsonObject.getOtherUserList().get(i).getProfilePic())
                                    .apply(new RequestOptions().centerCrop().placeholder(R.mipmap.user).error(R.mipmap.user))
                                    .into(imageFive);
                            imageFive.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                        case 5:
                            relativeMoreCounts.setVisibility(View.VISIBLE);
                            int maxCounts = mJsonObject.getOtherUserList().size();
                            mTextPeopleCounts.setText(getCounts(maxCounts));
                            relativeMoreCounts.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    relativeUserListView.performClick();
                                }
                            });
                            break;
                    }
                }
                relativeUserListView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, mJsonObject, "User List");
                        }
                    }
                });
            }
        }
    }

    private String getCounts(int size) {
        int reamingCounts = size - 5;
        return moContext.getResources().getString(R.string.people_counts, String.valueOf(reamingCounts));
    }

    public interface onItemClickLisnear {
        void onClickEvent(int position, DealData mJsonObject, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }


}
