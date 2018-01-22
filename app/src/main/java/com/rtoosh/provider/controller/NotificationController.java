package com.rtoosh.provider.controller;

/*
 * Created by rishav on 11/6/2017.
 */

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.model.custom.Utils;
import com.rtoosh.provider.views.MainActivity;
import com.rtoosh.provider.views.OrderDetailsActivity;
import com.rtoosh.provider.views.PhoneVerificationActivity;
import com.rtoosh.provider.views.RequestsActivity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import timber.log.Timber;

public class NotificationController extends FirebaseMessagingService {

    private static final String TAG = NotificationController.class.getSimpleName();
    NotificationManager mNotificationManager;
    final AtomicInteger c = new AtomicInteger(0);
    String title = "", app_name = "";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Timber.e("Notification -->>>>> %s", remoteMessage.getData().toString());
        String notify_type = remoteMessage.getData().get("notify_type");
        String accountStatus = remoteMessage.getData().get("account_status");
        String message = remoteMessage.getData().get("message");
        String orderId = remoteMessage.getData().get("orderID");
        String payment_mode = remoteMessage.getData().get("payment_mode");
        String orderType = remoteMessage.getData().get("orderType");
        String userID = remoteMessage.getData().get("user_id");
        String orderTime = remoteMessage.getData().get("order_time");

        if (RPPreferences.readString(getApplicationContext(), Constants.LANGUAGE_KEY).equals(Constants.LANGUAGE_AR)) {
            app_name = "رتوش مقدمي الخدمة";
        } else {
            app_name = "Rtoosh Provider";
        }

        if (message == null)
            return;

        String user_id = RPPreferences.readString(this, Constants.USER_ID_KEY);

        if (!user_id.isEmpty() && user_id.equals(userID)) {
            if (notify_type.equals(Constants.NOTIFY_ACCOUNT_STATUS)) {
                RPPreferences.putString(getApplicationContext(), Constants.ACCOUNT_STATUS_KEY, accountStatus);
                switch (accountStatus) {
                    case Constants.ACCOUNT_ACTIVE:
                        accountNotification(Constants.ACCOUNT_ACTIVE, message);
                        break;
                    case Constants.ACCOUNT_REVIEWING:
                        accountNotification(Constants.ACCOUNT_REVIEWING, message);
                        break;
                    case Constants.ACCOUNT_SUSPENDED:
                        accountNotification(Constants.ACCOUNT_SUSPENDED, message);
                        break;

                    case Constants.ACCOUNT_INACTIVE:
                        break;
                }
            } else {
                if (notify_type.equals(Constants.NOTIFY_SERVICE_STARTED)) {
                    startService(message, orderId, orderType);
                    return;
                }
                sendNotification(message, orderId, orderType, orderTime);
            }
        }
    }

    private void accountNotification(String status, String messageBody) {
        int code = (int) System.currentTimeMillis();
        PendingIntent resultPendingIntent;
        Intent intent;

        if (status.equals(Constants.ACCOUNT_SUSPENDED)) {
            intent = new Intent(getApplicationContext(), PhoneVerificationActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    code, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            RPPreferences.clearPref(getApplicationContext());
            startActivity(intent);
        } else {
            intent = new Intent();
            resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        }

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        // The user-visible name of the channel.
        CharSequence name = getApplicationContext().getString(R.string.account_status);
        // The user-visible description of the channel.
        String description = getApplicationContext().getString(R.string.notification_account_status);
           /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");*/
        showSmallNotification(mBuilder, R.drawable.ic_notification_logo, app_name,
                messageBody, resultPendingIntent, name, description, code);
    }

    private void startService(String messageBody, String requestId, String orderType) {
        Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("request_id", requestId);
        startActivity(intent);
    }

    private void sendNotification(String messageBody, String requestId, String orderType, String orderTime) {
        int code = (int) System.currentTimeMillis();
        // The user-visible name of the channel.
        CharSequence name = getApplicationContext().getString(R.string.service_requests);
        // The user-visible description of the channel.
        String description = getApplicationContext().getString(R.string.customer_service_request);
         /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");*/


        int id = c.incrementAndGet();

        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());

        if (orderType != null && orderType.equals(Constants.ORDER_ONLINE)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("service_request", true);
            intent.putExtra("order_id", requestId);
            intent.putExtra("notifyId", id);

            startActivity(intent);
            // Intent notiIntent = new Intent();

            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    code, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            String title = "";
            if (RPPreferences.readString(getApplicationContext(), Constants.LANGUAGE_KEY).equals(Constants.LANGUAGE_EN)) {
                title = "Online Order";
            } else {
                title = "الطلب المباشر";
            }

            showSmallNotification(mBuilder, R.drawable.ic_notification_logo, title,
                    messageBody, resultPendingIntent, name, description, id);

            Utils.clearNotification(getApplicationContext(), id);

        } else {
            String title = "";
            if (RPPreferences.readString(getApplicationContext(), Constants.LANGUAGE_KEY).equals(Constants.LANGUAGE_EN)) {
                title = "Schedule Order";
            } else {
                title = "الطلب المجدول";
            }
            Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    code, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            showSmallNotification(mBuilder, R.drawable.ic_notification_logo, title,
                    messageBody, resultPendingIntent, name, description, id);
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message,
                                       PendingIntent resultPendingIntent, CharSequence name, String description, int notId) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "rtoosh_provider";
        NotificationChannel mChannel;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            mChannel = new NotificationChannel(id, name, importance);

            // Configure the notification channel.
            mChannel.setDescription(description);
            mChannel.enableLights(true);
            // Sets the notification light color for notifications posted to this
            // channel, if the device supports this feature.
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
               // .setSound(alarmSound)
                .setStyle(inboxStyle)
                .setSound(defaultSoundUri)
                .setSmallIcon(R.drawable.ic_notification_logo)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.deep_purple_700))
                .setContentText(message)
                .setChannelId(id)
               // .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .build();

        if (mNotificationManager != null)
            mNotificationManager.notify(notId, notification);
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
     //   if (newIntent == null)
     //       newIntent = new Intent(getApplicationContext(), MainActivity.class);
        return newIntent;
    }
}

