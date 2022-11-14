package com.flippbidd.Model.IPortal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Data {

    @SerializedName("iportal_id")
    @Expose
    private String iportal_id;

    @SerializedName("login_id")
    @Expose
    private String login_id;

    @SerializedName("referredBy")
    @Expose
    private String referredBy;

    @SerializedName("investorName")
    @Expose
    private String investorName;

    @SerializedName("companyName")
    @Expose
    private String companyName;

    @SerializedName("capitalOnHand")
    @Expose
    private String capitalOnHand;

    @SerializedName("attorneyName")
    @Expose
    private String attorneyName;

    @SerializedName("areaofIntrest")
    @Expose
    private String areaofIntrest;

    @SerializedName("states")
    @Expose
    private ArrayList<String> states;

    @SerializedName("is_nationwide")
    @Expose
    private String is_nationwide;

    @SerializedName("assetsType")
    @Expose
    private ArrayList<String> assetsType;


    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("address1_type")
    @Expose
    private String address1_type;

    @SerializedName("address2")
    @Expose
    private String address2;

    @SerializedName("address2_type")
    @Expose
    private String address2_type;

    @SerializedName("address3")
    @Expose
    private String address3;

    @SerializedName("address3_type")
    @Expose
    private String address3_type;

    @SerializedName("address4")
    @Expose
    private String address4;

    @SerializedName("address4_type")
    @Expose
    private String address4_type;

    @SerializedName("address5")
    @Expose
    private String address5;

    @SerializedName("address5_type")
    @Expose
    private String address5_type;

    @SerializedName("notes")
    @Expose
    private String notes;

    @SerializedName("created_at")
    @Expose
    private String created_at;

    @SerializedName("updated_at")
    @Expose
    private String updated_at;

    @SerializedName("is_active")
    @Expose
    private String is_active;

    @SerializedName("isApprove")
    @Expose
    private String isApprove;

    public String getIportal_id() {
        return iportal_id;
    }

    public void setIportal_id(String iportal_id) {
        this.iportal_id = iportal_id;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getLogin_id() {
        return login_id;
    }

    public void setLogin_id(String login_id) {
        this.login_id = login_id;
    }

    public String getReferredBy() {
        return referredBy;
    }

    public void setReferredBy(String referredBy) {
        this.referredBy = referredBy;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCapitalOnHand() {
        return capitalOnHand;
    }

    public void setCapitalOnHand(String capitalOnHand) {
        this.capitalOnHand = capitalOnHand;
    }

    public String getAttorneyName() {
        return attorneyName;
    }

    public void setAttorneyName(String attorneyName) {
        this.attorneyName = attorneyName;
    }

    public String getAreaofIntrest() {
        return areaofIntrest;
    }

    public void setAreaofIntrest(String areaofIntrest) {
        this.areaofIntrest = areaofIntrest;
    }

    public ArrayList<String> getStates() {
        return states;
    }

    public void setStates(ArrayList<String> states) {
        this.states = states;
    }

    public String getIs_nationwide() {
        return is_nationwide;
    }

    public void setIs_nationwide(String is_nationwide) {
        this.is_nationwide = is_nationwide;
    }

    public ArrayList<String> getAssetsType() {
        return assetsType;
    }

    public void setAssetsType(ArrayList<String> assetsType) {
        this.assetsType = assetsType;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress1_type() {
        return address1_type;
    }

    public void setAddress1_type(String address1_type) {
        this.address1_type = address1_type;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress2_type() {
        return address2_type;
    }

    public void setAddress2_type(String address2_type) {
        this.address2_type = address2_type;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress3_type() {
        return address3_type;
    }

    public void setAddress3_type(String address3_type) {
        this.address3_type = address3_type;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public String getAddress4_type() {
        return address4_type;
    }

    public void setAddress4_type(String address4_type) {
        this.address4_type = address4_type;
    }

    public String getAddress5() {
        return address5;
    }

    public void setAddress5(String address5) {
        this.address5 = address5;
    }

    public String getAddress5_type() {
        return address5_type;
    }

    public void setAddress5_type(String address5_type) {
        this.address5_type = address5_type;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getIsApprove() {
        return isApprove;
    }

    public void setIsApprove(String isApprove) {
        this.isApprove = isApprove;
    }

    public String getIs_active() {
        return is_active;
    }

    public void setIs_active(String is_active) {
        this.is_active = is_active;
    }
}
