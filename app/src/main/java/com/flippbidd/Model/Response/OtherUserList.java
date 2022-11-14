package com.flippbidd.Model.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OtherUserList implements Parcelable {
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("username")
    @Expose
    private String userName;
    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("email_address")
    @Expose
    private String email_address;

    protected OtherUserList(Parcel in) {
        loginId = in.readString();
        fullName = in.readString();
        userName = in.readString();
        profilePic = in.readString();
        email_address=in.readString();
    }

    public static final Creator<OtherUserList> CREATOR = new Creator<OtherUserList>() {
        @Override
        public OtherUserList createFromParcel(Parcel in) {
            return new OtherUserList(in);
        }

        @Override
        public OtherUserList[] newArray(int size) {
            return new OtherUserList[size];
        }
    };

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getEmail_address() {
        return email_address;
    }

    public void setEmail_address(String email_address) {
        this.email_address = email_address;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(loginId);
        parcel.writeString(fullName);
        parcel.writeString(userName);
        parcel.writeString(profilePic);
        parcel.writeString(email_address);
    }
}
