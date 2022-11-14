package com.flippbidd.Model.Response.TypeList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CommonTypeResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private List<CommonListData> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public List<CommonListData> getData() {
        return data;
    }

    public void setData(List<CommonListData> data) {
        this.data = data;
    }
}
