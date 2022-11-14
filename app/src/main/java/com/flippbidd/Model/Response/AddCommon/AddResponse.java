package com.flippbidd.Model.Response.AddCommon;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddResponse {

//    CommonResponse{"success":true,"text":"Property deleted successfully"}

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;

    @SerializedName("id")
    @Expose
    private int Id;

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

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }
}
