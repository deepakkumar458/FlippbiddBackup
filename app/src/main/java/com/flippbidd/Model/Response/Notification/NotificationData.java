package com.flippbidd.Model.Response.Notification;

import com.flippbidd.Model.Response.Data.DetailsData;
import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationData {


    @SerializedName("noti_id")
    @Expose
    private String notiId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("common_id")
    @Expose
    private String commonId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("action_name")
    @Expose
    private String actionName;
    @SerializedName("action_date_time")
    @Expose
    private String actionDateTime;
    @SerializedName("is_status")
    @Expose
    private String isStatus;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
    @SerializedName("propertyDetails")
    @Expose
    private DetailsData propertyDetails;

    @SerializedName("userDetails")
    @Expose
    private UserDetails userDetails;

    public String getNotiId() {
        return notiId;
    }

    public void setNotiId(String notiId) {
        this.notiId = notiId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getCommonId() {
        return commonId;
    }

    public void setCommonId(String commonId) {
        this.commonId = commonId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public String getActionDateTime() {
        return actionDateTime;
    }

    public void setActionDateTime(String actionDateTime) {
        this.actionDateTime = actionDateTime;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public DetailsData getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(DetailsData propertyDetails) {
        this.propertyDetails = propertyDetails;
    }

    public String getIsStatus() {
        return isStatus;
    }

    public void setIsStatus(String isStatus) {
        this.isStatus = isStatus;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }
}
