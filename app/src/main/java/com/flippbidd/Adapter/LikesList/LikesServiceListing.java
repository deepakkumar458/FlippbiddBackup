package com.flippbidd.Adapter.LikesList;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.Likes.Service.ServiceLikeDetails;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseViewHolder;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LikesServiceListing extends RecyclerView.Adapter<LikesServiceListing.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<ServiceLikeDetails> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;

    public LikesServiceListing(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<ServiceLikeDetails> arrayList) {
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


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_service_list_data_layout, parent, false);

        return new MyViewHolder(itemView);

    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.isRecyclable();
        final ServiceLikeDetails mServiceData = mPropertyDataList.get(position);
        if (holder != null) {
            holder.relativeLayoutActionboxsecond.setVisibility(View.VISIBLE);
            holder.relativeLayoutActionbox.setVisibility(View.GONE);
            if (mServiceData.getPropertyDetails().getSenderQbId() != null && !mServiceData.getPropertyDetails().getSenderQbId().equalsIgnoreCase("")) {
                if (!UserPreference.getInstance(mContext).getUserDetail().getLoginId().equalsIgnoreCase(mServiceData.getUserDetails().getLoginId())) {
                    holder.imgMessage.setVisibility(View.VISIBLE);
                } else {
                    holder.imgMessage.setVisibility(View.GONE);
                }
            } else {
                holder.imgMessage.setVisibility(View.GONE);
            }

            if (mServiceData.getPropertyDetails().getServicePrice() != null && !mServiceData.getPropertyDetails().getServicePrice().equalsIgnoreCase("")) {

                holder.textViewServicePrice.setText("$ " + mServiceData.getPropertyDetails().getServicePrice());
            }
            if (mServiceData.getPropertyDetails().getTitle() != null && !mServiceData.getPropertyDetails().getTitle().equalsIgnoreCase("")) {

                holder.textViewServiceTitle.setText(mServiceData.getPropertyDetails().getTitle());
            }
            if (mServiceData.getPropertyDetails().getAddress() != null && !mServiceData.getPropertyDetails().getAddress().equalsIgnoreCase("")) {

                holder.textViewServiceAddress.setText(mServiceData.getPropertyDetails().getAddress());
            }

            if (mServiceData.getPropertyDetails().getDescription() != null && !mServiceData.getPropertyDetails().getDescription().equalsIgnoreCase("")) {

                holder.textViewService.setText(mServiceData.getPropertyDetails().getDescription());
            }

            if (mServiceData.getIsLike().equalsIgnoreCase("1")) {
                holder.imgHeart.setImageResource(R.drawable.fav);
            } else {
                holder.imgHeart.setImageResource(R.drawable.unfav);
            }

//            if (mServiceData.getUserDetails().getProfilePic() != null && !mServiceData.getUserDetails().getProfilePic().equalsIgnoreCase("")) {
//                DisplayImageOptions options = new DisplayImageOptions.Builder()
//                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
//                        .cacheInMemory(true)
//                        .showImageOnFail(R.mipmap.unavailable)
//                        .bitmapConfig(Bitmap.Config.ARGB_4444)
//                        .cacheOnDisk(false)
//                        .build();
//                imageLoader.displayImage(mServiceData.getUserDetails().getProfilePic(), holder.imgUserPerson, options);
//            } else {
//                holder.imgUserPerson.setImageResource(R.mipmap.user);
//            }

            //new design code
            //new design code
            if (mServiceData.getPropertyDetails().getImages() != null && mServiceData.getPropertyDetails().getImages().size() != 0) {
                int lastIndex = mServiceData.getPropertyDetails().getImages().size() - 1;
                //reversed order
                if (mServiceData.getPropertyDetails().getImages().get(lastIndex).getImageName() != null && !mServiceData.getPropertyDetails().getImages().get(lastIndex).getImageName().equalsIgnoreCase("")) {
                    holder.lottieAnimationProgress.setVisibility(View.VISIBLE);
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                            .cacheOnDisc()
                            .cacheInMemory()
                            .showImageOnFail(R.drawable.no_image_icon)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    imageLoader.displayImage(mServiceData.getPropertyDetails().getImages().get(lastIndex).getImageName(), holder.service_image, options);

                } else {
                    if (holder.lottieAnimationProgress.isAnimating()) {
                        holder.lottieAnimationProgress.pauseAnimation();
                    }
                    holder.lottieAnimationProgress.setVisibility(View.GONE);
                    holder.service_image.setImageResource(R.drawable.no_image_icon);
                }

            } else {
                if (holder.lottieAnimationProgress.isAnimating()) {
                    holder.lottieAnimationProgress.pauseAnimation();
                }
                holder.lottieAnimationProgress.setVisibility(View.GONE);
                holder.service_image.setImageResource(R.drawable.no_image_icon);
            }

            holder.imgHeart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                    }

                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null)
                        mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.VIEW_ACTION);
                }
            });
            holder.imgMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (mOnItemClickLisnear != null) {
                        mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.MESSAGES_VIEW_ACTION);
                    }

                }
            });

        }
    }


    public interface onItemClickLisnear {
        void onClickEvent(int position, ServiceLikeDetails mServiceListData, String mActionType);
    }

    public void setItemOnClickEvent(onItemClickLisnear mOnItemClickLisnear) {
        this.mOnItemClickLisnear = mOnItemClickLisnear;
    }

    @Override
    public int getItemCount() {
        return mPropertyDataList.size();
    }

    public class MyViewHolder extends BaseViewHolder {
        @BindView(R.id.textViewServicePrice)
        CustomTextView textViewServicePrice;
        @BindView(R.id.textViewServiceTitle)
        CustomTextView textViewServiceTitle;
        @BindView(R.id.textViewServiceAddress)
        CustomTextView textViewServiceAddress;
        @BindView(R.id.textViewService)
        CustomTextView textViewService;
        @BindView(R.id.textViewUsername)
        CustomTextView textViewUsername;
        @BindView(R.id.imgEdit)
        ImageView imgEdit;
        @BindView(R.id.imgDelete)
        ImageView imgDelete;
        @BindView(R.id.relativeLayoutActionbox)
        RelativeLayout relativeLayoutActionbox;
        @BindView(R.id.relativeLayoutActionboxsecond)
        RelativeLayout relativeLayoutActionboxsecond;
        @BindView(R.id.imgUserPerson)
        CircleImageView imgUserPerson;
        @BindView(R.id.imgHeart)
        ImageView imgHeart;
        @BindView(R.id.imgMessage)
        ImageView imgMessage;

        //new design code
        @BindView(R.id.service_image)
        ImageView service_image;
        @BindView(R.id.lottieAnimationProgress)
        LottieAnimationView lottieAnimationProgress;


        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }
    }

}
