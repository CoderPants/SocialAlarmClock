package com.donteco.alarmClock;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.components.DayChooseDialog;
import com.donteco.alarmClock.components.DurationChooseDialog;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.AlarmClocksStorage;
import com.donteco.alarmClock.help.ConstantsForApp;

import org.json.JSONException;

import java.util.Calendar;

public class ChooseAlarmClockActivity extends AppCompatActivity
        implements DayChooseDialog.DayChooseDialogListener, DurationChooseDialog.DurationChooseDialogListener {

    //Middle one
    private NumberPicker pickerForHours;
    private NumberPicker pickerForMinutes;

    //For 12 hours format
    private Spinner amPmSpinner;

    //All in linear layout
    private TextView daysChoose;
    private TextView musicButton;
    private Switch vibrationSwitch;
    private EditText alarmName;
    private TextView alarmDuration;

    ActivityHelper activityHelper;

    //Info about alarm clock
    private boolean[] chosenDays;
    private Uri songData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_alarm_clock);

        activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        chosenDays = new boolean[7];
        songData = null;

        Button cancelBtn = findViewById(R.id.choose_alarm_cancel_btn);
        Button acceptBtn = findViewById(R.id.choose_alarm_accept_btn);

        amPmSpinner = findViewById(R.id.spinner_choose_alarm_am_pm);

        pickerForHours = findViewById(R.id.np_choose_hours);
        pickerForMinutes = findViewById(R.id.np_choose_minutes);

        daysChoose = findViewById(R.id.tv_choose_alarm_day_of_week_dialog);
        musicButton = findViewById(R.id.btn_choose_alarm_music);
        vibrationSwitch = findViewById(R.id.set_vibration_switch);
        alarmName = findViewById(R.id.et_alarm_clock_name);
        alarmDuration = findViewById(R.id.tv_alarm_clock_duration);

        acceptBtnLogic(acceptBtn);
        cancelBtnLogic(cancelBtn);

        Intent alarmClockInfo = getIntent();
        int alarmClockPosition = alarmClockInfo.getIntExtra("Alarm clock position", Integer.MAX_VALUE);

        if(alarmClockPosition == Integer.MAX_VALUE)
            logicForAddingAlarmClock();
        else
            logicForChangingAlarmClock(alarmClockPosition);

        chooseDaysLogic();
        chooseSongLogic();
        chooseDurationLogic();

        Calendar curTime = Calendar.getInstance();
        int hours = curTime.get(Calendar.HOUR);
        int minutes = curTime.get(Calendar.MINUTE);

        if(curTime.get(Calendar.AM_PM) == Calendar.PM)
        {
            hours = hours + 12;
            amPmSpinner.setSelection(DayPart.PM.ordinal());
        }
        else
            amPmSpinner.setSelection(DayPart.AM.ordinal());

        int maxHours = 23;
        int minHours = 0;

        if(!DateFormat.is24HourFormat(this))
        {
            maxHours = 12;
            minHours = 1;
            LinearLayout amPmLayout = findViewById(R.id.ll_choose_alarm_am_pm);
            View bottomLine = findViewById(R.id.bottom_border_alarm_am_pm);
            amPmLayout.setVisibility(View.VISIBLE);
            bottomLine.setVisibility(View.VISIBLE);
        }

        setNumberPickers(maxHours, minHours, hours, minutes);
    }

    private void logicForChangingAlarmClock(int alarmPosition)
    {
        AlarmClock curAlarmClock;
        try
        {
            curAlarmClock = AlarmClocksStorage.getAlarmClocks().get(alarmPosition);
            pickerForHours.setValue(curAlarmClock.getHours());
            pickerForMinutes.setValue(curAlarmClock.getMinutes());

            if(!curAlarmClock.isIs24HourFormat())
            {
                if(curAlarmClock.getDayPart() == DayPart.AM)
                    amPmSpinner.setSelection(DayPart.AM.ordinal());
                else
                    amPmSpinner.setSelection(DayPart.PM.ordinal());
            }

            onDaysDialogPositiveClick(curAlarmClock.getChosenDays());
            setMusicTitle(curAlarmClock.getAlarmClockMusicLocation());


        }
        catch (JSONException e) {
            Log.e(ConstantsForApp.LOG_TAG, "Exception in logicForChangingAlarmClock in ChooseAlarmClockActivity, caused by parsing json format");
        }
    }

    private void logicForAddingAlarmClock()
    {

    }

    private void setTimeInfo()
    {

    }

    private void acceptBtnLogic(final Button acceptBtn)
    {
        acceptBtn.setOnClickListener(view -> {

            Intent acceptedIntent = new Intent();

            String textInAlarmDuration = alarmDuration.getText().toString();
            int duration = Integer.parseInt(textInAlarmDuration.substring(0, textInAlarmDuration.indexOf(" ")));

            //'Cos i can't sent uri by intent extra data
            String songLocation = (songData == null)? null: songData.toString();

            boolean is12Hour = !DateFormat.is24HourFormat(ChooseAlarmClockActivity.this);
            DayPart amPm;

            if(is12Hour)
            {
                amPm = amPmSpinner.getSelectedItemPosition() == DayPart.AM.ordinal()
                        ? DayPart.AM: DayPart.PM;
                acceptedIntent.putExtra("DayPart", amPm);

            }

            acceptedIntent.putExtra("Hours", pickerForHours.getValue());
            acceptedIntent.putExtra("Minutes", pickerForMinutes.getValue());
            acceptedIntent.putExtra("Chosen days", chosenDays);
            acceptedIntent.putExtra("Music", songLocation);
            acceptedIntent.putExtra("Vibration", vibrationSwitch.isChecked());
            acceptedIntent.putExtra("Description", alarmName.getText().toString());
            acceptedIntent.putExtra("Duration", duration);
            setResult(RESULT_OK, acceptedIntent);
            finish();
        });
    }

    private void cancelBtnLogic(Button cancel)
    {
        cancel.setOnClickListener(view -> {
            Intent cancelIntent = new Intent();
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
        });
    }

    private void setNumberPickers(int maxHours, int minHours, int hours, int minutes)
    {
        pickerForHours.setMaxValue(maxHours);
        pickerForHours.setMinValue(minHours);

        pickerForMinutes.setMinValue(0);
        pickerForMinutes.setMaxValue(59);

        pickerForMinutes.setWrapSelectorWheel(true);
        pickerForHours.setWrapSelectorWheel(true);

        pickerForHours.setValue(hours);
        pickerForMinutes.setValue(minutes);
    }

    private void chooseDaysLogic()
    {
        daysChoose.setOnClickListener(view -> {
            DayChooseDialog dayChooseDialog = new DayChooseDialog();
            dayChooseDialog.show(getSupportFragmentManager(), ConstantsForApp.DAY_DIALOG_TAG);
        });
    }

    private void chooseSongLogic()
    {
        musicButton.setOnClickListener(view -> {
            activityHelper.getMusicPermition();
            Intent musicIntent;

            if(ContextCompat.checkSelfPermission(ChooseAlarmClockActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                musicIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(musicIntent, ConstantsForApp.MUSIC_PERMITION_REQUEST);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == ConstantsForApp.MUSIC_PERMITION_REQUEST
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
                Intent musicIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(musicIntent, ConstantsForApp.MUSIC_PERMITION_REQUEST);
            }
            else
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        System.out.println("In onActivityResult");
        if(requestCode == ConstantsForApp.MUSIC_PERMITION_REQUEST
                && resultCode == RESULT_OK
                && data != null)
        {
            try
            {
                System.out.println("In trycatch block " + data);
                songData = data.getData();
                System.out.println("Song data: " + songData);
                loadAudioFile();
            }
            catch (Exception e){
                Toast.makeText(this, "Something wrong, try one more time!  ", Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void loadAudioFile()
    {
        String[] dataForCursorLoader = new String[]{MediaStore.Audio.Media.TITLE};

        CursorLoader loader = new CursorLoader(getApplicationContext(), songData, dataForCursorLoader, null, null, null);
        Cursor cursor = loader.loadInBackground();

        if(cursor != null)
        {
            cursor.moveToFirst();
            setMusicTitle(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            cursor.close();
        }
    }

    private void setMusicTitle(String songTitle)
    {
        if(songTitle.length() > ConstantsForApp.MUSIC_NAME_LENGTH)
        {
            songTitle = songTitle.substring(0 , ConstantsForApp.MUSIC_NAME_LENGTH);
            songTitle = songTitle + "...";
        }

        musicButton.setText(songTitle);
    }

    private void chooseDurationLogic()
    {
        alarmDuration.setOnClickListener(view -> {
            DurationChooseDialog durationChooseDialog = new DurationChooseDialog();
            durationChooseDialog.show(getSupportFragmentManager(), ConstantsForApp.DURATION_DIALOG_TAG);
        });
    }

    @Override
    public void onDaysDialogPositiveClick(boolean[] checkedDays)
    {
        StringBuilder stringBuilder = new StringBuilder();

        boolean isAllChecked = true;

        for (int i = 0; i < checkedDays.length; i++)
        {
            if(checkedDays[i])
            {
                stringBuilder.append(ConstantsForApp.DAYS_LIST[i].getReductiveDayName());
                stringBuilder.append(", ");
            }
            else
                isAllChecked = false;
        }

        String outputText;

        if(isAllChecked)
            outputText = "Every day";
        else
        //Getting rid of the last ", "
        outputText = stringBuilder.toString().substring(0, stringBuilder.length()-2);

        daysChoose.setText(outputText);

        //Cos we might use checkedDays later
        System.arraycopy(checkedDays, 0, chosenDays, 0, checkedDays.length);
    }

    @Override
    public void onDurationDialogPositiveClick(String[] duration)
    {
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : duration)
            if (s.length() > 0)
                stringBuilder.append(s);

        String output = stringBuilder.toString();
        alarmDuration.setText(output);
    }
}
