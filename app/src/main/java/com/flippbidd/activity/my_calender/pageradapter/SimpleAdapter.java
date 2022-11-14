package com.flippbidd.activity.my_calender.pageradapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flippbidd.Model.Response.calendardata.CalendarResponse;
import com.flippbidd.R;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.dialog.CommonDialogView;
import com.flippbidd.interfaces.CommonDialogCallBack;
import com.flippbidd.sendbirdSdk.call.NewCallService;
import com.flippbidd.utils.DateUtils;
import com.flippbidd.utils.PreferenceUtils;
import com.google.android.material.textview.MaterialTextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import androidx.core.text.HtmlCompat;
import androidx.recyclerview.widget.RecyclerView;

public class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.Holder> {

    List<CalendarResponse.Datum> datumList = new ArrayList<>();
    Context mContext;

    public SimpleAdapter(Context moContext, List<CalendarResponse.Datum> datumList) {
        this.mContext = moContext;
        this.datumList = datumList;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_calendar_event_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {
        CalendarResponse.Datum data = datumList.get(position);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = format.format(calendar.getTime());
        String fullName;
        if (BaseApplication.getInstance().getLoginID().equalsIgnoreCase(data.getUserDetails().getLoginId())) {
            fullName = data.getOwnerDetails().getFullName();
        } else {
            fullName = data.getUserDetails().getFullName();
        }
        holder.tv.setText(HtmlCompat.fromHtml(mContext.getResources().getString(R.string.cal_event_with_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", data.getDate() +" "+data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
//            if (currentDate.equalsIgnoreCase(data.getDate())) {
//                holder.tv.setText(HtmlCompat.fromHtml(getResources().getString(R.string.cal_event_without_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
//            } else {
//                holder.tv.setText(HtmlCompat.fromHtml(getResources().getString(R.string.cal_event_with_date_messages, data.getType(), fullName, data.getAddress(), DateUtils.formateDateFromstring("yyyy-MM-dd HH:mm:ss", "MM/dd/yy", data.getDate() +" "+data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getStartTime()), DateUtils.timeFormat(data.getDate() + " " + data.getEndTime())), HtmlCompat.FROM_HTML_MODE_LEGACY));
//            }
        if (data.getIsCallBtn() == 1) {
            //Visible Button
            holder.btnCallNow.setVisibility(View.VISIBLE);
        } else {
            //hide
            holder.btnCallNow.setVisibility(View.GONE);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //compare date to current
                Log.e("TAG","Current Date"+currentDate);
                Log.e("TAG","Other Date"+data.getDate());
                if(data.getDate().compareTo(currentDate) < 0){
                    CommonDialogView.getInstance().showCommonDialog(mContext, "",
                            "This meeting has expired."
                            , ""
                            , "Ok"
                            , false, true, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {

                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });
                }else {
                    CommonDialogView.getInstance().showCommonDialog(mContext, "",
                            "Flippbidd will notify you 5 mins before your scheduled meeting."
                            , ""
                            , "Ok"
                            , false, true, true, false, false, new CommonDialogCallBack() {
                                @Override
                                public void onDialogYes(View view) {

                                }

                                @Override
                                public void onDialogCancel(View view) {
                                }
                            });
                }

            }
        });

        holder.btnCallNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String calleeIds,callerName;
                if (data.getOwnerDetails() != null) {
                    if (BaseApplication.getInstance().getQBLoginID().equalsIgnoreCase(data.getOwnerDetails().getEmailAddress())) {
                        calleeIds = data.getUserDetails().getEmailAddress();
                        callerName = data.getUserDetails().getFullName();
                    } else {
                        calleeIds = data.getOwnerDetails().getEmailAddress();
                        callerName = data.getOwnerDetails().getFullName();
                    }
                    if (data.getType().equalsIgnoreCase("Audio Call")) {
                        //call audio
                        NewCallService.dial(mContext, callerName,calleeIds, false);
                        PreferenceUtils.setCalleeId(calleeIds);

                    } else if (data.getType().equalsIgnoreCase("Video Call")) {
                        //call video
                        NewCallService.dial(mContext, callerName,calleeIds, true);
                        PreferenceUtils.setCalleeId(calleeIds);
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datumList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        MaterialTextView tv;
        MaterialTextView btnCallNow;

        Holder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.text_view);
            btnCallNow = itemView.findViewById(R.id.btnCallNow);
        }
    }

}
