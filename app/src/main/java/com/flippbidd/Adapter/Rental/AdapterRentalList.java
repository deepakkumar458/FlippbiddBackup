package com.flippbidd.Adapter.Rental;

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
import com.flippbidd.Fragments.PropertyList;
import com.flippbidd.Model.Response.CommonList.CommonData;
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

public class AdapterRentalList extends RecyclerView.Adapter<AdapterRentalList.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<CommonData> mRentalDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;
    private boolean isTimerStarted = false;

    //Timer
    private final List<MyViewHolder> lstHolders;

    public AdapterRentalList(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
        lstHolders = new ArrayList<>();


    }

    public void addAll(List<CommonData> arrayList) {
        mRentalDataList.clear();
        mRentalDataList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void updateAdapter(int position) {
        notifyItemChanged(position);
    }

    public void deleteAdapter(int position) {
        mRentalDataList.remove(position);
        if (mRentalDataList.size() == 0) {
            PropertyList.setNodataView("Rental");
        }
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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.isRecyclable();
        final CommonData mPData = mRentalDataList.get(position);
        holder.setData(mRentalDataList.get(position), holder);
        if (holder != null) {

            //new design code
            holder.relativeLayoutOfListedUnListedButton.setVisibility(View.INVISIBLE);

            //show screen wise box
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                holder.linearLayout_first.setVisibility(View.GONE);
                holder.linearLayout_second.setVisibility(View.VISIBLE);
            } else {
                holder.linearLayout_first.setVisibility(View.VISIBLE);
                holder.linearLayout_second.setVisibility(View.GONE);
            }
//            if (mPData.getSender_qb_id() != null && !mPData.getSender_qb_id().equalsIgnoreCase("")) {
            if (!UserPreference.getInstance(mContext).getUserDetail().getLoginId().equalsIgnoreCase(mPData.getData().getLoginId())) {
//                    holder.lenarLatoutOfMessageViewBox.setVisibility(View.VISIBLE);

                //show visibality of components of property/rental avaliblity or not
                if (mPData.getIsAvailable().equalsIgnoreCase("0")) {
                    //0 unavailable
                    if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                        holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    }

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
                    if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
//                        if (mPData.getSender_qb_id() != null && !mPData.getSender_qb_id().equalsIgnoreCase("")) {
                            holder.lenarLatoutOfMessageViewBox.setVisibility(View.VISIBLE);
                        /*} else {
                            holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                        }*/
                    } else {
                        holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                    }
                    //message button
                    holder.lenarLatoutOfMessageViewBox.setVisibility(View.VISIBLE);
                    //delete button
                    holder.imageViewDeleteIcon.setVisibility(View.VISIBLE);
                    //edit button
                    holder.imageEditPropertyIcon.setVisibility(View.VISIBLE);
                }
            } else {
                holder.lenarLatoutOfMessageViewBox.setVisibility(View.GONE);
                //show visibality of components of property/rental avaliblity or not
                if (mPData.getIsAvailable().equalsIgnoreCase("0")) {
                    //0 unavailable
                    if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                        holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                    } else {
                        holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    }
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

            if (mPData.isLike()) {
                holder.imageViewHeartIcon.setImageResource(R.drawable.fav);
            } else {
                holder.imageViewHeartIcon.setImageResource(R.drawable.unfav);
            }

            //new code
            holder.textViewPropertyAddress.setText(mPData.getAddress());
            //end

            //new design code
            holder.textViewPropertyTypeTitle.setText(mPData.getBuildingType());
            holder.textViewPropertyDetailsBedsNo.setText(mPData.getBedNos());
            holder.textViewPropertyDetailsBathNo.setText(mPData.getBathNos());
            holder.textViewAreaValues.setText(mPData.getArea() + " sqft");

            //convert dollr to million dollar
            if (mPData.getRentAmount() != null && !mPData.getRentAmount().equalsIgnoreCase("")) {
                holder.textViewPropertyDetailsMilValues.setVisibility(View.VISIBLE);
                int dollr = Integer.parseInt(mPData.getRentAmount());
                if (dollr <= 1000000) {
//                            int million = (dollr / 1000);
                    holder.textViewPropertyDetailsMilValues.setText("$" + mPData.getRentAmount());
                } else if (dollr <= 100000000) {
//                            int billion = (dollr / 10000);
                    holder.textViewPropertyDetailsMilValues.setText("$" + mPData.getRentAmount());
                } else {
                    holder.textViewPropertyDetailsMilValues.setText("$" + String.valueOf(dollr));
                }
            } else {
                holder.textViewPropertyDetailsMilValues.setVisibility(View.INVISIBLE);
            }


            if (mPData.getImages() != null && mPData.getImages().size() != 0) {
                //reversed order
                if (mPData.getImages().get(0).getImageUrl() != null && !mPData.getImages().get(0).getImageUrl().equalsIgnoreCase("")) {
                    holder.lottieAnimationProgress.setVisibility(View.VISIBLE);
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                            .cacheOnDisc()
                            .cacheInMemory()
                            .showImageOnFail(R.drawable.no_image_icon)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    imageLoader.displayImage(mPData.getImages().get(0).getImageUrl(), holder.property_image, options);

                } else {
                    if (holder.lottieAnimationProgress.isAnimating()) {
                        holder.lottieAnimationProgress.pauseAnimation();
                    }
                    holder.lottieAnimationProgress.setVisibility(View.GONE);
                    holder.property_image.setImageResource(R.drawable.no_image_icon);
                }

            } else {
                if (holder.lottieAnimationProgress.isAnimating()) {
                    holder.lottieAnimationProgress.pauseAnimation();
                }
                holder.lottieAnimationProgress.setVisibility(View.GONE);
                holder.property_image.setImageResource(R.drawable.no_image_icon);

            }

            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                //check user Type
                holder.updateTimeRemaining(System.currentTimeMillis());
                /*if (UserPreference.getInstance(mContext).getUserDetail().getPlan() != null && !UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase("")) {

                    if (UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FREE)
                            || UserPreference.getInstance(mContext).getUserDetail().getPlan().equalsIgnoreCase(Constants.USER_TYPE.USER_FULL)) {

                        holder.viewTimeCountDown.setVisibility(View.VISIBLE);
                        holder.updateTimeRemaining(System.currentTimeMillis());
                    } else {

                        holder.viewTimeCountDown.setVisibility(View.GONE);
                    }
                } else {
                    holder.viewTimeCountDown.setVisibility(View.GONE);
                }*/
            } else {
                holder.viewTimeCountDown.setVisibility(View.GONE);
            }

            holder.imageViewDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        if (mPData.getIsAvailable().equalsIgnoreCase("1")) {
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.DELETE_ACTION);
                        }
                    }
                }
            });
            holder.imageEditPropertyIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        if (mPData.getIsAvailable().equalsIgnoreCase("1")) {
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.EDIT_ACTION);
                        }
                    }
                }
            });

            holder.imageViewUnavailableIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        if (mPData.getIsAvailable().equalsIgnoreCase("1")) {
                            mPData.setIsAvailable("0");
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);
                        } else {
                            mPData.setIsAvailable("1");
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                        }

                    }
                }
            });
            //chat view
            holder.lenarLatoutOfMessageViewBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        if (mPData.getIsAvailable().equalsIgnoreCase("1")) {
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.MESSAGES_VIEW_ACTION);
                        }
                    }
                }
            });

            holder.imageViewHeartIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (mPData.isLike()) {

                        mPData.setLike(false);
                        holder.imageViewHeartIcon.setImageResource(R.drawable.unfav);

                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                        }

                    } else {
                        mPData.setLike(true);
                        holder.imageViewHeartIcon.setImageResource(R.drawable.fav);

                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.PROPERTY_LIKES_ACTION);
                        }

                    }
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null)
                        mOnItemClickLisnear.onRentalListClickEvents(position, mPData, Constants.ACTION.VIEW_ACTION);
                }
            });
        }


    }

    public interface onItemClickLisnear {
        void onRentalListClickEvents(int position, CommonData mRentalData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    @Override
    public int getItemCount() {
        return mRentalDataList.size();
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
        @BindView(R.id.textViewPropertyDetailsMilValues)
        CustomTextView textViewPropertyDetailsMilValues;
//        //new add
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

        CommonData mPData;

        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }

        public void setData(CommonData item, MyViewHolder holder) {
            mPData = item;

            holder.updateTimeRemaining(System.currentTimeMillis());
           /* if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {

                holder.updateTimeRemaining(System.currentTimeMillis());
            }else {
                holder.viewTimeCountDown.setVisibility(View.GONE);
            }*/
        }

        public void updateTimeRemaining(long currentTime) {


            /*long timeDiff = mPData.getExpriedData() * 1000 - currentTime;
            if (timeDiff > 0) {
                textViewofChatTime.setText("" + Utils.getDate(mPData.getExpriedData()));
                mPData.setExpiriedStatus(false); //Change code true to false
                viewTimeCountDown.setVisibility(View.VISIBLE);

            } else {
                mPData.setExpiriedStatus(false);
                viewTimeCountDown.setVisibility(View.GONE);
            }*/
            textViewofChatTime.setText("" + Utils.getConvertData(mPData.getCreated_date()));
        }
    }


    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
//        startUpdateTimer();
    }
}
