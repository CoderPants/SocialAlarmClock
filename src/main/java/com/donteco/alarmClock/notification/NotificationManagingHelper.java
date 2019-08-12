package com.donteco.alarmClock.notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

public class NotificationManagingHelper {

    private NotificationManagingHelper manager;

    private NotificationManagerCompat notificationManagerCompat;
    private NotificationManager notificationManager;
    private Context context;

    private NotificationManagingHelper(Context context) {
        notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManager = context.getSystemService(NotificationManager.class);

    }

    /*public static void createInstance(Context context) {
        if(manager == null)
            manager = new NotificationManagingHelper(context);
    }*/

    public static void setNotificationChannel(String channelId, int notificationID)
    {
        //Notification notification = new NotificationCompat.Builder()
    }
}
