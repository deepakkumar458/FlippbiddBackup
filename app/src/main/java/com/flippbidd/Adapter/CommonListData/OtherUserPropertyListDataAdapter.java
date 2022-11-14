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
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Others.Constants;
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

public class OtherUserPropertyListDataAdapter extends RecyclerView.Adapter<OtherUserPropertyListDataAdapter.MyViewHolder> {

    //define object of Activity
    private Context mContext;
    private List<CommonData> mPropertyDataList = new ArrayList<>();
    private String loginId;
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;
    //Timer
    private static final int ITEM = 0;
    MediaPlayer mediaPlayer;


    public OtherUserPropertyListDataAdapter(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
    }


    public void addAll(OtherUserDetails otheruserDetails) {
        mPropertyDataList.clear();
        mPropertyDataList.addAll(otheruserDetails.getPropertyList());
        loginId = otheruserDetails.getLoginId();
        notifyDataSetChanged();
    }

    public void updateAdapter(int position) {
        notifyItemChanged(position);
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

    public void updatePositionData(int pos) {
        if (mPropertyDataList.size() != 0) {
            mPropertyDataList.get(pos).setIsAvailable("0");
            notifyDataSetChanged();
        }

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
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.setIsRecyclable(false);
        final CommonData loMessage = mPropertyDataList.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                if (holder != null) {

                    if (loMessage.getImages().size() != 0) {
                        holder.propertyNoImage.setVisibility(View.GONE);
                        holder.pager.setVisibility(View.VISIBLE);
                        holder.pager.setVisibility(View.VISIBLE);

                        PhotosAdapter adapter = new PhotosAdapter(mContext, loMessage.getImages(), loMessage.getPropertyId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
                        holder.pager.setAdapter(adapter);
                        holder.tabLayout.setViewPager(holder.pager);
                    } else {
                        holder.propertyNoImage.setVisibility(View.VISIBLE);
                        holder.pager.setVisibility(View.INVISIBLE);
                        holder.pager.setVisibility(View.INVISIBLE);
                    }

                    holder.txtPropertyName.setText(loMessage.getHouse_name());
                    if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                        holder.img_eye.setVisibility(View.GONE);
                        holder.txtPropertyLocation1.setTextColor(mContext.getResources().getColor(R.color.grey_font));
                        holder.txtPropertyLocation1.setText(loMessage.getAddress1());
                        holder.txtPropertyLocation2.setText(loMessage.getAddress2());
                    } else {
                        if (!BaseApplication.getInstance().getLoginID().equals(loginId)) {
                            holder.img_eye.setVisibility(View.VISIBLE);
                            holder.txtPropertyLocation1.setText("****");
                            holder.txtPropertyLocation1.setTextSize(15);
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
                    holder.txtPropertyTypeName.setText(loMessage.getPropertyType());


                    if (loMessage.getRentAmount() != null && !loMessage.getRentAmount().equalsIgnoreCase("")) {
                        holder.txtPropertyPrice.setVisibility(View.VISIBLE);
                        int dollr = Integer.parseInt(loMessage.getRentAmount());
                        if (dollr <= 1000000) {
                            holder.txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                        } else if (dollr <= 100000000) {
                            holder.txtPropertyPrice.setText("$" + currencyFormat(loMessage.getRentAmount()));
                        } else {
                            holder.txtPropertyPrice.setText("$" + currencyFormat(String.valueOf(dollr)));
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
                            holder.txtRatingValue.setText(String.valueOf(loMessage.getData().getRateAverage()));
                        } else {
                            holder.txtRatingValue.setText("0");
                        }
                    }

                    if (loMessage.getIsAvailable().equalsIgnoreCase("0")) {
                        //0 unavailable
                        holder.imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                    } else {
                        //1 available
                        holder.imageViewUnavailableIconView.setVisibility(View.GONE);
                    }

//                    String days = Utils.getDays(loMessage.getCreated_date(), "");
//                    if (days.equalsIgnoreCase("1") || days.equalsIgnoreCase("0")) {
//                        holder.textViewofChatTime.setText("New on Flippbidd");
//                    } else {
//                        holder.textViewofChatTime.setText(days + " days on Flippbidd");
//                    }

                    if (loMessage.getThumsCounts() != null && loMessage.getThumsCounts() != 0) {
                        holder.textViewPropertyDetailsThumCount.setText("" + loMessage.getThumsCounts());
                    } else {
                        holder.textViewPropertyDetailsThumCount.setText("0");
                    }

                    if (loMessage.getBiddCount() != 0) {
                        holder.textViewPropertyDetailsBiddCounts.setText("" + loMessage.getViewCounts());
                    } else {
                        holder.textViewPropertyDetailsBiddCounts.setText("0");
                    }
                    //yyyy-MM-dd hh:mm:ss

                    holder.imagePropertyLikeStatus.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

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
                break;
        }
    }

    public static String currencyFormat(String amount) {
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

        CustomTextView textViewPropertyDetailsBiddCounts, txtPropertyName, txtPropertyLocation1, txtPropertyLocation2, txtPropertyPrice, txtUnlistedListedTag, txtPropertyTypeName, txtRatingValue, textViewofChatTime, textViewPropertyDetailsThumCount;
        ImageView imagePropertyLikeStatus, imageViewUnavailableIconView, img_eye;
        CircleImageView imagePUser;
        RelativeLayout relativeUnlistedListedTag, relativeUserDetails, viewTimeCountDown;
        ImageView propertyNoImage;
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
            textViewPropertyDetailsBiddCounts = loView.findViewById(R.id.textViewPropertyDetailsBiddCounts);
            textViewPropertyDetailsThumCount = loView.findViewById(R.id.textViewPropertyDetailsThumCount);
            imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
            txtRatingValue = loView.findViewById(R.id.txtRatingValue);
            imagePUser = loView.findViewById(R.id.imagePUser);
            imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
            txtPropertyName = loView.findViewById(R.id.txtPropertyName);
            img_eye = loView.findViewById(R.id.img_eye);
            txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
            txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
            txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
            txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
            txtPropertyTypeName = loView.findViewById(R.id.txtPropertyTypeName);
            relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
            relativeUserDetails = loView.findViewById(R.id.relativeUserDetails);
            viewTimeCountDown = loView.findViewById(R.id.viewTimeCountDown);
            textViewofChatTime = loView.findViewById(R.id.textViewofChatTime);


            viewTimeCountDown.setVisibility(View.GONE);
            relativeUserDetails.setVisibility(View.GONE);
            imagePropertyLikeStatus.setVisibility(View.GONE);

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
