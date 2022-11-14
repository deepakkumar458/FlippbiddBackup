package com.flippbidd.sendbirdSdk.groupchannel.tabs;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import com.flippbidd.sendbirdSdk.groupchannel.NewChatCreateActivity;
import com.github.tamir7.contacts.Contact;
import com.github.tamir7.contacts.Contacts;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyContactUserFragments extends Fragment implements RefreshListener ,InviteContactUserAdapter.OnItemCheckedChangeListener{

    private RelativeLayout mRlInviteBox;
    private CustomAppCompatButton mBtnInvite;
    private CustomEditText mEdtBusinessSearch;
    private LinearLayoutManager newInviteLayoutManager;
    private RecyclerView mRecyclerViewInvite;
    private InviteContactUserAdapter mInviteContactUserAdapter;

    private ContactSelectedListener mContactSelectedListener;
    List<Contact> contacts =new ArrayList<>();

    List<String> moSelectedContact = new ArrayList<>();

    boolean isBox;
    public MyContactUserFragments(boolean isValue){
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
                System.out.println("Text [" + charSequence + "]");
                mInviteContactUserAdapter.getFilter().filter(charSequence.toString());
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

        Intent I = new Intent(Intent.ACTION_VIEW);
        I.setData(Uri.parse("smsto:"));
        I.setType("vnd.android-dir/mms-sms");
        I.putExtra("address", new String(contactsList));
        I.putExtra("sms_body", getString(R.string.string_share_sms_message));

        try {
            getActivity().startActivity(I);
            getActivity().finish();
            Log.i("Sms Send", getString(R.string.string_share_sms_message));
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

        mInviteContactUserAdapter = new InviteContactUserAdapter(getActivity(),contacts);

        newInviteLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewInvite.setLayoutManager(newInviteLayoutManager);
        mRecyclerViewInvite.setAdapter(mInviteContactUserAdapter);
        mRecyclerViewInvite.setNestedScrollingEnabled(false);
        mRecyclerViewInvite.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));

        checkPermission(Manifest.permission.READ_CONTACTS, 909);

    }

    // Function to check and request permission
    public void checkPermission(String permission, int requestCode) {
        // Checking if permission is not granted
        if (ContextCompat.checkSelfPermission(
                getActivity(),
                permission)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat
                    .requestPermissions(
                            getActivity(),
                            new String[]{permission},
                            requestCode);
        } else {
            syncyData();
        }
    }

    private void syncyData() {
        synchronized (getActivity()) {
            //get total size
            List<Contact> contacts = Contacts.getQuery().find();
            addData(contacts);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 909) {
            // Checking whether user granted the permission or not.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                syncyData();
            } else {
                checkPermission(Manifest.permission.READ_CONTACTS, 909);
            }
        }
    }
    private void addData(List<Contact> contactList) {
        mInviteContactUserAdapter.setUserList(contactList);
        mInviteContactUserAdapter.setItemCheckedChangeListener(this);
    }
}
