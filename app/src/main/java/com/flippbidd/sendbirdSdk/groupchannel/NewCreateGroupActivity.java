package com.flippbidd.sendbirdSdk.groupchannel;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.FlippbiddUserFragments;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.MyContactUserFragments;
import com.flippbidd.sendbirdSdk.view.BaseActivity;
import com.flippbidd.utils.PreferenceUtils;
import com.github.tamir7.contacts.Contact;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;


public class NewCreateGroupActivity extends BaseActivity implements FlippbiddUserFragments.UsersSelectedListener, MyContactUserFragments.ContactSelectedListener, SelectDistinctFragment.DistinctSelectedListener {

    public static final String EXTRA_NEW_CHANNEL_URL = "EXTRA_NEW_CHANNEL_URL";

    static final int STATE_SELECT_USERS = 0;
    static final int STATE_SELECT_DISTINCT = 1;

//    private Button mNextButton, mCreateButton;
    List<String> moSelectedContact;
    public static List<Users> mSelectedIds;
    private boolean mIsDistinct = true;
    private CustomTextView mTvNewChatCancel;
    private CustomEditText mEdtBusinessSearch;
    private CustomTextView mTvNewChatNext;
    private int mCurrentState;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_create_group_layout);

        moSelectedContact = new ArrayList<>();
        mSelectedIds = new ArrayList<>();
        Fragment fragment = SelectUserFragment.newInstance(true,false);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .replace(R.id.container_create_group_channel, fragment)
                .commit();


        mTvNewChatCancel = findViewById(R.id.tvNewChatCancel);
        mTvNewChatCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mTvNewChatNext = findViewById(R.id.tvNewChatNext);
        mTvNewChatNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentState == STATE_SELECT_USERS) {
                    mIsDistinct = PreferenceUtils.getGroupChannelDistinct();
                    //open NewGCreateSelectedActivity
                    if (mSelectedIds != null && mSelectedIds.size() != 0) {
                        startActivity(new Intent(NewCreateGroupActivity.this, NewGCreateSelectedActivity.class));
                        finish();
                    } else {
                        Toast.makeText(NewCreateGroupActivity.this, "Please select user", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });


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

    void setState(int state) {
        if (state == STATE_SELECT_USERS) {
            mCurrentState = STATE_SELECT_USERS;
        } else if (state == STATE_SELECT_DISTINCT) {
            mCurrentState = STATE_SELECT_DISTINCT;
        }
    }

    @Override
    public void onUserSelected(boolean selected, Users userId) {
        if (selected) {
            mSelectedIds.add(userId);
        } else {
            mSelectedIds.remove(userId);
        }

    }

    @Override
    public void onDistinctSelected(boolean distinct) {
        mIsDistinct = distinct;
    }

    @Override
    public void onContactSelected(boolean selected, Contact mContact) {
        if (selected) {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                moSelectedContact.add(mContact.getPhoneNumbers().get(0).getNumber());
            }
        } else {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                moSelectedContact.remove(mContact.getPhoneNumbers().get(0).getNumber());
            }
        }
    }
}
