package com.donteco.alarmClock.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.activities.MainActivity;
import com.donteco.alarmClock.adapters.SocialNetworkAdapter;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;

import java.util.ArrayList;
import java.util.List;

public class SocialNetworkFragment extends Fragment {

    private MainActivity activity;
    private SocialNetworkAdapter socialNetworkAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        try{
            activity = (MainActivity) getActivity();
            return inflater.inflate(R.layout.social_media_fragment, container, false);
        }
        catch (Exception e) {
            Log.wtf(ConstantsForApp.LOG_TAG, "Exception in SocialNetworkFragment in onCreateView method " + e);
            return super.onCreateView(inflater, container, savedInstanceState);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.rv_social_media);
        socialFragmentLogic(recyclerView);
    }

    private void socialFragmentLogic(RecyclerView recyclerView)
    {
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        setSocialNetworkAdapter();

        recyclerView.setAdapter(socialNetworkAdapter);
    }

    private void setSocialNetworkAdapter()
    {
        List<SocialNetworkUser> userList = ApplicationStorage.getSocialNetworkUsers();

        //At the first app initialization
        if(userList.isEmpty())
        {
            userList.add(new SocialNetworkUser(ConstantsForApp.VK_NAME,
                    getResources().getDrawable(R.drawable.vk_round_icon, null)));

            userList.add(new SocialNetworkUser(ConstantsForApp.FACEBOOK_NAME,
                    getResources().getDrawable(R.drawable.facebook_round_icon, null)));
        }


        socialNetworkAdapter = new SocialNetworkAdapter(socialNetworkUser ->
        {
            switch (socialNetworkUser.getSocialNetworkName())
            {
                case "VK":
                    activity.vkLogin();
                    break;
                case "FaceBook":
                    activity.fbLogin();
                    break;

            }
        }, userList);
    }

    public void updateUser(int position, SocialNetworkUser user) {
        ApplicationStorage.setUser(position, user);
        socialNetworkAdapter.updateUser(position, user);
    }
}
