package com.donteco.alarmClock.help;

import android.content.Context;
import android.content.SharedPreferences;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.auth.VKAccessToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ApplicationStorage {
    private static ApplicationStorage applicationStorage;

    private static List<AlarmClock> alarmClocks;
    private static List<SocialNetworkUser> socialNetworkUsers;
    private static VKAccessToken vkAccessToken;
    //private static AccessToken fbAccessToken;

    private static SharedPreferences storage;

    private ApplicationStorage(Context context) {
        storage = context.getSharedPreferences(ConstantsForApp.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static void getInstance(Context context) {
        if (applicationStorage == null)
            applicationStorage = new ApplicationStorage(context);
    }

    public static void pullFromStorage(){
        pullAlarmClocksFromStorage();
        pullSocialNetworkUsersFormStorage();
        pullVkAccessTokenFromStorage();

    }

    /*public static void pushToStorage(){
        System.out.println("In push all to storage ");
        pushAlarmClocksToStorage();
        pushSocialNetworkUsersToStorage();
        pushVkAccessTokenToStorage();
    }*/

    //In another thread
    public static void setAlarmClocks(List<AlarmClock> newAlarmClocks) {
        alarmClocks = newAlarmClocks;
        pushAlarmClocksToStorage();
    }

    public static List<AlarmClock> getAlarmClocks()
    {
        if(alarmClocks == null)
            alarmClocks = new ArrayList<>();

        return alarmClocks;
    }

    public static void addAlarmClock(AlarmClock alarmClock) {
        if (alarmClocks == null)
            alarmClocks = new ArrayList<>();

        alarmClocks.add(alarmClock);
        pushAlarmClocksToStorage();
    }

    public static void setAlarmClock(int position, AlarmClock alarmClock) {
        alarmClocks.set(position, alarmClock);
        pushAlarmClocksToStorage();
    }

    public static void pullAlarmClocksFromStorage()
    {
        Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_ALARMS, "");
        Type type;

        if (json.isEmpty())
            return;

        type = new TypeToken<List<AlarmClock>>() {}.getType();

        if (alarmClocks == null)
            alarmClocks = gson.fromJson(json, type);
    }

    public static void pushAlarmClocksToStorage() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(alarmClocks);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ConstantsForApp.KEY_FOR_STORED_ALARMS, jsonString);
        editor.apply();
    }

    //---------------------------------------------------------------------------------------------

    /*public static void addUser(SocialNetworkUser newUser) {
        socialNetworkUsers.add(newUser);
    }*/

    public static void setUser(int position, SocialNetworkUser newUser) {
        socialNetworkUsers.set(position, newUser);
        pushSocialNetworkUsersToStorage();
    }

    public static List<SocialNetworkUser> getSocialNetworkUsers() {
        if(socialNetworkUsers == null)
            socialNetworkUsers = new ArrayList<>();

        return socialNetworkUsers;
    }

    public static void pullSocialNetworkUsersFormStorage()
    {
        Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_SOCIAL_NETWORK_USERS, "");
        Type type;

        if (json.isEmpty())
            return;

        type = new TypeToken<List<SocialNetworkUser>>(){}.getType();

        if(socialNetworkUsers == null)
            socialNetworkUsers = gson.fromJson(json, type);
    }

    private static void pushSocialNetworkUsersToStorage() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(socialNetworkUsers);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ConstantsForApp.KEY_FOR_STORED_SOCIAL_NETWORK_USERS, jsonString);
        editor.apply();
    }

    //----------------------------------------------------------------------------------------------
    //VkAccess token

    public static void setVkAccessToken(VKAccessToken token) {
        vkAccessToken = token;
        pushVkAccessTokenToStorage();
    }

    public static VKAccessToken getVkAccessToken() {
        return vkAccessToken;
    }

    public static void pullVkAccessTokenFromStorage(){
        vkAccessToken = VKAccessToken.Companion.restore(storage);
    }

    private static void pushVkAccessTokenToStorage() {
        vkAccessToken.save(storage);
    }
}
