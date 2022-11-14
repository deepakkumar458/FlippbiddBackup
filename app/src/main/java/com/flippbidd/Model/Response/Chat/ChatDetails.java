package com.flippbidd.Model.Response.Chat;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChatDetails implements Serializable{


//    {"success":true,"data":[{"sender_id":"53","receiver_id":"58","sender_qb_id":"58098540",
//            "receiver_qb_id":"58103319","dialog_id":"5b77f0f1a0eb4725c0507eef","dialog_type":"PRIVATE",
//            "status":"Active","username":"harsh00","profile_pic":"http:\/\/flippbidd.nop-theme.com\/uploads\/profiles\/4913.jpg",
//            "login_id":"58"}],"text":"List of dialog"}

    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("sender_qb_id")
    @Expose
    private String senderQbId;
    @SerializedName("receiver_qb_id")
    @Expose
    private String receiverQbId;
    @SerializedName("dialog_id")
    @Expose
    private String dialogId;
    @SerializedName("dialog_type")
    @Expose
    private String dialogType;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("profile_pic")
    @Expose
    private String profile;
    @SerializedName("login_id")
    @Expose
    private String userId;
    @SerializedName("common_username")
    @Expose
    private String commonUsername;
    @SerializedName("is_user_status")
    @Expose
    private String is_user_status;
    @SerializedName("block_status")
    @Expose
    private String isBlock;

    @SerializedName("last_message")
    @Expose
    private String lastMessage;
    @SerializedName("updated_time")
    @Expose
    private String lastUpdatedTime;

    @SerializedName("details")
    @Expose
    private CommonData details;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getSenderQbId() {
        return senderQbId;
    }

    public void setSenderQbId(String senderQbId) {
        this.senderQbId = senderQbId;
    }

    public String getReceiverQbId() {
        return receiverQbId;
    }

    public void setReceiverQbId(String receiverQbId) {
        this.receiverQbId = receiverQbId;
    }

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public String getDialogType() {
        return dialogType;
    }

    public void setDialogType(String dialogType) {
        this.dialogType = dialogType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getCommonUsername() {
        return commonUsername;
    }

    public void setCommonUsername(String commonUsername) {
        this.commonUsername = commonUsername;
    }

    public CommonData getDetails() {
        return details;
    }

    public void setDetails(CommonData details) {
        this.details = details;
    }

    public String isBlock() {
        return isBlock;
    }

    public void setBlock(String block) {
        isBlock = block;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIs_user_status() {
        return is_user_status;
    }

    public void setIs_user_status(String is_user_status) {
        this.is_user_status = is_user_status;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getLastUpdatedTime() {
        return lastUpdatedTime;
    }

    public void setLastUpdatedTime(String lastUpdatedTime) {
        this.lastUpdatedTime = lastUpdatedTime;
    }
}
