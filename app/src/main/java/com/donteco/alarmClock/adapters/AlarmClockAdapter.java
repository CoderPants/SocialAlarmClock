package com.donteco.alarmClock.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.dialogs.SocialNetworkChooseDialog;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmClockViewHolder>
{
    private List<AlarmClock> alarmClocks;
    private AlarmClockCallBack alarmClockCallBack;
    private int curAlarmPosition;

    public AlarmClockAdapter(AlarmClockCallBack alarmClockCallBack)
    {
        alarmClocks = ApplicationStorage.getAlarmClocks();
        this.alarmClockCallBack = alarmClockCallBack;

        notifyDataSetChanged();
    }

    public void refreshAlarmClocks() {
        alarmClocks = ApplicationStorage.getAlarmClocks();
        notifyDataSetChanged();
    }

    public void addItem(AlarmClock alarmClock)
    {
        ApplicationStorage.addAlarmClock(alarmClock);
        //Kinda bad
        notifyDataSetChanged();
    }

    public void setItem(AlarmClock alarmClock) {
        ApplicationStorage.setAlarmClock(curAlarmPosition, alarmClock);
        notifyItemChanged(curAlarmPosition);
    }

    @NonNull
    @Override
    public AlarmClockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_clock_recyclerview_element, parent, false);
        return new AlarmClockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmClockViewHolder holder, int position) {
        holder.bind(alarmClocks.get(position));
    }

    @Override
    public int getItemCount() {
        return alarmClocks.size();
    }

    public class AlarmClockViewHolder extends RecyclerView.ViewHolder
    {
        private ImageView shareBtn;

        private LinearLayout alarmLayout;
        private TextView alarmTime;
        private TextView alarmName;
        private TextView alarmDayPart;
        private TextView alarmDaysOfTheWeek;

        private Switch alarmSwitch;

        public AlarmClockViewHolder(@NonNull View itemView)
        {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_clock_chosen_time);
            alarmName = itemView.findViewById(R.id.alarm_clock_name);
            alarmDayPart = itemView.findViewById(R.id.alarm_clock_chosen_day_part);
            alarmDaysOfTheWeek = itemView.findViewById(R.id.alarm_clock_days_of_the_week);
            alarmSwitch = itemView.findViewById(R.id.alarm_clock_switch_btn);
            alarmLayout = itemView.findViewById(R.id.ll_alarm_clock_recyclerview_fragment);
            shareBtn = itemView.findViewById(R.id.iv_share_alarm_clock_recyclerview_fragment);
        }

        private void bind(AlarmClock alarmClock)
        {
            String time = String.format(Locale.ENGLISH, "%02d : %02d", alarmClock.getHours(), alarmClock.getMinutes());

            alarmTime.setText(time);

            StringBuilder description = new StringBuilder(alarmClock.getDescription());
            if(description.length() > ConstantsForApp.TEXT_LENGTH_IN_TEXT_VIEW)
            {
                description = description.delete(ConstantsForApp.TEXT_LENGTH_IN_TEXT_VIEW, description.length());
                description.append("...");
            }
            alarmName.setText(description.toString());

            if(!alarmClock.isIs24HourFormat())
            {
                alarmDayPart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                alarmDayPart.setVisibility(View.VISIBLE);
                alarmDayPart.setText(alarmClock.getDayPartToString());
            }
            else
            {
                alarmDayPart.setVisibility(View.INVISIBLE);
                alarmDayPart.setHeight(0);
                alarmDayPart.setWidth(0);
            }
            setAlarmDaysOfTheWeek(alarmClock.getChosenDays());

            if(alarmClock.isAlive())
                alarmSwitch.setChecked(true);
            else
                alarmSwitch.setChecked(false);

            layoutLogic();
            switchLogic();
            shareBtnLogic();
        }

        private void setAlarmDaysOfTheWeek(boolean[] chosenDays)
        {
            StringBuilder days = new StringBuilder();

            boolean isAllChecked = true;

            for (int i = 0; i < chosenDays.length; i++)
            {
                if(chosenDays[i])
                {
                    days.append(ConstantsForApp.DAYS_LIST[i].getReductiveDayName());
                    days.append(", ");
                }
                else
                    isAllChecked = false;
            }

            if(days.length() > 0)
            {
                String outputText;

                if(isAllChecked)
                    outputText = "Every day";
                else
                    //Getting rid of the last ", "
                    outputText = days.toString().substring(0, days.length()-2);

                alarmDaysOfTheWeek.setText(outputText);
            }
            else
                alarmDaysOfTheWeek.setText(R.string.choose_day_of_week);
        }

        private void layoutLogic()
        {
            alarmLayout.setOnLongClickListener(view ->
            {
                alarmClockCallBack.onLongPress();
                return false;
            });

            alarmLayout.setOnClickListener(view ->
            {
                curAlarmPosition = getAdapterPosition();
                alarmClockCallBack.onPress(alarmClocks.get(curAlarmPosition).getId());
            });
        }

        private void switchLogic()
        {
            alarmSwitch.setOnClickListener(view -> {
                AlarmClock alarmClock = alarmClocks.get(getAdapterPosition());
                alarmClock.setAlive(!alarmClock.isAlive());
                alarmClockCallBack.switchLogic(alarmClock, alarmSwitch);
            });
        }

        private void shareBtnLogic() {
            shareBtn.setOnClickListener(view ->
                    alarmClockCallBack.onSharePress(alarmTime.getText().toString()));
        }
    }

    //Interface for activity connection
    public interface AlarmClockCallBack {
        void onLongPress();
        void onPress(int id);
        void onSharePress(String time);
        void switchLogic(AlarmClock alarmClock, Switch isAlive);
    }
}
