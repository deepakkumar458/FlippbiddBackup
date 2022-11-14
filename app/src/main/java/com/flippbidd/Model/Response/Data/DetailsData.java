package com.flippbidd.Model.Response.Data;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DetailsData implements Serializable {


    @SerializedName("common_id")
    @Expose
    private String commonId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("property_image")
    @Expose
    private String propertyImage;
    @SerializedName("rental_img")
    @Expose
    private String rentalImg;
    @SerializedName("saller_type")
    @Expose
    private String sallerType;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
    @SerializedName("images")
    @Expose
    private List<CommonData.Image> images = null;
    @SerializedName("bidds")
    @Expose
    private List<CommonData.Bidds> bidds = null;
    @SerializedName("house")
    @Expose
    private String house;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("building_type")
    @Expose
    private String buildingType;
    @SerializedName("state")
    @Expose
    private String state;
    @SerializedName("city")
    @Expose
    private String city;
    @SerializedName("available_date")
    @Expose
    private String availableDate;
    @SerializedName("bed_nos")
    @Expose
    private String bedNos;
    @SerializedName("bath_nos")
    @Expose
    private String bathNos;
    @SerializedName("area")
    @Expose
    private String area;
    @SerializedName("area_measure")
    @Expose
    private String areaMeasure;
    @SerializedName("property_listed")
    @Expose
    private String propertyListed;
    @SerializedName("pre_foreclosure")
    @Expose
    private String preForeclosure;
    @SerializedName("property_vacant")
    @Expose
    private String propertyVacant;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("rent_amount")
    @Expose
    private String rentAmount;
    @SerializedName("upload_doc")
    @Expose
    private String uploadDoc;
    @SerializedName("userDetails")
    @Expose
    private UserDetails userDetails;
    @SerializedName("property_for")
    @Expose
    private String propertyFor;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;
    @SerializedName("sequrity_amount")
    @Expose
    private String sequrityAmount;
    @SerializedName("other_fees")
    @Expose
    private String otherFees;
    @SerializedName("lease_term")
    @Expose
    private String leaseTerm;
    @SerializedName("credit_check")
    @Expose
    private String creditCheck;
    @SerializedName("doc_need")
    @Expose
    private String docNeed;
    @SerializedName("sender_qb_id")
    @Expose
    private String senderQbId;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("service_type")
    @Expose
    private String serviceType;
    @SerializedName("business_year")
    @Expose
    private String businessYear;
    @SerializedName("price_on")
    @Expose
    private String priceOn;

    @SerializedName("ndrs_number")
    @Expose
    private String ndrsNumber;
    @SerializedName("service_image")
    @Expose
    private String serviceImage;
    @SerializedName("rec_username")
    @Expose
    private String recUsername;
    @SerializedName("rec_country_code")
    @Expose
    private String recCountryCode;
    @SerializedName("rec_mobile_number")
    @Expose
    private String recMobileMumber;

    public String getCommonId() {
        return commonId;
    }

    public void setCommonId(String commonId) {
        this.commonId = commonId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public String getRentalImg() {
        return rentalImg;
    }

    public void setRentalImg(String rentalImg) {
        this.rentalImg = rentalImg;
    }

    public String getSallerType() {
        return sallerType;
    }

    public void setSallerType(String sallerType) {
        this.sallerType = sallerType;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }

    public String getPreForeclosure() {
        return preForeclosure;
    }

    public void setPreForeclosure(String preForeclosure) {
        this.preForeclosure = preForeclosure;
    }

    public List<CommonData.Image> getImages() {
        return images;
    }

    public void setImages(List<CommonData.Image> images) {
        this.images = images;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
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

    public String getAvailableDate() {
        return availableDate;
    }

    public void setAvailableDate(String availableDate) {
        this.availableDate = availableDate;
    }

    public String getBedNos() {
        return bedNos;
    }

    public void setBedNos(String bedNos) {
        this.bedNos = bedNos;
    }

    public String getBathNos() {
        return bathNos;
    }

    public void setBathNos(String bathNos) {
        this.bathNos = bathNos;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaMeasure() {
        return areaMeasure;
    }

    public void setAreaMeasure(String areaMeasure) {
        this.areaMeasure = areaMeasure;
    }

    public String getPropertyListed() {
        return propertyListed;
    }

    public void setPropertyListed(String propertyListed) {
        this.propertyListed = propertyListed;
    }

    public String getPropertyVacant() {
        return propertyVacant;
    }

    public void setPropertyVacant(String propertyVacant) {
        this.propertyVacant = propertyVacant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(String rentAmount) {
        this.rentAmount = rentAmount;
    }

    public String getUploadDoc() {
        return uploadDoc;
    }

    public void setUploadDoc(String uploadDoc) {
        this.uploadDoc = uploadDoc;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getPropertyFor() {
        return propertyFor;
    }

    public void setPropertyFor(String propertyFor) {
        this.propertyFor = propertyFor;
    }

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getSequrityAmount() {
        return sequrityAmount;
    }

    public void setSequrityAmount(String sequrityAmount) {
        this.sequrityAmount = sequrityAmount;
    }

    public String getOtherFees() {
        return otherFees;
    }

    public void setOtherFees(String otherFees) {
        this.otherFees = otherFees;
    }

    public String getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(String leaseTerm) {
        this.leaseTerm = leaseTerm;
    }

    public String getCreditCheck() {
        return creditCheck;
    }

    public void setCreditCheck(String creditCheck) {
        this.creditCheck = creditCheck;
    }

    public String getDocNeed() {
        return docNeed;
    }

    public void setDocNeed(String docNeed) {
        this.docNeed = docNeed;
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

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getBusinessYear() {
        return businessYear;
    }

    public void setBusinessYear(String businessYear) {
        this.businessYear = businessYear;
    }

    public String getPriceOn() {
        return priceOn;
    }

    public void setPriceOn(String priceOn) {
        this.priceOn = priceOn;
    }

    public String getNdrsNumber() {
        return ndrsNumber;
    }

    public void setNdrsNumber(String ndrsNumber) {
        this.ndrsNumber = ndrsNumber;
    }

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }

    public String getRecUsername() {
        return recUsername;
    }

    public String getRecCountryCode() {
        return recCountryCode;
    }

    public String getRecMobileMumber() {
        return recMobileMumber;
    }

    public List<CommonData.Bidds> getBidds() {
        return bidds;
    }

    public void setBidds(List<CommonData.Bidds> bidds) {
        this.bidds = bidds;
    }
}
