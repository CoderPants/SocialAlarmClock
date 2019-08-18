package com.donteco.alarmClock.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.donteco.alarmClock.R;
import com.donteco.alarmClock.fragments.AlarmClockFragment;
import com.donteco.alarmClock.fragments.SocialNetworkFragment;
import com.donteco.alarmClock.fragments.TimeFragment;
import com.donteco.alarmClock.help.ActivityHelper;
import com.donteco.alarmClock.help.ApplicationStorage;
import com.donteco.alarmClock.help.ConstantsForApp;
import com.donteco.alarmClock.socialNetwork.SocialNetworkUser;
import com.donteco.alarmClock.socialNetwork.VKUserRequest;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;
import com.vk.api.sdk.exceptions.VKApiExecutionException;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private SocialNetworkFragment socialNetworkFragment;

   /*//Tracking if user logged in
    private AccessTokenTracker accessTokenTracker;*/

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        callbackManager = CallbackManager.Factory.create();

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken)
            {
                if(currentAccessToken == null)
                {
                    //set all fields to default value
                }
                else
                    loadUserData(currentAccessToken);
            }
        };

        ActivityHelper activityHelper = new ActivityHelper(this);
        activityHelper.getRidOfTopBar();

        ViewPager viewPager = findViewById(R.id.vp_for_app);
        PagerAdapter pagerAdapter = new PagerAdapterImpl(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //Get into it!
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //Opacity for animation
        viewPager.setPageTransformer(false, (v, pos) -> {
            final float opacity = Math.abs(Math.abs(pos) - 1);
            v.setAlpha(opacity);
        });

    }

    //Pause thread before quiting
    @Override
    protected void onStop() {
        System.out.println("In quiting method");
        ApplicationStorage.setAlarmClocksToStorage();
        super.onStop();
    }

    //Cos we get only 3 fragments
    private class PagerAdapterImpl extends FragmentPagerAdapter
    {
        public PagerAdapterImpl(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return ConstantsForApp.PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position)
            {
                case 0:
                    return new TimeFragment();

                case 1:
                    return new AlarmClockFragment();

                case 2:
                    socialNetworkFragment = new SocialNetworkFragment();
                    return socialNetworkFragment;
            }

            Log.wtf(ConstantsForApp.LOG_TAG, "In MainActivity in pagerAdapterImpl int getItem method: didn't find any fragment!");
            return null;
        }

        //Need to change to
        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
            //Cheat!!!!!!!!!!!!!!!
            SpannableStringBuilder ssb = new SpannableStringBuilder("    ");
            Drawable drawable;

            int iconId = Integer.MAX_VALUE;

            switch (position)
            {
                case 0:
                    iconId = R.drawable.clock_icon;
                    break;
                case 1:
                    iconId = R.drawable.alarm_clock_icon;
                    break;
                case 2:
                    iconId = R.drawable.social_network_icon;
                    break;
            }

            if(iconId == Integer.MAX_VALUE)
            {
                Log.wtf(ConstantsForApp.LOG_TAG, "In main Activity in getPageTitle method error - didn't find correct id");
                return "Title " + position;
            }

            drawable = ResourcesCompat.getDrawable(MainActivity.this.getResources(),
                    iconId, null);

            //Get into it!!!!
            drawable.setBounds(0, 0, ConstantsForApp.IMAGE_SIZE_IN_TABS, ConstantsForApp.IMAGE_SIZE_IN_TABS);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            ssb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            return ssb;
        }
    }

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
        LoginManager loginManager = LoginManager.getInstance();
        loginManager.logInWithReadPermissions(this, Collections.singletonList("public_profile"));
        //loginManager.logInWithPublishPermissions(this, Collections.singleton("user_posts"));
        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult) {
            }

            @Override
            public void onCancel() {
                System.out.println("Cancel!");
            }

            @Override
            public void onError(FacebookException error) {
                System.out.println("Error!");
            }
        });
    }

    public void loadUserData(AccessToken accessToken)
    {
        //ApplicationStorage.setFbAccessToken(accessToken);

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
        //
        //If not logged in
        if(!VK.onActivityResult(requestCode, resultCode, data, getVKCallbacks()))
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private VKAuthCallback getVKCallbacks(){
        return  new VKAuthCallback() {
            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                Toast.makeText(MainActivity.this, "Logging was successfully", Toast.LENGTH_SHORT).show();

                //Storing in the sharedPref
                ApplicationStorage.setVkAccessToken(vkAccessToken);

                int userId = vkAccessToken.getUserId();

                List<SocialNetworkUser> userList = new ArrayList<>(
                        Collections.singletonList(new SocialNetworkUser(userId)));

                VKUserRequest request = new VKUserRequest("users.get", userList);
                VK.execute(request, new VKApiCallback<List<SocialNetworkUser>>() {
                    @Override
                    public void success(List<SocialNetworkUser> users) {
                        System.out.println("In success list " + users);
                        System.out.println("In success fragment " + socialNetworkFragment);
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
                Toast.makeText(MainActivity.this, "Logging failed", Toast.LENGTH_SHORT).show();
            }
        };

    }
}
