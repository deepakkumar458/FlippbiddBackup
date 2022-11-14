package com.flippbidd.activity.my_calender;

import android.os.Parcel;
import android.os.Parcelable;

import com.applandeo.materialcalendarview.EventDay;

import java.util.List;

public class CalendarModelData implements Parcelable {

    private String meetingID;
    private String userName;
    private String callingType;
    private String meetingDate;
    private String meetingStartTime;
    private String meetingEndTime;
    private String address;
    private List<EventDay> eventData;

    public String getMeetingID() {
        return meetingID;
    }

    public String getUserName() {
        return userName;
    }

    public String getCallingType() {
        return callingType;
    }

    public String getMeetingDate() {
        return meetingDate;
    }

    public String getMeetingStartTime() {
        return meetingStartTime;
    }

    public String getMeetingEndTime() {
        return meetingEndTime;
    }

    public String getAddress() {
        return address;
    }

    public List<EventDay> getEventData() {
        return eventData;
    }

    public static Creator<CalendarModelData> getCREATOR() {
        return CREATOR;
    }

    public CalendarModelData(String meetingID, String userName, String callingType, String meetingDate, String meetingStartTime, String meetingEndTime, String address, List<EventDay> eventData) {
        this.meetingID = meetingID;
        this.userName = userName;
        this.callingType = callingType;
        this.meetingDate = meetingDate;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.address = address;
        this.eventData = eventData;
    }

    protected CalendarModelData(Parcel in) {
        meetingID = in.readString();
        userName = in.readString();
        callingType = in.readString();
        meetingDate = in.readString();
        meetingStartTime = in.readString();
        meetingEndTime = in.readString();
        address = in.readString();
    }

    public static final Creator<CalendarModelData> CREATOR = new Creator<CalendarModelData>() {
        @Override
        public CalendarModelData createFromParcel(Parcel in) {
            return new CalendarModelData(in);
        }

        @Override
        public CalendarModelData[] newArray(int size) {
            return new CalendarModelData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meetingID);
        dest.writeString(userName);
        dest.writeString(callingType);
        dest.writeString(meetingDate);
        dest.writeString(meetingStartTime);
        dest.writeString(meetingEndTime);
        dest.writeString(address);
    }
}
