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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
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
import com.donteco.alarmClock.alarmclock.AlarmClockManager;
import com.donteco.alarmClock.alarmclock.AlarmClockReceiver;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.help.KeysForIntents;
import com.donteco.alarmClock.notification.NotificationBuilder;
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

    //private NotificationManager manager;
    private NotificationManagerCompat managerCompat;
    private NotificationBuilder builder;

    //HEY YOU!
    private String timeForSharing;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try {
            activity = getActivity();
            managerCompat = NotificationManagerCompat.from(getContext());
            builder = new NotificationBuilder();
            //We need only one
            builder.createNotificationChanel();

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
            public void onSharePress(String curTime)
            {
                timeForSharing = curTime;

                SocialNetworkChooseDialog dayChooseDialog = new SocialNetworkChooseDialog();
                dayChooseDialog.setTargetFragment(AlarmClockFragment.this,
                        ConstantsForApp.SHARE_DIALOG_SHOW_REQUEST);

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
                    alarmClockAdapter.refreshAlarmClocks();
                    break;

                case ConstantsForApp.ALARM_CHANGE_INFO_REQUEST:
                    alarmClockAdapter.setItem(convertToAlarmClock(data));
                    break;

                case ConstantsForApp.SHARE_DIALOG_SHOW_REQUEST:
                    boolean [] checkedSocialNetwork = data.getBooleanArrayExtra("Checked_networks");

                    for (boolean b : checkedSocialNetwork) {
                        System.out.println("Checked socialNetworks " + b);
                    }

                    share(checkedSocialNetwork);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }


    //Need to fix it
    private void share(boolean[] checkedSocialNetwork)
    {
        VKAccessToken vkAccessToken = ApplicationStorage.getVkAccessToken();

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
                if(vkAccessToken.isValid())
                    VKShare();

                if(!AccessToken.getCurrentAccessToken().isExpired())
                    FBShare();
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
        AccessToken faceBookToken = AccessToken.getCurrentAccessToken();
        System.out.println("Permitions " + faceBookToken.getPermissions());

        Bitmap image = BitmapFactory.decodeResource(getContext().getResources(),
                R.drawable.alarm_clock_for_social_sharing);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        ShareDialog shareFaceBook = new ShareDialog(this);

        System.out.println("Can show " + shareFaceBook.canShow(content, ShareDialog.Mode.AUTOMATIC));

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


        setAlarmClock(alarmClock);
        //calendar.set(Calendar.DAY_OF_WEEK,Calendar);

        //createNotification(alarmClock);

        return alarmClock;
    }

    private void setAlarmClock(AlarmClock alarmClock)
    {
        Calendar calendar = Calendar.getInstance();

        int hours = alarmClock.getHours();
        int minutes = alarmClock.getMinutes();
        boolean[] chosenDays = alarmClock.getChosenDays();
        boolean is24Hour = alarmClock.isIs24HourFormat();
        DayPart dayPart = alarmClock.getDayPart();

        int curHours = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinutes = calendar.get(Calendar.MINUTE);

        //check this shit!
        if(!is24Hour && dayPart == DayPart.PM)
            hours += 12;


        int curDay = calendar.get(Calendar.DAY_OF_WEEK);
        int nextAlarmDay = curDay;
        boolean noRepeatDays = true;

        //If user choose smth and this is not curDay
        for (int i = 0; i < chosenDays.length; i++)
            if(chosenDays[i] && (i+1) != curDay)
            {
                nextAlarmDay = i+1;
                noRepeatDays = false;
            }

        //If user choose cur time and there is no chosen days
        if(curHours >= hours && curMinutes >= minutes && noRepeatDays)
            ++nextAlarmDay;

        System.out.println("Calendar info " + hours + " " + minutes + " " + nextAlarmDay + " song uri " + alarmClock.getAlarmClockMusicLocation() + " duration " + alarmClock.getDuration() + " hsVibration " + alarmClock.hasVibration());

        calendar.set(Calendar.HOUR_OF_DAY, hours);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.DAY_OF_WEEK, nextAlarmDay);
        calendar.set(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 1);

        System.out.println("Passed set calendar");
        Intent startAlarmClockIntent = new Intent(getContext(), AlarmClockReceiver.class);
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_MUSIC, alarmClock.getAlarmClockMusicLocation());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_DURATION, alarmClock.getDuration());
        startAlarmClockIntent.putExtra(KeysForIntents.ALARM_CLOCK_VIBRATION, alarmClock.hasVibration());

        System.out.println("Passed set startIntent");

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(),
                0, startAlarmClockIntent, 0);

        System.out.println("Passed pending intent creation ");

        AlarmClockManager.setExtact(calendar.getTimeInMillis(), pendingIntent);

        System.out.println("Passed alarm clock");
        /*calendar.set(Calendar.MINUTE, alarmClock.getMinutes());
        calendar.set(Calendar.HOUR_OF_DAY, alarmClock.getHours());*/


       /* boolean notChecked = true;

        for (int i = 0; i < chosenDays.length; i++)
            if(chosenDays[i])
            {
                calendar.set(Calendar.DAY_OF_WEEK, i+1);
                notChecked = false;
            }

        if(notChecked)
            calendar.set( Calendar.DAY_OF_WEEK, calendar.get(Calendar.DAY_OF_WEEK) + 1 );*/

    }

    /*private void createNotification(AlarmClock alarmClock)
    {
        Intent activityIntent = new Intent(getContext(), MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, activityIntent, 0);

        Intent broadcastIntent = new Intent(getContext(), NotificationReceiver.class);
        broadcastIntent.putExtra("message", "in pending intent");
        PendingIntent actionIntent = PendingIntent.getBroadcast(getContext(), 0,
                broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri uri;
        String alarmClockMusic = alarmClock.getAlarmClockMusicLocation();

        if(alarmClockMusic == null)
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://"+
                    getApplicationContext().getPackageName() + "/" + R.raw.alarm_cock_standart_music);
        else
            uri = Uri.parse(alarmClockMusic);

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(activity.getApplicationContext(), ConstantsForApp.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_access_alarm_black_24dp)
                .setWhen(System.currentTimeMillis() + 6000)
                .setContentTitle("Alarm clock")
                .setContentText("Time " + alarmClock.getHours() + " " + alarmClock.getMinutes())
                .setContentIntent(contentIntent)
                .addAction(R.mipmap.ic_launcher, "Toast", actionIntent)
                .setAutoCancel(true);

        *//*if(alarmClock.hasVibration())
        {
            long[] vibrationPattern = new long[alarmClock.getDuration()*60000];
            Arrays.fill(vibrationPattern, 1000);

            builder.setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 });
        }*//*

        managerCompat.notify(alarmClockAdapter.getCurAlarmPosition(), builder.build());
    }*/

}
