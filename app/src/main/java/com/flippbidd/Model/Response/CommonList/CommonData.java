package com.flippbidd.Model.Response.CommonList;

import com.flippbidd.Model.Response.UserDetails;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class CommonData implements Serializable {


    @SerializedName("common_id")
    @Expose
    private String commonId;
    @SerializedName("property_id")
    @Expose
    private String propertyId;
    @SerializedName("login_id")
    @Expose
    private String loginId;
    @SerializedName("property_image")
    @Expose
    private String propertyImage;
    @SerializedName("saller_type")
    @Expose
    private String sallerType;
    @SerializedName("images")
    @Expose
    private List<Image> images = null;
    @SerializedName("bidds")
    @Expose
    private List<CommonData.Bidds> bidds = null;
    @SerializedName("property_type")
    @Expose
    private String propertyType;
    @SerializedName("house")
    @Expose
    private String house = "";

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("address1")
    @Expose
    private String address1;

    @SerializedName("address2")
    @Expose
    private String address2;

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
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("rent_amount")
    @Expose
    private String rentAmount;
    @SerializedName("upload_doc")
    @Expose
    private String uploadDoc;
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
    //"thums_counts":0,"view_counts":0
    @SerializedName("thums_counts")
    @Expose
    private Integer thumsCounts;
    @SerializedName("view_counts")
    @Expose
    private Integer viewCounts;
    @SerializedName("bidd_count")
    @Expose
    private Integer biddCount;
    @SerializedName("doc_need")
    @Expose
    private String docNeed;
    @SerializedName("sender_qb_id")
    @Expose
    private String sender_qb_id;
    @SerializedName("is_like")
    @Expose
    private boolean isLike;
    @SerializedName("is_thumb")
    @Expose
    private boolean isThumb;
    @SerializedName("is_bidd")
    @Expose
    private Integer isBidd;
    @SerializedName("property_price")
    @Expose
    private String propertyPrice;
    @SerializedName("pre_foreclosure")
    @Expose
    private String preForeclosure;
    @SerializedName("created_date")
    @Expose
    private String created_date;
    @SerializedName("ndrs_number")
    @Expose
    private String ndrs_number;
    @SerializedName("title")
    @Expose
    private String title = "";
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("lang")
    @Expose
    private String lang;
    @SerializedName("24h_date")
    @Expose
    private long expriedData;
    @SerializedName("is_status")
    @Expose
    private boolean isExpiriedStatus;
    @SerializedName("payment_status")
    @Expose
    private String paymentStatus;
    @SerializedName("payoff_amt")
    @Expose
    private String payoffAmt;
    @SerializedName("note_balance")
    @Expose
    private String noteBalance;
    @SerializedName("is_surrender")
    @Expose
    private int is_surrender;
    @SerializedName("surrender_agreement")
    @Expose
    private String surrenderAgreement;
    @SerializedName("flippBidd_services")
    @Expose
    private String flippBiddServices;
    @SerializedName("must_close_by_date")
    @Expose
    private String mustCloseByDate;
    @SerializedName("userDetails")
    @Expose
    private UserDetails userDetails;
    @SerializedName("provider_name")
    @Expose
    private String providerName;
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
    @SerializedName("rec_username")
    @Expose
    private String recUsername;
    @SerializedName("rec_country_code")
    @Expose
    private String recCountryCode;
    @SerializedName("rec_mobile_number")
    @Expose
    private String recMobileMumber;
    @SerializedName("rec_email_address")
    @Expose
    private String recEmailAddress;
    @SerializedName("rec_id")
    @Expose
    private String recId;
    @SerializedName("view_365")
    @Expose
    private String view365;
    @SerializedName("house_name")
    @Expose
    private String house_name;

    public UserDetails getData() {
        return userDetails;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setData(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public String getNdrs_number() {
        return ndrs_number;
    }

    public void setNdrs_number(String ndrs_number) {
        this.ndrs_number = ndrs_number;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public long getExpriedData() {
        return expriedData;
    }

    public void setExpriedData(long expriedData) {
        this.expriedData = expriedData;
    }

    public boolean isExpiriedStatus() {
        return isExpiriedStatus;
    }

    public void setExpiriedStatus(boolean expiriedStatus) {
        isExpiriedStatus = expiriedStatus;
    }

    public String getCommonId() {
        return commonId;
    }

    public void setCommonId(String commonId) {
        this.commonId = commonId;
    }

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

    public Integer getThumsCounts() {
        return thumsCounts;
    }

    public void setThumsCounts(Integer thumsCounts) {
        this.thumsCounts = thumsCounts;
    }

    public Integer getViewCounts() {
        return viewCounts;
    }

    public Integer getBiddCount() {
        return biddCount;
    }

    public void setViewCounts(Integer viewCounts) {
        this.viewCounts = viewCounts;
    }

    public String getDocNeed() {
        return docNeed;
    }

    public void setDocNeed(String docNeed) {
        this.docNeed = docNeed;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isThumb() {
        return isThumb;
    }

    public void setThumb(boolean thumb) {
        isThumb = thumb;
    }

    public Integer isBidd() {
        return isBidd;
    }

    public String getPropertyPrice() {
        return propertyPrice;
    }

    public void setPropertyPrice(String propertyPrice) {
        this.propertyPrice = propertyPrice;
    }

    public String getPreForeclosure() {
        return preForeclosure;
    }

    public void setPreForeclosure(String preForeclosure) {
        this.preForeclosure = preForeclosure;
    }

    public String getPayoffAmt() {
        return payoffAmt;
    }

    public void setPayoffAmt(String payoffAmt) {
        this.payoffAmt = payoffAmt;
    }

    public String getNoteBalance() {
        return noteBalance;
    }

    public void setNoteBalance(String noteBalance) {
        this.noteBalance = noteBalance;
    }

    public int getIs_surrender() {
        return is_surrender;
    }

    public void setIs_surrender(int is_surrender) {
        this.is_surrender = is_surrender;
    }

    public String getSurrenderAgreement() {
        return surrenderAgreement;
    }

    public void setSurrenderAgreement(String surrenderAgreement) {
        this.surrenderAgreement = surrenderAgreement;
    }

    public String getFlippBiddServices() {
        return flippBiddServices;
    }

    public void setFlippBiddServices(String flippBiddServices) {
        this.flippBiddServices = flippBiddServices;
    }

    public String getMustCloseByDate() {
        return mustCloseByDate;
    }

    public void setMustCloseByDate(String mustCloseByDate) {
        this.mustCloseByDate = mustCloseByDate;
    }

    public String getSender_qb_id() {
        return sender_qb_id;
    }

    public void setSender_qb_id(String sender_qb_id) {
        this.sender_qb_id = sender_qb_id;
    }

    public List<Bidds> getBidds() {
        return bidds;
    }

    public String getProviderName() {
        return providerName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getBusinessYear() {
        return businessYear;
    }

    public String getPriceOn() {
        return priceOn;
    }

    public String getServiceImage() {
        return serviceImage;
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

    public String getRecEmailAddress() {
        return recEmailAddress;
    }

    public String getRecId() {
        return recId;
    }

    public String getView365() {
        return view365;
    }

    public String getHouse_name() {
        return house_name;
    }

    public void setHouse_name(String house_name) {
        this.house_name = house_name;
    }

    public class Image implements Serializable {

//  "image_id": "34",
//          "common_id": "66",
//          "image_name": "",
//          "is_active": "1"

        @SerializedName("image_id")
        @Expose
        private String imageId;
        @SerializedName("common_id")
        @Expose
        private String commonId;
        @SerializedName("image_name")
        @Expose
        private String imageUrl;
        @SerializedName("is_active")
        @Expose
        private String isActive;


        public String getImageId() {
            return imageId;
        }

        public void setImageId(String imageId) {
            this.imageId = imageId;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getCommonId() {
            return commonId;
        }

        public void setCommonId(String commonId) {
            this.commonId = commonId;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
        }
    }

    public class Bidds implements Serializable {
        @SerializedName("bidd_id")
        @Expose
        private String biddId;

        @SerializedName("login_id")
        @Expose
        private String loginId;

        @SerializedName("property_id")
        @Expose
        private String propertyId;

        @SerializedName("price")
        @Expose
        private String price;

        @SerializedName("is_notify")
        @Expose
        private String isNotify;

        @SerializedName("is_active")
        @Expose
        private String isActive;

        @SerializedName("property_type")
        @Expose
        private String propertyType;

        @SerializedName("username")
        @Expose
        private String username;

        @SerializedName("user_full_name")
        @Expose
        private String userFullName;

        @SerializedName("user_image")
        @Expose
        private String userImage;

        @SerializedName("user_email")
        @Expose
        private String user_email;

        @SerializedName("status")
        @Expose
        private String status;

        public String getBiddId() {
            return biddId;
        }

        public String getLoginId() {
            return loginId;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public String getPrice() {
            return price;
        }

        public String getIsNotify() {
            return isNotify;
        }

        public String getIsActive() {
            return isActive;
        }

        public String getPropertyType() {
            return propertyType;
        }

        public String getUsername() {
            return username;
        }

        public String getUserFullName() {
            return userFullName;
        }

        public String getUserImage() {
            return userImage;
        }

        public String getUser_email() {
            return user_email;
        }

        public void setUser_email(String user_email) {
            this.user_email = user_email;
        }

        public String getStatus() {
            return status;
        }
    }
}
