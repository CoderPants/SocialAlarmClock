package com.donteco.alarmClock.help;

import android.content.Context;
import android.content.SharedPreferences;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vk.api.sdk.auth.VKAccessToken;

import org.json.JSONException;

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

    //In another thread
    public static void setAlarmClocks(List<AlarmClock> newAlarmClocks) {
        alarmClocks = newAlarmClocks;
        setAlarmClocksToStorage();
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
        setAlarmClocksToStorage();
    }

    /*public static void removeAlarmClock(AlarmClock alarmClock) {
        alarmClocks.remove(alarmClock);
        setAlarmClocksToStorage();
    }*/

   /* public static AlarmClock getAlarmClock(int position) {
        return alarmClocks.get(position);
    }*/

    public static void setAlarmClock(int position, AlarmClock alarmClock) {
        alarmClocks.set(position, alarmClock);
        setAlarmClocksToStorage();
    }

    public static void getAlarmClocksFromStorage() throws JSONException {
        Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_ALARMS, "");
        Type type;

        if (json.isEmpty())
            throw new JSONException("No data in the storage");

        else
            type = new TypeToken<List<AlarmClock>>() {}.getType();

        if (alarmClocks == null)
            alarmClocks = gson.fromJson(json, type);
    }

    public static void setAlarmClocksToStorage() {
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
        setSocialNetworkUsersToStorage();
    }

    private static void setSocialNetworkUsersToStorage() {
        Gson gson = new Gson();
        String jsonString = gson.toJson(socialNetworkUsers);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ConstantsForApp.KEY_FOR_STORED_SOCIAL_NETWORK_USERS, jsonString);
        editor.apply();
    }

    public static List<SocialNetworkUser> getSocialNetworkUsers() {
        if(socialNetworkUsers == null)
            socialNetworkUsers = new ArrayList<>();

        return socialNetworkUsers;
    }

    public static void getSocialNetworkUsersFormStorage() throws JSONException
    {
        Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_SOCIAL_NETWORK_USERS, "");
        Type type;

        if (json.isEmpty())
        {
            throw new JSONException("No data in the storage");
        }
        else
            type = new TypeToken<List<SocialNetworkUser>>(){}.getType();

        if(socialNetworkUsers == null)
            socialNetworkUsers = gson.fromJson(json, type);
    }

    //----------------------------------------------------------------------------------------------
    //VkAccess token

    public static void setVkAccessToken(VKAccessToken token) {
        System.out.println("Vk access token " + token);
        vkAccessToken = token;
        setVkAccessTokenToStorage();
    }

    public static VKAccessToken getVkAccessToken() {
        return vkAccessToken;
    }

    public static void getVkAccessTokenFromStorage(){

        vkAccessToken = VKAccessToken.Companion.restore(storage);
        /*Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_VK_ACCESS_TOKEN, "");
        Type type;

        if (json.isEmpty())
            throw new JSONException("No data in the storage");
        else
            type = new TypeToken<VKAccessToken>(){}.getType();

        System.out.println("Json string " + json);
        if(vkAccessToken == null)
            vkAccessToken = gson.fromJson(json, type);*/

        System.out.println("VkAccess token " + vkAccessToken);
    }

    private static void setVkAccessTokenToStorage(){

        vkAccessToken.save(storage);
        /*Gson gson = new Gson();
        String jsonString = gson.toJson(vkAccessToken);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ConstantsForApp.KEY_FOR_STORED_VK_ACCESS_TOKEN, jsonString);
        editor.apply();*/
    }

    //----------------------------------------------------------------------------------------------
    //Facebook access token
    //Don't need it
    //For now

    /*public static void setFbAccessToken(AccessToken token) {
        fbAccessToken = token;
        setFbAccessTokenToStorage();
    }

    public static AccessToken getFbAccessToken() {
        return fbAccessToken;
    }

    public static void getFbAccessTokenFromStorage() throws JSONException{
        Gson gson = new Gson();
        String json = storage.getString(ConstantsForApp.KEY_FOR_STORED_FB_ACCESS_TOKEN, "");
        Type type;

        if (json.isEmpty())
            throw new JSONException("No data in the storage");
        else
            type = new TypeToken<AccessToken>(){}.getType();

        if(fbAccessToken == null)
            fbAccessToken = gson.fromJson(json, type);

        System.out.println("Fb access token " + fbAccessToken);
    }

    public static void setFbAccessTokenToStorage(){
        Gson gson = new Gson();
        String jsonString = gson.toJson(fbAccessToken);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ConstantsForApp.KEY_FOR_STORED_VK_ACCESS_TOKEN, jsonString);
        editor.apply();
    }*/

}
