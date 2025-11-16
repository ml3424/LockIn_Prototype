package com.example.lockin_prototype;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {

    // constants as defined in the presentation
    private static final String CHANNEL_ID = "reminder_channel_id";
    private static final String CHANNEL_NAME = "Daily Reminders";
    private static final int NOTIFICATION_ID = 1;
    public static final String MESSAGE_KEY = "user_message_text";

    @Override
    public void onReceive(Context context, Intent intent) {
        // this method runs when the alarm manager triggers the pendingintent

        // 1. retrieve the custom message passed from the activity
        String notificationMessage = intent.getStringExtra(MESSAGE_KEY);
        if (notificationMessage == null || notificationMessage.isEmpty()) {
            notificationMessage = "Time to check LockIn!";
        }

        // 2. build and fire the notification
        showNotification(context, notificationMessage);
    }

    // function to build and fire the notification (The combined logic)
    private void showNotification(Context context, String text) {
        // get the notification manager service
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // build the notification content
        NotificationCompat.Builder notiBuilder = new
                NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info) // icon for the notification
                .setContentTitle("Notifications Demo App")
                .setContentText(text) // the custom text from the user
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notiBuilder.build());
        }
    }

    // static function to create the notification channel
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription("channel for lockin timed reminders");

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }
}