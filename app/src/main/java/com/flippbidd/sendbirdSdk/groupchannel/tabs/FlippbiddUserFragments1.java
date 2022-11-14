package com.flippbidd.sendbirdSdk.groupchannel.tabs;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flippbidd.CustomClass.CustomEditText;
import com.flippbidd.Model.ContactList.ContactSycnResponse;
import com.flippbidd.Model.ContactList.Users;
import com.flippbidd.Model.RefreshListener;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.NewSelectableUserListAdapter;
import com.flippbidd.utils.PreferenceUtils;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FlippbiddUserFragments1 extends Fragment implements RefreshListener {

    private CustomEditText mEdtBusinessSearch;
    private LinearLayoutManager mLayoutManager;
    private RecyclerView mRecyclerViewInvite;
    private NewSelectableUserListAdapter mListAdapter;

    private UsersSelectedListener mListener;
    boolean isBox;


    public FlippbiddUserFragments1(boolean isValue){
        this.isBox = isValue;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragments_flippbidd_user_list, container, false);

        //id
        mRecyclerViewInvite = view.findViewById(R.id.recycler_invite_user);

        //set recycreview
        mListAdapter = new NewSelectableUserListAdapter(getActivity(), false, isBox, true);

        mListAdapter.setItemCheckedChangeListener(new NewSelectableUserListAdapter.OnItemCheckedChangeListener() {
            @Override
            public void OnItemChecked(Users user, boolean checked) {
                if (checked) {
                    mListener.onUserSelected(true, user);
                } else {
                    mListener.onUserSelected(false, user);
                }
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
                mListAdapter.getFilter().filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mListener = (UsersSelectedListener) getActivity();

        setUpRecyclerView();
        return view;
    }

    private void setUpRecyclerView() {
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerViewInvite.setLayoutManager(mLayoutManager);
        mRecyclerViewInvite.setAdapter(mListAdapter);
        mRecyclerViewInvite.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));


        ContactSycnResponse data = new ContactSycnResponse();
        Gson gson = new Gson();
        data = gson.fromJson(PreferenceUtils.getContactListResponse(), ContactSycnResponse.class);
        if (data != null && !data.equals("")) {
            if (data.getFriendRequestList() != null && data.getFriendRequestList().size() != 0) {
                mListAdapter.setUserList(data.getFriendRequestList());
            }
        }
    }


    // To pass selected user IDs to the parent Activity.
    public interface UsersSelectedListener {
        void onUserSelected(boolean selected, Users userId);
    }


    @Override
    public void refresh(boolean b) {

    }
}
