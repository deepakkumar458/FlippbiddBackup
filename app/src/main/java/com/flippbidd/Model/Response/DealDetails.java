package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DealDetails {

    @SerializedName("deal_id")
    @Expose
    private String deal_id;

    @SerializedName("login_id")
    @Expose
    private String login_id;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("lat")
    @Expose
    private String lat;

    @SerializedName("lang")
    @Expose
    private String lang;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("state_id")
    @Expose
    private String state_id;


    @SerializedName("city_id")
    @Expose
    private String city_id;


    @SerializedName("is_notify")
    @Expose
    private String is_notify;


    @SerializedName("is_delete")
    @Expose
    private String is_delete;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("is_active")
    @Expose
    private String is_active;

    @SerializedName("other_users")
    @Expose
    private ArrayList<OtherUserList> otherUserList;

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getIs_notify() {
        return is_notify;
    }

    public void setIs_notify(String is_notify) {
        this.is_notify = is_notify;
    }

    public String getIs_delete() {
        return is_delete;
    }

    public void setIs_delete(String is_delete) {
        this.is_delete = is_delete;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }

    public ArrayList<OtherUserList> getOtherUserList() {
        return otherUserList;
    }

    public void setOtherUserList(ArrayList<OtherUserList> otherUserList) {
        this.otherUserList = otherUserList;
    }
}
