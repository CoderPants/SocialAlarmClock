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

import java.time.YearMonth;
import java.util.Calendar;

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

    public static void setExact(long millis, PendingIntent pendingIntent) {
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, millis, pendingIntent);
        Log.i(ConstantsForApp.LOG_TAG, "Passed alarm manager setting");
    }

    public static Intent createIntent(Context context, AlarmClock alarmClock, int alarmClockPosition)
    {
        Intent startAlarmClockIntent = new Intent(context, AlarmClockPlayerActivity.class);
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_MUSIC, alarmClock.getAlarmClockMusicLocation());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_DURATION, alarmClock.getDuration());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, alarmClock.hasVibration());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_INDEX, alarmClockPosition);

        System.out.println("Calendar info position " + alarmClockPosition);

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

    public static long getNextAlarmExecuteTime(AlarmClock alarmClock, boolean hasFired)
    {
        Calendar calendar = Calendar.getInstance();

        int hours = alarmClock.getHours();
        int minutes = alarmClock.getMinutes();
        boolean[] chosenDays = alarmClock.getChosenDays();
        boolean[] schedule = alarmClock.getSchedule();
        boolean is24Hour = alarmClock.isIs24HourFormat();
        DayPart dayPart = alarmClock.getDayPart();

        int curHours = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinutes = calendar.get(Calendar.MINUTE);

        if(!is24Hour && dayPart == DayPart.PM)
            hours += 12;

        int curWeekDay = calendar.get(Calendar.DAY_OF_WEEK);
        //accurate
        int curMonthDay = calendar.get(Calendar.DAY_OF_MONTH);
        //-1 need to increment
        int curMonth = calendar.get(Calendar.MONTH);
        //accurate
        int curYear = calendar.get(Calendar.YEAR);

        int[] calendarInfo = getNextTime(curHours, curMinutes, curWeekDay,
                curMonthDay, curMonth, curYear, hours, minutes, chosenDays, schedule,
                alarmClock, hasFired);

        System.out.println("Calendar info for next days: weekdDay " + calendarInfo[0] + " monthDay " + calendarInfo[1] + " month " + calendarInfo[2] + " year " + calendarInfo[3] + " hours " + hours + " minutes " + minutes + " hasFired " + hasFired);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.DAY_OF_WEEK, calendarInfo[0]);
        calendar.set(Calendar.DAY_OF_MONTH, calendarInfo[1]);
        calendar.set(Calendar.MONTH, calendarInfo[2]);
        calendar.set(Calendar.YEAR, calendarInfo[3]);


        return calendar.getTimeInMillis();
    }

    //Week day, month day, month, year
    //Only God knows what's going on here
    //Beware, from this line goes pure evil
    private static int[] getNextTime(int curHours, int curMinutes, int curWeekDay,  int curMonthDay, int curMonth, int curYear,
                                    int userHours, int userMinutes, boolean[] chosenDays, boolean[] schedule,
                                     AlarmClock alarmClock, boolean hasFired)
    {
        int[] result = new int[4];

        //Need normal number
        YearMonth yearMonthObject = YearMonth.of(curYear, curMonth);
        int daysInMonth = yearMonthObject.lengthOfMonth();

        boolean hasChosenDays = false;

        //Check if user choose smth
        for (boolean chosenDay : chosenDays)
            if (chosenDay) {
                hasChosenDays = true;
                break;
            }

        if (!hasChosenDays)
        {
            if(curHours >= userHours && curMinutes >= userMinutes)
                curMonthDay++;

            result[0] = curWeekDay;
            result[1] = curMonthDay;
            result[2] = curMonth;
            result[3] = curYear;

            return result;
        }

        //------------------------------------------------------------------------------------------
        //No else, cos i hate brackets
        //Cos calendar has another week numeration
        if(curWeekDay == 1)
            curWeekDay = 6;
        else
            curWeekDay -= 2;

        int nextWeekDay = curWeekDay;

        int i = nextWeekDay;

       if(curHours >= userHours && curMinutes >= userMinutes)
       {
           i++;
           if(i == schedule.length)
               i = 0;
       }

        while (!schedule[i])
        {
            ++i;
            if(i == schedule.length)
                i = 0;
        }

        nextWeekDay = i;
        //This day finished
        schedule[i] = false;

        //If we reach the same day
        if(nextWeekDay == curWeekDay &&
                (hasFired || (curHours >= userHours && curMinutes >= userMinutes) ))
            curMonthDay += schedule.length;

        if(nextWeekDay > curWeekDay)
            curMonthDay += nextWeekDay - curWeekDay;

         if(nextWeekDay < curWeekDay)
             curMonthDay += schedule.length - curWeekDay + nextWeekDay;

        if(nextWeekDay == 6)
            nextWeekDay = 1;
        else
            nextWeekDay += 2;

        if(curMonthDay > daysInMonth)
        {
            curMonthDay -= daysInMonth;
            curMonth++;

            if(curMonth > 11)
            {
                curMonth = 0;
                curYear++;
            }
        }

        result[0] = nextWeekDay;
        result[1] = curMonthDay;
        result[2] = curMonth;
        result[3] = curYear;

        //Reset schedule
        boolean runOutOfDays = true;

        for (boolean isNextDay : schedule) {
            if(isNextDay)
                runOutOfDays = false;
        }

        if(runOutOfDays)
        {
            System.out.println("Calendar info runofdays " + runOutOfDays);
            alarmClock.setSchedule(chosenDays);
        }

        return result;
    }
}
