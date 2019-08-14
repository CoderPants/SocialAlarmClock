package com.donteco.alarmClock.alarmclock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.donteco.alarmClock.help.KeysForIntents;

public class AlarmClockReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent received) {
        Toast.makeText(context, "Got inte alarmClockReciever ", Toast.LENGTH_SHORT).show();

        System.out.println("Got into onRecieve method!");

        String songLocation = received.getStringExtra(KeysForIntents.ALARM_CLOCK_MUSIC);
        int duration = received.getIntExtra(KeysForIntents.ALARM_CLOCK_DURATION, 1);
        boolean hasVibration = received.getBooleanExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, true);

        try {
            Intent executeAlarmClock = new Intent();
            executeAlarmClock.setClassName("com.donteco.alarmclock",
                    "com.donteco.alarmClock.activities.AlarmClockPlayerActivity");

            executeAlarmClock.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            executeAlarmClock.putExtra(KeysForIntents.ALARM_CLOCK_MUSIC, songLocation);
            executeAlarmClock.putExtra(KeysForIntents.ALARM_CLOCK_DURATION, duration);
            executeAlarmClock.putExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, hasVibration);

            context.startActivity(executeAlarmClock);
        }
        catch (Exception e) {
            System.out.println("Catch exception in receiver " + e);
        }
    }
}
