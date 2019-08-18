package com.donteco.alarmClock.alarm;

//Cos i'm using 23 api, not 26
public enum DayOfWeek
{
    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);

    private final int dayNumber;

    DayOfWeek(int dayNumber) {

        if (dayNumber == 0)
            throw new NumberFormatException("Day of the week can't be 0!");

        this.dayNumber = dayNumber;
    }

    @Override
    public String toString()
    {
        String result = "";

        switch (dayNumber)
        {
            case 1:
                result = "Monday";
                break;
            case 2:
                result = "Tuesday";
                break;
            case 3:
                result = "Wednesday";
                break;
            case 4:
                result = "Thursday";
                break;
            case 5:
                result = "Friday";
                break;
            case 6:
                result = "Saturday";
                break;
            case 7:
                result = "Sunday";
                break;
        }
        return result;
    }

    public String getReductiveDayName()
    {
        String result = "";

        switch (dayNumber)
        {
            case 1:
                result = "mon";
                break;
            case 2:
                result = "tue";
                break;
            case 3:
                result = "wed";
                break;
            case 4:
                result = "thu";
                break;
            case 5:
                result = "fri";
                break;
            case 6:
                result = "sat";
                break;
            case 7:
                result = "sun";
                break;
        }
        return result;
    }
}
