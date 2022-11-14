package com.flippbidd.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flippbidd.Fragments.Tabs.TabProperty;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.flippbidd.Adapter.Pager.TabFragments;
import com.flippbidd.Fragments.UploadsTab.TabUploadProperty;
import com.flippbidd.Fragments.UploadsTab.TabUploadRental;
import com.flippbidd.Fragments.UploadsTab.TabUploadService;
import com.flippbidd.MainActivity;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseFragment;


public class MyUploads extends BaseFragment {

    public static final String TAG = LogUtils.makeLogTag(MyUploads.class);

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_my_likes_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainActivity.mainTabBar.setVisibility(View.GONE);
        MainActivity.showIcon(2, Constants.SELECTION_HEADER_TITLE.TITLE_MY_PROPERTIRS);

        initsView();
    }

    private void initsView() {

        // Begin the transaction
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.like_content_frame, new TabUploadProperty());
        ft.commit();

    }

}
