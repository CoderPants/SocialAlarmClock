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

        try{
            ApplicationStorage.getVkAccessTokenFromStorage();
        }
        catch (JSONException e){
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty vk access token", e);
        }

        try{
            ApplicationStorage.getFbAccessTokenFromStorage();
        }
        catch (JSONException e){
            Log.i(ConstantsForApp.LOG_TAG,
                    "In application onCreate. Json exception by creating empty fb access token", e);
        }

        VK.initialize(getApplicationContext());
    }
}
