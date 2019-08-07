package com.donteco.alarmClock.components;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.DeleteAlarmsActivity;
import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.AlarmClock;
import com.donteco.alarmClock.help.AlarmClocksStorage;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

//Shared preference
//singileton
//из аkтивити лист
//callback interface
public class AlarmClockAdapter extends RecyclerView.Adapter<AlarmClockAdapter.AlarmClockViewHolder>
{
    private List<AlarmClock> alarmClocks;
    private Mcallback mcallback;

    public AlarmClockAdapter( Mcallback mcallback) {
        alarmClocks = new ArrayList<>();
        this.mcallback = mcallback;
    }


    public void clearItems(){
        alarmClocks.clear();
        //for adapter to know, that list changed
        notifyDataSetChanged();
    }

    public void addItems(List<AlarmClock> alarmClocks)
    {
        this.alarmClocks = alarmClocks;
        notifyDataSetChanged();
    }

    public void addItem(AlarmClock alarmClock)
    {
        alarmClocks.add(alarmClock);
        AlarmClocksStorage.setAlarmClocks(alarmClocks);
        //Kinda bad
        notifyDataSetChanged();
    }

    /*public void changeItem(int position, AlarmClock newAlarmClock)
    {
        AlarmClock oldAlarmClock = alarmClocks.get(position);

        if(!oldAlarmClock.equals(newAlarmClock))
        {
            alarmClocks.set(position, newAlarmClock);
            notifyItemChanged(position);
        }
    }

    public void deleteItem(int position)
    {
        alarmClocks.remove(position);

    }*/

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
        private TextView alarmTime;
        private TextView alarmName;
        private Switch alarmSwitch;
        private LinearLayout alarmLayout;

        public AlarmClockViewHolder(@NonNull View itemView)
        {
            super(itemView);
            alarmTime = itemView.findViewById(R.id.alarm_clock_chosen_time);
            alarmName = itemView.findViewById(R.id.alarm_clock_name);
            alarmSwitch = itemView.findViewById(R.id.alarm_clock_switch_btn);
            alarmLayout = itemView.findViewById(R.id.ll_alarm_clock_recyclerview_fragment);

        }

        private void bind(AlarmClock alarmClock)
        {
            String time = String.format(Locale.ENGLISH, "%02d : %02d", alarmClock.getHours(), alarmClock.getMinutes());

            if(!alarmClock.isIs24HourFormat())
                time = time + " " + alarmClock.getDayPart();

            alarmTime.setText(time);
            alarmName.setText(alarmClock.getDescription());

            if(alarmClock.isAlive())
                alarmSwitch.setChecked(true);

            layoutLogic();
            switchLogic();
        }

        private void layoutLogic()
        {
            alarmLayout.setOnLongClickListener(view -> {
                mcallback.onLongPress();
                return false;
            });
        }

        private void switchLogic()
        {
            alarmSwitch.setOnClickListener(view -> {
                AlarmClock alarmClock = alarmClocks.get(getAdapterPosition());
                alarmClock.setAlive(!alarmClock.isAlive());
            });
        }
    }

    interface Mcallback{
        void onLongPress();
    }
}
