package com.donteco.alarmClock.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.activities.MainActivity;
import com.donteco.alarmClock.help.ConstantsForApp;

import java.util.Arrays;

import static com.facebook.FacebookSdk.getApplicationContext;

public class NotificationBuilder
{

    public NotificationBuilder() {
    }

    public static void createNotificationChanel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    ConstantsForApp.ALARM_CLOCK_NOTIFICATION_CHANNEL,
                    "Alarm clock notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Alarm clock notification to walk up");
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            //Only by this i can get rid of vibration
            channel.enableVibration(true);
            long[] vibration = new long[0];
            channel.setVibrationPattern(vibration);

            NotificationManager manager = NotificationManagingHelper.getNotificationManager();
            manager.createNotificationChannel(channel);
        }
    }
}
