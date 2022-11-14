package com.flippbidd.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;

import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.InviteContactUserAdapter;
import com.flippbidd.sendbirdSdk.groupchannel.InviteMemberActivity;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InviteContactListActivity extends AppCompatActivity {


    LinearLayoutManager newInviteLayoutManager;
    RecyclerView mRecyclerViewInvite;
    InviteContactUserAdapter mInviteContactUserAdapter;
    List<Contact> mContacts = new ArrayList<>();

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
        setContentView(R.layout.activity_invite_list_layout);

        mRecyclerViewInvite = (RecyclerView) findViewById(R.id.recycler_invite_user);
        mInviteContactUserAdapter = new InviteContactUserAdapter(InviteContactListActivity.this, mContacts);


        setUpRecyclerViewInviteList();
    }

    private void setUpRecyclerViewInviteList() {
        newInviteLayoutManager = new LinearLayoutManager(InviteContactListActivity.this);
        mRecyclerViewInvite.setLayoutManager(newInviteLayoutManager);
        newInviteLayoutManager.setAutoMeasureEnabled(false);
        mRecyclerViewInvite.setAdapter(mInviteContactUserAdapter);
        mRecyclerViewInvite.setNestedScrollingEnabled(false);
        mRecyclerViewInvite.addItemDecoration(new DividerItemDecoration(InviteContactListActivity.this, DividerItemDecoration.VERTICAL));

        mInviteContactUserAdapter.setItemCheckedChangeListener(new InviteContactUserAdapter.OnItemCheckedChangeListener() {
            @Override
            public void OnItemChecked(Contact mContact, boolean checked) {

                if (checked) {
                    if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {

                    }

                } else {
                    if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                    }
                }
            }
        });


        synchronized (this) {
            List<Contact> contacts = Contacts.getQuery().find();
            Log.d("TAG","New data"+contacts.size());
            addData(contacts);
            //get total size
        }
    }
    private void addData(List<Contact> contactList) {
        mInviteContactUserAdapter.setUserList(contactList);
    }
}
