package com.flippbidd.Model.Response.planlist;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanListResponse implements Parcelable {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private List<PlanDetails> data = null;

    protected PlanListResponse(Parcel in) {
        byte tmpSuccess = in.readByte();
        success = tmpSuccess == 0 ? null : tmpSuccess == 1;
        text = in.readString();
        data = in.createTypedArrayList(PlanDetails.CREATOR);
    }

    public static final Creator<PlanListResponse> CREATOR = new Creator<PlanListResponse>() {
        @Override
        public PlanListResponse createFromParcel(Parcel in) {
            return new PlanListResponse(in);
        }

        @Override
        public PlanListResponse[] newArray(int size) {
            return new PlanListResponse[size];
        }
    };

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

    public List<PlanDetails> getData() {
        return data;
    }

    public void setData(List<PlanDetails> data) {
        this.data = data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (success == null ? 0 : success ? 1 : 2));
        dest.writeString(text);
        dest.writeTypedList(data);
    }
}
