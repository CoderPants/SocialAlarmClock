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

import com.donteco.alarmClock.help.ConstantsForApp;

public class DurationChooseDialog extends DialogFragment {

    public interface DurationChooseDialogListener{
        void onDurationDialogPositiveClick(String[] duration);
    }

    private DurationChooseDialogListener listener;

    public DurationChooseDialog(){}

    //Mb, it's illegal
    //But only maybe...
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (DurationChooseDialogListener) context;
        }
        catch (ClassCastException e){
            Log.e(ConstantsForApp.LOG_TAG, "In DurationChooseDialog onAttach method error by casting context to listener", e);
            throw new ClassCastException(context.toString() + " must implement DurationChooseDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        final String[] duration = new String[]{"1 min" ,"5 min", "10 min", "15 min", "20 min", "30 min"};

        //Kinda cheat
        final String [] userChoice =  new String[]{"" ,"", "", "", "", ""};

        DialogInterface.OnClickListener acceptBtnListener = (dialogInterface, id) -> listener.onDurationDialogPositiveClick(userChoice);

        DialogInterface.OnClickListener cancelBtnListener = (dialogInterface, i) -> dialogInterface.cancel();

        DialogInterface.OnClickListener singleChoiceListener = (dialogInterface, item) -> {
            for (int i = 0; i < userChoice.length; i++)
                if(!userChoice[i].equals(""))
                    userChoice[i] = "";

               userChoice[item] = duration[item];
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose duration of the alarm")
                .setSingleChoiceItems(duration, -1, singleChoiceListener)
                .setPositiveButton("Ok", acceptBtnListener)
                .setNegativeButton("Cancel", cancelBtnListener);

        return builder.create();
    }
}
