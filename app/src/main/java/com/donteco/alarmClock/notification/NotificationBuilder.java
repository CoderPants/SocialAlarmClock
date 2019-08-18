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

    public void createNotificationChanel()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(
                    ConstantsForApp.CHANNEL_ID,
                    "Alarm clock notification",
                    NotificationManager.IMPORTANCE_HIGH
            );

            channel.setDescription("Alarm clock notification to walk up");

            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            channel.setSound(Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                    getApplicationContext().getPackageName() + "/" + R.raw.alarm_cock_standart_music), audioAttributes);


            channel.enableVibration(true);

            //Vibration for 1 sec for vibration length repeat amount
            long[] vibration = new long[60];
            Arrays.fill(vibration, 1000);

            channel.setVibrationPattern(vibration);

            NotificationManager manager = NotificationManagingHelper.getNotificationManager();
            manager.createNotificationChannel(channel);
        }
    }
}
