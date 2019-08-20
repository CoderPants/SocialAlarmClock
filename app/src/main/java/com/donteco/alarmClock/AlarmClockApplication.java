package com.donteco.alarmClock;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.util.Log;

import com.donteco.alarmClock.background.AlarmClockManager;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.notification.NotificationBuilder;
import com.donteco.alarmClock.notification.NotificationManagingHelper;
import com.vk.api.sdk.VK;

import org.json.JSONException;

public class AlarmClockApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //Setting alarmClock storage
        ApplicationStorage.getInstance(getApplicationContext());
        /*try {
            ApplicationStorage.pullAlarmClocksFromStorage();
        } catch (JSONException e) {
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty alarm clocks", e);
        }

        //Setting social network storage
        try {
            ApplicationStorage.pullSocialNetworkUsersFormStorage();
        }
        catch (JSONException e) {
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty social network users", e);
        }

        //Vk have stock serialization method
        ApplicationStorage.pullVkAccessTokenFromStorage();*/

        //Getting all info from storage
        ApplicationStorage.pullFromStorage();

        VK.initialize(getApplicationContext());

        //For showing notification
        NotificationManagingHelper.createInstance(getSystemService(NotificationManager.class));
        NotificationBuilder.createNotificationChanel();

        //For alarm execute
        AlarmClockManager.setInstance((AlarmManager)getSystemService(ALARM_SERVICE));
    }
}
