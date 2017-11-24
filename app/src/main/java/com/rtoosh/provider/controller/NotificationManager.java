package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/6/2017.
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.views.MainActivity;
import com.rtoosh.provider.views.RequestsActivity;

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
        String orderType = remoteMessage.getData().get("orderType");

        Timber.e("order id-- "+orderId);
        if (message == null)
            return;

        String user_id = RPPreferences.readString(this, Constants.USER_ID_KEY);
        if (!user_id.isEmpty()) {
            sendNotification(message, orderId, orderType);
        }
    }

    private void sendNotification(String messageBody, String requestId, String orderType) {
        if (orderType.equals(Constants.ORDER_ONLINE)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            // RPPreferences.putString(getApplicationContext(), "request_id", requestId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.putExtra("service_request", true);
            intent.putExtra("order_id", requestId);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
            // RPPreferences.putString(getApplicationContext(), "request_id", requestId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

           /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");*/
            showSmallNotification(mBuilder, R.mipmap.ic_launcher, getString(R.string.app_name),
                    messageBody, resultPendingIntent);
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title,
                                       String message,PendingIntent resultPendingIntent) {

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification;
        notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
               // .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .build();

        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null)
        notificationManager.notify(0, notification);
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

