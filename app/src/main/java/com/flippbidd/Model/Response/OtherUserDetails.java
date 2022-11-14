package com.flippbidd.Model.Response;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class OtherUserDetails implements Serializable {

    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("email_address")
    @Expose
    private String emailAddress;
    @SerializedName("qb_id")
    @Expose
    private String qbId;
    @SerializedName("qb_dialog_id")
    @Expose
    private String qbDialogId;
    @SerializedName("apple_user_id")
    @Expose
    private Object appleUserId;
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
    @SerializedName("pof_doc")
    @Expose
    private String pofDoc;
    @SerializedName("doc_uploaded_date")
    @Expose
    private String docUploadedDate;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("modify_date")
    @Expose
    private String modifyDate;
    @SerializedName("is_active")
    @Expose
    private String isActive;
    @SerializedName("stateName")
    @Expose
    private String stateName;
    @SerializedName("cityName")
    @Expose
    private String cityName;
    @SerializedName("plan")
    @Expose
    private String plan;
    @SerializedName("rating")
    @Expose
    private Float rating;
    @SerializedName("rating_list")
    @Expose
    private List<RatingData> ratingList = null;
    @SerializedName("total_ratting")
    @Expose
    private Integer totalRatting;
    @SerializedName("property_list")
    @Expose
    private List<CommonData> propertyList = null;
    @SerializedName("total_property")
    @Expose
    private Integer totalProperty;

    public String getToken() {
        return token;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getQbId() {
        return qbId;
    }

    public String getQbDialogId() {
        return qbDialogId;
    }

    public Object getAppleUserId() {
        return appleUserId;
    }

    public String getProfileId() {
        return profileId;
    }

    public String getLoginId() {
        return loginId;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getAddress() {
        return address;
    }

    public String getState() {
        return state;
    }

    public String getCity() {
        return city;
    }

    public String getProfession() {
        return profession;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getPofDoc() {
        return pofDoc;
    }

    public String getDocUploadedDate() {
        return docUploadedDate;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public String getStateName() {
        return stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public String getPlan() {
        return plan;
    }

    public Float getRating() {
        return rating;
    }

    public List<RatingData> getRatingList() {
        return ratingList;
    }

    public Integer getTotalRatting() {
        return totalRatting;
    }

    public List<CommonData> getPropertyList() {
        return propertyList;
    }

    public Integer getTotalProperty() {
        return totalProperty;
    }
}
