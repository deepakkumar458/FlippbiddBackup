package com.flippbidd.Model;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.Data.DetailsData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BidListResponse {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("data")
        @Expose
        private List<CommonData.Bidds> data;

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

        public List<CommonData.Bidds> getData() {
            return data;
        }

        public void setData(List<CommonData.Bidds> data) {
            this.data = data;
        }

}
