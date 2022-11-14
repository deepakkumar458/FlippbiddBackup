package com.flippbidd.Model.Response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class UserDetails implements Serializable{

    @SerializedName("profile_id")
    @Expose
    private String profileId;

    @SerializedName("login_id")
    @Expose
    private String loginId;

    @SerializedName("profile_pic")
    @Expose
    private String profilePic;

    @SerializedName("full_name")
    @Expose
    private String fullName;

    @SerializedName("username")
    @Expose
    private String username;

    /*    "country_code": "91",
    "mobile_number": "9377982271",*/
    @SerializedName("country_code")
    @Expose
    private String countryCode;

    @SerializedName("mobile_number")
    @Expose
    private String mobileNumber;

    @SerializedName("address")
    @Expose
    private String address;

    @SerializedName("state")
    @Expose
    private String state;

    @SerializedName("city")
    @Expose
    private String city;

    @SerializedName("profession")
    @Expose
    private String profession;

    @SerializedName("about_me")
    @Expose
    private String aboutMe;

    @SerializedName("latitude")
    @Expose
    private String latitude;

    @SerializedName("longitude")
    @Expose
    private String longitude;

    @SerializedName("created_date")
    @Expose
    private String createdDate;

    @SerializedName("modify_date")
    @Expose
    private String modifyDate;

    @SerializedName("is_active")
    @Expose
    private String isActive;

    @SerializedName("plan")
    @Expose
    private String plan;

    @SerializedName("token")
    @Expose
    private String token;

    @SerializedName("qb_id")
    @Expose
    private String qbId;

    @SerializedName("qb_dialog_id")
    @Expose
    private String qbDialogId;

    @SerializedName("email_address")
    @Expose
    private String emailAddress;

    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("cityName")
    @Expose
    private String cityName;

    @SerializedName("state_id")
    @Expose
    private String stateId;

    @SerializedName("city_id")
    @Expose
    private String cityId;

    @SerializedName("profession_id")
    @Expose
    private String professionId;

    @SerializedName("login_type")
    @Expose
    private String loginType;
    @SerializedName("rating")
    @Expose
    private Double rateAverage;

    @SerializedName("rate_count")
    @Expose
    private Integer rateCount;

    @SerializedName("pof_doc")
    @Expose
    private String pofDoc;

    @SerializedName("doc_uploaded_date")
    @Expose
    private String docUploadedDate;

    @SerializedName("user_status")
    @Expose
    private Integer userStatus;

    @SerializedName("is_rate_noti")
    @Expose
    private Integer isRateNoti;

    @SerializedName("is_find_my_deal_noti")
    @Expose
    private Integer isFindMyDealNoti;
    @SerializedName("is_property_view_noti")
    @Expose
    private Integer isPropertyViewNoti;

    @SerializedName("is_property_upload_noti")
    @Expose
    private Integer isPropertyUploadNoti;

    @SerializedName("is_premium_user")
    @Expose
    private Integer is_premium_user;

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }


    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getProfession() {
        return profession;
    }

    public void setProfession(String profession) {
        this.profession = profession;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getQbId() {
        return qbId;
    }

    public void setQbId(String qbId) {
        this.qbId = qbId;
    }

    public String getQbDialogId() {
        return qbDialogId;
    }

    public void setQbDialogId(String qbDialogId) {
        this.qbDialogId = qbDialogId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
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

    public String getProfessionId() {
        return professionId;
    }

    public void setProfessionId(String professionId) {
        this.professionId = professionId;
    }

    public String getLoginType() {
        return loginType;
    }

    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }

    public Integer getRateCount() {
        return rateCount;
    }

    public void setRateCount(Integer rateCount) {
        this.rateCount = rateCount;
    }

    public Double getRateAverage() {
        return rateAverage;
    }

    public void setRateAverage(Double rateAverage) {
        this.rateAverage = rateAverage;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getPofDoc() {
        return pofDoc;
    }

    public void setPofDoc(String pofDoc) {
        this.pofDoc = pofDoc;
    }

    public String getDocUploadedDate() {
        return docUploadedDate;
    }

    public void setDocUploadedDate(String docUploadedDate) {
        this.docUploadedDate = docUploadedDate;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public Integer getIsRateNoti() {
        return isRateNoti;
    }

    public Integer getIsFindMyDealNoti() {
        return isFindMyDealNoti;
    }

    public Integer getIsPropertyViewNoti() {
        return isPropertyViewNoti;
    }

    public Integer getIsPropertyUploadNoti() {
        return isPropertyUploadNoti;
    }

    public void setIsPropertyUploadNoti(Integer isPropertyUploadNoti) {
        this.isPropertyUploadNoti = isPropertyUploadNoti;
    }

    public Integer getIs_premium_user() {
        return is_premium_user;
    }

    public void setIs_premium_user(Integer is_premium_user) {
        this.is_premium_user = is_premium_user;
    }
}
