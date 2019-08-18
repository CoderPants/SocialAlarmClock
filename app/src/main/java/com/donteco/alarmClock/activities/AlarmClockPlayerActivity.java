package com.donteco.alarmClock.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.background.AlarmClockManager;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.help.KeysForIntents;

import java.util.Arrays;
import java.util.List;

import in.shadowfax.proswipebutton.ProSwipeButton;

public class AlarmClockPlayerActivity extends AppCompatActivity {

    Vibrator vibrator;
    int alarmClockPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_clock_player);

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        Intent intent = getIntent();
        String songLocation = intent.getStringExtra(KeysForIntents.ALARM_CLOCK_MUSIC);
        //Getting time in min
        int duration = intent.getIntExtra(KeysForIntents.ALARM_CLOCK_DURATION, 1) * 60000;
        boolean hasVibration = intent.getBooleanExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, true);
        alarmClockPosition = intent.getIntExtra(KeysForIntents.ALARM_CLOCK_INDEX, -1);

        ProSwipeButton swipeBtn = findViewById(R.id.alarm_clock_player_swipe_btn);

        AudioPlayer audioPlayer = new AudioPlayer(songLocation, duration);

        vibrator = null;

        if(hasVibration)
        {
            vibrator =  (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            setVibrator(duration);
        }

        swipeBtn.setOnSwipeListener(() -> {
            if(vibrator != null)
                vibrator.cancel();

            audioPlayer.stopMediaPlayer();

            finish();
        });

        audioPlayer.startPlayer();
    }


    //Continuing executing this alarm if it has more days
    @Override
    protected void onDestroy()
    {
        List<AlarmClock> alarmClocks = ApplicationStorage.getAlarmClocks();
        AlarmClock curAlarmClock = alarmClocks.get(alarmClockPosition);

        boolean noRepeat = true;
        boolean[] chosenDays = curAlarmClock.getChosenDays();

        for (boolean chosenDay : chosenDays)
        {
            if(chosenDay)
                noRepeat = false;
        }

        if(noRepeat)
            curAlarmClock.setAlive(false);
        else
        {
            Intent startAlarmClockIntent = AlarmClockManager.createIntent(getApplicationContext(),
                    curAlarmClock, alarmClockPosition);


            PendingIntent alarmExecuteIntent = PendingIntent.getActivity(getApplicationContext(),
                    curAlarmClock.getId(), startAlarmClockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmClockManager.setExact(AlarmClockManager.getNextAlarmExecuteTime(curAlarmClock, true),
                    alarmExecuteIntent);
        }



        super.onDestroy();
    }

    private void setVibrator(int duration)
    {
        //Getting amount of seconds
        long[] vibrationPattern = new long[duration/10000];
        Arrays.fill(vibrationPattern, 1000);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && vibrator != null)
            vibrator.vibrate(VibrationEffect.createWaveform(vibrationPattern, 0));
        else
            vibrator.vibrate(duration);
    }

    private class AudioPlayer implements Runnable
    {
        private MediaPlayer mediaPlayer;
        private Uri songUri;
        private long userChooseDurationMS;

        private long curMusicDuration;
        private boolean isPlaying;

        private AudioPlayer(String songLocation, long userChooseDurationMS)
        {
            if(songLocation == null)
                songUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                        getApplicationContext().getPackageName() + "/" + R.raw.alarm_cock_standart_music);
            else
                songUri = Uri.parse(songLocation);

            this.userChooseDurationMS = userChooseDurationMS;

            curMusicDuration = 0;
            mediaPlayer = MediaPlayer.create(getApplicationContext(), songUri);
            mediaPlayer.start();
            isPlaying = true;
        }

        @Override
        public void run()
        {
            float curVolume = 0f;

            try
            {
                while (isPlaying && curMusicDuration != userChooseDurationMS)
                {
                    if(curMusicDuration <= ConstantsForApp.MUSIC_FADEIN_DUTATION_MS)
                    {
                        mediaPlayer.setVolume(curVolume, curVolume);
                        curVolume += ConstantsForApp.MUSIC_FADEIN_VOLUME_INCREASE;
                    }

                    try {
                        Thread.sleep(ConstantsForApp.MUSIC_MUSIC_PAUSE);
                    } catch (InterruptedException e) {
                        Log.e(ConstantsForApp.LOG_TAG, "Some thread interrupt song playing " +
                                "thread in alarm clock player activity", e);
                    }
                }

                stopMediaPlayer();
            }
            catch (IllegalStateException e)
            {
                Log.e(ConstantsForApp.LOG_TAG, "Looks like, media player is null", e);
            }
        }

        private void startPlayer() {
            if(mediaPlayer != null)
                mediaPlayer.start();
        }

        private void stopMediaPlayer() {
            isPlaying = false;

            if(mediaPlayer != null)
            {
                mediaPlayer.stop();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}
