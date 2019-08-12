package com.donteco.alarmClock.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;

import java.util.List;

public class SocialNetworkChooseDialog extends DialogFragment
{
    public interface SocialNetworkDialogListener {
        void onSocialNetworkPositiveClick(boolean [] checkedSocialNetworks);
    }

    SocialNetworkDialogListener listener;

    public SocialNetworkChooseDialog(){}

    //Mb, it's illegal
    //But only maybe...
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            listener = (SocialNetworkChooseDialog.SocialNetworkDialogListener) context;
        }
        catch (ClassCastException e){
            Log.e(ConstantsForApp.LOG_TAG, "In SocialNetworkChooseDialog onAttach method error by casting context to listener", e);
            throw new ClassCastException(context.toString() + " must implement SocialNetworkDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        List<SocialNetworkUser> socialNetworkUsers = ApplicationStorage.getSocialNetworkUsers();
        final String[] socialNetworkNames = new String[socialNetworkUsers.size()];

        for (int i = 0; i < socialNetworkUsers.size(); i++)
            socialNetworkNames[i] = socialNetworkUsers.get(i).getSocialNetworkName();

        final boolean[] checked = new boolean[]{false, false};

        DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener = (dialogInterface, what, isChecked) -> checked[what] = isChecked;

        DialogInterface.OnClickListener acceptBtnListener = (dialogInterface, id) -> listener.onSocialNetworkPositiveClick(checked);

        DialogInterface.OnClickListener cancelBtnListener = (dialogInterface, i) -> dialogInterface.cancel();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle("Choose day of the week")
                .setMultiChoiceItems(socialNetworkNames, checked, multiChoiceClickListener)
                .setPositiveButton("Ok", acceptBtnListener)
                .setNegativeButton("Cancel", cancelBtnListener);

        return builder.create();
    }
}
