package com.flippbidd.Model.Response.Service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceListResponse {


    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private List<ServiceListData> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<ServiceListData> getData() {
        return data;
    }

    public void setData(List<ServiceListData> data) {
        this.data = data;
    }


}
