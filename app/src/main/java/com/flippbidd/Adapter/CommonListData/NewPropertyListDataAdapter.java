package com.flippbidd.Adapter.CommonListData;

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
import com.flippbidd.Fragments.PropertyList;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.Utils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseViewHolder;
import com.flippbidd.utils.PreferenceUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

public class NewPropertyListDataAdapter extends RecyclerView.Adapter<NewPropertyListDataAdapter.MyViewHolder> {

    //define object of Activity
    private Context mContext;
    private List<CommonData> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;

    //Timer
    private static final int ITEM = 0;
    MediaPlayer mp;

    public NewPropertyListDataAdapter(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
    }


    public void addAll(List<CommonData> arrayList) {
        mPropertyDataList.clear();
        mPropertyDataList.addAll(arrayList);
        notifyDataSetChanged();
    }

    public void updateAdapter(int position) {
        //notifyItemChanged(position);
        notifyDataSetChanged();
    }

    public void deleteAdapter(int position) {
        mPropertyDataList.remove(position);
        if (mPropertyDataList.size() == 0) {
            PropertyList.setNodataView("Property");
        }
        notifyDataSetChanged();

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        MyViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
        }
        return viewHolder;

    }

    @NonNull
    private MyViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        MyViewHolder viewHolder;
        //adapter_property_data_listing_layout old
        View v1 = inflater.inflate(R.layout.item_new_property_ui, parent, false);
        viewHolder = new MyViewHolder(v1);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final CommonData loMessage = mPropertyDataList.get(position);//0,1,2,3,4
        switch (getItemViewType(position)) {
            case ITEM:
                if (holder != null) {
                    if (loMessage.getImages().size() != 0) {
                        holder.propertyNoImage.setVisibility(View.GONE);
                        holder.pager.setVisibility(View.VISIBLE);
                        holder.tabLayout.setVisibility(View.VISIBLE);

                        PhotosAdapter adapter = new PhotosAdapter(mContext, loMessage.getImages(), loMessage.getCommonId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
                        holder.pager.setAdapter(adapter);
                        holder.tabLayout.setViewPager(holder.pager);

                    } else {
                        //show no image view
                        holder.propertyNoImage.setVisibility(View.VISIBLE);
                        holder.pager.setVisibility(View.INVISIBLE);
                        holder.tabLayout.setVisibility(View.INVISIBLE);
                    }

                    holder.txtPropertyName.setText(loMessage.getHouse());
                    if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                        holder.img_eye.setVisibility(View.GONE);
                        holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                        holder.txtPropertyLocation1.setText(loMessage.getAddress1());
                        holder.txtPropertyLocation2.setText(loMessage.getAddress2());
                    } else {
                        if (!BaseApplication.getInstance().getLoginID().equals(loMessage.getData().getLoginId())) {
                            holder.img_eye.setVisibility(View.VISIBLE);
                            holder.txtPropertyLocation1.setText("****");
                            holder.txtPropertyLocation1.setTextSize(13);
                            holder.txtPropertyLocation2.setText(loMessage.getAddress2());
                        } else {
                            holder.img_eye.setVisibility(View.GONE);
                            holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                            holder.txtPropertyLocation1.setText(loMessage.getAddress1());
                            holder.txtPropertyLocation2.setText(loMessage.getAddress2());
                        }
                    }
                   if (PreferenceUtils.getPlanVersionStatus()){
                            holder.img_eye.setVisibility(View.GONE);
                            holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                            holder.txtPropertyLocation1.setText(loMessage.getAddress1());
                            holder.txtPropertyLocation2.setText(loMessage.getAddress2());
                    }

                    //holder.txtPropertyLocation.setText(loMessage.getAddress());
                    holder.txtPropertyTypeName.setText(loMessage.getPropertyType());

                    if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
                        holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                        holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                        holder.txtUnlistedListedTag.setText("Pre-Foreclosure");
                    } else {
                        if (loMessage.getPropertyListed().equalsIgnoreCase("0")) {
                            holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
                            holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                            holder.txtUnlistedListedTag.setText("Off-Market");

                        } else {
                            holder.viewTimeCountDown.setBackgroundResource(R.drawable.orange);
                            holder.relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_gradian);
                            holder.txtUnlistedListedTag.setText("Listed");
                        }
                    }


                    if (loMessage.getIsAvailable().equalsIgnoreCase("0")) {
                        //0 unavailable
                        holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                    } else {
                        //1 available
                        holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    }

                    if (loMessage.isLike()) {
                        holder.imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                    } else {
                        holder.imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
                    }

                    if (loMessage.getRentAmount() != null && !loMessage.getRentAmount().equalsIgnoreCase("")) {
                        if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
                            holder.txtPropertyPrice.setVisibility(View.GONE);
                        } else {
                            holder.txtPropertyPrice.setVisibility(View.VISIBLE);
                            int dollr = Integer.parseInt(loMessage.getRentAmount());
                            if (dollr <= 1000000) {
                                holder.txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                            } else if (dollr <= 100000000) {
                                holder.txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                            } else {
                                holder.txtPropertyPrice.setText("$" + currencyFormat(String.valueOf(dollr)));
                            }
                        }

                    } else {
                        holder.txtPropertyPrice.setVisibility(View.INVISIBLE);
                    }


                    if (loMessage.getData() != null && !loMessage.getData().equals("")) {
                        if (loMessage.getData().getProfilePic() != null && !loMessage.getData().getProfilePic().equalsIgnoreCase("")) {
                            DisplayImageOptions options = new DisplayImageOptions.Builder()
                                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                                    .cacheInMemory(true)
                                    .cacheOnDisk(true)
                                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                                    .build();
                            imageLoader.displayImage(loMessage.getData().getProfilePic(), holder.imagePUser, options);
                        } else {
                            holder.imagePUser.setImageResource(R.mipmap.user);
                        }
                        if (loMessage.getData().getRateAverage() != null && loMessage.getData().getRateAverage() != 0) {
                            holder.txtRatingValue.setText("" + loMessage.getData().getRateAverage());
                        } else {
                            holder.txtRatingValue.setText("0");
                        }
                    }

                    String days = Utils.getDays(loMessage.getCreated_date(), "");
                    if (days.equalsIgnoreCase("1") || days.equalsIgnoreCase("0")) {
                        holder.textViewofChatTime.setText("New on Flippbidd");
                    } else {
                        holder.textViewofChatTime.setText(days + " days on Flippbidd");
                    }
                    //trending
                    if (loMessage.getViewCounts() > Constants.TRENDING_COUNTS) {
                        holder.viewTimeCountDown.setBackgroundResource(R.drawable.ic_green);
                        holder.textViewofChatTime.setText("Trending");
                    }

                    if (loMessage.getThumsCounts() != 0) {
                        holder.textViewPropertyDetailsThumCount.setText("" + loMessage.getThumsCounts());
                    } else {
                        holder.textViewPropertyDetailsThumCount.setText("0");
                    }

                    if (loMessage.getViewCounts() != 0) {
                        holder.textViewPropertyDetailsViewCounts.setText("" + loMessage.getViewCounts());
                    } else {
                        holder.textViewPropertyDetailsViewCounts.setText("0");
                    }
                    if (loMessage.getBiddCount() != 0) {
                        holder.textViewPropertyDetailsBiddCounts.setText("" + loMessage.getBiddCount());
                    } else {
                        holder.textViewPropertyDetailsBiddCounts.setText("0");
                    }
                    //yyyy-MM-dd hh:mm:ss
                    holder.imagePropertyLikeStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //play tone
                            if (loMessage.isLike()) {
                                loMessage.setLike(false);
                                holder.imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);

                                if (mOnItemClickLisnear != null) {
                                    mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                                }

                            } else {
                                loMessage.setLike(true);
                                holder.imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                                if (mOnItemClickLisnear != null) {
                                    mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROPERTY_LIKES_ACTION);
                                }
                                //click to like play tone
                                mp = MediaPlayer.create(mContext, R.raw.like_tone);
                                mp.start();
                                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                    public void onCompletion(MediaPlayer mp) {
                                        //code
                                        mp.release();
                                    }
                                });
                            }
                        }


                    });

                }

                holder.getItemView().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(holder.getAdapterPosition(), loMessage, Constants.ACTION.VIEW_ACTION);
                        }
                    }
                });

                holder.imageThreeDotAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROPERTY_MY_ACTION);
                        }
                    }
                });

                holder.imagePUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, loMessage, Constants.ACTION.PROFILE_VIEW_ACTION);
                        }
                    }
                });
                break;
        }
    }

    public static String currencyFormat(String amount) {
        //1,234,567.89
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, CommonData mPropertyData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    @Override
    public int getItemCount() {
        return mPropertyDataList.size();
    }

    public class MyViewHolder extends BaseViewHolder {

        CustomTextView txtPropertyName, txtPropertyLocation, txtPropertyPrice, txtUnlistedListedTag, txtPropertyTypeName, txtRatingValue, textViewofChatTime, txtPropertyLocation1, txtPropertyLocation2;
        CustomTextView textViewPropertyDetailsBiddCounts, textViewPropertyDetailsViewCounts, textViewPropertyDetailsThumCount;
        ImageView imagePropertyLikeStatus, imageThreeDotAction, imageViewUnavailableIconView, img_eye;
        ImageView propertyNoImage, img_pin;
        CircleImageView imagePUser;
        RelativeLayout relativeUnlistedListedTag, relativeUserDetails, viewTimeCountDown;
        CircleIndicator tabLayout;
        ViewPager pager;

        CommonData mPData;

        public MyViewHolder(View loView) {
            super(loView);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, loView);

            pager = loView.findViewById(R.id.viewpager2);
            tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
            propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
            textViewPropertyDetailsViewCounts = loView.findViewById(R.id.textViewPropertyDetailsViewCounts);
            textViewPropertyDetailsBiddCounts = loView.findViewById(R.id.textViewPropertyDetailsBiddCounts);
            textViewPropertyDetailsThumCount = loView.findViewById(R.id.textViewPropertyDetailsThumCount);
            txtRatingValue = loView.findViewById(R.id.txtRatingValue);
            imagePUser = loView.findViewById(R.id.imagePUser);
            imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
            imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
            imageThreeDotAction = loView.findViewById(R.id.imageThreeDotAction);
            txtPropertyName = loView.findViewById(R.id.txtPropertyName);
            txtPropertyLocation = loView.findViewById(R.id.txtPropertyLocation);
            txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
            txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
            txtPropertyTypeName = loView.findViewById(R.id.txtPropertyTypeName);
            relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
            relativeUserDetails = loView.findViewById(R.id.relativeUserDetails);
            viewTimeCountDown = loView.findViewById(R.id.viewTimeCountDown);
            textViewofChatTime = loView.findViewById(R.id.textViewofChatTime);
            txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
            img_eye = loView.findViewById(R.id.img_eye);
            txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
            img_pin = loView.findViewById(R.id.img_pin);


            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_SELLER)) {
                imagePropertyLikeStatus.setVisibility(View.GONE);
                imageThreeDotAction.setVisibility(View.VISIBLE);
            } else {
                imageThreeDotAction.setVisibility(View.GONE);
                imagePropertyLikeStatus.setVisibility(View.VISIBLE);
            }
            viewTimeCountDown.setVisibility(View.VISIBLE);
            relativeUserDetails.setVisibility(View.VISIBLE);


        }


        public void setData(CommonData item, MyViewHolder holder) {
            mPData = item;
            holder.updateTimeRemaining(System.currentTimeMillis());


        }

        public void updateTimeRemaining(long currentTime) {
            //textViewofChatTime.setText("" + Utils.getConvertData(mPData.getCreated_date()));
        }
    }


}
