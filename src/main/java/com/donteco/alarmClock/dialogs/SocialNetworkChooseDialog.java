package com.donteco.alarmClock.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.facebook.AccessToken;
import com.vk.api.sdk.auth.VKAccessToken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SocialNetworkChooseDialog extends DialogFragment
{

    private String [] socialNetworkNames;
    private boolean [] socialNetworksChecked;

    public SocialNetworkChooseDialog(){}

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState)
    {
        prepareData();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        DialogInterface.OnClickListener cancelBtnListener = (dialogInterface, i) -> dialogInterface.cancel();

        if(socialNetworkNames != null && socialNetworkNames.length != 0)
        {
            DialogInterface.OnMultiChoiceClickListener multiChoiceClickListener = (dialogInterface, what, isChecked) ->
            {
                socialNetworksChecked[what] = isChecked;
            };

            //'Cos i sent data to the fragment
            DialogInterface.OnClickListener acceptBtnListener = (dialogInterface, id) ->
            {
                Intent intent = new Intent();
                intent.putExtra("Checked_networks", socialNetworksChecked);
                getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, intent);
            };

            builder.setTitle("Choose social network to share")
                    .setMultiChoiceItems(socialNetworkNames, socialNetworksChecked, multiChoiceClickListener)
                    .setPositiveButton("Ok", acceptBtnListener)
                    .setNegativeButton("Cancel", cancelBtnListener);
        }
        else
        {
            builder.setTitle("No social network found")
                    .setMessage("Please, log in in some social network!")
                    .setNegativeButton("Ok", cancelBtnListener);
        }

        return builder.create();
    }

    private void prepareData()
    {
        List<SocialNetworkUser> socialNetworkUsers = ApplicationStorage.getSocialNetworkUsers();
        List<String> socialNetworkNames = new ArrayList<>();

        VKAccessToken vkAccessToken = ApplicationStorage.getVkAccessToken();
        //AccessToken fbAccessToken = ApplicationStorage.getFbAccessToken();
        AccessToken fbAccessToken = AccessToken.getCurrentAccessToken();

        if(vkAccessToken != null && vkAccessToken.isValid())
            socialNetworkNames.add( socialNetworkUsers.get(ConstantsForApp.VK_POSITION).getSocialNetworkName() );

        if(fbAccessToken != null && !fbAccessToken.isExpired())
            socialNetworkNames.add( socialNetworkUsers.get(ConstantsForApp.FACE_BOOK_POSITION).getSocialNetworkName() );

        this.socialNetworkNames = new String[socialNetworkNames.size()];
        this.socialNetworkNames = socialNetworkNames.toArray(this.socialNetworkNames);

        socialNetworksChecked = new boolean[ConstantsForApp.AMOUNT_OF_SOCIAL_NETWORKS];

        Arrays.fill(socialNetworksChecked, false);
    }
}
