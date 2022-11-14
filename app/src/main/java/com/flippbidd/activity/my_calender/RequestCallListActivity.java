package com.flippbidd.activity.my_calender;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.flippbidd.Model.Response.calendardata.CalendarResponse;
import com.flippbidd.R;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class RequestCallListActivity extends AppCompatActivity implements SecondFragment.MyCalendarItem {

    private Context moContext;
    private AppBarConfiguration appBarConfiguration;
    NavController navController;
    MaterialTextView txtHeaderMyCalendar;
    AppCompatImageView imageBellIcon, tvNotificationView;
    Toolbar toolbar;

    MyCalendarItemList moMyCalendarItem;

    public interface MyCalendarItemList {
        void onRefreshData(List<CalendarResponse.Datum> moJsonArrayList);
    }

    public void setMyCalendarItemList(MyCalendarItemList moMyCalendarItem) {
        this.moMyCalendarItem = moMyCalendarItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        statusBarColorChanged();
        super.onCreate(savedInstanceState);
        this.moContext = RequestCallListActivity.this;
        setContentView(R.layout.activity_request_call_list);
        toolbar = findViewById(R.id.toolbar);
        txtHeaderMyCalendar = findViewById(R.id.txtHeaderMyCalendar);
        tvNotificationView = findViewById(R.id.tvNotificationView);
        imageBellIcon = findViewById(R.id.imageBellIcon);
//        setSupportActionBar(toolbar);
//        setBackButton();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_request_call_list);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);


        findViewById(R.id.imageRequestCallListBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (navController.popBackStack()) {
                    toolbarSet(true);
                } else {
                    onBackPressed();
                }
            }
        });

        imageBellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toolbarSet(false);
                navController.navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    public void toolbarSet(boolean isValue) {
        if (isValue) {
            imageBellIcon.setVisibility(View.VISIBLE);
            txtHeaderMyCalendar.setText(moContext.getResources().getString(R.string.second_fragment_label));
        } else {
            imageBellIcon.setVisibility(View.GONE);
            tvNotificationView.setVisibility(View.GONE);
            txtHeaderMyCalendar.setText(moContext.getResources().getString(R.string.first_fragment_label));
        }
    }

    private void statusBarColorChanged() {
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.parseColor("#3FA3D1"));
    }

    protected void updateCounts(String counts) {
        if (counts.equalsIgnoreCase("0")) {
            tvNotificationView.setVisibility(View.GONE);
            return;
        }
        tvNotificationView.setVisibility(View.VISIBLE);
        imageBellIcon.setVisibility(View.GONE);

//        tvNotificationCounts.setText(counts);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_request_call_list);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.call_notification, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_main_notification:
                navController.navigate(R.id.action_SecondFragment_to_FirstFragment);
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("TAG", "test");
    }

    @Override
    public void onRefreshData(List<CalendarResponse.Datum> moJsonArrayList) {
        Log.e("TAG", "onRefreshData");
        if (moMyCalendarItem != null) {
            moMyCalendarItem.onRefreshData(moJsonArrayList);
        }
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag("call");
//        if (fragment instanceof MyCallListFragment) {
//            ((MyCallListFragment) fragment).onRefreshData(moJsonArrayList);
//        }

    }

    @Override
    public void onClear() {
    }
}