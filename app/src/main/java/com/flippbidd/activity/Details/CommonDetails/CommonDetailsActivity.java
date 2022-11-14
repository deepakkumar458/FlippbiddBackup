package com.flippbidd.activity.Details.CommonDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Contract.RequestContractActivity;
import com.flippbidd.activity.Profile.UserProfileDetails;
import com.flippbidd.activity.Rating.RatingList;
import com.flippbidd.activity.UploadFiles_Activity_List;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.google.android.material.snackbar.Snackbar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CommonDetailsActivity extends BaseActivity {

    private static final String TAG = CommonDetailsActivity.class.getSimpleName();

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_MAIN_ID = "GROUP_CHANNEL_MAIN_ID";
    public static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    public static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";

    Activity mBaseAppCompatActivity;

    private List<String> mSelectedIds;
    private boolean mIsDistinct = true;

    private String mStringScreen, mCommonId, mScreenName, mSelectedTypeIs, mLoginId;
    private boolean isExpiredStatus = true;
    private Disposable disposable;


    @BindView(R.id.linearRadioGroup)
    LinearLayout linearRadioGroup;
    @BindView(R.id.linearCheckedView)
    LinearLayout linearCheckedView;
    @BindView(R.id.relativeLeaseTermsTypeBox)
    RelativeLayout relativeLeaseTermsTypeBox;
    /*
        @BindView(R.id.relativeSellerTypeBox)
        RelativeLayout relativeSellerTypeBox;
    */
    @BindView(R.id.relativeLayoutOfAvailableDateBox)
    RelativeLayout relativeLayoutOfAvailableDateBox;

    @BindView(R.id.imageMainPropertyHeader)
    ImageView imageMainPropertyHeader;
    @BindView(R.id.textViewPropertyDeatilsAddress)
    CustomTextView textViewPropertyDeatilsAddress;
    @BindView(R.id.textViewPropertyDetailsType)
    CustomTextView textViewPropertyDetailsType;
    @BindView(R.id.textViewPropertyDetailsDateTime)
    CustomTextView textViewPropertyDetailsDateTime;
    @BindView(R.id.textViewPropertyDetailsBedsNo)
    CustomTextView textViewPropertyDetailsBedsNo;
    @BindView(R.id.textViewPropertyDetailsBathNo)
    CustomTextView textViewPropertyDetailsBathNo;
    @BindView(R.id.textViewPropertyDetailsRent)
    CustomTextView textViewPropertyDetailsRent;
    @BindView(R.id.textViewPropertyDetailsDescription)
    CustomTextView textViewPropertyDetailsDescription;

    @BindView(R.id.textViewStateValues)
    CustomTextView textViewStateValues;
    @BindView(R.id.textViewCityValues)
    CustomTextView textViewCityValues;
    @BindView(R.id.textViewAreaValues)
    CustomTextView textViewAreaValues;


    /*@BindView(R.id.relativeLayoutDocumentUrl)
    RelativeLayout relativeLayoutDocumentUrl;
    @BindView(R.id.imageViewUploadDocumentsIcon)
    ImageView imageViewUploadDocumentsIcon;
        @BindView(R.id.textViewDetailsDocuments)
        CustomTextView textViewDetailsDocuments;*/
    @BindView(R.id.scroll)
    NestedScrollView scrollView;
    @BindView(R.id.linearLayoutOfRentalData)
    LinearLayout linearLayoutOfRentalData;
    @BindView(R.id.textViewRentalRateValues)
    CustomTextView textViewRentalRateValues;
    @BindView(R.id.textViewRentalSecurityVaule)
    CustomTextView textViewRentalSecurityVaule;
    @BindView(R.id.textViewRentalOtherFeesVaule)
    CustomTextView textViewRentalOtherFeesVaule;
    @BindView(R.id.relativeLayoutOfPriceBox)
    RelativeLayout relativeLayoutOfPriceBox;

    /*
        @BindView(R.id.textViewSellerTypeValues)
        CustomTextView textViewSellerTypeValues;
    */
    @BindView(R.id.textViewPropetyTypeValues)
    CustomTextView textViewPropetyTypeValues;
    @BindView(R.id.textViewLeaseTermsValues)
    CustomTextView textViewLeaseTermsValues;

    @BindView(R.id.radioVacantGroup)
    RadioGroup radioVacantGroup;
    @BindView(R.id.groupRadioListed)
    RadioGroup groupRadioListed;
    @BindView(R.id.radioCreditCheckGroup)
    RadioGroup radioCreditCheckGroup;

    //    @BindView(R.id.linearLayoutNeededDoc)
//    LinearLayout linearLayoutNeededDoc;
    @BindView(R.id.checkBoxW2)
    CheckBox checkBoxW2;
    @BindView(R.id.checkBoxDriverLicense)
    CheckBox checkBoxDriverLicense;
    @BindView(R.id.checkBoxPaystubs)
    CheckBox checkBoxPaystubs;
    @BindView(R.id.checkBoxBankStatements)
    CheckBox checkBoxBankStatements;
    @BindView(R.id.checkBoxTaxReturn)
    CheckBox checkBoxTaxReturn;
    @BindView(R.id.checkBoxOther)
    CheckBox checkBoxOther;
    @BindView(R.id.rilativeaLayoutPropety)
    RelativeLayout rilativeaLayoutPropety;
    @BindView(R.id.relativeMessageBox)
    RelativeLayout relativeMessageBox;
    @BindView(R.id.relativeContractBox)
    RelativeLayout relativeContractBox;
    @BindView(R.id.iconEnvalop)
    ImageView iconEnvalop;
    @BindView(R.id.relativeFilesUploadBox)
    RelativeLayout relativeFilesUploadBox;
    @BindView(R.id.iconFileUpload)
    ImageView iconFileUplod;
    @BindView(R.id.relativeLayoutUserDetailsBox)
    RelativeLayout relativeLayoutUserDetailsBox;
    @BindView(R.id.imageViewUserProfile)
    CircleImageView imageViewUserProfile;
    @BindView(R.id.textViewUserName)
    CustomTextView textViewUserName;

    @BindView(R.id.relativeLayoutOfArea)
    RelativeLayout relativeLayoutOfArea;
    @BindView(R.id.linearlayoutOfBedsBathBox)
    LinearLayout linearlayoutOfBedsBathBox;
    @BindView(R.id.relativeLayoutOfApartmentType)
    RelativeLayout relativeLayoutOfApartmentType;

    //service
    @BindView(R.id.relativeLayoutServiceType)
    RelativeLayout relativeLayoutServiceType;
    @BindView(R.id.relativeLayoutServicePrice)
    RelativeLayout relativeLayoutServicePrice;
    @BindView(R.id.relativeLayoutServiceLicensed)
    RelativeLayout relativeLayoutServiceLicensed;
    @BindView(R.id.textViewServiceTypeValues)
    CustomTextView textViewServiceTypeValues;
    @BindView(R.id.textViewServicePriceValues)
    CustomTextView textViewServicePriceValues;
    @BindView(R.id.textViewServiceListedValues)
    CustomTextView textViewServiceListedValues;
    @BindView(R.id.imageViewBack)
    ImageView imageViewBack;
    @BindView(R.id.imageViewShareIcon)
    ImageView imageViewShareIcon;
    @BindView(R.id.relativeLayoutPropertyPerforeclosure)
    RelativeLayout relativeLayoutPropertyPerforeclosure;
    @BindView(R.id.textViewPropertyPerforeclosureValues)
    CustomTextView textViewPropertyPerforeclosureValues;
    @BindView(R.id.textViewOfMessagesBox)
    CustomTextView textViewOfMessagesBox;
    @BindView(R.id.relativeMessageBox1)
    RelativeLayout relativeMessageBox1;
    @BindView(R.id.textViewNDRSValues)
    CustomTextView textViewNDRSValues;
    @BindView(R.id.relativeLayoutOfNDRS)
    RelativeLayout relativeLayoutOfNDRS;

    @BindView(R.id.textViewProductTitle)
    CustomTextView textViewProductTitle;
    @BindView(R.id.rotationratingbar_main)
    RatingBar rotationratingbar_main;

    @BindView(R.id.tvDetailsHeaderTitle)
    CustomTextView tvDetailsHeaderTitle;

    private RadioButton radioListedButton, radioVacatedButton;
    private RadioButton radioCreditCheckButton;
    List<String> mDocNeededList = new ArrayList<>();

    ImageLoader mImageLoader;
    CommonData mCommonDetailsData;
    String channelStatus = "";
    String channelUrl = "";
    String channelMainId = "";
    String channelPropertyId = "";
    String propertyOwenrEmail = "";

    String FROME_TO = "";

    /*dyanmic*/
/*
    Uri dynamicLinkUri;
    ProgressDialog mProgressDialog;
*/


    public static Intent getIntentActivity(Context mContext, String mId, String mLoginId, String mScreenType, boolean isTimeExpired) {
        Intent mIntent = new Intent(mContext, CommonDetailsActivity.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mId);
        mIntent.putExtra(Constants.EXTRA.LOGINID, mLoginId);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mScreenType);
        mIntent.putExtra(Constants.EXTRA.EXPIRED_STATUS, isTimeExpired);
        mIntent.putExtra(Constants.EXTRA.FROM_TO, "");
        return mIntent;
    }

  /*  @Override
    protected int getLayoutResource() {
        return R.layout.activity_new_common_details_property_layout;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_common_details_property_layout);
        ButterKnife.bind(this);
        this.mBaseAppCompatActivity = CommonDetailsActivity.this;

        mSelectedIds = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            mStringScreen = mBundle.getString(Constants.EXTRA.SCREEN_TYPE);
            mCommonId = mBundle.getString(Constants.EXTRA.DATA);
            mLoginId = mBundle.getString(Constants.EXTRA.LOGINID);
            isExpiredStatus = mBundle.getBoolean(Constants.EXTRA.EXPIRED_STATUS);
            if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY)) {
                mScreenName = "property";
                mSelectedTypeIs = "Property";
                tvDetailsHeaderTitle.setText("Property details");
                //Property item LikesDetails code
            } else if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                //Rental Item LikesDetails code
                mScreenName = "rental";
                mSelectedTypeIs = "Rental";
                tvDetailsHeaderTitle.setText("Property details");
            } else if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE)) {
                //Service Item LikesDetails Code
                mScreenName = "service";
                mSelectedTypeIs = "Home Service";
                tvDetailsHeaderTitle.setText("Service details");
            }

            //check key
            try {
                FROME_TO = mBundle.getString(Constants.EXTRA.FROM_TO);
            } catch (Exception e) {
                FROME_TO = "";
            }

        }
        getDetailsProperty(true);


        rotationratingbar_main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                try {
                    if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
                        startActivity(new Intent(CommonDetailsActivity.this, RatingList.class).putExtra("user_id", mCommonDetailsData.getLoginId()));
                    }
                } catch (Exception e) {
                }
                return false;
            }
        });
    }

    private void showMessageButtonBox() {
        if (!UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId().equalsIgnoreCase(mLoginId)) {
            iconFileUplod.setVisibility(View.VISIBLE);
            if (!isExpiredStatus) {
                if (!mCommonDetailsData.getIsAvailable().equalsIgnoreCase("0")) {
                    relativeMessageBox.setVisibility(View.GONE);//Client required
                    relativeContractBox.setVisibility(View.VISIBLE);
                    relativeMessageBox1.setVisibility(View.VISIBLE);
                } else {
                    relativeMessageBox.setVisibility(View.GONE);
                    relativeContractBox.setVisibility(View.GONE);
                    relativeMessageBox1.setVisibility(View.GONE);
                    iconFileUplod.setVisibility(View.GONE);
                }
            } else {
                //add for test
                relativeMessageBox.setVisibility(View.GONE);
                relativeContractBox.setVisibility(View.GONE);
                relativeMessageBox1.setVisibility(View.GONE);
            }

            //add user
            mSelectedIds.add(PreferenceUtils.getUserId());//current user id
            mSelectedIds.add(mCommonDetailsData.getData().getEmailAddress()); //other user id
        } else {
            //chat hide
            relativeMessageBox.setVisibility(View.GONE);
            relativeContractBox.setVisibility(View.GONE);
            relativeMessageBox1.setVisibility(View.GONE);

            iconFileUplod.setVisibility(View.VISIBLE);
        }

        //show upload file
        relativeFilesUploadBox.setVisibility(View.VISIBLE);


    }

    private void getDetailsProperty(boolean isProgress) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showSnkbar(getString(R.string.no_internet));
            return;
        }

        //token,username,qb_id
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));

        showProgressBar(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<DetailsTypeRespons> observable;
        observable = userService.getDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<DetailsTypeRespons>() {
            @Override
            public void onSuccess(DetailsTypeRespons response) {
                showProgressBar(false);
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    mCommonDetailsData = response.getData();
                    if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY)) {
                        mScreenName = "property";
                        //property item details code
                        setDataOfProperty();
                    } else if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_RENTAL)) {
                        //Rental Item LikesDetails code
                        mScreenName = "rental";
                        setDataOfRental();
                    } else if (mStringScreen.equalsIgnoreCase(Constants.SCREEN_SELCTION_NAME.SELECTE_SERVICE)) {
                        //Service Item LikesDetails
                        mScreenName = "service";
                        dataOfService();
                    }

                    //get call for status check
                    //check status for chat
                    checkChannelStatus(false);
                }

                //show message button view
                showMessageButtonBox();
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);

            }
        });

    }


    //data of Property
    private void setDataOfProperty() {
        //linearLayoutNeededDoc.setVisibility(View.GONE);
        linearLayoutOfRentalData.setVisibility(View.GONE);
        linearRadioGroup.setVisibility(View.VISIBLE);
        linearCheckedView.setVisibility(View.GONE);
        relativeLeaseTermsTypeBox.setVisibility(View.GONE);
        relativeLayoutOfAvailableDateBox.setVisibility(View.GONE);

        relativeLayoutPropertyPerforeclosure.setVisibility(View.VISIBLE);
        rilativeaLayoutPropety.setVisibility(View.VISIBLE);
        //relativeSellerTypeBox.setVisibility(View.VISIBLE);
        relativeLayoutOfPriceBox.setVisibility(View.VISIBLE);

        //hide service
        relativeLayoutServicePrice.setVisibility(View.GONE);
        relativeLayoutServiceLicensed.setVisibility(View.GONE);
        relativeLayoutServiceType.setVisibility(View.GONE);

        //titleAddress.setText("Address");

        if (mCommonDetailsData.getHouse() != null && !mCommonDetailsData.getHouse().equalsIgnoreCase("")) {
            textViewProductTitle.setVisibility(View.VISIBLE);
            textViewProductTitle.setText(mCommonDetailsData.getHouse());
        } else {
            textViewProductTitle.setVisibility(View.GONE);
        }

        if (mCommonDetailsData.getAddress() != null && !mCommonDetailsData.getAddress().equalsIgnoreCase("")) {
            textViewPropertyDeatilsAddress.setText(String.valueOf(mCommonDetailsData.getAddress()));
        }

        if (mCommonDetailsData.getState() != null && !mCommonDetailsData.getState().equalsIgnoreCase("")) {
            textViewStateValues.setText(String.valueOf(mCommonDetailsData.getState()));
        }

        if (mCommonDetailsData.getCity() != null && !mCommonDetailsData.getCity().equalsIgnoreCase("")) {
            textViewCityValues.setText(String.valueOf(mCommonDetailsData.getCity()));
        }

        if (mCommonDetailsData.getArea() != null && !mCommonDetailsData.getArea().equalsIgnoreCase("")) {
            textViewAreaValues.setText(String.valueOf(mCommonDetailsData.getArea() + " " + mCommonDetailsData.getAreaMeasure()));
        }


        if (mCommonDetailsData.getPropertyType() != null && !mCommonDetailsData.getPropertyType().equalsIgnoreCase("")) {
            textViewPropertyDetailsType.setText(String.valueOf(mCommonDetailsData.getPropertyType()));
        }

        if (mCommonDetailsData.getAvailableDate() != null && !mCommonDetailsData.getAvailableDate().equalsIgnoreCase("")) {
            textViewPropertyDetailsDateTime.setText(String.valueOf(mCommonDetailsData.getAvailableDate()));
        }

        if (mCommonDetailsData.getBedNos() != null && !mCommonDetailsData.getBedNos().equalsIgnoreCase("")) {
            textViewPropertyDetailsBedsNo.setText(String.valueOf(mCommonDetailsData.getBedNos()));
        }

        if (mCommonDetailsData.getBathNos() != null && !mCommonDetailsData.getBathNos().equalsIgnoreCase("")) {
            textViewPropertyDetailsBathNo.setText(String.valueOf(mCommonDetailsData.getBathNos()));
        }

        if (mCommonDetailsData.getRentAmount() != null && !mCommonDetailsData.getRentAmount().equalsIgnoreCase("")) {
            textViewPropertyDetailsRent.setText("$ " + currencyFormat(mCommonDetailsData.getRentAmount()));
        }

        if (mCommonDetailsData.getDescription() != null && !mCommonDetailsData.getDescription().equalsIgnoreCase("")) {
            textViewPropertyDetailsDescription.setText(String.valueOf(mCommonDetailsData.getDescription()));
        }

        if (mCommonDetailsData.getPropertyType() != null && !mCommonDetailsData.getPropertyType().equalsIgnoreCase("")) {
            textViewPropetyTypeValues.setText(String.valueOf(mCommonDetailsData.getPropertyType()));
        }

        /*if (mCommonDetailsData.getSallerType() != null && !mCommonDetailsData.getSallerType().equalsIgnoreCase("")) {
            textViewSellerTypeValues.setText(String.valueOf(mCommonDetailsData.getSallerType()));
        }*/

        if (mCommonDetailsData.getNdrs_number() != null && !mCommonDetailsData.getNdrs_number().equalsIgnoreCase("")) {
            relativeLayoutOfNDRS.setVisibility(View.VISIBLE);
            textViewNDRSValues.setText(mCommonDetailsData.getNdrs_number());
        } else {
            relativeLayoutOfNDRS.setVisibility(View.GONE);
        }


        if (mCommonDetailsData.getPropertyListed().equalsIgnoreCase("0")) {
            radioListedButton = groupRadioListed.findViewById(R.id.radioListedNo);
            radioListedButton.setChecked(true);
        } else {
            radioListedButton = groupRadioListed.findViewById(R.id.radioListedYes);
            radioListedButton.setChecked(true);
        }

        if (mCommonDetailsData.getPropertyVacant().equalsIgnoreCase("0")) {
            radioVacatedButton = radioVacantGroup.findViewById(R.id.radioVacantNo);
            radioVacatedButton.setChecked(true);
        } else {
            radioVacatedButton = radioVacantGroup.findViewById(R.id.radioVacantYes);
            radioVacatedButton.setChecked(true);
        }

        if (mCommonDetailsData.getPreForeclosure().equalsIgnoreCase("0")) {
            textViewPropertyPerforeclosureValues.setText("No");
        } else {
            textViewPropertyPerforeclosureValues.setText("Yes");
        }

        if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
            //reversed option
            int lastIndex = mCommonDetailsData.getImages().size() - 1;
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getImages().get(0).getImageUrl(), imageMainPropertyHeader, options);
        } else {
            imageMainPropertyHeader.setImageResource(R.drawable.no_image_icon);
        }

        textViewUserName.setText(mCommonDetailsData.getData().getFullName());
        if (mCommonDetailsData.getData().getProfilePic() != null && !mCommonDetailsData.getData().getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getData().getProfilePic(), imageViewUserProfile, options);
        } else {
            imageViewUserProfile.setImageResource(R.mipmap.user);
        }

        /*if (mCommonDetailsData.getUploadDoc() != null && !mCommonDetailsData.getUploadDoc().equalsIgnoreCase("")) {
            textViewDetailsDocuments.setText(mCommonDetailsData.getUploadDoc());
        } else {
            relativeLayoutDocumentUrl.setVisibility(View.GONE);
        }*/


//        if (mCommonDetailsData.getData().getRateAverage() != null && !mCommonDetailsData.getData().getRateAverage().equalsIgnoreCase("")) {
//            float rate = Float.parseFloat(mCommonDetailsData.getData().getRateAverage());
//            rotationratingbar_main.setRating(rate);
//        } else {
//            rotationratingbar_main.setRating(0);
//        }
    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }

    //data of Rental
    private void setDataOfRental() {
        rilativeaLayoutPropety.setVisibility(View.GONE);
        relativeLayoutOfPriceBox.setVisibility(View.GONE);
        linearRadioGroup.setVisibility(View.GONE);
        relativeLayoutPropertyPerforeclosure.setVisibility(View.GONE);
        relativeLayoutOfNDRS.setVisibility(View.GONE);
        linearCheckedView.setVisibility(View.VISIBLE);
        linearLayoutOfRentalData.setVisibility(View.VISIBLE);
        //relativeSellerTypeBox.setVisibility(View.GONE);
        relativeLeaseTermsTypeBox.setVisibility(View.VISIBLE);
        relativeLayoutOfAvailableDateBox.setVisibility(View.VISIBLE);
        //linearLayoutNeededDoc.setVisibility(View.GONE);

        //hide service
        relativeLayoutServicePrice.setVisibility(View.GONE);
        relativeLayoutServiceLicensed.setVisibility(View.GONE);
        relativeLayoutServiceType.setVisibility(View.GONE);

        //titleAddress.setText("Address");
        if (mCommonDetailsData.getHouse() != null && !mCommonDetailsData.getHouse().equalsIgnoreCase("")) {
            textViewProductTitle.setVisibility(View.VISIBLE);
            textViewProductTitle.setText(mCommonDetailsData.getHouse());
        } else {
            textViewProductTitle.setVisibility(View.GONE);
        }


        if (mCommonDetailsData.getAddress() != null && !mCommonDetailsData.getAddress().equalsIgnoreCase("")) {
            textViewPropertyDeatilsAddress.setText(String.valueOf(mCommonDetailsData.getAddress()));
        }

        if (mCommonDetailsData.getState() != null && !mCommonDetailsData.getState().equalsIgnoreCase("")) {
            textViewStateValues.setText(String.valueOf(mCommonDetailsData.getState()));
        }

        if (mCommonDetailsData.getCity() != null && !mCommonDetailsData.getCity().equalsIgnoreCase("")) {
            textViewCityValues.setText(String.valueOf(mCommonDetailsData.getCity()));
        }

        if (mCommonDetailsData.getArea() != null && !mCommonDetailsData.getArea().equalsIgnoreCase("")) {
            textViewAreaValues.setText(String.valueOf(mCommonDetailsData.getArea() + " Sq. Feet"));
        }

        if (mCommonDetailsData.getPropertyType() != null && !mCommonDetailsData.getPropertyType().equalsIgnoreCase("")) {
            textViewPropertyDetailsType.setText(String.valueOf(mCommonDetailsData.getBuildingType()));
        }

        if (mCommonDetailsData.getAvailableDate() != null && !mCommonDetailsData.getAvailableDate().equalsIgnoreCase("")) {
            textViewPropertyDetailsDateTime.setText(String.valueOf(mCommonDetailsData.getAvailableDate()));
        }

        if (mCommonDetailsData.getBedNos() != null && !mCommonDetailsData.getBedNos().equalsIgnoreCase("")) {
            textViewPropertyDetailsBedsNo.setText(String.valueOf(mCommonDetailsData.getBedNos()));
        }

        if (mCommonDetailsData.getBathNos() != null && !mCommonDetailsData.getBathNos().equalsIgnoreCase("")) {
            textViewPropertyDetailsBathNo.setText(String.valueOf(mCommonDetailsData.getBathNos()));
        }

        if (mCommonDetailsData.getRentAmount() != null && !mCommonDetailsData.getRentAmount().equalsIgnoreCase("")) {
            textViewRentalRateValues.setText("$ " + currencyFormat(mCommonDetailsData.getRentAmount()));
        }

        if (mCommonDetailsData.getSequrityAmount() != null && !mCommonDetailsData.getSequrityAmount().equalsIgnoreCase("")) {
            textViewRentalSecurityVaule.setText("$ " + currencyFormat(mCommonDetailsData.getSequrityAmount()));
        }

        if (mCommonDetailsData.getOtherFees() != null && !mCommonDetailsData.getOtherFees().equalsIgnoreCase("")) {
            textViewRentalOtherFeesVaule.setText("$ " + currencyFormat(mCommonDetailsData.getOtherFees()));
        }

        if (mCommonDetailsData.getDescription() != null && !mCommonDetailsData.getDescription().equalsIgnoreCase("")) {
            textViewPropertyDetailsDescription.setText(String.valueOf(mCommonDetailsData.getDescription()));
        }


        if (mCommonDetailsData.getPropertyType() != null && !mCommonDetailsData.getPropertyType().equalsIgnoreCase("")) {
            textViewPropetyTypeValues.setText(String.valueOf(mCommonDetailsData.getPropertyType()));
        }

        if (mCommonDetailsData.getLeaseTerm() != null && !mCommonDetailsData.getLeaseTerm().equalsIgnoreCase("")) {
            textViewLeaseTermsValues.setText(String.valueOf(mCommonDetailsData.getLeaseTerm()));
        }


        if (mCommonDetailsData.getCreditCheck().equalsIgnoreCase("0")) {
            radioCreditCheckButton = radioCreditCheckGroup.findViewById(R.id.radioCreditCheckYes);
            radioCreditCheckButton.setChecked(true);
        } else {
            radioCreditCheckButton = radioCreditCheckGroup.findViewById(R.id.radioCreditCheckNo);
            radioCreditCheckButton.setChecked(true);
        }


        if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getImages().get(0).getImageUrl(), imageMainPropertyHeader, options);
        } else {
            imageMainPropertyHeader.setImageResource(R.drawable.no_image_icon);
        }

       /* if (mCommonDetailsData.getUploadDoc() != null && !mCommonDetailsData.getUploadDoc().equalsIgnoreCase("")) {
            textViewDetailsDocuments.setText(mCommonDetailsData.getUploadDoc());
        } else {
            relativeLayoutDocumentUrl.setVisibility(View.GONE);
        }*/

        /*if (mCommonDetailsData.getDocNeed() != null && !mCommonDetailsData.getDocNeed().equalsIgnoreCase("")) {
            mDocNeededList = new ArrayList<String>(Arrays.asList(mCommonDetailsData.getDocNeed().split(",")));
            //LogUtils.LOGD(TAG, "Documents Array -->" + mDocNeededList.size());


            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_w2))) {
                checkBoxW2.setChecked(true);
            }

            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_driver_license))) {
                checkBoxDriverLicense.setChecked(true);
            }

            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_paystubs))) {
                checkBoxPaystubs.setChecked(true);
            }

            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_bank_statements))) {
                checkBoxBankStatements.setChecked(true);
            }

            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_other))) {
                checkBoxOther.setChecked(true);
            }

            if (mDocNeededList.contains(getResources().getString(R.string.string_doc_tax_return))) {
                checkBoxTaxReturn.setChecked(true);
            }
        } else {
            linearLayoutNeededDoc.setVisibility(View.GONE);
        }*/


        textViewUserName.setText(mCommonDetailsData.getData().getFullName());
        if (mCommonDetailsData.getData().getProfilePic() != null && !mCommonDetailsData.getData().getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getData().getProfilePic(), imageViewUserProfile, options);
        } else {
            imageViewUserProfile.setImageResource(R.mipmap.user);
        }

//        if (mCommonDetailsData.getData().getRateAverage() != null && mCommonDetailsData.getData().getRateAverage()!=0) {
//            float rate = Float.parseFloat(mCommonDetailsData.getData().getRateAverage());
//            rotationratingbar_main.setRating(rate);
//        }

    }

    private void dataOfService() {
        //hide property and rental
        rilativeaLayoutPropety.setVisibility(View.GONE);
        relativeLayoutOfPriceBox.setVisibility(View.GONE);
        linearRadioGroup.setVisibility(View.GONE);
        linearCheckedView.setVisibility(View.GONE);
        relativeLayoutPropertyPerforeclosure.setVisibility(View.GONE);
        linearLayoutOfRentalData.setVisibility(View.GONE);
        // relativeSellerTypeBox.setVisibility(View.GONE);
        relativeLeaseTermsTypeBox.setVisibility(View.GONE);
        relativeLayoutOfNDRS.setVisibility(View.GONE);
        relativeLayoutOfAvailableDateBox.setVisibility(View.GONE);
        //linearLayoutNeededDoc.setVisibility(View.GONE);
        relativeLayoutOfArea.setVisibility(View.GONE);
        linearlayoutOfBedsBathBox.setVisibility(View.GONE);
        relativeLayoutOfApartmentType.setVisibility(View.GONE);
        //View Service

        relativeLayoutServicePrice.setVisibility(View.VISIBLE);
        relativeLayoutServiceLicensed.setVisibility(View.VISIBLE);
        relativeLayoutServiceType.setVisibility(View.VISIBLE);

        //titleAddress.setText("Title");
        textViewPropertyDeatilsAddress.setText(mCommonDetailsData.getTitle());
        textViewStateValues.setText(mCommonDetailsData.getState());
        textViewCityValues.setText(mCommonDetailsData.getCity());

        textViewPropertyDetailsDescription.setText(mCommonDetailsData.getDescription());
        textViewServiceTypeValues.setText(mCommonDetailsData.getServiceType());
        textViewServicePriceValues.setText(mCommonDetailsData.getRentAmount() + " " + mCommonDetailsData.getPriceOn());

        if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getImages().get(0).getImageUrl(), imageMainPropertyHeader, options);
        } else {
            imageMainPropertyHeader.setImageResource(R.drawable.no_image_icon);
        }

        /*if (mCommonDetailsData.getUploadDoc() != null && !mCommonDetailsData.getUploadDoc().equalsIgnoreCase("")) {
            textViewDetailsDocuments.setText(mCommonDetailsData.getUploadDoc());
        } else {
            relativeLayoutDocumentUrl.setVisibility(View.GONE);
        }
*/
        if (mCommonDetailsData.getPropertyListed().equalsIgnoreCase("0")) {
            textViewServiceListedValues.setText("No");
        } else {
            textViewServiceListedValues.setText("Yes");
        }

        textViewUserName.setText(mCommonDetailsData.getData().getFullName());
        if (mCommonDetailsData.getData().getProfilePic() != null && !mCommonDetailsData.getData().getProfilePic().equalsIgnoreCase("")) {
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .bitmapConfig(Bitmap.Config.ARGB_4444)
                    .build();
            mImageLoader.displayImage(mCommonDetailsData.getData().getProfilePic(), imageViewUserProfile, options);
        } else {
            imageViewUserProfile.setImageResource(R.mipmap.user);
        }

//        if (mCommonDetailsData.getData().getRateAverage() != null && !mCommonDetailsData.getData().getRateAverage().equalsIgnoreCase("")) {
//            float rate = Float.parseFloat(mCommonDetailsData.getData().getRateAverage());
//            rotationratingbar_main.setRating(rate);
//        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FROME_TO.equalsIgnoreCase("")) {
            finish();
        } else {
            startActivity(new Intent(CommonDetailsActivity.this, MainActivity.class));
            finishAffinity();
        }
    }

    private void openMultipleImage(CommonData mCommonDetailsData) {

        GenericDraweeHierarchyBuilder hierarchyBuilder = GenericDraweeHierarchyBuilder.newInstance(getResources())
                .setProgressBarImage(R.drawable.progressbardrawable);

        List<CommonData.Image> images = mCommonDetailsData.getImages();
        //items show reverse form
        Collections.reverse(images);
        new ImageViewer.Builder<>(this, images)
                .setFormatter(new ImageViewer.Formatter<CommonData.Image>() {
                    @Override
                    public String format(CommonData.Image customImage) {
                        return customImage.getImageUrl();
                    }
                })
                .setCustomDraweeHierarchyBuilder(hierarchyBuilder).show();

    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    @OnClick({R.id.textViewUserName, R.id.imageViewUserProfile, R.id.imageViewBack, R.id.imageViewShareIcon, R.id.imageMainPropertyHeader, R.id.iconEnvalop, R.id.iconFileUpload, R.id.relativeFilesUploadBox, R.id.textViewOfMessagesBox, R.id.relativeContractBox})
    void viewonClickEvents(View view) {
        switch (view.getId()) {
            case R.id.imageViewBack:
                if (FROME_TO.equalsIgnoreCase("")) {
                    finish();
                } else {
                    startActivity(new Intent(CommonDetailsActivity.this, MainActivity.class));
                    finishAffinity();
                }
                break;
            case R.id.imageViewShareIcon:

                shareData(mCommonDetailsData);
                //get share link of dynamic link
                //mProgressDialog = new ProgressDialog(this);
                //generateLink(mCommonDetailsData);
                break;
           /* case R.id.textViewDetailsDocuments:
                startActivity(PdfViewer.getIntentActivity(mBaseAppCompatActivity, mCommonDetailsData.getUploadDoc(), Constants.SELECTION_HEADER_TITLE.FILE_VIEWER));
                break;*/
            case R.id.imageMainPropertyHeader:
                if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
                    openMultipleImage(mCommonDetailsData);
                }
                break;
            case R.id.iconEnvalop:
                //chat init
                mIsDistinct = PreferenceUtils.getGroupChannelDistinct();
                createGroupChannel(mSelectedIds, mIsDistinct);
                break;
            case R.id.textViewOfMessagesBox:
                //chat init
                mIsDistinct = PreferenceUtils.getGroupChannelDistinct();
                createGroupChannel(mSelectedIds, mIsDistinct);
                break;
            case R.id.textViewUserName:
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivity(new Intent(UserProfileDetails.getIntentActivity(mBaseAppCompatActivity, mCommonDetailsData.getData())));
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
                break;
            case R.id.imageViewUserProfile:
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivity(new Intent(UserProfileDetails.getIntentActivity(mBaseAppCompatActivity, mCommonDetailsData.getData())));
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
                break;
            case R.id.iconFileUpload:
                //file upload start activity
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivityIfNeeded(new Intent(CommonDetailsActivity.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", mScreenName)
                            .putExtra("post_id", mCommonId)
                            .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }

                break;
            case R.id.relativeFilesUploadBox:
                //file upload start activity
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivityIfNeeded(new Intent(CommonDetailsActivity.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", mScreenName)
                            .putExtra("post_id", mCommonId)
                            .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
                break;

            case R.id.relativeContractBox:
                //open new activity
                startActivity(new Intent(CommonDetailsActivity.this, RequestContractActivity.class)
                        .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName));
                overridePendingTransition(R.anim.slide_up, R.anim.stay);
                break;
        }
    }

    //DetailsData
    private void shareData(CommonData mCommonDetailsData) {
        try {
            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            String houseName = "";
            if (mCommonDetailsData.getHouse() != null && !mCommonDetailsData.getHouse().equalsIgnoreCase("")) {
                houseName = mCommonDetailsData.getHouse();

            } else {
                houseName = mCommonDetailsData.getTitle();
            }

            //check image size
            String images = "";
            if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
                images = mCommonDetailsData.getImages().get(0).getImageUrl();
            } else {
                images = "";
            }

            String price = "0";

            if (mCommonDetailsData.getPriceOn() != null && !mCommonDetailsData.getPriceOn().equalsIgnoreCase("")) {
                price = mCommonDetailsData.getPriceOn();
            } else {
                price = mCommonDetailsData.getRentAmount();
            }


            String message = "FlippBidd:Property\n" + houseName + "\nPrice:" + "$" + price + "\nAddress:" + mCommonDetailsData.getAddress() + "\n\n"
                    + images + "\nDownload FlippBidd @ www.flippbiddapp.com";

            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share to"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }

    /*chat create */

    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds  The users to be members of the new channel.
     * @param distinct Whether the channel is unique for the selected members.
     *                 If you attempt to create another Distinct channel with the same members,
     *                 the existing channel instance will be returned.
     */
    private void createGroupChannel(List<String> userIds, boolean distinct) {

        if (channelStatus.equalsIgnoreCase("0")) {

            if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
                //String chatURL = BaseApplication.getInstance().getLoginID() + "_" + mCommonDetailsData.getCommonId() + "_url";

                String chatName, chatAddress, chatCoverUrl;

                if (mCommonDetailsData.getHouse() != null && !mCommonDetailsData.getHouse().equalsIgnoreCase("")) {
                    chatName = mCommonDetailsData.getHouse();

                } else {
                    chatName = mCommonDetailsData.getTitle();
                }
                /*address**ownerID**addedby**propertyID**channelMainID##loginid*/
                chatAddress = getAddress();

                if (mCommonDetailsData.getImages() != null && mCommonDetailsData.getImages().size() != 0) {
                    chatCoverUrl = mCommonDetailsData.getImages().get(0).getImageUrl();
                } else {
                    chatCoverUrl = "";
                }

                //admin list
                List<String> moAdmin = new ArrayList<>();
                moAdmin.add(userIds.get(1));

                GroupChannelParams params = new GroupChannelParams()
                        .setPublic(false)
                        .setEphemeral(false)
                        .setDistinct(false)
                        .addUserIds(userIds)
                        .setOperatorUserIds(moAdmin)// Or .setOperators(List<User> operators)
                        .setName(chatName)
                        .setCoverUrl(chatCoverUrl)
                        .setData(chatAddress)//.setCoverImage(FILE)            // Or .setCoverUrl(COVER_URL)
                        .setCustomType(mScreenName);
                //setChannelUrl(chatURL)  // In a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.
                GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
                    @Override
                    public void onResult(GroupChannel groupChannel, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            if (e.getMessage().equalsIgnoreCase("\"channel_url\" violates unique constraint.")) {
//                                enterGroupChannel(chatURL);
                            }
                            return;
                        }

                        createChannel(true, groupChannel.getUrl());
                    }
                });

            }
        } else {
            //get url form iur server
            GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }
                    enterGroupChannel(groupChannel.getUrl(), channelMainId, channelPropertyId, propertyOwenrEmail);
                }
            });
        }

    }

    /**
     * Enters a Group Channel with a URL.
     *
     * @param extraChannelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String extraChannelUrl, String channelMainId, String channelPropertyId, String ownerEmail) {
        Intent mainIntent = new Intent(CommonDetailsActivity.this, GroupChatActivity.class);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_MAIN_ID, channelMainId);
        mainIntent.putExtra(EXTRA_PROPERTY_ID, channelPropertyId);
        mainIntent.putExtra(EXTRA_OWNER_EMAIL, ownerEmail);
        startActivity(mainIntent);
    }


    /*server base chat manage*/
    private void checkChannelStatus(boolean isProgress) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showSnkbar(getString(R.string.no_internet));
            return;
        }
        //owner_id, buyer_id, property_id, type
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getData().getEmailAddress()));
        linkedHashMap.put("buyer_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getQBLoginID()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));

        showProgressBar(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ChannelCreatedResponse> observable;
        observable = userService.channelStatusChecked(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ChannelCreatedResponse>() {
            @Override
            public void onSuccess(ChannelCreatedResponse response) {
                showProgressBar(false);
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");

                if (response.getSuccess()) {
                    channelStatus = "1";
                    channelUrl = response.getData().getChannelId();
                    channelMainId = response.getData().getChannelMainId();
                    channelPropertyId = response.getData().getPropertyId();
                    propertyOwenrEmail = response.getData().getOwnerId();
                } else {
                    //create new channel with user id
                    channelStatus = "0";
                    channelUrl = "";
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }


    private void createChannel(boolean isProgress, String channelID) {
        //token, sender_id, receiver_id, sender_qb_id, receiver_qb_id, dialog_id, dialog_type
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showSnkbar(getString(R.string.no_internet));
            return;
        }

        //buyer_id, channel_id, owner_id, property_id, type, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("channel_id", RequestBody.create(MediaType.parse("multipart/form-data"), channelID));
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getData().getEmailAddress()));
        linkedHashMap.put("buyer_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getQBLoginID()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), "2"));

        showProgressBar(isProgress);

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<ChannelCreatedResponse> observable;
        observable = userService.createChannel(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<ChannelCreatedResponse>() {
            @Override
            public void onSuccess(ChannelCreatedResponse response) {
                showProgressBar(false);
                LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //get channel url
                    channelMainId = response.getData().getChannelMainId();
                    channelPropertyId = response.getData().getPropertyId();
                    propertyOwenrEmail = response.getData().getOwnerId();
                    enterGroupChannel(channelID, channelMainId, channelPropertyId, propertyOwenrEmail);
                } else {
                    //create new channel with user id
                    Log.e(TAG, "Created else part");
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);

            }
        });
    }

    public String getAddress() {
        /*address**ownerID**addedby**propertyID**channelMainID*/
        return mCommonDetailsData.getAddress() + "##" + mCommonDetailsData.getData().getEmailAddress() + "##"
                + mCommonDetailsData.getData().getFullName() + "##" + mCommonDetailsData.getCommonId() + "##" + channelMainId + "";
    }

    public String getAddress2() {
        /*address**ownerID**addedby**propertyID**channelMainID*/
        return mCommonDetailsData.getAddress() + "##" + mCommonDetailsData.getData().getEmailAddress() + "##"
                + mCommonDetailsData.getData().getFullName() + "##" + mCommonDetailsData.getCommonId() + "##" + channelMainId + "##" + mCommonDetailsData.getData().getLoginId();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mCommonDetailsData != null) {
            checkChannelStatus(true);
        }
    }


}
