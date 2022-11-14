package com.flippbidd.Adapter.LikesList;

import static com.flippbidd.Others.Constants.PRO_VERSION;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Likes.LikesData;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseViewHolder;
import com.flippbidd.utils.PreferenceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class LikesPropertyListing extends RecyclerView.Adapter<LikesPropertyListing.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<LikesData> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;
    MediaPlayer mediaPlayer;
    //Timer
    private final List<MyViewHolder> lstHolders;

    public LikesPropertyListing(Context mContext, String isScreenType) {
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


    public void deleteAdapter(int position) {
        mPropertyDataList.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //adapter_property_data_listing_layout old layout
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_new_property_ui, parent, false);

        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        holder.isRecyclable();
        holder.setData(mPropertyDataList.get(position), holder);
        holder.setIsRecyclable(false);
        final LikesData loMessage = mPropertyDataList.get(position);
        if (holder != null) {

            if (loMessage.getPropertyDetails() != null && !loMessage.getPropertyDetails().equals("")) {

                if (loMessage.getPropertyDetails().getImages() != null && !loMessage.getPropertyDetails().getImages().equals("")) {
                    if (loMessage.getPropertyDetails().getImages().size() != 0) {
                        PhotosAdapter adapter = new PhotosAdapter(mContext, loMessage.getPropertyDetails().getImages(), loMessage.getCommonId(), loMessage.getLoginId(), false, loMessage.getPropertyDetails().getIsAvailable());
                        holder.pager.setAdapter(adapter);
                        holder.tabLayout.setViewPager(holder.pager);
                    }
                }
                if (loMessage.getPropertyDetails().getHouse() != null && !loMessage.getPropertyDetails().getHouse().equalsIgnoreCase("")) {
                    holder.txtPropertyName.setText(loMessage.getPropertyDetails().getHouse());
                }
                if (loMessage.getPropertyDetails().getAddress1() != null && !loMessage.getPropertyDetails().getAddress1().equalsIgnoreCase("") && loMessage.getPropertyDetails().getAddress2() != null && !loMessage.getPropertyDetails().getAddress2().equalsIgnoreCase("")) {
                    //holder.txtPropertyLocation1.setText(loMessage.getPropertyDetails().getAddress());
                    if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                        holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                        holder.txtPropertyLocation1.setText(loMessage.getPropertyDetails().getAddress1());
                        holder.img_eye.setVisibility(View.GONE);
                        holder.txtPropertyLocation2.setText(loMessage.getPropertyDetails().getAddress2());
                    } else {
                        if (!BaseApplication.getInstance().getLoginID().equals(loMessage.getPropertyDetails().getLoginId())) {
                            holder.img_eye.setVisibility(View.VISIBLE);
                            holder.txtPropertyLocation1.setText("****");
                            holder.txtPropertyLocation2.setText(loMessage.getPropertyDetails().getAddress2());
                        } else {
                            holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                            holder.txtPropertyLocation1.setText(loMessage.getPropertyDetails().getAddress1());
                            holder.img_eye.setVisibility(View.GONE);
                            holder.txtPropertyLocation2.setText(loMessage.getPropertyDetails().getAddress2());
                        }

                    }
                }
                if (PreferenceUtils.getPlanVersionStatus()){
                    holder.img_eye.setVisibility(View.GONE);
                    holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                    holder.txtPropertyLocation1.setText(loMessage.getPropertyDetails().getAddress1());
                    holder.txtPropertyLocation2.setText(loMessage.getPropertyDetails().getAddress2());
                }
                if (loMessage.getPropertyDetails().getPropertyType() != null && !loMessage.getPropertyDetails().getPropertyType().equalsIgnoreCase("")) {
                    holder.txtPropertyTypeName.setText(loMessage.getPropertyDetails().getPropertyType());
                }

                if (loMessage.getPropertyDetails().getPreForeclosure() != null && !loMessage.getPropertyDetails().getPreForeclosure().equalsIgnoreCase("")) {
                    if (loMessage.getPropertyDetails().getPreForeclosure().equalsIgnoreCase("1")) {
                        holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                        holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                        holder.txtUnlistedListedTag.setText("Pre-Foreclosure");
                    } else {
                        if (loMessage.getPropertyDetails().getPropertyListed().equalsIgnoreCase("0")) {
                            holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                            holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                            holder.txtUnlistedListedTag.setText("Off-Market");

                        } else {
                            holder.viewTimeCountDown.setBackgroundResource(R.drawable.orange);
                            holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_gradian);
                            holder.txtUnlistedListedTag.setText("Listed");
                        }
                    }
                } else {
                    holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                    holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                    holder.txtUnlistedListedTag.setText("None");

                }
                if (loMessage.getPropertyDetails().getIsLike() != null && !loMessage.getPropertyDetails().getIsLike().equals("")) {
                    if (loMessage.getPropertyDetails().getIsLike()) {
                        holder.imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                    } else {
                        holder.imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
                    }
                }

                if (loMessage.getPropertyDetails().getRentAmount() != null && !loMessage.getPropertyDetails().getRentAmount().equalsIgnoreCase("")) {
                    holder.txtPropertyPrice.setVisibility(View.VISIBLE);
                    int dollr = Integer.parseInt(loMessage.getPropertyDetails().getRentAmount());
                    if (dollr <= 1000000) {
                        holder.txtPropertyPrice.setText("$" + loMessage.getPropertyDetails().getRentAmount());
                    } else if (dollr <= 100000000) {
                        holder.txtPropertyPrice.setText("$" + loMessage.getPropertyDetails().getRentAmount());
                    } else {
                        holder.txtPropertyPrice.setText("$" + String.valueOf(dollr));
                    }
                } else {
                    holder.txtPropertyPrice.setVisibility(View.INVISIBLE);
                }

                if (loMessage.getPropertyDetails().getIsAvailable() != null && !loMessage.getPropertyDetails().getIsAvailable().equalsIgnoreCase("")) {
                    if (loMessage.getPropertyDetails().getIsAvailable().equalsIgnoreCase("0")) {
                        //0 unavailable
                        holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                    } else {
                        //1 available
                        holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    }
                }

                if (loMessage.getUserDetails() != null && !loMessage.getUserDetails().equals("")) {
                    if (loMessage.getUserDetails().getProfilePic() != null && !loMessage.getUserDetails().getProfilePic().equalsIgnoreCase("")) {
                        DisplayImageOptions options = new DisplayImageOptions.Builder()
                                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                                .cacheInMemory(true)
                                .cacheOnDisk(true)
                                .bitmapConfig(Bitmap.Config.ARGB_4444)
                                .build();
                        imageLoader.displayImage(loMessage.getUserDetails().getProfilePic(), holder.imagePUser, options);
                    } else {
                        holder.imagePUser.setImageResource(R.mipmap.user);
                    }
                    if (loMessage.getUserDetails().getRateCount() != null && loMessage.getUserDetails().getRateCount() != 0) {
                        holder.txtRatingValue.setText(String.valueOf(loMessage.getUserDetails().getRateCount()));
                    } else {
                        holder.txtRatingValue.setText("0");
                    }
                }

                if (loMessage.getPropertyDetails().getCreatedDate() != null && !loMessage.getPropertyDetails().getCreatedDate().equalsIgnoreCase("")) {
                    String days = Utils.getDays(loMessage.getPropertyDetails().getCreatedDate(), "");
                    if (days.equalsIgnoreCase("1") || days.equalsIgnoreCase("0")) {
                        holder.textViewofChatTime.setText("New on Flippbidd");
                    } else {
                        holder.textViewofChatTime.setText(days + " days on Flippbidd");
                    }
                }

                //trending
                if (loMessage.getPropertyDetails().getViewCounts() != null) {
                    if (loMessage.getPropertyDetails().getViewCounts() > Constants.TRENDING_COUNTS) {
                        holder.viewTimeCountDown.setBackgroundResource(R.drawable.ic_green);
                        holder.textViewofChatTime.setText("Trending");
                    }
                }

                if (loMessage.getPropertyDetails().getThumsCounts() != null) {
                    if (loMessage.getPropertyDetails().getThumsCounts() != 0) {
                        holder.textViewPropertyDetailsThumCount.setText("" + loMessage.getPropertyDetails().getThumsCounts());
                    } else {
                        holder.textViewPropertyDetailsThumCount.setText("0");
                    }
                }

                if (loMessage.getPropertyDetails().getViewCounts() != null) {
                    if (loMessage.getPropertyDetails().getViewCounts() != 0) {
                        holder.textViewPropertyDetailsViewCounts.setText("" + loMessage.getPropertyDetails().getViewCounts());
                    } else {
                        holder.textViewPropertyDetailsViewCounts.setText("0");
                    }
                }

                if (loMessage.getPropertyDetails().getBiddCounts() != null) {
                    if (loMessage.getPropertyDetails().getBiddCounts() != 0) {
                        holder.textViewPropertyDetailsBiddCounts.setText("" + loMessage.getPropertyDetails().getBiddCounts());
                    } else {
                        holder.textViewPropertyDetailsBiddCounts.setText("0");
                    }
                }

                holder.imagePropertyLikeStatus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (loMessage.getPropertyDetails().getIsLike() != null) {
                            if (loMessage.getPropertyDetails().getIsLike()) {
                                loMessage.getPropertyDetails().setIsLike(false);
                                holder.imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);

                                if (mOnItemClickLisnear != null) {
                                    mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                                }

                            } else {
                                loMessage.getPropertyDetails().setIsLike(true);
                                holder.imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);

                                if (mOnItemClickLisnear != null) {
                                    mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROPERTY_LIKES_ACTION);
                                }
                                //click to like play tone
                                mediaPlayer = MediaPlayer.create(mContext, R.raw.like_tone);
                                mediaPlayer.start();
                                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    public void onCompletion(MediaPlayer mp) {
                                        //code
                                        mp.release();
                                    }
                                });
                            }
                        }
                    }
                });

            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.VIEW_ACTION);
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

        CustomTextView txtPropertyName, txtPropertyLocation1, txtPropertyLocation2, txtPropertyPrice, txtUnlistedListedTag, txtPropertyTypeName, txtRatingValue, textViewofChatTime;
        CustomTextView textViewPropertyDetailsBiddCounts, textViewPropertyDetailsViewCounts, textViewPropertyDetailsThumCount;
        ImageView imagePropertyLikeStatus, imageViewUnavailableIconView, img_eye;
        CircleImageView imagePUser;
        RelativeLayout relativeUnlistedListedTag, relativeUserDetails, viewTimeCountDown;
        CircleIndicator tabLayout;
        ViewPager pager;

        LikesData mPData;

        public MyViewHolder(View loView) {
            super(loView);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, loView);
            pager = loView.findViewById(R.id.viewpager2);
            tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);

            textViewPropertyDetailsBiddCounts = loView.findViewById(R.id.textViewPropertyDetailsBiddCounts);
            textViewPropertyDetailsViewCounts = loView.findViewById(R.id.textViewPropertyDetailsViewCounts);
            textViewPropertyDetailsThumCount = loView.findViewById(R.id.textViewPropertyDetailsThumCount);
            imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
            txtRatingValue = loView.findViewById(R.id.txtRatingValue);
            imagePUser = loView.findViewById(R.id.imagePUser);
            imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
            txtPropertyName = loView.findViewById(R.id.txtPropertyName);
            txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
            txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
            img_eye = loView.findViewById(R.id.img_eye);
            txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
            txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
            txtPropertyTypeName = loView.findViewById(R.id.txtPropertyTypeName);
            relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
            relativeUserDetails = loView.findViewById(R.id.relativeUserDetails);
            viewTimeCountDown = loView.findViewById(R.id.viewTimeCountDown);
            textViewofChatTime = loView.findViewById(R.id.textViewofChatTime);


            viewTimeCountDown.setVisibility(View.VISIBLE);
            relativeUserDetails.setVisibility(View.VISIBLE);
            imagePropertyLikeStatus.setVisibility(View.VISIBLE);
        }

        public void setData(LikesData item, MyViewHolder holder) {
            if (item != null && !item.equals("")) {
                mPData = item;
                if (mPData.getPropertyDetails() != null && !mPData.getPropertyDetails().equals("")) {
                    if (mPData.getPropertyDetails().getCreatedDate() != null && !mPData.getPropertyDetails().getCreatedDate().equals("")) {
                        if (!mPData.getPropertyDetails().getCreatedDate().isEmpty()) {
                            textViewofChatTime.setText("" + Utils.getConvertData(mPData.getPropertyDetails().getCreatedDate()));
                        }
                    }

                }
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }
}
