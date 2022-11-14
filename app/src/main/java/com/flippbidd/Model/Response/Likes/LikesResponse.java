package com.flippbidd.Model.Response.Likes;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LikesResponse {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("data")
        @Expose
        private List<LikesData> data = null;

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

        public List<LikesData> getData() {
            return data;
        }

        public void setData(List<LikesData> data) {
            this.data = data;
        }







}
