package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DealDetailsResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("data")
    @Expose
    private DealDetails data;

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

    public DealDetails getData() {
        return data;
    }

    public void setData(DealDetails data) {
        this.data = data;
    }
}
