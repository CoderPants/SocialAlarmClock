package com.donteco.alarmClock.components;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.donteco.alarmClock.alarm.DayOfWeek;
import com.donteco.alarmClock.help.ConstantsForApp;

public class DayChooseDialog extends DialogFragment {


    public interface DayChooseDialogListener {
            void onDaysDialogPositiveClick(boolean[] checkedDays);
    }

    private DayChooseDialogListener listener;

    public DayChooseDialog(){}

    //Mb, it's illegal
    //But only maybe...
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DayChooseDialogListener) context;
        }
        catch (ClassCastException e){
            Log.e(ConstantsForApp.LOG_TAG, "In DayChooseDialog onAttach method error by casting context to listener", e);
            throw new ClassCastException(context.toString() + " must implement DayChooseDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        final String[] days = new String[7];
        DayOfWeek[] daysEnum = ConstantsForApp.DAYS_LIST;

        for (int i = 0; i < days.length; i++)
            days[i] = daysEnum[i].toString();

        final boolean[] checked = new boolean[]{false, false, false, false, false, false, false};


        DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener = new DialogInterface.OnMultiChoiceClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int what, boolean isChecked) {
                checked[what] = isChecked;
            }
        };

        DialogInterface.OnClickListener acceptBtnListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int id)
            {
                listener.onDaysDialogPositiveClick(checked);
            }
        };

        DialogInterface.OnClickListener cancelBtnListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose day of the week")
                .setMultiChoiceItems(days, checked, multiChoiceClickListener)
                .setPositiveButton("Ok", acceptBtnListener)
                .setNegativeButton("Cancel", cancelBtnListener);

        return builder.create();
    }
}
