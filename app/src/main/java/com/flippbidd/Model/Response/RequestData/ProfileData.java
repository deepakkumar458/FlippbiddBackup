package com.flippbidd.Model.Response.RequestData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ProfileData implements Serializable {

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

    public String getProfileId() {
        return profileId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getProfession() {
        return profession;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPofDoc() {
        return pofDoc;
    }

    public String getDocUploadedDate() {
        return docUploadedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public String getIsActive() {
        return isActive;
    }
}
