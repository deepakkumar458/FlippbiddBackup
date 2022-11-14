package com.flippbidd.Model.ContactList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContactSycnResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("friend_request_list")
    @Expose
    private List<Users> friendRequestList = null;
    @SerializedName("invite_friend_list")
    @Expose
    private List<String> inviteFriendList = null;

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

    public List<Users> getFriendRequestList() {
        return friendRequestList;
    }

    public void setFriendRequestList(List<Users> friendRequestList) {
        this.friendRequestList = friendRequestList;
    }

    public List<String> getInviteFriendList() {
        return inviteFriendList;
    }

    public void setInviteFriendList(List<String> inviteFriendList) {
        this.inviteFriendList = inviteFriendList;
    }
}
