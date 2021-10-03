package com.ramez.shopp.Firebase;

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
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.onesignal.OneSignal;
import com.ramez.shopp.ApiHandler.DataFeacher;
import com.ramez.shopp.Classes.Constants;
import com.ramez.shopp.Classes.UtilityApp;
import com.ramez.shopp.Models.MemberModel;
import com.ramez.shopp.Models.ResultAPIModel;
import com.ramez.shopp.R;

public class MessagingService extends FirebaseMessagingService {

    private static final String ADMIN_CHANNEL_ID = "admin_channel";
    String activity = "";
    private NotificationManager notificationManager;

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
        Log.i("TAG", "Log  onNewToken token " + token);

        OneSignal.idsAvailable((userId, registrationId) -> {
            Log.d("debug", "Log User:" + userId);
            if (registrationId != null)
                UtilityApp.setFCMToken(OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId());

            if (UtilityApp.getUserData() != null && UtilityApp.getUserData().getId() != null) {
                String updateToken = OneSignal.getPermissionSubscriptionState().getSubscriptionStatus().getUserId();
                MemberModel memberModel = UtilityApp.getUserData();
                memberModel.setDeviceToken(updateToken);
                UtilityApp.setUserData(memberModel);
                UpdateToken(memberModel);
            }

        });

    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        try {
            Log.d("onMessageReceived", "Log remoteMessage: " + remoteMessage.getData());


        } catch (Exception e) {
            Log.d("onMessageReceived", "Log remoteMessage Exception: " + e);
        }

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        try {


            if (remoteMessage.getNotification() != null) {
                sendNotification(remoteMessage.getNotification().getBody(), remoteMessage.getNotification().getTitle());
            }
        } catch (Exception e) {

        }
    }

    public void sendNotification(String message, String title) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            setupChannels();
        }

        Intent intent = new Intent(this, Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.small_logo);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, ADMIN_CHANNEL_ID).setLargeIcon(largeIcon).setContentTitle(title).setContentText(message).setSmallIcon(R.drawable.small_logo).setAutoCancel(true).setSound(soundUri).setColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)).setContentIntent(pendingIntent);

        notificationManager.notify(0 /*ID of notification*/, builder.build());


    }

    private void setupChannels() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence adminChannelName = getString(R.string.app_name);
            String adminChannelDescription = getString(R.string.app_name);

            NotificationChannel adminChannel;
            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
            adminChannel.setDescription(adminChannelDescription);
            adminChannel.enableLights(true);
            adminChannel.setLightColor(Color.RED);
            adminChannel.enableVibration(true);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(adminChannel);
            }
        }

    }


    private void UpdateToken(MemberModel memberModel) {

        new DataFeacher(false, (obj, func, IsSuccess) -> {
            ResultAPIModel<String> result = (ResultAPIModel) obj;
            if (IsSuccess) {
                Log.i("TAG", "Log  UpdateToken Success ");


            }


        }).UpdateTokenHandle(memberModel);

    }
}


//
//    private static final String TAG = "MyFirebaseMsgService";
//    private static final String ADMIN_CHANNEL_ID = "admin_channel";
//    private NotificationManager notificationManager;
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//
//        Log.d(TAG, "From: " + remoteMessage.getFrom());
//
//        if (remoteMessage.getData().size() > 0) {
//            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
//
//            /* Check if data needs to be processed by long running job */
//            if ( true) {
//                scheduleJob();
//            } else {
//
//                // Handle message within 10 seconds
//                handleNow();
//            }
//
//        }
//
//        // Check if message contains a notification payload.
//        if (remoteMessage.getNotification() != null) {
//            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
//
//            sendNotification(remoteMessage.getNotification().getBody());
//
//        }
//
//
//        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        // Also if you intend on generating your own notifications as a result of a received FCM
//        // message, here is where that should be initiated. See sendNotification method below.
//    }
//    // [END receive_message]
//
//
//    // [START on_new_token]
//    /**
//     * Called if FCM registration token is updated. This may occur if the security of
//     * the previous token had been compromised. Note that this is called when the
//     * FCM registration token is initially generated so this is where you would retrieve
//     * the token.
//     */
//    @Override
//    public void onNewToken(String token) {
//        Log.d(TAG, "Refreshed token: " + token);
//
//        // If you want to send messages to this application instance or
//        // manage this apps subscriptions on the server side, send the
//        // FCM registration token to your app server.
//        sendRegistrationToServer(token);
//        UtilityApp.setFCMToken(token);
//    }
//    // [END on_new_token]
//
//    /**
//     * Schedule async work using WorkManager.
//     */
//    private void scheduleJob() {
//        // [START dispatch_job]
//        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
//                .build();
//        WorkManager.getInstance().beginWith(work).enqueue();
//        // [END dispatch_job]
//    }
//
//    /**
//     * Handle time allotted to BroadcastReceivers.
//     */
//    private void handleNow() {
//        Log.d(TAG, "Short lived task is done.");
//    }
//
//    /**
//     * Persist token to third-party servers.
//     *
//     * Modify this method to associate the user's FCM registration token with any
//     * server-side account maintained by your application.
//     *
//     * @param token The new token.
//     */
//    private void sendRegistrationToServer(String token) {
//        // TODO: Implement this method to send token to your app server.
//    }
//
//    /**
//     * Create and show a simple notification containing the received FCM message.
//     *
//     * @param messageBody FCM message body received.
//     */
//    private void sendNotification(String messageBody) {
//        Intent intent = new Intent(this, Constants.INSTANCE.getMAIN_ACTIVITY_CLASS());
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
//                PendingIntent.FLAG_ONE_SHOT);
//
//        String channelId = getString(R.string.default_notification_channel_id);
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        NotificationCompat.Builder notificationBuilder =
//                new NotificationCompat.Builder(this, channelId)
//                        .setSmallIcon(R.drawable.small_logo)
//                        .setContentTitle(getString(R.string.app_name))
//                        .setContentText(messageBody)
//                        .setAutoCancel(true)
//                        .setSound(defaultSoundUri)
//                        .setContentIntent(pendingIntent);
//
//         notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Since android Oreo notification channel is needed.
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
////            NotificationChannel channel = new NotificationChannel(channelId,
////                    "Channel human readable title",
////                    NotificationManager.IMPORTANCE_DEFAULT);
////            notificationManager.createNotificationChannel(channel);
//
//            CharSequence adminChannelName = getString(R.string.app_name);
//            String adminChannelDescription = getString(R.string.app_name);
//
//            NotificationChannel adminChannel;
//            adminChannel = new NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW);
//            adminChannel.setDescription(adminChannelDescription);
//            adminChannel.enableLights(true);
//            adminChannel.setLightColor(Color.RED);
//            adminChannel.enableVibration(true);
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(adminChannel);
//            }
//
//        }
//
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
//    }
//
//
//
//}