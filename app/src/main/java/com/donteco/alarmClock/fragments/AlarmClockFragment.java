package com.donteco.alarmClock.fragments;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.activities.ChooseAlarmClockActivity;
import com.donteco.alarmClock.activities.DeleteAlarmsActivity;
import com.donteco.alarmClock.adapters.AlarmClockAdapter;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.alarm.DayPart;
import com.donteco.alarmClock.background.AlarmClockManager;
import com.donteco.alarmClock.dialogs.SocialNetworkChooseDialog;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.help.FlagsForIntents;
import com.donteco.alarmClock.help.KeysForIntents;
import com.donteco.alarmClock.socialNetwork.VkWallRequest;
import com.facebook.AccessToken;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.exceptions.VKApiExecutionException;

import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

import static android.app.Activity.RESULT_OK;

public class AlarmClockFragment extends Fragment {


    private Activity activity;
    private AlarmClockAdapter alarmClockAdapter;


    //HEY YOU!
    private String timeForSharing;

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

                startActivityForResult(intent, FlagsForIntents.DELETE_ALARM_REQUEST);

            }

            @Override
            public void onPress(int position)
            {
                Intent addAlarmIntent = new Intent(activity, ChooseAlarmClockActivity.class);
                System.out.println("Cur alarm position in press " + position);
                addAlarmIntent.putExtra(KeysForIntents.ALARM_CLOCK_POSITION, position);
                startActivityForResult(addAlarmIntent, FlagsForIntents.ALARM_CHANGE_INFO_REQUEST);
            }

            @Override
            public void onSharePress(String curTime)
            {
                timeForSharing = curTime;

                SocialNetworkChooseDialog dayChooseDialog = new SocialNetworkChooseDialog();
                dayChooseDialog.setTargetFragment(AlarmClockFragment.this,
                        FlagsForIntents.SHARE_DIALOG_SHOW_REQUEST);

                dayChooseDialog.show(getFragmentManager(), ConstantsForApp.SOCIAL_NETWORK_TAG);
            }

            @Override
            public void switchLogic(int position, Switch alarmClockSwitch)
            {
                AlarmClock alarmClock = ApplicationStorage.getAlarmClocks().get(position);

                if(alarmClockSwitch.isChecked())
                    startAlarmClock(alarmClock);
                else
                {
                    Intent startAlarmClockIntent = AlarmClockManager.createIntent(activity.getApplicationContext(),
                            alarmClock, position);

                    PendingIntent alarmExecuteIntent = PendingIntent.getActivity(activity.getApplicationContext(),
                            alarmClock.getId(), startAlarmClockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

                    AlarmClockManager.setExact(AlarmClockManager.deleteAlarmClock(),
                            alarmExecuteIntent);

                    //Refresh schedule
                    alarmClock.setSchedule(alarmClock.getChosenDays());
                }
            }
        });
        recyclerView.setAdapter(alarmClockAdapter);
    }

    private void addAlarmClockBtnLogic(ImageButton imageButton) {
        imageButton.setOnClickListener(view -> {
            Intent addAlarmIntent = new Intent(activity, ChooseAlarmClockActivity.class);
            startActivityForResult(addAlarmIntent, FlagsForIntents.ALARM_GET_INFO_REQUEST);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && data != null)
        {
            switch (requestCode) {
                case FlagsForIntents.ALARM_GET_INFO_REQUEST:
                    AlarmClock newAlarm = convertToAlarmClock(data);
                    alarmClockAdapter.addItem(newAlarm);
                    startAlarmClock(newAlarm);
                    break;

                case FlagsForIntents.DELETE_ALARM_REQUEST:
                    alarmClockAdapter.refreshAlarmClocks();
                    break;

                case FlagsForIntents.ALARM_CHANGE_INFO_REQUEST:
                    AlarmClock existingAlarm = convertToAlarmClock(data);
                    alarmClockAdapter.setItem(existingAlarm);
                    startAlarmClock(existingAlarm);
                    break;

                case FlagsForIntents.SHARE_DIALOG_SHOW_REQUEST:
                    share(data.getBooleanArrayExtra(KeysForIntents.CHOSEN_SOCIAL_NETWOKS));
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //Need to fix it
    private void share(boolean[] checkedSocialNetwork)
    {
        VKAccessToken vkAccessToken = ApplicationStorage.getVkAccessToken();
        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();

        if(checkedSocialNetwork.length == ConstantsForApp.AMOUNT_OF_SOCIAL_NETWORKS)
        {
            if(checkedSocialNetwork[0])
                VKShare();


            if(checkedSocialNetwork[1])
                FBShare();
        }
        else
        {
            if(checkedSocialNetwork[0])
            {
                if(vkAccessToken != null && vkAccessToken.isValid())
                    VKShare();

                if(fbAccessToken != null && !fbAccessToken.isExpired())
                    FBShare();
            }
        }
    }

    private void VKShare()
    {
        VKAccessToken curToken = ApplicationStorage.getVkAccessToken();

        String postMessage = ConstantsForApp.VK_START_OF_POST_MESSAGE
                + timeForSharing + ConstantsForApp.VK_END_OF_POST_MESSAGE;

        VkWallRequest wallRequest = new VkWallRequest("wall.post",
                (curToken != null && curToken.isValid())? curToken.getUserId(): 0,
                postMessage);

        VK.execute(wallRequest, new VKApiCallback<Integer>() {
            @Override
            public void success(Integer integer) {
                Toast.makeText(getContext(), "VK sharing completed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void fail(@NotNull VKApiExecutionException e) {
                Toast.makeText(getContext(), "VK sharing failed! Check internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void FBShare()
    {
        //AccessToken faceBookToken = ApplicationStorage.getFbAccessToken();
        //AccessToken faceBookToken = AccessToken.getCurrentAccessToken();

        Bitmap image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.alarm_clock_for_social_sharing);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareFaceBook = new ShareDialog(this);


        if(shareFaceBook.canShow(content, ShareDialog.Mode.AUTOMATIC))
            shareFaceBook.show(content);
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
            dayPart = (DayPart) data.getSerializableExtra(KeysForIntents.DAY_PART);
        }

        int hours = data.getIntExtra(KeysForIntents.HOURS, defaultHours);
        int minutes = data.getIntExtra(KeysForIntents.MINUTES, defaultMinutes);
        boolean [] chosenDays = data.getBooleanArrayExtra(KeysForIntents.CHOSEN_DAYS);
        String songLocation = data.getStringExtra(KeysForIntents.MUSIC);
        boolean vibration = data.getBooleanExtra(KeysForIntents.VIBRATION, true);
        String description = data.getStringExtra(KeysForIntents.DESCRIPTION);
        int duration = data.getIntExtra(KeysForIntents.DURATION, defaultDuration);

        int index = data.getIntExtra(KeysForIntents.ALARM_CLOCK_INDEX, Integer.MAX_VALUE);

        //Delete existing
        if(index != Integer.MAX_VALUE)
        {
            AlarmClock alarmClock = ApplicationStorage.getAlarmClocks().get(index);

            Intent startAlarmClockIntent = AlarmClockManager.createIntent(activity.getApplicationContext(),
                    alarmClock, index);

            PendingIntent alarmExecuteIntent = PendingIntent.getActivity(activity.getApplicationContext(),
                    alarmClock.getId(), startAlarmClockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            AlarmClockManager.setExact(AlarmClockManager.deleteAlarmClock(),
                    alarmExecuteIntent);
        }

        return new AlarmClock(hours, minutes, chosenDays,
                songLocation, vibration, description, duration, is24HourFormat, dayPart);
    }

    private void startAlarmClock(AlarmClock alarmClock)
    {
        int alarmIndex = ApplicationStorage.getAlarmClocks().indexOf(alarmClock);

        Intent startAlarmClockIntent = AlarmClockManager.createIntent(activity.getApplicationContext(),
                alarmClock, alarmIndex);

        //Cancel or update?
        PendingIntent alarmExecuteIntent = PendingIntent.getActivity(activity.getApplicationContext(), alarmClock.getId(),
                startAlarmClockIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        //AlarmClockManager.getNextAlarmExecuteTime(alarmClock);
        AlarmClockManager.setExact(AlarmClockManager.getNextAlarmExecuteTime(alarmClock, false),
                alarmExecuteIntent);
    }
}
