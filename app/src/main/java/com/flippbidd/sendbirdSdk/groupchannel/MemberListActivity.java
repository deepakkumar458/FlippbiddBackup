package com.flippbidd.sendbirdSdk.groupchannel;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.SyncManagerUtils;
import com.sendbird.android.BaseChannel;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.Member;
import com.sendbird.android.SendBirdException;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class MemberListActivity extends BaseActivity {

    static final String EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL";
    static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    static final String EXTRA_USER_PROFILE_URL = "EXTRA_USER_PROFILE_URL";
    static final String EXTRA_USER_NICKNAME = "EXTRA_USER_NICKNAME";
    static final String EXTRA_USER_BLOCKED_BY_ME = "EXTRA_USER_BLOCKED_BY_ME";

    private UserListAdapter mListAdapter;
    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;
    private GroupChannel mChannel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_list);

        if (getIntent().hasExtra(GroupChatFragment.EXTRA_CHANNEL)) {
            byte[] serializedChannelData = getIntent().getByteArrayExtra(GroupChatFragment.EXTRA_CHANNEL);
            mChannel = (GroupChannel) BaseChannel.buildFromSerializedData(serializedChannelData);
        }

        if (mChannel == null) {
            //Toast.makeText(this, R.string.no_channel, Toast.LENGTH_SHORT).show();
            onBackPressed();
            finish();
        }

        mRecyclerView = findViewById(R.id.recycler_member_list);
        mListAdapter = new UserListAdapter(this, mChannel.getUrl(), true);

        Toolbar toolbar = findViewById(R.id.toolbar_member_list);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back);
        }

        setMemberList(mChannel.getMembers());
        setUpRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();

        getChannelFromUrl(mChannel.getUrl());
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mListAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getChannelFromUrl(String url) {
        GroupChannel.getChannel(url, new GroupChannel.GroupChannelGetHandler() {
            @Override
            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                mChannel = groupChannel;

                refreshChannel();
            }
        });
    }

    private void refreshChannel() {
        mChannel.refresh(new GroupChannel.GroupChannelRefreshHandler() {
            @Override
            public void onResult(SendBirdException e) {
                if (e != null) {
                    // Error!
                    return;
                }

                setMemberList(mChannel.getMembers());
            }
        });
    }

    private void setMemberList(List<Member> memberList) {
        List<Member> sortedUserList = new ArrayList<>();
        List<String> sortedRoleList = new ArrayList<>();
        String myUserId = SyncManagerUtils.getMyUserId();
        boolean isUserAdmin = false;
        String adminID = "";
        for (Member member : memberList) {
            if (member.getUserId().equals(myUserId)) {
                sortedUserList.add(0, member);
                sortedRoleList.add(0, member.getRole().getValue());
                if (member.getRole().getValue().equalsIgnoreCase("OPERATOR")) {
                    isUserAdmin = true;
                    adminID = member.getUserId();
                }
            } else {
                sortedUserList.add(member);
                sortedRoleList.add(member.getRole().getValue());
            }
        }

        mListAdapter.setUserList(sortedUserList, sortedRoleList, isUserAdmin, adminID,"","");
    }
}
