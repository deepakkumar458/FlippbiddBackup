package com.flippbidd.Model.Response.planlist;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanDetails implements Parcelable {

    @SerializedName("inapp_plan_id")
    @Expose
    private String inappPlanId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("sub_title")
    @Expose
    private String subTitle;
    @SerializedName("plan_type")
    @Expose
    private String planType;
    @SerializedName("months")
    @Expose
    private String months;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("points")
    @Expose
    private List<String> points = null;
    @SerializedName("descriptions")
    @Expose
    private String descriptions;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("index")
    @Expose
    private String index;
    @SerializedName("creayed_at")
    @Expose
    private String creayedAt;
    @SerializedName("updated_at")
    @Expose
    private String updatedAt;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("is_now")
    @Expose
    private String isNow;

    protected PlanDetails(Parcel in) {
        inappPlanId = in.readString();
        title = in.readString();
        subTitle = in.readString();
        planType = in.readString();
        months = in.readString();
        amount = in.readString();
        points = in.createStringArrayList();
        descriptions = in.readString();
        releaseDate = in.readString();
        status = in.readString();
        index = in.readString();
        creayedAt = in.readString();
        updatedAt = in.readString();
        isActive = in.readString();
        isNow = in.readString();
    }

    public static final Creator<PlanDetails> CREATOR = new Creator<PlanDetails>() {
        @Override
        public PlanDetails createFromParcel(Parcel in) {
            return new PlanDetails(in);
        }

        @Override
        public PlanDetails[] newArray(int size) {
            return new PlanDetails[size];
        }
    };

    public String getInappPlanId() {
        return inappPlanId;
    }

    public void setInappPlanId(String inappPlanId) {
        this.inappPlanId = inappPlanId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getPlanType() {
        return planType;
    }

    public void setPlanType(String planType) {
        this.planType = planType;
    }

    public String getMonths() {
        return months;
    }

    public void setMonths(String months) {
        this.months = months;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public List<String> getPoints() {
        return points;
    }

    public void setPoints(List<String> points) {
        this.points = points;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getCreayedAt() {
        return creayedAt;
    }

    public void setCreayedAt(String creayedAt) {
        this.creayedAt = creayedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getIsNow() {
        return isNow;
    }

    public void setIsNow(String isNow) {
        this.isNow = isNow;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(inappPlanId);
        dest.writeString(title);
        dest.writeString(subTitle);
        dest.writeString(planType);
        dest.writeString(months);
        dest.writeString(amount);
        dest.writeStringList(points);
        dest.writeString(descriptions);
        dest.writeString(releaseDate);
        dest.writeString(status);
        dest.writeString(index);
        dest.writeString(creayedAt);
        dest.writeString(updatedAt);
        dest.writeString(isActive);
        dest.writeString(isNow);
    }
}
