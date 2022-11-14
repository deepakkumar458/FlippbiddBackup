package com.flippbidd.Model.Response.RequestData;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class RequestProfileData implements Serializable {

    @SerializedName("folder_req_id")
    @Expose
    private String folderReqId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("folder_data")
    @Expose
    private String folderData;
    @SerializedName("is_other")
    @Expose
    private String isOther;
    @SerializedName("other")
    @Expose
    private String other;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("is_view")
    @Expose
    private Integer isView;
    @SerializedName("UserDetails")
    @Expose
    private ProfileData userDetails;

    public String getFolderReqId() {
        return folderReqId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public String getFolderData() {
        return folderData;
    }

    public String getIsOther() {
        return isOther;
    }

    public String getOther() {
        return other;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getIsActive() {
        return isActive;
    }

    public Integer getIsView() {
        return isView;
    }

    public ProfileData getUserDetails() {
        return userDetails;
    }
}
