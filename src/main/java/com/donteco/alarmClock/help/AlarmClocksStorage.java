package com.donteco.alarmClock.help;

import android.content.Context;
import android.content.SharedPreferences;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

public class AlarmClocksStorage
{
    private static AlarmClocksStorage alarmClocksStorage;
    private static List<AlarmClock> alarmClocks;
    private static SharedPreferences storage;

    private AlarmClocksStorage(Context context)
    {
        storage = context.getSharedPreferences(ConstantsForApp.SHARED_PREFERENCES_FILE_NAME, Context.MODE_PRIVATE);
    }

    public static AlarmClocksStorage getInstance(Context context)
    {
        if(alarmClocksStorage == null)
            alarmClocksStorage = new AlarmClocksStorage(context);

        return alarmClocksStorage;
    }

    //In another thread
    public static void setAlarmClocks(List<AlarmClock> newAlarmClocks)
    {
        alarmClocks = newAlarmClocks;

        Gson gson = new Gson();
        String jsonString = gson.toJson(newAlarmClocks);
        System.out.println("JsonString " + jsonString);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString("Key", jsonString);
        editor.apply();
    }

    public static List<AlarmClock> getAlarmClocks() throws JSONException
    {
        Gson gson = new Gson();
        String json = storage.getString("Key", "");
        Type type;

        if (json.isEmpty())
            throw new JSONException("No data in the storage");
        else
            type = new TypeToken<List<AlarmClock>>(){}.getType();

        List<AlarmClock> list = gson.fromJson(json, type);

        System.out.println("Alarm clock after json :" + list + " alarm clock before json " + alarmClocks);
        return list;
    }
}
