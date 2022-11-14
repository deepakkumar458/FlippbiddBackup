package com.flippbidd.Model.Response.Likes.Details;

import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class LikesDetails implements Serializable{

        @SerializedName("like_id")
        @Expose
        private String likeId;
        @SerializedName("login_id")
        @Expose
        private String loginId;
        @SerializedName("common_id")
        @Expose
        private String commonId;
        @SerializedName("is_like")
        @Expose
        private String isLike;
        @SerializedName("propertyDetails")
        @Expose
        private LikePropertyDetails propertyDetails;
        @SerializedName("userDetails")
        @Expose
        private UserDetails userDetails;

        public String getLikeId() {
            return likeId;
        }

        public void setLikeId(String likeId) {
            this.likeId = likeId;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getCommonId() {
            return commonId;
        }

        public void setCommonId(String commonId) {
            this.commonId = commonId;
        }

        public String getIsLike() {
            return isLike;
        }

        public void setIsLike(String isLike) {
            this.isLike = isLike;
        }

        public LikePropertyDetails getPropertyDetails() {
            return propertyDetails;
        }

        public void setPropertyDetails(LikePropertyDetails propertyDetails) {
            this.propertyDetails = propertyDetails;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
        }





}
