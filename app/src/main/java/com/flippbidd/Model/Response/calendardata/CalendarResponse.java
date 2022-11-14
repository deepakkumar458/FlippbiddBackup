package com.flippbidd.Model.Response.calendardata;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CalendarResponse {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("data")
    @Expose
    private List<Datum> data = null;

    public Boolean getSuccess() {
        return success;
    }

    public String getText() {
        return text;
    }

    public List<Datum> getData() {
        return data;
    }

    public class Datum {

        @SerializedName("meeting_id")
        @Expose
        private String meetingId;
        @SerializedName("property_id")
        @Expose
        private String propertyId;
        @SerializedName("owner_id")
        @Expose
        private String ownerId;
        @SerializedName("login_id")
        @Expose
        private String loginId;
        @SerializedName("date")
        @Expose
        private String date;
        @SerializedName("start_time")
        @Expose
        private String startTime;
        @SerializedName("end_time")
        @Expose
        private String endTime;
        @SerializedName("type")
        @Expose
        private String type;
        @SerializedName("created_type")
        @Expose
        private String created_type;
        @SerializedName("instruction")
        @Expose
        private String instruction;
        @SerializedName("created_at")
        @Expose
        private String createdAt;
        @SerializedName("updated_at")
        @Expose
        private String updatedAt;
        @SerializedName("status")
        @Expose
        private String status;
        @SerializedName("is_active")
        @Expose
        private String isActive;
        @SerializedName("house_name")
        @Expose
        private String houseName;
        @SerializedName("address")
        @Expose
        private String address;
        @SerializedName("no_of_beds")
        @Expose
        private String noOfBeds;
        @SerializedName("no_of_baths")
        @Expose
        private String noOfBaths;
        @SerializedName("propery_area")
        @Expose
        private String properyArea;
        @SerializedName("property_price")
        @Expose
        private String propertyPrice;
        @SerializedName("is_call_btn")
        @Expose
        private Integer isCallBtn;
        @SerializedName("OwnerDetails")
        @Expose
        private OwnerDetails ownerDetails;
        @SerializedName("UserDetails")
        @Expose
        private UserDetails userDetails;
        @SerializedName("property_image")
        @Expose
        private String propertyImage;

        public String getMeetingId() {
            return meetingId;
        }

        public void setMeetingId(String meetingId) {
            this.meetingId = meetingId;
        }

        public String getPropertyId() {
            return propertyId;
        }

        public void setPropertyId(String propertyId) {
            this.propertyId = propertyId;
        }

        public String getOwnerId() {
            return ownerId;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getLoginId() {
            return loginId;
        }

        public void setLoginId(String loginId) {
            this.loginId = loginId;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreated_type() {
            return created_type;
        }

        public void setCreated_type(String created_type) {
            this.created_type = created_type;
        }

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getIsActive() {
            return isActive;
        }

        public void setIsActive(String isActive) {
            this.isActive = isActive;
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

        public String getPropertyPrice() {
            return propertyPrice;
        }

        public void setPropertyPrice(String propertyPrice) {
            this.propertyPrice = propertyPrice;
        }

        public Integer getIsCallBtn() {
            return isCallBtn;
        }

        public OwnerDetails getOwnerDetails() {
            return ownerDetails;
        }

        public void setOwnerDetails(OwnerDetails ownerDetails) {
            this.ownerDetails = ownerDetails;
        }

        public UserDetails getUserDetails() {
            return userDetails;
        }

        public void setUserDetails(UserDetails userDetails) {
            this.userDetails = userDetails;
        }

        public String getPropertyImage() {
            return propertyImage;
        }

        public void setPropertyImage(String propertyImage) {
            this.propertyImage = propertyImage;
        }

        public class OwnerDetails {

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
            @SerializedName("timezone")
            @Expose
            private String timezone;
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
            @SerializedName("email_address")
            @Expose
            private String emailAddress;

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

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
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

            public String getEmailAddress() {
                return emailAddress;
            }
        }

        public class UserDetails {

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
            @SerializedName("timezone")
            @Expose
            private String timezone;
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
            @SerializedName("email_address")
            @Expose
            private String emailAddress;

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

            public String getTimezone() {
                return timezone;
            }

            public void setTimezone(String timezone) {
                this.timezone = timezone;
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

            public String getEmailAddress() {
                return emailAddress;
            }
        }

    }
}
