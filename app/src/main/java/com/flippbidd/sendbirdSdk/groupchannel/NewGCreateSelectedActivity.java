package com.flippbidd.sendbirdSdk.groupchannel;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.asksira.bsimagepicker.BSImagePicker;
import com.flippbidd.CommonClass.CircleImageView;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.Others.FileUtils;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NewGCreateSelectedActivity extends BaseActivity implements BSImagePicker.OnSingleImageSelectedListener, SelectedGroupUserAdapter.OnItemCheckedChangeListener {
    private static final String TAG = NewGCreateSelectedActivity.class.getSimpleName();

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";
    public static final String EXTRA_GROUP_CHANNEL_URL = "GROUP_CHANNEL_URL";

    private Activity moActivity;
    private CustomTextView mTvFinalBack, mTvFinalCreate, mTvTotalMemberCounts;
    private CustomEditText medtGrname;
    private CircleImageView mIvFinalGIconSelection;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewSelectedList;
    SelectedGroupUserAdapter selectedGroupUserAdapter;
    List<Users> sList = new ArrayList<>();
    private File mFile = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_g_create_ui);
        this.moActivity = NewGCreateSelectedActivity.this;
        //bind
        mTvTotalMemberCounts = findViewById(R.id.tvTotalMemberCounts);
        medtGrname = findViewById(R.id.edtGrname);
        mTvFinalBack = findViewById(R.id.tvFinalBack);
        mTvFinalCreate = findViewById(R.id.tvFinalCreate);
        mIvFinalGIconSelection = findViewById(R.id.ivFinalGIconSelection);
        mRecyclerViewSelectedList = findViewById(R.id.recyclerViewSelectedList);

        mTvFinalBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mIvFinalGIconSelection.setOnClickListener(new View.OnClickListener() {
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

        mTvFinalCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (sList != null && sList.size() != 0) {
                    String groupName = medtGrname.getText().toString();
                    if (groupName != null && !groupName.equalsIgnoreCase("")) {
                        List<String> moUser = new ArrayList<>();
                        for (int i = 0; i < sList.size(); i++) {
                            moUser.add(sList.get(i).getUserId());
                        }
                        //create chat
                        createGroupChannel(moUser, groupName);
                    } else {
                        Toast.makeText(moActivity, "Please enter group name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(moActivity, "Please select user", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        selectedGroupUserAdapter = new SelectedGroupUserAdapter(this, false);
        setUpRecyclerView();

    }


    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewSelectedList.setLayoutManager(mLayoutManager);
        mRecyclerViewSelectedList.setAdapter(selectedGroupUserAdapter);
        if (NewCreateGroupActivity.mSelectedIds != null && NewCreateGroupActivity.mSelectedIds.size() != 0) {
            sList.addAll(NewCreateGroupActivity.mSelectedIds);
            selectedGroupUserAdapter.setUserList(sList);
            selectedGroupUserAdapter.setItemCheckedChangeListener(this::OnItemChecked);

            mTvTotalMemberCounts.setText("Total participants: " + NewCreateGroupActivity.mSelectedIds.size());
        }
    }

    /*update image*/
    @Override
    public void onSingleImageSelected(Uri uri) {
        if (uri != null) {
            mIvFinalGIconSelection.setImageURI(uri);
            /*uri to file*/
            if (uri != null) {
                mFile = FileUtils.getFile(moActivity, uri);
            }
        }
    }

    /*chat create */

    /**
     * Creates a new Group Channel.
     * <p>
     * Note that if you have not included empty channels in your GroupChannelListQuery,
     * the channel will not be shown in the user's channel list until at least one message
     * has been sent inside.
     *
     * @param userIds   The users to be members of the new channel.
     * @param groupName Whether the channel is unique for the selected members.
     *                  If you attempt to create another Distinct channel with the same members,
     *                  the existing channel instance will be returned.
     */
    private void createGroupChannel(List<String> userIds, String groupName) {

        //add created id
        List<User> moAdminUser = new ArrayList<>();
        moAdminUser.add(SendBird.getCurrentUser());

        GroupChannelParams params = new GroupChannelParams()
                .setPublic(false)
                .setEphemeral(false)
                .setDistinct(false)
                .addUserIds(userIds)
                .setOperators(moAdminUser)  // Or .setOperators(List<User> operators)
                .setName(groupName)
                // In a group channel, you can create a new channel by specifying its unique channel URL in a 'GroupChannelParams' object.
                .setCoverImage(mFile)
                .setData("none") //.setCoverImage(FILE)            // Or .setCoverUrl(COVER_URL)
                .setCustomType("new_c_group");

        //.setChannelUrl(groupName)
        showProgressBar(true);
        GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                showProgressBar(false);
                if (e != null) {
                    // Error!
                    if (e.getMessage().equalsIgnoreCase("\"channel_url\" violates unique constraint.")) {
                        enterGroupChannel(groupName);
                    }
                    return;
                }
                enterGroupChannel(groupChannel.getUrl());

            }
        });
    }


    /**
     * Enters a Group Channel with a URL.
     *
     * @param extraChannelUrl The URL of the channel to enter.
     */
    void enterGroupChannel(String extraChannelUrl) {
        showProgressBar(false);
        Intent mainIntent = new Intent(NewGCreateSelectedActivity.this, GroupChatActivity.class);
        mainIntent.putExtra(EXTRA_GROUP_CHANNEL_URL, extraChannelUrl);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void OnItemChecked(Users user, boolean checked) {
        try {
            if (checked) {
                sList.add(user);
            } else {
                sList.remove(user);
            }
            selectedGroupUserAdapter.setUserList(sList);
            mTvTotalMemberCounts.setText("Total participants: " + sList.size());
        } catch (Exception e) {
        }
    }
}
