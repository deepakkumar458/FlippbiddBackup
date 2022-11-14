package com.flippbidd.activity;

import static com.flippbidd.Others.Constants.PRO_VERSION;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Model.Response.OtherUserDetailsResponse;
import com.flippbidd.Model.Response.RatingData;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Rating.RateAndReview;
import com.flippbidd.activity.Rating.RatingList;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.flippbidd.views.ShowMoreTextView;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import cn.refactor.lib.colordialog.ColorDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PropertyOtherUserDetailsActivity extends BaseActivity {

    public static final String USER_ID = "USER_ID";
    public static final int UPDATE_REVIEW = 653;
    Disposable disposable;
    private String UserID;
    private ImageLoader mImageLoader;
    OtherUserDetails loOtherUserDetails;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_other_user_profile_layout);
        mImageLoader = ImageLoader.getInstance();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            UserID = bundle.getString(USER_ID, "");
        }

        initView();

        eventClick();
    }

    private void eventClick() {

        findViewById(R.id.imageProfileBackView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.textViewOfSubmitReview).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityIfNeeded(new Intent(PropertyOtherUserDetailsActivity.this, RateAndReview.class).putExtra("user_id", UserID), UPDATE_REVIEW);
            }
        });
    }

    private void initView() {
        getUserDetails(true);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == UPDATE_REVIEW) {
                getUserDetails(false);
            }
        }
    }

    private void getUserDetails(boolean isProgress) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressBar(isProgress);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserID));

        UserServices userService = ApiFactory.getInstance(PropertyOtherUserDetailsActivity.this).provideService(UserServices.class);
        Observable<OtherUserDetailsResponse> observable;
        observable = userService.getOtherUserDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<OtherUserDetailsResponse>() {
            @Override
            public void onSuccess(OtherUserDetailsResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
                    setUserDetails(response.getData());
                } else {
                    showSnkbar(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void setUserDetails(OtherUserDetails data) {
        loOtherUserDetails = data;
        ((CustomTextView) findViewById(R.id.textViewAdminName)).setText(data.getFullName());
//        ((CustomTextView) findViewById(R.id.textViewAdminName)).setText(data.getUsername());

        if (data.getProfilePic() != null && !data.getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(data.getProfilePic(), ((CircleImageView) findViewById(R.id.imageViewAdminProfile)), options);
        } else {
            ((CircleImageView) findViewById(R.id.imageViewAdminProfile)).setImageResource(R.mipmap.user);
        }

        if (data.getRating() != null && data.getRating() != 0) {
            ((RatingBar) findViewById(R.id.rotationratingbar_main)).setRating(data.getRating());
            if (data.getRating() > 4.4) {
                findViewById(R.id.imageProUser).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.imageProUser).setVisibility(View.INVISIBLE);
            }
        }
        if (data.getTotalRatting() > 1) {
            ((CustomTextView) findViewById(R.id.txtAdminRatingReviewCount)).setText(data.getTotalRatting() + " Reviews");


        } else {
            ((CustomTextView) findViewById(R.id.txtAdminRatingReviewCount)).setText(data.getTotalRatting() + " Review");
        }

        //set property
        setPropertyData(data.getPropertyList(), data.getTotalProperty(), data.getLoginId());

        if (data.getRatingList().isEmpty()) {
            if (!data.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                findViewById(R.id.relativeBeTheFirstRateBox).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.relativeBeTheFirstRateBox).setVisibility(View.GONE);
            }
        } else {
            findViewById(R.id.relativeBeTheFirstRateBox).setVisibility(View.GONE);
        }

        setReviewUser(data.getRatingList());

    }

    private void setReviewUser(List<RatingData> ratingList) {

        if (ratingList.size() != 0) {
            findViewById(R.id.linearReviewNRating).setVisibility(View.VISIBLE);
            findViewById(R.id.linerViewRatingNReview).setVisibility(View.VISIBLE);

            LinearLayout linerFqs = findViewById(R.id.linerViewRatingNReview);
            if (ratingList != null && ratingList.size() != 0) {
                linerFqs.removeAllViews();
                LayoutInflater loLayoutInflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < ratingList.size(); i++) {

                    if (i == 0 || i == 1) {
                        View loView = loLayoutInflator.inflate(R.layout.item_new_rating_ui, null);
                        linerFqs.addView(loView);

                        final RatingData loMessage = ratingList.get(i);
                        RelativeLayout rrView;
                        ConstraintLayout contentRatingMain;
                        CustomTextView txtRateUserName, txtUserRatingCount, txtRatingTimes;
                        ShowMoreTextView txtRatingDescription;

                        contentRatingMain = loView.findViewById(R.id.contentRatingMain);
                        rrView = loView.findViewById(R.id.rrView);
                        txtRatingTimes = loView.findViewById(R.id.txtRatingTimes);
                        txtUserRatingCount = loView.findViewById(R.id.txtUserRatingCount);
                        txtRatingDescription = loView.findViewById(R.id.txtRatingDescription);
                        txtRateUserName = loView.findViewById(R.id.txtRateUserName);

                        if (i == 0) {
                            contentRatingMain.setBackgroundResource(R.drawable.new_sadow_ui);
                        }
                        if (i == 1) {
                            contentRatingMain.setBackgroundResource(R.drawable.new_sadow_bottom_ui);
                        }

                        txtRatingDescription.addShowMoreText("Show more+");
                        txtRatingDescription.addShowLessText("Less");
                        txtRatingDescription.setShowingLine(3);
                        txtRatingDescription.setShowMoreColor(getResources().getColor(R.color.text_color_dark_grey_));
                        txtRatingDescription.setShowLessTextColor(getResources().getColor(R.color.text_color_dark_grey_));

                        String et1 = loMessage.getRate1();
                        String et2 = loMessage.getRate2();
                        String et3 = loMessage.getRate3();
                        String et4 = loMessage.getRate4();
                        String et5 = loMessage.getRate5();

                        float num1 = Float.parseFloat(et1);
                        float num2 = Float.parseFloat(et2);
                        float num3 = Float.parseFloat(et3);
                        float num4 = Float.parseFloat(et4);
                        float num5 = Float.parseFloat(et5);
                        float calculate = ((num1 + num2 + num3 + num4 + num5) / 5);

                        float result = calculate;
//            holder.ratingBarCount.setNumStars(5);
//            holder.ratingBarCount.setRating(result);

                        txtUserRatingCount.setText("" + result);
                        txtRatingDescription.setText(loMessage.getComment());

                        String createdDate = loMessage.getCreatedDate() + " 11:11:11";
                        txtRatingTimes.setText(DateUtils.getTimeAgo(createdDate));

                        if (loMessage.getUserDetails() != null) {
                            txtRateUserName.setText(loMessage.getUserDetails().getFullName());
//                            txtRateUserName.setText(loMessage.getUserDetails().getUsername());
                        }
                        loView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (loMessage.getLoginId() != null && !loMessage.getLoginId().equalsIgnoreCase(""))
                                    startActivityIfNeeded(new Intent(PropertyOtherUserDetailsActivity.this, PropertyOtherUserDetailsActivity.class)
                                            .putExtra(PropertyOtherUserDetailsActivity.USER_ID, loMessage.getLoginId()), 0);
                            }
                        });
                    }

                }

                if (ratingList.size() > 2) {
                    findViewById(R.id.txtReviewsViewAll).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.txtReviewsViewAll).setVisibility(View.VISIBLE);
                }

                findViewById(R.id.txtReviewsViewAll).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ratingList != null && ratingList.size() != 0) {
                            startActivityIfNeeded(new Intent(PropertyOtherUserDetailsActivity.this, RatingList.class)
                                    .putExtra("user_id", UserID), UPDATE_REVIEW);//ratingList.get(0).getLoginId()
                        }
                    }
                });
            }


        } else {
            findViewById(R.id.linearReviewNRating).setVisibility(View.GONE);
            findViewById(R.id.linerViewRatingNReview).setVisibility(View.GONE);
            findViewById(R.id.txtReviewsViewAll).setVisibility(View.GONE);
        }
    }


    private void setPropertyData(List<CommonData> propertyList, Integer totalCount, String loginId) {
        if (propertyList != null && propertyList.size() != 0) {
            findViewById(R.id.linearPropertyHeaderBox).setVisibility(View.VISIBLE);
            findViewById(R.id.linerViewMyPropertyList).setVisibility(View.VISIBLE);

            if (totalCount > 1) {
                ((CustomTextView) findViewById(R.id.txtPropertyTitle)).setText("Properties (" + totalCount + ")");
            } else {
                ((CustomTextView) findViewById(R.id.txtPropertyTitle)).setText("Property (" + totalCount + ")");
            }


            LinearLayout linerFqs = findViewById(R.id.linerViewMyPropertyList);
            if (propertyList != null && propertyList.size() != 0) {
                linerFqs.removeAllViews();
                LayoutInflater loLayoutInflator = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                for (int i = 0; i < propertyList.size(); i++) {

                    if (i == 0 || i == 1) {
                        View loView = loLayoutInflator.inflate(R.layout.item_new_property_ui, null);
                        linerFqs.addView(loView);

                        final CommonData loMessage = propertyList.get(i);
                        CustomTextView txtPropertyName, txtPropertyLocation1, txtPropertyLocation2, txtPropertyPrice, txtUnlistedListedTag, txtPropertyTypeName;
                        CustomTextView textViewPropertyDetailsThumCount, textViewPropertyDetailsViewCounts;
                        ImageView imagePropertyLikeStatus, imageTempView, imageViewUnavailableIconView, img_eye;
                        RelativeLayout relativeUnlistedListedTag;

                        imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
                        imageTempView = loView.findViewById(R.id.imageTempView);
                        imageTempView.setVisibility(View.GONE);

                        imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
                        txtPropertyName = loView.findViewById(R.id.txtPropertyName);

                        img_eye = loView.findViewById(R.id.img_eye);
                        txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
                        txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
                        txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
                        txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
                        txtPropertyTypeName = loView.findViewById(R.id.txtPropertyTypeName);
                        relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);

                        textViewPropertyDetailsViewCounts = loView.findViewById(R.id.textViewPropertyDetailsViewCounts);
                        textViewPropertyDetailsThumCount = loView.findViewById(R.id.textViewPropertyDetailsThumCount);

                        txtPropertyName.setText(loMessage.getHouse_name());
                        if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                            img_eye.setVisibility(View.GONE);
                            txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                            txtPropertyLocation1.setText(loMessage.getAddress1());
                            txtPropertyLocation2.setText(loMessage.getAddress2());
                        } else {
                            if (!BaseApplication.getInstance().getLoginID().equals(loginId)) {
                                img_eye.setVisibility(View.VISIBLE);
                                txtPropertyLocation1.setText("****");
                                txtPropertyLocation1.setTextSize(15);
                                txtPropertyLocation2.setText(loMessage.getAddress2());
                            } else {
                                img_eye.setVisibility(View.GONE);
                                txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                                txtPropertyLocation1.setText(loMessage.getAddress1());
                                txtPropertyLocation2.setText(loMessage.getAddress2());
                            }
                        }
                        txtPropertyPrice.setText("$" + currencyFormat(loMessage.getPropertyPrice()));
                        txtPropertyTypeName.setText(loMessage.getPropertyType());

                        if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
                            relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                            txtUnlistedListedTag.setText("Pre-Foreclosure");
                        } else {
                            if (loMessage.getPropertyListed().equalsIgnoreCase("0")) {
                                relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_ab_gradient);
                                txtUnlistedListedTag.setText("Off-Market");

                            } else {
                                relativeUnlistedListedTag.setBackgroundResource(R.drawable.button_gradian);
                                txtUnlistedListedTag.setText("Listed");
                            }
                        }

                        if (loMessage.isLike()) {
                            imagePropertyLikeStatus.setImageResource(R.drawable.heart_border_new);
                        } else {
                            imagePropertyLikeStatus.setImageResource(R.drawable.ic_favorite_filled);
                        }

                        if (loMessage.getIsAvailable().equalsIgnoreCase("0")) {
                            //0 unavailable
                            imageViewUnavailableIconView.setVisibility(View.VISIBLE);
                        } else {
                            //1 available
                            imageViewUnavailableIconView.setVisibility(View.GONE);
                        }

                        if (loMessage.getThumsCounts() != null && loMessage.getThumsCounts() != 0) {
                            textViewPropertyDetailsThumCount.setText("" + loMessage.getThumsCounts());
                        } else {
                            textViewPropertyDetailsThumCount.setText("0");
                        }
                        if (loMessage.getViewCounts() != null && loMessage.getViewCounts() != 0) {
                            textViewPropertyDetailsViewCounts.setText("" + loMessage.getViewCounts());
                        } else {
                            textViewPropertyDetailsViewCounts.setText("0");
                        }

                        if (loMessage.getImages() != null) {
                            if (loMessage.getImages().size() != 0) {
                                ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
                                propertyNoImage.setVisibility(View.GONE);

                                ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
                                PhotosAdapter adapter = new PhotosAdapter(PropertyOtherUserDetailsActivity.this, loMessage.getImages(), loMessage.getPropertyId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
                                pager.setAdapter(adapter);
                                CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
                                tabLayout.setViewPager(pager);
                            } else {
                                //show no image view
                                ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
                                propertyNoImage.setVisibility(View.VISIBLE);
                                ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
                                pager.setVisibility(View.INVISIBLE);
                                CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
                                tabLayout.setVisibility(View.INVISIBLE);
                            }
                        } else {
                            //show no image view
                            ImageView propertyNoImage = loView.findViewById(R.id.PropertyNoimage);
                            propertyNoImage.setVisibility(View.VISIBLE);
                            ViewPager pager = (ViewPager) loView.findViewById(R.id.viewpager2);
                            pager.setVisibility(View.INVISIBLE);
                            CircleIndicator tabLayout = (CircleIndicator) loView.findViewById(R.id.indicator);
                            tabLayout.setVisibility(View.INVISIBLE);
                        }

                        loView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (loMessage.getLoginId() != null && !loMessage.getLoginId().equals("")
                                        && loMessage.getPropertyId() != null && !loMessage.getPropertyId().equals("")) {
                                    if (loMessage.getIsAvailable().equalsIgnoreCase("1")) {
                                        startActivityIfNeeded(PropertyDetails.getIntentActivity(PropertyOtherUserDetailsActivity.this,
                                                loMessage.getPropertyId(), loMessage.getLoginId(),
                                                "PROPERTY", loMessage.isExpiriedStatus()), UPDATE_REVIEW);
                                    } else {
                                        if (loMessage.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                            startActivityIfNeeded(PropertyDetails.getIntentActivity(PropertyOtherUserDetailsActivity.this,
                                                    loMessage.getPropertyId(), loMessage.getLoginId(),
                                                    "PROPERTY", loMessage.isExpiriedStatus()), UPDATE_REVIEW);
                                        } else {
                                            showNotAvailable();
                                        }
                                    }
                                } else {
                                    showNotAvailable();
                                }

                            }
                        });
                    }

                }

                if (propertyList.size() > 2) {
                    findViewById(R.id.txtPropertiesViewAll).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.txtPropertiesViewAll).setVisibility(View.GONE);
                }
                findViewById(R.id.txtPropertiesViewAll).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (propertyList != null && propertyList.size() != 0) {
                            startActivity(new Intent(PropertyOtherUserDetailsActivity.this, OtherUserPropertyList.class)
                                    .putExtra("p_list", loOtherUserDetails));
                        }
                    }
                });

            }

        } else {
            findViewById(R.id.linearPropertyHeaderBox).setVisibility(View.GONE);
            findViewById(R.id.linerViewMyPropertyList).setVisibility(View.GONE);
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(PropertyOtherUserDetailsActivity.this);
        dialog.setTitle("Flippbidd");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentText(getString(R.string.String_property_not_available));
        dialog.setPositiveListener("OK", new ColorDialog.OnPositiveListener() {
            @Override
            public void onClick(ColorDialog dialog) {
                dialog.dismiss();
            }
        }).show();
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(R.id.otherUserDetailscontent), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }
}
