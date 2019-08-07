package com.donteco.alarmClock.components;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.alarm.AlarmClock;

import java.util.List;
import java.util.Locale;

public class AlarmClockDeleteAdapter extends RecyclerView.Adapter<AlarmClockDeleteAdapter.AlarmClockDeleteViewHolder>
{
    private List<AlarmClock> alarmClocks;

    public AlarmClockDeleteAdapter(List<AlarmClock> alarmClocks) {
        this.alarmClocks = alarmClocks;
        notifyDataSetChanged();
    }

    public List<AlarmClock> getAlarmClocks() {
        return alarmClocks;
    }

    @NonNull
    @Override
    public AlarmClockDeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.alarm_delete_recyclerview_element, parent, false);

        return new AlarmClockDeleteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmClockDeleteViewHolder holder, int position) {
        holder.bind(alarmClocks.get(position));
    }

    @Override
    public int getItemCount() {
        return alarmClocks.size();
    }

    public class AlarmClockDeleteViewHolder extends RecyclerView.ViewHolder
    {
        private TextView alarmTime;
        private TextView alarmName;
        private TextView deleteAlarmBtn;

        public AlarmClockDeleteViewHolder(@NonNull View itemView) {
            super(itemView);

            alarmTime = itemView.findViewById(R.id.delete_alarm_clock_chosen_time);
            alarmName = itemView.findViewById(R.id.delete_alarm_clock_name);
            deleteAlarmBtn = itemView.findViewById(R.id.delete_alarm_clock_tv);
        }

        private void bind(final AlarmClock alarmClock)
        {
            String time = String.format(Locale.ENGLISH, "%02d : %02d", alarmClock.getHours(), alarmClock.getMinutes());

            if(!alarmClock.isIs24HourFormat())
                time = time + " " + alarmClock.getDayPart();

            alarmTime.setText(time);
            alarmName.setText(alarmClock.getDescription());
            deleteAlarmBtn.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    alarmClocks.remove(alarmClock);
                    notifyDataSetChanged();
                }
            });
        }

    }
}
