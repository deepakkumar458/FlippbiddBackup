package com.flippbidd.activity.my_calender;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.flippbidd.CustomClass.CustomTextView;
import com.flippbidd.Model.Response.calendardata.CalendarResponse;
import com.flippbidd.R;
import com.flippbidd.activity.my_calender.pageradapter.SimpleAdapter;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyCallListFragment extends Fragment implements RequestCallListActivity.MyCalendarItemList {

    private RecyclerView recyclerViewRequestCallList;
    private CustomTextView noDataMessage;
    private RelativeLayout relativeNoDataView;
    LinearLayoutManager layoutManager;
    private Context moContext;
    private ProgressBar mProgressBar;
    SimpleAdapter simpleAdapter;
    List<CalendarResponse.Datum> moMyMeetingsList = new ArrayList<>();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        moContext = requireActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        relativeNoDataView = view.findViewById(R.id.relativeNoDataView);
        noDataMessage = view.findViewById(R.id.tvNoDataMessage);
        relativeNoDataView.setVisibility(View.VISIBLE);
        noDataMessage.setText("You have no scheduled meeting at this time.");
        recyclerViewRequestCallList = view.findViewById(R.id.recyclerViewRequestCallList);
        mProgressBar = view.findViewById(R.id.progressBarView);
        simpleAdapter = new SimpleAdapter(moContext, moMyMeetingsList);
        layoutManager = new LinearLayoutManager(moContext);
        recyclerViewRequestCallList.setLayoutManager(layoutManager);
        recyclerViewRequestCallList.setAdapter(simpleAdapter);

        //init interface
        ((RequestCallListActivity) moContext).setMyCalendarItemList(this);
    }

    @Override
    public void onRefreshData(List<CalendarResponse.Datum> moJsonArrayList) {
        Log.e("TAG", "onClear()");
        if (moJsonArrayList != null && moJsonArrayList.size() != 0) {
            recyclerViewRequestCallList.setVisibility(View.VISIBLE);
            relativeNoDataView.setVisibility(View.GONE);
            if (moMyMeetingsList != null) {
                moMyMeetingsList.clear();
            }
            moMyMeetingsList.addAll(moJsonArrayList);
            simpleAdapter.notifyDataSetChanged();
        } else {
            if (simpleAdapter != null)
                simpleAdapter.notifyDataSetChanged();
            recyclerViewRequestCallList.setVisibility(View.GONE);
            relativeNoDataView.setVisibility(View.VISIBLE);
            noDataMessage.setText("You have no scheduled meeting at this time.");
        }
    }
}
