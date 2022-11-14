package com.flippbidd.Model.Response.Service;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ServiceListData implements Serializable {


    @SerializedName("service_id")
    @Expose
    private String serviceId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("sender_qb_id")
    @Expose
    private String senderQbId;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("service_type_id")
    @Expose
    private String serviceTypeId;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("business_year")
    @Expose
    private String businessYear;
    @SerializedName("service_image")
    @Expose
    private String serviceImage;
    @SerializedName("service_document")
    @Expose
    private String serviceDocument;
    @SerializedName("service_price")
    @Expose
    private String servicePrice;
    @SerializedName("price_on")
    @Expose
    private String priceOn;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("service_listed")
    @Expose
    private String serviceListed;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modify_date")
    @Expose
    private String modifyDate;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
    @SerializedName("service_type")
    @Expose
    private String serviceType;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("images")
    @Expose
    private List<CommonData.Image> images = null;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("userDetails")
    @Expose
    private UserDetails userDetails;

    @SerializedName("is_like")
    @Expose
    private boolean isLike;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public UserDetails getData() {
        return userDetails;
    }

    public void setData(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getSenderQbId() {
        return senderQbId;
    }

    public void setSenderQbId(String senderQbId) {
        this.senderQbId = senderQbId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(String stateId) {
        this.stateId = stateId;
    }

    public String getCityId() {
        return cityId;
    }

    public void setCityId(String cityId) {
        this.cityId = cityId;
    }

    public String getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getServiceDocument() {
        return serviceDocument;
    }

    public void setServiceDocument(String serviceDocument) {
        this.serviceDocument = serviceDocument;
    }

    public String getServicePrice() {
        return servicePrice;
    }

    public void setServicePrice(String servicePrice) {
        this.servicePrice = servicePrice;
    }

    public String getPriceOn() {
        return priceOn;
    }

    public void setPriceOn(String priceOn) {
        this.priceOn = priceOn;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getServiceListed() {
        return serviceListed;
    }

    public void setServiceListed(String serviceListed) {
        this.serviceListed = serviceListed;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public List<CommonData.Image> getImages() {
        return images;
    }

    public void setImages(List<CommonData.Image> images) {
        this.images = images;
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

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
