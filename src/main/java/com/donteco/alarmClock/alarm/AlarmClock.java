package com.donteco.alarmClock.alarm;

import android.net.Uri;

import java.util.Arrays;
import java.util.Objects;

public class AlarmClock
{
    private int hours;
    private int minutes;
    private boolean[] chosenDays;
    private String alarmClockMusicLocation;
    private boolean vibration;
    private String description;
    private int duration;
    private boolean is24HourFormat;
    private DayPart dayPart;

    private boolean alive;


    public AlarmClock(int hours, int minutes, boolean[] chosenDays,
                      String alarmClockMusicLocation, boolean vibration, String description,
                      int duration, boolean is24HourFormat, DayPart dayPart)
    {
        this.hours = hours;
        this.minutes = minutes;
        this.chosenDays = chosenDays;
        this.alarmClockMusicLocation = alarmClockMusicLocation;
        this.vibration = vibration;
        this.description = description;
        this.duration = duration;
        this.is24HourFormat = is24HourFormat;
        this.dayPart = dayPart;
        this.alive = true;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public boolean[] getChosenDays() {
        return chosenDays;
    }

    public void setChosenDays(boolean[] chosenDays) {
        this.chosenDays = chosenDays;
    }

    public String getAlarmClockMusicLocation() {
        return alarmClockMusicLocation;
    }

    public void setAlarmClockMusicLocation(String alarmClockMusicLocation) {
        this.alarmClockMusicLocation = alarmClockMusicLocation;
    }

    public boolean isVibration() {
        return vibration;
    }

    public void setVibration(boolean vibration) {
        this.vibration = vibration;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public boolean isIs24HourFormat() {
        return is24HourFormat;
    }

    public void setIs24HourFormat(boolean is24HourFormat) {
        this.is24HourFormat = is24HourFormat;
    }

    public DayPart getDayPart() {
        return dayPart;
    }

    public void setDayPart(DayPart dayPart) {
        this.dayPart = dayPart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AlarmClock that = (AlarmClock) o;
        return hours == that.hours &&
                minutes == that.minutes &&
                vibration == that.vibration &&
                duration == that.duration &&
                is24HourFormat == that.is24HourFormat &&
                Arrays.equals(chosenDays, that.chosenDays) &&
                Objects.equals(alarmClockMusicLocation, that.alarmClockMusicLocation) &&
                Objects.equals(description, that.description) &&
                dayPart == that.dayPart;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(hours, minutes, alarmClockMusicLocation, vibration, description, duration, is24HourFormat, dayPart);
        result = 31 * result + Arrays.hashCode(chosenDays);
        return result;
    }
}
