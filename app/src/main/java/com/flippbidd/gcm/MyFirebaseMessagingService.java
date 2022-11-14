/**
 * Copyright 2016 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.flippbidd.gcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.flippbidd.MainActivity;
import com.flippbidd.Others.Constants;
import com.flippbidd.Others.UserPreference;
import com.flippbidd.R;
import com.flippbidd.SplashActivity;
import com.flippbidd.activity.Details.PropertyDetails;
import com.flippbidd.baseclass.BaseApplication;
import com.flippbidd.utils.PreferenceUtils;
import com.flippbidd.utils.PushUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.calls.SendBirdCall;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    private static final AtomicReference<String> pushToken = new AtomicReference<>();

    public interface ITokenResult {
        void onPushTokenReceived(String pushToken);
    }

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "onNewToken(" + token + ")");

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(final String token) {
        SendBird.registerPushTokenForCurrentUser(token, new SendBird.RegisterPushTokenWithStatusHandler() {
            @Override
            public void onRegistered(SendBird.PushTokenRegistrationStatus pushTokenRegistrationStatus, SendBirdException e) {
                if (e != null) {
                    //Toast.makeText(MyFirebaseMessagingService.this, "" + e.getCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pushTokenRegistrationStatus == SendBird.PushTokenRegistrationStatus.PENDING) {
                    //Toast.makeText(MyFirebaseMessagingService.this, "Connection required to register push token.", Toast.LENGTH_SHORT).show();
                }
                pushToken.set(token);
            }
        });

        if (SendBirdCall.getCurrentUser() != null) {
            PushUtils.registerPushToken(token, e -> {
                if (e != null) {
                    Log.i(BaseApplication.TAG, "[MyFirebaseMessagingService] registerPushTokenForCurrentUser() => e: " + e.getMessage());
                }
            });
        } else {
            PreferenceUtils.setPushToken(token);
        }
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String channelUrl = null;
        if (remoteMessage.getData().containsKey("sendbird")) {
            JSONObject sendBird = null;
            try {
                sendBird = new JSONObject(remoteMessage.getData().get("sendbird"));
                JSONObject channel = (JSONObject) sendBird.get("channel");
                channelUrl = (String) channel.get("channel_url");

                SendBird.markAsDelivered(channelUrl);
                // Also if you intend on generating your own notifications as a result of a received FCM
                // message, here is where that should be initiated. See sendNotification method below.
                sendNotification(this, remoteMessage.getData().get("message"), channelUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (SendBirdCall.handleFirebaseMessageData(remoteMessage.getData())) {
            Log.d(BaseApplication.TAG, "[MyFirebaseMessagingService] onMessageReceived() => " + remoteMessage.getData().toString());
        } else {

            final String message = remoteMessage.getData().get("message");
            final String title = remoteMessage.getData().get("title");

            if (title != null && !title.equalsIgnoreCase("")) {
                if (title.equalsIgnoreCase("Admin Notification")) {
                    //notification form Admin side
                    sendNotification(message, "From Admin", "", "Admin", "", "", remoteMessage);
                } else {
                    final String messageTitle = remoteMessage.getData().get("title");
                    final String common_id = remoteMessage.getData().get("common_id");
                    final String mType = remoteMessage.getData().get("type");
                    final String mNotificationId = remoteMessage.getData().get("noti_id");
                    sendNotification(message, messageTitle, common_id, mType, mNotificationId, "", remoteMessage);
                }
            }

            /*Bundle[{google.delivered_priority=high, google.sent_time=1625731613670, google.ttl=60, google.original_priority=high, from=707002053850, google.message_id=0:1625731613689446%3b6d4efdf9fd7ecd, sendbird_call={"user_id":"shah3625@gmail.com","push_sound":"default","is_voip":true,"push_alert":"Incoming call from Milap Shah","command":{"sequence_number":0,"payload":{"turn_credential":{"password":"Lvk0icJe5bJP8UfAzq6BAwJ\/W3g=","transport_policy":"all","turn_urls":["turn:turn-18-205-7-22-us-3.calls.sendbird.com:5349"],"username":"1625818013:234D8778-355D-4777-A6D7-EA10C7EA290E-c2hhaDM2MjVAZ21haWwuY29t"},"caller":{"metadata":{},"is_active":true,"role":"dc_caller","profile_url":"","user_id":"shah2536@gmail.com","nickname":"Milap Shah"},"capabilities":["turn_changed"],"is_video_call":false,"sbcall_short_lived_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJhIjoiMEZGMjlBRDctNENBRS00NUEyLUE1NUEtMjU3RTBBNEFEMzM3IiwidSI6InNoYWgzNjI1QGdtYWlsLmNvbSIsInYiOjEsImV4cCI6MTYyNTczMTY3M30.sxwArT7GhjO9Ga8rbyTzshl2-2tgTaemGp9mJHQYDeU","callee":{"metadata":{},"is_active":true,"role":"dc_callee","profile_url":"","user_id":"shah3625@gmail.com","nickname":"Urmil Shah"},"constraints":{"audio":true,"video":true},"custom_items":{},"call_id":"234D8778-355D-4777-A6D7-EA10C7EA290E"},"delivery_info":{"type":"push"},"message_id":"1c524200-2b54-4990-9604-f9928d7fbef6","cmd":"CALL","type":"dial","call_type":"direct_call","version":1},"receiver_type":"user"}, message=Incoming call from Milap Shah, google.c.sender.id=707002053850}]*/
            Log.d(TAG, "From: " + "remoteMessage.getFrom() Harsh Shah....");
        }


    }

    private void sendNotification(String message, final String messageTitle, String common_id, String mType, String mNotificationId, String mesagTypeValues, RemoteMessage moBuild) {
        String pushMessage = "", messageType = "";
        try {
            String CHANNEL_ID = "my_channel_01";
            pushMessage = message;
            messageType = messageTitle;
            int notificationId = (int) System.currentTimeMillis();


            NotificationCompat.Builder mBuilder = null;
            mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID).setAutoCancel(true).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle(messageType)
                    .setWhen(System.currentTimeMillis()).setDefaults(Notification.DEFAULT_SOUND).setLights(Color.GREEN, 500, 1000).setContentText(pushMessage).setStyle(new NotificationCompat.BigTextStyle().bigText(pushMessage));
            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            Intent notificationIntent = null;

            if (UserPreference.getInstance(this).isUserLogin()) {
                if (mType.equalsIgnoreCase("sent_no")) {
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "push");
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, mNotificationId);

                } else if (mType.equalsIgnoreCase("Admin")) {
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "admin");
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "0");
                } else if (mType.equalsIgnoreCase("POF update reminder")) {
                    //POF reminder push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "profile_edit");
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "8");
                } else if (mType.equalsIgnoreCase("Add New Bidd")) {
                    //New Bidd Add push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "new_bidd");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra("isBidd", 1);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "9");

                } else if (mType.equalsIgnoreCase("Ratting")) {
                    //Add rate push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "add_rate");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "10");

                } else if (mType.equalsIgnoreCase("Document Folder View")) {
                    //Add rate push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "doc_view");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "11");

                } else if (mType.equalsIgnoreCase("Property View")) {
                    //Add rate push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "property_view");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "16");

                } else if (mType.equalsIgnoreCase("Poke")) {
                    //poke push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "Poke");
                    notificationIntent.putExtra("GROUP_CHANNEL_URL", moBuild.getData().get("url"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "12");

                } else if (mType.equalsIgnoreCase("property_trending")) {
                    //Trending push notification
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "property_trending");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "13");

                } else if (mType.equalsIgnoreCase("find_my_deal_done")) {
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, PropertyDetails.class);
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, "created_user");
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.EXPIRED_STATUS, false);
                    notificationIntent.putExtra(Constants.EXTRA.FROM_TO, "notification_to");

                } else if (mType.equalsIgnoreCase("find_my_deal")) {
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "find_my_deal");
                    notificationIntent.putExtra(Constants.EXTRA.ADDRESS, moBuild.getData().get("address"));
                    notificationIntent.putExtra(Constants.EXTRA.DEAL_ID, moBuild.getData().get("deal_id"));
                    notificationIntent.putExtra(Constants.EXTRA.LAT, moBuild.getData().get("lat"));
                    notificationIntent.putExtra(Constants.EXTRA.LANG, moBuild.getData().get("lang"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "15");

                } else if (mType.equalsIgnoreCase("Bidd Accepted")) {
                    //request contract screen
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "bidd_accept");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "16");

                } else if (mType.equalsIgnoreCase("Document Request") || mType.equalsIgnoreCase("Property Document")) {
                    //request contract screen
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "request_data_folder");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "17");

                } else if (mType.equalsIgnoreCase("flippbidd_call")) {
                    //request contract screen
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "Flippbidd Call");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.PROPERTY_ADDRESS, moBuild.getData().get("address"));
                    notificationIntent.putExtra(Constants.EXTRA.OWNER_NAME, moBuild.getData().get("owner_name"));
                    notificationIntent.putExtra(Constants.EXTRA.OWNER_EMAIL, moBuild.getData().get("owner_email"));
                    notificationIntent.putExtra(Constants.EXTRA.MEETING_TYPE, moBuild.getData().get("meeting_type"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_TIME_TYPE, moBuild.getData().get("noti_type"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "19");

                } else if (mType.equalsIgnoreCase("Call Request")
                        || mType.equalsIgnoreCase("Suggest New Time")
                        || mType.equalsIgnoreCase("Call Request Accepted")
                        || mType.equalsIgnoreCase("Call Request Reject")) {
                    //request new time for meeting
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, MainActivity.class);
                    notificationIntent.putExtra(Constants.EXTRA.TYPE_PUSH, "call_view");
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, moBuild.getData().get("login_id"));
                    notificationIntent.putExtra(Constants.EXTRA.NOTIFICATION_ID, "18");

                } else if (mType.equalsIgnoreCase("Push Test")) {
                    notificationIntent = new Intent();
                } else {
                    notificationIntent = new Intent(MyFirebaseMessagingService.this, PropertyDetails.class);
                    notificationIntent.putExtra(Constants.EXTRA.DATA, common_id);
                    notificationIntent.putExtra(Constants.EXTRA.LOGINID, "created_user");
                    notificationIntent.putExtra(Constants.EXTRA.SCREEN_TYPE, mType);
                    notificationIntent.putExtra(Constants.EXTRA.EXPIRED_STATUS, false);
                    notificationIntent.putExtra(Constants.EXTRA.FROM_TO, "notification_to");
                }
            } else {
                notificationIntent = new Intent(MyFirebaseMessagingService.this, SplashActivity.class);
            }

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            mBuilder.setChannelId(CHANNEL_ID);
            mBuilder.setSmallIcon(getNotificationIcon());

            if (mesagTypeValues.equalsIgnoreCase(Constants.PUSH_TYPE.PUSH_PHOTO)) {
                mBuilder.setLargeIcon(getBitmapFromURL(pushMessage));
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, messageTitle, importance);
                mChannel.setDescription(message);
                mChannel.enableLights(true);
                mChannel.setLightColor(ContextCompat.getColor(getApplicationContext(), R.color
                        .colorPrimary));
                mNotificationManager.createNotificationChannel(mChannel);
            }
            mNotificationManager.notify(notificationId, mBuilder.build());


        } catch (Exception e) {
            Log.e("PushError", e.getMessage());
        }
    }

    private int getNotificationIcon() {
        return R.mipmap.ic_launcher_round;
    }


    private Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    public static void sendNotification(Context context, String messageBody, String channelUrl) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        final String CHANNEL_ID = "CHANNEL_ID";
        if (Build.VERSION.SDK_INT >= 26) {  // Build.VERSION_CODES.O
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, "CHANNEL_NAME", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent intent = new Intent(context, SplashActivity.class);
        intent.putExtra("GROUP_CHANNEL_URL", channelUrl);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setColor(Color.parseColor("#41B0DD"))  //7469C4 small icon background color
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(context.getResources().getString(R.string.app_name))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setPriority(Notification.PRIORITY_MAX)
                .setDefaults(Notification.DEFAULT_ALL)
                .setContentIntent(pendingIntent);

        if (PreferenceUtils.getNotificationsShowPreviews()) {
            notificationBuilder.setContentText(messageBody);
        } else {
            notificationBuilder.setContentText("Somebody sent you a message.");
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public static void getPushToken(ITokenResult listner) {
        String token = pushToken.get();
        if (!TextUtils.isEmpty(token)) {
            listner.onPushTokenReceived(token);
            return;
        }

        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                InstanceIdResult result = task.getResult();
                if (result != null) {
                    String token = result.getToken();
                    UserPreference.getInstance(BaseApplication.getInstance()).setDeviceToken(token);
                    Log.d(TAG, "FCM token : " + token);

                    pushToken.set(token);
                    listner.onPushTokenReceived(token);
                }
            }
        });
    }
}