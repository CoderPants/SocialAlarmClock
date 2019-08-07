package com.donteco.alarmClock.help;

import com.donteco.alarmClock.alarm.DayOfWeek;

public class ConstantsForApp {

    public static final int MUSIC_PERMITION_REQUEST = 1;
    public static final int ONE_SECOND_PAUSE = 1000;
    public static final String LOG_TAG = "alarmClock.LogTag";
    public static final int PAGE_COUNT = 2;
    public static final int IMAGE_SIZE_IN_TABS = 60;
    public static final int ALARM_INFO_REQUEST = 10;
    public static final int DELETE_ALARM_REQUEST = 20;
    public static final int MUSIC_NAME_LENGTH = 10;

    public static final DayOfWeek[] DAYS_LIST = new DayOfWeek[] {
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY };

    public static final String SHARED_PREFERENCES_FILE_NAME = "alarm_clocks_storage";

    public static final String DAY_DIALOG_TAG = "day_dialog_tag";
    public static final String DURATION_DIALOG_TAG = "duration_dialog_tag";
}
