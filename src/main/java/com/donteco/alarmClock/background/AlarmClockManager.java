package com.donteco.alarmClock.background;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.util.Log;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.help.ConstantsForApp;

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

    public static long getNextAlarmExecuteTime(AlarmClock alarmClock)
    {
        Calendar calendar = Calendar.getInstance();

        int hours = alarmClock.getHours();
        int minutes = alarmClock.getMinutes();
        boolean[] chosenDays = alarmClock.getChosenDays();
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
                curMonthDay, curMonth+1, curYear, hours, minutes, chosenDays);


        //System.out.println("Next day in alarm clock " + nextAlarmDay);
        //System.out.println("Calendar info " + hours + " " + minutes + " " + curMonthDay);

        System.out.println("Calendar info for next days: weekdDay " + calendarInfo[0] + " monthDay " + calendarInfo[1] + " month " + calendarInfo[2] + " year " + calendarInfo[3]);

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        //calendar.set(Calendar.DAY_OF_WEEK, nextAlarmWeekDay);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1);


        return calendar.getTimeInMillis();
    }

    //Week day, month day, month, year
    private static int[] getNextTime(int curHours, int curMinutes, int curWeekDay,  int curMonthDay, int curMonth, int curYear,
                                    int userHours, int userMinutes, boolean[] chosenDays)
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

        while (!chosenDays[i])
        {
            ++i;
            if(i == chosenDays.length)
                i = 0;
        }

        nextWeekDay = i;

        //No equation 'cos we've already checked it in the first return statement
        System.out.println("Calendar info days in month weekday " + curMonthDay);
        if(nextWeekDay > curWeekDay)
            curMonthDay += nextWeekDay - curWeekDay;
        else
            curMonthDay += chosenDays.length - curWeekDay + nextWeekDay;

        System.out.println("Calendar info days in month weekday " + nextWeekDay + " " + curWeekDay);

        if(nextWeekDay == 6)
            nextWeekDay = 1;
        else
            nextWeekDay += 2;

        if(curMonthDay > daysInMonth)
        {
            System.out.println("Calendar info days in month " + curMonthDay + " " + daysInMonth );
            curMonthDay -= daysInMonth;
            curMonth++;

            if(curMonth > 12)
            {
                curMonth = 1;
                curYear++;
            }
        }

        result[0] = nextWeekDay;
        result[1] = curMonthDay;
        result[2] = curMonth;
        result[3] = curYear;

        return result;
    }

   /* private static int getWeekDay(int curDay, int curHours, int curMinutes,
                                  int userHours, int userMinutes, boolean[] chosenDays)
    {
        int nextAlarmDay = curDay;

        if(nextAlarmDay == 1)
            nextAlarmDay = 6;
        else
            nextAlarmDay -= 2;

        boolean hasChosenDays = false;

        //Check if user choose smth
        for (boolean chosenDay : chosenDays)
            if (chosenDay) {
                hasChosenDays = true;
                break;
            }

        if (hasChosenDays)
        {
            boolean match = false;
            int i = nextAlarmDay;

            while (!match)
            {
                match = chosenDays[i];
                i++;

                if(i == chosenDays.length)
                    i = 0;
            }

            nextAlarmDay = i;

            if(nextAlarmDay == 6)
                nextAlarmDay = 1;
            else
                nextAlarmDay += 2;
        }
        else
        {
            if(curHours >= userHours && curMinutes >= userMinutes)
            {
                if(nextAlarmDay == 6)
                    nextAlarmDay = 1;
                else
                    nextAlarmDay += 2;
            }
            else
                nextAlarmDay = curDay;
        }

        return nextAlarmDay;
    }*/
}
