package com.donteco.alarmClock.alarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;

public class AlarmClockManager
{
    private static AlarmManager alarmManager;

    private AlarmClockManager(){
    }

    public static void setInstance(AlarmManager manager)
    {
        if(alarmManager == null)
            alarmManager = manager;
    }

    public static void setExtact(long millis, PendingIntent pendingIntent) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        System.out.println("Passed alarm manager setting");
    }
}
