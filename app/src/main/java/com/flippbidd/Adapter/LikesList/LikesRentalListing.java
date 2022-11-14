package com.flippbidd.Adapter.LikesList;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Likes.LikesData;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikesRentalListing extends RecyclerView.Adapter<LikesRentalListing.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<LikesData> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;

    //Timer
    private final List<MyViewHolder> lstHolders;

    public LikesRentalListing(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
        lstHolders = new ArrayList<>();
    }

    public void addAll(List<LikesData> arrayList) {
        mPropertyDataList.clear();
        mPropertyDataList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void updateAdapter(int position) {
        notifyItemChanged(position);
    }

    public void deleteAdapter(int position) {
        mPropertyDataList.remove(position);
        notifyDataSetChanged();
    }

    private void setFadeAnimation(View view) {

        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        //adapter_property_data_listing_layout old layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_new_property_item_list_layout, parent, false);

        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.isRecyclable();
        final LikesData mPData = mPropertyDataList.get(position);
        holder.setData(mPData, holder);
        if (holder != null) {
            holder.linearLayout_first.setVisibility(View.GONE);
            holder.linearLayout_second.setVisibility(View.VISIBLE);

            //new design code
            holder.relativeLayoutOfListedUnListedButton.setVisibility(View.INVISIBLE);

//            if (mPData.getPropertyDetails().getSenderQbId() != null && !mPData.getPropertyDetails().getSenderQbId().equalsIgnoreCase("")) {
            if (!UserPreference.getInstance(mContext).getUserDetail().getLoginId().equalsIgnoreCase(mPData.getUserDetails().getLoginId())) {
//                    holder.lenarLatoutOfMessageViewBox.setVisibility(View.VISIBLE);

                //show visibality of components of property/rental avaliblity or not
                if (mPData.getPropertyDetails().getIsAvailable().equalsIgnoreCase("0")) {
                    //0 unavailable

                    holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);

                    //hide components
                    //message button
                    holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                    //delete button
                    holder.imageViewDeleteIcon.setVisibility(View.GONE);
                    //edit button
                    holder.imageEditPropertyIcon.setVisibility(View.GONE);

                } else {
                    //1 available
                    holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    //show components

                    if (mPData.getPropertyDetails().getSenderQbId() != null && !mPData.getPropertyDetails().getSenderQbId().equalsIgnoreCase("")) {
                        holder.lenarLatoutOfMessageViewBox.setVisibility(View.VISIBLE);
                    } else {
                        holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                    }
                    //delete button
                    holder.imageViewDeleteIcon.setVisibility(View.VISIBLE);
                    //edit button
                    holder.imageEditPropertyIcon.setVisibility(View.VISIBLE);
                }
            } else {
                holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                //show visibality of components of property/rental avaliblity or not
                if (mPData.getPropertyDetails().getIsAvailable().equalsIgnoreCase("0")) {
                    //0 unavailable

                    holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    //delete button
                    holder.imageViewDeleteIcon.setVisibility(View.GONE);
                    //edit button
                    holder.imageEditPropertyIcon.setVisibility(View.GONE);

                } else {
                    //1 available
                    holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    //show components
                    //delete button
                    holder.imageViewDeleteIcon.setVisibility(View.VISIBLE);
                    //edit button
                    holder.imageEditPropertyIcon.setVisibility(View.VISIBLE);
                }
            }
//            } else {
//                holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
//            }


            //check user Type
            holder.updateTimeRemaining(System.currentTimeMillis());
           /* if (UserPreference.getInstance(mContext).getUserDetail().getPlan() != null && !UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase("")) {

                if (UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FREE)
                        || UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FULL)) {
                    holder.viewTimeCountDown.setVisibility(View.VISIBLE);


                } else {

                    holder.viewTimeCountDown.setVisibility(View.GONE);
                }
            } else {
                holder.viewTimeCountDown.setVisibility(View.GONE);
            }*/

            if (mPData.getIsLike() != null) {
                if (mPData.getIsLike().equalsIgnoreCase("1")) {
                    holder.imageViewHeartIcon.setImageResource(R.drawable.fav);
                } else {
                    holder.imageViewHeartIcon.setImageResource(R.drawable.unfav);
                }
            }
            //new code
//            if (mPData.getPropertyDetails().getAddress() != null && mPData.getPropertyDetails().getState() != null && mPData.getPropertyDetails().getCity() != null) {
//                holder.textViewPropertyAddressTitle.setText(mPData.getPropertyDetails().getAddress() + ",\n" + mPData.getPropertyDetails().getCity() + "," + mPData.getPropertyDetails().getState());
//            }
//
//            if (mPData.getPropertyDetails().getRentAmount() != null) {
//                holder.textViewPropertyPriceTitle.setText("$ " + mPData.getPropertyDetails().getRentAmount());
//            }

            //new design code
            holder.textViewPropertyAddress.setText(mPData.getPropertyDetails().getAddress());
            holder.textViewPropertyTypeTitle.setText(mPData.getPropertyDetails().getBuildingType());
            holder.textViewPropertyDetailsBedsNo.setText(mPData.getPropertyDetails().getBedNos());
            holder.textViewPropertyDetailsBathNo.setText(mPData.getPropertyDetails().getBathNos());
            holder.textViewAreaValues.setText(mPData.getPropertyDetails().getArea() + " sqft");
            //convert dollr to million dollar
            if (mPData.getPropertyDetails().getRentAmount() != null && !mPData.getPropertyDetails().getRentAmount().equalsIgnoreCase("")) {
                holder.textViewPropertyDetailsMilValues.setVisibility(View.VISIBLE);
                int dollr = Integer.parseInt(mPData.getPropertyDetails().getRentAmount());
                if (dollr <= 1000000) {
//                            int million = (dollr / 1000);
                    holder.textViewPropertyDetailsMilValues.setText("$" + mPData.getPropertyDetails().getRentAmount());
                } else if (dollr <= 100000000) {
//                            int billion = (dollr / 10000);
                    holder.textViewPropertyDetailsMilValues.setText("$" + mPData.getPropertyDetails().getRentAmount());
                } else {
                    holder.textViewPropertyDetailsMilValues.setText("$" + String.valueOf(dollr));
                }
            } else {
                holder.textViewPropertyDetailsMilValues.setVisibility(View.INVISIBLE);
            }


            if (mPData.getPropertyDetails().getImages() != null && mPData.getPropertyDetails().getImages().size() != 0) {
//                int lastIndex = mPData.getPropertyDetails().getImages().size() - 1;
                holder.lottieAnimationProgress.setVisibility(View.VISIBLE);
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheInMemory(true)
                        .showImageOnFail(R.drawable.no_image_icon)
                        .bitmapConfig(Bitmap.Config.ARGB_4444)
                        .cacheOnDisk(false)
                        .build();
                imageLoader.displayImage(mPData.getPropertyDetails().getImages().get(0).getImageUrl(), holder.property_image, options);
            } else {
                if (holder.lottieAnimationProgress.isAnimating()) {
                    holder.lottieAnimationProgress.pauseAnimation();
                }
                holder.lottieAnimationProgress.setVisibility(View.GONE);
                holder.property_image.setImageResource(R.drawable.no_image_icon);
            }

            holder.imageViewHeartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, mPData, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, mPData, Constants.ACTION.VIEW_ACTION);
                    }
                }
            });

            holder.lenarLatoutOfMessageViewBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mPData.getPropertyDetails().getIsAvailable() != null) {
                        if (mPData.getPropertyDetails().getIsAvailable().equalsIgnoreCase("1")) {
                            if (mOnItemClickLisnear != null) {
                                mOnItemClickLisnear.onClickEvent(position, mPData, Constants.ACTION.MESSAGES_VIEW_ACTION);
                            }
                        }
                    }
                }
            });
        }

    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, LikesData mLikeData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    @Override
    public int getItemCount() {
        return mPropertyDataList.size();
    }

    public class MyViewHolder extends BaseViewHolder {

        @BindView(R.id.property_image)
        ImageView property_image;
        @BindView(R.id.textViewPropertyTitle)
        CustomTextView textViewPropertyTitle;
        @BindView(R.id.textViewBedsRoomTitle)
        CustomTextView textViewBedsRoomTitle;
        @BindView(R.id.textViewBathRoom)
        CustomTextView textViewBathRoom;
        @BindView(R.id.textViewSquareTitle)
        CustomTextView textViewSquareTitle;
        @BindView(R.id.textViewCityTitle)
        CustomTextView textViewCityTitle;
        @BindView(R.id.textViewRentsTitle)
        CustomTextView textViewRentsTitle;
        @BindView(R.id.linearLayout_second)
        LinearLayout linearLayout_second;
        @BindView(R.id.linearLayout_first)
        LinearLayout linearLayout_first;
        @BindView(R.id.imageViewDeleteIcon)
        ImageView imageViewDeleteIcon;
        @BindView(R.id.imageEditPropertyIcon)
        ImageView imageEditPropertyIcon;
        @BindView(R.id.imageViewUnavailableIcon)
        ImageView imageViewUnavailableIcon;
        @BindView(R.id.imageViewHeartIcon)
        ImageView imageViewHeartIcon;
        @BindView(R.id.imageViewUnavailableIconView)
        ImageView imageViewUnavailableIconView;
        @BindView(R.id.lottieAnimationProgress)
        LottieAnimationView lottieAnimationProgress;
        @BindView(R.id.lenarLatoutOfMessageViewBox)
        LinearLayout lenarLatoutOfMessageViewBox;
        @BindView(R.id.textViewofChatTime)
        CustomTextView textViewofChatTime;
        @BindView(R.id.viewTimeCountDown)
        RelativeLayout viewTimeCountDown;

        //new add
//        @BindView(R.id.textViewPropertyPriceTitle)
//        CustomTextView textViewPropertyPriceTitle;
        @BindView(R.id.textViewPropertyAddress)
        CustomTextView textViewPropertyAddress;

        //new design code
        @BindView(R.id.textViewPropertyDetailsBedsNo)
        CustomTextView textViewPropertyDetailsBedsNo;
        @BindView(R.id.textViewPropertyDetailsBathNo)
        CustomTextView textViewPropertyDetailsBathNo;
        @BindView(R.id.textViewAreaValues)
        CustomTextView textViewAreaValues;
        @BindView(R.id.textViewPropertyTypeTitle)
        CustomTextView textViewPropertyTypeTitle;
        @BindView(R.id.relativeLayoutOfListedUnListedButton)
        RelativeLayout relativeLayoutOfListedUnListedButton;
        @BindView(R.id.textViewPropertyDetailsMilValues)
        CustomTextView textViewPropertyDetailsMilValues;


        LikesData mPData;

        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }

        public void setData(LikesData item, MyViewHolder holder) {
            mPData = item;
            //check user Type
           /* if (UserPreference.getInstance(mContext).getUserDetail().getPlan() != null && !UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase("")) {

                if (UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FREE)
                        || UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FULL)) {
                    holder.viewTimeCountDown.setVisibility(View.VISIBLE);

                } else {
                    holder.viewTimeCountDown.setVisibility(View.GONE);
                }
            } else {
                holder.viewTimeCountDown.setVisibility(View.GONE);
            }*/
            holder.updateTimeRemaining(System.currentTimeMillis());

        }

        public void updateTimeRemaining(long currentTime) {
//            long timeDiff = (Utils.getDateTimeToLong(mPData.getExpriedData()) - currentTime);
           /* long timeDiff = mPData.getPropertyDetails().get24hDate() * 1000 - currentTime;
            if (timeDiff > 0) {
                textViewofChatTime.setText("" + Utils.getDate(mPData.getPropertyDetails().get24hDate()));
                mPData.getPropertyDetails().setExpiriedStatus(false); //change code to false
                viewTimeCountDown.setVisibility(View.VISIBLE);
            } else {
                mPData.getPropertyDetails().setExpiriedStatus(false);
                viewTimeCountDown.setVisibility(View.GONE);
            }*/
            textViewofChatTime.setText("" + Utils.getConvertData(mPData.getPropertyDetails().getCreatedDate()));
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

}
