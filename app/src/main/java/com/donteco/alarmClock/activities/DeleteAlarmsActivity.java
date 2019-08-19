package com.donteco.alarmClock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.adapters.AlarmClockDeleteAdapter;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.background.AlarmClockManager;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.ApplicationStorage;

import java.util.List;

public class DeleteAlarmsActivity extends AppCompatActivity {

    private AlarmClockDeleteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_alarms);

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        acceptBtnLogic(findViewById(R.id.delete_alarm_accept_btn));
        cancelBtnLogic(findViewById(R.id.delete_alarm_cancel_btn));

        RecyclerView recyclerView = findViewById(R.id.rv_delete_alarm_clocks);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlarmClockDeleteAdapter();

        recyclerView.setAdapter(adapter);
    }

    private void acceptBtnLogic(Button acceptDeletingBtn)
    {
        acceptDeletingBtn.setOnClickListener(view -> {

            deleteAlarmClocks();
            ApplicationStorage.setAlarmClocks(adapter.getAlarmClocks());

            Intent acceptIntent = new Intent();
            setResult(RESULT_OK, acceptIntent);
            finish();
        });
    }

    private void deleteAlarmClocks()
    {
        List<AlarmClock> alarmClocks = ApplicationStorage.getAlarmClocks();
        List<AlarmClock> deletedAlarmClocks = adapter.getAlarmClocks();
        AlarmClock alarmClock;

        for (int i = 0; i < alarmClocks.size(); i++)
        {
            alarmClock = alarmClocks.get(i);
            if( !deletedAlarmClocks.contains(alarmClock) )
            {
                Intent startAlarmClockIntent = AlarmClockManager.createIntent(getApplicationContext(),
                        alarmClock, i);

                PendingIntent alarmExecuteIntent = PendingIntent.getActivity(getApplicationContext(),
                        alarmClock.getId(), startAlarmClockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                AlarmClockManager.setExact(AlarmClockManager.deleteAlarmClock(),
                        alarmExecuteIntent);
            }
        }
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
