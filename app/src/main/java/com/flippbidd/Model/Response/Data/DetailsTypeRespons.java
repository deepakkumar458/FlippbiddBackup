package com.flippbidd.Model.Response.Data;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DetailsTypeRespons {

        @SerializedName("success")
        @Expose
        private Boolean success;
        @SerializedName("text")
        @Expose
        private String text;
        @SerializedName("data")
        @Expose
        private CommonData data;

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

        public CommonData getData() {
            return data;
        }

        public void setData(CommonData data) {
            this.data = data;
        }

}
