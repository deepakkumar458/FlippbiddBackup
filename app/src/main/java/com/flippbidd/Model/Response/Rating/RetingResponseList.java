package com.flippbidd.Model.Response.Rating;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RetingResponseList {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("data")
        @Expose
        private List<RatingData> data = null;

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

        public List<RatingData> getData() {
            return data;
        }

        public void setData(List<RatingData> data) {
            this.data = data;
        }



}
