package com.flippbidd.activity.my_calender;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnCalendarPageChangeListener;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.calendardata.CalendarResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.activity.my_calender.pageradapter.MyAdapter;
import com.flippbidd.baseclass.BaseApplication;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewpager.widget.ViewPager;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SecondFragment extends Fragment {

    private Context moContext;
    List<CalendarResponse.Datum> moJsonArrayList = new ArrayList<CalendarResponse.Datum>();
    Disposable disposable;
    View moView;
    CalendarView calendarView;
    List<EventDay> events = new ArrayList<>();
    List<CalendarModelData> saveData = new ArrayList<>();
    public int counts = 0;

    TabLayout tabLayout;
    ViewPager viewPager;

    MyCalendarItem moMyCalendarItem;

   public interface MyCalendarItem {
        void onRefreshData(List<CalendarResponse.Datum> moJsonArrayList);
        void onClear();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof MyCalendarItem) {
            moMyCalendarItem = (MyCalendarItem) context;
            moMyCalendarItem.onClear();
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MyCalendarItem");
        }
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        moContext = getActivity();
        moView = view;



        tabLayout=(TabLayout)view.findViewById(R.id.tabLayout);
        viewPager=(ViewPager)view.findViewById(R.id.viewPager);


        tabLayout.addTab(tabLayout.newTab().setText("Requests"));
        tabLayout.addTab(tabLayout.newTab().setText("Calls"));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        MyAdapter adapter = new MyAdapter(moContext, requireActivity().getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        view.findViewById(R.id.relativeViewRequests).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
                ((RequestCallListActivity) moContext).toolbarSet(false);
            }
        });

        eventAdd(moView);
        moMyCalendarItem.onClear();
    }

    private void eventAdd(View view) {
        calendarView = (CalendarView) view.findViewById(R.id.calendarView);
//        calendarView.setForwardButtonImage(moContext.getDrawable(R.drawable.ic_baseline_arrow_right_24));
//        calendarView.setPreviousButtonImage(moContext.getDrawable(R.drawable.ic_baseline_arrow_left_24));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(calendar.getTime());
//        calendarView.setMinimumDate(calendar);

        try {
            calendarView.setDate(calendar);
        } catch (OutOfDateRangeException e) {
            e.printStackTrace();
        }
//        calendarView.setSwipeEnabled(false);

        calendarView.setOnPreviousPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                callGetAllCallRequestList(getDates(calendarView.getCurrentPageDate().getTime()));
                Log.e("TAG", "Previous Months " + getDates(calendarView.getCurrentPageDate().getTime()));
            }
        });
        //by default current date and time
        callGetAllCallRequestList(getDates(calendarView.getCurrentPageDate().getTime()));
    }

    private String getDates(Date date) {
        String monthNumber = (String) DateFormat.format("dd-MM-yyyy", date); // 06
        return monthNumber;
    }

    /*all api for getting list of request call*/
    private void callGetAllCallRequestList(String dateValue) {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("date", RequestBody.create(MediaType.parse("multipart/form-data"), dateValue));

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.getListByDateFilter(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
//                hideProgressDialog();
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    Gson gson = new Gson();
                    CalendarResponse calendarResponse = gson.fromJson(response, CalendarResponse.class);
                    if (moJsonArrayList != null) {
                        moJsonArrayList.clear();
                    }

                    if (calendarResponse.getData() != null && calendarResponse.getData().size() != 0) {
                        moJsonArrayList.addAll(calendarResponse.getData());
                        updateCalendarEvent();
                    }
                } else {
//                    openErorrDialog(mJsonObject.get("text").getAsString());
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }

    @SuppressLint("RestrictedApi")
    private void updateCalendarEvent() {
        counts = 0;
        for (int cc = 0; cc < moJsonArrayList.size(); cc++) {
            if (moJsonArrayList.get(cc).getStatus().equalsIgnoreCase("1")) {
                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(getDate(moJsonArrayList.get(cc)));
                EventDay eventDay = new EventDay(calendar1, R.drawable.ic_red);
                eventDay.setEnabled(true);
                events.add(eventDay);
                //store data into custom model
                saveData.add(new CalendarModelData(moJsonArrayList.get(cc).getMeetingId()
                        , moJsonArrayList.get(cc).getUserDetails().getFullName()
                        , moJsonArrayList.get(cc).getType()
                        , moJsonArrayList.get(cc).getDate()
                        , moJsonArrayList.get(cc).getStartTime()
                        , moJsonArrayList.get(cc).getEndTime()
                        , moJsonArrayList.get(cc).getAddress()
                        , events));
            }
        }
        Log.e("TAG", "events " + events.size());
        //event save in calendar
        calendarView.setEnabled(true);
        calendarView.setEvents(events);

        //click view
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String selectedDate = format.format(eventDay.getCalendar().getTime());
                dataData(selectedDate);

            }
        });

        calendarView.setOnForwardPageChangeListener(new OnCalendarPageChangeListener() {
            @Override
            public void onChange() {
                callGetAllCallRequestList(getDates(calendarView.getCurrentPageDate().getTime()));
                Log.e("TAG", "Next Months " + getDates(calendarView.getCurrentPageDate().getTime()));
                adapterEmpty();
            }
        });

//        ((RequestCallListActivity) moContext).updateCounts(PreferenceUtils.getMettingCounts());
        //show current date and after five data record
        Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            String selectedDate = format.format(calendar.getTime());
            showUpdateFirstFiveData(selectedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void showUpdateFirstFiveData(String currentDate) {
        //"date": "2021-07-10"
        List<CalendarResponse.Datum> selectedData = new ArrayList<>();
        int eventCounts = 0;
        for (int cc = 0; cc < moJsonArrayList.size(); cc++) {
            if (eventCounts == 5) {
                break;
            }
            if (moJsonArrayList.get(cc).getStatus().equalsIgnoreCase("1")) {
//                eventCounts++;
                if (moJsonArrayList.get(cc).getDate().compareTo(currentDate) > 0) {
                    eventCounts++;
                    selectedData.add(moJsonArrayList.get(cc));
                } else if (moJsonArrayList.get(cc).getDate().compareTo(currentDate) == 0) {
                    //same date
                    eventCounts++;
                    selectedData.add(moJsonArrayList.get(cc));
                }
            }
        }
//        RecyclerView recyclerView = moView.findViewById(R.id.list_view);
//        MaterialTextView tvUpcoming = moView.findViewById(R.id.tvUpcoming);
//        if (selectedData != null && selectedData.size() != 0) {
//            tvUpcoming.setVisibility(View.VISIBLE);
//        } else {
//            tvUpcoming.setVisibility(View.GONE);
//        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(moContext));
//        recyclerView.setAdapter(new SimpleAdapter(moContext,selectedData));

        if(moMyCalendarItem!=null){
            moMyCalendarItem.onRefreshData(selectedData);
        }


    }

    private void adapterEmpty() {
        List<CalendarResponse.Datum> selectedData = new ArrayList<>();
//        RecyclerView recyclerView = moView.findViewById(R.id.list_view);
//        MaterialTextView tvUpcoming = moView.findViewById(R.id.tvUpcoming);
//        if (selectedData.size() != 0) {
//            tvUpcoming.setVisibility(View.VISIBLE);
//        } else {
//            tvUpcoming.setVisibility(View.GONE);
//        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(moContext));
//        recyclerView.addItemDecoration(new DividerItemDecoration(moContext, DividerItemDecoration.VERTICAL));
//        recyclerView.setAdapter(new SimpleAdapter(moContext,selectedData));

        if(moMyCalendarItem!=null){
            moMyCalendarItem.onRefreshData(selectedData);
        }
    }

    private void dataData(String selectedDate) {
        List<CalendarResponse.Datum> selectedData = new ArrayList<>();
        for (int cc = 0; cc < moJsonArrayList.size(); cc++) {
            if (moJsonArrayList.get(cc).getDate().equalsIgnoreCase(selectedDate)) {
                selectedData.add(moJsonArrayList.get(cc));
            }
        }
//        RecyclerView recyclerView = moView.findViewById(R.id.list_view);
//        MaterialTextView tvUpcoming = moView.findViewById(R.id.tvUpcoming);
//
//        if (selectedData.size() != 0) {
//            tvUpcoming.setVisibility(View.VISIBLE);
//        } else {
//            tvUpcoming.setVisibility(View.GONE);
//        }
//        recyclerView.setLayoutManager(new LinearLayoutManager(moContext));
//        recyclerView.addItemDecoration(new DividerItemDecoration(moContext, DividerItemDecoration.VERTICAL));
//        recyclerView.setAdapter(new SimpleAdapter(moContext,selectedData));

        if(moMyCalendarItem!=null){
            moMyCalendarItem.onRefreshData(selectedData);
        }
    }

    private Date getDate(CalendarResponse.Datum datum) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            String dateValue = datum.getDate() + "T" + datum.getStartTime() + "Z";
            SimpleDateFormat df_output = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            df_output.setTimeZone(TimeZone.getDefault());
            Date date = format.parse(dateValue);
            String newDate = df_output.format(date);
            Log.e("TAG","Convert Date==>"+format.parse(newDate));
            return format.parse(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //Error Dialog
    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(moContext);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_HELP);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(moContext.getString(R.string.app_name));
        mPromptDialog.setContentText(text);
        mPromptDialog.setPositiveListener(moContext.getString(R.string.string_ok), new PromptDialog.OnPositiveListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.setNegativeListener(getString(R.string.string_Cancel), new PromptDialog.OnNegativeListener() {
            @Override
            public void onClick(PromptDialog dialog) {
                dialog.dismiss();
            }
        });
        mPromptDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposeCall();
    }

    private void disposeCall() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }


//    class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.Holder> {
//
//        List<CalendarResponse.Datum> datumList = new ArrayList<>();
//
//        public SimpleAdapter(List<CalendarResponse.Datum> datumList) {
//            this.datumList = datumList;
//
//        }
//
//        @Override
//        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
//            return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_event_layout, parent, false));
//        }
//
//        @Override
//        public void onBindViewHolder(Holder holder, int position) {
//            CalendarResponse.Datum data = datumList.get(position);
//
//            Calendar calendar = Calendar.getInstance();
//            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//            String currentDate = format.format(calendar.getTime());
//            String fullName;
//            if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(data.getUserDetails().getLoginId())) {
//                fullName = data.getOwnerDetails().getFullName();
//            } else {
//                fullName = data.getUserDetails().getFullName();
//            }
//            holder.tv.setText(HtmlCompat.fromHtml(getResources().getString(R.string.cal_event_with_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", data.getDate() +" "+data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
////            if (currentDate.equalsIgnoreCase(data.getDate())) {
////                holder.tv.setText(HtmlCompat.fromHtml(getResources().getString(R.string.cal_event_without_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
////            } else {
////                holder.tv.setText(HtmlCompat.fromHtml(getResources().getString(R.string.cal_event_with_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", data.getDate() +" "+data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
////            }
//            if (data.getIsCallBtn() == 1) {
//                //Visible Button
//                holder.btnCallNow.setVisibility(View.VISIBLE);
//            } else {
//                //hide
//                holder.btnCallNow.setVisibility(View.GONE);
//            }
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    //compare date to current
//                    Log.e("TAG","Current Date"+currentDate);
//                    Log.e("TAG","Other Date"+data.getDate());
//                    if(data.getDate().compareTo(currentDate) < 0){
//                        CommonDialogView.getInstance().showCommonDialog(moContext, "",
//                                "This meeting has expired."
//                                , ""
//                                , "Ok"
//                                , false, true, true, false, false, new CommonDialogCallBack() {
//                                    @Override
//                                    public void onDialogYes(View view) {
//
//                                    }
//
//                                    @Override
//                                    public void onDialogCancel(View view) {
//                                    }
//                                });
//                    }else {
//                        CommonDialogView.getInstance().showCommonDialog(moContext, "",
//                                "Flippbidd will notify you 5 mins before your scheduled meeting."
//                                , ""
//                                , "Ok"
//                                , false, true, true, false, false, new CommonDialogCallBack() {
//                                    @Override
//                                    public void onDialogYes(View view) {
//
//                                    }
//
//                                    @Override
//                                    public void onDialogCancel(View view) {
//                                    }
//                                });
//                    }
//
//                }
//            });
//
//            holder.btnCallNow.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String calleeIds,callerName;
//                    if (data.getOwnerDetails() != null) {
//                        if (BaseApplication.getInstance().getQBLoginID().equalsIgnoreCase(data.getOwnerDetails().getEmailAddress())) {
//                            calleeIds = data.getUserDetails().getEmailAddress();
//                            callerName = data.getUserDetails().getFullName();
//                        } else {
//                            calleeIds = data.getOwnerDetails().getEmailAddress();
//                            callerName = data.getOwnerDetails().getFullName();
//                        }
//                        if (data.getType().equalsIgnoreCase("Audio Call")) {
//                            //call audio
//                            NewCallService.dial(moContext, callerName,calleeIds, false);
//                            PreferenceUtils.setCalleeId(calleeIds);
//
//                        } else if (data.getType().equalsIgnoreCase("Video Call")) {
//                            //call video
//                            NewCallService.dial(moContext, callerName,calleeIds, true);
//                            PreferenceUtils.setCalleeId(calleeIds);
//                        }
//                    }
//                }
//            });
//        }
//
//        @Override
//        public int getItemCount() {
//            return datumList.size();
//        }
//
//        class Holder extends RecyclerView.ViewHolder {
//
//            MaterialTextView tv;
//            MaterialTextView btnCallNow;
//
//            Holder(View itemView) {
//                super(itemView);
//                tv = itemView.findViewById(R.id.text_view);
//                btnCallNow = itemView.findViewById(R.id.btnCallNow);
//            }
//        }
//
//    }
}