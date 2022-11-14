package com.flippbidd.sendbirdSdk.groupchannel;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.Bottoms.ChatBottomSheetDialogFragment;
import com.flippbidd.Bottoms.CommonListBottomSheet;
import com.flippbidd.Bottoms.MeetingScheduleSheet;
import com.flippbidd.BuildConfig;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.Others.RealPathUtil;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.PropertyOtherUserDetailsActivity;
import com.flippbidd.activity.inapppurchase.InAppPurchaseActivity;
import com.flippbidd.activity.support.SupportActivity;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.FileUtils;
import com.flippbidd.utils.MediaPlayerActivity;
import com.flippbidd.utils.PhotoViewerActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.TextUtils;
import com.flippbidd.utils.ToastUtils;
import com.flippbidd.utils.UrlPreviewInfo;
import com.flippbidd.utils.WebUtils;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.AdminMessage;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.BaseMessage;
import com.sendbird.android.FileMessage;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.UserMessage;
import com.sendbird.syncmanager.FailedMessageEventActionReason;
import com.sendbird.syncmanager.MessageCollection;
import com.sendbird.syncmanager.MessageEventAction;
import com.sendbird.syncmanager.MessageFilter;
import com.sendbird.syncmanager.handler.CompletionHandler;
import com.sendbird.syncmanager.handler.FetchCompletionHandler;
import com.sendbird.syncmanager.handler.MessageCollectionCreateHandler;
import com.sendbird.syncmanager.handler.MessageCollectionHandler;

import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import id.zelory.compressor.Compressor;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

import static android.os.Build.VERSION.SDK_INT;

public class GroupChatActivity extends BaseActivity implements BSImagePicker.OnSingleImageSelectedListener {

    private static final String LOG_TAG = GroupChatActivity.class.getSimpleName();

    Activity moActivity;

    private static final String CONNECTION_HANDLER_ID = "CONNECTION_HANDLER_GROUP_CHAT";
    private static final String CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT";

    private static final int STATE_NORMAL = 0;
    private static final int STATE_EDIT = 1;

    private static final String STATE_CHANNEL_URL = "STATE_CHANNEL_URL";
    private static final int INTENT_REQUEST_CHOOSE_MEDIA = 301;
    private static final int INTENT_REQUEST_CHOOSE_DOCUMENT = 303;
    private static final int INTENT_REQUEST_UPDATE_NAME = 302;
    private static final int PERMISSION_WRITE_EXTERNAL_STORAGE = 13;
    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    static final String EXTRA_CHANNEL = "EXTRA_CHANNEL";
    static final String EXTRA_MAIN_ID = "EXTRA_MAIN_ID";
    static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";
    final ChatBottomSheetDialogFragment chatBottomSheetDialogFragment = ChatBottomSheetDialogFragment.newInstance();

    private InputMethodManager mIMM;
    private CoordinatorLayout mCoordinatorMainView;
    private CollapsingToolbarLayout mToolbarLayout;
    private Toolbar mToolbarGroupChannel;
    private ImageView mIvChatBackView;
    private RelativeLayout mRootLayout;
    private RecyclerView mRecyclerView;
    private GroupChatAdapter mChatAdapter;
    private LinearLayoutManager mLayoutManager;
    private EditText mMessageEditText;
    private ImageView mMessageSendButton;
    private ImageButton mUploadFileButton;
    private View mCurrentEventLayout;
    private TextView mCurrentEventText;
    private TextView mNewMessageText;
    private CustomTextView mTvChatPropertyName;
    private CustomTextView mTvChatAddress;
    private CustomTextView mTvProvideBY;
    private CustomTextView mTvInviteUser;
    private LinearLayout linearAddressBox;
    private AppBarLayout mAppBarLayout;
    private ImageView mIvGroupInfo, ivMeetingSchedule;
    private CircleImageView mIvCoverImage;
    private CircleImageView mOtherUserProfile;

    private GroupChannel mChannel;
    private String mChannelUrl;
    private String mChannelMainIdUrl = "";
    private String mChannelPropertyId = "";
    private String mChannelPropertyOwenrEmail = "";
    private String mChannelPropertyOwenrName = "";
    List<String> mNormalList = new ArrayList<>();
    private int mCurrentState = STATE_NORMAL;
    private BaseMessage mEditingMessage = null;

    private CommonData mCommonDetailsData = null;

    final MessageFilter mMessageFilter = new MessageFilter(BaseChannel.MessageTypeFilter.ALL, null, null);
    private MessageCollection mMessageCollection;

    private long mLastRead;
    private String chatStr = "Chat", callType = "Audio Call";
    private String ownerEmail = "", requestEmail = "";
    Disposable disposable;

    private String ACTION_VIEW = "ACTION_VIEW", ACTION_PROFILE = "ACTION_PROFILE";//new feature

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        this.moActivity = GroupChatActivity.this;

        mIMM = (InputMethodManager) moActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        if (savedInstanceState != null) {
            mChannelUrl = savedInstanceState.getString(STATE_CHANNEL_URL);
        } else {
            if (getIntent().getExtras() != null) {
                mChannelUrl = getIntent().getExtras().getString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_URL);

                if (getIntent().hasExtra(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_MAIN_ID)) {
                    mChannelMainIdUrl = getIntent().getExtras().getString(GroupChannelListFragment.EXTRA_GROUP_CHANNEL_MAIN_ID);
                }
                if (getIntent().hasExtra(GroupChannelListFragment.EXTRA_PROPERTY_ID)) {
                    mChannelPropertyId = getIntent().getExtras().getString(GroupChannelListFragment.EXTRA_PROPERTY_ID);
                    //get property details bg api
                    synchronized (GroupChatActivity.this) {
                        getDetailsProperty();
                    }
                }
                if (getIntent().hasExtra(GroupChannelListFragment.EXTRA_OWNER_EMAIL)) {
                    mChannelPropertyOwenrEmail = getIntent().getExtras().getString(GroupChannelListFragment.EXTRA_OWNER_EMAIL);
                }

            }
        }

        Log.d(LOG_TAG, mChannelUrl);
        mLastRead = PreferenceUtils.getLastRead(mChannelUrl);
        mChatAdapter = new GroupChatAdapter(moActivity);
        setUpChatListAdapter();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mIvCoverImage = (CircleImageView) findViewById(R.id.ivCoverImage);
        mOtherUserProfile = (CircleImageView) findViewById(R.id.otherUserProfile);
        mCoordinatorMainView = (CoordinatorLayout) findViewById(R.id.coordinatorMainView);
        mToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        mToolbarGroupChannel = (Toolbar) findViewById(R.id.toolbar_group_channel);
        mIvChatBackView = (ImageView) findViewById(R.id.ivChatBackView);
        mRootLayout = (RelativeLayout) findViewById(R.id.layout_group_chat_root);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_group_chat);

        mCurrentEventLayout = findViewById(R.id.layout_group_chat_current_event);
        mCurrentEventText = (TextView) findViewById(R.id.text_group_chat_current_event);

        mMessageEditText = (EditText) findViewById(R.id.edittext_group_chat_message);
        mMessageSendButton = (ImageView) findViewById(R.id.button_group_chat_send);
        mUploadFileButton = (ImageButton) findViewById(R.id.button_group_chat_upload);
        mNewMessageText = (TextView) findViewById(R.id.text_group_chat_new_message);
//        mCoordinatorMainView.getViewTreeObserver().addOnGlobalLayoutListener(keyboardLayoutListener);

        mTvChatPropertyName = (CustomTextView) findViewById(R.id.tvChatPropertyName);
        mTvChatAddress = (CustomTextView) findViewById(R.id.tvChatAddress);
        //hide and show manage some tim
        mTvProvideBY = (CustomTextView) findViewById(R.id.tvProvideBY);
        linearAddressBox = (LinearLayout) findViewById(R.id.linearAddressBox);

        //set my profile
        if (UserPreference.getInstance(moActivity).getUserDetail().getProfilePic() != null &&
                !UserPreference.getInstance(moActivity).getUserDetail().getProfilePic().equalsIgnoreCase("")) {
            Glide.with(moActivity).load(UserPreference.getInstance(moActivity).getUserDetail().getProfilePic()).into(mOtherUserProfile);
        } else {
            Glide.with(moActivity).load(R.mipmap.user).into(mOtherUserProfile);
        }

        ivMeetingSchedule = (ImageView) findViewById(R.id.ivMeetingSchedule);
//        mIvCallButton = (ImageView) findViewById(R.id.ivCallButton);
        /*call button click*/
        /*mIvCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannel != null) {
                    if (mChannel.getMembers().isEmpty()) {
                        return;
                    }
                }
                String calleeId = getMemberUID(mChannel.getMembers());
                CallService.dial(moActivity, calleeId, true);
                PreferenceUtils.setCalleeId(calleeId);

                *//*String calleeId;
                if (BaseApplication.getInstance().getQBLoginID().equalsIgnoreCase(ownerEmail)) {
                    calleeId = requestEmail;
                } else {
                    calleeId = ownerEmail;
                }
                if (callType.equalsIgnoreCase("Video Call")) {
                    //video call
                    CallService.dial(moActivity, calleeId, true);
                } else {
                    //auido call
                    CallService.dial(moActivity, calleeId, false);
                }
                PreferenceUtils.setCalleeId(calleeId);*//*
            }
        });*/
        //open group info
        mIvGroupInfo = (ImageView) findViewById(R.id.ivGroupInfo);
        mIvGroupInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(moActivity, GroupInfoActivity.class);
                intent.putExtra(EXTRA_CHANNEL, mChannel.serialize());
                intent.putExtra(EXTRA_MAIN_ID, mChannelMainIdUrl);
                intent.putExtra(EXTRA_PROPERTY_ID, mChannelPropertyId);
                intent.putExtra(EXTRA_OWNER_EMAIL, mChannelPropertyOwenrEmail);
                startActivityIfNeeded(intent, INTENT_REQUEST_UPDATE_NAME);
            }
        });

        mTvInviteUser = (CustomTextView) findViewById(R.id.tvInviteUser);
        mTvInviteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(moActivity, InviteMemberActivity.class);
                intent.putExtra(EXTRA_CHANNEL_URL, mChannelUrl);
                intent.putExtra(EXTRA_MAIN_ID, mChannelMainIdUrl);
                intent.putExtra(EXTRA_PROPERTY_ID, mChannelPropertyId);
                intent.putExtra(EXTRA_OWNER_EMAIL, mChannelPropertyOwenrEmail);
                startActivity(intent);
            }
        });

        mAppBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mToolbarLayout.setTitle(chatStr);
                    mToolbarLayout.setCollapsedTitleTextColor(moActivity.getResources().getColor(R.color.color_white));
                    isShow = true;
//                    showOption(R.id.action_info);

                } else if (isShow) {
                    mToolbarLayout.setTitle("");
                    mToolbarGroupChannel.setTitle("");
                    isShow = false;
//                    hideOption(R.id.action_info);
                }
            }
        });

        /*touch*/
        mToolbarGroupChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIMM.isActive()) {
                    mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                    if (mAppBarLayout != null) {
                        mAppBarLayout.setExpanded(true, true);
                    }
                }
            }
        });

        /*manage keyboard*/
        mMessageEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                mAppBarLayout.setExpanded(false, true);
                return false;

            }
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //check
                if (containCheck(s)) {
                    CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getResources().getString(R.string.app_name),
                            "Exchanging contact information in the user chat goes against FlippBidd Networks Terms/Conditions"
                            , ""
                            , moActivity.getResources().getString(R.string.string_ok)
                            , false, false, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    mMessageEditText.getText().clear();
                                    mMessageEditText.setText("");
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mMessageSendButton.setEnabled(true);
                } else {
                    mMessageSendButton.setEnabled(false);
                }
            }
        });

        mMessageSendButton.setEnabled(false);
        mMessageSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check if user free
                if (!PreferenceUtils.getPlanVersionStatus()) {
                    if (mChatAdapter.getMessageCounts() >= 10) {
                        //open alert
//                        openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1));
                        if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                            openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1), "Ok");
                        } else {
                            openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_2), "Upgrade");
                        }
                        return;
                    }
                }
                Log.e("TAG", "Message Counts -->" + mChatAdapter.getMessageCounts());

                if (mCurrentState == STATE_EDIT) {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() > 0 && mEditingMessage != null) {
                        editMessage(mEditingMessage, userInput);
                    }
                    setState(STATE_NORMAL, null, -1);
                } else {
                    String userInput = mMessageEditText.getText().toString();
                    if (userInput.length() == 0) {
                        return;
                    }

                    sendUserMessage(userInput);
                    mMessageEditText.setText("");

                    mRecyclerView.scrollToPosition(0);
                }
            }
        });

        mUploadFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMedia();
            }
        });
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    setTypingStatus(false);
                } else {
                    setTypingStatus(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        mNewMessageText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mNewMessageText.setVisibility(View.GONE);
                mMessageCollection.resetViewpointTimestamp(Long.MAX_VALUE);
                fetchInitialMessages();
            }
        });

        mIvChatBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        setUpRecyclerView();
        createMessageCollection(mChannelUrl);
    }

    private void openLimitExtendPlan(String s, String s1) {
        CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, "",
                s
                , "Cancel"
                , s1
                , false, true, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        if (s1.equalsIgnoreCase("Upgrade")) {
                            //open upgrade plan activity
                            Intent mIntent = new Intent(GroupChatActivity.this, InAppPurchaseActivity.class);
                            startActivity(mIntent);
                        } else {
                            //open property details screen
                            if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
                                startActivity(PropertyDetails.getIntentActivity(GroupChatActivity.this, mChannelPropertyId, mCommonDetailsData.getLoginId(), Constants.SCREEN_SELCTION_NAME.SELECTE_PROPERTY, mCommonDetailsData.isExpiriedStatus()));
                            }
                        }
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }

    /*Get Message Count*/
    private int getMessageCounts() {
        int messageCounts = 0;
        if (mMessageCollection != null) {
        }
        return messageCounts;
    }

    /*get other member*/
    private String getMemberUID(List<Member> memberList) {
        String userAdminId = "";
        String otherUserId = "";
        for (Member member : memberList) {
            if (member.getRole().getValue().equalsIgnoreCase("OPERATOR")) {
                userAdminId = member.getUserId();
            } else {
                otherUserId = member.getUserId();
            }
        }
        if (otherUserId.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {
            otherUserId = userAdminId;
        }
        return otherUserId;
    }

    private void calendarIconVisiblity(String emailAddress) {
        if (!emailAddress.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {
            ivMeetingSchedule.setVisibility(View.GONE);
            ivMeetingSchedule.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                        if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
                            openBottomSheet();
                        }
                    }
                }
            });
        } else {
            ivMeetingSchedule.setVisibility(View.INVISIBLE);
        }
    }

    /*open meeting schedule sheet*/
    private void openBottomSheet() {

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
        if (!NetworkUtils.isInternetAvailable(moActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        showProgressBar(true);

        //token, property_id, owner_id, login_id, date, start_time, end_time, instruction, type
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getLoginId()));
        documentRequest.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelPropertyId));
        documentRequest.put("date", RequestBody.create(MediaType.parse("multipart/form-data"), date));
        documentRequest.put("start_time", RequestBody.create(MediaType.parse("multipart/form-data"), startTime));
        documentRequest.put("end_time", RequestBody.create(MediaType.parse("multipart/form-data"), endTime));
        documentRequest.put("instruction", RequestBody.create(MediaType.parse("multipart/form-data"), message));
        documentRequest.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), type));
        documentRequest.put("created_type", RequestBody.create(MediaType.parse("multipart/form-data"), createdType));


        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.requestMeetingSchedule(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                showProgressBar(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    //success message
                    if (mJsonObject.has("text")) {
                        ToastUtils.longToast(mJsonObject.get("text").getAsString());
                    }
                } else {
                    //error messsage
                    ToastUtils.longToast(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    /*end*/

    /*end*/

    private boolean containCheck(CharSequence values) {
        Pattern digitPattern = Pattern.compile("\\d{6}");
        Pattern digitthree = Pattern.compile("\\d{3}");

        if (values.toString().toLowerCase().contains("call me")
                || values.toString().toLowerCase().contains("contact me")
                || values.toString().toLowerCase().contains("gmail")
                || values.toString().toLowerCase().contains("email")
                || values.toString().toLowerCase().contains("mobile")
                || values.toString().toLowerCase().contains("phone")
                || values.toString().toLowerCase().contains("@")
                || values.toString().toLowerCase().contains("outlook")
                || values.toString().toLowerCase().contains("yahoo")
                || values.toString().toLowerCase().contains(".com")) {
            return true;
        }
        Matcher mThree = digitthree.matcher(values);
        if (mThree.find()) {
            return true;
        }

        Matcher m = digitPattern.matcher(values);
        if (m.find()) {
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();

        mChatAdapter.setContext(moActivity); // Glide bug fix (java.lang.IllegalArgumentException: You cannot start a load for a destroyed activity)`

        SendBird.addConnectionHandler(CONNECTION_HANDLER_ID, new SendBird.ConnectionHandler() {
            @Override
            public void onReconnectStarted() {
            }

            @Override
            public void onReconnectSucceeded() {
                if (mMessageCollection != null) {
                    if (mLayoutManager.findFirstVisibleItemPosition() <= 0) {
                        mMessageCollection.fetchAllNextMessages(new FetchCompletionHandler() {
                            @Override
                            public void onCompleted(boolean hasMore, SendBirdException e) {
                            }
                        });
                    }

                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new FetchCompletionHandler() {
                            @Override
                            public void onCompleted(boolean hasMore, SendBirdException e) {
                            }
                        });
                    }
                }
            }

            @Override
            public void onReconnectFailed() {
            }
        });

        SendBird.addChannelHandler(CHANNEL_HANDLER_ID, new SendBird.ChannelHandler() {
            @Override
            public void onMessageReceived(BaseChannel baseChannel, BaseMessage baseMessage) {
            }

            @Override
            public void onReadReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTypingStatusUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    List<Member> typingUsers = channel.getTypingMembers();
                    displayTyping(typingUsers);
                }
            }

            @Override
            public void onDeliveryReceiptUpdated(GroupChannel channel) {
                if (channel.getUrl().equals(mChannelUrl)) {
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChannelDeleted(String channelUrl, BaseChannel.ChannelType channelType) {
                if (channelUrl.equals(mChannelUrl)) {
                    CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getResources().getString(R.string.app_name),
                            "Property administrator has deleted this chat"
                            , ""
                            , moActivity.getResources().getString(R.string.string_ok)
                            , false, false, true, false, false, new CommonDialogCallBack() {
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

        });
    }

    @Override
    public void onPause() {
        setTypingStatus(false);
        displayTyping(null);

        SendBird.removeConnectionHandler(CONNECTION_HANDLER_ID);
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        // Save messages to cache.
        if (mMessageCollection != null) {
            mMessageCollection.setCollectionHandler(null);
            mMessageCollection.remove();
        }
//        mCoordinatorMainView.getViewTreeObserver().removeOnGlobalLayoutListener(keyboardLayoutListener);
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_CHANNEL_URL, mChannelUrl);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_group_chat, menu);
        return true;
    }

   /* @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_group_chat, menu);
        super.onCreateOptionsMenu(menu);
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_group_channel_invite) {
            Intent intent = new Intent(moActivity, InviteMemberActivity.class);
            intent.putExtra(EXTRA_CHANNEL_URL, mChannelUrl);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_group_channel_view_members) {
            Intent intent = new Intent(moActivity, MemberListActivity.class);
            intent.putExtra(EXTRA_CHANNEL, mChannel.serialize());
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 696) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showBottomView(getList(mChannelPropertyOwenrEmail));
            } else {
                requestMedia();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Set this as true to restore background connection management.
        SendBird.setAutoBackgroundDetection(true);

        if (requestCode == INTENT_REQUEST_CHOOSE_MEDIA && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
            sendFileWithThumbnail(data.getData());
        } else if (requestCode == INTENT_REQUEST_CHOOSE_DOCUMENT && resultCode == Activity.RESULT_OK) {
            // If user has successfully chosen the image, show a dialog to confirm upload.
            if (data == null) {
                Log.d(LOG_TAG, "data is null!");
                return;
            }
//            Uri contentUri = data.getData();
            sendFileWithThumbnail(data.getData());
        } else if (requestCode == INTENT_REQUEST_UPDATE_NAME && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        moActivity.onBackPressed();
                    }
                }, 500);
                return;
            } else {
                String moName = data.getStringExtra("update_name");

                if (moName.equalsIgnoreCase("leave_group")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            moActivity.onBackPressed();
                        }
                    }, 500);
                    return;
                }

                mTvChatPropertyName.setText(moName);
            }
        } else if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            try {
                // See code above
                Uri takenPhotoUri = getCaptureImageOutputUri(GroupChatActivity.this, photoFileName);
                //Log.e(APP_TAG,"takenPhotoUri=>"+takenPhotoUri);
                sentCameraImage(photoFile, takenPhotoUri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == 2296 && resultCode == Activity.RESULT_OK) {
            browseDocuments();
        }
    }

    private void sentCameraImage(File mFile, Uri mImageUi) {
        if (mChannel == null) {
            return;
        }
        //check if user free
        if (!PreferenceUtils.getPlanVersionStatus()) {
            if (mChatAdapter.getMessageCounts() >= 10) {
                //open alert
//                openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1));
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1), "Ok");
                } else {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_2), "Upgrade");
                }
                return;
            }
        }
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        final String name = (String) mFile.getName();
        final String mime = (String) "image/jpg";
        final int size = (int) Integer.parseInt(String.valueOf(mFile.length() / 1024));

        BaseChannel.SendFileMessageHandler fileMessageHandler = new BaseChannel.SendFileMessageHandler() {
            @Override
            public void onSent(FileMessage fileMessage, SendBirdException e) {
                mMessageCollection.handleSendMessageResponse(fileMessage, e);
                mMessageCollection.fetchAllNextMessages(null);
                if (e != null) {
                    Log.d("MyTag", "onSent: " + moActivity);
                    if (moActivity != null) {
                        //Toast.makeText(moActivity, getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };

        // Send image with thumbnails in the specified dimensions
        FileMessage tempFileMessage = mChannel.sendFileMessage(mFile, name, mime, size, "", null, thumbnailSizes, fileMessageHandler);

        mChatAdapter.addTempFileMessageInfo(tempFileMessage, mImageUi);

        if (mMessageCollection != null) {
            mMessageCollection.appendMessage(tempFileMessage);
        }
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(moActivity);
        mLayoutManager.setReverseLayout(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mChatAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, null);
                        mNewMessageText.setVisibility(View.GONE);
                    }

                    if (mLayoutManager.findLastVisibleItemPosition() == mChatAdapter.getItemCount() - 1) {
                        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, null);
                    }

                }
            }
        });
    }

    private void setUpChatListAdapter() {
        mChatAdapter.setItemClickListener(new GroupChatAdapter.OnItemClickListener() {
            @Override
            public void onUserMessageItemClick(UserMessage message, String actionType) {
                if (actionType.equalsIgnoreCase(ACTION_VIEW)) {
                    if (mChatAdapter.isFailedMessage(message) && !mChatAdapter.isResendingMessage(message)) {
                        retryFailedMessage(message);
                        return;
                    }

                    // Message is sending. Do nothing on click event.
                    if (mChatAdapter.isTempMessage(message)) {
                        return;
                    }

                    if (message.getCustomType().equals(GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE)) {
                        try {
                            UrlPreviewInfo info = new UrlPreviewInfo(message.getData());
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(info.getUrl()));
                            startActivity(browserIntent);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return;
                    } else {
                        //check url or not
                        List<String> urls = WebUtils.extractUrls(message.getMessage());
                        if (urls.size() > 0) {
                            try {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(urls.get(0)));
                                startActivity(browserIntent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    }
                } else {
                    //photo click new feature
                    if (!mChannelPropertyId.equalsIgnoreCase("")) {

                        openPhotoClickBottomSheetView(message, null);

                    }
                }
            }

            @Override
            public void onFileMessageItemClick(FileMessage message, String actionType) {

                if (actionType.equalsIgnoreCase(ACTION_VIEW)) {

                    // Load media chooser and removeSucceededMessages the failed message from list.
                    if (mChatAdapter.isFailedMessage(message)) {
                        retryFailedMessage(message);
                        return;
                    }

                    // Message is sending. Do nothing on click event.
                    if (mChatAdapter.isTempMessage(message)) {
                        return;
                    }

                    onFileMessageClicked(message);
                } else {
                    //photo click new feature
                    if (!mChannelPropertyId.equalsIgnoreCase("")) {


                        openPhotoClickBottomSheetView(null, message);

                    }
                }
            }

            @Override
            public void onAdminMessageItemClick(AdminMessage message) {

            }
        });

        mChatAdapter.setItemLongClickListener(new GroupChatAdapter.OnItemLongClickListener() {
            @Override
            public void onUserMessageItemLongClick(final UserMessage message, int position) {
                if (mChannelPropertyOwenrEmail.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {

                    CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getResources().getString(R.string.app_name),
                            moActivity.getString(R.string.send_image_message)
                            , moActivity.getResources().getString(R.string.cancel)
                            , moActivity.getResources().getString(R.string.upload)
                            , false, false, false, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    showAddLinkDialog(message);
                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });


                 /*   new AlertDialog.Builder(moActivity)
                            .setMessage(moActivity.getString(R.string.send_image_message))
                            .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (which == DialogInterface.BUTTON_POSITIVE) {


                                    }
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).show();*/
                }
                /*if (message.getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                    showMessageOptionsDialog(message, position);
                }*/
            }

            @Override
            public void onFileMessageItemLongClick(FileMessage message) {
            }

            @Override
            public void onAdminMessageItemLongClick(AdminMessage message) {
            }

        });
    }


    private void setState(int state, BaseMessage editingMessage, final int position) {
        switch (state) {
            case STATE_NORMAL:
                mCurrentState = STATE_NORMAL;
                mEditingMessage = null;

                mUploadFileButton.setVisibility(View.VISIBLE);
//                mMessageSendButton.setText(getString(R.string.action_send_message));
                mMessageEditText.setText("");
                break;

            case STATE_EDIT:
                mCurrentState = STATE_EDIT;
                mEditingMessage = editingMessage;

                mUploadFileButton.setVisibility(View.GONE);
//                mMessageSendButton.setText(getString(R.string.action_update_message));
                String messageString = ((UserMessage) editingMessage).getMessage();
                if (messageString == null) {
                    messageString = "";
                }
                mMessageEditText.setText(messageString);
                if (messageString.length() > 0) {
                    mMessageEditText.setSelection(0, messageString.length());
                }

                mMessageEditText.requestFocus();
                mMessageEditText.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIMM.showSoftInput(mMessageEditText, 0);

                        mRecyclerView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mAppBarLayout.setExpanded(true, true);
                                mRecyclerView.scrollToPosition(position);
                            }
                        }, 500);
                    }
                }, 100);
                break;
        }
    }

    private void createMessageCollection(final String channelUrl) {
        GroupChannel.getChannel(channelUrl, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    MessageCollection.create(channelUrl, mMessageFilter, mLastRead, new MessageCollectionCreateHandler() {
                        @Override
                        public void onResult(MessageCollection messageCollection, SendBirdException e) {
                            if (e == null) {
                                if (mMessageCollection != null) {
                                    mMessageCollection.remove();
                                }

                                mMessageCollection = messageCollection;
                                mMessageCollection.setCollectionHandler(mMessageCollectionHandler);

                                mChannel = mMessageCollection.getChannel();
                                mChatAdapter.setChannel(mChannel);

                                if (moActivity == null) {
                                    return;
                                }

                                moActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatAdapter.clear();
                                        updateActionBarTitle();
                                    }
                                });

                                fetchInitialMessages();
                            } else {
                                //Toast.makeText(moActivity, getString(R.string.get_channel_failed), Toast.LENGTH_SHORT).show();
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        moActivity.onBackPressed();
                                    }
                                }, 1000);
                            }
                        }


                    });
                } else {
                    if (mMessageCollection != null) {
                        mMessageCollection.remove();
                    }

                    mMessageCollection = new MessageCollection(groupChannel, mMessageFilter, mLastRead);
                    mMessageCollection.setCollectionHandler(mMessageCollectionHandler);

                    mChannel = groupChannel;
                    mChatAdapter.setChannel(mChannel);
                    mChatAdapter.clear();
                    updateActionBarTitle();

                    fetchInitialMessages();
                }
            }
        });
    }

    private void refresh() {
        if (mChannel != null) {
            mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        e.printStackTrace();
                        return;
                    }

                    updateActionBarTitle();
                    createMessageCollection(mChannel.getUrl());
                }
            });
        } else {
            createMessageCollection(mChannelUrl);
        }
    }

    private void fetchInitialMessages() {
        if (mMessageCollection == null) {
            return;
        }

        mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.PREVIOUS, new FetchCompletionHandler() {
            @Override
            public void onCompleted(boolean hasMore, SendBirdException e) {
                mMessageCollection.fetchSucceededMessages(MessageCollection.Direction.NEXT, new FetchCompletionHandler() {
                    @Override
                    public void onCompleted(boolean hasMore, SendBirdException e) {
                        mMessageCollection.fetchFailedMessages(new CompletionHandler() {
                            @Override
                            public void onCompleted(SendBirdException e) {
                                if (moActivity == null) {
                                    return;
                                }

                                moActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mChatAdapter.markAllMessagesAsRead();
                                        mLayoutManager.scrollToPositionWithOffset(mChatAdapter.getLastReadPosition(mLastRead), mRecyclerView.getHeight() / 2);
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }

    private void retryFailedMessage(final BaseMessage message) {
        new AlertDialog.Builder(moActivity)
                .setMessage(getString(R.string.request_retry_send_message))
                .setPositiveButton(R.string.resend_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            if (message instanceof UserMessage) {
                                mChannel.resendUserMessage((UserMessage) message, new BaseChannel.ResendUserMessageHandler() {
                                    @Override
                                    public void onSent(UserMessage userMessage, SendBirdException e) {
                                        mMessageCollection.handleSendMessageResponse(userMessage, e);
                                    }
                                });
                            } else if (message instanceof FileMessage) {
                                Uri uri = mChatAdapter.getTempFileMessageUri(message);
                                sendFileWithThumbnail(uri);
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                })
                .setNegativeButton(R.string.delete_message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            if (message instanceof UserMessage) {
                                mMessageCollection.deleteMessage(message);
                            } else if (message instanceof FileMessage) {
                                mChatAdapter.removeFailedMessages(Collections.singletonList(message));
                            }
                        }
                    }
                }).show();
    }

    /**
     * Display which users are typing.
     * If more than two users are currently typing, this will state that "multiple users" are typing.
     *
     * @param typingUsers The list of currently typing users.
     */
    private void displayTyping(List<Member> typingUsers) {
        if (typingUsers != null && typingUsers.size() > 0) {
            mCurrentEventLayout.setVisibility(View.VISIBLE);
            String string;

            if (typingUsers.size() == 1) {
                string = String.format(getString(R.string.user_typing), typingUsers.get(0).getNickname());
            } else if (typingUsers.size() == 2) {
                string = String.format(getString(R.string.two_users_typing), typingUsers.get(0).getNickname(), typingUsers.get(1).getNickname());
            } else {
                string = getString(R.string.users_typing);
            }
            mCurrentEventText.setText(string);
        } else {
            mCurrentEventLayout.setVisibility(View.GONE);
        }
    }

    private void requestMedia() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GroupChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE
                        , Manifest.permission.MANAGE_EXTERNAL_STORAGE
                        , Manifest.permission.CAMERA}, 696);
            } else {
                showBottomView(getList(mChannelPropertyOwenrEmail));
            }
        } else {
            showBottomView(getList(mChannelPropertyOwenrEmail));
        }
    }

    private static final int TAKE_PICTURE = 1034;
    public final String APP_TAG = "MyFlippbiddApp";
    public String photoFileName = "photo.jpg";
    File photoFile;
    private File moFile;
    private Uri mImageUri;
    private String mCurrentPhotoPath;

    private void showBottomView(List<String> list) {
        final CommonListBottomSheet commonListBottomSheet = CommonListBottomSheet.newInstance();
        commonListBottomSheet.addListener(GroupChatActivity.this, new CommonListBottomSheet.DailogListener() {
            @Override
            public void onSaveCollection(int mPosition, String actionType) {

                switch (actionType) {
                    case "Take Photo":
                        openCamera();
                        SendBird.setAutoBackgroundDetection(false);
                        break;
                    case "Browse File":
                        if (SDK_INT >= Build.VERSION_CODES.R) {
                            ToastUtils.shortToast(GroupChatActivity.this.getResources().getString(R.string.os_message));
                            return;
                        }
                        browseDocuments();
//                        if (checkPermission_()) {
//                            browseDocuments();
//                        } else {
//                            //for android 11
//                            requestPermission();
//                        }
                        break;
                    case "Customer Support":
                        //Non-Circumvent
                        startActivity(new Intent(GroupChatActivity.this, SupportActivity.class));
//                        CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getString(R.string.chat_rule_message),
//                                ""
//                                , moActivity.getResources().getString(R.string.string_no)
//                                , moActivity.getResources().getString(R.string.string_yes)
//                                , true, false, false, false, false, new CommonDialogCallBack() {
//                                    @Override
//                                    public void onDialogYes(View view) {
////                                        String userInput = "Please do not exchange contact information on this chat unless authorized by the property administrator.  Violating this request may result in immediate termination of this discussion for all parties. - Thank You";
//                                        String userInput = "*Please be advised as per FlippBidd terms/conditions you are asked not to exchange contact information I.e. Emails, Phone Numbers until you are ready to Request Contract.  Violating these terms can result in termination of your FlippBidd subscription..*";
//                                        sendUserMessage(userInput);
//                                        mMessageEditText.setText("");
//
//                                        mRecyclerView.scrollToPosition(0);
//                                    }
//
//                                    @Override
//                                    public void onDialogCancel(View view) {
//                                    }
//                                });

                        break;
                    case "Choose from Library":
                        requestPemission();
//                        Intent intent = new Intent();
//                        intent.setType("image/*");
////                        intent.setType("vnd.android.cursor.dir/image");
//                        intent.setAction(Intent.ACTION_GET_CONTENT);
////                         Always show the chooser (if there are multiple options available)
//                        startActivityIfNeeded(Intent.createChooser(intent, "Select Media"), INTENT_REQUEST_CHOOSE_MEDIA);
//                        // Set this as false to maintain connection
//                        // even when an external Activity is started.
                        SendBird.setAutoBackgroundDetection(false);
                        break;
                    case "Cancel":
                        break;
                }

                commonListBottomSheet.dismiss();
            }
        }, list);
        commonListBottomSheet.show(getSupportFragmentManager(), CommonListBottomSheet.class.getSimpleName());
    }

    private void openCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(GroupChatActivity.this, "com.flippbidd.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, TAKE_PICTURE);
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("APP_TAG", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }

    public File getDocumentFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d("APP_TAG", "failed to create directory");
        }

        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

        return file;
    }
    /*camera*/


    /*end*/
    public static Uri getCaptureImageOutputUri(Context context, String name) {
        Uri outputFileUri = null;
        File getImage = context.getExternalCacheDir();
        if (getImage != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                outputFileUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider",
                        new File(getImage.getPath(), name));
            } else {
                outputFileUri = Uri.fromFile(new File(getImage.getPath(), name));
            }
        }
        return outputFileUri;
    }

    /*Document*/
//    private boolean checkPermission_() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            return Environment.isExternalStorageManager();
//        } else {
//            int result = ContextCompat.checkSelfPermission(GroupChatActivity.this, READ_EXTERNAL_STORAGE);
//            int result1 = ContextCompat.checkSelfPermission(GroupChatActivity.this, WRITE_EXTERNAL_STORAGE);
//            return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
//        }
//    }
//
//    private void requestPermission() {
//        if (SDK_INT >= Build.VERSION_CODES.R) {
//            try {
//                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
//                intent.addCategory("android.intent.category.DEFAULT");
//                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
//                startActivityIfNeeded(intent, 2296);
//            } catch (Exception e) {
//                Intent intent = new Intent();
//                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
//                startActivityIfNeeded(intent, 2296);
//            }
//        } else {
//            //below android 11
//            browseDocuments();
//        }
//    }
    /*end document*/


    /*open document selection*/
    private void browseDocuments() {

        String[] mimeTypes =
                {"application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint", "application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0, mimeTypesStr.length() - 1));
        }
        startActivityIfNeeded(Intent.createChooser(intent, "ChooseFile"), INTENT_REQUEST_CHOOSE_DOCUMENT);

    }

    /*1. Choose from Library
    2. Take Photo
    3. Browse File
    4. Chat Rule*/
    private List<String> getList(String userId) {
        if (mNormalList != null)
            mNormalList.clear();
        mNormalList.add("Choose from Library");
        mNormalList.add("Take Photo");
        mNormalList.add("Browse File");
        mNormalList.add("Customer Support");
//        if (!userId.equalsIgnoreCase("")) {
//            if (userId.equalsIgnoreCase(UserPreference.getInstance(moActivity).getUserDetail().getEmailAddress())) {
//                mNormalList.add("Non-Circumvent");
//            }
//        } else {
//            if (mChannel != null) {
//                if (mChannel.getMyRole().getValue().equalsIgnoreCase("none")) {
//                    Log.e("TAG", "My Role " + mChannel.getMyRole().getValue());
//                    mNormalList.add("Non-Circumvent");
//                }
//            }
//        }
        return mNormalList;
    }

    private void requestStoragePermissions() {

        Snackbar.make(findViewById(android.R.id.content), moActivity.getResources().getString(R.string.request_external_storage),
                Snackbar.LENGTH_LONG)
                .setAction(moActivity.getResources().getString(R.string.accept_permission), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                                ActivityCompat.requestPermissions(GroupChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
                            }
                        } else {
                            ActivityCompat.requestPermissions(GroupChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                                    , Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_EXTERNAL_STORAGE);
                        }
                    }
                })
                .show();
    }

    private void onFileMessageClicked(FileMessage message) {
        String type = message.getType().toLowerCase();
        if (type.startsWith("image")) {

            if (mChannelPropertyOwenrEmail.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {

                CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getString(R.string.send_image_message),
                        ""
                        , moActivity.getResources().getString(R.string.cancel)
                        , moActivity.getResources().getString(R.string.upload)
                        , true, false, false, false, false, new CommonDialogCallBack() {
                            @Override
                            public void onDialogYes(View view) {
                                showRenameDialog(message, "image");
                            }

                            @Override
                            public void onDialogCancel(View view) {
                                Intent iM = new Intent(moActivity, PhotoViewerActivity.class);
                                iM.putExtra("url", message.getUrl());
                                iM.putExtra("type", message.getType());
                                startActivity(iM);
                            }
                        });
                //upload alert
                /*new AlertDialog.Builder(moActivity)
                        .setMessage(getString(R.string.send_image_message))
                        .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (which == DialogInterface.BUTTON_POSITIVE) {
                                    showRenameDialog(message, "image");
                                }
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Intent iM = new Intent(moActivity, PhotoViewerActivity.class);
                                iM.putExtra("url", message.getUrl());
                                iM.putExtra("type", message.getType());
                                startActivity(iM);

                            }
                        }).show();*/

            } else {
                //Image Show
                Intent i = new Intent(moActivity, PhotoViewerActivity.class);
                i.putExtra("url", message.getUrl());
                i.putExtra("type", message.getType());
                startActivity(i);
            }


        } else if (type.startsWith("video")) {
            Intent intent = new Intent(moActivity, MediaPlayerActivity.class);
            intent.putExtra("url", message.getUrl());
            startActivity(intent);
        } else {
            showDownloadConfirmDialog(message);
        }
    }

    private void showDownloadConfirmDialog(final FileMessage message) {
        if (ContextCompat.checkSelfPermission(moActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // If storage permissions are not granted, request permissions at run-time,
            // as per < API 23 guidelines.
            requestStoragePermissions();
        } else {
            //check for download / upload file
            //upload doc in folder

            if (mChannelPropertyOwenrEmail.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {
                //upload alert
                CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getString(R.string.send_upload_message),
                        ""
                        , moActivity.getResources().getString(R.string.cancel)
                        , moActivity.getResources().getString(R.string.upload)
                        , true, false, false, false, false, new CommonDialogCallBack() {
                            @Override
                            public void onDialogYes(View view) {
                                showRenameDialog(message, "document");
                            }

                            @Override
                            public void onDialogCancel(View view) {
                                //download option show
                                showDownloadDialog(message);
                            }
                        });
//                new AlertDialog.Builder(moActivity)
//                        .setMessage(getString(R.string.send_upload_message))
//                        .setPositiveButton("Upload", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                //if (which == DialogInterface.BUTTON_POSITIVE) {
////                                    callUploadDocumentApi(message.getName(), message.getUrl());
//                                dialog.dismiss();
//                                showRenameDialog(message, "document");
//                                //}
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                new AlertDialog.Builder(moActivity)
//                                        .setMessage(getString(R.string.request_download_file))
//                                        .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
//                                            @Override
//                                            public void onClick(DialogInterface dialog, int which) {
//                                                if (which == DialogInterface.BUTTON_POSITIVE) {
//                                                    FileUtils.downloadFile(moActivity, message.getUrl(), message.getName());
//                                                }
//                                                dialog.dismiss();
//                                            }
//                                        })
//                                        .setNegativeButton(R.string.cancel, null).show();
//                            }
//                        }).show();

            } else {
                //download
                CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, moActivity.getString(R.string.request_download_file),
                        ""
                        , moActivity.getResources().getString(R.string.cancel)
                        , moActivity.getResources().getString(R.string.download)
                        , true, false, false, true, false, new CommonDialogCallBack() {
                            @Override
                            public void onDialogYes(View view) {
                                FileUtils.downloadFile(moActivity, message.getUrl(), message.getName());
                            }

                            @Override
                            public void onDialogCancel(View view) {
                            }
                        });
//                new AlertDialog.Builder(moActivity)
//                        .setMessage(getString(R.string.request_download_file))
//                        .setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//                                if (which == DialogInterface.BUTTON_POSITIVE) {
//                                    FileUtils.downloadFile(moActivity, message.getUrl(), message.getName());
//                                }
//                            }
//                        })
//                        .setNegativeButton(R.string.cancel, null).show();
            }
        }

    }

    private void showDownloadDialog(final FileMessage message) {
        Dialog mDialog = new Dialog(GroupChatActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = mDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        mDialog.setContentView(R.layout.dilaog_logout_ui);

        CustomTextView moTxtHeader = mDialog.findViewById(R.id.txt_message_header);
        moTxtHeader.setText(moActivity.getString(R.string.request_download_file));
        CustomTextView moTxtOkay = mDialog.findViewById(R.id.txt_okay);
        moTxtOkay.setText(moActivity.getResources().getString(R.string.download));
        CustomTextView moTxtMessage = mDialog.findViewById(R.id.txt_message);
        moTxtMessage.setVisibility(View.GONE);

        mDialog.findViewById(R.id.txt_okay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    mDialog.dismiss();
                    // use the value.
                    FileUtils.downloadFile(moActivity, message.getUrl(), message.getName());
                } catch (Exception e) {
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

    private void showAddLinkDialog(UserMessage message) {
        // Creating alert Dialog with one Button
        Dialog alertDialog = new Dialog(GroupChatActivity.this);
        alertDialog.setContentView(R.layout.upload_link_dialog_ui);
        // Setting Dialog Title
        alertDialog.setCancelable(false);
        // Setting Dialog Message

        EditText editText1 = alertDialog.findViewById(R.id.tvTypeName);
        EditText editText2 = alertDialog.findViewById(R.id.tvAddLink);

        //set text
        editText2.setText(message.getMessage());

        alertDialog.findViewById(R.id.btnPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get type name
                String typeName = editText1.getText().toString();
                String addLinkName = editText2.getText().toString();

                if (typeName.isEmpty()) {
                    editText1.setError(Constants.ENTER_DOC_NAME);
                    editText1.requestFocus();
                    return;
                } else if (addLinkName.isEmpty()) {
                    editText2.setError(Constants.ENTER_LINK);
                    editText2.requestFocus();
                    return;
                } else if (!isValid(addLinkName)) {
                    editText2.setError(Constants.ENTER_VALID_LINK);
                    editText2.requestFocus();
                    return;
                } else {
                    alertDialog.dismiss();
                    //cal api for upload document
                    callUploadDocumentApi(typeName, addLinkName, "link");
                }

            }
        });


        alertDialog.findViewById(R.id.btnNegative).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private boolean isValid(String urlString) {
        try {
            return URLUtil.isValidUrl(urlString) && Patterns.WEB_URL.matcher(urlString).matches();
        } catch (Exception e) {

        }
        return false;
    }

    private void showRenameDialog(final FileMessage message, String mType) {
        // Creating alert Dialog with one Button
        Dialog alertDialog = new Dialog(GroupChatActivity.this);
        alertDialog.setContentView(R.layout.rename_dialog_ui);
        // Setting Dialog Title
        alertDialog.setCancelable(false);
        // Setting Dialog Message

        EditText editText = alertDialog.findViewById(R.id.tvContent);
        editText.setText(message.getName());
        alertDialog.findViewById(R.id.btnPositive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String editValue = editText.getText().toString();
                if (editValue.length() != 0) {
                    alertDialog.dismiss();
                    //cal api for upload document
                    callUploadDocumentApi(editValue, message.getUrl(), mType);

                } else {
                    editText.setError("Please add name here");
                }
            }
        });
        // Showing Alert Message
        alertDialog.show();
    }

    private void callUploadDocumentApi(String mTitleValue, String addLinkVlaue, String mtype) {
        if (!NetworkUtils.isInternetAvailable(moActivity)) {
            //openErorrDialog(getString(R.string.no_internet));
            return;
        }
        //showProgressDialog(true);

        LinkedHashMap<String, RequestBody> uploadRequest = new LinkedHashMap<String, RequestBody>();
        uploadRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        uploadRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        uploadRequest.put("common_type", RequestBody.create(MediaType.parse("multipart/form-data"), "property"));
        uploadRequest.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelPropertyId));
        uploadRequest.put("file_type", RequestBody.create(MediaType.parse("multipart/form-data"), mtype));
        uploadRequest.put("title", RequestBody.create(MediaType.parse("multipart/form-data"), mTitleValue));
        uploadRequest.put("insert_type", RequestBody.create(MediaType.parse("multipart/form-data"), "url"));
        uploadRequest.put("upload_file", RequestBody.create(MediaType.parse("multipart/form-data"), addLinkVlaue));


        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable = userService.documentsUploadFile(uploadRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                //hideProgressDialog();
                if (response.getSuccess()) {
                    //show alert dialog with message
                    new AlertDialog.Builder(moActivity)
                            .setMessage(getString(R.string.request_upload_doc_success_message))
                            .setPositiveButton(R.string.string_ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).show();

                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                //hideProgressDialog();
            }
        });
    }

    private void updateActionBarTitle() {
        String addressV = "";

        if (mChannel != null) {

            if (mChannel.getName().equalsIgnoreCase("Group Channel")) {
                chatStr = TextUtils.getGroupChannelTitle(mChannel);
                addressV = "";

                linearAddressBox.setVisibility(View.GONE);
                mTvProvideBY.setVisibility(View.GONE);
            } else {
                chatStr = mChannel.getName();
                addressV = mChannel.getData();

                //split
                String st = "##";
                String[] arrayString;

                if (addressV != null && !addressV.equalsIgnoreCase("")) {
                    arrayString = addressV.split(st);
                    try {
                        if (addressV.equalsIgnoreCase("none")) {
                            //show address view
                            linearAddressBox.setVisibility(View.GONE);
                            mTvProvideBY.setVisibility(View.GONE);
                        } else {
                            //show address view
                            linearAddressBox.setVisibility(View.VISIBLE);
                            mTvProvideBY.setVisibility(View.VISIBLE);

                            mTvChatAddress.setText(arrayString[0]);
                            mChannelPropertyOwenrEmail = arrayString[1];
                            mChannelPropertyOwenrName = arrayString[2];
                            //set post by name
                            mTvProvideBY.setText("Added by " + mChannelPropertyOwenrName);
                            mChannelPropertyId = arrayString[3];

                            if (mChannelMainIdUrl.equalsIgnoreCase("")) {
                                if (arrayString.length <= 4) {
                                    mChannelMainIdUrl = arrayString[4];
                                }
                            }

                        }

                    } catch (Exception w) {
                        //show address view
//                        linearAddressBox.setVisibility(View.GONE);
//                        mTvProvideBY.setVisibility(View.GONE);
                    }
                } else {
                    if (addressV.equalsIgnoreCase("")) {
                        linearAddressBox.setVisibility(View.GONE);
                    } else {
                        mTvChatAddress.setText(addressV);
                    }

                    if (mChannelPropertyOwenrName.equalsIgnoreCase("")) {
                        mTvProvideBY.setVisibility(View.GONE);
                    }
                }


            }
            mTvChatPropertyName.setText(chatStr);
            //set image cover
            RequestOptions myOptions = new RequestOptions()
                    .dontAnimate()
                    .circleCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
            if (mChannel.getCoverUrl().contains("https://static.sendbird.com/sample/cover")) {
                Glide.with(moActivity)
                        .load(R.mipmap.ic_launcher_round)
                        .apply(myOptions)
                        .into(mIvCoverImage);
            } else {
                Glide.with(moActivity).load(mChannel.getCoverUrl()).apply(myOptions).into(mIvCoverImage);
            }


        }

        if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
            //get property details bg api
            synchronized (GroupChatActivity.this) {
                getDetailsProperty();
            }
        }


        linearAddressBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {

//                    Uri gmmIntentUri = Uri.parse("geo:" + mCommonDetailsData.getLat() + "," + mCommonDetailsData.getLang() + "?q=" + mCommonDetailsData.getAddress());
                    Uri gmmIntentUri = Uri.parse("http://maps.google.com/maps?daddr=" + mCommonDetailsData.getLat() + ","
                            + mCommonDetailsData.getLang() + "&q=" + mCommonDetailsData.getAddress());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private void sendUserMessageWithUrl(final String text, String url) {
        if (mChannel == null) {
            return;
        }

        new WebUtils.UrlPreviewAsyncTask() {
            @Override
            protected void onPostExecute(UrlPreviewInfo info) {
                if (mChannel == null) {
                    return;
                }

                UserMessage tempUserMessage = null;
                BaseChannel.SendUserMessageHandler handler = new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Error!
                            Log.e(LOG_TAG, e.toString());

                        }

                        mMessageCollection.handleSendMessageResponse(userMessage, e);
                    }
                };

                try {
                    // Sending a message with URL preview information and custom type.
                    String jsonString = info.toJsonString();
                    tempUserMessage = mChannel.sendUserMessage(text, jsonString, GroupChatAdapter.URL_PREVIEW_CUSTOM_TYPE, handler);
                } catch (Exception e) {
                    // Sending a message without URL preview information.
                    tempUserMessage = mChannel.sendUserMessage(text, handler);
                }
                // Display a user message to RecyclerView
                if (mMessageCollection != null) {
                    mMessageCollection.appendMessage(tempUserMessage);
                }
            }
        }.execute(url);
    }

    private void sendUserMessage(String text) {
        if (mChannel == null) {
            return;
        }

        List<String> urls = WebUtils.extractUrls(text);
        if (urls.size() > 0) {
            sendUserMessageWithUrl(text, urls.get(0));
            return;
        }

        final UserMessage pendingMessage = mChannel.sendUserMessage(text, new BaseChannel.SendUserMessageHandler() {
            @Override
            public void onSent(UserMessage userMessage, SendBirdException e) {
                if (mMessageCollection != null) {
                    mMessageCollection.handleSendMessageResponse(userMessage, e);
                    mMessageCollection.fetchAllNextMessages(null);
                }

                if (e != null) {
                    // Error!
                    Log.e(LOG_TAG, e.toString());
                    //Toast.makeText(moActivity, getString(R.string.send_message_error, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        if (mMessageCollection != null) {
            mMessageCollection.appendMessage(pendingMessage);
        }
    }

    /**
     * Notify other users whether the current user is typing.
     *
     * @param typing Whether the user is currently typing.
     */
    private void setTypingStatus(boolean typing) {
        if (mChannel == null) {
            return;
        }

        if (typing) {
            mChannel.startTyping();
        } else {
            mChannel.endTyping();
        }
    }


    /**
     * Sends a File Message containing an image file.
     * Also requests thumbnails to be generated in specified sizes.
     *
     * @param uri The URI of the image, which in this case is received through an Intent request.
     */
    private void sendFileWithThumbnail(Uri uri) {
        if (mChannel == null) {
            return;
        }
        //check if user free
        if (!PreferenceUtils.getPlanVersionStatus()) {
            if (mChatAdapter.getMessageCounts() >= 10) {
                //open alert
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1), "Ok");
                } else {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_2), "Upgrade");
                }
                return;
            }
        }
        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));

        Hashtable<String, Object> info = FileUtils.getFileInfo(moActivity, uri);

        final String name, path, mime;
        final File file;
        final int size;
        if (info == null || info.isEmpty()) {
            path = RealPathUtil.getRealPath(GroupChatActivity.this, uri);
            file = RealPathUtil.getFile(GroupChatActivity.this, uri);
            if (file.getName() != null && !file.getName().equalsIgnoreCase("")) {
                name = file.getName();
            } else {
                name = "Sendbird File";
            }
            mime = getMimeType(uri);
            size = getSize(uri);
        } else {

            if (info.containsKey("name")) {
                name = (String) info.get("name");
            } else {
                name = "Sendbird File";
            }

            path = (String) info.get("path");
            file = new File(path);
            mime = (String) info.get("mime");
            size = (int) info.get("size");
            Log.e("TAG", "File ==>" + file.getPath());
            Log.e("TAG", "File ==>" + file.getAbsolutePath());
            Log.e("TAG", "File ==>" + path);
        }

        if (path == null || path.equals("")) {
            Toast.makeText(moActivity, getString(R.string.wrong_file_path), Toast.LENGTH_LONG).show();
        } else {
            BaseChannel.SendFileMessageHandler fileMessageHandler = new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    mMessageCollection.handleSendMessageResponse(fileMessage, e);
                    mMessageCollection.fetchAllNextMessages(null);
                    if (e != null) {
                        Log.d("MyTag", "onSent: " + moActivity);
                        if (moActivity != null) {
                            //Toast.makeText(moActivity, getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };

            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(file, name, mime, size, "", null, thumbnailSizes, fileMessageHandler);

            mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);

            if (mMessageCollection != null) {
                mMessageCollection.appendMessage(tempFileMessage);
            }
        }
    }

    private void showGallery() {

        BSImagePicker multiSelectionPicker = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                .setMinimumMultiSelectCount(1) //Default: 1.
                .setMultiSelectBarBgColor(android.R.color.white) //Default: #FFFFFF. You can also set it to a translucent color.
                .setMultiSelectTextColor(R.color.primary_text) //Default: #212121(Dark grey). This is the message in the multi-select bottom bar.
                .setMultiSelectDoneTextColor(R.color.colorAccent) //Default: #388e3c(Green). This is the color of the "Done" TextView.
                .setOverSelectTextColor(R.color.error_text) //Default: #b71c1c. This is the color of the message shown when user tries to select more than maximum select count.
                .disableOverSelectionMessage() //You can also decide not to show this over select message.\
                .build();
        multiSelectionPicker.show(getSupportFragmentManager(), "picker");
    }

    private void requestPemission() {
        // Checking if permission is not granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(GroupChatActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE}, 696);
            } else {
                showGallery();
            }
        } else {
            showGallery();
        }
    }

    private int getSize(Uri mImageUri) {
        /*
         * Get the file's content URI from the incoming Intent,
         * then query the server app to get the file's display name
         * and size.
         */

        Cursor returnCursor =
                getContentResolver().query(mImageUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         * move to the first row in the Cursor, get the data,
         * and display it.
         */
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        return sizeIndex;
    }

    public String getMimeType(Uri uri) {
        String mimeType = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            ContentResolver cr = getApplicationContext().getContentResolver();
            mimeType = cr.getType(uri);
        } else {
            String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri
                    .toString());
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                    fileExtension.toLowerCase());
        }
        return mimeType;
    }

    private void editMessage(final BaseMessage message, String editedMessage) {
        if (mChannel == null) {
            return;
        }

        mChannel.updateUserMessage(message.getMessageId(), editedMessage, null, null, new BaseChannel.UpdateUserMessageHandler() {
            @Override
            public void onUpdated(UserMessage userMessage, SendBirdException e) {
                if (e != null) {
                    // Error!
                    //Toast.makeText(moActivity, getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (mMessageCollection != null) {
                    mMessageCollection.updateMessage(userMessage);
                }
            }
        });
    }

    /**
     * Deletes a message within the channel.
     * Note that users can only delete messages sent by oneself.
     *
     * @param message The message to delete.
     */
    private void deleteMessage(final BaseMessage message) {
        if (message.getMessageId() == 0) {
            mMessageCollection.deleteMessage(message);
        } else {
            if (mChannel == null) {
                return;
            }

            mChannel.deleteMessage(message, new BaseChannel.DeleteMessageHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        //Toast.makeText(moActivity, getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    mMessageCollection.deleteMessage(message);
                }
            });
        }
    }

    private void updateLastSeenTimestamp(List<BaseMessage> messages) {
        long lastSeenTimestamp = mLastRead == Long.MAX_VALUE ? 0 : mLastRead;
        for (BaseMessage message : messages) {
            if (lastSeenTimestamp < message.getCreatedAt()) {
                lastSeenTimestamp = message.getCreatedAt();
            }
        }

        if (lastSeenTimestamp > mLastRead) {
            PreferenceUtils.setLastRead(mChannelUrl, lastSeenTimestamp);
            mLastRead = lastSeenTimestamp;
        }
    }

    private MessageCollectionHandler mMessageCollectionHandler = new MessageCollectionHandler() {
        @Override
        public void onMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
        }

        @Override
        public void onSucceededMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onSucceededMessageEvent: size = " + messages.size() + ", action = " + action);

            if (moActivity == null) {
                return;
            }

            moActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertSucceededMessages(messages);
                            mChatAdapter.markAllMessagesAsRead();
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;

                        case UPDATE:
                            mChatAdapter.updateSucceededMessages(messages);
                            break;

                        case CLEAR:
                            mChatAdapter.clear();
                            break;
                    }
                }
            });

            updateLastSeenTimestamp(messages);
        }

        @Override
        public void onPendingMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action) {
            Log.d("SyncManager", "onPendingMessageEvent: size = " + messages.size() + ", action = " + action);
            if (moActivity == null) {
                return;
            }

            moActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            List<BaseMessage> pendingMessages = new ArrayList<>();
                            for (BaseMessage message : messages) {
                                if (!mChatAdapter.failedMessageListContains(message)) {
                                    pendingMessages.add(message);
                                }
                            }
                            mChatAdapter.insertSucceededMessages(pendingMessages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeSucceededMessages(messages);
                            break;
                    }
                }
            });
        }

        @Override
        public void onFailedMessageEvent(MessageCollection collection, final List<BaseMessage> messages, final MessageEventAction action, final FailedMessageEventActionReason reason) {
            Log.d("SyncManager", "onFailedMessageEvent: size = " + messages.size() + ", action = " + action);
            if (moActivity == null) {
                return;
            }

            moActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (action) {
                        case INSERT:
                            mChatAdapter.insertFailedMessages(messages);
                            break;

                        case REMOVE:
                            mChatAdapter.removeFailedMessages(messages);
                            break;
                        case UPDATE:
                            if (reason == FailedMessageEventActionReason.UPDATE_RESEND_FAILED) {
                                mChatAdapter.updateFailedMessages(messages);
                            }
                            break;
                    }
                }
            });
        }

        @Override
        public void onNewMessage(MessageCollection collection, BaseMessage message) {
            Log.d("SyncManager", "onNewMessage: message = " + message);
            //show when the scroll position is bottom ONLY.
            if (mLayoutManager.findFirstVisibleItemPosition() != 0) {
                if (message instanceof UserMessage) {
                    if (!((UserMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                        mNewMessageText.setText("New Message = " + ((UserMessage) message).getSender().getNickname() + " : " + ((UserMessage) message).getMessage());
                        mNewMessageText.setVisibility(View.VISIBLE);
                    }
                } else if (message instanceof FileMessage) {
                    if (!((FileMessage) message).getSender().getUserId().equals(PreferenceUtils.getUserId())) {
                        mNewMessageText.setText("New Message = " + ((FileMessage) message).getSender().getNickname() + "Send a File");
                        mNewMessageText.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };

    //new feature
    //check
    String fullname = "Flippbidd";
    String otherUserEmail = "";

    private void openPhotoClickBottomSheetView(UserMessage message, FileMessage fileMessage) {
        //check
        if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
            if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
               /* if (!mCommonDetailsData.getData().getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                    return;

                } */
                if (message != null) {
                    fullname = message.getSender().getNickname();
                    otherUserEmail = message.getSender().getUserId();
                } else {
                    fullname = fileMessage.getSender().getNickname();
                    otherUserEmail = fileMessage.getSender().getUserId();
                }


                chatBottomSheetDialogFragment.addListener(new ChatBottomSheetDialogFragment.DailogListener() {
                    @Override
                    public void onVoiceCallClick() {
                        if (!mCommonDetailsData.getData().getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                            callCountApi(false);
                        } else {
                            chatBottomSheetDialogFragment.dismiss();
                            NewCallService.dial(GroupChatActivity.this, fullname, otherUserEmail, false);
                            PreferenceUtils.setCalleeId(otherUserEmail);
                        }
                    }

                    @Override
                    public void onUVideoCallClick() {
                        if (!mCommonDetailsData.getData().getLoginId().equalsIgnoreCase(BaseApplication.getInstance().getLoginID())) {
                            callCountApi(true);
                        } else {
                            chatBottomSheetDialogFragment.dismiss();
                            NewCallService.dial(GroupChatActivity.this, fullname, otherUserEmail, true);
                            PreferenceUtils.setCalleeId(otherUserEmail);
                        }
                    }

                    @Override
                    public void onViewProfileClick() {
                        chatBottomSheetDialogFragment.dismiss();
                        startActivity(new Intent(GroupChatActivity.this, PropertyOtherUserDetailsActivity.class)
                                .putExtra(PropertyOtherUserDetailsActivity.USER_ID, otherUserEmail));
                    }

                    @Override
                    public void onCancelChatClick() {
                        chatBottomSheetDialogFragment.dismiss();
                    }
                }, fullname);
                chatBottomSheetDialogFragment.show(getSupportFragmentManager(), "chatdialog");


            }
        }
    }

    private void callCountApi(boolean isVideoCall) {
        if (!NetworkUtils.isInternetAvailable(moActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<CommonResponse_> observable;
        observable = userService.callCount(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse_>() {
            @Override
            public void onSuccess(CommonResponse_ response) {
                showProgressBar(false);
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //start call dialog here
                    if (isVideoCall) {
                        chatBottomSheetDialogFragment.dismiss();
                        NewCallService.dial(GroupChatActivity.this, fullname, otherUserEmail, true);
                        PreferenceUtils.setCalleeId(otherUserEmail);
                    } else {
                        chatBottomSheetDialogFragment.dismiss();
                        NewCallService.dial(GroupChatActivity.this, fullname, otherUserEmail, false);
                        PreferenceUtils.setCalleeId(otherUserEmail);
                    }
                } else {
                    //show upgrade dialog

                    CommonDialogView.getInstance().showCommonDialog(GroupChatActivity.this, "",
                            (response.getText())
                            , "Cancel"
                            , "Upgrade"
                            , false, true, false, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {
                                    //open upgrade plan activity
                                    Intent mIntent = new Intent(GroupChatActivity.this, InAppPurchaseActivity.class);
                                    startActivity(mIntent);
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
    //end

    @Override
    protected void onResumeFragments() {
        Log.e("TAG", "EVENT");
        super.onResumeFragments();
    }


    /*@Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back.
            if (mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), InputMethodManager.SHOW_IMPLICIT)) {
                mIMM.hideSoftInputFromWindow(mMessageEditText.getWindowToken(), 0);
                if (mAppBarLayout != null) {
                    mAppBarLayout.setExpanded(true, true);
                }
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
*/

   /* private void callButtonStatus() {
        //token, meeting_id, login_id, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), mCommonDetailsData.getLoginId()));
        linkedHashMap.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelPropertyId));

        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<JsonElement> observable;
        observable = userService.requestMeetingScheduleStatus(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                //{
                //    "success": false,
                //    "text": "Button is not visible",
                //    "data": {
                //        "type": "Audio Call",
                //        "owner_id": "330",
                //        "login_id": "290",
                //        "owner_email": "bhavisha.hcuboidtech@gmail.com",
                //        "other_email": "milap123@gmail.com"
                //    }
                //}
                if (response.getAsJsonObject().get("status").getAsBoolean()) {
                    JsonObject data = response.getAsJsonObject().get("data").getAsJsonObject();
                    callType = data.get("type").getAsString();
                    ownerEmail = data.get("owner_email").getAsString();
                    requestEmail = data.get("other_email").getAsString();
                    if (callType.equalsIgnoreCase("Audio Call")) {
                        mIvCallButton.setImageResource(R.drawable.audio_white);
                    } else if (callType.equalsIgnoreCase("Video Call")) {
                        mIvCallButton.setImageResource(R.drawable.video_white);
                    }
                    mIvCallButton.setVisibility(View.VISIBLE);
                } else {
                    mIvCallButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }*/

    /*get property Details*/
    private void getDetailsProperty() {
        if (!NetworkUtils.isInternetAvailable(moActivity)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("common_id", RequestBody.create(MediaType.parse("multipart/form-data"), mChannelPropertyId));
        linkedHashMap.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), "Property"));

        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<DetailsTypeRespons> observable;
        observable = userService.getDetails(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<DetailsTypeRespons>() {
            @Override
            public void onSuccess(DetailsTypeRespons response) {
                showProgressBar(false);
                //LogUtils.LOGD(TAG, "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    mCommonDetailsData = response.getData();
//                    callButtonStatus();
                    if (mCommonDetailsData.getData() != null && !mCommonDetailsData.getData().equals("")) {
                        calendarIconVisiblity(mCommonDetailsData.getData().getEmailAddress());
                    }

                } else {
                    ToastUtils.longToast(response.getText());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    @Override
    public void onSingleImageSelected(Uri uri) {
        sentImageFromGallery(uri);
    }

    private void sentImageFromGallery(Uri uri) {
        if (mChannel == null) {
            return;
        }
        //check if user free
        if (!PreferenceUtils.getPlanVersionStatus()) {
            if (mChatAdapter.getMessageCounts() >= 10) {
                //open alert
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_1), "Ok");
                } else {
                    openLimitExtendPlan(GroupChatActivity.this.getResources().getString(R.string.alert_message_2), "Upgrade");
                }
                return;
            }
        }
        // Specify two dimensions of thumbnails to generate
        List<FileMessage.ThumbnailSize> thumbnailSizes = new ArrayList<>();
        thumbnailSizes.add(new FileMessage.ThumbnailSize(240, 240));
        thumbnailSizes.add(new FileMessage.ThumbnailSize(320, 320));
        File mFile = null;
        File mCompressor = null;
        if (uri != null) {
            mFile = com.flippbidd.Others.FileUtils.getFile(GroupChatActivity.this, uri);
            try {
                mCompressor = new Compressor(GroupChatActivity.this).compressToFile(mFile);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mCompressor = com.flippbidd.Others.FileUtils.getFile(GroupChatActivity.this, uri);
        }
        if (mCompressor.getPath() == null || mCompressor.getPath().equals("")) {
            Toast.makeText(moActivity, getString(R.string.wrong_file_path), Toast.LENGTH_LONG).show();
        } else {
            BaseChannel.SendFileMessageHandler fileMessageHandler = new BaseChannel.SendFileMessageHandler() {
                @Override
                public void onSent(FileMessage fileMessage, SendBirdException e) {
                    mMessageCollection.handleSendMessageResponse(fileMessage, e);
                    mMessageCollection.fetchAllNextMessages(null);
                    if (e != null) {
                        Log.d("MyTag", "onSent: " + moActivity);
                        if (moActivity != null) {
                            //Toast.makeText(moActivity, getString(R.string.sendbird_error_with_code, e.getCode(), e.getMessage()), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };
            int file_size = Integer.parseInt(String.valueOf(mCompressor.length() / 1024));
            // Send image with thumbnails in the specified dimensions
            FileMessage tempFileMessage = mChannel.sendFileMessage(mCompressor, mCompressor.getName(), getMimeType(uri), file_size, "", null, thumbnailSizes, fileMessageHandler);

            mChatAdapter.addTempFileMessageInfo(tempFileMessage, uri);

            if (mMessageCollection != null) {
                mMessageCollection.appendMessage(tempFileMessage);
            }
        }
    }
    /*end*/
}
