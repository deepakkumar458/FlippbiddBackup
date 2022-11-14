package com.flippbidd.Model.Response.Filter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FileterData {

    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private Data data;

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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        @SerializedName("filter_id")
        @Expose
        private String filterId;
        @SerializedName("login_id")
        @Expose
        private String loginId;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("state")
        @Expose
        private String state;
        @SerializedName("city")
        @Expose
        private String city;
        @SerializedName("listed")
        @Expose
        private String listed;
        @SerializedName("min_price")
        @Expose
        private String minPrice;
        @SerializedName("max_price")
        @Expose
        private String maxPrice;
        @SerializedName("sqft")
        @Expose
        private String sqft;
        @SerializedName("building_type")
        @Expose
        private String buildingType;
        @SerializedName("service_type")
        @Expose
        private String serviceType;
        @SerializedName("licensed")
        @Expose
        private String licensed;

        @SerializedName("all_state_city")
        @Expose
        private String allstatecity;
        @SerializedName("is_active")
        @Expose
        private String isActive;

        public String getFilterId() {
            return filterId;
        }

        public void setFilterId(String filterId) {
            this.filterId = filterId;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getListed() {
            return listed;
        }

        public void setListed(String listed) {
            this.listed = listed;
        }

        public String getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(String minPrice) {
            this.minPrice = minPrice;
        }

        public String getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(String maxPrice) {
            this.maxPrice = maxPrice;
        }

        public String getSqft() {
            return sqft;
        }

        public void setSqft(String sqft) {
            this.sqft = sqft;
        }

        public String getBuildingType() {
            return buildingType;
        }

        public void setBuildingType(String buildingType) {
            this.buildingType = buildingType;
        }

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;
        }

        public String getLicensed() {
            return licensed;
        }

        public void setLicensed(String licensed) {
            this.licensed = licensed;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

        public String getAllstatecity() {
            return allstatecity;
        }

        public void setAllstatecity(String allstatecity) {
            this.allstatecity = allstatecity;
        }
    }


}
