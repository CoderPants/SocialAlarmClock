package com.donteco.alarmClock.help;

import com.donteco.alarmClock.alarm.DayOfWeek;

public class ConstantsForApp {

    public static final int MUSIC_PERMITION_REQUEST = 1;
    public static final int ONE_SECOND_PAUSE = 1000;
    public static final String LOG_TAG = "MY_LOG_TAG_FOR_APP";
    public static final int PAGE_COUNT = 3;
    public static final int IMAGE_SIZE_IN_TABS = 60;
    public static final int ALARM_GET_INFO_REQUEST = 10;
    public static final int DELETE_ALARM_REQUEST = 20;
    public static final int ALARM_CHANGE_INFO_REQUEST = 30;
    public static final int SHARE_DIALOG_SHOW_REQUEST = 40;
    //Need to find solution
    //Find code by printing it in the console
    public static final int VK_LOGGING_REQUEST_CODE = 282;
    public static final int FACEBOOK_LOGIN_CODE = 64206;
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
    public static final String SOCIAL_NETWORK_TAG = "social_network_tag";


    public static final String VK_TOKEN_KEY = "VK_ACCESS_TOKEN";
    public static final String VK_ACCESS_KEY = "ed945bf0ed945bf0ed945bf01eedf87777eed94ed945bf0b0d979fe232d25b06f472601";

    public static final String ALARM_CLOCK_NOTIFICATION_CHANNEL = "channel_when_alarm_clock_starts";

    public static final String KEY_FOR_STORED_ALARMS = "GET_ALARMS_IN_STORAGE";
    public static final String KEY_FOR_STORED_SOCIAL_NETWORK_USERS = "GET_USERS_IN_STORAGE";
    public static final String KEY_FOR_STORED_VK_ACCESS_TOKEN = "GET_VK_ACCESS_TOKEN";
    public static final String KEY_FOR_STORED_FB_ACCESS_TOKEN = "GET_FB_ACCESS_TOKEN";

    public static final String FACEBOOK_NAME = "FaceBook";
    public static final String VK_NAME = "VK";

    public static final int VK_POSITION = 0;
    public static final int FACE_BOOK_POSITION = 1;
    public static final int AMOUNT_OF_SOCIAL_NETWORKS = 2;

    public static final String VK_START_OF_POST_MESSAGE = "TESTING APP!!!!!!\n" +
            "Hello, guys! I set the alarm clock to : ";
    public static final String VK_END_OF_POST_MESSAGE = " see you later!";


    public static final String CHANNEL_ID = "NOTIFICATION_CHANNEL_ONLY_ONE";

}
