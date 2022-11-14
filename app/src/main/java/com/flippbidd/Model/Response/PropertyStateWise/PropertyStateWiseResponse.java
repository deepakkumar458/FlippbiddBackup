package com.flippbidd.Model.Response.PropertyStateWise;

import com.flippbidd.Model.Response.TypeList.CommonListData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PropertyStateWiseResponse {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private Boolean text;
    @SerializedName("data")
    @Expose
    private List<StatePropertyList> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public Boolean getText() {
        return text;
    }

    public List<StatePropertyList> getData() {
        return data;
    }
}
