package com.flippbidd.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Model.Response.OtherUserDetailsResponse;
import com.flippbidd.Model.Response.RatingData;
import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.Others.Utils;
import com.flippbidd.Others.Validator;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.ProfileEditActivity;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.activity.Rating.RatingList;
import com.flippbidd.activity.Rental.AddNewRental;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.baseclass.BaseFragment;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.flippbidd.views.ShowMoreTextView;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import butterknife.BindView;
import butterknife.OnClick;
import cn.refactor.lib.colordialog.ColorDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static com.flippbidd.Others.Constants.FREE_VERSION;
import static com.flippbidd.Others.Constants.PRO_VERSION;

public class MyProfile extends BaseFragment implements Spinner.OnItemSelectedListener, BSImagePicker.OnSingleImageSelectedListener {

    private static final String TAG = LogUtils.makeLogTag(AddNewRental.class);

    public final int REQUEST_TO_PRO_CODE = 1122;
    public final int REQUEST_TO_EDITPROFILE_CODE = 1133;

    @BindView(R.id.textViewMyProfileUserNameValues)
    CustomTextView textViewMyProfileUserNameValues;

    @BindView(R.id.ratingBar2)
    RatingBar ratingBar2;
    @BindView(R.id.textViewRatingCount)
    CustomTextView textViewRatingCount;


    @BindView(R.id.tvPOFNameLink)
    CustomTextView tvPOFNameLink;
    @BindView(R.id.tvPOFHeaderMessage)
    CustomTextView tvPOFHeaderMessage;
    @BindView(R.id.tvPOFLastUpdateDate)
    CustomTextView tvPOFLastUpdateDate;
    @BindView(R.id.relativePOFLink)
    RelativeLayout relativePOFLink;
    @BindView(R.id.relativePOFUploadBox)
    RelativeLayout relativePOFUploadBox;
    @BindView(R.id.UploadPFO)
    CustomTextView UploadPFO;
    @BindView(R.id.txtUpgradedToPro)
    CustomTextView txtUpgradedToPro;
    @BindView(R.id.txtProUser)
    CustomTextView txtProUser;


    Validator mValidator;
    private static File mImageThumbFile = null;
    List<CommonListData> mProfessionList = new ArrayList<>();

    public static CircleImageView imageCircleViewMyProfile;
    public static boolean isClickable = false;
    UserDetails mUserDetails;
    ImageLoader mImageLoader;
    Disposable disposable;
    Handler handler = new Handler();

    View moView;


    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_my_profile_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.moView = view;
        mUserDetails = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
        mImageLoader = ImageLoader.getInstance();
        imageCircleViewMyProfile = view.findViewById(R.id.imageCircleViewMyProfile);


        MainActivity.image_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //open new edit activity
                getActivity().startActivityIfNeeded(new Intent(getActivity(), ProfileEditActivity.class), REQUEST_TO_EDITPROFILE_CODE);

            }
        });
        txtUpgradedToPro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startActivityIfNeeded(new Intent(getActivity(), InAppPurchaseActivity.class), REQUEST_TO_PRO_CODE);
            }
        });

        getUserDetails(true);
    }

    private void isProButton(int isVisible) {
        txtUpgradedToPro.setVisibility(isVisible);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick({R.id.imageCircleViewMyProfile})
    void viewOnClickEvent(View view) {
        switch (view.getId()) {
            case R.id.imageCircleViewMyProfile:
                if (isClickable) {
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                            .build();
                    pickerDialog.show(getChildFragmentManager(), "picker");
                }
                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            // other 'case' lines to check for other
            // permissions this app might request.
        }
    }

    public void seekBarShow(String mString, int mColor) {
        Snackbar snackbar = Snackbar.make(getActivity().findViewById(android.R.id.content), mString, 1000);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(mColor);
        snackbar.show();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (handler != null)
            handler.removeCallbacksAndMessages("stop");

        mUserDetails = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isClickable = false;
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = ((TextView) view.findViewById(R.id.textViewTitle)).getText().toString();
        if (mProfessionList != null && mProfessionList.size() != 0) {
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        // "file:///mnt/sdcard/FileName.mp3"
        try {
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(uri).setCropShape(CropImageView.CropShape.OVAL).setAspectRatio(4,4)
                    .start(getContext(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                File file = null;
                try {
                    String path = resultUri.toString();
                    file = new File(new URI(path));
                    mImageThumbFile = file;
                    String filePath = mImageThumbFile.getPath();
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    imageCircleViewMyProfile.setImageBitmap(bitmap);
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == REQUEST_TO_EDITPROFILE_CODE) {
            mUserDetails = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
            getUserDetails(false);
        } else if (requestCode == Constants.REQUEST_UNAVILABLE) {
            getUserDetails(false);
        }
    }


    private void getUserDetails(boolean isProgress) {

        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressDialog(isProgress);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<OtherUserDetailsResponse> observable;
        observable = userService.getOtherUserDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<OtherUserDetailsResponse>() {
            @Override
            public void onSuccess(OtherUserDetailsResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                hideProgressDialog();
                if (response.getSuccess()) {
                    setUserDetails(response.getData());
                } else {
                    showToast(response.getText());
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    private void setUserDetails(OtherUserDetails data) {
        System.out.println("X set user Details called");
        ((CustomTextView) moView.findViewById(R.id.textViewMyProfileUserNameValues)).setText(data.getFullName());
//        ((CustomTextView) moView.findViewById(R.id.textViewMyProfileUserNameValues)).setText(data.getUsername());

        if (data.getProfilePic() != null && !data.getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(data.getProfilePic(), ((CircleImageView) moView.findViewById(R.id.imageCircleViewMyProfile)), options);
        } else {
            ((CircleImageView) moView.findViewById(R.id.imageCircleViewMyProfile)).setImageResource(R.mipmap.user);
        }

        if (data.getRating() != null && data.getRating() != 0) {
            ((RatingBar) moView.findViewById(R.id.ratingBar2)).setRating(data.getRating());
            if (data.getRating() > 4.4) {
                moView.findViewById(R.id.imageProUser).setVisibility(View.VISIBLE);
            } else {
                moView.findViewById(R.id.imageProUser).setVisibility(View.INVISIBLE);
            }
        }
        if (data.getTotalRatting() > 1) {
            ((CustomTextView) moView.findViewById(R.id.textViewRatingCount)).setText("(" + data.getTotalRatting() + " Reviews)");
        } else {
            ((CustomTextView) moView.findViewById(R.id.textViewRatingCount)).setText("(" + data.getTotalRatting() + " Review)");
        }


        if (data.getPofDoc() != null && !data.getPofDoc().equalsIgnoreCase("")) {
            //show
            tvPOFHeaderMessage.setText(getActivity().getString(R.string.doc_expired_message));
            tvPOFHeaderMessage.setVisibility(View.GONE);
            relativePOFLink.setVisibility(View.GONE);
            tvPOFLastUpdateDate.setVisibility(View.GONE);
            relativePOFUploadBox.setVisibility(View.GONE);

            tvPOFNameLink.setPaintFlags(tvPOFNameLink.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            tvPOFNameLink.setText("" + data.getUsername() + "'s POF");

            try {
                if (data.getModifyDate() != null && !data.getModifyDate().equalsIgnoreCase("")) {
                    //2020-11-18
                    tvPOFLastUpdateDate.setText("Updated: " + Utils.getConvertData1(data.getModifyDate(), "yyyy-mm-dd", "MM/dd/yyyy"));
                } else {
                    tvPOFLastUpdateDate.setVisibility(View.GONE);
                }
            } catch (Exception e) {

            }

            tvPOFNameLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.parse("http://docs.google.com/viewer?url=" + data.getPofDoc()), "text/html");
                        startActivity(intent);
                    } catch (Exception e) {

                    }
                }
            });

        } else {
            tvPOFHeaderMessage.setText("");//getActivity().getString(R.string.doc_not_uploaded_message_)
            relativePOFLink.setVisibility(View.GONE);
            tvPOFLastUpdateDate.setVisibility(View.GONE);
            relativePOFUploadBox.setVisibility(View.GONE);

            UploadPFO.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //open new edit activity
                    getActivity().startActivityIfNeeded(new Intent(getActivity(), ProfileEditActivity.class), REQUEST_TO_EDITPROFILE_CODE);
                }
            });


        }
        //check user Plan Name
        if (data.getPlan().equalsIgnoreCase(FREE_VERSION)) {
            isProButton(View.VISIBLE);
            txtProUser.setVisibility(View.GONE);

            //update plan status view
            Intent intent = new Intent(Constants.UPDATE_PLAN_STATUS);
            intent.putExtra(Constants.PLAN_STATUS, false);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        } else {
            isProButton(View.GONE);
            //pro user
            txtProUser.setVisibility(View.VISIBLE);
            //update plan status view
            Intent intent = new Intent(Constants.UPDATE_PLAN_STATUS);
            intent.putExtra(Constants.PLAN_STATUS, true);
            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
        }
        //set property
        setPropertyData(data.getPropertyList(), data.getTotalProperty());

        setReviewUser(data.getRatingList());

    }

    private void setReviewUser(List<RatingData> ratingList) {
        System.out.println("X setReviewUser called");
        System.out.println("Review user called");
        if (ratingList.size() != 0) {
            moView.findViewById(R.id.linearReviewNRating).setVisibility(View.VISIBLE);
            moView.findViewById(R.id.linerViewRatingNReview).setVisibility(View.VISIBLE);

            LinearLayout linerFqs = moView.findViewById(R.id.linerViewRatingNReview);
            if (ratingList != null && ratingList.size() != 0) {
                linerFqs.removeAllViews();
                LayoutInflater loLayoutInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                        txtRatingDescription.setShowingLine(2);
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
//                            txtRateUserName.setText(loMessage.getUserDetails().getFullName());
                            txtRateUserName.setText(loMessage.getUserDetails().getUsername());
                        }
                        loView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (loMessage.getLoginId() != null && !loMessage.getLoginId().equalsIgnoreCase(""))
                                    startActivity(new Intent(getActivity(), PropertyOtherUserDetailsActivity.class)
                                            .putExtra(PropertyOtherUserDetailsActivity.USER_ID, loMessage.getLoginId()));
                            }
                        });
                    }

                }

                if (ratingList.size() > 2) {
                    moView.findViewById(R.id.txtReviewsViewAll).setVisibility(View.VISIBLE);
                } else {
                    moView.findViewById(R.id.txtReviewsViewAll).setVisibility(View.VISIBLE);
                }

                moView.findViewById(R.id.txtReviewsViewAll).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ratingList != null && ratingList.size() != 0) {
                            startActivity(new Intent(getActivity(), RatingList.class)
                                    .putExtra("user_id", BaseApplication.getInstance().getLoginID()));//ratingList.get(0).getLoginId()
                        }
                    }
                });
            }


        } else {
            System.out.println("HIDDDDEN -:>");
            moView.findViewById(R.id.linearReviewNRating).setVisibility(View.GONE);
            moView.findViewById(R.id.linerViewRatingNReview).setVisibility(View.GONE);
            moView.findViewById(R.id.txtReviewsViewAll).setVisibility(View.GONE);
        }
    }

    private void setPropertyData(List<CommonData> propertyList, Integer total) {
        System.out.println("X setPropertyDetails Called");
        if (propertyList != null && propertyList.size() != 0) {
            moView.findViewById(R.id.linearPropertyHeaderBox).setVisibility(View.VISIBLE);
            moView.findViewById(R.id.linerViewMyPropertyList).setVisibility(View.VISIBLE);

            if (total > 1) {
                ((CustomTextView) moView.findViewById(R.id.txtPropertyTitle)).setText("Properties (" + total + ")");
            } else {
                ((CustomTextView) moView.findViewById(R.id.txtPropertyTitle)).setText("Property (" + total + ")");
            }


            LinearLayout linerFqs = moView.findViewById(R.id.linerViewMyPropertyList);
            if (propertyList != null && propertyList.size() != 0) {
                linerFqs.removeAllViews();
                LayoutInflater loLayoutInflator = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                for (int i = 0; i < propertyList.size(); i++) {
                    View loView = loLayoutInflator.inflate(R.layout.item_new_property_ui, null);
                    linerFqs.addView(loView);

                    final CommonData loMessage = propertyList.get(i);
//                    for (int z= 0; i<propertyList.size(); i++){
//                        System.out.println( "->>>:"+propertyList.get(z).getAddress());
//                        System.out.println( "->>>:"+propertyList.get(z).getAddress1());
//                        System.out.println( "->>>:"+propertyList.get(z).getAddress2());
//                    }
                    CustomTextView txtPropertyName, txtPropertyLocation1,txtPropertyLocation2, txtPropertyPrice, txtUnlistedListedTag, txtPropertyTypeName;
                    CustomTextView textViewPropertyDetailsThumCount, textViewPropertyDetailsViewCounts;
                    ImageView imagePropertyLikeStatus, imageTempView, imageViewUnavailableIconView,img_eye_icon;
                    RelativeLayout relativeUnlistedListedTag;

                    imageViewUnavailableIconView = loView.findViewById(R.id.imageViewUnavailableIconView);
                    imageTempView = loView.findViewById(R.id.imageTempView);
                    imageTempView.setVisibility(View.GONE);

                    imagePropertyLikeStatus = loView.findViewById(R.id.imagePropertyLikeStatus);
                    txtPropertyName = loView.findViewById(R.id.txtPropertyName);
                    txtPropertyLocation1 = loView.findViewById(R.id.txtPropertyLocation1);
                    txtPropertyLocation2 = loView.findViewById(R.id.txtPropertyLocation2);
                    txtPropertyPrice = loView.findViewById(R.id.txtPropertyPrice);
                    txtUnlistedListedTag = loView.findViewById(R.id.txtUnlistedListedTag);
                    txtPropertyTypeName = loView.findViewById(R.id.txtPropertyTypeName);
                    relativeUnlistedListedTag = loView.findViewById(R.id.relativeUnlistedListedTag);
                    textViewPropertyDetailsViewCounts = loView.findViewById(R.id.textViewPropertyDetailsViewCounts);
                    textViewPropertyDetailsThumCount = loView.findViewById(R.id.textViewPropertyDetailsThumCount);
                    img_eye_icon = loView.findViewById(R.id.img_eye);

                    //   this is for checking the user plan and display the address vice-versa  //

                    if (PreferenceUtils.getIsPremiumUser() == 1) {// is premium user is true
                        img_eye_icon.setVisibility(View.GONE);
                        txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                        txtPropertyLocation1.setText(loMessage.getAddress1());
                        txtPropertyLocation2.setText(loMessage.getAddress2());
                    } else {
                        if (!BaseApplication.getInstance().getLoginID().equals(loMessage.getData().getLoginId())) {
                            img_eye_icon.setVisibility(View.VISIBLE);
                            txtPropertyLocation1.setText("****");
                            txtPropertyLocation1.setTextSize(15);
                            txtPropertyLocation2.setText(loMessage.getAddress2());
                        } else {
                            img_eye_icon.setVisibility(View.GONE);
                            txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                            txtPropertyLocation1.setText(loMessage.getAddress1());
                            txtPropertyLocation2.setText(loMessage.getAddress2());
                        }

                    }

                    if (PreferenceUtils.getIsPremiumUser() != 1){
                        img_eye_icon.setVisibility(View.VISIBLE);
                        txtPropertyLocation1.setText("****");
                        txtPropertyLocation1.setTextSize(15);
                        txtPropertyLocation2.setText(loMessage.getAddress2());
                    }
                    if (PreferenceUtils.getPlanVersionStatus()){
                    img_eye_icon.setVisibility(View.GONE);
                    txtPropertyLocation1.setTextColor(getResources().getColor(R.color.grey_font));
                    txtPropertyLocation1.setText(loMessage.getAddress1());
                    txtPropertyLocation2.setText(loMessage.getAddress2());
                    }
                    txtPropertyName.setText(loMessage.getHouse_name());
                    //txtPropertyLocation.setText(loMessage.getAddress());
                    // address 2 also need to show there  //
                    txtPropertyPrice.setText("$" + currencyFormat(loMessage.getPropertyPrice()));
                    txtPropertyTypeName.setText(loMessage.getPropertyType());
                    if (loMessage.getPreForeclosure().equalsIgnoreCase("1")) {
//                        holder.viewTimeCountDown.setBackgroundResource(R.drawable.blue);
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
                            PhotosAdapter adapter = new PhotosAdapter(mBaseAppCompatActivity, loMessage.getImages(), loMessage.getPropertyId(), loMessage.getLoginId(), loMessage.isExpiriedStatus(), loMessage.getIsAvailable());
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
                            if (loMessage.getLoginId() != null && !loMessage.getLoginId().equals("")) {
                                if (loMessage.getIsAvailable().equalsIgnoreCase("1")) {
                                    mBaseAppCompatActivity.startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity,
                                            loMessage.getPropertyId(), loMessage.getLoginId(),
                                            "PROPERTY", loMessage.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
                                } else {
                                    if (loMessage.getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                                        mBaseAppCompatActivity.startActivityIfNeeded(PropertyDetails.getIntentActivity(mBaseAppCompatActivity,
                                                loMessage.getPropertyId(), loMessage.getLoginId(),
                                                "PROPERTY", loMessage.isExpiriedStatus()), Constants.REQUEST_UNAVILABLE);
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

        } else {
            moView.findViewById(R.id.linearPropertyHeaderBox).setVisibility(View.GONE);
            moView.findViewById(R.id.linerViewMyPropertyList).setVisibility(View.GONE);
        }
    }

    private void showNotAvailable() {
        ColorDialog dialog = new ColorDialog(getActivity());
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


}
