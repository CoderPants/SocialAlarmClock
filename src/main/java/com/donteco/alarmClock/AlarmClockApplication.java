package com.donteco.alarmClock;

import android.app.Application;
import android.util.Log;

import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
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
            ApplicationStorage.getSocialNetworkUsersFormStorage();
        } catch (JSONException e) {
            Log.i(ConstantsForApp.LOG_TAG, "In application onCreate. Json exception", e);
        }

        VK.initialize(getApplicationContext());
    }
}
