package com.flippbidd.Model.Response.Feedback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PendingFeedback {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private List<PendingData> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public List<PendingData> getData() {
        return data;
    }
}
