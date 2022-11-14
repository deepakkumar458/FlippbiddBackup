package com.flippbidd.Model.request;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChatUser {

    @SerializedName("nickname")
    @Expose
    private String nickname;

    public ChatUser(String nickname) {
        this.nickname = nickname;
    }
}
