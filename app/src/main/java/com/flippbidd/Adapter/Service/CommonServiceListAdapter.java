package com.flippbidd.Adapter.Service;

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
import com.flippbidd.Model.Response.Service.ServiceListData;
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

public class CommonServiceListAdapter extends RecyclerView.Adapter<CommonServiceListAdapter.MyViewHolder> {

    //define object of Activity
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds
    private Context mContext;
    private List<ServiceListData> mPropertyDataList = new ArrayList<>();
    ImageLoader imageLoader;
    private String mScreenType;
    onItemClickLisnear mOnItemClickLisnear;

    public CommonServiceListAdapter(Context mContext, String isScreenType) {
        this.mContext = mContext;
        this.mScreenType = isScreenType;
        imageLoader = ImageLoader.getInstance();
    }

    public void addAll(List<ServiceListData> arrayList) {
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
        final ServiceListData mServiceData = mPropertyDataList.get(position);
        if (holder != null) {
            if (mScreenType.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_HOMEOWNER)) {
                holder.relativeLayoutActionbox.setVisibility(View.GONE);
                holder.relativeLayoutActionboxsecond.setVisibility(View.VISIBLE);
            } else {
                holder.relativeLayoutActionbox.setVisibility(View.VISIBLE);
                holder.relativeLayoutActionboxsecond.setVisibility(View.GONE);
            }

            if (mServiceData.getSenderQbId() != null && !mServiceData.getSenderQbId().equalsIgnoreCase("")) {
                if (!UserPreference.getInstance(mContext).getUserDetail().getLoginId().equalsIgnoreCase(mServiceData.getLoginId())) {
                    holder.imgMessage.setVisibility(View.VISIBLE);
                } else {
                    holder.imgMessage.setVisibility(View.GONE);
                }
            } else {
                holder.imgMessage.setVisibility(View.GONE);
            }


            if (mServiceData.getServicePrice() != null && !mServiceData.getServicePrice().equalsIgnoreCase("")) {

                holder.textViewServicePrice.setText("$ " + mServiceData.getServicePrice());
            }
            if (mServiceData.getTitle() != null && !mServiceData.getTitle().equalsIgnoreCase("")) {

                holder.textViewServiceTitle.setText(mServiceData.getTitle());
            }
            if (mServiceData.getAddress() != null && !mServiceData.getAddress().equalsIgnoreCase("")) {

                holder.textViewServiceAddress.setText(mServiceData.getAddress());
            } else {
                holder.textViewServiceAddress.setText(mServiceData.getCity() + "," + mServiceData.getState());
            }

            if (mServiceData.getServiceType() != null && !mServiceData.getServiceType().equalsIgnoreCase("")) {

                holder.textViewService.setText(mServiceData.getDescription());
            }

            if (mServiceData.isLike()) {
                holder.imgHeart.setImageResource(R.drawable.fav);
            } else {
                holder.imgHeart.setImageResource(R.drawable.unfav);
            }

            //new design code
            if (mServiceData.getImages() != null && mServiceData.getImages().size() != 0) {
                int lastIndex = mServiceData.getImages().size() - 1;
                //reversed order
                if (mServiceData.getImages().get(lastIndex).getImageUrl() != null && !mServiceData.getImages().get(lastIndex).getImageUrl().equalsIgnoreCase("")) {
                    holder.lottieAnimationProgress.setVisibility(View.VISIBLE);
                    DisplayImageOptions options = new DisplayImageOptions.Builder()
                            .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                            .cacheOnDisc()
                            .cacheInMemory()
                            .showImageOnFail(R.drawable.no_image_icon)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .build();
                    imageLoader.displayImage(mServiceData.getImages().get(lastIndex).getImageUrl(), holder.service_image, options);

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
                    if (mServiceData.isLike()) {
                        mServiceData.setLike(false);
                        holder.imgHeart.setImageResource(R.drawable.unfav);
                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.PROPERTY_UNLIKES_ACTION);
                        }

                    } else {
                        mServiceData.setLike(true);
                        holder.imgHeart.setImageResource(R.drawable.fav);

                        if (mOnItemClickLisnear != null) {
                            mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.PROPERTY_LIKES_ACTION);
                        }

                    }
                }
            });
            //action click
            holder.imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null)
                        mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.DELETE_ACTION);

                }
            });

            holder.imgEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnItemClickLisnear != null)
                        mOnItemClickLisnear.onClickEvent(position, mServiceData, Constants.ACTION.EDIT_ACTION);
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
        void onClickEvent(int position, ServiceListData mServiceListData, String mActionType);
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
        @BindView(R.id.textViewServiceAddress)
        CustomTextView textViewServiceAddress;

        //new design code
        @BindView(R.id.lottieAnimationProgress)
        LottieAnimationView lottieAnimationProgress;
        @BindView(R.id.service_image)
        ImageView service_image;


        public MyViewHolder(View view) {
            super(view);
            //ID OF TEXT VIEW
            ButterKnife.bind(this, view);
        }
    }

}
