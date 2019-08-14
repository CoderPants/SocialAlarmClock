package com.donteco.alarmClock.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.adapters.AlarmClockDeleteAdapter;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.ApplicationStorage;

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

        adapter = new AlarmClockDeleteAdapter(alarmClock ->
        {
            /*NotificationManager notificationManager = getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.deleteNotificationChannel(alarmClock.getChannelID());*/
        });

        recyclerView.setAdapter(adapter);
    }

    private void acceptBtnLogic(Button acceptDeletingBtn)
    {
        acceptDeletingBtn.setOnClickListener(view -> {

            ApplicationStorage.setAlarmClocks(adapter.getAlarmClocks());

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
