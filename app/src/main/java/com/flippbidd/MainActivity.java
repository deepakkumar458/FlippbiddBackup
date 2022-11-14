package com.flippbidd;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Fragments.CreatedFileter;
import com.flippbidd.Fragments.Drawer.FragmentDrawer;
import com.flippbidd.Fragments.FMDBottomsheetFragment;
import com.flippbidd.Fragments.FindMyDealFragment;
import com.flippbidd.Fragments.MyLikes;
import com.flippbidd.Fragments.MyLocationFragments;
import com.flippbidd.Fragments.MyProfile;
import com.flippbidd.Fragments.MyUploads;
import com.flippbidd.Fragments.Notification;
import com.flippbidd.Fragments.PropertyList;
import com.flippbidd.Fragments.RequestCallListFragment;
import com.flippbidd.Fragments.Services;
import com.flippbidd.Fragments.new_HomeFragments;
import com.flippbidd.Fragments.settings;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.Response.Feedback.PendingFeedback;
import com.flippbidd.Model.Response.Service.ServiceListData;
import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.RetrofitAPI;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Model.request.ChatUser;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.activity.AppUpdate.AppUpdateActivity;
import com.flippbidd.activity.Contract.RequestContractActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.Login;
import com.flippbidd.activity.ProfileEditActivity;
import com.flippbidd.activity.Property.PostNewProperty;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.activity.Rental.AddNewRental;
import com.flippbidd.activity.Services.AddNewServicer;
import com.flippbidd.activity.UploadFiles_Activity_List;
import com.flippbidd.activity.feedbackview.FeedBackActivity;
import com.flippbidd.activity.info.InfoActivity;
import com.flippbidd.activity.my_calender.RequestCallListActivity;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.killservice.StickyService;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChannelListFragment;
import com.flippbidd.sendbirdSdk.groupchannel.GroupChatActivity;
import com.flippbidd.sendbirdSdk.groupchannel.NewChatCreateActivity;
import com.flippbidd.sendbirdSdk.main.ConnectionManager;
import com.flippbidd.sendbirdSdk.widget.AuthenticationUtils;
import com.flippbidd.sendbirdSdk.widget.BroadcastUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.SyncManagerUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.calls.DirectCallLog;
import com.sendbird.syncmanager.SendBirdSyncManager;
import com.sendbird.syncmanager.handler.CompletionHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends BaseAppCompatActivity implements FragmentDrawer.FragmentDrawerListener, GroupChannelListFragment.OnFragmentInteractionListener, new_HomeFragments.OnFragmentInteractionListener {

    @BindView(R.id.len_drawer_view)
    LinearLayout len_drawer_view;
    @BindView(R.id.image_toolbar)
    ImageView drawer_icon;
    @BindView(R.id.main_toolbar)
    Toolbar main_toolbar;

    public static ImageView loivCreateNewChat;
    public static ImageView image_edit, image_delete_notification, imageRightItemList, imageRightOfJV,
            image3_toolbar, image5_toolbar, image6_toolbar, imageSubmitProperty, imageHeadreIcon;
    /*image2_toolbar*/
    public static LinearLayout viewRight_1, viewRight_4, viewRight_delete_All_notification;
    public static LinearLayout viewRight_2, viewRight_3;
    public static SearchView msearView;
    public static CustomTextView txt_title_toolbar;
    public static String mToolbarTitle = "";

    private DrawerLayout drawer;
    FragmentDrawer fragmentDrawer;
    int indexeOf = 0;
    boolean isLinearView = true;

    UserDetails mUserResponse;
    Fragment mFragment;

    private Disposable disposable;


    //new code 11/02/19
//    public static BadgedTabLayout mainTabBar;
    public static BottomNavigationView mainTabBar;

    int lastSelectedIndex = 0;
    int lastSelectedDrawerIndex = 0;
    int secondlastSelectedDrawerIndex = 0;
    Menu moMenu;

    /*chat*/
    private static final int INTENT_REQUEST_NEW_GROUP_CHANNEL = 302;
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";


    Fragment active;
    final FragmentManager fm = getSupportFragmentManager();
    Fragment newHomeFragments, myLocationFragments, chatList, myProfile, myNotification, findMyDeal, myLike, createdFileter, myUploads, settings, requestCallListFragment;
    public static String isActivatedFrag = "Home";


    private BroadcastReceiver mReceiver;
    private static final String[] MANDATORY_PERMISSIONS = {
            Manifest.permission.RECORD_AUDIO,   // for VoiceCall and VideoCall
            Manifest.permission.CAMERA          // for VideoCall
    };

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private boolean isFeedCall = false;

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
//        //start service
        Intent stickyService = new Intent(this, StickyService.class);
        startService(stickyService);
        //status bar color set
//        setStatusBarGradiant(this);
        mUserResponse = UserPreference.getInstance(mBaseAppCompatActivity).getUserDetail();
        callPUTDataMethod();
        registerReceiver();
        initWegites();
        setupTabLayout();


        //set drawer click listener open or close drawer
        drawer_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer(GravityCompat.START);
                //call epi for counst
//                if(!BuildConfig.DEBUG){
                getMeetingCounts(true);
//                }

            }
        });
        msearView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txt_title_toolbar.setVisibility(View.GONE);
                image6_toolbar.setVisibility(View.GONE);
                image5_toolbar.setVisibility(View.GONE);
                PropertyList.showHideViw(false);
            }
        });

        //notification redirect screen
        Bundle mBundle = getIntent().getExtras();
        if (mBundle != null) {
            String mPushVaules = mBundle.getString(Constants.EXTRA.TYPE_PUSH);
            if (mPushVaules != null && !mPushVaules.equalsIgnoreCase("")) {
                if (mPushVaules.equalsIgnoreCase("push")) {
                    //String mNID = mBundle.getString(Constants.EXTRA.NOTIFICATION_ID);
                    displayView(2);
                } else if (mPushVaules.equalsIgnoreCase("admin")) {
                    displayView(3);
                } else if (mPushVaules.equalsIgnoreCase("profile_edit")) {
                    displayView(1);
                } else if (mPushVaules.equalsIgnoreCase("Poke")) {
                    String extraChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
                    Intent mainIntent = new Intent(MainActivity.this, GroupChatActivity.class);
                    mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
                    startActivity(mainIntent);
                } else if (mPushVaules.equalsIgnoreCase("new_bidd")) {
                    //show bidd list
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);

                } else if (mPushVaules.equalsIgnoreCase("add_rate")) {
                    //User Details
                    startActivity(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), PropertyOtherUserDetailsActivity.class)
                            .putExtra(PropertyOtherUserDetailsActivity.USER_ID, BaseApplication.getInstance().getLoginID()));
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("doc_view")) {
                    //Document List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("property_view")) {
                    //Property Details List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("property_trending")) {
                    //Document List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("request_data_folder")) {
                    //Document Upload Screen
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), UploadFiles_Activity_List.class)
                            .putExtra("post_type", "property")
                            .putExtra("post_id", mCommnId)
                            .putExtra("user_id", ownerId), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("bidd_accept")) {
                    //request contract screen
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    startActivityIfNeeded(new Intent(MainActivity.this, RequestContractActivity.class)
                            .putExtra("p_id", mCommnId).putExtra("p_type", Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("call_view")) {
                    //request call
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), RequestCallListActivity.class)
                            .putExtra("post_type", "property")
                            .putExtra("post_id", mCommnId)
                            .putExtra("user_id", ownerId), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("find_my_deal")) {
                    //access property deal
                    showDialogForBeforeUploadProperty(mBundle);
                } else if (mPushVaules.equalsIgnoreCase("Flippbidd Call")) {
                    //Open alert dialog of call
                    String propertyAddress = mBundle.getString(Constants.EXTRA.PROPERTY_ADDRESS);
                    String ownerName = mBundle.getString(Constants.EXTRA.OWNER_NAME);
                    String ownerEmail = mBundle.getString(Constants.EXTRA.OWNER_EMAIL);
                    String meetingType = mBundle.getString(Constants.EXTRA.MEETING_TYPE);
                    String pushNotiyTime = mBundle.getString(Constants.EXTRA.NOTIFICATION_TIME_TYPE);
                    if (pushNotiyTime.equalsIgnoreCase("5minago")) {
                        openCallDialog(propertyAddress, ownerEmail, ownerName, meetingType);
                    }

                } else {
                    displayView(indexeOf);
                }
            } else {
                displayView(indexeOf);
            }
        }
//        openCallDialog("1003/25 Vijaynagar Housing Society,Ahmedabad","bhavisha.hcuboditexh@gmail.com","Bhavisha Solanki","Audio Call");
        loivCreateNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewChatCreateActivity.class);
                startActivityIfNeeded(intent, INTENT_REQUEST_NEW_GROUP_CHANNEL);
            }
        });

        imageRightItemList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER);

                PropertyList propertyList = new PropertyList();
                propertyList.setArguments(bundle);

                fm.beginTransaction().add(R.id.content_frame, propertyList, "11").commit();
                fm.beginTransaction().hide(active).show(propertyList).commit();
                active = propertyList;
                isActivatedFrag = "Property List";
                showIcon(1, Constants.SELECTION_HEADER_TITLE.TITLE_PROPERTIES);
                if (UserPreference.getInstance(MainActivity.this).isFilterIntro()) {
                    showDiag_();
                }
            }
        });

        connect();
        checkPermissions();
    }


    private void openCallDialog(String propertyAddress, String ownerEmail, String ownerName, String callType) {
        String callerName;
        if (BaseApplication.getInstance().getQBLoginID().equalsIgnoreCase(ownerEmail)) {
            callerName = BaseApplication.getInstance().getUserFullName();
        } else {
            callerName = ownerName;
        }
        CommonDialogView.getInstance().showCommonDialog(MainActivity.this
                , ((BaseAppCompatActivity) mBaseAppCompatActivity).getString(R.string.direct_call_alert_popup_message, callType, ownerName, propertyAddress),
                ""
                , ((BaseAppCompatActivity) mBaseAppCompatActivity).getResources().getString(R.string.cancel)
                , ((BaseAppCompatActivity) mBaseAppCompatActivity).getResources().getString(R.string.call)
                , true, false, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        if (ownerEmail != null && !ownerEmail.equalsIgnoreCase("")) {
                            if (callType.equalsIgnoreCase("Audio Call")) {
                                //call audio
                                NewCallService.dial(MainActivity.this, callerName, ownerEmail, false);
                                PreferenceUtils.setCalleeId(ownerEmail);

                            } else if (callType.equalsIgnoreCase("Video Call")) {
                                //call video
                                NewCallService.dial(MainActivity.this, callerName, ownerEmail, true);
                                PreferenceUtils.setCalleeId(ownerEmail);
                            }
                        }
                    }

                    @Override
                    public void onDialogCancel(View view) {

                    }
                });


    }

    public String is_similar_deal = "0";
    public String propertyDealID = "0";

    public void showDialogForBeforeUploadProperty(Bundle mBundle) {

        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA.LOCATION, mBundle.getString(Constants.EXTRA.ADDRESS));
        bundle.putString(Constants.EXTRA.LAT, mBundle.getString(Constants.EXTRA.LAT));
        bundle.putString(Constants.EXTRA.LANG, mBundle.getString(Constants.EXTRA.LANG));
        bundle.putString(Constants.EXTRA.DEAL_ID, mBundle.getString(Constants.EXTRA.DEAL_ID));
        FMDBottomsheetFragment fmdBottomsheetFragment = new FMDBottomsheetFragment();
        fmdBottomsheetFragment.setArguments(bundle);
        fmdBottomsheetFragment.showNow(getSupportFragmentManager(), FMDBottomsheetFragment.class.getSimpleName());
        hideProgressDialog();

        /*String address = mBundle.getString(Constants.EXTRA.ADDRESS);
        String dealID = mBundle.getString(Constants.EXTRA.DEAL_ID);
        String lat = mBundle.getString(Constants.EXTRA.LAT);
        String lang = mBundle.getString(Constants.EXTRA.LANG);
        LatLng latLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lang));

        Dialog mDialog = new Dialog(mBaseAppCompatActivity);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.deal_confirm_dialog_ui);

        CustomTextView tvHeaderMessage, tvCancel, tvUpload;
        MaterialCheckBox materialCheckBox, similarCheckedView;

        similarCheckedView = ((MaterialCheckBox) mDialog.findViewById(R.id.similarCheckedView));
        materialCheckBox = ((MaterialCheckBox) mDialog.findViewById(R.id.checkView));
        tvHeaderMessage = ((CustomTextView) mDialog.findViewById(R.id.txt_message_header));
        tvHeaderMessage.setText(address);
        tvCancel = ((CustomTextView) mDialog.findViewById(R.id.txt_cancle));
        tvUpload = ((CustomTextView) mDialog.findViewById(R.id.txt_upload));

        materialCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_similar_deal = "0";
                    propertyDealID = "0";
                    similarCheckedView.setChecked(false);
                    tvUpload.setEnabled(true);
                    tvUpload.setClickable(true);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color));
                } else {
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color_dark_grey));
                }

            }
        });
        similarCheckedView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    is_similar_deal = "1";
                    propertyDealID = dealID;
                    materialCheckBox.setChecked(false);
                    tvUpload.setEnabled(true);
                    tvUpload.setClickable(true);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color));
                } else {
                    is_similar_deal = "0";
                    propertyDealID = "0";
                    tvUpload.setEnabled(false);
                    tvUpload.setClickable(false);
                    tvUpload.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.text_color_dark_grey));
                }

            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
            }
        });
        tvUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!is_similar_deal.equalsIgnoreCase("1")) {
                    startActivityIfNeeded(new Intent(MainActivity.this, PostNewProperty.class)
                            .putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.ADD)
                            .putExtra("is_similar_deal", is_similar_deal)
                            .putExtra("deal_id", propertyDealID)
                            .putExtra("deal_address", address)
                            .putExtra("deal_lat_lang", latLng), Constants.REQUEST_UPLOAD_FUND);
                } else {
                    startActivityIfNeeded(new Intent(MainActivity.this, PostNewProperty.class)
                            .putExtra(Constants.EXTRA.SCREEN_TYPE, Constants.SCREEN_ACTION_TYPE.ADD)
                            .putExtra("is_similar_deal", is_similar_deal)
                            .putExtra("deal_id", propertyDealID), Constants.REQUEST_UPLOAD_FUND);
                }
                mDialog.dismiss();
            }
        });
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();*/

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    private void setupTabLayout() {

        /*moMenu = mainTabBar.getMenu();
        moMenu.findItem(R.id.dashbord).setIcon(R.drawable.ic_white_view);
        moMenu.findItem(R.id.my_location).setIcon(R.drawable.ic_white_location);
        moMenu.findItem(R.id.my_chat).setIcon(R.drawable.ic_inbox);
        moMenu.findItem(R.id.my_upload).setIcon(R.drawable.ic_white_cloud_upload);*/

        moMenu = mainTabBar.getMenu();
        moMenu.findItem(R.id.dashbord).setIcon(R.drawable.ic_white_view);
        moMenu.findItem(R.id.my_calendar).setIcon(R.drawable.ic_calendar);
        moMenu.findItem(R.id.my_upload).setIcon(R.drawable.ic_white_cloud_upload);
        moMenu.findItem(R.id.my_info).setIcon(R.drawable.portalblack);

        //define fragment
        newHomeFragments = new new_HomeFragments();//NewHomeFragments //new_HomeFragments
        //myLocationFragments = new MyLocationFragments();
        //requestCallListFragment = new RequestCallListFragment();
        chatList = GroupChannelListFragment.newInstance();
        active = newHomeFragments;

        fm.beginTransaction().add(R.id.content_frame, newHomeFragments, "0").commit();
        //fm.beginTransaction().add(R.id.content_frame, myLocationFragments, "1").hide(myLocationFragments).commit();
        //fm.beginTransaction().add(R.id.content_frame, requestCallListFragment, "1").hide(requestCallListFragment).commit();
        fm.beginTransaction().add(R.id.content_frame, chatList, "2").hide(chatList).commit();
        showIcon(3, "Properties");


        mainTabBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.dashbord:
                        lastSelectedIndex = 0;
                        fm.beginTransaction().hide(active).show(newHomeFragments).commit();
                        active = newHomeFragments;
                        isActivatedFrag = "Home";
                        showIcon(3, "Home");
                        return true;
                    case R.id.my_calendar:
                        lastSelectedIndex = 1;
                        /*fm.beginTransaction().hide(active).show(requestCallListFragment).commit();
                        //active = myLocationFragments;
                        active = requestCallListFragment;
                        isActivatedFrag = "My Calendar";
                        showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_MY_CALENDAR);*/
                        startActivity(new Intent(MainActivity.this, RequestCallListActivity.class), true);
                        return true;
//                    case R.id.my_chat:
//                        removeBadge();
//                        lastSelectedIndex = 2;
//                        fm.beginTransaction().hide(active).show(chatList).commit();
//                        active = chatList;
//                        isActivatedFrag = "Chat List";
//                        showIcon(9, Constants.SELECTION_HEADER_TITLE.TITLE_INBOX);
//                        return true;
                    case R.id.my_upload:
                        lastSelectedIndex = 2;
                        CommonData mPropertyData = new CommonData();
                        ((BaseAppCompatActivity) mBaseAppCompatActivity)
                                .startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity
                                        , mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), Constants.REQUEST_UPLOAD_FUND
                                        , true);
                        return true;


                    case R.id.my_info:
                        lastSelectedIndex = 3;
                        startActivity(new Intent(MainActivity.this, InfoActivity.class));
                        return true;

                }
                return false;
            }
        });

    }

    private void setCurrentTabFragment(int tabPosition) {
        switch (tabPosition) {
            case 0:
                lastSelectedIndex = 0;
                displayView(0);
                break;
            case 1:
                lastSelectedIndex = 1;
                //Home
                mFragment = new MyLocationFragments();
                showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_MY_LOCATION);
                mainTabBar.setVisibility(View.VISIBLE);
                pushFragments(mFragment);
                break;
            case 2:
                //my chat
                lastSelectedIndex = 2;
                showIcon(9, Constants.SELECTION_HEADER_TITLE.TITLE_INBOX);
                mFragment = GroupChannelListFragment.newInstance();
                pushFragments(mFragment);
                break;
            case 3:
                CommonData mPropertyData = new CommonData();
                mBaseAppCompatActivity.startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), Constants.REQUEST_UPLOAD_FUND, true);
                break;
        }
    }

    public static void setStatusBarGradiant(Activity activity) {

        Window window = activity.getWindow();
        Drawable background = activity.getResources().getDrawable(R.drawable.ab_gradient);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setNavigationBarColor(activity.getResources().getColor(android.R.color.transparent));
        window.setBackgroundDrawable(background);
    }


    @Override
    protected int getLayoutResource() {
        return R.layout.activity_main;
    }

    @Override
    protected void onPermissionGranted() {

    }

    @Override
    protected void onLocationEnable() {
    }

    private void initWegites() {
        if (PreferenceUtils.getUserId() != null) {
            SyncManagerUtils.setup(MainActivity.this, PreferenceUtils.getUserId(), new CompletionHandler() {
                @Override
                public void onCompleted(SendBirdException e) {
                    if (e != null) {
                        //Toast.makeText(SplashActivity.this, "Cannot Setup SyncManager", Toast.LENGTH_SHORT).show();
                        SendBirdSyncManager.getInstance().clearCache();
                        return;
                    }
                }
            });
        }
        //firebase analisy event add
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, mUserResponse.getLoginId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, mUserResponse.getFullName());
        BaseApplication.getFirebaseAnalyticsInstance().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        mainTabBar = findViewById(R.id.mainTabBar);
        drawer = findViewById(R.id.drawer_layoutMain);
        imageSubmitProperty = findViewById(R.id.imageSubmitProperty);
        //image2_toolbar = findViewById(R.id.image2_toolbar);
        image3_toolbar = findViewById(R.id.image3_toolbar);
        image5_toolbar = findViewById(R.id.image5_toolbar);
        image6_toolbar = findViewById(R.id.image6_toolbar);
        txt_title_toolbar = findViewById(R.id.txt_title_toolbar);
        imageHeadreIcon = findViewById(R.id.imageHeadreIcon);
        imageRightOfJV = findViewById(R.id.imageRightOfJV);
        image_delete_notification = findViewById(R.id.image_delete_notification);
        image_edit = findViewById(R.id.image_edit);
        viewRight_delete_All_notification = findViewById(R.id.viewRight_delete_All_notification);
        viewRight_4 = findViewById(R.id.viewRight_4);
        viewRight_1 = findViewById(R.id.viewRight_1);
        viewRight_2 = findViewById(R.id.viewRight_2);
        viewRight_3 = findViewById(R.id.viewRight_3);
        msearView = findViewById(R.id.image4_toolbar);
        imageRightItemList = findViewById(R.id.imageRightItemList);

        //set font style search view
        AutoCompleteTextView hEditText = (AutoCompleteTextView) msearView.findViewById(androidx.appcompat.R.id.search_src_text);
        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/Poppins-Regular.ttf");
        hEditText.setTypeface(myCustomFont);

        //create chat new option
        loivCreateNewChat = findViewById(R.id.ivCreateNewChat);

        //id of image drawer in toolbar
        drawer_icon.setVisibility(View.VISIBLE);
        //id of linear layyout
        len_drawer_view = findViewById(R.id.len_drawer_view);
        //fragments define of drawer
        fragmentDrawer = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        fragmentDrawer.setUp(R.id.fragment_navigation_drawer, drawer, main_toolbar, len_drawer_view);
        fragmentDrawer.setDrawerListener(this);

        if (UserPreference.getInstance(MainActivity.this).isMenuItemIntro()) {
          showDiag();
         Log.d("SHOW DIA","SHOW");
        } else {
         // showAppViewDialog();
            Log.d("SHOW DIA2","SHOW");
        }

        if (mUserResponse.getIsFindMyDealNoti() != null) {
            if (mUserResponse.getIsFindMyDealNoti() == 1) {
                PreferenceUtils.setMyDealNotifications(true);
            } else {
                PreferenceUtils.setMyDealNotifications(false);
            }
        }

        if (mUserResponse.getIsPropertyViewNoti() != null) {
            if (mUserResponse.getIsPropertyViewNoti() == 1) {
                PreferenceUtils.setViewNotifications(true);
            } else {
                PreferenceUtils.setViewNotifications(false);
            }
        }

        if (mUserResponse.getIsPropertyUploadNoti() != null) {
            if (mUserResponse.getIsPropertyUploadNoti() == 1) {
                PreferenceUtils.setUploadPropertyNotifications(true);
            } else {
                PreferenceUtils.setUploadPropertyNotifications(false);
            }
        }
    }

    private void displayView(int position) {
        secondlastSelectedDrawerIndex = lastSelectedDrawerIndex;
        lastSelectedDrawerIndex = position;
        switch (position) {
            case 0:
                //Home
                mainTabBar.setVisibility(View.VISIBLE);
                mainTabBar.setSelectedItemId(R.id.dashbord);
                fm.beginTransaction().hide(active).show(newHomeFragments).commit();
                active = newHomeFragments;
                isActivatedFrag = "Home";
                showIcon(3, "Home");
                ((new_HomeFragments) newHomeFragments).refresh(false);
                break;
            case 1:
                //My Profile
                myProfile = new MyProfile();
                fm.beginTransaction().add(R.id.content_frame, myProfile, "3").commit();
                fm.beginTransaction().hide(active).show(myProfile).commit();
                active = myProfile;
                isActivatedFrag = "My Profile";
                showIcon(7, Constants.SELECTION_HEADER_TITLE.TITLE_MYPROFILE);
                mainTabBar.setVisibility(View.GONE);
//                pushFragments(mFragment);
                break;
            case 2:
                //My Calendar
                startActivity(new Intent(MainActivity.this, RequestCallListActivity.class), true);
                break;
            case 3:
                //Find My Deal
                findMyDeal = new FindMyDealFragment();
                fm.beginTransaction().add(R.id.content_frame, findMyDeal, "4").commit();
                fm.beginTransaction().hide(active).show(findMyDeal).commit();
                active = findMyDeal;
                isActivatedFrag = "Find My Deal";
                showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_FIND_MY_DEAL);
                mainTabBar.setVisibility(View.GONE);
                break;
            case 4:
                //new code
                myUploads = new MyUploads();
                fm.beginTransaction().add(R.id.content_frame, myUploads, "8").commit();
                fm.beginTransaction().hide(active).show(myUploads).commit();
                active = myUploads;
                isActivatedFrag = "My Upload";
                break;


            case 5:
                //Notification
                myNotification = new Notification();
                fm.beginTransaction().add(R.id.content_frame, myNotification, "5").commit();
                fm.beginTransaction().hide(active).show(myNotification).commit();
                active = myNotification;
                isActivatedFrag = "Flippbidd Alerts";
                showIcon(8, Constants.SELECTION_HEADER_TITLE.TITLE_NOTIFICATION);
                mainTabBar.setVisibility(View.GONE);
//                pushFragments(mFragment);
                break;
            case 6:
                //My Likes
                myLike = new MyLikes();
                fm.beginTransaction().add(R.id.content_frame, myLike, "6").commit();
                fm.beginTransaction().hide(active).show(myLike).commit();
                active = myLike;
                isActivatedFrag = "My Like";
                showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_MY_LIKES);
                mainTabBar.setVisibility(View.GONE);
//                pushFragments(mFragment);
                break;
            case 7:
                //Created Filter
                createdFileter = new CreatedFileter();
                fm.beginTransaction().add(R.id.content_frame, createdFileter, "7").commit();
                fm.beginTransaction().hide(active).show(createdFileter).commit();
                active = createdFileter;
                isActivatedFrag = "Created Filter";
                showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_NOTIFICATION_FILTER);
                mainTabBar.setVisibility(View.GONE);
                break;

            case 8:
                //Setting
                settings = new settings();
                fm.beginTransaction().add(R.id.content_frame, settings, "9").commit();
                fm.beginTransaction().hide(active).show(settings).commit();
                active = settings;
                isActivatedFrag = "Settings";
                showIcon(6, Constants.SELECTION_HEADER_TITLE.TITLE_SETTING);
                mainTabBar.setVisibility(View.GONE);
//                pushFragments(mFragment);
                break;
            case 9:
                //Logout
                LogoutApp();
                break;
        }
    }

    public void pushFragments(Fragment fragment) {
        mBaseAppCompatActivity.replaceFragment(fragment, false);
    }

    public void LogoutApp() {
        Dialog mDialog = new Dialog(MainActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dilaog_logout_ui);
        mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    mDialog.dismiss();
                    //call connection remove

                    callLogoutApi();


                } catch (Exception e) {
                    //Disconnect Chat
                    disconnect();
                }
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

    private void callLogoutApi() {

        if (!NetworkUtils.isInternetAvailable(this)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        /*token, login_id, property_id, price, is_notify*/
        showProgressBar(true);
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), UserPreference.getInstance(this).getUserDetail().getLoginId()));
        linkedHashMap.put("device_type", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));
        linkedHashMap.put("device_token", RequestBody.create(MediaType.parse("multipart/form-data"), PreferenceUtils.getPushToken()));

        UserServices userService = ApiFactory.getInstance(this).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.logout(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                AuthenticationUtils.deauthenticate(getApplicationContext(), new AuthenticationUtils.DeauthenticateHandler() {
                    @Override
                    public void onResult(boolean isSuccess) {

                        System.out.println("LOGOUT - -CALLED ");
                        //Disconnect Chat
                        disconnect();
                    }
                });
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    /**
     * Unregisters all push tokens for the current user so that they do not receive any notifications,
     * then disconnects from Sendbird.
     */
    private void disconnect() {
        SendBird.unregisterPushTokenAllForCurrentUser(new SendBird.UnregisterPushTokenHandler() {
            @Override
            public void onUnregistered(SendBirdException e) {
                if (e != null) {
                    // Error!
                    e.printStackTrace();
                    // Don't return because we still need to disconnect.
                } else {
//                    Toast.makeText(MainActivity.this, "All push tokens unregistered.", Toast.LENGTH_SHORT).show();
                }
                //remove connection
                ConnectionManager.disconnect(new SendBird.DisconnectHandler() {
                    @Override
                    public void onDisconnected() {
                        String userId = PreferenceUtils.getUserId();
                        // if you want to clear cache of specific user when disconnect, you can do like this.
                        SendBirdSyncManager.getInstance().clearCache(userId);

                        UserPreference.getInstance(getApplicationContext()).clear();

                        startActivity(new Intent(getApplicationContext(), Login.class));
                        finish(false);
                    }
                });
            }
        });
    }


    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }

    public static void showIcon(int i, String toolbarTitle) {
        mToolbarTitle = toolbarTitle;
        txt_title_toolbar.setText(toolbarTitle);
        switch (i) {
            case 1:
                viewRight_1.setVisibility(View.GONE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.VISIBLE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);
                break;
            case 2:
                viewRight_1.setVisibility(View.VISIBLE);
                //image2_toolbar.setVisibility(View.VISIBLE);//spark
                imageSubmitProperty.setVisibility(View.VISIBLE);//spark
                image3_toolbar.setVisibility(View.GONE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                break;
            case 3:
                try {
                    image6_toolbar.setVisibility(View.VISIBLE);
                    image5_toolbar.setVisibility(View.VISIBLE);
                    msearView.onActionViewCollapsed();
                    msearView.setQuery("", false);
                    msearView.clearFocus();
                } catch (Exception e) {
                }

                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.VISIBLE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                viewRight_3.setVisibility(View.GONE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                mainTabBar.setVisibility(View.VISIBLE);
                break;
            case 4:
                viewRight_1.setVisibility(View.GONE);//Temp hide for coming soon
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.VISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                break;
            case 5:
                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.GONE);//spark
                imageSubmitProperty.setVisibility(View.GONE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.VISIBLE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                break;

            case 6:
                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                viewRight_3.setVisibility(View.GONE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                break;
            case 7:
                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.VISIBLE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                viewRight_3.setVisibility(View.GONE);
                //chat create
                loivCreateNewChat.setVisibility(View.GONE);

                break;
            case 8:
                //notification view
                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.VISIBLE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                viewRight_3.setVisibility(View.GONE);

                //chat create
                loivCreateNewChat.setVisibility(View.GONE);
                break;

            case 9:
                //my likes
                viewRight_1.setVisibility(View.INVISIBLE);
                //image2_toolbar.setVisibility(View.INVISIBLE);//spark
                imageSubmitProperty.setVisibility(View.INVISIBLE);//spark
                image3_toolbar.setVisibility(View.INVISIBLE);//add
                viewRight_4.setVisibility(View.GONE);
                viewRight_delete_All_notification.setVisibility(View.GONE);

                viewRight_2.setVisibility(View.GONE);
                imageHeadreIcon.setVisibility(View.GONE);
                imageRightOfJV.setVisibility(View.GONE);
                imageRightItemList.setVisibility(View.GONE);
                txt_title_toolbar.setVisibility(View.VISIBLE);
                viewRight_3.setVisibility(View.GONE);

                //only this show for
                loivCreateNewChat.setVisibility(View.VISIBLE);
                break;
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (fragmentDrawer != null) {
            fragmentDrawer.notifiyData();
        }
    }

    @Override
    public void onBackPressed() {
        if (!isActivatedFrag.equalsIgnoreCase("Home")) {
            lastSelectedIndex = 0;
            fm.beginTransaction().hide(active).show(newHomeFragments).commit();
            active = newHomeFragments;
            isActivatedFrag = "Home";
            showIcon(3, "Home");
        } else {
            super.onBackPressed();
        }
    }

    @OnClick({R.id.imageSubmitProperty, R.id.image3_toolbar, R.id.image_edit, R.id.image6_toolbar})
    void viewClickEvent(View view) {
        switch (view.getId()) {
            case R.id.image3_toolbar:
                if (mToolbarTitle.equalsIgnoreCase(Constants.SELECTION_HEADER_TITLE.TITLE_MY_PROPERTIRS)) {
                    //Add Property
                    CommonData mPropertyData = new CommonData();
                    mBaseAppCompatActivity.startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), Constants.REQUEST_UPLOAD_FUND, true);
                } else if (mToolbarTitle.equalsIgnoreCase(Constants.SELECTION_HEADER_TITLE.TITLE_MY_RENTAL)) {
                    //Add Rentals
                    CommonData mRentalData = new CommonData();
                    mBaseAppCompatActivity.startActivity(AddNewRental.getIntentActivity(mBaseAppCompatActivity, mRentalData, Constants.SCREEN_ACTION_TYPE.ADD), true);
                } else {
                    //service add
                    ServiceListData mServiceListData = new ServiceListData();
                    mBaseAppCompatActivity.startActivity(AddNewServicer.getIntentActivity(mBaseAppCompatActivity, mServiceListData, Constants.SCREEN_ACTION_TYPE.ADD), true);
                }
                break;
            case R.id.imageSubmitProperty:
                //submit property
                CommonData mPropertyData = new CommonData();
                ((BaseAppCompatActivity) mBaseAppCompatActivity).startActivityIfNeeded(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), Constants.REQUEST_UPLOAD_FUND, true);
                break;
            case R.id.image6_toolbar:
                if (mToolbarTitle.equalsIgnoreCase(Constants.SELECTION_HEADER_TITLE.SELECTE_HOMEOWNER)) {
                    Services.showHideViw(isLinearView);
                }
                if (mToolbarTitle.equalsIgnoreCase(Constants.SELECTION_HEADER_TITLE.TITLE_PROPERTIES)) {
                    PropertyList.showHideViw(isLinearView);
                }
                if (mToolbarTitle.equalsIgnoreCase(Constants.SELECTION_HEADER_TITLE.TITLE_RENTAL)) {
                    PropertyList.showHideViw(isLinearView);
                }
                break;
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
        unregisterReceiver();
    }

    private void connect() {
        if (SendBird.getConnectionState() != SendBird.ConnectionState.OPEN) {
            ConnectionManager.connect(MainActivity.this, PreferenceUtils.getUserId(), PreferenceUtils.getNickname(), PreferenceUtils.getProfilePic(), new SendBird.ConnectHandler() {
                @Override
                public void onConnected(User user, SendBirdException e) {
                    if (e != null) {
                        e.printStackTrace();
                    } else {
                        checkExtra();
                    }
                    retrieveTasks();
                }
            });
        } else {
            checkExtra();
        }
    }

    private void callPUTDataMethod() {
        String baseUrl = "https://api-" + BaseApplication.APP_CALL + ".sendbird.com/" + BaseApplication.APP_VERSION;
        // on below line we are creating a retrofit
        // builder and passing our base url
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                // as we are sending data in json format so
                // we have to add Gson converter factory
                .addConverterFactory(GsonConverterFactory.create())
                // at last we are building our retrofit builder.
                .build();

        // below the line is to create an instance for our retrofit api class.
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);

        // passing data from our text fields to our modal class.
        ChatUser modal = new ChatUser(PreferenceUtils.getNickname());

        // calling a method to create an update and passing our modal class.
        Call<JsonElement> call = retrofitAPI.updateData(PreferenceUtils.getUserId(), modal);

        // on below line we are executing our method.
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                // this method is called when we get response from our api.
                try {
                    Log.e("TAG", "Nick name is==>" + response.body());
                } catch (Exception e) {
                    Log.e("TAG", "Nick name is >" + response.body());
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // setting text to our text view when
                // we get error response from API.
                Log.e("TAG", "Error " + t.getMessage());
            }
        });
    }

    private void checkExtra() {
        if (getIntent().hasExtra(EXTRA_GROUP_CHANNEL_URL)) {
            String extraChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
            Intent mainIntent = new Intent(MainActivity.this, GroupChatActivity.class);
            mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
            startActivity(mainIntent);
        }
    }


    /*chat code*/

    @Override
    protected String getConnectionHandlerId() {
        return "CONNECTION_HANDLER_GROUP_CHANNEL_ACTIVITY";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == INTENT_REQUEST_NEW_GROUP_CHANNEL) {
            if (resultCode == RESULT_OK) {
                // Channel successfully created
                // Enter the newly created channel.
                String newChannelUrl = data.getStringExtra(NewChatCreateActivity.EXTRA_NEW_CHANNEL_URL);
                if (newChannelUrl != null) {
                    enterGroupChannel(newChannelUrl);
                }
            } else {
                Log.d("GrChLIST", "resultCode not STATUS_OK");
            }
        } else if (requestCode == 666) {
            PropertyList.isClickBack = false;
        } else {

            if (isActivatedFrag.equalsIgnoreCase("Property List")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("10");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (isActivatedFrag.equalsIgnoreCase("Home")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("0");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (isActivatedFrag.equalsIgnoreCase("My Location")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("1");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (isActivatedFrag.equalsIgnoreCase("My Like")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("6");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (isActivatedFrag.equalsIgnoreCase("My Profile")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("3");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            } else if (isActivatedFrag.equalsIgnoreCase("Find My Deal")) {
                Fragment fragment = (Fragment) fm.findFragmentByTag("4");
                if (fragment != null) {
                    fragment.onActivityResult(requestCode, resultCode, data);
                }
            }

        }
    }

    /**
     * Enters a Group Channel with a URL.
     *
     * @param channelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String channelUrl) {
        Intent mainIntent = new Intent(this, GroupChatActivity.class);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, channelUrl);
        startActivity(mainIntent);
    }

    private void retrieveTasks() {
        Log.e("TAG", "retrieveTasks==>");
        SendBird.getTotalUnreadChannelCount(new GroupChannel.GroupChannelTotalUnreadChannelCountHandler() {
            @Override
            public void onResult(int i, SendBirdException e) {
                if (e != null) {
                    Log.e("TAG", "Counts is==>" + e.getMessage());
                    // Handle error.
                    e.printStackTrace();
                    return;
                }
                if (i == 0) {
                } else {
                 //   addBadge("" + i);
                }

            }
        });
    }

    private View notificationsBadge = null;

    /*chat counts */
    private View getBadge() {
        if (notificationsBadge != null) {
            return notificationsBadge;
        }
        BottomNavigationMenuView mbottomNavigationMenuView = (BottomNavigationMenuView) mainTabBar.getChildAt(2);
        notificationsBadge = LayoutInflater.from(this).inflate(R.layout.chat_count_view,
                mbottomNavigationMenuView, false);

        return notificationsBadge;
    }

   // deprecate from the APP  //
    private void removeBadge() {
        if (notificationsBadge != null) {
            mainTabBar.removeView(notificationsBadge);
        }
    }

    // This Functionality  is removed from the bottom Nav view //
    private void addBadge(String count) {
     //   getBadge();
        TextView textView = notificationsBadge.findViewById(R.id.notifications_badge);
        textView.setText(count);
        mainTabBar.addView(notificationsBadge);
    }

    /*end*/

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle mBundle = intent.getExtras();
        if (mBundle != null) {
            String mPushVaules = mBundle.getString(Constants.EXTRA.TYPE_PUSH);
            if (mPushVaules != null && !mPushVaules.equalsIgnoreCase("")) {
                if (mPushVaules.equalsIgnoreCase("push")) {
                    //String mNID = mBundle.getString(Constants.EXTRA.NOTIFICATION_ID);
                    displayView(2);
                } else if (mPushVaules.equalsIgnoreCase("admin")) {
                    displayView(3);
                } else if (mPushVaules.equalsIgnoreCase("profile_edit")) {
                    startActivityIfNeeded(new Intent(mBaseAppCompatActivity, ProfileEditActivity.class), 897);
                } else if (mPushVaules.equalsIgnoreCase("Poke")) {
                    String extraChannelUrl = getIntent().getStringExtra(EXTRA_GROUP_CHANNEL_URL);
                    Intent mainIntent = new Intent(MainActivity.this, GroupChatActivity.class);
                    mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
                    startActivity(mainIntent);
                } else if (mPushVaules.equalsIgnoreCase("new_bidd")) {
                    //show bidd list
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("add_rate")) {
                    //User Details
                    startActivity(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), PropertyOtherUserDetailsActivity.class)
                            .putExtra(PropertyOtherUserDetailsActivity.USER_ID, BaseApplication.getInstance().getLoginID()));
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("doc_view")) {
                    //Document List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("property_view")) {
                    //Property Details List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("property_trending")) {
                    //Document List
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(MainActivity.this, mCommnId, ownerId,
                            Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, false), 678);
                } else if (mPushVaules.equalsIgnoreCase("request_data_folder")) {
                    //Document Upload Screen
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), UploadFiles_Activity_List.class)
                            .putExtra("post_type", "property")
                            .putExtra("post_id", mCommnId)
                            .putExtra("user_id", ownerId), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("bidd_accept")) {
                    //request contract screen
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    startActivityIfNeeded(new Intent(MainActivity.this, RequestContractActivity.class)
                            .putExtra("p_id", mCommnId).putExtra("p_type", Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);

                } else if (mPushVaules.equalsIgnoreCase("call_view")) {
                    //request call
                    String mCommnId = mBundle.getString(Constants.EXTRA.DATA);
                    String ownerId = mBundle.getString(Constants.EXTRA.LOGINID);
                    startActivityIfNeeded(new Intent(((BaseAppCompatActivity) mBaseAppCompatActivity), RequestCallListActivity.class)
                            .putExtra("post_type", "property")
                            .putExtra("post_id", mCommnId)
                            .putExtra("user_id", ownerId), 678);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                } else if (mPushVaules.equalsIgnoreCase("find_my_deal")) {
                    //access property deal
                    showDialogForBeforeUploadProperty(mBundle);
                } else if (mPushVaules.equalsIgnoreCase("Flippbidd Call")) {
                    //Open alert dialog of call
                    String propertyAddress = mBundle.getString(Constants.EXTRA.PROPERTY_ADDRESS);
                    String ownerName = mBundle.getString(Constants.EXTRA.OWNER_NAME);
                    String ownerEmail = mBundle.getString(Constants.EXTRA.OWNER_EMAIL);
                    String meetingType = mBundle.getString(Constants.EXTRA.MEETING_TYPE);
                    String pushNotiyTime = mBundle.getString(Constants.EXTRA.NOTIFICATION_TIME_TYPE);
                    if (pushNotiyTime.equalsIgnoreCase("5minago")) {
                        openCallDialog(propertyAddress, ownerEmail, ownerName, meetingType);
                    }
                } else {
                    displayView(indexeOf);
                }
            } else {
                displayView(indexeOf);
            }
        }
    }

    private void showAppViewDialog() {

        final View dialogViewDashBord = View.inflate(this, R.layout.alert_dialog_ui, null);

        final Dialog dialogDashbord = new Dialog(MainActivity.this, R.style.MyAlertDialogStyle);
        dialogDashbord.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = dialogDashbord.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogDashbord.setContentView(dialogViewDashBord);

        CustomTextView txtView;
        RelativeLayout relativeMyCalendar, relativeMyMessages, relativeHomePage;

        relativeMyCalendar = dialogDashbord.findViewById(R.id.relativeMyCalendar);
        relativeMyMessages = dialogDashbord.findViewById(R.id.relativeMyMessages);
        relativeHomePage = dialogDashbord.findViewById(R.id.relativeHomePage);
        txtView = dialogDashbord.findViewById(R.id.tvTitle);

        txtView.setText("" + getTimeZone() + " " + mUserResponse.getFullName());

        relativeMyCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //list property
                /*Bundle bundle = new Bundle();
                bundle.putString("screen", Constants.SCREEN_SELCTION_NAME.SELECTE_BUYER);

                PropertyList propertyList = new PropertyList();
                propertyList.setArguments(bundle);

                fm.beginTransaction().add(R.id.content_frame, propertyList, "10").commit();
                fm.beginTransaction().hide(active).show(propertyList).commit();
                active = propertyList;
                isActivatedFrag = "Property List";
                showIcon(1, Constants.SELECTION_HEADER_TITLE.TITLE_PROPERTIES);
                if (UserPreference.getInstance(MainActivity.this).isFilterIntro()) {
                    showDiag_();
                }*/
                startActivity(new Intent(MainActivity.this, RequestCallListActivity.class), true);
                dialogDashbord.dismiss();
            }
        });

        relativeMyMessages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*CommonData mPropertyData = new CommonData();
                mBaseAppCompatActivity.startActivity(PostNewProperty.getIntentActivity(mBaseAppCompatActivity, mPropertyData, Constants.SCREEN_ACTION_TYPE.ADD), true);*/

                 // this badge is removed///
               // removeBadge();
                lastSelectedIndex = 2;
                fm.beginTransaction().hide(active).show(chatList).commit();
                active = chatList;
                isActivatedFrag = "Chat List";
                showIcon(9, Constants.SELECTION_HEADER_TITLE.TITLE_INBOX);

                dialogDashbord.dismiss();
            }
        });

        relativeHomePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDashbord.dismiss();
            }
        });

        dialogDashbord.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogDashbord.show();
    }

    private String getTimeZone() {
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
        String dayString = "Good Morning";
        if (timeOfDay >= 0 && timeOfDay < 12) {
            dayString = "Good Morning";
        } else if (timeOfDay >= 12 && timeOfDay < 16) {
            dayString = "Good Afternoon";
        } else if (timeOfDay >= 16 && timeOfDay < 24) {
            dayString = "Good Evening";
        }
        return dayString;
    }


    private void showDiag() {


        final View dialogViewDashBord = View.inflate(this, R.layout.info_dialog, null);

        final Dialog dialogDashbord = new Dialog(this, R.style.MyAlertDialogStyle);
        dialogDashbord.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogDashbord.setContentView(dialogViewDashBord);

        ImageView imageView = (ImageView) dialogDashbord.findViewById(R.id.closeDialogImg);
        ImageView imageMyUploadView = (ImageView) dialogDashbord.findViewById(R.id.imageMyUploadView);
        CustomTextView txtTitle = (CustomTextView) dialogDashbord.findViewById(R.id.txtTitle);
        txtTitle.setText(getResources().getString(R.string.intro_2));
        CustomTextView txtGOTIT = (CustomTextView) dialogDashbord.findViewById(R.id.txtGOTIT);
        RelativeLayout relativeMenuItem = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeMenuItem);
        relativeMenuItem.setVisibility(View.VISIBLE);

        CustomTextView txtHeaderWelcome = (CustomTextView) dialogDashbord.findViewById(R.id.txtHeaderWelcome);
        RelativeLayout relativeList = (RelativeLayout) dialogDashbord.findViewById(R.id.relativePropertyList);
        RelativeLayout relativeMapView = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeMapView);
        RelativeLayout relativeMyMapView = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeMyMapView);
        RelativeLayout relativeChatView = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeChatView);
        RelativeLayout relativeuploadView = (RelativeLayout) dialogDashbord.findViewById(R.id.relativeUploadView);

        UserPreference.getInstance(MainActivity.this).setIsMenuItemIntro(false);

        txtGOTIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UserPreference.getInstance(MainActivity.this).isPropertyListIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsPropertyListIntro(false);
                    txtHeaderWelcome.setVisibility(View.GONE);
                    relativeMenuItem.setVisibility(View.GONE);
                    relativeList.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_1));
                } else if (UserPreference.getInstance(MainActivity.this).isMapViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsMapViewIntro(false);
                    relativeList.setVisibility(View.GONE);
                    relativeMapView.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_3));
                } else if (UserPreference.getInstance(MainActivity.this).isMyMapViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsMyMapViewIntro(false);
                    relativeMapView.setVisibility(View.GONE);
                    relativeMyMapView.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_4));
                } else if (UserPreference.getInstance(MainActivity.this).isChatViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsChatViewIntro(false);
                    relativeMyMapView.setVisibility(View.GONE);
                    relativeChatView.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_5));
                } else if (UserPreference.getInstance(MainActivity.this).isUploadViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsUploadViewIntro(false);
                    relativeChatView.setVisibility(View.GONE);
                    relativeuploadView.setVisibility(View.VISIBLE);
                    txtTitle.setText(getResources().getString(R.string.intro_6));
                } else {
                    revealShow(dialogViewDashBord, false, dialogDashbord);
                  //  showAppViewDialog();
                }

                /*else if (UserPreference.getInstance(MainActivity.this).isFindMyDealViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsFindMyDealViewIntro(false);
                    txtHeaderWelcome.setVisibility(View.GONE);
                    relativeMenuItem.setVisibility(View.VISIBLE);
                    relativeList.setVisibility(View.GONE);
                    txtTitle.setText(getResources().getString(R.string.intro_12));
                }else if (UserPreference.getInstance(MainActivity.this).isSettingsViewIntro()) {
                    UserPreference.getInstance(MainActivity.this).setIsSettingsViewIntro(false);
                    txtHeaderWelcome.setVisibility(View.GONE);
                    relativeMenuItem.setVisibility(View.VISIBLE);
                    relativeList.setVisibility(View.GONE);
                    txtTitle.setText(getResources().getString(R.string.intro_13));
                }*/
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

        int cx = (int) (imageRightItemList.getX() + (imageRightItemList.getWidth() / 2));
        int cy = (int) (imageRightItemList.getY()) + imageRightItemList.getHeight() + 56;


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

    private void showDiag_() {
        final View dialogView = View.inflate(this, R.layout.intro_dialog, null);

        final Dialog dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(dialogView);

        ImageView imageView = (ImageView) dialog.findViewById(R.id.closeDialogImg);
        CustomTextView txtTitle = (CustomTextView) dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(getResources().getString(R.string.intro_7));
        CustomTextView txtGOTIT = (CustomTextView) dialog.findViewById(R.id.txtGOTIT);
        UserPreference.getInstance(MainActivity.this).setIsFilterIntro(false);

        txtGOTIT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealShow(dialogView, false, dialog);
            }
        });

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                revealShow(dialogView, true, null);
            }
        });

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    revealShow(dialogView, false, dialog);
                    return true;
                }

                return false;
            }
        });


        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialog.show();
    }

    @Override
    public void onFragmentInteraction(String messageCounts) {

    }


    private void getVersionUpdate() {
//{"success":true,"text":"Version Details","android":"2.3","ios":"1.2"}
        UserServices userService = ApiFactory.getInstance(MainActivity.this).provideService(UserServices.class);
        Observable<JsonElement> observable;
        observable = userService.getVersionData();
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                JsonObject jsonObject = response.getAsJsonObject();
                if (!BuildConfig.VERSION_NAME.equalsIgnoreCase(jsonObject.get("android").getAsString())) {
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        // Do something for lollipop and above versions
                        startActivitybottomToTop(new Intent(MainActivity.this, AppUpdateActivity.class)
                                .putExtra("latest_version", jsonObject.get("android").getAsString()), true);
                    } else {
                        // do something for phones running an SDK before lollipop
                        startActivity(new Intent(MainActivity.this, AppUpdateActivity.class)
                                .putExtra("latest_version", jsonObject.get("android").getAsString()), true);
                    }
                } else {
                    //check feedback
                    if (!isFeedCall) {
                        //single time call
                        isFeedCall = true;
                        checkPendingFeedback();
                    }
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    private void getMeetingCounts(boolean isCallAgain) {
        UserServices userService = ApiFactory.getInstance(MainActivity.this).provideService(UserServices.class);

        LinkedHashMap<String, RequestBody> meetingCountsRequest = new LinkedHashMap<String, RequestBody>();
        meetingCountsRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        meetingCountsRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));


        Observable<JsonElement> observable;
        observable = userService.getMeetingCounts(meetingCountsRequest);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                int counts;
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    counts = mJsonObject.get("data").getAsInt();
                } else {
                    counts = 0;
                }
                //update event count
                Intent intent = new Intent(Constants.UPDATE_COUNTS);
                intent.putExtra(Constants.COUNTS_DATA, "" + counts);
                LocalBroadcastManager.getInstance(MainActivity.this).sendBroadcast(intent);

                PreferenceUtils.setMettingCounts(String.valueOf(counts));
                //check version update
                if (!isCallAgain) {
                    getVersionUpdate();
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });


    }
    /*call*/

    private void registerReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] registerReceiver()");

        if (mReceiver != null) {
            return;
        }

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i(BaseApplication.TAG, "[MainActivity] onReceive()");

                DirectCallLog callLog = (DirectCallLog) intent.getSerializableExtra(BroadcastUtils.INTENT_EXTRA_CALL_LOG);
                if (callLog != null) {

                }
            }
        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadcastUtils.INTENT_ACTION_ADD_CALL_LOG);
        registerReceiver(mReceiver, intentFilter);
    }

    private void unregisterReceiver() {
        Log.i(BaseApplication.TAG, "[MainActivity] unregisterReceiver()");

        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    private void checkPermissions() {
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                deniedPermissions.add(permission);
            }
        }

        if (deniedPermissions.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(deniedPermissions.toArray(new String[0]), REQUEST_PERMISSIONS_REQUEST_CODE);
            } else {
                ToastUtils.shortToast("Permission denied.");
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            boolean allowed = true;

            for (int result : grantResults) {
                allowed = allowed && (result == PackageManager.PERMISSION_GRANTED);
            }

            if (!allowed) {
                ToastUtils.shortToast("Permission denied.");
                checkPermissions();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void checkPendingFeedback() {
        //token, meeting_id, login_id, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(MainActivity.this).provideService(UserServices.class);
        Observable<PendingFeedback> observable;
        observable = userService.pendingRequestFeedback(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<PendingFeedback>() {
            @Override
            public void onSuccess(PendingFeedback response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //open feedback activity
                    startActivitybottomToTop(new Intent(MainActivity.this, FeedBackActivity.class).putExtra("data", response.getData().get(0)), true);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }


    @Override
    public void onFragmentCreated() {
        Log.e("TAG", "onFragmentCreated()");
        //check pending feedback
//        if(!BuildConfig.DEBUG){
        getMeetingCounts(false);
//        }
    }
}
