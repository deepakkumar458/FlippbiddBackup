package com.flippbidd.sendbirdSdk.groupchannel;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flippbidd.Bottoms.MeetingScheduleSheet;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.activity.Contract.RequestContractActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.activity.UploadFiles_Activity_List;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.SyncManagerUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class GroupInfoActivity extends BaseActivity implements BSImagePicker.OnSingleImageSelectedListener {

    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    static final String EXTRA_CHANNEL = "EXTRA_CHANNEL";
    static final String EXTRA_MAIN_ID = "EXTRA_MAIN_ID";
    static final String EXTRA_PROPERTY_ID = "EXTRA_PROPERTY_ID";
    static final String EXTRA_OWNER_EMAIL = "EXTRA_OWNER_EMAIL";

    private ImageView mExpandImage, ivMeetingSchedule;
    private CustomTextView mTvGName, mTvMemberCounts, mBtnLeaveGroup;
    private CustomTextView mTvEditName;
    private ImageView relativeLOIRequestBox;
    private RelativeLayout mRlCoverUpdate;
    private GroupChannel mChannel;
    private Activity moActivity;
    private RelativeLayout moRlCreateNewGroup;
    private UserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private ImageView mIvUpdateGroupCover, mIvGroupInfoBackView;

    private String mChannelMainId = "";
    private String mChannelPropertyId = "";
    private String mChannelOwnerEmail = "";

    private String intentName = "";
    private String isAdmin = "";
    private Disposable disposable;
    private CommonData mCommonDetailsData = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info_layout);
        this.moActivity = GroupInfoActivity.this;

        if (getIntent().hasExtra(GroupChatFragment.EXTRA_CHANNEL)) {
            byte[] serializedChannelData = getIntent().getByteArrayExtra(GroupChatFragment.EXTRA_CHANNEL);
            mChannel = (GroupChannel) BaseChannel.buildFromSerializedData(serializedChannelData);

            mChannelMainId = getIntent().getStringExtra(GroupChatFragment.EXTRA_MAIN_ID);
            mChannelPropertyId = getIntent().getStringExtra(GroupChatFragment.EXTRA_PROPERTY_ID);
            mChannelOwnerEmail = getIntent().getStringExtra(GroupChatFragment.EXTRA_OWNER_EMAIL);

        }


        if (mChannel == null) {
            //Toast.makeText(this, R.string.no_channel, Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }
        //define id
        ivMeetingSchedule = findViewById(R.id.ivMeetingSchedule);
        mExpandImage = findViewById(R.id.expandedImage);
        mTvGName = findViewById(R.id.tvGName);
        mTvMemberCounts = findViewById(R.id.tvTotalMemberCounts);
        mRlCoverUpdate = findViewById(R.id.rlCoverUpdate);
        relativeLOIRequestBox = findViewById(R.id.relativeLOIRequestBox);

        mBtnLeaveGroup = findViewById(R.id.btnLeaveGroup);

        moRlCreateNewGroup = findViewById(R.id.rlCreateNewGroup);
        moRlCreateNewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(moActivity, InviteMemberActivity.class);
                intent.putExtra(EXTRA_CHANNEL_URL, mChannel.getUrl());
                intent.putExtra(EXTRA_MAIN_ID, mChannelMainId);
                intent.putExtra(EXTRA_PROPERTY_ID, mChannelPropertyId);
                intent.putExtra(EXTRA_OWNER_EMAIL, mChannelOwnerEmail);
                startActivityIfNeeded(intent, 801);
            }
        });

        mIvGroupInfoBackView = findViewById(R.id.ivGroupInfoBackView);
        mIvGroupInfoBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mIvUpdateGroupCover = findViewById(R.id.ivUpdateGroupCover);
        mIvUpdateGroupCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    BSImagePicker pickerDialog = new BSImagePicker.Builder("com.flippbidd.fileprovider")
                            .build();
                    pickerDialog.show(getSupportFragmentManager(), "picker");
                } catch (Exception e) {
                }
            }
        });

        mTvEditName = findViewById(R.id.tvEditName);
        mTvEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //show dialog with edit text
                showDialogWithText();
            }
        });

        mRecyclerView = findViewById(R.id.rcvMemberList);
        mListAdapter = new UserListAdapter(this, mChannel.getUrl(), true);


        //set data
        if (mChannel.getName().equalsIgnoreCase("Group Channel")) {
            mTvGName.setText("");
            mTvGName.setVisibility(View.GONE);
            mTvEditName.setVisibility(View.GONE);
        } else {
            mTvGName.setText(mChannel.getName());
            mTvGName.setVisibility(View.VISIBLE);
            mTvEditName.setVisibility(View.VISIBLE);
        }

        /*OPERATOR,*/
        mTvMemberCounts.setText("Total participants: " + mChannel.getMemberCount());

        /*mChannel.getMemberCount()*/
        mBtnLeaveGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show a dialog to confirm that the user wants to leave the channel.
                new AlertDialog.Builder(moActivity)
                        .setTitle(getString(R.string.request_leave_channel, mChannel.getName()))
                        .setPositiveButton(R.string.action_leave_channel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                leaveChannel(mChannel);
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), null)
                        .create().show();
            }
        });

        /*JOINED*/
        //set cover image
        RequestOptions myOptions = new RequestOptions()
                .dontAnimate()
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC);


        if (mChannel.getCoverUrl().equalsIgnoreCase("https://static.sendbird.com/sample/cover/cover_12.jpg")) {
            Glide.with(moActivity)
                    .asBitmap()
                    .load(R.drawable.banner_logo)
                    .apply(myOptions)
                    .into(mExpandImage);
        } else {
            Glide.with(moActivity)
                    .asBitmap()
                    .load(mChannel.getCoverUrl())
                    .apply(myOptions)
                    .into(mExpandImage);
        }

        if (mChannelPropertyId.equalsIgnoreCase("")) {
            findViewById(R.id.relativeDataFolderBox).setVisibility(View.GONE);
        } else {
            findViewById(R.id.relativeDataFolderBox).setVisibility(View.VISIBLE);
        }

        findViewById(R.id.relativeFilesUploadBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    String userID = "";
                    //check email
                    if (mChannelOwnerEmail != null && !mChannelOwnerEmail.equalsIgnoreCase("")) {
                        if (mChannelOwnerEmail.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {
                            userID = BaseApplication.getInstance().getLoginID();
                        }
                    }

                    startActivityIfNeeded(new Intent(GroupInfoActivity.this, UploadFiles_Activity_List.class)
                            .putExtra("post_type", "property")
                            .putExtra("post_id", mChannelPropertyId)
                            .putExtra("user_id", userID), 802);
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }
            }
        });

        findViewById(R.id.relativeLOIRequestBox).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {

                    startActivity(new Intent(GroupInfoActivity.this, RequestContractActivity.class)
                            .putExtra("p_id", mChannelPropertyId).putExtra("p_type", "property"));
                    overridePendingTransition(R.anim.slide_up, R.anim.stay);
                }

            }
        });

        if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
            findViewById(R.id.ivPropertyInfo).setVisibility(View.VISIBLE);
            findViewById(R.id.rlOPenPropertyDetails).setVisibility(View.VISIBLE);

//            if (!mChannelOwnerEmail.equalsIgnoreCase(BaseApplication.getInstance().getQBLoginID())) {
//                findViewById(R.id.ivMeetingSchedule).setVisibility(View.VISIBLE);
//                //call api for property details
//                getDetailsProperty(false);
//            }
        } else {
            findViewById(R.id.ivPropertyInfo).setVisibility(View.GONE);
            findViewById(R.id.rlOPenPropertyDetails).setVisibility(View.GONE);
            findViewById(R.id.ivMeetingSchedule).setVisibility(View.GONE);
        }


        findViewById(R.id.ivPropertyInfo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(GroupInfoActivity.this,
                            mChannelPropertyId, mChannelMainId,
                            "PROPERTY", false), 666);
                }
            }
        });

        findViewById(R.id.rlOPenPropertyDetails).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    startActivityIfNeeded(PropertyDetails.getIntentActivity(GroupInfoActivity.this,
                            mChannelPropertyId, mChannelMainId,
                            "PROPERTY", false), 666);
                }
            }
        });
        /*meeting schedule click*/
        findViewById(R.id.ivMeetingSchedule).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannelPropertyId != null && !mChannelPropertyId.equalsIgnoreCase("")) {
                    if (mCommonDetailsData != null && !mCommonDetailsData.equals("")) {
                        openBottomSheet();
                    }
                }
            }
        });


        setMemberList(mChannel.getMembers());
        setUpRecyclerView();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 801) {
                onBackPressed();
            }
        }
    }

    /*open meeting schedule sheet*/
    private void openBottomSheet() {

        MeetingScheduleSheet meetingScheduleSheet = MeetingScheduleSheet.newInstance();
        meetingScheduleSheet.addListener(new MeetingScheduleSheet.DialogListener() {
            @Override
            public void scheduleMeetingRequest(String selectedDate, String selectedStartTime, String selectedEndTime, String mMessage, String selectedType,String createdType) {
                meetingScheduleSheet.dismiss();
                requestMS(selectedDate, selectedStartTime, selectedEndTime, mMessage, selectedType,createdType);
            }

            @Override
            public void onCancelClick() {
                meetingScheduleSheet.dismiss();
            }
        }, "user",null,mCommonDetailsData);
        meetingScheduleSheet.show(getSupportFragmentManager(), GroupInfoActivity.class.getSimpleName());
    }
    /*end*/

    private void showDialogWithText() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(GroupInfoActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.group_name_input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(GroupInfoActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final CustomEditText userInputDialogEditText = (CustomEditText) mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.setText(mChannel.getName());
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {
                        // ToDo get user input here
                        String updateName = userInputDialogEditText.getText().toString();

                        if (updateName.length() <= 0) {
                            Toast.makeText(moActivity, "Please add name", Toast.LENGTH_LONG).show();
                            return;
                        }

                        mChannel.updateChannel(updateName, mChannel.getCoverUrl(), mChannel.getData(), new GroupChannel.GroupChannelUpdateHandler() {
                            @Override
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                mTvGName.setText(groupChannel.getName());
                                intentName = groupChannel.getName();
                                Toast.makeText(moActivity, "Update successfully", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                })

                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }

    /**
     * Leaves a Group Channel.
     *
     * @param channel The channel to leave.
     */
    private void leaveChannel(final GroupChannel channel) {
        //check admin
        if (isAdmin.equalsIgnoreCase(SendBird.getCurrentUser().getUserId())) {
            //admin
            String channel_Id = channel.getUrl();
            channel.delete(new GroupChannel.GroupChannelDeleteHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }

                    //call delete api for server chat delete
                    callApiForDeleteChat(channel_Id);
                }
            });

        } else {
            //other user
            channel.leave(new GroupChannel.GroupChannelLeaveHandler() {
                @Override
                public void onResult(SendBirdException e) {
                    if (e != null) {
                        // Error!
                        return;
                    }

                    // Re-query message list
                    Intent moIntent = new Intent();
                    moIntent.putExtra("update_name", "leave_group");
                    setResult(RESULT_OK, moIntent);
                    finish();
                }
            });
        }


    }

    private void callApiForDeleteChat(String channel_id) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(moActivity, moActivity.getString(R.string.no_internet), Toast.LENGTH_LONG).show();
            return;
        }

        //buyer_id, channel_id, owner_id, property_id, type, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("channel_id", RequestBody.create(MediaType.parse("multipart/form-data"), channel_id));

        UserServices userService = ApiFactory.getInstance(moActivity).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.deleteChannel(linkedHashMap);
        Disposable disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    //get channel url
                    // Re-query message list
                    Intent moIntent = new Intent();
                    moIntent.putExtra("update_name", "leave_group");
                    setResult(RESULT_OK, moIntent);
                    finish();
                } else {
                    Log.e("TAG", "Delete else part");
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void setMemberList(List<Member> memberList) {
        List<Member> sortedUserList = new ArrayList<>();
        List<String> sortedRoleList = new ArrayList<>();
        String myUserId = SyncManagerUtils.getMyUserId();
        boolean isUserAdmin = false;
        String userAdminId = "";
        for (Member member : memberList) {
            if (member.getUserId().equals(myUserId)) {
                sortedUserList.add(0, member);
                sortedRoleList.add(0, member.getRole().getValue());

                //check
                if (member.getRole().getValue().equalsIgnoreCase("OPERATOR")) {
                    isUserAdmin = true;
                    userAdminId = member.getUserId();
                }

            } else {
                sortedUserList.add(member);
                sortedRoleList.add(member.getRole().getValue());
            }
        }

        mListAdapter.setUserList(sortedUserList, sortedRoleList, isUserAdmin, userAdminId, mChannelPropertyId, mChannelOwnerEmail);

        //update edit option
        updateEditOption(userAdminId);
    }

    private void updateEditOption(String adminId) {
        isAdmin = adminId;
        String currentId = SendBird.getCurrentUser().getUserId();
        if (currentId.equalsIgnoreCase(adminId)) {
            //show
            relativeLOIRequestBox.setVisibility(View.GONE);
            mTvEditName.setVisibility(View.VISIBLE);
            mRlCoverUpdate.setVisibility(View.VISIBLE);

        } else {
            //hide
            relativeLOIRequestBox.setVisibility(View.VISIBLE);
            mTvEditName.setVisibility(View.GONE);
            mRlCoverUpdate.setVisibility(View.GONE);
        }
    }


    /*update image*/
    @Override
    public void onSingleImageSelected(Uri uri) {

        if (uri != null) {
            mExpandImage.setImageURI(uri);
            /*uri to file*/
            File mFile = null;
            if (uri != null) {
                mFile = FileUtils.getFile(moActivity, uri);
            }
            mChannel.updateChannel(mChannel.getName(), mFile, mChannel.getData(), new GroupChannel.GroupChannelUpdateHandler() {
                @Override
                public void onResult(GroupChannel groupChannel, SendBirdException e) {
                    Toast.makeText(moActivity, "Update successfully", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent moIntent = new Intent();
        moIntent.putExtra("update_name", intentName);
        setResult(RESULT_OK, moIntent);
        finish();
    }

    /*request meeting schedule call api*/
    private void requestMS(String date, String startTime, String endTime, String message, String type,String createdType) {
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
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressBar(false);
            }
        });
    }

    /*end*/
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
