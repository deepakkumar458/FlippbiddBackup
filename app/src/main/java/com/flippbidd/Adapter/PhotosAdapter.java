package com.flippbidd.Adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.baseclass.BaseApplication;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.viewpager.widget.PagerAdapter;
import cn.refactor.lib.colordialog.ColorDialog;

public class PhotosAdapter extends PagerAdapter {

    private Context context;
    private List<CommonData.Image> arrayList;
    private LayoutInflater layoutInflater;
    private String loCommonId, loLoginID, moIsAvailableStatus = "0";
    private boolean loExpired;

    public PhotosAdapter(Context context, List<CommonData.Image> arrayList, String proID, String loginID, boolean expired, String isAvailableStatus) {
        this.context = context;
        this.arrayList = arrayList;
        this.loCommonId = proID;
        this.loLoginID = loginID;
        this.loExpired = expired;
        this.moIsAvailableStatus = isAvailableStatus;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == ((View) o);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = layoutInflater.inflate(R.layout.row_slider_image, container, false);

        AppCompatImageView ivProductImage = view.findViewById(R.id.ivProductImage);

        if (!TextUtils.isEmpty(arrayList.get(position).getImageUrl())) {
            Glide.with(context)
                    .load(arrayList.get(position).getImageUrl())
                    .apply(new RequestOptions().centerCrop().placeholder(R.drawable.no_image_icon).error(R.drawable.no_image_icon))
                    .into(ivProductImage);


            ivProductImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (loLoginID != null && !loLoginID.equalsIgnoreCase("")) {

                        if (moIsAvailableStatus.equalsIgnoreCase("1")) {
                            ((Context)context).startActivity(PropertyDetails.getIntentActivity(context,
                                    loCommonId, loLoginID,
                                    "PROPERTY", loExpired));
                        } else {
                            if (loLoginID.equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                (context).startActivity(PropertyDetails.getIntentActivity(context,
                                        loCommonId, loLoginID,
                                        "PROPERTY", loExpired));
                            } else {
                                showNotAvailable();
                            }
                        }
                    } else {
                        openMultipleImage(arrayList);
                    }
                }
            });
        }

        container.addView(view);
        return view;
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(context);
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(context.getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    private void openMultipleImage(List<CommonData.Image> mCommonDetailsData) {
        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(context.getResources())
                .setProgressBarImage(R.drawable.progressbardrawable)
                .setFadeDuration(1000);
        //items show reverse formFresco
        new ImageViewer.Builder<>(context, mCommonDetailsData)
                .setFormatter(new ImageViewer.Formatter<CommonData.Image>() {
                    @Override
                    public String format(CommonData.Image customImage) {
                        return customImage.getImageUrl();
                    }
                })
                .setStartPosition(0).allowZooming(true).setCustomDraweeHierarchyBuilder(hierarchyBuilder).show();

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
