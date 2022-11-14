package com.flippbidd.Model.Response.PropertyStateWise;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.DealData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class StatePropertyList implements Serializable {

    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("property_list")
    @Expose
    private List<CommonData> propertyList = null;
    @SerializedName("deal_list")
    @Expose
    private List<DealData> dealList = null;

    public String getState() {
        return state;
    }

    public List<CommonData> getPropertyList() {
        return propertyList;
    }

    public List<DealData> getDealList() {
        return dealList;
    }
}
