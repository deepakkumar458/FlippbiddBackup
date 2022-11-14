package com.flippbidd.sendbirdSdk.groupchannel.tabs;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.InviteContactUserAdapter;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyContactUserFragments1 extends Fragment implements RefreshListener, InviteContactUserAdapter.OnItemCheckedChangeListener {

    private RelativeLayout mRlInviteBox;
    private CustomAppCompatButton mBtnInvite;
    private CustomEditText mEdtBusinessSearch;
    private LinearLayoutManager newInviteLayoutManager;
    private RecyclerView mRecyclerViewInvite;
    private InviteContactUserAdapter mInviteContactUserAdapter;

    private ContactSelectedListener mContactSelectedListener;
    List<Contact> contacts = new ArrayList<>();

    List<String> moSelectedContact = new ArrayList<>();

    boolean isBox;

    public MyContactUserFragments1(boolean isValue) {
        this.isBox = isValue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_flippbidd_user_list, container, false);

        mRecyclerViewInvite = view.findViewById(R.id.recycler_invite_user);
        mRlInviteBox = view.findViewById(R.id.rlInviteBox);
        mBtnInvite = view.findViewById(R.id.btnInvite);

        //show
        mRlInviteBox.setVisibility(View.VISIBLE);

        setUpRecyclerViewInviteList();

        mBtnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (moSelectedContact.size() <= 0) {
                    return;
                }
                //invite user
                inviteUser();
            }
        });

        mEdtBusinessSearch = view.findViewById(R.id.edtBusinessSearch);
        mEdtBusinessSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (mInviteContactUserAdapter != null) {
                        if (!charSequence.toString().equalsIgnoreCase("")) {
                            mInviteContactUserAdapter.getFilter().filter(charSequence.toString());
                        }
                    }
                } catch (NullPointerException e) {
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mContactSelectedListener = (ContactSelectedListener) getActivity();

        return view;
    }

    private void inviteUser() {

        String contactsList;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            contactsList = String.join(",", moSelectedContact);
        } else {
            contactsList = android.text.TextUtils.join(",", moSelectedContact);
        }

        /*You have been invited to join a FlippBidd chat.  Please down FlippBidd @ LINK (IOS) LINK (ANDROID) **FOR IOS AND ANDROID***/
        Intent I = new Intent(Intent.ACTION_VIEW);
        I.setData(Uri.parse("smsto:"));
        I.setType("vnd.android-dir/mms-sms");
        I.putExtra("address", new String(contactsList));
        I.putExtra("sms_body", getString(R.string.string_share_sms_message));

        try {
            getActivity().startActivity(I);
            getActivity().finish();
            Log.i("Sms Send", "");
        } catch (Exception e) {
            Toast.makeText(getActivity(), "Sms not send", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void refresh(boolean b) {
    }

    @Override
    public void OnItemChecked(Contact mContact, boolean checked) {
        if (checked) {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                mContactSelectedListener.onContactSelected(true, mContact);

                moSelectedContact.add(mContact.getPhoneNumbers().get(0).getNumber());
            }
        } else {
            if (mContact.getPhoneNumbers() != null && mContact.getPhoneNumbers().size() != 0) {
                mContactSelectedListener.onContactSelected(false, mContact);

                moSelectedContact.remove(mContact.getPhoneNumbers().get(0).getNumber());
            }
        }
    }

    public interface ContactSelectedListener {
        void onContactSelected(boolean selected, Contact userId);
    }

    private void setUpRecyclerViewInviteList() {

        mInviteContactUserAdapter = new InviteContactUserAdapter(getActivity(), contacts);

        newInviteLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewInvite.setLayoutManager(newInviteLayoutManager);
        mRecyclerViewInvite.setAdapter(mInviteContactUserAdapter);
        mRecyclerViewInvite.setNestedScrollingEnabled(false);
        mRecyclerViewInvite.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        synchronized (getActivity()) {
            //get total size
            List<Contact> contacts = Contacts.getQuery().find();
            addData(contacts);
        }

    }

    private void addData(List<Contact> contactList) {
        mInviteContactUserAdapter.setUserList(contactList);
        mInviteContactUserAdapter.setItemCheckedChangeListener(this);
    }
}
