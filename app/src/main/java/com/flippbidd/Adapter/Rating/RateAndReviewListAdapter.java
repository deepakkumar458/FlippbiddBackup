package com.flippbidd.Adapter.Rating;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Rating.RatingData;
import com.flippbidd.R;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.baseclass.BaseViewHolder;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.views.ShowMoreTextView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class RateAndReviewListAdapter extends RecyclerView.Adapter<RateAndReviewListAdapter.MyViewHolder> {

    //define object of Activity
    private Context mContext;
    private List<RatingData> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;


    public RateAndReviewListAdapter(Context mContext) {
        this.mContext = mContext;
        imageLoader = ImageLoader.getInstance();
    }


    public void addAll(List<RatingData> arrayList) {
        mPropertyDataList.clear();
        mPropertyDataList.addAll(arrayList);
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        viewHolder = getViewHolder(parent, inflater);

        return viewHolder;

    }

    @NonNull
    private MyViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        MyViewHolder viewHolder;
        //rating_list_adapter_layout
        View v1 = inflater.inflate(R.layout.item_new_rating_ui, parent, false);
        viewHolder = new MyViewHolder(v1);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        RatingData responseList = mPropertyDataList.get(position);
        if (holder != null) {
            holder.txtRateUserName.setText(responseList.getUserDetails().getFullName());
//            holder.txtRateUserName.setText(responseList.getUserDetails().getUsername());
            holder.txtRatingDescription.setText(responseList.getComment());


           /* holder.txtRatingDescription.addShowMoreText("Show more+");
            holder.txtRatingDescription.addShowLessText("Less");
            holder.txtRatingDescription.setShowingLine(3);
            holder.txtRatingDescription.setShowMoreColor(mContext.getResources().getColor(R.color.text_color_dark_grey_));
            holder.txtRatingDescription.setShowLessTextColor(mContext.getResources().getColor(R.color.text_color_dark_grey_));*/


            //calculation of rate
            String et1 = responseList.getRate1();
            String et2 = responseList.getRate2();
            String et3 = responseList.getRate3();
            String et4 = responseList.getRate4();
            String et5 = responseList.getRate5();

            float num1 = Float.parseFloat(et1);
            float num2 = Float.parseFloat(et2);
            float num3 = Float.parseFloat(et3);
            float num4 = Float.parseFloat(et4);
            float num5 = Float.parseFloat(et5);
            float calculate = ((num1 + num2 + num3 + num4 + num5) / 5);

            float result = calculate;
//            holder.ratingBarCount.setNumStars(5);
//            holder.ratingBarCount.setRating(result);

            holder.txtUserRatingCount.setText("" + result);
            String createdDate = responseList.getCreatedDate()+ " 11:11:11";
            holder.txtRatingTimes.setText(DateUtils.getTimeAgo(createdDate));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (responseList.getLoginId() != null && !responseList.getLoginId().equalsIgnoreCase(""))
                        (mContext).startActivity(new Intent(mContext, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, responseList.getLoginId()) );
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mPropertyDataList.size();
    }

    public class MyViewHolder extends BaseViewHolder {


        CustomTextView txtRateUserName, txtUserRatingCount, txtRatingTimes;
        ShowMoreTextView txtRatingDescription;
        ConstraintLayout contentRatingMain;

        public MyViewHolder(View loView) {
            super(loView);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, loView);
            contentRatingMain = loView.findViewById(R.id.contentRatingMain);
            contentRatingMain.setBackgroundResource(R.drawable.new_sadow_ui);
            txtRatingTimes = loView.findViewById(R.id.txtRatingTimes);
            txtUserRatingCount = loView.findViewById(R.id.txtUserRatingCount);
            txtRatingDescription = loView.findViewById(R.id.txtRatingDescription);
            txtRateUserName = loView.findViewById(R.id.txtRateUserName);


        }

    }


}
