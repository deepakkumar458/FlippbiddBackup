package com.flippbidd.Model.Response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class RecentlySearchResponse {

    @SerializedName("success")
    @Expose
    public Boolean success;
    @SerializedName("text")
    @Expose
    public String text;
    @SerializedName("data")
    @Expose
    public List<DealData> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public List<DealData> getData() {
        return data;
    }

}
