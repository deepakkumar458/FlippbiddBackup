package com.flippbidd.Model.Response.Likes.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LikePropertyDetails implements Serializable {

    public class Image implements Serializable {

        @SerializedName("image_id")
        @Expose
        private String imageId;
        @SerializedName("common_id")
        @Expose
        private String commonId;
        @SerializedName("image_name")
        @Expose
        private String imageName;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("is_active")
        @Expose
        private String isActive;

        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getCommonId() {
            return commonId;
        }

        public void setCommonId(String commonId) {
            this.commonId = commonId;
        }

        public String getImageName() {
            return imageName;
        }

        public void setImageName(String imageName) {
            this.imageName = imageName;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }

    }


    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("sender_qb_id")
    @Expose
    private String senderQbId;
    @SerializedName("property_image")
    @Expose
    private String propertyImage;
    @SerializedName("saller_type")
    @Expose
    private String sallerType;
    @SerializedName("property_type_id")
    @Expose
    private String propertyTypeId;
    @SerializedName("house_name")
    @Expose
    private String houseName;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("state_id")
    @Expose
    private String stateId;
    @SerializedName("city_id")
    @Expose
    private String cityId;
    @SerializedName("no_of_beds")
    @Expose
    private String noOfBeds;
    @SerializedName("no_of_baths")
    @Expose
    private String noOfBaths;
    @SerializedName("propery_area")
    @Expose
    private String properyArea;
    @SerializedName("propery_area_measure")
    @Expose
    private String properyAreaMeasure;
    @SerializedName("property_listed")
    @Expose
    private String propertyListed;
    @SerializedName("property_vacant")
    @Expose
    private String propertyVacant;
    @SerializedName("property_price")
    @Expose
    private String propertyPrice;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("upload_document")
    @Expose
    private String uploadDocument;
    @SerializedName("property_for")
    @Expose
    private String propertyFor;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modify_date")
    @Expose
    private String modifyDate;
    @SerializedName("is_available")
    @Expose
    private String isAvailable;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
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
    @SerializedName("rent_amount")
    @Expose
    private String rentAmount;
    @SerializedName("upload_doc")
    @Expose
    private String uploadDoc;
    @SerializedName("is_like")
    @Expose
    private Boolean isLike;
    @SerializedName("lease_term")
    @Expose
    private String leaseTerm;
    @SerializedName("sequrity_amount")
    @Expose
    private String sequrityAmount;
    @SerializedName("other_fees")
    @Expose
    private String otherFees;
    @SerializedName("credit_check")
    @Expose
    private String creditCheck;
    @SerializedName("doc_need")
    @Expose
    private String docNeed;
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
    @SerializedName("service_image")
    @Expose
    private String serviceImage;

    public String getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(String propertyId) {
        this.propertyId = propertyId;
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

    public String getPropertyImage() {
        return propertyImage;
    }

    public void setPropertyImage(String propertyImage) {
        this.propertyImage = propertyImage;
    }

    public String getSallerType() {
        return sallerType;
    }

    public void setSallerType(String sallerType) {
        this.sallerType = sallerType;
    }

    public String getPropertyTypeId() {
        return propertyTypeId;
    }

    public void setPropertyTypeId(String propertyTypeId) {
        this.propertyTypeId = propertyTypeId;
    }

    public String getHouseName() {
        return houseName;
    }

    public void setHouseName(String houseName) {
        this.houseName = houseName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getNoOfBeds() {
        return noOfBeds;
    }

    public void setNoOfBeds(String noOfBeds) {
        this.noOfBeds = noOfBeds;
    }

    public String getNoOfBaths() {
        return noOfBaths;
    }

    public void setNoOfBaths(String noOfBaths) {
        this.noOfBaths = noOfBaths;
    }

    public String getProperyArea() {
        return properyArea;
    }

    public void setProperyArea(String properyArea) {
        this.properyArea = properyArea;
    }

    public String getProperyAreaMeasure() {
        return properyAreaMeasure;
    }

    public void setProperyAreaMeasure(String properyAreaMeasure) {
        this.properyAreaMeasure = properyAreaMeasure;
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

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUploadDocument() {
        return uploadDocument;
    }

    public void setUploadDocument(String uploadDocument) {
        this.uploadDocument = uploadDocument;
    }

    public String getPropertyFor() {
        return propertyFor;
    }

    public void setPropertyFor(String propertyFor) {
        this.propertyFor = propertyFor;
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

    public String getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(String isAvailable) {
        this.isAvailable = isAvailable;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
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

    public Boolean getIsLike() {
        return isLike;
    }

    public void setIsLike(Boolean isLike) {
        this.isLike = isLike;
    }

    public String getLeaseTerm() {
        return leaseTerm;
    }

    public void setLeaseTerm(String leaseTerm) {
        this.leaseTerm = leaseTerm;
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

    public String getServiceImage() {
        return serviceImage;
    }

    public void setServiceImage(String serviceImage) {
        this.serviceImage = serviceImage;
    }


}
