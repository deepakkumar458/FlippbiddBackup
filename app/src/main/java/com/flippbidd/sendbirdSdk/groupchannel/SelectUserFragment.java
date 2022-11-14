package com.flippbidd.sendbirdSdk.groupchannel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flippbidd.Adapter.Pager.TabFragments;
import com.flippbidd.Others.Constants;
import com.flippbidd.R;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.FlippbiddUserFragments;
import com.flippbidd.sendbirdSdk.groupchannel.tabs.MyContactUserFragments;
import com.google.android.material.tabs.TabLayout;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;


/**
 * A fragment displaying a list of selectable users.
 */
public class SelectUserFragment extends Fragment {


    boolean isCheckBox = false;
    boolean isMyContactBox = false;


    //new code for tab
    TabFragments mViewPagerAdapter;
    TabLayout mViewTabLyaout;
    ViewPager mViewViewPager;

    static SelectUserFragment newInstance(boolean isShowCheckBox, boolean isShowMyContact) {
        SelectUserFragment fragment = new SelectUserFragment();
        Bundle mBundle = new Bundle();
        mBundle.putBoolean("is_show_check", isShowCheckBox);
        mBundle.putBoolean("is_show_my_contact", isShowMyContact);
        fragment.setArguments(mBundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_select_user, container, false);
        //get bundle data
        Bundle bundle = getArguments();
        if (bundle != null) {
            isCheckBox = bundle.getBoolean("is_show_check", false);
            isMyContactBox = bundle.getBoolean("is_show_my_contact", false);
        }

        //id
        mViewTabLyaout = rootView.findViewById(R.id.viewTabLyaout);
        mViewViewPager = rootView.findViewById(R.id.viewViewPager);

        if (isCheckBox) {
            ((NewCreateGroupActivity) getActivity()).setState(NewCreateGroupActivity.STATE_SELECT_USERS);
        } else {
            ((NewChatCreateActivity) getActivity()).setState(NewChatCreateActivity.STATE_SELECT_USERS);
        }
        //initview
        initView(rootView);


        return rootView;
    }

    private void initView(View rootView) {
        mViewPagerAdapter = new TabFragments(getChildFragmentManager());

        FlippbiddUserFragments flippbiddUserFragments = new FlippbiddUserFragments(isCheckBox);
        MyContactUserFragments myContactUserFragments = new MyContactUserFragments(isCheckBox);

        mViewPagerAdapter.addFragment(flippbiddUserFragments, "Flippbidd User");
        mViewPagerAdapter.addFragment(myContactUserFragments, "My Contacts");
        mViewViewPager.setAdapter(mViewPagerAdapter);
        mViewTabLyaout.setupWithViewPager(mViewViewPager);


        mViewViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case Constants.TABS.TAB_0:
                        ((FlippbiddUserFragments) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;
                    case Constants.TABS.TAB_1:
                        ((MyContactUserFragments) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

    }
}
