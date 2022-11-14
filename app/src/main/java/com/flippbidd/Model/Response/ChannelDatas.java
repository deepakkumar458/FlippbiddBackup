package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class ChannelDatas implements Serializable {

    @SerializedName("channel_main_id")
    @Expose
    private String channelMainId;
    @SerializedName("channel_id")
    @Expose
    private String channelId;
    @SerializedName("owner_id")
    @Expose
    private String ownerId;
    @SerializedName("buyer_id")
    @Expose
    private String buyerId;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("is_active")
    @Expose
    private String isActive;

    public String getChannelMainId() {
        return channelMainId;
    }

    public void setChannelMainId(String channelMainId) {
        this.channelMainId = channelMainId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBuyerId() {
        return buyerId;
    }

    public void setBuyerId(String buyerId) {
        this.buyerId = buyerId;
    }

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }


}
