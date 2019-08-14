package com.donteco.alarmClock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.util.Log;

import com.donteco.alarmClock.alarmclock.AlarmClockManager;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.notification.NotificationManagingHelper;
import com.vk.api.sdk.VK;

import org.json.JSONException;

public class AlarmClockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Setting alarmClock storage
        ApplicationStorage.getInstance(getApplicationContext());
        try {
            ApplicationStorage.getAlarmClocksFromStorage();
        } catch (JSONException e) {
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty alarm clocks", e);
        }

        try {
            ApplicationStorage.getSocialNetworkUsersFormStorage();
        }
        catch (JSONException e) {
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty social network users", e);
        }

        //Vk have stock serialization method
        ApplicationStorage.getVkAccessTokenFromStorage();

        VK.initialize(getApplicationContext());

        //For showing notification
        NotificationManagingHelper.createInstance(getSystemService(NotificationManager.class));

        //For alarm execute
        AlarmClockManager.setInstance((AlarmManager)getSystemService(ALARM_SERVICE));
    }
}
