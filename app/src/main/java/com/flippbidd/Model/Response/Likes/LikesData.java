package com.flippbidd.Model.Response.Likes;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class LikesData implements Serializable {


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
    private PropertyDetails propertyDetails;

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

    public PropertyDetails getPropertyDetails() {
        return propertyDetails;
    }

    public void setPropertyDetails(PropertyDetails propertyDetails) {
        this.propertyDetails = propertyDetails;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }


    public class PropertyDetails implements Serializable {

        @SerializedName("property_price")
        @Expose
        private String propertyPrice;
        @SerializedName("description")
        @Expose
        private String description;
        @SerializedName("upload_document")
        @Expose
        private String uploadDocument;
        @SerializedName("ndrs_number")
        @Expose
        private String ndrsNumber;
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
        @SerializedName("pre_foreclosure")
        @Expose
        private String preForeclosure;
        @SerializedName("payment_status")
        @Expose
        private String paymentStatus;

        @SerializedName("property_id")
        @Expose
        private String propertyId;
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

        @SerializedName("address")
        @Expose
        private String address;

        @SerializedName("address1")
        @Expose
        private String address1;

        @SerializedName("address2")
        @Expose
        private String address2;

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
        @SerializedName("service_listed")
        @Expose
        private String serviceListed;

        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("24h_date")
        @Expose
        private long expriedData;
        @SerializedName("property_image")
        @Expose
        private String propertyImage;
        @SerializedName("rental_img")
        @Expose
        private String rentalImg;
        @SerializedName("images")
        @Expose
        private List<CommonData.Image> images = null;
        @SerializedName("saller_type")
        @Expose
        private String sallerType;
        @SerializedName("property_type")
        @Expose
        private String propertyType;
        @SerializedName("house")
        @Expose
        private String house;
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
        @SerializedName("property_vacant")
        @Expose
        private String propertyVacant;
        @SerializedName("rent_amount")
        @Expose
        private String rentAmount;
        @SerializedName("upload_doc")
        @Expose
        private String uploadDoc;
        @SerializedName("is_like")
        @Expose
        private Boolean isLike;
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
        @SerializedName("service_type")
        @Expose
        private String serviceType;
        @SerializedName("is_status")
        @Expose
        private boolean isExpiriedStatus;
        @SerializedName("thums_counts")
        @Expose
        private Integer thumsCounts;
        @SerializedName("view_counts")
        @Expose
        private Integer viewCounts;
        @SerializedName("bidd_count")
        @Expose
        private Integer biddCounts;
        @SerializedName("rating")
        @Expose
        private Integer rateAverage;
        @SerializedName("rate_count")
        @Expose
        private Integer rateCount;


        public String getPropertyPrice() {
            return propertyPrice;
        }

        public void setPropertyPrice(String propertyPrice) {
            this.propertyPrice = propertyPrice;
        }

        public String getIsAvailable() {
            return isAvailable;
        }

        public void setIsAvailable(String isAvailable) {
            this.isAvailable = isAvailable;
        }

        public String getPreForeclosure() {
            return preForeclosure;
        }

        public void setPreForeclosure(String preForeclosure) {
            this.preForeclosure = preForeclosure;
        }

        public String getPaymentStatus() {
            return paymentStatus;
        }

        public void setPaymentStatus(String paymentStatus) {
            this.paymentStatus = paymentStatus;
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

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }


        public String getAddress1() {
            return address1;
        }

        public void setAddress1(String address1) {
            this.address1 = address1;
        }

        public String getAddress2() {
            return address2;
        }

        public void setAddress2(String address2) {
            this.address2 = address2;
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

        public long get24hDate() {
            return expriedData;
        }

        public void set24hDate(long _24hDate) {
            this.expriedData = _24hDate;
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

        public List<CommonData.Image> getImages() {
            return images;
        }

        public void setImages(List<CommonData.Image> images) {
            this.images = images;
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

        public String getHouse() {
            return house;
        }

        public void setHouse(String house) {
            this.house = house;
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

        public String getPropertyFor() {
            return propertyFor;
        }

        public void setPropertyFor(String propertyFor) {
            this.propertyFor = propertyFor;
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

        public String getServiceType() {
            return serviceType;
        }

        public void setServiceType(String serviceType) {
            this.serviceType = serviceType;

        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public boolean isExpiriedStatus() {
            return isExpiriedStatus;
        }

        public void setExpiriedStatus(boolean expiriedStatus) {
            isExpiriedStatus = expiriedStatus;
        }

        public Integer getThumsCounts() {
            return thumsCounts;
        }

        public Integer getViewCounts() {
            return viewCounts;
        }

        public Integer getBiddCounts() {
            return biddCounts;
        }

        public Integer getRateAverage() {
            return rateAverage;
        }

        public Integer getRateCount() {
            return rateCount;
        }

        public class Image {

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
    }
}

