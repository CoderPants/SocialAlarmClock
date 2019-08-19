package com.donteco.alarmClock.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.help.ConstantsForApp;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class TimeFragment extends Fragment {

    private Activity activity;
    private Timer timer;
    private TextView currentTimeTV;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        try {
            activity = getActivity();
            return inflater.inflate(R.layout.show_time_fragment, container, false);
        }
        catch (Exception e) {
            Log.wtf(ConstantsForApp.LOG_TAG, "Exception in TimeFragment in onCreateView method " + e);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        currentTimeTV = view.findViewById(R.id.show_time_tv_current_time);
        showCurrentTime();
    }

    //For timer deleting
    @Override
    public void onPause() {

        if(timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer.purge();
            timer = null;
        }
        Log.i(ConstantsForApp.LOG_TAG, "Timer stopped");

        super.onPause();
    }

    //For timer resuming
    @Override
    public void onResume() {
        showCurrentTime();

        Log.i(ConstantsForApp.LOG_TAG, "Timer resumed");
        super.onResume();
    }

    //Get info about threadsafe state
    //Not on destroy, cos we won't close all out app
    //Ony one fragment
    @Override
    public void onDestroyView()
    {
        if(timer != null)
        {
            currentTimeTV.setText("");
            timer.cancel();
            timer.purge();
            timer = null;
            Log.i(ConstantsForApp.LOG_TAG, "In onDestroy view. Timer instance has been destroyed ");
        }

        super.onDestroyView();
    }

    private void showCurrentTime()
    {
        if(timer != null)
            timer.cancel();

        //If JVM stop it before quiting from app
        timer = new Timer(true);
        TimeFragment.TimeHandler timeHandler = new TimeFragment.TimeHandler();
        timer.schedule(timeHandler, 0, ConstantsForApp.ONE_SECOND_PAUSE);
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
            activity.runOnUiThread(this::updateTime);
        }

        //Need to find better way to handle it
        private void updateTime()
        {
            currentTimeTV.setText(android.text.format.DateFormat.format( timeFormat,
                    Calendar.getInstance().getTime() ));
        }
    }
}
