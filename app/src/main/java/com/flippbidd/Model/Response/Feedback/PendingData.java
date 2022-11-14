package com.flippbidd.Model.Response.Feedback;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PendingData implements Parcelable {

    @SerializedName("meeting_id")
    @Expose
    private String meetingId;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("instruction")
    @Expose
    private String instruction;
    @SerializedName("created_type")
    @Expose
    private String createdType;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("feedback_off")
    @Expose
    private String feedbackOff;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("feedback_count")
    @Expose
    private String feedbackCount;
    @SerializedName("house_name")
    @Expose
    private String houseName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("no_of_beds")
    @Expose
    private String noOfBeds;
    @SerializedName("no_of_baths")
    @Expose
    private String noOfBaths;
    @SerializedName("propery_area")
    @Expose
    private String properyArea;
    @SerializedName("property_price")
    @Expose
    private String propertyPrice;
    @SerializedName("OwnerDetails")
    @Expose
    private OwnerDetails ownerDetails;
    @SerializedName("UserDetails")
    @Expose
    private UserDetails_1 userDetails;
    @SerializedName("property_image")
    @Expose
    private String propertyImage;

    protected PendingData(Parcel in) {
        meetingId = in.readString();
        propertyId = in.readString();
        ownerId = in.readString();
        loginId = in.readString();
        date = in.readString();
        startTime = in.readString();
        endTime = in.readString();
        type = in.readString();
        instruction = in.readString();
        createdType = in.readString();
        createdAt = in.readString();
        updatedAt = in.readString();
        status = in.readString();
        feedbackOff = in.readString();
        isActive = in.readString();
        feedbackCount = in.readString();
        houseName = in.readString();
        address = in.readString();
        noOfBeds = in.readString();
        noOfBaths = in.readString();
        properyArea = in.readString();
        propertyPrice = in.readString();
        ownerDetails = in.readParcelable(OwnerDetails.class.getClassLoader());
        userDetails = in.readParcelable(UserDetails_1.class.getClassLoader());
        propertyImage = in.readString();
    }

    public static final Creator<PendingData> CREATOR = new Creator<PendingData>() {
        @Override
        public PendingData createFromParcel(Parcel in) {
            return new PendingData(in);
        }

        @Override
        public PendingData[] newArray(int size) {
            return new PendingData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(meetingId);
        dest.writeString(propertyId);
        dest.writeString(ownerId);
        dest.writeString(loginId);
        dest.writeString(date);
        dest.writeString(startTime);
        dest.writeString(endTime);
        dest.writeString(type);
        dest.writeString(instruction);
        dest.writeString(createdType);
        dest.writeString(createdAt);
        dest.writeString(updatedAt);
        dest.writeString(status);
        dest.writeString(feedbackOff);
        dest.writeString(isActive);
        dest.writeString(feedbackCount);
        dest.writeString(houseName);
        dest.writeString(address);
        dest.writeString(noOfBeds);
        dest.writeString(noOfBaths);
        dest.writeString(properyArea);
        dest.writeString(propertyPrice);
        dest.writeParcelable(ownerDetails, flags);
        dest.writeParcelable(userDetails, flags);
        dest.writeString(propertyImage);
    }

    public String getMeetingId() {
        return meetingId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getType() {
        return type;
    }

    public String getInstruction() {
        return instruction;
    }

    public String getCreatedType() {
        return createdType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public String getFeedbackOff() {
        return feedbackOff;
    }

    public String getIsActive() {
        return isActive;
    }

    public String getFeedbackCount() {
        return feedbackCount;
    }

    public String getHouseName() {
        return houseName;
    }

    public String getAddress() {
        return address;
    }

    public String getNoOfBeds() {
        return noOfBeds;
    }

    public String getNoOfBaths() {
        return noOfBaths;
    }

    public String getProperyArea() {
        return properyArea;
    }

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public OwnerDetails getOwnerDetails() {
        return ownerDetails;
    }

    public UserDetails_1 getUserDetails() {
        return userDetails;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public static Creator<PendingData> getCREATOR() {
        return CREATOR;
    }
}
