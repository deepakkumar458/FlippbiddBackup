package com.flippbidd.Model.Response.Feedback;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OwnerDetails implements Parcelable {

    @SerializedName("profile_id")
    @Expose
    private String profileId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("country_code")
    @Expose
    private String countryCode;
    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("profession")
    @Expose
    private String profession;
    @SerializedName("about_me")
    @Expose
    private String aboutMe;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("pof_doc")
    @Expose
    private String pofDoc;
    @SerializedName("doc_uploaded_date")
    @Expose
    private String docUploadedDate;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modify_date")
    @Expose
    private String modifyDate;
    @SerializedName("is_active")
    @Expose
    private String isActive;

    protected OwnerDetails(Parcel in) {
        profileId = in.readString();
        loginId = in.readString();
        profilePic = in.readString();
        fullName = in.readString();
        username = in.readString();
        countryCode = in.readString();
        mobileNumber = in.readString();
        address = in.readString();
        state = in.readString();
        city = in.readString();
        timezone = in.readString();
        profession = in.readString();
        aboutMe = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        pofDoc = in.readString();
        docUploadedDate = in.readString();
        createdDate = in.readString();
        modifyDate = in.readString();
        isActive = in.readString();
    }

    public static final Creator<OwnerDetails> CREATOR = new Creator<OwnerDetails>() {
        @Override
        public OwnerDetails createFromParcel(Parcel in) {
            return new OwnerDetails(in);
        }

        @Override
        public OwnerDetails[] newArray(int size) {
            return new OwnerDetails[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(profileId);
        dest.writeString(loginId);
        dest.writeString(profilePic);
        dest.writeString(fullName);
        dest.writeString(username);
        dest.writeString(countryCode);
        dest.writeString(mobileNumber);
        dest.writeString(address);
        dest.writeString(state);
        dest.writeString(city);
        dest.writeString(timezone);
        dest.writeString(profession);
        dest.writeString(aboutMe);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(pofDoc);
        dest.writeString(docUploadedDate);
        dest.writeString(createdDate);
        dest.writeString(modifyDate);
        dest.writeString(isActive);
    }
}
