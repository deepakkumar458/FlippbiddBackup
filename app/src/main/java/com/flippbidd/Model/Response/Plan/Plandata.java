package com.flippbidd.Model.Response.Plan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Plandata implements Serializable{

    @SerializedName("plan_id")
    @Expose
    private String planId;
    @SerializedName("plan_name")
    @Expose
    private String planName;
    @SerializedName("plan_price")
    @Expose
    private String planPrice;
    @SerializedName("plan_stripe_id")
    @Expose
    private String planStripeId;
    @SerializedName("is_active")
    @Expose
    private String isActive;

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public String getPlanPrice() {
        return planPrice;
    }

    public void setPlanPrice(String planPrice) {
        this.planPrice = planPrice;
    }

    public String getPlanStripeId() {
        return planStripeId;
    }

    public void setPlanStripeId(String planStripeId) {
        this.planStripeId = planStripeId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

}
