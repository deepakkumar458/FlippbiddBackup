package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeleteAccountResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;

    @SerializedName("text")
    @Expose
    private String text;

}
