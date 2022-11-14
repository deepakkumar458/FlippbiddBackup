package com.flippbidd.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;


import com.flippbidd.Adapter.UsersListAdapter;
import com.flippbidd.Model.Response.DealData;
import com.flippbidd.Model.Response.OtherUserDetails;
import com.flippbidd.Model.Response.OtherUserList;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.flippbidd.R;
import com.flippbidd.databinding.ActivityUserListLayoutBinding;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class UserListActivity extends AppCompatActivity {

    ActivityUserListLayoutBinding binding;
    String invester_address ="",addressFromFMDBottomsheet="";
    DealData mDealData;
    List<OtherUserList> otherUserList = new ArrayList<>();
    Boolean isFromFMDPopUp = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUserListLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        System.out.println("Inside the UserLIst activity");
        setToolbarHeader();
        getIntentData();

        binding.tvHeader.setText(R.string.investor_name);

    }

    private void setToolbarHeader() {
        //toolbar
//        setSupportActionBar(binding.toolbarUserList);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.new_back);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setTitle("User List");
        binding.btnBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void getIntentData() {
        mDealData = (DealData) getIntent().getParcelableExtra("user_data");
        otherUserList = getIntent().getParcelableArrayListExtra("OtherUserDetails");
        isFromFMDPopUp = getIntent().getExtras().getBoolean("isFromFMDPopUp");
        addressFromFMDBottomsheet = getIntent().getExtras().getString("pass_address");
        //set in adapter
        UsersListAdapter usersListAdapter = null;
        if (mDealData != null) {
             invester_address = mDealData.getAddress().toString();
            binding.investorAddress.setText("Address: "+invester_address);
            System.out.println("inside the mdeal data");
            usersListAdapter = new UsersListAdapter(UserListActivity.this, mDealData.getOtherUserList(), isFromFMDPopUp);
        } else if (otherUserList != null) {
            System.out.println("received address ->"+addressFromFMDBottomsheet);
                binding.investorAddress.setText("Address: "+addressFromFMDBottomsheet);
            usersListAdapter = new UsersListAdapter(UserListActivity.this, otherUserList, isFromFMDPopUp);
        }

        binding.rvUserList.setLayoutManager(new LinearLayoutManager(UserListActivity.this));
        binding.rvUserList.setAdapter(usersListAdapter);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }
}
