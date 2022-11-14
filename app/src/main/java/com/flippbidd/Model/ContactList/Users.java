package com.flippbidd.Model.ContactList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Users implements Serializable {

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getNickname() {
        return name;
    }

    public void setNickname(String name) {
        this.name = name;
    }

    public String getUserId() {
        return emailAddress;
    }

    public void setUserId(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getProfilePic() {
        return profilePic;
    }
}
