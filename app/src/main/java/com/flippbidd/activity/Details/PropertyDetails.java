package com.flippbidd.activity.Details;

import static com.flippbidd.Others.Constants.FREE_VERSION;
import static com.flippbidd.Others.Constants.PRO_VERSION;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.flippbidd.Adapter.PhotosAdapter;
import com.flippbidd.Adapter.PropertyBiddListAdapter;
import com.flippbidd.Bottoms.AdminMenuBottomSheetDialogFragment;
import com.flippbidd.Bottoms.CallDialogFragment;
import com.flippbidd.Bottoms.MeetingScheduleSheet;
import com.flippbidd.Bottoms.NewMenuBottomSheetDialogFragment;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.MainActivity;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Model.Response.OtherUserDetailsResponse;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.AddressActivity;
import com.flippbidd.activity.Contract.RequestContractActivity;
import com.flippbidd.activity.ProfileEditActivity;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.activity.UploadFiles_Activity_List;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.activity.reportcontent.ReportContentActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.sendbirdSdk.groupchannel.GroupInfoActivity;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import me.relex.circleindicator.CircleIndicator;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class PropertyDetails extends BaseActivity implements View.OnClickListener, PropertyBiddListAdapter.onItemClickLisnear {

    private static final String TAG = PropertyDetails.class.getSimpleName();

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_MAIN_ID = "GROUP_CHANNEL_MAIN_ID";
    public static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    public static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";

    Activity mBaseAppCompatActivity;
    Disposable disposable;


    private List<String> mSelectedIds;
    private boolean mIsDistinct = true;
    ImageLoader mImageLoader;
    private String mCommonId = "", mScreenName = "property", mLoginId = "";
    CommonData mCommonDetailsData;
    String channelStatus = "";
    String channelUrl = "";
    String channelMainId = "";
    String channelPropertyId = "";
    String propertyOwenrEmail = "";

    String FROME_TO = "";
    private boolean appBarExpanded = true;
    private String callType = "", ownerEmail = "", requestEmail = "";
    OtherUserDetails mOtherUserDetails;
    UserDetails mUserDetails;

    private ImageView imageCallingView, imageCalling;
    private RelativeLayout relativeSendEmail, relativecall;

    private int remainingCounts = 5;
    private String userPlanName = "";

    //new feature
    RelativeLayout relativePreClosureInformationBox;
    //end new feature

    public static Intent getIntentActivity(Context mContext, String mId, String mLoginId, String mScreenType, boolean isTimeExpired) {
        Intent mIntent = new Intent(mContext, PropertyDetails.class);
        mIntent.putExtra(Constants.EXTRA.DATA, mId);
        mIntent.putExtra(Constants.EXTRA.LOGINID, mLoginId);
        mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mScreenType);
        mIntent.putExtra(Constants.EXTRA.EXPIRED_STATUS, isTimeExpired);
        mIntent.putExtra(Constants.EXTRA.FROM_TO, "");
        return mIntent;
    }

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
        setContentView(R.layout.activity_new_design_property_details_layout);
        //get intent data
        this.mBaseAppCompatActivity = PropertyDetails.this;
        getUserDetails();
        mUserDetails = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
        mSelectedIds = new ArrayList<>();
        mImageLoader = ImageLoader.getInstance();
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
//            mStringScreen = mBundle.getString(Constants.EXTRA.SCREEN_TYPE);
            mCommonId = mBundle.getString(Constants.EXTRA.DATA);
            mLoginId = mBundle.getString(Constants.EXTRA.LOGINID);
//            isExpiredStatus = mBundle.getBoolean(Constants.EXTRA.EXPIRED_STATUS);
            //check key
            try {
                if (getIntent().hasExtra(Constants.EXTRA.FROM_TO)) {
                    FROME_TO = mBundle.getString(Constants.EXTRA.FROM_TO);
                } else {
                    FROME_TO = "";
                }

            } catch (Exception e) {
            }

        }
        initview();

        clickEvent();

        //get remaning counts
        getMettingCountsReamining();
    }

    /*get meeting counts remaning*/
    private void getMettingCountsReamining() {
        LinkedHashMap<String, RequestBody> remaningRequest = new LinkedHashMap<String, RequestBody>();
        remaningRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        remaningRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.getMeetingCountsRemaning(remaningRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                /*{"success":true,"text":"Inapp Subscription Details","data":{"plan":"FREE VERSION","start_date":"","expire_date":"","this_month_remaining_meeting_count":0}}*/
                JsonObject mJsonObject = response.getAsJsonObject();
                LogUtils.LOGD("TAG", "Response = [" + mJsonObject.toString() + "]");
                if (mJsonObject.get("success").getAsBoolean()) {
                    //success
                    if (mJsonObject.get("data").getAsJsonObject().get("plan").getAsString().equalsIgnoreCase(PRO_VERSION)) {
                        PreferenceUtils.setPlanVersionStatus(true);

                        //update plan status view
                        Intent intent = new Intent(Constants.UPDATE_PLAN_STATUS);
                        intent.putExtra(Constants.PLAN_STATUS, true);
                        LocalBroadcastManager.getInstance(PropertyDetails.this).sendBroadcast(intent);
                    } else {
                        PreferenceUtils.setPlanVersionStatus(false);
                        //update plan status view
                        Intent intent = new Intent(Constants.UPDATE_PLAN_STATUS);
                        intent.putExtra(Constants.PLAN_STATUS, true);
                        LocalBroadcastManager.getInstance(PropertyDetails.this).sendBroadcast(intent);
                    }
                    userPlanName = mJsonObject.get("data").getAsJsonObject().get("plan").getAsString();
                    remainingCounts = mJsonObject.get("data").getAsJsonObject().get("this_month_remaining_meeting_count").getAsInt();
                } else {
                    //error message
                    openAlertDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }
    /**/


    private void showDiag() {
        final View dialogViewDashBord = View.inflate(this, R.layout.details_intro_dialog, null);

        final Dialog dialogDashbord = new Dialog(this, R.style.MyAlertDialogStyle);
        dialogDashbord.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDashbord.setContentView(dialogViewDashBord);

        CustomTextView txtTitle = (CustomTextView) dialogDashbord.findViewById(R.id.txtTitle);
        txtTitle.setText(getResources().getString(R.string.intro_10));
        CustomTextView txtGOTIT = (CustomTextView) dialogDashbord.findViewById(R.id.txtGOTIT);
        RelativeLayout relativeThreeDotList = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeThreeDotList);
        relativeThreeDotList.setVisibility(View.VISIBLE);
        RelativeLayout relativeThumbList = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeThumbList);

        UserPreference.getInstance(PropertyDetails.this).setIsThreeDotIntro(false);

        txtGOTIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPreference.getInstance(PropertyDetails.this).isThumbIntro()) {
                    UserPreference.getInstance(PropertyDetails.this).setIsThumbIntro(false);
                    relativeThreeDotList.setVisibility(View.INVISIBLE);
                    relativeThumbList.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_9));
                } else {
                    revealShow(dialogViewDashBord, false, dialogDashbord);
                    if (UserPreference.getInstance(PropertyDetails.this).isBiddIntro()) {
                        showIntroBidd();
                    }
                }
            }
        });


        dialogDashbord.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogViewDashBord, true, null);
            }
        });

        dialogDashbord.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogViewDashBord, false, dialogDashbord);
                    return true;
                }

                return false;
            }
        });


        dialogDashbord.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogDashbord.show();
    }

    private void revealShow(View dialogView, boolean b, final Dialog dialog) {

        final View view = dialogView.findViewById(R.id.dialog);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (findViewById(R.id.imageMoreOption).getX() + (findViewById(R.id.imageMoreOption).getWidth() / 2));
        int cy = (int) (findViewById(R.id.imageMoreOption).getY()) + findViewById(R.id.imageMoreOption).getHeight() + 56;


        if (b) {
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(700);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);

                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

    private void showIntroBidd() {


        final View dialogViewBiddView = View.inflate(this, R.layout.intro_bidd_dialog, null);

        final Dialog dialogBidd = new Dialog(this, R.style.MyAlertDialogStyle);
        dialogBidd.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogBidd.setContentView(dialogViewBiddView);

        CustomTextView txtTitle = (CustomTextView) dialogBidd.findViewById(R.id.txtTitle);
        txtTitle.setText(getResources().getString(R.string.intro_11));
        CustomTextView txtGOTIT = (CustomTextView) dialogBidd.findViewById(R.id.txtGOTIT);
        UserPreference.getInstance(PropertyDetails.this).setIsBiddIntro(false);

        RelativeLayout relativeBidd = (RelativeLayout) dialogBidd.findViewById(R.id.relativeBidd);
        RelativeLayout relativeData = (RelativeLayout) dialogBidd.findViewById(R.id.relativeData);
        RelativeLayout relativeContract = (RelativeLayout) dialogBidd.findViewById(R.id.relativeContract);
        RelativeLayout relativeMyCalendar = (RelativeLayout) dialogBidd.findViewById(R.id.relativeMyCalendar);


        txtGOTIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPreference.getInstance(PropertyDetails.this).isDataIntro()) {
                    //show data
                    relativeBidd.setVisibility(View.INVISIBLE);
                    relativeData.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_14));
                    UserPreference.getInstance(PropertyDetails.this).setIsDataIntro(false);
                } else if (UserPreference.getInstance(PropertyDetails.this).isContractIntro()) {
                    //contract
                    relativeData.setVisibility(View.INVISIBLE);
                    relativeContract.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_15));
                    UserPreference.getInstance(PropertyDetails.this).setIsContractIntro(false);
                } else if (UserPreference.getInstance(PropertyDetails.this).isMyCalendarIntro()) {
                    //My Calendar
                    relativeContract.setVisibility(View.INVISIBLE);
                    relativeMyCalendar.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_16));
                    UserPreference.getInstance(PropertyDetails.this).setIsMyCalendarIntro(false);
                } else {
                    revealShow(dialogViewBiddView, false, dialogBidd);
                }

            }
        });


        dialogBidd.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogViewBiddView, true, null);
            }
        });

        dialogBidd.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogViewBiddView, false, dialogBidd);
                    return true;
                }

                return false;
            }
        });


        dialogBidd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogBidd.show();
    }

    private void clickEvent() {

        findViewById(R.id.imageDBack).setOnClickListener(this::onClick);
        findViewById(R.id.imageShare).setOnClickListener(this::onClick);
        findViewById(R.id.imageContractBox).setOnClickListener(this::onClick);
//        findViewById(R.id.imageMessageBox).setOnClickListener(this::onClick);
        //    findViewById(R.id.relativeMessageBox1).setOnClickListener(this::onClick);
        findViewById(R.id.relativeSendEmail).setOnClickListener(this::onClick);
        findViewById(R.id.relativecall).setOnClickListener(this::onClick);
        findViewById(R.id.imageCallingView).setOnClickListener(this::onClick);
        findViewById(R.id.imageMoreOption).setOnClickListener(this::onClick);
        findViewById(R.id.imageFileUpload).setOnClickListener(this::onClick);
        findViewById(R.id.txtViewAllBidd).setOnClickListener(this::onClick);
//        findViewById(R.id.relativeRequestBidd).setOnClickListener(this::onClick);
        findViewById(R.id.relativeLayoutAdminBox).setOnClickListener(this::onClick);
        findViewById(R.id.imageDThumb).setOnClickListener(this::onClick);

        //bidd
        findViewById(R.id.relativePlaceBiddBox).setOnClickListener(this::onClick);

    }

    private void initview() {
        relativeSendEmail = findViewById(R.id.relativeSendEmail);
        relativecall = findViewById(R.id.relativecall);
        imageCallingView = findViewById(R.id.imageCallingView);

        relativePreClosureInformationBox = findViewById(R.id.relativePreClosureInformationBox);//new feature


        ((AppBarLayout) findViewById(R.id.app_bar_layout)).addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                //  Vertical offset == 0 indicates appBar is fully expanded.
                if (Math.abs(verticalOffset) > 200) {
                    appBarExpanded = false;
                    invalidateOptionsMenu();
                } else {
                    appBarExpanded = true;
                    invalidateOptionsMenu();
                }
            }
        });

        getDetailsProperty(true);
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
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));//1136
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), mScreenName));

        System.out.println("!!!token = " + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken());
        System.out.println("!!!loginId = " + UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId());
        System.out.println("!!!CommonId = " + mCommonId);
        System.out.println("!!!screenname = " + mScreenName);


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
                    System.out.println("!!!response.getData() = " + response.getData());
                    //set data of property
                    setDataProperty(mCommonDetailsData);
                    //check status for chat
                    checkChannelStatus(false);
                } else {
                    CommonDialogView.getInstance().showCommonDialog(mBaseAppCompatActivity, "",
                            response.getText()
                            , ""
                            , "Ok"
                            , false, true, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    finish();
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });

    }

    private void showMessageButtonBox() {
        //add user
        mSelectedIds.add(PreferenceUtils.getUserId());//current user id
        mSelectedIds.add(mCommonDetailsData.getData().getEmailAddress()); //other user id
        if (!BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
            findViewById(R.id.relativeRequestBidd).setVisibility(View.GONE);
            findViewById(R.id.imageFileUpload).setVisibility(View.VISIBLE);
            // findViewById(R.id.relativeMessageBox1).setVisibility(View.VISIBLE);
            findViewById(R.id.relativeSendEmail).setVisibility(View.VISIBLE);
            findViewById(R.id.relativecall).setVisibility(View.VISIBLE);
            findViewById(R.id.imageCallingView).setVisibility(View.VISIBLE);
//            findViewById(R.id.imageMessageBox).setVisibility(View.VISIBLE);
            findViewById(R.id.imageContractBox).setVisibility(View.VISIBLE);
            //bidd
            findViewById(R.id.relativeBiddFirstBox).setVisibility(View.VISIBLE);
            findViewById(R.id.relativePlaceBiddBox).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.relativeRequestBidd).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageFileUpload).setVisibility(View.VISIBLE);
            //   findViewById(R.id.relativeMessageBox1).setVisibility(View.INVISIBLE);
            findViewById(R.id.relativeSendEmail).setVisibility(View.GONE);
            findViewById(R.id.relativecall).setVisibility(View.GONE);
            findViewById(R.id.imageCallingView).setVisibility(View.GONE);
//            findViewById(R.id.imageMessageBox).setVisibility(View.INVISIBLE);
            findViewById(R.id.imageContractBox).setVisibility(View.GONE);
            //bidd
            findViewById(R.id.relativeBiddFirstBox).setVisibility(View.GONE);
            findViewById(R.id.relativePlaceBiddBox).setVisibility(View.GONE);
        }

    }

    private void setDataProperty(CommonData mCommonDetailsData) {
        //show message button view
        showMessageButtonBox();
        if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
            mLoginId = mCommonDetailsData.getLoginId();

            setViewPager(mCommonDetailsData);

            if (!mCommonDetailsData.getHouse_name().isEmpty()) {
                ((CustomTextView) findViewById(R.id.txtPropertyTitle)).setText(mCommonDetailsData.getHouse_name());
            } else {
                ((CustomTextView) findViewById(R.id.txtPropertyTitle)).setText(mCommonDetailsData.getHouse());
            }
            if (PreferenceUtils.getIsPremiumUser() == 1 ) {// is premium user is true
                ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setTextColor(getResources().getColor(R.color.grey_font));
                ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setText(mCommonDetailsData.getAddress1());
                ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.GONE);
                ((CustomTextView) findViewById(R.id.txtPropertyLocation2)).setText(mCommonDetailsData.getAddress2());
                ((ImageView) findViewById(R.id.img_lock_contract)).setVisibility(View.GONE);
                ((ImageView) findViewById(R.id.img_lock_data)).setVisibility(View.GONE);
            } else {
                if (!BaseApplication.getInstance().getLoginID().equals(mCommonDetailsData.getData().getLoginId())) {
                    ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.VISIBLE);
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setText("****");
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setTextSize(13);
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation2)).setText(mCommonDetailsData.getAddress2());
                    ((ImageView) findViewById(R.id.img_lock_contract)).setVisibility(View.VISIBLE);
                    ((ImageView) findViewById(R.id.img_lock_data)).setVisibility(View.VISIBLE);
                } else {
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setTextColor(getResources().getColor(R.color.grey_font));
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setText(mCommonDetailsData.getAddress1());
                    ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.GONE);
                    ((CustomTextView) findViewById(R.id.txtPropertyLocation2)).setText(mCommonDetailsData.getAddress2());
                    ((ImageView) findViewById(R.id.img_lock_contract)).setVisibility(View.GONE);
                    ((ImageView) findViewById(R.id.img_lock_data)).setVisibility(View.GONE);
                }
            }
         if (PreferenceUtils.getPlanVersionStatus()){
             ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setTextColor(getResources().getColor(R.color.grey_font));
             ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setText(mCommonDetailsData.getAddress1());
             ((ImageView) findViewById(R.id.img_eye)).setVisibility(View.GONE);
             ((CustomTextView) findViewById(R.id.txtPropertyLocation2)).setText(mCommonDetailsData.getAddress2());
             ((ImageView) findViewById(R.id.img_lock_contract)).setVisibility(View.GONE);
             ((ImageView) findViewById(R.id.img_lock_data)).setVisibility(View.GONE);
         }
            ((CustomTextView) findViewById(R.id.textViewPropertyDetailsBedsNo)).setText(mCommonDetailsData.getBedNos());
            ((CustomTextView) findViewById(R.id.textViewPropertyDetailsBathNo)).setText(mCommonDetailsData.getBathNos());

            if (!mCommonDetailsData.getPropertyType().isEmpty()) {
                ((RelativeLayout) findViewById(R.id.relativeVacantBox)).setVisibility(View.VISIBLE);
                ((CustomTextView) findViewById(R.id.txtPropertyType)).setText(mCommonDetailsData.getPropertyType());
            } else {
                ((RelativeLayout) findViewById(R.id.relativeVacantBox)).setVisibility(View.GONE);
            }

            String areaMeasure = " Sq/Ft";
            String propertyArea = mCommonDetailsData.getArea() + "" + areaMeasure;

            ((CustomTextView) findViewById(R.id.textViewPropertyDetailsMeterNo)).setText(propertyArea);

            if (mCommonDetailsData.getPropertyVacant().equalsIgnoreCase("0")) {
                ((CustomTextView) findViewById(R.id.txtVacant)).setText("No");
            } else {
                ((CustomTextView) findViewById(R.id.txtVacant)).setText("Yes");
            }

            if (mCommonDetailsData.getPreForeclosure().equalsIgnoreCase("1")) {
                ((RelativeLayout) findViewById(R.id.relativeUnlistedListedTag)).setBackgroundResource(R.drawable.button_ab_gradient);
                ((CustomTextView) findViewById(R.id.txtUnlistedListedTag)).setText("Pre-Forclosure");
                ((CustomTextView) findViewById(R.id.txtListed)).setText("None");


            } else {
                if (mCommonDetailsData.getPropertyListed().equalsIgnoreCase("0")) {
                    ((RelativeLayout) findViewById(R.id.relativeUnlistedListedTag)).setBackgroundResource(R.drawable.button_ab_gradient);
                    ((CustomTextView) findViewById(R.id.txtUnlistedListedTag)).setText("Off-Market");
                    ((CustomTextView) findViewById(R.id.txtListed)).setText("No");
                } else {
                    ((RelativeLayout) findViewById(R.id.relativeUnlistedListedTag)).setBackgroundResource(R.drawable.button_gradian);
                    ((CustomTextView) findViewById(R.id.txtUnlistedListedTag)).setText("Listed");
                    ((CustomTextView) findViewById(R.id.txtListed)).setText("Yes");
                }
            }

            if (mCommonDetailsData.getPreForeclosure().equalsIgnoreCase("0")) {
                ((CustomTextView) findViewById(R.id.txtForPreClosure)).setText("No");

                //new feature
                relativePreClosureInformationBox.setVisibility(View.GONE);

                //end new feature
            } else {
                ((CustomTextView) findViewById(R.id.txtForPreClosure)).setText("Yes");

                //new feature
                relativePreClosureInformationBox.setVisibility(View.VISIBLE);
                if (mCommonDetailsData.getPayoffAmt() != null && !mCommonDetailsData.getPayoffAmt().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.txtPayOffAmount)).setText("$ " + mCommonDetailsData.getPayoffAmt());
                } else {
                    ((CustomTextView) findViewById(R.id.txtPayOffAmount)).setText("N/A");
                }
                if (mCommonDetailsData.getNoteBalance() != null && !mCommonDetailsData.getNoteBalance().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.txtNoteBalance)).setText("$ " + mCommonDetailsData.getNoteBalance());
                } else {
                    ((CustomTextView) findViewById(R.id.txtNoteBalance)).setText("N/A");
                }
                if (mCommonDetailsData.getSurrenderAgreement() != null && !mCommonDetailsData.getSurrenderAgreement().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.txtSurrenderAgreement)).setText("$ " + mCommonDetailsData.getSurrenderAgreement());
                } else {
                    ((CustomTextView) findViewById(R.id.txtSurrenderAgreement)).setText("N/A");
                }
                if (mCommonDetailsData.getFlippBiddServices() != null && !mCommonDetailsData.getFlippBiddServices().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.txtFlippBiddServices)).setText("$ " + mCommonDetailsData.getFlippBiddServices());
                } else {
                    ((CustomTextView) findViewById(R.id.txtFlippBiddServices)).setText("N/A");
                }
                if (mCommonDetailsData.getMustCloseByDate() != null && !mCommonDetailsData.getMustCloseByDate().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.txtCloseDate)).setText(changeDateFormate(mCommonDetailsData.getMustCloseByDate()));
                } else {
                    ((CustomTextView) findViewById(R.id.txtCloseDate)).setText("None");
                }

                //end new feature


            }


            ((ImageView) findViewById(R.id.img_eye)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PreferenceUtils.getIsPremiumUser() == 1 ) {
                        startActivity(new Intent(PropertyDetails.this, AddressActivity.class).putExtra("Lat", mCommonDetailsData.getLat())
                                .putExtra("Lng", mCommonDetailsData.getLang())
                                .putExtra("Address1", mCommonDetailsData.getAddress1())
                                .putExtra("Address2", mCommonDetailsData.getAddress2()));
                    }else if(PreferenceUtils.getPlanVersionStatus()){
                        startActivity(new Intent(PropertyDetails.this, AddressActivity.class).putExtra("Lat", mCommonDetailsData.getLat())
                                .putExtra("Lng", mCommonDetailsData.getLang())
                                .putExtra("Address1", mCommonDetailsData.getAddress1())
                                .putExtra("Address2", mCommonDetailsData.getAddress2()));
                    }
                    else {
                        showAlertDialogForPremiumUser(false);
                    }
                }
            });


            ((CustomTextView) findViewById(R.id.txtPropertyLocation1)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (PreferenceUtils.getIsPremiumUser() == 1 ) {
                        startActivity(new Intent(PropertyDetails.this, AddressActivity.class).putExtra("Lat", mCommonDetailsData.getLat())
                                .putExtra("Lng", mCommonDetailsData.getLang())
                                .putExtra("Address1", mCommonDetailsData.getAddress1())
                                .putExtra("Address2", mCommonDetailsData.getAddress2()));
                    }else if(PreferenceUtils.getPlanVersionStatus()){
                        startActivity(new Intent(PropertyDetails.this, AddressActivity.class).putExtra("Lat", mCommonDetailsData.getLat())
                                .putExtra("Lng", mCommonDetailsData.getLang())
                                .putExtra("Address1", mCommonDetailsData.getAddress1())
                                .putExtra("Address2", mCommonDetailsData.getAddress2()));
                    } else {
                        showAlertDialogForPremiumUser(false);
                    }
                }
            });


            ((CustomTextView) findViewById(R.id.txtPropertyLocation2)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PropertyDetails.this, AddressActivity.class).putExtra("Lat", mCommonDetailsData.getLat())
                            .putExtra("Lng", mCommonDetailsData.getLang())
                            .putExtra("Address1", mCommonDetailsData.getAddress1())
                            .putExtra("Address2", mCommonDetailsData.getAddress2())
                            .putExtra("Plan", mCommonDetailsData.getData().getPlan()));

                }
            });

            if (mCommonDetailsData.getNdrs_number() != null && !mCommonDetailsData.getNdrs_number().equalsIgnoreCase("")) {
                ((CustomTextView) findViewById(R.id.txtMlsAgentId)).setText(mCommonDetailsData.getNdrs_number());
            } else {
                ((CustomTextView) findViewById(R.id.txtMlsAgentId)).setText("N/A");
            }


            if (mCommonDetailsData.getPreForeclosure().equalsIgnoreCase("1")) {
                ((CustomTextView) findViewById(R.id.txtPropertyPrice)).setVisibility(View.GONE);
            } else {
                ((CustomTextView) findViewById(R.id.txtPropertyPrice)).setText("$ " + currencyFormat(mCommonDetailsData.getRentAmount()));
            }

            ((CustomTextView) findViewById(R.id.textViewAdminName)).setText(mCommonDetailsData.getData().getFullName());
//            ((CustomTextView) findViewById(R.id.textViewAdminName)).setText(mCommonDetailsData.getData().getUsername());
            if (mCommonDetailsData.getData().getProfilePic() != null && !mCommonDetailsData.getData().getProfilePic().equalsIgnoreCase("")) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.ARGB_4444)
                        .build();
                mImageLoader.displayImage(mCommonDetailsData.getData().getProfilePic(), ((CircleImageView) findViewById(R.id.imageViewAdminProfile)), options);

            } else {
                ((CircleImageView) findViewById(R.id.imageViewAdminProfile)).setImageResource(R.mipmap.user);
            }


            if (mCommonDetailsData.getData().getRateAverage() != null && mCommonDetailsData.getData().getRateAverage() != 0) {
                ((CustomTextView) findViewById(R.id.txtAdminRatingCount)).setText(String.valueOf(mCommonDetailsData.getData().getRateAverage()));
                if (mCommonDetailsData.getData().getRateCount() > 1) {
                    ((CustomTextView) findViewById(R.id.txtAdminRatingReviewCount)).setText("/ 5 (" + mCommonDetailsData.getData().getRateCount() + " reviews)");
                } else {
                    ((CustomTextView) findViewById(R.id.txtAdminRatingReviewCount)).setText(" / 5 (" + mCommonDetailsData.getData().getRateCount() + " review)");
                }
            }

            if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                if (mCommonDetailsData.getRecUsername().isEmpty()) {
                    findViewById(R.id.txtReceivedByTitle).setVisibility(View.GONE);
                    findViewById(R.id.relativeLayoutUserDetailsBox).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.txtReceivedByTitle).setVisibility(View.VISIBLE);
                    findViewById(R.id.relativeLayoutUserDetailsBox).setVisibility(View.VISIBLE);


                    ((CustomTextView) findViewById(R.id.textViewTitleContactLink)).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                            contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                            contactIntent
                                    .putExtra(ContactsContract.Intents.Insert.NAME, mCommonDetailsData.getRecUsername())
                                    .putExtra(ContactsContract.Intents.Insert.PHONE, mCommonDetailsData.getRecCountryCode() + "" + mCommonDetailsData.getRecMobileMumber());
                            startActivityIfNeeded(contactIntent, 1);
                        }
                    });
                }
            }

            if (mCommonDetailsData.getDescription() != null && !mCommonDetailsData.getDescription().equalsIgnoreCase("")) {
                ((CustomTextView) findViewById(R.id.txtPropertyDescription)).setText(mCommonDetailsData.getDescription());
            }

            /*imageDThumb*/
            if (mCommonDetailsData.isThumb()) {
                ((ImageView) findViewById(R.id.imageDThumb)).setImageResource(R.drawable.thumbs_up_detail_fill);
            } else {
                ((ImageView) findViewById(R.id.imageDThumb)).setImageResource(R.drawable.thumbs_up_detailview);
            }

            try {
                ((CustomTextView) findViewById(R.id.textViewUserName)).setText(mCommonDetailsData.getRecUsername());
                if (mCommonDetailsData.getRecCountryCode() != null && !mCommonDetailsData.getRecCountryCode().equalsIgnoreCase("")
                        || mCommonDetailsData.getRecMobileMumber() != null && !mCommonDetailsData.getRecMobileMumber().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.textViewTitleContactLink)).setText("(" + mCommonDetailsData.getRecCountryCode() + ") " + mCommonDetailsData.getRecMobileMumber());
                    ((CustomTextView) findViewById(R.id.textViewTitleContactLink)).setPaintFlags(((CustomTextView) findViewById(R.id.textViewTitleContactLink)).getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                } else {
                    ((CustomTextView) findViewById(R.id.textViewTitleContactLink)).setVisibility(View.INVISIBLE);
                }
                if (mCommonDetailsData.getRecEmailAddress() != null && !mCommonDetailsData.getRecEmailAddress().equalsIgnoreCase("")) {
                    ((CustomTextView) findViewById(R.id.textViewTitleEmailAddress)).setVisibility(View.VISIBLE);
                    ((CustomTextView) findViewById(R.id.textViewTitleEmailAddress)).setText(mCommonDetailsData.getRecEmailAddress());
                } else {
                    ((CustomTextView) findViewById(R.id.textViewTitleEmailAddress)).setVisibility(View.GONE);
                }
            } catch (Exception e) {
            }
        }

        //set up view bidd
        setViewBidd(mCommonDetailsData);

        //call api for chat button
        if (!BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
//            UserPreference.getInstance(PropertyDetails.this).setIsThreeDotIntro(true);
//            UserPreference.getInstance(PropertyDetails.this).setIsBiddIntro(true);
//            UserPreference.getInstance(PropertyDetails.this).setIsDataIntro(true);
//            UserPreference.getInstance(PropertyDetails.this).setIsMyCalendarIntro(true);
//            UserPreference.getInstance(PropertyDetails.this).setIsContractIntro(true);
            if (UserPreference.getInstance(PropertyDetails.this).isThreeDotIntro()) {
                showDiag();
            }
        }

    }

    public static String currencyFormat(String amount) {
        DecimalFormat formatter = new DecimalFormat("##,###,###");
        return formatter.format(Double.parseDouble(amount));
    }


    private void setViewBidd(CommonData mCommonDetailsData) {

        if (mCommonDetailsData.getBidds() != null && mCommonDetailsData.getBidds().size() != 0) {
            findViewById(R.id.relativeViewBidd_s).setVisibility(View.VISIBLE);
            findViewById(R.id.txtOtherUserBiddTitle).setVisibility(View.VISIBLE);
            findViewById(R.id.relativeBiddFirstBox).setVisibility(View.GONE);
            findViewById(R.id.relativePlaceBiddBox_1).setVisibility(View.GONE);

            RecyclerView recyclerView = findViewById(R.id.recyclerViewBiddList);
            if (mCommonDetailsData.getBidds() != null && mCommonDetailsData.getBidds().size() != 0) {
                PropertyBiddListAdapter propertyBiddListAdapter = new PropertyBiddListAdapter(PropertyDetails.this, mCommonDetailsData.getBidds(), false, mLoginId);
                recyclerView.setLayoutManager(new LinearLayoutManager(PropertyDetails.this));
                recyclerView.setHasFixedSize(true);
                recyclerView.setAdapter(propertyBiddListAdapter);
                propertyBiddListAdapter.setItemOnClickEvent(this::onClickEvent);
            }
            if (mCommonDetailsData.getBidds().size() > 2) {
                findViewById(R.id.txtViewAllBidd).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.txtViewAllBidd).setVisibility(View.VISIBLE);
            }

        } else {

//            findViewById(R.id.linearViewAllBidd).setVisibility(View.GONE);
            findViewById(R.id.recyclerViewBiddList).setVisibility(View.GONE);
            findViewById(R.id.txtViewAllBidd).setVisibility(View.GONE);
            findViewById(R.id.txtOtherUserBiddTitle).setVisibility(View.GONE);

            if (!BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                findViewById(R.id.relativeBiddFirstBox).setVisibility(View.VISIBLE);
                findViewById(R.id.relativeViewBidd_s).setVisibility(View.VISIBLE);
                findViewById(R.id.relativePlaceBiddBox_1).setVisibility(View.VISIBLE);
            } else {
                findViewById(R.id.relativeBiddFirstBox).setVisibility(View.GONE);
                findViewById(R.id.relativeViewBidd_s).setVisibility(View.GONE);
                findViewById(R.id.relativePlaceBiddBox_1).setVisibility(View.GONE);
            }

            findViewById(R.id.relativePlaceBiddBox_1).setOnClickListener(this::onClick);
        }

    }


    private void setViewPager(CommonData mCommonDetailsData) {

        if (mCommonDetailsData.getImages().size() != 0) {
            ImageView propertyNoImage = findViewById(R.id.PropertyNoimage);
            propertyNoImage.setVisibility(View.GONE);


            ViewPager pager = (ViewPager) findViewById(R.id.viewpager2);
            PhotosAdapter adapter = new PhotosAdapter(PropertyDetails.this, mCommonDetailsData.getImages(), "", "", mCommonDetailsData.isExpiriedStatus(), mCommonDetailsData.getIsAvailable());
            pager.setAdapter(adapter);
            CircleIndicator tabLayout = (CircleIndicator) findViewById(R.id.indicator);
            tabLayout.setViewPager(pager);
        } else {
            //show no image view
            ImageView propertyNoImage = findViewById(R.id.PropertyNoimage);
            propertyNoImage.setVisibility(View.VISIBLE);
        }
    }

    public void showSnkbar(String showStr) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), showStr, Snackbar.LENGTH_LONG);
        View snackBarView = snackbar.getView();
        snackBarView.setBackgroundColor(Color.parseColor("#ff21ab29")); // snackbar background color
        snackbar.show();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageDBack:
                //back
                if (FROME_TO.equalsIgnoreCase("")) {
                    finish();
                } else {
                    startActivity(new Intent(PropertyDetails.this, MainActivity.class));
                    finishAffinity();
                }
                break;
            case R.id.imageShare:
                shareData(mCommonDetailsData);
                break;
            case R.id.relativeSendEmail:
                //open meeting
                //openMeetingBottomSheet();
                //open email composer here
                openEmailComposer();
                break;
            case R.id.relativecall:
                openCallDialog();
                break;
            case R.id.imageCallingView:
                openCallDialog();


                //openMeetingBottomSheet();
//                }
                break;
            case R.id.imageMoreOption:
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    //for same user
                    boolean isAvailable;
                    if (mCommonDetailsData.getIsAvailable().equalsIgnoreCase("1")) {
                        isAvailable = true;
                    } else {
                        isAvailable = false;
                    }
                    openAdminBottomSheet(isAvailable);
                } else {
                    //for other user
                    if (PreferenceUtils.getIsPremiumUser() == 1 || mCommonDetailsData.getData().getPlan().equalsIgnoreCase(PRO_VERSION)) {
                        openBottomSheet(true);
                    } else {
                        openBottomSheet(false);
                    }

                }
                break;
            case R.id.imageFileUpload:
                if (mCommonDetailsData.getData().getLoginId().equals(UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId())) {
                    startActivityIfNeeded(new Intent(PropertyDetails.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", mScreenName)
                            .putExtra("post_id", mCommonId)
                            .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
                } else {
                    if (PreferenceUtils.getIsPremiumUser() == 1 ) {
                        if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                            if (mCommonDetailsData.getData().getLoginId() != null && !mCommonDetailsData.getData().getLoginId().equalsIgnoreCase("")) {
                                startActivityIfNeeded(new Intent(PropertyDetails.this, UploadFiles_Activity_List.class)
                                        .putExtra("post_type", mScreenName)
                                        .putExtra("post_id", mCommonId)
                                        .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
//                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                            }
                        }
                    }else if(PreferenceUtils.getPlanVersionStatus()){
                        if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                            if (mCommonDetailsData.getData().getLoginId() != null && !mCommonDetailsData.getData().getLoginId().equalsIgnoreCase("")) {
                                startActivityIfNeeded(new Intent(PropertyDetails.this, UploadFiles_Activity_List.class)
                                        .putExtra("post_type", mScreenName)
                                        .putExtra("post_id", mCommonId)
                                        .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
//                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                            }
                        }
                    } else {
                        showAlertDialogForPremiumUser(true);
                    }
                }

                break;
            case R.id.txtViewAllBidd:
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.getData().getLoginId() != null && !mCommonDetailsData.getData().getLoginId().equalsIgnoreCase("")) {
                        startActivityIfNeeded(new Intent(PropertyDetails.this, PropertyBiddListActivity.class)
                                .putExtra("user_id", mCommonDetailsData.getData().getLoginId())
                                .putExtra("p_id", mCommonId)
                                .putExtra("isBidd", mCommonDetailsData.isBidd())
                                .putExtra("p_type", mScreenName), 0);
//                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                    }
                }
                break;
            case R.id.relativeRequestBidd:
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.isBidd() == 0) {
                        //open dialog
                        openDilaogView();
                    } else {
                        ToastUtils.shortToast(Constants.ALREADY_BIDD_ALERT);
                    }
                }
                break;
            case R.id.relativeLayoutAdminBox:
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.getData().getLoginId() != null && !mCommonDetailsData.getData().getLoginId().equalsIgnoreCase("")) {
                        startActivityIfNeeded(new Intent(PropertyDetails.this, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, mCommonDetailsData.getData().getLoginId()), 3);
//                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                    }
                }
                break;
            case R.id.imageDThumb:

                Vibrator vb = (Vibrator) PropertyDetails.this.getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vb.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vb.vibrate(500);
                }

                if (!mCommonDetailsData.isThumb()) {
                    mCommonDetailsData.setThumb(true);
                    ((ImageView) findViewById(R.id.imageDThumb)).setImageResource(R.drawable.thumbs_up_detail_fill);
                    addThum();
                }
                break;
            case R.id.imageContractBox:
                if (PreferenceUtils.getIsPremiumUser() == 1 ) {//
                    if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                        openActionRestricted();
                        return;
                    }
                    //open new activity
                    startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
                            .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }else if(PreferenceUtils.getPlanVersionStatus()){
                    if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                        openActionRestricted();
                        return;
                    }
                    //open new activity
                    startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
                            .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
            } else {
                    //show dialog
                    showAlertDialogForPremiumUser(false);
                }

//                if (mOtherUserDetails != null) {
//                    if (mOtherUserDetails.getPofDoc() != null &&
//                            !mOtherUserDetails.getPofDoc().equalsIgnoreCase("")) {
//                        startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
//                                .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
////                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
//                    } else {
//                        showAlertDialog();
//                    }
//                } else {
//                    if (mUserDetails.getPofDoc() != null &&
//                            !mUserDetails.getPofDoc().equalsIgnoreCase("")) {
//                        startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
//                                .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
////                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
//                    } else {
//                        showAlertDialog();
//                    }
//                }
                break;
            case R.id.relativePlaceBiddBox:
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.isBidd() == 0) {
                        //open dialog
                        openDilaogView();
                    } else {
                        ToastUtils.shortToast(Constants.ALREADY_BIDD_ALERT);
                    }
                }
                break;
            case R.id.relativePlaceBiddBox_1:
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.isBidd() == 0) {
                        //open dialog
                        openDilaogView();
                    } else {
                        ToastUtils.shortToast(Constants.ALREADY_BIDD_ALERT);
                    }
                }
                break;
        }
    }

    private void openEmailComposer() {
        String vacant = "";
        String preForecloser = "";
        if (mCommonDetailsData.getPropertyVacant().equals("1")) {
            vacant = "Yes";
        } else {
            vacant = "No";
        }

        if (mCommonDetailsData.getPreForeclosure().equals("1")) {
            preForecloser = "yes";
        } else {
            preForecloser = "No";
        }


        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        emailIntent.setType("text/html");
        emailIntent.setPackage("com.google.android.gm");
        String[] recipients;
        if (mCommonDetailsData.getLoginId().equals("201")) {
            recipients = new String[]{"support@flippbidd.com"};
        } else {
            recipients = new String[]{"flippbidd@gmail.com"};
        }
        emailIntent.putExtra(Intent.EXTRA_EMAIL, recipients);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getFullName() + " is looking for your property.");

        emailIntent.putExtra(Intent.EXTRA_TEXT, "Property Title: " + mCommonDetailsData.getHouse() + "\n" +
                "Description: " + mCommonDetailsData.getDescription() + "\n" +
                "Bath: " + mCommonDetailsData.getBathNos() + "\n" +
                "Beds: " + mCommonDetailsData.getBedNos() + "\n" +
                "Sq.Ft: " + mCommonDetailsData.getArea() + "\n" +
                "Vacant: " + vacant + "\n" +
                "Family Type: " + mCommonDetailsData.getPropertyType() + "\n" +
                "Pre-Foreclosure: " + preForecloser + "\n\n" +
                "Buyer's Details" + "\n" +
                "User Name: " + BaseApplication.getInstance().getUserFullName() + "\n" +
                "Mobile Number: " + BaseApplication.getInstance().getUserMobileNum());
        startActivity(emailIntent);
    }

    /*open meeting schedule sheet*/
    private void openMeetingBottomSheet() {
        //check remaining counts
        if (userPlanName.equalsIgnoreCase(Constants.FREE_VERSION)) {
            if (remainingCounts <= 0) {
                openLimitExtendPlan(PropertyDetails.this.getResources().getString(R.string.alert_message_3));
                return;
            }
        }

        MeetingScheduleSheet meetingScheduleSheet = MeetingScheduleSheet.newInstance();
        meetingScheduleSheet.addListener(new MeetingScheduleSheet.DialogListener() {

            @Override
            public void scheduleMeetingRequest(String selectedDate, String selectedStartTime, String selectedEndTime, String mMessage, String selectedType, String createdType) {
                meetingScheduleSheet.dismiss();
                requestMS(selectedDate, selectedStartTime, selectedEndTime, mMessage, selectedType, createdType);
            }

            @Override
            public void onCancelClick() {
                meetingScheduleSheet.dismiss();
            }
        }, "user", null, mCommonDetailsData);
        meetingScheduleSheet.show(getSupportFragmentManager(), GroupInfoActivity.class.getSimpleName());
    }

    /*request meeting schedule call api*/
    private void requestMS(String date, String startTime, String endTime, String message, String type, String createdType) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        showProgressBar(true);

        //token, property_id, owner_id, login_id, date, start_time, end_time, instruction, type
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getLoginId()));
        documentRequest.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        documentRequest.put("date", RequestBody.create(MediaType.parse("multipart/form-data"), date));
        documentRequest.put("start_time", RequestBody.create(MediaType.parse("multipart/form-data"), startTime));
        documentRequest.put("end_time", RequestBody.create(MediaType.parse("multipart/form-data"), endTime));
        documentRequest.put("instruction", RequestBody.create(MediaType.parse("multipart/form-data"), message));
        documentRequest.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), type));
        documentRequest.put("created_type", RequestBody.create(MediaType.parse("multipart/form-data"), createdType));


        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.requestMeetingSchedule(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                showProgressBar(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    //success message
                    remainingCounts = mJsonObject.get("remaining_count").getAsInt();
                    if (mJsonObject.has("text")) {
                        if (mJsonObject.get("text").getAsString().equalsIgnoreCase(PropertyDetails.this.getResources().getString(R.string.alert_message_3))) {
                            openLimitExtendPlan(mJsonObject.get("text").getAsString());
                        } else {
                            openAlertDialog(mJsonObject.get("text").getAsString());
                        }
                    }
                } else {
                    //error messsage
                    openAlertDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void openAlertDialog(String s) {
        CommonDialogView.getInstance().showCommonDialog(PropertyDetails.this, "",
                s
                , "Ok"
                , "Ok"
                , false, true, true, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {

                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }

    private void openLimitExtendPlan(String s) {
        CommonDialogView.getInstance().showCommonDialog(PropertyDetails.this, "",
                s
                , "Cancel"
                , "Upgrade"
                , false, true, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        //open upgrade plan activity
                        Intent mIntent = new Intent(PropertyDetails.this, InAppPurchaseActivity.class);
                        startActivity(mIntent);
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }
    /*end*/

    /*end*/

    private void showAlertDialog() {
        CommonDialogView.getInstance().showCommonDialog(PropertyDetails.this, "",
                Constants.NOT_UPLOAD_POF
                , mBaseAppCompatActivity.getResources().getString(R.string.upload_pof)
                , mBaseAppCompatActivity.getResources().getString(R.string.request_contract)
                , false, true, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
                                .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                    }

                    @Override
                    public void onDialogCancel(View view) {
                        startActivityIfNeeded(new Intent(mBaseAppCompatActivity, ProfileEditActivity.class), 897);
                    }
                });
    }

    private void showAlertDialogForPremiumUser(Boolean isFromData) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        if (isFromData) {
            builder.setMessage("You do not have access to this content.  Please fill out the Investor Portal Form or contact \nsupport@flippbidd.com");
        } else {
            //builder.setMessage("You can not access this content. Please contact flippbidd admin.");
            builder.setMessage("You do not have access to this content.  Please fill out the Investor Portal Form or contact \nsupport@flippbidd.com");
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.dismiss();
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void openActionRestricted() {

        Dialog mDialog = new Dialog(PropertyDetails.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dialog_action_restricted);


        mDialog.findViewById(R.id.txt_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    String isNotify = "0";

    private void openDilaogView() {

        Dialog mDialog = new Dialog(PropertyDetails.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dialog_place_bidd);

        ((CheckBox) mDialog.findViewById(R.id.checkboxNotifyDrop)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isNotify = "1";
                } else {
                    isNotify = "0";
                }
            }
        });

        mDialog.findViewById(R.id.txt_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (((CustomEditText) mDialog.findViewById(R.id.txt_message)).getText().toString().isEmpty()) {
                    showSnkbar("Please add price");
                    return;
                }
                String priceValue = ((CustomEditText) mDialog.findViewById(R.id.txt_message)).getText().toString();
                addBidd(priceValue, isNotify);

                mDialog.dismiss();
            }
        });
        mDialog.findViewById(R.id.txt_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.show();
    }

    private void addBidd(String priceValue, String isNotify) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressBar(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyDetails.this).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyDetails.this).getUserDetail().getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
        linkedHashMap.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), priceValue));
        linkedHashMap.put("is_notify", RequestBody.create(MediaType.parse("multipart/form-data"), isNotify));

        UserServices userService = ApiFactory.getInstance(PropertyDetails.this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.addBidd(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
                    getDetailsProperty(false);
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

    private void addThum() {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressBar(false);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyDetails.this).getUserDetail().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(PropertyDetails.this).getUserDetail().getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));

        UserServices userService = ApiFactory.getInstance(PropertyDetails.this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.addThum(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                showProgressBar(false);
                if (response.getSuccess()) {
//                    showSnkbar(response.getText());
                    setResult(RESULT_OK);
                } else {
//                    showSnkbar(response.getText());
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }


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

            String address;

            if (PreferenceUtils.getIsPremiumUser() == 1 ) {
                address = mCommonDetailsData.getAddress1() + " " + mCommonDetailsData.getAddress2();
            }else if(PreferenceUtils.getPlanVersionStatus()){
                address = mCommonDetailsData.getAddress1() + " " + mCommonDetailsData.getAddress2();
            } else {
                address = "***" + " " + mCommonDetailsData.getAddress2();
            }
            String message = mUserDetails.getFullName() + " Shared a Property \n" + houseName + "\nPrice : " + "$" + currencyFormat(price) + "\nAddress : " + address + "\n\n"
                    + "View Picture Below:\n\n" + images;
            sendIntent.putExtra(Intent.EXTRA_TEXT, message);
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, "Share to"));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (FROME_TO.equalsIgnoreCase("")) {
            finish();
        } else {
            startActivity(new Intent(PropertyDetails.this, MainActivity.class));
            finishAffinity();
        }
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
                /*address**ownerID**addedby**propertyID**channelMainID*/
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
        Intent mainIntent = new Intent(PropertyDetails.this, GroupChatActivity.class);
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

        System.out.println("!!!mCommonDetailsData.getData().getEmailAddress() = " + mCommonDetailsData.getData().getEmailAddress());
        System.out.println("!!!BaseApplication.getInstance().getQBLoginID() = " + BaseApplication.getInstance().getQBLoginID());
        System.out.println("!!!mCommonId = " + mCommonId);
        System.out.println("!!!mScreenName = " + mScreenName);

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
        /*address##ownerID##addedby##propertyID##channelMainID*/
        return mCommonDetailsData.getAddress() + "##" + mCommonDetailsData.getData().getEmailAddress() + "##"
                + mCommonDetailsData.getData().getFullName() + "##" + mCommonDetailsData.getCommonId() + "##" + channelMainId;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCommonDetailsData != null) {
            checkChannelStatus(false);
        }
    }

    private void openCallDialog() {
        CallDialogFragment callDialogFragment = CallDialogFragment.newInstance();
        callDialogFragment.addListener(new CallDialogFragment.DailogListener() {

            @Override
            public void onCallClick() {
                callDialogFragment.dismiss();
                String calleeIds, callerName;
                calleeIds = mCommonDetailsData.getData().getEmailAddress();
                callerName = mCommonDetailsData.getData().getFullName();
                NewCallService.dial(PropertyDetails.this, callerName, calleeIds, false);
                PreferenceUtils.setCalleeId(calleeIds);
            }

            @Override
            public void onScheduleCallClick() {
                openMeetingBottomSheet();
                callDialogFragment.dismiss();
            }

            @Override
            public void onCancelClick() {
                callDialogFragment.dismiss();
            }
        });

        callDialogFragment.show(getSupportFragmentManager(), PropertyDetails.class.getSimpleName());
    }

    //admin bottom view
    private void openAdminBottomSheet(boolean isStatus) {
        AdminMenuBottomSheetDialogFragment adminMenuBottomSheetDialogFragment = AdminMenuBottomSheetDialogFragment.newInstance();
        adminMenuBottomSheetDialogFragment.addListener(new AdminMenuBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onRequestEditPropertyClick() {
                adminMenuBottomSheetDialogFragment.dismiss();
                Intent mIntent = new Intent(PropertyDetails.this, PostNewProperty.class);
                mIntent.putExtra(Constants.EXTRA.DATA, mCommonDetailsData);
                mIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.EDIT);
                startActivityIfNeeded(mIntent, 987);
//                overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }

            @Override
            public void onRequestViewDataFolderClick() {
                adminMenuBottomSheetDialogFragment.dismiss();
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivityIfNeeded(new Intent(PropertyDetails.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", mScreenName)
                            .putExtra("post_id", mCommonId)
                            .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
//                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
            }

            @Override
            public void onRequestViewBiddClick() {
                adminMenuBottomSheetDialogFragment.dismiss();
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.getData().getLoginId() != null && !mCommonDetailsData.getData().getLoginId().equalsIgnoreCase("")) {
                        startActivityIfNeeded(new Intent(PropertyDetails.this, PropertyBiddListActivity.class)
                                .putExtra("user_id", mCommonDetailsData.getData().getLoginId())
                                .putExtra("p_id", mCommonId)
                                .putExtra("isBidd", mCommonDetailsData.isBidd())
                                .putExtra("p_type", mScreenName), 4);
//                        overridePendingTransition(R.anim.slide_up, R.anim.stay);
                    }
                }

            }

            @Override
            public void onRequestPropertyUnavilable(String values) {
                adminMenuBottomSheetDialogFragment.dismiss();
                if (values.equalsIgnoreCase("Available")) {
                    //call unavailable
                    warningDialog(Constants.ACTION_HEADER_TYPE.UNAVAILABLE, "Property", 0, mCommonDetailsData, mBaseAppCompatActivity.getResources().getString(R.string.string_available_message), Constants.ACTION.PROPERTY_UNAVAILABLE_ACTION);
                } else {
                    warningDialog(Constants.ACTION_HEADER_TYPE.AVAILABLE, "Property", 0, mCommonDetailsData, mBaseAppCompatActivity.getResources().getString(R.string.string_unavailable_message), Constants.ACTION.PROPERTY_AVAILABLE_ACTION);
                }
            }

            @Override
            public void onCancelClick() {
                adminMenuBottomSheetDialogFragment.dismiss();
            }
        }, isStatus);
        adminMenuBottomSheetDialogFragment.show(getSupportFragmentManager(), PropertyDetails.class.getSimpleName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 987 || requestCode == 4) {
                getDetailsProperty(false);
            }
            if (requestCode == 897) {

            }
        }
    }

    //open bottom sheet
    private void openBottomSheet(boolean isPremiumUser) {

        NewMenuBottomSheetDialogFragment newMenuBottomSheetDialogFragment = NewMenuBottomSheetDialogFragment.newInstance();
        newMenuBottomSheetDialogFragment.addListener(new NewMenuBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onRequestContractClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                //open new activity
                startActivityIfNeeded(new Intent(PropertyDetails.this, RequestContractActivity.class)
                        .putExtra("p_id", mCommonId).putExtra("p_type", mScreenName), 764);
//                overridePendingTransition(R.anim.slide_up, R.anim.stay);

            }

            @Override
            public void onRequestViewDataFolderClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    startActivityIfNeeded(new Intent(PropertyDetails.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", mScreenName)
                            .putExtra("post_id", mCommonId)
                            .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
//                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
            }

            @Override
            public void onRequestMessageUserClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                mIsDistinct = PreferenceUtils.getGroupChannelDistinct();
                createGroupChannel(mSelectedIds, mIsDistinct);
            }

            @Override
            public void onRequestPlaceBiddClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(mLoginId)) {
                    openActionRestricted();
                    return;
                }
                if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                    if (mCommonDetailsData.isBidd() == 0) {
                        //open dialog
                        openDilaogView();
                    } else {
                        ToastUtils.shortToast(Constants.ALREADY_BIDD_ALERT);
                    }
                }
            }

            @Override
            public void onRequestSharePropertyClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                shareData(mCommonDetailsData);
            }

            @Override
            public void onRequestReportContentClick() {
                newMenuBottomSheetDialogFragment.dismiss();
                startActivityIfNeeded(new Intent(PropertyDetails.this, ReportContentActivity.class)
                        .putExtra("post_type", mScreenName)
                        .putExtra("post_id", mCommonId)
                        .putExtra("user_id", mCommonDetailsData.getData().getLoginId()), 802);
//                overridePendingTransition(R.anim.slide_up, R.anim.stay);
            }

            @Override
            public void onCancelClick() {
                newMenuBottomSheetDialogFragment.dismiss();
            }
        }, isPremiumUser);
        newMenuBottomSheetDialogFragment.showNow(getSupportFragmentManager(), PropertyDetails.class.getSimpleName());
    }

    public void warningDialog(String headerTitle, final String mView, final int position, final CommonData mPropertyData, String messages,
                              final String mActionView) {

        CommonDialogView.getInstance().showCommonDialog(PropertyDetails.this, "",
                messages
                , mBaseAppCompatActivity.getResources().getString(R.string.string_no)
                , mBaseAppCompatActivity.getResources().getString(R.string.string_yes)
                , false, true, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        if (mActionView.equalsIgnoreCase(Constants.ACTION.PROPERTY_AVAILABLE_ACTION)) {
                            callUnAvailableApi(position, mPropertyData, "0");
                        } else {
                            callUnAvailableApi(position, mPropertyData, "1");
                        }
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });

    }

    private void callUnAvailableApi(final int position, CommonData mPropertyData, String
            isAvailable) {
        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            showSnkbar(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> hashMap = new LinkedHashMap<>();
        hashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getToken()));
        hashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail().getLoginId()));
        hashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mPropertyData.getCommonId()));
        hashMap.put("is_available", RequestBody.create(MediaType.parse("multipart/form-data"), isAvailable));

        showProgressBar(true);
        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.propertyUnavailable(hashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                showProgressBar(false);
                if (response.getSuccess()) {
                    setResult(RESULT_OK);
                    finish();
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    private void getUserDetails() {

        if (!NetworkUtils.isInternetAvailable(mBaseAppCompatActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("user_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(mBaseAppCompatActivity).provideService(UserServices.class);
        Observable<OtherUserDetailsResponse> observable;
        observable = userService.getOtherUserDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<OtherUserDetailsResponse>() {
            @Override
            public void onSuccess(OtherUserDetailsResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    mOtherUserDetails = response.getData();
                }

            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    @Override
    public void onClickEvent(int position, CommonData.Bidds mJsonObject, String mActionType, String priceValue) {
        switch (mActionType) {
            case Constants
                    .ACCEPT:
                callUpdateStatus("1", mJsonObject.getBiddId(), priceValue);
                break;
            case Constants
                    .REJECT:
                callUpdateStatus("2", mJsonObject.getBiddId(), priceValue);
                break;
            case Constants
                    .COUNTERED:
                callUpdateStatus("3", mJsonObject.getBiddId(), priceValue);
                break;

        }
    }

    private void callUpdateStatus(String status, String biddID, String price) {
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("bidd_id", RequestBody.create(MediaType.parse("multipart/form-data"), biddID));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), status));
        if (status.equalsIgnoreCase("3")) {
            linkedHashMap.put("price", RequestBody.create(MediaType.parse("multipart/form-data"), price));
        }

        UserServices userService = ApiFactory.getInstance(PropertyDetails.this).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.updateBiddStatus(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {

                } else {
                    showSnkbar(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }

//    private void callButtonStatus() {
//        //token, meeting_id, login_id, status
//        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
//        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
//        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
//        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getLoginId()));
//        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonId));
//
//        UserServices userService = ApiFactory.getInstance(PropertyDetails.this).provideService(UserServices.class);
//        Observable<JsonElement> observable;
//        observable = userService.requestMeetingScheduleStatus(linkedHashMap);
//        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
//            @Override
//            public void onSuccess(JsonElement response) {
//                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
//                //{"success":false,"text":"Button is not visible"}`
//                //{
//                //    "success": false,
//                //    "text": "Button is not visible",
//                //    "data": {
//                //        "type": "Audio Call",
//                //        "owner_id": "330",
//                //        "login_id": "290",
//                //        "owner_email": "bhavisha.hcuboidtech@gmail.com",
//                //        "other_email": "milap123@gmail.com"
//                //    }
//                //}
//                if (response.getAsJsonObject().get("status").getAsBoolean()) {
//                    JsonObject data = response.getAsJsonObject().get("data").getAsJsonObject();
//                    callType = data.get("type").getAsString();
//                    ownerEmail = data.get("owner_email").getAsString();
//                    requestEmail = data.get("other_email").getAsString();
//                    if (callType.equalsIgnoreCase("Audio Call")) {
//                        imageCallingView.setImageResource(R.drawable.audio_white);
//                        imageCalling.setImageResource(R.drawable.audio_white);
//                    } else if (callType.equalsIgnoreCase("Video Call")) {
//                        imageCallingView.setImageResource(R.drawable.video_white);
//                        imageCalling.setImageResource(R.drawable.video_white);
//                    } else {
//                        relativeSendEmail.setVisibility(View.GONE);
//                        imageCallingView.setVisibility(View.GONE);
//                    }
//                } else {
//                    callType = "";
//                    ownerEmail = "";
//                    requestEmail = "";
//                    relativeSendEmail.setVisibility(View.VISIBLE);
//                    imageCallingView.setVisibility(View.VISIBLE);
//                    imageCallingView.setImageResource(R.drawable.ic_calander_blackshadow);
//                    imageCalling.setImageResource(R.drawable.ic_calendar);
//                }
//
//            }
//
//            @Override
//            public void onFailed(Throwable throwable) {
//
//            }
//        });
//    }
}
