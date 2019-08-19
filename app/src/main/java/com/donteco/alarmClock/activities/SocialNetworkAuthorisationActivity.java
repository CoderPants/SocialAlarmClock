package com.donteco.alarmClock.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.donteco.alarmClock.fragments.SocialNetworkFragment;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.donteco.alarmClock.socialNetwork.VKUserRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKApiExecutionException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SocialNetworkAuthorisationActivity extends FragmentActivity
{
    protected SocialNetworkFragment socialNetworkFragment = new SocialNetworkFragment();
    protected CallbackManager callbackManager;

    //FaceBook
    private LoginManager loginManager;

    public void vkLogin()
    {
        //Getting permissions
        ArrayList<VKScope> list = new ArrayList<>();
        list.add(VKScope.WALL);
        list.add(VKScope.PHOTOS);
        VK.login(this, list);
    }

    public void fbLogin()
    {
        loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Collections.singletonList("public_profile"));
        //loginManager.logInWithPublishPermissions(this, Collections.singleton("user_posts"));

         callbackManager = CallbackManager.Factory.create();

        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
                Toast.makeText(SocialNetworkAuthorisationActivity.this, "FaceBook authorisation has been canceled", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(SocialNetworkAuthorisationActivity.this, "FaceBook error, check internet connection!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void vkLogOut(){
        VK.logout();
    }

    public void fbLogOut() {
        if(loginManager == null)
            loginManager = LoginManager.getInstance();

        loginManager.logOut();
    }

    public void loadUserData(AccessToken accessToken, SocialNetworkFragment socialNetworkFragment)
    {
        //ApplicationStorage.setFbAccessToken(accessToken);
        this.socialNetworkFragment = socialNetworkFragment;

        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try
            {
                int id = object.getInt("id");
                String name = object.getString("name");

                JSONObject image = object.getJSONObject("picture");
                JSONObject data = image.getJSONObject("data");
                String url = data.getString("url");

                String[] firstLastName = name.split(" ");


                socialNetworkFragment.updateUser(ConstantsForApp.FACE_BOOK_POSITION,
                        new SocialNetworkUser(id, firstLastName[0], firstLastName[1], url, ConstantsForApp.FACEBOOK_NAME));

            } catch (JSONException e) {
                System.out.println("Error in parse!");
                e.printStackTrace();
            }
        });

        Bundle permission_param = new Bundle();
        permission_param.putString("fields", "id,name,picture.width(100).height(100)");
        request.setParameters(permission_param);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == ConstantsForApp.FACEBOOK_LOGIN_CODE)
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }

        //If not logged in
        if(!VK.onActivityResult(requestCode, resultCode, data, getVKCallbacks()))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private VKAuthCallback getVKCallbacks(){
        return new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                Toast.makeText(SocialNetworkAuthorisationActivity.this, "Logging was successfully", Toast.LENGTH_SHORT).show();

                //Storing in the sharedPref
                ApplicationStorage.setVkAccessToken(vkAccessToken);

                int userId = vkAccessToken.getUserId();

                List<SocialNetworkUser> userList = new ArrayList<>(
                        Collections.singletonList(new SocialNetworkUser(userId)));

                VKUserRequest request = new VKUserRequest("users.get", userList);
                VK.execute(request, new VKApiCallback<List<SocialNetworkUser>>() {
                    @Override
                    public void success(List<SocialNetworkUser> users) {
                        socialNetworkFragment.updateUser(ConstantsForApp.VK_POSITION, users.get(0));
                    }

                    @Override
                    public void fail(@NotNull VKApiExecutionException e) {
                        Log.e(ConstantsForApp.LOG_TAG, "Error caused by getting vk request ", e);
                    }
                });
            }

            @Override
            public void onLoginFailed(int i) {
                //Never come here
                Toast.makeText(SocialNetworkAuthorisationActivity.this, "Logging failed", Toast.LENGTH_SHORT).show();
            }
        };
    }
}
