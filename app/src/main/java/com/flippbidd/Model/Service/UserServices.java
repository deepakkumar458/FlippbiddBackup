package com.flippbidd.Model.Service;


import com.flippbidd.Model.BidListResponse;
import com.flippbidd.Model.IPortal.Response;
import com.flippbidd.Model.Response.AddCommon.AddResponse;
import com.flippbidd.Model.Response.ChannelCreatedResponse;
import com.flippbidd.Model.Response.Chat.Chatresponse;
import com.flippbidd.Model.Response.CommonList.CommonListResponse;
import com.flippbidd.Model.Response.CommonResponse;
import com.flippbidd.Model.Response.CommonResponse_;
import com.flippbidd.Model.Response.Data.DetailsTypeRespons;
import com.flippbidd.Model.Response.DealDetailsResponse;
import com.flippbidd.Model.Response.DeleteAccountResponse;
import com.flippbidd.Model.Response.Feedback.PendingFeedback;
import com.flippbidd.Model.Response.Filter.FileterData;
import com.flippbidd.Model.Response.Likes.LikesResponse;
import com.flippbidd.Model.Response.Likes.Service.ServiceLikeListResponse;
import com.flippbidd.Model.Response.LoginResponse;
import com.flippbidd.Model.Response.Notification.NotificationListRespons;
import com.flippbidd.Model.Response.OtherUserDetailsResponse;
import com.flippbidd.Model.Response.Plan.SubcribPlanResponse;
import com.flippbidd.Model.Response.PropertyStateWise.PropertyStateWiseResponse;
import com.flippbidd.Model.Response.Rating.RetingResponseList;
import com.flippbidd.Model.Response.RecentlySearchResponse;
import com.flippbidd.Model.Response.Service.ServiceListResponse;
import com.flippbidd.Model.Response.TempModel;
import com.flippbidd.Model.Response.TypeList.CommonTypeResponse;
import com.google.gson.JsonElement;


import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;

/**
 * Created by Thems on 21-12-2017.
 */

public interface UserServices {
    /*
      1) TODO User Login API
    */
    @Multipart
    @POST("login")
    Observable<LoginResponse> userLogin(@PartMap Map<String, RequestBody> requestData);


    @Multipart
    @POST("contacts")
    Observable<TempModel> addContact(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("forgot_password")
    Observable<CommonResponse> userForgotPassword(@PartMap Map<String, RequestBody> requestData);

    /*
     4) TODO Sing Up API
   */
    @Multipart
    @POST("register")
    Observable<LoginResponse> userSingUp(@PartMap Map<String, RequestBody> requestData);

    /*
     5) TODO Change Password  API
   */
    @Multipart
    @POST("change_password")
    Observable<CommonResponse> changePassword(@PartMap Map<String, RequestBody> requestData);

    /*
     6) TODO Seller Property API
   */
    @Multipart
    @POST("common/property")
    Observable<CommonListResponse> commonListApi(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("get/allstate/property/list")
    Observable<PropertyStateWiseResponse> stateWiseProperty(@PartMap Map<String, RequestBody> requestData);


//    /*
//     7) TODO Add New Property Seller API
//   */
//    @Multipart
//    @POST("property/my/new")
//    Observable<CommonResponse> addNewProperty(@Part MultipartBody.Part fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);

    /*
     7) TODO Add New Property Seller API
   */
    @Multipart
    @POST("property/my/new")
    Observable<AddResponse> addNewProperty(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);


    /*
     10) TODO Add Quick Property API
   */
    @Multipart
    @POST("property/quick/new")
    Observable<AddResponse> addQuickProperty(@PartMap Map<String, RequestBody> requestData);

    /*
     11) TODO Add Delete Property API
   */
    @Multipart
    @POST("property/my/delete")
    Observable<CommonResponse> propertyDelete(@PartMap Map<String, RequestBody> requestData);

    /*
       13) TODO Edit Property Seller API
     */
    @Multipart
    @POST("property/my/edit")
    Observable<AddResponse> editProperty(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);

    /*
       14) TODO  Property LIKES API
     */
    @Multipart
    @POST("property_like")
    Observable<CommonResponse> propertyLikes(@PartMap Map<String, RequestBody> requestData);

    /*
       15) TODO Edit Property UNAVAILABLE API
     */
    @Multipart
    @POST("property_unavailable")
    Observable<CommonResponse> propertyUnavailable(@PartMap Map<String, RequestBody> requestData);


    /*
       18) TODO Add new rental API
     */
    @Multipart
    @POST("property/rental/my/new")
    Observable<AddResponse> addNewRental(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);

    /*
       19) TODO Edit Rental API
     */
    @Multipart
    @POST("property/rental/my/edit")
    Observable<AddResponse> editNewRental(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);

    /*
       19) TODO Delete Rental API
     */
    @Multipart
    @POST("property/rental/my/delete")
    Observable<CommonResponse> deleteRental(@PartMap Map<String, RequestBody> requestData);

    /*
       20) TODO Unavailable Rental API
     */
    @Multipart
    @POST("rental_unavailable")
    Observable<CommonResponse> unavailableRental(@PartMap Map<String, RequestBody> requestData);

    /*
       21) TODO Likes Rental API
     */
    @Multipart
    @POST("rental_like")
    Observable<CommonResponse> likesRental(@PartMap Map<String, RequestBody> requestData);

    /*
       23) TODO Common Type List API
     */
    @Multipart
    @POST("common/type")
    Observable<CommonTypeResponse> getListdata(@PartMap Map<String, RequestBody> requestData);


    /*
   20) TODO Unavailable Rental API
   */
    @Multipart
    @POST("common/property/like_list")
    Observable<LikesResponse> likesList(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("common/property/like_list")
    Observable<ServiceLikeListResponse> likesserviceList(@PartMap Map<String, RequestBody> requestData);

    /*
     21) TODO Add New Service  API
   */
    @Multipart
    @POST("property/service/my/new")
    Observable<AddResponse> addNewService(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);

    /*
     21) TODO Add New Service  API
   */
    @Multipart
    @POST("property/service/my/edit")
    Observable<AddResponse> editNewService(@Part List<MultipartBody.Part> fileThumb, @Part MultipartBody.Part docFile, @PartMap Map<String, RequestBody> requestData);


    /*
     22) TODO Service List API
   */
    @Multipart
    @POST("property/service/list")
    Observable<ServiceListResponse> serviceList(@PartMap Map<String, RequestBody> requestData);

    /*
     23) TODO My Service List API
   */
    @Multipart
    @POST("property/service/my/list")
    Observable<ServiceListResponse> myServiceList(@PartMap Map<String, RequestBody> requestData);

    /*
     24) TODO Delete Service API
   */
    @Multipart
    @POST("property/service/my/delete")
    Observable<CommonResponse> deleteService(@PartMap Map<String, RequestBody> requestData);

    /*
       19) TODO Edit Rental API
     */
    @Multipart
    @POST("edit_profile")
    Observable<LoginResponse> editProfile(@Part MultipartBody.Part ediProfile, @Part MultipartBody.Part ediPofDoc, @PartMap Map<String, RequestBody> requestData);


    /*
        222) TODO CHAT DIALOG
        */
    @Multipart
    @POST("chat")
    Observable<CommonResponse> chat(@PartMap Map<String, RequestBody> data);


    /*
   52) TODO CHAT LIST
   */
    @Multipart
    @POST("chat/list")
    Observable<Chatresponse> chatList(@PartMap Map<String, RequestBody> data);

    //new chat api imaplements

    /*
       222) TODO CHAT DIALOG
       */
    @Multipart
    @POST("common/property/update_sender_qb_id")
    Observable<CommonResponse> updateSenderQbId(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO CHAT DIALOGBody
       */
    @Multipart
    @POST("common/property/details")
    Observable<DetailsTypeRespons> getDetails(@PartMap Map<String, RequestBody> data);


    /*
   222) TODO CHAT DIALOG
   */
    @Multipart
    @POST("service_like")
    Observable<CommonResponse> isServiceLike(@PartMap Map<String, RequestBody> data);

    /*
   222) TODO Create Filter
   */
    @Multipart
    @POST("notification/filter/add")
    Observable<FileterData> addFilter(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Update Count
       */
    @Multipart
    @POST("count_message")
    Observable<CommonResponse> updateMessageCount(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Update Count
       */
    @Multipart
    @POST("update/latest/time")
    Observable<CommonResponse> updateDialogTimeStamp(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Get Message Count
       */
    @Multipart
    @POST("get_count_message")
    Observable<CommonResponse> getMessageCount(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Get Message Count
       */
    @Multipart
    @POST("plan/subscription")
    Observable<LoginResponse> callPaymentMethod(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Get Message Count
       */
    @Multipart
    @POST("plan/list")
    Observable<SubcribPlanResponse> getPlanList(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Get Message Count
       */
    @Multipart
    @POST("cancel/subscription")
    Observable<CommonResponse> planCancled(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Block User
       */
    @Multipart
    @POST("chat/block")
    Observable<CommonResponse> blockuser(@PartMap Map<String, RequestBody> data);

    /* /*
       222) TODO Block User
       */
    @Multipart
    @POST("chat/unblock")
    Observable<CommonResponse> unblockuser(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO Block User
       */
    @Multipart
    @POST("chat/delete")
    Observable<CommonResponse> deleteChat(@PartMap Map<String, RequestBody> data);

    //Notification List
    @Multipart
    @POST("get_notification_list")
    Observable<NotificationListRespons> getNotificationList(@PartMap Map<String, RequestBody> data);

    //Notification Delete
    @Multipart
    @POST("delete/notification")
    Observable<CommonResponse> deleteNotification(@PartMap Map<String, RequestBody> data);


    /*
       222) TODO Push message
       */
    @Multipart
    @POST("chat/notification")
    Observable<CommonResponse> messagePush(@PartMap Map<String, RequestBody> data);

    /*
       222) TODO user details
       */
    @Multipart
    @POST("get_user_details")
    Observable<LoginResponse> getUserDetails(@PartMap Map<String, RequestBody> data);


    /*
       222) TODO user details
       */
    @Multipart
    @POST("rate/add")
    Observable<CommonResponse> giveFeedbackUser(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("rate/list")
    Observable<RetingResponseList> getRatingList(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("get_friends")
    Observable<JsonElement> uploadContact(@PartMap Map<String, RequestBody> data);


    @Multipart
    @POST("check_channel_status")
    Observable<ChannelCreatedResponse> channelStatusChecked(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("create_channel")
    Observable<ChannelCreatedResponse> createChannel(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("delete_channel")
    Observable<CommonResponse> deleteChannel(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("update_channel_status")
    Observable<CommonResponse> updateChannelStatus(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("update_mobile_number")
    Observable<CommonResponse> updateMobileNumber(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("common/document/list")
    Observable<JsonElement> getDocumentsList(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("common/document/delete")
    Observable<CommonResponse> documentsDelete(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("common/upload_files")
    Observable<CommonResponse_> documentsUploadFile(@PartMap Map<String, RequestBody> data, @Part MultipartBody.Part docFile);

    @Multipart
    @POST("common/upload_files")
    Observable<CommonResponse_> documentsUploadFile(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("property_owner/mail")
    Observable<CommonResponse> OwnerMail(@PartMap Map<String, RequestBody> data, @Part MultipartBody.Part docFile);

    @Multipart
    @POST("property_submit/mail")
    Observable<CommonResponse> requestSubmitProperty(@Part List<MultipartBody.Part> fileThumb, @Part List<MultipartBody.Part> docFile, @PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("bidd/list")
    Observable<BidListResponse> biddList(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("bidd/add")
    Observable<CommonResponse_> addBidd(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("call/count")
    Observable<CommonResponse_> callCount(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("property/add_thums")
    Observable<CommonResponse_> addThum(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("submit/review")
    Observable<CommonResponse_> reportSubmit(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("get_other_user_details")
    Observable<OtherUserDetailsResponse> getOtherUserDetails(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("help/mail")
    Observable<CommonResponse_> helMail(@Part MultipartBody.Part image1, @Part MultipartBody.Part image2, @Part MultipartBody.Part image3, @PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("chat/push/send")
    Observable<CommonResponse_> pushPock(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("notification/setting")
    Observable<CommonResponse_> updateMyDealNotificationStatus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("push_test")
    Observable<CommonResponse_> pustNotificationTest(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("property_upload/notification/setting")
    Observable<CommonResponse_> updatePropertyUploadNotificationStatus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("property_view/notification/setting")
    Observable<CommonResponse_> updateViewNotificationStatus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("find/deal")
    Observable<JsonElement> findMyDeal(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("find/deal/search_list")
    Observable<RecentlySearchResponse> getFindMyDeal(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("other/find/deal/search_list")
    Observable<RecentlySearchResponse> getOtherFindMyDeal(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("find/deal/list")
    Observable<RecentlySearchResponse> getFindDealList(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("location/update")
    Observable<CommonResponse> updateLocation(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("bidd/status")
    Observable<CommonResponse> updateBiddStatus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("property/folder/request")
    Observable<CommonResponse_> propertyRequestData(@PartMap Map<String, RequestBody> requestData);

    @GET("app/version")
    Observable<JsonElement> getVersionData();

    @Multipart
    @POST("property/folder/request_list")
    Observable<JsonElement> requestDataList(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("pending_meeting_request_count")
    Observable<JsonElement> getMeetingCounts(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/add")
    Observable<JsonElement> requestMeetingSchedule(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/edit")
    Observable<JsonElement> editMeetingSchedule(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/list")
    Observable<JsonElement> requestMeetingScheduleList(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/list/byfilter")
    Observable<JsonElement> getListByDateFilter(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/delete")
    Observable<CommonResponse> requestMeetingScheduleDelete(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/request/response")
    Observable<CommonResponse> requestMeetingScheduleStatusUpdate(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("meeting/call/visible/status")
    Observable<JsonElement> requestMeetingScheduleStatus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("pending_feedback_meeting_list")
    Observable<PendingFeedback> pendingRequestFeedback(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("add/meeting_feedback")
    Observable<JsonElement> feedbackAdd(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("inapp/plan/list")
    Observable<JsonElement> inappPlanList(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("inapp/plan/purchase")
    Observable<JsonElement> inappStatusUpdate(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("get/user/inapp/subscription")
    Observable<JsonElement> getMeetingCountsRemaning(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("property/image/delete")
    Observable<CommonResponse> imageDelete(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("device_token_update")
    Observable<CommonResponse> deviceTokenUpdate(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("find/deal/details")
    Observable<DealDetailsResponse> dealDetails(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("logout")
    Observable<CommonResponse_> logout(@PartMap Map<String, RequestBody> data);

    @Multipart
    @POST("user/iportal/add")
    Observable<Response> addIPortal(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("user/premium/status")
    Observable<Response> checkPremiumUserStstus(@PartMap Map<String, RequestBody> requestData);

    @Multipart
    @POST("user/delete")
    Observable<DeleteAccountResponse>allowUserToDeleteAccount(@PartMap Map<String,RequestBody> data);



}