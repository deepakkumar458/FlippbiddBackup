package com.flippbidd.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;
import android.view.View;

import com.flippbidd.Adapter.Pager.TabFragments;
import com.flippbidd.CustomClass.CustomAppCompatButton;
import com.flippbidd.Fragments.TabFilter.TabFilterProperty;
import com.flippbidd.Fragments.TabFilter.TabFilterRental;
import com.flippbidd.Fragments.TabFilter.TabFilterServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseFragment;

import butterknife.BindView;
import butterknife.OnClick;

public class CreatedFileter extends BaseFragment {


    public static final String TAG = LogUtils.makeLogTag(CreatedFileter.class);

    //    @BindView(R.id.TabLayoutPropertyData)
//    TabLayout TabLayoutPropertyData;
    @BindView(R.id.ViewpagerPropertyData)
    ViewPager ViewpagerPropertyData;
    @BindView(R.id.btnPropertySelected)
    CustomAppCompatButton btnPropertySelected;
//    @BindView(R.id.btnRentalsSelected)
//    CustomAppCompatButton btnRentalsSelected;
//    @BindView(R.id.btnServiceSelected)
//    CustomAppCompatButton btnServiceSelected;


    TabFragments mViewPagerAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragments_created_fileter_layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initsView();
    }

    private void initsView() {


        mViewPagerAdapter = new TabFragments(mBaseAppCompatActivity.getSupportFragmentManager());

        TabFilterProperty propertyFragment = new TabFilterProperty();
       // TabFilterRental rentalFragment = new TabFilterRental();
       // TabFilterServices serviceFragment = new TabFilterServices();

        mViewPagerAdapter.addFragment(propertyFragment, mBaseAppCompatActivity.getString(R.string.String_title_property));
//        mViewPagerAdapter.addFragment(rentalFragment, mBaseAppCompatActivity.getString(R.string.String_title_rental));
//        mViewPagerAdapter.addFragment(serviceFragment, mBaseAppCompatActivity.getString(R.string.String_title_services));
        ViewpagerPropertyData.setAdapter(mViewPagerAdapter);
//        TabLayoutPropertyData.setupWithViewPager(ViewpagerPropertyData);


        ViewpagerPropertyData.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case Constants.TABS.TAB_0:
                        //propertyTabSet();
                        ((TabFilterProperty) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;
                   /* case Constants.TABS.TAB_1:
                        //rentalsTabSet();
                        ((TabFilterRental) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;
                    case Constants.TABS.TAB_2:
                        //serviceTabSet();
                        ((TabFilterServices) mViewPagerAdapter.getItem(position)).refresh(false);
                        break;*/
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        //propertyTabSet();
    }

   /* @OnClick({R.id.btnServiceSelected, R.id.btnPropertySelected, R.id.btnRentalsSelected})
    void viewClickEvents(View view) {
        switch (view.getId()) {
            case R.id.btnPropertySelected:
                //propertyTabSet();
                ViewpagerPropertyData.setCurrentItem(0, true);
                break;
            case R.id.btnRentalsSelected:
                //rentalsTabSet();
                ViewpagerPropertyData.setCurrentItem(1, true);
                break;
            case R.id.btnServiceSelected:
                //serviceTabSet();
                ViewpagerPropertyData.setCurrentItem(2, true);
                break;
        }
    }*/

    /*private void propertyTabSet() {
        btnServiceSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));
        btnPropertySelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.button_gradian));
        btnRentalsSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));

        btnServiceSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
        btnPropertySelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.colorWhite));
        btnRentalsSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
    }

    private void rentalsTabSet() {
        btnServiceSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));
        btnPropertySelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));
        btnRentalsSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.button_gradian));

        btnServiceSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
        btnPropertySelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
        btnRentalsSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.colorWhite));
    }

    private void serviceTabSet() {
        //selected button drawer
        btnServiceSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.button_gradian));
        btnPropertySelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));
        btnRentalsSelected.setBackground(mBaseAppCompatActivity.getResources().getDrawable(R.drawable.white_button_gradian));

        btnServiceSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.colorWhite));
        btnPropertySelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
        btnRentalsSelected.setTextColor(mBaseAppCompatActivity.getResources().getColor(R.color.titleText));
    }*/
}
