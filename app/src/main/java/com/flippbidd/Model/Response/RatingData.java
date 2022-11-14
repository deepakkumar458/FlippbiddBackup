package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RatingData implements Serializable {

    @SerializedName("rating_id")
    @Expose
    private String ratingId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("rate1")
    @Expose
    private String rate1;
    @SerializedName("rate2")
    @Expose
    private String rate2;
    @SerializedName("rate3")
    @Expose
    private String rate3;
    @SerializedName("rate4")
    @Expose
    private String rate4;
    @SerializedName("rate5")
    @Expose
    private String rate5;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modify_date")
    @Expose
    private String modifyDate;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("UserDetails")
    @Expose
    private UserDetails userDetails;

    public String getRatingId() {
        return ratingId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getUserId() {
        return userId;
    }

    public String getRate1() {
        return rate1;
    }

    public String getRate2() {
        return rate2;
    }

    public String getRate3() {
        return rate3;
    }

    public String getRate4() {
        return rate4;
    }

    public String getRate5() {
        return rate5;
    }

    public String getComment() {
        return comment;
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

    public UserDetails getUserDetails() {
        return userDetails;
    }
}
