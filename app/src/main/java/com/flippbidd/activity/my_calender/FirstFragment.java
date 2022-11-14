package com.flippbidd.activity.my_calender;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import com.flippbidd.Adapter.callrequest.CallRequestListAdapter;
import com.flippbidd.Bottoms.MeetingScheduleSheet;
import com.flippbidd.Bottoms.OwnerBottomSheetDialogFragment;
import com.flippbidd.Bottoms.UserBottomSheetDialogFragment;
import com.flippbidd.Model.ApiFactory;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.RxJava2ApiCallHelper;
import com.flippbidd.Model.RxJava2ApiCallback;
import com.flippbidd.Model.Service.UserServices;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.LogUtils;
import com.flippbidd.Others.NetworkUtils;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.groupchannel.GroupInfoActivity;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.ToastUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.refactor.lib.colordialog.PromptDialog;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;

public class FirstFragment extends Fragment implements CallRequestListAdapter.onItemClickLisnear {

    private RelativeLayout moRelativeNoDataView;
    private Context moContext;
    private ProgressBar mProgressBar;
    private RecyclerView recyclerViewRequestCallList;
    LinearLayoutManager layoutManager;
    CallRequestListAdapter callRequestAdapterList;

    private Disposable disposable;
    List<JsonElement> moJsonArrayList = new ArrayList<JsonElement>();


    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat dateFormatter1 = new SimpleDateFormat("MM-dd-yyyy");
//    Calendar dateSelected = Calendar.getInstance();
    private String date = "", start_time = "", end_time = "";
    private int selectedS_H = 0, selectedS_M = 0;
    private String selectedEndTime, selectedStartTime;

    Calendar calendarSelected = Calendar.getInstance();

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        moContext = context;
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //define if of UI
        recyclerViewRequestCallList = view.findViewById(R.id.recyclerViewRequestCallList);
        moRelativeNoDataView = view.findViewById(R.id.relativeNoDataView);
        mProgressBar = view.findViewById(R.id.progressBarView);
        //init layout
        callRequestAdapterList = new CallRequestListAdapter(moContext, moJsonArrayList, false, "");
        layoutManager = new LinearLayoutManager(moContext);
        recyclerViewRequestCallList.setLayoutManager(layoutManager);
        recyclerViewRequestCallList.setAdapter(callRequestAdapterList);
        recyclerViewRequestCallList.addItemDecoration(new DividerItemDecoration(moContext, DividerItemDecoration.VERTICAL));
        callRequestAdapterList.setItemOnClickEvent(this::onClickEvent);

        callGetAllCallRequestList();
    }

    /*all api for getting list of request call*/
    private void callGetAllCallRequestList() {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            openErorrDialog(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token, login_id, common_type, common_id, limit, offset
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        documentRequest.put("limit", RequestBody.create(MediaType.parse("multipart/form-data"), "100"));
        documentRequest.put("offset", RequestBody.create(MediaType.parse("multipart/form-data"), "0"));


        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable = userService.requestMeetingScheduleList(documentRequest);

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                hideProgressDialog();

                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    if (moJsonArrayList != null) {
                        moJsonArrayList.clear();
                    }

                    //show response of data list
                    JsonArray jsonArray = mJsonObject.get("data").getAsJsonArray();
                    for (int arr = 0; arr < jsonArray.size(); arr++) {
                        moJsonArrayList.add(jsonArray.get(arr));
                    }
                    //array add
                    callRequestAdapterList.addData(moJsonArrayList);

                    //show recyclerview
                    recyclerViewRequestCallList.setVisibility(View.VISIBLE);
                    //Hide no data view
                    moRelativeNoDataView.setVisibility(View.GONE);

                } else {
//                    openErorrDialog(mJsonObject.get("text").getAsString());

                    //hide recyclerview
                    recyclerViewRequestCallList.setVisibility(View.GONE);
                    //show no data view
                    moRelativeNoDataView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                hideProgressDialog();
            }
        });
    }

    //Error Dialog
    public void openErorrDialog(String text) {
        PromptDialog mPromptDialog = new PromptDialog(moContext);
        mPromptDialog.setCanceledOnTouchOutside(false);
        mPromptDialog.setDialogType(PromptDialog.DIALOG_TYPE_WRONG);
        mPromptDialog.setAnimationEnable(true);
        mPromptDialog.setTitleText(moContext.getString(R.string.string_error));
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

    public void showProgressDialog(boolean isShow) {
        if (mProgressBar != null) {
            if (isShow) {
                mProgressBar.setVisibility(View.VISIBLE);
            } else {
                mProgressBar.setVisibility(View.GONE);
            }
        }
    }

    public void hideProgressDialog() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.GONE);
        }
    }


    @Override
    public void onClickEvent(int position, JsonElement mJsonObject, String mActionType, String priceValue) {
        JsonObject jsonObject = mJsonObject.getAsJsonObject();
        switch (mActionType) {
            case Constants
                    .ACCEPT:
                callUpdateStatus("1", jsonObject.get("meeting_id").getAsString());
                break;
            case Constants
                    .REJECT:
                callUpdateStatus("2", jsonObject.get("meeting_id").getAsString());
                break;
            case Constants
                    .NEWTIME:
                //open bottom sheet
                setDateTimeField(jsonObject, "admin");
                break;
            case Constants.ACTION_BOTTOM_SHEET_OWNER:
                openOwnerBottomSheet(jsonObject);
                break;
            case Constants.ACTION_BOTTOM_SHEET_USER:
                openUserBottomSheet(jsonObject);
                break;
        }
    }


    /*calender*/
    private void setDateTimeField(JsonObject jsonObject, String admin) {
        /*new*/
//        final Calendar cldr = Calendar.getInstance();
//        int day = cldr.get(Calendar.DAY_OF_MONTH);
//        int month = cldr.get(Calendar.MONTH);
//        int year = cldr.get(Calendar.YEAR);
//        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker dp, int year, int month, int dayOfMonth) {
//                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                Date strDate = null;
//                try {
//                    strDate = sdf.parse(dayOfMonth + "/" + (month + 1) + "/" + year);
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    if (strDate.compareTo(sdf.parse(sdf.format(new Date()))) > 0) {
//                        Log.i("app", "Date1 is after Date2");
//                    } else if (strDate.compareTo(sdf.parse(sdf.format(new Date()))) < 0) {
//                        Log.i("app", "Date1 is before Date2");
//                        ToastUtils.longToast(Constants.SELECT_DATE_ERROR);
//                        setDateTimeField(jsonObject, admin);
//                        return;
//                    } else if (strDate.compareTo(sdf.parse(sdf.format(new Date()))) == 0) {
//                        Log.i("app", "Date1 is equal to Date2");
//                    }
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//
//                dateSelected.set(year, month, dayOfMonth);
////                dateSelected.setTimeInMillis(dp.get());
//                //selected date
//                date = dateFormatter.format(dateSelected.getTime());
//                showStartTimePickerDialog(jsonObject, admin);
//            }
//        }, year, month, day);
//        datePickerDialog.setCancelable(false);
//        datePickerDialog.setCanceledOnTouchOutside(false);
//        datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
//        datePickerDialog.show();

        /**************************************************************************/
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendarSelected.set(year, month, dayOfMonth);
                date = dateFormatter.format(calendarSelected.getTime());
                showStartTimePickerDialog(jsonObject,admin);
            }
        }, year, month, day);
        datePickerDialog.setCanceledOnTouchOutside(false);
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(cldr.getTimeInMillis());
        datePickerDialog.show();

    }

    /*Start time picker*/
    public String showStartTimePickerDialog(JsonObject jsonObject, String admin) {
//        final Calendar c = Calendar.getInstance();
//        int hour = c.get(Calendar.HOUR_OF_DAY);
//        int minute = c.get(Calendar.MINUTE);
//        /*new code*/
//        /*new*/
//        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                boolean isPM = (hourOfDay >= 12);
//                start_time = String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");
//
//                selectedStartTime = hourOfDay + ":" + minute+ ":00";
//                selectedS_H = hourOfDay;
//                selectedS_M = minute;
//
//                //open end time picker
//                showEndTimePickerDialog(jsonObject, admin);
//            }
//        }, hour, minute, false);
//        timePickerDialog.setCancelable(false);
//        timePickerDialog.setCanceledOnTouchOutside(false);
//        timePickerDialog.show();

        /***********************************************************************/
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        /*new*/
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                /**********************************************/
                Calendar datetime = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                boolean isCurrentDate = false;
                try {
                    if (sdf.format(calendarSelected.getTime()).equalsIgnoreCase(sdf.format(datetime.getTime()))) {
                        isCurrentDate = true;
                    } else {
                        isCurrentDate = false;
                    }
                    if (isCurrentDate) {
                        //current date
                        Calendar c = Calendar.getInstance();
                        datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        datetime.set(Calendar.MINUTE, minute);
                        if (datetime.getTimeInMillis() >= c.getTimeInMillis()) {
                        } else {
                            //it's before current'
                            Toast.makeText(getActivity(), "Invalid Time", Toast.LENGTH_LONG).show();
                            showStartTimePickerDialog(jsonObject, admin);
                            return;
                        }
                    }

                    /*******************************************/
                    boolean isPM = (hourOfDay >= 12);
                    start_time = String.format("%02d:%02d %s", (hourOfDay == 12 || hourOfDay == 0) ? 12 : hourOfDay % 12, minute, isPM ? "PM" : "AM");
                    selectedStartTime = hourOfDay + ":" + minute + ":00";
                    selectedS_H = hourOfDay;
                    selectedS_M = minute;

                    //open end time picker
                    end_time = showEndTimePickerDialog(jsonObject, admin);

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }, hour, minute, false);
        timePickerDialog.setCanceledOnTouchOutside(false);
        timePickerDialog.setCancelable(false);
        timePickerDialog.show();
        return start_time;
    }

    public String showEndTimePickerDialog(JsonObject jsonObject, String admin) {
//        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
//        int hourOfDay, minute;
//        hourOfDay = selectedS_H;
//        minute = selectedS_M;
//
//        Date afterAdding30Mins = null;
//        Calendar newCal = Calendar.getInstance();
//        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
//        newCal.set(Calendar.MINUTE, minute);
//        newCal.set(Calendar.SECOND, 00);
//        String newTime="";
//        try {
//            afterAdding30Mins = new Date(newCal.getTimeInMillis() + (30 * 60 * 1000));
//            newTime = df.format(afterAdding30Mins.getTime());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        //after 30 minuts add new cal instance create
//        Calendar _30minCalendar = Calendar.getInstance();
//        _30minCalendar.setTime(afterAdding30Mins);
//        //end
//
//
//        boolean isPM = (_30minCalendar.get(Calendar.HOUR_OF_DAY) >= 12);
//        end_time = String.format("%02d:%02d %s", (_30minCalendar.get(Calendar.HOUR_OF_DAY) == 12 || _30minCalendar.get(Calendar.HOUR_OF_DAY) == 0) ? 12 : _30minCalendar.get(Calendar.HOUR_OF_DAY) % 12, _30minCalendar.get(Calendar.MINUTE), isPM ? "PM" : "AM");;
//
//        selectedEndTime = newTime;
//        Log.e("TAG", "End Time=>" + _30minCalendar.getTime());
//        Log.e("TAG", "New End Time=>" + end_time);

        /****************************************************************/
        SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss");
        int hourOfDay, minute;
        hourOfDay = selectedS_H;
        minute = selectedS_M;

        Date afterAdding30Mins = null;
        Calendar newCal = Calendar.getInstance();
        newCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        newCal.set(Calendar.MINUTE, minute);
        newCal.set(Calendar.SECOND, 00);
        String newTime = "";
        try {
            afterAdding30Mins = new Date(newCal.getTimeInMillis() + (30 * 60 * 1000));
            newTime = df.format(afterAdding30Mins.getTime());
        } catch (Exception e) {
            e.printStackTrace();
        }

        //after 30 minuts add new cal instance create
        Calendar _30minCalendar = Calendar.getInstance();
        _30minCalendar.setTime(afterAdding30Mins);
        boolean isPM = (_30minCalendar.get(Calendar.HOUR_OF_DAY) >= 12);
        end_time = String.format("%02d:%02d %s", (_30minCalendar.get(Calendar.HOUR_OF_DAY) == 12 || _30minCalendar.get(Calendar.HOUR_OF_DAY) == 0) ? 12 : _30minCalendar.get(Calendar.HOUR_OF_DAY) % 12, _30minCalendar.get(Calendar.MINUTE), isPM ? "PM" : "AM");
        selectedEndTime = newTime;
        //open new request alert dialog
        openRequestAlert(jsonObject, admin);


        return newTime;
    }

    private void openRequestAlert(JsonObject jsonObject, String admin) {
        //date conver log
        //date and time to convert UTC
        String convertUTC = localToUTC(date + " " + selectedStartTime);
        String convertEndUTC = localToUTC(date + " " + selectedEndTime);

        CommonDialogView.getInstance().showCommonDialog(getActivity(), "Flippbidd",
                getActivity().getResources().getString(R.string.request_new_from_admin_alert, jsonObject.get("type").getAsString(), dateFormatter1.format(calendarSelected.getTime()), start_time, end_time)
                , "Cancel"
                , "Done"
                , false, false, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        //new time status chaneg
                        callUpdateStatus("3", jsonObject.get("meeting_id").getAsString());
                        //add meeting for new request time
                        requestMS(getDateOnly(convertUTC), getTimeOnly(convertUTC), getTimeOnly(convertEndUTC), jsonObject.get("instruction").getAsString(), jsonObject.get("type").getAsString(), admin, jsonObject);
                    }

                    @Override
                    public void onDialogCancel(View view) {
                        if (callRequestAdapterList != null) {
                            callRequestAdapterList.notifyDataSetChanged();
                        }
                    }
                });
    }

    private boolean startEndTimeCompareTo() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        try {
            Date inTime = sdf.parse(start_time);
            Date outTime = sdf.parse(end_time);

            int dateDelta = inTime.compareTo(outTime);
            switch (dateDelta) {
                case 0:
                    //startTime and endTime not **Equal**
                    ToastUtils.shortToast("startTime and endTime not Equal");
                    return false;

                case 1:
                    //endTime is **Greater** then startTime
                    ToastUtils.shortToast("EndTime is Greater then startTime");
                    return false;

                case -1:
                    //startTime is **Greater** then endTime
//                    ToastUtils.shortToast("StartTime is Greater then endTime");
                    return true;
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void openOwnerBottomSheet(JsonObject jsonObject) {
        OwnerBottomSheetDialogFragment ownerBottomSheetDialogFragment = OwnerBottomSheetDialogFragment.newInstance();
        ownerBottomSheetDialogFragment.addListener(new OwnerBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onRequestEditClick() {
                ownerBottomSheetDialogFragment.dismiss();
                //edit request
                openBottomSheet(jsonObject, "user");
            }

            @Override
            public void onRequestCancelClick() {
                ownerBottomSheetDialogFragment.dismiss();
                requestDelete(jsonObject.get("meeting_id").getAsString());
            }

            @Override
            public void onCancelClick() {
                ownerBottomSheetDialogFragment.dismiss();
            }
        }, false);
        ownerBottomSheetDialogFragment.show(getChildFragmentManager(), FirstFragment.class.getSimpleName());
    }

    private String getTimeOnly(String outDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date value = formatter.parse(outDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm:ss"); //this format changeable
            outDate = dateFormatter.format(value);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            outDate = "00:00:00";
        }
        return outDate;
    }

    private String getDateOnly(String outDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date value = formatter.parse(outDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getDefault());
//            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            outDate = dateFormatter.format(value);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            outDate = "00:00:00";
        }
        return outDate;
    }

    private String localToUTC(String ourDate) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            formatter.setTimeZone(TimeZone.getDefault());
            Date value = formatter.parse(ourDate);

            SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //this format changeable
            dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            ourDate = dateFormatter.format(value);

            //Log.d("ourDate", ourDate);
        } catch (Exception e) {
            ourDate = "00-00-0000 00:00";
        }
        return ourDate;
    }


    private void openUserBottomSheet(JsonObject jsonObject) {
        UserBottomSheetDialogFragment userBottomSheetDialogFragment = UserBottomSheetDialogFragment.newInstance();
        userBottomSheetDialogFragment.addListener(new UserBottomSheetDialogFragment.DailogListener() {
            @Override
            public void onRequestAddToCalender() {
                userBottomSheetDialogFragment.dismiss();
                //add calendar event
                updateTCalendar(jsonObject);
            }

            @Override
            public void onRequestCancel() {
                userBottomSheetDialogFragment.dismiss();
                //open request alert dialog
                openRequestAlertDialog(jsonObject);

            }

            @Override
            public void onCancelClick() {
                userBottomSheetDialogFragment.dismiss();
            }
        }, false);
        userBottomSheetDialogFragment.show(getChildFragmentManager(), FirstFragment.class.getSimpleName());
    }

    private void openRequestAlertDialog(JsonObject jsonObject) {
        CommonDialogView.getInstance().showCommonDialog(moContext, "Cancel",
                "Are you sure you want to cancel this meeting?."
                , "No"
                , "Yes"
                , false, false, false, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        requestDelete(jsonObject.get("meeting_id").getAsString());
                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }

    private void updateTCalendar(JsonObject jsonObject) {
        String s_Date = jsonObject.get("date").getAsString() + " " + jsonObject.get("start_time").getAsString();
        long startDate = DateUtils.formatDateToLong("yyyy-MM-dd hh:mm:ss", "MM/dd/yy hh:mm:ss", s_Date);
        Log.e("Tag", "S Date" + s_Date);
        String e_Date = jsonObject.get("date").getAsString() + " " + jsonObject.get("end_time").getAsString();
        long endDate = DateUtils.formatDateToLong("yyyy-MM-dd hh:mm:ss", "MM/dd/yy hh:mm:ss", e_Date);
        Log.e("Tag", "E Date" + e_Date);

        addAppointmentsToCalender(getActivity(), jsonObject.get("OwnerDetails").getAsJsonObject().get("full_name").getAsString()
                , jsonObject.get("address").getAsString(), "Flippbidd", 1, startDate, endDate, true, false);

    }

    /*add to calendar event*/
    /*event add into cal*/
    public long addAppointmentsToCalender(Activity curActivity, String title,
                                          String desc, String place, int status, long startDate, long endDate,
                                          boolean needReminder, boolean needMailService) {
/***************** Event: add event *******************/
        long eventID = -1;
        try {
            String eventUriString = "content://com.android.calendar/events";
            ContentValues eventValues = new ContentValues();
            eventValues.put("calendar_id", 1); // id, We need to choose from
            // our mobile for primary its 1
            eventValues.put("title", title);
            eventValues.put("description", desc);
            eventValues.put("eventLocation", place);

//            long endDate_ = startDate + 1000 * 10 * 10; // For next 10min
            eventValues.put("dtstart", startDate);
            eventValues.put("dtend", endDate);

            // values.put("allDay", 1); //If it is bithday alarm or such
            // kind (which should remind me for whole day) 0 for false, 1
            // for true
            eventValues.put("eventStatus", status); // This information is
            // sufficient for most
            // entries tentative (0),
            // confirmed (1) or canceled
            // (2):
            eventValues.put("eventTimezone", "UTC/GMT +5:30");
            /*
             * Comment below visibility and transparency column to avoid
             * java.lang.IllegalArgumentException column visibility is invalid
             * error
             */
            // eventValues.put("allDay", 1);
            // eventValues.put("visibility", 0); // visibility to default (0),
            // confidential (1), private
            // (2), or public (3):
            // eventValues.put("transparency", 0); // You can control whether
            // an event consumes time
            // opaque (0) or transparent (1).

            eventValues.put("hasAlarm", 1); // 0 for false, 1 for true

            Uri eventUri = curActivity.getApplicationContext()
                    .getContentResolver()
                    .insert(Uri.parse(eventUriString), eventValues);
            eventID = Long.parseLong(eventUri.getLastPathSegment());

            if (needReminder) {
                /***************** Event: Reminder(with alert) Adding reminder to event ***********        ********/

                String reminderUriString = "content://com.android.calendar/reminders";
                ContentValues reminderValues = new ContentValues();
                reminderValues.put("event_id", eventID);
                reminderValues.put("minutes", 5); // Default value of the
                // system. Minutes is a integer
                reminderValues.put("method", 1); // Alert Methods: Default(0),
                // Alert(1), Email(2),SMS(3)

                Uri reminderUri = curActivity.getApplicationContext()
                        .getContentResolver()
                        .insert(Uri.parse(reminderUriString), reminderValues);
            }

/***************** Event: Meeting(without alert) Adding Attendies to the meeting *******************/

            if (needMailService) {
                String attendeuesesUriString = "content://com.android.calendar/attendees";
                /********
                 * To add multiple attendees need to insert ContentValues
                 * multiple times
                 ***********/
                ContentValues attendeesValues = new ContentValues();
                attendeesValues.put("event_id", eventID);
                attendeesValues.put("attendeeName", "Bhavish Solanki"); // Attendees name
                attendeesValues.put("attendeeEmail", "bhavish.hcuboictech@gmail.com");// Attendee Email
                attendeesValues.put("attendeeRelationship", 0); // Relationship_Attendee(1),
                // Relationship_None(0),
                // Organizer(2),
                // Performer(3),
                // Speaker(4)
                attendeesValues.put("attendeeType", 0); // None(0), Optional(1),
                // Required(2),
                // Resource(3)
                attendeesValues.put("attendeeStatus", 0); // NOne(0),
                // Accepted(1),
                // Decline(2),
                // Invited(3),
                // Tentative(4)

                Uri eventsUri = Uri.parse("content://calendar/events");
                Uri url = curActivity.getApplicationContext()
                        .getContentResolver()
                        .insert(eventsUri, attendeesValues);

                 /*Uri attendeuesesUri = curActivity.getApplicationContext()
                 .getContentResolver()
                 .insert(Uri.parse(attendeuesesUriString), attendeesValues);*/
            }
        } catch (Exception ex) {
            Log.e("Error :", ex.getMessage());
        }

        return eventID;

    }


    /*open meeting schedule sheet*/
    private void openBottomSheet(JsonObject jsonObject, String createdTYPE) {
        MeetingScheduleSheet meetingScheduleSheet = MeetingScheduleSheet.newInstance();
        meetingScheduleSheet.addListener(new MeetingScheduleSheet.DialogListener() {
            @Override
            public void scheduleMeetingRequest(String selectedDate, String selectedStartTime, String selectedEndTime, String mMessage, String selectedType, String createdType) {
                meetingScheduleSheet.dismiss();
                requestMS(selectedDate, selectedStartTime, selectedEndTime, mMessage, selectedType, createdType, jsonObject);
            }

            @Override
            public void onCancelClick() {
                meetingScheduleSheet.dismiss();
            }
        }, createdTYPE, jsonObject, null);
        meetingScheduleSheet.show(getChildFragmentManager(), GroupInfoActivity.class.getSimpleName());
    }

    /*request meeting schedule call api*/
    private void requestMS(String date, String startTime, String endTime, String message, String type, String created_type, JsonObject jsonObject) {
        if (!NetworkUtils.isInternetAvailable(moContext)) {
            ToastUtils.longToast(getString(R.string.no_internet));
            return;
        }
        showProgressDialog(true);

        //token, property_id, owner_id, login_id, date, start_time, end_time, instruction, type
        LinkedHashMap<String, RequestBody> documentRequest = new LinkedHashMap<String, RequestBody>();
        documentRequest.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        documentRequest.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));

        if (!created_type.equalsIgnoreCase("admin")) {
            documentRequest.put("meeting_id", RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.get("meeting_id").getAsString()));
            documentRequest.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.get("owner_id").getAsString()));
        } else {
            documentRequest.put("owner_id", RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.get("login_id").getAsString()));
        }

        documentRequest.put("property_id", RequestBody.create(MediaType.parse("multipart/form-data"), jsonObject.get("property_id").getAsString()));
        documentRequest.put("date", RequestBody.create(MediaType.parse("multipart/form-data"), date));
        documentRequest.put("start_time", RequestBody.create(MediaType.parse("multipart/form-data"), startTime));
        documentRequest.put("end_time", RequestBody.create(MediaType.parse("multipart/form-data"), endTime));
        documentRequest.put("instruction", RequestBody.create(MediaType.parse("multipart/form-data"), message));
        documentRequest.put("type", RequestBody.create(MediaType.parse("multipart/form-data"), type));
        documentRequest.put("created_type", RequestBody.create(MediaType.parse("multipart/form-data"), created_type));


        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<JsonElement> observable;
        if (!created_type.equalsIgnoreCase("admin")) {
            observable = userService.editMeetingSchedule(documentRequest);
        } else {
            observable = userService.requestMeetingSchedule(documentRequest);
        }

        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<JsonElement>() {
            @Override
            public void onSuccess(JsonElement response) {
                showProgressDialog(false);
                JsonObject mJsonObject = response.getAsJsonObject();
                if (mJsonObject.get("success").getAsBoolean()) {
                    //success message
                    if (mJsonObject.has("text")) {
                        //get list
                        callGetAllCallRequestList();
                        //open alert message
                        openAlertDialog(mJsonObject.get("text").getAsString(), "3");
                    }
                } else {
                    //error messsage
                    openAlertDialog(mJsonObject.get("text").getAsString(), "3");
                }
            }

            @Override
            public void onFailed(Throwable throwable) {
                showProgressDialog(false);
            }
        });
    }

    private void openAlertDialog(String s, String statusCode) {
        CommonDialogView.getInstance().showCommonDialog(moContext, "",
                s
                , "Ok"
                , "Ok"
                , false, true, true, false, false, new CommonDialogCallBack() {
                    @Override
                    public void onDialogYes(View view) {
                        if (!statusCode.equalsIgnoreCase("3")) {
                            ((RequestCallListActivity) moContext).onBackPressed();
                        }

                    }

                    @Override
                    public void onDialogCancel(View view) {
                    }
                });
    }

    /*end*/

    //request update
    private void callUpdateStatus(String status, String meeting_id) {
        //token, meeting_id, login_id, status
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("meeting_id", RequestBody.create(MediaType.parse("multipart/form-data"), meeting_id));
        linkedHashMap.put("status", RequestBody.create(MediaType.parse("multipart/form-data"), status));


        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.requestMeetingScheduleStatusUpdate(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                if (response.getSuccess()) {
                    if (!status.equalsIgnoreCase("3")) {
                        openAlertDialog(response.getText(), status);
                    }
                } else {
                    openAlertDialog(response.getText(), status);
                }
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }

    //request delete
    private void requestDelete(String meeting_id) {

        //token, meeting_id, login_id
        LinkedHashMap<String, RequestBody> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("token", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getToken()));
        linkedHashMap.put("login_id", RequestBody.create(MediaType.parse("multipart/form-data"), BaseApplication.getInstance().getLoginID()));
        linkedHashMap.put("meeting_id", RequestBody.create(MediaType.parse("multipart/form-data"), meeting_id));

        UserServices userService = ApiFactory.getInstance(moContext).provideService(UserServices.class);
        Observable<CommonResponse> observable;
        observable = userService.requestMeetingScheduleDelete(linkedHashMap);
        disposable = RxJava2ApiCallHelper.call(observable, new RxJava2ApiCallback<CommonResponse>() {
            @Override
            public void onSuccess(CommonResponse response) {
                LogUtils.LOGD("TAG", "onSuccess() called with: response = [" + response.toString() + "]");
                callGetAllCallRequestList();
                openAlertDialog(response.getText(), "3");
            }

            @Override
            public void onFailed(Throwable throwable) {

            }
        });
    }
}