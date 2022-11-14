package com.flippbidd.Model.Response.Likes.Service;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ServiceLikeListResponse {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("data")
        @Expose
        private List<ServiceLikeDetails> data = null;

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

        public List<ServiceLikeDetails> getData() {
            return data;
        }

        public void setData(List<ServiceLikeDetails> data) {
            this.data = data;
        }



}
