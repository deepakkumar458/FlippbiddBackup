package com.flippbidd.Model.Response.TypeList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonListData {


    @SerializedName("common_id")
    @Expose
    private String commonId;
    @SerializedName("common_name")
    @Expose
    private String commonName;
    @SerializedName("is_active")
    @Expose
    private String isActive;

    public String getCommonId() {
        return commonId;
    }

    public void setCommonId(String commonId) {
        this.commonId = commonId;
    }

    public String getCommonName() {
        return commonName;
    }

    public void setCommonName(String commonName) {
        this.commonName = commonName;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }
}
