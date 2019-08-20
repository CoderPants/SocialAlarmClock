package com.donteco.alarmClock.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.donteco.alarmClock.activities.AlarmClockPlayerActivity;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.help.KeysForIntents;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AlarmClockManager
{
    private static AlarmManager alarmManager;

    private static final long secondInMs = 1000;
    private static final long minuteInMS = 60 * secondInMs;
    private static final long hourInMS  = 60 * minuteInMS;
    private static final long dayInMS = 24 * hourInMS;

    private AlarmClockManager(){
    }

    public static void setInstance(AlarmManager manager)
    {
        if(alarmManager == null)
            alarmManager = manager;
    }

    public static void setExact(long millis, PendingIntent pendingIntent) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        Log.i(ConstantsForApp.LOG_TAG, "Passed alarm manager setting");
    }

    public static Intent createIntent(Context context, AlarmClock alarmClock)
    {
        Intent startAlarmClockIntent = new Intent(context, AlarmClockPlayerActivity.class);
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_MUSIC, alarmClock.getAlarmClockMusicLocation());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_DURATION, alarmClock.getDuration());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, alarmClock.hasVibration());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_ID, alarmClock.getId());

        return startAlarmClockIntent;
    }

    //Kinda doom's day
    public static long deleteAlarmClock()
    {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR) + 1000);


        return calendar.getTimeInMillis();
    }

    public static long getNextAlarmExecuteTime(AlarmClock alarmClock, boolean hasFired) {
        return nextDayInMs(alarmClock);
    }

    private static long nextDayInMs(AlarmClock alarmClock)
    {
        Calendar calendar = Calendar.getInstance(Locale.getDefault());

        int userHours = alarmClock.getHours();
        int userMinutes = alarmClock.getMinutes();
        boolean[] chosenDays = alarmClock.getChosenDays();
        boolean[] schedule = alarmClock.getSchedule();

        //For 12 hour format
        if(!alarmClock.isIs24HourFormat() && alarmClock.getDayPart() == DayPart.PM)
            userHours += 12;

        long curTime = calendar.getTimeInMillis();
        long addedTime = 0L;
        int curHours = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinutes = calendar.get(Calendar.MINUTE);
        int curSeconds = calendar.get(Calendar.SECOND);
        int curWeekDay = calendar.get(Calendar.DAY_OF_WEEK);

        if(curWeekDay == 1)
            curWeekDay = 6;
        else
            curWeekDay -= 2;

        addedTime = addDaysInMS(curHours, curMinutes, userHours, userMinutes);

        if(hasChosenDays(chosenDays))
        {
            int i = curWeekDay;
            int amountOfDays = 0;

            if(curHours >= userHours && curMinutes >= userMinutes)
            {
                i++;
                amountOfDays++;
                if(i == schedule.length)
                    i = 0;
            }

            while (!schedule[i])
            {
                amountOfDays++;
                i++;
                if(i == schedule.length)
                    i = 0;
            }

            schedule[i] = false;

            if(curHours >= userHours && curMinutes >= userMinutes)
            {
                //Cos we already have one day already
                if(amountOfDays == 0)
                    addedTime += 6 * dayInMS;
                else
                    addedTime += (amountOfDays - 1) * dayInMS;

            }
            else
                addedTime += amountOfDays * dayInMS;


            //Reset schedule
            boolean runOutOfDays = true;

            for (boolean isNextDay : schedule)
            {
                if(isNextDay)
                    runOutOfDays = false;
            }

            if(runOutOfDays)
                alarmClock.setSchedule(chosenDays);
        }

        addedTime -= curSeconds * secondInMs;
        addedTime -= calendar.get(Calendar.MILLISECOND);
        curTime += addedTime;

        return curTime;
    }

    private static boolean hasChosenDays(boolean[] chosenDays)
    {
        for (boolean chosenDay : chosenDays)
            if (chosenDay)
                return true;

        return false;
    }

    private static long addDaysInMS(int curHours, int curMinutes, int userHours, int userMinutes)
    {
        long result = 0L;

        if(curHours >= userHours && curMinutes >= userMinutes)
        {
            if (curMinutes != 0)
            {
                result += (60 - curMinutes + userMinutes) * minuteInMS;
                curHours++;
            }

            result += (24 - curHours + userHours ) * hourInMS;
        }
        else
        {
            if (curMinutes != 0)
            {
                result += (60 - curMinutes + userMinutes) * minuteInMS;
                curHours++;
            }

            result += (userHours - curHours) * hourInMS;
        }

        return result;
    }

    /*private static long logicForNoRepeat(int curHours, int curMinutes, int userHours, int userMinutes)
    {
        long addedTime = 0L;
        if(curHours >= userHours && curMinutes >= userMinutes)
        {
            if (curMinutes != 0)
            {
                addedTime += (60 - curMinutes + userMinutes) * minuteInMS;
                curHours++;
            }

            addedTime += (24 - curHours + userHours ) * hourInMS;
        }
        else
        {
            if (curMinutes != 0)
            {
                addedTime += (60 - curMinutes + userMinutes) * minuteInMS;
                curHours++;
            }

            addedTime += (userHours - curHours) * hourInMS;
        }
        return addedTime;
    }*/
}
