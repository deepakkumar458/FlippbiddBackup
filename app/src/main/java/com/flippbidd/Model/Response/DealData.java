package com.flippbidd.Model.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DealData implements Parcelable {

    @SerializedName("deal_id")
    @Expose
    private String dealId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("is_notify")
    @Expose
    private String isNotify;
    @SerializedName("is_delete")
    @Expose
    private String isDelete;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("uploaded_date")
    @Expose
    private String uploadedDate;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("uploaded_login_id")
    @Expose
    private Integer uploadedLoginId;
    @SerializedName("uploaded_property_id")
    @Expose
    private Integer uploadedPropertyId;
    @SerializedName("uploaded_property_is_available")
    @Expose
    private Integer uploadedPropertyIsAvailable;

    @SerializedName("other_users")
    @Expose
    private List<OtherUserList> otherUserList = null;

    protected DealData(Parcel in) {
        dealId = in.readString();
        loginId = in.readString();
        address = in.readString();
        lat = in.readString();
        lang = in.readString();
        isNotify = in.readString();
        isDelete = in.readString();
        createdAt = in.readString();
        uploadedDate = in.readString();
        isActive = in.readString();
        if (in.readByte() == 0) {
            uploadedLoginId = null;
        } else {
            uploadedLoginId = in.readInt();
        }
        if (in.readByte() == 0) {
            uploadedPropertyId = null;
        } else {
            uploadedPropertyId = in.readInt();
        }
        if (in.readByte() == 0) {
            uploadedPropertyIsAvailable = null;
        } else {
            uploadedPropertyIsAvailable = in.readInt();
        }
        otherUserList = in.createTypedArrayList(OtherUserList.CREATOR);
    }

    public static final Creator<DealData> CREATOR = new Creator<DealData>() {
        @Override
        public DealData createFromParcel(Parcel in) {
            return new DealData(in);
        }

        @Override
        public DealData[] newArray(int size) {
            return new DealData[size];
        }
    };

    public String getDealId() {
        return dealId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getAddress() {
        return address;
    }

    public String getLat() {
        return lat;
    }

    public String getLang() {
        return lang;
    }

    public String getIsNotify() {
        return isNotify;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUploadedDate() {
        return uploadedDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public Integer getUploadedLoginId() {
        return uploadedLoginId;
    }

    public Integer getUploadedPropertyId() {
        return uploadedPropertyId;
    }

    public Integer getUploadedPropertyIsAvailable() {
        return uploadedPropertyIsAvailable;
    }

    public List<OtherUserList> getOtherUserList() {
        return otherUserList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(dealId);
        parcel.writeString(loginId);
        parcel.writeString(address);
        parcel.writeString(lat);
        parcel.writeString(lang);
        parcel.writeString(isNotify);
        parcel.writeString(isDelete);
        parcel.writeString(createdAt);
        parcel.writeString(uploadedDate);
        parcel.writeString(isActive);
        if (uploadedLoginId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(uploadedLoginId);
        }
        if (uploadedPropertyId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(uploadedPropertyId);
        }
        if (uploadedPropertyIsAvailable == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(uploadedPropertyIsAvailable);
        }
        parcel.writeTypedList(otherUserList);
    }
}
