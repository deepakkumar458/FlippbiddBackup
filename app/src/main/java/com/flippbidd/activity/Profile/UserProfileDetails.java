package com.flippbidd.activity.Profile;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;

import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Rating.RatingList;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import butterknife.BindView;
import butterknife.OnClick;

public class UserProfileDetails extends BaseAppCompatActivity {

    private static final String TAG = LogUtils.makeLogTag(UserProfileDetails.class);

    @BindView(R.id.textViewMyProfileUserNameTitle1)
    CustomTextView textViewMyProfileUserNameTitle1;
    @BindView(R.id.textViewMyProfileAddressTitle)
    CustomTextView textViewMyProfileAddressTitle;
    @BindView(R.id.textViewMyProfileUserNameValues)
    CustomTextView textViewMyProfileUserNameValues;
    @BindView(R.id.textViewMyProfileFullNameValues)
    CustomTextView textViewMyProfileFullNameValues;
    /*
        @BindView(R.id.textViewSelectStateTitle)
        CustomTextView textViewSelectStateTitle;
        @BindView(R.id.textViewSelectCityTitle)
        CustomTextView textViewSelectCityTitle;
        @BindView(R.id.editViewMyProfileProfessionValues)
        CustomTextView editViewMyProfileProfessionValues;
    */
    @BindView(R.id.imageCircleViewMyProfile)
    CircleImageView imageCircleViewMyProfile;
    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;
    @BindView(R.id.textViewRatingCount)
    CustomTextView textViewRatingCount;
    @BindView(R.id.ratingBar2)
    RatingBar ratingBar2;
    @BindView(R.id.linearLayoutOfReviewData)
    LinearLayout linearLayoutOfReviewData;

    ImageLoader mImageLoader;
    UserDetails mCommonDetailsData;


    public static Intent getIntentActivity(Context mContext, UserDetails mUserDetails) {
        Intent mIntent = new Intent(mContext, UserProfileDetails.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mUserDetails);
        return mIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoader.getInstance();
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mCommonDetailsData = (UserDetails) mBundle.getSerializable(Constants.EXTRA.DATA);
        }

        setUserDetails();
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }
    private void setUserDetails() {

        if (mCommonDetailsData.getFullName() != null && !mCommonDetailsData.getFullName().equalsIgnoreCase("")) {
            textViewMyProfileUserNameTitle1.setText(mCommonDetailsData.getFullName());
        }
//        if (mCommonDetailsData.getUsername() != null && !mCommonDetailsData.getUsername().equalsIgnoreCase("")) {
//            textViewMyProfileUserNameTitle1.setText(mCommonDetailsData.getUsername());
//        }

        /*if (mCommonDetailsData.getAddress() != null && !mCommonDetailsData.getAddress().equalsIgnoreCase("")) {
            textViewMyProfileAddressTitle.setVisibility(View.VISIBLE);
            textViewMyProfileAddressTitle.setText(mCommonDetailsData.getAddress());
        } else {
            textViewMyProfileAddressTitle.setVisibility(View.GONE);
        }*/

        if (mCommonDetailsData.getUsername() != null && !mCommonDetailsData.getUsername().equalsIgnoreCase("")) {
            textViewMyProfileUserNameValues.setText(mCommonDetailsData.getUsername());
        }
        if (mCommonDetailsData.getFullName() != null && !mCommonDetailsData.getFullName().equalsIgnoreCase("")) {
            textViewMyProfileFullNameValues.setText(mCommonDetailsData.getFullName());
        }
//        if (mCommonDetailsData.getUsername() != null && !mCommonDetailsData.getUsername().equalsIgnoreCase("")) {
//            textViewMyProfileFullNameValues.setText(mCommonDetailsData.getUsername());
//        }

 /*       if (mCommonDetailsData.getStateName() != null && !mCommonDetailsData.getStateName().equalsIgnoreCase("")) {

            if (mCommonDetailsData.getStateName().equalsIgnoreCase("0")) {
                textViewSelectStateTitle.setText("");
            } else {
                textViewSelectStateTitle.setText(mCommonDetailsData.getStateName());
            }
        }

        if (mCommonDetailsData.getCityName() != null && !mCommonDetailsData.getCityName().equalsIgnoreCase("")) {
            if (mCommonDetailsData.getCityName().equalsIgnoreCase("0")) {
                textViewSelectCityTitle.setText("");
            } else {
                textViewSelectCityTitle.setText(mCommonDetailsData.getCityName());
            }

        }
        if (mCommonDetailsData.getProfession() != null && !mCommonDetailsData.getProfession().equalsIgnoreCase("")) {
            if (mCommonDetailsData.getProfession().equalsIgnoreCase("0")) {
                editViewMyProfileProfessionValues.setText("");
            } else {
                editViewMyProfileProfessionValues.setText(mCommonDetailsData.getProfession());
            }
        }
*/
        if (mCommonDetailsData.getProfilePic() != null && !mCommonDetailsData.getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheOnDisc(true)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getProfilePic(), imageCircleViewMyProfile, options);
        } else {
            imageCircleViewMyProfile.setImageResource(R.mipmap.user);
        }

        if (mCommonDetailsData.getRateAverage() != null && mCommonDetailsData.getRateAverage() != 0) {
            float rate = Float.parseFloat("" + mCommonDetailsData.getRateAverage());
            ratingBar2.setRating(rate);
        }
        textViewRatingCount.setText("(" + mCommonDetailsData.getRateCount() + ")");
    }

    @Override
    protected int getLayoutResource() {
        return R.layout.activity_user_profile_details_layout;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {

    }

    @OnClick(R.id.imageViewBack)
    void onClickView() {
        onBackPressed();
    }


    @OnClick({R.id.linearLayoutOfReviewData, R.id.ratingBar2, R.id.textViewRatingCount})
    void ratingClick() {
        startActivity(new Intent(UserProfileDetails.this, RatingList.class).putExtra("user_id", mCommonDetailsData.getLoginId()), true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishTop(true);
    }
}
