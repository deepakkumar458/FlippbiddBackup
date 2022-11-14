package com.flippbidd.Model.Response.CommonList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonListResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private List<CommonData> data = null;

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

    public List<CommonData> getData() {
        return data;
    }

    public void setData(List<CommonData> data) {
        this.data = data;
    }

}



