package com.donteco.alarmClock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.KeysForIntents;

public class AlarmClockPlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock_player);

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        Intent intent = getIntent();
        String songLocation = intent.getStringExtra(KeysForIntents.ALARM_CLOCK_MUSIC);
        int duration = intent.getIntExtra(KeysForIntents.ALARM_CLOCK_DURATION, 1);
        boolean hasVibration = intent.getBooleanExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, true);

        System.out.println("Data in alarm clock executor " + songLocation + " " + duration + " " + hasVibration);
    }
}
