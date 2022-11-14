package com.flippbidd.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.flippbidd.baseclass.BaseApplication;

public class PreferenceUtils {
    private static final String USER_PRO ="PRO USER";
    private static final String PREFERENCE_IS_PRO_VERSION = "is_pro_version";
    private static final String PREFERENCE_KEY_USER_ID = "userId";
    private static final String PREFERENCE_KEY_NICKNAME = "nickname";
    private static final String PREFERENCE_KEY_PROFILE = "profilepick";
    private static final String PREFERENCE_KEY_CONNECTED = "connected";
    private static final String PREF_KEY_CALLEE_ID = "callee_id";
    private static final String PREF_KEY_PUSH_TOKEN = "push_token";
    private static final String PREF_KEY_ACCESS_TOKEN = "access_token";
    private static final String PREF_KEY_APP_ID = "app_id";
    private static final String PREF_KEY_CALL_APP_ID = "app_call_id";
    private static final String PREF_KEY_REQUEST_COUNTS = "meeting_request_counts";

    private static final String PREFERENCE_KEY_UPLOAD_PROPERTY_NOTIFICATIONS = "upload_property_notifications";
    private static final String PREFERENCE_KEY_FIND_DEAL_NOTIFICATIONS = "find_my_deal_notifications";
    private static final String PREFERENCE_KEY_NOTIFICATIONS = "notifications";
    private static final String PREFERENCE_KEY_VIEW_NOTIFICATIONS = "view_notifications";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS = "notificationsShowPreviews";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB = "notificationsDoNotDisturb";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM = "notificationsDoNotDisturbFrom";
    private static final String PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO = "notificationsDoNotDisturbTo";
    private static final String PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT = "channelDistinct";
    private static final String PREFERENCE_KEY_GROUP_CHANNEL_LAST_READ = "last_read";

    private static final String PREFERENCE_KEY_CONTACT_LIST_RESPONSE = "contact_list_response";
    private static final String PREFERENCE_KEY_SYNC_CONTACT = "contact_sync_is";
    private static final String PREFERENCE_KEY_IS_PREMIUM_USER = "is_premium_user";

    private static Context mAppContext;

    // Prevent instantiation
    private PreferenceUtils() {
    }

    public static void init(Context appContext) {
        mAppContext = appContext;
    }

    private static SharedPreferences getSharedPreferences() {
        return mAppContext.getSharedPreferences("sendbird", Context.MODE_PRIVATE);
    }

    public static void setAppId(Context context, String appId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_APP_ID, appId).apply();
    }

    public static String getAppId(Context context) {
        return getSharedPreferences().getString(PREF_KEY_APP_ID, BaseApplication.APP_CHAT);
    }

    public static void setAppCallId(Context context, String appId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_CALL_APP_ID, appId).apply();
    }

    public static String getAppCallId(Context context) {
        return getSharedPreferences().getString(PREF_KEY_CALL_APP_ID, BaseApplication.APP_CALL);
    }

    public static void setUserId(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_USER_ID, userId).apply();
    }

    public static String getUserId() {
        return getSharedPreferences().getString(PREFERENCE_KEY_USER_ID, "");
    }

    public static void setCalleeId(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_CALLEE_ID, userId).apply();
    }

    public static String getCalleeId() {
        return getSharedPreferences().getString(PREF_KEY_CALLEE_ID, "");
    }

    public static void setMettingCounts(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_REQUEST_COUNTS, userId).apply();
    }

    public static String getMettingCounts() {
        return getSharedPreferences().getString(PREF_KEY_REQUEST_COUNTS, "");
    }

    public static void setNickname(String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NICKNAME, nickname).apply();
    }

    public static String getNickname() {
        return getSharedPreferences().getString(PREFERENCE_KEY_NICKNAME, "");
    }

    public static void setProfilePic(String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_PROFILE, nickname).apply();
    }

    public static String getProfilePic() {
        return getSharedPreferences().getString(PREFERENCE_KEY_PROFILE, "");
    }

    public static void setIsPremiumUser(Integer premiumUser) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putInt(PREFERENCE_KEY_IS_PREMIUM_USER, premiumUser).apply();
    }

    public static Integer getIsPremiumUser() {
        return getSharedPreferences().getInt(PREFERENCE_KEY_IS_PREMIUM_USER, 0);
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear().apply();
    }

    public static void setViewNotifications(boolean notifications) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_VIEW_NOTIFICATIONS, notifications).apply();
    }

    public static boolean getViewNotifications() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_VIEW_NOTIFICATIONS, true);
    }

    public static void setNotifications(boolean notifications) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS, notifications).apply();
    }

    public static boolean getPlanVersionStatus() {
        return getSharedPreferences().getBoolean(PREFERENCE_IS_PRO_VERSION, false);
    }

    public static void setPlanVersionStatus(boolean isPlanStatus) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_IS_PRO_VERSION, isPlanStatus).apply();
    }

    public static boolean getNotifications() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS, true);
    }

    public static void setMyDealNotifications(boolean notifications) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_FIND_DEAL_NOTIFICATIONS, notifications).apply();
    }

    public static boolean getMyDealNotifications() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_FIND_DEAL_NOTIFICATIONS, true);
    }

    public static void setUploadPropertyNotifications(boolean notifications) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_UPLOAD_PROPERTY_NOTIFICATIONS, notifications).apply();
    }

    public static boolean getUploadPropertyNotifications() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_UPLOAD_PROPERTY_NOTIFICATIONS, true);
    }

    public static void setNotificationsShowPreviews(boolean notificationsShowPreviews) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS, notificationsShowPreviews).apply();
    }

    public static boolean getNotificationsShowPreviews() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS_SHOW_PREVIEWS, true);
    }

    public static void setNotificationsDoNotDisturb(boolean notificationsDoNotDisturb) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB, notificationsDoNotDisturb).apply();
    }

    public static boolean getNotificationsDoNotDisturb() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB, false);
    }

    public static void setNotificationsDoNotDisturbFrom(String notificationsDoNotDisturbFrom) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM, notificationsDoNotDisturbFrom).apply();
    }

    public static String getNotificationsDoNotDisturbFrom() {
        return getSharedPreferences().getString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_FROM, "");
    }

    public static void setNotificationsDoNotDisturbTo(String notificationsDoNotDisturbTo) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO, notificationsDoNotDisturbTo).apply();
    }

    public static String getNotificationsDoNotDisturbTo() {
        return getSharedPreferences().getString(PREFERENCE_KEY_NOTIFICATIONS_DO_NOT_DISTURB_TO, "");
    }

    public static void setGroupChannelDistinct(boolean channelDistinct) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT, channelDistinct).apply();
    }

    public static boolean getGroupChannelDistinct() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_GROUP_CHANNEL_DISTINCT, true);
    }

    public static void setLastRead(String groupChannelUrl, long ts) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putLong(PREFERENCE_KEY_GROUP_CHANNEL_LAST_READ + groupChannelUrl, ts).apply();
    }

    public static long getLastRead(String groupChannelUrl) {
        return getSharedPreferences().getLong(PREFERENCE_KEY_GROUP_CHANNEL_LAST_READ + groupChannelUrl, Long.MAX_VALUE);
    }

    public static void setContactListResponse(String jsonResponse) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_CONTACT_LIST_RESPONSE, jsonResponse).apply();
    }

    public static String getContactListResponse() {
        return getSharedPreferences().getString(PREFERENCE_KEY_CONTACT_LIST_RESPONSE, "");
    }

    public static void setConnected(boolean tf) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_CONNECTED, tf).apply();
    }

    public static boolean getConnected() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_CONNECTED, false);
    }

    public static void setContactSync(boolean tf) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_SYNC_CONTACT, tf).apply();
    }

    public static boolean getContactSync() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_SYNC_CONTACT, false);
    }

    public static void setAccessToken(String accessToken) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_ACCESS_TOKEN, accessToken).apply();
    }

    public static String getAccessToken() {
        return getSharedPreferences().getString(PREF_KEY_ACCESS_TOKEN, "");
    }


    public static void setPushToken(String pushToken) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREF_KEY_PUSH_TOKEN, pushToken).apply();
    }

    public static String getPushToken() {
        return getSharedPreferences().getString(PREF_KEY_PUSH_TOKEN, "");
    }

    public static String getUserPro(){
        return getSharedPreferences().getString(USER_PRO,"");
    }
    public static void setUserPro(String userPro){
         SharedPreferences.Editor editor = getSharedPreferences().edit();
         editor.putString(USER_PRO,userPro).apply();
    }

}
