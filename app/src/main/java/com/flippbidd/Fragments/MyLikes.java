package com.flippbidd.Fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.flippbidd.Adapter.Pager.TabFragments;
import com.flippbidd.Fragments.Tabs.TabProperty;
import com.flippbidd.Fragments.Tabs.TabRental;
import com.flippbidd.Fragments.Tabs.TabService;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseAppCompatActivity;
import com.flippbidd.baseclass.BaseFragment;
import butterknife.BindView;

public class MyLikes extends BaseFragment{

    public static final String TAG = LogUtils.makeLogTag(MyLikes.class);

//    @BindView(R.id.TabLayoutPropertyData)
//    TabLayout TabLayoutPropertyData;
//    @BindView(R.id.ViewpagerPropertyData)
//    ViewPager ViewpagerPropertyData;


//    TabFragments mViewPagerAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_my_likes_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initsView();
    }

    private void initsView() {
        // Begin the transaction
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.like_content_frame, new TabProperty(),"Like List");
        ft.commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = (Fragment) getChildFragmentManager().findFragmentByTag("Like List");
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}
