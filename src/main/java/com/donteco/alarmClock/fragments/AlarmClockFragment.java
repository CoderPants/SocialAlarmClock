package com.donteco.alarmClock.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.activities.ChooseAlarmClockActivity;
import com.donteco.alarmClock.activities.DeleteAlarmsActivity;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.adapters.AlarmClockAdapter;
import com.donteco.alarmClock.dialogs.SocialNetworkChooseDialog;
import com.donteco.alarmClock.help.ConstantsForApp;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class AlarmClockFragment extends Fragment {


    private Activity activity;
    private AlarmClockAdapter alarmClockAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            activity = getActivity();
            return inflater.inflate(R.layout.alarm_clock_fragment, container, false);
        }
        catch (Exception e) {
            Log.wtf(ConstantsForApp.LOG_TAG, "Exception in AlarmClockFragment in onCreateView method " + e);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        alarmFragmentLogic(view.findViewById(R.id.rv_alarm_clocks));

        addAlarmClockBtnLogic(view.findViewById(R.id.ib_add_alarm_clock_btn));
    }

    private void alarmFragmentLogic(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        alarmClockAdapter = new AlarmClockAdapter(new AlarmClockAdapter.AlarmClockCallBack() {
            @Override
            public void onLongPress()
            {
                Intent intent = new Intent(getContext(), DeleteAlarmsActivity.class);
                startActivityForResult(intent, ConstantsForApp.DELETE_ALARM_REQUEST);
            }

            @Override
            public void onPress(int position)
            {
                Intent addAlarmIntent = new Intent(activity, ChooseAlarmClockActivity.class);
                addAlarmIntent.putExtra("Alarm clock position", position);
                startActivityForResult(addAlarmIntent, ConstantsForApp.ALARM_CHANGE_INFO_REQUEST);
            }

            @Override
            public void onSharePress() {
                SocialNetworkChooseDialog dayChooseDialog = new SocialNetworkChooseDialog();
                dayChooseDialog.show(getFragmentManager(), ConstantsForApp.SOCIAL_NETWORK_TAG);
            }
        });
        recyclerView.setAdapter(alarmClockAdapter);
    }

    private void addAlarmClockBtnLogic(ImageButton imageButton) {
        imageButton.setOnClickListener(view -> {
            Intent addAlarmIntent = new Intent(activity, ChooseAlarmClockActivity.class);
            startActivityForResult(addAlarmIntent, ConstantsForApp.ALARM_GET_INFO_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && data != null)
        {
            switch (requestCode) {
                case ConstantsForApp.ALARM_GET_INFO_REQUEST:
                    alarmClockAdapter.addItem(convertToAlarmClock(data));
                    //System.out.println("In alarm add result " + alarmClockAdapter.getItemCount() + " "+ data);
                    break;

                case ConstantsForApp.DELETE_ALARM_REQUEST:
                    alarmClockAdapter.addItems();
                    break;

                case ConstantsForApp.ALARM_CHANGE_INFO_REQUEST:
                    alarmClockAdapter.setItem(convertToAlarmClock(data));
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private AlarmClock convertToAlarmClock(Intent data)
    {
        Calendar curTime = Calendar.getInstance();
        int defaultHours = curTime.get(Calendar.HOUR);
        int defaultMinutes = curTime.get(Calendar.MINUTE);
        //1 minute
        int defaultDuration = 1;

        boolean is24HourFormat;
        DayPart dayPart = null;

        if(DateFormat.is24HourFormat(activity))
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

        AlarmClock alarmClock =  new AlarmClock(hours, minutes, chosenDays,
                songLocation, vibration, description, duration, is24HourFormat, dayPart);

        /*alarmClock.setChannelID(hours + " " + minutes);
        alarmClock.createNotificationChannel();

        NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(alarmClock.getAlarmNotificationChannel());*/

        return alarmClock;
    }

}
