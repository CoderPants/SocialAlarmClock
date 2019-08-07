package com.donteco.alarmClock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.components.AlarmClockDeleteAdapter;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.AlarmClocksStorage;
import com.donteco.alarmClock.help.ConstantsForApp;

import org.json.JSONException;

import java.util.List;

public class DeleteAlarmsActivity extends AppCompatActivity {

    private List<AlarmClock> curAlarmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_alarms);

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        acceptBtnLogic(findViewById(R.id.delete_alarm_accept_btn));
        cancelBtnLogic(findViewById(R.id.delete_alarm_cancel_btn));

        try {
            curAlarmList = AlarmClocksStorage.getAlarmClocks();
        } catch (JSONException e) {
            Log.e(ConstantsForApp.LOG_TAG, "Exception in parsing json format");
        }

        RecyclerView recyclerView = findViewById(R.id.rv_delete_alarm_clocks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new AlarmClockDeleteAdapter(curAlarmList));

    }

    private void acceptBtnLogic(Button acceptDeletingBtn)
    {
        acceptDeletingBtn.setOnClickListener(view -> {
            Intent acceptIntent = new Intent();
            setResult(RESULT_OK, acceptIntent);
            finish();
        });
    }

    private void cancelBtnLogic(Button cancelDeletingBtn)
    {
        cancelDeletingBtn.setOnClickListener(view -> {
            Intent cancelIntent = new Intent();
            setResult(RESULT_CANCELED, cancelIntent);
            finish();
        });
    }
}
