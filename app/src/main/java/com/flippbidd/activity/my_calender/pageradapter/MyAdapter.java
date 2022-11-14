package com.flippbidd.activity.my_calender.pageradapter;

import android.content.Context;

import com.flippbidd.activity.my_calender.FirstFragment;
import com.flippbidd.activity.my_calender.MyCallListFragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MyAdapter extends FragmentPagerAdapter {
    private Context myContext;
    FragmentManager fragmentManager;
    int totalTabs;

    public MyAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        fragmentManager = fm;
        myContext = context;
        this.totalTabs = totalTabs;
    }

    // this is for fragment tabs
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FirstFragment requestList = new FirstFragment();
                return requestList;
            case 1:
                MyCallListFragment myCallListFragment = new MyCallListFragment();
                return myCallListFragment;

            default:
                return null;
        }
    }

    // this counts total number of tabs
    @Override
    public int getCount() {
        return totalTabs;
    }
}
