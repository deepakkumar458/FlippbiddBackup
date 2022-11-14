package com.flippbidd.Model.Response.PropertyStateWise;

import com.flippbidd.Model.Response.CommonList.CommonData;
import com.flippbidd.Model.Response.UserDetails;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class PPData implements ClusterItem {

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
    private List<CommonData.Image> images = null;
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


    @NonNull
    @Override
    public LatLng getPosition() {
        return null;
    }

    @Nullable
    @Override
    public String getTitle() {
        return null;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return null;
    }
}
