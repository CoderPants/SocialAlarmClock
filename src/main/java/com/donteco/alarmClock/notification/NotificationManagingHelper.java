package com.donteco.alarmClock.notification;

import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationManagerCompat;

public class NotificationManagingHelper {

    //private NotificationManagingHelper manager;
    private static NotificationManager notificationManager;

    private NotificationManagingHelper() {

    }

    public static void createInstance(NotificationManager manager) {
        if(notificationManager == null)
            notificationManager = manager;
    }

    public static NotificationManager getNotificationManager(){
        return notificationManager;
    }
}
