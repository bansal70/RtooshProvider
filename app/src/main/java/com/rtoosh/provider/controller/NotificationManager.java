package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/6/2017.
 */

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.views.MainActivity;

import java.util.List;

import timber.log.Timber;

public class NotificationManager extends FirebaseMessagingService {

    private static final String TAG = NotificationManager.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "Notification -->>>>> "+remoteMessage.getData().toString());
        String message = remoteMessage.getData().get("message");
        String orderId = remoteMessage.getData().get("orderID");
        String payment_mode = remoteMessage.getData().get("payment_mode");

        Timber.e("order id-- "+orderId);
        if (message == null)
            return;

        String user_id = RPPreferences.readString(this, "user_id");
        if (!user_id.isEmpty()) {
            sendNotification(message, orderId);
        }
    }

    private void sendNotification(String messageBody, String requestId) {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("service_request", true);
        RPPreferences.putString(getApplicationContext(), "request_id", requestId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
       /* PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "request_id";
        CharSequence channelName = "Customer Request";
        Notification notificationBuilder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationBuilder = new Notification.Builder(this, channelId)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build();
        }

        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = android.app.NotificationManager.IMPORTANCE_LOW;
        }
        NotificationChannel notificationChannel;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationBuilder.flags |= Notification.FLAG_AUTO_CANCEL;

        notificationManager.notify(0, notificationBuilder);*/

    }

    private Intent getPreviousIntent() {
        Intent newIntent = null;
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final List<ActivityManager.AppTask> recentTaskInfos = activityManager.getAppTasks();
            if (!recentTaskInfos.isEmpty()) {
                for (ActivityManager.AppTask appTaskTaskInfo: recentTaskInfos) {
                        newIntent = appTaskTaskInfo.getTaskInfo().baseIntent;
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
            }
        } else {
            final List<ActivityManager.RecentTaskInfo> recentTaskInfos = activityManager.getRecentTasks(1024, 0);
            if (!recentTaskInfos.isEmpty()) {
                for (ActivityManager.RecentTaskInfo recentTaskInfo: recentTaskInfos) {
                        newIntent = recentTaskInfo.baseIntent;
                        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                }
            }
        }
        if (newIntent == null) newIntent = new Intent();
        return newIntent;
    }
}

