package com.donteco.alarmClock.components;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.ChooseAlarmClockActivity;
import com.donteco.alarmClock.DeleteAlarmsActivity;
import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.help.AlarmClocksStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.google.gson.Gson;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static android.app.Activity.RESULT_OK;

public class PageFragment extends Fragment
{
    private static final String KEY = "key_for_bundle";

    private Activity activity;

    private int pageNumber;

    private Timer timer;
    private TextView currentTimeTV;

    private AlarmClockAdapter alarmClockAdapter;

    //Because it's highly recommended by doc
    //Constructor needs to be empty
    public static PageFragment getInstance(int pageNum)
    {
        PageFragment pageFragment = new PageFragment();
        Bundle argsForFragment = new Bundle();
        argsForFragment.putInt(KEY, pageNum);
        pageFragment.setArguments(argsForFragment);
        return pageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        if(getArguments() == null)
        {
            Log.e(ConstantsForApp.LOG_TAG, "Null pointer in getArguments function in PageFragment class onCreate method");
            pageNumber = 1;
        }
        else
            pageNumber = getArguments().getInt(KEY);

        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState)
    {
        int viewId = Integer.MAX_VALUE;

        switch (pageNumber)
        {
            case 0:
                viewId = R.layout.show_time_fragment;
                break;

            case 1:
                viewId = R.layout.alarm_clock_fragment;
                break;

            case 2:
                //for the third page
                break;
        }

        if(viewId == Integer.MAX_VALUE)
        {
            Log.wtf(ConstantsForApp.LOG_TAG,
                    "In PageFormat class in onCreateView method error - can't find view layout");
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        //Why null????
        //NEED TO BE FIXED
        //first one was (show_time...., null)
        return inflater.inflate(viewId, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        switch (pageNumber)
        {
            case 0:
                currentTimeTV = view.findViewById(R.id.show_time_tv_current_time);
                timeFragmentLogic();
                break;

            case 1:
                RecyclerView recyclerView = view.findViewById(R.id.rv_alarm_clocks);
                alarmFragmentLogic(recyclerView);

                ImageButton imageButton = view.findViewById(R.id.ib_add_alarm_clock_btn);
                addAlarmClockBtnLogic(imageButton);
                break;

        }
    }

    //For timer deleting
    @Override
    public void onPause() {

        if(pageNumber == 0 && timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer = null;
        }

        super.onPause();
    }

    //For timer resuming
    @Override
    public void onResume() {

        if (pageNumber == 0)
            timeFragmentLogic();

        super.onResume();
    }

    //Get info about threadsafe state
    //Not on destroy, cos we won't close all out app
    //Ony one fragment
    @Override
    public void onDestroyView()
    {
        if(pageNumber == 0 && timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer = null;
            Log.i(ConstantsForApp.LOG_TAG, "In onDestroy view. Timer instance has been destroyed " + timer);
            System.out.println("In onDestroy view. Timer instance has been destroyed " + timer);
        }

        super.onDestroyView();
    }

    private void timeFragmentLogic()
    {
        if(timer != null)
            timer.cancel();

        //If JVM stop it before quiting from app
        timer = new Timer(true);
        TimeHandler timeHandler = new TimeHandler();
        timer.schedule(timeHandler, 0, ConstantsForApp.ONE_SECOND_PAUSE);
    }


    //Get into it!!!!!!!!!!!!!!!!!!!!!
    private void alarmFragmentLogic(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        alarmClockAdapter = new AlarmClockAdapter(new AlarmClockAdapter.CallBack() {
            @Override
            public void onLongPress() {
                Intent intent = new Intent(getContext(), DeleteAlarmsActivity.class);
                startActivityForResult(intent, ConstantsForApp.DELETE_ALARM_REQUEST);
            }

            @Override
            public void onPress(int position) {
                Intent addAlarmIntent = new Intent(getActivity(), ChooseAlarmClockActivity.class);
                addAlarmIntent.putExtra("Alarm clock position", position);
                startActivityForResult(addAlarmIntent, ConstantsForApp.ALARM_INFO_REQUEST);
            }
        });

        recyclerView.setAdapter(alarmClockAdapter);
    }

    private void addAlarmClockBtnLogic(ImageButton imageButton)
    {
        imageButton.setOnClickListener(view -> {
            Intent addAlarmIntent = new Intent(getActivity(), ChooseAlarmClockActivity.class);
            startActivityForResult(addAlarmIntent, ConstantsForApp.ALARM_INFO_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {

        switch (requestCode)
        {
            case ConstantsForApp.ALARM_INFO_REQUEST:
                    if(resultCode == RESULT_OK && data != null)
                        setAlarmInfo(data);
                break;
            case ConstantsForApp.DELETE_ALARM_REQUEST:
                if(resultCode == RESULT_OK && data != null)
                {
                    try {
                        alarmClockAdapter.addItems(AlarmClocksStorage.getAlarmClocks());
                    }
                    catch (JSONException e){
                        Log.e(ConstantsForApp.LOG_TAG, "Exception in onActivityResult in PageFragment, caused by parsing json format");
                    }

                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setAlarmInfo(Intent data)
    {
        Calendar curTime = Calendar.getInstance();
        int defaultHours = curTime.get(Calendar.HOUR);
        int defaultMinutes = curTime.get(Calendar.MINUTE);
        //1 minute
        int defaultDuration = 1;

        boolean is24HourFormat;
        DayPart dayPart = null;

        if(DateFormat.is24HourFormat(getActivity()))
            is24HourFormat = true;
        else
        {
            is24HourFormat = false;
            dayPart = (DayPart) data.getSerializableExtra("DayPart");
        }

        int hours = data.getIntExtra("Hours", defaultHours);
        int minutes = data.getIntExtra("Minutes", defaultMinutes);
        boolean [] chosenDays = data.getBooleanArrayExtra("Chosen days");
        String songLocation = data.getStringExtra("Music");
        boolean vibration = data.getBooleanExtra("Vibration", true);
        String description = data.getStringExtra("Description");
        int duration = data.getIntExtra("Duration", defaultDuration);

        AlarmClock alarmClock = new AlarmClock(hours, minutes, chosenDays,
                songLocation, vibration, description, duration, is24HourFormat, dayPart);

        alarmClockAdapter.addItem(alarmClock);
    }

    //----------------------------------------------------------------------------------------------
    //Thread for time showing
    private class TimeHandler extends TimerTask
    {
        private String timeFormat;

        private TimeHandler() {
            timeFormat = "HH:mm:ss";
        }

        //Main thread for getting time
        @Override
        public void run()
        {
            activity.runOnUiThread(() -> updateTime());
        }

        //Need to find better way to handle it
        private void updateTime()
        {
            currentTimeTV.setText(android.text.format.DateFormat.format( timeFormat,
                    Calendar.getInstance().getTime() ));
        }
    }
}
