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
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rtoosh.provider.R;
import com.rtoosh.provider.model.Constants;
import com.rtoosh.provider.model.RPPreferences;
import com.rtoosh.provider.views.MainActivity;
import com.rtoosh.provider.views.OrderDetailsActivity;
import com.rtoosh.provider.views.PhoneVerificationActivity;
import com.rtoosh.provider.views.RequestsActivity;

import java.util.List;

import timber.log.Timber;

public class NotificationController extends FirebaseMessagingService {

    private static final String TAG = NotificationController.class.getSimpleName();
    NotificationManager mNotificationManager;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e(TAG, "Notification -->>>>> "+remoteMessage.getData().toString());
        String notify_type = remoteMessage.getData().get("notify_type");
        String accountStatus = remoteMessage.getData().get("account_status");
        String message = remoteMessage.getData().get("message");
        String orderId = remoteMessage.getData().get("orderID");
        String payment_mode = remoteMessage.getData().get("payment_mode");
        String orderType = remoteMessage.getData().get("orderType");

        Timber.e("order id-- "+orderId);
        if (message == null)
            return;

        String user_id = RPPreferences.readString(this, Constants.USER_ID_KEY);
        if (!user_id.isEmpty()) {
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
                sendNotification(message, orderId, orderType);
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
        CharSequence name = getString(R.string.account_status);
        // The user-visible description of the channel.
        String description = getString(R.string.notification_account_status);
           /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");*/
        showSmallNotification(mBuilder, R.mipmap.ic_launcher, getString(R.string.app_name),
                messageBody, resultPendingIntent, name, description);
    }

    private void startService(String messageBody, String requestId, String orderType) {
        Intent intent = new Intent(getApplicationContext(), OrderDetailsActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("request_id", requestId);
        startActivity(intent);
    }

    private void sendNotification(String messageBody, String requestId, String orderType) {
        if (orderType != null && orderType.equals(Constants.ORDER_ONLINE)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("service_request", true);
            intent.putExtra("order_id", requestId);
            startActivity(intent);
        } else {
            int code = (int) System.currentTimeMillis();
            Intent intent = new Intent(getApplicationContext(), RequestsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            final PendingIntent resultPendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    code, intent, PendingIntent.FLAG_UPDATE_CURRENT);


            final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext());


            // The user-visible name of the channel.
            CharSequence name = getString(R.string.service_requests);
            // The user-visible description of the channel.
            String description = getString(R.string.customer_service_request);
           /* final Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + getApplicationContext().getPackageName() + "/raw/notification");*/
            showSmallNotification(mBuilder, R.mipmap.ic_launcher, getString(R.string.app_name),
                    messageBody, resultPendingIntent, name, description);
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message,
                                       PendingIntent resultPendingIntent, CharSequence name, String description) {
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // The id of the channel.
        String id = "rtoosh_provider";
        NotificationChannel mChannel;
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
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
                .setChannelId(id)
                //.setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                .build();

        if (mNotificationManager != null)
            mNotificationManager.notify(0, notification);
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

