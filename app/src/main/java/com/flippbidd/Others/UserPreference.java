package com.flippbidd.Others;

import android.content.Context;
import android.text.TextUtils;

import com.flippbidd.Model.Response.UserDetails;
import com.flippbidd.utils.PreferenceUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class UserPreference {

    private Context mContext;

    private SharedPreferenceUtils helperPreference;

    private UserPreference(Context context) {
        mContext = context;
        helperPreference = SharedPreferenceUtils.getInstance(context);
    }

    public static UserPreference getInstance(Context context) {
        return new UserPreference(context);
    }


    public void save(String userProfileJson) {
        helperPreference.setValue(Constants.USER_PREFERENCES.USER_PREFERENCE, userProfileJson);
    }


    public boolean isUserLogin() {
        //return getUserData() != null;
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.IS_USER_LOGIN, false);
    }

    public void setUserLogin(boolean isUserLogin) {
        helperPreference.setValue(Constants.USER_PREFERENCES.IS_USER_LOGIN, isUserLogin);
    }

    public String getLatitude() {
        return helperPreference.getStringValue(Constants.USER_PREFERENCES.LATITUDE, "0");
    }

    public void setLatitude(double latitude) {
        helperPreference.setValue(Constants.USER_PREFERENCES.LATITUDE, String.valueOf(latitude));
    }

    public String getMobileNumber() {
        return helperPreference.getStringValue(Constants.USER_PREFERENCES.MOBILE_NUMBER, "");
    }

    public void setMobileNumber(String auth) {
        helperPreference.setValue(Constants.USER_PREFERENCES.MOBILE_NUMBER, String.valueOf(auth));
    }

    public String getCCode() {
        return helperPreference.getStringValue(Constants.USER_PREFERENCES.C_CODE, "0");
    }

    public void setCCode(String ccCode) {
        helperPreference.setValue(Constants.USER_PREFERENCES.C_CODE, String.valueOf(ccCode));
    }



    public boolean isBiddIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_11, true);
    }

    public void setIsBiddIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_11, isIntro);
    }
    public boolean isDataIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_14, true);
    }

    public void setIsDataIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_14, isIntro);
    }

    public boolean isContractIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_15, true);
    }

    public void setIsContractIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_15, isIntro);
    }
    public boolean isMyCalendarIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_16, true);
    }

    public void setIsMyCalendarIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_16, isIntro);
    }

    public boolean isThreeDotIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_10, true);
    }

    public void setIsThreeDotIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_10, isIntro);
    }

    public boolean isThumbIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_9, true);
    }

    public void setIsThumbIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_9, isIntro);
    }

    public boolean isSaveIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_8, true);
    }

    public void setIsSaveIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_8, isIntro);
    }
    public boolean isFilterIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_7, true);
    }

    public void setIsFilterIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_7, isIntro);
    }
    public boolean isPropertyListIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_1, true);
    }

    public void setIsPropertyListIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_1, isIntro);
    }
    public boolean isMenuItemIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_2, true);
    }

    public void setIsMenuItemIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_2, isIntro);
    }
    public boolean isMapViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_3, true);
    }

    public void setIsMapViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_3, isIntro);
    }
    public boolean isFindMyDealViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_12, true);
    }

    public void setIsFindMyDealViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_12, isIntro);
    }
    public boolean isSettingsViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_13, true);
    }

    public void setIsSettingsViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_13, isIntro);
    }
    public boolean isMyMapViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_4, true);
    }

    public void setIsMyMapViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_4, isIntro);
    }

    public boolean isChatViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_5, true);
    }

    public void setIsChatViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_5, isIntro);
    }
    public boolean isUploadViewIntro() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.INTRO_6, true);
    }

    public void setIsUploadViewIntro(boolean isIntro) {
        helperPreference.setValue(Constants.USER_PREFERENCES.INTRO_6, isIntro);
    }


    public int getRateApp() {
        return helperPreference.getIntValue(Constants.USER_PREFERENCES.RATE_APP, 0);
    }

    public void setRateApp(int rateApp) {
        helperPreference.setValue(Constants.USER_PREFERENCES.RATE_APP, rateApp);
    }

    public int getUserCredite() {
        return helperPreference.getIntValue(Constants.USER_PREFERENCES.CREDITE_COUNT, 0);
    }

    public void setUserCredite(int count) {
        helperPreference.setValue(Constants.USER_PREFERENCES.CREDITE_COUNT, count);
    }


    public String getLongitude() {
        return helperPreference.getStringValue(Constants.USER_PREFERENCES.LONGITUDE, "0");
    }

    public void setLongitude(double longitude) {
        helperPreference.setValue(Constants.USER_PREFERENCES.LONGITUDE, String.valueOf(longitude));
    }

    public String getDeviceToken() {
        return helperPreference.getStringValue(Constants.USER_PREFERENCES.CONSTANT_FCM_TOKEN, "");
    }

    public void setDeviceToken(String deviceToken) {
        helperPreference.setValue(Constants.USER_PREFERENCES.CONSTANT_FCM_TOKEN, deviceToken);
    }

    public void clear() {
        //clear all app storage data
        helperPreference.setValue(Constants.USER_PREFERENCES.USER_PREFERENCE, "");
        helperPreference.setValue(Constants.USER_PREFERENCES.USER_DETAILS, "");
        helperPreference.setValue(Constants.USER_PREFERENCES.AUTH, "");
        helperPreference.setValue(Constants.USER_PREFERENCES.MOBILE_NUMBER, "");
        helperPreference.setValue(Constants.USER_PREFERENCES.CC_CODE, "");
        helperPreference.setValue(Constants.USER_PREFERENCES.CREDITE_COUNT, 20);

//        ChatHelper.getInstance().logout();
        setUserLogin(false);
//        PreferenceUtils.setContactListResponse("");
    }


    public UserDetails getUserDetail() {
        UserDetails data = null;
        String userProfileJson = helperPreference.getStringValue(Constants.USER_PREFERENCES.USER_DETAILS, "");
        if (!TextUtils.isEmpty(userProfileJson)) {
            data = new Gson().fromJson(userProfileJson, UserDetails.class);
        }
        return data;
    }

    public void setUserDetail(UserDetails userDetail) {
        Gson gson = new Gson();
        Type type = new TypeToken<UserDetails>() {
        }.getType();
        String gsonUserDetails = gson.toJson(userDetail, type);
        helperPreference.setValue(Constants.USER_PREFERENCES.USER_DETAILS, gsonUserDetails);
        setUserLogin(true);
    }


    public void setPushSub(boolean isAction) {
        helperPreference.setValue(Constants.USER_PREFERENCES.ACTION_CHAT_NOTIFICATION, isAction);
    }

    public boolean getPushSub() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.ACTION_CHAT_NOTIFICATION, true);
    }

    public void setPushView(boolean isAction) {
        helperPreference.setValue(Constants.USER_PREFERENCES.ACTION_VIEW_NOTIFICATION, isAction);
    }

    public boolean getPushView() {
        return helperPreference.getBoolanValue(Constants.USER_PREFERENCES.ACTION_VIEW_NOTIFICATION, true);
    }
}
